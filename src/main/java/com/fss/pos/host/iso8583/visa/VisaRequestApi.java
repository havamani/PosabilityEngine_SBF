package com.fss.pos.host.iso8583.visa;

import java.util.Collections;
import java.util.HashMap;
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
import com.fss.pos.base.commons.utils.PosUtil;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.base.services.transactionlog.TransactionLogData;

@HostRequest(HostType = "17")
public class VisaRequestApi extends VisaApi {

	private static final boolean EMV_TERTIARY_ENABLED = false;
	// private static final String DE100_DEFAULT = "00";
	private static final String EMV_DATASET_ID = "01";
	private static final String DE90_FIID_DEFAULT = "00000000000";

	private static final Map<EmvTags, String> EMV_TAG_FIELD_MAP;

	static {
		HashMap<EmvTags, String> tempMap = new HashMap<EmvTags, String>();
		tempMap.put(EmvTags.TAG_9F33, Constants.DE130);
		tempMap.put(EmvTags.TAG_95, Constants.DE131);
		tempMap.put(EmvTags.TAG_9F37, Constants.DE132);
		tempMap.put(EmvTags.TAG_9F10, Constants.DE134);
		tempMap.put(EmvTags.TAG_9F26, Constants.DE136);
		tempMap.put(EmvTags.TAG_9F36, Constants.DE137);
		tempMap.put(EmvTags.TAG_82, Constants.DE138);
		tempMap.put(EmvTags.TAG_91, Constants.DE140);
		tempMap.put(EmvTags.TAG_9C, Constants.DE144);
		tempMap.put(EmvTags.TAG_9F1A, Constants.DE145);
		tempMap.put(EmvTags.TAG_9A, Constants.DE146);
		tempMap.put(EmvTags.TAG_9F02, Constants.DE147);
		tempMap.put(EmvTags.TAG_5F2A, Constants.DE148);
		tempMap.put(EmvTags.TAG_9F03, Constants.DE149);
		tempMap.put(EmvTags.TAG_9F6E, Constants.DE150);//For DCC added
		tempMap.put(EmvTags.TAG_9F7C, Constants.DE151);//For DCC added
	
		EMV_TAG_FIELD_MAP = Collections.unmodifiableMap(tempMap);
	}

	@ModifyTransaction(value = Transactions.DEFAULT)
	public void modifyAll(IsoBuffer buffer, Data data) throws Exception {
		HostData hostData = (HostData) data;

		buffer.fillBuffer(false, true, true);

		IsoBuffer subBuffer = new IsoBuffer();
		subBuffer.fillBuffer(true, false, false);
		buffer.putBuffer(Constants.DE44, subBuffer);
		subBuffer = new IsoBuffer();
		subBuffer.fillBuffer(true, false, false);
		buffer.putBuffer(Constants.DE60, subBuffer);
		subBuffer = new IsoBuffer();
		subBuffer.fillBuffer(true, false, false);
		buffer.putBuffer(Constants.DE62, subBuffer);
		subBuffer = new IsoBuffer();
		subBuffer.fillBuffer(true, false, false);
		buffer.putBuffer(Constants.DE63, subBuffer);
		/*subBuffer = new IsoBuffer();
		subBuffer.fillBuffer(true, false, false);
		buffer.putBuffer(Constants.DE126, subBuffer);*/

		buffer.put(Constants.DE18, hostData.getMcc());
		buffer.put(Constants.DE32, hostData.getAcqInstId());

		if (Constants.ENTRY_MODE_ICC_TO_MAG_FALLBACK.equals(buffer.get(
				Constants.DE22).substring(0, 2))
				|| Constants.ENTRY_MODE_MAG_STRIPE.equals(buffer.get(
						Constants.DE22).substring(0, 2)))
			buffer.put(Constants.DE22, "90"
					+ buffer.get(Constants.DE22).substring(2, 3));

		buffer.put(Constants.DE42,
				Util.appendChar(hostData.getMerchantId(), ' ', 15, false));

		StringBuilder p43Data = new StringBuilder();
		p43Data.append(hostData.getBusinessName()).append(
				hostData.getMerchantAddress());
		
		StringBuilder de43 = new StringBuilder();
		de43.append((p43Data.toString().length() <= 38)?Util.appendChar(
				p43Data.toString(), ' ', 38, false) : p43Data.substring(0, 38)); 
		
		de43.append(hostData.getCountryCode().substring(0, 2));		
		buffer.put(Constants.DE43, de43.toString());
		
		String currencyCode = Util.appendChar(hostData.getCurrencyCode(), '0',
				3, true);
		buffer.put(Constants.DE19,
				Util.appendChar(hostData.getCurrencyCode(), '0', 4, true));
		buffer.put(Constants.DE49, currencyCode);

		buffer.put(Constants.DE22, buffer.get(Constants.DE22) + Constants.ZERO);

		String serviceCode = getServiceCodeUpdate(buffer.get(Constants.DE35));
		buffer.put(Constants.DE35, buffer.get(Constants.DE35).replace("=", "D"));

		if (Constants.ZERO.equals(hostData.getTxnCommunicationFlow())) {
			StringBuilder msgType = new StringBuilder(
					buffer.get(Constants.ISO_MSG_TYPE));
			msgType.setCharAt(1, '1');
			buffer.put(Constants.ISO_MSG_TYPE, msgType.toString());
			IsoBuffer tempBuffer = buffer.getBuffer(Constants.DE60);
			tempBuffer.put(Constants.DE1, "0");// Terminal Type - 0 not
												// specified
			tempBuffer.put(Constants.DE2, "5");// entry capability
			if (buffer.get(Constants.DE22).contains("90")) {
				if (serviceCode.substring(0, 1).equals("2")
						|| serviceCode.substring(0, 1).equals("6")) {
					tempBuffer.put(Constants.DE3, "1");// chip condition code
					tempBuffer.put(Constants.DE6, "0");// transaction indicator
				} else {
					tempBuffer.put(Constants.DE3, "0");// chip condition code
					tempBuffer.put(Constants.DE6, "0");// transaction indictor
				}
			} else {
				tempBuffer.put(Constants.DE3, "0");// chip condition code
				tempBuffer.put(Constants.DE6, "1");// transaction indicator
			}
			tempBuffer.put(Constants.DE4, "0");// special condition indicator
			tempBuffer.put(Constants.DE5, "00");// merchant group indicator
			tempBuffer.put(Constants.DE7, "0");// card auth reliability
												// indicator
			tempBuffer = buffer.getBuffer(Constants.DE62);
			tempBuffer.put(Constants.DE1, "Y"); // auth characteristics
												// Indicator
			// tempBuffer.put(Constants.DE2, "Y");
			// tempBuffer.put(Constants.DE23, "Y");
			tempBuffer = buffer.getBuffer(Constants.DE63);
			tempBuffer.put(Constants.DE1, "0002"); // Network Id
		}
		if (!buffer.isFieldEmpty(Constants.DE52))
			buffer.put(Constants.DE53, "2001010100000000");

		buffer.disableField(Constants.DE61);
		buffer.disableField(Constants.DE47);
		buffer.disableField(Constants.DE24);

		buffer.put(Constants.VISA_SRC_STATION, hostData.getSrcStationId());

		// de22 change on 12/12 visa certification
		if (!buffer.isFieldEmpty(Constants.DE52)) {
			String de22 = buffer.get(Constants.DE22);
			de22 = de22.substring(0, de22.length() - 2);
			de22 = de22.concat("1");
			buffer.put(Constants.DE22, de22 + Constants.ZERO);
		}
	}

	private String getServiceCodeUpdate(String value) {

		int i = value.contains("D") ? value.indexOf('D') + 1 + 4 : value
				.indexOf('=') + 1 + 4;
		if (i < value.length())
			return value.substring(i, i + 3);
		else
			return "";

	}

	@ModifyTransaction(value = Transactions.PURCHASE)
	public void modifyPurchace(IsoBuffer buffer, Data data) {

		HostData hostData = (HostData) data;

		// modifying visa on 12/06 by Yogita
		if (!buffer.isFieldEmpty(Constants.DE6)
				&& !buffer.isFieldEmpty(Constants.DE51)) {

			if (Integer.parseInt(hostData.getCardDecimal()) >= 3) {
				String amt = buffer.get(Constants.DE6);
				amt = amt.substring(0, amt.length() - 1);
				amt = amt.concat("0");
				buffer.put(Constants.DE6, amt);
			}

		} else {

			if (Integer.parseInt(hostData.getDecimalLength()) >= 3) {
				String amt = buffer.get(Constants.DE4);
				amt = amt.substring(0, amt.length() - 1);
				amt = amt.concat("0");
				buffer.put(Constants.DE4, amt);
			}

		}

		if (!buffer.isFieldEmpty(Constants.DE54)) {

			StringBuilder de54 = new StringBuilder(buffer.get(Constants.DE3)
					.substring(2, 4)); // account type
			de54.append("44"); // Amt Gratuity
			de54.append(Util.appendChar(hostData.getCurrencyCode(), '0', 3,
					true)); // RAshmi append 0
			de54.append("D");
			de54.append(buffer.get(Constants.DE4));
			buffer.put(Constants.DE54, de54.toString());
			IsoBuffer subBuffer = new IsoBuffer();
			subBuffer.fillBuffer(true, false, false);
			buffer.putBuffer(Constants.DE62, subBuffer);
			subBuffer.put(Constants.DE4, "H");
		}

		if (!buffer.isFieldEmpty(Constants.DE6)
				&& !buffer.isFieldEmpty(Constants.DE51)) {
			if (!buffer.isFieldEmpty(Constants.DE54)) {

				StringBuilder de54 = new StringBuilder(buffer
						.get(Constants.DE3).substring(2, 4)); // account type
				de54.append("44"); // Amt Gratuity
				de54.append(buffer.get(Constants.DE49)); // RAshmi append 0
				de54.append("D");
				de54.append(buffer.get(Constants.DE4));
				buffer.put(Constants.DE54, de54.toString());

				IsoBuffer subBuffer = new IsoBuffer();
				subBuffer.fillBuffer(true, false, false);
				buffer.putBuffer(Constants.DE62, subBuffer);
				subBuffer.put(Constants.DE4, "H");
			}

			buffer.put(Constants.DE4, buffer.get(Constants.DE6));
			buffer.put(Constants.DE49, buffer.get(Constants.DE51));

			buffer.disableField(Constants.DE6);
			buffer.disableField(Constants.DE10);
			buffer.disableField(Constants.DE51);

			IsoBuffer subBuffer = new IsoBuffer();
			subBuffer = new IsoBuffer();
			subBuffer.fillBuffer(true, false, false);
			buffer.putBuffer(Constants.DE126, subBuffer);
			
			IsoBuffer tempBuffer = buffer.getBuffer(Constants.DE126);
			tempBuffer.put(Constants.DE19, "1");// dcc bit enabled

		}

		buffer.disableField(Constants.DE44);

	}

	@ModifyTransaction(value = Transactions.REFUND)
	public void modifyRefund(IsoBuffer buffer, Data data) {

		HostData hostData = (HostData) data;

		// modifying visa on 12/06 by Yogita
		if (!buffer.isFieldEmpty(Constants.DE6)
				&& !buffer.isFieldEmpty(Constants.DE51)) {

			if (Integer.parseInt(hostData.getCardDecimal()) >= 3) {
				String amt = buffer.get(Constants.DE6);
				amt = amt.substring(0, amt.length() - 1);
				amt = amt.concat("0");
				buffer.put(Constants.DE6, amt);
			}

		} else {

			if (Integer.parseInt(hostData.getDecimalLength()) >= 3) {
				String amt = buffer.get(Constants.DE4);
				amt = amt.substring(0, amt.length() - 1);
				amt = amt.concat("0");
				buffer.put(Constants.DE4, amt);
			}

		}

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

	@ModifyTransaction(value = Transactions.TIP)
	public void modifyTipAdjustment(IsoBuffer buffer, Data data) {
		buffer.disableField(Constants.DE54);
		IsoBuffer tempBuffer = buffer.getBuffer(Constants.DE63);
		tempBuffer.put(Constants.DE1, "0002"); // Network Id
		tempBuffer.put(Constants.DE2, "0002"); // Preauth Time
		tempBuffer.put(Constants.DE3, "2502"); // Message Reason Code

		buffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_AUTH_ADVICE);

		HostData hd = (HostData) data;
		StringBuilder s90 = new StringBuilder(hd.getOrgMessageType());
		s90.append(hd.getOrgStan());
		s90.append(hd.getOrgTransmissionDt());
		s90.append(Util.appendChar(hd.getOrgAcquirerId(), '0', 11, true));
		s90.append("00000000000");// FIID
		buffer.put(Constants.DE90, s90.toString());
		buffer.put(Constants.DE39, Constants.SUCCESS);

		// dcc
		if (!buffer.isFieldEmpty(Constants.DE6)
				&& !buffer.isFieldEmpty(Constants.DE51)) {

			buffer.put(Constants.DE4, buffer.get(Constants.DE6));
			buffer.put(Constants.DE49, buffer.get(Constants.DE51));

			buffer.disableField(Constants.DE6);
			buffer.disableField(Constants.DE10);
			buffer.disableField(Constants.DE51);

			IsoBuffer subBuffer = new IsoBuffer();
			subBuffer = new IsoBuffer();
			subBuffer.fillBuffer(true, false, false);
			buffer.putBuffer(Constants.DE126, subBuffer);
			tempBuffer = buffer.getBuffer(Constants.DE126);
			tempBuffer.put(Constants.DE19, "1"); // dcc bit enabled
		}
		buffer.disableField(Constants.DE44);
	}

	@ModifyTransaction(value = Transactions.VOID)
	public void modifyVoid(IsoBuffer buffer, Data data) {

		HostData hd = (HostData) data;

		buffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_REV_400);
		buffer.put(Constants.DE3, hd.getOrgProcCode());
		// buffer.disableField(Constants.DE54);
		// buffer.put(Constants.DE22, "0110");
		IsoBuffer tempBuffer = buffer.getBuffer(Constants.DE63);
		tempBuffer.put(Constants.DE1, "0002"); // Network Id
		tempBuffer.put(Constants.DE3, "2501"); // Message Reason Code
		buffer.put(Constants.DE3,
				"00" + buffer.get(Constants.DE3).substring(2, 6));
		
		//only for FSSNET DCC///s///
		
		//buffer.put(Constants.DE11, hd.getOrgStan());  //-- - VISADCC Added
		buffer.put(Constants.DE12, hd.getMerchantTime());//--- VISADCC Added
		buffer.put(Constants.DE13, hd.getMerchantDate());//--- VISADCC Added
		buffer.put(Constants.DE61, buffer.get(Constants.DE54));//VISADCC -Added

		//////////////////////////

		StringBuilder s90 = new StringBuilder(hd.getOrgMessageType());
		s90.append(hd.getOrgStan());
		s90.append(hd.getOrgTransmissionDt());
		s90.append(Util.appendChar(hd.getOrgAcquirerId(), '0', 11, true));
		s90.append("00000000000");// FIID
		buffer.put(Constants.DE90, s90.toString());

		Map<EmvTags, String> tvMap = hd.getEmvMap();
		emvDetails(tvMap, buffer);

		if (null != hd.getOrgAuthCode()
				&& !Constants.ZERO.equals(hd.getOrgAuthCode()))
			buffer.put(Constants.DE38, hd.getOrgAuthCode());

		// buffer.put(Constants.DE39, Constants.SUCCESS);
		buffer.disableField(Constants.DE39);

		IsoBuffer subBuffer = new IsoBuffer();
		subBuffer.fillBuffer(true, false, false);
		buffer.putBuffer(Constants.DE60, subBuffer);
		subBuffer = new IsoBuffer();
		subBuffer.fillBuffer(true, false, false);

		// modifying visa on 13/06 by Yogita
		if (null == hd.getCardDecimal()) {

			if (Integer.parseInt(hd.getDecimalLength()) >= 3) {
				String amt = buffer.get(Constants.DE4);
				amt = amt.substring(0, amt.length() - 1);
				amt = amt.concat("0");
				buffer.put(Constants.DE4, amt);
			}

		} else if (null == hd.getDecimalLength()) {

			if (Integer.parseInt(hd.getCardDecimal()) >= 3) {
				String amt = buffer.get(Constants.DE4);
				amt = amt.substring(0, amt.length() - 1);
				amt = amt.concat("0");
				buffer.put(Constants.DE4, amt);
			}

		}

		if (!buffer.isFieldEmpty(Constants.DE54)) {
			StringBuilder de54 = new StringBuilder(buffer.get(Constants.DE3)
					.substring(2, 4)); // account type
			de54.append("44"); // Amt Gratuity
			// de54.append(hd.getCurrencyCode());
			de54.append(Util.appendChar(hd.getCurrencyCode(), '0', 3, true)); // RAshmi
																				// append
																				// 0
			de54.append("D");
			de54.append(buffer.get(Constants.DE4));
			buffer.put(Constants.DE54, de54.toString());
			buffer.putBuffer(Constants.DE62, subBuffer);
			subBuffer.put(Constants.DE4, "H");
		}

		if (!buffer.isFieldEmpty(Constants.DE6)
				&& !buffer.isFieldEmpty(Constants.DE51)) {

			if (!buffer.isFieldEmpty(Constants.DE54)) {

				StringBuilder de54 = new StringBuilder(buffer
						.get(Constants.DE3).substring(2, 4)); // account type
				de54.append("44"); // Amt Gratuity
				// de54.append(hd.getCurrencyCode());
				de54.append(buffer.get(Constants.DE49)); // RAshmi // 0
				de54.append("D");
				de54.append(buffer.get(Constants.DE4));
				buffer.put(Constants.DE54, de54.toString());
				buffer.putBuffer(Constants.DE62, subBuffer);
				subBuffer.put(Constants.DE4, "H");

			}

			buffer.put(Constants.DE4, buffer.get(Constants.DE6));
			buffer.put(Constants.DE49, buffer.get(Constants.DE51));

			//IsoBuffer subBuffer = new IsoBuffer();
			subBuffer = new IsoBuffer();
			subBuffer.fillBuffer(true, false, false);
			buffer.putBuffer(Constants.DE126, subBuffer);
			tempBuffer = buffer.getBuffer(Constants.DE126);
			tempBuffer.put(Constants.DE19, "1");// dcc bit enabled

			buffer.disableField(Constants.DE6);
			buffer.disableField(Constants.DE10);
			buffer.disableField(Constants.DE51);
		}

		String s = hd.getReqDe62();
		Map<String, String> p62Data = getP62(s);

		tempBuffer = buffer.getBuffer(Constants.DE62);
		for (Map.Entry<String, String> entry : p62Data.entrySet()) {
			tempBuffer.put(entry.getKey(), entry.getValue());
		}

		tempBuffer = buffer.getBuffer(Constants.DE60);
		tempBuffer.put(Constants.DE1, "0");// Terminal Type - 0 not
											// specified
		tempBuffer.put(Constants.DE2, "5");// entry capability

		buffer.put(Constants.DE22, hd.getReqDe22());
		//buffer.disableField(Constants.DE12); by parandaman
		//buffer.disableField(Constants.DE13); by parandaman
		buffer.disableField(Constants.DE44);

	}

	private Map<String, String> getP62(String s) {
		s = s.replace("{", "");
		s = s.replace("}", "");
		Map<String, String> p62 = new HashMap<String, String>();
		if (!s.isEmpty()) {
			String req62[] = s.split(",");
			String g;
			for (int i = 0; i < req62.length; i++) {
				g = req62[i];
				String[] a = g.split("=");
				p62.put(a[0], a[1]);
			}
		}
		return p62;

	}

	@ModifyTransaction(value = Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer buffer, Data data) throws Exception {
		TransactionLogData trd = (TransactionLogData) data;

		String de63Value = buffer.get(Constants.DE63);

		IsoBuffer subBuffer = new IsoBuffer();
		subBuffer.fillBuffer(true, false, false);
		buffer.putBuffer(Constants.DE44, subBuffer);
		subBuffer = new IsoBuffer();
		subBuffer.fillBuffer(true, false, false);
		buffer.putBuffer(Constants.DE60, subBuffer);
		subBuffer = new IsoBuffer();
		subBuffer.fillBuffer(true, false, false);
		buffer.putBuffer(Constants.DE62, subBuffer);
		subBuffer = new IsoBuffer();
		subBuffer.fillBuffer(true, false, false);
		buffer.putBuffer(Constants.DE63, subBuffer);
		/*subBuffer = new IsoBuffer();
		subBuffer.fillBuffer(true, false, false);
		buffer.putBuffer(Constants.DE126, subBuffer);*/

		// buffer.put(Constants.DE39, Constants.SUCCESS);
		buffer.disableField(Constants.DE39);

		// modifying visa on 13/06 by Yogita
		if (null == trd.getCardDecimal()) {

			if (Integer.parseInt(trd.getDecimalLength()) >= 3) {
				String amt = buffer.get(Constants.DE4);
				amt = amt.substring(0, amt.length() - 1);
				amt = amt.concat("0");
				buffer.put(Constants.DE4, amt);
			}

		} else if (null == trd.getDecimalLength()) {

			if (Integer.parseInt(trd.getCardDecimal()) >= 3) {
				String amt = buffer.get(Constants.DE4);
				amt = amt.substring(0, amt.length() - 1);
				amt = amt.concat("0");
				buffer.put(Constants.DE4, amt);
			}

		}
		
		

		if (null != trd.getAuthCode()
				&& !Constants.ZERO.equals(trd.getAuthCode()))
			buffer.put(Constants.DE38, trd.getAuthCode());

		StringBuilder s90 = new StringBuilder(trd.getOriginalMsgType());
		s90.append(buffer.get(Constants.DE11));
		s90.append(trd.getTransmissionDt());
		s90.append(Util.appendChar(trd.getAcqInstId(), '0', 11, true));
		s90.append(DE90_FIID_DEFAULT);
		buffer.put(Constants.DE90, s90.toString());

		buffer.put(Constants.DE18, trd.getMcc());
		buffer.put(Constants.DE3, trd.getOrgProcCode());
		
		// Only for FSSNET DCC ///////////

		buffer.put(Constants.DE11, trd.getOrgStan());  //--Para - VISADCC
		buffer.put(Constants.DE12, trd.getMerchantTime());//--Para - VISADCC
		buffer.put(Constants.DE13, trd.getMerchantDate());//--Para - VISADCC
		
		///////////////////////////////////

		/*
		 * String p43Data = trd.getBusinessName() + trd.getMerchantAddress(); if
		 * (p43Data.length() <= 38) p43Data =
		 * Util.appendChar(p43Data.toString(), ' ', 38, false); else p43Data =
		 * p43Data.substring(0, 38); p43Data +=
		 * (trd.getCountryCode().substring(0, 2));
		 */
		buffer.put(Constants.DE42,
				Util.appendChar(trd.getMerchantId(), ' ', 15, false));
		buffer.put(Constants.DE43, trd.getReqDe43());
		String currencyCode = Util.appendChar(trd.getCurrencyCode(), '0', 3,
				true);
		buffer.put(Constants.DE19,
				Util.appendChar(trd.getCurrencyCode(), '0', 4, true));
		buffer.put(Constants.DE49, currencyCode);
		buffer.put(Constants.DE22, buffer.get(Constants.DE22) + Constants.ZERO);
		buffer.put(Constants.DE32, trd.getAcqInstId());

		if (trd.getRespDe62() != null && !"0".equals(trd.getRespDe62())) {
			Map<String, String> de62 = PosUtil.parseBuffer(trd.getRespDe62());
			IsoBuffer buffer62 = buffer.getBuffer(Constants.DE62);
			for (String k : de62.keySet()) {
				/*
				 * if (k.equals(Constants.DE2)) { buffer62.put(k,
				 * Util.appendChar(de62.get(k), '0', 16, true)); }
				 */
				buffer62.put(k, de62.get(k));

			}
		}
		subBuffer = new IsoBuffer();
		subBuffer.fillBuffer(true, false, false);

		if (!buffer.isFieldEmpty(Constants.DE54)) {

			StringBuilder de54 = new StringBuilder(buffer.get(Constants.DE3)
					.substring(2, 4)); // account type
			de54.append("44"); // Amt Gratuity
			// de54.append(trd.getCurrencyCode());
			de54.append(Util.appendChar(trd.getCurrencyCode(), '0', 3, true)); // RAshmi
																				// append
																				// 0
			de54.append("D");
			de54.append(buffer.get(Constants.DE4));
			buffer.put(Constants.DE54, de54.toString());
		}

		if (!buffer.isFieldEmpty(Constants.DE6)
				&& !buffer.isFieldEmpty(Constants.DE51)) {
			if (!buffer.isFieldEmpty(Constants.DE54)) {

				StringBuilder de54 = new StringBuilder(buffer
						.get(Constants.DE3).substring(2, 4)); // account type
				de54.append("44"); // Amt Gratuity
				// de54.append(trd.getCurrencyCode());
				de54.append(buffer.get(Constants.DE49)); // RAshmi append 0
				de54.append("D");
				de54.append(buffer.get(Constants.DE4));
				buffer.put(Constants.DE54, de54.toString());
			}

			buffer.put(Constants.DE4, buffer.get(Constants.DE6));
			buffer.put(Constants.DE49, buffer.get(Constants.DE51));

			//IsoBuffer subBuffer = new IsoBuffer();
			subBuffer = new IsoBuffer();
			subBuffer.fillBuffer(true, false, false);
			buffer.putBuffer(Constants.DE126, subBuffer);
			IsoBuffer tempBuffer = buffer.getBuffer(Constants.DE126);
			tempBuffer.put(Constants.DE19, "1");// dcc bit enabled

			buffer.disableField(Constants.DE6);
			buffer.disableField(Constants.DE10);
			buffer.disableField(Constants.DE51);

		}

		buffer.disableField(Constants.DE52);
		buffer.disableField(Constants.DE47);
		buffer.disableField(Constants.DE61);
		buffer.disableField(Constants.DE35);
		// buffer.disableField(Constants.DE60);

		/***
		 * Vijayarumugam K For Adding the reversal message type
		 */
		buffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_REV_400);

		IsoBuffer tempBuffer = buffer.getBuffer(Constants.DE63);
		tempBuffer.put(Constants.DE1, "0002"); // Network Id
		tempBuffer.put(Constants.DE3, "2502"); // Message Reason Code

		// Log.debug("DE63 val", de63Value);
		if ("00".equals(de63Value)) {
			// timeout
			buffer.disableField(Constants.DE55);
			tempBuffer.put(Constants.DE3, "2502"); // Message Reason Code
		} else if ("01".equals(de63Value)) {
			tempBuffer.put(Constants.DE3, "2503");
			// card declined
			Map<EmvTags, String> tvMap = trd.getEmvMap();
			emvDetails(tvMap, buffer);
		} else {
			buffer.disableField(Constants.DE55);
		}

		tempBuffer = buffer.getBuffer(Constants.DE60);
		tempBuffer.put(Constants.DE1, "0");// Terminal Type - 0 not
											// specified
		tempBuffer.put(Constants.DE2, "5");// entry capability

		buffer.put(Constants.VISA_SRC_STATION, trd.getSrcStationId());
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

		if (EMV_TERTIARY_ENABLED) {
			for (EmvTags tag : EMV_TAG_FIELD_MAP.keySet()) {
				String val = tvMap.get(tag);
				if (EmvTags.TAG_9F1A.equals(tag)
						|| EmvTags.TAG_5F2A.equals(tag)) {
					
					buffer.put(EMV_TAG_FIELD_MAP.get(tag), val == null ? "*"
							: (Constants.ZERO + val));
					continue;
				}
				buffer.put(EMV_TAG_FIELD_MAP.get(tag), val == null ? "*" : val);
			}
			buffer.disableField(Constants.DE55);
		} else {

			if (Util.isNullOrEmpty(tvMap.get(EmvTags.TAG_9F34))) {
				tvMap.remove(EmvTags.TAG_9F34);
			}
			if (Util.isNullOrEmpty(tvMap.get(EmvTags.TAG_9F03))) {
				tvMap.remove(EmvTags.TAG_9F03);
			}
			/*
			 * contactless changes on 07/01
			 * 
			 * if (Util.isNullOrEmpty(tvMap.get(EmvTags.TAG_9F6E))) {
			 * tvMap.remove(EmvTags.TAG_9F6E); }
			 */

			String entryMode = buffer.get(Constants.DE22).substring(0, 2);
			if (Constants.ENTRY_MODE_ICC_CL.equals(entryMode)
					|| Constants.ENTRY_MODE_MAG_STRIPE_CL.equals(entryMode)) {
				tvMap.remove(EmvTags.TAG_57);
				tvMap.remove(EmvTags.TAG_9F1E);
			}

			String cardSeqNo = tvMap.get(EmvTags.TAG_5F34);
			if (Util.isNullOrEmpty(cardSeqNo)) {
				tvMap.remove(EmvTags.TAG_5F34);
			} else {
				buffer.put(Constants.DE23,
						Util.appendChar(cardSeqNo, '0', 4, true));
			}

			// Log.debug("Visa EMV", tvMap.toString());
			StringBuilder d = new StringBuilder();
			for (EmvTags tag : tvMap.keySet()) {
				String val = tvMap.get(tag);
				d.append(tag.toString());
				d.append(Util.appendChar(
						String.valueOf(Integer.toHexString(val.length() / 2)),
						'0', 2, true));
				d.append(val);
			}

			String emvDataAscii = IsoUtil.hex2AsciiChar(d.toString());
			int emvLen = emvDataAscii.length();
			String emvHexLen = Util.appendChar(Integer.toHexString(emvLen),
					'0', 4, true);

			StringBuilder emvDataBuffer = new StringBuilder();
			emvDataBuffer.append(EMV_DATASET_ID);
			emvDataBuffer.append(emvHexLen);
			emvDataBuffer.append(IsoUtil.asciiChar2hex(emvDataAscii));
			buffer.put(Constants.DE55, emvDataBuffer.toString());
		}

	}

	@ModifyTransaction(value = Transactions.PRE_AUTHORIZATION)
	public void modifyPreAuth(IsoBuffer buffer, Data data) throws Exception {

		HostData hostData = (HostData) data;

		// modifying visa on 12/06 by Yogita
		if (!buffer.isFieldEmpty(Constants.DE6)
				&& !buffer.isFieldEmpty(Constants.DE51)) {

			if (Integer.parseInt(hostData.getCardDecimal()) >= 3) {
				String amt = buffer.get(Constants.DE6);
				amt = amt.substring(0, amt.length() - 1);
				amt = amt.concat("0");
				buffer.put(Constants.DE6, amt);
			}

		} else {

			if (Integer.parseInt(hostData.getDecimalLength()) >= 3) {
				String amt = buffer.get(Constants.DE4);
				amt = amt.substring(0, amt.length() - 1);
				amt = amt.concat("0");
				buffer.put(Constants.DE4, amt);
			}

		}

		// dcc
		if (!buffer.isFieldEmpty(Constants.DE6)
				&& !buffer.isFieldEmpty(Constants.DE51)) {

			buffer.put(Constants.DE4, buffer.get(Constants.DE6));
			buffer.put(Constants.DE49, buffer.get(Constants.DE51));

			buffer.disableField(Constants.DE6);
			buffer.disableField(Constants.DE10);
			buffer.disableField(Constants.DE51);

			IsoBuffer subBuffer = new IsoBuffer();
			subBuffer = new IsoBuffer();
			subBuffer.fillBuffer(true, false, false);
			buffer.putBuffer(Constants.DE126, subBuffer);
			IsoBuffer tempBuffer = buffer.getBuffer(Constants.DE126);
			tempBuffer.put(Constants.DE19, "1");// dcc bit enabled
		}

		buffer.put(Constants.DE3,
				"00" + buffer.get(Constants.DE3).substring(2, 6));
		IsoBuffer tempBuffer = buffer.getBuffer(Constants.DE63);
		tempBuffer.put(Constants.DE2, "0002"); // Preauth Time

		buffer.disableField(Constants.DE44);
	}

	@ModifyTransaction(value = Transactions.COMPLETION)
	public void modifyCompletion(IsoBuffer buffer, Data data) throws Exception {
		buffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_AUTH_ADVICE);
		HostData hd = (HostData) data;
		if (null != hd.getOrgAuthCode()
				&& !Constants.ZERO.equals(hd.getOrgAuthCode()))
			buffer.put(Constants.DE38, hd.getOrgAuthCode());
		StringBuilder s90 = new StringBuilder(hd.getOrgMessageType());
		s90.append(hd.getOrgStan());
		s90.append(hd.getOrgTransmissionDt());
		s90.append(Util.appendChar(hd.getOrgAcquirerId(), '0', 11, true));
		s90.append("00000000000");// FIID

		buffer.put(Constants.DE90, s90.toString());
		IsoBuffer tempBuffer = buffer.getBuffer(Constants.DE63);
		tempBuffer.put(Constants.DE2, "0002"); // Preauth Time
		tempBuffer.put(Constants.DE3, "2104");
		buffer.disableField(Constants.DE44);
	}

	@ModifyTransaction(value = Transactions.BALANCE_INQUIRY)
	public void modifyBalanceInquiry(IsoBuffer buffer, Data data)
			throws Exception {
		buffer.put(Constants.DE3,
				"30" + buffer.get(Constants.DE3).substring(2, 6));
		buffer.disableField(Constants.DE44);
	}

	@ModifyTransaction(value = Transactions.CASH_BACK)
	public void modifyCashback(IsoBuffer buffer, Data data) {
		HostData hd = (HostData) data;
		buffer.put(Constants.DE3,
				"01" + buffer.get(Constants.DE3).substring(2, 6));

		if (!buffer.isFieldEmpty(Constants.DE6)
				&& !buffer.isFieldEmpty(Constants.DE51)) {
			StringBuilder de54 = new StringBuilder(buffer.get(Constants.DE3)
					.substring(2, 4)); // account type
			de54.append("40"); // Amt Gratuity
			de54.append(buffer.get(Constants.DE49)); // RAshmi append 0
			de54.append("C");
			de54.append(buffer.get(Constants.DE4));
			buffer.put(Constants.DE54, de54.toString());

			buffer.put(Constants.DE4, buffer.get(Constants.DE6));
			buffer.put(Constants.DE49, buffer.get(Constants.DE51));

			buffer.disableField(Constants.DE6);
			buffer.disableField(Constants.DE10);
			buffer.disableField(Constants.DE51);

			IsoBuffer subBuffer = new IsoBuffer();
			subBuffer = new IsoBuffer();
			subBuffer.fillBuffer(true, false, false);
			buffer.putBuffer(Constants.DE126, subBuffer);
			IsoBuffer tempBuffer = buffer.getBuffer(Constants.DE126);
			tempBuffer.put(Constants.DE19, "1");// dcc bit enabled
		} else {

			StringBuilder de54 = new StringBuilder(buffer.get(Constants.DE3)
					.substring(2, 4)); // account type
			de54.append("40"); // amount type
			// de54.append(hd.getCurrencyCode());
			de54.append(Util.appendChar(hd.getCurrencyCode(), '0', 3, true)); // RAshmi
																				// append
																				// 0
			de54.append("C");
			de54.append(buffer.get(Constants.DE4));
			buffer.put(Constants.DE54, de54.toString());

		}

		buffer.disableField(Constants.DE44);
	}
	
	@ModifyTransaction(value = Transactions.CASH_ADVANCE)
	public void modifyCashAdvance(IsoBuffer buffer, Data data) {
		
		HostData hostData = (HostData) data;
		if (!buffer.isFieldEmpty(Constants.DE6)
				&& !buffer.isFieldEmpty(Constants.DE51)) {

			StringBuilder de54 = new StringBuilder(buffer.get(Constants.DE3)
					.substring(2, 4)); // account// type
			de54.append("40"); // POI Amt
			de54.append(buffer.get(Constants.DE49));
			de54.append("C");
			de54.append(buffer.get(Constants.DE4));
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
				de54.append("40"); // amount type
				de54.append(hostData.getCurrencyCode());
				de54.append("C");
				de54.append(buffer.get(Constants.DE4));
				buffer.put(Constants.DE54, de54.toString());
			}
		}
		
		buffer.disableField(Constants.DE44);
	}
}
