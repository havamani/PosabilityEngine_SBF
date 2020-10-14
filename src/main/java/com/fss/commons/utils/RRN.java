package com.fss.commons.utils;

import java.util.Random;

public class RRN {

	public static String genRRN(int len) {
		len = len / 2;
		Random random = new Random();
		char[] digits = new char[len];
		digits[0] = (char) (random.nextInt(9) + '1');
		for (int i = 1; i < len; i++)
			digits[i] = (char) (random.nextInt(10) + '0');
		StringBuilder sb = new StringBuilder(new String(digits));
		char[] digits1 = new char[len];
		digits1[0] = (char) (random.nextInt(9) + '1');
		for (int i = 1; i < len; i++)
			digits1[i] = (char) (random.nextInt(10) + '0');
		sb.append(new String(digits1));
		return sb.toString();
	}

}
