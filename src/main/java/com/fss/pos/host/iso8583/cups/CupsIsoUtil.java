package com.fss.pos.host.iso8583.cups;

public class CupsIsoUtil {
	
	
	public static String appendChar(String data, char ch, int totalLen,
			boolean appendLeft) {
		if (data == null)
			data = "";
		StringBuilder sb = new StringBuilder();
		if (!appendLeft)
			sb.append(data);
		for (int i = 1; i <= (totalLen - data.length()); i++)
			sb.append(ch);
		if (appendLeft)
			sb.append(data);
		return sb.toString();
	}

}
