package com.fss.pos.rest.settlement;

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
//@Api(description="To process terminal settlement")
@Component
public class SettlementController {
	
	@Autowired
	private RestSettlementService settlementService;
	
	private static final String SETTLEMENT = "PSP_SETTTLEMENT";
	
	/*@ApiOperation(value = "To process settlement request", response = SettlementResponseMasterBean.class)
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "Settlement done successfully"),
	        @ApiResponse(code = 401, message = "Not authorized to view the resource"),
	        @ApiResponse(code = 403, message = "Resource forbidden"),
	        @ApiResponse(code = 404, message = "Resource not found")
	    	}
	    )*/
	//@RequestMapping(value = "/terminalSettlement", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	
	public SettlementResponseMasterBean settlement( SettlementRequestMasterBean settlementRequestMasterBean) throws Exception {
			
			ThreadLocalUtil.setRRN(RRN.genRRN(12));
			ThreadLocalUtil.setMsp("ALB");
			Log.trace("Terminal Settlement Request : " +settlementRequestMasterBean.toString());		
			SettlementResponseMasterBean response=new SettlementResponseMasterBean();
		try {
			String channel=settlementRequestMasterBean.getRequest().getChannel();
			String terminalId=settlementRequestMasterBean.getRequest().getDeviceInfo().getTerminalId();
			String batchNo=settlementRequestMasterBean.getRequest().getSettlement().getBatchNo();
			
			if(!(terminalId.isEmpty() || terminalId==null)) {	
				  response = settlementService.doSettlement(settlementRequestMasterBean,terminalId,channel,
						  			"ALB",batchNo,"S",SETTLEMENT);
			}			
			}catch (Exception e) {				
				Log.error("Settlement Controller:", e);
				return null;
			}		
		return response;
		}

}
