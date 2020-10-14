package com.fss.pos.hsm.futurex;



import com.fss.pos.base.api.hsm.Hsm;
import com.fss.pos.base.api.hsm.HsmData;
import com.fss.pos.base.api.hsm.HsmResponse;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.hsm.AbstractHsmApi;


@Hsm("4")
public class FutureXApi extends AbstractHsmApi {

	//COMMON
	private static final String MSG_HEADER = "[AO";
	private static final String DELIMITER = ";";
	private static final String MSG_TAIL = "]";
	
	//KEY_DERIVATION
	private static final String CMD_DERIVATION = "GWKS";
	private static final int AS_KEY_DERV_KEY_MODIFIER=8;
	private static final int FS_KEY_DERV_KEY_LENGTH=2;
	
	
	//PIN TRANSLATION
	private static final String CMD_PINTRANSLATION = "TPIN";
	private static final int PIN_BLOCK_TYPE = 3;
	private static final int PIN_BLOCK_FORMAT = 1;
	private static final int DUKPT_INDICATOR = 1;
	private static final int TRIPLEDES_INDICATOR = 1;
	private static final String BB_PINTRAN_RES_SUCCESS= "Y";
	
	//P2PE
	private static final String CMD_P2PE = "DCDK";
	private static final int FS_P2PE_COMMAND_MODE = 2;
	private static final int TD_P2PE_DEC_MAJOR_KEY = 1;
	
	//ERROR COMMAND
	private static final String CMD_ERROR = "ERRO";
	

	@Override
	public HsmResponse generateKey(final String ktmSpec, final String keyType,
			final String cryptoType, String checkDigit, String mspAcr)
			throws PosException {

		try {
			Log.debug("HSM REQ", getGenRequest(ktmSpec, keyType, cryptoType));
			String rspData = sendAndReceive(
					getGenRequest(ktmSpec, keyType, cryptoType), mspAcr);
			Log.debug("HSM RES",rspData);
			HsmResponse hsmGenRsp = new HsmResponse();
			String[] rspBuffer=rspData.split(";");
			
			if(rspBuffer[0].contains(CMD_ERROR))
			{
				hsmGenRsp.setRspCode(rspBuffer[1].trim().substring(2));
				hsmGenRsp.setRspCode(rspBuffer[2].trim().substring(2));
				hsmGenRsp.setRspDesc(rspBuffer[3].trim().substring(2));
				Log.debug("Error response from HSM", hsmGenRsp.getErrCode());
				throw new PosException(Constants.ERR_HSM_CONNECT);
			}
			else  {
				String rspLmkKey = rspBuffer[1].trim().substring(2);
				String rspKey = rspBuffer[2].trim().substring(2);
				String rspCheckSum = rspBuffer[3].trim().substring(2);
				hsmGenRsp.setParentEncryptedKey(rspKey);
				hsmGenRsp.setLmkEncryptedKey(rspLmkKey);
				hsmGenRsp.setChecksum(rspCheckSum);
			} 
			return hsmGenRsp;
		} catch (Exception e) {
			Log.error("Thales HSM ", e);
			throw new PosException(Constants.ERR_HSM_CONNECT);
		}

	
	}

	public static String getGenRequest(String ktmSpec, String keyType,
			String cryptoType) throws Exception {
		StringBuilder reqBuffer = new StringBuilder(MSG_HEADER
				+ CMD_DERIVATION +DELIMITER); // changed
		reqBuffer.append("AS"+AS_KEY_DERV_KEY_MODIFIER+DELIMITER);
		reqBuffer.append("AP"+ktmSpec+DELIMITER);
		reqBuffer.append("FS"+FS_KEY_DERV_KEY_LENGTH+DELIMITER);
		reqBuffer.append(MSG_TAIL);
		return reqBuffer.toString();
	}


	public static String getPINTranRequest(String encPINBlock, String terminalPPK,
			String cardNo, String cryptoType, String instPPK,
			IsoBuffer isoBuffer, String keyEntry) throws Exception {
		if (isoBuffer.isFieldEmpty(Constants.DE44)
				&& keyEntry.equals(Constants.KEY_SCHEME_MASTER_SESSION)) {
			StringBuilder reqBuffer = new StringBuilder(MSG_HEADER
					+ CMD_PINTRANSLATION + DELIMITER);
			reqBuffer.append("AW"+PIN_BLOCK_TYPE+DELIMITER);
			reqBuffer.append("AX"+terminalPPK+DELIMITER);
			reqBuffer.append("BT"+instPPK+DELIMITER);
			reqBuffer.append("AL"+encPINBlock+DELIMITER);
			reqBuffer.append("AK"+Util.getANBFromCardNo(cardNo)+DELIMITER);
			reqBuffer.append("ZA"+PIN_BLOCK_FORMAT+DELIMITER);
			reqBuffer.append(MSG_TAIL);
			return reqBuffer.toString();
		} else {
			// DUKPT
			StringBuilder reqBuffer = new StringBuilder(MSG_HEADER
					+ CMD_PINTRANSLATION + DELIMITER);
			
			reqBuffer.append("AW"+PIN_BLOCK_TYPE+DELIMITER);
			reqBuffer.append("GO"+DUKPT_INDICATOR+DELIMITER);
			reqBuffer.append("GK"+terminalPPK+DELIMITER);
			reqBuffer.append("BT"+instPPK+DELIMITER);
			reqBuffer.append("AL"+encPINBlock+DELIMITER);
			reqBuffer.append("GM"+isoBuffer.get(Constants.DE44)+DELIMITER);
			reqBuffer.append("TD"+TRIPLEDES_INDICATOR+DELIMITER);
			reqBuffer.append("AK"+Util.getANBFromCardNo(cardNo)+DELIMITER);
			reqBuffer.append("ZA"+PIN_BLOCK_FORMAT+DELIMITER);
			reqBuffer.append(MSG_TAIL);
			
			
			Log.debug("request buffer", reqBuffer.toString());
			return reqBuffer.toString();
		}

	}

	@Override
	public HsmResponse translatePIN(String encPINBlock, String terminalPPK,
			String tpkCheckValue, String cardNo, String cryptoType,
			String instPPK, String checkDigit, String mspAcr,
			IsoBuffer isoBuffer, String keyEntry) {

		HsmResponse hsmRspBean = new HsmResponse();
		try {

			Log.debug("key entry", keyEntry);
			String pinTranReq = getPINTranRequest(encPINBlock, terminalPPK,
					cardNo, cryptoType, instPPK, isoBuffer, keyEntry);
			Log.debug("HSM REQ",pinTranReq);
			String hsmRsp = sendAndReceive(pinTranReq, mspAcr);
			Log.debug("HSM RES",hsmRsp);
			
			
			HsmResponse hsmGenRsp = new HsmResponse();
			String[] rspBuffer=hsmRsp.split(";");
			
			if(rspBuffer[0].contains(CMD_ERROR))
			{
				hsmGenRsp.setRspCode(rspBuffer[1].trim().substring(2));
				hsmGenRsp.setRspCode(rspBuffer[2].trim().substring(2));
				hsmGenRsp.setRspDesc(rspBuffer[3].trim().substring(2));
				Log.debug("Error response from HSM", hsmGenRsp.getErrCode());
				throw new PosException(Constants.ERR_HSM_CONNECT);
			}
			else if(rspBuffer[2].substring(2).equals(BB_PINTRAN_RES_SUCCESS)) {
				
				hsmRspBean.setPinBlock(rspBuffer[1].substring(2));
				hsmRspBean.setRspCode("00");
			} 
			else
			{
				Log.debug("Error response from HSM", hsmGenRsp.getErrCode());
				throw new PosException(Constants.ERR_HSM_CONNECT);
			}
			
		} catch (Exception e) {
			Log.error(this.getClass() + " Pin Translation failed ", e);
		}
		return hsmRspBean;
	}

	@Override
	public String verifyPIN(String encPINBlock, String instPPK, String cardNo,
			String instPVK, String validationData, String offset,
			String checkLen) {
		return null;
	}

	@Override
	public HsmResponse importZonalPinKey(String kirSpec, String zpkKey,
			String zmkCheckValue, String keyLengthType, String checkDigit)
			throws PosException {
		
		return null;
	}

	public static String appendIpHeader(String psMessage) throws Exception {
		int piLen = psMessage.length();
		char pcTe1 = (char) (piLen / 256);
		char pcTe = (char) (piLen % 256);
		psMessage = "" + pcTe1 + "" + pcTe + psMessage;
		return psMessage;

	}

	@Override
	public String translateP2Pcmd(IsoBuffer clientBuffer, HsmData hsmData,
			String mspAcr) throws PosException {

		
		String decData = "";
		try {
			
			String p2pData = clientBuffer.get(Constants.DE48);
			int p2pKsnlen = IsoUtil.hex2decimal(IsoUtil.hex2AsciiChar(clientBuffer.get(Constants.DE48).substring(0, 4)));
			String p2pKsn = p2pData.substring(4, (p2pKsnlen * 2) + 4);
			StringBuilder reqBuffer = new StringBuilder(MSG_HEADER
					+ CMD_P2PE);
			reqBuffer.append("FS"+FS_P2PE_COMMAND_MODE+DELIMITER);
			reqBuffer.append("AK"+p2pData+DELIMITER);
			reqBuffer.append("GL"+hsmData.getP2peTerminalPPK()+DELIMITER);
			reqBuffer.append("GM"+p2pKsn+DELIMITER);
			reqBuffer.append("TD"+TD_P2PE_DEC_MAJOR_KEY+DELIMITER);
			reqBuffer.append(MSG_TAIL);

			
			Log.debug("HSM REQ",reqBuffer.toString());
			String rspData = sendAndReceive(reqBuffer.toString(), mspAcr);
			Log.debug("HSM RES",rspData);
			
			
			String[] resBuffer=rspData.split(";");
			decData=resBuffer[2].trim().substring(2);
			
	
		} catch (PosException e) {
			Log.error("FutureX HSM ", e);
			throw new PosException(Constants.ERR_HSM_CONNECT);
		}
		return decData;
	}

	@Override
	public HsmResponse importZonalPinKey73Cmd(String kirSpec, String zpkKey,
			String zmkCheckValue, String keyLengthType, String mspAcr)
			throws PosException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String decToHex(String cmd) {
		return Util.appendChar(Util.decToHex(cmd.length() / 2), '0', 4, true)
				+ cmd;
	}

}

