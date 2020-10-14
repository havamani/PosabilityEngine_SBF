package com.fss.pos.host.iso8583.amex;

import org.springframework.stereotype.Component;

import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.host.iso8583.Iso8583Api;

@Component
public class AmexIso93Api  extends Iso8583Api{
	
	
	private static final int[] BITMAP93 = { 16, -2, 6, 12, 12, 0, 10, 0,// 8
		0, 0, 6, 12, 4, 4, 0, 0, // 16
		0, 0, 3, 0, 0, 12, 0, 3, // 24
		0, 4, 0, 0, 0, 0, 0, -2, // 32
		0, 0, -2, 0, 12, 0, 0, 0, // 40
		8, 15, -2, 0, -2, 0, 0, 0, // 48
		3, 3, 0, 0, -2, 0, -3, 0, // 56
		0, 0, 0, 0, 0, 0, 0, 0, // 64
};

	@Override
	protected int[] getBitmapArray() {
		return BITMAP93;
	}

	@Override
	protected String parseMessageType(String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String parsePrimaryBitmap(String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String parseSecondaryBitmap(String bitMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String buildBitmap(String bitMapBinary) {
		return IsoUtil.binary2hex(bitMapBinary);
	}

	@Override
	protected String buildHeader(IsoBuffer isoBuffer) {
		return "ISO025100055";
	}

	@Override
	protected void parseHeader(String message, IsoBuffer isoBuffer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getLength(String data) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String buildIso87Request(String iobsgMsgType, IsoBuffer isoBuffer) {
		try {
			String losgMessage = buildIso87(iobsgMsgType, isoBuffer);
			/*
			 * StringBuilder buffer = new StringBuilder(IsoUtil.pad(String
			 * .valueOf(IsoUtil.toGraphical(losgMessage.length(), 2)), ' ', 2,
			 * true));
			 */
			StringBuilder buffer = new StringBuilder(appendChar(
					losgMessage.length() + "", '0', 4, true));
			buffer.append(losgMessage);
			//Log.debug("Final Message :::::::::::", IsoUtil.asciiChar2hex(buffer.toString()));
			
			return buffer.toString();
		} catch (Exception e) {
			Log.error(this.getClass().getCanonicalName()
					+ "  buildIso93Request() ", e);
			return null;
		}
	}
	
	public static String appendChar(String data, char ch, int totalLen,
			boolean appendLeft) {
		if (data == null)
			data = "";
		StringBuilder sb = new StringBuilder();
		if (!appendLeft)
			sb.append(data);
		for (int i = 1; i <= (totalLen - data.length()); i++)
			sb.append(ch);
		if (appendLeft)
			sb.append(data);
		return sb.toString();
	}

}
