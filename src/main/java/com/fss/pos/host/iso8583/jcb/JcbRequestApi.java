package com.fss.pos.host.iso8583.jcb;

import java.util.Map;

import com.fss.pos.base.api.host.HostData;
import com.fss.pos.base.api.host.HostRequest;
import com.fss.pos.base.api.transactions.ModifyTransaction;
import com.fss.pos.base.api.transactions.Transactions;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.EmvTags;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.base.services.transactionlog.TransactionLogData;

@HostRequest(HostType = "22")
public class JcbRequestApi extends JcbApi {

	@ModifyTransaction(value = Transactions.DEFAULT)
	public void modifyAll(IsoBuffer buffer, Data data) throws Exception {
		HostData hostData = (HostData) data;

		buffer.put(Constants.DE12, hostData.getMerchantTime());
		buffer.put(Constants.DE13, hostData.getMerchantDate());
		buffer.put(Constants.DE18, hostData.getMcc());

		String entryMode = buffer.get(Constants.DE22).substring(0, 2);
		if (Constants.ENTRY_MODE_ICC_TO_MAG_FALLBACK.equals(entryMode)) {
			buffer.put(Constants.DE22,
					"97".concat(buffer.get(Constants.DE22).substring(2, 3)));
		}
		buffer.put(Constants.DE22, buffer.get(Constants.DE22) + Constants.ZERO);
		buffer.put(Constants.DE32, hostData.getAcqInstId());
		buffer.put(Constants.DE33, hostData.getFiid()); // hostData.getFiid()
		buffer.put(Constants.DE35, buffer.get(Constants.DE35).replace("=", "D"));
		buffer.put(Constants.DE41,
				Util.appendChar(hostData.getTerminalId(), ' ', 8, false));
		buffer.put(Constants.DE42,
				Util.appendChar(hostData.getMerchantId(), ' ', 15, false));
		
		//String businessName = hostData.getBusinessName();
		//String city = hostData.getCity();
		StringBuilder de43=new StringBuilder();
		de43.append((hostData.getBusinessName().length() < 22)? Util.appendChar(
				hostData.getBusinessName(), ' ', 22, false) : Util.rightTrimChars(hostData.getBusinessName(), 22));
		
		de43.append((hostData.getCity().length() > 13) ? hostData.getCity()
				.substring(0, 13) : Util.appendChar(hostData.getCity(), ' ',
				13, false));		
		//p43.append(businessName);
		//p43.append(" "); // space
		//p43.append(city);
		//p43.append(" "); // space
		de43.append(hostData.getCountryCode()); //alphabetic
		// p43.append(hostData.getCurrencyCode()); //numeric
		//p43.append("BHR");

		buffer.put(Constants.DE43, de43.toString());
		buffer.put(Constants.DE49, hostData.getCurrencyCode()); // numeric
		
		if(!buffer.isFieldEmpty(Constants.DE52)){
			buffer.put(Constants.DE26, "06"); //Point of service PIN capture code
			
			StringBuilder sb = new StringBuilder();
			sb.append("99"); //key type
			sb.append("01"); //PIN Encryption Algorithm
			sb.append("01");  //PIN Block Format
			sb.append("00");  //Key Index
			sb.append("00");  //MAC Index
			sb.append(hostData.getZpkChecksum().substring(0, 4));  //Check Digits
			sb.append("00"); //reserved
			
			buffer.put(Constants.DE53, sb.toString());
			
		}

	}

	@ModifyTransaction(value = Transactions.PURCHASE)
	public void modifyPurchase(IsoBuffer buffer, Data data) throws Exception {
		HostData hostData = (HostData) data;

		buffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_AUTH);

		buffer.put(Constants.DE3, "000000");

		StringBuilder p48 = new StringBuilder();
		/*
		 * p48.append("03"); // tag p48.append("14"); // length
		 * p48.append(Util.appendChar(hostData.getZipCode(), ' ', 9, false)); //
		 * value p48.append("12345"); //street address
		 */p48.append("04"); // tag
		p48.append("01"); // length
		p48.append("0"); // value
		buffer.put(Constants.DE48, p48.toString());

		StringBuilder p61 = new StringBuilder();
		p61.append("2"); // non MOTO
		p61.append("2"); // non recurring
		p61.append("2"); // non preauth
		p61.append(hostData.getCurrencyCode());
		buffer.put(Constants.DE61, p61.toString());

		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE63);
		buffer.disableField(Constants.DE44);

	}

	@ModifyTransaction(value = Transactions.PRE_AUTHORIZATION)
	public void modifyPreAuth(IsoBuffer buffer, Data data) throws Exception {

		HostData hostData = (HostData) data;

		buffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_AUTH);
		buffer.put(Constants.DE3, "000000"); //original proc code
		buffer.put(Constants.DE25, "06");

		StringBuilder p48 = new StringBuilder();
		/*
		 * p48.append("03"); // tag p48.append("14"); // length
		 * p48.append(Util.appendChar(hostData.getZipCode(), ' ', 9, false)); //
		 * value p48.append("12345"); // street address
		 */p48.append("04"); // tag
		p48.append("01"); // length
		p48.append("0"); // value
		buffer.put(Constants.DE48, p48.toString());

		StringBuilder p61 = new StringBuilder();
		p61.append("2"); // non MOTO
		p61.append("2"); // non recurring
		p61.append("1"); // preauth
		p61.append(hostData.getCurrencyCode());
		buffer.put(Constants.DE61, p61.toString());

		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE63);
		buffer.disableField(Constants.DE44);
	}

	@ModifyTransaction(value = Transactions.EMV_DEFAULT)
	public void modifyEmv(IsoBuffer buffer, Data data) {

		HostData hd = (HostData) data;
		Map<EmvTags, String> tvMap = hd.getEmvMap();
		emvDetails(tvMap, buffer);
	}

	private void emvDetails(Map<EmvTags, String> tvMap, IsoBuffer buffer) {

		if (tvMap == null || tvMap.isEmpty())
			return;

		String cardSeqNo = tvMap.get(EmvTags.TAG_5F34);
		if (Util.isNullOrEmpty(cardSeqNo)) {
			tvMap.remove(EmvTags.TAG_5F34);
		} else {
			buffer.put(Constants.DE23, Util.appendChar(cardSeqNo, '0', 4, true));
			tvMap.remove(EmvTags.TAG_5F34);
		}

		StringBuilder d = new StringBuilder();
		for (EmvTags tag : tvMap.keySet()) {
			String val = tvMap.get(tag);
			d.append(tag.toString());
			d.append(Util.appendChar(
					String.valueOf(Integer.toHexString(val.length() / 2)), '0',
					2, true));
			d.append(val);
		}

		String emvDataAscii = IsoUtil.hex2AsciiChar(d.toString());

		StringBuilder emvDataBuffer = new StringBuilder();
		emvDataBuffer.append(IsoUtil.asciiChar2hex(emvDataAscii));
		buffer.put(Constants.DE55, emvDataBuffer.toString());
	}

	@ModifyTransaction(value = Transactions.VOID)
	public void modifyVoid(IsoBuffer buffer, Data data) throws Exception {

		HostData hd = (HostData) data;
		/*
		 * buffer.put(Constants.DE12, td.getMerchantTime());
		 * buffer.put(Constants.DE13, td.getMerchantDate());
		 * buffer.put(Constants.DE32, td.getAcqInstId());
		 * buffer.put(Constants.DE33, td.getFiid());
		 */

		buffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_REV_420);
		//buffer.put(Constants.DE3, "000000");
		buffer.put(Constants.DE3, hd.getOrgProcCode());
		buffer.put(Constants.DE22, hd.getReqDe22());
		if (null != hd.getReqDe23()) {
			buffer.put(Constants.DE23, hd.getReqDe23());
		}
		buffer.put(Constants.DE38, hd.getOrgAuthCode());
		buffer.put(Constants.DE39, "17"); // customer cancellation

		StringBuilder p90 = new StringBuilder("0100");
		p90.append(hd.getOrgStan());
		p90.append(hd.getOrgTransmissionDt());
		p90.append(Util.appendChar(hd.getOrgAcquirerId(), '0', 11, true));
		p90.append(Util.appendChar(hd.getOrgFiid(), '0', 11, true));
		buffer.put(Constants.DE90, p90.toString());

		buffer.put(Constants.DE95, "000000000000000000000000000000000000000000");

		buffer.disableField(Constants.DE14);
		buffer.disableField(Constants.DE18);
		buffer.disableField(Constants.DE41);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE63);
		buffer.disableField(Constants.DE44);

	}

	@ModifyTransaction(value = Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer buffer, Data data) throws Exception {

		TransactionLogData td = (TransactionLogData) data;
		buffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_REV_420);
		buffer.put(Constants.DE3, td.getOrgProcCode());
		buffer.put(Constants.DE12, td.getMerchantTime());
		buffer.put(Constants.DE13, td.getMerchantDate());
		String entryMode = buffer.get(Constants.DE22).substring(0, 2);
		if (Constants.ENTRY_MODE_ICC_TO_MAG_FALLBACK.equals(entryMode)) {
			buffer.put(Constants.DE22,
					"97".concat(buffer.get(Constants.DE22).substring(2, 3)));
		}
		buffer.put(Constants.DE22, td.getReqDe22());
		if (null != td.getReqDe23()) {
			buffer.put(Constants.DE23, td.getReqDe23());
		}
		buffer.put(Constants.DE32, td.getAcqInstId());
		buffer.put(Constants.DE33, td.getFiid());
		// buffer.put(Constants.DE35, buffer.get(Constants.DE35).replace("=",
		// "D"));
		if (null != td.getOrgAuthCode()
				&& !Constants.ZERO.equals(td.getOrgAuthCode()))
			buffer.put(Constants.DE38, td.getOrgAuthCode());
		// buffer.put(Constants.DE38, td.getOrgAuthCode());
		buffer.put(Constants.DE39, "68"); // time out

		// buffer.put(Constants.DE41, Util.appendChar(td.getTerminalId(), ' ',
		// 8, false));
		buffer.put(Constants.DE42,
				Util.appendChar(td.getMerchantId(), ' ', 15, false));

		/*
		 * StringBuilder p43 = new StringBuilder();
		 * p43.append(td.getBusinessName()); p43.append(" "); //space
		 * p43.append(td.getCity()); p43.append(" "); //space
		 * p43.append(td.getCountryCode()); //alphabetic
		 */
		buffer.put(Constants.DE43, td.getReqDe43());

		buffer.put(Constants.DE49, td.getCurrencyCode());

		StringBuilder p90 = new StringBuilder("0100");
		p90.append(td.getOrgStan());
		p90.append(td.getOrgTransmissionDt());
		p90.append(Util.appendChar(td.getAcqInstId(), '0', 11, true));
		p90.append(Util.appendChar(td.getFiid(), '0', 11, true));
		buffer.put(Constants.DE90, p90.toString());

		buffer.put(Constants.DE95, "000000000000000000000000000000000000000000");

		buffer.disableField(Constants.DE14);
		buffer.disableField(Constants.DE35);
		buffer.disableField(Constants.DE41);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE63);
		buffer.disableField(Constants.DE44);
		buffer.disableField(Constants.DE55);

	}

}
