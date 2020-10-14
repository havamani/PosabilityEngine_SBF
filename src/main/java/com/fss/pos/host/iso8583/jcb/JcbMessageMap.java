package com.fss.pos.host.iso8583.jcb;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class JcbMessageMap {

	protected Map<String, String> msgObj;

	public JcbMessageMap() {
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

	/*
	 * Creating the Bitmap & Field attributes string Written
	 */

	protected int getBitmapFieldAtt(final Map<String, String> isoBuffer,
			StringBuffer fieldMessage, StringBuffer finalbitMap,
			JcbField aauthfield[]) {
		String bitDetails = "";
		int messageSize = 0;
		char fieldLengthDataType = 'B';
		char fieldMssgDataType = 'B';
		char fieldSubElement = 'N';
		String data = "";
		int bitMapLen = 0;
		StringBuffer bitMap = new StringBuffer();
		int i = 0;
		try {
			for (; i < 128; i++) {

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
						fieldMessage.append(getSubBitmapFieldAtt(isoBuffer,
								(i + 1 + "")));
					} else {
						switch (fieldMssgDataType) {

						case 'B':

							// String asciivalue = null;

							for (int j = 0; j < data.length(); j++) {
								if (JcbISOUtil.noSplChars(data.charAt(j))) {
									data = data.replaceAll(
											data.charAt(j) + "",
											JcbISOUtil.alpha2Hex(data.charAt(j)
													+ ""));
								}
							}

							fieldMessage.append(calculateLength(data.length(),
									messageSize, fieldLengthDataType));

							fieldMessage.append(JcbISOUtil.hex2AsciiChar(data));

							break;

						case 'S':

							fieldMessage.append(calculateLength(data.length(),
									messageSize, fieldLengthDataType));
							fieldMessage.append(new String(data));
							break;

						case 'E':

							fieldMessage.append(calculateLength(data.length(),
									messageSize, fieldLengthDataType));
							fieldMessage.append((new String(new String(data
									.getBytes(), "ISO8859_1")
									.getBytes("Cp1047"))));

							break;
						default:
							fieldMessage.append(calculateLength(data.length(),
									messageSize, fieldLengthDataType));
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
			//
			finalbitMap.append(primaryBitMap.toString());
			//
			bitMapLen = 16;
			//
			if (!checkZero(secondaryBitMap)) {
				finalbitMap.setCharAt(0, '1');
				finalbitMap.append(secondaryBitMap);
				bitMapLen = 32;
			}

			// **************** Bitmap formatting ends*********************
		} catch (Exception e) {
			// Log.error("VISA getBitmapFieldAtt field" + (i + 1), e);
			throw new RuntimeException(
					"Problem in Bitmap and filed attribute formatting ");
		} finally {
			bitMap = null;
		}
		return bitMapLen;
	}

	

	/*
	 * CheckZero
	 */
	protected boolean checkZero(String bitsdetail) {
		boolean flag = true;
		try {
			if (bitsdetail.contains("1"))
				flag = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;

	}

	/*
	 * parseBitmap
	 */

	protected static String parseBitmap(final String bitmap) {

		StringBuilder parseBitmapResult = new StringBuilder();
		StringBuilder losgUpperBitmap = new StringBuilder();
		losgUpperBitmap.append("00000000000000000000000000000000");
		StringBuilder losgLowerBitmap = new StringBuilder();
		losgLowerBitmap.append("00000000000000000000000000000000");
		losgUpperBitmap.append(Long.toBinaryString(Long.parseLong(
				bitmap.substring(0, 8), 16)));
		losgLowerBitmap.append(Long.toBinaryString(Long.parseLong(
				bitmap.substring(8), 16)));
		parseBitmapResult.append(losgUpperBitmap.toString().substring(
				losgUpperBitmap.toString().length() - 32));
		parseBitmapResult.append(losgLowerBitmap.toString().substring(
				losgLowerBitmap.toString().length() - 32));
		return parseBitmapResult.toString();
	}

	/*
	 * Filling the buffer with dummy data Written by :
	 */

	protected static void fillISOBuffer(Map<String, String> isoBuffer)
			throws Exception {
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

	/*
	 * Splitting the JCB message to fill the buffer Written
	 * 
	 * @return
	 */

	protected static void splitMessage(String message,
			Map<String, String> isoBuffer, String bitMap, int offset,
			JcbField[] fieldInfo) {

		int i = 0;
		try {
			String data = "";

			int messageSize = 0;
			String lengthIndicator = null;
			String tempLengthIndicator = null;

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

						case 'B':

							/*
							 * data = message.substring(offset, offset +
							 * messageSize);
							 */

							data = JcbISOUtil.alpha2Hex(message.substring(
									offset, offset + messageSize));
							offset += messageSize;

							break;

						case 'T':

							// offset += (-1 * messageSize);

							data = message.substring(offset, offset
									+ messageSize);
							offset += messageSize;

							break;

						case 'S':

							messageSize = messageSize * 2;
							data = message.substring(offset, offset
									+ messageSize);

							offset += messageSize;
							break;

						case 'E':

							data = new String(message.substring(offset,
									offset + messageSize).getBytes(
									StandardCharsets.ISO_8859_1), "Cp1047");
							offset += messageSize;

							break;
						}

						isoBuffer.put("F-" + (i + 1), data);
					}

					else

					{
						messageSize = Math.abs(messageSize);
						tempLengthIndicator = (message.substring(offset, offset
								+ messageSize));

						lengthIndicator = convertLengthIndicator(
								tempLengthIndicator, fieldLengthDataType);

						offset += messageSize;


						if (Subelements == 'S') {/*
												 * if (fieldLengthDataType ==
												 * 'B') lengthIndicator =
												 * lengthIndicator * 2;
												 * 
												 * splitSubFieldMessage(message.
												 * substring(offset + (-1 *
												 * messageSize), (offset + (-1 *
												 * messageSize) +
												 * lengthIndicator)), isoBuffer,
												 * (i + 1) + ""); offset =
												 * (offset + 2) +
												 * (lengthIndicator);
												 */
						} else {
							switch (fieldMssgDataType) {

							case 'B':

								// offset += (-1 * messageSize);

								if (Integer.parseInt(lengthIndicator) % 2 != 0) {
									lengthIndicator = Integer.toString(Integer.parseInt(lengthIndicator)+1);
									lengthIndicator = Integer.toString(Integer
											.parseInt(lengthIndicator) / 2);
									data = message.substring(offset,offset+ Integer.parseInt(lengthIndicator));
									data = JcbISOUtil.alpha2Hex(data);
									data = data.substring(1);
								} else {
									if(!lengthIndicator.equals("02")){
										lengthIndicator = Integer.toString(Integer
												.parseInt(lengthIndicator) / 2);
									}
									
									data = message.substring(offset,offset
															+ Integer
																	.parseInt(lengthIndicator));
									data = JcbISOUtil.alpha2Hex(data);
								}

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

								offset += Integer.parseInt(lengthIndicator);

								break;

							case 'H':

								offset += (-1 * messageSize);

								data = message
										.substring(
												offset,
												offset
														+ Integer
																.parseInt(tempLengthIndicator));


								offset += Integer.parseInt(tempLengthIndicator);

								break;

							case 'E':
								if(!lengthIndicator.equals("02")){
									lengthIndicator = Integer.toString(Integer
											.parseInt(lengthIndicator) / 2);
								}
								data = message.substring(offset,offset
										+ Integer
												.parseInt(lengthIndicator));
								data = new String(data.getBytes(StandardCharsets.ISO_8859_1), "Cp1047");

								offset += Integer.parseInt(lengthIndicator);
								break;
							case 'T':

								offset += (-1 * messageSize);

								data = new String(JcbISOUtil.binary2asciiChar(JcbISOUtil.hex2binary(message
														.substring(offset,offset+ Integer.parseInt(lengthIndicator))))
												.getBytes(), "Cp1047");

								offset += Integer.parseInt(lengthIndicator);

								break;
							}

							/*
							 * data = trimUnWantedLeftPads(data,
							 * tempLengthIndicator, fieldLengthDataType);
							 */

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
			// Log.error("Parsing error in field " + String.valueOf(i), e);
			throw new RuntimeException(
					"Error occured while splitting Message::");
		}

	}

	private static String trimUnWantedLeftPads(String data,
			int tempLengthIndicator, char fieldLengthDataType) {

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

	private static String trimUnWantedLeftPads(String data, int lengthIndicator) {

		if (data.length() > lengthIndicator) {

			data = StringUtils.removeStart(data, "0");
			data = StringUtils.removeStart(data, "F");
		}

		return data;
	}

	private static String convertLengthIndicator(String tempLengthIndicator,
			char fieldLengthDataType) {

		switch (fieldLengthDataType) {

		case 'B':

			tempLengthIndicator = JcbISOUtil.alpha2Hex(tempLengthIndicator);

			break;

		case 'H':
		/*	length = JcbISOUtil.binary2hex(JcbISOUtil.hex2binary(Integer
					.toHexString(dataLength)));

			length = JcbISOUtil.hex2AsciiChar(length);*/
			tempLengthIndicator = JcbISOUtil.asciiChar2hex(tempLengthIndicator);
			//tempLengthIndicator = JcbISOUtil.hex2binary(tempLengthIndicator);
			 tempLengthIndicator = Integer.toString(Integer.parseInt(tempLengthIndicator, 16));
			 break;
			
		case 'T':
			tempLengthIndicator = JcbISOUtil.asciiChar2hex(tempLengthIndicator);
			tempLengthIndicator = Integer.toString(Integer.parseInt(tempLengthIndicator, 16));
			tempLengthIndicator = Integer.toString(Integer.parseInt(tempLengthIndicator) * 2);
			break;

		default:

			break;
		}

		return tempLengthIndicator;
	}

	private static void splitSubFieldMessage(String message,
			Map<String, String> isoBuffer, String bitpos) {
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
			JcbField[] field_length_indicator_temp = null;
			char field_length_indicator_type = 'B';
			// Prakash

			// if (bitpos.equals("63")) {
			// field_length_indicator_temp = CupsMessageMap.CUPSFIELDMAP63;
			// fieldLength = 21;
			// offset = 6;
			// bitMap = JcbJcbISOUtil.hex2binary(message.substring(0, 6));
			// }
			// else

			// Prakash

			// if (bitpos.equals("60")) {
			// field_length_indicator_temp = JcbMessageMap.CUPSFIELDMAP60;
			// fieldLength = 10;
			// offset = 0;
			// bitMap = JcbJcbJcbISOUtil.hex2binary("0000000000000000");
			// }
			// else if (bitpos.equals("44")) {
			// field_length_indicator_temp = CupsMessageMap.CUPSFIELDMAP44;
			// fieldLength = 14;
			// offset = 0;
			// bitMap = JcbJcbISOUtil.hex2binary("0000000000000000");
			// return;

			// }
			// else if (bitpos.equals("62")) {
			// field_length_indicator_temp = CupsMessageMap.CUPSFIELDMAP62;
			// fieldLength = 26;
			//
			// bitMap = JcbJcbISOUtil.hex2binary(message.substring(0, 16));
			// }

			// prakash
			// else if (bitpos.equals("126")) {
			// field_length_indicator_temp = CupsMessageMap.CUPSFIELDMAP126;
			// bitMap = JcbJcbISOUtil.hex2binary(message.substring(0, 16));
			// fieldLength = 19;
			// }

			// prakash

			for (int i = 0; i < fieldLength; i++) {

				data = "";

				if (bitMap.charAt(i) == '1'
						|| (bitpos.equals("44") && subFieldLength > 0)
						|| (bitpos.equals("60")) && subFieldLength > 0) {

					messageSize = field_length_indicator_temp[i]
							.getFieldlength();
					fieldMssgDataType = field_length_indicator_temp[i]
							.getBitdatatype();

					if (messageSize > 0) {
						switch (fieldMssgDataType) {

						case 'T':
						case 'B':
							subFieldLength = subFieldLength - messageSize;
							if ((messageSize % 2 != 0) && !bitpos.equals("60"))
								offset = offset + 1;

							data = message.substring(offset, offset
									+ messageSize);

							offset += messageSize;

							break;

						case 'E':
							subFieldLength = subFieldLength - (messageSize * 2);
							messageSize = messageSize * 2;

							String tempdata = new String((message.substring(
									offset, offset + messageSize)).getBytes(),
									"8859_1");
							data = new String(JcbISOUtil.binary2asciiChar(
									JcbISOUtil.hex2binary((tempdata)))
									.getBytes(), "Cp1047");
							offset += messageSize;

							break;
						case 'N':
							offset += 2;

						}

						if (i + 1 != 4)
							data = trimUnWantedLeftPads(data,
									field_length_indicator_temp[i]
											.getFieldlength());

						// Log.trace("S-" + bitpos + "." + (i + 1) +
						// "\t\t\t::\t"
						// + data);
						isoBuffer.put("S-" + bitpos + "." + (i + 1), data);
					} else if (messageSize < 0) {
						field_length_indicator_type = field_length_indicator_temp[i]
								.getFieldlengthtype();

						tempLengthIndicator = Integer.parseInt(
								message.substring(offset,
										offset + Math.abs(messageSize)), 16);

						/*
						 * lengthIndicator = convertLengthIndicator(
						 * tempLengthIndicator, field_length_indicator_type);
						 */

						if (bitpos.equals("126")
								&& ((i + 1) == 6 || (i + 1) == 7)) {

							offset = offset + 32 - lengthIndicator;
						} else if (bitpos.equals("126") && ((i + 1) == 18)) {
							offset = offset + 22 - lengthIndicator;
						}

						switch (fieldMssgDataType) {
						case 'T':
						case 'B':

							offset += (-1 * messageSize);

							data = message.substring(offset, offset
									+ lengthIndicator);

							offset += lengthIndicator;

							break;

						case 'E':

							offset += (-1 * messageSize);

							data = new String(JcbISOUtil.binary2asciiChar(
									JcbISOUtil.hex2binary(message.substring(
											offset, offset + lengthIndicator)))
									.getBytes(), "Cp1047");

							offset += lengthIndicator;

							break;
						}

						data = trimUnWantedLeftPads(data, tempLengthIndicator,
								field_length_indicator_type);

						// Log.trace("S-" + bitpos + "." + (i + 1) +
						// "\t\t\t::\t"
						// + data);
						isoBuffer.put("S-" + bitpos + "." + (i + 1), data);
					}
				}

			}
		} catch (Exception e) {
			throw new RuntimeException("Error occured splitSubFieldMessage");
		}

	}

	public String getSubBitmapFieldAtt(final Map<String, String> isoBuffer,
			String bitPos) {

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

				if (!isoBuffer.containsKey("S-" + bitPos + "." + (i + 1)))
					isoBuffer.put("S-" + bitPos + "." + (i + 1), "*");
			}

			if (bitPos.equals("60"))
				return subField60Formatting(isoBuffer);

			for (int i = 0; i < Fieldlength; i++) {

				bitDetails = isoBuffer.get("S-" + bitPos + "." + (i + 1));

				if (!bitDetails.equals("*")) {
					// if (!(bitPos + "." + (i + 1)).equals("126.10")) {
					// Log.trace("S-" + bitPos + "." + (i + 1) + "\t\t\t::\t"
					// + bitDetails);
					// }
					bitMap.append("1");

					data = bitDetails;

					// prakash

					// if (bitPos.equals("61")) {
					// messageSize = CupsMessageMap.CUPSFIELDMAP61[i]
					// .getFieldlength();
					// fieldMssgDataType = CupsMessageMap.CUPSFIELDMAP61[i]
					// .getBitdatatype();
					// Fieldlength = 26;
					// bitmapLength = 8;
					// bitmapchar = 64;
					// }

					// prakash

					// if (bitPos.equals("62")) {
					// messageSize = CupsMessageMap.CUPSFIELDMAP62[i]
					// .getFieldlength();
					// fieldMssgDataType = CupsMessageMap.CUPSFIELDMAP62[i]
					// .getBitdatatype();
					// Fieldlength = 26;
					// bitmapLength = 8;
					// bitmapchar = 64;
					// }

					// prakash

					// if (bitPos.equals("63")) {
					// messageSize = CupsMessageMap.CUPSFIELDMAP63[i]
					// .getFieldlength();
					// fieldMssgDataType = CupsMessageMap.CUPSFIELDMAP63[i]
					// .getBitdatatype();
					// Fieldlength = 21;
					// bitmapLength = 3;
					// bitmapchar = 24;
					// }

					// prakash

					// else if (bitPos.equals("44")) {
					// messageSize = CupsMessageMap.CUPSFIELDMAP44[i]
					// .getFieldlength();
					// fieldMssgDataType = CupsMessageMap.CUPSFIELDMAP44[i]
					// .getBitdatatype();
					// Fieldlength = 15;
					// }

					// prakash
					// else if (bitPos.equals("126")) {
					// messageSize = CupsMessageMap.CUPSFIELDMAP126[i]
					// .getFieldlength();
					// fieldMssgDataType = CupsMessageMap.CUPSFIELDMAP126[i]
					// .getBitdatatype();
					// fieldLengthDataType = CupsMessageMap.CUPSFIELDMAP126[i]
					// .getFieldlengthtype();
					// Fieldlength = 19;
					// bitmapLength = 8;
					// bitmapchar = 64;
					// }
					// prakash

					if (messageSize < 0) {
						fieldMessage.append(calculateLength(data.length(),
								messageSize, fieldLengthDataType));
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

						if (messageSize > 0)
							subFieldLength = subFieldLength + messageSize / 2;
						else if (messageSize < 0) {
							subFieldLength = subFieldLength + value.length()
									/ 2;
						}

						fieldMessage.append(value);

						break;

					case 'E':

						value = (JcbISOUtil.alpha2Hex((new String(new String(
								data.getBytes(), "ISO8859_1")
								.getBytes("cp1047")))));
						fieldMessage.append(value);

						if (messageSize > 0)
							subFieldLength = subFieldLength + messageSize;
						else if (messageSize < 0) {
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
			JcbISOUtil.padRightZero(bitMap, bitmapchar);

			return String.valueOf(JcbISOUtil.binary2hex(JcbISOUtil
					.hex2binary(Integer.toHexString(subFieldLength
							+ bitmapLength))))
					+ (bitmapLength != 0 ? (JcbISOUtil.binary2hex(bitMap
							.toString())) : "") + fieldMessage.toString();

		} catch (Exception e) {
			throw new RuntimeException("Error occured Sub Element formatting");
		} finally {
			bitMap = null;
			fieldMessage = null;
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
				if (bitDetails.equals("*"))
					tempData.append(" ");
				else
					tempData.append(bitDetails);
				bitDetails = isoBuffer.get("S-60." + (i + 2));
				if (bitDetails.equals("*"))
					tempData.append(" ");
				else {
					tempData.append(bitDetails);
				}
				i++;
				data.append(tempData.toString());
				tempData.delete(0, tempData.length());
			} else {
				bitDetails = isoBuffer.get("S-60." + (i + 1));
				if (bitDetails.equals("*"))
					tempData.append("  ");
				else
					data.append(bitDetails);
			}
		}
		String finalData = data.toString();
		for (int j = data.length(); j >= 0; j = j - 2) {
			if (finalData.substring(j - 2, j).equals("  ")) {
				finalData = finalData.substring(0, j - 2)
						+ finalData.substring(j - 2, j).replace("  ", "");
			} else {
				j = 0;
				break;
			}

		}
		finalData = finalData.replaceAll(" ", "0");
		// Log.trace("S-60" + "\t\t\t::\t" + finalData);
		return JcbISOUtil
				.binary2hex(JcbISOUtil.hex2binary((finalData.length() / 2) + ""))
				+ finalData;

	}

	private static String calculateLength(int dataLength, int messageSize,
			char fieldLengthDataType) {

		String length = "";
		if (messageSize < 0) {

			switch (fieldLengthDataType) {

			case 'B':
				// length = Integer.toString(dataLength);

				// length =
				// JcbISOUtil.binary2asciiChar(Integer.toString(dataLength));
				length = JcbISOUtil.hex2AsciiChar(Integer.toString(dataLength));

				break;

			case 'H':

				length = JcbISOUtil.binary2hex(JcbISOUtil.hex2binary(Integer
						.toHexString(dataLength)));

				length = JcbISOUtil.hex2AsciiChar(length);

				break;

			case 'T':
				if (dataLength % 2 != 0)
					dataLength = dataLength + 1;
				length = JcbISOUtil.binary2hex(JcbISOUtil.hex2binary(Integer
						.toHexString(dataLength / 2)));
				length = "00"+length;
				length = JcbISOUtil.hex2AsciiChar(length);

				break;

			}
		}

		return length;
	}
}
