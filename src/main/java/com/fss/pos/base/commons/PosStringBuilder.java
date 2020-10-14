package com.fss.pos.base.commons;

public class PosStringBuilder {

	private StringBuilder sb = new StringBuilder();
	private static final String OPTION_BIT_DISABLE = "0";
	private static final String OPTION_BIT_ENABLE = "1";

	public StringBuilder appendOptionBitNot(String value) throws PosException {
		if (null == value || value.isEmpty()) {
			sb.append(OPTION_BIT_DISABLE);
			return sb;
		} else {
			if (OPTION_BIT_DISABLE.equals(value))
				sb.append(OPTION_BIT_ENABLE);
			else if (OPTION_BIT_ENABLE.equals(value))
				sb.append(OPTION_BIT_DISABLE);
			return sb;
		}
	}

	public StringBuilder appendOptionBit(String str) {
		if (null == str || str.isEmpty())
			Log.error("appendOptionBit", new Exception());
		sb.append(getDefaultIfEmpty(str, OPTION_BIT_DISABLE));
		return sb;
	}

	public StringBuilder append(String str, String defaultValue) {
		if (null == str || str.isEmpty())
			Log.error("appendOptionBit", new Exception());
		sb.append(getDefaultIfEmpty(str, defaultValue));
		return sb;
	}

	public StringBuilder append(String str, String defaultValue, int bit) {
		if (null == str || str.isEmpty())
			Log.error("appendOptionBit", new Exception());
		str = getDefaultIfEmpty(str, defaultValue);
		int len = str.length();
		String temp = str;
		if (len < bit) {
			for (int i = len; i < bit; i++)
				temp = "0" + temp;
		}
		sb.append(temp);
		return sb;
	}

	public StringBuilder append(String str) {
		sb.append(null == str ? "" : str);
		return sb;
	}

	private String getDefaultIfEmpty(String value, String defaultValue) {
		return (null == value || value.isEmpty()) ? defaultValue : value;
	}

	public void clear() {
		sb.delete(0, sb.length());
	}

	@Override
	public String toString() {
		return sb.toString();
	}

}
