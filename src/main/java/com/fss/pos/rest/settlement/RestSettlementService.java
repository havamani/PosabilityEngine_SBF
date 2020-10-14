package com.fss.pos.rest.settlement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.pos.base.api.db.storedprocedure.BatchStatus;
import com.fss.pos.base.api.db.storedprocedure.ResponseStatus;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.factory.ApiFactory;
@Service
public class RestSettlementService {

	@Autowired
	private ApiFactory apiFactory;
	
	private static final String MISMATCH_RSP = "95";
	public static final String SETTLEMENT_TYPE_INITIAL = "S";
	public static final String SETTLEMENT_TYPE_FINAL = "F";
	public static final String SETTLEMENT_TYPE_ZERO = "Z";
	
	@SuppressWarnings("unused")
	public SettlementResponseMasterBean doSettlement(SettlementRequestMasterBean request,
			String terminalId,String channel, String mspAcr, String batchNo,			 
			String type, String procedureName) throws PosException {

		List<String> batchCounters = new ArrayList<String>();
		batchCounters.add(request.getRequest().getSettlement().getPurchaseCount());
		batchCounters.add(request.getRequest().getSettlement().getPurchaseAmount());
		batchCounters.add(request.getRequest().getSettlement().getTipCount());
		batchCounters.add(request.getRequest().getSettlement().getTipAmount());
		batchCounters.add(request.getRequest().getSettlement().getVoidCount());
		batchCounters.add(request.getRequest().getSettlement().getVoidAmount());
		batchCounters.add(request.getRequest().getSettlement().getRefundCount());
		batchCounters.add(request.getRequest().getSettlement().getRefundAmount());
		batchCounters.add(request.getRequest().getSettlement().getCompletionCount());
		batchCounters.add(request.getRequest().getSettlement().getCompletionAmount());
		batchCounters.add(request.getRequest().getSettlement().getCashbackCount());
		batchCounters.add(request.getRequest().getSettlement().getCashbackAmount());
		batchCounters.add(request.getRequest().getSettlement().getCashadvanceCount());
		batchCounters.add(request.getRequest().getSettlement().getCashadvanceAmount());
		batchCounters.add("0");//For Moto count
		batchCounters.add("0");
		batchCounters.add("0");//For CashDeposit count
		batchCounters.add("0");

		int countOfAll = 0;
		for (String c : batchCounters)
			countOfAll += Long.parseLong(c);
		if (countOfAll == 0)
			type = SETTLEMENT_TYPE_ZERO;

		List<Object> params = new ArrayList<Object>();
		params.add(terminalId);
		params.add(batchNo);
		params.add(type);
		for (String c : batchCounters)
			params.add(c);

		List<Class<?>> classList = new ArrayList<Class<?>>();
		classList.add(ResponseStatus.class);
		classList.add(BatchStatus.class);
		try {
			List<Object> objList = apiFactory.getStoredProcedureApi().getBean(
					params, procedureName, classList, mspAcr);
			String status = ((List<ResponseStatus>) objList.get(0)).get(0)
					.getStatus();
			
			SettlementDeviceInfo deviceInfo = new SettlementDeviceInfo();
			SettlementResponse response=new SettlementResponse();
			SettlementResponseMasterBean settlementResponseMasterBean = new SettlementResponseMasterBean();
			
			if(!(status.isEmpty())) {
			String batchNo1 = ((List<BatchStatus>) objList.get(1)).get(0)
					.getBatchNo();
			Log.info("Response from " + procedureName, status + "\t" + "BatchNo" + batchNo1);
			String[] arr = status.split(",");
			if (MISMATCH_RSP.equals(arr[0]))
				Log.info("Settlement Error - ProcCode : ", arr[1]
						+ " Type :  "
						+ new String("1".equals(arr[2]) ? "Count Mismatched"
								: "Amount Mismatched"));
			
			//SettlementDeviceInfo deviceInfo = new SettlementDeviceInfo();
			deviceInfo.setTerminalId(terminalId);
			deviceInfo.setDeviceModel("devModel-hc");
			deviceInfo.setDeviceSerialNo("devSerialNo-hc");
			
			//SettlementResponse response=new SettlementResponse();
			response.setChannel(channel);
			response.setStan("stan-hc");
			response.setInvoiceNo("invoice-hc");
			response.setBatchNo(batchNo1);
			response.setResponseCode(status);
			if(status.equals("00"))
				response.setResponseDescription("Success");
			else if(MISMATCH_RSP.equals(arr[0]) && arr[2].equals("1")) 
				response.setResponseDescription("Count mismatched");
			else if(MISMATCH_RSP.equals(arr[0]) && arr[2].equals("2")) 
				response.setResponseDescription("Amount mismatched");
			else
				response.setResponseDescription(status+" Please check logs");
			response.setDeviceInfo(deviceInfo);
			
			//SettlementResponseMasterBean settlementResponseMasterBean = new SettlementResponseMasterBean();
			settlementResponseMasterBean.setResponse(response);
			
			return settlementResponseMasterBean;
			}else {
				response.setResponseCode(status);
				response.setResponseDescription("No data found");
				settlementResponseMasterBean.setResponse(response);
				
				return settlementResponseMasterBean;
			}
		} catch (SQLException e) {
			Log.error("Settlement  ", e);
			throw new PosException(Constants.ERR_DATABASE);
		} catch (Exception e) {
			Log.error("Settlement ", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}

	}
}
