package com.fss.pos.rest.txnsummaryreport;

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
import com.fss.pos.rest.transactionreport.ReportRequestMasterBean;
import com.fss.pos.rest.transactionreport.TransactionReport;

@Service
public class TxnSummaryReportService {

	@Autowired
	private ApiFactory apiFactory;
	
	@SuppressWarnings("unchecked")
	public TxnSummaryResponseMasterBean txnSummaryReportData(ReportRequestMasterBean request,
			String mspAcr,String procedureName) throws Exception {

		TxnSummaryDeviceInfo txnSummaryDeviceInfo = new TxnSummaryDeviceInfo();
		TxnSummaryReportInfo txnSummaryReportInfo=new TxnSummaryReportInfo();
		TxnSummaryResponse txnSummaryResponse = new TxnSummaryResponse();
		TxnSummaryResponseMasterBean txnSummaryResponseMasterBean= new TxnSummaryResponseMasterBean();
		
		String frmDt=request.getRequest().getReportInfo().getFromDate();
		String toDt=request.getRequest().getReportInfo().getToDate();
		
		if(!(frmDt.matches("\\d{4}-\\d{2}-\\d{2}") && toDt.matches("\\d{4}-\\d{2}-\\d{2}"))) {			
			txnSummaryResponse.setResponseCode("01");
			txnSummaryResponse.setResponseDescription("Invalid date format");
			txnSummaryResponseMasterBean.setResponse(txnSummaryResponse);
			return txnSummaryResponseMasterBean;
		}
		
		String reportId="2";
		
		List<Object> params = new ArrayList<Object>();
		params.add(request.getRequest().getDeviceInfo().getTerminalId());	
		params.add(request.getRequest().getReportInfo().getFromDate());
		params.add(request.getRequest().getReportInfo().getToDate());
		params.add(reportId);

		List<Class<?>> classList = new ArrayList<Class<?>>();
		classList.add(ResponseStatus.class);
		classList.add(TxnSummaryReport.class);		
		
		try {
			List<Object> objList = apiFactory.getStoredProcedureApi().getBean(
					params, procedureName, classList, mspAcr);			
			
			String status = ((List<ResponseStatus>) objList.get(0)).get(0)
					.getStatus();
			Log.info("Response from " + procedureName," "+ status);
			
			if(status.equals(Constants.SUCCESS)) {
				List<TxnSummaryReport> txnSummaryReportData = (List<TxnSummaryReport>) objList.get(1);
				
			if(!(txnSummaryReportData.isEmpty())) {
			List<TxnSummaryReportInfo> txnSumList = new ArrayList<>();
			int i=1;			
			for(TxnSummaryReport txn:txnSummaryReportData) {
				txnSummaryReportInfo = new TxnSummaryReportInfo();
				txnSummaryReportInfo.setSerialNo(Integer.toString(i));			   
				txnSummaryReportInfo.setTransactionType(txn.getTransactionType());
				txnSummaryReportInfo.setTransactionCurrency(txn.getTransactionCurrency());
				txnSummaryReportInfo.setSuccessCount(txn.getSuccessCount());
				txnSummaryReportInfo.setSuccessAmount(txn.getSuccessAmount());
				txnSummaryReportInfo.setFailureCount(txn.getFailureCount());
				txnSummaryReportInfo.setFailureAmount(txn.getFailureAmount());
				txnSumList.add(txnSummaryReportInfo);
			    i++;
			}						
			txnSummaryDeviceInfo.setTerminalId(txnSummaryReportData.get(0).getTerminalId());
			txnSummaryDeviceInfo.setMerchantName(txnSummaryReportData.get(0).getMerchantName());
			txnSummaryDeviceInfo.setStoreName(txnSummaryReportData.get(0).getStoreName());
			
			txnSummaryResponse.setChannel(request.getRequest().getDeviceInfo().getChannel());
			txnSummaryResponse.setDeviceInfo(txnSummaryDeviceInfo);
			txnSummaryResponse.setResponseCode("00");
			txnSummaryResponse.setResponseDescription("Success");
			txnSummaryResponse.setReportInfo(txnSumList);
			
			txnSummaryResponseMasterBean.setResponse(txnSummaryResponse);
			
			return txnSummaryResponseMasterBean;				
			}else {
				txnSummaryResponse.setResponseCode("01");
				txnSummaryResponse.setResponseDescription("No data found");
				txnSummaryResponseMasterBean.setResponse(txnSummaryResponse);
				
				return txnSummaryResponseMasterBean;
			}
			}else {
				txnSummaryResponse.setResponseCode(status);				
				if(status.equalsIgnoreCase(Constants.ERR_UE))				
					txnSummaryResponse.setResponseDescription(Validator.getErrorDescription(Constants.ERR_UE));
				else if(status.equalsIgnoreCase(Constants.ERR_UI))
					txnSummaryResponse.setResponseDescription(Validator.getErrorDescription(Constants.ERR_UI));
				else if(status.equalsIgnoreCase(Constants.ERR_UH))
					txnSummaryResponse.setResponseDescription(Validator.getErrorDescription(Constants.ERR_UH));
				else if(status.equalsIgnoreCase(Constants.ERR_VQ))
					txnSummaryResponse.setResponseDescription(Validator.getErrorDescription(Constants.ERR_VQ));
				txnSummaryResponseMasterBean.setResponse(txnSummaryResponse);
				
				return txnSummaryResponseMasterBean;
			}
		} catch (SQLException e) {
			Log.error("Transaction summary report  ", e);
			throw new PosException(Constants.ERR_DATABASE);
		} 
	}
	

	}
