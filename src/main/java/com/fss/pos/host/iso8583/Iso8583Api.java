package com.fss.pos.host.iso8583;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;

/**
 * An abstraction to handle ISO 8583 messages.
 * 
 * @author Priyan
 */
public abstract class Iso8583Api {

	protected static final String ISO = "ISO";
	protected static final String DC_ID = "DC-ID";
	protected static final String MSG_TYP = "MSG-TYP";
	protected static final String REL_ID = "REL-ID";
	protected static final String REASON_CODE = "REASON-CODE";
	protected static final String ORIGINATOR = "ORIGINATOR";
	protected static final String AUTHORIZOR = "AUTHORIZOR";

	public static final String STAR = "*";

	protected static final List<String> VALID_MSG_TYPES;

	static {
		ArrayList<String> vmt = new ArrayList<String>();
		vmt.add("0200");
		vmt.add("0210");
		vmt.add("0215");
		vmt.add("0220");
		vmt.add("0205");
		vmt.add("0221");
		vmt.add("0230");
		vmt.add("0402");
		vmt.add("0412");
		vmt.add("0420");
		vmt.add("0421");
		vmt.add("0430");
		vmt.add("0800");
		vmt.add("0810");
		vmt.add("0100");
		vmt.add("0110");
		vmt.add("9200");
		vmt.add("9220");
		vmt.add("9100");
		vmt.add("9400");
		vmt.add("9420");
		vmt.add("0120");
		vmt.add("0130");
		VALID_MSG_TYPES = Collections.unmodifiableList(vmt);
	}

	public String buildIso87Request(String iobsgMsgType, IsoBuffer isoBuffer) {
		try {
			StringBuilder losgMessage = new StringBuilder();
			losgMessage.append(buildHeader(isoBuffer));
			losgMessage.append(buildIso87(iobsgMsgType, isoBuffer));
			StringBuilder buffer = new StringBuilder(
					getLength(losgMessage.toString()));
			buffer.append(losgMessage.toString());
			/*
			 * Log.debug("ist  api build::",
			 * IsoUtil.asciiChar2hex(buffer.toString()));
			 */
			return buffer.toString();
		} catch (Exception e) {
			Log.error("Error building ISO request ", e);
			return null;
		}
	}

	public IsoBuffer parseIso87(String message, int offset) throws PosException {
		IsoBuffer buffer = new IsoBuffer();
		int i=0;
		try {
			int[] BITMAP87 = getBitmapArray();
			parseHeader(message, buffer);
			buffer.put(MSG_TYP, parseMessageType(message));

			if (!VALID_MSG_TYPES.contains(buffer.get(MSG_TYP)))
				throw new PosException(Constants.ERR_INVALID_MSG_TYPE);

			StringBuilder pBitmap=new StringBuilder();
			 pBitmap.append(parsePrimaryBitmap(message));

			for ( i = 0; i < 64; i++) {
				if ('1' == pBitmap.charAt(i)) {
					if (BITMAP87[i] < 0) {
						int size = Integer.parseInt(message.substring(offset,
								offset + -1 * BITMAP87[i]));
						offset += -1 * BITMAP87[i];
						buffer.put(IsoBuffer.PREFIX_PRIMARY + (i + 1),
								message.substring(offset, offset + size));
						offset += size;
					} else {
						buffer.put(IsoBuffer.PREFIX_PRIMARY + (i + 1),
								message.substring(offset, offset + BITMAP87[i]));
						offset += BITMAP87[i];
					}
				} else {
					buffer.put(IsoBuffer.PREFIX_PRIMARY + (i + 1), STAR);
				}
			}
			if ('1' == pBitmap.charAt(0)) {
				String sBitmap = parseSecondaryBitmap(buffer.get(Constants.DE1));
				for ( i = 64; i < 128; i++) {
					if ('1' == sBitmap.charAt(i - 64)) {
						if (BITMAP87[i] < 0) {
							int size = Integer.parseInt(message.substring(
									offset, offset + -1 * BITMAP87[i]));
							offset += -1 * BITMAP87[i];
							buffer.put(IsoBuffer.PREFIX_SECONDARY + (i + 1),
									message.substring(offset, offset + size));
							offset += size;
						} else {
							buffer.put(
									IsoBuffer.PREFIX_SECONDARY + (i + 1),
									message.substring(offset, offset
											+ BITMAP87[i]));
							offset += BITMAP87[i];
						}
					} else {
						buffer.put(IsoBuffer.PREFIX_SECONDARY + (i + 1), STAR);
					}
				}
			}
		} catch (Exception e) {
			Log.error("Iso87 parsing failed due to ", e );
			// Log.info("PARSING ERROR !! ", buffer.toString());
			//throw new PosException(null);
			//Log.error("HpdhClientApi parseHpdhMessage problem bit  : "
			//		+ (i + 1), e);
			Log.info("Parsing error in bit  : " + (i + 1),
					buffer.toString());
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
		return buffer;
	}

	public String buildIso87(String iobstMsgType, IsoBuffer pthtISOBuffer) {
		// Log.debug("iobstMsgType", iobstMsgType);
		// Log.debug("pthtISOBuffer", pthtISOBuffer.toString());
		int lonuSize = 0;
		StringBuilder losgMessage = new StringBuilder();
		StringBuilder losgPrimaryBitMap = new StringBuilder();
		StringBuilder losgSecondaryBitMap = new StringBuilder();
		int[] BITMAP87 = getBitmapArray();
	    //pthtISOBuffer.put("P-1", "0000000000000000");
		for (int i = 0; i < 128; i++) {
			if (i <= 63) {
				if (!pthtISOBuffer.get(IsoBuffer.PREFIX_PRIMARY + (i + 1))
						.equals(STAR)) {
					if (BITMAP87[i] < 0) {
						lonuSize = pthtISOBuffer.get(
								IsoBuffer.PREFIX_PRIMARY + (i + 1)).length();
						losgMessage.append(IsoUtil.leftPadZeros(
								String.valueOf(lonuSize), -1 * BITMAP87[i]));
						losgMessage.append(pthtISOBuffer
								.get(IsoBuffer.PREFIX_PRIMARY + (i + 1)));
					} else {
						losgMessage.append(pthtISOBuffer
								.get(IsoBuffer.PREFIX_PRIMARY + (i + 1)));
					}
					losgPrimaryBitMap.append("1");
				} else {
					losgPrimaryBitMap.append("0");
				}

			} else {
				if (!pthtISOBuffer.get(IsoBuffer.PREFIX_SECONDARY + (i + 1))
						.equals(STAR)) {
					if (BITMAP87[i] < 0) {
						lonuSize = pthtISOBuffer
								.get(IsoBuffer.PREFIX_SECONDARY + (i + 1))
								.toString().length();
						losgMessage.append(IsoUtil.leftPadZeros(
								String.valueOf(lonuSize), -1 * BITMAP87[i]));
						losgMessage.append(pthtISOBuffer
								.get(IsoBuffer.PREFIX_SECONDARY + (i + 1)));
					} else {
						losgMessage.append(pthtISOBuffer
								.get(IsoBuffer.PREFIX_SECONDARY + (i + 1)));
					}
					losgSecondaryBitMap.append("1");
				} else {
					losgSecondaryBitMap.append("0");
				}
			}
		}
		StringBuilder message = new StringBuilder();
		if (losgSecondaryBitMap
				.toString()
				.equals("0000000000000000000000000000000000000000000000000000000000000000")) {
			message.append(iobstMsgType);
			Log.debug("inside if :::::::::::::::", losgPrimaryBitMap.toString() );
			Log.debug("inside if  buildBitmap:::::::::::::::", buildBitmap(losgPrimaryBitMap.toString()) );
			message.append(buildBitmap(losgPrimaryBitMap.toString()));
			message.append(losgMessage);
		} else {
			message.append(iobstMsgType);
			losgPrimaryBitMap.setCharAt(0, '1');
			Log.debug("inside else :::::::::::::::", losgPrimaryBitMap.toString() );
			Log.debug("inside else  buildBitmap primary:::::::::::::::", buildBitmap(losgPrimaryBitMap.toString()) );
			Log.debug("inside else :::::::::::::::", losgPrimaryBitMap.toString() );
			Log.debug("inside else  buildBitmap secondary:::::::::::::::", buildBitmap(losgPrimaryBitMap.toString()) );
			message.append(buildBitmap(losgPrimaryBitMap.toString()));
			message.append(buildBitmap(losgSecondaryBitMap.toString()));
			message.append(losgMessage);
		}
		Log.debug("length :::::::::::::::" + message.toString().length() +
		 ":::Message :::::::::::", message.toString());
		/*
		 * Log.debug("length :::::::::::::::" + message.toString().length() +
		 * ":::Message :::::::::::", message.toString());
		 */

		return message.toString();
	}

	public String parseBitmap(String iobsgBitmap) {
		StringBuilder losgUpperBitmap = new StringBuilder();
		losgUpperBitmap.append("00000000000000000000000000000000");
		StringBuilder losgLowerBitmap = new StringBuilder();
		losgLowerBitmap.append("00000000000000000000000000000000");
		losgUpperBitmap.append(Long.toBinaryString(Long.parseLong(
				iobsgBitmap.substring(0, 8), 16)));
		losgLowerBitmap.append(Long.toBinaryString(Long.parseLong(
				iobsgBitmap.substring(8), 16)));
		StringBuilder parseBitmapResult = new StringBuilder();
		parseBitmapResult.append(losgUpperBitmap.toString().substring(
				losgUpperBitmap.toString().length() - 32));
		parseBitmapResult.append(losgLowerBitmap.toString().substring(
				losgLowerBitmap.toString().length() - 32));
		return parseBitmapResult.toString();
	}

	protected abstract int[] getBitmapArray();

	protected abstract String parseMessageType(String message);

	protected abstract String parsePrimaryBitmap(String message);

	protected abstract String parseSecondaryBitmap(String bitMap);

	protected abstract String buildBitmap(String bitMapBinary);

	protected abstract String buildHeader(IsoBuffer isoBuffer);

	protected abstract void parseHeader(String message, IsoBuffer isoBuffer);

	protected abstract String getLength(String data);

}
