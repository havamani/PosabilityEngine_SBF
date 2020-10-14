package com.fss.pos.host.iso8583.jcb;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.host.AbstractHostApi;
import com.fss.pos.host.iso8583.cups.CupsConstants;

public class JcbApi extends AbstractHostApi {

	private static final List<String> CONTAINS_SUB_FIELDS_DE;
	private static final List<String> CONTAINS_SUB_FIELDS;
	private static final String FIELD_PREFIX = "F-";
	private static final String FIELD_PREFIX_SUBFIELD = "S-";
	private static final String DISABLED = "*";

	static {
		ArrayList<String> l = new ArrayList<String>();
		CONTAINS_SUB_FIELDS = Collections.unmodifiableList(l);

		ArrayList<String> l1 = new ArrayList<String>();
		CONTAINS_SUB_FIELDS_DE = Collections.unmodifiableList(l1);
	}

	@Override
	public IsoBuffer parse(String message) throws PosException {
		JcbMessage msg = new JcbMessage();

		for (int i = 0; i <= 192; i++)
			msg.setElement(FIELD_PREFIX + i, DISABLED);

		Map<String, String> unpack = msg.unpack(message);

		IsoBuffer isoBuffer = new IsoBuffer();
		isoBuffer.put(Constants.ISO_MSG_TYPE, unpack.get(CupsConstants.MSG_TYP));

		copyFieldsToBuffer(IsoBuffer.PREFIX_PRIMARY, isoBuffer, unpack, 1, 64);
		copyFieldsToBuffer(IsoBuffer.PREFIX_SECONDARY, isoBuffer, unpack, 65,
				128);

		return isoBuffer;
	}

	@Override
	protected String build(IsoBuffer isoBuffer) {

		JcbMessage jcbMsg = new JcbMessage();
		
		String msGType = isoBuffer.get(Constants.ISO_MSG_TYPE);
		try {
			msGType = (new String(msGType.getBytes("Cp1047"), "ISO8859_1"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		jcbMsg.setElement(JcbConstants.MSG_TYP,msGType);

		for (int i = 1; i <= 128; i++)
			jcbMsg.setElement(FIELD_PREFIX + i, DISABLED);

		copyFieldsFromBuffer(IsoBuffer.PREFIX_PRIMARY, isoBuffer, jcbMsg, 1, 64);
		copyFieldsFromBuffer(IsoBuffer.PREFIX_SECONDARY, isoBuffer, jcbMsg, 65,
				128);
		return new String(jcbMsg.pack(), StandardCharsets.ISO_8859_1);
	}

	private void copyFieldsFromBuffer(String prefix, IsoBuffer isoBuffer,
			JcbMessage message, int startPos, int endPos) {
		for (int i = startPos; i <= endPos; i++) {
			String key = prefix + i;
			if (CONTAINS_SUB_FIELDS_DE.contains(key)) {
				if (!isoBuffer.isFieldEmpty(key)) {
					IsoBuffer tempBuffer = isoBuffer.getBuffer(key);
					if (tempBuffer.isAllFieldsEmpty(false, false)) {
						message.setElement(FIELD_PREFIX + i, DISABLED);
					} else {
						message.setElement(FIELD_PREFIX + i, "Y");
						for (int j = 1; j <= 192; j++) {
							String inKey = prefix + j;
							if (!tempBuffer.isFieldEmpty(inKey)
									&& null != tempBuffer.get(inKey)) {
								message.setElement(FIELD_PREFIX_SUBFIELD + i
										+ "." + j, tempBuffer.get(inKey));
							}
						}
					}
				}
			} else {

				message.setElement(FIELD_PREFIX + i, isoBuffer.get(key));
			}
		}
	}

	private void copyFieldsToBuffer(String prefix, IsoBuffer isoBuffer,
			Map<String, String> message, int startPos, int endPos) {
		for (int i = startPos; i <= endPos; i++) {
			String key = FIELD_PREFIX + i;
			if (CONTAINS_SUB_FIELDS.contains(key)) {
				// if ("Y".equals(message.getElement(FIELD_PREFIX + i))) {
				IsoBuffer tempBuffer = new IsoBuffer();
				tempBuffer.fillBuffer(true, true, true);
				for (int j = 1; j <= 192; j++) {
					String val = message.get(FIELD_PREFIX_SUBFIELD + i + "."
							+ j);
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
