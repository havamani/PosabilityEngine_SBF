package com.fss.pos.rest.riskprofiledownload;

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
//@Api(description = "To download risk profiles")
@Component
public class RiskProfileController {
	
	@Autowired
	private RiskProfileService riskProfileService;
	
	private static final String PROF_DWNLD = "PSP_PPOSDOWNLOAD";
	
/*	@ApiOperation(value = "To process risk profile request", response = RiskProfResponseMasterBean.class)
	

    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Risk profiles downloaded successfully"),
        @ApiResponse(code = 401, message = "Not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Resource forbidden"),
        @ApiResponse(code = 404, message = "Resource not found")
    	}
    )*/
//	@RequestMapping(value = "/terminalRiskProfile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	
	public RiskProfResponseMasterBean downloadRiskProfiles(RiskProfRequestMasterBean riskProfRequestMasterBean) throws Exception {
			
			ThreadLocalUtil.setRRN(RRN.genRRN(12));
			ThreadLocalUtil.setMsp("ALB");
			Log.trace("Terminal risk profile download : " +riskProfRequestMasterBean.toString());		
			RiskProfResponseMasterBean response=new RiskProfResponseMasterBean();
		try {
			String channel=riskProfRequestMasterBean.getRequest().getDeviceInfo().getChannel();
			String terminalId=riskProfRequestMasterBean.getRequest().getDeviceInfo().getTerminalId();
			
			if(!(terminalId.isEmpty() || terminalId==null)) {	
				  response = riskProfileService.profDownload(terminalId, channel, "ALB", PROF_DWNLD);
			}			
			}catch (Exception e) {				
				Log.error("Risk profile controller:", e);
				return null;
			}		
		return response;
		}
}
