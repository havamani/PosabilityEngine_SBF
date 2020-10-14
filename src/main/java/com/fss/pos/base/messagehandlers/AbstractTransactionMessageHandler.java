package com.fss.pos.base.messagehandlers;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.fss.pos.base.api.client.ClientApi;
import com.fss.pos.base.api.client.Validator;
import com.fss.pos.base.api.db.storedprocedure.ResponseStatus;
import com.fss.pos.base.api.db.storedprocedure.StoredProcedureInfo;
import com.fss.pos.base.api.db.storedprocedure.TransactionEnquiry;
import com.fss.pos.base.api.host.HostData;
import com.fss.pos.base.api.host.TimeoutData;
import com.fss.pos.base.api.hsm.HsmApi;
import com.fss.pos.base.api.hsm.HsmData;
import com.fss.pos.base.api.hsm.HsmResponse;
import com.fss.pos.base.commons.Config;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.DccParams;
import com.fss.pos.base.commons.EmvTags;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.StaticStore;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.base.commons.utils.ThreadLocalUtil;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.base.commons.utils.security.SecureData;
import com.fss.pos.base.commons.utils.security.SecurityService;
import com.fss.pos.base.factory.ApiFactory;
import com.fss.pos.base.services.fssconnect.FssConnect;
import com.fss.pos.base.services.transactionlog.TransactionLogService;
import com.fss.pos.base.services.transactionlog.TransactionResponse;
import com.fss.pos.client.services.additionaldata.AdditionalDataApi;
import com.fss.pos.client.services.additionaldata.AdditionalDataInfo;
import com.fss.pos.client.services.operator.OperatorApi;
import com.fss.pos.client.services.operator.TerminalInfo;
import com.fss.pos.client.services.operator.TerminalOperator;

public abstract class AbstractTransactionMessageHandler extends
		AbstractMessageHandler {

	private static final String FORMAT_TXN_TIME = "HHmmss";
	private static final String FORMAT_TXN_DATE = "MMdd";
	//private static final String AMEX_MSG_PROTOCOL = "20";
	//private static final String JCB_MSG_PROTOCOL = "22";
	private static final String AMEX_REVERSAL_REQ = "1420";
	private static final Map<String, String> HOSTMAP;
	static ObjectMapper objectMapper = new ObjectMapper();
	

	static {
		HashMap<String, String> hostApi = new HashMap<String, String>();
		hostApi.put(Constants.VISA, "Visa");
		hostApi.put(Constants.MASTER, "Master Card");
		hostApi.put(Constants.AMEX, "Amex");
		hostApi.put(Constants.BENEFIT, "Benefit");
		hostApi.put(Constants.VISIONPLUS, "Vision Plus");
		hostApi.put(Constants.JCB, "Jcb");
		hostApi.put(Constants.CUP, "Cup");
		hostApi.put(Constants.OMANNET, "Omannet");
		hostApi.put(Constants.RUPAY, "Rupay");
		hostApi.put(Constants.MONEX, "Monex");
		hostApi.put(Constants.NED, "NED Bank");
		hostApi.put(Constants.ABSA, "ABSA Bank");
		hostApi.put(Constants.FNB, "FNB Bank");
		hostApi.put(Constants.STD, "STD Bank");
		hostApi.put(Constants.DINERS, "Diners");
		HOSTMAP = Collections.unmodifiableMap(hostApi);
	}

	@Autowired
	private TransactionLogService transactionLogService;

	@Autowired
	private ApiFactory apiFactory;

	@Autowired
	private SecurityService securityUtils;

	@Autowired
	private Config config;

	@Override
	public String handleMessage(IsoBuffer isoBuffer, FssConnect fssConnect,
			ClientApi clientApi) throws UnsupportedEncodingException,
			PosException, JSONException {
		return transact(isoBuffer, fssConnect, clientApi,
				this instanceof OfflineTransactionMessageHandler);
	}

	private String transact(IsoBuffer clientBuffer, FssConnect fssc,
			ClientApi clientApi, boolean isOffline) throws PosException,
			JSONException, UnsupportedEncodingException {

		String myStation = fssc.getDestination();
		String mspAcr = fssc.getIIN();
		
		

		TerminalOperator ud=null;
		TerminalInfo ti=null;
		AdditionalDataInfo adInfo = null;
		Map<EmvTags, String> emvTags=null;
		
		ud = ((OperatorApi) clientApi)
				.getTerminalOperatorData(clientBuffer);
		ti = ((OperatorApi) clientApi)
				.getTerminalInfo(clientBuffer);
		adInfo = ((AdditionalDataApi) clientApi)
				.getAdditionalDataInfo(clientBuffer);
		
		
		// transaction enquiry
		if (clientBuffer.get(Constants.DE3).substring(0, 2)
				.equals(Constants.PROC_CODE_GENERIC_ENQUIRY)) {
			return genericEnquiry(clientBuffer, fssc, clientApi);
		}

		// For DCC
		if ((clientBuffer.get(Constants.DE25).equals(Constants.DCC_ENQUIRY)))
			return identifyDCC(clientBuffer, fssc, clientApi);

		if ((clientBuffer.get(Constants.DE25)
				.equals(Constants.DCC_REFUND_ENQUIRY))
				|| (clientBuffer.get(Constants.DE25)
						.equals(Constants.DCC_PREAUTH_COMPLETION)))
			return dccRefund(clientBuffer, fssc, clientApi);

		if ((clientBuffer.get(Constants.DE25)
				.equals(Constants.DCC_PREAUTH_CANCEL))) {
			dccRefund(clientBuffer, fssc, clientApi);
			if (clientBuffer.get(Constants.DE39).equals(Constants.SUCCESS)) {
				clientBuffer.put(Constants.DE25, "00");
				clientBuffer.disableField(Constants.DE39);
			} else {
				String clientStation = fssc.getSource();
				fssc.setSource(fssc.getDestination());
				fssc.setMessage(clientApi.build(clientBuffer));
				fssc.setDestination(clientStation);
				fssc.setAlternateSourceArray(new String[0]);
				return fssc.toString();
			}
		}

		

		fillPanFromTrack2(clientBuffer);

		// refund enquiry
		if ((clientBuffer.get(Constants.DE3).substring(0, 2) + clientBuffer
				.get(Constants.DE25)).equals(Constants.REFUND_TXN_TYPE)) {
			/*clientBuffer.put(Constants.DE37,
					clientBuffer.isFieldEmpty(Constants.DE48) ? "*"
							: clientBuffer.get(Constants.DE48));
		*/	clientBuffer.put(
					Constants.DE37, adInfo.getRrn());
		}

		// pre auth completion
		if ((clientBuffer.get(Constants.DE3).substring(0, 2) + clientBuffer
				.get(Constants.DE25)).equals(Constants.PREAUTH_COMPL_TXN_TYPE)) {
/*			clientBuffer.put(
					Constants.DE37,
					clientBuffer.isFieldEmpty(Constants.DE48) ? clientBuffer
							.isFieldEmpty(Constants.DE37) ? "*" : clientBuffer
							.get(Constants.DE37) : clientBuffer
							.get(Constants.DE48));*/
			clientBuffer.put(
					Constants.DE37, adInfo.getRrn());
			
		}
		
		emvTags = clientApi.parseEmvData(clientBuffer);
		
		List<Data> txnDataList=null;

		if(clientApi.checkHostToHost())
		{
			txnDataList=getISO8583TxnFlowData(clientApi.getStoredProcedure(),
					clientBuffer, mspAcr, fssc.getSource(), ud, ti.getMsgVersion(),
					isOffline, emvTags);
		}
		else
		{
			txnDataList=getTxnFlowData(clientApi.getStoredProcedure(),
					clientBuffer, mspAcr, fssc.getSource(), ud, ti.getMsgVersion(),
					isOffline, emvTags);
		}
		
		 
		 HostData hostData = (HostData) txnDataList.get(0);
		 HsmData hsmData = (HsmData) txnDataList.get(1);

		// Dukpt Validation

		if (hsmData.getMsgProtocol().equals(Constants.KEY_SCHEME_DUKPT)
				&& !clientBuffer.isFieldEmpty(Constants.DE44)) {
			if (clientBuffer.get(Constants.DE3).equals(
					Constants.PROC_CODE_TMK_DOWNLOAD)
					|| clientBuffer.get(Constants.DE3).equals(
							Constants.PROC_CODE_SESSION_KEY_DOWNLOAD)) {
				clientBuffer.put(Constants.DE39, Constants.LOGON_NOT_ALLOWED);
				return respondClient(clientBuffer, fssc, clientApi);
			}
		}

		hostData.setRrn(Util.appendChar(hostData.getRrn(), '0', 12, true));
		clientBuffer.put(Constants.DE37, hostData.getRrn());
		clientBuffer.setTranId(hostData.getTranId());

		if (Constants.PROC_CODE_CASH_RECORDING.equals(clientBuffer.get(
				Constants.DE3).substring(0, 2)))
			return recordCash(clientBuffer, hostData.getRrn(), fssc, clientApi);

		if (Constants.MSG_TYPE_OFFLINE.equals(clientBuffer
				.get(Constants.ISO_MSG_TYPE)))
			return offlineTranx(clientBuffer, hostData.getRrn(), fssc,
					clientApi);

		if (Constants.ENABLE.equals(hsmData.getEnableP2Pe())
				&& !clientBuffer.isFieldEmpty(Constants.DE48)) {
			String resp = p2pe(clientBuffer.get(Constants.DE2),
					hostData.getRrn(), hsmData, hostData, mspAcr, clientBuffer);
			if (!resp.equals(Constants.SUCCESS)) {
				clientBuffer.put(Constants.DE39, resp);
				clientBuffer.put(Constants.DE63, "P2P Error");
				return respondClient(clientBuffer, fssc, clientApi);
			}
		}

		if (!clientBuffer.isFieldEmpty(Constants.DE52)) {

			if (Validator.validatePinConfig(clientBuffer)) {

				if (null == hostData.getZonalKey()
						|| hostData.getZonalKey().isEmpty())
					throw new PosException(Constants.ERR_NO_ZONAL_KEY);
				translatePIN(clientBuffer, hsmData, hostData.getZonalKey(),
						hostData.getZpkChecksum(), mspAcr,
						hsmData.getMsgProtocol());

			}

		}

		IsoBuffer hostBuffer = new IsoBuffer(clientBuffer);
		hostBuffer.putIfAbsent(Constants.DE12, hostData.getMerchantTime());
		hostBuffer.putIfAbsent(Constants.DE13, hostData.getMerchantDate());
		hostBuffer.putIfAbsent(Constants.DE7, hostData.getTxnDateTime());

		// Log.debug("EMV Map", emvTags.toString());
		hostData.setEmvMap(emvTags);
		hostData.setOffline(isOffline);

		hostBuffer.put(Constants.DE41, hostData.getTerminalId());
		hostBuffer.put(Constants.DE42,
				Util.appendChar(hostData.getMerchantId(), ' ', 15, false));

		if (!apiFactory.containsHostRequestApi(hostData.getMsgProtocol()))
			throw new PosException(Constants.ERR_INVALID_ACQUIRER_PROTOCOL);

		Log.trace("Api | " + HOSTMAP.get(hostData.getMsgProtocol()));

		StringBuilder hostRequest= new StringBuilder();
		hostRequest.append(apiFactory.getHostRequestApi(
				hostData.getMsgProtocol()).modiyAndbuild(hostBuffer,
				clientBuffer, hostData));

		String source = fssc.getSource();
		StringBuilder fsscUniqueId = new StringBuilder();

		
		switch(hostData.getMsgProtocol()) {

			case Constants.RUPAY:
				fsscUniqueId.append(Util.getIsoFsscUniqueId(hostData.getRrn(),
						hostBuffer.get(Constants.DE11),
						hostBuffer.get(Constants.DE41)));
				break;
				
			case Constants.DINERS:
				fsscUniqueId.append(Util.getIsoFsscUniqueId(hostBuffer.get(Constants.DE7),
						hostBuffer.get(Constants.DE11),
						hostBuffer.get(Constants.ISO_MSG_TYPE).substring(0, 2)));
				break;
				
			case Constants.AMEX:	
				if (hostBuffer.get(Constants.ISO_MSG_TYPE).equals(
						AMEX_REVERSAL_REQ))
					fsscUniqueId.append(Util.getIsoFsscUniqueId(
							hostBuffer.get(Constants.DE4),
							hostBuffer.get(Constants.DE11),
							hostBuffer.get(Constants.DE12)));
				break;
				
			case Constants.JCB:
				fsscUniqueId.append(Util.getIsoFsscUniqueId(hostData.getRrn(),
						clientBuffer.get(Constants.DE11),
						hostBuffer.get(Constants.DE7)));
			break;
			
			default:
				fsscUniqueId.append(Util.getIsoFsscUniqueId(hostData.getRrn(),
						clientBuffer.get(Constants.DE11),
						hostBuffer.get(Constants.DE41)));
				
		}

		
		TimeoutData td = new TimeoutData();
		td.setIsoBufferMap(new IsoBuffer(clientBuffer).getMap());
		td.setIin(mspAcr);
		td.setThreadLocalId(ThreadLocalUtil.getRRN());
		td.setHostStationName(hostData.getStationName());
		td.setClientStationName(source);
		td.setClientClass(clientApi.getClass().getCanonicalName());
		td.setUti(hostData.getUti());
		td.setTerminalId(clientBuffer.get(Constants.DE41));
		td.setMerchantId(clientBuffer.get(Constants.DE42));
		td.setTxnCommunicationFlow(hostData.getTxnCommunicationFlow());
		td.setTransmissionDateAndTime(hostBuffer.get(Constants.DE7));
		td.setMyStation(myStation);
		td.setTranId(hostData.getTranId());
		td.setIsDCC((hostData.getIsDCC().isEmpty() || hostData.getIsDCC() == null) ? "0"
				: hostData.getIsDCC());
		td.setDccStationName(hostData.getDccStationName());
		td.setQuotationId(clientBuffer.get(Constants.DE63));
		td.setDccAlternateStationName(hostData.getDccAlternateStationName());
		if (fssc.getPlainPrivateKey() != null)
			td.setPlainKey(fssc.getPlainPrivateKey());
		else
			td.setPlainKey(null);

		// For ABSA/////////////
		td.setRespDe48(hostBuffer.get(Constants.DE48));
		td.setRespDe126(hostBuffer.get(Constants.DE126));
		// ////////////////////

		updateTimer(fsscUniqueId.toString(), mspAcr, td, hostBuffer,
				clientBuffer.get(Constants.DE41), hostData.getTranId());

		fssc.setSource(fssc.getDestination());
		fssc.setDestination(hostData.getStationName());
		fssc.setUniqueId(Constants.EMPTY_STRING);
		fssc.setAlternateDestinationArray(new String[] { hostData
				.getAlternateStationName() == null ? "" : hostData
				.getAlternateStationName() });
		fssc.setMessage(hostRequest.toString());

		Log.debug("Fss Connect Message::::", fssc.getMessage());

		return fssc.toString();
	}

	@SuppressWarnings("unchecked")
	private String genericEnquiry(IsoBuffer clientBuffer, FssConnect fssc,
			ClientApi clientApi) throws JSONException, PosException {
		String source = fssc.getSource();
		String destReceived = fssc.getDestination();
		IsoBuffer isobuffer = new IsoBuffer(clientBuffer);
		List<Object> params = new ArrayList<Object>();
		ArrayList<Class<?>> classList = new ArrayList<Class<?>>();
		params.add(isobuffer.get(Constants.DE48));
		classList.add(ResponseStatus.class);
		classList.add(TransactionEnquiry.class);
		String rspStatus;
		try {
			List<Object> objList = apiFactory.getStoredProcedureApi().getBean(
					params, StoredProcedureInfo.GENERIC_ENQUIRY, classList,
					fssc.getIIN());
			rspStatus = ((List<ResponseStatus>) objList.get(0)).get(0)
					.getStatus();

			Log.debug("Response from Procedure "
					+ StoredProcedureInfo.GENERIC_ENQUIRY, rspStatus);

			isobuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_AUTH);
			// isobuffer.put(Constants.DE12, txnEnq.getMerchantTime());
			// isobuffer.put(Constants.DE13, txnEnq.getMerchantDate());
			isobuffer.put(Constants.DE37, isobuffer.get(Constants.DE48));

			if (Constants.SUCCESS.equals(rspStatus)) {

				TransactionEnquiry txnEnq = ((List<TransactionEnquiry>) objList
						.get(1)).get(0);

				txnEnq.setRrn(isobuffer.get(Constants.DE48));
				String orgData = objectMapper.writeValueAsString(txnEnq);
				JSONObject json = new JSONObject(orgData);
				// json.remove("merchantTime");
				// json.remove("merchantDate");
				isobuffer.put(Constants.DE61, json.toString());
				isobuffer.put(Constants.DE39, txnEnq.getStatus());

				if (txnEnq.getStatus().equals(Constants.ORG_TXN_FAILED)) {
					isobuffer
							.put(Constants.DE63, "Original Transaction Failed");
				}
				if (txnEnq.getStatus().equals(Constants.ORG_TXN_EXPIRED)) {
					isobuffer.put(Constants.DE63,
							"Original Transaction Expired");
				}
				if (txnEnq.getStatus().equals(Constants.ORG_TXN_VOIDED)) {
					isobuffer
							.put(Constants.DE63, "Original Transaction Voided");
				}
				if (txnEnq.getStatus().equals(Constants.ORG_TXN_REVERSED)) {
					isobuffer.put(Constants.DE63,
							"Original Transaction Reversed");
				}
		
			} else {
				isobuffer.put(Constants.DE39, rspStatus);
				isobuffer.put(Constants.DE63, "Unable to locate the record");
			}
			isobuffer.disableField(Constants.DE48);
			isobuffer.disableField(Constants.DE7);

	Log.debug("Client response", isobuffer.toString());
			Log.info("Client response", isobuffer.logData());
			fssc.setDestination(source);
			fssc.setSource(destReceived);
			fssc.setMessage(clientApi.build(isobuffer));
			fssc.setAlternateSourceArray(new String[0]);
			return fssc.toString();

		} catch (Exception e) {
			Log.error("List Empty from Procedure:::"
					+ StoredProcedureInfo.GENERIC_ENQUIRY + e.getMessage(), e);
			fssc.setDestination(source);
			fssc.setSource(destReceived);
			fssc.setAlternateSourceArray(new String[0]);
		}
		return fssc.toString();
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private String dccRefund(IsoBuffer isoBuffer, FssConnect fssc,
			ClientApi clientApi) throws PosException, JSONException,
			UnsupportedEncodingException {
		String clientStation = fssc.getSource();
		List<Object> params = new ArrayList<Object>();
		params.add(isoBuffer.get(Constants.DE3).substring(0, 2)
				+ isoBuffer.get(Constants.DE25));
		// params.add(isoBuffer.get(Constants.DE3).substring(0, 2));
		params.add(isoBuffer.get(Constants.DE11));
	    /*String RRN = "rrn";
		AdditionalDataInfoTemp adinfo = new AdditionalDataInfoTemp();
		if (!isoBuffer.isFieldEmpty(Constants.DE48)) {
			if (Validator.isJSONValid(isoBuffer.get(Constants.DE48))) {
				JSONObject json = new JSONObject(isoBuffer.get(Constants.DE48));
				adinfo.setRrn((String) json.get(RRN));
						params.add(adinfo.getRrn()); 
			} else {
				params.add(isoBuffer.get(Constants.DE48));
             }
		}*/
		AdditionalDataInfo adInfo = null;
		adInfo = ((AdditionalDataApi) clientApi)
				.getAdditionalDataInfo(isoBuffer);
		params.add(adInfo.getRrn());
		params.add(isoBuffer.get(Constants.DE41));
		params.add(isoBuffer.get(Constants.DE42));

		ArrayList<Class<?>> classList = new ArrayList<Class<?>>();

		classList.add(ResponseStatus.class);
		classList.add(DccParams.class);
		String rspStatus = null;
		String tranId = null;
		try {
			List<Object> objList = apiFactory.getStoredProcedureApi().getBean(
					params, StoredProcedureInfo.DCC_REFUND_ENQUIRY, classList,
					fssc.getIIN());
			rspStatus = ((List<ResponseStatus>) objList.get(0)).get(0)
					.getStatus();
			Log.debug(
					"Response from " + StoredProcedureInfo.DCC_REFUND_ENQUIRY,
					rspStatus);
			if (Constants.SUCCESS.equals(rspStatus)) {

				List<DccParams> listDccCards = (List<DccParams>) objList.get(1);
				List<Object> listObj = new ArrayList<Object>();
				for (DccParams dccParams : listDccCards) {

					tranId = dccParams.getTranId();
					if (dccParams.getIsDCC().equals("1")) {
						isoBuffer.put(Constants.DE6,
								dccParams.getOrgTxnAmount());
						isoBuffer.put(Constants.DE51,
								dccParams.getCurrencyCodeN());
						isoBuffer.put(Constants.DE63,
								dccParams.getQuotationId());
					}

					isoBuffer.put(Constants.DE4,
							dccParams.getOrgCardTxnAmount());

					isoBuffer.put(Constants.DE12, dccParams.getMerchantTime());
					isoBuffer.put(Constants.DE13, dccParams.getMerchantDate());
					isoBuffer.put(Constants.DE37, dccParams.getRrn());
					isoBuffer.put(Constants.DE39, rspStatus);

					isoBuffer.put(Constants.DE49, dccParams.getCurrencyCode());

					if (dccParams.getOrgCardAddAmount() != null) {
						isoBuffer.put(Constants.DE54,
								dccParams.getOrgCardAddAmount());       // for dcc refund //13/08/2019
					}
					if ((isoBuffer.get(Constants.DE3).substring(0, 2) + isoBuffer
							.get(Constants.DE25)).equals("2297")) {
						isoBuffer.put(Constants.DE62, dccParams.getInvoice());
					}
				}
			} else {
				throw new PosException(rspStatus);
			}
		} catch (PosException e) {
			throw e;
		} catch (Exception e) {
			Log.error("List Empty from Procedure:::"
					+ StoredProcedureInfo.DCC_REFUND_ENQUIRY + e.getMessage(),
					e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}

		isoBuffer.put(Constants.DE39, rspStatus);
		if (rspStatus.equals(Constants.SUCCESS)) {
			try {
				int status = (rspStatus.equals(Constants.SUCCESS)) ? Constants.TXNLOG_STATUS_SUCCESS
						: Constants.TXNLOG_STATUS_FAILED;

				updateTxnLog(isoBuffer, status, tranId, fssc.getDestination(),
						fssc.getIIN(),clientApi);
			} catch (PosException e) {
				isoBuffer.put(Constants.DE39, e.getMessage());
			}
		}

		if ((isoBuffer.get(Constants.DE3).substring(0, 2) + isoBuffer
				.get(Constants.DE25)).equals("2098")
				|| (isoBuffer.get(Constants.DE3).substring(0, 2) + isoBuffer
						.get(Constants.DE25)).equals("0095")) {

			fssc.setSource(fssc.getDestination());
			fssc.setMessage(clientApi.build(isoBuffer));
			fssc.setDestination(clientStation);
			fssc.setAlternateSourceArray(new String[0]);
			Log.info("Client response", isoBuffer.logData());
		}

		return fssc.toString();
	}

	@SuppressWarnings("unchecked")
	private String identifyDCC(IsoBuffer isoBuffer, FssConnect fssc,
			ClientApi clientApi) throws PosException, JSONException,
			UnsupportedEncodingException {
		String clientStation = fssc.getSource();
		String mspAcr = fssc.getIIN();
		String pan = isoBuffer.get(Constants.DE2, null);
		List<Object> params = new ArrayList<Object>();

		ArrayList<Class<?>> classList = new ArrayList<Class<?>>();
		params.add(pan);
		params.add(isoBuffer.get(Constants.DE41));
		params.add(isoBuffer.get(Constants.DE3).substring(0, 2)
				+ isoBuffer.get(Constants.DE25));
		params.add(isoBuffer.get(Constants.DE4));
		if (isoBuffer.get(Constants.DE54).equals(Constants.DISABLED_FIELD))
			params.add("0");
		else
			params.add(isoBuffer.get(Constants.DE54));
		params.add(isoBuffer.get(Constants.DE22));
		params.add(clientStation);
		params.add(isoBuffer.get(Constants.DE11));
		params.add(null);
		params.add(isoBuffer.get(Constants.DE48).equals("*") ? null : isoBuffer
				.get(Constants.DE48));
		params.add(isoBuffer.get(Constants.ISO_MSG_TYPE));
		params.add(isoBuffer.get(Constants.DE7));

		if (pan != null) {
			try {
				SecureData sd = StaticStore.deks.get(fssc.getIIN());
				byte[] encPan;
				if (mspAcr.equals("AFP") || mspAcr.equals("AUB")) {// afs
					encPan = securityUtils.encryptText(pan.getBytes(),
							config.getAliasNamePrefix() + "AHB",
							sd.getKekCode(), sd.getDekBytes(), mspAcr,
							sd.getEncryptType());
				} else {
					encPan = securityUtils.encryptText(pan.getBytes(),
							config.getAliasNamePrefix() + mspAcr,
							sd.getKekCode(), sd.getDekBytes(), mspAcr,
							sd.getEncryptType());
				}
				params.add(new String(Base64.encodeBase64(encPan)));
			} catch (Exception e) {
				params.add(null);
				Log.error("Card Encryption Exception", e);
			}

		} else
			params.add(null);

		try {
			params.add(pan == null ? null : Util.hashSHA512(pan,
					Constants.EMPTY_STRING));
		} catch (Exception e) {
			params.add(null);
			Log.error("Hashing Card Exception", e);
		}

		classList.add(ResponseStatus.class);
		classList.add(DccParams.class);
		String tranId = null;
		String rspStatus = null;
		try {
			List<Object> objList = apiFactory.getStoredProcedureApi().getBean(
					params, StoredProcedureInfo.DCC_VALIDATE, classList,
					fssc.getIIN());
			rspStatus = ((List<ResponseStatus>) objList.get(0)).get(0)
					.getStatus();

			Log.debug("Response from Procedure "
					+ StoredProcedureInfo.DCC_VALIDATE, rspStatus);

			if (Constants.SUCCESS.equals(rspStatus)) {

				List<DccParams> listDccCards = (List<DccParams>) objList.get(1);
				List<Object> listObj = new ArrayList<Object>();
				for (DccParams dccParams : listDccCards) {
					if (dccParams.getMsgProtocol().equals(Constants.FSS_DCC)) // FSS
																				// DCC
					{
						JSONObject jsonDcc = new JSONObject();
						jsonDcc.put("CACD", dccParams.getCurrencyCodeA());
						jsonDcc.put("CNCD", dccParams.getCurrencyCodeN());
						jsonDcc.put("CEXP", dccParams.getCardExponent());
						jsonDcc.put("CVAL", dccParams.getTxnAmount());
						jsonDcc.put("EXCR", dccParams.getExchangeRate());
						jsonDcc.put("COMM", dccParams.getCommission());
						jsonDcc.put("MARK", dccParams.getMarkUp());
						jsonDcc.put("AEXP", dccParams.getMerchantExponent());
						jsonDcc.put("ACMV", dccParams.getMerMarginValue());
						jsonDcc.put("CAMV", dccParams.getCardMarginValue());
						jsonDcc.put("QUOT", dccParams.getQuotationId());
						tranId = dccParams.getTranId();
						listObj.add(jsonDcc);
					} else if (dccParams.getMsgProtocol().equals(
							Constants.MONEX))// MONEX
					{
						Log.trace("Api | " + HOSTMAP.get(Constants.MONEX));
						String hostRequest = apiFactory.getHostRequestApi(
								dccParams.getMsgProtocol()).modiyAndbuild(
								isoBuffer, isoBuffer, dccParams);

						// Update Timer Data
						TimeoutData td = new TimeoutData();
						td.setIsoBufferMap(new IsoBuffer(isoBuffer).getMap());
						td.setIin(fssc.getIIN());
						td.setThreadLocalId(ThreadLocalUtil.getRRN());
						td.setHostStationName(dccParams.getDccStationName());
						td.setClientStationName(fssc.getSource());
						td.setClientClass(clientApi.getClass()
								.getCanonicalName());
						td.setMyStation(fssc.getDestination());
						td.setTranId(dccParams.getTranId());
						td.setMerchantExponent(dccParams.getMerchantExponent());
						td.setTerminalId(isoBuffer.get(Constants.DE41));
						td.setMerchantId(isoBuffer.get(Constants.DE42));

						String fsscUniqueId = isoBuffer.get(Constants.DE11)
								+ isoBuffer.get(Constants.DE41);

						// DCC Enquiry Request RRN Added
						isoBuffer.put(Constants.DE37, dccParams.getRrn());

						updateTimer(fsscUniqueId, fssc.getIIN(), td, isoBuffer,
								isoBuffer.get(Constants.DE41),
								dccParams.getTranId());

						fssc.setMessage(hostRequest);
						fssc.setSource(fssc.getDestination());
						fssc.setDestination(dccParams.getDccStationName());
						fssc.setAlternateDestinationArray(new String[] { dccParams
								.getDccAlternateStationName() == null ? ""
								: dccParams.getDccAlternateStationName() });
						return fssc.toString();
					}
				}
				JSONArray jsonArr = new JSONArray(listObj);
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("DCC", jsonArr);
				isoBuffer.put(Constants.DE63, jsonObj.toString());
				isoBuffer.put(Constants.DE39, Constants.SUCCESS);

			} else {

				throw new PosException(rspStatus);

			}
		} catch (PosException e) {
			throw e;
		} catch (Exception e) {
			Log.error("List Empty from Procedure:::"
					+ StoredProcedureInfo.DCC_VALIDATE + e.getMessage(), e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
		/*
		 * catch (Exception e) { Log.error("List Empty from Procedure:::" +
		 * StoredProcedureInfo.DCC_VALIDATE + e.getMessage(), e); }
		 */

		fssc.setSource(fssc.getDestination());
		fssc.setMessage(clientApi.build(isoBuffer));
		fssc.setDestination(clientStation);
		fssc.setAlternateSourceArray(new String[0]);

		isoBuffer.put(Constants.DE39, rspStatus);
		if (rspStatus.equals(Constants.SUCCESS)) {
			try {
				int status = (rspStatus.equals(Constants.SUCCESS)) ? Constants.TXNLOG_STATUS_SUCCESS
						: Constants.TXNLOG_STATUS_FAILED;

				updateTxnLog(isoBuffer, status, tranId, fssc.getDestination(),
						fssc.getIIN(),clientApi);
				Log.info("Client response", isoBuffer.logData());
			} catch (PosException e) {
				isoBuffer.put(Constants.DE39, e.getMessage());
			}
		}

		return fssc.toString();

	}

	private String updateTxnLog(IsoBuffer isoBuffer, int status, String tranID,
			String hostStationName, String fssIIN,ClientApi clientApi) throws PosException {

		

		
		String procedurename =clientApi.checkHostToHost()?StoredProcedureInfo.TXN_RESPONSE_UPDATE_AGGISO8583:StoredProcedureInfo.TXN_RESPONSE_UPDATE;
		
		TransactionResponse txnRsp = transactionLogService.updateTxnLog(
				isoBuffer.get(Constants.DE2, null), isoBuffer
						.get(Constants.DE41), tranID, hostStationName,
				isoBuffer.get(Constants.DE39), isoBuffer
						.isFieldEmpty(Constants.DE38) ? Constants.ZERO
						: isoBuffer.get(Constants.DE38), status, isoBuffer
						.get(Constants.DE42), isoBuffer.get(Constants.DE3)
						.substring(0, 2) + isoBuffer.get(Constants.DE25),
				fssIIN, isoBuffer.get(Constants.DE11), isoBuffer.get(
						Constants.DE7, null),procedurename);
		Log.debug("Mapped response code from procedure ",
				txnRsp.getResponseCode());
		isoBuffer.put(Constants.DE39, txnRsp.getResponseCode());
		// Log.debug("transaction descrptn", txnRsp.getResponseDescription());
		return txnRsp.getResponseDescription();
	}

	protected String recordCash(IsoBuffer isoBuffer, String rrn,
			FssConnect fssc, ClientApi clientApi)
			throws UnsupportedEncodingException, JSONException {
		isoBuffer.put(Constants.DE39, Constants.SUCCESS);
		isoBuffer.put(Constants.DE37, Util.appendChar(rrn, '0', 12, true));
		isoBuffer.put(Constants.DE12, Util.getTime(FORMAT_TXN_TIME));
		isoBuffer.put(Constants.DE13, Util.getTime(FORMAT_TXN_DATE));
		isoBuffer.disableField(Constants.DE62);
		return respondClient(isoBuffer, fssc, clientApi);
	}

	protected String offlineTranx(IsoBuffer isoBuffer, String rrn,
			FssConnect fssc, ClientApi clientApi)
			throws UnsupportedEncodingException, JSONException {
		// isoBuffer.put(Constants.ISO_MSG_TYPE,
		// Constants.MSG_TYPE_RSP_OFFLINE);
		isoBuffer.put(Constants.DE39, Constants.ERR_OFFLINE_APPROVED);
		isoBuffer.put(Constants.DE37, Util.appendChar(rrn, '0', 12, true));
		isoBuffer.put(Constants.DE12, Util.getTime(FORMAT_TXN_TIME));
		isoBuffer.put(Constants.DE13, Util.getTime(FORMAT_TXN_DATE));
		isoBuffer.disableField(Constants.DE62);
		return respondClient(isoBuffer, fssc, clientApi);
	}

	@SuppressWarnings("unchecked")
	protected List<Data> getTxnFlowData(String procedureName,
			IsoBuffer isoBuffer, String mspAcr, String srcStation,
			TerminalOperator operator, String msgVersion, boolean isOffline,
			Map<EmvTags, String> emvTags) throws PosException {
		try {
			List<Object> objList = transactionLogService.fetchData(
					procedureName, isoBuffer, mspAcr, srcStation, operator,
					isOffline, msgVersion, emvTags);
			String status = ((List<ResponseStatus>) objList.get(0)).get(0)
					.getStatus();
			Log.debug("Response from " + procedureName, status);

			if (!Constants.SUCCESS.equals(status)) {
				if (objList.size() > 1) {
					List<HostData> hdList = (List<HostData>) objList.get(2);
					if (!hdList.isEmpty()) {
						isoBuffer.put(Constants.DE37, Util.appendChar(hdList
								.get(0).getRrn(), '0', 12, true));
						if (null == hdList.get(0).getOrgAuthCode()
								|| hdList.get(0).getOrgAuthCode().equals("")) {
							isoBuffer.put(Constants.DE38, "");
						} else {
							isoBuffer.put(Constants.DE38, hdList.get(0)
									.getOrgAuthCode());
						}
						isoBuffer.setTranId(hdList.get(0).getTranId());
					}
					if (!hdList.isEmpty()
							&& Util.isNullOrEmpty(isoBuffer.get(Constants.DE37)))
						throw new PosException(status + "-"
								+ isoBuffer.get(Constants.DE37));
					else
						throw new PosException(status);
				} else {
					throw new PosException(status);
				}

			}

			ArrayList<Data> list = new ArrayList<Data>();

			list.add(((List<HostData>) objList.get(2)).get(0));

			// list.add(((List<HsmData>) objList.get(1)).get(0));
			// list.add(((List<HostData>) objList.get(2)).get(0));
			int hsmDataSize = ((List<HsmData>) objList.get(1)).size();
			if (hsmDataSize != 0) {
				HsmData hsmData = ((List<HsmData>) objList.get(1)).get(0);
				Log.debug("Length:::", String.valueOf(hsmDataSize));
				if (hsmDataSize == 2) {
					hsmData.setTerminalPPK(((List<HsmData>) objList.get(1))
							.get(1).getP2peTerminalPPK());
					hsmData.setCheckSum(((List<HsmData>) objList.get(1)).get(1)
							.getP2peCheckSum());
					Log.debug("P2P and Pin Enabled", hsmData.toString());
					list.add(hsmData);
				} else {
					list.add(hsmData);

				}
			}

			return list;
		} catch (PosException e) {
			throw e;
		} catch (SQLException e) {
			Log.error("getTxnFlowData ", e);
			throw new PosException(Constants.ERR_DATABASE);
		} catch (Exception e) {
			Log.error("getTxnFlowData ", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
	}

	
	
	@SuppressWarnings("unchecked")
	protected List<Data> getISO8583TxnFlowData(String procedureName,
			IsoBuffer isoBuffer, String mspAcr, String srcStation,
			TerminalOperator operator, String msgVersion, boolean isOffline,
			Map<EmvTags, String> emvTags) throws PosException {
		try {
			List<Object> objList = transactionLogService.fetchISO8583Data(
					procedureName, isoBuffer, mspAcr, srcStation, operator,
					isOffline, msgVersion, emvTags);
			String status = ((List<ResponseStatus>) objList.get(0)).get(0)
					.getStatus();
			Log.debug("Response from " + procedureName, status);

			if (!Constants.SUCCESS.equals(status)) {
				if (objList.size() > 1) {
					List<HostData> hdList = (List<HostData>) objList.get(2);
					if (!hdList.isEmpty()) {
						if (null == hdList.get(0).getOrgAuthCode()
								|| hdList.get(0).getOrgAuthCode().equals("")) {
							isoBuffer.put(Constants.DE38, "");
						} else {
							isoBuffer.put(Constants.DE38, hdList.get(0)
									.getOrgAuthCode());
						}
						isoBuffer.setTranId(hdList.get(0).getTranId());
					}
					if (!hdList.isEmpty()
							&& Util.isNullOrEmpty(isoBuffer.get(Constants.DE37)))
						throw new PosException(status + "-"
								+ isoBuffer.get(Constants.DE37));
					else
						throw new PosException(status);
				} else {
					throw new PosException(status);
				}

			}

			ArrayList<Data> list = new ArrayList<Data>();

			list.add(((List<HostData>) objList.get(2)).get(0));

			// list.add(((List<HsmData>) objList.get(1)).get(0));
			// list.add(((List<HostData>) objList.get(2)).get(0));
			int hsmDataSize = ((List<HsmData>) objList.get(1)).size();
			if (hsmDataSize != 0) {
				HsmData hsmData = ((List<HsmData>) objList.get(1)).get(0);
				Log.debug("Length:::", String.valueOf(hsmDataSize));
				if (hsmDataSize == 2) {
					hsmData.setTerminalPPK(((List<HsmData>) objList.get(1))
							.get(1).getP2peTerminalPPK());
					hsmData.setCheckSum(((List<HsmData>) objList.get(1)).get(1)
							.getP2peCheckSum());
					Log.debug("P2P and Pin Enabled", hsmData.toString());
					list.add(hsmData);
				} else {
					list.add(hsmData);

				}
			}

			return list;
		} catch (PosException e) {
			throw e;
		} catch (SQLException e) {
			Log.error("getTxnFlowData ", e);
			throw new PosException(Constants.ERR_DATABASE);
		} catch (Exception e) {
			Log.error("getTxnFlowData ", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
	}

	
	
	// Added by Bebismita
	@SuppressWarnings({ "unused" })
	private String p2pe(String encryptedPan, String rrn, HsmData hsmData,
			HostData hostData, String mspAcr, IsoBuffer clientBuffer)
			throws PosException {
		String cardNo = null;

		// calling Prism
		HsmApi hsmApi = apiFactory.getHsmApi(hsmData.getHsmModel());
		return hsmApi.translateP2Pcmd(clientBuffer, hsmData, mspAcr);

		/*
		 * try { // do p2pe List<Object> objList = updatePan(cardNo,
		 * PosUtil.maskCardNumber(cardNo, mspAcr), Util.hashSHA512(cardNo, ""),
		 * rrn, mspAcr); String status; if (!(Constants.SUCCESS .equals(status =
		 * ((List<ResponseStatus>) objList.get(0)) .get(0).getStatus()))) throw
		 * new PosException(status); hsmData = ((List<HsmData>)
		 * objList.get(1)).get(0); hostData = ((List<HostData>)
		 * objList.get(2)).get(0); } catch (PosException e) { throw e; } catch
		 * (Exception e) { Log.error("P2pe error ", e); throw new
		 * PosException(Constants.ERR_P2PE); }
		 */
	}

	private void translatePIN(IsoBuffer isoBuffer, HsmData hsmData,
			String zonalKey, String zpkCheckSum, String mspAcr, String keyEntry)
			throws PosException {
		isoBuffer.put(Constants.DE52, isoBuffer.get(Constants.DE52)
				.toUpperCase());
		HsmApi hsmApi = apiFactory.getHsmApi(hsmData.getHsmModel());
		hsmApi.setDestination(hsmData.getStationName());
		hsmApi.setAlternateDestination(hsmData.getAlternateStationName());
		HsmResponse hsmRspBean = hsmApi.translatePIN(
				isoBuffer.get(Constants.DE52), hsmData.getTerminalPPK(),
				hsmData.getCheckSum(), isoBuffer.get(Constants.DE2),
				hsmData.getCryptoType(), zonalKey, zpkCheckSum, mspAcr,
				isoBuffer, keyEntry);
		if (!Constants.SUCCESS.equals(hsmRspBean.getRspCode()))
			throw new PosException(Constants.ERR_HSM_CONNECT);
		isoBuffer.put(Constants.DE52, hsmRspBean.getPinBlock());
	}

	@SuppressWarnings("unused")
	private List<Object> updatePan(String pan, String maskedPan,
			String hashedPan, String rrn, String mspAcr) throws PosException {
		List<Object> params = new ArrayList<Object>();
		params.add(pan);
		params.add(rrn);
		List<Class<?>> classList = new ArrayList<Class<?>>();
		classList.add(ResponseStatus.class);
		classList.add(HsmData.class);
		classList.add(HostData.class);
		try {
			return apiFactory.getStoredProcedureApi().getBean(params,
					StoredProcedureInfo.UPDATE_PAN_AFTER_P2PE, classList,
					mspAcr);
		} catch (SQLException e) {
			Log.error("update pan after p2pe", e);
			throw new PosException(Constants.ERR_DATABASE);
		} catch (Exception e) {
			Log.error("update pan after p2pe", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
	}

	@Override
	protected void modifyB4RespondingClient(IsoBuffer isoBuffer,
			FssConnect fssc, ClientApi clientApi) throws JSONException {

		String rrn = "";
		if (Constants.PROC_CODE_CASH_RECORDING.equals(isoBuffer.get(
				Constants.DE3).substring(0, 2))) {
			rrn = isoBuffer.get(Constants.DE5);
		} else if (isoBuffer.get(Constants.DE39).equals(
				Constants.ERR_HSM_CONNECT)) {
			Log.debug("HSM ERROR:::::::",
					"TranID " + isoBuffer.get(Constants.DE5) + "Response Code "
							+ isoBuffer.get(Constants.DE39));
			rrn = isoBuffer.get(Constants.DE5);
		} else {
			rrn = isoBuffer.isFieldEmpty(Constants.DE37) ? null : isoBuffer
					.get(Constants.DE37);
		}

		String procCode = isoBuffer.isFieldEmpty(Constants.DE3) ? null
				: isoBuffer.get(Constants.DE3).substring(0, 2)
						+ isoBuffer.get(Constants.DE25);
		boolean offlineApproved = false;
		if (Constants.ERR_OFFLINE_APPROVED
				.equals(isoBuffer.get(Constants.DE39))) {
			offlineApproved = true;
			isoBuffer.put(Constants.DE39, Constants.SUCCESS);
			isoBuffer.disableField(Constants.DE55); // added on 23/01
			if (isoBuffer.get(Constants.DE38).equals("")) {
				// isoBuffer.disableField(Constants.DE38);
				isoBuffer.put(Constants.DE38, IsoUtil.genRRN(6));
			} else {
				isoBuffer.put(Constants.DE38, isoBuffer.get(Constants.DE38));
			}
		}
		isoBuffer.put(
				Constants.ISO_MSG_TYPE,
				"0"
						+ (Integer.parseInt(isoBuffer
								.get(Constants.ISO_MSG_TYPE)) + 10));
		int status = Constants.SUCCESS.equals(isoBuffer.get(Constants.DE39)) ? Constants.TXNLOG_STATUS_SUCCESS
				: Constants.TXNLOG_STATUS_FAILED;

		String procedurename =clientApi.checkHostToHost()?StoredProcedureInfo.TXN_RESPONSE_UPDATE_AGGISO8583:StoredProcedureInfo.TXN_RESPONSE_UPDATE;
		
		TransactionResponse txnRsp = transactionLogService.updateTxnLog(
				isoBuffer.get(Constants.DE2, null), isoBuffer
						.get(Constants.DE41),
				isoBuffer.getTranId(), // rrn
				null, offlineApproved ? Constants.ERR_OFFLINE_APPROVED
						: isoBuffer.get(Constants.DE39), isoBuffer
						.isFieldEmpty(Constants.DE38) ? Constants.ZERO
						: isoBuffer.get(Constants.DE38), status, isoBuffer
						.get(Constants.DE42), procCode, fssc.getIIN(),
				isoBuffer.get(Constants.DE11), isoBuffer.get(Constants.DE7),procedurename);
		if (!Constants.SUCCESS.equals(txnRsp.getResponseCode()))
			isoBuffer.put(Constants.DE39, txnRsp.getResponseCode());

		if ((isoBuffer.get(Constants.DE25).equals(Constants.DCC_ENQUIRY))
				|| (isoBuffer.get(Constants.DE25)
						.equals(Constants.DCC_PREAUTH_COMPLETION))
				|| (isoBuffer.get(Constants.DE25)
						.equals(Constants.DCC_REFUND_ENQUIRY))) {
			isoBuffer.put(Constants.DE64, txnRsp.getResponseDescription());
		} else if (!isoBuffer.isFieldEmpty(Constants.DE44)
				&& isoBuffer.get(Constants.ISO_MSG_TYPE).equals(
						Constants.MSG_TYPE_NETWORK_MANAGEMENT)) {
			isoBuffer.put(Constants.DE63,
					Validator.getErrorDescription(Constants.LOGON_NOT_ALLOWED));
		} else if (Constants.PROC_CODE_CASH_RECORDING.equals(isoBuffer.get(
				Constants.DE3).substring(0, 2))
				|| Constants.ERR_HSM_CONNECT.equals(isoBuffer
						.getObject(Constants.DE39))) {
			isoBuffer.disableField(Constants.DE5); // /disabled P-5(tranID)
		} else {
			isoBuffer.put(Constants.DE63, txnRsp.getResponseDescription());
		}
		clientApi.modifyBits4Response(isoBuffer, null);
	}

}
