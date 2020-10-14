package com.fss.pos.hsm.safenet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fss.pos.base.api.hsm.Hsm;
import com.fss.pos.base.api.hsm.HsmData;
import com.fss.pos.base.api.hsm.HsmResponse;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.hsm.AbstractHsmApi;

@Hsm("1")
public class SafenetApi extends AbstractHsmApi {

	private static final String GEN_CMD = "EE0400";
	private static final String GEN_FM = "00";
	private static final String TRAN_CMD = "EE0602";
	private static final String TRAN_FM = "00";
	private static final String VERIFY_CMD = "EE0603";
	private static final String VERIFY_FM = "00";
	private static final String CARDNO_APPEND = "01";
	private static final String IMPORT_CMD = "EE0200";
	private static final String IMPORT_FM = "00";

	private static final Map<String, String> KEY_ALGORITHMS;

	static {
		HashMap<String, String> keyAlgorithms = new HashMap<String, String>();
		keyAlgorithms.put(Constants.HSM_SINGLE_LENGTH
				+ Constants.HSM_DATA_ENC_KEY, "0000");
		keyAlgorithms.put(Constants.HSM_SINGLE_LENGTH
				+ Constants.HSM_PIN_ENC_KEY, "0002");
		keyAlgorithms.put(Constants.HSM_SINGLE_LENGTH
				+ Constants.HSM_KEY_ENC_KEY, "0008");
		keyAlgorithms.put(Constants.HSM_DOUBLE_LENGTH
				+ Constants.HSM_DATA_ENC_KEY, "0100");
		keyAlgorithms.put(Constants.HSM_DOUBLE_LENGTH
				+ Constants.HSM_PIN_ENC_KEY, "0200");
		keyAlgorithms.put(Constants.HSM_DOUBLE_LENGTH
				+ Constants.HSM_KEY_ENC_KEY, "0800");
		keyAlgorithms.put(Constants.HSM_TRIPLE_LENGTH
				+ Constants.HSM_DATA_ENC_KEY, "0010");
		keyAlgorithms.put(Constants.HSM_TRIPLE_LENGTH
				+ Constants.HSM_PIN_ENC_KEY, "0020");
		keyAlgorithms.put(Constants.HSM_TRIPLE_LENGTH
				+ Constants.HSM_KEY_ENC_KEY, "0080");
		KEY_ALGORITHMS = Collections.unmodifiableMap(keyAlgorithms);
	}

	@Override
	public HsmResponse generateKey(final String ktmSpec, final String keyType,
			final String keyLengthType, String checkDigit, String mspAcr) throws PosException {
		if (Util.isAnyNullOrEmpty(ktmSpec, keyType, keyLengthType))
			throw new PosException(Constants.ERR_PIN_CONFIG);
		try {
			String request = getGenerationRequest(ktmSpec, keyType,
					keyLengthType);
			return getResponse(request, keyLengthType, mspAcr);
		} catch (Exception e) {
			Log.error("Safenet HSM ", e);
			throw new PosException(Constants.ERR_HSM_CONNECT);
		}
	}

	public String getGenerationRequest(String ktmSpec, String keyType,
			String keyLengthType) throws PosException {
		StringBuilder reqBuffer = new StringBuilder(GEN_CMD + GEN_FM);
		reqBuffer.append(ktmSpec);
		String keyAlgoId = keyLengthType + keyType;
		if (!KEY_ALGORITHMS.containsKey(keyAlgoId))
			throw new PosException(null);
		reqBuffer.append(KEY_ALGORITHMS.get(keyAlgoId));
		return reqBuffer.toString();
	}

	public String getPINTranRequest(String encPINBlock, String terminalPPK,
			String cardNo, String instPPK) {
		StringBuilder reqBuffer = new StringBuilder(TRAN_CMD + TRAN_FM);
		reqBuffer.append(encPINBlock);
		reqBuffer.append(terminalPPK);
		reqBuffer.append(CARDNO_APPEND);
		reqBuffer.append(Util.getANBFromCardNo(cardNo));
		reqBuffer.append(CARDNO_APPEND);
		reqBuffer.append(instPPK);
		return reqBuffer.toString();
	}

	public String getPINVerifyRequest(String encPINBlock, String instPPK,
			String cardNo, String instPVK, String validationData,
			String offset, String checkLen) {
		StringBuilder reqBuffer = new StringBuilder(VERIFY_CMD + VERIFY_FM);
		reqBuffer.append(encPINBlock);
		reqBuffer.append(instPPK);
		reqBuffer.append(CARDNO_APPEND);
		reqBuffer.append(Util.getANBFromCardNo(cardNo));
		reqBuffer.append(instPVK);
		reqBuffer.append(validationData);
		reqBuffer.append(offset);
		reqBuffer.append(checkLen);
		return reqBuffer.toString();
	}

	@Override
	public String verifyPIN(String encPINBlock, String instPPK, String cardNo,
			String instPVK, String validationData, String offset,
			String checkLen) throws PosException {
		try {
			String mspAcr = "";
			return sendAndReceive(getPINVerifyRequest(encPINBlock, instPPK,
					cardNo, instPVK, validationData, offset, checkLen), mspAcr);
		} catch (Exception e) {
			Log.error("Pin Verification Failed ", e);
			throw new PosException(Constants.ERR_HSM_CONNECT);
		}
	}

	@Override
	public HsmResponse translatePIN(String encPINBlock, String terminalPPK,
			String tpkCheckValue, String cardNo, String cryptoType,
			String instPPK, String checkDigit, String mspAcr, IsoBuffer isoBuffer, String keyEntry)
			throws PosException {
		if (Util.isAnyNullOrEmpty(encPINBlock, terminalPPK, cardNo, instPPK))
			throw new PosException(Constants.ERR_PIN_CONFIG);
		HsmResponse hsmRspBean = new HsmResponse();
		try {
			String pinTranReq = getPINTranRequest(encPINBlock, terminalPPK,
					cardNo, instPPK);
			Log.debug("encPINBlock", encPINBlock);
			Log.debug("terminalPPK", terminalPPK);
			Log.debug("cardNo", cardNo);
			Log.debug("instPPK", instPPK);
			String hsmRsp = sendAndReceive(pinTranReq, mspAcr);
			String hsmRspCode = hsmRsp.substring(6, 8);
			hsmRspBean.setRspCode(hsmRspCode);
			hsmRspBean.setRspDesc(ERROR_MAP.containsKey(hsmRspCode) ? ERROR_MAP
					.get(hsmRspCode) : DEFAULT_ERROR);
			Log.info("Pin translation HSM Response Code", hsmRspCode
					+ " Description : " + ERROR_MAP.get(hsmRspCode));
			if (Constants.SUCCESS.equals(hsmRspCode))
				hsmRspBean.setPinBlock(hsmRsp.substring(8, hsmRsp.length()));
			return hsmRspBean;
		} catch (Exception e) {
			Log.error("SafenetApi translatePIN ", e);
			throw new PosException(Constants.ERR_HSM_CONNECT);
		}
	}

	@Override
	public HsmResponse importZonalPinKey(String kirSpec, String zpkKey,
			String checkValue, String keyLengthType, String checkDigit)
			throws PosException {
		if (Util.isAnyNullOrEmpty(kirSpec, zpkKey, keyLengthType))
			throw new PosException(Constants.ERR_PIN_CONFIG);
		StringBuilder sb = new StringBuilder(IMPORT_CMD + IMPORT_FM);
		sb.append(kirSpec);
		sb.append("01");// PPK
		sb.append("00"); // ECB
		sb.append(Integer.toHexString(zpkKey.length() / 2));
		sb.append(zpkKey);
		try {
			try {
		
				String mspAcr = "";
				String rspData = sendAndReceive(sb.toString(), mspAcr);
				int indx = 6;
				String rspCode = rspData.substring(indx, indx + 2);
				indx += 2;
				HsmResponse hsmGenRsp = new HsmResponse();
				hsmGenRsp.setRspCode(rspCode);
				hsmGenRsp.setRspDesc(ERROR_MAP.containsKey(rspCode) ? ERROR_MAP
						.get(rspCode) : DEFAULT_ERROR);
				indx += 4;

				if (Constants.SUCCESS.equals(hsmGenRsp.getRspCode())) {

					int keylen = 0;
					if (Constants.HSM_SINGLE_LENGTH.equals(keyLengthType))
						keylen = 16;
					else if (Constants.HSM_DOUBLE_LENGTH.equals(keyLengthType))
						keylen = 32;
					else if (Constants.HSM_TRIPLE_LENGTH.equals(keyLengthType))
						keylen = 48;

					String rspKey = rspData.substring(indx, indx + keylen);
					indx += keylen;
					String rspCheckSum = rspData.substring(indx, indx + 6);

					hsmGenRsp.setParentEncryptedKey(rspKey);
					hsmGenRsp.setChecksum(rspCheckSum);
				}
				return hsmGenRsp;
			} catch (Exception e) {
				Log.error("SafenetApi importZonalPinKey ", e);
				throw new PosException(Constants.ERR_HSM_CONNECT);
			}
		} catch (Exception e) {
			Log.error("SafenetApi importZonalPinKey ", e);
			throw new PosException(Constants.ERR_HSM_CONNECT);
		}
	}

	public HsmResponse getResponse(String request, String keyLengthType, String mspAcr) throws PosException {
		try {
			String rspData = sendAndReceive(request, mspAcr);
			//EE04000A
			//EE040000011093E12C5044A7F12F27D96013390CCD3F1111AD3EE75F0348DD06A6BBE332A6B22B15D09329
			int indx = 6;
			String rspCode = rspData.substring(indx, indx + 2);
			indx += 2;
			HsmResponse hsmGenRsp = new HsmResponse();
			hsmGenRsp.setRspCode(rspCode);
			hsmGenRsp.setRspDesc(ERROR_MAP.containsKey(rspCode) ? ERROR_MAP
					.get(rspCode) : DEFAULT_ERROR);
			indx += 4;

			if (Constants.SUCCESS.equals(hsmGenRsp.getRspCode())) {

				int keylen = 0;
				if (Constants.HSM_SINGLE_LENGTH.equals(keyLengthType))
					keylen = 16;
				else if (Constants.HSM_DOUBLE_LENGTH.equals(keyLengthType))
					keylen = 32;
				else if (Constants.HSM_TRIPLE_LENGTH.equals(keyLengthType))
					keylen = 48;

				String rspKey = rspData.substring(indx, indx + keylen);
				indx += keylen;
				String rspLmkKey = rspData.substring(indx, indx + keylen + 4);
				indx = indx + keylen + 4;
				String rspCheckSum = rspData.substring(indx, indx + 6);

				hsmGenRsp.setParentEncryptedKey(rspKey);
				hsmGenRsp.setLmkEncryptedKey(rspLmkKey);
				hsmGenRsp.setChecksum(rspCheckSum);
			}
			return hsmGenRsp;
		} catch (Exception e) {
			Log.error("Safenet HSM ", e);
			throw new PosException(Constants.ERR_HSM_CONNECT);
		}
	}

	@Override
	public String translateP2Pcmd(IsoBuffer isoBuffer, HsmData hsmData,
			String mspAcr) throws PosException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HsmResponse importZonalPinKey73Cmd(String kirSpec, String zpkKey,
			String zmkCheckValue, String keyLengthType, String mspAcr)
			throws PosException {
		// TODO Auto-generated method stub
		return null;
	}



}
