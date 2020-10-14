package com.fss.pos.rest.transactionreport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.fss.commons.utils.RRN;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.utils.ThreadLocalUtil;


@RestController
public class ReportController {
	
	@Autowired
	private ReportService reportService;

	private static final String TXN_REPORT = "PSP_APIREPORT";
	public ReportResponseMasterBean txnReport(ReportRequestMasterBean reportRequestMasterBean) throws Exception {
		
		ThreadLocalUtil.setRRN(RRN.genRRN(12));
		ThreadLocalUtil.setMsp("ALB");
		Log.trace("Report Request : " +reportRequestMasterBean.toString());		
		ReportResponseMasterBean response=new ReportResponseMasterBean();
	try {	
			  response = reportService.txnReportData(reportRequestMasterBean, "ALB", TXN_REPORT);
		}catch (Exception e) {				
			Log.error("Report controller:", e);
			return null;
		}		
	return response;
	}
}
