package com.fss.pos.base.messagehandlers;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fss.pos.base.api.client.ClientApi;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.services.fssconnect.FssConnect;
import com.fss.pos.client.services.operator.OperatorApi;
import com.fss.pos.client.services.operator.TerminalOperator;
import com.fss.pos.client.services.settlement.SettlementService;

@Handler(Constants.MSG_TYPE_BATCHUPLOAD)
public class BatchMessageHandler extends AbstractMessageHandler {

	@Autowired
	private SettlementService settlementService;

	@Override
	public String handleMessage(IsoBuffer isoBuffer, FssConnect fssConnect,
			ClientApi clientApi) throws UnsupportedEncodingException,
			PosException, JSONException {
		return verifyBatch(isoBuffer, fssConnect, clientApi);
	}

	private String verifyBatch(IsoBuffer isoBuffer, FssConnect fssc,
			ClientApi clientApi) throws PosException,
			UnsupportedEncodingException, JSONException {
		fillPanFromTrack2(isoBuffer);
		// fillTrack2FromPan(isoBuffer);
		TerminalOperator userdata = ((OperatorApi) clientApi)
				.getTerminalOperatorData(isoBuffer);
		settlementService.doBatchUpload(isoBuffer, userdata.getUserId(),
				fssc.getIIN(), clientApi);
		isoBuffer.put(Constants.DE39, Constants.SUCCESS);
		clientApi.modifyBits4Response(isoBuffer, null);
		return respondClient(isoBuffer, fssc, clientApi);
	}

}
