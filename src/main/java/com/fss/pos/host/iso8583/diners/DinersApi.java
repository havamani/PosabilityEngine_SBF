package com.fss.pos.host.iso8583.diners;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.host.AbstractHostApi;
import com.fss.pos.host.iso8583.visa.VisaConstants;


public class DinersApi extends AbstractHostApi {

	@Autowired
	private DinersIso93Api dinersIso93Api;

	private static final List<String> CONTAINS_SUB_FIELDS;
	private static final List<String> CONTAINS_SUB_FIELDS_DE;

	static {
		ArrayList<String> l = new ArrayList<String>();
		CONTAINS_SUB_FIELDS = Collections.unmodifiableList(l);

		ArrayList<String> l1 = new ArrayList<String>();
		CONTAINS_SUB_FIELDS_DE = Collections.unmodifiableList(l1);
	}
	@Override
	public IsoBuffer parse(String message) throws PosException {
		DinersMessage dinersmessage = new DinersMessage();

		for (int i = 0; i <= 192; i++)
			dinersmessage.setElement(Constants.FIELD_PREFIX + i, Constants.DISABLED);

		if (!dinersmessage.unpack(message.getBytes(StandardCharsets.ISO_8859_1)))
			throw new PosException(Constants.ERR_SYSTEM_ERROR);

		IsoBuffer isoBuffer = new IsoBuffer();
		isoBuffer.fillBuffer(true, true, true);
		isoBuffer.put(Constants.ISO_MSG_TYPE,
				dinersmessage.getElement(VisaConstants.MSG_TYP));

		copyFieldsToBuffer(IsoBuffer.PREFIX_PRIMARY, isoBuffer, dinersmessage, 1,
				64);
		copyFieldsToBuffer(IsoBuffer.PREFIX_SECONDARY, isoBuffer, dinersmessage,
				65, 128);
		copyFieldsToBuffer(IsoBuffer.PREFIX_TERTIARY, isoBuffer, dinersmessage,
				129, 192);

		return isoBuffer;
	}
@Override
	public String build(IsoBuffer isoBuffer) {
		DinersMessage message = new DinersMessage();
		message.setElement(VisaConstants.MSG_TYP,
				isoBuffer.get(Constants.ISO_MSG_TYPE));

		for (int i = 1; i <= 192; i++)
			message.setElement(Constants.FIELD_PREFIX + i, Constants.DISABLED);

		copyFieldsFromBuffer(IsoBuffer.PREFIX_PRIMARY, isoBuffer, message, 1,
				64);
		copyFieldsFromBuffer(IsoBuffer.PREFIX_SECONDARY, isoBuffer, message,
				65, 128);
		/*copyFieldsFromBuffer(IsoBuffer.PREFIX_TERTIARY, isoBuffer, message,
				129, 192);*/
		return new String(message.pack(), StandardCharsets.ISO_8859_1);
	}
	
	private void copyFieldsFromBuffer(String prefix, IsoBuffer isoBuffer,
			DinersMessage message, int startPos, int endPos) {
		for (int i = startPos; i <= endPos; i++) {
			String hipen;
			String key = prefix + i;
			if (CONTAINS_SUB_FIELDS_DE.contains(key)) {
				if (!isoBuffer.isFieldEmpty(key)) {
					IsoBuffer tempBuffer = isoBuffer.getBuffer(key);
					if (tempBuffer.isAllFieldsEmpty(false, false)) {
						message.setElement(Constants.FIELD_PREFIX + i, Constants.DISABLED);
					} else {
						message.setElement(Constants.FIELD_PREFIX + i, "Y");
						for (int j = 1; j <= 192; j++) {
							if(j<=64){
								hipen = IsoBuffer.PREFIX_PRIMARY;
							}else if(j>64 && j<=128){
								hipen = IsoBuffer.PREFIX_SECONDARY;
							}else{
								hipen = IsoBuffer.PREFIX_TERTIARY;
							}
							String inKey = hipen + j;
							if (!tempBuffer.isFieldEmpty(inKey)
									&& null != tempBuffer.get(inKey)) {
								message.setElement(Constants.FIELD_PREFIX_SUBFIELD + i
										+ "." + j, tempBuffer.get(inKey));
							}
						}
					}
				}
			} else {
				message.setElement(Constants.FIELD_PREFIX + i, isoBuffer.get(key));
			}
		}
	}

	private void copyFieldsToBuffer(String prefix, IsoBuffer isoBuffer,
			DinersMessage message, int startPos, int endPos) {
		for (int i = startPos; i <= endPos; i++) {
			String key = Constants.FIELD_PREFIX + i;
			if (CONTAINS_SUB_FIELDS.contains(key)) {
				// if ("Y".equals(message.getElement(FIELD_PREFIX + i))) {
				IsoBuffer tempBuffer = new IsoBuffer();
				tempBuffer.fillBuffer(true, true, true);
				for (int j = 1; j <= 192; j++) {
					String val = message.getElement(Constants.FIELD_PREFIX_SUBFIELD + i
							+ "." + j);
					tempBuffer.put(prefix + j, val == null ? Constants.DISABLED : val);
				}
				isoBuffer.putBuffer(prefix + i, tempBuffer);
				// }
			} else {
				isoBuffer.put(prefix + i, message.getElement(key));
			}
		}
	}
}