package com.fss.pos.host.iso8583.benefit;

import com.fss.pos.base.api.client.ClientData;
import com.fss.pos.base.api.host.HostResponse;
import com.fss.pos.base.api.transactions.ModifyTransaction;
import com.fss.pos.base.api.transactions.Transactions;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.constants.Constants;

@HostResponse(HostType = "29")
public class BenefitResponseApi extends BenefitApi {

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
	

	@ModifyTransaction(value = Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer isoBuffer, Data data) {
		
		ClientData cd = (ClientData) data;
	//	isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_REV_430);
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_REV_410);
		isoBuffer.put(Constants.DE41, cd.getTerminalId().substring(0, 8));

	}

/*	@ModifyTransaction(value = Transactions.VOID)
	public void modifyVoid(IsoBuffer isoBuffer, Data data) {
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_REV_430);

	}*/

	@ModifyTransaction(value = Transactions.CUT_OVER)
	public void modifyCutOver(IsoBuffer buffer, Data data) {

		//Log.debug("inside cutover response method", buffer.toString());
		if(buffer.get(Constants.ISO_MSG_TYPE).equals(Constants.MSG_TYPE_NETWORK_MANAGEMENT)){
			buffer.put(Constants.ISO_MSG_TYPE, "0810");
		}else if(buffer.get(Constants.ISO_MSG_TYPE).equals(Constants.MSG_TYPE_NETWORK_MANAGEMENT_ADVICE) ||
				buffer.get(Constants.ISO_MSG_TYPE).equals(Constants.MSG_TYPE_NETWORK_MANAGEMENT_REPEAT)){
			buffer.put(Constants.ISO_MSG_TYPE, "0830");
		}
		
		buffer.put(Constants.DE1, "");
		buffer.put(Constants.DE39, "00");
		buffer.disableField(Constants.DE55);
	}

	@ModifyTransaction(value = Transactions.RECON)
	public void modifyReconciliation(IsoBuffer buffer, Data data) {

		//Log.debug("inside recon response method", buffer.toString());
		
		buffer.put(Constants.DE1, "");
		if (buffer.get(Constants.ISO_MSG_TYPE).equals(
				Constants.MSG_TYPE_SETTLEMENT)) {
			buffer.put(Constants.ISO_MSG_TYPE, "0510");

		} else if (buffer.get(Constants.ISO_MSG_TYPE).equals(
				Constants.MSG_TYPE_ACQ_RECON_ADVICE)) {
			buffer.put(Constants.ISO_MSG_TYPE, "0530");

		} else if (buffer.get(Constants.ISO_MSG_TYPE).equals(
				Constants.MSG_TYPE_ACQ_RECON_REPEAT)) {
			buffer.put(Constants.ISO_MSG_TYPE, "0530");

		} else if (buffer.get(Constants.ISO_MSG_TYPE).equals(
				Constants.MSG_TYPE_CARD_ISSUER_RECON)) {
			buffer.put(Constants.ISO_MSG_TYPE, "0512");

		} else if (buffer.get(Constants.ISO_MSG_TYPE).equals(
				Constants.MSG_TYPE_ISSUER_RECON_ADVICE)) {
			buffer.put(Constants.ISO_MSG_TYPE, "0532");

		} else if (buffer.get(Constants.ISO_MSG_TYPE).equals(
				Constants.MSG_TYPE_ISSUER_RECON_REPEAT)) {
			buffer.put(Constants.ISO_MSG_TYPE, "0532");
		}
		buffer.put(Constants.DE66, "9"); // Message received but will not be
											// checked against totals at the
											// present time. This response code
											// will always be expected by
											// BENEFIT, since reconciliation
											// totals are not validated on-line.
		
		buffer.disableField(Constants.DE55);
		buffer.disableField(Constants.DE74);
		buffer.disableField(Constants.DE75);
		buffer.disableField(Constants.DE76);
		buffer.disableField(Constants.DE77);
		buffer.disableField(Constants.DE78);
		buffer.disableField(Constants.DE79);
		buffer.disableField(Constants.DE80);
		buffer.disableField(Constants.DE81);
		buffer.disableField(Constants.DE86);
		buffer.disableField(Constants.DE87);
		buffer.disableField(Constants.DE88);
		buffer.disableField(Constants.DE89);
		buffer.disableField(Constants.DE97);
	}
}
