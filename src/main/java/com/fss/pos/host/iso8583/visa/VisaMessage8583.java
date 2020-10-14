package com.fss.pos.host.iso8583.visa;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;

import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.utils.IsoUtil;

public class VisaMessage8583 extends VisaMessage {
	protected VisaMessage8583() {
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

	/***************************************************************************
	 * Purpose in brief : To format the provided data into VISA format Written
	 * by : SubramaniMohanam Created on : 30th April 2015 Last Modified : 30th
	 * April 2015; Arguments passed : VisaField Modified By : SubramaniMohanam
	 **************************************************************************/

	protected byte[] pack(VisaField aauthfield[]) {
		StringBuffer fieldMessage = null;
		StringBuffer actualBitMap = null;
		StringBuffer message = null;
		StringBuffer MsgTypeBitmapField = new StringBuffer();
		byte[] totalbytes1 = null;
		int bitMapLen = 0;
		int msgTypeLen = 0;
		try {
			// Log.trace(" ----- Transaction Starts ------------- ");
			// Log.trace(" --- Message Packing Starts ---   ");

			// ****************** Message Type Length Starts************ //
			msgTypeLen = getMesssageTypelen(msgObj);

			// ****************** Message Type Length Ends************ //

			// **************** BITMAP & Field attribute generation
			// ******************* //

			fieldMessage = new StringBuffer();
			actualBitMap = new StringBuffer();
			bitMapLen = getBitmapFieldAtt(msgObj, fieldMessage, actualBitMap,
					aauthfield);

		//	Log.debug("fieldMessage", fieldMessage.toString());

			//Log.debug("Visa Request", msgObj.toString());

			// **************** BITMAP & Field attribute generation
			// Ends******************* //

			// **************** Header length calculation **********************

			int HdrLen = getHeaderLength(super.msgObj);

			// **************** Header length calculation
			// Ends**********************

			/*
			 * ************** Message Type + BitMap + Field Attribute Starts
			 * ***********
			 */
			message = new StringBuffer();

			message.append(msgObj.get(VisaConstants.MSG_TYP));
		//	Log.debug("actualBitMap ::::::::", actualBitMap.toString());
			message.append(IsoUtil.binary2hex(actualBitMap.toString()));

			message.append(fieldMessage);

			MsgTypeBitmapField.append(message);

			int fieldLen = fieldMessage.length();

			int totalLen = HdrLen + msgTypeLen + bitMapLen + fieldLen;

			msgObj.put(VisaConstants.H_TOT_MSG_LNT, StringUtils.leftPad(
					Integer.toHexString(totalLen / 2) + "", 4, '0'));

			message.delete(0, message.length());
			headerFormat(message);

			String HeaderMessage = message.toString();
			String MsgTypeBitmapFieldMessage = MsgTypeBitmapField.toString();
			String totalMessage = HeaderMessage + (MsgTypeBitmapFieldMessage);
			//Log.debug("Message ::::::::", message.toString());
			//Log.debug("MsgTypeBitmapFieldMessage ::::::::",MsgTypeBitmapFieldMessage.toString());
			totalbytes1 = Hex.decodeHex(totalMessage.toCharArray());

		} catch (Exception e) {
			setElement("Error",
					"Error occured in formatting the VISA message pack()");
			Log.error(
					"Error occured in formatting the VISA message :: "
							+ this.getClass(), e);
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

	protected boolean unpack(String message, VisaField aauthfield[]) {

		Map<String, String> isoBuffer = new HashMap<String, String>();

		if (message == null) {
			Log.error("Response message is empty :: " + this.getClass(), null);
			throw new RuntimeException(VisaConstants.INVALID_TRANSACTION);
		}
		try {
			// Log.trace(" --- Unpack Started ---- ");
			fillISOBuffer(isoBuffer);

			int offset = 0;

			String primaryBitMap = "";
			String secondaryBitMap = "";
			String tertiaryBitMap = "";
			String totalBitMap = "";

			isoBuffer.put(VisaConstants.H_LENGTH,
					message.substring(offset, offset + 2));
			offset += 2;

			isoBuffer.put(VisaConstants.H_FLAG_FORMAT,
					message.substring(offset, offset + 2));

			offset += 2;
			isoBuffer.put(VisaConstants.H_TXT_FORMAT,
					message.substring(offset, offset + 2));
			offset += 2;

			isoBuffer.put(VisaConstants.H_TOT_MSG_LNT,
					message.substring(offset, offset + 4));
			offset += 4;

			isoBuffer.put(VisaConstants.H_DST_STAT_ID,
					message.substring(offset, offset + 6));
			offset += 6;
			isoBuffer.put(VisaConstants.H_SRC_STAT_ID,
					message.substring(offset, offset + 6));
			offset += 6;
			isoBuffer.put(VisaConstants.H_RND_CON_INF,
					message.substring(offset, offset + 2));
			offset += 2;
			isoBuffer.put(VisaConstants.H_BASE_1_FLAG,
					message.substring(offset, offset + 4));
			offset += 4;
			isoBuffer.put(VisaConstants.H_MSG_STATUS_FLAG,
					message.substring(offset, offset + 6));
			offset += 6;
			isoBuffer.put(VisaConstants.H_BAT_NO,
					message.substring(offset, offset + 2));
			offset += 2;
			isoBuffer.put(VisaConstants.H_RESERVED,
					message.substring(offset, offset + 6));
			offset += 6;
			isoBuffer.put(VisaConstants.H_USR_INFO,
					message.substring(offset, offset + 2));
			offset += 2;

			isoBuffer.put(VisaConstants.MSG_TYP,
					message.substring(offset, offset + 4));
			offset += 4;
			// Log.trace("MSG TYP ::" + isoBuffer.get(VisaConstants.MSG_TYP));

			primaryBitMap = message.substring(offset, offset + 16);

			// Log.trace("Primary BitMap ::" + primaryBitMap);

			offset += 16;

			isoBuffer.put("PRIMARY-BITMAP", primaryBitMap);

			primaryBitMap = parseBitmap(primaryBitMap);

			switch (primaryBitMap.charAt(0)) {

			case '1':

				secondaryBitMap = message.substring(offset, offset + 16);

				// Log.trace("Secondary BitMap ::" + secondaryBitMap);

				offset += 16;

				isoBuffer.put("SECONDARY-BITMAP", secondaryBitMap);

				secondaryBitMap = parseBitmap(secondaryBitMap);

				switch (secondaryBitMap.charAt(0)) {

				case '1':

					// tertiaryBitMap = IsoUtil.binary2hex(IsoUtil
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
			splitMessage(message, isoBuffer, totalBitMap, offset, aauthfield);
			this.msgObj = isoBuffer;
			// Log.debug("Visa Response", isoBuffer.toString());
		} catch (Exception e) {
			setElement("Error", "Error occured in unpacking the VISA message");
			Log.error(
					"Error occured in unpacking the VISA message :: "
							+ this.getClass(), e);

		}

		return true;
	}

}
