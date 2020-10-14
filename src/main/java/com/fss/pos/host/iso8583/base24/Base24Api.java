package com.fss.pos.host.iso8583.base24;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fss.pos.base.api.host.HostApi;
import com.fss.pos.base.api.host.HostData;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.EmvTags;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.base.services.transactionlog.TransactionLogData;
import com.fss.pos.host.AbstractHostApi;

/**
 * An object to handle the base24 transactions.
 * 
 * @author Priyan
 * @see HostApi
 */
public abstract class Base24Api extends AbstractHostApi {

	protected static final String BALANCE_INQ_AMOUNT = "000000000000";
	protected static final String P_48_HARDCODED = "000000000000000000000000000";
	protected static final String S90_FILLER = "          ";

	// EMV
	protected static final String AMPERSAND = "&";
	protected static final String EYE_CATCHER = "!";
	protected static final String SPACE = " ";
	// Request
	private static final String USER_FIELD_BLANK = "usrfldblnk";
	private static final String USER_FIELD_1 = "usrfld1";
	private static final String USER_FIELD_2 = "usrfld2";
	private static final String TAG_9F10 = "9F10";
	private static final String TAG_84 = "84";
	private static final String TAG_9F33 = "9F33";
	private static final String TAG_9F1E = "9F1E";
	private static final String TAG_9F34 = "9F34";
	private static final String TAG_5F34 = "5F34";
	private static final String ONE = "1";
	private static final String TWO = "2";
	private static final String ZERO = "0";
	// private static final String B0_TAG_HEADER = "! B0";
	private static final String B2_TAG_HEADER = "! B2";
	private static final String B3_TAG_HEADER = "! B3";
	private static final String B4_TAG_HEADER = "! B4";
	private static final String C4_TAG_HEADER = "! C4";
	private static final String QK_TAG_HEADER = "! QK";

	/**
	 * Response tokens
	 */
	private static final String TOKEN_B5 = "B5";
	private static final String TOKEN_B6 = "B6";

	/**
	 * B2 Token tags in sorted order
	 */
	private static final List<String> ORDERED_B2_TAGS;
	private static final Map<String, Integer> B2_TAG_LENGTH_MAP;

	/**
	 * B3 Token tags in sorted order
	 */
	private static final List<String> ORDERED_B3_TAGS;
	private static final Map<String, Integer> B3_TAG_LENGTH_MAP;

	static {
		ORDERED_B2_TAGS = new ArrayList<String>();
		ORDERED_B2_TAGS.add(USER_FIELD_1);
		ORDERED_B2_TAGS.add("9F27");
		ORDERED_B2_TAGS.add("95");
		ORDERED_B2_TAGS.add("9F26");
		ORDERED_B2_TAGS.add("9F02");
		ORDERED_B2_TAGS.add("9F03");
		ORDERED_B2_TAGS.add("82");
		ORDERED_B2_TAGS.add("9F36");
		ORDERED_B2_TAGS.add("9F1A");
		ORDERED_B2_TAGS.add("5F2A");
		ORDERED_B2_TAGS.add("9A");
		ORDERED_B2_TAGS.add("9C");
		ORDERED_B2_TAGS.add("9F37");
		ORDERED_B2_TAGS.add(USER_FIELD_BLANK);
		ORDERED_B2_TAGS.add(USER_FIELD_BLANK);
		ORDERED_B2_TAGS.add("9F10");

		ORDERED_B3_TAGS = new ArrayList<String>();
		ORDERED_B3_TAGS.add("9F1E");
		ORDERED_B3_TAGS.add("9F33");
		ORDERED_B3_TAGS.add(USER_FIELD_1);
		ORDERED_B3_TAGS.add(USER_FIELD_2);
		ORDERED_B3_TAGS.add("9F35");
		ORDERED_B3_TAGS.add("9F09");
		ORDERED_B3_TAGS.add("9F34");
		ORDERED_B3_TAGS.add("84");
		ORDERED_B3_TAGS.add(USER_FIELD_BLANK);
		ORDERED_B3_TAGS.add(USER_FIELD_BLANK);
		ORDERED_B3_TAGS.add(USER_FIELD_BLANK);
		ORDERED_B3_TAGS.add(USER_FIELD_BLANK);
		ORDERED_B3_TAGS.add(USER_FIELD_BLANK);
		ORDERED_B3_TAGS.add(USER_FIELD_BLANK);
		ORDERED_B3_TAGS.add(USER_FIELD_BLANK);
		ORDERED_B3_TAGS.add(USER_FIELD_BLANK);

		B2_TAG_LENGTH_MAP = new HashMap<String, Integer>();
		B2_TAG_LENGTH_MAP.put(USER_FIELD_1, 4);
		B2_TAG_LENGTH_MAP.put("9F27", 2);
		B2_TAG_LENGTH_MAP.put("95", 10);
		B2_TAG_LENGTH_MAP.put("9F26", 16);
		B2_TAG_LENGTH_MAP.put("9F02", 12);
		B2_TAG_LENGTH_MAP.put("9F03", 12);
		B2_TAG_LENGTH_MAP.put("82", 4);
		B2_TAG_LENGTH_MAP.put("9F36", 4);
		B2_TAG_LENGTH_MAP.put("9F1A", 4);
		B2_TAG_LENGTH_MAP.put("5F2A", 4);
		B2_TAG_LENGTH_MAP.put("9A", 6);
		B2_TAG_LENGTH_MAP.put("9C", 2);
		B2_TAG_LENGTH_MAP.put("9F37", 8);
		B2_TAG_LENGTH_MAP.put(USER_FIELD_BLANK, 0);
		B2_TAG_LENGTH_MAP.put("9F10", 4);

		B3_TAG_LENGTH_MAP = new HashMap<String, Integer>();
		B3_TAG_LENGTH_MAP.put("9F1E", 16);
		B3_TAG_LENGTH_MAP.put("9F33", 8);
		B3_TAG_LENGTH_MAP.put(USER_FIELD_1, 4);
		B3_TAG_LENGTH_MAP.put(USER_FIELD_2, 8);
		B3_TAG_LENGTH_MAP.put("9F35", 2);
		B3_TAG_LENGTH_MAP.put("9F09", 4);
		B3_TAG_LENGTH_MAP.put("9F34", 6);
		B3_TAG_LENGTH_MAP.put("84", 4);
		B3_TAG_LENGTH_MAP.put(USER_FIELD_BLANK, 0);
	}

	@Autowired
	private Base24Iso87Api base24Iso87Api;

	@Override
	public IsoBuffer parse(String message) throws PosException {
		return base24Iso87Api.parseIso87(message.substring(2), 32);
	}

	@Override
	public String build(IsoBuffer isoBuffer) {
		Log.debug("Base24 Host Requset : ", isoBuffer.toString());
		return base24Iso87Api.buildIso87Request(
				isoBuffer.get(Constants.ISO_MSG_TYPE), isoBuffer);
	}

	/**
	 * To form the B2 token for Base24 host
	 * 
	 * @param tvMap
	 *            the populated emv tag value map
	 * @return {@link String} B2 token data
	 * @throws PosException
	 * @throws JSONException
	 */

	/*
	 * public String getB0Token() { StringBuilder b0Buffer = new
	 * StringBuilder(); b0Buffer.append(B0_TAG_HEADER); String bitMap =
	 * IsoUtil.binary2hex(bitmap.toString()); b0Buffer.append(Util.appendChar(
	 * Integer.toString(bTags.length() + bitMap.length()), '0', 5, true));
	 * b0Buffer.append(SPACE); b0Buffer.append(bitMap); b0Buffer.append(bTags);
	 * return b0Buffer.toString(); }
	 */
	public String getB2Token(Map<String, String> tvMap, Data data, String msgTyp)
			throws PosException, JSONException {
		if (msgTyp.equals(Constants.MSG_TYPE_REV_420)) {
			TransactionLogData tranLogData = (TransactionLogData) data;
			StringBuilder bitmap = new StringBuilder();
			StringBuilder bTags = new StringBuilder();
			for (String tag : ORDERED_B2_TAGS) {
				if (USER_FIELD_BLANK.equals(tag) || !tvMap.containsKey(tag)) {
					bitmap.append(ZERO);
					appendZeros(bTags, tag, B2_TAG_LENGTH_MAP);
				} else {
					bitmap.append(ONE);
					if (USER_FIELD_1.equals(tag)) {
						appendZeros(bTags, tag, B2_TAG_LENGTH_MAP);
					} else if (TAG_9F10.equals(tag)) {

						String issuerAppData = tvMap.get(tag);
						// added for ABSA
						if (tranLogData.getMsgProtocol().equals("14")
								|| tranLogData.getMsgProtocol().equals("16")) {

							bTags.append(Util.appendChar(Integer
									.toString(issuerAppData.length() / 2), '0',
									4, true));
							bTags.append(issuerAppData);

							String issuerAppfinalData = new StringBuilder(
									issuerAppData).append(
									IsoUtil.asciiChar2hex(Util.appendChar(
											Constants.EMPTY_STRING, '0',
											(64 - issuerAppData.length()) / 2,
											false))).toString();
							bTags.append(Util.appendChar(Integer
									.toString(issuerAppfinalData.length() / 2),
									'0', 4, true));
							bTags.append(issuerAppfinalData);

						}

						else {
							String issuerAppfinalData = new StringBuilder(
									issuerAppData).append(
									IsoUtil.asciiChar2hex(Util.appendChar(
											Constants.EMPTY_STRING, ' ',
											(64 - issuerAppData.length()) / 2,
											false))).toString();
							bTags.append(Util.appendChar(Integer
									.toString(issuerAppfinalData.length() / 2),
									'0', 4, true));
							bTags.append(issuerAppfinalData);
						}

					} else {
						bTags.append(tvMap.get(tag));
					}
				}
			}
			StringBuilder b2Buffer = new StringBuilder();
			b2Buffer.append(B2_TAG_HEADER);
			String bitMap = IsoUtil.binary2hex(bitmap.toString());
			b2Buffer.append(Util.appendChar(
					Integer.toString(bTags.length() + bitMap.length()), '0', 5,
					true));
			b2Buffer.append(SPACE);
			b2Buffer.append(bitMap);
			b2Buffer.append(bTags);
			return b2Buffer.toString();
		} else {
			HostData hostData = (HostData) data;
			StringBuilder bitmap = new StringBuilder();
			StringBuilder bTags = new StringBuilder();
			for (String tag : ORDERED_B2_TAGS) {
				if (USER_FIELD_BLANK.equals(tag) || !tvMap.containsKey(tag)) {
					bitmap.append(ZERO);
					appendZeros(bTags, tag, B2_TAG_LENGTH_MAP);
				} else {
					bitmap.append(ONE);
					if (USER_FIELD_1.equals(tag)) {
						appendZeros(bTags, tag, B2_TAG_LENGTH_MAP);
					} else if (TAG_9F10.equals(tag)) {

						String issuerAppData = tvMap.get(tag);
						// added for ABSA
						if (hostData.getMsgProtocol().equals("14")
								|| hostData.getMsgProtocol().equals("16")) {

							bTags.append(Util.appendChar(Integer
									.toString(issuerAppData.length() / 2), '0',
									4, true));
							bTags.append(issuerAppData);

							String issuerAppfinalData = new StringBuilder(
									issuerAppData).append(
									IsoUtil.asciiChar2hex(Util.appendChar(
											Constants.EMPTY_STRING, '0',
											(64 - issuerAppData.length()) / 2,
											false))).toString();
							bTags.append(Util.appendChar(Integer
									.toString(issuerAppfinalData.length() / 2),
									'0', 4, true));
							bTags.append(issuerAppfinalData);

						}

						else {
							String issuerAppfinalData = new StringBuilder(
									issuerAppData).append(
									IsoUtil.asciiChar2hex(Util.appendChar(
											Constants.EMPTY_STRING, ' ',
											(64 - issuerAppData.length()) / 2,
											false))).toString();
							bTags.append(Util.appendChar(Integer
									.toString(issuerAppfinalData.length() / 2),
									'0', 4, true));
							bTags.append(issuerAppfinalData);
						}

					} else {
						bTags.append(tvMap.get(tag));
					}
				}
			}
			StringBuilder b2Buffer = new StringBuilder();
			b2Buffer.append(B2_TAG_HEADER);
			String bitMap = IsoUtil.binary2hex(bitmap.toString());
			b2Buffer.append(Util.appendChar(
					Integer.toString(bTags.length() + bitMap.length()), '0', 5,
					true));
			b2Buffer.append(SPACE);
			b2Buffer.append(bitMap);
			b2Buffer.append(bTags);
			return b2Buffer.toString();
		}

	}

	/**
	 * To form the B3 token for Base24 host
	 * 
	 * @param tvMap
	 *            the populated emv tag value map
	 * @param terminalId
	 * @return {@link String} B3 token data
	 */
	public String getB3Token(Map<String, String> tvMap, String apiId) {
		StringBuilder b3Buffer = new StringBuilder();
		StringBuilder bitmap = new StringBuilder();
		StringBuilder bTags = new StringBuilder();
		b3Buffer.append(B3_TAG_HEADER);
		for (String tag : ORDERED_B3_TAGS) {
			if (USER_FIELD_BLANK.equals(tag) || !tvMap.containsKey(tag)) {
				bitmap.append(ZERO);
				appendZeros(bTags, tag, B3_TAG_LENGTH_MAP);
			} else {
				bitmap.append(ONE);
				if (USER_FIELD_1.equals(tag)) {
					appendZeros(bTags, tag, B3_TAG_LENGTH_MAP);
				} else if (USER_FIELD_2.equals(tag)) {
					appendZeros(bTags, tag, B3_TAG_LENGTH_MAP);
				} else if (TAG_84.equals(tag)) {
					String issDFName = tvMap.get(tag);
					// added for ABSA
					if (apiId.equals("14") || apiId.equals("16")) {
						bTags.append(Util.appendChar(
								Integer.toString(issDFName.length() / 2), '0',
								4, true));
						bTags.append(issDFName);

						String dfNamefinalData = new StringBuilder(issDFName)
								.append(IsoUtil.asciiChar2hex(Util.appendChar(
										Constants.EMPTY_STRING, '0',
										(32 - issDFName.length()) / 2, false)))
								.toString();
						bTags.append(Util.appendChar(
								Integer.toString(dfNamefinalData.length() / 2),
								'0', 4, true));
						bTags.append(dfNamefinalData);

					}

					else {
						String dfNamefinalData = new StringBuilder(issDFName)
								.append(IsoUtil.asciiChar2hex(Util.appendChar(
										Constants.EMPTY_STRING, ' ',
										(32 - issDFName.length()) / 2, false)))
								.toString();
						bTags.append(Util.appendChar(
								Integer.toString(dfNamefinalData.length() / 2),
								'0', 4, true));
						bTags.append(dfNamefinalData);

					}
				} else if (TAG_9F33.equals(tag)) {
					bTags.append(Util.appendChar(tvMap.get(tag), '0', 8, false));
				} else if (TAG_9F1E.equals(tag)) {
					bTags.append(IsoUtil.hex2AsciiChar(tvMap.get(tag)));
				} else {
					bTags.append(tvMap.get(tag));
				}
			}
		}
		String bitMap = IsoUtil.binary2hex(bitmap.toString());
		b3Buffer.append(Util.appendChar(
				Integer.toString(bTags.length() + bitMap.length()), '0', 5,
				true));
		b3Buffer.append(SPACE);
		b3Buffer.append(bitMap);
		b3Buffer.append(bTags);
		return b3Buffer.toString();
	}

	/**
	 * To form the B4 token for Base24 host
	 * 
	 * @param tvMap
	 *            the populated emv tag value map
	 * @param entryMode
	 *            terminal card entry mode
	 * @param hostData
	 *            the host data bean
	 * @return {@link String} B4 token data
	 */
	public String getB4Token(Map<String, String> tvMap, IsoBuffer isoBuffer,
			Data data) {
		if (isoBuffer.get(Constants.ISO_MSG_TYPE).equals(
				Constants.MSG_TYPE_REV_420)) {
			TransactionLogData tranLogData = (TransactionLogData) data;
			StringBuilder bTags = new StringBuilder();
			bTags.append(isoBuffer.get(Constants.DE22));
			/*
			 * if (!(tranLogData.getMsgProtocol().equals("14"))) {
			 * bTags.append(tranLogData.getEnableEMV()); //terminal entry
			 * capability bTags.append(ZERO); // last emv status
			 * bTags.append(ZERO); // data suspect }
			 */

			if (tranLogData.getMsgProtocol().equals("14")
					|| tranLogData.getMsgProtocol().equals("16")) {
				if (isoBuffer.get(Constants.DE22).substring(0, 2).equals("80")) {
					// fallback
					bTags.append(ZERO); // terminal entry capability
					bTags.append(ZERO); // last emv status
					bTags.append(ZERO); // data suspect
				} else {
					bTags.append("5"); // terminal entry capability
					bTags.append("1"); // last emv status
					bTags.append(ZERO); // data suspect
				}

			} else {
				bTags.append(tranLogData.getEnableEMV()); // terminal entry
															// capability
				bTags.append(ZERO); // last emv status
				bTags.append(ZERO); // data suspect
			}

			// appln pan seq no
			bTags.append(tvMap.get(TAG_5F34) == null ? SPACE + SPACE : tvMap
					.get(TAG_5F34));
			// dev info
			bTags.append(tvMap.get(TAG_9F34) == null ? "000" + "000" : tvMap
					.get(TAG_9F34));
			bTags.append("1502"); // reason online code
			bTags.append(ZERO); // ARQC verify
			bTags.append(ZERO); // ISO RC IND

			StringBuilder b4Buffer = new StringBuilder();
			b4Buffer.append(B4_TAG_HEADER);
			b4Buffer.append(Util.appendChar(Integer.toString(bTags.length()),
					'0', 5, true));
			b4Buffer.append(SPACE);
			b4Buffer.append(bTags);
			return b4Buffer.toString();
		} else {
			HostData hostData = (HostData) data;
			StringBuilder bTags = new StringBuilder();
			bTags.append(isoBuffer.get(Constants.DE22));

			/*
			 * if (!(hostData.getMsgProtocol().equals("14"))) {
			 * bTags.append(hostData.getEnableEMV()); // terminal entry
			 * capability bTags.append(ZERO); // last emv status
			 * bTags.append(ZERO); // data suspect }
			 */

			if (hostData.getMsgProtocol().equals("14")
					|| hostData.getMsgProtocol().equals("16")) {
				if (isoBuffer.get(Constants.DE22).substring(0, 2).equals("80")) {
					// fallback
					bTags.append(ZERO); // terminal entry capability
					bTags.append(ZERO); // last emv status
					bTags.append(ZERO); // data suspect
				} else {
					bTags.append("5"); // terminal entry capability
					bTags.append("1"); // last emv status
					bTags.append(ZERO); // data suspect
				}

			} else {
				bTags.append(hostData.getEnableEMV()); // terminal entry
														// capability
				bTags.append(ZERO); // last emv status
				bTags.append(ZERO); // data suspect
			}

			// appln pan seq no
			bTags.append(tvMap.get(TAG_5F34) == null ? SPACE + SPACE : tvMap
					.get(TAG_5F34));
			// dev info
			bTags.append(tvMap.get(TAG_9F34) == null ? "000" + "000" : tvMap
					.get(TAG_9F34));
			bTags.append("1502"); // reason online code
			bTags.append(ZERO); // ARQC verify
			bTags.append(ZERO); // ISO RC IND

			StringBuilder b4Buffer = new StringBuilder();
			b4Buffer.append(B4_TAG_HEADER);
			b4Buffer.append(Util.appendChar(Integer.toString(bTags.length()),
					'0', 5, true));
			b4Buffer.append(SPACE);
			b4Buffer.append(bTags);
			return b4Buffer.toString();
		}

	}

	/**
	 * To form the B4 token for Base24 host
	 * 
	 * @param tvMap
	 *            the populated emv tag value map
	 * @param isoBuffer
	 *            buffer with iso request
	 * @param hostData
	 *            the host data bean
	 * @return {@link String} C4 token data
	 */
	public String getC4Token(Map<String, String> tvMap, IsoBuffer isoBuffer,
			Data data) {

		if (isoBuffer.get(Constants.ISO_MSG_TYPE).equals(
				Constants.MSG_TYPE_REV_420)) {
			TransactionLogData tranLogData = (TransactionLogData) data;
			StringBuilder bTags = new StringBuilder();
			bTags.append(ZERO); // TERM-ATTEND-IND
			bTags.append(ZERO); // TERM-OPER-IND
			bTags.append(ZERO); // TERM-LOC-IND
			String procCode = isoBuffer.get(Constants.DE3);
			bTags.append((Constants.PROC_CODE_SALE_MOTO_COMPLETION
					.equals(procCode) && Constants.CONDITION_CODE_MOTO
					.equals(isoBuffer.get(Constants.DE25))) ? TWO : ZERO); // CRDHLDR-PRESENT-IND
			bTags.append((Constants.PROC_CODE_SALE_MOTO_COMPLETION
					.equals(procCode) && Constants.CONDITION_CODE_MOTO
					.equals(isoBuffer.get(Constants.DE25))) ? ONE : ZERO); // CRD-PRESENT-IND
			bTags.append(ONE); // CRD-CAPTR-IND
			if (Constants.PROC_CODE_PREAUTH.equals(procCode))
				bTags.append(ONE); // TXN-STAT-IND
			else if (Constants.PROC_CODE_SALE_MOTO_COMPLETION.equals(procCode)
					&& Constants.CONDITION_CODE_COMPLETION.equals(isoBuffer
							.get(Constants.DE25)))
				bTags.append("4");
			else if (Constants.PROC_CODE_CASHBACK.equals(procCode))
				bTags.append("7");
			else
				bTags.append(ZERO);
			bTags.append(TWO); // TXN-SEC-IND
			bTags.append(ZERO); // TXN-RTN-IND
			bTags.append(ZERO); // CRDHLDR-ACTVT-TERM-IND
			// TERM-INPUT-CAP-IND
			if (!(tranLogData.getMsgProtocol().equals("14"))) {
				if (Constants.ENABLE.equals(tranLogData.getKeyEntry())
						&& Constants.DISABLE.equals(tranLogData
								.getMagneticStripe())
						&& Constants.DISABLE.equals(tranLogData.getIcContact()))
					bTags.append("6");
				else if (Constants.DISABLE.equals(tranLogData.getKeyEntry())
						&& Constants.ENABLE.equals(tranLogData
								.getMagneticStripe())
						&& Constants.DISABLE.equals(tranLogData.getIcContact()))
					bTags.append(TWO);
				else if (Constants.DISABLE.equals(tranLogData.getKeyEntry())
						&& Constants.DISABLE.equals(tranLogData
								.getMagneticStripe())
						&& Constants.ENABLE.equals(tranLogData.getIcContact()))
					bTags.append("9");
				else if (Constants.ENABLE.equals(tranLogData.getKeyEntry())
						&& Constants.ENABLE.equals(tranLogData
								.getMagneticStripe())
						&& Constants.DISABLE.equals(tranLogData.getIcContact()))
					bTags.append("7");
				else if (Constants.DISABLE.equals(tranLogData.getKeyEntry())
						&& Constants.ENABLE.equals(tranLogData
								.getMagneticStripe())
						&& Constants.ENABLE.equals(tranLogData.getIcContact()))
					bTags.append("5");
				else if (Constants.ENABLE.equals(tranLogData.getKeyEntry())
						&& Constants.ENABLE.equals(tranLogData
								.getMagneticStripe())
						&& Constants.ENABLE.equals(tranLogData.getIcContact()))
					bTags.append("8");
				else
					bTags.append(ZERO);
			}

			else
				bTags.append("3");
			bTags.append(tranLogData.getCardHolderVerification()); // CRDHLDR-ID-METHOD

			StringBuilder c4Buffer = new StringBuilder();
			c4Buffer.append(C4_TAG_HEADER);
			c4Buffer.append(Util.appendChar(Integer.toString(bTags.length()),
					'0', 5, true));
			c4Buffer.append(SPACE);
			c4Buffer.append(bTags);
			return c4Buffer.toString();
		} else {
			HostData hostData = (HostData) data;
			StringBuilder bTags = new StringBuilder();
			bTags.append(ZERO); // TERM-ATTEND-IND
			bTags.append(ZERO); // TERM-OPER-IND
			bTags.append(ZERO); // TERM-LOC-IND
			String procCode = isoBuffer.get(Constants.DE3);
			bTags.append((Constants.PROC_CODE_SALE_MOTO_COMPLETION
					.equals(procCode) && Constants.CONDITION_CODE_MOTO
					.equals(isoBuffer.get(Constants.DE25))) ? TWO : ZERO); // CRDHLDR-PRESENT-IND
			bTags.append((Constants.PROC_CODE_SALE_MOTO_COMPLETION
					.equals(procCode) && Constants.CONDITION_CODE_MOTO
					.equals(isoBuffer.get(Constants.DE25))) ? ONE : ZERO); // CRD-PRESENT-IND
			bTags.append(ONE); // CRD-CAPTR-IND
			if (Constants.PROC_CODE_PREAUTH.equals(procCode))
				bTags.append(ONE); // TXN-STAT-IND
			else if (Constants.PROC_CODE_SALE_MOTO_COMPLETION.equals(procCode)
					&& Constants.CONDITION_CODE_COMPLETION.equals(isoBuffer
							.get(Constants.DE25)))
				bTags.append("4");
			else if (Constants.PROC_CODE_CASHBACK.equals(procCode))
				bTags.append("7");
			else
				bTags.append(ZERO);
			bTags.append(TWO); // TXN-SEC-IND
			bTags.append(ZERO); // TXN-RTN-IND
			bTags.append(ZERO); // CRDHLDR-ACTVT-TERM-IND
			// TERM-INPUT-CAP-IND
			if (!(hostData.getMsgProtocol().equals("14"))) {
				if (Constants.ENABLE.equals(hostData.getKeyEntry())
						&& Constants.DISABLE.equals(hostData
								.getMagneticStripe())
						&& Constants.DISABLE.equals(hostData.getIcContact()))
					bTags.append("6");
				else if (Constants.DISABLE.equals(hostData.getKeyEntry())
						&& Constants.ENABLE
								.equals(hostData.getMagneticStripe())
						&& Constants.DISABLE.equals(hostData.getIcContact()))
					bTags.append(TWO);
				else if (Constants.DISABLE.equals(hostData.getKeyEntry())
						&& Constants.DISABLE.equals(hostData
								.getMagneticStripe())
						&& Constants.ENABLE.equals(hostData.getIcContact()))
					bTags.append("9");
				else if (Constants.ENABLE.equals(hostData.getKeyEntry())
						&& Constants.ENABLE
								.equals(hostData.getMagneticStripe())
						&& Constants.DISABLE.equals(hostData.getIcContact()))
					bTags.append("7");
				else if (Constants.DISABLE.equals(hostData.getKeyEntry())
						&& Constants.ENABLE
								.equals(hostData.getMagneticStripe())
						&& Constants.ENABLE.equals(hostData.getIcContact()))
					bTags.append("5");
				else if (Constants.ENABLE.equals(hostData.getKeyEntry())
						&& Constants.ENABLE
								.equals(hostData.getMagneticStripe())
						&& Constants.ENABLE.equals(hostData.getIcContact()))
					bTags.append("8");
				else
					bTags.append(ZERO);
			} else
				bTags.append("3");

			bTags.append(hostData.getCardHolderVerification()); // CRDHLDR-ID-METHOD

			StringBuilder c4Buffer = new StringBuilder();
			c4Buffer.append(C4_TAG_HEADER);
			c4Buffer.append(Util.appendChar(Integer.toString(bTags.length()),
					'0', 5, true));
			c4Buffer.append(SPACE);
			c4Buffer.append(bTags);
			return c4Buffer.toString();
		}

	}

	/**
	 * To form the QK token for Base24 host
	 * 
	 * @param guid
	 *            transaction unique id
	 * @return {@link String} QK token data
	 */
	public String getQKToken(String guid) {
		guid = (guid.length() > 36) ? guid.substring(0, 36) : Util.appendChar(
				guid, ' ', 36, false);
		StringBuilder qkBuffer = new StringBuilder();
		qkBuffer.append(QK_TAG_HEADER);
		qkBuffer.append(Util.appendChar(Integer.toString(guid.length()), '0',
				5, true));
		qkBuffer.append(SPACE);
		qkBuffer.append(guid);
		return qkBuffer.toString();
	}

	/**
	 * To parse the emv response data from Base24
	 * 
	 * @param emvData
	 *            the emv response data from Base24
	 * @return a map {@link Map} containing emv token and values
	 * @throws PosException
	 *             thrown when exception during parsing
	 */
	public Map<EmvTags, String> parseEmvTokens(String emvData)
			throws PosException {
		try {
			Map<EmvTags, String> emvMap = new HashMap<EmvTags, String>();
			String[] tokens = emvData.split(EYE_CATCHER + SPACE);
			for (String tokenData : tokens) {
				if (tokenData.startsWith(AMPERSAND))
					continue;
				int idx = 0;
				String token = tokenData.substring(idx, idx += 2);
				String length = tokenData.substring(idx, idx += 5);
				idx++;
				String data = tokenData.substring(idx,
						idx += (Integer.parseInt(length)));
				if (TOKEN_B5.equals(token)) {
					idx = 0;
					String innerDataLen = data.substring(idx, idx += 4);
					String tag91Data = data.substring(idx,
							idx += (Integer.parseInt(innerDataLen) * 2));
					emvMap.put(EmvTags.TAG_91, tag91Data);
				} else if (TOKEN_B6.equals(token)) {
					while (data.length() > 0) {
						String innerDataLen = data.substring(0, 4);
						data = data.substring(4);
						String innerData = data.substring(0,
								Integer.parseInt(innerDataLen) * 2);
						data = data.substring(innerData.length());
						String innerToken = innerData.substring(0, 2);
						if (EmvTags.TAG_71.toString().equals(innerToken)
								|| EmvTags.TAG_72.toString().equals(innerToken)) {
							emvMap.put(EmvTags.getEmvTag(innerToken),
									innerData.substring(4));
						}
					}
				}
			}
			return emvMap;
		} catch (NumberFormatException e) {
			Log.error("Base24Api parseEmvTokens ", e);
			throw new PosException(Constants.ERR_HOST_EMV_PARSING);
		}
	}

	private void appendZeros(StringBuilder bTags, String tag,
			Map<String, Integer> map) {
		for (int i = 0; i < map.get(tag); i++)
			bTags.append(ZERO);
	}

}
