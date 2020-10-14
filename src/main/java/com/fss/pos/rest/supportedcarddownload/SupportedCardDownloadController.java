package com.fss.pos.rest.supportedcarddownload;

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
//@Api(description="To download supported cards")
@Component
public class SupportedCardDownloadController {
	
	@Autowired
	private SupportedCardDownloadService supportedcarddownloadservice;
  /*  @ApiOperation(value = "To download Supportedcards", response = SupportedCardDownloadResponse.class)
    @ApiResponses(value= {
            @ApiResponse(code = 200, message = "Successfully downloaded supportedcards"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )*/
	
	//@RequestMapping(value = "/supportcardDownload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public SupportedCardDownloadResponse supportedcarddownload(SupportedCardDownloadRequest supportedcardDownloadRequest) throws Exception  {
		ThreadLocalUtil.setRRN(RRN.genRRN(12));
		ThreadLocalUtil.setMsp("ALB");
		Log.trace("Request : " +supportedcardDownloadRequest.toString());		
		SupportedCardDownloadResponse response=new SupportedCardDownloadResponse();
		
		try {
			String channel=supportedcardDownloadRequest.getRequest().getDeviceInfo().getChannel();
			IsoBuffer isoBuffer=new IsoBuffer();
			isoBuffer.put("channel",channel);				
			isoBuffer.put(Constants.DE41,supportedcardDownloadRequest.getRequest().getDeviceInfo().getTerminalId());
			isoBuffer.put(Constants.DE3, "930000");	
			response = supportedcarddownloadservice.supportedcardDownloadResponse("ALB", isoBuffer);
			Log.trace("Response from PSP_PPOSDOWNLOAD : "+response.getResponse().getResponseCode());
			}catch (Exception e) {				
				Log.error("SupportedCardDownload:", e);
				return null;
			}		

		return response;
		}
}
	

