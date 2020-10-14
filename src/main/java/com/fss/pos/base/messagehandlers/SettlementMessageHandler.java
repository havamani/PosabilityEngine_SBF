package com.fss.pos.base.messagehandlers;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fss.pos.base.api.client.ClientApi;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.services.fssconnect.FssConnect;
import com.fss.pos.client.services.settlement.BatchTotals;
import com.fss.pos.client.services.settlement.SettlementApi;
import com.fss.pos.client.services.settlement.SettlementService;

@Handler(Constants.MSG_TYPE_SETTLEMENT)
public class SettlementMessageHandler extends AbstractMessageHandler {

	@Autowired
	private SettlementService settlementService;

	@Override
	public String handleMessage(IsoBuffer isoBuffer, FssConnect fssConnect,
			ClientApi clientApi) throws UnsupportedEncodingException,
			PosException, JSONException {
		return settle(isoBuffer, fssConnect, clientApi);
	}

	private String settle(IsoBuffer isoBuffer, FssConnect fssc,
			ClientApi clientApi) throws PosException,
			UnsupportedEncodingException, JSONException {

		String rspCode;
		if (Constants.PROC_CODE_INIT_SETTLEMENT.equals(isoBuffer.get(
				Constants.DE3).substring(0, 2))
				&& (Constants.COND_CODE_INIT_SETTLEMENT.equals(isoBuffer
						.get(Constants.DE25)))) {
			BatchTotals bd = ((SettlementApi) clientApi)
					.parseSettlementRequest(isoBuffer.get(Constants.DE63));
			rspCode = settlementService.doSettlement(isoBuffer
					.get(Constants.DE41), bd, fssc.getIIN(), isoBuffer
					.get(Constants.DE60),
					SettlementService.SETTLEMENT_TYPE_INITIAL,
					((SettlementApi) clientApi).getBatchTotalsFormat(),
					((SettlementApi) clientApi)
							.getSettlementStoredProcedureExt());
			Log.debug("Response Code Settlement", rspCode);
			// added latest batch no
			String[] arr = rspCode.split(",");
			isoBuffer.put(Constants.DE39, arr[0]);
			isoBuffer.put(Constants.DE60, arr[1]);

		}
		else if (Constants.PROC_CODE_INIT_SETTLEMENT.equals(isoBuffer.get(
				Constants.DE3).substring(0, 2)))
				 {
			BatchTotals bd = ((SettlementApi) clientApi)
					.parseSettlementRequest(isoBuffer.get(Constants.DE63));
			rspCode = settlementService.doSettlement(
					isoBuffer.get(Constants.DE41), bd, fssc.getIIN(),
					isoBuffer.get(Constants.DE60),
					SettlementService.SETTLEMENT_TYPE_INITIAL,
					((SettlementApi) clientApi).getBatchTotalsFormat(),
					((SettlementApi) clientApi).getSettlementStoredProcedure());
			Log.debug("Response Code Settlement", rspCode);
			// added latest batch no
			String[] arr = rspCode.split(",");
			isoBuffer.put(Constants.DE39, arr[0]);
			isoBuffer.put(Constants.DE60, arr[1]);
			
		} else if (Constants.PROC_CODE_FINAL_SETTLEMENT.equals(isoBuffer.get(
				Constants.DE3).substring(0, 2))
				&& (Constants.COND_CODE_INIT_SETTLEMENT.equals(isoBuffer
						.get(Constants.DE25)))) {
			try {
				BatchTotals bd = ((SettlementApi) clientApi)
						.parseSettlementRequest(isoBuffer.get(Constants.DE63));
				rspCode = settlementService.doSettlement(isoBuffer
						.get(Constants.DE41), bd, fssc.getIIN(), isoBuffer
						.get(Constants.DE60),
						SettlementService.SETTLEMENT_TYPE_FINAL,
						((SettlementApi) clientApi).getBatchTotalsFormat(),
						((SettlementApi) clientApi)
								.getSettlementStoredProcedureExt());
				Log.debug("Response Code Settlement", rspCode);
				// added latest batch no
				String[] arr = rspCode.split(",");
				isoBuffer.put(Constants.DE39,
						arr[0].equals(Constants.SUCCESS) ? Constants.SUCCESS
								: arr[0]);
				isoBuffer.put(Constants.DE60, arr[1]);
			} catch (Exception e) {
				Log.error("Final Settlement", e);
			}
			
		 } else if (Constants.PROC_CODE_FINAL_SETTLEMENT.equals(isoBuffer.get(
				Constants.DE3).substring(0, 2)))
				 {
			try {
				BatchTotals bd = ((SettlementApi) clientApi)
						.parseSettlementRequest(isoBuffer.get(Constants.DE63));
				rspCode = settlementService.doSettlement(isoBuffer
						.get(Constants.DE41), bd, fssc.getIIN(), isoBuffer
						.get(Constants.DE60),
						SettlementService.SETTLEMENT_TYPE_FINAL,
						((SettlementApi) clientApi).getBatchTotalsFormat(),
						((SettlementApi) clientApi)
								.getSettlementStoredProcedure());
				Log.debug("Response Code Settlement", rspCode);
				// added latest batch no
				String[] arr = rspCode.split(",");
				isoBuffer.put(Constants.DE39,
						arr[0].equals(Constants.SUCCESS) ? Constants.SUCCESS
								: arr[0]);
				isoBuffer.put(Constants.DE60, arr[1]);
				
			} catch (Exception e) {
				Log.error("Final Settlement", e);
			}
			// isoBuffer.put(Constants.DE39, Constants.SUCCESS); 
		} else {
			throw new PosException(Constants.ERR_INVALID_PROC_CODE);
		}
		return respondClient(isoBuffer, fssc, clientApi);
	}
}
