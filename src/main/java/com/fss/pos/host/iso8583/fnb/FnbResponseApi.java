package com.fss.pos.host.iso8583.fnb;

import java.util.Map;

import com.fss.pos.base.api.client.ClientData;
import com.fss.pos.base.api.host.HostResponse;
import com.fss.pos.base.api.transactions.ModifyTransaction;
import com.fss.pos.base.api.transactions.Transactions;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.EmvTags;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.host.KeyImportResponse;
import com.fss.pos.host.iso8583.base24.Base24Api;

@HostResponse(HostType = "16")
public class FnbResponseApi extends Base24Api{

	@ModifyTransaction(Transactions.DEFAULT)
	public void modify4All(IsoBuffer isoBuffer, Data data) {

		ClientData cd = (ClientData) data;
		isoBuffer.put(Constants.DE62, cd.getInvoice());
		isoBuffer.put(Constants.DE41, cd.getTerminalId());
		isoBuffer.put(Constants.DE3, cd.getProcCode());

	}

	@ModifyTransaction(Transactions.EMV_DEFAULT)
	public void modifyEmv(IsoBuffer isoBuffer, Data txnData)
			throws PosException {

		if (isoBuffer.isFieldEmpty(Constants.DE63))
			return;

		ClientData cd = (ClientData) txnData;
		String emvData = isoBuffer.get(Constants.DE63);
		isoBuffer.disableField(Constants.DE63);
		Map<EmvTags, String> emvMap = parseEmvTokens(emvData);
		cd.setEmvResponseMap(emvMap);
	}

	@ModifyTransaction(Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer isoBuffer, Data data) {
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_REV_410);
	}
	@ModifyTransaction(Transactions.KEY_IMPORT)
	public void handleKeyImport(IsoBuffer isoBuffer, Data data) {
		KeyImportResponse kiRes = (KeyImportResponse) data;
		String de123 = isoBuffer.get(Constants.DE123);
		int idx = 33;
		String key = de123.substring(idx, idx += 32);
		kiRes.setZpk(key);
		kiRes.setCheckDigit(isoBuffer.get(Constants.DE120));
		kiRes.setRespCode(Constants.SUCCESS);
	}
}
