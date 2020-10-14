package com.fss.pos.host.iso8583.omannet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fss.pos.base.api.host.HostData;
import com.fss.pos.base.api.host.HostRequest;
import com.fss.pos.base.api.transactions.ModifyTransaction;
import com.fss.pos.base.api.transactions.Transactions;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.EmvTags;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.base.services.transactionlog.TransactionLogData;

@HostRequest(HostType = "24")
public class OmannetRequestApi extends OmannetApi {
	private static final List<EmvTags> SUPPORTED_TAGS = new ArrayList();

	static {
		SUPPORTED_TAGS.add(EmvTags.TAG_9F26);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F27);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F37);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F36);
		SUPPORTED_TAGS.add(EmvTags.TAG_95);
		SUPPORTED_TAGS.add(EmvTags.TAG_9A);
		SUPPORTED_TAGS.add(EmvTags.TAG_9C);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F02);
		SUPPORTED_TAGS.add(EmvTags.TAG_5F2A);
		SUPPORTED_TAGS.add(EmvTags.TAG_82);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F1A);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F03);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F33);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F35);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F1E);
		SUPPORTED_TAGS.add(EmvTags.TAG_4F);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F09);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F41);
		SUPPORTED_TAGS.add(EmvTags.TAG_91);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F10);
		//contactless 25/03
		SUPPORTED_TAGS.add(EmvTags.TAG_9F6E); 
		SUPPORTED_TAGS.add(EmvTags.TAG_9F7C);
	}

	@ModifyTransaction(Transactions.DEFAULT)
	public void modifyAll(IsoBuffer isoBuffer, Data data) {
		HostData hostData = (HostData) data;
		isoBuffer.put("P-7", hostData.getTxnDateTime());
		isoBuffer.put("P-12",
				Util.getFormattedDateTime("yy") + hostData.getMerchantDate()
						+ hostData.getMerchantTime());
		isoBuffer.put("P-17", isoBuffer.get("P-13"));

		String entryMode = isoBuffer.get("P-22").substring(0, 2);
		String entryMode1 = "";
		boolean b = false;
		if (("02".equals(entryMode))) {
			entryMode = "2";
		} else if ("01".equals(entryMode)) {
			entryMode = "6";
		} else if ("80".equals(entryMode)) {
			entryMode = "5";
			entryMode1 = "8";
			b = true;
		} else if("05".equals(entryMode)){
			entryMode = "8";
			entryMode1 ="5";
			b = true;
		}
		else if("07".equals(entryMode)||"91".equals(entryMode)){
			entryMode = "8";
			entryMode1 ="4";
			b = true;
		}
		
		else {
			entryMode = "2";
		}
		if (!b) {
			entryMode1 = entryMode;
		}
		String pinData = "1";
		if (isoBuffer.isFieldEmpty("P-52")) {
			pinData = "0";
		}

		if ("80".equals(entryMode))
			isoBuffer.put("P-22", entryMode + "10101" + entryMode1 + pinData
					+ "112" + "6");
		else {
			isoBuffer.put("P-22", entryMode + "10101" + entryMode1 + pinData
					+ "112" + "6");
		}
		isoBuffer.put("P-35", isoBuffer.get("P-35").replace("=", "D"));

		isoBuffer.put("P-32", hostData.getAcqInstId());
		isoBuffer.put("P-37", hostData.getRrn());
		isoBuffer.put("P-41",
				Util.appendChar(hostData.getTerminalId(), ' ', 16, false));
		isoBuffer.put("P-42",
				Util.appendChar(hostData.getMerchantId(), ' ', 15, false));			
		StringBuilder de43 = new StringBuilder();
		de43.append(hostData.getBusinessName());
		de43.append(hostData.getMerchantAddress());
		de43.append(hostData.getCity());
		de43.append(Util.appendChar(hostData.getZipCode(), ' ', 10, true));
		de43.append(hostData.getState());
		de43.append(hostData.getCountryCode());		
		isoBuffer.put("P-43", de43.toString());
		isoBuffer.put("P-49", hostData.getCurrencyCode());
		isoBuffer.put("P-53", "010101");
		isoBuffer.disableField("P-13");
		isoBuffer.put("P-35", isoBuffer.get("P-35").replace("=", "D"));
		if (!isoBuffer.isFieldEmpty("P-52"))
			isoBuffer.put("P-53", "2001010100000000");
		isoBuffer.disableField("P-63");
		isoBuffer.disableField("P-60");
		isoBuffer.disableField("P-62");
		isoBuffer.disableField("P-25");
		isoBuffer.disableField("P-44");
		
	}

	@ModifyTransaction(Transactions.EMV_DEFAULT)
	public void modifyEmv(IsoBuffer isoBuffer, Data data) {
		HostData hd = (HostData) data;
		Map<EmvTags, String> tvMap = hd.getEmvMap();

		StringBuilder d = new StringBuilder();
		for (EmvTags tag : tvMap.keySet()) {
			if (SUPPORTED_TAGS.contains(tag)) {
				d.append(tag.toString());
				String val = (String) tvMap.get(tag);
				d.append(Util.appendChar(String.valueOf(val.length()), '0', 2,
						true));
				d.append(val);
			}

		}
		
/*		String entryMode = isoBuffer.get("P-22").substring(0, 2);
		 if("07".equals(entryMode))
		 {
			 isoBuffer.put("P-22", "810101411126");
		 }
		 else
		 {
			 isoBuffer.put("P-22", "810101511126");
		 }*/

		String cardSeqNo = (String) tvMap.get(EmvTags.TAG_5F34);

		if (Util.isNullOrEmpty(cardSeqNo))
			tvMap.remove(EmvTags.TAG_5F34);
		else {
			isoBuffer.put("P-23", Util.appendChar(cardSeqNo, '0', 3, true));
		}

		isoBuffer.put("P-26", Util.appendChar(hd.getMcc(), '0', 4, true));
		isoBuffer.disableField("P-2");
		isoBuffer.disableField("P-14");
		isoBuffer.disableField("P-25");
		// Log.debug("EMV Data :::::::::::::", isoBuffer.get("P-55"));
		isoBuffer.put("P-24", "200");
	}

	@ModifyTransaction(Transactions.REFUND)
	public void modifyRefund(IsoBuffer isoBuffer, Data data) {
		HostData hd = (HostData) data;
		isoBuffer.put("MSG-TYP", "1200");
		isoBuffer.put("P-25", "1500");
		isoBuffer.put("P-26", Util.appendChar(hd.getMcc(), '0', 4, true));
		isoBuffer.disableField("P-47");
		isoBuffer.disableField("P-61");
		isoBuffer.disableField("P-38");
		isoBuffer.disableField("P-39");
	}

	@ModifyTransaction(Transactions.CASH_ADVANCE)
	public void modifyCashAdvance(IsoBuffer isoBuffer, Data data) {
		HostData hd = (HostData) data;
		isoBuffer.put("MSG-TYP", "1200");
		isoBuffer.put("P-24", "200");
		isoBuffer.put("P-26", Util.appendChar(hd.getMcc(), '0', 4, true));
		isoBuffer.disableField("P-47");
		isoBuffer.disableField("P-61");
		isoBuffer.disableField("P-38");
		isoBuffer.disableField("P-39");
	}

	@ModifyTransaction(Transactions.CASH_BACK)
	public void modifyCashBack(IsoBuffer isoBuffer, Data data) {
		HostData hd = (HostData) data;
		isoBuffer.put("P-26", Util.appendChar(hd.getMcc(), '0', 4, true));
		isoBuffer.put("MSG-TYP", "1200");
		isoBuffer.put("P-24", "200");
		isoBuffer.disableField("P-47");
		isoBuffer.disableField("P-61");
		isoBuffer.disableField("P-38");
		isoBuffer.disableField("P-39");
	}

	@ModifyTransaction(Transactions.BALANCE_INQUIRY)
	public void modifyBalanceEnquire(IsoBuffer isoBuffer, Data data) {
		HostData hd = (HostData) data;
		isoBuffer.put("P-26", Util.appendChar(hd.getMcc(), '0', 4, true));
		isoBuffer.put("MSG-TYP", "1200");
		isoBuffer.disableField("P-47");
		isoBuffer.disableField("P-61");
		isoBuffer.disableField("P-38");
		isoBuffer.disableField("P-39");
		isoBuffer.disableField("P-54");
	}

	@ModifyTransaction(Transactions.PRE_AUTHORIZATION)
	public void modifyPreAuthorization(IsoBuffer isoBuffer, Data data) {
		HostData hd = (HostData) data;
		isoBuffer.put("P-26", Util.appendChar(hd.getMcc(), '0', 4, true));
		isoBuffer.put("MSG-TYP", "1100");
		isoBuffer.put("P-3", "00" + isoBuffer.get("P-3").substring(2, 6));
		isoBuffer.put("P-24", "100");
		isoBuffer.put("P-57", "105");
		isoBuffer.put("P-39", "00");
		isoBuffer.disableField("P-47");
		isoBuffer.disableField("P-61");
		isoBuffer.disableField("P-38");
		isoBuffer.disableField("P-39");
	}

	@ModifyTransaction(Transactions.COMPLETION)
	public void modifyCompletion(IsoBuffer isoBuffer, Data data) {
		HostData hd = (HostData) data;
		isoBuffer.put("P-3", "00" + isoBuffer.get("P-3").substring(2, 6));
		if ((hd.getOrgAuthCode() != null) && (!"0".equals(hd.getOrgAuthCode()))) {
			isoBuffer.put("P-38", hd.getOrgAuthCode());
		}
		isoBuffer.put("P-35",
				isoBuffer.get("P-2") + "=" + isoBuffer.get("P-14") + "600");

		isoBuffer.put("MSG-TYP", "1220");
		isoBuffer.put("P-24", "201");
		isoBuffer.put("P-22", hd.getReqDe22());
		isoBuffer.put("P-39", hd.getRespDe39());
		isoBuffer.put("P-26", Util.appendChar(hd.getMcc(), '0', 4, true));
		StringBuilder s56 = new StringBuilder(hd.getOrgMessageType());
		s56.append(hd.getOrgStan());
		s56.append(hd.getOrgTransmissionDt());
		s56.append((hd.getAcqInstId().length() > 10 ? hd.getAcqInstId()
				.length() : new StringBuilder("0").append(
				hd.getAcqInstId().length()).toString())
				+ hd.getAcqInstId());
		isoBuffer.put("P-56", s56.toString());
		isoBuffer.disableField("P-47");
		isoBuffer.disableField("P-25");
		isoBuffer.disableField("P-61");
		isoBuffer.disableField("P-2");
		isoBuffer.disableField("P-14");
	}

	@ModifyTransaction(Transactions.VOID)
	public void modifyVoid(IsoBuffer isoBuffer, Data data) {
		HostData trd = (HostData) data;
		isoBuffer.put("MSG-TYP", "1420");
		isoBuffer.put("P-3", "00" + isoBuffer.get("P-3").substring(2, 6));
		isoBuffer.put("P-19", trd.getCurrencyCode());
		isoBuffer.put("P-24", "400");
		String pinEnabled = "5";
		if (!isoBuffer.isFieldEmpty("P-52")) {
			pinEnabled = "1";
		}
	//	isoBuffer.put("P-22", "8101016" + pinEnabled + "0126");
		isoBuffer.put("P-25", "4000");
		isoBuffer.put("P-32", trd.getAcqInstId());
		if ((trd.getOrgAuthCode() != null)
				&& (!"0".equals(trd.getOrgAuthCode()))) {
			isoBuffer.put("P-38", trd.getOrgAuthCode());
		}
		isoBuffer.put("P-12", trd.getOrgTransmissionDY());
		isoBuffer.put("P-17", trd.getMerchantDate());
		isoBuffer.put("P-26", Util.appendChar(trd.getMcc(), '0', 4, true));
		isoBuffer.put("P-53", "010101");
		isoBuffer.put("P-39", "000");
		isoBuffer.put("P-41",
				Util.appendChar(trd.getTerminalId(), ' ', 16, false));
		// isoBuffer.put("P-43", trd.getBusinessName());
		isoBuffer.put("P-49", trd.getCurrencyCode());
		StringBuilder s56 = new StringBuilder(trd.getOrgMessageType());
		s56.append(trd.getOrgStan());
		s56.append(trd.getOrgTransmissionDt());
		s56.append((trd.getAcqInstId().length() > 10 ? trd.getAcqInstId()
				.length() : new StringBuilder("0").append(
				trd.getAcqInstId().length()).toString())
				+ trd.getAcqInstId());
		isoBuffer.put("P-56", s56.toString());

		isoBuffer.disableField("P-55");
		isoBuffer.disableField("P-47");
		isoBuffer.disableField("P-61");
		isoBuffer.disableField("P-62");
		isoBuffer.disableField("P-63");
		isoBuffer.disableField("P-14");
	}

	@ModifyTransaction(Transactions.TIP)
	public void modifyTIP(IsoBuffer isoBuffer, Data data) {
		isoBuffer.put("MSG-TYP", "1200");
		isoBuffer.put("P-24", "200");
		HostData hd = (HostData) data;
		isoBuffer.put("P-26", Util.appendChar(hd.getMcc(), '0', 4, true));
		isoBuffer.disableField("P-2");
		isoBuffer.disableField("P-14");
		isoBuffer.disableField("P-25");
		isoBuffer.disableField("P-47");
		isoBuffer.disableField("P-61");
		isoBuffer.disableField("P-38");
		isoBuffer.disableField("P-39");
	}

	@ModifyTransaction(Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer isoBuffer, Data data) {
		TransactionLogData trd = (TransactionLogData) data;
		isoBuffer.put("MSG-TYP", "1420");
		isoBuffer.put("P-19", trd.getCurrencyCode());
		isoBuffer.put("P-24", "400");
	//	isoBuffer.put("P-22", "810101610126");
		
		String entryMode = isoBuffer.get("P-22").substring(0, 2);
		 if("07".equals(entryMode)||"91".equals(entryMode))
		 {
			 isoBuffer.put("P-22", "810101410126"); 
		 }
		 else
		 {
			 isoBuffer.put("P-22", "810101610126");
		 }
		
		
		isoBuffer.put("P-32", trd.getAcqInstId());
		if ((trd.getAuthCode() != null) && (!"0".equals(trd.getAuthCode()))) {
			isoBuffer.put("P-38", trd.getAuthCode());
		}
		isoBuffer.put("P-12", trd.getReqDe12());
		isoBuffer.put("P-17", trd.getMerchantDate());
		isoBuffer.put("P-26", Util.appendChar(trd.getMcc(), '0', 4, true));
		isoBuffer.put("P-53", "010101");
		isoBuffer.put("P-39", "000");
		isoBuffer.put("P-41",
				Util.appendChar(trd.getTerminalId(), ' ', 16, false));
		// isoBuffer.put("P-43", trd.getBusinessName());
		isoBuffer.put("P-43", trd.getReqDe43());
		isoBuffer.put("P-49", trd.getCurrencyCode());
		StringBuilder s56 = new StringBuilder(trd.getOriginalMsgType());
		s56.append(isoBuffer.get("P-11"));
		s56.append(trd.getTransmissionDt());
		s56.append((trd.getAcqInstId().length() > 10 ? trd.getAcqInstId()
				.length() : new StringBuilder("0").append(
				trd.getAcqInstId().length()).toString())
				+ trd.getAcqInstId());
		isoBuffer.put("P-56", s56.toString());
		isoBuffer.put("P-25", "4020");
		isoBuffer.disableField("P-55");
		isoBuffer.disableField("P-47");
		isoBuffer.disableField("P-61");
		isoBuffer.disableField("P-62");
		isoBuffer.disableField("P-63");
		isoBuffer.disableField("P-14");
		isoBuffer.disableField("P-44");
	}

	@ModifyTransaction(Transactions.PURCHASE)
	public void modifyPurchace(IsoBuffer isoBuffer, Data data) {
		HostData hostData = (HostData) data;
		if (!isoBuffer.isFieldEmpty("P-54")) {
			StringBuilder de54 = new StringBuilder(isoBuffer.get("P-3")
					.substring(2, 4));
			de54.append("42");
			de54.append(hostData.getCurrencyCode());
			de54.append("D");
			de54.append(isoBuffer.get("P-54"));
			isoBuffer.put("P-54", de54.toString());
		}

		isoBuffer.put("MSG-TYP", "1200");
		isoBuffer.put("P-24", "200");
		isoBuffer.disableField("P-25");
		isoBuffer.put("P-26", Util.appendChar(hostData.getMcc(), '0', 4, true));
		isoBuffer.disableField("P-47");
		isoBuffer.disableField("P-61");
		isoBuffer.disableField("P-38");
		isoBuffer.disableField("P-39");
		isoBuffer.disableField("P-60");
	}
}