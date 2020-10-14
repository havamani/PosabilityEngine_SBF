package com.fss.pos.host.iso8583.master;

import java.nio.charset.StandardCharsets;

import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.host.AbstractHostApi;

public class MasterApi extends AbstractHostApi {

	@Override
	public IsoBuffer parse(String message) throws PosException {
		MasterFieldDefinition base24hisoposmessage = new MasterFieldDefinition();
		base24hisoposmessage.unpack(message.substring(2).getBytes(
				StandardCharsets.ISO_8859_1));
		IsoBuffer isoBuffer = new IsoBuffer();
		isoBuffer
				.put(Constants.ISO_MSG_TYPE, base24hisoposmessage.getMsgType());
		isoBuffer.fillBuffer(true, false, false);
		for (int i = 1; i < 65; i++) {
			if (base24hisoposmessage.getStringElement(i) != null)
				isoBuffer.put("P-" + i,
						base24hisoposmessage.getStringElement(i));
		}
		for (int j = 65; j < 129; j++) {
			if (base24hisoposmessage.getStringElement(j) != null)
				isoBuffer.put("S-" + j,
						base24hisoposmessage.getStringElement(j));
		}
		
		return isoBuffer;
	}

	@Override
	public String build(IsoBuffer isoBuffer) {
		MasterFieldDefinition base24hisoposmessage = new MasterFieldDefinition();
		base24hisoposmessage.setMsgType(isoBuffer.get(Constants.ISO_MSG_TYPE));
		for (int i = 1; i < 65; i++) {
			if (isoBuffer.get("P-" + i) != null
					&& !isoBuffer.get("P-" + i).equalsIgnoreCase(
							MasterConstants.STAR))
				base24hisoposmessage.setElement(i, isoBuffer.get("P-" + i));
		}
		for (int j = 65; j < 129; j++) {
			if (isoBuffer.get("S-" + j) != null
					&& !isoBuffer.get("S-" + j).equalsIgnoreCase(
							MasterConstants.STAR))
				base24hisoposmessage.setElement(j, isoBuffer.get("S-" + j));
		}
		byte[] messageBytes = base24hisoposmessage.pack();
		byte abyte1[] = new byte[2];
		int i = new String(messageBytes, StandardCharsets.ISO_8859_1).length();
		abyte1[0] = (byte) (i / 256);
		abyte1[1] = (byte) (i - abyte1[0] * 256);
		String message = new String(abyte1, StandardCharsets.ISO_8859_1)
				+ new String(messageBytes, StandardCharsets.ISO_8859_1);
		return message;
	}

}
