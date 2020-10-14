package com.fss.pos.rest.transactioninquiry;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import com.fss.pos.base.api.db.storedprocedure.ResponseStatus;
import com.fss.pos.base.api.db.storedprocedure.StoredProcedureInfo;
import com.fss.pos.base.api.db.storedprocedure.TransactionEnquiry;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.factory.ApiFactory;

@Service
public class TransactionInquiryService {
	@Autowired
	private ApiFactory apifactory;
	
	//private final TranascationInqRsMapper tranascationInqRsMapper = new TranascationInqRsMapperImpl();
	
	
	public TransactionInquiryResponse transactionInqRs(String rrn,String channel,String procedureName) throws Exception {
		TransactionInquiryResponse transactionInquiryResponse = new TransactionInquiryResponse();
		TransactionInqResponse response = new TransactionInqResponse();
		TransactionInqDeviceInfo deviceInfo = new TransactionInqDeviceInfo();
		PaymentInfo paymentInfo= new PaymentInfo();
		List<Object> objList = getTransactionDetails(rrn,procedureName);
		String status = ((List<ResponseStatus>) objList.get(0)).get(0).getStatus();
		  if (status.equals(Constants.INVALID_RSP_CODE)) {
				response.setResponseCode(status);
				response.setResponseDescription("No-Data Found");
			}
		  else if(CollectionUtils.isNotEmpty(objList)) {
			TransactionEnquiry txnEnq = ((List<TransactionEnquiry>) objList.get(1)).get(0);
		//	transactionInquiryResponse = tranascationInqRsMapper.getTraxnInqRes(txnEnq);
             deviceInfo.setTerminalId(txnEnq.getTid());
             paymentInfo.setTransactionType(txnEnq.getProcCode());
            paymentInfo.setTransactionAmount(txnEnq.getAmount()); 
            paymentInfo.setTransactionStatus(txnEnq.getStatus());
    		response.setChannel(channel);
			response.setStan(txnEnq.getStan());
            response.setRrn(txnEnq.getRrn());
            response.setInvoiceNo(txnEnq.getInvoice());
            response.setResponseCode(txnEnq.getRespCode());
            response.setResponseDescription(txnEnq.getStatus());
            response.setAuthCode(txnEnq.getApprCode());
            response.setDeviceInfo(deviceInfo);
            response.setPaymentInfo(paymentInfo);
           // transactionInquiryResponse.setReponse(response);
		}
          transactionInquiryResponse.setReponse(response);

		
		return transactionInquiryResponse;
	}
	
	public List<Object>  getTransactionDetails(String rrn,String procedureName) throws Exception{
		//IsoBuffer isobuffer = new IsoBuffer();
		List<Object> params = new ArrayList<Object>();
		ArrayList<Class<?>> classList = new ArrayList<Class<?>>();
		params.add(rrn);
		classList.add(ResponseStatus.class);
		classList.add(TransactionEnquiry.class);
		List<Object> objList = null;
		try {
			objList =apifactory.getStoredProcedureApi().getBean(params, procedureName,classList, "ALB");
		String status = ((List<ResponseStatus>) objList.get(0)).get(0).getStatus();
			Log.debug("Response from Procedure " + StoredProcedureInfo.GENERIC_ENQUIRY, status);
		}
		catch (Exception e) {
			Log.error("Transaction Inquiry error: ", e);
			throw new PosException(Constants.ERR_DATABASE);

		}
		/*
		 * if("12".equals(status)) { throw new PosException("No-Data Found"); } if
		 * (!Constants.SUCCESS.equals(status)) throw new PosException(status);
		 */
	return	objList;
	}
}
