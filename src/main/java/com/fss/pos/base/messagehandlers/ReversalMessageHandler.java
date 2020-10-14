package com.fss.pos.base.messagehandlers;

import java.sql.SQLException;
import java.util.Map;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fss.pos.base.api.client.ClientApi;
import com.fss.pos.base.api.client.Validator;
import com.fss.pos.base.api.db.storedprocedure.StoredProcedureInfo;
import com.fss.pos.base.api.host.TimeoutData;
import com.fss.pos.base.commons.EmvTags;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.ThreadLocalUtil;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.base.factory.ApiFactory;
import com.fss.pos.base.services.fssconnect.FssConnect;
import com.fss.pos.base.services.transactionlog.TransactionLogData;
import com.fss.pos.base.services.transactionlog.TransactionLogService;
import com.fss.pos.base.services.transactionlog.TransactionResponse;
import com.fss.pos.client.services.operator.OperatorApi;
import com.fss.pos.client.services.operator.TerminalOperator;
import com.fss.pos.client.services.reversal.ReversalService;

@Handler(Constants.MSG_TYPE_REV_400)
public class ReversalMessageHandler extends AbstractMessageHandler {

	private static final String AMEX_MSG_PROTOCOL = "20";
	private static final String AMEX_REVERSAL_REQ = "1420";
	private static final String JCB_MSG_PROTOCOL = "22";
	
	@Autowired
	private ReversalService reversalService;

	@Autowired
	private ApiFactory apiFactory;

	@Autowired
	private TransactionLogService transactionLogService;

	@Override
	public String handleMessage(IsoBuffer isoBuffer, FssConnect fssConnect,
			ClientApi clientApi) throws Exception {
		return reverse(isoBuffer, fssConnect, clientApi);
	}

	private String reverse(IsoBuffer clientBuffer, FssConnect fssc,
			ClientApi clientApi) throws Exception {

		if (!Validator.isReversalRequired(clientBuffer)) {
			clientBuffer.put(Constants.DE39, Constants.ERR_DONT_CARE);
			return respondClient(clientBuffer, fssc, clientApi);
		}

		String mspAcr = fssc.getIIN();
		TerminalOperator userdata = ((OperatorApi) clientApi).getTerminalOperatorData(clientBuffer);
		fillPanFromTrack2(clientBuffer);
		TransactionLogData trd = null;

		
		
		try {
			if(clientApi.checkHostToHost())
			{
				trd = reversalService.getISO8583ReversalData(clientBuffer, fssc.getSource(), userdata.getSessionId(), mspAcr);
			}
			else
			{
				trd = reversalService.getReversalData(clientBuffer, fssc.getSource(), userdata.getSessionId(), mspAcr);
			}
			
			if (!Constants.SUCCESS.equals(trd.getStatus())) {
				clientBuffer.put(Constants.DE39, trd.getStatus());
				return respondClient(clientBuffer, fssc, clientApi);
			}
			clientBuffer.put(Constants.DE37, Util.appendChar(trd.getRrn(), '0', 12, true));
			clientBuffer.setTranId(trd.getTranId());

		} catch (SQLException e) {
			Log.error("AbstractPosClient doReversal", e);
			/*
			 * clientBuffer.put(Constants.DE39, Constants.ERR_DATABASE); return
			 * respondClient(clientBuffer, fssc, clientApi);
			 */

			fssc.setSource(null);
			fssc.setDestination(null);
			fssc.setUniqueId(Constants.EMPTY_STRING);

			return fssc.toString();
		}
		

		String myStation = fssc.getDestination();

		Map<EmvTags, String> emvTags = clientApi.parseEmvData(clientBuffer);
		Log.debug("EMV Map", emvTags.toString());
		trd.setEmvMap(emvTags);

		IsoBuffer hostBuffer = new IsoBuffer(clientBuffer);
		trd.setReversal(true);
		trd.setOffline(false);

		hostBuffer.putIfAbsent(Constants.DE7, trd.getTransmissionDt());

		String hostRequest = apiFactory.getHostRequestApi(trd.getMsgProtocol()).modiyAndbuild(hostBuffer, clientBuffer,
				trd);

		String source = fssc.getSource();
		String fsscUniqueId = Util.getIsoFsscUniqueId(hostBuffer.get(Constants.DE37), hostBuffer.get(Constants.DE11),
				hostBuffer.get(Constants.DE41));

		if (trd.getMsgProtocol().equals(AMEX_MSG_PROTOCOL)
				&& hostBuffer.get(Constants.ISO_MSG_TYPE).equals(AMEX_REVERSAL_REQ)) {
			fsscUniqueId = Util.getIsoFsscUniqueId(hostBuffer.get(Constants.DE4), hostBuffer.get(Constants.DE11),
					hostBuffer.get(Constants.DE12));
		}
		else if (trd.getMsgProtocol().equals(Constants.DINERS)) {
			fsscUniqueId = Util.getIsoFsscUniqueId(hostBuffer.get(Constants.DE7),
					hostBuffer.get(Constants.DE11),
					hostBuffer.get(Constants.ISO_MSG_TYPE).substring(0, 2));
		}
		if (trd.getMsgProtocol().equals(JCB_MSG_PROTOCOL)) {

			fsscUniqueId = Util.getIsoFsscUniqueId(hostBuffer.get(Constants.DE37), hostBuffer.get(Constants.DE11),
					hostBuffer.get(Constants.DE7));
		}
		fssc.setSource(fssc.getDestination());
		fssc.setDestination(trd.getStationName());
		fssc.setUniqueId(Constants.EMPTY_STRING);
		fssc.setAlternateDestinationArray(new String[] { trd.getAlternateStationName() });
		fssc.setMessage(hostRequest);

		TimeoutData td = new TimeoutData();
		td.setIsoBufferMap(new IsoBuffer(clientBuffer).getMap());
		td.setIin(mspAcr);
		td.setThreadLocalId(ThreadLocalUtil.getRRN());
		td.setHostStationName(trd.getStationName());
		td.setClientStationName(source);
		td.setClientClass(clientApi.getClass().getCanonicalName());
		td.setUti(null);
		td.setTerminalId(clientBuffer.get(Constants.DE41));
		td.setMerchantId(clientBuffer.get(Constants.DE42));
		td.setTxnCommunicationFlow(null);
		td.setTransmissionDateAndTime(hostBuffer.get(Constants.DE7));
		td.setTranId(trd.getTranId());
		td.setMyStation(myStation);
		td.setIsDCC((trd.getIsDCC().isEmpty() || trd.getIsDCC() == null) ? "0" : trd.getIsDCC());
		td.setQuotationId(clientBuffer.get(Constants.DE63));
		td.setDccStationName(trd.getDccStationName());
		td.setDccAlternateStationName(trd.getDccAlternateStationName());
		updateTimer(fsscUniqueId, mspAcr, td, hostBuffer, clientBuffer.get(Constants.DE41), trd.getTranId());

		return fssc.toString();

	}

	@Override
	protected void modifyB4RespondingClient(IsoBuffer isoBuffer,
			FssConnect fssc, ClientApi clientApi) throws JSONException {
		int status = Constants.SUCCESS.equals(isoBuffer.get(Constants.DE39)) ? Constants.TXNLOG_STATUS_SUCCESS
				: Constants.TXNLOG_STATUS_FAILED;
		String rrn = isoBuffer.isFieldEmpty(Constants.DE37) ? null : isoBuffer
				.get(Constants.DE37);
		String procCode = isoBuffer.isFieldEmpty(Constants.DE3) ? null
				: isoBuffer.get(Constants.DE3).substring(0, 2)
						+ isoBuffer.get(Constants.DE25);

		String procedurename =clientApi.checkHostToHost()?StoredProcedureInfo.TXN_RESPONSE_UPDATE_AGGISO8583:StoredProcedureInfo.TXN_RESPONSE_UPDATE;
		
		TransactionResponse txnRsp = transactionLogService.updateTxnLog(
				isoBuffer.get(Constants.DE2, null),
				isoBuffer.get(Constants.DE41), isoBuffer.getTranId(), null,
				isoBuffer.get(Constants.DE39), Constants.ZERO, status,
				isoBuffer.get(Constants.DE42), procCode, fssc.getIIN(),
				isoBuffer.get(Constants.DE11), isoBuffer.get(Constants.DE7),procedurename);

		if (!Constants.SUCCESS.equals(txnRsp.getResponseCode()))
			isoBuffer.put(Constants.DE39, txnRsp.getResponseCode());
		clientApi.modifyBits4Response(isoBuffer, null);
	}
}
