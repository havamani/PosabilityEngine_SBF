package com.fss.pos.host.iso8583.diners;

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


import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@HostResponse(HostType="32")
public class DinersResponseApi extends DinersApi
{
  @ModifyTransaction(Transactions.DEFAULT)
  public void modify4All(IsoBuffer isoBuffer, Data data)
  {
    ClientData cd = (ClientData)data;
    isoBuffer.put(Constants.DE62, cd.getInvoice());
    isoBuffer.put(Constants.DE41, cd.getTerminalId());
    isoBuffer.put(Constants.DE3, cd.getProcCode());
    isoBuffer.put(Constants.DE12, isoBuffer.get(Constants.DE7).substring(4));
    isoBuffer.put(Constants.DE13, cd.getDate());
    isoBuffer.put(Constants.DE12, cd.getTime());
    isoBuffer.disableField(Constants.DE53);
    isoBuffer.disableField(Constants.DE25);
    isoBuffer.disableField(Constants.DE26);
    isoBuffer.disableField(Constants.DE43);
    isoBuffer.disableField(Constants.DE45);
    isoBuffer.disableField(Constants.DE46);
    isoBuffer.disableField(Constants.DE100);
    isoBuffer.disableField(Constants.DE128);
	isoBuffer.disableField(Constants.DE23);
	isoBuffer.disableField(Constants.DE24);
	isoBuffer.disableField(Constants.DE33);
	isoBuffer.disableField(Constants.DE92);
  }

  @ModifyTransaction(Transactions.PURCHASE)
  public void modifyPurchace(IsoBuffer isoBuffer, Data data)
  {
    isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_TXN);
  }

  @ModifyTransaction(Transactions.EMV_DEFAULT)
  public void modifyEmv(IsoBuffer isoBuffer, Data data) throws UnsupportedEncodingException
  {
		if (!isoBuffer.isFieldEmpty(Constants.DE55)) {
			
			ClientData cd = (ClientData) data;
			Map<EmvTags, String> emvMap = new HashMap<EmvTags, String>();
			String emvRsp = isoBuffer.get(Constants.DE55);
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

  @ModifyTransaction(Transactions.REVERSAL_DEFAULT)
  public void modifyReversal(IsoBuffer isoBuffer, Data data)
  {
    ClientData cd = (ClientData)data;
    isoBuffer.put(Constants.DE62, cd.getInvoice());
    isoBuffer.put(Constants.DE41, cd.getTerminalId());
    isoBuffer.put(Constants.DE3, cd.getProcCode());
    //isoBuffer.put("P-12", cd.getTime());
    isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_REV_410);
    isoBuffer.disableField(Constants.DE12);
    isoBuffer.disableField(Constants.DE26);
    isoBuffer.disableField(Constants.DE100);
    isoBuffer.disableField(Constants.DE128);
  }

  @ModifyTransaction(Transactions.BALANCE_INQUIRY)
  public void modifyBalanceEnquire(IsoBuffer isoBuffer, Data data) {
	  isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_TXN);
  }
  @ModifyTransaction(Transactions.REFUND)
  public void modifyRefund(IsoBuffer isoBuffer, Data data) {
    isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_TXN);
  }
  @ModifyTransaction(Transactions.PRE_AUTHORIZATION)
  public void modifyPreAuthorization(IsoBuffer isoBuffer, Data data) {
    isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.ENQUIRY_MSG_RESPONSE);
  }
  @ModifyTransaction(Transactions.COMPLETION)
  public void modifyCompletion(IsoBuffer isoBuffer, Data data) {
    isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_TXN);
  }
  @ModifyTransaction(Transactions.VOID)
	public void modifyVoid(IsoBuffer isoBuffer, Data data) {
	  isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_TXN);
	}
  @ModifyTransaction(Transactions.CASH_ADVANCE)
  public void modifyCashAdvance(IsoBuffer isoBuffer, Data data) {
	  isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_TXN);
	}
}