package com.fss.pos.base.messagehandlers;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fss.commons.utils.CipherUtil;
import com.fss.pos.base.api.client.ClientApi;
import com.fss.pos.base.api.client.ClientData;
import com.fss.pos.base.api.client.Validator;
import com.fss.pos.base.api.db.storedprocedure.ResponseStatus;
import com.fss.pos.base.api.db.storedprocedure.StoredProcedureInfo;
import com.fss.pos.base.api.host.TimeoutData;
import com.fss.pos.base.commons.Config;
import com.fss.pos.base.commons.DccParams;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.StaticStore;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.base.commons.utils.ThreadLocalUtil;
import com.fss.pos.base.factory.ApiFactory;
import com.fss.pos.base.services.fssconnect.FssConnect;
import com.fss.pos.base.services.transactionlog.TransactionLogService;

public abstract class AbstractMessageHandler implements MessageHandler {

	
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
		hostApi.put(Constants.DINERS, "Diners");
		HOSTMAP = Collections.unmodifiableMap(hostApi);
	}
	
	private static ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private Config config;

	@Autowired
	private ApiFactory apiFactory;

	@Autowired
	private TransactionLogService txnLogService;

	protected void modifyB4RespondingClient(IsoBuffer isoBuffer,
			FssConnect fssc, ClientApi clientApi) throws JSONException {
		return;
	}

	protected String handleMessage(IsoBuffer isoBuffer, FssConnect fssConnect,
			ClientApi clientApi) throws Exception {
		return null;
	}

	@Override
	public String handle(IsoBuffer isoBuffer, FssConnect fssConnect,
			ClientApi clientApi) throws Exception {
		try {
			if (!(isoBuffer.get(Constants.DE25).equals(Constants.DCC_ENQUIRY))
					&& !(isoBuffer.get(Constants.DE25)
							.equals(Constants.DCC_REFUND_ENQUIRY))
					&& !(isoBuffer.get(Constants.DE25)
							.equals(Constants.DCC_PREAUTH_COMPLETION)))
				Validator.validate(isoBuffer);
			return handleMessage(isoBuffer, fssConnect, clientApi);
		} catch (PosException e) {
			String s = e.getMessage();
			if(s.contains("-")){
				 String arr[] = s.split("-");
				 isoBuffer.put(Constants.DE39, arr[0]);
				 isoBuffer.put(Constants.DE37, arr[1]);
			}else{
				isoBuffer.put(Constants.DE39, e.getMessage());
			}	
			return respondClient(isoBuffer, fssConnect, clientApi);
		} catch (Exception e) {
			Log.error("AbstractMessageHandler handle", e);
			isoBuffer.put(Constants.DE39, Constants.ERR_SYSTEM_ERROR);
			return respondClient(isoBuffer, fssConnect, clientApi);
		}
	}

	@Override
	public String respondClient(IsoBuffer isoBuffer, FssConnect fssc,
			ClientApi clientApi) throws JSONException,
			UnsupportedEncodingException {

		IsoBuffer clinetIsoBuffer = new IsoBuffer(isoBuffer);
		if (isoBuffer.isFieldEmpty(Constants.DE39))
			isoBuffer.put(Constants.DE39, Constants.ERR_SYSTEM_ERROR);

		boolean isDCC = (!isoBuffer.get(Constants.DE63).isEmpty()
				&& !isoBuffer.get(Constants.DE63).equals(Constants.DISABLED_FIELD)
				&& (isoBuffer.get(Constants.DE39).equals(Constants.ERR_OFFLINE_APPROVED)
						|| (isoBuffer.get(Constants.DE39).equals("72")
								&& isoBuffer.get(Constants.DE25).substring(0, 2).toString().equals("06")))) ? true
										: false;

		if (isDCC) {

			if (Constants.ERR_OFFLINE_APPROVED.equals(isoBuffer.get(Constants.DE39))) {
				isoBuffer.put(Constants.DE39, Constants.SUCCESS);
				isoBuffer.disableField(Constants.DE55);
			} else {
				isoBuffer.put(Constants.DE39, Constants.MONEX_REVERSAL);
				isoBuffer.disableField(Constants.DE55);
			}

			// TODO Auto-generated method stub
			try {
				ClientData cd = new ClientData();
				cd.setIsDCC((!isoBuffer.isFieldEmpty(Constants.DE6) && !isoBuffer.isFieldEmpty(Constants.DE51)) ? "1"
						: "0");
				String dccStationname = null;
				String dccAlternateStationname = null;
				boolean isVoid = (isoBuffer.get(Constants.DE3).substring(0, 2) + isoBuffer.get(Constants.DE25))
						.equals("2200") ? true : false;
				IsoBuffer orgHostData = new IsoBuffer(isoBuffer);
				List<Object> params = new ArrayList<Object>();
				params.add(isoBuffer.get(Constants.DE63));
				ArrayList<Class<?>> classList = new ArrayList<Class<?>>();
				classList.add(ResponseStatus.class);
				classList.add(DccParams.class);
				try {
					List<Object> objList = apiFactory.getStoredProcedureApi().getBean(params,
							StoredProcedureInfo.LOG_DCC_DETAILS, classList, fssc.getIIN());

					String rspStatus = ((List<ResponseStatus>) objList.get(0)).get(0).getStatus();
					if (Constants.SUCCESS.equals(rspStatus)) {

						List<DccParams> listDccCards = (List<DccParams>) objList.get(1);
						List<Object> listObj = new ArrayList<Object>();
						for (DccParams dccParams : listDccCards) {
							String OCAV = dccParams.getOrgCardTxnAmount();
							String OCHV = dccParams.getOrgTxnAmount();
							String CCAV = dccParams.getOrgCardTxnAmount();
							String CCHV = dccParams.getOrgTxnAmount();
							String CAC = dccParams.getCurrencyCode();
							String CHC = dccParams.getCurrencyCodeN();
							String REX = dccParams.getExchangeRate();
							String QUOT = dccParams.getQuotationId();
							String AuthCode = isoBuffer.get(Constants.DE38);
							String cardHolderDccAcc = cd.getIsDCC().equals("1") ? "Y" : "N";
							dccStationname = dccParams.getDccStationName();
							dccAlternateStationname = dccParams.getDccAlternateStationName();
							isoBuffer
									.setTranId(isoBuffer.getTranId() != null ? isoBuffer
											.getTranId() : dccParams
											.getTranId());

							if (isVoid) {
								isoBuffer.put(Constants.DE63, "RA99=" + QUOT + ";");
								isoBuffer.disableField(Constants.DE39);
								isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_REV_400);
								isoBuffer.disableField(Constants.DE2);
								cd.setReversal(false);
							} else {
								isoBuffer.put(Constants.DE63,
										"CA11=" + OCAV + ";12=" + OCHV + ";13=" + CCAV + ";14=" + CCHV + ";15=" + REX
												+ ";16=" + CAC + ";17=" + CHC + ";18=" + cardHolderDccAcc + ";19="
												+ AuthCode + ";99=" + QUOT + ";");
								isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.COMPLETION_MSG_REQUEST);
							}

							isoBuffer.put(Constants.DE48, dccParams.getAcqInstId());
							isoBuffer.put(Constants.DE43, dccParams.getCurrencyCode());
							isoBuffer.put(Constants.DE37, dccParams.getDccAckRRN());
						}

					} else {
						isoBuffer.put(Constants.DE39, rspStatus);
					}
				} catch (Exception e) {
					isoBuffer.put(Constants.DE39, Constants.ERR_SYSTEM_ERROR);
				}

				isoBuffer.put(Constants.DE25, "96");
				StringBuilder msgProc=new StringBuilder();
				msgProc.append((StaticStore.hostMessageProtocols.get(fssc.getIIN()).get(dccStationname) == null)
						? (StaticStore.alterStationDetails.get(fssc.getIIN()).get(dccStationname))
						: (StaticStore.hostMessageProtocols.get(fssc.getIIN()).get(dccStationname)));

				Log.trace("Api | " + HOSTMAP.get(Constants.MONEX));

				String hostRequest = apiFactory.getHostRequestApi(msgProc.toString()).modiyAndbuild(isoBuffer, isoBuffer, cd);

				// Update Timer Data
				TimeoutData td1 = new TimeoutData();
				td1.setThreadLocalId(ThreadLocalUtil.getRRN());
				td1.setIsoBufferMap(new IsoBuffer(clinetIsoBuffer).getMap()); // Host
				// Response
				td1.setIin(fssc.getIIN());
				td1.setThreadLocalId(ThreadLocalUtil.getRRN());
				td1.setHostStationName(dccStationname);
				td1.setClientStationName(fssc.getSource());
				td1.setClientClass(clientApi.getClass().getCanonicalName());
				td1.setMyStation(fssc.getDestination());
				td1.setTranId(isoBuffer.getTranId());
				td1.setMerchantExponent(null);
				td1.setTerminalId(isoBuffer.get(Constants.DE41));
				td1.setMerchantId(isoBuffer.get(Constants.DE42));
				td1.setUti(orgHostData.get(Constants.DE25));
				td1.setZmkCheckSum(orgHostData.get(Constants.DE3));
				td1.setTxnCommunicationFlow(null);
				td1.setIsDCC("1");

				StringBuilder fsscUniqueId=new StringBuilder();
				fsscUniqueId.append(isoBuffer.get(Constants.DE37) + isoBuffer.get(Constants.DE11)
						+ isoBuffer.get(Constants.DE41));

				updateTimer(fsscUniqueId.toString(), fssc.getIIN(), td1, isoBuffer, isoBuffer.get(Constants.DE41),
						isoBuffer.getTranId());

				fssc.setMessage(hostRequest);
				fssc.setSource(dccStationname);
				fssc.setAlternateDestinationArray(
						new String[] { dccAlternateStationname == null ? "" : dccAlternateStationname });
			} catch (PosException e) {
				isoBuffer.put(Constants.DE39, Constants.ERR_SYSTEM_ERROR);
				modifyB4RespondingClient(isoBuffer, fssc, clientApi);
			}
		} else {
			modifyB4RespondingClient(isoBuffer, fssc, clientApi);
		}

		// String clientRspCode;
		if (!clientApi.errorDescriptionRequired()) {
			Log.debug("inside respond client", Boolean.toString(clientApi.errorDescriptionRequired()));
			/*
			 * isoBuffer.put(Constants.DE63,
			 * Validator.getErrorDescription(clientRspCode));
			 */
			if ((isoBuffer.get(Constants.DE25).equals(Constants.DCC_ENQUIRY))
					|| (isoBuffer.get(Constants.DE25).equals(Constants.DCC_PREAUTH_COMPLETION))
					|| (isoBuffer.get(Constants.DE25).equals(Constants.DCC_REFUND_ENQUIRY))) {
				isoBuffer.disableField(Constants.DE64);
			} else {
				isoBuffer.disableField(Constants.DE63);
			}
		}

		if (isDCC && !isoBuffer.get(Constants.DE39).equals(Constants.SUCCESS)
				&& !isoBuffer.get(Constants.DE39).equals(Constants.DISABLED_FIELD))
			Log.info("Client Response", isoBuffer.logData());
		else if (!isDCC)
			Log.info("Client Response", isoBuffer.logData());

		String destReceived = fssc.getDestination();
		String srcReceived = fssc.getSource();
		fssc.setDestination(srcReceived);
		fssc.setSource(destReceived);

		if (fssc.getPlainPrivateKey() != null) {
			String val = clientApi.build(isoBuffer);
			String finalMsg = new String(Base64.encodeBase64(val.getBytes(StandardCharsets.ISO_8859_1)),
					StandardCharsets.ISO_8859_1);
			Log.debug("Encoded Msg ", finalMsg + "\n" + IsoUtil.asciiChar2hex(finalMsg));
			String plainKey = new String(
					Base64.decodeBase64(fssc.getPlainPrivateKey().getBytes(StandardCharsets.ISO_8859_1)),
					StandardCharsets.ISO_8859_1);
			Log.debug("Plain Key while encryt:::", IsoUtil.asciiChar2hex(plainKey));
			String encyMsg = CipherUtil.enCryptString(finalMsg, plainKey);
			Log.debug("Encryted Msg ", encyMsg + "\n" + IsoUtil.asciiChar2hex(encyMsg));
			StringBuilder sBuffer = new StringBuilder(
					IsoUtil.pad(String.valueOf(IsoUtil.toGraphical(encyMsg.length(), 2)), ' ', 2, true));
			sBuffer.append(encyMsg);
			Log.debug("Final Msg to Client", sBuffer.toString());
			fssc.setMessage(sBuffer.toString());
		} else if (!isDCC)
			fssc.setMessage(clientApi.build(isoBuffer));

		fssc.setAlternateSourceArray(new String[0]);
		return fssc.toString();

	}

	/*protected void fillTrack2FromPan(IsoBuffer isoBuffer) {
		if ((!isoBuffer.isFieldEmpty((Constants.DE35)))
				|| isoBuffer.isFieldEmpty(Constants.DE2))
			return;
		StringBuilder track2 = new StringBuilder();
		track2.append(isoBuffer.get(Constants.DE2));
		track2.append("=");
		track2.append(isoBuffer.isFieldEmpty(Constants.DE14) ? "0000"
				: isoBuffer.get(Constants.DE14));
		track2.append("000000");
		track2.append("?");
		isoBuffer.put(Constants.DE35, track2.toString());
	}

	protected void fillPanFromTrack2(IsoBuffer isoBuffer) {
		if (!isoBuffer.isFieldEmpty(Constants.DE2)
				|| ( isoBuffer.isFieldEmpty(Constants.DE35)))
			return;
		String track2 = isoBuffer.get(Constants.DE35);
		if ("F".equals(track2.substring(track2.length() - 1, track2.length()))) {
			track2 = track2.substring(0, track2.length() - 1);
		}
		track2 = track2.replace("D", "=");
		isoBuffer.put(Constants.DE35, track2);
		String[] arr = track2.split("=");
		isoBuffer.put(Constants.DE2, arr[0]);
		isoBuffer.put(Constants.DE14, arr[1].substring(0, 4));
	}*/
	
	protected void fillTrack2FromPan(IsoBuffer isoBuffer) {
		if (!isoBuffer.isFieldEmpty("P-35") || 
			isoBuffer.isFieldEmpty("P-2"))
			return; 
		StringBuilder track2 = new StringBuilder();
		track2.append(isoBuffer.get("P-2"));
		track2.append("=");
		track2.append(isoBuffer.isFieldEmpty("P-14") ? "0000" : 
				isoBuffer.get("P-14"));
		track2.append("000000");
		track2.append("?");
		isoBuffer.put("P-35", track2.toString());
	}
	
	protected void fillPanFromTrack2(IsoBuffer isoBuffer) {
		//Log.debug("fill from track2 method ", isoBuffer.toString());
		if (!isoBuffer.isFieldEmpty("P-2") || 
				isoBuffer.isFieldEmpty("P-35"))
				return; 
			StringBuilder track2 = new StringBuilder();
			track2.append(isoBuffer.get("P-35"));
			if ("F".equals(track2.substring(track2.length() - 1, track2.length())))
				track2.append(track2.substring(0, track2.length() - 1)); 
			track2.append(track2.toString().replace("D", "="));
			isoBuffer.put("P-35", track2.toString());
			String[] arr = track2.toString().split("=");
			isoBuffer.put("P-2", arr[0]);
			isoBuffer.put("P-14", arr[1].substring(0, 4));
	}

	protected void updateTimer(String fsscUniqueId, String mspAcr,
			TimeoutData td, IsoBuffer hostBuffer, String tid, String tranId)
			throws PosException {
		try {
			StringBuilder timerJson=new StringBuilder();
			timerJson.append(objectMapper.writeValueAsString(td));
			txnLogService.updateHostLog(tid, hostBuffer,
					StoredProcedureInfo.LOG_HOST_REQUEST, mspAcr, fsscUniqueId,
					timerJson.toString(), tranId);
		} catch (Exception e) {
			Log.error("Error in updateTimer", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
	}

}
