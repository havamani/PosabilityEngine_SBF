package com.fss.pos.rest.paramdownload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fss.commons.utils.RRN;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.ThreadLocalUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

//@RestController
//@Api(description="To download parameters in terminal")
@Component
public class ParamDownloadController {
	@Autowired
	private ParamDownloadService paramDownloadService;

	/*@ApiOperation(value = "To process parameter download request", response = ParamDownloadResponseMasterBean.class)
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "Param downloaded successfully"),
	        @ApiResponse(code = 401, message = "Not authorized to view the resource"),
	        @ApiResponse(code = 403, message = "Resource forbidden"),
	        @ApiResponse(code = 404, message = "Resource not found")
	    	}
	    )*/
	//@RequestMapping(value = "/parameterDownload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ParamDownloadResponseMasterBean parameterDownload(ParamDownloadRequestMasterBean paramDownloadRequestMasterBean) throws Exception {
			ThreadLocalUtil.setRRN(RRN.genRRN(12));
			ThreadLocalUtil.setMsp("ALB");
			Log.trace("Param download Request : " +paramDownloadRequestMasterBean.toString());		
			ParamDownloadResponseMasterBean response=new ParamDownloadResponseMasterBean();
		try {
			String channel=paramDownloadRequestMasterBean.getRequest().getDeviceInfo().getChannel();
			IsoBuffer isoBuffer=new IsoBuffer();	
			isoBuffer.put("channel",channel);	
			isoBuffer.put(Constants.DE41,paramDownloadRequestMasterBean.getRequest().getDeviceInfo().getTerminalId());
			isoBuffer.put(Constants.DE3, "930000");	
			response = paramDownloadService.paramDownloadResponse("ALB", isoBuffer);
			Log.trace("Response from PSP_PPOSDOWNLOAD : "+response.getResponse().getResponseCode());
			}catch (Exception e) {				
				Log.error("ParamDownload Controller:", e);
				return null;
			}		
		return response;
		}
}
