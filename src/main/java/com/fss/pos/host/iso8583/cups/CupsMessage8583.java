package com.fss.pos.host.iso8583.cups;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.utils.IsoUtil;

public class CupsMessage8583 extends CupsMessage {
	private static final int HdrLen = 46;

	protected CupsMessage8583() {
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
	protected byte[] pack(CupsField aauthfield[]) {
		StringBuffer fieldMessage = null;
		StringBuffer actualBitMap = null;
		StringBuffer Finalmessage = null;
		StringBuffer MsgTypeBitmapField = new StringBuffer();
		byte[] totalbytes1 = null;
		int bitMapLen = 0;
		int msgTypeLen = 0;
		StringBuffer messageHdr=null;
		
		try {
			

			fieldMessage = new StringBuffer();
			actualBitMap = new StringBuffer();
			
			
			//Packing all data
			bitMapLen = getBitmapFieldAtt(msgObj, fieldMessage, actualBitMap,
					aauthfield);
			
			
				
			int fieldMessageLen = fieldMessage.length();
			
			int totalLen1 = HdrLen + 4 + fieldMessageLen+bitMapLen/2;
			
			
			String msgLen=CupsIsoUtil.appendChar(Integer.toString(totalLen1), '0', 4 ,true);
			
						
			//Log.debug("fieldMessage", fieldMessage.toString());

		
			messageHdr = buildheader(msgLen);
		
			Finalmessage = new StringBuffer();
			Finalmessage.append(msgLen);
			Finalmessage.append(messageHdr);
			
			Finalmessage.append(msgObj.get(CupsConstants.ISO_MSG_TYPE));
			
			Finalmessage.append(IsoUtil.binary2asciiChar(actualBitMap.toString()));
			
			Finalmessage.append(fieldMessage);
			

				
			String totalMessage =Finalmessage.toString();
			totalbytes1 = totalMessage.getBytes(StandardCharsets.ISO_8859_1);
		

		} catch (Exception e) {
			e.printStackTrace();
			setElement("Error",
					"Error occured in formatting the CUP message pack()");
			//Log.error(
				//	"Error occured in formatting the VISA message :: "
				//			+ this.getClass(), e);
			totalbytes1 = null;
		} finally {
			fieldMessage = null;
			actualBitMap = null;
			Finalmessage = null;
			MsgTypeBitmapField = null;
			messageHdr = null;
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

	protected boolean unpack(String message, CupsField aauthfield[]) {

		Map<String, String> isoBuffer = new HashMap<String, String>();

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
			splitMessage(message, isoBuffer, totalBitMap, offset, aauthfield);
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
