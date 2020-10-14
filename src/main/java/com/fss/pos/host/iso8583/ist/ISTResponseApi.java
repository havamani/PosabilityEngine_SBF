package com.fss.pos.host.iso8583.ist;

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

@HostResponse(HostType = "23")
public class ISTResponseApi extends ISTApi {

	@ModifyTransaction(Transactions.DEFAULT)
	public void modify4All(IsoBuffer isoBuffer, Data data) {

		ClientData cd = (ClientData) data;
		isoBuffer.put(Constants.DE62, cd.getInvoice());
		isoBuffer.put(Constants.DE41, cd.getTerminalId());
		isoBuffer.put(Constants.DE3, cd.getProcCode());
	}

	@ModifyTransaction(Transactions.EMV_DEFAULT)
	public void modifyEmv(IsoBuffer isoBuffer, Data data) {

		Map<EmvTags, String> emvMap = new HashMap<EmvTags, String>();
		String emvTags = isoBuffer.get(Constants.DE55);

		if (!isoBuffer.isFieldEmpty(Constants.DE55)) {
			ClientData cd = (ClientData) data;
			while (emvTags.length() > 0) {
				int idx = 0;
				String tag = emvTags.substring(0, idx + 2);
				if (EMV_TAG_LEN_2.contains(tag))
					idx += 2;
				else
					tag = emvTags.substring(idx, idx += 4);
				int len = Integer.parseInt(emvTags.substring(idx, idx += 2));

				String value = emvTags.substring(idx, idx += len);
				emvTags = emvTags.substring(idx);
				emvMap.put(EmvTags.getEmvTag(tag), value);
			}
			/*emvMap.put(EmvTags.TAG_91,
					isoBuffer.get(Constants.DE55).substring(4, 24));*/
			cd.setEmvResponseMap(emvMap);
		}

	}

	@ModifyTransaction(value = Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer buffer, Data data) throws Exception {
		buffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_REV_410);
	}

}
