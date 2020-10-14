package com.fss.pos.host.iso8583.postilion.nedbank;

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
import com.fss.pos.host.KeyImportResponse;
import com.fss.pos.host.iso8583.postilion.PostilionApi;

@HostResponse(HostType = "13")
public class NedbankResponseApi extends PostilionApi {

	@ModifyTransaction(Transactions.DEFAULT)
	public void modify4All(IsoBuffer isoBuffer, Data data) {

		isoBuffer.disableField(Constants.DE30);
		isoBuffer.disableField(Constants.DE28);
		isoBuffer.disableField(Constants.DE23);
		isoBuffer.disableField(Constants.DE40);
		isoBuffer.disableField(Constants.DE59);

		ClientData cd = (ClientData) data;
		isoBuffer.put(Constants.DE62, cd.getInvoice());
		isoBuffer.put(Constants.DE41, cd.getTerminalId());
		isoBuffer.put(Constants.DE3, cd.getProcCode());
	}

	@ModifyTransaction(Transactions.EMV_DEFAULT)
	public void modifyEmv(IsoBuffer isoBuffer, Data data) {

		ClientData cd = (ClientData) data;

		if (isoBuffer.isFieldEmpty(Constants.DE127)) {
			Log.trace("No 127 Bit in response from Nedbank Host");
			return;
		}
		IsoBuffer isoBuffer127 = isoBuffer.getBuffer(Constants.DE127);

		if (isoBuffer127.isFieldEmpty(Constants.DE25))
			return;

		IsoBuffer isoBuffer127_25 = isoBuffer127.getBuffer(Constants.DE25);
		Map<EmvTags, String> emvMap = new HashMap<EmvTags, String>();
		if (!isoBuffer127_25.isFieldEmpty(Constants.DE31)) {
			emvMap.put(EmvTags.TAG_91, isoBuffer127_25.get(Constants.DE31));
		}
		if (!isoBuffer127_25.isFieldEmpty(Constants.DE32)) {
			emvMap.put(EmvTags.TAG_71, isoBuffer127_25.get(Constants.DE32));
		}
		if (!isoBuffer127_25.isFieldEmpty(Constants.DE33)) {
			emvMap.put(EmvTags.TAG_72, isoBuffer127_25.get(Constants.DE33));
		}
		cd.setEmvResponseMap(emvMap);
	}

	@ModifyTransaction(Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer isoBuffer, Data data) {
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_REV_400);
	}

	@ModifyTransaction(Transactions.KEY_IMPORT)
	public void handleKeyImport(IsoBuffer isoBuffer, Data data) {
		KeyImportResponse kiRes = (KeyImportResponse) data;
		String de125 = isoBuffer.get(Constants.DE125);
		int idx = 0;
		String key = de125.substring(idx, idx += 32);
		kiRes.setZpk(key);
		kiRes.setCheckDigit(de125.substring(de125.length() - 6, de125.length()));
		kiRes.setRespCode(Constants.SUCCESS);
	}
}
