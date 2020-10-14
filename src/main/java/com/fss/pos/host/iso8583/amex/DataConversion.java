package com.fss.pos.host.iso8583.amex;

public class DataConversion {
	
	private static final char[] ASCII_translate_EBCDIC = {};

	 public static String convertASCIIToEBCDICString(String input)
	  {
	    StringBuffer output = new StringBuffer("");
	    for (int i = 0; i < input.length(); i++)
	    {
	      char temp = input.charAt(i);
	      int x = ASCII_translate_EBCDIC[temp];
	      output.append(Integer.toHexString(x).toUpperCase());
	    }
	    return output.toString();
	  }
	 
	 public static String convertNumericsToBCD(String getValue)
	  {
	    String[] BCD = { "0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001" };
	    
	    StringBuffer sb = new StringBuffer();
	    for (int iCount = 0; iCount < getValue.length(); iCount++)
	    {
	      char temp = getValue.charAt(iCount);
	      int temp1 = Character.getNumericValue(temp);
	      sb.append(BCD[temp1]);
	    }
	    return sb.toString();
	  }
	  
}
