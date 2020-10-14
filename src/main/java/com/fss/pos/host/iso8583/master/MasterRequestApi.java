package com.fss.pos.host.iso8583.master;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.fss.pos.base.api.host.HostData;
import com.fss.pos.base.api.host.HostRequest;
import com.fss.pos.base.api.transactions.ModifyTransaction;
import com.fss.pos.base.api.transactions.Transactions;
import com.fss.pos.base.commons.Config;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.EmvTags;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.base.services.transactionlog.TransactionLogData;

@HostRequest(HostType = "18")
public class MasterRequestApi extends MasterApi {

	// private static final String DE23_DEFAULT = "000";

	@Autowired
	private Config config;

	@ModifyTransaction(value = Transactions.DEFAULT)
	public void modifyAll(IsoBuffer buffer, Data data) {
		HostData hostData = (HostData) data;

		buffer.putIfAbsent(Constants.DE12, hostData.getMerchantTime());
		buffer.putIfAbsent(Constants.DE13, hostData.getMerchantDate());
		buffer.put(Constants.DE18, hostData.getMcc());

		
		buffer.put(Constants.DE32, Util.appendChar(hostData.getAcqInstId(), '0', 6, true));

		// buffer.put(Constants.DE32, "019111");

		if (Constants.ENTRY_MODE_MAG_STRIPE.equals(buffer.get(Constants.DE22)
				.substring(0, 2)))
			buffer.put(Constants.DE22, "90"
					+ buffer.get(Constants.DE22).substring(2, 3));

	buffer.put(Constants.DE35, buffer.get(Constants.DE35).replace("=", "D"));
		
		buffer.put(Constants.DE42,
				Util.appendChar(hostData.getMerchantId(), ' ', 15, false));
		StringBuilder p43Data = new StringBuilder();

		// added by Yogita on 16/01
		if (hostData.getBusinessName().length() > 22)
			p43Data.append(Util.rightTrimChars(hostData.getBusinessName(), 22));
		else
			p43Data.append(Util.appendChar(hostData.getBusinessName(), ' ', 22,
					false));
		p43Data.append(" ");

		// Changed by purna for getting exact length data
		if (hostData.getMerchantAddress().length() > 13)
			p43Data.append(Util.rightTrimChars(hostData.getMerchantAddress(),
					13));
		else
			p43Data.append(Util.appendChar(hostData.getMerchantAddress(), ' ',
					13, false));
		p43Data.append(" ");
		p43Data.append(Util.appendChar(hostData.getCountryCode(), ' ', 3, false));
		buffer.put(Constants.DE43, p43Data.toString());

		if(hostData.getCurrencyCode().length() <= 2){
		buffer.put(Constants.DE49,
				Util.appendChar(hostData.getCurrencyCode(), '0', 3, true));
		}else{
			buffer.put(Constants.DE49,hostData.getCurrencyCode());
		}

		// buffer.put(Constants.DE33, "200561"); // as oman net //commented by
		// Rashmi on 01/11
		// buffer.put(Constants.DE33, "121771");
		buffer.put(Constants.DE33, hostData.getFiid()); // AUB
		buffer.put(Constants.DE48, "R");
		
		if(!buffer.get(Constants.DE22).equals(Constants.ENTRY_MODE_MANUAL)){
			StringBuilder de48 = new StringBuilder(buffer.get(Constants.DE48));
			de48.append("23"); // sublement id
			de48.append("02"); // changed by parandaman
			de48.append("00"); //card value
			buffer.put(Constants.DE48, de48.toString());
		}
		
		buffer.disableField(Constants.DE60);

		if (Constants.ZERO.equals(hostData.getTxnCommunicationFlow())) {
			StringBuilder msgType = new StringBuilder(
					buffer.get(Constants.ISO_MSG_TYPE));
			msgType.setCharAt(1, '1');
			buffer.put(Constants.ISO_MSG_TYPE, msgType.toString());
			if (msgType.charAt(2) == '2')
				buffer.put(Constants.DE60, "190");
		}

		buffer.put(
				Constants.DE61,
				getDE61(hostData.getCurrencyCode(), buffer.get(Constants.DE22)
						.substring(0, 2)));

		if (buffer.get(Constants.ISO_MSG_TYPE).equals("0100")) {
			StringBuilder p48 = new StringBuilder(buffer.get(Constants.DE48));
			p48.append("37"); // sublement id
			p48.append("15");
			p48.append("02"); // subfield id
			p48.append("11"); // acquirer id length
			p48.append(Util.appendChar(hostData.getAcqInstId(), '0', 11, true));
			buffer.put(Constants.DE48, p48.toString());
		}

		buffer.disableField(Constants.DE24);
		buffer.disableField(Constants.DE25);
		buffer.disableField(Constants.DE47);
		buffer.disableField(Constants.DE38);
		buffer.disableField(Constants.DE39);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE63);
		buffer.disableField(Constants.DE64);
		buffer.disableField(Constants.DE55);

	}

	private String getDE61(String currencyCode, String entryMode) {
		// Log.debug("Entry mode", entryMode);
		StringBuilder de61 = new StringBuilder("0");// pos terminal attendance
		de61.append("0");// RFU
		de61.append("0");// Terminal location
		de61.append("0");// cardholder presence
		de61.append("0");// card presence
		// card capture capabilties
		// de61.append("1");
		de61.append("0");// changed on 21-08 as per suman for bahrain
		de61.append("0");// POS txn status
		de61.append("0");// POS txn security
		de61.append("0");// RFU
		de61.append("0");// CAT Level

		// As Per Master Card We need to send always 3 only
		de61.append("3");// Card input capability
	//	de61.append("5"); temporary for certification
		/*
		 * if (Constants.ENTRY_MODE_ICC_CL.equals(entryMode) ||
		 * Constants.ENTRY_MODE_MAG_STRIPE_CL.equals(entryMode)) {
		 * de61.append("3");// Card input capability } else if
		 * (Constants.ENTRY_MODE_UNSPECIFIED.equals(entryMode)) {
		 * de61.append("0"); } else { de61.append("8"); }
		 */
		de61.append("00");// Auth life cycle
		if(currencyCode.length() <= 2){
			de61.append(Util.appendChar(currencyCode, '0', 3, true));// currency code
		}else {
			de61.append(currencyCode);
		}
		
		return de61.toString();
	}

	@ModifyTransaction(value = Transactions.PURCHASE)
	public void modifyPurchace(IsoBuffer buffer, Data data) {

		HostData hostData = (HostData) data;

		StringBuilder p48 = new StringBuilder(buffer.get(Constants.DE48));
		p48.append("610500001");
		buffer.put(Constants.DE48, p48.toString());

		if (!buffer.isFieldEmpty(Constants.DE6)
				&& !buffer.isFieldEmpty(Constants.DE51)) {

			StringBuilder de54 = new StringBuilder(buffer.get(Constants.DE3)
					.substring(2, 4)); // account type
			de54.append("58"); // POI Amt
			de54.append(buffer.get(Constants.DE49));
			de54.append("D");// ///added by prakash
			de54.append(buffer.get(Constants.DE4));
			buffer.put(Constants.DE54, de54.toString());

			buffer.put(Constants.DE4, buffer.get(Constants.DE6));
			buffer.put(Constants.DE49, buffer.get(Constants.DE51));

			/*
			 * p48.append("610500001"); p48.append("7806 Y    "); // subelement
			 * 1,3-6 contain space buffer.put(Constants.DE48, p48.toString());
			 */

			buffer.disableField(Constants.DE6);
			buffer.disableField(Constants.DE10);
			buffer.disableField(Constants.DE51);
		} else {
			if (!buffer.isFieldEmpty(Constants.DE54)) {
				StringBuilder de54 = new StringBuilder(buffer
						.get(Constants.DE3).substring(2, 4)); // account type
				de54.append("44"); // Amt Gratuity
				de54.append(Util.appendChar(hostData.getCurrencyCode(), '0', 3,
						true));
				// de54.append(hostData.getCurrencyCode()); //by Rashmi
				de54.append("D");
				de54.append(buffer.get(Constants.DE4));
				buffer.put(Constants.DE54, de54.toString());
			}

		}
		
		buffer.disableField(Constants.DE44);

	}
	
	@ModifyTransaction(value = Transactions.REFUND)
	public void modifyRefund(IsoBuffer buffer, Data data) {
		
		if (!buffer.isFieldEmpty(Constants.DE6)
				&& !buffer.isFieldEmpty(Constants.DE51)) {
			
			buffer.put(Constants.DE4, buffer.get(Constants.DE6));
			buffer.put(Constants.DE49, buffer.get(Constants.DE51));
			
			buffer.disableField(Constants.DE6);
			buffer.disableField(Constants.DE10);
			buffer.disableField(Constants.DE51);
		}
		
		buffer.disableField(Constants.DE44);
	}

	@ModifyTransaction(value = Transactions.CASH_ADVANCE)
	public void modifyCashAdvance(IsoBuffer buffer, Data data) {
		HostData hostData = (HostData) data;
		buffer.put(Constants.DE48, "C");
		buffer.put(Constants.DE3,
				"17" + buffer.get(Constants.DE3).substring(2, 6));

		// StringBuilder p48 = new StringBuilder(buffer.get(Constants.DE48));

		if (!buffer.isFieldEmpty(Constants.DE6)
				&& !buffer.isFieldEmpty(Constants.DE51)) {

			StringBuilder de54 = new StringBuilder(buffer.get(Constants.DE3)
					.substring(2, 4)); // account// type
			de54.append("58"); // POI Amt
			de54.append(buffer.get(Constants.DE49));
			de54.append("C");
			de54.append(buffer.get(Constants.DE4));
			buffer.put(Constants.DE54, de54.toString());

			buffer.put(Constants.DE4, buffer.get(Constants.DE6));
			buffer.put(Constants.DE49, buffer.get(Constants.DE51));

			// p48.append("7806 Y    "); // subelement 1,3-6 contain space

			buffer.disableField(Constants.DE6);
			buffer.disableField(Constants.DE10);
			buffer.disableField(Constants.DE51);
		} else {
			if (!buffer.isFieldEmpty(Constants.DE54)) {
				StringBuilder de54 = new StringBuilder(buffer
						.get(Constants.DE3).substring(2, 4)); // account type
				de54.append("40"); // amount type
				de54.append(hostData.getCurrencyCode());
				de54.append("C");
				de54.append(buffer.get(Constants.DE4));
				buffer.put(Constants.DE54, de54.toString());
			}
		}
		
		buffer.disableField(Constants.DE44);

	}

	@ModifyTransaction(value = Transactions.COMPLETION)
	public void modifyCompletion(IsoBuffer buffer, Data data) throws Exception {
		HostData hd = (HostData) data;
		if (null != hd.getOrgAuthCode()
				&& !Constants.ZERO.equals(hd.getOrgAuthCode()))
			buffer.put(Constants.DE38, hd.getOrgAuthCode());
		buffer.put(Constants.DE60, "191");
		
		buffer.disableField(Constants.DE44);
	}

	@ModifyTransaction(value = Transactions.BALANCE_INQUIRY)
	public void modifyBalanceInquiry(IsoBuffer buffer, Data data)
			throws Exception {
		buffer.put(Constants.DE3,
				"30" + buffer.get(Constants.DE3).substring(2, 6));
		
		buffer.disableField(Constants.DE44);
	}

	@ModifyTransaction(value = Transactions.PRE_AUTHORIZATION)
	public void modifyPreAuth(IsoBuffer buffer, Data data) {
		buffer.put(Constants.DE48, "H");
		StringBuilder p61 = new StringBuilder(buffer.get(Constants.DE61));
		p61.setCharAt(6, '4');// position 7 - pre authorized request
		buffer.put(Constants.DE61, p61.toString());
		buffer.put(Constants.DE3,
				"00" + buffer.get(Constants.DE3).substring(2, 6));
		StringBuilder p48 = new StringBuilder(buffer.get(Constants.DE48));
		p48.append("610500000");
		buffer.put(Constants.DE48, p48.toString());

		// dcc
		if (!buffer.isFieldEmpty(Constants.DE6)
				&& !buffer.isFieldEmpty(Constants.DE51)) {

			buffer.put(Constants.DE4, buffer.get(Constants.DE6));
			buffer.put(Constants.DE49, buffer.get(Constants.DE51));

			buffer.disableField(Constants.DE6);
			buffer.disableField(Constants.DE51);
		}
		buffer.disableField(Constants.DE44);
	}

	@ModifyTransaction(value = Transactions.TIP)
	public void modifyTipAdjustment(IsoBuffer buffer, Data data) {
		buffer.disableField(Constants.DE54);
		buffer.disableField(Constants.DE44);
		// HostData hd = (HostData) data;
		// StringBuilder s90 = new StringBuilder(hd.getOrgMessageType());
		// s90.append(hd.getOrgStan());
		// s90.append(hd.getOrgTransmissionDt());
		// s90.append(Util.appendChar(hd.getOrgAcquirerId(), '0', 11, true));
		// s90.append("00000000000");// FIID
		// buffer.put(Constants.DE90, s90.toString());
	}

	@ModifyTransaction(value = Transactions.VOID)
	public void modifyVoid(IsoBuffer buffer, Data data) {
		buffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_REV_400);
		buffer.put(Constants.DE3,
				"00" + buffer.get(Constants.DE3).substring(2, 6));
		buffer.disableField(Constants.DE54);
		buffer.put(Constants.DE39, "17");
		HostData hd = (HostData) data;
		StringBuilder s90 = new StringBuilder(hd.getOrgMessageType());
		s90.append(hd.getOrgStan());
		s90.append(hd.getOrgTransmissionDt());
		s90.append(Util.appendChar(hd.getOrgAcquirerId(), '0', 11, true));
		s90.append("00000000000");
		buffer.put(Constants.DE90, s90.toString());
		buffer.put(
				Constants.DE48,
				getReversalDE48(hd.getRespDe48(), hd.getRespDe63(),
						hd.getPinEnabledTxn()));

		// dcc
		if (!buffer.isFieldEmpty(Constants.DE6)
				&& !buffer.isFieldEmpty(Constants.DE51)) {
			buffer.put(Constants.DE4, buffer.get(Constants.DE6));
			buffer.put(Constants.DE49, buffer.get(Constants.DE51));

			buffer.disableField(Constants.DE6);
			buffer.disableField(Constants.DE10);
			buffer.disableField(Constants.DE51);
		}
		
		buffer.disableField(Constants.DE44);

	}

	@ModifyTransaction(value = Transactions.CASH_BACK)
	public void modifyCashback(IsoBuffer buffer, Data data) {
		HostData hd = (HostData) data;

		if (!buffer.isFieldEmpty(Constants.DE6)
				&& !buffer.isFieldEmpty(Constants.DE51)) {

			
			StringBuilder de54 = new StringBuilder(buffer
					.get(Constants.DE3).substring(2, 4)); // account type
			de54.append("58"); //POI amount
			de54.append(buffer.get(Constants.DE49)); //merchant currency
			de54.append("D"); 
			de54.append(buffer.get(Constants.DE4)); 
			de54.append(buffer
					.get(Constants.DE3).substring(2, 4));
			de54.append("40"); // amount type cashback
			de54.append(buffer.get(Constants.DE51)); //customer currency
			de54.append("D"); 
			de54.append(buffer.get(Constants.DE54)); //cashback amount
			buffer.put(Constants.DE54, de54.toString());
			
			buffer.put(Constants.DE4, buffer.get(Constants.DE6));
			buffer.put(Constants.DE49, buffer.get(Constants.DE51));
	
			buffer.disableField(Constants.DE6);
			buffer.disableField(Constants.DE10);
			buffer.disableField(Constants.DE51);

		} else {
			if (!buffer.isFieldEmpty(Constants.DE54)) {
				StringBuilder de54 = new StringBuilder(buffer
						.get(Constants.DE3).substring(2, 4)); // account type
				de54.append("40"); // amount type cashback
				// de54.append(hd.getCurrencyCode());
				de54.append(Util.appendChar(hd.getCurrencyCode(), '0', 3, true)); // RAshmi
																					// append
																					// 0
				de54.append("D"); // modified by yogita C to D 2/7/18
				de54.append(buffer.get(Constants.DE4));
				buffer.put(Constants.DE54, de54.toString());
			}
		}
		
		buffer.disableField(Constants.DE44);

	}

	@ModifyTransaction(value = Transactions.EMV_DEFAULT)
	public void modifyEmv(IsoBuffer buffer, Data data) {
		HostData hd = (HostData) data;
		Map<EmvTags, String> tvMap = hd.getEmvMap();

		if (tvMap.containsKey(EmvTags.TAG_5F34)) {
			buffer.put(Constants.DE23,
					Util.appendChar(tvMap.get(EmvTags.TAG_5F34), '0', 3, true));
		} else {
			buffer.disableField(Constants.DE23);
		}
		/*
		 * String de23 = tvMap.containsKey(EmvTags.TAG_5F34) ? Util.appendChar(
		 * tvMap.get(EmvTags.TAG_5F34), '0', 3, true) : DE23_DEFAULT;
		 */

		// if (!DE23_DEFAULT.equals(de23)) //Commented by Rashmi on 07/11/2018
		// buffer.put(Constants.DE23, de23);

		tvMap.remove(EmvTags.TAG_5F34);
		tvMap.remove(EmvTags.TAG_5A);
		tvMap.remove(EmvTags.TAG_57);
		tvMap.remove(EmvTags.TAG_5F24);
		tvMap.remove(EmvTags.TAG_9F08);

		if (Util.isNullOrEmpty(tvMap.get(EmvTags.TAG_84))) {
			tvMap.remove(EmvTags.TAG_84);
		}

		StringBuilder d = new StringBuilder();
		for (EmvTags tag : tvMap.keySet()) {
			d.append(tag.toString());
			String val = tvMap.get(tag);
			d.append(Util.appendChar(
					String.valueOf(Integer.toHexString(val.length() / 2)), '0',
					2, true));
			d.append(val);
		}

		buffer.put(Constants.DE55, d.toString());
		buffer.put(Constants.DE127, "0831200302000000006714");
	}

	@ModifyTransaction(value = Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer buffer, Data data) throws Exception {
		TransactionLogData trd = (TransactionLogData) data;
		buffer.put(Constants.DE39, "68"); // changed on 21-08

		if (null != trd.getAuthCode()
				&& !Constants.ZERO.equals(trd.getAuthCode()))
			buffer.put(Constants.DE38, trd.getAuthCode());

		StringBuilder s90 = new StringBuilder(trd.getOriginalMsgType());
		s90.append(trd.getOrgStan());
		s90.append(trd.getTransmissionDt());
		s90.append(Util.appendChar(trd.getAcqInstId(), '0', 11, true));
		s90.append("00000000000");
		buffer.put(Constants.DE90, s90.toString());

		buffer.putIfAbsent(Constants.DE12, trd.getMerchantTime());
		buffer.putIfAbsent(Constants.DE13, trd.getMerchantDate());
		buffer.put(Constants.DE18, trd.getMcc());
		buffer.put(Constants.DE32, trd.getAcqInstId());
		// buffer.put(Constants.DE32, "019111");
		// buffer.put(Constants.DE33, "200561");
		buffer.put(Constants.DE33, trd.getFiid()); // AUB
		buffer.put(Constants.DE3, trd.getOrgProcCode());//changed on 30-06
		/*
		 * StringBuilder p43Data = new StringBuilder();
		 * p43Data.append(Util.appendChar(trd.getBusinessName(), ' ', 22,
		 * false)); p43Data.append(" ");
		 * 
		 * if (trd.getMerchantAddress().length() > 13)
		 * p43Data.append(Util.rightTrimChars(trd.getMerchantAddress(), 13));
		 * else p43Data.append(Util.appendChar(trd.getMerchantAddress(), ' ',
		 * 13, false)); p43Data.append(" ");
		 * p43Data.append(Util.appendChar(trd.getCountryCode(), ' ', 3, false));
		 */
		
		buffer.put(Constants.DE42,
				Util.appendChar(trd.getMerchantId(), ' ', 15, false));
		buffer.put(Constants.DE43, trd.getReqDe43());
       
		buffer.put(Constants.DE49, trd.getCurrencyCode());

		buffer.put(
				Constants.DE61,
				getDE61(trd.getCurrencyCode(), buffer.get(Constants.DE22)
						.substring(0, 2)));

		if (!Util.isNullOrEmpty(trd.getFiid()))
			buffer.put(Constants.DE33, trd.getFiid());

		buffer.put(
				Constants.DE48,
				getReversalDE48(trd.getRespDe48(), trd.getRespDe63(),
						trd.getPinEnabledTxn()));

		buffer.disableField(Constants.DE54);
		buffer.disableField(Constants.DE24);
		buffer.disableField(Constants.DE63);
		buffer.disableField(Constants.DE64);
		buffer.disableField(Constants.DE60);
		buffer.disableField(Constants.DE52);
		buffer.disableField(Constants.DE47);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE35);
		buffer.disableField(Constants.DE25);
		buffer.disableField(Constants.DE55);
		buffer.disableField(Constants.DE44);

		// dcc

		if (!buffer.isFieldEmpty(Constants.DE6)
				&& !buffer.isFieldEmpty(Constants.DE51)) {

			buffer.put(Constants.DE4, buffer.get(Constants.DE6));
			buffer.put(Constants.DE49, buffer.get(Constants.DE51));

			buffer.disableField(Constants.DE6);
			buffer.disableField(Constants.DE10);
			buffer.disableField(Constants.DE51);

		}

	}

	private String getReversalDE48(String respDe48, String respDe63,
			String pinEnabledTxn) {
		StringBuilder sb = new StringBuilder();
		sb.append(respDe48 == null ? "R" : respDe48);
		sb.append("20");
		sb.append("01");
		if (pinEnabledTxn == null) {
			sb.append("P");
		} else {
			sb.append(Constants.ENABLE.equals(pinEnabledTxn) ? "P" : "S");
		}
		sb.append("63");
		sb.append("15");
		sb.append(respDe63 == null ? "000000000000000" : Util.appendChar(
				respDe63, ' ', 15, false));
		return sb.toString();
	}

}
