package com.fss.pos.rest.settlementbatchupload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fss.pos.base.commons.Log;
import com.fss.pos.rest.paramdownload.ParamDownloadResponseMasterBean;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

//@RestController
//@Api(description = "Settlemet batch upload")
@Component
public class SettlementBatchUploadController {
	
	@Autowired
	private SettlementBatchUploadService settlementBatchUploadService;
	
	/*@ApiOperation(value = "Settlemet batch upload", response = ParamDownloadResponseMasterBean.class)
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = " Settlement Batch Uploaded successfully"),
	        @ApiResponse(code = 401, message = "Not authorized to view the resource"),
	        @ApiResponse(code = 403, message = "Resource forbidden"),
	        @ApiResponse(code = 404, message = "Resource not found")
	    	}
	    )*/
	//@RequestMapping(value = "/settlementBatchUpload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
     public SettlementBatchUploadResponse settlementbatchUpload(SettlemtBatchUploadRequest request) throws Exception{
		Log.trace("Settlement Batch Upload Request : " + request.toString());
		SettlementBatchUploadResponse response = new SettlementBatchUploadResponse();
		response = settlementBatchUploadService.getResponse(request);
		return response;
	}

}
