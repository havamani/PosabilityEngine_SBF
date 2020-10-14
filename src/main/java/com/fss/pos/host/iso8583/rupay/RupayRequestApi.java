package com.fss.pos.host.iso8583.rupay;

/**
 * @author Paranthamanv
 *
 */
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.fss.pos.base.api.client.Validator;
import com.fss.pos.base.api.host.HostData;
import com.fss.pos.base.api.host.HostRequest;
import com.fss.pos.base.api.transactions.ModifyTransaction;
import com.fss.pos.base.api.transactions.Transactions;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.EmvTags;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.base.services.transactionlog.TransactionLogData;

@HostRequest(HostType = "19")
public class RupayRequestApi extends RupayApi { // 720428

	private static final List<EmvTags> SUPPORTED_TAGS;
	private static final String fiid = "00000000000";
	private static final String TLE_UKPT = "03";
	private static final String JSON_SOURCE_OF_FUND = "sourceOfFund";
	private static final String JSON_REVERSAL_TYPE = "reversalType";
	// private static final String TLE_DUKPT = "05";

	static {
		SUPPORTED_TAGS = new ArrayList<EmvTags>();
		SUPPORTED_TAGS.add(EmvTags.TAG_82);
		SUPPORTED_TAGS.add(EmvTags.TAG_95);
		SUPPORTED_TAGS.add(EmvTags.TAG_9A);
		SUPPORTED_TAGS.add(EmvTags.TAG_9C);
		SUPPORTED_TAGS.add(EmvTags.TAG_5F2A);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F02);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F03);// C
		SUPPORTED_TAGS.add(EmvTags.TAG_9F09);// O
		SUPPORTED_TAGS.add(EmvTags.TAG_9F10);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F1A);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F1E);// O
		SUPPORTED_TAGS.add(EmvTags.TAG_9F26);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F27);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F33);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F34);// O
		SUPPORTED_TAGS.add(EmvTags.TAG_9F35);// O
		SUPPORTED_TAGS.add(EmvTags.TAG_9F36);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F37);
		SUPPORTED_TAGS.add(EmvTags.TAG_84);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F06);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F07);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F41);
		// SUPPORTED_TAGS.add(EmvTags.TAG_5F34);
		SUPPORTED_TAGS.add(EmvTags.TAG_4F);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F5B);
	}

	@ModifyTransaction(value = Transactions.DEFAULT)
	public void modifyAll(IsoBuffer buffer, Data data) {

		HostData hostData = (HostData) data;
		buffer.put(Constants.DE7, hostData.getTxnDateTime());
		buffer.put(Constants.DE12, hostData.getMerchantTime());
		buffer.put(Constants.DE18, hostData.getMcc());
		buffer.put(Constants.DE19, hostData.getCurrencyCode());
		buffer.put(Constants.DE32, hostData.getAcqInstId());

		// buffer.put(Constants.DE37, hostData.getRrn());
		buffer.put(Constants.DE37,
				Util.appendChar(hostData.getRrn(), '0', 12, true));
		String p43Data = Util.appendChar(hostData.getBusinessName(), ' ', 23,
				false)
				+ Util.appendChar(hostData.getCity(), ' ', 13, false)
				+ Util.appendChar(hostData.getState(), ' ', 2, false)
				// + hostData.getCurrencyCodeA().substring(0, 2);
				+ hostData.getCurrencyName().substring(0, 2);
		buffer.put(Constants.DE43, p43Data);
		if (Constants.ZERO.equals(hostData.getTxnCommunicationFlow())) {
			buffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_AUTH);
		}
	}

	@ModifyTransaction(value = Transactions.PURCHASE)
	public void modifyPurchase(IsoBuffer buffer, Data data)
			throws ParseException {

		HostData hostData = (HostData) data;
		String serviceCode = RupayIsoUtil.getServiceCodeUpdate(buffer
				.get(Constants.DE35));
		buffer.put(Constants.DE40, serviceCode);

		String p48Data = TLE_UKPT.equalsIgnoreCase("03") ? (buffer.get(
				Constants.DE22).substring(0, 2)
				.equalsIgnoreCase(Constants.ENTRY_MODE_ICC_CL)) ? "051005POS0107800203"
				: "051005POS0107800203"
				: "051005POS01";

		if (!p48Data.equals(Constants.DISABLED_FIELD)) {
			buffer.put(Constants.DE48, p48Data);
		}

		/*
		 * if (TLE_UKPT.equalsIgnoreCase("03")) { buffer.put(Constants.DE48,
		 * "051005POS0107800203"); // buffer.put(Constants.DE48,
		 * "051005POS0107800203083001S"); } else { buffer.put(Constants.DE48,
		 * "051005POS01"); }
		 */

		buffer.put(Constants.DE49, hostData.getCurrencyCode());
		// String field8 = Constants.DE61_SF8;

		StringBuffer field61 = new StringBuffer();

		String field1 = RupayIsoUtil.getBuildP61_1(buffer);
		String field2 = RupayIsoUtil.getBuildP61_2(buffer);
		String field7 = RupayIsoUtil.getBuildP61_7(buffer);
		String field9 = RupayIsoUtil.getBuildP61_9(buffer);
		String field10 = RupayIsoUtil.getBuildP61_10(buffer);
		field61.append(field1);// 1
		field61.append(field2);// 2
		field61.append("1");// 3
		field61.append("1");// 4- changes for MPOS
		// field61.append("7");// 4
		field61.append("1");// 5
		field61.append("2");// 6
		field61.append(field7);// 7

		Map<EmvTags, String> tvMap = hostData.getEmvMap();
		// Log.debug("Constants.DE52 ", buffer.get(Constants.DE52));
		if (!buffer.get(Constants.DE52).isEmpty()
				&& !buffer.get(Constants.DE52).equals("*")) {
			String field8 = "2";
			field61.append(field8);// 8
		} else {
			if (!tvMap.get(EmvTags.TAG_9F34).isEmpty()) {
				// Log.debug("EMV Map SubString ", tvMap.get(EmvTags.TAG_9F34)
				// .substring(0, 2));
				if (tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
						.equalsIgnoreCase("5E")
						|| tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
								.equalsIgnoreCase("1E")) {
					Log.debug("Constants.DE61_SF8 ", Constants.DE61_SF8);
					// buffer.put(Constants.DE61_SF8, "3");
					String field8 = Constants.DE61_SF8;
					field61.append(field8);// 8

				} else if (tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
						.equalsIgnoreCase("41")
						|| tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
								.equalsIgnoreCase("01")) {
					// Log.debug("Offline Pin ", tvMap.get(EmvTags.TAG_9F34));
					String field8 = "2";
					field61.append(field8);// 8

				} else {
					String field8 = "1";
					field61.append(field8);// 8
					// Log.debug("TAG 9F34 ", tvMap.get(EmvTags.TAG_9F34));

				}
			} else {
				// Log.debug(" unknown ", tvMap.get(EmvTags.TAG_9F34));
				String field8 = "1";
				field61.append(field8);// 8
			}
		}

		field61.append(field9);// 9
		field61.append(field10);// 10
		field61.append("3");// 11
		field61.append("9");// 12
		field61.append(Util.appendChar(hostData.getZipCode(), '0', 9, true));// 13

		if (hostData.getBusinessName().length() <= 20) {
			field61.append(Util.appendChar(hostData.getBusinessName(), ' ', 20,
					false));// 14
		} else {
			field61.append(hostData.getBusinessName().substring(0, 20));
		}

		// field61.append(Util.appendChar(hostData.getBusinessName(), ' ', 20,
		// false));// 14
		// Log.debug("Feild 61", field61.toString());
		buffer.put(Constants.DE61, field61.toString());

		buffer.disableField(Constants.DE14);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE47);
		buffer.disableField(Constants.DE54);
	}

	@SuppressWarnings("unused")
	@ModifyTransaction(value = Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer buffer, Data data)
			throws JSONException {

		TransactionLogData hostData = (TransactionLogData) data;
		buffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_REV_420);// 0200
		buffer.put(Constants.DE7, hostData.getTxnDateTime());
		buffer.put(Constants.DE11, hostData.getOrgStan());
		buffer.put(Constants.DE12, hostData.getOrgTransmissionDY());
		buffer.put(Constants.DE13, hostData.getMerchantDate());
		buffer.put(Constants.DE18, hostData.getMcc());
		buffer.put(Constants.DE19, hostData.getCurrencyCode());
		buffer.put(Constants.DE32, hostData.getAcqInstId());
		// buffer.put(Constants.DE37, hostData.getRrn());
		buffer.put(Constants.DE37,
				Util.appendChar(hostData.getRrn(), '0', 12, true));
		String p43Data = Util.appendChar(hostData.getBusinessName(), ' ', 23,
				false)
				+ Util.appendChar(hostData.getCity(), ' ', 13, false)
				+ Util.appendChar(hostData.getStateCode(), ' ', 2, false)
				+ hostData.getIsoCountryCode();
		buffer.put(Constants.DE43, p43Data);
		String p3Data = buffer.get(Constants.DE3).substring(0, 2);
		Log.debug("p3Data ", p3Data);
		if (null != hostData.getOrgAuthCode()
				&& !Constants.ZERO.equals(hostData.getOrgAuthCode())) {
			// if(!buffer.get(Constants.DE39).equals("E2")){

			AdditionalDataInfo adinfo = new AdditionalDataInfo();
			// Added by Senthil
			if (!buffer.isFieldEmpty(Constants.DE48)) {
				if (Validator.isJSONValid(buffer.get(Constants.DE48))) {

					JSONObject json = new JSONObject(buffer.get(Constants.DE48));

					adinfo.setReversalType((String) json
							.get(JSON_REVERSAL_TYPE));
					buffer.put(Constants.DE38, hostData.getOrgAuthCode());
					buffer.put(
							Constants.DE39,
							(Constants.DE39 == "00") ? "22"
									: (p3Data == "22") ? "17" : adinfo
											.getReversalType());

					// Log.debug("DE 38 -1 ", hostData.getOrgAuthCode());
					// Log.debug("DE 39 -1 ", buffer.get(Constants.DE39));

					// Log.debug("DE 38 -1 ", hostData.getOrgAuthCode());
					// Log.debug("DE 39 -1 ", buffer.get(Constants.DE39));

				} else {
					buffer.put(Constants.DE38, hostData.getOrgAuthCode());
					buffer.put(
							Constants.DE39,
							(p3Data == "22") ? "17"
									: buffer.get(Constants.DE39).isEmpty()
											|| buffer.get(Constants.DE39) == null
											|| buffer.get(Constants.DE39)
													.equals("*") ? "22"
											: buffer.get(Constants.DE39));

					// Log.debug("DE 38 -2 ", hostData.getOrgAuthCode());
					// Log.debug("DE 39 -2 ", buffer.get(Constants.DE39));

				}
			} else {
				// Log.debug("DE 38 -3 ", hostData.getOrgAuthCode());

				buffer.put(Constants.DE38, hostData.getOrgAuthCode());
				buffer.put(
						Constants.DE39,
						(p3Data == "22") ? "17"
								: buffer.get(Constants.DE39).isEmpty()
										|| buffer.get(Constants.DE39) == null
										|| buffer.get(Constants.DE39).equals(
												"*") ? "22" : buffer
										.get(Constants.DE39));
				// Log.debug("DE 39 -3 ", buffer.get(Constants.DE39));
			}

		} else {
			buffer.put(Constants.DE39, "68");
		}
		buffer.put(Constants.DE49, hostData.getCurrencyCode());
		String p22Data = buffer.get(Constants.DE22).substring(0, 2);
		if (p22Data.equals(Constants.ENTRY_MODE_ICC_CB)
				|| p22Data.equals(Constants.ENTRY_MODE_ICC_CL)) {
			modifyEmv(buffer, data);
		}

		StringBuilder de54;
		if (p3Data.equalsIgnoreCase("09")) {
			de54 = new StringBuilder();
			de54.append("90");
			de54.append("90"); // POI Amt
			de54.append(buffer.get(Constants.DE49));
			de54.append("D");
			de54.append(buffer.get(Constants.DE54));
			buffer.put(Constants.DE54, de54.toString());
		}

		StringBuilder s90 = new StringBuilder(hostData.getOriginalMsgType());
		s90.append(hostData.getOrgStan());
		s90.append(hostData.getOrgTransmissionDt());
		s90.append(Util.appendChar(hostData.getAcqInstId(), '0', 11, true));
		s90.append(Util.appendChar(fiid, '0', 11, true)); // 38210048
		buffer.put(Constants.DE90, s90.toString());

		buffer.disableField(Constants.DE14);
		buffer.disableField(Constants.DE35);
		buffer.disableField(Constants.DE61);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE63);
		buffer.disableField(Constants.DE47);
		buffer.disableField(Constants.DE102);

	}

	@ModifyTransaction(value = Transactions.VOID)
	// In RUPAY,Void same as REVERSAL
	public void modifyVoid(IsoBuffer buffer, Data data) {

		HostData hostData = (HostData) data;

		buffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_REV_420);
		buffer.put(Constants.DE3, hostData.getOrgProcCode());
		buffer.put(Constants.DE11, hostData.getOrgStan());
		buffer.put(Constants.DE12, hostData.getOrgTransmissionDY());
		buffer.put(Constants.DE13, hostData.getMerchantDate());
		buffer.put(Constants.DE22, hostData.getReqDe22());
		buffer.put(Constants.DE38, hostData.getOrgAuthCode());
		buffer.put(Constants.DE39, "17");
		buffer.put(Constants.DE49, hostData.getCurrencyCode());

		StringBuilder de54;
		if (buffer.get(Constants.DE3).substring(0, 2).equalsIgnoreCase("09")) {
			de54 = new StringBuilder();
			de54.append("90");
			de54.append("90"); // POI Amt
			de54.append(buffer.get(Constants.DE49));
			de54.append("D");
			de54.append(buffer.get(Constants.DE54));
			buffer.put(Constants.DE54, de54.toString());
		}
		if (buffer.get(Constants.DE22).substring(0, 2).toString()
				.equals(Constants.ENTRY_MODE_ICC_CB)) {
			buffer.put(Constants.DE55, hostData.getReqDe55());
			buffer.put(Constants.DE23, hostData.getReqDe23());
		}

		StringBuilder s90 = new StringBuilder(hostData.getOrgMessageType());
		s90.append(hostData.getOrgStan());
		s90.append(hostData.getOrgTransmissionDt());
		s90.append(Util.appendChar(hostData.getAcqInstId(), '0', 11, true));
		s90.append(Util.appendChar(fiid, '0', 11, true));
		buffer.put(Constants.DE90, s90.toString());

		buffer.disableField(Constants.DE14);
		buffer.disableField(Constants.DE35);
		buffer.disableField(Constants.DE61);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE63);
		buffer.disableField(Constants.DE47);
		buffer.disableField(Constants.DE60);
	}

	@ModifyTransaction(value = Transactions.CASH_BACK)
	public void modifyPurchase_Cashback(IsoBuffer buffer, Data data) {

		HostData hostData = (HostData) data;
		String serviceCode = RupayIsoUtil.getServiceCodeUpdate(buffer
				.get(Constants.DE35));
		buffer.put(Constants.DE40, serviceCode);

		String p48Data = TLE_UKPT.equalsIgnoreCase("03") ? (buffer.get(
				Constants.DE22).substring(0, 2)
				.equalsIgnoreCase(Constants.ENTRY_MODE_ICC_CL)) ? "*"
				: "051005POS0107800203" : "051005POS01";

		if (!p48Data.equals(Constants.DISABLED_FIELD)) {
			buffer.put(Constants.DE48, p48Data);
		}

		/*
		 * if (TLE_UKPT.equalsIgnoreCase("03")) { buffer.put(Constants.DE48,
		 * "051005POS0107800203"); // buffer.put(Constants.DE48,
		 * "051005POS0107800203083001S"); } else { buffer.put(Constants.DE48,
		 * "051005POS01"); }
		 */
		buffer.put(Constants.DE49, hostData.getCurrencyCode());

		StringBuilder de54;
		if (buffer.get(Constants.DE3).substring(0, 2).equalsIgnoreCase("09")) {
			de54 = new StringBuilder();
			de54.append("90");
		} else {
			de54 = new StringBuilder(buffer.get(Constants.DE3).substring(2, 4));
		}
		de54.append("90"); // POI Amt
		de54.append(buffer.get(Constants.DE49));
		de54.append("D");
		de54.append(buffer.get(Constants.DE54));
		buffer.put(Constants.DE54, de54.toString());

		StringBuffer field61 = new StringBuffer();
		String field1 = RupayIsoUtil.getBuildP61_1(buffer);
		String field2 = RupayIsoUtil.getBuildP61_2(buffer);
		String field7 = RupayIsoUtil.getBuildP61_7(buffer);
		// String field8 = RupayIsoUtil.getBuildP61_8(buffer);

		String field9 = RupayIsoUtil.getBuildP61_9(buffer);
		String field10 = RupayIsoUtil.getBuildP61_10(buffer);
		field61.append(field1);// 1
		field61.append(field2);// 2
		field61.append("1");// 3
		field61.append("1");// 4- changes for MPOS
		// field61.append("7");// 4
		field61.append("1");// 5
		field61.append("2");// 6
		field61.append(field7);// 7

		Map<EmvTags, String> tvMap = hostData.getEmvMap();
		// Log.debug("Constants.DE52 ", buffer.get(Constants.DE52));
		if (!buffer.get(Constants.DE52).isEmpty()
				&& !buffer.get(Constants.DE52).equals("*")) {
			String field8 = "2";
			field61.append(field8);// 8
		} else {
			if (!tvMap.get(EmvTags.TAG_9F34).isEmpty()) {
				// Log.debug("EMV Map SubString ", tvMap.get(EmvTags.TAG_9F34)
				// .substring(0, 2));
				if (tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
						.equalsIgnoreCase("5E")
						|| tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
								.equalsIgnoreCase("1E")) {
					// Log.debug("Constants.DE61_SF8 ", Constants.DE61_SF8);
					// buffer.put(Constants.DE61_SF8, "3");
					String field8 = Constants.DE61_SF8;
					field61.append(field8);// 8

				} else if (tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
						.equalsIgnoreCase("41")
						|| tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
								.equalsIgnoreCase("01")) {
					// Log.debug("Offline Pin ", tvMap.get(EmvTags.TAG_9F34));
					String field8 = "2";
					field61.append(field8);// 8

				} else {
					// Log.debug("TAG 9F34 ", tvMap.get(EmvTags.TAG_9F34));
					String field8 = "1";
					field61.append(field8);// 8

				}
			} else {
				// Log.debug("unknown ", tvMap.get(EmvTags.TAG_9F34));
				String field8 = "1";
				field61.append(field8);// 8
			}
		}
		// field61.append(field8);// 8
		field61.append(field9);// 9
		field61.append(field10);// 10
		field61.append("3");// 11
		field61.append("9");// 12
		field61.append(Util.appendChar(hostData.getZipCode(), '0', 9, true));// 13
		if (hostData.getBusinessName().length() <= 20) {
			field61.append(Util.appendChar(hostData.getBusinessName(), ' ', 20,
					false));// 14
		} else {
			field61.append(hostData.getBusinessName().substring(0, 20));
			// field61.append(Util.appendChar(hostData.getBusinessName(), ' ',
			// 20,
			// false));// 14
		}
		Log.debug("Feild 61", field61.toString());
		buffer.put(Constants.DE61, field61.toString());

		buffer.disableField(Constants.DE14);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE47);

	}

	@ModifyTransaction(value = Transactions.CASH_ADVANCE)
	public void modifyCashatPOS(IsoBuffer buffer, Data data) {
		HostData hostData = (HostData) data;
		String serviceCode = RupayIsoUtil.getServiceCodeUpdate(buffer
				.get(Constants.DE35));
		buffer.put(Constants.DE40, serviceCode);

		String p48Data = TLE_UKPT.equalsIgnoreCase("03") ? (buffer.get(
				Constants.DE22).substring(0, 2)
				.equalsIgnoreCase(Constants.ENTRY_MODE_ICC_CL)) ? "*"
				: "051005POS0107800203" : "051005POS01";

		if (!p48Data.equals(Constants.DISABLED_FIELD)) {
			buffer.put(Constants.DE48, p48Data);
		}

		/*
		 * if (TLE_UKPT.equalsIgnoreCase("03")) { buffer.put(Constants.DE48,
		 * "051005POS0107800203"); // buffer.put(Constants.DE48,
		 * "051005POS0107800203083001S"); } else { buffer.put(Constants.DE48,
		 * "051005POS01"); }
		 */

		buffer.put(Constants.DE49, hostData.getCurrencyCode());

		StringBuffer field61 = new StringBuffer();
		String field1 = RupayIsoUtil.getBuildP61_1(buffer);
		String field2 = RupayIsoUtil.getBuildP61_2(buffer);
		String field7 = RupayIsoUtil.getBuildP61_7(buffer);

		// String field8 = RupayIsoUtil.getBuildP61_8(buffer);
		String field9 = RupayIsoUtil.getBuildP61_9(buffer);
		String field10 = RupayIsoUtil.getBuildP61_10(buffer);
		field61.append(field1);// 1
		field61.append(field2);// 2
		field61.append("1");// 3
		field61.append("1");// 4- changes for MPOS
		// field61.append("7");// 4
		field61.append("1");// 5
		field61.append("2");// 6
		field61.append(field7);// 7
		Map<EmvTags, String> tvMap = hostData.getEmvMap();
		// Log.debug("Constants.DE52 ", buffer.get(Constants.DE52));
		if (!buffer.get(Constants.DE52).isEmpty()
				&& !buffer.get(Constants.DE52).equals("*")) {
			String field8 = "2";
			field61.append(field8);// 8
		} else {
			if (!tvMap.get(EmvTags.TAG_9F34).isEmpty()) {
				// Log.debug("EMV Map SubString ", tvMap.get(EmvTags.TAG_9F34)
				// .substring(0, 2));
				if (tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
						.equalsIgnoreCase("5E")
						|| tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
								.equalsIgnoreCase("1E")) {
					// Log.debug("Constants.DE61_SF8 ", Constants.DE61_SF8);
					// buffer.put(Constants.DE61_SF8, "3");
					String field8 = Constants.DE61_SF8;
					field61.append(field8);// 8

				} else if (tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
						.equalsIgnoreCase("41")
						|| tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
								.equalsIgnoreCase("01")) {
					// Log.debug("Offline Pin ", tvMap.get(EmvTags.TAG_9F34));
					String field8 = "2";
					field61.append(field8);// 8

				} else {
					// Log.debug("TAG 9F34 ", tvMap.get(EmvTags.TAG_9F34));
					String field8 = "1";
					field61.append(field8);// 8

				}
			} else {
				// Log.debug("Unknown ", tvMap.get(EmvTags.TAG_9F34));
				String field8 = "1";
				field61.append(field8);// 8
			}
		}
		// field61.append(field8);// 8
		field61.append(field9);// 9
		field61.append(field10);// 10
		field61.append("3");// 11
		field61.append("9");// 12
		field61.append(Util.appendChar(hostData.getZipCode(), '0', 9, true));// 13
		if (hostData.getBusinessName().length() <= 20) {
			field61.append(Util.appendChar(hostData.getBusinessName(), ' ', 20,
					false));// 14
		} else {
			field61.append(hostData.getBusinessName().substring(0, 20));
		}
		// field61.append(Util.appendChar(hostData.getBusinessName(), ' ', 20,
		// false));// 14
		Log.debug(Constants.DE61, field61.toString());
		buffer.put(Constants.DE61, field61.toString());

		buffer.disableField(Constants.DE14);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE47);
		buffer.disableField(Constants.DE54);
	}

	@ModifyTransaction(value = Transactions.BALANCE_INQUIRY)
	public void modifyBalanceEnquiry(IsoBuffer buffer, Data data) {
		HostData hostData = (HostData) data;

		String serviceCode = RupayIsoUtil.getServiceCodeUpdate(buffer
				.get(Constants.DE35));

		buffer.put(Constants.DE40, serviceCode);

		String p48Data = TLE_UKPT.equalsIgnoreCase("03") ? (buffer.get(
				Constants.DE22).substring(0, 2)
				.equalsIgnoreCase(Constants.ENTRY_MODE_ICC_CL)) ? "*"
				: "051005POS0107800203" : "051005POS01";

		if (!p48Data.equals(Constants.DISABLED_FIELD)) {
			buffer.put(Constants.DE48, p48Data);
		}

		buffer.put(Constants.DE49, hostData.getCurrencyCode());

		StringBuffer field61 = new StringBuffer();
		String field1 = RupayIsoUtil.getBuildP61_1(buffer);
		String field2 = RupayIsoUtil.getBuildP61_2(buffer);
		String field7 = RupayIsoUtil.getBuildP61_7(buffer);

		// String field8 = RupayIsoUtil.getBuildP61_8(buffer);
		String field9 = RupayIsoUtil.getBuildP61_9(buffer);
		String field10 = RupayIsoUtil.getBuildP61_10(buffer);
		field61.append(field1);// 1
		field61.append(field2);// 2
		field61.append("1");// 3
		field61.append("1");// 4- changes for MPOS
		// field61.append("7");// 4
		field61.append("1");// 5
		field61.append("2");// 6
		field61.append(field7);// 7
		Map<EmvTags, String> tvMap = hostData.getEmvMap();
		// Log.debug("Constants.DE52 ", buffer.get(Constants.DE52));
		if (!buffer.get(Constants.DE52).isEmpty()
				&& !buffer.get(Constants.DE52).equals("*")) {
			String field8 = "2";
			field61.append(field8);// 8
		} else {
			if (!tvMap.get(EmvTags.TAG_9F34).isEmpty()) {
				// Log.debug("EMV Map SubString ", tvMap.get(EmvTags.TAG_9F34)
				// .substring(0, 2));
				if (tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
						.equalsIgnoreCase("5E")
						|| tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
								.equalsIgnoreCase("1E")) {
					// Log.debug("Constants.DE61_SF8 ", Constants.DE61_SF8);
					// buffer.put(Constants.DE61_SF8, "3");
					String field8 = Constants.DE61_SF8;
					field61.append(field8);// 8

				} else if (tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
						.equalsIgnoreCase("41")
						|| tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
								.equalsIgnoreCase("01")) {
					// Log.debug("Offline Pin ", tvMap.get(EmvTags.TAG_9F34));
					String field8 = "2";
					field61.append(field8);// 8

				} else {
					// Log.debug("TAG 9F34 ", tvMap.get(EmvTags.TAG_9F34));
					String field8 = "1";
					field61.append(field8);// 8

				}
			} else {
				// Log.debug("unknown ", tvMap.get(EmvTags.TAG_9F34));
				String field8 = "1";
				field61.append(field8);// 8
			}
		}
		// field61.append(field8);// 8
		field61.append(field9);// 9
		field61.append(field10);// 10
		field61.append("3");// 11
		field61.append("9");// 12
		field61.append(Util.appendChar(hostData.getZipCode(), '0', 9, true));// 13
		if (hostData.getBusinessName().length() <= 20) {
			field61.append(Util.appendChar(hostData.getBusinessName(), ' ', 20,
					false));// 14
		} else {
			field61.append(hostData.getBusinessName().substring(0, 20));
		}
		// field61.append(Util.appendChar(hostData.getBusinessName(), ' ', 20,
		// false));// 14
		Log.debug("Feild 61", field61.toString());
		buffer.put(Constants.DE61, field61.toString());

		buffer.disableField(Constants.DE14);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE47);
		buffer.disableField(Constants.DE54);
	}

	@ModifyTransaction(value = Transactions.LOAD_MONEY)
	public void modifyLoadMoney(IsoBuffer buffer, Data data)
			throws JSONException {
		HostData hostData = (HostData) data;
		String serviceCode = RupayIsoUtil.getServiceCodeUpdate(buffer
				.get(Constants.DE35));

		buffer.put(Constants.DE40, serviceCode);
		String p48Data = null;
		QSPARC qsparc = new QSPARC();

		if (!buffer.isFieldEmpty(Constants.DE48)) {
			if (Validator.isJSONValid(buffer.get(Constants.DE48))) {

				JSONObject json = new JSONObject(buffer.get(Constants.DE48));
				JSONObject childjson = (JSONObject) json.get("qSPARC");
				qsparc.setSourceOfFund((String) childjson
						.get(JSON_SOURCE_OF_FUND));

				p48Data = TLE_UKPT.equalsIgnoreCase("03") ? (buffer.get(
						Constants.DE22).substring(0, 2)
						.equalsIgnoreCase(Constants.ENTRY_MODE_ICC_CL))
						|| (buffer.get(Constants.DE22).substring(0, 2)
								.equalsIgnoreCase(Constants.ENTRY_MODE_ICC_CB)) ? "051005POS01078002030790040000082002"
						+ qsparc.getSourceOfFund()
						: "051005POS0107800203079004000008200201"
						: "051005POS01";

			} else {
				p48Data = TLE_UKPT.equalsIgnoreCase("03") ? (buffer.get(
						Constants.DE22).substring(0, 2)
						.equalsIgnoreCase(Constants.ENTRY_MODE_ICC_CL))
						|| (buffer.get(Constants.DE22).substring(0, 2)
								.equalsIgnoreCase(Constants.ENTRY_MODE_ICC_CB)) ? "051005POS0107800203079004000008200201"
						: "051005POS0107800203079004000008200201"
						: "051005POS01";
			}
		}

		if (!p48Data.equals(Constants.DISABLED_FIELD)) {
			buffer.put(Constants.DE48, p48Data);
		}

		/*
		 * if (TLE_UKPT.equalsIgnoreCase("03")) { buffer.put(Constants.DE48,
		 * "051005POS0107800203"); // buffer.put(Constants.DE48,
		 * "051005POS0107800203083001S"); } else { buffer.put(Constants.DE48,
		 * "051005POS01"); }
		 */

		buffer.put(Constants.DE49, hostData.getCurrencyCode());

		StringBuffer field61 = new StringBuffer();
		String field1 = RupayIsoUtil.getBuildP61_1(buffer);
		String field2 = RupayIsoUtil.getBuildP61_2(buffer);
		String field7 = RupayIsoUtil.getBuildP61_7(buffer);

		// String field8 = RupayIsoUtil.getBuildP61_8(buffer);
		String field9 = RupayIsoUtil.getBuildP61_9(buffer);
		String field10 = RupayIsoUtil.getBuildP61_10(buffer);
		field61.append(field1);// 1
		field61.append(field2);// 2
		field61.append("1");// 3
		field61.append("1");// 4- changes for MPOS
		// field61.append("7");// 4
		field61.append("1");// 5
		field61.append("2");// 6
		field61.append(field7);// 7
		Map<EmvTags, String> tvMap = hostData.getEmvMap();
		// Log.debug("Constants.DE52 ", buffer.get(Constants.DE52));
		if (!buffer.get(Constants.DE52).isEmpty()
				&& !buffer.get(Constants.DE52).equals("*")) {
			String field8 = "2";
			field61.append(field8);// 8
		} else {
			if (!tvMap.get(EmvTags.TAG_9F34).isEmpty()) {
				// Log.debug("EMV Map SubString ", tvMap.get(EmvTags.TAG_9F34)
				// .substring(0, 2));
				if (tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
						.equalsIgnoreCase("5E")
						|| tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
								.equalsIgnoreCase("1E")) {
					// Log.debug("Constants.DE61_SF8 ", Constants.DE61_SF8);
					// buffer.put(Constants.DE61_SF8, "3");
					String field8 = Constants.DE61_SF8;
					field61.append(field8);// 8

				} else if (tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
						.equalsIgnoreCase("41")
						|| tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
								.equalsIgnoreCase("01")) {
					// Log.debug("Offline Pin ", tvMap.get(EmvTags.TAG_9F34));
					String field8 = "2";
					field61.append(field8);// 8

				} else {
					// Log.debug("TAG 9F34 ", tvMap.get(EmvTags.TAG_9F34));
					String field8 = "1";
					field61.append(field8);// 8

				}
			} else {
				// Log.debug("Unknown ", tvMap.get(EmvTags.TAG_9F34));
				String field8 = "1";
				field61.append(field8);// 8
			}
		}
		// field61.append(field8);// 8
		field61.append(field9);// 9
		field61.append(field10);// 10
		field61.append("3");// 11
		field61.append("9");// 12
		field61.append(Util.appendChar(hostData.getZipCode(), '0', 9, true));// 13
		if (hostData.getBusinessName().length() <= 20) {
			field61.append(Util.appendChar(hostData.getBusinessName(), ' ', 20,
					false));// 14
		} else {
			field61.append(hostData.getBusinessName().substring(0, 20));
		}
		// field61.append(Util.appendChar(hostData.getBusinessName(), ' ', 20,
		// false));// 14
		Log.debug(Constants.DE61, field61.toString());
		buffer.put(Constants.DE61, field61.toString());

		buffer.disableField(Constants.DE14);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE47);
		buffer.disableField(Constants.DE54);
	}

	@ModifyTransaction(value = Transactions.BALANCE_UPDATE)
	public void modifybalanceUpdate(IsoBuffer buffer, Data data)
			throws JSONException {
		HostData hostData = (HostData) data;
		String serviceCode = RupayIsoUtil.getServiceCodeUpdate(buffer
				.get(Constants.DE35));

		buffer.put(Constants.DE40, serviceCode);
		String p48Data = null;
		QSPARC qsparc = new QSPARC();

		if (!buffer.isFieldEmpty(Constants.DE48)) {

			if (Validator.isJSONValid(buffer.get(Constants.DE48))) {

				JSONObject json = new JSONObject(buffer.get(Constants.DE48));

				JSONObject childjson = (JSONObject) json.get("qSPARC");
				qsparc.setSourceOfFund((String) childjson
						.get(JSON_SOURCE_OF_FUND));

				p48Data = "051005POS0107800203082002"
						+ qsparc.getSourceOfFund();

			} else {
				p48Data = TLE_UKPT.equalsIgnoreCase("03") ? (buffer.get(
						Constants.DE22).substring(0, 2)
						.equalsIgnoreCase(Constants.ENTRY_MODE_ICC_CL)) ? "051005POS010780020308200201"
						: "051005POS010780020308200201"
						: "051005POS01";
			}
		}

		/*
		 * if (TLE_UKPT.equalsIgnoreCase("03")) { buffer.put(Constants.DE48,
		 * "051005POS0107800203"); // buffer.put(Constants.DE48,
		 * "051005POS0107800203083001S"); } else { buffer.put(Constants.DE48,
		 * "051005POS01"); }
		 */
		if (!p48Data.equals(Constants.DISABLED_FIELD)) {
			buffer.put(Constants.DE48, p48Data);
		}
		buffer.put(Constants.DE49, hostData.getCurrencyCode());

		StringBuffer field61 = new StringBuffer();
		String field1 = RupayIsoUtil.getBuildP61_1(buffer);
		// String field2 = RupayIsoUtil.getBuildP61_2(buffer);
		String field7 = RupayIsoUtil.getBuildP61_7(buffer);

		// String field8 = RupayIsoUtil.getBuildP61_8(buffer);
		String field9 = RupayIsoUtil.getBuildP61_9(buffer);
		String field10 = RupayIsoUtil.getBuildP61_10(buffer);
		field61.append(field1);// 1
		field61.append("2");// 2
		field61.append("1");// 3
		field61.append("1");// 4- changes for MPOS
		// field61.append("7");// 4
		field61.append("1");// 5
		field61.append("2");// 6
		field61.append(field7);// 7
		field61.append("2");// 8
		field61.append(field9);// 9
		field61.append(field10);// 10
		field61.append("3");// 11
		field61.append("9");// 12
		field61.append(Util.appendChar(hostData.getZipCode(), '0', 9, true));// 13
		if (hostData.getBusinessName().length() <= 20) {
			field61.append(Util.appendChar(hostData.getBusinessName(), ' ', 20,
					false));// 14
		} else {
			field61.append(hostData.getBusinessName().substring(0, 20));
		}
		// field61.append(Util.appendChar(hostData.getBusinessName(), ' ', 20,
		// false));// 14
		Log.debug("Feild 61", field61.toString());
		buffer.put(Constants.DE61, field61.toString());

		buffer.disableField(Constants.DE14);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE47);
		buffer.disableField(Constants.DE54);
	}

	@ModifyTransaction(value = Transactions.SERVICE_CREATION)
	public void modifyServiceCreation(IsoBuffer buffer, Data data) {
		HostData hostData = (HostData) data;
		String serviceCode = RupayIsoUtil.getServiceCodeUpdate(buffer
				.get(Constants.DE35));
		buffer.put(Constants.DE40, serviceCode);

		String p48Data = TLE_UKPT.equalsIgnoreCase("03") ? (buffer.get(
				Constants.DE22).substring(0, 2)
				.equalsIgnoreCase(Constants.ENTRY_MODE_ICC_CL)) ? "051005POS01078002030790040019"
				: "051005POS01078002030790040019"
				: "051005POS01";

		if (!p48Data.equals(Constants.DISABLED_FIELD)) {
			buffer.put(Constants.DE48, p48Data);
		}

		/*
		 * if (TLE_UKPT.equalsIgnoreCase("03")) { buffer.put(Constants.DE48,
		 * "051005POS0107800203"); // buffer.put(Constants.DE48,
		 * "051005POS0107800203083001S"); } else { buffer.put(Constants.DE48,
		 * "051005POS01"); }
		 */

		StringBuffer field61 = new StringBuffer();
		String field1 = RupayIsoUtil.getBuildP61_1(buffer);
		String field2 = RupayIsoUtil.getBuildP61_2(buffer);
		String field7 = RupayIsoUtil.getBuildP61_7(buffer);

		String field9 = RupayIsoUtil.getBuildP61_9(buffer);
		String field10 = RupayIsoUtil.getBuildP61_10(buffer);
		field61.append(field1);// 1
		field61.append(field2);// 2
		field61.append("1");// 3
		field61.append("1");// 4- changes for MPOS
		// field61.append("7");// 4
		field61.append("1");// 5
		field61.append("2");// 6
		field61.append(field7);// 7
		Map<EmvTags, String> tvMap = hostData.getEmvMap();
		// Log.debug("Constants.DE52 ", buffer.get(Constants.DE52));
		if (!buffer.get(Constants.DE52).isEmpty()
				&& !buffer.get(Constants.DE52).equals("*")) {
			String field8 = "2";
			field61.append(field8);// 8
		} else {
			if (!tvMap.get(EmvTags.TAG_9F34).isEmpty()) {
				// Log.debug("EMV Map SubString ", tvMap.get(EmvTags.TAG_9F34)
				// .substring(0, 2));
				if (tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
						.equalsIgnoreCase("5E")
						|| tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
								.equalsIgnoreCase("1E")) {
					// Log.debug("Constants.DE61_SF8 ", Constants.DE61_SF8);
					// buffer.put(Constants.DE61_SF8, "3");
					String field8 = Constants.DE61_SF8;
					field61.append(field8);// 8

				} else if (tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
						.equalsIgnoreCase("41")
						|| tvMap.get(EmvTags.TAG_9F34).substring(0, 2)
								.equalsIgnoreCase("01")) {
					// Log.debug("Offline Pin ", tvMap.get(EmvTags.TAG_9F34));
					String field8 = "2";
					field61.append(field8);// 8

				} else {
					// Log.debug("TAG 9F34 ", tvMap.get(EmvTags.TAG_9F34));
					String field8 = "1";
					field61.append(field8);// 8

				}
			} else {
				// Log.debug("Unknown ", tvMap.get(EmvTags.TAG_9F34));
				String field8 = "1";
				field61.append(field8);// 8
			}
		}
		// field61.append(field8);// 8
		field61.append(field9);// 9
		field61.append(field10);// 10
		field61.append("3");// 11
		field61.append("9");// 12
		field61.append(Util.appendChar(hostData.getZipCode(), '0', 9, true));// 13
		if (hostData.getBusinessName().length() <= 20) {
			field61.append(Util.appendChar(hostData.getBusinessName(), ' ', 20,
					false));// 14
		} else {
			field61.append(hostData.getBusinessName().substring(0, 20));
		}
		// field61.append(Util.appendChar(hostData.getBusinessName(), ' ', 20,
		// false));// 14
		Log.debug("Feild 61", field61.toString());
		buffer.put(Constants.DE61, field61.toString());

		buffer.disableField(Constants.DE14);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE47);
		buffer.disableField(Constants.DE54);
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

	@ModifyTransaction(value = Transactions.EMV_DEFAULT)
	public void modifyEmv(IsoBuffer buffer, Data data) {

		StringBuilder d = new StringBuilder();
		Map<EmvTags, String> tvMap;
		if (buffer.get(Constants.ISO_MSG_TYPE).equals(
				Constants.MSG_TYPE_REV_420)) {
			if (buffer.get(Constants.DE39).equalsIgnoreCase("17")) {
				HostData hd = (HostData) data;
				tvMap = hd.getEmvMap();
			} else {
				TransactionLogData hd = (TransactionLogData) data;
				tvMap = hd.getEmvMap();
				// buffer.put(Constants.DE39,"E2");

			}
			String cardSeqNo = tvMap.get(EmvTags.TAG_5F34);
			buffer.put(Constants.DE23, Util.appendChar(cardSeqNo, '0', 3, true));
			for (EmvTags tag : tvMap.keySet()) {
				if (SUPPORTED_TAGS.contains(tag)) {
					d.append(tag.toString());
					String val = tvMap.get(tag);
					d.append(Util.appendChar(String.valueOf(Integer
							.toHexString(val.length() / 2)), '0', 2, true));
					d.append(val);
				}
			}
		} else {
			HostData hd = (HostData) data;
			tvMap = hd.getEmvMap();
			String cardSeqNo = tvMap.get(EmvTags.TAG_5F34);
			buffer.put(Constants.DE23, Util.appendChar(cardSeqNo, '0', 3, true));
			d = new StringBuilder();
			// tvMap.remove(EmvTags.TAG_5F34);
			for (EmvTags tag : tvMap.keySet()) {
				if (SUPPORTED_TAGS.contains(tag)) {
					d.append(tag.toString());
					String val = tvMap.get(tag);
					d.append(Util.appendChar(String.valueOf(Integer
							.toHexString(val.length() / 2)), '0', 2, true));
					d.append(val);
				}
			}
		}
		Log.debug("EMV Datas", d.toString());
		buffer.put(Constants.DE55, d.toString()); // Inserting to map in clear
													// form

		// if (tvMap.get(EmvTags.TAG_9F34).substring(0,
		// 2).equalsIgnoreCase("5E")) {
		// buffer.put(Constants.DE61,
		// "421112321239000400051Merchant One Legal  "); // signature - 2
		// // buffer.put(Constants.DE61, "2"); // signature - 2
		// // buffer.put(Constants.DE61, buffer.get(Contains.)); // signature -
		// 2
		// }
	}
}
