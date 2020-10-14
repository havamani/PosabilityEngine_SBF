package com.fss.pos.rest.txnsummaryreport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.fss.commons.utils.RRN;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.utils.ThreadLocalUtil;
import com.fss.pos.rest.transactionreport.ReportRequestMasterBean;


@RestController
public class TxnSummaryReportController {

	@Autowired
	private TxnSummaryReportService txnSummaryReportService;
	
	private static final String TXN_REPORT = "PSP_APIREPORT";
	public TxnSummaryResponseMasterBean txnSummaryReport(ReportRequestMasterBean reportRequestMasterBean) throws Exception {
		
		ThreadLocalUtil.setRRN(RRN.genRRN(12));
		ThreadLocalUtil.setMsp("ALB");
		Log.trace("TxnSummaryReport Request : " +reportRequestMasterBean.toString());		
		TxnSummaryResponseMasterBean response=new TxnSummaryResponseMasterBean();
	try {	
			  response = txnSummaryReportService.txnSummaryReportData(reportRequestMasterBean, "ALB", TXN_REPORT);
		}catch (Exception e) {				
			Log.error("TxnSumarryReport controller:", e);
			return null;
		}		
	return response;
	}

}
