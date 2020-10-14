package com.fss.pos.host.iso8583.master;

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

@HostResponse(HostType = "18")
public class MasterResponseApi extends MasterApi {

	@ModifyTransaction(Transactions.DEFAULT)
	public void modify4All(IsoBuffer isoBuffer, Data data) {

		ClientData cd = (ClientData) data;
		isoBuffer.put(Constants.DE62, cd.getInvoice());
		isoBuffer.put(Constants.DE41, cd.getTerminalId());
		isoBuffer.put(Constants.DE3, cd.getProcCode());

		if (Constants.ZERO.equals(cd.getTxnCommunicationFlow())) {
			StringBuilder msgType = new StringBuilder(
					isoBuffer.get(Constants.ISO_MSG_TYPE));
			msgType.setCharAt(1, '2');
			isoBuffer.put(Constants.ISO_MSG_TYPE, msgType.toString());
		}

		isoBuffer.disableField(Constants.DE5);
		isoBuffer.disableField(Constants.DE6);
		isoBuffer.disableField(Constants.DE9);
		isoBuffer.disableField(Constants.DE12);
		isoBuffer.disableField(Constants.DE13);
		isoBuffer.disableField(Constants.DE63);
		isoBuffer.disableField(Constants.DE54);
		//isoBuffer.disableField(Constants.DE55);
		// for dcc
		isoBuffer.disableField(Constants.DE16);
		isoBuffer.disableField(Constants.DE33);
		isoBuffer.disableField(Constants.DE126);
		isoBuffer.disableField(Constants.DE127);

	}

	@ModifyTransaction(Transactions.BALANCE_INQUIRY)
	public void modifyBalanceInquiry(IsoBuffer isoBuffer, Data hostClientData) {
		ClientData cd = (ClientData) hostClientData;
		if (Constants.ZERO.equals(cd.getTxnCommunicationFlow()))
			isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_AUTH);
	}

	@ModifyTransaction(Transactions.REVERSAL_DEFAULT)
	public void modifyReversalDefault(IsoBuffer isoBuffer, Data hostClientData) {
		ClientData cd = (ClientData) hostClientData;
		isoBuffer.put(Constants.DE3, cd.getProcCode());
		isoBuffer.disableField(Constants.DE6);
		isoBuffer.disableField(Constants.DE63);
		isoBuffer.disableField(Constants.DE54);
		// for dcc
		isoBuffer.disableField(Constants.DE16);
		isoBuffer.disableField(Constants.DE33);
		isoBuffer.disableField(Constants.DE127);
		isoBuffer.disableField(Constants.DE90);
	}

	@ModifyTransaction(Transactions.REFUND)
	public void modifyRefund(IsoBuffer isoBuffer, Data hostClientData) {
		isoBuffer.disableField(Constants.DE54);
	}

	@ModifyTransaction(Transactions.PRE_AUTHORIZATION)
	public void modifyPreAuth(IsoBuffer isoBuffer, Data hostClientData) {
		ClientData cd = (ClientData) hostClientData;
		if (Constants.ZERO.equals(cd.getTxnCommunicationFlow()))
			isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_AUTH);
	}

	@ModifyTransaction(value = Transactions.EMV_DEFAULT)
	public void modifyEmv(IsoBuffer buffer, Data data) throws Exception {
		if (!buffer.isFieldEmpty(Constants.DE55)) {
			ClientData cd = (ClientData) data;
			Map<EmvTags, String> emvMap = new HashMap<EmvTags, String>();
			String emvRsp = buffer.get(Constants.DE55);
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
			/*if (emvMap.containsKey(EmvTags.TAG_91)) {
				String value = emvMap.get(EmvTags.TAG_91);
				Log.debug("value", value);
				value = value.substring(0, value.length() - 4);
				Log.debug("value1", value);
				String fi = value.concat("3030");
				Log.debug("fi", fi);
				emvMap.put(EmvTags.TAG_91, fi);
			}*/
			cd.setEmvResponseMap(emvMap);
		}
	}
}
