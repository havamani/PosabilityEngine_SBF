package com.fss.pos.host.iso8583.benefit;

public class ISOUtil {
	
	static final char PAD_CHAR = '0';
	public static String asciiChar2hex(String iobsgAsciiString) {
		return binary2hex(asciiChar2binary(iobsgAsciiString)).toUpperCase();
	}
	
	
	public static String hex2AsciiChar(String s) {
		return binary2asciiChar(hex2binary(s));
	}
	
	public static String asciiChar2binary(String iobsgAsciiString) {
		if (iobsgAsciiString == null)
			return null;
		StringBuilder losgBinaryString = new StringBuilder();
		StringBuilder losgTemp = new StringBuilder();
		int lonuIntValue = 0;
		for (int i = 0; i < iobsgAsciiString.length(); i++) {
			lonuIntValue = iobsgAsciiString.charAt(i);
			losgTemp.append("00000000");
			losgTemp.append(Integer.toBinaryString(lonuIntValue));
			losgBinaryString.append(losgTemp.toString().substring(
					losgTemp.toString().length() - 8));
		}
		return losgBinaryString.toString();
	}	
	
	public static String byteArrayToString(byte[] bytes, int length) {
		char[] chars = new char[length];

		for (int i = 0; i != chars.length; i++) {
			chars[i] = (char) (bytes[i] & 0xff);
		}

		return new String(chars);
	}

	public static String byteArrayToString(byte[] bytes) {
		return byteArrayToString(bytes, bytes.length);
	}	
	
	
	/**
	 * Convert the passed in String to a byte array by taking the bottom 8 bits
	 * of each character it contains.
	 * 
	 * @param string
	 *            the string to be converted
	 * @return a byte array representation
	 */
	public static byte[] stringToByteArray(String string) {
		byte[] bytes = new byte[string.length()];
		char[] chars = string.toCharArray();

		for (int i = 0; i != chars.length; i++) {
			bytes[i] = (byte) chars[i];
		}

		return bytes;
	}	
	
	
	public static boolean noSplChars(char val) {
		String iChars = "~`!@#$%^&*()+=[]\\\'{}|\"<>?";
			if (iChars.indexOf(val) != -1) {
	  			return true;
	  		}
	  	return false;
	}
	
	public static String hex2binary(String hexString) {
		if (hexString == null)
			return null;
		if (hexString.length() % 2 != 0)
			hexString = "0" + hexString;
		StringBuilder binary = new StringBuilder();
		StringBuilder temp = new StringBuilder();
		for (int i = 0; i < hexString.length(); i++) {
			temp.append("0000");
			temp.append(Integer.toBinaryString(Character.digit(
					hexString.charAt(i), 16)));
			binary.append(temp.toString().substring(
					temp.toString().length() - 4));
		}
		return binary.toString();
	}

	public static String binary2asciiChar(String iobsgBinaryString) {
		StringBuilder losgCharString = new StringBuilder();
		if (iobsgBinaryString == null)
			return null;
		for (int i = 0; i < iobsgBinaryString.length(); i += 8) {
			String losgTemp = iobsgBinaryString.substring(i, i + 8);
			int lonuIntValue = 0;
			for (int k = 0, j = losgTemp.length() - 1; j >= 0; j--, k++) {
				lonuIntValue += Character.getNumericValue(losgTemp.charAt(j))
						* Math.pow(2, k);
			}
			losgCharString.append((char) lonuIntValue);
		}
		return losgCharString.toString();
	}
	
	public static String binary2hex(String iobsgBinaryString) {
		if (iobsgBinaryString == null)
			return null;
		StringBuilder losgHexString = new StringBuilder();
		for (int i = 0; i < iobsgBinaryString.length(); i += 8) {
			StringBuilder losgTemp = new StringBuilder();
			losgTemp.append(iobsgBinaryString.substring(i, i + 8));
			int lonuIntValue = 0;
			for (int k = 0, j = losgTemp.toString().length() - 1; j >= 0; j--, k++) {
				lonuIntValue += Integer.parseInt("" + losgTemp.charAt(j))
						* Math.pow(2, k);
			}
			losgTemp.append("0").append(Integer.toHexString(lonuIntValue));
			losgHexString.append(losgTemp.toString().substring(
					losgTemp.toString().length() - 2));
		}
		return losgHexString.toString().toUpperCase();
	}
	
	
	  public static String padRightZero(StringBuffer s, int i)
	    {
		   StringBuffer stringbuffer = new StringBuffer(i);
	        int j = i - s.length();
	        for(int k = 0; k < j; k++)
	            stringbuffer.append("0");

	        s.append(stringbuffer.toString());
	        return s.toString();
	    }


	public static byte[] asciiToEbcdic(byte[] a) {
		byte[] e = new byte[a.length];
		for (int i = 0; i < a.length; i++)
			e[i] = ASCII2EBCDIC[a[i] & 0xFF];
		return e;
	}

	public static final byte[] ASCII2EBCDIC = new byte[] { (byte) 0x0,
			(byte) 0x1, (byte) 0x2, (byte) 0x3, (byte) 0x37, (byte) 0x2D,
			(byte) 0x2E, (byte) 0x2F, (byte) 0x16, (byte) 0x5, (byte) 0x15,
			(byte) 0xB, (byte) 0xC, (byte) 0xD, (byte) 0xE, (byte) 0xF,
			(byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x13, (byte) 0x3C,
			(byte) 0x3D, (byte) 0x32, (byte) 0x26, (byte) 0x18, (byte) 0x19,
			(byte) 0x3F, (byte) 0x27, (byte) 0x1C, (byte) 0x1D, (byte) 0x1E,
			(byte) 0x1F, (byte) 0x40, (byte) 0x5A, (byte) 0x7F, (byte) 0x7B,
			(byte) 0x5B, (byte) 0x6C, (byte) 0x50, (byte) 0x7D, (byte) 0x4D,
			(byte) 0x5D, (byte) 0x5C, (byte) 0x4E, (byte) 0x6B, (byte) 0x60,
			(byte) 0x4B, (byte) 0x61, (byte) 0xF0, (byte) 0xF1, (byte) 0xF2,
			(byte) 0xF3, (byte) 0xF4, (byte) 0xF5, (byte) 0xF6, (byte) 0xF7,
			(byte) 0xF8, (byte) 0xF9, (byte) 0x7A, (byte) 0x5E, (byte) 0x4C,
			(byte) 0x7E, (byte) 0x6E, (byte) 0x6F, (byte) 0x7C, (byte) 0xC1,
			(byte) 0xC2, (byte) 0xC3, (byte) 0xC4, (byte) 0xC5, (byte) 0xC6,
			(byte) 0xC7, (byte) 0xC8, (byte) 0xC9, (byte) 0xD1, (byte) 0xD2,
			(byte) 0xD3, (byte) 0xD4, (byte) 0xD5, (byte) 0xD6, (byte) 0xD7,
			(byte) 0xD8, (byte) 0xD9, (byte) 0xE2, (byte) 0xE3, (byte) 0xE4,
			(byte) 0xE5, (byte) 0xE6, (byte) 0xE7, (byte) 0xE8, (byte) 0xE9,
			(byte) 0xAD, (byte) 0xE0, (byte) 0xBD, (byte) 0x5F, (byte) 0x6D,
			(byte) 0x79, (byte) 0x81, (byte) 0x82, (byte) 0x83, (byte) 0x84,
			(byte) 0x85, (byte) 0x86, (byte) 0x87, (byte) 0x88, (byte) 0x89,
			(byte) 0x91, (byte) 0x92, (byte) 0x93, (byte) 0x94, (byte) 0x95,
			(byte) 0x96, (byte) 0x97, (byte) 0x98, (byte) 0x99, (byte) 0xA2,
			(byte) 0xA3, (byte) 0xA4, (byte) 0xA5, (byte) 0xA6, (byte) 0xA7,
			(byte) 0xA8, (byte) 0xA9, (byte) 0xC0, (byte) 0x4F, (byte) 0xD0,
			(byte) 0xA1, (byte) 0x7, (byte) 0x20, (byte) 0x21, (byte) 0x22,
			(byte) 0x23, (byte) 0x24, (byte) 0x25, (byte) 0x6, (byte) 0x17,
			(byte) 0x28, (byte) 0x29, (byte) 0x2A, (byte) 0x2B, (byte) 0x2C,
			(byte) 0x9, (byte) 0xA, (byte) 0x1B, (byte) 0x30, (byte) 0x31,
			(byte) 0x1A, (byte) 0x33, (byte) 0x34, (byte) 0x35, (byte) 0x36,
			(byte) 0x8, (byte) 0x38, (byte) 0x39, (byte) 0x3A, (byte) 0x3B,
			(byte) 0x4, (byte) 0x14, (byte) 0x3E, (byte) 0xFF, (byte) 0x41,
			(byte) 0xAA, (byte) 0x4A, (byte) 0xB1, (byte) 0x9F, (byte) 0xB2,
			(byte) 0x6A, (byte) 0xB5, (byte) 0xBB, (byte) 0xB4, (byte) 0x9A,
			(byte) 0x8A, (byte) 0xB0, (byte) 0xCA, (byte) 0xAF, (byte) 0xBC,
			(byte) 0x90, (byte) 0x8F, (byte) 0xEA, (byte) 0xFA, (byte) 0xBE,
			(byte) 0xA0, (byte) 0xB6, (byte) 0xB3, (byte) 0x9D, (byte) 0xDA,
			(byte) 0x9B, (byte) 0x8B, (byte) 0xB7, (byte) 0xB8, (byte) 0xB9,
			(byte) 0xAB, (byte) 0x64, (byte) 0x65, (byte) 0x62, (byte) 0x66,
			(byte) 0x63, (byte) 0x67, (byte) 0x9E, (byte) 0x68, (byte) 0x74,
			(byte) 0x71, (byte) 0x72, (byte) 0x73, (byte) 0x78, (byte) 0x75,
			(byte) 0x76, (byte) 0x77, (byte) 0xAC, (byte) 0x69, (byte) 0xED,
			(byte) 0xEE, (byte) 0xEB, (byte) 0xEF, (byte) 0xEC, (byte) 0xBF,
			(byte) 0x80, (byte) 0xFD, (byte) 0xFE, (byte) 0xFB, (byte) 0xFC,
			(byte) 0xBA, (byte) 0xAE, (byte) 0x59, (byte) 0x44, (byte) 0x45,
			(byte) 0x42, (byte) 0x46, (byte) 0x43, (byte) 0x47, (byte) 0x9C,
			(byte) 0x48, (byte) 0x54, (byte) 0x51, (byte) 0x52, (byte) 0x53,
			(byte) 0x58, (byte) 0x55, (byte) 0x56, (byte) 0x57, (byte) 0x8C,
			(byte) 0x49, (byte) 0xCD, (byte) 0xCE, (byte) 0xCB, (byte) 0xCF,
			(byte) 0xCC, (byte) 0xE1, (byte) 0x70, (byte) 0xDD, (byte) 0xDE,
			(byte) 0xDB, (byte) 0xDC, (byte) 0x8D, (byte) 0x8E, (byte) 0xDF };
	
	private static final String HEX_DIGITS = "0123456789abcdef";
	
	public static String byteArrayToHexString(byte[] data, int length) {
		StringBuffer buf = new StringBuffer();

		for (int i = 0; i != length; i++) {
			int v = data[i] & 0xff;

			buf.append(HEX_DIGITS.charAt(v >> 4));
			buf.append(HEX_DIGITS.charAt(v & 0xf));
		}

		return buf.toString();
	}

	/**
	 * Return the passed in byte array as a hex string.
	 * 
	 * @param data
	 *            the bytes to be converted.
	 * @return a hex representation of data.
	 */
	public static String byteArrayToHexString(byte[] data) {
		return byteArrayToHexString(data, data.length);
	}		
	
	public static int toDecimal(char[] graphical) {
		
		int length = graphical.length;
		int decimal = 0;
		
		for (int i = 0; i < graphical.length; i++) {
			length--;
			decimal += graphical[i] * (long) Math.pow(16, 2 * length);
		}
		return decimal;
	}
	
	/** Convert decimal to graphical equivalent */
	public static char[] toGraphical(long decimal, int length) {
		
		char[] graphical = new char[length];
		
		for (int i = 0; i < graphical.length; i++) {
			length--;
			graphical[i] = (char) (decimal / Math.pow(16, 2 * length));
			decimal %= Math.pow(16, 2 * length);
		}
		return graphical;
	}		

	/**
	 * @MethodName leftPadZeros
	 * @Return String
	 */
	public static String leftPadZeros(String ilosgStr, int ilonuLength) {
		StringBuilder sb = new StringBuilder(ilonuLength);
		while (sb.length() < (ilonuLength - ilosgStr.length()))
			sb.append(PAD_CHAR);
		sb.append(ilosgStr);
		return sb.toString();
	}
	
	public static String alpha2Hex(String data)
	{
		char[] alpha = data.toCharArray();
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < alpha.length; i++)
		{
			int count = Integer.toHexString(alpha[i]).toUpperCase().length();
			if(count <= 1)
			{
				sb.append("0").append(Integer.toHexString(alpha[i]).toUpperCase());
			}
			else
			{
				sb.append(Integer.toHexString(alpha[i]).toUpperCase());
			}
		}
		if(sb != null)
		{
			//"Hexabyte Value Returned";
		}

		
		return sb.toString();
	}
}
