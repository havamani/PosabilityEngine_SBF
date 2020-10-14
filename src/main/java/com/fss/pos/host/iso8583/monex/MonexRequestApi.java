package com.fss.pos.host.iso8583.monex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fss.pos.base.api.host.HostRequest;
import com.fss.pos.base.api.transactions.ModifyTransaction;
import com.fss.pos.base.api.transactions.Transactions;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.DccParams;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.constants.Constants;


@HostRequest(HostType="27")
public class MonexRequestApi extends MonexApi {
	
	private static final List<String> RESPONSE_FIELDS;
	static {
	List<String> rspFields = new ArrayList<String>();
	rspFields.add(Constants.DE2);
	rspFields.add(Constants.DE3);
	rspFields.add(Constants.DE11);
	rspFields.add(Constants.DE12);
	rspFields.add(Constants.DE13);
	rspFields.add(Constants.DE37);
	rspFields.add(Constants.DE39);
	rspFields.add(Constants.DE41);
	rspFields.add(Constants.DE42);
	rspFields.add(Constants.DE43);
	rspFields.add(Constants.DE48);
	rspFields.add(Constants.DE63);
	
	RESPONSE_FIELDS = Collections.unmodifiableList(rspFields);
	}
	
	private static final String DCC_ENQUIRY_PROCODE = "000000";

	@ModifyTransaction(value = Transactions.DCC_ENQUIRY)
	public void modifyDccEnquiry (IsoBuffer isobuffer, Data data)throws Exception {
		DccParams dccParams = (DccParams) data;
		isobuffer.put(Constants.DE3, DCC_ENQUIRY_PROCODE); 
		isobuffer.put(Constants.DE41, dccParams.getTerminalId());
		isobuffer.put(Constants.DE48, dccParams.getAcqInstId());
		isobuffer.putIfAbsent(Constants.DE12, dccParams.getMerchantTime());
		isobuffer.putIfAbsent(Constants.DE13, dccParams.getMerchantDate());
		isobuffer.disableField(Constants.DE62);
		isobuffer.disableField(Constants.DE22);
		isobuffer.disableField(Constants.DE25);
		isobuffer.disableField(Constants.DE7);
		isobuffer.disableField(Constants.DE54);
		isobuffer.put(Constants.DE49, dccParams.getCurrencyCode());
		//isobuffer.put(Constants.DE2, "4761340000000050");
		isobuffer.put(Constants.DE43, dccParams.getCountryCode());
	}
	
	@ModifyTransaction(value = Transactions.DCC_COMPLETION)
	public void completionAdvice(IsoBuffer isobuffer,Data data) throws Exception
	{
		
		isobuffer.put(Constants.DE3, DCC_ENQUIRY_PROCODE); 
		
		try {
			//isoBuffer.put("TERTIARY-BITMAP", "*");
			isobuffer.put("SECONDARY-BITMAP", "*");

			for (int i = 1; i <= 128; i++) {
				if (i <= 63) {
					if(!RESPONSE_FIELDS.contains(IsoBuffer.PREFIX_PRIMARY+i)){
						isobuffer.disableField(IsoBuffer.PREFIX_PRIMARY+i);
					}
				}else{
					isobuffer.put(IsoBuffer.PREFIX_SECONDARY+i, STAR);
				}
			}
			
		} catch (Exception e) {
			throw new Exception("Error Occured in fillISOBuffer");
		}
		
	}
		
		
	@ModifyTransaction(value = Transactions.REVERSAL_DEFAULT)
	public void completionReversal(IsoBuffer isobuffer, Data data) throws Exception {

		isobuffer.put(Constants.ISO_MSG_TYPE, "0120");
		isobuffer.put(Constants.DE3, DCC_ENQUIRY_PROCODE);

		try {
			// isoBuffer.put("TERTIARY-BITMAP", "*");
			isobuffer.put("SECONDARY-BITMAP", "*");

			for (int i = 1; i <= 128; i++) {
				if (i <= 63) {
					if (!RESPONSE_FIELDS.contains(IsoBuffer.PREFIX_PRIMARY + i)) {
						isobuffer.disableField(IsoBuffer.PREFIX_PRIMARY + i);
					}
				} else {
					isobuffer.put(IsoBuffer.PREFIX_SECONDARY + i, STAR);
				}
			}
		} catch (Exception e) {
			throw new Exception("Error Occured in fillISOBuffer");
			}
	}
}
