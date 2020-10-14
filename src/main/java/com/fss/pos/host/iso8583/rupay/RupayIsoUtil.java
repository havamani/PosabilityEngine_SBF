package com.fss.pos.host.iso8583.rupay;

import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.Util;

public class RupayIsoUtil {

	public static String appendChar(String data, char ch, int totalLen,
			boolean appendLeft) {
		if (data == null) {
			data = "";
		}
		StringBuilder sb = new StringBuilder();
		if (!appendLeft) {
			sb.append(data);
		}
		for (int i = 1; i <= (totalLen - data.length()); i++) {
			sb.append(ch);
		}
		if (appendLeft) {
			sb.append(data);
		}
		return sb.toString();
	}

	public String getReversalDE48(String respDe48, String respDe63,
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

	public static String getBuildP61_1(IsoBuffer buffer) {

		String entryMode = buffer.get(Constants.DE22).substring(0, 2);

		entryMode = "8";
		/*
		 * switch (entryMode) {
		 * 
		 * case Constants.ENTRY_MODE_MANUAL: entryMode = "5"; break;
		 * 
		 * case Constants.ENTRY_MODE_ICC_CL: // card with contact and
		 * contactless and chip entryMode = "7"; break;
		 * 
		 * case Constants.ENTRY_MODE_ICC_CB: entryMode = "4"; break;
		 * 
		 * default: entryMode = "4"; break;
		 * 
		 * }
		 */

		return entryMode;

		/*
		 * if (buffer.get(Constants.DE22).substring(2).equals("01")) {
		 * s60_1.append("6"); } if (buffer.get(Constants.DE22).substring(0, 2)
		 * .equals(Constants.ENTRY_MODE_ICC_CL)) { s60_1.append("7"); } else {
		 * s60_1.append("4"); } return s60_1.toString();
		 */
	}

	public static String getBuildP61_2(IsoBuffer buffer) {
		return (!buffer.isFieldEmpty(Constants.DE52)) ? "2" : "1";
	}

	public static String getBuildP61_6(IsoBuffer buffer) {
		// check with parandaman\

		String de22 = buffer.get(Constants.DE22).substring(0, 2);
		return (de22.equals(Constants.ENTRY_MODE_MANUAL) || de22
				.equals(Constants.ENTRY_MODE_ICC_CL)) ? "2" : "1";
	}

	public static String getBuildP61_7(IsoBuffer buffer) {
		String entryMode = buffer.get(Constants.DE22).substring(0, 2);
		switch (entryMode) {

		case Constants.ENTRY_MODE_MAG_STRIPE:
			entryMode = "2";
			break;
		case Constants.ENTRY_MODE_MANUAL:
			entryMode = "1";
			break;
		case Constants.ENTRY_MODE_ICC_CB:
			entryMode = "3";
			break;
		case Constants.ENTRY_MODE_ICC_CL:
			entryMode = "4";
			break;
		case Constants.ENTRY_MODE_ICC_TO_MAG_FALLBACK:
			entryMode = "2";
			break;
		default:
			entryMode = "0";
			break;
		}

		return entryMode;

		/*
		 * if (buffer.get(Constants.DE22).substring(0, 2).equals("02")) {
		 * s60_2.append("2"); } else if (buffer.get(Constants.DE22).substring(0,
		 * 2).equals("01")) { s60_2.append("7"); } else if
		 * (buffer.get(Constants.DE22).substring(0, 2).equals("05")) {
		 * s60_2.append("3"); } else if (buffer.get(Constants.DE22).substring(0,
		 * 2).equals("07")) { s60_2.append("4"); } else if
		 * (buffer.get(Constants.DE22).substring(0, 2).equals("80")) {
		 * s60_2.append("2"); } else { s60_2.append("0"); } return
		 * s60_2.toString();
		 */
	}

	/*
	 * public static String getBuildP61_8(IsoBuffer buffer) { if
	 * (buffer.get(Constants.DE61_SF8).equals("3")) {
	 * Log.debug("Build 61_8 value", buffer.get(Constants.DE61_SF8)); return
	 * buffer.get(Constants.DE61_SF8).toString(); } else { return
	 * (!buffer.isFieldEmpty(Constants.DE52)) ? "2" : "1"; } }
	 */

	public static String getBuildP61_8(IsoBuffer buffer) {
		return (!buffer.isFieldEmpty(Constants.DE52)) ? "2" : "1";
	}

	public static String getBuildP61_9(IsoBuffer buffer) {

		String entryMode = buffer.get(Constants.DE22).substring(0, 2);
		return (entryMode.equals(Constants.ENTRY_MODE_ICC_CB) || entryMode
				.equals(Constants.ENTRY_MODE_ICC_CL)) ? "1" : "0";

		/*
		 * StringBuilder s60_9 = new StringBuilder(); if
		 * (buffer.get(Constants.DE22).substring(0, 2).equals("05")) {
		 * s60_9.append("1"); } else { s60_9.append("0"); } return
		 * s60_9.toString();
		 */
	}

	public static String getServiceCodeUpdate(String value) {

		int i = value.contains("D") ? value.indexOf('D') + 1 + 4 : value
				.indexOf('=') + 1 + 4;
		if (i < value.length()) {
			return value.substring(i, i + 3);
		} else {
			return "";
		}

	}

	public static String alpha2Hex(String data) {
		char[] alpha = data.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < alpha.length; i++) {
			int count = Integer.toHexString(alpha[i]).toUpperCase().length();
			if (count <= 1) {
				sb.append("0").append(
						Integer.toHexString(alpha[i]).toUpperCase());
			} else {
				sb.append(Integer.toHexString(alpha[i]).toUpperCase());
			}
		}
		return sb.toString();
	}

	public static boolean noSplChars(char val) {
		String iChars = "~`!@#$%^&*()+=[]\\\'{}|\"<>?";
		if (iChars.indexOf(val) != -1) {
			return true;
		}
		return false;
	}

	public static String getBuildP61_10(IsoBuffer buffer) {

		String entryMode = buffer.get(Constants.DE22).substring(0, 2);

		return entryMode.equals(Constants.ENTRY_MODE_MAG_STRIPE) ? "1"
				: (entryMode.equals(Constants.ENTRY_MODE_ICC_CB) || entryMode
						.equals(Constants.ENTRY_MODE_ICC_CL)) ? "2" : "0";

		/*
		 * StringBuilder s60_10 = new StringBuilder(); if
		 * (buffer.get(Constants.DE22).substring(0, 2).equals("02")) {
		 * s60_10.append("1"); } else if
		 * (buffer.get(Constants.DE22).substring(0, 2).equals("05")) {
		 * s60_10.append("2"); } else { s60_10.append("0"); } return
		 * s60_10.toString();
		 */
	}

	/*
	 * public static void main(String[] args) {
	 * 
	 * String entryMode = "071";
	 * 
	 * switch (entryMode) {
	 * 
	 * case Constants.ENTRY_MODE_MANUAL: entryMode = "5"; break;
	 * 
	 * case Constants.ENTRY_MODE_ICC_CL: // card with contact and contactless and
	 * chip entryMode = "8"; break;
	 * 
	 * case Constants.ENTRY_MODE_ICC_CB: entryMode = "4"; break;
	 * 
	 * default: entryMode = "4"; break;
	 * 
	 * }
	 * 
	 * System.out.println(entryMode);
	 * 
	 * }
	 */
}
