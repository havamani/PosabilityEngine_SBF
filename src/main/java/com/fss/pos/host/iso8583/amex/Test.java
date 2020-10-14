package com.fss.pos.host.iso8583.amex;

public class Test {

	/*
	 * public static void main(String[] args) throws Exception {
	 * 
	 * 
	 * String entryMode = "05"; String entryMode1 = ""; boolean b = false; if
	 * (("02".equals(entryMode))) { entryMode = "2"; } else if
	 * ("01".equals(entryMode)) { entryMode = "6"; } else if
	 * ("80".equals(entryMode)) { entryMode = "5"; entryMode1 = "8"; b = true; }
	 * else if("05".equals(entryMode)){ entryMode = "8"; entryMode1 ="5"; b = true;
	 * } else if("07".equals(entryMode)||"91".equals(entryMode)){ entryMode = "8";
	 * entryMode1 ="4"; b = true; } if (!b) { entryMode1 = entryMode; }
	 * 
	 * String track2 = "3569990012300058d21121010000000000000"; if
	 * ("F".equals(track2.substring(track2.length() - 1, track2.length()))) { track2
	 * = track2.substring(0, track2.length() - 1); } boolean s =
	 * track2.contains("D"); boolean f = track2.contains("d"); String[] arr = null;
	 * if(s){ arr = track2.split("D"); } if(f){ arr = track2.split("d"); }
	 * 
	 * if ("F".equals(track2.substring(track2.length() - 1, track2.length()))) {
	 * track2 = track2.substring(0, track2.length() - 1); }
	 * 
	 * 
	 * //isoBuffer.put(Constants.DE2, arr[0]); //isoBuffer.put(Constants.DE14,
	 * arr[1].substring(0, 4));
	 * 
	 * 
	 * List<String> rowList = new ArrayList<String>(); File file = new
	 * File("D:\\Oman-Prod-Mask.xls"); HSSFWorkbook workbook = new HSSFWorkbook(new
	 * FileInputStream(file)); HSSFSheet sheet = workbook.getSheetAt(0); int rows =
	 * sheet.getPhysicalNumberOfRows(); //8996 HSSFRow row = null; for (int j = 1; j
	 * < rows; j++) { row = sheet.getRow(j); int physicalRow =
	 * row.getPhysicalNumberOfCells(); for (int i = 0; i < physicalRow; ) { HSSFCell
	 * cell = row.getCell(i); rowList.add(cell.toString());
	 * 
	 * i=i+2; }
	 * 
	 * }
	 * 
	 * IsoBuffer buffer = new IsoBuffer(); IsoBuffer subBuffer = new IsoBuffer();
	 * subBuffer.fillBuffer(true, false, false); buffer.putBuffer(Constants.DE62,
	 * subBuffer); String p62 = "{P-1=N, P-2=0468271597984119, P-3=NP  }";
	 * Map<String, String> de62 = PosUtil.parseBuffer(p62); IsoBuffer buffer62 =
	 * buffer.getBuffer(Constants.DE62); for (String k : de62.keySet()) { if
	 * (k.equals(Constants.DE2)) { buffer62.put(k, Util.appendChar(de62.get(k), '0',
	 * 16, true)); } buffer62.put(k, de62.get(k));
	 * 
	 * }
	 * 
	 * 
	 * 
	 * }
	 */
}
