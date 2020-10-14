package com.fss.pos.host.iso8583.diners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.host.iso8583.Iso8583Api;

@Component
public class DinersIso93Api extends Iso8583Api {
	protected static final List<String> VALID_IST_MSG_TYPES;
	protected static final String PRIMARY_BIT_PREFIX = "P-";
	protected static final String SECONDARY_BIT_PREFIX = "S-";
	private static final int[] BITMAP93 = { 16, 16, -2, 6, 12, 12, 12, 10, 0, 8, 8,
			6, 12, 0, 4, 6, 4, 4, 0, 3, 0, 0, 12, 3, 3, 4, 4, 0, 6, 0, 24, 0,
			-2, -2, 0, -2, -3, 12, 6, 3, 3, 16, 15, -2, 0, -2, -3, 0, -3, 3, 3,
			3, 16, -2, -3, -3, -2, 0, -2, -3, 0, 0, -3, 0, 16, 0, 0, 2, 0, 0,
			3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 42, 0,
			0, 0, 0, 42, 0, 0, 0, 0, -2, 0, -2, -2, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -3, 0, 0, 0, -3, 16 };

	static {
		ArrayList<String> vmt = new ArrayList<String>();
		vmt.add("0100");
		vmt.add("1210");
		vmt.add("1220");
		vmt.add("1230");
		vmt.add("1430");
		vmt.add("1110");
		vmt.add("0200");
		vmt.add("0210");
		vmt.add("0220");
		vmt.add("0400");
		VALID_IST_MSG_TYPES = Collections.unmodifiableList(vmt);
	}

	protected int[] getBitmapArray() {
		return BITMAP93;
	}

	protected String parseMessageType(String message) {
		return message.substring(0, 4);
	}

	public String parsePrimaryBitmap(String message) {
		return parseBitmap(message.substring(4, 20));
	}

	protected String parseSecondaryBitmap(String bitMap) {
		return parseBitmap(bitMap);
	}

	protected String buildBitmap(String bitMapBinary) {
		return IsoUtil.binary2hex(bitMapBinary);
	}

	protected String buildHeader(IsoBuffer isoBuffer) {
		return "ISO025100055";
	}

	protected void parseHeader(String message, IsoBuffer isoBuffer) {
		if (message.substring(0, 3).equals("ISO")) {
			isoBuffer.put("ISO", message.substring(0, 3));
			isoBuffer.put("DC-ID", message.substring(3, 5));
			isoBuffer.put("REL-ID", message.substring(5, 7));
			isoBuffer.put("REASON-CODE", message.substring(7, 10));
			isoBuffer.put("ORIGINATOR", message.substring(10, 11));
			isoBuffer.put("AUTHORIZOR", message.substring(11, 12));
		}
	}

	public String buildIso87Request(String iobsgMsgType, IsoBuffer isoBuffer) {
		try {
			String losgMessage = buildIso87(iobsgMsgType, isoBuffer);

			StringBuilder buffer = new StringBuilder(appendChar(
					Integer.toString(losgMessage.length()), '0', 4, true));
			buffer.append(losgMessage);

			return buffer.toString();
		} catch (Exception e) {
			Log.error(getClass().getCanonicalName() + "  buildIso93Request() ",
					e);
		}
		return null;
	}

	public static String appendChar(String data, char ch, int totalLen,
			boolean appendLeft) {
		if (data == null)
			data = "";
		StringBuilder sb = new StringBuilder();
		if (!appendLeft)
			sb.append(data);
		for (int i = 1; i <= totalLen - data.length(); i++)
			sb.append(ch);
		if (appendLeft)
			sb.append(data);
		return sb.toString();
	}

	public IsoBuffer parseIso87(String message, int offset) throws PosException {
		IsoBuffer buffer = new IsoBuffer();
		try {
			int[] BITMAP87 = getBitmapArray();

			buffer.put("MSG-TYP", parseMessageType(message));

			if (!VALID_IST_MSG_TYPES.contains(buffer.get("MSG-TYP"))) {
				throw new PosException("XZ");
			}
			String pBitmap = parsePrimaryBitmap(message);

			for (int i = 0; i < 64; i++) {
				if ('1' == pBitmap.charAt(i)) {
					if (BITMAP87[i] < 0) {
						int size = Integer.parseInt(message.substring(offset,
								offset + -1 * BITMAP87[i]));
						offset += -1 * BITMAP87[i];
						buffer.put("P-" + (i + 1),
								message.substring(offset, offset + size));
						offset += size;
					} else {
						buffer.put("P-" + (i + 1),
								message.substring(offset, offset + BITMAP87[i]));
						if (i == 53) {
							String DE54Value = message.substring(offset, offset
									+ BITMAP87[i]);
							buffer.put("P-" + (i + 1) + ".1",
									DE54Value.substring(0, 2));
							buffer.put("P-" + (i + 1) + ".2",
									DE54Value.substring(2, 4));
							buffer.put("P-" + (i + 1) + ".3",
									DE54Value.substring(4, 7));
							buffer.put("P-" + (i + 1) + ".4",
									DE54Value.substring(7, 8));
							buffer.put("P-" + (i + 1) + ".5",
									DE54Value.substring(8, 20));
							DE54Value = null;
						}

						offset += BITMAP87[i];
					}
				} else
					buffer.put("P-" + (i + 1), "*");

			}
			if ('1' == pBitmap.charAt(0)) {
				String sBitmap = parseSecondaryBitmap(buffer.get("P-1"));
				for (int i = 64; i < 128; i++) {
					if (i == 94)
					if ('1' == sBitmap.charAt(i - 64)) {
						if (BITMAP87[i] < 0) {
							int size = Integer.parseInt(message.substring(
									offset, offset + -1 * BITMAP87[i]));
							offset += -1 * BITMAP87[i];
							buffer.put("S-" + (i + 1),
									message.substring(offset, offset + size));
							offset += size;
						} else {
							buffer.put(
									"S-" + (i + 1),
									message.substring(offset, offset
											+ BITMAP87[i]));
							offset += BITMAP87[i];
						}
					} else
						buffer.put("S-" + (i + 1), "*");

				}
			}
		} catch (Exception e) {
			Log.error("Iso93 parsing failed due to ", e);
			Log.info("PARSING ERROR !! ", buffer.toString());
			throw new PosException(null);
		}
		return buffer;
	}

	protected String getLength(String data) {
		return Util.appendChar(Integer.toString(data.length()), '0', 4, true);
	}

	/*
	 * public static void main(String[] args) throws PosException { DinersIso93Api
	 * api = new DinersIso93Api(); String message =
	 * "1230F23204410AC088000000000010000001164263710001177094000000000000001800031412282400001118031416282400000021010125012560110951263963880731200760300088881111        11112222       512060102021151296800001                 "
	 * ; api.parseIso87(message, 20); }
	 */
}