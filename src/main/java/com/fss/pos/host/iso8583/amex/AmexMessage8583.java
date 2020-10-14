package com.fss.pos.host.iso8583.amex;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;

import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;

public class AmexMessage8583 extends AmexMessage {
	protected AmexMessage8583() {
	}

	@Override
	public void setElement(String i, String s) {
		super.setElement(i, s.toUpperCase());
	}

	@Override
	public String getElement(String i) {
		return super.getElement(i);
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

	protected byte[] pack(AmexField aauthfield[]) {
		StringBuffer fieldMessage = null;
		StringBuffer actualBitMap = null;
		StringBuffer message = null;
		StringBuffer MsgTypeBitmapField = new StringBuffer();
		byte[] totalbytes1 = null;
		int bitMapLen = 0;
		int msgTypeLen = 0;
		try {
		
			// ****************** Message Type Length Starts************ //
			msgTypeLen = getMesssageTypelen(msgObj);

			// ****************** Message Type Length Ends************ //

			// **************** BITMAP & Field attribute generation ******************* //
			
			fieldMessage = new StringBuffer();
			actualBitMap = new StringBuffer();
			bitMapLen = getBitmapFieldAtt(msgObj, fieldMessage, actualBitMap,
					aauthfield);

			// **************** BITMAP & Field attribute generation Ends******************* //	

			// **************** Header length calculation **********************

			//int HdrLen = getHeaderLength(super.msgObj);

		//	int HdrLen = 0;
			// **************** Header length calculation Ends**********************

			/* ************** Message Type + BitMap + Field Attribute Starts *********** */
			message = new StringBuffer();

			message.append(msgObj.get(Constants.ISO_MSG_TYPE).toLowerCase());
			message.append(IsoUtil.hex2AsciiChar(IsoUtil.binary2hex(actualBitMap.toString())));
			message.append(fieldMessage);

			int fieldLen = fieldMessage.length();
			int totalLen = msgTypeLen + bitMapLen + fieldLen;
			
			String temp = pad (String.valueOf (IsoUtil.toGraphical (totalLen, 2)), ' ', 2, true);
			String totalMessage = temp + message;
			totalbytes1 = totalMessage.getBytes(StandardCharsets.ISO_8859_1);
			
			//totalbytes1 = Hex.decodeHex(totalMessage.toCharArray());
				

		} catch (Exception e) {
			setElement("Error",
					"Error occured in formatting the VISA message pack()");
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
	/***************************************************************************
	 * Purpose in brief : To unpack the VISA format and fill the buffer Written
	 * by : Maathavan Created on : 30th April 2015 Last Modified : 30th April
	 * 2015; Arguments passed : VisaField Modified By : Maathavan
	 * 
	 * @throws Exception
	 **************************************************************************/

	protected Map<String, String> unpack(String message, AmexField aauthfield[]) {

		Map<String, String> isoBuffer = new HashMap<String, String>();

		if (message == null) {
			throw new RuntimeException(AmexConstants.INVALID_TRANSACTION);
		}
		try {
			// Log.trace(" --- Unpack Started ---- ");
			fillISOBuffer(isoBuffer);

			int offset = 0;

			String primaryBitMap = "";
			String secondaryBitMap = "";
			String tertiaryBitMap = "";
			String totalBitMap = "";

			isoBuffer.put(AmexConstants.MSG_TYP, message.substring(0, 4));
			offset += 4;
			
			primaryBitMap = Hex.encodeHexString(message.substring(offset, offset+8).getBytes("Cp1047"));
			isoBuffer.put("PRIMARY-BITMAP", primaryBitMap);
			primaryBitMap = parseBitmap(primaryBitMap);
			offset += 8;

			
			switch (primaryBitMap.charAt(0)) {

			case '1':

				secondaryBitMap = Hex.encodeHexString(message.substring(offset, offset+8).getBytes("Cp1047")); //converting in hex in starting


				offset += 8;

				isoBuffer.put("SECONDARY-BITMAP", secondaryBitMap);
				break;

			default:

				isoBuffer.put("SECONDARY-BITMAP", "*");
				break;
			}


			totalBitMap = primaryBitMap + secondaryBitMap + tertiaryBitMap;
			splitMessage(message, isoBuffer, totalBitMap, offset,
					aauthfield);
		//	Log.debug("Amex Response", isoBuffer.toString());
			return isoBuffer;
		} catch (Exception e) {
			setElement("Error", "Error occured in unpacking the VISA message");
			Log.error(
					"Error occured in unpacking the VISA message :: "
							+ this.getClass(), e);

		}

		return isoBuffer;
	}

}
