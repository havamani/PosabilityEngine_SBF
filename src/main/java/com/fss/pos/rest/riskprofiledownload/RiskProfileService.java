package com.fss.pos.rest.riskprofiledownload;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.pos.base.api.db.storedprocedure.ResponseStatus;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.factory.ApiFactory;
import com.fss.pos.client.services.download.parameter.CardRange;
import com.fss.pos.client.services.download.parameter.EmvTerminalData;
import com.fss.pos.client.services.download.parameter.IccData;
import com.fss.pos.client.services.download.parameter.IssuerData;
import com.fss.pos.client.services.download.parameter.KeyData;
import com.fss.pos.client.services.download.parameter.TerminalConfig;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.api.client.Validator;

@Service
public class RiskProfileService {

	@Autowired
	private ApiFactory apiFactory;
	
	@SuppressWarnings("unchecked")
	public RiskProfResponseMasterBean profDownload(String terminalId,String channel,
			String mspAcr,String procedureName) throws Exception {

		List<Object> params = new ArrayList<Object>();
		params.add(terminalId);		

		List<Class<?>> classList = new ArrayList<Class<?>>();
		classList.add(ResponseStatus.class);
		classList.add(TerminalConfig.class);
		classList.add(CardRange.class);
		classList.add(IssuerData.class);
		classList.add(IccData.class);
		classList.add(IccData.class);
		classList.add(KeyData.class);
		classList.add(EmvTerminalData.class);
		
		try {
			List<Object> objList = apiFactory.getStoredProcedureApi().getBean(
					params, procedureName, classList, mspAcr);
			String status = ((List<ResponseStatus>) objList.get(0)).get(0)
					.getStatus();
			
			RiskPrflDwnldDeviceInfoRequest deviceInfo = new RiskPrflDwnldDeviceInfoRequest();
			RiskProfileResponse response=new RiskProfileResponse();
			RiskProfResponseMasterBean riskProfResponseMasterBean = new RiskProfResponseMasterBean();
			
			if(status.equals(Constants.SUCCESS)) {
				List<IssuerData> issuerList = (List<IssuerData>) objList.get(3);
				
				if (issuerList.isEmpty()) {
					Log.trace("No terminal risk profiles");
					throw new PosException(Constants.ERR_DATABASE);
				}
				List<IssuerData> issuerListFinal = new ArrayList<IssuerData>();
				
				int size=issuerList.size();
				for (int i=0;i<size;i++) {								
					issuerListFinal.add(issuerList.get(i));
				}
				Log.info("Response from " + procedureName," "+ status);
				
				deviceInfo.setTerminalId(terminalId);
				deviceInfo.setDeviceModel("devModel-hc");
				deviceInfo.setDeviceSerialNo("devSerialNo-hc");
				
				response.setBatchNo("");
				response.setChannel(channel);
				response.setResponseCode(status);
				if(status.equalsIgnoreCase(Constants.SUCCESS))
					response.setResponseDescription("Success");
				response.setDeviceInfo(deviceInfo);
				response.setRiskProfileInfo(issuerListFinal);
				
				riskProfResponseMasterBean.setResponse(response);
				
				return riskProfResponseMasterBean;
				}else {
					response.setResponseCode(status);
					if(status.equalsIgnoreCase(Constants.ERR_UE))				
						response.setResponseDescription(Validator.getErrorDescription(Constants.ERR_UE));
					else if(status.equalsIgnoreCase(Constants.ERR_UI))
						response.setResponseDescription(Validator.getErrorDescription(Constants.ERR_UI));
					else if(status.equalsIgnoreCase(Constants.ERR_UH))
						response.setResponseDescription(Validator.getErrorDescription(Constants.ERR_UH));
					else if(status.equalsIgnoreCase(Constants.ERR_VQ))
						response.setResponseDescription(Validator.getErrorDescription(Constants.ERR_VQ));
					else 
						response.setResponseDescription("No data found");
					
					riskProfResponseMasterBean.setResponse(response);
					
					return riskProfResponseMasterBean;
					}				
		} catch (SQLException e) {
			Log.error("Risk Profile Download  ", e);
			throw new PosException(Constants.ERR_DATABASE);
		} 
	}

	
}
