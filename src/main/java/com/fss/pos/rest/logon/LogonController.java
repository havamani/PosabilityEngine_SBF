package com.fss.pos.rest.logon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fss.commons.utils.RRN;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.utils.ThreadLocalUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

//@RestController
//@Api(description = "To process logon")
@Component
public class LogonController {
	
	@Autowired
	private LogonService logonService;
	
/*	@ApiOperation(value = "To process terminal logon request", response = LogonResponseMasterBean.class)
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "Logon success"),
	        @ApiResponse(code = 401, message = "Not authorized to view the resource"),
	        @ApiResponse(code = 403, message = "Resource forbidden"),
	        @ApiResponse(code = 404, message = "Resource not found")
	    	}
	    )*/
	//@RequestMapping(value = "/logon", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	
	public LogonResponseMasterBean logon(LogonRequestMasterBean logonRequestMasterBean) throws Exception {
			
			ThreadLocalUtil.setRRN(RRN.genRRN(12));
			ThreadLocalUtil.setMsp("ALB");
			Log.trace("Logon Request : " +logonRequestMasterBean.toString());		
			LogonResponseMasterBean response=new LogonResponseMasterBean();
		try {
			String channel=logonRequestMasterBean.getRequest().getChannel();
			String terminalId=logonRequestMasterBean.getRequest().getDeviceInfo().getTerminalId();
			
			if(!(terminalId.isEmpty() || terminalId==null)) {	
				  response = logonService.logon(logonRequestMasterBean,true, terminalId, "ALB", channel);
			}			
			}catch (Exception e) {				
				Log.error("Logon controller:", e);
				return null;
			}		
		return response;
		}

}
