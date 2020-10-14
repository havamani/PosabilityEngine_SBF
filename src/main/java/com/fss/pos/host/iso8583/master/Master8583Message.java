package com.fss.pos.host.iso8583.master;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;

import org.apache.commons.codec.binary.Hex;

import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.utils.IsoUtil;

public class Master8583Message extends MasterMessage {

	protected Master8583Message(String s) {
		priBitmap = new BitSet(64);
		secBitmap = new BitSet(64);
		msgHdr = s;
		if (msgHdr == null)
			s = "";
	}

	@Override
	public void setElement(int i, String s) {
		if (i <= 64)
			priBitmap.set(i - 1);
		else
			secBitmap.set(i - 1 - 64);
		super.setElement(i, s);
	}

	@Override
	public void setElement(int i, byte abyte0[]) {
		if (i <= 64)
			priBitmap.set(i - 1);
		else
			secBitmap.set(i - 1 - 64);
		super.setElement(i, abyte0);
	}

	public void setMsgType(String s) {
		msgType = s.getBytes();
	}

	public String getMsgType() {
		String s = new String(msgType);
		return s;
	}

	/***************************************************************************
	 * Purpose in brief : To format the provided data into Master format Written
	 * by : SubramaniMohanam Created on : 30th April 2015 Last Modified : 30th
	 * April 2015; Arguments passed : MasterField Modified By : SubramaniMohanam
	 **************************************************************************/
	protected byte[] pack(MasterField aauthfield[]) {
		/*
		 * ByteArrayOutputStream bytearrayoutputstream = new
		 * ByteArrayOutputStream( 512);
		 */
		StringBuffer headerbuffer = new StringBuffer();
		boolean flag = false;
		try {
			// Log.trace("Packing ISO Message:");// new
			headerbuffer.append(getMsgType());
			// Log.trace("    Message Type: " + getMsgType());// new
			if (secBitmap.length() > 0) {
				priBitmap.set(0);
				headerbuffer.append(MasterIsoUtil.bitSetToHexString(priBitmap));
				headerbuffer.append(MasterIsoUtil.bitSetToHexString(secBitmap));
				// Log.trace("    Primary Bit Map: "
				// + MasterIsoUtil.bitSetToHexString(priBitmap));// new
				// Log.trace("    Secondary Bit Map: "
				// + MasterIsoUtil.bitSetToHexString(secBitmap));// new
			} else {
				headerbuffer.append(MasterIsoUtil.bitSetToHexString(priBitmap));
				// byte abyte1[] =
				// super.formatElement(IsoUtil.bitSetToHexString(
				// priBitmap).getBytes(), aauthfield[0], 0);
				// bytearrayoutputstream.write(abyte1, 0, abyte1.length);
				// Log.trace("    Primary Bit Map: "
				// + MasterIsoUtil.bitSetToHexString(priBitmap));// new
			}
			int len = 0;
			byte[] finalarry = new byte[4096];
			for (int i = 1; i < 64; i++)
				if (priBitmap.get(i)) {
					byte abyte2[] = super.getBytesElement(i + 1);
					if (abyte2 != null && aauthfield[i + 1] != null) {
						byte abyte3[] = super.formatElement(abyte2,
								aauthfield[i + 1], (i + 1));
						if (abyte3 == null) {
							flag = true;
						} else {
							// if (i == 63 || i == 51) {
							// Log.trace("    Bit #" + (i + 1) + " "
							// + aauthfield[i + 1].getName() + ":: "
							// + new String(Hex.encodeHex(abyte3)));
							// } else if (i == 54) {
							// Log.trace("    Bit #"
							// + (i + 1)
							// + " "
							// + aauthfield[i + 1].getName()
							// + ":: "
							// + IsoUtil.asciiChar2hex(new String(
							// abyte3,
							// StandardCharsets.ISO_8859_1)));
							// } else {
							// Log.trace("    Bit #" + (i + 1) + " "
							// + aauthfield[i + 1].getName() + ":: "
							// + new String(abyte3, "Cp1047"));
							// }
							System.arraycopy(abyte3, 0, finalarry, len,
									abyte3.length);
							len = len + abyte3.length;
						}
					}
				}

			for (int j = 0; j < 64; j++)
				if (secBitmap.get(j)) {
					byte abyte4[] = super.getBytesElement(j + 65);
					if (abyte4 != null && aauthfield[j + 65] != null) {
						byte abyte5[] = super.formatElement(abyte4,
								aauthfield[j + 65], (j + 65));
						if (abyte5 == null) {
							flag = true;
						} else {
							// try {
							// Log.trace("    Bit #" + (j + 65) + " "
							// + aauthfield[j + 65].getName() + " "
							// + new String(abyte5, "Cp1047"));
							// } catch (UnsupportedEncodingException e) {
							// // TODO Auto-generated catch block
							// e.printStackTrace();
							// }// new
							System.arraycopy(abyte5, 0, finalarry, len,
									abyte5.length);
							len = len + abyte5.length;
						}
					}
				}

			if (flag)
				return null;
			else
				return finalrequestbuffer(finalarry, headerbuffer, len);
		} catch (Exception e) {
			Log.error("Error Occured while pack::", e);
		} finally {
			headerbuffer = null;
			// bytearrayoutputstream=null;
		}
		return null;
	}

	protected byte[] finalrequestbuffer(byte[] bytearrayoutputstream,
			StringBuffer headerbuffer, int len) {

		byte[] finalarray = null;
		byte[] messagetype = null;
		byte[] primseconbitmap = null;
		byte[] fieldatt = null;
		byte[] arrayfinal = null;
		try {
			finalarray = new byte[1024];

			int lengthfield = 0;

			messagetype = headerbuffer.substring(0, 4).getBytes("Cp1047");

			System.arraycopy(messagetype, 0, finalarray, 0, messagetype.length);

			primseconbitmap = Hex.decodeHex(headerbuffer.substring(4,
					headerbuffer.length()).toCharArray());

			System.arraycopy(primseconbitmap, 0, finalarray,
					messagetype.length, primseconbitmap.length);

			fieldatt = new byte[len];

			System.arraycopy(bytearrayoutputstream, 0, fieldatt, 0,
					fieldatt.length);

			System.arraycopy(fieldatt, 0, finalarray,
					(primseconbitmap.length + messagetype.length),
					fieldatt.length);

			lengthfield = messagetype.length + primseconbitmap.length
					+ fieldatt.length;
			arrayfinal = new byte[lengthfield];

			System.arraycopy(finalarray, 0, arrayfinal, 0, lengthfield);
			return arrayfinal;
		} catch (Exception e) {
			Log.error("Error Occured while pack::", e);
		} finally {
			finalarray = null;
			messagetype = null;
			primseconbitmap = null;
			fieldatt = null;
		}

		return null;
	}

	/***************************************************************************
	 * Purpose in brief : To unpack the Master format and fill the buffer
	 * Written by : Maathavan Created on : 30th April 2015 Last Modified : 30th
	 * April 2015; Arguments passed : MasterField Modified By : Maathavan
	 * 
	 * @throws Exception
	 **************************************************************************/
	protected boolean unpack(byte abyte0[], MasterField aauthfield[]) {
		try {
			String s = new String(abyte0, "ISO8859_1");
			boolean flag = true;
			boolean flag1 = false;
			int i = 0;
			// Log.trace("Unpacking ISO Message:");
			i = msgHdr.length();
			byte byte0 = 4;
			msgType = new String(s.substring(i, i + byte0).getBytes(), "Cp1047")
					.getBytes();
			Log.trace("    Message Type: " + getMsgType());
			i += byte0;
			byte0 = 8;
			String s1 = s.substring(i, i + byte0);
			priBitmap = MasterIsoUtil.hexStringToBitSet(
					new String(Hex.encodeHex(s1.getBytes("ISO8859_1")))
							.getBytes(), 0, false);
			// Log.trace("    Primary Bit Map: "
			// + new String(Hex.encodeHex(s1.getBytes("ISO8859_1"))));
			// Log.trace("    Primary Bit Map: " + s1);
			i += byte0;
			if (priBitmap.get(0)) {
				String s2 = s.substring(i, i + byte0);
				secBitmap = MasterIsoUtil.hexStringToBitSet(
						new String(Hex.encodeHex(s2.getBytes("ISO8859_1")))
								.getBytes(), 0, false);
				flag1 = true;
				i += byte0;
			}
			for (int k = 2; k <= (flag1 ? 128 : 64); k++) {
				boolean flag3;
				if (flag)
					flag3 = priBitmap.get(k - 1);
				else
					flag3 = secBitmap.get(k - 1 - 64);
				if (!flag3) {
					if (k == 64 && flag1)
						flag = false;
				} else {
					int l = aauthfield[k].getMinLength();
					int i1 = aauthfield[k].getLengthHeader();
					boolean flag4 = false;
					if (i1 > 0)
						flag4 = true;
					String s3;

					if (flag4) {
						if (k == 55) {
							String s4 = new String(s.substring(i, i += i1)
									.getBytes(), "Cp1047");
							int j = Integer.valueOf(s4).intValue();
							String tagLen = s.substring(i, i += 2);
							s3 = new String(s.substring(i, i += (j - 2))
									.getBytes(), StandardCharsets.ISO_8859_1);
							s3 = tagLen + s3;
							s3 = IsoUtil.asciiChar2hex(s3);
						} else {
							String s4 = new String(s.substring(i, i += i1)
									.getBytes(), "Cp1047");
							int j = Integer.valueOf(s4).intValue();
							s3 = new String(s.substring(i, i += j).getBytes(),
									"Cp1047");
						}
					} else {
						s3 = new String(s.substring(i, i += l).getBytes(),
								"Cp1047");
					}

					// Log.trace("    Bit #" + k + " " + aauthfield[k].getName()
					// + " " + s3);// new
					setElement(k, s3);
					if (k == 64 && flag1)
						flag = false;
				}
			}
		} catch (Exception e) {
			Log.error("Error Occured while unpack::", e);
		}

		return true;
	}

	private BitSet priBitmap;
	private BitSet secBitmap;
	private byte msgType[];
	private String msgHdr;
}
