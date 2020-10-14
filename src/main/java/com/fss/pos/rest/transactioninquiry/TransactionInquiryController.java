package com.fss.pos.rest.transactioninquiry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fss.pos.base.api.db.storedprocedure.StoredProcedureInfo;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.utils.ThreadLocalUtil;

import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;



//@RestController
//@Api(description = "Transaction Inquiry")
@Component
public class TransactionInquiryController {
	
	@Autowired
	private TransactionInquiryService transactioninquiryService;
	
/*	@ApiOperation(value = "Transaction Inquiry Process",response = TransactionInquiryResponse.class)
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved list"),
	        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
	        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
	        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	}
	)*/
	//@RequestMapping(value = "/transactionInquiry", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public TransactionInquiryResponse transactionInquiry(TransactionInquiryRequest transactionInquiryRequest) throws Exception {
		//ThreadLocalUtil.setRRN(RRN.genRRN(12));
		Log.trace("Transaction Inquiry Request : " +transactionInquiryRequest.toString());		
		TransactionInquiryResponse transactionInquiryResponse = new TransactionInquiryResponse();
		String rrn=transactionInquiryRequest.getRequest().getRrn();
		String channel=transactionInquiryRequest.getRequest().getChannel();
		transactionInquiryResponse=transactioninquiryService.transactionInqRs(rrn,channel,StoredProcedureInfo.GENERIC_ENQUIRY);
		Log.trace("Transaction Inquiry Response : " + transactionInquiryResponse.toString());		
         return transactionInquiryResponse;
	}
}
