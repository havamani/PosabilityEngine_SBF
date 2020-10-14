package com.fss.pos.host.iso8583.base24.ahi;

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
import com.fss.pos.host.iso8583.base24.Base24Api;

@HostResponse(HostType = "11")
public class AhiResponseApi extends Base24Api {

	@ModifyTransaction(Transactions.DEFAULT)
	public void modify4All(IsoBuffer isoBuffer, Data data) {

		ClientData cd = (ClientData) data;
		isoBuffer.put(Constants.DE62, cd.getInvoice());
		isoBuffer.put(Constants.DE41, cd.getTerminalId());
		isoBuffer.put(Constants.DE3, cd.getProcCode());

		// isoBuffer.disableField(Constants.DE63);
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
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_REV_400);
	}

}
