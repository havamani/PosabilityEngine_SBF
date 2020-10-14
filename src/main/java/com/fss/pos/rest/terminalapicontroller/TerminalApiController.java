package com.fss.pos.rest.terminalapicontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fss.pos.base.commons.Log;
import com.fss.pos.rest.emvdownload.EmvDownloadController;
import com.fss.pos.rest.emvdownload.EmvDownloadRequest;
import com.fss.pos.rest.emvdownload.EmvDownloadResponse;
import com.fss.pos.rest.logon.LogonController;
import com.fss.pos.rest.logon.LogonRequestMasterBean;
import com.fss.pos.rest.logon.LogonResponseMasterBean;
import com.fss.pos.rest.paramdownload.ParamDownloadController;
import com.fss.pos.rest.paramdownload.ParamDownloadRequestMasterBean;
import com.fss.pos.rest.paramdownload.ParamDownloadResponseMasterBean;
import com.fss.pos.rest.riskprofiledownload.RiskProfRequestMasterBean;
import com.fss.pos.rest.riskprofiledownload.RiskProfResponseMasterBean;
import com.fss.pos.rest.riskprofiledownload.RiskProfileController;
import com.fss.pos.rest.settlement.SettlementController;
import com.fss.pos.rest.settlement.SettlementRequestMasterBean;
import com.fss.pos.rest.settlement.SettlementResponseMasterBean;
import com.fss.pos.rest.settlementbatchupload.SettlementBatchUploadController;
import com.fss.pos.rest.settlementbatchupload.SettlementBatchUploadResponse;
import com.fss.pos.rest.settlementbatchupload.SettlemtBatchUploadRequest;
import com.fss.pos.rest.supportedcarddownload.SupportedCardDownloadController;
import com.fss.pos.rest.supportedcarddownload.SupportedCardDownloadRequest;
import com.fss.pos.rest.supportedcarddownload.SupportedCardDownloadResponse;
import com.fss.pos.rest.transactioninquiry.TransactionInquiryController;
import com.fss.pos.rest.transactioninquiry.TransactionInquiryRequest;
import com.fss.pos.rest.transactioninquiry.TransactionInquiryResponse;
import com.fss.pos.rest.transactionreport.ReportController;
import com.fss.pos.rest.transactionreport.ReportRequestMasterBean;
import com.fss.pos.rest.transactionreport.ReportResponseMasterBean;
import com.fss.pos.rest.txnsummaryreport.TxnSummaryReportController;
import com.fss.pos.rest.txnsummaryreport.TxnSummaryResponseMasterBean;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(description = "To download the data to process the transaction")
public class TerminalApiController {
		
	@Autowired
	private ParamDownloadController paramDownloadController;
	
	@Autowired
	private RiskProfileController riskProfileController;
	
	@Autowired
	private SupportedCardDownloadController supportedCardDownloadController;
	
	@Autowired
    private LogonController logonController;
	
	@Autowired
	private SettlementController settlementController;
	
	@Autowired
	private TransactionInquiryController transactionInquiryController;
	
	@Autowired
	private ReportController reportController;
	
	@Autowired
	private TxnSummaryReportController txnSummaryReportController;
	
	@Autowired
	private SettlementBatchUploadController settlementBatchUploadController;
	
	@Autowired
	private EmvDownloadController emvdownloadcontroller;
	
	@ApiOperation(value = "To process parameter download request", response = ParamDownloadResponseMasterBean.class)
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "Param downloaded successfully"),
	        @ApiResponse(code = 401, message = "Not authorized to view the resource"),
	        @ApiResponse(code = 403, message = "Resource forbidden"),
	        @ApiResponse(code = 404, message = "Resource not found")
	    	}
	    )
	@RequestMapping(value = "/parameterDownload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ParamDownloadResponseMasterBean parameterDownload(
			@RequestBody ParamDownloadRequestMasterBean paramDownloadRequestMasterBean) throws Exception {
       	return paramDownloadController.parameterDownload(paramDownloadRequestMasterBean);
	}
	
	
    @ApiOperation(value = "To process risk profile request", response = RiskProfResponseMasterBean.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Risk profiles downloaded successfully"),
        @ApiResponse(code = 401, message = "Not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Resource forbidden"),
        @ApiResponse(code = 404, message = "Resource not found")
    	}
    )
    
	@RequestMapping(value = "/terminalRiskProfile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public RiskProfResponseMasterBean downloadRiskProfiles(
			@RequestBody RiskProfRequestMasterBean riskProfRequestMasterBean) throws Exception {
	return riskProfileController.downloadRiskProfiles(riskProfRequestMasterBean);
	}
	
	
	@ApiOperation(value = "To download Supportedcards", response = SupportedCardDownloadResponse.class)
    @ApiResponses(value= {
            @ApiResponse(code = 200, message = "Successfully downloaded supportedcards"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
	@RequestMapping(value = "/supportcardDownload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public SupportedCardDownloadResponse supportedcarddownload(
			@RequestBody SupportedCardDownloadRequest supportedcardDownloadRequest) throws Exception  {
	return supportedCardDownloadController.supportedcarddownload(supportedcardDownloadRequest);
	}
	
	
	@ApiOperation(value = "To process terminal logon request", response = LogonResponseMasterBean.class)
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "Logon success"),
	        @ApiResponse(code = 401, message = "Not authorized to view the resource"),
	        @ApiResponse(code = 403, message = "Resource forbidden"),
	        @ApiResponse(code = 404, message = "Resource not found")
	    	}
	    )
     @RequestMapping(value = "/logon", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public LogonResponseMasterBean logon(
			@RequestBody LogonRequestMasterBean logonRequestMasterBean) throws Exception {
    	 return logonController.logon(logonRequestMasterBean);
     }
     @ApiOperation(value = "To process settlement request",response = SettlementResponseMasterBean.class)
 	@ApiResponses(value = {
 	        @ApiResponse(code = 200, message = "Settlement done successfully"),
 	        @ApiResponse(code = 401, message = "Not authorized to view the resource"),
 	        @ApiResponse(code = 403, message = "Resource forbidden"),
 	        @ApiResponse(code = 404, message = "Resource not found")
 	    	}
 	    )
     @RequestMapping(value = "/terminalSettlement", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    	public SettlementResponseMasterBean settlement(
 			@RequestBody SettlementRequestMasterBean settlementRequestMasterBean) throws Exception {
 	   return settlementController.settlement(settlementRequestMasterBean);
     }
    
     @ApiOperation(value = "Transaction Inquiry Process",response = TransactionInquiryResponse.class)
     @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved Data"),
 	        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
 	        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
 	        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
 	}
 	)
     @RequestMapping(value = "/transactionInquiry", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
 	public TransactionInquiryResponse transactionInquiry(@RequestBody TransactionInquiryRequest transactionInquiryRequest) throws Exception {
 	return transactionInquiryController.transactionInquiry(transactionInquiryRequest);
     }
     
 	@ApiOperation(value = "Transaction Report", response = ReportResponseMasterBean.class)
 	@ApiResponses(value = {
 	        @ApiResponse(code = 200, message = "Transaction data retrieved successfully"),
 	        @ApiResponse(code = 401, message = "Not authorized to view the resource"),
 	        @ApiResponse(code = 403, message = "Resource forbidden"),
 	        @ApiResponse(code = 404, message = "Resource not found")
 	    	}
 	    )
 	@RequestMapping(value = "/txnReport", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
 	public ReportResponseMasterBean txnReport(
 			@RequestBody ReportRequestMasterBean reportRequestMasterBean) throws Exception {
        	return reportController.txnReport(reportRequestMasterBean);
 	}
	
    
 	@ApiOperation(value = "TransactionSummary Report", response = TxnSummaryResponseMasterBean.class)
 	@ApiResponses(value = {
 	        @ApiResponse(code = 200, message = "Transactionsummary retrieved successfully"),
 	        @ApiResponse(code = 401, message = "Not authorized to view the resource"),
 	        @ApiResponse(code = 403, message = "Resource forbidden"),
 	        @ApiResponse(code = 404, message = "Resource not found")
 	    	}
 	    )
 	@RequestMapping(value = "/txnSummaryReport", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
 	public TxnSummaryResponseMasterBean txnSummaryReport(
 			@RequestBody ReportRequestMasterBean reportRequestMasterBean) throws Exception {
        	return txnSummaryReportController.txnSummaryReport(reportRequestMasterBean);
 	}
 	
 	@ApiOperation(value = "Settlemet batch upload", response = ParamDownloadResponseMasterBean.class)
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = " Settlement Batch Uploaded successfully"),
	        @ApiResponse(code = 401, message = "Not authorized to view the resource"),
	        @ApiResponse(code = 403, message = "Resource forbidden"),
	        @ApiResponse(code = 404, message = "Resource not found")
	    	}
	    )
	@RequestMapping(value = "/settlementBatchUpload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
 	 public SettlementBatchUploadResponse settlementbatchUpload(@RequestBody SettlemtBatchUploadRequest request) throws Exception{
		return settlementBatchUploadController.settlementbatchUpload(request);
	}
	
	@ApiOperation(value = "To download EMVdetails", response = EmvDownloadResponse.class)
    @ApiResponses(value= {
            @ApiResponse(code = 200, message = "Successfully downloaded EMVdetails"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
	@RequestMapping(value = "/emvDownload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
 	public EmvDownloadResponse emvdownloadresponse(@RequestBody EmvDownloadRequest emvdownloadrequest)throws Exception {
 	return emvdownloadcontroller.emvdownloadresponse(emvdownloadrequest);
 	}
}
