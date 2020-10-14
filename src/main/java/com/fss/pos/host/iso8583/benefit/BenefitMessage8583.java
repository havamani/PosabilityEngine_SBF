package com.fss.pos.host.iso8583.benefit;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class BenefitMessage8583 extends Benefit1Message {
	protected BenefitMessage8583() {
	}

	public void setElement(String i, String s) {
		super.setElement(i, s.toUpperCase());
	}

	public String getElement(String i) {
		return super.getElement(i);
	}

	protected String pack(BenefitField aauthfield[]) {
		StringBuffer fieldMessage = null;
		StringBuffer actualBitMap = null;
		StringBuffer message = null;
		StringBuffer MsgTypeBitmapField = new StringBuffer();
		byte[] totalbytes1 = null;
		String totalMessage = null;
		int bitMapLen = 0;
		int msgTypeLen = 0;
		try {
			
			// ****************** Message Type Length Starts************ //
			// msgTypeLen = getMesssageTypelen(msgObj);

			// ****************** Message Type Length Ends************ //

			// **************** BITMAP & Field attribute generation
			// ******************* //

			fieldMessage = new StringBuffer();
			actualBitMap = new StringBuffer();
			bitMapLen = getBitmapFieldAtt(msgObj, fieldMessage, actualBitMap,
					aauthfield);

			// **************** BITMAP & Field attribute generation
			// Ends******************* //

			// **************** Header length calculation **********************

			// int HdrLen = getHeaderLength(super.msgObj);

			// **************** Header length calculation
			// Ends**********************

			/*
			 *  ************** Message Type + BitMap + Field Attribute Starts
			 * ***********
			 */
			message = new StringBuffer();
			if (msgObj.get(Constants.MSG_TYP).equals("0510")
					|| msgObj.get(Constants.MSG_TYP).equals("0530")
					|| msgObj.get(Constants.MSG_TYP).equals("0512")
					|| msgObj.get(Constants.MSG_TYP).equals("0532")) {
				message.append("ISO005000070"); // benefit header
			} else {
				message.append("ISO025000070"); // benefit header
			}
			message.append(msgObj.get(Constants.MSG_TYP));
			message.append(ISOUtil.binary2hex(actualBitMap.toString()));
			message.append(fieldMessage);
			// message.append("071851230414033680160111002102000000001");

			MsgTypeBitmapField.append(message);

			/*
			 *  *************** Message Type + BitMap + Field Attribute Ends
			 * ************
			 */

			// int fieldLen = fieldMessage.length();
			int totalLen = message.length();

			String temp = pad(String.valueOf(toGraphical(totalLen, 2)), ' ', 2,
					true);
			totalMessage = temp + message.toString();
			totalbytes1 = totalMessage.getBytes(StandardCharsets.ISO_8859_1);

			// totalbytes1 = Hex.decodeHex(totalMessage.toCharArray());
			

		} catch (Exception e) {
			setElement("Error",
					"Error occured in formatting the Benefit message pack()");
			e.printStackTrace();
			totalbytes1 = null;
		} finally {
			fieldMessage = null;
			actualBitMap = null;
			message = null;
			MsgTypeBitmapField = null;
		}
		return totalMessage;
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

	/***************************************************************************
	 * Purpose in brief : To unpack the VISA format and fill the buffer Written
	 * by : Maathavan Created on : 30th April 2015 Last Modified : 30th April
	 * 2015; Arguments passed : VisaField Modified By : Maathavan
	 * 
	 * @throws Exception
	 **************************************************************************/

	protected BenefitMessage unpack(String message, BenefitField aauthfield[]) {

		Map<String, String> isoBuffer = new HashMap<String, String>();
		BenefitMessage isobuffer = new BenefitMessage();
		String msg = null;

		if (message == null) {
			throw new RuntimeException(Constants.INVALID_TRANSACTION);
		}
		try {
			fillISOBuffer(isoBuffer);

			int offset = 0;

			String primaryBitMap = "";
			String secondaryBitMap = "";
			String totalBitMap = "";

			isoBuffer.put(Constants.MSG_TYP,
					message.substring(offset, offset + 4));
			offset += 4;

			primaryBitMap = message.substring(offset, offset + 16);

			offset += 16;

			isoBuffer.put("PRIMARY-BITMAP", primaryBitMap);

			primaryBitMap = parseBitmap(primaryBitMap);

			switch (primaryBitMap.charAt(0)) {

			case '1':

				secondaryBitMap = message.substring(offset, offset + 16);
				// offset += 16;
				isoBuffer.put("SECONDARY-BITMAP", secondaryBitMap);
				secondaryBitMap = parseBitmap(secondaryBitMap);
				break;

			default:

				isoBuffer.put("SECONDARY-BITMAP", "*");
				break;
			}

			totalBitMap = primaryBitMap + secondaryBitMap;
			isobuffer = splitMessage(message, isoBuffer, totalBitMap, offset,
					aauthfield);
			isobuffer.setElement(Constants.MSG_TYP,
					isoBuffer.get(Constants.MSG_TYP));
			// this.msgObj = isoBuffer;
			
		} catch (Exception e) {
			e.printStackTrace();
			msg = "1FM0045" + e + "Error while unpacking benefit response";
		}

		return isobuffer;
	}

}
