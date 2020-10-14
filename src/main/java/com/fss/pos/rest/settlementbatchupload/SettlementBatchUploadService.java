package com.fss.pos.rest.settlementbatchupload;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.pos.base.api.client.Validator;
import com.fss.pos.base.api.db.storedprocedure.StoredProcedureInfo;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.PosUtil;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.base.factory.ApiFactory;

@Service
public class SettlementBatchUploadService {
	
	@Autowired
	private ApiFactory apiFactory;

	
	public SettlementBatchUploadResponse getResponse(SettlemtBatchUploadRequest request)throws Exception {
		SettlementBatchUploadResponse response = new SettlementBatchUploadResponse();
		 String rspCode = batchUploadCall(request); 
		SettlementBatchUploadDeviceInfo deviceInfo = new SettlementBatchUploadDeviceInfo();
		deviceInfo.setTerminalId(request.getRequest().getDeviceInfo().getTerminalId());
		SettlementBatchUploadPaymentInfo paymentInfo = new SettlementBatchUploadPaymentInfo();
		 StmnttBatchUploadRs stmnttBatchUploadRs = new StmnttBatchUploadRs();

		if(rspCode.equals(Constants.SUCCESS)) {
		paymentInfo.setTransactionAmount(request.getRequest().getPaymentInfo().getTransactionAmount());
		 stmnttBatchUploadRs.setAuthCode(request.getRequest().getPaymentInfo().getAuthCode());
		  stmnttBatchUploadRs.setChannel(request.getRequest().getChannel()); 
		 stmnttBatchUploadRs.setStan(request.getRequest().getStan());
		 stmnttBatchUploadRs.setInvoiceNo(request.getRequest().getInvoiceNo()); 
		 stmnttBatchUploadRs.setRrn(request.getRequest().getRrn());
		 stmnttBatchUploadRs.setDeviceInfo(deviceInfo);
		 stmnttBatchUploadRs.setPaymentInfo(paymentInfo);
		 stmnttBatchUploadRs.setResponseCode(rspCode);
			if(rspCode.equalsIgnoreCase(Constants.SUCCESS))
				stmnttBatchUploadRs.setResponseDescription("Success");
		
      //	 stmnttBatchUploadRs.setResponseDescription("Approved or completed Successfully.");
		}
		else {
			stmnttBatchUploadRs.setResponseCode(rspCode);
			if(rspCode.equalsIgnoreCase(Constants.ERR_UE))				
				stmnttBatchUploadRs.setResponseDescription(Validator.getErrorDescription(Constants.ERR_UE));
			else if(rspCode.equalsIgnoreCase(Constants.ERR_UI))
				stmnttBatchUploadRs.setResponseDescription(Validator.getErrorDescription(Constants.ERR_UI));
			else if(rspCode.equalsIgnoreCase(Constants.ERR_UH))
				stmnttBatchUploadRs.setResponseDescription(Validator.getErrorDescription(Constants.ERR_UH));
			else if(rspCode.equalsIgnoreCase(Constants.ERR_VQ))
				stmnttBatchUploadRs.setResponseDescription(Validator.getErrorDescription(Constants.ERR_VQ));
			else 
				stmnttBatchUploadRs.setResponseDescription("No data found");
		}
		
		 response.setResponse(stmnttBatchUploadRs);
		
		
		return response;
	}
	
	
	
	public String batchUploadCall(SettlemtBatchUploadRequest request) throws Exception {
		String rspCode =null;
		List<Object> params = new ArrayList<Object>();
		params.add(request.getRequest().getDeviceInfo().getTerminalId());
		params.add(request.getRequest().getRrn());
        params.add(request.getRequest().getPaymentInfo().getTransactionType());
		params.add(request.getRequest().getPaymentInfo().getTransactionAmount());
        params.add(null);//merchantID
		params.add(request.getRequest().getInvoiceNo());
        params.add(null);//UserId
		params.add(PosUtil.maskCardNumber(request.getRequest().getPaymentInfo().getPaymentNo(),"ALB"));
        params.add(null);//HSDCardNumber
        params.add(null);//CaptureMode
		params.add(request.getRequest().getStan());
        params.add(null);//AccountType
		params.add(Float.parseFloat(request.getRequest().getPaymentInfo().getTipAmount()));
		params.add(request.getRequest().getTransactionDT());
        params.add(null);//AppCode


		/*
		 * params.add(request.getRequest().getPaymentInfo().getEntryMode());
		 * params.add(request.getRequest().getPaymentInfo().getConditionCode());
		 * params.add(request.getRequest().getPaymentInfo().getAuthCode());
		 * params.add(request.getRequest().getPaymentInfo().getAuthData());
		 * params.add(request.getRequest().getPaymentInfo().getEntryMode());
		 * params.add(request.getRequest().getPaymentInfo().getExpiryDate()); //
		 * params.add(request.getRequest().getPaymentInfo().getPaymentTrack()); //
		 * params.add(request.getRequest().getPaymentInfo().getEmvData());
		 * params.add(Util.hashSHA512(request.getRequest().getPaymentInfo().getPaymentNo
		 * (), "ALB"));
		 */
		try {
			 rspCode = apiFactory.getStoredProcedureApi().getString(params, StoredProcedureInfo.BATCH_UPLOAD, "ALB");
				Log.info("Response from " + StoredProcedureInfo.BATCH_UPLOAD," "+ rspCode);

			
		} catch (Exception e) {
			Log.error("SettlementService doBatchUpload ", e);
			throw new PosException(Constants.ERR_DATABASE);
		}
		return rspCode;
	}

}
