package com.fss.pos.host.iso8583.jcb;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.host.iso8583.cups.CupsConstants;

public class Test {

	private static final String FIELD_PREFIX = "F-";
	private static final String DISABLED = "*";

	private static final List<String> CONTAINS_SUB_FIELDS_DE;
	private static final List<String> CONTAINS_SUB_FIELDS;
	private static final String FIELD_PREFIX_SUBFIELD = "S-";

	static {
		ArrayList<String> l = new ArrayList<String>();
		CONTAINS_SUB_FIELDS = Collections.unmodifiableList(l);

		ArrayList<String> l1 = new ArrayList<String>();
		CONTAINS_SUB_FIELDS_DE = Collections.unmodifiableList(l1);
	}

	/*
	 * public static void main(String[] args) throws UnsupportedEncodingException {
	 * 
	 * String mesg =
	 * "8PHx8HI8AAGOQIYAEDVpmQASKXEiMAAAAAAAADAABAQJIFYAAgcSIFYEBCESCIiAQAAIiCJwAPnw+fTx8vDw+PX19Pf08\\/f38vDw8PDw8PDw8PDw8PDw8PH48PT4KPDw8PHw9PjD8PDw8PDw8PXw8PDw8PDw8vD0+MTw8PDw8PDw8PL0+PAADJEK4WAR2x6pkhkwMA==";
	 * 
	 * byte[] messageByte = null; messageByte = Base64.decodeBase64(mesg); mesg =
	 * new String(messageByte, StandardCharsets.ISO_8859_1); JcbMessage msg = new
	 * JcbMessage(); for (int i = 0; i <= 192; i++) msg.setElement(FIELD_PREFIX + i,
	 * DISABLED); Map<String, String> unpack = msg.unpack(mesg);
	 * 
	 * IsoBuffer isoBuffer = new IsoBuffer(); isoBuffer .put(Constants.ISO_MSG_TYPE,
	 * unpack.get(CupsConstants.MSG_TYP));
	 * 
	 * copyFieldsToBuffer(IsoBuffer.PREFIX_PRIMARY, isoBuffer, unpack, 1, 64);
	 * copyFieldsToBuffer(IsoBuffer.PREFIX_SECONDARY, isoBuffer, unpack, 65,128);
	 * 
	 * 
	 * 
	 * String data = "0314123      1234504010"; String ebc = ((new String(new
	 * String(data .getBytes(), "ISO8859_1") .getBytes("Cp1047")))); //
	 * 07F0F1F0F3F1F2F3 // F0F1F0F3F1F2F3
	 * 
	 * String length = JcbISOUtil.binary2hex(JcbISOUtil.hex2binary(Integer
	 * .toHexString(data.length())));
	 * 
	 * length = JcbISOUtil.hex2AsciiChar(length); //String length =
	 * JcbISOUtil.hex2AsciiChar(Integer.toString(data.length()));
	 * 
	 * 
	 * 
	 * 
	 * String message =
	 * "ARAAAPD28vACIAAAAAEAAAMYCVcxAAAW+zAxMDByPESBqOGADBY1aZkAEjAAWAAAAAAAAAAQAAMYCVcYAAAwElcYAxghEnARAhAABhYgAgYWIAI4NWmZABIwAFg9IRIQEAAAAAAAAEY5RjBGN0Y3RjFGMkYwRjBGN0Y0RjlGNUYwRjBGMEYwRjFGMUY4RjBGMEYwRjBGMEYwRjBGMEYwRjBGMEYwRjJGMEY2RjNFM0M1RTJFMzQwRTNDNUQ5RDRDOUQ1QzFEMzQwNDA0MDQwNDA0MDQwNDA0MDQwRDRDMUQ1QzFENEMxNDA0MDQwNDA0MDQwNDA0MEMyQzhDNBgDFBJf///wQBBGMEY0RjgGIiBIAAAw"
	 * ;
	 * 
	 * byte[] messageByte = null; messageByte = Base64.decodeBase64(message);
	 * message = new String(messageByte, StandardCharsets.ISO_8859_1);
	 * 
	 * message = message.substring(4); JcbMessage msg = new JcbMessage();
	 * 
	 * for (int i = 0; i <= 192; i++) msg.setElement(FIELD_PREFIX + i, DISABLED);
	 * 
	 * Map<String, String> unpack = msg.unpack(message);
	 * 
	 * }
	 */

	@SuppressWarnings("unused")
	private static void copyFieldsToBuffer(String prefix, IsoBuffer isoBuffer, Map<String, String> message,
			int startPos, int endPos) {
		for (int i = startPos; i <= endPos; i++) {
			String key = FIELD_PREFIX + i;
			if (CONTAINS_SUB_FIELDS.contains(key)) {
				// if ("Y".equals(message.getElement(FIELD_PREFIX + i))) {
				IsoBuffer tempBuffer = new IsoBuffer();
				tempBuffer.fillBuffer(true, true, true);
				for (int j = 1; j <= 192; j++) {
					String val = message.get(FIELD_PREFIX_SUBFIELD + i + "." + j);
					tempBuffer.put(prefix + j, val == null ? DISABLED : val);
				}
				isoBuffer.putBuffer(prefix + i, tempBuffer);
				// }
			} else {
				isoBuffer.put(prefix + i, message.get(key));
			}
		}
	}

}
