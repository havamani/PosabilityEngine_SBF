package com.fss.pos.host.iso8583.visa;

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

@HostResponse(HostType = "17")
public class VisaResponseApi extends VisaApi {

	@ModifyTransaction(Transactions.DEFAULT)
	public void modify4All(IsoBuffer isoBuffer, Data data) {

		ClientData cd = (ClientData) data;
		isoBuffer.put(Constants.DE41, cd.getTerminalId());
		isoBuffer.put(Constants.DE3, cd.getProcCode());
		isoBuffer.putIfAbsent(Constants.DE12, cd.getTime());
		isoBuffer.putIfAbsent(Constants.DE13, cd.getDate());

		isoBuffer.disableField(Constants.DE10);
		//isoBuffer.disableField(Constants.DE38);
		isoBuffer.disableField(Constants.DE5);
		isoBuffer.disableField(Constants.DE6);
		isoBuffer.disableField(Constants.DE9);
		isoBuffer.disableField(Constants.DE19);
		isoBuffer.disableField(Constants.DE23);
		isoBuffer.disableField(Constants.DE44);
		isoBuffer.disableField(Constants.DE54);
		isoBuffer.disableField(Constants.DE60);
		isoBuffer.disableField(Constants.DE62);
		isoBuffer.disableField(Constants.DE63);
		isoBuffer.disableField(Constants.DE126);

		if (Constants.ZERO.equals(cd.getTxnCommunicationFlow())) {
			StringBuilder msgType = new StringBuilder(
					isoBuffer.get(Constants.ISO_MSG_TYPE));
			msgType.setCharAt(1, '2');
			isoBuffer.put(Constants.ISO_MSG_TYPE, msgType.toString());
		}
		
		
	}

	@ModifyTransaction(value = Transactions.TIP)
	public void modifyTipAdjustment(IsoBuffer buffer, Data data) {
		buffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_TXN);
	}

	@ModifyTransaction(value = Transactions.VOID)
	public void modifyVoid(IsoBuffer buffer, Data data) {
		buffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_REV_410);
		ClientData cd = (ClientData) data;
		buffer.put(Constants.DE11, cd.getStan());
	}
	@ModifyTransaction(value = Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer buffer, Data data) throws Exception {
		
		ClientData cd = (ClientData) data;
		buffer.put(Constants.DE3, cd.getProcCode());
		buffer.put(Constants.DE11, cd.getStan());
		buffer.disableField(Constants.DE10);
		buffer.disableField(Constants.DE38);
		buffer.disableField(Constants.DE5);
		buffer.disableField(Constants.DE6);
		buffer.disableField(Constants.DE9);
		buffer.disableField(Constants.DE19);
		buffer.disableField(Constants.DE44);
		buffer.disableField(Constants.DE60);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE63);
		buffer.disableField(Constants.DE126);
	}

	@ModifyTransaction(Transactions.BALANCE_INQUIRY)
	public void modifyBalanceInquiry(IsoBuffer isoBuffer, Data hostClientData) {
		ClientData cd = (ClientData) hostClientData;

		if (Constants.ZERO.equals(cd.getTxnCommunicationFlow()))
			isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_AUTH);

		String balanceAmount = isoBuffer.get(Constants.DE54);
		isoBuffer.put(Constants.DE4, balanceAmount);
		// isoBuffer.disableField(Constants.DE54);
	}

	@ModifyTransaction(Transactions.PRE_AUTHORIZATION)
	public void modifyPreAuth(IsoBuffer isoBuffer, Data hostClientData) {
		ClientData cd = (ClientData) hostClientData;
		if (Constants.ZERO.equals(cd.getTxnCommunicationFlow()))
			isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_AUTH);
	}

	@ModifyTransaction(Transactions.EMV_DEFAULT)
	public void modifyEmv(IsoBuffer isoBuffer, Data data)
			throws UnsupportedEncodingException {

		if (!isoBuffer.isFieldEmpty(Constants.DE55)) {
			ClientData cd = (ClientData) data;
			Map<EmvTags, String> emvMap = new HashMap<EmvTags, String>();

			// enable for tertiary emv
			// String iadEbc = isoBuffer.get(Constants.DE139);
			// String iadEbcrspCode = iadEbc.substring(16, 20);
			// byte[] iaDataRspBytes =
			// IsoUtil.hex2AsciiChar(iadEbcrspCode).getBytes(
			// StandardCharsets.ISO_8859_1);
			// String iadRsp = IsoUtil.asciiChar2hex(new String(iaDataRspBytes,
			// Constants.CHARSET_EBCIDIC));
			// iadEbc = iadEbc.substring(0, 16) + iadRsp;
			// emvMap.put(EmvTags.TAG_91, iadEbc);

			String emvRsp = isoBuffer.get(Constants.DE55);
			emvRsp = emvRsp.substring(2);
			int tagDataLength = IsoUtil.hex2decimal(emvRsp.substring(0, 4));
			emvRsp = emvRsp.substring(4);
			String emvTags = emvRsp.substring(0, (tagDataLength * 2));

			while (emvTags.length() > 0) {
				int idx = 0;
				String tag = emvTags.substring(0, idx + 2);
				if (EMV_TAG_LEN_2.contains(tag))
					idx += 2;
				else
					tag = emvTags.substring(idx, idx += 4);
				int len = IsoUtil.hex2decimal(emvTags.substring(idx, idx += 2));
				len *= 2;

				String value = emvTags.substring(idx, idx += len);
				emvTags = emvTags.substring(idx);
				emvMap.put(EmvTags.getEmvTag(tag), value);
			}
			// emvMap.put(EmvTags.TAG_91, "10000000234567893030");
			cd.setEmvResponseMap(emvMap);
		}
	}

	@ModifyTransaction(value = Transactions.COMPLETION)
	public void modifyCompletion(IsoBuffer buffer, Data data) throws Exception {
		buffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_TXN);
	}

}
