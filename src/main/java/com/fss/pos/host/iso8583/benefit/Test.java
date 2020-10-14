package com.fss.pos.host.iso8583.benefit;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;


public class Test {
	
	private static final String FIELD_PREFIX = "F-";
	private static final String FIELD_PREFIX_SUBFIELD = "S-";
	private static final String DISABLED = "*";
	private static final List<String> CONTAINS_SUB_FIELDS_DE;

	static {
		ArrayList<String> l1 = new ArrayList<String>();
		CONTAINS_SUB_FIELDS_DE = Collections.unmodifiableList(l1);
	}

	
	/*
	 * public static void main(String[] args) throws PosException {
	 * 
	 * String message = "="; byte[] messageByte = Base64.decodeBase64(message);
	 * message = new String(messageByte, StandardCharsets.ISO_8859_1); message =
	 * IsoUtil.asciiChar2hex(message);
	 * 
	 * IsoBuffer buf =
	 * parse("r5ISO0250000770210B23EC6012EC1829800000000100000000000000000000020000428094334000390124333042822011116111658120510011104819000000214550120267574020=22019118125222785011040097575966        100003257      027000100010001000100058120BHR048012..._��//\\.d0000351001600000000+000000001900000000100000000001104819000000"
	 * ); }
	 */
	
	public static IsoBuffer parse(String message) throws PosException {

		IsoBuffer buffer = new IsoBuffer();
		String header = message.substring(0, 14);
		Log.debug("Header of benefit::", header);
		message = message.substring(14);
	
		BenefitMessage msg = new BenefitMessage();
		msg = msg.unpack(message);
		copyFieldsToBuffer(IsoBuffer.PREFIX_PRIMARY, buffer, msg, 1,
				64);
		copyFieldsToBuffer(IsoBuffer.PREFIX_SECONDARY, buffer, msg,
				65, 128);
		buffer.put(Constants.ISO_MSG_TYPE, msg.getElement(Constants.ISO_MSG_TYPE));
		return buffer;
	}
	
	private static void copyFieldsToBuffer(String prefix, IsoBuffer buffer,
			BenefitMessage msg, int startPos, int endPos) {
		for (int i = startPos; i <= endPos; i++) {
			String key = FIELD_PREFIX + i;
			if (CONTAINS_SUB_FIELDS_DE.contains(key)) {
				// if ("Y".equals(message.getElement(FIELD_PREFIX + i))) {
				IsoBuffer tempBuffer = new IsoBuffer();
				tempBuffer.fillBuffer(true, true, true);
				for (int j = 1; j <= 192; j++) {
					String val = msg.getElement(FIELD_PREFIX_SUBFIELD + i
							+ "." + j);
					tempBuffer.put(prefix + j, val == null ? DISABLED : val);
				}
				buffer.putBuffer(prefix + i, tempBuffer);
				// }
			} else {
				if(i==55){
					String data = new String(msg.getElement(key).getBytes(),StandardCharsets.ISO_8859_1);
					//Log.debug("55 charset data", data);
					buffer.put(prefix + i, IsoUtil.hex2AsciiChar(msg.getElement(key)));
					//Log.debug("pakka 55 data", buffer.get(Constants.DE55));
				}else{
					buffer.put(prefix + i, msg.getElement(key));
				}
			}
		}
	}

}
