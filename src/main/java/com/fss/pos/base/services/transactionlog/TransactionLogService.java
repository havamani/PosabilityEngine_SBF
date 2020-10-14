package com.fss.pos.base.services.transactionlog;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.pos.base.api.db.storedprocedure.ResponseStatus;
import com.fss.pos.base.api.db.storedprocedure.StoredProcedureInfo;
import com.fss.pos.base.api.host.HostData;
import com.fss.pos.base.api.host.TimeoutData;
import com.fss.pos.base.api.host.ZonalKeysModel;
import com.fss.pos.base.api.hsm.HsmData;
import com.fss.pos.base.commons.Config;
import com.fss.pos.base.commons.EmvTags;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.StaticStore;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.base.commons.utils.PosUtil;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.base.commons.utils.security.SecureData;
import com.fss.pos.base.commons.utils.security.SecurityService;
import com.fss.pos.base.factory.ApiFactory;
import com.fss.pos.client.services.operator.TerminalOperator;

@Service
public class TransactionLogService {

	private static final List<String> ADJUSTMENT_TXNS;
	private static ObjectMapper objectMapper = new ObjectMapper();

	static {
		ADJUSTMENT_TXNS = new ArrayList<String>();
		ADJUSTMENT_TXNS.add(Constants.PROC_CODE_TIP + Constants.ZERO
				+ Constants.ZERO);
		ADJUSTMENT_TXNS.add(Constants.PROC_CODE_VOID + Constants.ZERO
				+ Constants.ZERO);
		ADJUSTMENT_TXNS.add(Constants.PROC_CODE_SALE_MOTO_COMPLETION
				+ Constants.CONDITION_CODE_COMPLETION);
		ADJUSTMENT_TXNS.add(Constants.PROC_CODE_SALE_MOTO_COMPLETION
				+ Constants.CONDITION_CODE_ADVICE);
	}

	@Autowired
	private ApiFactory apiFactory;

	@Autowired
	private SecurityService securityUtils;

	@Autowired
	private Config config;

	@SuppressWarnings("unchecked")
	public TransactionResponse updateTxnLog(String pan, String tid, String rrn,
			String hostStationName, String responseCode, String approvalCode,
			int status, String merchantId, String txnType, String mspAcr,
			String stan, String transmissionDateTime, String procedurename) {
		TransactionResponse txnResponse = new TransactionResponse();
		try {
			List<Object> params = new ArrayList<Object>();
			params.add(tid);
			params.add(rrn);
			params.add(hostStationName);
			params.add(responseCode);
			params.add(approvalCode);
			params.add(status);
			params.add(merchantId.trim());
			params.add(txnType);
			if (pan != null) {
				try {
					params.add(PosUtil.maskCardNumber(pan, mspAcr));
				} catch (Exception e) {
					Log.error("Card Masking error", e);
					params.add(null);
				}
				params.add(Util.hashSHA512(pan, Constants.EMPTY_STRING));
			} else {
				params.add(null);
				params.add(null);
			}
			params.add(stan);
			params.add("*".equals(transmissionDateTime) ? null
					: transmissionDateTime);

			ArrayList<Class<?>> classList = new ArrayList<Class<?>>();
			classList.add(ResponseStatus.class);
			classList.add(TransactionResponse.class);
			List<Object> objList = apiFactory.getStoredProcedureApi().getBean(
					params, procedurename, classList, mspAcr);
			String rspStatus = ((List<ResponseStatus>) objList.get(0)).get(0)
					.getStatus();
			Log.debug("Response from " + procedurename, rspStatus);
			if (Constants.SUCCESS.equals(rspStatus)) {
				if (!((List<TransactionResponse>) objList.get(1)).isEmpty()) {
					txnResponse = ((List<TransactionResponse>) objList.get(1))
							.get(0);
				} else {
					Log.debug("No data in updateTxnLog", "");
					txnResponse.setResponseCode(Constants.ERR_SYSTEM_ERROR);
				}
			} else {
				txnResponse.setResponseCode(rspStatus);
				Log.info("Error in db while logging response ", rspStatus);
			}
		} catch (Exception e) {
			Log.error(this.getClass() + " updateTxnLog ", e);
			// throw new PosException(Constants.ERR_SYSTEM_ERROR);
			// txnResponse.setResponseCode(Constants.ERR_SYSTEM_ERROR);
		}
		return txnResponse;
	}

	@SuppressWarnings("unchecked")
	public String updateReversalLog(String tid, String stan, String merchantId,
			String txnType, String invoice, String mspAcr) throws PosException {
		try {
			List<Object> params = new ArrayList<Object>();
			params.add(tid);
			params.add(stan);
			params.add(merchantId.trim());
			params.add(invoice);
			ArrayList<Class<?>> classList = new ArrayList<Class<?>>();
			classList.add(ResponseStatus.class);
			classList.add(TransactionResponse.class);
			List<Object> objList = apiFactory.getStoredProcedureApi().getBean(
					params, StoredProcedureInfo.UPDATE_REVERSAL, classList,
					mspAcr);
			String rspStatus = ((List<ResponseStatus>) objList.get(0)).get(0)
					.getStatus();
			Log.debug("Response from " + StoredProcedureInfo.UPDATE_REVERSAL,
					rspStatus);
			return rspStatus;
		} catch (Exception e) {
			Log.error(this.getClass() + " updateReversalLog ", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
	}

	public List<Object> fetchData(String procedureName, IsoBuffer isoBuffer,
			String mspAcr, String srcStation, TerminalOperator operator,
			boolean isOffline, String msgVersion, Map<EmvTags, String> emvTags)
			throws Exception {
		String pan = isoBuffer.get(Constants.DE2, null);
		String txnType = isoBuffer.get(Constants.DE3).substring(0, 2)
				+ isoBuffer.get(Constants.DE25);

		List<Object> params = new ArrayList<Object>();
		params.add(isoBuffer.get(Constants.DE11, null));
		params.add(isoBuffer.get(Constants.DE62, null));
		params.add(pan);
		params.add(isoBuffer.get(Constants.DE22, null));
		params.add(isoBuffer.get(Constants.DE3).substring(2, 4));
		params.add(isoBuffer.get(Constants.DE7, null));
		params.add(txnType);
		params.add(isoBuffer.get(Constants.DE4, Constants.ZERO));
		params.add(isoBuffer.get(Constants.DE54, Constants.ZERO));
		params.add(isoBuffer.get(Constants.DE42).trim());
		params.add(isoBuffer.get(Constants.DE47, null));
		params.add(isoBuffer.get(Constants.DE41));
		params.add(srcStation);
		params.add((operator == null || operator.getSessionId() == null || operator
				.getSessionId().isEmpty()) ? Constants.ZERO : operator
				.getSessionId());
		params.add(isoBuffer.get(Constants.ISO_MSG_TYPE));
		params.add(isOffline ? Constants.ENABLE : Constants.DISABLE);
		params.add(isoBuffer.isFieldEmpty(Constants.DE54) ? Constants.DISABLE
				: Constants.ENABLE);
		params.add((!isoBuffer.isFieldEmpty(Constants.DE6) && !isoBuffer
				.isFieldEmpty(Constants.DE51)) ? 1 : 0);// dcc
		SecureData sd = StaticStore.deks.get(mspAcr);

		try {
			byte[] encPan;
			if (mspAcr.equals("AFP") || mspAcr.equals("AUB")) {// afs
				encPan = securityUtils.encryptText(pan.getBytes(),
						config.getAliasNamePrefix() + "AHB", sd.getKekCode(),
						sd.getDekBytes(), mspAcr, sd.getEncryptType());
			} else {
				encPan = securityUtils.encryptText(pan.getBytes(),
						config.getAliasNamePrefix() + mspAcr, sd.getKekCode(),
						sd.getDekBytes(), mspAcr, sd.getEncryptType());
			}
			params.add(new String(Base64.encodeBase64(encPan)));
		} catch (Exception e) {
			Log.error("Card Encryption", e);
			params.add(null);
		}
		params.add(pan == null ? null : Util.hashSHA512(pan,
				Constants.EMPTY_STRING));
		params.add((!isoBuffer.isFieldEmpty(Constants.DE6) && !isoBuffer
				.isFieldEmpty(Constants.DE51)) ? isoBuffer.get(Constants.DE6)
				: 0); // dcc amt
		params.add(isoBuffer.get(Constants.DE42).trim());
		params.add(msgVersion);
		params.add(ADJUSTMENT_TXNS.contains(txnType) ? Constants.ENABLE
				: Constants.DISABLE);
		params.add(isoBuffer.isFieldEmpty(Constants.DE37) ? null : isoBuffer
				.get(Constants.DE37));
		// Card Masking Number//
		if (pan != null) {
			String maskedno = PosUtil.maskCardNumber(pan, mspAcr);
			params.add(maskedno);
		} else
			params.add(null);
		if (!(isoBuffer.get(Constants.DE6).equals(Constants.DISABLED_FIELD)))
			params.add(isoBuffer.get(Constants.DE63));
		else {
			params.add(Constants.ZERO);
		}
		// for offline txn added on 26/12
		params.add(isoBuffer.get(Constants.DE3));
		params.add(isoBuffer.get(Constants.DE25));
		params.add(isoBuffer.isFieldEmpty(Constants.DE52) ? "" : isoBuffer
				.get(Constants.DE52));
		params.add(isoBuffer.isFieldEmpty(Constants.DE55) ? "" : IsoUtil
				.asciiChar2hex(isoBuffer.get(Constants.DE55)));
		params.add(isoBuffer.isFieldEmpty(Constants.DE38) ? "" : isoBuffer
				.get(Constants.DE38));

		// for jcb refund service code is mandatory
		String service = null;
		if (!isoBuffer.isFieldEmpty(Constants.DE35)) {
			String track2 = isoBuffer.get(Constants.DE35);
			int i = track2.contains("D") ? track2.indexOf('D') + 1 + 4 : track2
					.indexOf('=') + 1 + 4;
			if (i < track2.length())
				service = track2.substring(i, i + 3);
		}

		params.add(null == service ? "" : service);

		if (emvTags == null || emvTags.isEmpty()) {
			params.add("");
		} else {
			String cardSeqNo = emvTags.get(EmvTags.TAG_5F34);
			if (Util.isNullOrEmpty(cardSeqNo)) {
				params.add("");
			} else {
				params.add(cardSeqNo);
			}
		}

		params.add(isoBuffer.get(Constants.DE14).isEmpty()
				|| isoBuffer.get(Constants.DE14).equals(
						Constants.DISABLED_FIELD) ? null : isoBuffer
				.get(Constants.DE14));
		
		String addtPrivateData = null;
		if (!isoBuffer.isFieldEmpty(Constants.DE61)) {
			JSONObject jo = new JSONObject(isoBuffer.get(Constants.DE61));
			addtPrivateData = (String) jo.opt(Constants.JSON_KEY_MAC_ID);
		}
		params.add(addtPrivateData);// DCC Added - Para
		
		ArrayList<Class<?>> classList = new ArrayList<Class<?>>();
		classList.add(ResponseStatus.class);
		classList.add(HsmData.class);
		classList.add(HostData.class);
		return apiFactory.getStoredProcedureApi().getBean(params,
				procedureName, classList, mspAcr);
	}

	public List<Object> fetchISO8583Data(String procedureName,
			IsoBuffer isoBuffer, String mspAcr, String srcStation,
			TerminalOperator operator, boolean isOffline, String msgVersion,
			Map<EmvTags, String> emvTags) throws Exception {
		String pan = isoBuffer.get(Constants.DE2, null);
		String txnType = isoBuffer.get(Constants.DE3).substring(0, 2)
				+ isoBuffer.get(Constants.DE25);

		List<Object> params = new ArrayList<Object>();

		params.add(isoBuffer.get(Constants.DE2, null));
		params.add(isoBuffer.get(Constants.DE3, null));
		params.add(isoBuffer.get(Constants.DE4, null));
		params.add(isoBuffer.get(Constants.DE7, null));
		params.add(isoBuffer.get(Constants.DE11, null));
		params.add(isoBuffer.get(Constants.DE12, null));
		params.add(isoBuffer.get(Constants.DE13, null));
		params.add(isoBuffer.isFieldEmpty(Constants.DE14) ? "" : isoBuffer
				.get(Constants.DE14));
		params.add(isoBuffer.get(Constants.DE18, null));
		params.add(isoBuffer.get(Constants.DE22, null));
		params.add(isoBuffer.get(Constants.DE23, null));
		params.add(isoBuffer.get(Constants.DE25, null));
		params.add(isoBuffer.isFieldEmpty(Constants.DE35) ? "" : isoBuffer
				.get(Constants.DE35));
		params.add(isoBuffer.get(Constants.DE37, null));
		params.add(isoBuffer.isFieldEmpty(Constants.DE38) ? "" : isoBuffer
				.get(Constants.DE38));
		params.add(isoBuffer.get(Constants.DE41, null));
		params.add(isoBuffer.get(Constants.DE42, null));
		params.add(isoBuffer.get(Constants.DE43, null));
		params.add(isoBuffer.get(Constants.DE49, null));
		params.add(isoBuffer.isFieldEmpty(Constants.DE52) ? "" : isoBuffer
				.get(Constants.DE52));
		params.add(isoBuffer.isFieldEmpty(Constants.DE54) ? "" : isoBuffer
				.get(Constants.DE54));
		params.add(isoBuffer.isFieldEmpty(Constants.DE55) ? "" : IsoUtil
				.asciiChar2hex(isoBuffer.get(Constants.DE55)));
		params.add(isoBuffer.get(Constants.DE62, null));
		params.add(isoBuffer.get(Constants.DE3).substring(2, 4));
		params.add(txnType);
		if (!(isoBuffer.get(Constants.DE6).equals(Constants.DISABLED_FIELD)))
			params.add(isoBuffer.get(Constants.DE63));
		else {
			params.add(Constants.ZERO);
		}

		params.add(srcStation);
		params.add((operator == null || operator.getSessionId() == null || operator
				.getSessionId().isEmpty()) ? Constants.ZERO : operator
				.getSessionId());
		params.add(isoBuffer.get(Constants.ISO_MSG_TYPE));
		params.add(isOffline ? Constants.ENABLE : Constants.DISABLE);
		params.add(msgVersion);
		params.add(ADJUSTMENT_TXNS.contains(txnType) ? Constants.ENABLE
				: Constants.DISABLE);

		SecureData sd = StaticStore.deks.get(mspAcr);

		try {
			byte[] encPan;
			if (mspAcr.equals("AFP") || mspAcr.equals("AUB")) {// afs
				encPan = securityUtils.encryptText(pan.getBytes(),
						config.getAliasNamePrefix() + "AHB", sd.getKekCode(),
						sd.getDekBytes(), mspAcr, sd.getEncryptType());
			} else {
				encPan = securityUtils.encryptText(pan.getBytes(),
						config.getAliasNamePrefix() + mspAcr, sd.getKekCode(),
						sd.getDekBytes(), mspAcr, sd.getEncryptType());
			}
			params.add(new String(Base64.encodeBase64(encPan)));
		} catch (Exception e) {
			Log.error("Card Encryption", e);
			params.add(null);
		}
		params.add(pan == null ? null : Util.hashSHA512(pan,
				Constants.EMPTY_STRING));

		// Card Masking Number//
		if (pan != null) {
			String maskedno = PosUtil.maskCardNumber(pan, mspAcr);
			params.add(maskedno);
		} else
			params.add(null);

		ArrayList<Class<?>> classList = new ArrayList<Class<?>>();
		classList.add(ResponseStatus.class);
		classList.add(HsmData.class);
		classList.add(HostData.class);
		return apiFactory.getStoredProcedureApi().getBean(params,
				procedureName, classList, mspAcr);
	}

	@SuppressWarnings("unchecked")
	public TimeoutData updateHostLog(String tid, IsoBuffer isoBuffer,
			String procedureName, String mspAcr, String uniqueId,
			String timerData, String tranId) throws PosException {
		try {
			IsoBuffer tempBuffer = new IsoBuffer(isoBuffer);

			tempBuffer.put(Constants.DE52, tempBuffer
					.isFieldEmpty(Constants.DE52) ? Constants.DISABLE
					: Constants.ENABLE);

			List<Object> params = new ArrayList<Object>();
			params.add(tid.equals(Constants.DISABLED_FIELD) ? "" : tid);
			// params.add(tempBuffer.isFieldEmpty(Constants.DE41) ? "" :
			// tempBuffer.get(Constants.DE41));
			params.add(tempBuffer.get(Constants.ISO_MSG_TYPE));
			addBufferParams(tempBuffer, params, 1, 64, IsoBuffer.PREFIX_PRIMARY);
			addBufferParams(tempBuffer, params, 65, 128,
					IsoBuffer.PREFIX_SECONDARY);
			addBufferParams(tempBuffer, params, 129, 192,
					IsoBuffer.PREFIX_TERTIARY);
			params.add(uniqueId);
			params.add(timerData);
			if (StoredProcedureInfo.LOG_HOST_REQUEST.equals(procedureName))
				params.add(tranId);

			List<Class<?>> classlist = new ArrayList<Class<?>>();
			classlist.add(ResponseStatus.class);
			classlist.add(TransactionTimerData.class);
			List<Object> objList = apiFactory.getStoredProcedureApi().getBean(
					params, procedureName, classlist, mspAcr);
			String rspStatus = ((List<ResponseStatus>) objList.get(0)).get(0)
					.getStatus();
			Log.debug("Response from " + procedureName, rspStatus);

			if (Constants.SUCCESS.equals(rspStatus)) {
				TransactionTimerData ttd = ((List<TransactionTimerData>) objList
						.get(1)).get(0);

				// Log.debug("timer data", ttd.getTransactionTimerData());

				if (!Constants.SUCCESS.equals(ttd.getTransactionTimerData())) {
					TimeoutData td = objectMapper.readValue(
							ttd.getTransactionTimerData(), TimeoutData.class);
					return td;
				}
			}
			return null;
		} catch (Exception e) {
			Log.error(this.getClass() + " updateHostLog ", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
	}

	private void addBufferParams(IsoBuffer isoBuffer, List<Object> params,
			int start, int end, String prefix)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {
		for (int i = start; i <= end; i++) {
			String k = prefix + i;
			if (isoBuffer.isFieldEmpty(k)) {
				params.add(null);
			} else {
				if (isoBuffer.hasSubFields(k)) {
					params.add(isoBuffer.getBuffer(k).toString());
				} else {
					if (i == 35) {
						if (!isoBuffer.get(k).equals(""))
							params.add(getServiceCodeUpdate(isoBuffer.get(k)));
					} else if (i == 2) {
						params.add(Util.hashSHA512(isoBuffer.get(k).toString(),
								Constants.EMPTY_STRING));
					} else
						params.add(isoBuffer.get(k));
				}
			}

		}
	}

	private String getServiceCodeUpdate(String value) {
		int i = value.contains("D") ? value.indexOf('D') + 1 + 4 : value
				.indexOf('=') + 1 + 4;
		if (i < value.length())
			return value.substring(i, i + 3);
		else
			return "";
	}

	@SuppressWarnings("unchecked")
	public ZonalKeysModel fetchHsmDetails(BigDecimal acqIdPk, String mspAcr,
			String procedureName) throws PosException {
		try {
			ZonalKeysModel zonalKeyModel = new ZonalKeysModel();
			List<Object> params = new ArrayList<Object>();
			params.add(acqIdPk);
			List<Class<?>> classList = new ArrayList<Class<?>>();
			classList.add(ZonalKeysModel.class);
			List<Object> objList = apiFactory.getStoredProcedureApi().getBean(
					params, procedureName, classList, mspAcr);
			List<ZonalKeysModel> list = (List<ZonalKeysModel>) objList.get(0);
			zonalKeyModel = list.get(0);
			return zonalKeyModel;
		} catch (Exception e) {
			Log.error(this.getClass() + " fetchHsm Details ", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
	}

	public String updateZonalKeys(BigDecimal acqIdPk, String mspAcr,
			String procedureName, String lmkKey, String checksum)
			throws PosException {
		try {
			List<Object> params = new ArrayList<Object>();
			params.add(acqIdPk);
			params.add(lmkKey);
			params.add(checksum);
			return apiFactory.getStoredProcedureApi().getString(params,
					procedureName, mspAcr);
		} catch (Exception e) {
			Log.error(this.getClass()
					+ " Error Updating Key Details in DB Details ", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
	}
}
