package com.fss.pos.host.iso8583.jcb;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class JcbMessage8583 extends JcbMessageMap {

	protected JcbMessage8583() {
	}

	public void setElement(String i, String s) {
		super.setElement(i, s.toUpperCase());
	}

	public String getElement(String i) {
		return super.getElement(i);
	}

	/*
	 * To format the provided data into JCB format
	 */

	protected byte[] pack(JcbField aauthfield[]) {
		StringBuffer fieldMessage = null;
		StringBuffer actualBitMap = null;
		StringBuffer message = null;
		StringBuffer MsgTypeBitmapField = new StringBuffer();
		byte[] totalbytes1 = null;
		int bitMapLen = 0;
//		int msgTypeLen = 0;
		try {
		


			fieldMessage = new StringBuffer();
			actualBitMap = new StringBuffer();
			bitMapLen = getBitmapFieldAtt(msgObj, fieldMessage, actualBitMap,
					aauthfield);
			
			int fieldMessageLen = fieldMessage.length();
			
			int msgTypeLen = getMesssageTypelen(msgObj);
			
			int totalLen1 =  msgTypeLen  + bitMapLen + fieldMessageLen;
			
			message = new StringBuffer();
			message.append(msgObj.get(JcbConstants.MSG_TYP).toLowerCase());
			message.append(JcbISOUtil.binary2asciiChar(actualBitMap.toString()));
			message.append(fieldMessage);

			MsgTypeBitmapField.append(message);

			//String hex = JcbISOUtil.asciiChar2hex(message.toString());
			totalLen1 = message.length();
	
			String temp1 = String.valueOf(toGraphical(totalLen1, 2));
			String temp = pad(temp1, '0', 4,true);
			//String temp = pad(String.valueOf(toGraphical(totalLen1, 2)), ' ', 2,true);
			String totalMessage = temp +  message;
			totalbytes1 = totalMessage.getBytes(StandardCharsets.ISO_8859_1);

//			 totalbytes1 = Hex.decodeHex(totalMessage.toCharArray());

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

	/*
	 * To format the provided data into JCB format
	 */
	
	private int getMesssageTypelen(Map<String, String> fieldInfo) {
		return ((String) fieldInfo.get(JcbConstants.MSG_TYP)).length();
	}

	protected Map<String, String> unpack(String message, JcbField aauthfield[]) {

		Map<String, String> isoBuffer = new HashMap<String, String>();
		JcbMessageMap isobuffer = new JcbMessageMap();
		String msg = null;
		

		if (message == null) {
			throw new RuntimeException(JcbConstants.INVALID_TRANSACTION);
		}
		try {
		fillISOBuffer(isoBuffer);

		int offset = 0;

		String primaryBitMap = "";
		String secondaryBitMap = "";
		String totalBitMap = "";
		
		isoBuffer.put(JcbConstants.MSG_TYP, new String(message.substring(offset, offset + 4).getBytes(StandardCharsets.ISO_8859_1),"Cp1047"));
		offset += 4;
			String bitMap = JcbISOUtil.alpha2Hex(message.substring(offset, offset + 8));

//			offset += 16;
			offset += 8;

			isoBuffer.put("PRIMARY-BITMAP", bitMap);

			primaryBitMap = parseBitmap(bitMap);

			switch (primaryBitMap.charAt(0)) {

			case '1':
				secondaryBitMap =JcbISOUtil.alpha2Hex(message.substring(offset, offset + 8));
				//offset += 8;
				isoBuffer.put("SECONDARY-BITMAP", secondaryBitMap);
				secondaryBitMap = parseBitmap(secondaryBitMap);
				break;

			default:

				isoBuffer.put("SECONDARY-BITMAP", "*");
				break;
			}

		 totalBitMap = primaryBitMap + secondaryBitMap;
		
		 splitMessage(message, isoBuffer, totalBitMap, offset, aauthfield);

		// isobuffer.setElement(JcbConstants.MSG_TYP, isoBuffer.get(JcbConstants.MSG_TYP));
		 

		
		 /*isobuffer.setElement(JcbConstants.FIELD_7, isoBuffer.get(JcbConstants.FIELD_7));
		 isobuffer.setElement(JcbConstants.FIELD_11, isoBuffer.get(JcbConstants.FIELD_11));
		 isobuffer.setElement(JcbConstants.FIELD_33, isoBuffer.get(JcbConstants.FIELD_33));
		 isobuffer.setElement(JcbConstants.FIELD_39, isoBuffer.get(JcbConstants.FIELD_39));
		 isobuffer.setElement(JcbConstants.FIELD_100, isoBuffer.get(JcbConstants.FIELD_100));
		 isobuffer.setElement(JcbConstants.FIELD_105, isoBuffer.get(JcbConstants.FIELD_105));*/
			//this.msgObj = isoBuffer;
		 
		 
		} catch (Exception e) {
			e.printStackTrace();
			msg = "1FM0045" + e
					+ "Error while unpacking benefit response";
			//EventCollectorAPI.WriteToSocket(msg);
		}

		return isoBuffer;
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

	/*public static String pad(String str, char ch, int length, boolean append)
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
	}*/
	
	public static String pad(String str, char ch, int length, boolean append)
			throws Exception {
		String padStr = new String(str);
		if (length < str.length()) {
			return str.substring(0, length);
		}
		for (int i = str.length(); i < length; i++) {
			if (append) {
				padStr = padStr + JcbISOUtil.hex2AsciiChar("0");
			} else {
				padStr = ch + padStr;
			}
		}
		return padStr;
	}
}
