package com.fss.pos.host.iso8583.rupay;

/**
 * @author Paranthamanv
 *
 */
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.host.iso8583.benefit.Constants;

public class RupayMessage8583 extends RupayMessage {

	protected RupayMessage8583() {
	}

	@Override
	public void setElement(String i, String s) {
		if (s == null)
			return;
		super.setElement(i, s.toUpperCase());
	}

	@Override
	public String getElement(String i) {
		return super.getElement(i);
	}

	/************
	 * To format the provided data into RUPAY format Written
	 **********/
	protected byte[] pack(RupayField aauthfield[]) {

		StringBuffer fieldMessage = null;
		StringBuffer actualBitMap = null;
		StringBuffer message = null;
		StringBuffer MsgTypeBitmapField = new StringBuffer();
		byte[] totalbytes1 = null;
		int bitMapLen = 0;
		try {

			fieldMessage = new StringBuffer();
			actualBitMap = new StringBuffer();
			bitMapLen = getBitmapFieldAtt(msgObj, fieldMessage, actualBitMap,
					aauthfield);
			//

			int fieldMessageLen = fieldMessage.length();

			int totalLen = msgObj.get(Constants.MSG_TYP).length()
					+ fieldMessageLen + bitMapLen / 2;
			// int totalLen = 4 + fieldMessageLen + bitMapLen;
			message = new StringBuffer();
			String temp = pad(String.valueOf(toGraphical(totalLen, 2)), ' ', 2,
					true);
			message.append(temp);
			message.append(msgObj.get(Constants.MSG_TYP));
			message.append(IsoUtil.binary2asciiChar(actualBitMap.toString()));
			message.append(fieldMessage);

			totalbytes1 = message.toString().getBytes(
					StandardCharsets.ISO_8859_1);

		} catch (Exception e) {
			setElement("Error",
					"Error occured in formatting the JCB message pack()");
			e.printStackTrace();
			totalbytes1 = null;
		} finally {
			fieldMessage = null;
			actualBitMap = null;
			message = null;
			MsgTypeBitmapField = null;
		}
		return totalbytes1;

	}

	/**
	 * @param msgObj
	 * @param fieldMessage
	 * @param actualBitMap
	 * @param aauthfield
	 * @return
	 */

	/***************
	 * To unpack the RUPAY format and fill the buffer Written
	 * 
	 * @throws Exception
	 ****************/

	protected boolean unpack(String message, RupayField aauthfield[]) {

		Map<String, String> isoBuffer = new HashMap<String, String>();
		RupayMessageMap isobuffer = new RupayMessageMap();

		if (message == null) {
			throw new RuntimeException(RupayConstants.INVALID_TRANSACTION);
		}
		try {
			fillISOBuffer(isoBuffer);

			int offset = 0;

			String primaryBitMap = "";
			String secondaryBitMap = "";
			String totalBitMap = "";

			isoBuffer.put(RupayConstants.MSG_TYP,
					message.substring(offset, offset + 4));
			offset += 4;

			// primaryBitMap = ISOUtil.alpha2Hex(message.substring(offset,
			// offset + 16));
			primaryBitMap = IsoUtil.alpha2Hex(message.substring(offset,
					offset + 8));

			// offset += 16;
			offset += 8;

			isoBuffer.put("PRIMARY-BITMAP", primaryBitMap);

			primaryBitMap = parseBitmap(primaryBitMap);

			switch (primaryBitMap.charAt(0)) {

			case '1':
				secondaryBitMap = IsoUtil.alpha2Hex(message.substring(offset,
						offset + 8));
				offset += 8;
				isoBuffer.put("SECONDARY-BITMAP", secondaryBitMap);
				secondaryBitMap = parseBitmap(secondaryBitMap);
				break;

			default:

				isoBuffer.put("SECONDARY-BITMAP", "*");
				break;
			}

			totalBitMap = primaryBitMap + secondaryBitMap;

			splitMessage(message, isoBuffer, totalBitMap, offset, aauthfield);

			this.msgObj = isoBuffer;
			Log.debug("Host Response", isoBuffer.toString());

		} catch (Exception e) {
			setElement("Error", "Error occured in unpacking the Jcb message");
			Log.error(
					"Error occured in unpacking the Jcb message :: "
							+ this.getClass(), e);

		}

		return true;
	}

	/** Pads the char */

	public static char[] toGraphical(long decimal, int length) {

		char[] graphical = new char[length];
		for (int i = 0; i < graphical.length; i++) {
			length--;
			graphical[i] = (char) (decimal / Math.pow(16, 2 * length));
			decimal %= Math.pow(16, 2 * length);
		}
		return graphical;
	}

	/** Pads the char */

	public static String pad(String str, char ch, int length, boolean append)
			throws Exception {
		String padStr = new String(str);
		if (length < str.length()) {
			return str.substring(0, length);
		}
		for (int i = str.length(); i < length; i++) {
			if (append) {
				padStr = padStr + ch;
			} else {
				padStr = ch + padStr;
			}
		}
		return padStr;
	}
}
