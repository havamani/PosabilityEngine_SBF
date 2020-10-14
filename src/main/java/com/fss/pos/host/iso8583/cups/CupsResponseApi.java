package com.fss.pos.host.iso8583.cups;

import java.util.HashMap;
import java.util.Map;

import com.fss.pos.base.api.client.ClientData;
import com.fss.pos.base.api.host.HostResponse;
import com.fss.pos.base.api.transactions.ModifyTransaction;
import com.fss.pos.base.api.transactions.Transactions;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.EmvTags;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.host.iso8583.amex.ISOUtil;
@HostResponse(HostType = "21")
public class CupsResponseApi extends CupsApi {
	
	@ModifyTransaction(Transactions.DEFAULT)
	public void modify4All(IsoBuffer isoBuffer,Data data) {
		
		ClientData cd = (ClientData) data;
		isoBuffer.put(Constants.DE62, cd.getInvoice());
		isoBuffer.put(Constants.DE41, cd.getTerminalId());
		isoBuffer.put(Constants.DE3, cd.getProcCode());
		isoBuffer.put(Constants.DE13, cd.getDate());
		isoBuffer.put(Constants.DE12, cd.getTime());
		isoBuffer.put(Constants.DE11, cd.getStan());
		isoBuffer.disableField(Constants.DE19);
		isoBuffer.disableField(Constants.DE23);
		isoBuffer.disableField(Constants.DE44);
		isoBuffer.disableField(Constants.DE54);
		isoBuffer.disableField(Constants.DE60);
	//	isoBuffer.disableField(Constants.DE62);
		isoBuffer.disableField(Constants.DE63);
		isoBuffer.disableField(Constants.DE126);
		isoBuffer.disableField(Constants.DE121);
		isoBuffer.disableField(Constants.DE100);
		
		
		}
		
	@ModifyTransaction(Transactions.PURCHASE)
	public void modifyPurchase(IsoBuffer isoBuffer, Data data) {
			isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_TXN);
		
	}
	@ModifyTransaction(Transactions.PRE_AUTHORIZATION)
	public void modifyPreAuth(IsoBuffer isoBuffer, Data data) {
		ClientData cd = (ClientData) data;
		if (Constants.ZERO.equals(cd.getTxnCommunicationFlow()))
			isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_AUTH);
		 if(isoBuffer.get(Constants.DE3).substring(0, 2).equals("03"))
		  {
			isoBuffer.put(Constants.DE3,
	     			"30" +isoBuffer.get(Constants.DE3).substring(2, 6)); 
		  }
	}
	@ModifyTransaction(Transactions.COMPLETION)
	public void modifyCompletion(IsoBuffer isoBuffer, Data hostClientData) {
		isoBuffer.disableField(Constants.DE54);
	}
	
	@ModifyTransaction(Transactions.VOID)
	public void modifyVoid(IsoBuffer isoBuffer, Data data) {
			isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_TXN);
		
	}
	@ModifyTransaction(Transactions.REFUND)
	public void modifyRefund(IsoBuffer isoBuffer, Data hostClientData) {
		isoBuffer.put(Constants.ISO_MSG_TYPE,"0210");
		isoBuffer.disableField(Constants.DE54);
	}
	@ModifyTransaction(Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer isoBuffer, Data data) {
		ClientData cd = (ClientData) data;
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_REV_410);

		  if(isoBuffer.get(Constants.DE3).substring(0, 2).equals("03"))
		  {
			isoBuffer.put(Constants.DE3,
	     			"30" +isoBuffer.get(Constants.DE3).substring(2, 6)); 
		  }
		isoBuffer.put(Constants.DE41, cd.getTerminalId().substring(0, 8));
		isoBuffer.put(Constants.DE11, cd.getStan());
		
	}
	@ModifyTransaction(value = Transactions.CUT_OVER)
	public void modifyCutOver(IsoBuffer buffer, Data data) {

		Log.debug("inside cutover response method", buffer.toString());
		if(buffer.get(Constants.ISO_MSG_TYPE).equals(Constants.MSG_TYPE_NETWORK_MANAGEMENT)){
			buffer.put(Constants.ISO_MSG_TYPE, "0810");
		}else if(buffer.get(Constants.ISO_MSG_TYPE).equals(Constants.MSG_TYPE_NETWORK_MANAGEMENT_ADVICE) ||
				buffer.get(Constants.ISO_MSG_TYPE).equals(Constants.MSG_TYPE_NETWORK_MANAGEMENT_REPEAT)){
			buffer.put(Constants.ISO_MSG_TYPE, "0830");
		}
		
		buffer.put(Constants.DE1, "");
		buffer.put(Constants.DE39, "00");
	}
	
	@ModifyTransaction(Transactions.EMV_DEFAULT)
	public void modifyEmv(IsoBuffer buffer, Data data) throws Exception {
		if (!buffer.isFieldEmpty(Constants.DE55)) {
			
			ClientData cd = (ClientData) data;
			Map<EmvTags, String> emvMap = new HashMap<EmvTags, String>();
			String emvRsp = ISOUtil.alpha2Hex(buffer.get(Constants.DE55));
			while (emvRsp.length() > 0) {
				int idx = 0;
				String tag = emvRsp.substring(0, idx + 2);
				if (EMV_TAG_LEN_2.contains(tag))
					idx += 2;
				else
					tag = emvRsp.substring(idx, idx += 4);
				int len = IsoUtil.hex2decimal(emvRsp.substring(idx, idx += 2));
				len *= 2;
				String value = emvRsp.substring(idx, idx += len);
				emvRsp = emvRsp.substring(idx);
				emvMap.put(EmvTags.getEmvTag(tag), value);
			}
			cd.setEmvResponseMap(emvMap);
			Log.debug("finally parsed emv", cd.getEmvResponseMap().toString());
		}
	}


}

