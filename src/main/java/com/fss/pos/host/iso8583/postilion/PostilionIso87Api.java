package com.fss.pos.host.iso8583.postilion;

import org.springframework.stereotype.Component;

import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.host.iso8583.Iso8583Api;

/**
 * An object to parse and build Postilion ISO messages.
 * 
 * @author Priyan
 */
@Component
public class PostilionIso87Api extends Iso8583Api {

	private static final int[] BITMAP87 = { 8, -2, 6, 12, 12, 12, 10, 8,// 8
			8, 8, 6, 6, 4, 4, 4, 4,// 16
			4, 4, 3, 3, 3, 3, 3, 3, // 24
			2, 2, 1, 9, 9, 9, 9, -2, // 32
			-2, -2, -2, -3, 12, 6, 2, 3,// 40
			8, 15, 40, -2, -2, -3, -3, -3,// 48
			3, 3, 3, 16, 16, -3, -3, -3,// 56
			-3, 0, -3, -3, -3, -3, -3, 16,// 64
			8, 1, 2, 3, 3, 3, 4, 4,// 72
			6, 10, 10, 10, 10, 10, 10, 10,// 80
			10, 12, 12, 12, 12, 16, 16, 16,// 88
			16, 42, 0, 2, 5, 7, 42, 16,// 96
			17, 25, -2, -2, -2, -2, -2, -3,// 104
			-3, -3, -3, -3, -3, -3, -3, -3, // 112
			-3, -3, -3, -3, -3, -3, -3, -3, -3,// 120
			-3, -3, -3, -3, -3, -6, 16 };// 128

	@Override
	public String parsePrimaryBitmap(String message) {
		return parseBitmap(IsoUtil.asciiChar2hex(message.substring(4, 4 + 8)));
	}

	@Override
	protected String buildBitmap(String bitMapBinary) {
		return IsoUtil.binary2asciiChar(bitMapBinary);
	}

	@Override
	protected String buildHeader(IsoBuffer isoBuffer) {
		return "";
	}

	@Override
	protected void parseHeader(String message, IsoBuffer isoBuffer) {
		return;
	}

	@Override
	protected String parseMessageType(String message) {
		return message.substring(0, 4);
	}

	@Override
	protected int[] getBitmapArray() {
		return BITMAP87;
	}

	@Override
	protected String parseSecondaryBitmap(String bitMap) {
		return parseBitmap(IsoUtil.asciiChar2hex(bitMap));
	}

	protected String buildDE127(IsoBuffer isoBuffer, int[] bitFormat) {
		StringBuilder message = new StringBuilder();
		StringBuilder bitmap = new StringBuilder("0");
		String prefix = IsoBuffer.PREFIX_PRIMARY;
		try {
			for (int i = 1; i < 64; i++) {
				if (!isoBuffer.isFieldEmpty(prefix + (i + 1))) {
					if (bitFormat[i] < 0) {
						int lonuSize = isoBuffer.get(prefix + (i + 1))
								.toString().length();
						message.append(IsoUtil.leftPadZeros(
								String.valueOf(lonuSize), -1 * bitFormat[i]));
						message.append(isoBuffer.get(prefix + (i + 1))
								.toString());
					} else {
						message.append(isoBuffer.get(prefix + (i + 1))
								.toString());
					}
					bitmap.append("1");
				} else {
					bitmap.append("0");
				}
			}
			if (!bitmap.substring(1).toString().contains("1"))
				return "*";
			return IsoUtil.binary2asciiChar(bitmap.toString())
					+ message.toString();
		} catch (Exception e) {
			Log.error("Building 127 DE ", e);
			return null;
		}
	}

	protected String buildDE127_25(IsoBuffer isoBuffer, int[] bitFormat) {
		StringBuilder message = new StringBuilder();
		StringBuilder bitmap = new StringBuilder("0");
		String prefix = IsoBuffer.PREFIX_PRIMARY;
		int i = 1;
		try {
			for (; i < 64; i++) {
				if (!isoBuffer.isFieldEmpty(prefix + (i + 1))) {
					if (bitFormat[i] < 0) {
						int lonuSize = isoBuffer.get(prefix + (i + 1))
								.toString().length();
						message.append(IsoUtil.leftPadZeros(
								String.valueOf(lonuSize), -1 * bitFormat[i]));
						message.append(isoBuffer.get(prefix + (i + 1))
								.toString());
					} else {
						message.append(isoBuffer.get(prefix + (i + 1))
								.toString());
					}
					bitmap.append("1");
				} else {
					bitmap.append("0");
				}
			}
			if (!bitmap.substring(1).toString().contains("1"))
				return "*";
			return IsoUtil.binary2hex(bitmap.toString()).toUpperCase()
					+ message.toString();
		} catch (Exception e) {
			Log.error("Building 127.25 DE - Field " + (i + 1), e);
			return null;
		}
	}

	protected IsoBuffer parseDE127_25(String message, int[] bitFormat) {
		int offset = 0;
		String pBitmap = parseBitmap(message.substring(0, 16));
		offset += 16;
		IsoBuffer isoBuffer = new IsoBuffer();
		isoBuffer.disableField(Constants.DE1);
		String prefix = IsoBuffer.PREFIX_PRIMARY;
		int i = 1;
		try {
			for (; i < 64; i++) {
				if ('1' == pBitmap.charAt(i)) {
					if (bitFormat[i] < 0) {
						int size = Integer.parseInt(message.substring(offset,
								offset + -1 * bitFormat[i]));
						offset += -1 * bitFormat[i];
						isoBuffer.put(prefix + (i + 1),
								message.substring(offset, offset + size));
						offset += size;
					} else {
						isoBuffer.put(prefix + (i + 1), message.substring(
								offset, offset + bitFormat[i]));
						offset += bitFormat[i];
					}
				} else {
					isoBuffer.put(prefix + (i + 1), PostilionIso87Api.STAR);
				}
			}
			return isoBuffer;
		} catch (Exception e) {
			Log.debug("Parsing error in 127.25 DE : FIELD : " + i,
					isoBuffer.toString());
			Log.error("Parsing 127.25 DE ", e);
			return null;
		}
	}

	protected IsoBuffer parseDE127(String message, int[] bitFormat) {
		int offset = 0;
		String pBitmap = parseBitmap(IsoUtil.asciiChar2hex(message.substring(0,
				8)));
		offset += 8;
		IsoBuffer isoBuffer = new IsoBuffer();
		isoBuffer.disableField(Constants.DE1);
		String prefix = IsoBuffer.PREFIX_PRIMARY;
		int i = 1;
		try {
			for (; i < 64; i++) {
				if ('1' == pBitmap.charAt(i)) {
					if (bitFormat[i] < 0) {
						int size = Integer.parseInt(message.substring(offset,
								offset + -1 * bitFormat[i]));
						offset += -1 * bitFormat[i];
						isoBuffer.put(prefix + (i + 1),
								message.substring(offset, offset + size));
						offset += size;
					} else {
						isoBuffer.put(prefix + (i + 1), message.substring(
								offset, offset + bitFormat[i]));
						offset += bitFormat[i];
					}
				} else {
					isoBuffer.put(prefix + (i + 1), PostilionIso87Api.STAR);
				}
			}
			return isoBuffer;
		} catch (Exception e) {
			Log.debug("Parsing error in 127 DE : FIELD : " + i,
					isoBuffer.toString());
			Log.error("Parsing 127 DE ", e);
			return null;
		}
	}

	@Override
	protected String getLength(String data) {
		return IsoUtil.pad(
				String.valueOf(IsoUtil.toGraphical(data.length(), 2)), ' ', 2,
				true);
	}
}
