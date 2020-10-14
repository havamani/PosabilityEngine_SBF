package com.fss.pos.scheduler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fss.pos.base.api.host.TimeoutData;
import com.fss.pos.base.commons.Config;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.StaticStore;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.ThreadLocalUtil;
import com.fss.pos.base.factory.ApiFactory;
import com.fss.pos.base.services.fssconnect.FssConnect;
import com.fss.pos.base.services.fssconnect.FssConnectService;

@Scope("prototype")
@Component("reversalTask")
public class ReversalTask implements Runnable {

	private static final String PROCEDURE_REVERSAL_INIT = "PSP_GETTRANSACTIONTIMERDATA";

	private static ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private ApiFactory apiFactory;

	@Autowired
	private Config config;

	@Autowired
	private FssConnectService fssConnectService;

	private String msp;

	public ReversalTask(String msp) {
		this.msp = msp;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		List<Class<?>> classList = new ArrayList<Class<?>>();
		classList.add(TimedOutTxnData.class);
		try {
			List<Object> objList = apiFactory.getStoredProcedureApi().getBean(
					new ArrayList<Object>(), PROCEDURE_REVERSAL_INIT, classList, msp);
			List<TimedOutTxnData> tdList = (List<TimedOutTxnData>) objList
					.get(0);
			for (TimedOutTxnData txndata : tdList) {
				TimeoutData td = objectMapper.readValue(
						txndata.getTransactionTimerData(), TimeoutData.class);
				Log.trace("Intiated Reverasal :::::::::::::" + td.getTranId()
						+ " MSP : " + msp);
				ThreadLocalUtil.setRRN(td.getThreadLocalId());
				ThreadLocalUtil.setMsp(td.getIin());
				IsoBuffer isoBuffer = new IsoBuffer(td.getIsoBufferMap());
				if (Constants.FLAG_YES
						.equals(config.getEnableTimeoutReversal())) {
					
						isoBuffer.disableField(Constants.DE37);
						isoBuffer.disableField(Constants.DE35);
						isoBuffer.disableField(Constants.DE52);
						isoBuffer.put(Constants.DE63,
								(isoBuffer.get(Constants.DE63).isEmpty()
										&& isoBuffer.get(Constants.DE63).equals(Constants.DISABLED_FIELD)) ? "00"
												: isoBuffer.get(Constants.DE63));
						
						isoBuffer.put(Constants.ISO_MSG_TYPE,
								Constants.MSG_TYPE_REV_400);
						sendSelfReversal(
								apiFactory.getClientApi(td.getClientClass())
										.build(isoBuffer), td);
					
				}
			}
		} catch (Exception e) {
			Log.error("ReversalTask", e);
		}
	}

	private void sendSelfReversal(String message, final TimeoutData td)
			throws Exception {
		FssConnect fssc = new FssConnect(StandardCharsets.ISO_8859_1);
		String fsscSource = StaticStore.fsscSrcDetails.get(msp).get(msp);
		String fsscUrl = StaticStore.fsscUrlDetails.get(msp).get(msp);
		// String fsscUrl="http://10.44.59.141:5045/";
		fssc.setSource(fsscSource);
		// fssc.setSource("POS_MBLWR=NODE_ONE=HTTP_SERVER1");
		fssc.setDestination(td.getMyStation());
		fssc.setAlternateSourceArray(new String[0]);
		fssc.setResponseRequired(FssConnect.RESPONSE_REQUIRED);
		fssc.setIIN(td.getIin());
		fssc.setMessage(message);
		fssc.setUniqueId(Constants.EMPTY_STRING);
		fssc.setTimeStamp(Constants.EMPTY_STRING);
		fssc.setNotifyDestArray(new String[0]);
		fssc.setMsgId("1");
		fssc.setAlternateDestinationArray(new String[0]);
		fssConnectService.send(fssc, 15000, fsscUrl);
	}

}
