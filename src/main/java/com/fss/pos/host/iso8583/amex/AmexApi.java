package com.fss.pos.host.iso8583.amex;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.host.AbstractHostApi;

public class AmexApi extends AbstractHostApi {

	@Autowired
	private AmexIso93Api amexIso93Api;

	private static final List<String> CONTAINS_SUB_FIELDS;
	private static final String FIELD_PREFIX = "F-";
	private static final String FIELD_PREFIX_SUBFIELD = "S-";

	private static final String DISABLED = "*";

	static {
		ArrayList<String> l = new ArrayList<String>();
	
		CONTAINS_SUB_FIELDS = Collections.unmodifiableList(l);
	}

	@Override
	public IsoBuffer parse(String message) throws PosException {

		AmexMessageMap visaMessage = new AmexMessageMap();

		for (int i = 0; i <= 128; i++)
			visaMessage.setElement(FIELD_PREFIX + i, DISABLED);

		Map<String, String> unpack = visaMessage.unpack(message);

		IsoBuffer isoBuffer = new IsoBuffer();
		isoBuffer.put(AmexConstants.MSG_TYP, unpack.get(AmexConstants.MSG_TYP));
	
		copyFieldsToBuffer(IsoBuffer.PREFIX_PRIMARY, 1, 64, unpack,
				isoBuffer);
		copyFieldsToBuffer(IsoBuffer.PREFIX_SECONDARY, 65, 128, unpack,
				isoBuffer);
		
		if(isoBuffer.get(Constants.DE38).equals("404040404040")){
			isoBuffer.put(Constants.DE38, " ");
		}
		String de42 = isoBuffer.get(Constants.DE42).substring(0, 10);
		isoBuffer.put(Constants.DE42, de42);
		
		if(isoBuffer.isFieldEmpty(Constants.DE37)){
			isoBuffer.put(Constants.DE37, "");
		}
		if(isoBuffer.isFieldEmpty(Constants.DE41)){
			isoBuffer.put(Constants.DE41, "");
		}
	
		return isoBuffer;
	}
	

	private void copyFieldsToBuffer(String prefix, int startPos, int endPos,
			Map<String, String> unpack, IsoBuffer isoBuffer) {
		for (int i = startPos; i <= endPos; i++) {
			String key = FIELD_PREFIX + i;
				isoBuffer.put(prefix + i, unpack.get(key));
			}
	}
	

	@Override
	public String build(IsoBuffer isoBuffer) {

		AmexMessageMap message = new AmexMessageMap();

		//Log.debug("amex buffer", isoBuffer.toString());

		String msGType = isoBuffer.get(Constants.ISO_MSG_TYPE);
		try {
			msGType = (new String(msGType.getBytes("Cp1047"), "ISO8859_1"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		message.setElement(AmexConstants.MSG_TYP, msGType);

		for (int i = 1; i <= 128; i++)
			message.setElement(FIELD_PREFIX + i, DISABLED);

		copyFieldsFromBuffer(IsoBuffer.PREFIX_PRIMARY, isoBuffer, message, 1,
				64);
		copyFieldsFromBuffer(IsoBuffer.PREFIX_SECONDARY, isoBuffer, message,
				65, 128);

		/*
		 * copyFieldsFromBuffer(FIELD_PREFIX_TERTIARY, isoBuffer, message, 129,
		 * 192);
		 */

		return new String(message.pack(), StandardCharsets.ISO_8859_1);

		// return
		// amexIso93Api.buildIso87Request(isoBuffer.get(Constants.ISO_MSG_TYPE),
		// isoBuffer);
	}

	private void copyFieldsFromBuffer(String prefix, IsoBuffer isoBuffer,
			AmexMessage message, int startPos, int endPos) {
		for (int i = startPos; i <= endPos; i++) {
			String key = prefix + i;
			if (isoBuffer.hasSubFields(key)
					|| CONTAINS_SUB_FIELDS.contains(key)) {
				if (!isoBuffer.isFieldEmpty(key)) {
					IsoBuffer tempBuffer = isoBuffer.getBuffer(key);
					if (tempBuffer.isAllFieldsEmpty(false, false)) {
						message.setElement(FIELD_PREFIX + i, DISABLED);
					} else {
						message.setElement(FIELD_PREFIX + i, "Y");
						for (int j = 1; j <= 128; j++) {
							key = prefix + j;
							if (!tempBuffer.isFieldEmpty(key)
									&& null != tempBuffer.get(key)) {
								message.setElement(FIELD_PREFIX_SUBFIELD + i
										+ "." + j, tempBuffer.get(key));
							}
						}
					}
				}
			} else {
				message.setElement(FIELD_PREFIX + i, isoBuffer.get(key));

			}
		}
	}


}
