package com.fss.pos.host.iso8583.cups;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.fss.pos.base.commons.Config;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.host.AbstractHostApi;

public class CupsApi  extends AbstractHostApi {

	@Autowired
	private Config config;

	private static final List<String> CONTAINS_SUB_FIELDS;
	private static final List<String> CONTAINS_SUB_FIELDS_DE;
	private static final String FIELD_PREFIX = "F-";
	private static final String FIELD_PREFIX_SUBFIELD = "S-";
	private static final String DISABLED = "*";

	static {
		ArrayList<String> l = new ArrayList<String>();
	//	l.add("F-44");
	//	l.add("F-60");
	//	l.add("F-62");
	//	l.add("F-63");
	//	l.add("F-126");
		CONTAINS_SUB_FIELDS = Collections.unmodifiableList(l);

		ArrayList<String> l1 = new ArrayList<String>();
	//	l1.add("P-44");
	//l1.add("P-60");
	//	l1.add("P-62");
	//	l1.add("P-63");
	//	l1.add("S-126");
		CONTAINS_SUB_FIELDS_DE = Collections.unmodifiableList(l1);
	}

	@Override
	public IsoBuffer parse(String message) throws PosException {

		CupsMessageMap cupsMessage = new CupsMessageMap();

		for (int i = 0; i <= 192; i++)
			cupsMessage.setElement(FIELD_PREFIX + i, DISABLED);

		if (!cupsMessage.unpack(message.substring(50)))
			throw new PosException(Constants.ERR_SYSTEM_ERROR);

		IsoBuffer isoBuffer = new IsoBuffer();
		isoBuffer.fillBuffer(true, true, true);
		isoBuffer.put(Constants.ISO_MSG_TYPE,
				cupsMessage.getElement(CupsConstants.MSG_TYP));

		copyFieldsToBuffer(IsoBuffer.PREFIX_PRIMARY, isoBuffer, cupsMessage, 1,
				64);
		copyFieldsToBuffer(IsoBuffer.PREFIX_SECONDARY, isoBuffer, cupsMessage,
				65, 128);
		copyFieldsToBuffer(IsoBuffer.PREFIX_TERTIARY, isoBuffer, cupsMessage,
				129, 192);

		return isoBuffer;
	}

	@Override
	public String build(IsoBuffer isoBuffer) {

		CupsMessageMap message = new CupsMessageMap();



		message.setElement(CupsConstants.MSG_TYP,
				isoBuffer.get(CupsConstants.ISO_MSG_TYPE));

		for (int i = 1; i <= 192; i++)
			message.setElement(FIELD_PREFIX + i, DISABLED);
		copyFieldsFromBuffer(IsoBuffer.PREFIX_PRIMARY, isoBuffer, message, 1,
				64);
		copyFieldsFromBuffer(IsoBuffer.PREFIX_SECONDARY, isoBuffer, message,
				65, 128);
		copyFieldsFromBuffer(IsoBuffer.PREFIX_TERTIARY, isoBuffer, message,
				129, 192);

		/*
		 * Log.debug("Cups Message in Hex Value :::::::::::", IsoUtil
		 * .asciiChar2hex(new String(message.pack(),
		 * StandardCharsets.ISO_8859_1)));
		 */
		return new String(message.pack(), StandardCharsets.ISO_8859_1);
	}

	private void copyFieldsFromBuffer(String prefix, IsoBuffer isoBuffer,
			CupsMessage message, int startPos, int endPos) {
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
			CupsMessage message, int startPos, int endPos) {
		for (int i = startPos; i <= endPos; i++) {
			String key = FIELD_PREFIX + i;
			if (CONTAINS_SUB_FIELDS.contains(key)) {
				// if ("Y".equals(message.getElement(FIELD_PREFIX + i))) {
				IsoBuffer tempBuffer = new IsoBuffer();
				tempBuffer.fillBuffer(true, true, true);
				for (int j = 1; j <= 192; j++) {
					String val = message.getElement(FIELD_PREFIX_SUBFIELD + i
							+ "." + j);
					tempBuffer.put(prefix + j, val == null ? DISABLED : val);
				}
				isoBuffer.putBuffer(prefix + i, tempBuffer);
				// }
			} else {
				isoBuffer.put(prefix + i, message.getElement(key));
			}
		}
	}
	

}
