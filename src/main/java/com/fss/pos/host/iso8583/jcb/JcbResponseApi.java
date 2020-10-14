package com.fss.pos.host.iso8583.jcb;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.fss.pos.base.api.client.ClientData;
import com.fss.pos.base.api.host.HostResponse;
import com.fss.pos.base.api.transactions.ModifyTransaction;
import com.fss.pos.base.api.transactions.Transactions;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.EmvTags;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;

@HostResponse(HostType = "22")
public class JcbResponseApi extends JcbApi{

	
	@ModifyTransaction(Transactions.DEFAULT)
	public void modify4All(IsoBuffer isoBuffer, Data data) {

		ClientData cd = (ClientData) data;
		isoBuffer.put(Constants.DE62, cd.getInvoice());
		isoBuffer.put(Constants.DE41, cd.getTerminalId().substring(0, 8));
		isoBuffer.put(Constants.DE3, cd.getProcCode());

		isoBuffer
				.put(Constants.DE12, isoBuffer.get(Constants.DE7).substring(4));
		isoBuffer.put(Constants.DE13, cd.getDate());
		isoBuffer.put(Constants.DE12, cd.getTime());

	}

	@ModifyTransaction(value = Transactions.PURCHASE)
	public void modifyPurchace(IsoBuffer isoBuffer, Data data) {
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_TXN);
	}
	
	@ModifyTransaction(value = Transactions.VOID)
	public void modifyVoid(IsoBuffer isoBuffer, Data data) {
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_TXN);
	}
	
	@ModifyTransaction(value = Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer isoBuffer, Data data) {
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_REV_410);
		isoBuffer.disableField(Constants.DE12);
		isoBuffer.disableField(Constants.DE13);
		isoBuffer.disableField(Constants.DE90);
		isoBuffer.disableField(Constants.DE95);
	
	}
	
	@ModifyTransaction(Transactions.EMV_DEFAULT)
	public void modifyEmv(IsoBuffer isoBuffer, Data data)
			throws UnsupportedEncodingException {
		
		if (!isoBuffer.isFieldEmpty(Constants.DE55)) {
			
			ClientData cd = (ClientData) data;
			Map<EmvTags, String> emvMap = new HashMap<EmvTags, String>();
			
			String emvRsp = isoBuffer.get(Constants.DE55);
			/*emvRsp = emvRsp.substring(2);
			int tagDataLength = IsoUtil.hex2decimal(emvRsp.substring(0, 4));
			emvRsp = emvRsp.substring(4);
			String emvTags = emvRsp.substring(0, (tagDataLength * 2));*/
			
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
		}
	}
}
