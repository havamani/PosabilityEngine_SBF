package com.fss.pos.hsm.thales;

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

@Hsm("2")
public class ThalesApi extends AbstractHsmApi {

	private static final String MSG_HEADER = "RSM5";
	private static final String KEYGENERATION_CMDCODE = "A0";
	private static final String KEYTRANSLATION_CMDCODE = "CA";
	private static final String KEYTRANSLATION_G0_DUKPT_CODE = "G0";
	private static final String P2PCOMMAND = "M2";
	private static final String MSG_MODE = "1";
	private static final String TMK_TPK_KEYTYPE = "70D";
	private static final String SINGLE_KEYSCHEME = "Z";
	private static final String DOUBLE_KEYSCHEME = "U";
	private static final String DELIMITER = ";";
	private static final String TMK_FLAG = "1";
	private static final String ATALLA_VARIANT = "X";
	// private static final String KEYTRANSLATION_KEYSCHEME = "0";
	// private static final String KEYTRANSLATION_RESERVED = "0";
	// private static final String KEYTRANSLATION_KEYCHKVALTYPE = "1";

	private static final int DATA_LEN = 2;
	private static final int MSGHEADER_LEN = 4;
	private static final int RESPCODE_LEN = 2;
	private static final int ERRCODE_LEN = 2;
	private static final int KEYSCHEME_LEN = 1;
	private static final int CHECKDIGIT_LEN = 6;

	@Override
	public HsmResponse generateKey(final String ktmSpec, final String keyType,
			final String cryptoType, String checkDigit, String mspAcr)
			throws PosException {

		int indx = 0;
		try {
			String rspData = sendAndReceive(
					getGenRequest(ktmSpec, keyType, cryptoType), mspAcr);
			indx = DATA_LEN;
			indx += MSGHEADER_LEN;
			String rspCode = rspData.substring(indx, indx + RESPCODE_LEN);
			indx += RESPCODE_LEN;
			String errCode = rspData.substring(indx, indx + ERRCODE_LEN);
			indx += ERRCODE_LEN;
			HsmResponse hsmGenRsp = new HsmResponse();
			hsmGenRsp.setRspCode(rspCode);
			hsmGenRsp.setErrCode(errCode);
			hsmGenRsp.setRspDesc(ERROR_MAP.containsKey(rspCode) ? ERROR_MAP
					.get(rspCode) : DEFAULT_ERROR);

			if (Constants.SUCCESS.equals(hsmGenRsp.getErrCode())) {

				String key_Scheme = rspData.substring(indx, indx
						+ KEYSCHEME_LEN);
				indx += KEYSCHEME_LEN;

				int keylen = 0;
				if (SINGLE_KEYSCHEME.equals(key_Scheme)) {
					keylen = 16;
				} else if (DOUBLE_KEYSCHEME.equals(key_Scheme)) {
					keylen = 32;
				} else {
					keylen = 48;
				}

				String rspLmkKey = rspData.substring(indx, indx + keylen);
				indx += keylen;
				indx += KEYSCHEME_LEN;
				String rspKey = rspData.substring(indx, indx + keylen);
				indx = indx + keylen;
				String rspCheckSum = rspData.substring(indx, indx
						+ CHECKDIGIT_LEN);

				hsmGenRsp.setParentEncryptedKey(rspKey);
				hsmGenRsp.setLmkEncryptedKey(rspLmkKey);
				hsmGenRsp.setChecksum(rspCheckSum);
			} else {
				Log.debug("Error response from HSM", hsmGenRsp.getErrCode());
				throw new PosException(Constants.ERR_HSM_CONNECT);
			}
			return hsmGenRsp;
		} catch (Exception e) {
			Log.error("Thales HSM ", e);
			throw new PosException(Constants.ERR_HSM_CONNECT);
		}
	}

	public static String getGenRequest(String ktmSpec, String keyType,
			String cryptoType) throws Exception {
		String keyScheme = null;
		StringBuilder reqBuffer = new StringBuilder(MSG_HEADER
				+ KEYGENERATION_CMDCODE + MSG_MODE + TMK_TPK_KEYTYPE); // changed
																		// in
																		// rashmi
																		// sir
																		// pc
		if (Constants.HSM_SINGLE_LENGTH.equals(cryptoType))
			keyScheme = SINGLE_KEYSCHEME;
		else if (Constants.HSM_DOUBLE_LENGTH.equals(cryptoType))
			keyScheme = DOUBLE_KEYSCHEME;
		else if (Constants.HSM_TRIPLE_LENGTH.equals(cryptoType))
			keyScheme = DOUBLE_KEYSCHEME;
		reqBuffer.append(keyScheme);
		reqBuffer.append(DELIMITER);
		reqBuffer.append(TMK_FLAG);
		reqBuffer.append(keyScheme);
		reqBuffer.append(ktmSpec);
		reqBuffer.append(ATALLA_VARIANT);
		return appendIpHeader(reqBuffer.toString());
	}

	public String getPINTranRequest(String encPINBlock, String terminalPPK,
			String cardNo, String cryptoType, String instPPK,
			IsoBuffer isoBuffer, String keyEntry) throws Exception {
		if (isoBuffer.isFieldEmpty(Constants.DE44)
				&& keyEntry.equals(Constants.KEY_SCHEME_MASTER_SESSION)) {
			StringBuilder reqBuffer = new StringBuilder(MSG_HEADER
					+ KEYTRANSLATION_CMDCODE);
			String keyScheme = null;
			if (Constants.HSM_SINGLE_LENGTH.equals(cryptoType))
				keyScheme = SINGLE_KEYSCHEME;
			else if (Constants.HSM_DOUBLE_LENGTH.equals(cryptoType))
				keyScheme = DOUBLE_KEYSCHEME;
			reqBuffer.append(keyScheme);
			reqBuffer.append(terminalPPK);
			reqBuffer.append(keyScheme);
			reqBuffer.append(instPPK);
			reqBuffer.append("12");
			reqBuffer.append(encPINBlock);
			reqBuffer.append("01");
			reqBuffer.append("01");
			reqBuffer.append(Util.getANBFromCardNo(cardNo));
			return appendIpHeader(reqBuffer.toString());
		} else {
			// DUKPT
			StringBuilder reqBuffer = new StringBuilder(MSG_HEADER
					+ KEYTRANSLATION_G0_DUKPT_CODE);
			String keyScheme = null;
			if (Constants.HSM_SINGLE_LENGTH.equals(cryptoType))
				keyScheme = SINGLE_KEYSCHEME;
			else if (Constants.HSM_DOUBLE_LENGTH.equals(cryptoType))
				keyScheme = DOUBLE_KEYSCHEME;
			reqBuffer.append(keyScheme);
			reqBuffer.append(terminalPPK);
			reqBuffer.append(keyScheme);
			reqBuffer.append(instPPK);
			
			if(isoBuffer.get(Constants.DE44).length() == 16)
				reqBuffer.append("605");
			else if(isoBuffer.get(Constants.DE44).length() == 20)
				reqBuffer.append("A05");
			else
				reqBuffer.append("KSN");
			
			reqBuffer.append(isoBuffer.get(Constants.DE44));
			reqBuffer.append(encPINBlock);
			reqBuffer.append("01");
			reqBuffer.append("01");
			reqBuffer.append(Util.getANBFromCardNo(cardNo));
			Log.debug("request buffer", reqBuffer.toString());
			return appendIpHeader(reqBuffer.toString());
		}

	}

	@Override
	public HsmResponse translatePIN(String encPINBlock, String terminalPPK,
			String tpkCheckValue, String cardNo, String cryptoType,
			String instPPK, String checkDigit, String mspAcr,
			IsoBuffer isoBuffer, String keyEntry) {

		HsmResponse hsmRspBean = new HsmResponse();
		int indx = 0;
		try {

			Log.debug("key entry", keyEntry);
			String pinTranReq = getPINTranRequest(encPINBlock, terminalPPK,
					cardNo, cryptoType, instPPK, isoBuffer, keyEntry);
			// String val =
			// "RSM5G0~D6C1F48AD9C8BAF2C2B3A779A467A8BAFDF633D1A7EB4FDE5D65E1DF1F0BB375605C999F40001000115E05E329598A7B6A40101890000020006";
			// String pinTranReq = appendIpHeader(val);
			Log.debug("inside thales", pinTranReq + ":" + mspAcr);
			String hsmRsp = sendAndReceive(pinTranReq, mspAcr);
			Log.debug("hsm resp", hsmRsp);
			indx = DATA_LEN;
			indx += MSGHEADER_LEN;
			String rspCode = hsmRsp.substring(indx, indx + RESPCODE_LEN);
			indx += RESPCODE_LEN;
			String errCode = hsmRsp.substring(indx, indx + ERRCODE_LEN);
			indx += ERRCODE_LEN;
			HsmResponse hsmGenRsp = new HsmResponse();
			hsmGenRsp.setRspCode(rspCode);
			hsmGenRsp.setErrCode(errCode);
			hsmGenRsp.setRspDesc(ERROR_MAP.containsKey(rspCode) ? ERROR_MAP
					.get(rspCode) : DEFAULT_ERROR);

			if (Constants.SUCCESS.equals(errCode)) {
				indx += 2;// PIN length
				hsmRspBean.setPinBlock(hsmRsp.substring(indx, indx + 16));
				indx += 16;
				hsmRspBean.setRspCode(errCode);
			} else {
				Log.debug("Error response from HSM", errCode);
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
		HsmResponse hsmRspBean = new HsmResponse();
		int indx = 0;
		try {
			// String pinTranReq = getPINTranRequest(encPINBlock, terminalPPK,
			// cardNo, cryptoType, instPPK);

			String hsmRsp = sendAndReceive(null, null);
			indx = DATA_LEN;
			indx += MSGHEADER_LEN;
			String rspCode = hsmRsp.substring(indx, indx + RESPCODE_LEN);
			indx += RESPCODE_LEN;
			String errCode = hsmRsp.substring(indx, indx + ERRCODE_LEN);
			indx += ERRCODE_LEN;
			HsmResponse hsmGenRsp = new HsmResponse();
			hsmGenRsp.setRspCode(rspCode);
			hsmGenRsp.setErrCode(errCode);
			hsmGenRsp.setRspDesc(ERROR_MAP.containsKey(rspCode) ? ERROR_MAP
					.get(rspCode) : DEFAULT_ERROR);

			if (Constants.SUCCESS.equals(errCode)) {
				String key_Scheme = hsmRsp
						.substring(indx, indx + KEYSCHEME_LEN);
				indx += KEYSCHEME_LEN;

				int keylen = 0;
				if (SINGLE_KEYSCHEME.equals(key_Scheme)) {
					keylen = 16;
				} else if (DOUBLE_KEYSCHEME.equals(key_Scheme)) {
					keylen = 32;
				} else {
					keylen = 48;
				}

				hsmRspBean.setPinBlock(hsmRsp.substring(indx, indx + keylen));
				indx += keylen;
				hsmRspBean.setChecksum(hsmRsp.substring(indx, indx
						+ CHECKDIGIT_LEN));
			} else {
				Log.debug("Error response from HSM", errCode);
				throw new PosException(Constants.ERR_HSM_CONNECT);
			}
		} catch (Exception e) {
			Log.error(this.getClass() + " Pin Translation failed ", e);
		}
		return hsmRspBean;
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

		String status = "";
		try {
			String p2pData = clientBuffer.get(Constants.DE48);
			int p2pKsnlen = IsoUtil.hex2decimal(IsoUtil.hex2AsciiChar(clientBuffer.get(Constants.DE48).substring(0, 4)));
			String p2pKsn = p2pData.substring(4, (p2pKsnlen * 2) + 4);
			StringBuilder reqBuffer = new StringBuilder(MSG_HEADER
					+ P2PCOMMAND);
			String keyScheme = null;
			/*if (Constants.HSM_SINGLE_LENGTH.equals(cryptoType))
			keyScheme = SINGLE_KEYSCHEME;
		else if (Constants.HSM_DOUBLE_LENGTH.equals(cryptoType))
			keyScheme = DOUBLE_KEYSCHEME;*/
		//reqBuffer.append(keyScheme);
			reqBuffer.append("00");//ECB mode
			reqBuffer.append("1");//input format flag
			reqBuffer.append("1");//output format flag
			reqBuffer.append("009");//lmk key type
			if(p2pKsnlen== 16)
				reqBuffer.append("605");
			else if(p2pKsnlen == 20)
				reqBuffer.append("A05");
			else
				reqBuffer.append("KSN");
			reqBuffer.append(p2pKsn); //KSN
			String p2pVal = p2pData.substring((p2pKsnlen * 2) + 4);
			reqBuffer.append(appendLength(p2pVal));
			
			

			String requestCommand = appendLength(reqBuffer.toString());
			//String requestCommand = appendLength("EDF1200038010000C10C0A87B4C53075BFBDE885038C72F960BF009D5F6B5DC9FFFF073522000000000C00000000000000000000000000000035333030333530386FCA76A3A8EA0AD43030323071F52B948CD026EA5656DE1C56E3D2ED02A526EB5D8DCADE8E94B701359DC4843030");
			Log.debug("Thales Pintranslate Command", requestCommand);
			String rspData = sendAndReceive(requestCommand, mspAcr);
			Log.debug("Response Data", rspData);
	//		String errCode = rspData.substring(idx, idx += 2);
			/*int idx = rspData.indexOf("C");
			idx += 2;
			String errCode = rspData.substring(idx, idx += 2);
			if (Constants.SUCCESS.equals(errCode)) {
				String respData = rspData.substring(idx+12 , rspData.length()); 
				String respDataP2P = respData.substring(4);
				if(EMV_TAGS_MAP.contains(respDataP2P.substring(0, 2)))
				{
					int len = IsoUtil.hex2decimal(respDataP2P.substring(2, 4));
					String track2Data = respDataP2P.substring(4, len * 2);
					track2Data = track2Data.replace("D", "=");
					clientBuffer.put(Constants.DE35, track2Data);
					String[] arr = track2Data.split("=");
					clientBuffer.put(Constants.DE2, clientBuffer.isFieldEmpty(Constants.DE2) ? Constants.DISABLE : arr[0]);
					clientBuffer.put(Constants.DE14, clientBuffer.isFieldEmpty(Constants.DE14) ? Constants.DISABLE : arr[1].substring(0, 4));
					
				}
				status = errCode;
			} else {
				Log.debug("Error Response from Prism for P2PE", errCode);
				status = errCode;
				throw new PosException(Constants.ERR_HSM_CONNECT);
			}*/
		} catch (PosException e) {
			Log.error("Prism HSM ", e);
			throw new PosException(Constants.ERR_HSM_CONNECT);
		}
		return status;
	
	}

	@Override
	public HsmResponse importZonalPinKey73Cmd(String kirSpec, String zpkKey,
			String zmkCheckValue, String keyLengthType, String mspAcr)
			throws PosException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String appendLength(String cmd) {
		return Util.appendChar(Util.decToHex(cmd.length() / 2), '0', 4, true)
				+ cmd;
	}



}
