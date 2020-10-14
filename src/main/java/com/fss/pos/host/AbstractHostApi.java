package com.fss.pos.host;

import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandle;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.fss.commons.utils.CipherUtil;
import com.fss.pos.base.api.client.ClientApi;
import com.fss.pos.base.api.client.ClientData;
import com.fss.pos.base.api.client.Validator;
import com.fss.pos.base.api.db.storedprocedure.ResponseStatus;
import com.fss.pos.base.api.db.storedprocedure.StoredProcedureInfo;
import com.fss.pos.base.api.host.HostApi;
import com.fss.pos.base.api.host.HostData;
import com.fss.pos.base.api.host.HostRequest;
import com.fss.pos.base.api.host.HostResponse;
import com.fss.pos.base.api.host.TimeoutData;
import com.fss.pos.base.api.host.ZonalKeysModel;
import com.fss.pos.base.api.hsm.HsmApi;
import com.fss.pos.base.api.hsm.HsmResponse;
import com.fss.pos.base.api.transactions.Transactions;
import com.fss.pos.base.commons.AbstractBeanData;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.DccParams;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.StaticStore;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.base.commons.utils.PosUtil;
import com.fss.pos.base.commons.utils.ThreadLocalUtil;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.base.factory.ApiFactory;
import com.fss.pos.base.factory.Factories;
import com.fss.pos.base.services.fssconnect.FssConnect;
import com.fss.pos.base.services.transactionlog.TransactionLogService;
import com.fss.pos.base.services.transactionlog.TransactionResponse;

/**
 * The abstraction to handle the host transaction messages.
 * 
 * @author Priyan
 * @see HostApi
 */
public abstract class AbstractHostApi implements HostApi {

	static ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private TransactionLogService txnLogService;
	protected static final String FORMAT_TXN_DATE_TIME = "MMddHHmmss";
	protected static final String KEY_IMPORT_DE70 = "101";
	private static final String DE70_ABSA_KEYIMPORT = "162"; // ABSA Related for
																// handling
																// request from
																// host
	protected static final List<String> EMV_TAG_LEN_2;

	private static final String JSON_KEY_RSP_CODE = "respCode";
	private static final String JSON_KEY_ZPK = "zpk";
	private static final String JSON_KEY_CHECKDIGIT = "checkDigit";
	private static final String CUT_OVER = "CUTOVER";
	private static final String RECON_MSG = "RECON";
	private static final String DCC_ENQUIRY = "99";
	private static final String DCC_COMPLETION = "96";
	private static final String DCC_PREAUTH_COMPLETION = "95";
	//private static final String AMEX_MSG_PROTOCOL = "20";
	//private static final String JCB_MSG_PROTOCOL = "22";
	private static final String AMEX_REVERSAL_RES = "1430";

	private static final Map<String, String> HOSTMAP;

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
		HOSTMAP = Collections.unmodifiableMap(hostApi);
	}

	static {
		EMV_TAG_LEN_2 = new ArrayList<String>();
		EMV_TAG_LEN_2.add("82");
		EMV_TAG_LEN_2.add("84");
		EMV_TAG_LEN_2.add("95");
		EMV_TAG_LEN_2.add("9A");
		EMV_TAG_LEN_2.add("9C");
		EMV_TAG_LEN_2.add("4F");
		EMV_TAG_LEN_2.add("91");
		EMV_TAG_LEN_2.add("71");
		EMV_TAG_LEN_2.add("72");
		EMV_TAG_LEN_2.add("9B");
		EMV_TAG_LEN_2.add("8A");

	}

	@Autowired
	private TransactionLogService transactionLogger;

	@Autowired
	private ApiFactory apiFactory;

	private Map<String, MethodHandle> methodHandles;

	public AbstractHostApi() {
		methodHandles = new ConcurrentHashMap<String, MethodHandle>();
		if (this.getClass().isAnnotationPresent(HostRequest.class)) {
			this.setFactoryPrefix(Factories.HOSTREQUEST.toString());
			setType(this.getClass().getAnnotation(HostRequest.class).HostType());
		} else if (this.getClass().isAnnotationPresent(HostResponse.class)) {
			this.setFactoryPrefix(Factories.HOSTRESPONSE.toString());
			setType(this.getClass().getAnnotation(HostResponse.class)
					.HostType());
		}
	}

	@Override
	public String process(FssConnect fssc) throws UnsupportedEncodingException,
			JSONException {
		IsoBuffer isoBuffer = null;
		try {
			 Log.debug("data from host", fssc.getMessage());
			isoBuffer = parse(fssc.getMessage());
			Log.debug("HostBuffer", isoBuffer.logData());
			if (isoBuffer.get(Constants.ISO_MSG_TYPE).startsWith("9")) {
				Log.info("Error Message From Host ", fssc.getMessage()
						.substring(2));
				return null;
			}
			if (Constants.MSG_TYPE_NETWORK_MANAGEMENT.equals(isoBuffer
					.get(Constants.ISO_MSG_TYPE))
					|| Constants.MSG_TYPE_NETWORK_MANAGEMENT_ADVICE
							.equals(isoBuffer.get(Constants.ISO_MSG_TYPE))
					|| Constants.MSG_TYPE_NETWORK_MANAGEMENT_REPEAT
							.equals(isoBuffer.get(Constants.ISO_MSG_TYPE))) {

				if (isoBuffer.get(Constants.DE70).equals(
						Constants.CUT_OVER_VALUE)
						|| isoBuffer.get(Constants.DE70).equals(
								Constants.CUT_OVER_VALUE_END)) {
					Log.debug("CutOver request ", isoBuffer.get(Constants.DE70));
					return handleCutOverRequest(fssc, isoBuffer);
				} else {
					return null;
				}
			}
			if (Constants.MSG_TYPE_SETTLEMENT.equals(isoBuffer
					.get(Constants.ISO_MSG_TYPE))
					|| Constants.MSG_TYPE_ACQ_RECON_ADVICE.equals(isoBuffer
							.get(Constants.ISO_MSG_TYPE))
					|| Constants.MSG_TYPE_ACQ_RECON_REPEAT.equals(isoBuffer
							.get(Constants.ISO_MSG_TYPE))
					|| Constants.MSG_TYPE_CARD_ISSUER_RECON.equals(isoBuffer
							.get(Constants.ISO_MSG_TYPE))
					|| Constants.MSG_TYPE_ISSUER_RECON_ADVICE.equals(isoBuffer
							.get(Constants.ISO_MSG_TYPE))
					|| Constants.MSG_TYPE_ISSUER_RECON_REPEAT.equals(isoBuffer
							.get(Constants.ISO_MSG_TYPE))) {

				return handleReconciliationMessage(fssc, isoBuffer);
			}
			if (Constants.MSG_TYPE_NETWORK_MANAGEMENT.equals(isoBuffer
					.get(Constants.ISO_MSG_TYPE))) {
				if (isoBuffer.get(Constants.DE70).equals(DE70_ABSA_KEYIMPORT))
					return handleKeyExcahngeRequest(isoBuffer, fssc);
				else
					return null;
			}

			if (Constants.MSG_TYPE_RSP_NETWORK_MANAGEMENT.equals(isoBuffer
					.get(Constants.ISO_MSG_TYPE))) {
				return handleNetworkResponse(isoBuffer, fssc);
			}

			String apiId = ((StaticStore.hostMessageProtocols
					.get(fssc.getIIN()).get(fssc.getSource())) == null) ? (StaticStore.alterStationDetails
					.get(fssc.getIIN()).get(fssc.getSource()))
					: (StaticStore.hostMessageProtocols.get(fssc.getIIN())
							.get(fssc.getSource()));

			if (Constants.ENQUIRY_MSG_RESPONSE.equals(isoBuffer
					.get(Constants.ISO_MSG_TYPE)) && apiId.equals("27")) {
				return handleDCCEnqiry(fssc, isoBuffer);
			}

			if (Constants.COMPLETION_MSG_RESPONSE.equals(isoBuffer
					.get(Constants.ISO_MSG_TYPE)) && apiId.equals("27")) {
				return handleDCCEnqiry(fssc, isoBuffer);
			}

			if (Constants.MSG_TYPE_RSP_REV_410.equals(isoBuffer
					.get(Constants.ISO_MSG_TYPE)) && apiId.equals("27")) {
				return handleDCCEnqiry(fssc, isoBuffer);
			}

			return handleTransactionMessage(fssc, isoBuffer);

		} catch (Exception e) {
			Log.error("AbstractHostApi parsing error ", e);
			Log.info("Parsing Error !!! Message From Host ", fssc.getMessage());
			return null;
		}
	}

	private String handleKeyExcahngeRequest(IsoBuffer isoBuffer, FssConnect fssc) {
		try {
			Log.debug("Inside key exchange process method ",
					isoBuffer.toString());
			String apiId = StaticStore.hostMessageProtocols.get(fssc.getIIN())
					.get(fssc.getSource());
			IsoBuffer clientIsoBuffer = new IsoBuffer();
			HostData data = new HostData();
			if (DE70_ABSA_KEYIMPORT.equals(isoBuffer.get(Constants.DE70))) {

				String acquirerIdPk = StaticStore.acquirerDetails.get(
						fssc.getIIN()).get(fssc.getSource());
				String source = fssc.getSource();
				ZonalKeysModel zonalKeyModel = transactionLogger
						.fetchHsmDetails(new BigDecimal(acquirerIdPk),
								fssc.getIIN(), StoredProcedureInfo.HSM_DETAILS);
				Log.debug("Zonal Key Model::::", zonalKeyModel.toString());
				String responseHsm = importKeyForHost(isoBuffer, zonalKeyModel,
						fssc.getIIN());
				JSONObject jsonRsp = new JSONObject(responseHsm);
				if (!(jsonRsp.get(JSON_KEY_RSP_CODE).equals(Constants.SUCCESS)))
					isoBuffer.put(Constants.DE39,
							Constants.ERR_HSM_PROCESSING_ERR);
				else if (!(jsonRsp.getString(JSON_KEY_CHECKDIGIT).substring(0,
						4).equals(isoBuffer.get(Constants.DE120))))
					isoBuffer.put(Constants.DE39,
							Constants.ERR_HSM_PROCESSING_ERR);
				else {
					String responseCode = transactionLogger.updateZonalKeys(
							new BigDecimal(acquirerIdPk), fssc.getIIN(),
							StoredProcedureInfo.UPDATE_KEY_DETAILS,
							jsonRsp.getString(JSON_KEY_ZPK),
							jsonRsp.getString(JSON_KEY_CHECKDIGIT));
					Log.debug("Response Code "
							+ StoredProcedureInfo.UPDATE_KEY_DETAILS,
							responseCode);

					if (responseCode.equals(Constants.SUCCESS))
						isoBuffer.put(Constants.DE39, Constants.SUCCESS);
					else
						isoBuffer.put(Constants.DE39,
								Constants.ERR_HSM_PROCESSING_ERR);
				}

				data.setKeyImport(true);
				String keyExchangeResponseBuild = apiFactory.getHostRequestApi(
						apiId).modiyAndbuild(isoBuffer, clientIsoBuffer, data);

				fssc.setMessage(keyExchangeResponseBuild);
				fssc.setSource(fssc.getDestination());
				fssc.setDestination(source);
				return fssc.toString();
			} else {
				Log.trace("Unhandled 0800 Key Exchange Request Message:::::::::");
				return null;
			}
		} catch (Exception e) {
			Log.error("handleNetworkRequest Key Exchange Timer Data Error ", e);
			return null;
		}
	}

	private String importKeyForHost(IsoBuffer isoBuffer, ZonalKeysModel zKm,
			String mspAcr) throws PosException, JSONException {

		KeyImportResponse kiRes = new KeyImportResponse();
		kiRes.setKeyImport(true);
		modifyFields(isoBuffer, null, kiRes);
		Log.debug(
				"After Modification",
				"CheckSum and ZPK ::::" + kiRes.getCheckDigit()
						+ kiRes.getZpk());

		JSONObject jsonRsp = new JSONObject();
		jsonRsp.put(JSON_KEY_RSP_CODE,
				kiRes.getRespCode() == null ? Constants.ERR_SYSTEM_ERROR
						: kiRes.getRespCode());

		if (Constants.SUCCESS.equals(jsonRsp.get(JSON_KEY_RSP_CODE))) {
			Log.debug("Response", "success in key import");
			HsmApi hsmApi = apiFactory.getHsmApi(zKm.getHsmModelType()
					.toString());
			hsmApi.setDestination(zKm.getHsmStationName());

			HsmResponse hsmRes = hsmApi.importZonalPinKey73Cmd(
					zKm.getAcquirerIndex(), kiRes.getZpk(),
					zKm.getZmkChecksum(), zKm.getKeyLength(), mspAcr);
			jsonRsp.put(JSON_KEY_RSP_CODE, hsmRes.getErrCode());
			jsonRsp.put(JSON_KEY_ZPK, hsmRes.getLmkEncryptedKey());
			jsonRsp.put(JSON_KEY_CHECKDIGIT, hsmRes.getChecksum());
		}
		return jsonRsp.toString();
	}

	private String handleDCCEnqiry(FssConnect fssc, IsoBuffer isoBuffer)
			throws UnsupportedEncodingException, JSONException {
		TimeoutData td = null;
		IsoBuffer clientIsoBuffer = null;
		String uniqueId = null;
		try {
			if (isoBuffer.get(Constants.ISO_MSG_TYPE).equals(
					Constants.COMPLETION_MSG_RESPONSE)
					|| isoBuffer.get(Constants.ISO_MSG_TYPE).equals(
							Constants.MSG_TYPE_RSP_REV_410)) {
				uniqueId = isoBuffer.get(Constants.DE37)
						+ isoBuffer.get(Constants.DE11)
						+ isoBuffer.get(Constants.DE41);
			} else {
				uniqueId = isoBuffer.get(Constants.DE11)
						+ isoBuffer.get(Constants.DE41);
			}

			td = transactionLogger.updateHostLog(isoBuffer.get(Constants.DE41),
					isoBuffer, StoredProcedureInfo.LOG_HOST_RESPONSE,
					fssc.getIIN(), uniqueId, null, null);

			if (td == null) {
				fssc.setAlternateSourceArray(new String[0]);
				fssc.setSource(null);
				fssc.setDestination(null);
				return fssc.toString();
			}

			ThreadLocalUtil.setRRN(td.getThreadLocalId());
			Log.info("Host Response", isoBuffer.logData());
			Log.info("Host Response Code", isoBuffer.get(Constants.DE39));
			clientIsoBuffer = new IsoBuffer(td.getIsoBufferMap());

		} catch (Exception e) {
			Log.error("Exception In DCC Enquiry Response", e);
			fssc.setAlternateSourceArray(new String[0]);
			fssc.setSource(null);
			fssc.setDestination(null);
			return fssc.toString();
		}

		ClientData cd = null;
		try {
			clientIsoBuffer
					.put(Constants.DE37,
							(isoBuffer.get(Constants.ISO_MSG_TYPE)
									.equals(Constants.COMPLETION_MSG_RESPONSE)) ? td
									.getThreadLocalId() : isoBuffer
									.get(Constants.DE37));
			clientIsoBuffer.put(Constants.DE41, td.getTerminalId());
			clientIsoBuffer.put(Constants.DE42, td.getMerchantId());
			clientIsoBuffer
					.put(Constants.DE25,
							(isoBuffer.get(Constants.ISO_MSG_TYPE).equals(
									Constants.COMPLETION_MSG_RESPONSE) || isoBuffer
									.get(Constants.ISO_MSG_TYPE).equals(
											Constants.MSG_TYPE_RSP_REV_410)) ? Constants.DCC_COMPLETION
									: Constants.DCC_ENQUIRY);
			// clientIsoBuffer.put(Constants.DE25, Constants.DCC_ENQUIRY);

			cd = getClientData(clientIsoBuffer, td);

			modifyFields(isoBuffer, clientIsoBuffer, cd);

			if (clientIsoBuffer.get(Constants.DE25).equals(
					Constants.DCC_ENQUIRY)
					|| clientIsoBuffer.get(Constants.DE25).equals(
							Constants.DCC_PREAUTH_COMPLETION)) {
				if (Constants.SUCCESS.equals(isoBuffer.get(Constants.DE39))) {
					String respCode = updateDccData(isoBuffer, td);
					if (respCode.equals(Constants.SUCCESS)) {
						return toClient(fssc, clientIsoBuffer, isoBuffer, td,
								cd);
					} else {
						isoBuffer.put(Constants.DE39,
								Constants.ERR_SYSTEM_ERROR);
						Log.error("handleTransactionMessage process() "
								+ respCode, null);
						return toClient(fssc, clientIsoBuffer, isoBuffer, td,
								cd);
					}
				} else {
					isoBuffer.put(Constants.DE39,
							Constants.ERR_NO_RESPONSE_MONEX);
					Log.error(
							"handleTransactionMessage process() "
									+ isoBuffer.get(Constants.DE39), null);
					return toClient(fssc, clientIsoBuffer, isoBuffer, td, cd);
				}

			} else if (clientIsoBuffer.get(Constants.DE25).equals(
					Constants.DCC_COMPLETION)) {
			
				isoBuffer = new IsoBuffer(td.getIsoBufferMap());

				isoBuffer.put(Constants.DE25, td.getUti());
				clientIsoBuffer.put(Constants.DE25, td.getUti());
				isoBuffer.put(Constants.DE42, td.getMerchantId());
				clientIsoBuffer.put(Constants.DE42, td.getMerchantId());
				clientIsoBuffer.put(Constants.DE3, td.getZmkCheckSum());
				isoBuffer.put(Constants.DE3, td.getZmkCheckSum());
				isoBuffer.disableField(Constants.DE22);
				isoBuffer.put(Constants.DE39,
						clientIsoBuffer.get(Constants.DE39));

				boolean isReversal = Constants.MSG_TYPE_RSP_REV_410
						.equals(isoBuffer.get(Constants.ISO_MSG_TYPE))
						|| Constants.MSG_TYPE_RSP_REV_430.equals(isoBuffer
								.get(Constants.ISO_MSG_TYPE))
						|| Constants.MSG_TYPE_RSP_REV_1430.equals(isoBuffer
								.get(Constants.ISO_MSG_TYPE));
				cd.setReversal(isReversal);

				String msgProc = ((StaticStore.hostMessageProtocols.get(fssc
						.getIIN()).get(td.getHostStationName())) == null) ? (StaticStore.alterStationDetails
						.get(fssc.getIIN()).get(td.getHostStationName()))
						: (StaticStore.hostMessageProtocols.get(fssc.getIIN())
								.get(td.getHostStationName()));

				if (msgProc != null && !msgProc.equals("27")) {
					apiFactory.getHostResponseApi(msgProc).modiyAndbuild(
							isoBuffer, clientIsoBuffer, cd);
				}
				fssc.setSource(td.getMyStation());
				fssc.setDestination(td.getClientStationName());
				fssc.setAlternateDestinationArray(new String[0]);
				fssc.setMessage(apiFactory.getClientApi(td.getClientClass())
						.build(isoBuffer));

				return fssc.toString();
			}

		} catch (StringIndexOutOfBoundsException e) {
			isoBuffer.put(Constants.DE39, Constants.ERR_NO_RESPONSE_MONEX);
			Log.error("handleTransactionMessage process() ", e);
			fssc.setAlternateSourceArray(new String[0]);
			fssc.setSource(null);
			fssc.setDestination(null);
			return fssc.toString();
		} catch (Exception e) {
			isoBuffer.put(Constants.DE39, Constants.ERR_SYSTEM_ERROR);
			Log.error("handleTransactionMessage process() ", e);
			fssc.setAlternateSourceArray(new String[0]);
			fssc.setSource(null);
			fssc.setDestination(null);
			return fssc.toString();
		}

		return toClient(fssc, clientIsoBuffer, isoBuffer, td, cd);
	}

	private String updateDccData(IsoBuffer isoBuffer, TimeoutData td) {

		List<Object> listObj = new ArrayList<Object>();
		String respCode = "";
		try {
			List<Object> params = new ArrayList<Object>();

			JSONObject jsonObject = new JSONObject(isoBuffer
					.get(Constants.DE63).toString());
			String dcc = jsonObject.get("DCC").toString();
			dcc = dcc.substring(1, dcc.length() - 1);
			JSONObject jsonObj = new JSONObject(dcc);
			params.add(jsonObj.get("CACD"));
			params.add(Integer.parseInt(jsonObj.get("CNCD").toString()));
			params.add(jsonObj.get("CVAL"));
			params.add(jsonObj.get("EXCR"));
			params.add(jsonObj.get("COMM"));
			params.add(jsonObj.get("MARK"));
			params.add(jsonObj.get("ACMV"));
			params.add(jsonObj.get("CAMV"));
			params.add(jsonObj.get("QUOT"));
			params.add(td.getTranId());
			// /exponents
			params.add(Integer.parseInt(jsonObj.get("CEXP").toString()));
			params.add(Integer.parseInt(jsonObj.get("EXXP").toString()));
			params.add(Integer.parseInt(jsonObj.get("ACXP").toString()));
			params.add(Integer.parseInt(jsonObj.get("CAXP").toString()));

			respCode = apiFactory.getStoredProcedureApi().getString(params,
					StoredProcedureInfo.DCC_RESPONSE_UPDATE, td.getIin());

			jsonObj.remove("ACXP");
			jsonObj.remove("CAXP");
			jsonObj.remove("EXXP");

			listObj.add(jsonObj);
			JSONArray jsonArr = new JSONArray(listObj);
			JSONObject jsonObj1 = new JSONObject();
			jsonObj1.put("DCC", jsonArr);
			isoBuffer.put(Constants.DE63, jsonObj1.toString());

		} catch (Exception e) {
			Log.error("Exception Occoured while updating DCC Response", e);
			return null;
		}
		return respCode;
	}

	private String handleReconciliationMessage(FssConnect fssc,
			IsoBuffer isoBuffer) {

		IsoBuffer clientIsoBuffer = new IsoBuffer();
		String response;

		try {
			String apiId = ((StaticStore.hostMessageProtocols
					.get(fssc.getIIN()).get(fssc.getSource())) == null) ? (StaticStore.alterStationDetails
					.get(fssc.getIIN()).get(fssc.getSource()))
					: (StaticStore.hostMessageProtocols.get(fssc.getIIN())
							.get(fssc.getSource()));
			clientIsoBuffer.put(Constants.DE3, RECON_MSG);
			ClientData cd = new ClientData();
			response = apiFactory.getHostRequestApi(apiId).modiyAndbuild(
					isoBuffer, clientIsoBuffer, cd);
			String source = fssc.getSource();
			fssc.setSource(fssc.getDestination());
			fssc.setDestination(source);
			fssc.setMessage(response);
		} catch (Exception e) {
			Log.error("handleCutOver process", e);
			return null;

		}

		Log.debug("recon final fssc msg", fssc.toString());
		return fssc.toString();
	}

	private String handleNetworkResponse(IsoBuffer isoBuffer, FssConnect fssc) {
		try {
			if (KEY_IMPORT_DE70.equals(isoBuffer.get(Constants.DE70))) {
				TimeoutData td = new TimeoutData();
				String uniqueId = PosUtil.getIsoFsscKeyImportUId(isoBuffer);

				td = transactionLogger.updateHostLog(null, isoBuffer,
						StoredProcedureInfo.LOG_HOST_RESPONSE, fssc.getIIN(),
						uniqueId, null, null);

				if (td == null) {
					Log.info("Host Late Network Response", isoBuffer.logData());
					Log.info("Host Late Network Response Code",
							isoBuffer.get(Constants.DE39));
					return null;
				}
				ThreadLocalUtil.setRRN(td.getThreadLocalId());
				Log.info("Host Network Response", isoBuffer.toString());
				Log.info("Host Network Response Code",
						isoBuffer.get(Constants.DE39));

				fssc.setMessage(importKey(isoBuffer, td));
				fssc.setSource(td.getThisSource());
				fssc.setDestination(td.getClientStationName());
				return fssc.toString();
			} else {
				Log.trace("Unhandled 0810 message");
				return null;
			}
		} catch (Exception e) {
			Log.error("handleNetworkResponse get timer data error ", e);
			return null;
		}
	}

	private String importKey(IsoBuffer isoBuffer, TimeoutData td)
			throws PosException, JSONException {

		KeyImportResponse kiRes = new KeyImportResponse();
		kiRes.setKeyImport(true);
		modifyFields(isoBuffer, null, kiRes);
		Log.debug("After Modification", kiRes.getCheckDigit());

		JSONObject jsonRsp = new JSONObject();
		jsonRsp.put("respCode",
				kiRes.getRespCode() == null ? Constants.ERR_SYSTEM_ERROR
						: kiRes.getRespCode());

		if (Constants.SUCCESS.equals(jsonRsp.get(JSON_KEY_RSP_CODE))) {
			Log.debug("Response", "success in key import");
			HsmApi hsmApi = apiFactory.getHsmApi(td.getHsmId());
			hsmApi.setDestination(td.getHsmStationName());
			HsmResponse hsmRes = hsmApi.importZonalPinKey(
					td.getHsmAcquirerIndex(), kiRes.getZpk(),
					td.getZmkCheckSum(), td.getHsmAcquirerKeyLength(),
					kiRes.getCheckDigit());
			if (!(hsmRes.getErrCode().equals(Constants.SUCCESS)))
				throw new PosException(Constants.ERR_HSM_CONNECT);
			else {
				jsonRsp.put(JSON_KEY_ZPK, hsmRes.getLmkEncryptedKey());
				jsonRsp.put(JSON_KEY_CHECKDIGIT, hsmRes.getChecksum());
			}
		}
		return jsonRsp.toString();
	}

	private String handleCutOverRequest(FssConnect fssc, IsoBuffer isoBuffer) {

		// save p15 time being printing
		IsoBuffer clientIsoBuffer = new IsoBuffer();
		String response;

		try {
			String acqId = ((StaticStore.acquirerDetails.get(fssc.getIIN())
					.get(fssc.getSource())) == null) ? (StaticStore.alteracquirerDetails
					.get(fssc.getIIN()).get(fssc.getSource()))
					: (StaticStore.acquirerDetails.get(fssc.getIIN()).get(fssc
							.getSource()));

			List<Object> params = new ArrayList<Object>();
			params.add(acqId);
			params.add(isoBuffer.get(Constants.ISO_MSG_TYPE));
			params.add(isoBuffer.get(Constants.DE7));
			params.add(isoBuffer.get(Constants.DE11));
			params.add(isoBuffer.get(Constants.DE15));

			String status = apiFactory.getStoredProcedureApi().getString(
					params, StoredProcedureInfo.BENEFIT_CUTOVER_REQ,
					fssc.getIIN());

			Log.debug("response from procedure "
					+ StoredProcedureInfo.BENEFIT_CUTOVER_REQ, status);

			String apiId = ((StaticStore.hostMessageProtocols
					.get(fssc.getIIN()).get(fssc.getSource())) == null) ? (StaticStore.alterStationDetails
					.get(fssc.getIIN()).get(fssc.getSource()))
					: (StaticStore.hostMessageProtocols.get(fssc.getIIN())
							.get(fssc.getSource()));
			clientIsoBuffer.put(Constants.DE3, CUT_OVER);
			ClientData cd = new ClientData();
			response = apiFactory.getHostRequestApi(apiId).modiyAndbuild(
					isoBuffer, clientIsoBuffer, cd);
			String source = fssc.getSource();
			fssc.setSource(fssc.getDestination());
			fssc.setDestination(source);
			fssc.setMessage(response);
		} catch (Exception e) {
			Log.error("handleCutOver process", e);
			return null;
		}

		Log.debug("final fssc response", fssc.toString());
		return fssc.toString();
	}

	private String handleTransactionMessage(FssConnect fssc, IsoBuffer isoBuffer)
			throws UnsupportedEncodingException, JSONException {
		TimeoutData td;
		IsoBuffer clientIsoBuffer = null;

		try {

			StringBuilder apiId=new StringBuilder();
			apiId.append(((StaticStore.hostMessageProtocols
					.get(fssc.getIIN()).get(fssc.getSource())) == null) ? (StaticStore.alterStationDetails
					.get(fssc.getIIN()).get(fssc.getSource()))
					: (StaticStore.hostMessageProtocols.get(fssc.getIIN())
							.get(fssc.getSource())));
			StringBuilder uniqueId = new StringBuilder();
			
			//Commend by senthil
			/*uniqueId.append(Util.getIsoFsscUniqueId(
					isoBuffer.get(Constants.DE37),
					isoBuffer.get(Constants.DE11),
					isoBuffer.get(Constants.DE41)));*/
			//End
			
			// if (apiId.equals(RUPAY_MSG_PROTOCOL)) {
			// uniqueId = Util.getIsoFsscUniqueId(
			// isoBuffer.get(Constants.DE37),
			// isoBuffer.get(Constants.DE11),
			// isoBuffer.get(Constants.DE41));
			// }

			switch(apiId.toString()) {
			
				case Constants.AMEX:
					if(isoBuffer.get(Constants.ISO_MSG_TYPE).equals(
							AMEX_REVERSAL_RES))
						uniqueId.append(Util.getIsoFsscUniqueId(
								isoBuffer.get(Constants.DE4),
								isoBuffer.get(Constants.DE11),
								isoBuffer.get(Constants.DE12)));
					
				case Constants.DINERS:
					uniqueId.append(Util.getIsoFsscUniqueId(
							isoBuffer.get(Constants.DE4),
							isoBuffer.get(Constants.DE11),
							isoBuffer.get(Constants.DE12)));
					break;
					
				case Constants.JCB:
					uniqueId.append(Util.getIsoFsscUniqueId(
							isoBuffer.get(Constants.DE37),
							isoBuffer.get(Constants.DE11),
							isoBuffer.get(Constants.DE7)));
					break;
					
				default:
					uniqueId.append(Util.getIsoFsscUniqueId(
							isoBuffer.get(Constants.DE37),
							isoBuffer.get(Constants.DE11),
							isoBuffer.get(Constants.DE41)));
			
			}

			td = transactionLogger.updateHostLog(isoBuffer.get(Constants.DE41),
					isoBuffer, StoredProcedureInfo.LOG_HOST_RESPONSE,
					fssc.getIIN(), uniqueId.toString(), null, null);

			if (td == null) {
				Log.info("Host Late Response", isoBuffer.logData());
				Log.info("Host Late Response Code",
						isoBuffer.get(Constants.DE39));
				fssc.setAlternateSourceArray(new String[0]);
				fssc.setSource(null);
				fssc.setDestination(null);
				return fssc.toString();
			}

			ThreadLocalUtil.setRRN(td.getThreadLocalId());
			Log.info("Host Response", isoBuffer.logData());
			Log.info("Host Response Code", isoBuffer.get(Constants.DE39));

			clientIsoBuffer = new IsoBuffer(td.getIsoBufferMap());

		} catch (Exception e) {
			Log.error("handleTransactionMessage get timer data error ", e);
			fssc.setAlternateSourceArray(new String[0]);
			fssc.setSource(null);
			fssc.setDestination(null);
			return fssc.toString();
			// return null;
		}
		ClientData cd = null;

		try {
			clientIsoBuffer.put(Constants.DE41, td.getTerminalId());
			clientIsoBuffer.put(Constants.DE42, td.getMerchantId());
			cd = getClientData(clientIsoBuffer, td);
			boolean isReversal = Constants.MSG_TYPE_RSP_REV_410
					.equals(isoBuffer.get(Constants.ISO_MSG_TYPE))
					|| Constants.MSG_TYPE_RSP_REV_430.equals(isoBuffer
							.get(Constants.ISO_MSG_TYPE))
					|| Constants.MSG_TYPE_RSP_REV_1430.equals(isoBuffer
							.get(Constants.ISO_MSG_TYPE));
			cd.setReversal(isReversal);
			cd.setOffline(Constants.MSG_TYPE_RSP_OFFLINE.equals(isoBuffer
					.get(Constants.ISO_MSG_TYPE)));
			isoBuffer.put(Constants.DE41, clientIsoBuffer.get(Constants.DE41));
			isoBuffer.put(Constants.DE37, clientIsoBuffer.get(Constants.DE37));
			isoBuffer.put(Constants.TPDU_ID, clientIsoBuffer.get(Constants.TPDU_ID));
			cd.setIsDCC(td.getIsDCC());
			// dcc auth lag
			if (!td.getQuotationId().equals(Constants.DISABLED_FIELD)
					&& td.getQuotationId() != null
					&& td.getDccStationName() != null
					&& (StaticStore.hostMessageProtocols.get(fssc.getIIN())
							.get(td.getDccStationName())).equals("27")) {
				return sendMonexAck(isoBuffer, fssc, cd, td, clientIsoBuffer);
			} else {
				modifyFields(isoBuffer, clientIsoBuffer, cd);
			}
		} catch (Exception e) {
			isoBuffer.put(Constants.DE39, Constants.ERR_SYSTEM_ERROR);
			Log.error("handleTransactionMessage process() ", e);
			fssc.setAlternateSourceArray(new String[0]);
			fssc.setSource(null);
			fssc.setDestination(null);
			return fssc.toString();
		}

		return toClient(fssc, clientIsoBuffer, isoBuffer, td, cd);
	}

	public String sendMonexAck(IsoBuffer isoBuffer, FssConnect fssc,
			ClientData cd, TimeoutData td, IsoBuffer clientIsoBuffer)
			throws JSONException, PosException, UnsupportedEncodingException {
		// TODO Auto-generated method stub

		IsoBuffer orgHostData = new IsoBuffer(isoBuffer);
		for (int i = 1; i <= 192; i++) {

			String k = ((i <= 64) ? IsoBuffer.PREFIX_PRIMARY
					: (i > 64 && i <= 128) ? IsoBuffer.PREFIX_SECONDARY
							: (i > 128 && i <= 192) ? IsoBuffer.PREFIX_TERTIARY
									: "")
					+ i;
			if (orgHostData.hasSubFields(k)) {
				if (orgHostData.getBuffer(k).getSecuredMap().size() >= 0) {
					orgHostData.disableField(k);
				}

			}
		}
		orgHostData.put(Constants.DE62, cd.getInvoice());
		List<Object> params = new ArrayList<Object>();
		params.add(td.getQuotationId());
		ArrayList<Class<?>> classList = new ArrayList<Class<?>>();
		classList.add(ResponseStatus.class);
		classList.add(DccParams.class);
		try {
			List<Object> objList = apiFactory.getStoredProcedureApi().getBean(
					params, StoredProcedureInfo.LOG_DCC_DETAILS, classList,
					fssc.getIIN());

			@SuppressWarnings("unchecked")
			String rspStatus = ((List<ResponseStatus>) objList.get(0)).get(0)
					.getStatus();
			if (Constants.SUCCESS.equals(rspStatus)) {

				@SuppressWarnings("unchecked")
				List<DccParams> listDccCards = (List<DccParams>) objList.get(1);
				new ArrayList<Object>();
				for (DccParams dccParams : listDccCards) {
					String OCAV = dccParams.getOrgCardTxnAmount();
					String OCHV = dccParams.getOrgTxnAmount();
					String CCAV = dccParams.getOrgCardTxnAmount();
					String CCHV = dccParams.getOrgTxnAmount();
					String CAC = dccParams.getCurrencyCode();
					String CHC = dccParams.getCurrencyCodeN();
					String REX = dccParams.getExchangeRate();
					String QUOT = dccParams.getQuotationId();
					String AuthCode = isoBuffer.get(Constants.DE38).equals(
							Constants.DISABLED_FIELD) ? "      " : isoBuffer
							.get(Constants.DE38);
					String cardHolderDccAcc = td.getIsDCC().equals("1") ? "Y"
							: "N";

					if ((clientIsoBuffer.get(Constants.DE3).substring(0, 2) + clientIsoBuffer
							.get(Constants.DE25)).equals("2200")) {
						isoBuffer.put(Constants.DE63, "RA99=" + QUOT + ";");
						isoBuffer.disableField(Constants.DE39);
						isoBuffer.put(Constants.ISO_MSG_TYPE,
								Constants.MSG_TYPE_REV_400);
						isoBuffer.disableField(Constants.DE2);
						cd.setReversal(false);
					} else {
						isoBuffer.put(Constants.DE63, "CA11=" + OCAV + ";12="
								+ OCHV + ";13=" + CCAV + ";14=" + CCHV + ";15="
								+ REX + ";16=" + CAC + ";17=" + CHC + ";18="
								+ cardHolderDccAcc + ";19=" + AuthCode + ";99="
								+ QUOT + ";");

						isoBuffer.put(Constants.DE39,
								cd.isReversal() ? Constants.MONEX_REVERSAL
										: isoBuffer.get(Constants.DE39));
						isoBuffer.put(Constants.ISO_MSG_TYPE,
								Constants.COMPLETION_MSG_REQUEST);
					}
					isoBuffer.put(Constants.DE48, dccParams.getAcqInstId());
					isoBuffer.put(Constants.DE43, dccParams.getCurrencyCode());
					isoBuffer.put(Constants.DE37, dccParams.getDccAckRRN());
				}

			} else {
				isoBuffer.put(Constants.DE39, rspStatus);
				return toClient(fssc, clientIsoBuffer, isoBuffer, td, cd);
			}
		} catch (Exception e) {
			String source = fssc.getSource();
			isoBuffer.put(Constants.DE39, Constants.ERR_SYSTEM_ERROR);
			Log.error("handleTransactionMessage process() ", e);
			fssc.setAlternateSourceArray(new String[0]);
			fssc.setSource(fssc.getDestination());
			fssc.setDestination(source);
			return fssc.toString();
		}

		isoBuffer.put(Constants.DE25, "96");
		String msgProc = ((StaticStore.hostMessageProtocols.get(fssc.getIIN())
				.get(td.getDccStationName())) == null) ? (StaticStore.alterStationDetails
				.get(fssc.getIIN()).get(td.getDccStationName()))
				: (StaticStore.hostMessageProtocols.get(fssc.getIIN()).get(td
						.getDccStationName()));

		isoBuffer.put(Constants.DE42, td.getMerchantId());

		orgHostData.put(Constants.DE22, cd.getEnrtyMode());

		Log.trace("Api | " + HOSTMAP.get(Constants.MONEX));

		String hostRequest = apiFactory.getHostRequestApi(msgProc)
				.modiyAndbuild(isoBuffer, isoBuffer, cd);

		// Update Timer Data
		TimeoutData td1 = new TimeoutData();
		td1.setThreadLocalId(ThreadLocalUtil.getRRN());
		td1.setIsoBufferMap(new IsoBuffer(orgHostData).getMap()); // Host
																	// Response
		// td1.setThreadLocalId(orginalRRN);
		td1.setIin(fssc.getIIN());
		td1.setHostStationName(td.getHostStationName());
		td1.setClientStationName(td.getClientStationName());
		td1.setClientClass(td.getClientClass());
		td1.setMyStation(td.getMyStation());
		td1.setTranId(td.getTranId());
		td1.setMerchantExponent(td.getMerchantExponent());
		td1.setTerminalId(isoBuffer.get(Constants.DE41));
		td1.setMerchantId(isoBuffer.get(Constants.DE42));
		td1.setUti(clientIsoBuffer.get(Constants.DE25));
		td1.setZmkCheckSum(clientIsoBuffer.get(Constants.DE3));
		td1.setTxnCommunicationFlow(td.getTxnCommunicationFlow());
		td1.setIsDCC("1");

		String fsscUniqueId = isoBuffer.get(Constants.DE37)
				+ isoBuffer.get(Constants.DE11) + isoBuffer.get(Constants.DE41);

		updateTimer(fsscUniqueId, fssc.getIIN(), td1, isoBuffer,
				isoBuffer.get(Constants.DE41), td.getTranId());

		fssc.setMessage(hostRequest);
		fssc.setSource(fssc.getDestination());
		fssc.setDestination(td.getDccStationName());
		fssc.setAlternateDestinationArray(new String[] { td
				.getDccAlternateStationName() == null ? "" : td
				.getDccAlternateStationName() });

		return fssc.toString();

	}

	private void modifyFields(IsoBuffer isoBuffer, IsoBuffer clientIsoBuffer,
			Data data) throws PosException {
		long start = System.currentTimeMillis();
		Log.debug("Modifying Fields for ", this.toString());
		if (((AbstractBeanData) data).isKeyImport()) {
			invokeMethod(Transactions.KEY_IMPORT.toString(), isoBuffer, data);
			return;
		}
		if (((AbstractBeanData) data).isReversal()) {
			invokeMethod(Transactions.REVERSAL_DEFAULT.toString(), isoBuffer,
					data);
			return;
		}
		if (clientIsoBuffer.get(Constants.DE3).equals(CUT_OVER)) {
			invokeMethod(Transactions.CUT_OVER.toString(), isoBuffer, data);
			return;
		}
		if (clientIsoBuffer.get(Constants.DE3).equals(RECON_MSG)) {
			invokeMethod(Transactions.RECON.toString(), isoBuffer, data);
			return;
		}
		if (Transactions.BALANCE_INQUIRY.toString().equals(
				clientIsoBuffer.get(Constants.DE3))) {
			invokeMethod(Transactions.BALANCE_INQUIRY.toString(), isoBuffer,
					data);
			return;
		}
		// For Monex and DCC
		if (!clientIsoBuffer.get(Constants.DE25).equals("*")
				&& (clientIsoBuffer.get(Constants.DE25).substring(0, 2)
						.toString().equals(DCC_ENQUIRY) || clientIsoBuffer
						.get(Constants.DE25).substring(0, 2).toString()
						.equals(DCC_PREAUTH_COMPLETION))) {
			invokeMethod(Transactions.DCC_ENQUIRY.toString(), isoBuffer, data);
			return;
		}
		if (!clientIsoBuffer.get(Constants.DE25).equals("*")
				&& clientIsoBuffer.get(Constants.DE25).substring(0, 2)
						.equals(DCC_COMPLETION)) {
			invokeMethod(Transactions.DCC_COMPLETION.toString(), isoBuffer,
					data);
			return;
		}

		// isoBuffer.disableField(Constants.DE55);
		invokeMethod(Transactions.DEFAULT.toString(), isoBuffer, data);
		Log.debug("calling part for method", clientIsoBuffer.get(Constants.DE3)
				.substring(0, 2) + clientIsoBuffer.get(Constants.DE25));
		invokeMethod(clientIsoBuffer.get(Constants.DE3).substring(0, 2)
				+ clientIsoBuffer.get(Constants.DE25), isoBuffer, data);

		if (!clientIsoBuffer.get(Constants.DE22).equals("*")
				&& Validator.isChipCardTxn(clientIsoBuffer)) {
			invokeMethod(Transactions.EMV_DEFAULT.toString(), isoBuffer, data);
		}

		if (((AbstractBeanData) data).isOffline()) {
			invokeMethod(Transactions.OFFLINE_DEFAULT.toString(), isoBuffer,
					data);
		}
		Log.debug("Time taken for Method handle",
				String.valueOf((System.currentTimeMillis() - start)));
	}

	private void invokeMethod(String theKey, IsoBuffer isoBuffer, Data data)
			throws PosException {
		try {
			if (methodHandles.containsKey(theKey)) {
				Log.debug("Method invocation for ", theKey);
				methodHandles.get(theKey).invokeExact(this, isoBuffer, data);
			} else {
				Log.debug("No Method available", theKey);
			}
		} catch (StringIndexOutOfBoundsException e) {
			throw new PosException(Constants.ERR_IN_PARSING);
		} catch (Throwable e) {
			Log.error("AbstractHostApi Invoke", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
	}

	private String toClient(FssConnect fssc, IsoBuffer clientIsoBuffer,
			IsoBuffer isoBuffer, TimeoutData td, ClientData clientData)
			throws UnsupportedEncodingException, JSONException {

		try {

			if (isoBuffer.isFieldEmpty(Constants.DE39)) {
				isoBuffer.put(Constants.DE39,
						Constants.ERR_NO_RSP_CODE_FROM_HOST);
			}
			String rspDesc = updateTxnLog(clientIsoBuffer, isoBuffer, td);

			boolean isVoidAsReversal = Constants.PROC_CODE_VOID
					.equals(clientData.getProcCode().substring(0, 2))
					&& clientData.isReversal();

			if (isVoidAsReversal) {

				isoBuffer.put(Constants.ISO_MSG_TYPE,
						Constants.MSG_TYPE_RSP_TXN);
				isoBuffer.put(Constants.DE3, clientData.getProcCode());
			}

			/*if (Constants.SUCCESS.equals(isoBuffer.get(Constants.DE39))
					&& clientData.isReversal() && !isVoidAsReversal) {
				try {
					transactionLogger.updateReversalLog(
							clientIsoBuffer.get(Constants.DE41),
							clientIsoBuffer.get(Constants.DE11),
							clientIsoBuffer.get(Constants.DE42),
							clientIsoBuffer.get(Constants.DE3).substring(0, 2)
									+ isoBuffer.get(Constants.DE25),
							clientIsoBuffer.get(Constants.DE62), td.getIin());
				} catch (PosException e) {
					isoBuffer.put(Constants.DE39, e.getMessage());
					fssc.setAlternateSourceArray(new String[0]);
					fssc.setSource(null);
					fssc.setDestination(null);
					return fssc.toString();
				}
			}*/

			ClientApi clientApi = apiFactory.getClientApi(td.getClientClass());
			clientApi.modifyBits4Response(isoBuffer, clientData);
			if (!Constants.SUCCESS.equals(isoBuffer.get(Constants.DE39))
					&& clientApi.errorDescriptionRequired()) {
				rspDesc = rspDesc == null ? Validator
						.getErrorDescription(isoBuffer.get(Constants.DE39))
						: rspDesc;
				isoBuffer.put(Constants.DE63, rspDesc);
			}
			Log.info("Client response", isoBuffer.logData());
		} catch (PosException e) {
			isoBuffer.put(Constants.DE39, e.getMessage());
			fssc.setAlternateSourceArray(new String[0]);
			fssc.setSource(null);
			fssc.setDestination(null);
			return fssc.toString();
		}
		fssc.setSource(fssc.getDestination());
		fssc.setDestination(td.getClientStationName());
		fssc.setAlternateDestinationArray(new String[0]);

		String clientResp = apiFactory.getClientApi(td.getClientClass()).build(
				isoBuffer);
		if (td.getPlainKey() != null) {
			Log.debug("Plain Key", IsoUtil.asciiChar2hex(td.getPlainKey()));
			String finalMsg = new String(Base64.encodeBase64(clientResp
					.getBytes(StandardCharsets.ISO_8859_1)),
					StandardCharsets.ISO_8859_1);
			Log.debug("Encoded Msg:: Resp", IsoUtil.asciiChar2hex(finalMsg));
			String plainKey = new String(Base64.decodeBase64(td.getPlainKey()
					.getBytes(StandardCharsets.ISO_8859_1)),
					StandardCharsets.ISO_8859_1);

			String encyMsg = CipherUtil.enCryptString(finalMsg, plainKey);
			Log.debug("Encryted Msg ",
					encyMsg + "\n" + IsoUtil.asciiChar2hex(encyMsg));
			StringBuilder sBuffer = new StringBuilder(IsoUtil.pad(
					String.valueOf(IsoUtil.toGraphical(encyMsg.length(), 2)),
					' ', 2, true));
			sBuffer.append(encyMsg);
			Log.debug("Final Msg to Client", sBuffer.toString());
			fssc.setMessage(sBuffer.toString());
			Log.debug("Fss Connect Msg in Rsp",
					IsoUtil.asciiChar2hex(fssc.getMessage()));
		} else {
			fssc.setMessage(clientResp);
		}

		return fssc.toString();
	}

	private String updateTxnLog(IsoBuffer clientIsoBuffer, IsoBuffer isoBuffer,
			TimeoutData td) throws PosException {
		int status;
		if ((Constants.MSG_TYPE_RSP_REV_430.equals(isoBuffer
				.get(Constants.ISO_MSG_TYPE)) || Constants.MSG_TYPE_RSP_REV_1430
				.equals(isoBuffer.get(Constants.ISO_MSG_TYPE)))
				|| Constants.MSG_TYPE_RSP_REV_410.equals(isoBuffer
						.get(Constants.ISO_MSG_TYPE))) {
			status = Constants.TXNLOG_STATUS_REVERSED;
		} else {
			status = (Constants.SUCCESS.equals(isoBuffer.get(Constants.DE39))
					|| Constants.SUCCESS_93.equals(isoBuffer
							.get(Constants.DE39)) || Constants.SUCCESS_93_400
					.equals(isoBuffer.get(Constants.DE39))) ? Constants.TXNLOG_STATUS_SUCCESS
					: Constants.TXNLOG_STATUS_FAILED;
		}
		ClientApi clientApi = apiFactory.getClientApi(td.getClientClass());
		String procedurename = clientApi.checkHostToHost() ? StoredProcedureInfo.TXN_RESPONSE_UPDATE_AGGISO8583
				: StoredProcedureInfo.TXN_RESPONSE_UPDATE;
		TransactionResponse txnRsp = transactionLogger.updateTxnLog(
				clientIsoBuffer.get(Constants.DE2, null), clientIsoBuffer
						.get(Constants.DE41), td.getTranId(), td
						.getHostStationName(), isoBuffer.get(Constants.DE39),
				isoBuffer.isFieldEmpty(Constants.DE38) ? Constants.ZERO
						: isoBuffer.get(Constants.DE38), status,
				clientIsoBuffer.get(Constants.DE42),
				clientIsoBuffer.get(Constants.DE3).substring(0, 2)
						+ clientIsoBuffer.get(Constants.DE25), td.getIin(),
				isoBuffer.get(Constants.DE11), isoBuffer.get(Constants.DE7,
						null), procedurename);
		Log.debug("Mapped response code from procedure ",
				txnRsp.getResponseCode());
		isoBuffer.put(Constants.DE39, txnRsp.getResponseCode());
		// Log.debug("transaction descrptn", txnRsp.getResponseDescription());
		return txnRsp.getResponseDescription();
	}

	private ClientData getClientData(IsoBuffer isoBuffer, TimeoutData td) {
		ClientData cd = new ClientData();
		cd.setAmount(isoBuffer.get(Constants.DE4));
		cd.setProcCode(isoBuffer.get(Constants.DE3));
		cd.setRrn(isoBuffer.get(Constants.DE37)
				.equals(Constants.DISABLED_FIELD) ? Constants.DISABLED_FIELD
				: isoBuffer.get(Constants.DE37));
		cd.setStan(isoBuffer.get(Constants.DE11));
		cd.setTerminalId(isoBuffer.get(Constants.DE41));
		cd.setInvoice((isoBuffer.get(Constants.DE62).equals(Constants.DISABLE) || isoBuffer
				.get(Constants.DE62).equals(Constants.DISABLED_FIELD)) ? null
				: isoBuffer.get(Constants.DE62));
		cd.setEnrtyMode(isoBuffer.get(Constants.DE22).equals(Constants.DISABLE) ? Constants.DISABLED_FIELD
				: isoBuffer.get(Constants.DE22));
		if (td.getUti() != null) {
			cd.setUti(td.getUti());
		}
		cd.setDate(isoBuffer.get(Constants.DE13));
		cd.setTime(isoBuffer.get(Constants.DE12));
		if (td.getTxnCommunicationFlow() != null) {
			cd.setTxnCommunicationFlow(td.getTxnCommunicationFlow());
		}
		// For DCC
		if (isoBuffer.get(Constants.DE25).equals(Constants.DCC_ENQUIRY)) {
			cd.setMerchantExponent(td.getMerchantExponent());
		}
		return cd;
	}

	protected void updateTimer(String fsscUniqueId, String mspAcr,
			TimeoutData td, IsoBuffer hostBuffer, String tid, String tranId)
			throws PosException {
		try {

			Log.debug("Inside the Method", td.getIsoBufferMap().toString());

			 
			StringBuilder timerJson=new StringBuilder();
			 timerJson.append( objectMapper.writeValueAsString(td));


			Log.debug("After converting into json", timerJson.toString());

			txnLogService.updateHostLog(tid, hostBuffer,
					StoredProcedureInfo.LOG_HOST_REQUEST, mspAcr, fsscUniqueId,
					timerJson.toString().toString(), tranId);
		} catch (Exception e) {
			Log.error("Error in updateTimer", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
	}

	@Override
	public String modiyAndbuild(IsoBuffer isoBuffer, IsoBuffer clientIsoBuffer,
			Data data) throws PosException {
		modifyFields(isoBuffer, clientIsoBuffer, data);
		if (!this.toString().contains("Response")) {
			Log.info("Host Request", isoBuffer.logData());
			return build(isoBuffer);
		}
		return "";
	}

	protected abstract String build(IsoBuffer isoBuffer);

	private String type;
	private String factoryPrefix;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFactoryPrefix() {
		return factoryPrefix;
	}

	public void setFactoryPrefix(String factoryPrefix) {
		this.factoryPrefix = factoryPrefix;
	}

	public Map<String, MethodHandle> getMethodHandles() {
		return methodHandles;
	}

}
