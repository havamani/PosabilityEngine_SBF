package com.fss.pos.rest.transactionreport;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.pos.base.api.client.Validator;
import com.fss.pos.base.api.db.storedprocedure.ResponseStatus;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.factory.ApiFactory;


@Service
public class ReportService {
	

	@Autowired
	private ApiFactory apiFactory;
	
	@SuppressWarnings("unchecked")
	public ReportResponseMasterBean txnReportData(ReportRequestMasterBean request,
			String mspAcr,String procedureName) throws Exception {
		
		ReportResponse response = new ReportResponse();
		ReportInfoResponse reportInfoResponse=new ReportInfoResponse();
		DeviceInfoResponse deviceInfo = new DeviceInfoResponse();
		ReportResponseMasterBean reportResponseMasterBean= new ReportResponseMasterBean();
		
		String frmDt=request.getRequest().getReportInfo().getFromDate();
		String toDt=request.getRequest().getReportInfo().getToDate();
		
		if(!(frmDt.matches("\\d{4}-\\d{2}-\\d{2}") && toDt.matches("\\d{4}-\\d{2}-\\d{2}"))) {			
			response.setResponseCode("01");
			response.setResponseDescription("Invalid date format");
			reportResponseMasterBean.setResponse(response);
			return reportResponseMasterBean;
		}
		
		String reportId="1";
		
		List<Object> params = new ArrayList<Object>();
		params.add(request.getRequest().getDeviceInfo().getTerminalId());	
		params.add(request.getRequest().getReportInfo().getFromDate());
		params.add(request.getRequest().getReportInfo().getToDate());
		params.add(reportId);

		List<Class<?>> classList = new ArrayList<Class<?>>();
		classList.add(ResponseStatus.class);
		classList.add(TransactionReport.class);		
		
		try {
			List<Object> objList = apiFactory.getStoredProcedureApi().getBean(
					params, procedureName, classList, mspAcr);
			
			  String status = ((List<ResponseStatus>) objList.get(0)).get(0) .getStatus();
			  Log.info("Response from " + procedureName," "+ status);			
		
			if(status.equals(Constants.SUCCESS)) {
				 List<TransactionReport> txnReportData = (List<TransactionReport>) objList.get(1);
				if(!(txnReportData.isEmpty())) {
			List<ReportInfoResponse> reportInfoList = new ArrayList<>();
			int i=1;
			
			for(TransactionReport txn:txnReportData) {
				reportInfoResponse = new ReportInfoResponse();
			    reportInfoResponse.setSerialNo(Integer.toString(i));
			    reportInfoResponse.setBatchNo(txn.getBatchNo());
			    reportInfoResponse.setInvoice(txn.getInvoice());
			    reportInfoResponse.setStan(txn.getStan());
			    reportInfoResponse.setRrn(txn.getRrn());
			    reportInfoResponse.setTransactionDate(txn.getTransactionDate());
			    reportInfoResponse.setTransactionTime(txn.getTransactionTime());
			    reportInfoResponse.setTransactionType(txn.getTransactionType());
			    reportInfoResponse.setTransactionCurrency(txn.getTransactionCurrency());
			    reportInfoResponse.setTransactionAmount(txn.getTransactionAmount());
			    reportInfoResponse.setAdditionalAmount(txn.getAdditionalAmount());
			    reportInfoResponse.setCardNo(txn.getCardNo());
			    reportInfoResponse.setCardCaptureMode(txn.getCardCaptureMode());
			    reportInfoResponse.setAuthMode(txn.getAuthMode());
			    reportInfoResponse.setAcquirerName(txn.getAcquirerName());
			    reportInfoResponse.setTerminalResCode(txn.getTerminalResCode());
			    reportInfoResponse.setTransactionStatus(txn.getTransactionStatus());
			    reportInfoResponse.setSettlementStatus(txn.getSettlementStatus());					
			    reportInfoList.add(reportInfoResponse);
			    i++;
			}						
			deviceInfo.setTerminalId(txnReportData.get(0).getTerminalId());
			deviceInfo.setMerchantName(txnReportData.get(0).getMerchantName());
			deviceInfo.setStoreName(txnReportData.get(0).getStoreName());
			deviceInfo.setOperatorId(txnReportData.get(0).getOperatorId());
			
			response.setChannel(request.getRequest().getDeviceInfo().getChannel());
			response.setDeviceInfo(deviceInfo);
			response.setResponseCode(status);
			response.setResponseDescription("Success");
			response.setReportInfo(reportInfoList);
			
			reportResponseMasterBean.setResponse(response);
			
			return reportResponseMasterBean;				
			}else {
				response.setResponseCode("01");
				response.setResponseDescription("No data found");
				reportResponseMasterBean.setResponse(response);
				
				return reportResponseMasterBean;
			}
			}else {
				response.setResponseCode(status);
				if(status.equalsIgnoreCase(Constants.ERR_UE))				
					response.setResponseDescription(Validator.getErrorDescription(Constants.ERR_UE));
				else if(status.equalsIgnoreCase(Constants.ERR_UI))
					response.setResponseDescription(Validator.getErrorDescription(Constants.ERR_UI));
				else if(status.equalsIgnoreCase(Constants.ERR_UH))
					response.setResponseDescription(Validator.getErrorDescription(Constants.ERR_UH));
				else if(status.equalsIgnoreCase(Constants.ERR_VQ))
					response.setResponseDescription(Validator.getErrorDescription(Constants.ERR_VQ));
				reportResponseMasterBean.setResponse(response);
				
				return reportResponseMasterBean;
			}
		} catch (SQLException e) {
			Log.error("Transaction Report  ", e);
			throw new PosException(Constants.ERR_DATABASE);
		} 
	}
	
}
