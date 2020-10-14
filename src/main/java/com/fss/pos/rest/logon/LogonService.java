package com.fss.pos.rest.logon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.client.services.download.remotekey.KeyDownloadClientData;
import com.fss.pos.client.services.download.remotekey.RemoteKeyDownloadService;

import com.fss.pos.base.api.hsm.HsmResponse;

@Service
public class LogonService {
	
	@Autowired
	private RemoteKeyDownloadService remoteKeyDownloadService;
	
	public LogonResponseMasterBean logon(LogonRequestMasterBean request,boolean isMasterKey,String terminalId,
			String mspAcr,String channel) throws Exception {
			
		HsmResponse hsmResponse= new HsmResponse();
		KeyDownloadClientData keyClientData = new KeyDownloadClientData();
		
		try {
			keyClientData.setId(request.getRequest().getKey().getKeyId());
			keyClientData.setType(request.getRequest().getKey().getKeyType());
			keyClientData.setScheme(request.getRequest().getKey().getKeyScheme());
			keyClientData.setKsn("0");
			Log.info("Before Response from HSM", ":::ttt");
			hsmResponse=remoteKeyDownloadService.downloadKey(keyClientData, isMasterKey, terminalId, mspAcr);	
			Log.info("After calling Response from HSM","::::aaa");
			Log.info("Response from HSM", hsmResponse.getRspCode());
			
			KeyInfoResponse keyInfo= new KeyInfoResponse();
			keyInfo.setTerminalId(terminalId);
			keyInfo.setLmkEncryptedKey(hsmResponse.getLmkEncryptedKey());
			keyInfo.setParentEncryptedKey(hsmResponse.getParentEncryptedKey());
			keyInfo.setPinBlock(hsmResponse.getPinBlock());
			
			LogonResponse response= new LogonResponse();
			response.setChannel(channel);
			response.setResponseCode(hsmResponse.getRspCode());
			response.setResponseDescription(hsmResponse.getRspDesc());
			response.setKeyInfo(keyInfo);
			
			LogonResponseMasterBean logonResponseMasterBean= new LogonResponseMasterBean();
			logonResponseMasterBean.setResponse(response);			
						
			return logonResponseMasterBean;
		} catch (Exception e) {
			Log.error("Logon  ", e);
			throw new PosException(Constants.ERR_DATABASE);
		} 

	}
}	
