package com.fss.pos.host.iso8583.jcb;

public class JcbISOUtil {

	public static String appendChar(String data, char ch, int totalLen,
			boolean appendLeft) {
		if (data == null)
			data = "";
		StringBuilder sb = new StringBuilder();
		if (!appendLeft)
			sb.append(data);
		for (int i = 1; i <= (totalLen - data.length()); i++)
			sb.append(ch);
		if (appendLeft)
			sb.append(data);
		return sb.toString();
	}

	public static boolean noSplChars(char val) {
		String iChars = "~`!@#$%^&*()+=[]\\\'{}|\"<>?";
		if (iChars.indexOf(val) != -1) {
			return true;
		}
		return false;
	}

	public static String padRightZero(StringBuffer s, int i) {
		StringBuffer stringbuffer = new StringBuffer(i);
		int j = i - s.length();
		for (int k = 0; k < j; k++)
			stringbuffer.append("0");

		s.append(stringbuffer.toString());
		return s.toString();
	}

	public static String binary2hex(final String binaryString) {

		if (binaryString == null) {
			return null;
		}

		String hexString = "";

		for (int i = 0; i < binaryString.length(); i += 8) {

			String losgTemp = binaryString.substring(i, i + 8);

			int value = 0;

			for (int k = 0, j = losgTemp.length() - 1; j >= 0; j--, k++) {
				value += Integer.parseInt("" + losgTemp.charAt(j))
						* Math.pow(2, k);
			}
			losgTemp = "0" + Integer.toHexString(value);

			hexString += losgTemp.substring(losgTemp.length() - 2);
		}
		return hexString.toUpperCase();
	}

	public static String hex2binary(String hexString) {

		if (hexString == null) {
			return null;
		}

		// for(int j=0;j<hexString.length();j++)
		// {
		// if(noSplChars(hexString.charAt(j)))
		// {
		// hexString=hexString.replaceAll(hexString.charAt(j)+"",
		// ISOUtil.alpha2Hex(hexString.charAt(j)+""));
		// }
		// }

		if (hexString.length() % 2 != 0) {
			hexString = "0" + hexString;
		}

		// hexString="40345678231234563D030912312345000";
		String binary = "";
		String temp = "";

		for (int i = 0; i < hexString.length(); i++) {

			temp = "0000"
					+ Integer.toBinaryString(Character.digit(
							hexString.charAt(i), 16));
			binary += temp.substring(temp.length() - 4);
		}
		return binary;
	}

	public static String binary2asciiChar(String binaryString) {

		if (binaryString == null)
			return null;

		String charString = "";

		for (int i = 0; i < binaryString.length(); i += 8) {

			String temp = binaryString.substring(i, i + 8);
			int intValue = 0;

			for (int k = 0, j = temp.length() - 1; j >= 0; j--, k++) {
				intValue += Integer.parseInt("0" + temp.charAt(j))
						* Math.pow(2, k);
			}

			charString += (char) intValue;
		}

		return charString;
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
		if (sb != null) {
			// "Hexabyte Value Returned";
		}

		return sb.toString();
	}

	public static String hex2AsciiChar(String s) {
		return binary2asciiChar(hex2binary(s));
	}

	public static String asciiChar2hex(String iobsgAsciiString) {
		return binary2hex(asciiChar2binary(iobsgAsciiString)).toUpperCase();
	}

	public static String asciiChar2binary(final String asciiString) {

		if (asciiString == null) {
			return null;
		}

		String finalBinaryString = "";
		String tempBinaryString = "";

		int value = 0;

		for (int i = 0; i < asciiString.length(); i++) {

			value = asciiString.charAt(i);
			tempBinaryString = "00000000" + Integer.toBinaryString(value);

			finalBinaryString += tempBinaryString.substring(tempBinaryString
					.length() - 8);
		}

		return finalBinaryString;
	}

}
