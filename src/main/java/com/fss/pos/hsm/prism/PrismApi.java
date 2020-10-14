package com.fss.pos.hsm.prism;

import java.util.ArrayList;
import java.util.List;

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

@Hsm("3")
public class PrismApi extends AbstractHsmApi {
	
private static final List<String> EMV_TAGS_MAP;
	
	static{
		EMV_TAGS_MAP = new ArrayList<String>();
		EMV_TAGS_MAP.add("57");
	}

	@Override
	public HsmResponse importZonalPinKey(String kirSpec, String zpkKey,
			String zmkCheckValue, String keyLengthType, String checkDigit)
			throws PosException {
		StringBuilder cmd = new StringBuilder("EDF12000380100009500");
		cmd.append(kirSpec);
		cmd.append("00"); // parent key parity
		cmd.append(zmkCheckValue);
		cmd.append("00"); // mode
		cmd.append("02"); // key type
		cmd.append(zpkKey);
		cmd.append(checkDigit);
		cmd.append("02"); // key parity
		String requestCommand = appendLength(cmd.toString());
		String mspAcr = "";
		String rspData = sendAndReceive(requestCommand, mspAcr);
		int idx = 0;
		idx += 18;

		String errCode = rspData.substring(idx, idx += 2);
		if (Constants.SUCCESS.equals(errCode)) {
			HsmResponse hsmRsp = new HsmResponse();
			hsmRsp.setErrCode(errCode);
			hsmRsp.setRspCode(errCode);

			int keylen = 0;
			if (Constants.HSM_SINGLE_LENGTH.equals(keyLengthType))
				keylen = 16;
			else if (Constants.HSM_DOUBLE_LENGTH.equals(keyLengthType))
				keylen = 32;
			else if (Constants.HSM_TRIPLE_LENGTH.equals(keyLengthType))
				keylen = 48;

			hsmRsp.setLmkEncryptedKey(rspData.substring(idx, idx += keylen));
			hsmRsp.setChecksum(rspData.substring(idx, idx += 8));
			return hsmRsp;
		} else {
			Log.debug("Error Response from Prism", errCode);
			throw new PosException(Constants.ERR_HSM_CONNECT);
		}
	}

	@Override
	public HsmResponse generateKey(String ktmSpec, String keyType,
			String keyLengthType, String checkDigit, String mspAcr) throws PosException {
		StringBuilder cmd = new StringBuilder("EDF12000380100009300");
		cmd.append(ktmSpec);
		cmd.append("00"); // parity
		cmd.append(checkDigit);
		cmd.append("00");// mode - ECB
		if (Constants.HSM_PIN_ENC_KEY.equals(keyType))
			cmd.append("02");
		else
			cmd.append("00");
		cmd.append("00");
		String requestCommand = appendLength(cmd.toString());
		return getResponse(requestCommand, keyLengthType, mspAcr);
	}

	@Override
	public HsmResponse translatePIN(String encPINBlock, String terminalPPK,
			String tpkCheckValue, String cardNo, String keyLengthType,
			String instPPK, String checkDigit, String mspAcr, IsoBuffer isoBuffer, String keyEntry)
			throws PosException {
		StringBuilder cmd = new StringBuilder("EDF120003801000079");
		cmd.append("00"); // src key space id
		cmd.append("00");// dest key space id
		cmd.append("01");// src enc method
		cmd.append(terminalPPK);
		cmd.append("00"); // tpk parity
		cmd.append(tpkCheckValue);
		cmd.append("000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
		cmd.append("02");// dest key size
		cmd.append(instPPK);
		cmd.append("02"); // dest key parity
		cmd.append(checkDigit);
		cmd.append("01");// src format
		cmd.append("01");// dest format
		String anb = Util.getANBFromCardNo(cardNo);
		StringBuilder anbBuffer = new StringBuilder("00000000");
		for (char c : anb.toCharArray()) {
			anbBuffer.append("0");
			anbBuffer.append(c);
		}
		cmd.append(anbBuffer);
		cmd.append(anbBuffer);
		cmd.append(encPINBlock);
		String requestCommand = appendLength(cmd.toString());
		String rspData = sendAndReceive(requestCommand, mspAcr);
		//Log.debug("TRANSLATE PRISM RSP", rspData);
		int idx = 0;
		idx += 18;

		String errCode = rspData.substring(idx, idx += 2);
		if (Constants.SUCCESS.equals(errCode)) {
			HsmResponse hsmRsp = new HsmResponse();
			hsmRsp.setErrCode(errCode);
			hsmRsp.setRspCode(errCode);
			hsmRsp.setPinBlock(rspData.substring(idx, idx + 16));
			return hsmRsp;
		} else {
			Log.debug("Error Response from Prism", errCode);
			throw new PosException(Constants.ERR_HSM_CONNECT);
		}
	}

	@Override
	public String verifyPIN(String encPINBlock, String instPPK, String cardNo,
			String instPVK, String validationData, String offset,
			String checkLen) throws PosException {
		// TODO Auto-generated method stub
		return null;
	}

	private String appendLength(String cmd) {
		return Util.appendChar(Util.decToHex(cmd.length() / 2), '0', 4, true)
				+ cmd;
	}

	public HsmResponse getResponse(String request, String keyLengthType, String mspAcr) throws PosException {
		try {
			String rspData = sendAndReceive(request, mspAcr);
			int idx = 0;
			idx += 18;
			String errCode = rspData.substring(idx, idx += 2);

			if (Constants.SUCCESS.equals(errCode)) {
				HsmResponse hsmRsp = new HsmResponse();
				int keylen = 0;
				if (Constants.HSM_SINGLE_LENGTH.equals(keyLengthType))
					keylen = 16;
				else if (Constants.HSM_DOUBLE_LENGTH.equals(keyLengthType))
					keylen = 32;
				else if (Constants.HSM_TRIPLE_LENGTH.equals(keyLengthType))
					keylen = 48;
				hsmRsp.setErrCode(errCode);
				hsmRsp.setRspCode(errCode);
				hsmRsp.setLmkEncryptedKey(rspData.substring(idx, idx += keylen));
				hsmRsp.setChecksum(rspData.substring(idx, idx += 8));
				hsmRsp.setParentEncryptedKey(rspData.substring(idx,
						idx += keylen));
				return hsmRsp;
			} else {
				Log.debug("Error Response from Prism", errCode);
				throw new PosException(Constants.ERR_HSM_CONNECT);
			}
		} catch (Exception e) {
			Log.error("Prism HSM ", e);
			throw new PosException(Constants.ERR_HSM_CONNECT);
		}
	}

	//added by bebismita
	@Override
	public String translateP2Pcmd(IsoBuffer clientBuffer, HsmData hsmData,
			String mspAcr) throws PosException {
		String status = "";
		try {
			String p2pData = clientBuffer.get(Constants.DE48);
			int p2pKsnlen = IsoUtil.hex2decimal(IsoUtil.hex2AsciiChar(clientBuffer.get(Constants.DE48).substring(0, 4)));
			String p2pKsn = p2pData.substring(4, (p2pKsnlen * 2) + 4);
			StringBuilder cmd = new StringBuilder("EDF1200038010000C1");
			cmd.append("0C"); // key type
			cmd.append(hsmData.getP2peTerminalPPK());
		//	cmd.append("0A87B4C53075BFBDE885038C72F960BF");
			cmd.append("00"); //BDK Parity
			cmd.append(hsmData.getP2peCheckSum());
		//	cmd.append("9D5F6B5D");
			cmd.append("C9"); //mode
			cmd.append(Util.appendChar(p2pKsn, '0', 48, false)); //KSN
			String p2pVal = p2pData.substring((p2pKsnlen * 2) + 4);
			cmd.append(appendLength(p2pVal));
			String requestCommand = appendLength(cmd.toString());
			//String requestCommand = appendLength("EDF1200038010000C10C0A87B4C53075BFBDE885038C72F960BF009D5F6B5DC9FFFF073522000000000C00000000000000000000000000000035333030333530386FCA76A3A8EA0AD43030323071F52B948CD026EA5656DE1C56E3D2ED02A526EB5D8DCADE8E94B701359DC4843030");
			Log.debug("Prism Pintranslate Command", requestCommand);
			String rspData = sendAndReceive(requestCommand, mspAcr);
			Log.debug("Response Data", rspData);
	//		String errCode = rspData.substring(idx, idx += 2);
			int idx = rspData.indexOf("C");
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
			}
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
		StringBuilder cmd = new StringBuilder("EDF12000380100007300");
		cmd.append(kirSpec);
		cmd.append("00"); // parent key parity
		cmd.append(zmkCheckValue);
		cmd.append("00"); // mode
		cmd.append("02"); // key type
		cmd.append(zpkKey);
		cmd.append("02"); // key parity
		String requestCommand = appendLength(cmd.toString());
		String rspData = sendAndReceive(requestCommand, mspAcr);
		Log.debug("Response from Prism:::", rspData.toString());
		int idx = rspData.indexOf("7");
		idx += 2;
		/*int idx = 0;
		idx += 18;*/
		HsmResponse hsmRsp = new HsmResponse();
		String errCode = rspData.substring(idx, idx += 2);
		if (Constants.SUCCESS.equals(errCode)) {
			
			hsmRsp.setErrCode(errCode);
			hsmRsp.setRspCode(errCode);

			int keylen = 0;
			if (Constants.HSM_SINGLE_LENGTH.equals(keyLengthType))
				keylen = 16;
			else if (Constants.HSM_DOUBLE_LENGTH.equals(keyLengthType))
				keylen = 32;
			else if (Constants.HSM_TRIPLE_LENGTH.equals(keyLengthType))
				keylen = 48;

			hsmRsp.setLmkEncryptedKey(rspData.substring(idx, idx += keylen));
			hsmRsp.setChecksum(rspData.substring(idx, idx += 8));
			return hsmRsp;
		} else {
			Log.debug("Error Response from Prism", errCode);
			hsmRsp.setErrCode(errCode);
			return hsmRsp;
		}
	}

	





}
