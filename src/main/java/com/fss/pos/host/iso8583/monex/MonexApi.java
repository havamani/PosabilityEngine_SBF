package com.fss.pos.host.iso8583.monex;

import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.host.AbstractHostApi;

public class MonexApi extends AbstractHostApi{
	
	public static final String STAR = "*";
	
	 final static int[] bitmap87 ={ 8, -2, 6, 12, 12, 12, 10, 8,// 8
			8, 8, 6, 6, 4, 4, 4, 4,// 16
			4, 4, 3, 3, 3, 3, 3, 3, // 24
			2, 2, 1, 9, 9, 9, 9, -2, // 32
			-2, -2, -2, -3, 12, 6, 2, 3,// 40
			8, 15, 3, -2, -2, -3, -3, -3,// 48
			3, 3, 3, 16, 16, -3, -3, -3,// 56
			-3, 0, -3, -3, -3, -3, -3, 16,// 64
			8, 1, 2, 3, 3, 3, 4, 4,// 72
			6, 10, 10, 10, 10, 10, 10, 10,// 80
			10, 12, 12, 12, 12, 16, 16, 16,// 88
			16, 42, 0, 2, 5, 7, 42, 16,// 96
			17, 25, -2, -2, -2, -2, -2, -3,// 104
			-3, -3, -3, -3, -3, -3, -3, -3, // 112
			-3, -3, -3, -3, -3, -3, -3, -3, -3,// 120
			-3, -3, -3, -3, -3, -6, 16 };

	@Override
	public IsoBuffer parse(String message) throws PosException {
		int offset = 0;
		int size = 0;
		
		IsoBuffer buffer = new IsoBuffer();
		String sb = message.substring(2);
		StringBuilder respMsg = new StringBuilder();
		respMsg.append(sb);
		buffer.put(Constants.ISO_MSG_TYPE, respMsg.substring(offset, 4)); 
		offset += 4;
		
		String	pBitmap =  parseBitmap(IsoUtil.asciiChar2hex(respMsg.substring(offset, offset + 8)));
		
		offset += 8;

		
		for (int i = 0; i < 64; i++) {

				if ('1' == pBitmap.charAt(i)) {
				if (bitmap87[i] < 0) {
					size = Integer.parseInt(respMsg.substring(offset,
							offset + -1 * bitmap87[i]));
					offset += -1 * bitmap87[i];
					buffer.put("P-" + (i + 1), respMsg.substring(offset,
							offset + size));
					offset += size;
				} else {

					buffer.put("P-" + (i + 1), respMsg.substring(offset,
							offset + bitmap87[i]));

					offset += bitmap87[i];
				}
			} else {
				buffer.put("P-" + (i + 1), STAR);
			}

		}

			if ('1' == pBitmap.charAt(0)) {
				
			String	sBitmap = parseBitmap(IsoUtil.binary2hex(IsoUtil
						.asciiChar2binary(buffer
		 		 				 .get(Constants.DE1).toString())));
				
				for (int i = 64; i < 128; i++) {

					if ('1' == sBitmap.charAt(i - 64)) {

						if (bitmap87[i] < 0) {
							size = Integer.parseInt(respMsg.substring(offset,
									offset + -1 * bitmap87[i]));
							offset += -1 * bitmap87[i];
							buffer.put("S-" + (i + 1), respMsg.substring(
									offset, offset + size));
							offset += size;

						} else {
							buffer.put("S-" + (i + 1), respMsg.substring(
									offset, offset + bitmap87[i]));
							offset += bitmap87[i];
							
						}

					} else {
						buffer.put("S-" + (i + 1), STAR);
					}
				}

			} else {

				for (int i = 64; i < 128; i++) {

					buffer.put("S-" + (i + 1),STAR);

				}

			}
		return buffer;
	}
	
	public String parseBitmap(String iobsgBitmap) {
		StringBuilder losgUpperBitmap = new StringBuilder();
		losgUpperBitmap.append("00000000000000000000000000000000");
		StringBuilder losgLowerBitmap = new StringBuilder();
		losgLowerBitmap.append("00000000000000000000000000000000");
		losgUpperBitmap.append(Long.toBinaryString(Long.parseLong(
				iobsgBitmap.substring(0, 8), 16)));
		losgLowerBitmap.append(Long.toBinaryString(Long.parseLong(
				iobsgBitmap.substring(8), 16)));
		StringBuilder parseBitmapResult = new StringBuilder();
		parseBitmapResult.append(losgUpperBitmap.toString().substring(
				losgUpperBitmap.toString().length() - 32));
		parseBitmapResult.append(losgLowerBitmap.toString().substring(
				losgLowerBitmap.toString().length() - 32));
		return parseBitmapResult.toString();
	}
	
	
	
	@Override
	protected String build(IsoBuffer isoBuffer) {
		try {
			if(isoBuffer.get(Constants.ISO_MSG_TYPE).equals("0120")){
				fillIsoBuffer(isoBuffer);
		
			}
			String buildMsg = buildIso87(isoBuffer.get(Constants.ISO_MSG_TYPE), isoBuffer);
			StringBuilder buffer = new StringBuilder(getLength(buildMsg));	
			buffer.append(buildMsg);
			return buffer.toString();
		} catch (Exception e) {
			Log.error("Error building ISO request ", e);
			return null;
		}
	}

	
	private void fillIsoBuffer(IsoBuffer isoBuffer) throws Exception {
		try {
			//isoBuffer.put("TERTIARY-BITMAP", "*");
			isoBuffer.put("SECONDARY-BITMAP", "*");

			for (int i = 1; i <= 128; i++) {
				if (i <= 63) {
					if(isoBuffer.get(IsoBuffer.PREFIX_PRIMARY+i).isEmpty()){
						isoBuffer.put(IsoBuffer.PREFIX_PRIMARY+i, STAR);
					}
				}else{
					isoBuffer.put(IsoBuffer.PREFIX_SECONDARY+i, STAR);
				}
			}
			
		} catch (Exception e) {
			throw new Exception("Error Occured in fillISOBuffer");
		}
		
	}

	private String getLength(String data) {
		return IsoUtil.pad(
				String.valueOf(IsoUtil.toGraphical(data.length(), 2)), ' ', 2,
				true);
	}
	
	public String buildIso87(String iobstMsgType, IsoBuffer pthtISOBuffer) {
		
		
		int lonuSize = 0;
		StringBuilder losgMessage = new StringBuilder();
		StringBuilder losgPrimaryBitMap = new StringBuilder();
		StringBuilder losgSecondaryBitMap = new StringBuilder();
		for (int i = 0; i < 128; i++) {
			if (i <= 63) {
				if (!pthtISOBuffer.get(IsoBuffer.PREFIX_PRIMARY + (i + 1))
						.equals(STAR)) {
					if (bitmap87[i] < 0) {
						lonuSize = pthtISOBuffer.get(
								IsoBuffer.PREFIX_PRIMARY + (i + 1)).length();
						losgMessage.append(IsoUtil.leftPadZeros(
								String.valueOf(lonuSize), -1 * bitmap87[i]));
						losgMessage.append(pthtISOBuffer
								.get(IsoBuffer.PREFIX_PRIMARY + (i + 1)));
					} else {
						losgMessage.append(pthtISOBuffer
								.get(IsoBuffer.PREFIX_PRIMARY + (i + 1)));
					}
					losgPrimaryBitMap.append("1");
				} else {
					losgPrimaryBitMap.append("0");
				}

			} else {
				if (!pthtISOBuffer.get(IsoBuffer.PREFIX_SECONDARY + (i + 1))
						.equals(STAR)) {
					if (bitmap87[i] < 0) {
						lonuSize = pthtISOBuffer
								.get(IsoBuffer.PREFIX_SECONDARY + (i + 1))
								.toString().length();
						losgMessage.append(IsoUtil.leftPadZeros(
								String.valueOf(lonuSize), -1 * bitmap87[i]));
						losgMessage.append(pthtISOBuffer
								.get(IsoBuffer.PREFIX_SECONDARY + (i + 1)));
					} else {
						losgMessage.append(pthtISOBuffer
								.get(IsoBuffer.PREFIX_SECONDARY + (i + 1)));
					}
					losgSecondaryBitMap.append("1");
				} else {
					losgSecondaryBitMap.append("0");
				}
			}
		}
		StringBuilder message = new StringBuilder();
		if (losgSecondaryBitMap
				.toString()
				.equals("0000000000000000000000000000000000000000000000000000000000000000")) {
			message.append(iobstMsgType);
			message.append(buildBitmap(losgPrimaryBitMap.toString()));
			message.append(losgMessage);
		} else {
			message.append(iobstMsgType);
			message.append(buildBitmap(losgPrimaryBitMap.toString()));
			message.append(buildBitmap(losgSecondaryBitMap.toString()));
			message.append(losgMessage.substring(16));
		}
		
		
		return message.toString();
	}
	
	private String buildBitmap(String bitMapBinary) {
		return IsoUtil.binary2asciiChar(bitMapBinary);
	}
}
