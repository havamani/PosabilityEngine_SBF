package com.fss.pos.host.iso8583.base24;

import org.springframework.stereotype.Component;

import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.host.iso8583.Iso8583Api;

@Component
public class Base24Iso87Api extends Iso8583Api {

	private static final int[] BITMAP87 = { 16, -2, 6, 12, 12, 12, 10, 8,// 8
			8, 8, 6, 6, 4, 4, 4, 4,// 16
			4, 4, 3, 3, 3, 3, 3, 3, // 24
			2, 2, 1, 9, 9, 9, 9, -2, // 32
			-2, -2, -2, -3, 12, 6, 2, 3,// 40
			16, 15, 40, -2, -2, -3, -3, -3,// 48
			3, 3, 3, 16, 16, -3, -3, -3,// 56
			-3, 0, 0, -3, -3, -3, -3, 16,// 64
			8, 1, 2, 3, 3, 3, 4, 4,// 72
			6, 10, 10, 10, 10, 10, 10, 10,// 80
			10, 12, 12, 12, 12, 16, 16, 16,// 88
			16, 42, 0, 2, 5, 7, 42, 16,// 96
			17, 25, -2, -2, -2, -2, -2, -3,// 104
			-3, -3, -3, -3, -3, -3, -3, -3, // 112
			-3, -3, -3, -3, -3, -3, -3, -3, -3,// 120
			-3, -3, -3, -3, -3, -3, 16 };// 128

	@Override
	protected String buildHeader(IsoBuffer isoBuffer) {
		// return "ISO025100055";
		return (isoBuffer.get(Constants.ISO_MSG_TYPE)
				.equals(Constants.MSG_TYPE_NETWORK_MANAGEMENT)) ? "ISO006000060"
				: "ISO026000060";
		/*
		 * if(isoBuffer.get(Constants.ISO_MSG_TYPE).equals(Constants.
		 * MSG_TYPE_NETWORK_MANAGEMENT)) return "ISO006000060"; else return
		 * "ISO026000060"; //modified for ABSA
		 */
	}

	@Override
	protected void parseHeader(String message, IsoBuffer isoBuffer) {
		if (message.substring(0, 3).equals(ISO)) {
			isoBuffer.put(ISO, message.substring(0, 3));
			isoBuffer.put(DC_ID, message.substring(3, 5));
			isoBuffer.put(REL_ID, message.substring(5, 7));
			isoBuffer.put(REASON_CODE, message.substring(7, 10));
			isoBuffer.put(ORIGINATOR, message.substring(10, 11));
			isoBuffer.put(AUTHORIZOR, message.substring(11, 12));
		}
	}

	@Override
	protected String buildBitmap(String bitMapBinary) {
		return IsoUtil.binary2hex(bitMapBinary);
	}

	@Override
	public String parsePrimaryBitmap(String message) {
		return parseBitmap(message.substring(16, 32));
	}

	@Override
	protected String parseMessageType(String message) {
		return message.substring(12, 16);
	}

	@Override
	protected int[] getBitmapArray() {
		return BITMAP87;
	}

	@Override
	protected String parseSecondaryBitmap(String bitMap) {
		return parseBitmap(bitMap);
	}

	@Override
	protected String getLength(String data) {
		return IsoUtil.pad(
				String.valueOf(IsoUtil.toGraphical(data.length(), 2)), ' ', 2,
				true);
	}
}
