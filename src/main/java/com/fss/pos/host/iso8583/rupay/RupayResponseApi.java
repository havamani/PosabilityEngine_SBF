package com.fss.pos.host.iso8583.rupay;

/**
 * @author Paranthamanv
 *
 */
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


@HostResponse(HostType = "19")
public class RupayResponseApi extends RupayApi {

	@ModifyTransaction(Transactions.DEFAULT)
	public void modify4All(IsoBuffer isoBuffer, Data data) {
		ClientData cdata = (ClientData) data;
		isoBuffer.put(Constants.DE41, cdata.getTerminalId());
		isoBuffer.put(Constants.DE3, cdata.getProcCode());
		isoBuffer.put(Constants.DE12, cdata.getTime());
		isoBuffer.put(Constants.DE13, cdata.getDate());
		isoBuffer.put(Constants.DE62, cdata.getInvoice());

		if (Constants.ZERO.equals(cdata.getTxnCommunicationFlow())) // DMS
			isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_AUTH);

		isoBuffer.disableField(Constants.DE19);
		isoBuffer.disableField(Constants.DE23);

	}

	@ModifyTransaction(Transactions.PURCHASE)
	public void modifyPurchase(IsoBuffer isoBuffer, Data data) {
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_TXN);

	}

	@ModifyTransaction(Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer isoBuffer, Data data) {
		ClientData cd = (ClientData) data;
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_REV_410);
		isoBuffer.put(Constants.DE41, cd.getTerminalId().substring(0, 8));
		isoBuffer.put(Constants.DE11, cd.getStan());
		isoBuffer.disableField(Constants.DE102);
		isoBuffer.disableField(Constants.DE90);
		isoBuffer.disableField(Constants.DE12);
		isoBuffer.disableField(Constants.DE13);
		isoBuffer.disableField(Constants.DE19);

	}

	@ModifyTransaction(Transactions.VOID)
	public void modifyVoid(IsoBuffer isoBuffer, Data data) {
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_TXN);
		//
		ClientData cd = (ClientData) data;
		isoBuffer.put(Constants.DE11, cd.getStan());
		isoBuffer.disableField(Constants.DE102);
		isoBuffer.disableField(Constants.DE90);
		isoBuffer.disableField(Constants.DE12);
		isoBuffer.disableField(Constants.DE13);
		isoBuffer.disableField(Constants.DE19);
	}

	@ModifyTransaction(Transactions.CASH_BACK)
	public void modifyPurchase_Cashback(IsoBuffer isoBuffer, Data data) {
		ClientData cdata = (ClientData) data;
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_TXN);
		
//		isoBuffer.put(Constants.DE54, cdata.getRespDe54());
		

	}

	@ModifyTransaction(Transactions.CASH_ADVANCE)
	public void modifyCashatPOS(IsoBuffer isoBuffer, Data data) {
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_TXN);

	}

	@ModifyTransaction(value = Transactions.CUT_OVER)
	public void modifyCutOver(IsoBuffer buffer, Data data) {

		Log.debug("inside cutover response method", buffer.toString());
		if (buffer.get(Constants.ISO_MSG_TYPE).equals(
				Constants.MSG_TYPE_NETWORK_MANAGEMENT)) {
			buffer.put(Constants.ISO_MSG_TYPE, "0810");
		} else if (buffer.get(Constants.ISO_MSG_TYPE).equals(
				Constants.MSG_TYPE_NETWORK_MANAGEMENT_ADVICE)
				|| buffer.get(Constants.ISO_MSG_TYPE).equals(
						Constants.MSG_TYPE_NETWORK_MANAGEMENT_REPEAT)) {
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
