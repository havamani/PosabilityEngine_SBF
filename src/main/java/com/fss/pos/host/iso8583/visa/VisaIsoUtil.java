package com.fss.pos.host.iso8583.visa;

public class VisaIsoUtil {


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
	
	public static String padRightZero(StringBuffer s, int i) {
		StringBuffer stringbuffer = new StringBuffer(i);
		int j = i - s.length();
		for (int k = 0; k < j; k++)
			stringbuffer.append("0");

		s.append(stringbuffer.toString());
		return s.toString();
	}

	public static boolean noSplChars(char val) {
		String iChars = "~`!@#$%^&*()+=[]\\\'{}|\"<>?";
		if (iChars.indexOf(val) != -1) {
			return true;
		}
		return false;
	}

}
