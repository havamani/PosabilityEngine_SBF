package com.fss.pos.rest.emvdownload;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.fss.pos.base.api.client.Validator;
import com.fss.pos.base.api.db.storedprocedure.ResponseStatus;

import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.factory.ApiFactory;

import com.fss.pos.client.services.download.parameter.EmvTerminalData;
import com.fss.pos.client.services.download.parameter.IccData;
import com.fss.pos.client.services.download.parameter.KeyData;



@Service
public class EmvDownloadService {
	@Autowired
	private ApiFactory apiFactory;
	
	@SuppressWarnings("unchecked")
	public EmvDownloadResponse emvdownload(String terminalId,String channel,
			String mspAcr,String procedureName) throws Exception {
		
		List<Object> params = new ArrayList<Object>();
		params.add(terminalId);
		
		ArrayList<Class<?>> classList = new ArrayList<Class<?>>();
		classList.add(ResponseStatus.class);
		classList.add(IccData.class);
		classList.add(IccData.class);
		classList.add(KeyData.class);
		classList.add(EmvTerminalData.class);
		
		try {
			List<Object> objList = apiFactory.getStoredProcedureApi().getBean(
					params, procedureName, classList, mspAcr);
			String status = ((List<ResponseStatus>) objList.get(0)).get(0)
					.getStatus();
			
			  EmvIccContactlessdataResponse emvicccontactless = new EmvIccContactlessdataResponse();
				EmvResponse response = new EmvResponse();
				EmvDownloadResponse emvresponse = new EmvDownloadResponse();



			if(status.equals(Constants.SUCCESS)) {

			//EmvConfigData	
				
				  List<IccData> iccdataList = (List<IccData>) objList.get(2);
				  
				  IccData iccdata = iccdataList.get(0);
				  
				  
				  emvicccontactless.setTableNumber(iccdata.getTableNumber());
				  emvicccontactless.setAddTagID(iccdata.getAddTagID());
				  emvicccontactless.setAidLength(iccdata.getAidLength());
				  emvicccontactless.setAid(iccdata.getAid());
				  emvicccontactless.setAppSelectIndicator(iccdata.getAppSelectIndicator());
				  emvicccontactless.setTerminalFloorLimit(iccdata.getTerminalFloorLimit());
				  emvicccontactless.setAppVersionNo(iccdata.getAppVersionNo());
				  emvicccontactless.setDefaultTDOL(iccdata.getDefaultTDOL());
				  emvicccontactless.setOffApproveResCode(iccdata.getOffApproveResCode());
				  emvicccontactless.setOffDeclineResCode(iccdata.getOffDeclineResCode());
				  emvicccontactless.setUnApproveResCode(iccdata.getUnApproveResCode());
				  emvicccontactless.setUnDeclineResCode(iccdata.getUnDeclineResCode());
				  emvicccontactless.setTransactionLimit(iccdata.getTransactionLimit());
				  emvicccontactless.setCvmLimit(iccdata.getCvmLimit());
				  emvicccontactless.setMagStripeAppVersionNo(iccdata.getMagStripeAppVersionNo()
				  ); emvicccontactless.setCardScheme(iccdata.getCardScheme());
				  emvicccontactless.setTerminalProfileName(iccdata.getTerminalProfileName());
				  emvicccontactless.setTransactionTypes(iccdata.getTransactionTypes());
				  emvicccontactless.setTacDenial(iccdata.getTacDenial());
				  emvicccontactless.setTacOnline(iccdata.getTacOnline());
				  emvicccontactless.setTacDefault(iccdata.getTacDefault());
				  emvicccontactless.setTermianlRiskdata(iccdata.getTermianlRiskdata());
				  emvicccontactless.setTerminalQualifiers(iccdata.getTerminalQualifiers());
				  emvicccontactless.setDefaultTDOLLength(iccdata.getDefaultTDOLLength());
				  emvicccontactless.setTerminalDataCount(iccdata.getTerminalDataCount());
				  emvicccontactless.setTerminalCapabilities("terminalCapabilities");
				  emvicccontactless.setKernelConfiguration("kernelConfiguration");
				  emvicccontactless.setMchipCapLimit("mchipCapLimit");
				  emvicccontactless.setMchipCapBelowLimit("mchipCapBelowLimit");
				  emvicccontactless.setMagstripeAboveLimit("magstripeAboveLimit");
				  emvicccontactless.setMagstripeBelowLimit("magstripeBelowLimit");
				  emvicccontactless.setDcvTxnLimit("dcvTxnLimit");
				  emvicccontactless.setAidType("aidType");
				  emvicccontactless.setAcquirerID("acquirerID");
				 
			 
			 
				//Log.info("Response from " + procedureName," "+ status);
//Key Data Response 
				  List<KeyData> keyList = ((List<KeyData>) objList.get(3)); 
				 // KeyData keydata =keyList.get(0);
						  
				  List<KeydataResponse> keyResponselist=new ArrayList<>();
				  
				  
				  for(KeyData keydata:keyList) {
					  
						KeydataResponse keydataResponse = new KeydataResponse();

						 keydataResponse.setKeyActiveDate(keydata.getKeyActiveDate());
						 keydataResponse.setKeyHash(keydata.getKeyHash());
						 keydataResponse.setKeyLength(keydata.getKeyLength());
						 keydataResponse.setKeyExpiryDate(keydata.getKeyExpiryDate());
						 keydataResponse.setKeyIndex(keydata.getKeyIndex());
						 keydataResponse.setKeyExponent(keydata.getKeyExponent());
						 keydataResponse.setRid(keydata.getRid());
						 keydataResponse.setKey(keydata.getKey());
						 keydataResponse.setStatus(keydata.getStatus()); 
						 keyResponselist.add(keydataResponse)		;		 
				  }	

//EmvTerminalData
				
				  
				  List<EmvTerminalData> emvterminaldataList = ((List<EmvTerminalData>)
				  objList.get(4));
				  
				  
				  List<EmvTerminalDataResponse> emvresponseList=new ArrayList<>();
				  
				  
				  for(EmvTerminalData emvdata:emvterminaldataList) {
					    EmvTerminalDataResponse emvInfo=new EmvTerminalDataResponse();

				  emvInfo.setDdol(emvdata.getDdol());
				  emvInfo.setTerminalCountryCode(emvdata.getTerminalCountryCode());
				  emvInfo.setTxnCurrCode(emvdata.getTxnCurrCode());
				  emvInfo.setTxnCurrExponent(emvdata.getTxnCurrExponent());
				  emvInfo.setTrcc("1");
				  emvInfo.setTdolLength(emvdata.getTdolLength());
				  emvInfo.setTerminalType(emvdata.getTerminalType());
				  emvInfo.setAdditionalTerminalCapabilities(emvdata.
				  getAdditionalTerminalCapabilities()); emvInfo.setDdol(emvdata.getDdol());
				  emvInfo.setTerminalDataCount(emvdata.getTerminalDataCount());
				  emvInfo.setTerminalCapabilities(emvdata.getTerminalCapabilities());
				  emvInfo.setTdol(emvdata.getTdol());
				  emvInfo.setTerminalProfileName(emvdata.getTerminalProfileName());
				  emvresponseList.add(emvInfo); }
				 
				 
				 
				  Log.info("Response from " + procedureName," "+ status);
		
				response.setChannel(channel);
				response.setResponsecode(status);
				if(status.equalsIgnoreCase(Constants.SUCCESS))
				response.setResponsedescription("Success");
				response.setIccContactlessdata(emvicccontactless);
				response.setKeydata(keyResponselist);
				response.setEmvTerminalData(emvresponseList);


				
				emvresponse.setResponse(response);

				return emvresponse;
			}
				else {
					Log.debug("EmvDownloadErrorCode: ",status);
					response.setResponsecode(status);
					if(status.equalsIgnoreCase(Constants.ERR_UE))				
						response.setResponsedescription(Validator.getErrorDescription(Constants.ERR_UE));
					else if(status.equalsIgnoreCase(Constants.ERR_UI))
						response.setResponsedescription(Validator.getErrorDescription(Constants.ERR_UI));
					else if(status.equalsIgnoreCase(Constants.ERR_UH))
						response.setResponsedescription(Validator.getErrorDescription(Constants.ERR_UH));
					else if(status.equalsIgnoreCase(Constants.ERR_VQ))
						response.setResponsedescription(Validator.getErrorDescription(Constants.ERR_VQ));		
			else

				response.setResponsedescription("No data found");
				emvresponse.setResponse(response);
				return emvresponse;
			}				

}	catch (SQLException e) {
				Log.error("EmvDownload  ", e);
				throw new PosException(Constants.ERR_DATABASE);
			} 
		}
}


			