package com.fss.pos.host.iso8583.diners;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;



import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.host.iso8583.cups.CupsConstants;


public class DinersMessage8583 extends Diners1Message {
	protected DinersMessage8583() {
	}

	@Override
	public void setElement(String i, String s) {
		super.setElement(i, s.toUpperCase());
	}

	@Override
	public String getElement(String i) {
		return super.getElement(i);
	}

	/** Convert decimal to graphical equivalent */
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

	/***************************************************************************
	 * Purpose in brief : To format the provided data into VISA format Written
	 * by : SubramaniMohanam Created on : 30th April 2015 Last Modified : 30th
	 * April 2015; Arguments passed : VisaField Modified By : SubramaniMohanam
	 **************************************************************************/

	protected byte[] pack(DinersField aauthfield[]) {
		StringBuffer fieldMessage = null;
		StringBuffer actualBitMap = null;
		StringBuffer message = null;
		new StringBuffer();
		byte[] totalbytes1 = null;
		int bitMapLen = 0;
		int msgTypeLen = 0;
		try {

			// ****************** Message Type Length Starts************ //
			msgTypeLen = getMesssageTypelen(msgObj);

			// ****************** Message Type Length Ends************ //

			// **************** BITMAP & Field attribute generation
			// ******************* //

			fieldMessage = new StringBuffer();
			actualBitMap = new StringBuffer();
			bitMapLen = getBitmapFieldAtt(msgObj, fieldMessage, actualBitMap,
					aauthfield);
			// fieldMessage.append(3);
			// **************** BITMAP & Field attribute generation
			// Ends******************* //

			// **************** Header length calculation **********************

			// int HdrLen = getHeaderLength(super.msgObj);

			// int HdrLen = 0;
			// **************** Header length calculation
			// Ends**********************

			/*
			 * ************** Message Type + BitMap + Field Attribute Starts
			 * ***********
			 */
			message = new StringBuffer();

			message.append(msgObj.get(Constants.ISO_MSG_TYPE).toLowerCase());

			message.append(IsoUtil.hex2AsciiChar(IsoUtil
					.binary2hex(actualBitMap.toString())));

			message.append(fieldMessage);
			// message.append("3");
			int fieldLen = fieldMessage.length();
			int totalLen = msgTypeLen + bitMapLen + fieldLen;
			String totalMessage = IsoUtil.leftPadZeros(
					String.valueOf(totalLen), 4) + message;

			totalbytes1 = totalMessage.getBytes(StandardCharsets.ISO_8859_1);

		} catch (Exception e) {
			setElement("Error",
					"Error occured in formatting the AMEX message pack()");
			e.printStackTrace();
			totalbytes1 = null;
		} finally {
			fieldMessage = null;
			actualBitMap = null;
			message = null;
		}
		return totalbytes1;
	}

	/***************************************************************************
	 * Purpose in brief : To unpack the VISA format and fill the buffer Written
	 * by : Maathavan Created on : 30th April 2015 Last Modified : 30th April
	 * 2015; Arguments passed : VisaField Modified By : Maathavan
	 *
	 * @throws Exception
	 **************************************************************************/
	protected boolean unpack(byte messageBytes[], DinersField aauthfield[]) {

		Map<String, String> isoBuffer = new HashMap<String, String>();
		String message = new String(messageBytes, StandardCharsets.ISO_8859_1);
		if (message == null) {
			Log.error("Response message is empty :: " + this.getClass(), null);
			throw new RuntimeException(CupsConstants.INVALID_TRANSACTION);
		}
		try {
			// Log.trace(" --- Unpack Started ---- ");
			fillISOBuffer(isoBuffer);

			//	int offset = 0;

			String primaryBitMap = "";
			String secondaryBitMap = "";
			String tertiaryBitMap = "";
			String totalBitMap = "";
			int offset=0;

			isoBuffer.put(CupsConstants.MSG_TYP,
					message.substring(offset, offset + 4));
			offset += 4;
			 //Log.trace("MSG TYP ::" + isoBuffer.get(CupsConstants.MSG_TYP));

			primaryBitMap = IsoUtil.byteArrayToHexString(Arrays.copyOfRange(messageBytes, 4,
					messageBytes.length),8);
			 primaryBitMap = IsoUtil.alpha2Hex(message.substring(offset, offset +8));

			// Log.trace("Primary BitMap ::" + primaryBitMap);

			offset += 8;

			isoBuffer.put("PRIMARY-BITMAP", primaryBitMap);

			primaryBitMap = parseBitmap(primaryBitMap);

			switch (primaryBitMap.charAt(0)) {

			case '1':
				
				secondaryBitMap = IsoUtil.alpha2Hex(message.substring(offset, offset + 8));

				// Log.trace("Secondary BitMap ::" + secondaryBitMap);

				offset += 8;

				isoBuffer.put("SECONDARY-BITMAP", secondaryBitMap);

				secondaryBitMap = parseBitmap(secondaryBitMap);
				switch (secondaryBitMap.charAt(0)) {

				case '1':

					// tertiaryBitMap = CupsIsoUtil.binary2hex(CupsIsoUtil
					// .asciiChar2binary(message.substring(offset,
					// offset + 16)));
					tertiaryBitMap = message.substring(offset, offset + 16);

					offset += 16;

					isoBuffer.put("TERTIARY-BITMAP", tertiaryBitMap);

					tertiaryBitMap = parseBitmap(tertiaryBitMap);

					break;

				default:

					isoBuffer.put("TERTIARY-BITMAP", "*");

					break;
				}
				break;

			default:

				isoBuffer.put("SECONDARY-BITMAP", "*");
				break;
			}

			totalBitMap = primaryBitMap + secondaryBitMap + tertiaryBitMap;
			splitMessage(message, isoBuffer, totalBitMap, 12, aauthfield);
			this.msgObj = isoBuffer;
			 //Log.debug("Cup Response", isoBuffer.toString());
		} catch (Exception e) {
			setElement("Error", "Error occured in unpacking the Cup message");
			Log.error(
					"Error occured in unpacking the Cup message :: "
							+ this.getClass(), e);

		}

		return true;
	}
}
