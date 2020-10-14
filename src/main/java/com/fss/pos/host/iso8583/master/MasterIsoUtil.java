package com.fss.pos.host.iso8583.master;

import java.util.Arrays;
import java.util.BitSet;

public class MasterIsoUtil {

	public static String zeroFill(String s, int i) {
		StringBuffer stringbuffer = new StringBuffer(i);
		int j = i - s.length();
		for (int k = 0; k < j; k++)
			stringbuffer.append("0");

		stringbuffer.append(s);
		return stringbuffer.toString();
	}

	public static String padLeft(String s, int i, char c) {
		s = s.trim();
		if (s.length() >= i)
			return s;
		StringBuffer stringbuffer = new StringBuffer(i);
		for (int j = i - s.length(); j-- > 0;)
			stringbuffer.append(c);

		stringbuffer.append(s);
		return stringbuffer.toString();
	}

	public static String padRight(String s, int i) {
		if (s.length() >= i)
			return s;
		StringBuffer stringbuffer;
		for (stringbuffer = new StringBuffer(s); stringbuffer.length() < i; stringbuffer
				.append(' '))
			;
		return stringbuffer.toString();
	}

	public static String formatHexDump(byte abyte0[]) {
		StringBuffer stringbuffer = new StringBuffer(abyte0.length * 2
				+ abyte0.length / 2);
		int i = (abyte0.length + 15) / 16;
		char ac[] = new char[68];
		for (int j = 0; j < i; j++) {
			Arrays.fill(ac, ' ');
			ac[66] = '\r';
			ac[67] = '\n';
			ac[9] = ':';
			int k = Math.min(16, abyte0.length - j * 16);
			int l = j * 16;
			int i1 = j * 16;
			int j1 = 1;
			for (int k1 = 0; k1 < 8; k1++) {
				ac[j1++] = Character.toUpperCase(Character.forDigit(i1 >>> 28,
						16));
				i1 <<= 4;
			}

			j1 += 2;
			for (int l1 = 0; l1 < k; l1++) {
				ac[j1++] = Character.toUpperCase(Character.forDigit(abyte0[l
						+ l1] >>> 4 & 0xf, 16));
				ac[j1++] = Character.toUpperCase(Character.forDigit(abyte0[l
						+ l1] & 0xf, 16));
				if (l1 % 4 == 3)
					j1++;
				char c = (char) (abyte0[l + l1] & 0xff);
				ac[l1 + 48] = Character.isISOControl(c) ? '.' : c;
			}

			stringbuffer.append(ac);
		}

		return stringbuffer.toString();
	}

	public static BitSet hexStringToBitSet(byte abyte0[], int i, boolean flag) {
		char c = flag ? (Character.digit((char) abyte0[i], 16) & 8) != 8 ? '@'
				: '\200' : '@';
		BitSet bitset = new BitSet(c);
		for (int j = 0; j < c; j++) {
			int k = Character.digit((char) abyte0[i + (j >>> 2)], 16);
			if ((k & 8 >>> j % 4) > 0)
				bitset.set(j);
		}

		return bitset;
	}

	public static String bitSetToHexString(BitSet bitset) {
		return hexString(bitSetToByte(bitset));
	}

	public static byte[] bitSetToByte(BitSet bitset) {
		int i = (bitset.size() >>> 3) << 3;
		i = i <= 128 ? i : 128;
		byte abyte0[] = new byte[i >>> 3];
		for (int j = 0; j < i; j++)
			if (bitset.get(j))
				abyte0[j >>> 3] |= 128 >>> j % 8;

		if (i > 64)
			abyte0[0] |= 0x80;
		return abyte0;
	}

	public static String hexString(byte abyte0[]) {
		StringBuffer stringbuffer = new StringBuffer(abyte0.length * 2);
		for (int i = 0; i < abyte0.length; i++) {
			char c = Character.forDigit(abyte0[i] >>> 4 & 0xf, 16);
			char c1 = Character.forDigit(abyte0[i] & 0xf, 16);
			stringbuffer.append(Character.toUpperCase(c));
			stringbuffer.append(Character.toUpperCase(c1));
		}

		return stringbuffer.toString();
	}
}
