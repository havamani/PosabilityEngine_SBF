package com.fss.pos.host.iso8583.amex;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;

import com.fss.pos.base.api.client.ClientData;
import com.fss.pos.base.api.host.HostResponse;
import com.fss.pos.base.api.transactions.ModifyTransaction;
import com.fss.pos.base.api.transactions.Transactions;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.EmvTags;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;

@HostResponse(HostType = "20")
public class AmexResponseApi extends AmexApi {

	@ModifyTransaction(value = Transactions.DEFAULT)
	public void modify4All(IsoBuffer isoBuffer, Data data) {

		ClientData cd = (ClientData) data;

		isoBuffer.put(Constants.DE41, cd.getTerminalId());
		isoBuffer.put(Constants.DE3, cd.getProcCode());
		isoBuffer.put(Constants.DE12, cd.getTime());
		isoBuffer.put(Constants.DE13, cd.getDate());
		isoBuffer.put(Constants.DE62, cd.getInvoice());

		isoBuffer.disableField(Constants.DE19);
		isoBuffer.disableField(Constants.DE23);
		isoBuffer.disableField(Constants.DE31);
		isoBuffer.disableField(Constants.DE44);
		isoBuffer.disableField(Constants.DE60);
		isoBuffer.disableField(Constants.DE62);
		isoBuffer.disableField(Constants.DE63);
		isoBuffer.disableField(Constants.DE64);
		isoBuffer.disableField(Constants.DE126);
	}

	@ModifyTransaction(value = Transactions.PURCHASE)
	public void modifyPurchace(IsoBuffer isoBuffer, Data data) {
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_TXN);
	}

	@ModifyTransaction(value = Transactions.VOID)
	public void modifyVoid(IsoBuffer isoBuffer, Data data) {
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_TXN);
		isoBuffer.disableField(Constants.DE12);
		isoBuffer.disableField(Constants.DE31);
		isoBuffer.disableField(Constants.DE64);
	}
	@ModifyTransaction(value = Transactions.PRE_AUTHORIZATION)
	public void modifyPreAuth(IsoBuffer isoBuffer, Data data) {
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_AUTH);
		isoBuffer.disableField(Constants.DE12);
		isoBuffer.disableField(Constants.DE31);
		isoBuffer.disableField(Constants.DE64);
	}
	@ModifyTransaction(value = Transactions.COMPLETION)
	public void modifyCompletion(IsoBuffer isoBuffer, Data data) {
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_TXN);
		isoBuffer.disableField(Constants.DE12);
		isoBuffer.disableField(Constants.DE31);
		isoBuffer.disableField(Constants.DE64);
	}
	@ModifyTransaction(value = Transactions.REFUND)
	public void modifyRefund(IsoBuffer isoBuffer, Data data) {
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_TXN);
		isoBuffer.disableField(Constants.DE12);
		isoBuffer.disableField(Constants.DE31);
		isoBuffer.disableField(Constants.DE64);
	}
	@ModifyTransaction(value = Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer isoBuffer, Data data) {
		ClientData cd = (ClientData) data;
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_REV_410);
		isoBuffer.put(Constants.DE3, cd.getProcCode());
		isoBuffer.disableField(Constants.DE12);
		isoBuffer.disableField(Constants.DE19);
		isoBuffer.disableField(Constants.DE23);
		isoBuffer.disableField(Constants.DE31);
		isoBuffer.disableField(Constants.DE44);
		isoBuffer.disableField(Constants.DE60);
		isoBuffer.disableField(Constants.DE62);
		isoBuffer.disableField(Constants.DE63);
		isoBuffer.disableField(Constants.DE64);
		isoBuffer.disableField(Constants.DE126);
	}

	@ModifyTransaction(value = Transactions.EMV_DEFAULT)
	public void modifyEmv(IsoBuffer isoBuffer, Data data)
			throws UnsupportedEncodingException {


		if (!isoBuffer.isFieldEmpty(Constants.DE55)) {
			ClientData cd = (ClientData) data;
			Map<EmvTags, String> emvMap = new HashMap<EmvTags, String>();

			String emvRsp = isoBuffer.get(Constants.DE55);
			int count = 0;

			int len = emvRsp.length();
			String versionName = emvRsp.substring(count, count + 4);
			count = +4;
			String versionNo = emvRsp.substring(count, count + 2);
			versionNo = ISOUtil.asciiChar2hex(versionNo);
			count = count + 2;

			if(len > 6){
				
				String iadData = Hex.encodeHexString(emvRsp.substring(count, len)
						.getBytes("Cp1047"));
				
				if(len == 17){
					int offset = 0;
					String a = iadData.substring(offset, offset + 2);
					int iadlen = IsoUtil.hex2decimal(a);
					offset =offset+2;
					int actual91 = iadlen*2;
					String value = iadData.substring(offset, offset+actual91);
					emvMap.put(EmvTags.TAG_91, value);
				}else{
					int offset = 0;
					String a = iadData.substring(offset, offset + 2);
					int iadlen = IsoUtil.hex2decimal(a);
					offset =offset+2;
					int actual91 = iadlen*2;
					String value = iadData.substring(offset, offset+actual91);
					offset = offset+actual91;
					emvMap.put(EmvTags.TAG_91, value);
					
					String b = iadData.substring(offset, offset+2);
					int issuerDataLen = IsoUtil.hex2decimal(b);
					offset = offset+2;
					int entireIssuerLength = issuerDataLen*2;
					String tag = iadData.substring(offset, offset+2);
					offset = offset+2;
					String actual71 = iadData.substring(offset, offset+2);
					int length71 = IsoUtil.hex2decimal(actual71)*2;
					offset = offset+2;
					String value71 = iadData.substring(offset, offset+length71);
					emvMap.put(EmvTags.getEmvTag(tag), value71);
					
				}

			}else{
				isoBuffer.put(Constants.DE55, "*");
			}
	
			cd.setEmvResponseMap(emvMap);
			
			//Log.debug("finally parsed emv", cd.getEmvResponseMap().toString());
		}
	}
}
