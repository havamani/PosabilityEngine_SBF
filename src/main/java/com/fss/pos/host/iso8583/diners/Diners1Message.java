package com.fss.pos.host.iso8583.diners;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.base.commons.utils.Util;

public class Diners1Message {
	public Diners1Message() {
		msgObj = new HashMap<String, String>();
	}

	public void setElement(String pos, String value) {
		if (value == null) {
			return;
		} else {
			msgObj.put(pos, value);
			return;
		}
	}

	public String getElement(String pos) {
		if (pos == null) {
			return null;
		} else {
			return msgObj.get(pos);
		}
	}

	public String getStringElement(int pos) {
		Object o = msgObj.get(new Integer(pos));
		if (o == null || !(o instanceof byte[])) {
			return null;
		} else {
			return new String((byte[]) o);
		}
	}

	public byte[] getBytesElement(int pos) {
		Object o = msgObj.get(new Integer(pos));
		if (o == null || !(o instanceof byte[])) {
			return null;
		} else {
			return (byte[]) o;
		}
	}

	protected byte[] formatElement(byte data[], DinersField fieldInfo) {
		return "".getBytes();
	}

	protected String parseElement(byte datab[], DinersField fieldInfo) {
		return "";
	}

	protected int getMesssageTypelen(Map<String, String> fieldInfo) {
		return fieldInfo.get(Constants.ISO_MSG_TYPE).length();
	}

	protected int size() {
		return msgObj.size();
	}

	/***************************************************************************
	 * Purpose in brief : Filling the buffer with dummy data Written by :
	 * SubramaniMohanam Created on : 30th April 2015 Last Modified : 30th April
	 * 2015; Arguments passed : isoBuffer Modified By : SubramaniMohanam
	 *
	 * @throws Exception
	 **************************************************************************/

	protected static void fillISOBuffer(Map<String, String> isoBuffer) throws Exception {
		try {
			isoBuffer.put("TERTIARY-BITMAP", "*");
			isoBuffer.put("SECONDARY-BITMAP", "*");

			for (int i = 1; i <= 192; i++) {

				isoBuffer.put("F-" + i, "*");
			}
		} catch (Exception e) {
			throw new Exception("Error Occured in fillISOBuffer");
		}

	}

	/***************************************************************************
	 * Purpose in brief : Splitting the VISA message to fill the buffer Written by :
	 * Maathavan Created on : 30th April 2015 Last Modified : 30th April 2015;
	 * Arguments passed : message,isoBuffer,bitMap,offset,fieldInfo Modified By :
	 * Maathavan
	 *
	 * @return
	 **************************************************************************/

	protected static void splitMessage(String message, Map<String, String> isoBuffer, String bitMap, int offset,
			DinersField[] fieldInfo) {

		String field55 = null;
		int i = 0;
		try {
			String data = "";

			int messageSize = 0;
			int lengthIndicator = 0;
			int tempLengthIndicator = 0;

			char fieldMssgDataType = 'B';
			char fieldLengthDataType = 'B';
			char Subelements = 'N';
			for (; i < bitMap.length(); i++) {
				data = "";

				switch (bitMap.charAt(i)) {

				case '1':

					messageSize = fieldInfo[i].getFieldlength();
					// added by priyan
					if (messageSize == 0)
						continue;
					//
					fieldLengthDataType = fieldInfo[i].getFieldlengthtype();
					fieldMssgDataType = fieldInfo[i].getBitdatatype();
					Subelements = fieldInfo[i].getSubelements();

					if (messageSize > 0) {

						switch (fieldMssgDataType) {
						case 'I':
							data = IsoUtil.byteArrayToHexString(
									message.substring(offset, offset + 8).getBytes(StandardCharsets.ISO_8859_1));
							/*
							 * secondaryBitMap = Hex.encodeHexString(message.substring(offset, offset +
							 * 8).getBytes(StandardCharsets.ISO_8859_1));
							 * 
							 * isoBuffer.put(Constants.FIELD_1, secondaryBitMap);
							 * isoBuffer.put("SECONDARY-BITMAP", secondaryBitMap); secondaryBitMap =
							 * parseBitmap(secondaryBitMap);
							 */
							offset += messageSize;
							break;
						case 'B': // Compress Data

							if (i == 54) {
								messageSize = Math.abs(messageSize);
								field55 = message.substring(offset, offset + messageSize);
								offset += messageSize;
								messageSize = Math.abs(Integer.parseInt(field55));
								data = message.substring(offset, offset + messageSize);
								offset += messageSize;
								data = IsoUtil.asciiChar2hex(data);

							}
							/*
							 * if (messageSize % 2 != 0) offset = offset + 1;
							 */

							data = message.substring(offset, offset + messageSize);

							offset += messageSize;

							break;

						case 'S':// Compress length but Data in Expanded

							messageSize = messageSize * 2;
							data = message.substring(offset, offset + messageSize);

							offset += messageSize;
							break;

						case 'T':

							data = message.substring(offset, offset + messageSize);

							offset += messageSize;

							break;

						case 'E':
							messageSize = messageSize * 2;
							String tempdata = new String((message.substring(offset, offset + messageSize))
									.getBytes(StandardCharsets.ISO_8859_1), "8859_1");
							data = new String(IsoUtil.binary2asciiChar(IsoUtil.hex2binary((tempdata)))
									.getBytes(StandardCharsets.ISO_8859_1), "Cp1047");
							offset += messageSize;

							break;

						case 'H': // Clear Length

							data = message.substring(offset, offset + messageSize);

							offset += messageSize;

							break;
						}

						if (i + 1 != 4)
							data = trimUnWantedLeftPads(data, fieldInfo[i].getFieldlength());

						if (data.trim().length() == 0)
							data = "*";

						isoBuffer.put("F-" + (i + 1), data);
					} else { // Dynamic Length
						tempLengthIndicator = Integer
								.parseInt(message.substring(offset, offset + Math.abs(messageSize)), 10);

						lengthIndicator = convertLengthIndicator(tempLengthIndicator, fieldLengthDataType);

						if (Subelements == 'S') {
							if (fieldLengthDataType == 'B')
								lengthIndicator = lengthIndicator * 2;

							splitSubFieldMessage(message.substring(offset + (-1 * messageSize),
									(offset + (-1 * messageSize) + lengthIndicator)), isoBuffer, (i + 1) + "");
							offset = (offset + 2) + (lengthIndicator);
						} else {
							switch (fieldMssgDataType) {
							case 'A':

								messageSize = Math.abs(messageSize);
								// field55 = message.substring(offset, offset+ lengthIndicator);
								offset += messageSize;
								// messageSize = Math.abs(Integer.parseInt(lengthIndicator));
								data = message.substring(offset, offset + lengthIndicator);
								offset += lengthIndicator;
								data = IsoUtil.asciiChar2hex(data);
								break;
							case 'B':

								if (i == 54) {
									messageSize = Math.abs(messageSize);
									field55 = message.substring(offset, offset + messageSize);
									offset += messageSize;
									messageSize = Math.abs(Integer.parseInt(field55));
									data = message.substring(offset, offset + messageSize);
									offset += messageSize;
									data = IsoUtil.asciiChar2hex(data);

								}

								offset += (-1 * messageSize);

								data = message.substring(offset, offset + lengthIndicator);
								if ((i + 1) == 35) {
									data = data.replaceAll("3D", "=");
									data = data.replaceAll("3C", "<");
									data = data.replaceAll("3E", ">");
									data = data.replaceAll("3A", ":");
									data = data.replaceAll("3d", "=");
									data = data.replaceAll("3c", "<");
									data = data.replaceAll("3e", ">");
									data = data.replaceAll("3a", ":");
								}

								offset += lengthIndicator;

								break;

							case 'E':
							case 'T':

								offset += (-1 * messageSize);

								data = new String(IsoUtil
										.binary2asciiChar(
												IsoUtil.hex2binary(message.substring(offset, offset + lengthIndicator)))
										.getBytes(), "Cp1047");

								offset += lengthIndicator;

								break;

							case 'H':

								offset += (-1 * messageSize);

								data = message.substring(offset, offset + tempLengthIndicator);

								offset += tempLengthIndicator;

								break;
							}

							data = trimUnWantedLeftPads(data, tempLengthIndicator, fieldLengthDataType);

							// Log.debug("F-" + (i + 1) + "\t\t\t::\t", data);

							if (data.trim().length() == 0)
								data = "*";

							isoBuffer.put("F-" + (i + 1), data);
						}
					}

					break;

				default:

					isoBuffer.put("F-" + (i + 1), "*");

					break;

				}

			}
		} catch (Exception e) {
			Log.error("Parsing error in field " + String.valueOf(i), e);
			throw new RuntimeException("Error occured while splitting Message::");
		}

	}

	public static String subField60Formatting(Map<String, String> isoBuffer) {
		String bitDetails = "";
		StringBuffer data = new StringBuffer();
		StringBuffer tempData = new StringBuffer();

		int i = 0;
		for (i = 0; i < 10; i++) {
			if ((i + 1) == 1 || (i + 1) == 3 || (i + 1) == 6 || (i + 1) == 9) {
				bitDetails = isoBuffer.get("S-60." + (i + 1));
				if (bitDetails.equals("*")) {
					tempData.append(" ");
				} else {
					tempData.append(bitDetails);
				}
				bitDetails = isoBuffer.get("S-60." + (i + 2));
				if (bitDetails.equals("*")) {
					tempData.append(" ");
				} else {
					tempData.append(bitDetails);
				}
				i++;
				data.append(tempData.toString());
				tempData.delete(0, tempData.length());
			} else {
				bitDetails = isoBuffer.get("S-60." + (i + 1));
				if (bitDetails.equals("*")) {
					tempData.append("  ");
				} else {
					data.append(bitDetails);
				}
			}
		}
		String finalData = data.toString();
		for (int j = data.length(); j >= 0; j = j - 2) {
			if (finalData.substring(j - 2, j).equals("  ")) {
				finalData = finalData.substring(0, j - 2) + finalData.substring(j - 2, j).replace("  ", "");
			} else {
				j = 0;
				break;
			}

		}
		finalData = finalData.replaceAll(" ", "0");

		// finalData=IsoUtil.binary2hex(IsoUtil.hex2binary(finalData));

		return IsoUtil.binary2hex(IsoUtil.hex2binary((finalData.length() / 2) + "")) + finalData;

	}

	private static String trimUnWantedLeftPads(String data, int tempLengthIndicator, char fieldLengthDataType) {

		switch (fieldLengthDataType) {

		case 'B':

			if (tempLengthIndicator % 2 != 0) {

				data = StringUtils.removeStart(data, "0");
				data = StringUtils.removeStart(data, "F");
			}
			break;
		}

		return data;
	}

	private static int convertLengthIndicator(int lengthIndicator, char fieldLengthDataType) {

		switch (fieldLengthDataType) {

		case 'B':

			/*
			 * if (lengthIndicator % 2 != 0) { lengthIndicator = (lengthIndicator + 1); }
			 */

			break;

		case 'H':
		case 'T':
			lengthIndicator = lengthIndicator * 2;
			break;

		default:

			break;
		}

		return lengthIndicator;
	}

	private static String trimUnWantedLeftPads(String data, int lengthIndicator) {

		if (data.length() > lengthIndicator) {

			data = StringUtils.removeStart(data, "0");
			data = StringUtils.removeStart(data, "F");
		}

		return data;
	}

	protected static String parseBitmap(final String bitmap) {

		String upperBitmap = "00000000000000000000000000000000";
		String lowerBitmap = "00000000000000000000000000000000";

		upperBitmap += Long.toBinaryString(Long.parseLong(bitmap.substring(0, 8), 16));
		lowerBitmap += Long.toBinaryString(Long.parseLong(bitmap.substring(8), 16));

		upperBitmap = upperBitmap.substring(upperBitmap.length() - 32);
		lowerBitmap = lowerBitmap.substring(lowerBitmap.length() - 32);

		return upperBitmap + lowerBitmap;
	}

	/***************************************************************************
	 * Purpose in brief : Creating the Bitmap & Field attributes string Written by :
	 * SubramaniMohanam Created on : 30th April 2015 Last Modified : 30th April
	 * 2015; Arguments passed : isoBuffer,fieldMessage,finalbitMap,VisaField
	 * Modified By : SubramaniMohanam
	 **************************************************************************/

	protected int getBitmapFieldAtt(final Map<String, String> isoBuffer, StringBuffer fieldMessage,
			StringBuffer finalbitMap, DinersField aauthfield[]) {
		String bitDetails = "";
		int messageSize = 0;
		char fieldLengthDataType = 'B';
		char fieldMssgDataType = 'B';
		char fieldSubElement = 'N';
		String data = "";
		int bitMapLen = 0;
		StringBuffer bitMap = new StringBuffer();

		try {
			for (int i = 0; i < 192; i++) {

				bitDetails = isoBuffer.get("F-" + (i + 1));
				if (!bitDetails.equals("*")) {

					if ((i + 1) != 35 && (i + 1) != 14 && (i + 1) != 2) {
					}

					bitMap.append("1");

					data = bitDetails;
					messageSize = aauthfield[i].getFieldlength();
					fieldLengthDataType = aauthfield[i].getFieldlengthtype();
					fieldMssgDataType = aauthfield[i].getBitdatatype();
					fieldSubElement = aauthfield[i].getSubelements();

					if (fieldSubElement == 'S') {
						fieldMessage.append(getSubBitmapFieldAtt(isoBuffer, (i + 1 + "")));
					} else {
						switch (fieldMssgDataType) {

						case 'B':

							for (int j = 0; j < data.length(); j++) {
								if (IsoUtil.noSplChars(data.charAt(j))) {
									data = data.replaceAll(data.charAt(j) + "", IsoUtil.alpha2Hex(data.charAt(j) + ""));
								}
							}

							/*
							 * asciivalue = IsoUtil.binary2hex(IsoUtil .hex2binary(data));
							 */

							fieldMessage.append(calculateLength(data.length(), messageSize, fieldLengthDataType));

							fieldMessage.append(data);
							break;
						case 'A':
							messageSize = Math.abs(messageSize);
							fieldMessage.append(
									Util.appendChar(Integer.toString(data.length() / 2), '0', messageSize, true));
							fieldMessage.append(IsoUtil.hex2AsciiChar(data));
							break;
						case 'S':

							fieldMessage.append(calculateLength(data.length(), messageSize, fieldLengthDataType));
							fieldMessage.append(new String(data));
							break;

						case 'E':

							/*
							 * fieldMessage.append(calculateLength(data .length(), messageSize,
							 * fieldLengthDataType)); fieldMessage.append(IsoUtil .alpha2Hex((new String(new
							 * String(data .getBytes(), "8859_1") .getBytes("cp037")))));
							 */

							fieldMessage.append(calculateLength(data.length(), messageSize, fieldLengthDataType));
							// fieldMessage.append((new String(new
							// String(data.getBytes(),
							// StandardCharsets.ISO_8859_1).getBytes("Cp1047"))));
							fieldMessage.append(new String(data.getBytes("Cp1047"), "ISO8859_1"));
							break;
						default:
							fieldMessage.append(calculateLength(data.length(), messageSize, fieldLengthDataType));
							fieldMessage.append(new String(data));
							break;

						}
					}
				} else {
					bitMap.append("0");
				}
			}
			// **************** Bitmap formatting starts*********************
			String primaryBitMap = bitMap.substring(0, 64);
			String secondaryBitMap = bitMap.substring(64, 128);
			String tertiaryBitMap = bitMap.substring(128, 192);

			finalbitMap.append(primaryBitMap.toString());

			bitMapLen = 8;

			if (!checkZero(tertiaryBitMap)) {
				secondaryBitMap = StringUtils.overlay(secondaryBitMap, "1", 0, 1);
				finalbitMap.setCharAt(0, '1');
				finalbitMap.append(secondaryBitMap + tertiaryBitMap);
				bitMapLen = 48;
			} else if (!checkZero(secondaryBitMap)) {
				finalbitMap.setCharAt(0, '1');
				finalbitMap.append(secondaryBitMap);
				bitMapLen = 16;
			}
			// **************** Bitmap formatting ends*********************
		} catch (Exception e) {
			throw new RuntimeException("Problem in Bitmap and filed attribute formatting ");
		} finally {
			bitMap = null;
		}
		return bitMapLen;
	}

	/***************************************************************************
	 * Purpose in brief : Filling the Header details in buffer Written by :
	 * SubramaniMohanam Created on : 30th April 2015 Last Modified : 30th April
	 * 2015; Arguments passed : Message Modified By : SubramaniMohanam
	 **************************************************************************/

	protected StringBuffer headerFormat(StringBuffer message) {

		message.append(IsoUtil.binary2hex(IsoUtil.hex2binary(msgObj.get(Constants.H_TOT_MSG_LNT))));
		message.append("0000");
		message.append(IsoUtil.binary2hex(IsoUtil.hex2binary(msgObj.get(Constants.H_LENGTH))));

		message.append(IsoUtil.binary2hex(IsoUtil.hex2binary(msgObj.get(Constants.H_FLAG_FORMAT))));

		message.append(IsoUtil.binary2hex(IsoUtil.hex2binary(msgObj.get(Constants.H_TXT_FORMAT))));

		message.append(IsoUtil.binary2hex(IsoUtil.hex2binary(msgObj.get(Constants.H_TOT_MSG_LNT))));

		message.append(IsoUtil.binary2hex(IsoUtil.hex2binary(msgObj.get(Constants.H_DST_STAT_ID))));

		message.append(IsoUtil.binary2hex(IsoUtil.hex2binary(msgObj.get(Constants.H_SRC_STAT_ID))));

		message.append(IsoUtil.binary2hex(IsoUtil.hex2binary(msgObj.get(Constants.H_RND_CON_INF))));

		message.append(IsoUtil.binary2hex(IsoUtil.hex2binary(msgObj.get(Constants.H_BASE_1_FLAG))));

		message.append(IsoUtil.binary2hex(IsoUtil.hex2binary(msgObj.get(Constants.H_MSG_STATUS_FLAG))));

		message.append(IsoUtil.binary2hex(IsoUtil.hex2binary(msgObj.get(Constants.H_BAT_NO))));

		message.append(IsoUtil.binary2hex(IsoUtil.hex2binary(msgObj.get(Constants.H_RESERVED))));

		message.append(IsoUtil.binary2hex(IsoUtil.hex2binary(msgObj.get(Constants.H_USR_INFO))));

		return message;

	}

	protected boolean checkZero(String bitsdetail) {
		boolean flag = true;
		try {
			if (bitsdetail.contains("1")) {
				flag = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;

	}

	/***************************************************************************
	 * Purpose in brief : Calculate the data length based on the field length data
	 * type Written by : SubramaniMohanam Created on : 30th April 2015 Last Modified
	 * : 30th April 2015; Arguments passed :
	 * dataLength,messageSize,fieldLengthDataType Modified By : SubramaniMohanam
	 **************************************************************************/

	private static String calculateLength(int dataLength, int messageSize, char fieldLengthDataType) throws Exception {

		String length = "";
		if (messageSize < 0) {

			switch (fieldLengthDataType) {

			case 'B':
				length = IsoUtil.leftPadZeros(new String((dataLength + "").getBytes(StandardCharsets.ISO_8859_1),
						StandardCharsets.ISO_8859_1), Math.abs(messageSize));
				break;

			case 'H':

				length = IsoUtil.binary2hex(IsoUtil.hex2binary(Integer.toHexString(dataLength)));
				break;

			case 'T':
				if (dataLength % 2 != 0) {
					dataLength = dataLength + 1;
				}
				length = IsoUtil.binary2hex(IsoUtil.hex2binary(Integer.toHexString(dataLength / 2)));

				break;

			default:

				length = Integer.toHexString(dataLength);

				break;
			}
		}

		return length;
	}

	/***************************************************************************
	 * Purpose in brief : Calculate the header length to pack the message Written by
	 * : SubramaniMohanam Created on : 30th April 2015 Last Modified : 30th April
	 * 2015; Arguments passed : isoBuffer Modified By : SubramaniMohanam
	 **************************************************************************/

	public int getHeaderLength(final Map<String, String> isoBuffer) {
		int HdrLen = 0;

		HdrLen += IsoUtil.binary2hex(IsoUtil.hex2binary(isoBuffer.get(Constants.H_LENGTH))).length();

		HdrLen += IsoUtil.binary2hex(IsoUtil.hex2binary(isoBuffer.get(Constants.H_FLAG_FORMAT))).length();

		HdrLen += IsoUtil.binary2hex(IsoUtil.hex2binary(isoBuffer.get(Constants.H_TXT_FORMAT))).length();

		HdrLen += IsoUtil.binary2hex(IsoUtil.hex2binary(isoBuffer.get(Constants.H_DST_STAT_ID))).length();

		HdrLen += IsoUtil.binary2hex(IsoUtil.hex2binary(isoBuffer.get(Constants.H_SRC_STAT_ID))).length();

		HdrLen += IsoUtil.binary2hex(IsoUtil.hex2binary(isoBuffer.get(Constants.H_RND_CON_INF))).length();

		HdrLen += IsoUtil.binary2hex(IsoUtil.hex2binary(isoBuffer.get(Constants.H_BASE_1_FLAG))).length();

		HdrLen += IsoUtil.binary2hex(IsoUtil.hex2binary(isoBuffer.get(Constants.H_MSG_STATUS_FLAG))).length();

		HdrLen += IsoUtil.binary2hex(IsoUtil.hex2binary(isoBuffer.get(Constants.H_BAT_NO))).length();

		HdrLen += IsoUtil.binary2hex(IsoUtil.hex2binary(isoBuffer.get(Constants.H_RESERVED))).length();

		HdrLen += IsoUtil.binary2hex(IsoUtil.hex2binary(isoBuffer.get(Constants.H_USR_INFO))).length();

		HdrLen += 4;

		// HdrLen += HdrLen;
		return HdrLen;
	}

	/***************************************************************************
	 * Purpose in brief : Formatting the Sub Element bits in the pack method Written
	 * by : Maathavan Created on : 30th April 2015 Last Modified : 30th April 2015;
	 * Arguments passed : isoBuffer,bitPos Modified By : Maathavan
	 **************************************************************************/

	public String getSubBitmapFieldAtt(final Map<String, String> isoBuffer, String bitPos) {

		String bitDetails = "";
		int messageSize = 0;
		int subFieldLength = 0;
		StringBuffer bitMap = new StringBuffer();
		StringBuffer fieldMessage = new StringBuffer();

		char fieldMssgDataType = 'B';
		char fieldLengthDataType = 'B';
		String data = "";
		String value = "";
		int Fieldlength = 26;
		int bitmapLength = 0;
		int bitmapchar = 0;

		try {
			isoBuffer.put("TERTIARY-BITMAP", "*");
			isoBuffer.put("SECONDARY-BITMAP", "*");

			for (int i = 0; i < Fieldlength; i++) {

				if (!isoBuffer.containsKey("S-" + bitPos + "." + (i + 1))) {
					isoBuffer.put("S-" + bitPos + "." + (i + 1), "*");
				}
			}

			if (bitPos.equals("60")) {
				return subField60Formatting(isoBuffer);
			}

			for (int i = 0; i < Fieldlength; i++) {

				if (bitPos.equals("126") && i == 17) {
					bitDetails = isoBuffer.get("S-" + bitPos + "." + (i + 1));
				}

				if (!bitDetails.equals("*")) {
					if (!(bitPos + "." + (i + 1)).equals("126.10")) {
					}
					bitMap.append("1");

					data = bitDetails;
					if (bitPos.equals("62")) {
						messageSize = DinersMessage.VISAFIELDMAP62[i].getFieldlength();
						fieldMssgDataType = DinersMessage.VISAFIELDMAP62[i].getBitdatatype();
						Fieldlength = 26;
						bitmapLength = 8;
						bitmapchar = 64;
					} else if (bitPos.equals("63")) {
						messageSize = DinersMessage.VISAFIELDMAP63[i].getFieldlength();
						fieldMssgDataType = DinersMessage.VISAFIELDMAP63[i].getBitdatatype();
						Fieldlength = 21;
						bitmapLength = 3;
						bitmapchar = 24;
					} else if (bitPos.equals("44")) {
						messageSize = DinersMessage.VISAFIELDMAP44[i].getFieldlength();
						fieldMssgDataType = DinersMessage.VISAFIELDMAP44[i].getBitdatatype();
						Fieldlength = 15;
					} else if (bitPos.equals("126")) {
						messageSize = DinersMessage.VISAFIELDMAP126[i].getFieldlength();
						fieldMssgDataType = DinersMessage.VISAFIELDMAP126[i].getBitdatatype();
						fieldLengthDataType = DinersMessage.VISAFIELDMAP126[i].getFieldlengthtype();
						Fieldlength = 19;
						bitmapLength = 8;
						bitmapchar = 64;
					}
					if (messageSize < 0) {
						fieldMessage.append(calculateLength(data.length(), messageSize, fieldLengthDataType));
						subFieldLength += 1;
					}

					switch (fieldMssgDataType) {
					case 'T':
					case 'B':

						if (messageSize % 2 != 0) {
							messageSize = messageSize + 1;

						}
						if (data.length() % 2 != 0) {
							value = data + "0";
						} else {
							value = data;
						}

						if (messageSize > 0) {
							subFieldLength = subFieldLength + messageSize / 2;
						} else if (messageSize < 0) {
							subFieldLength = subFieldLength + value.length() / 2;
						}

						fieldMessage.append(value);

						break;

					case 'E':

						value = (IsoUtil
								.alpha2Hex((new String(new String(data.getBytes(), "ISO8859_1").getBytes("cp1047")))));
						fieldMessage.append(value);

						if (messageSize > 0) {
							subFieldLength = subFieldLength + messageSize;
						} else if (messageSize < 0) {
							subFieldLength = subFieldLength + data.length();
						}

						break;
					default:
						break;

					}
				} else {
					bitMap.append("0");
				}
			}
			IsoUtil.padRightZero(bitMap, bitmapchar);

			return String
					.valueOf(IsoUtil.binary2hex(IsoUtil.hex2binary(Integer.toHexString(subFieldLength + bitmapLength))))
					+ (bitmapLength != 0 ? (IsoUtil.binary2hex(bitMap.toString())) : "") + fieldMessage.toString();

		} catch (Exception e) {
			throw new RuntimeException("Error occured Sub Element formatting");
		} finally {
			bitMap = null;
			fieldMessage = null;
		}
	}

	/***************************************************************************
	 * Purpose in brief : Splitting the Sub Element bits in the unpack method
	 * Written by : Maathavan Created on : 30th April 2015 Last Modified : 30th
	 * April 2015; Arguments passed : isoBuffer,bitPos Modified By : Maathavan
	 **************************************************************************/

	private static void splitSubFieldMessage(String message, Map<String, String> isoBuffer, String bitpos) {
		try {
			String data = "";
			int offset = 16;
			int messageSize = 0;
			int fieldLength = 0;
			int subFieldLength = message.length();
			int tempLengthIndicator = 0;
			int lengthIndicator = 0;

			String bitMap = "";

			char fieldMssgDataType = 'B';
			DinersField[] field_length_indicator_temp = null;
			char field_length_indicator_type = 'B';

			if (bitpos.equals("63")) {
				field_length_indicator_temp = DinersMessage.VISAFIELDMAP63;
				fieldLength = 21;
				offset = 6;
				bitMap = IsoUtil.hex2binary(message.substring(0, 6));
			} else if (bitpos.equals("60")) {
				field_length_indicator_temp = DinersMessage.VISAFIELDMAP60;
				fieldLength = 10;
				offset = 0;
				bitMap = IsoUtil.hex2binary("0000000000000000");
			} else if (bitpos.equals("44")) {
				field_length_indicator_temp = DinersMessage.VISAFIELDMAP44;
				fieldLength = 14;
				offset = 0;
				bitMap = IsoUtil.hex2binary("0000000000000000");
			} else if (bitpos.equals("62")) {
				field_length_indicator_temp = DinersMessage.VISAFIELDMAP62;
				fieldLength = 26;

				bitMap = IsoUtil.hex2binary(message.substring(0, 16));
			} else if (bitpos.equals("126")) {
				field_length_indicator_temp = DinersMessage.VISAFIELDMAP126;
				bitMap = IsoUtil.hex2binary(message.substring(0, 16));
				fieldLength = 19;
			}

			for (int i = 0; i < fieldLength; i++) {

				data = "";

				if (bitMap.charAt(i) == '1' || (bitpos.equals("44") && subFieldLength > 0)
						|| (bitpos.equals("60")) && subFieldLength > 0) {

					messageSize = field_length_indicator_temp[i].getFieldlength();
					fieldMssgDataType = field_length_indicator_temp[i].getBitdatatype();

					if (messageSize > 0) {
						switch (fieldMssgDataType) {

						case 'T':
						case 'B':
							subFieldLength = subFieldLength - messageSize;
							if ((messageSize % 2 != 0) && !bitpos.equals("60")) {
								offset = offset + 1;
							}

							data = message.substring(offset, offset + messageSize);

							offset += messageSize;

							break;

						case 'E':
							subFieldLength = subFieldLength - (messageSize * 2);
							messageSize = messageSize * 2;

							String tempdata = new String((message.substring(offset, offset + messageSize)).getBytes(),
									"8859_1");
							data = new String(IsoUtil.binary2asciiChar(IsoUtil.hex2binary((tempdata))).getBytes(),
									"Cp1047");
							offset += messageSize;

							break;
						case 'N':
							offset += 2;

						}

						if (i + 1 != 4) {
							data = trimUnWantedLeftPads(data, field_length_indicator_temp[i].getFieldlength());
						}

						isoBuffer.put("S-" + bitpos + "." + (i + 1), data);
					} else if (messageSize < 0) {
						field_length_indicator_type = field_length_indicator_temp[i].getFieldlengthtype();

						tempLengthIndicator = Integer
								.parseInt(message.substring(offset, offset + Math.abs(messageSize)), 16);

						lengthIndicator = convertLengthIndicator(tempLengthIndicator, field_length_indicator_type);

						if (bitpos.equals("126") && ((i + 1) == 6 || (i + 1) == 7)) {

							offset = offset + 32 - lengthIndicator;
						} else if (bitpos.equals("126") && ((i + 1) == 18)) {
							offset = offset + 22 - lengthIndicator;
						}

						switch (fieldMssgDataType) {
						case 'T':
						case 'B':

							offset += (-1 * messageSize);

							data = message.substring(offset, offset + lengthIndicator);

							offset += lengthIndicator;

							break;

						case 'E':

							offset += (-1 * messageSize);

							data = new String(IsoUtil
									.binary2asciiChar(
											IsoUtil.hex2binary(message.substring(offset, offset + lengthIndicator)))
									.getBytes(), "Cp1047");

							offset += lengthIndicator;

							break;
						}

						data = trimUnWantedLeftPads(data, tempLengthIndicator, field_length_indicator_type);

						isoBuffer.put("S-" + bitpos + "." + (i + 1), data);
					}
				}

			}
		} catch (Exception e) {
			throw new RuntimeException("Error occured splitSubFieldMessage");
		}

	}

	protected Map<String, String> msgObj;

	/*
	 * public static void main(String[] args) throws Exception {
	 * 
	 * Map<String, String> isoBuffer = new HashMap<String, String>();
	 * splitSubFieldMessage(
	 * "F00000000000000001F101F106F8F5F3F2F1F409F9F8F5F6F3F2F7F4F1", isoBuffer,
	 * "126");
	 * 
	 * int offset = 78; int messageSize = -2; String message =
	 * "F1F8F1F42030010082000000F0F0F0F0F0F0F4F3F7F4F5F6F1F8F0F5F1F8F1F0F2F6F5F2F8F0F1F1F1F0F0F0F0F0F0F1F2F3F4F5F8F0F0";
	 * String tempdata = new String((message.substring(offset, (offset +
	 * (Math.abs(messageSize) * 2)))).getBytes(), "8859_1"); String length = new
	 * String(IsoUtil.binary2asciiChar( IsoUtil.hex2binary((tempdata))).getBytes(),
	 * "Cp1047");
	 * 
	 * new String(IsoUtil.binary2asciiChar(
	 * IsoUtil.hex2binary((message.substring(offset + 4, offset + 4 +
	 * (Integer.parseInt(length) * 2))))).getBytes(), "Cp1047");
	 * 
	 * }
	 */
}
