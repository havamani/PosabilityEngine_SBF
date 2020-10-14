package com.fss.pos.host.iso8583.amex;

import java.util.Map;


public class AmexMessageMap extends AmexMessage8583 {
	public AmexMessageMap() {
	}

	public byte[] pack() 
	{
		return super.pack(VISAFIELDMAP);
	}

	public Map<String, String> unpack(String message)
	{
		return super.unpack(message, VISAFIELDMAP);
	}

	/**
	 * For all fields, the format can be: AN (alphanumeric, EBCDIC) ANS
	 * (alphanumeric/special characters, EBCDIC) B (binary value) BCD (numeric,
	 * 4-bit BCD = unsigned packed) Bit string N (numeric, 1 byte per character)
	 * indicated as 'T'
	 * 
	 * 
	 * Fieldlength indicates length of the data if length is fixed non negative
	 * value provided,zero indicates bit is not in use Fieldlength data type
	 * indicates 'B' - binary,'H'- Ascii , 'T' - Integer Bitdatatype indicates
	 * 'B' -binary , 'E' - EBCDIC Subelement indicates 'N' - No subelements ,
	 * 'S' - Yes sublements available
	 */

    private static final AmexField VISAFIELDMAP[] = {
		
    	new AmexField("F-1",8,'I','I','N'),
    	new AmexField("F-2",-2,'E','E','N'),
    	new AmexField("F-3",6,'B','E','N'), //
    	new AmexField("F-4",12,'B','E','N'),
    	new AmexField("F-5",12,'B','E','N'),
    	new AmexField("F-6",12,'B','E','N'),
    	new AmexField("F-7",10,'B','E','N'),
    	new AmexField("F-8",8,'B','E','N'),
    	new AmexField("F-9",8,'B','E','N'),
    	new AmexField("F-10",8,'B','E','N'),
    	new AmexField("F-11",6,'B','E','N'),//
    	new AmexField("F-12",12,'B','E','N'),//
    	new AmexField("F-13",4,'B','E','N'),
    	new AmexField("F-14",4,'B','E','N'),
    	new AmexField("F-15",4,'B','E','N'),
    	new AmexField("F-16",4,'B','E','N'),
    	new AmexField("F-17",4,'T','E','N'),
    	new AmexField("F-18",4,'B','E','N'),
    	new AmexField("F-19",3,'B','E','N'),
    	new AmexField("F-20",3,'B','E','N'),
    	new AmexField("F-21",0,'B','E','N'),
    	new AmexField("F-22",12,'B','E','N'), // ans
    	new AmexField("F-23",3,'B','E','N'),
    	new AmexField("F-24",3,'B','E','N'),//ans
    	new AmexField("F-25",2,'B','E','N'),
    	new AmexField("F-26",4,'B','E','N'),
    	new AmexField("F-27",0,'B','B','N'),
    	new AmexField("F-28",9,'B','E','N'),
    	new AmexField("F-29",0,'B','E','N'),
    	new AmexField("F-30",0,'B','E','N'),
    	//new AmexField("F-31",-2,'B','E','N'),
    	new AmexField("F-31",-2,'E','E','N'),
    	new AmexField("F-32",-2,'E','E','N'),
    	new AmexField("F-33",-2,'B','E','N'),//
    	new AmexField("F-34",-2,'B','B','N'),
    	new AmexField("F-35",-2,'E','E','N'),
    	new AmexField("F-36",-2,'B','B','N'),
    	new AmexField("F-37",12,'H','E','N'),//an
    	new AmexField("F-38",6,'H','E','N'),
    	new AmexField("F-39",3,'H','E','N'),
    	new AmexField("F-40",0,'H','E','N'),
    	new AmexField("F-41",8,'H','E','N'),//ans
    	new AmexField("F-42",15,'H','E','N'),//an
    	new AmexField("F-43",-2,'E','E','N'),
    	new AmexField("F-44",-2,'H','E','S'),
    	new AmexField("F-45",-2,'H','E','N'),//ans
    	new AmexField("F-46",-2,'H','E','N'),
    	new AmexField("F-47",-2,'H','E','N'),
    	new AmexField("F-48",-3,'H','E','N'),
    	new AmexField("F-49",3,'H','E','N'),
    	//new VisaField("F-49",3,'B','E','N'),
    	new AmexField("F-50",3,'B','E','N'),
    	new AmexField("F-51",3,'B','B','N'),
    	new AmexField("F-52",16,'H','E','N'),
    	new AmexField("F-53",8,'B','B','N'),
    	new AmexField("F-54",-2,'H','E','N'),
    	new AmexField("F-55",-3,'E','H','N'),
    	new AmexField("F-56",-2,'E','E','N'),
    	new AmexField("F-57",-2,'H','E','N'),
    	new AmexField("F-58",-2,'H','E','N'),
    	new AmexField("F-59",-2,'H','E','N'),
    	new AmexField("F-60",-3,'H','E','S'),//an 210+316
    	new AmexField("F-61",-3,'B','E','N'),//check
    	new AmexField("F-62",-2,'B','E','S'),
    	new AmexField("F-63",0,'B','E','S'),
    	new AmexField("F-64",8,'B','B','N'),
    	new AmexField("F-65",0,'I','I','N'),
    	new AmexField("F-66",1,'T','B','N'),
    	new AmexField("F-67",0,'T','B','N'),
    	new AmexField("F-68",3,'T','B','N'),
    	new AmexField("F-69",0,'B','B','N'),
    	new AmexField("F-70",3,'T','B','N'),
    	new AmexField("F-71",0,'B','B','N'),
    	new AmexField("F-72",0,'B','B','N'),
    	new AmexField("F-73",6,'T','B','N'),
    	new AmexField("F-74",10,'T','B','N'),
    	new AmexField("F-75",10,'T','B','N'),
    	new AmexField("F-76",10,'T','B','N'),
    	new AmexField("F-77",10,'T','B','N'),
    	new AmexField("F-78",0,'T','B','N'),
    	new AmexField("F-79",0,'T','B','N'),
    	new AmexField("F-80",0,'B','B','N'),
    	new AmexField("F-81",0,'B','B','N'),
    	new AmexField("F-82",0,'B','B','N'),
    	new AmexField("F-83",0,'B','B','N'),
    	new AmexField("F-84",0,'B','B','N'),
    	new AmexField("F-85",0,'B','B','N'),
    	new AmexField("F-86",16,'T','B','N'),
    	new AmexField("F-87",16,'T','B','N'),
    	new AmexField("F-88",16,'T','B','N'),
    	new AmexField("F-89",16,'T','B','N'),
    	new AmexField("F-90",42,'T','B','N'),
    	new AmexField("F-91",1,'H','E','N'),
    	new AmexField("F-92",2,'H','E','N'),
    	new AmexField("F-93",0,'H','E','N'),
    	new AmexField("F-94",0,'B','E','N'),
    	new AmexField("F-95",42,'H','E','N'),
    	new AmexField("F-96",16,'B','B','N'),
    	new AmexField("F-97",17,'H','E','N'),
    	new AmexField("F-98",0,'H','E','N'),
    	new AmexField("F-99",-2,'B','B','N'),
    	new AmexField("F-100",-2,'B','B','N'),
    	new AmexField("F-101",-2,'H','E','N'),
    	new AmexField("F-102",-2,'H','E','N'),
    	new AmexField("F-103",-2,'H','E','N'),
    	new AmexField("F-104",-2,'H','B','N'),
    	new AmexField("F-105",32,'B','B','N'),
    	new AmexField("F-106",0,'B','B','N'),
    	new AmexField("F-107",0,'B','B','N'),
    	new AmexField("F-108",0,'B','B','N'),
    	new AmexField("F-109",0,'B','B','N'),
    	new AmexField("F-110",0,'B','B','N'),
    	new AmexField("F-111",0,'B','B','N'),
    	new AmexField("F-112",0,'B','B','N'),
    	new AmexField("F-113",0,'B','B','N'),
    	new AmexField("F-114",0,'B','B','N'),
    	new AmexField("F-115",-2,'H','E','N'),
    	new AmexField("F-116",-2,'H','B','N'),
    	new AmexField("F-117",-2,'H','E','N'),
    	new AmexField("F-118",-2,'H','E','N'),
    	new AmexField("F-119",-2,'H','E','N'),
    	new AmexField("F-120",0,'T','B','N'),
    	new AmexField("F-121",0,'H','E','N'),
    	new AmexField("F-122",0,'H','E','N'),
    	new AmexField("F-123",-2,'H','E','N'),
    	new AmexField("F-124",0,'H','E','N'),
    	new AmexField("F-125",-2,'H','E','N'),
    	new AmexField("F-126",-2,'T','B','S'),
    	new AmexField("F-127",-2,'B','B','N'),
    	new AmexField("F-128",0,'H','B','N'),
    	
    };
    

	public static final AmexField VISAFIELDMAP44[] = {

	new AmexField("S-44.1", 1, 'N', 'E', 'N'),
			new AmexField("S-44.2", 1, 'N', 'E', 'N'),
			new AmexField("S-44.3", 0, 'N', 'N', 'N'),
			new AmexField("S-44.4", 0, 'N', 'N', 'N'),
			new AmexField("S-44.5", 1, 'N', 'E', 'N'),
			new AmexField("S-44.6", 2, 'N', 'E', 'N'),
			new AmexField("S-44.7", 1, 'N', 'E', 'N'),
			new AmexField("S-44.8", 1, 'N', 'E', 'N'),
			new AmexField("S-44.9", 0, 'N', 'N', 'N'),
			new AmexField("S-44.10", 1, 'N', 'E', 'N'),
			new AmexField("S-44.11", 2, 'N', 'E', 'N'),
			new AmexField("S-44.12", 1, 'N', 'E', 'N'),
			new AmexField("S-44.13", 1, 'N', 'E', 'N'),
			new AmexField("S-44.14", 4, 'N', 'E', 'N')

	};
	
	
	
	public static final AmexField VISAFIELDMAP55[] = { //added for amex - start of 55 - pending need to set values

	new AmexField("S-55.1", 1, 'N', 'E', 'N'),
			new AmexField("S-55.2", 1, 'N', 'E', 'N'),
			new AmexField("S-55.3", 0, 'N', 'N', 'N'),
			new AmexField("S-55.4", 0, 'N', 'N', 'N'),
			new AmexField("S-55.5", 1, 'N', 'E', 'N'),
			new AmexField("S-55.6", 2, 'N', 'E', 'N'),
			new AmexField("S-55.7", 1, 'N', 'E', 'N'),
			new AmexField("S-55.8", 1, 'N', 'E', 'N'),
			new AmexField("S-55.9", 0, 'N', 'N', 'N'),
			new AmexField("S-55.10", 1, 'N', 'E', 'N'),
			new AmexField("S-55.11", 2, 'N', 'E', 'N'),
			new AmexField("S-55.12", 1, 'N', 'E', 'N'),
			new AmexField("S-55.13", 1, 'N', 'E', 'N'),
			new AmexField("S-55.14", 4, 'N', 'E', 'N'),
			new AmexField("S-55.15", 4, 'N', 'E', 'N'),
			new AmexField("S-55.16", 4, 'N', 'E', 'N'),

	}; //end of sub 55
	
	
	
	public static final AmexField VISAFIELDMAP61[] = {  //added for amex - start of 61 - pending need to set values


		new AmexField("S-61.1", 1, 'N', 'T', 'N'),
				new AmexField("S-61.2", 1, 'N', 'T', 'N'),
				new AmexField("S-61.3", 1, 'N', 'T', 'N'),
				new AmexField("S-61.4", 1, 'N', 'T', 'N'),
				new AmexField("S-61.5", 2, 'N', 'T', 'N'),
				new AmexField("S-61.6", 1, 'N', 'T', 'N'),
				new AmexField("S-61.7", 1, 'N', 'T', 'N'),
				new AmexField("S-61.8", 2, 'N', 'T', 'N'),
				new AmexField("S-61.9", 1, 'N', 'T', 'N'),
				new AmexField("S-61.10", 1, 'N', 'T', 'N') };
	
	 //end of sub 55
	
	

	public static final AmexField VISAFIELDMAP60[] = {

	new AmexField("S-60.1", 1, 'N', 'T', 'N'),
			new AmexField("S-60.2", 1, 'N', 'T', 'N'),
			new AmexField("S-60.3", 1, 'N', 'T', 'N'),
			new AmexField("S-60.4", 1, 'N', 'T', 'N'),
			new AmexField("S-60.5", 2, 'N', 'T', 'N'),
			new AmexField("S-60.6", 1, 'N', 'T', 'N'),
			new AmexField("S-60.7", 1, 'N', 'T', 'N'),
			new AmexField("S-60.8", 2, 'N', 'T', 'N'),
			new AmexField("S-60.9", 1, 'N', 'T', 'N'),
			new AmexField("S-60.10", 1, 'N', 'T', 'N') };

	public static final AmexField VISAFIELDMAP62[] = {

	new AmexField("S-62.1", 1, 'N', 'E', 'N'),
			new AmexField("S-62.2", 15, 'N', 'T', 'N'),
			new AmexField("S-62.3", 4, 'N', 'E', 'N'),
			new AmexField("S-62.4", 1, 'N', 'E', 'N'),
			new AmexField("S-62.5", 2, 'N', 'T', 'N'),
			new AmexField("S-62.6", 1, 'N', 'E', 'N'),
			new AmexField("S-62.7", 26, 'N', 'E', 'N'),
			new AmexField("S-62.8", 6, 'N', 'T', 'N'),
			new AmexField("S-62.9", 1, 'N', 'E', 'N'),
			new AmexField("S-62.10", 6, 'N', 'T', 'N'),
			new AmexField("S-62.11", 2, 'N', 'T', 'N'),
			new AmexField("S-62.12", 2, 'N', 'T', 'N'),
			new AmexField("S-62.13", 1, 'N', 'E', 'N'),
			new AmexField("S-62.14", 12, 'N', 'T', 'N'),
			new AmexField("S-62.15", 1, 'N', 'E', 'N'),
			new AmexField("S-62.16", 2, 'N', 'E', 'N'),
			new AmexField("S-62.17", 15, 'N', 'E', 'N'),
			new AmexField("S-62.18", 1, 'N', 'E', 'N'),
			new AmexField("S-62.19", 2, 'N', 'E', 'N'),
			new AmexField("S-62.20", 10, 'N', 'T', 'N'),
			new AmexField("S-62.21", 4, 'N', 'E', 'N'),
			new AmexField("S-62.22", 6, 'N', 'E', 'N'),
			new AmexField("S-62.23", 2, 'N', 'E', 'N'),
			new AmexField("S-62.24", 6, 'N', 'E', 'N'),
			new AmexField("S-62.25", 1, 'N', 'E', 'N'),
			new AmexField("S-62.26", 1, 'N', 'E', 'N') };

	public static final AmexField VISAFIELDMAP63[] = {

	new AmexField("S-63.1", 4, 'N', 'B', 'N'),
			new AmexField("S-63.2", 4, 'N', 'B', 'N'),
			new AmexField("S-63.3", 4, 'N', 'B', 'N'),
			new AmexField("S-63.4", 4, 'N', 'B', 'N'),
			new AmexField("S-63.5", 0, 'N', 'N', 'N'),
			new AmexField("S-63.6", 7, 'N', 'E', 'N'),
			new AmexField("S-63.7", 0, 'N', 'N', 'N'),
			new AmexField("S-63.8", 8, 'N', 'B', 'N'),
			new AmexField("S-63.9", 14, 'N', 'E', 'N'),
			new AmexField("S-63.10", 13, 'N', 'E', 'N'),
			new AmexField("S-63.11", 1, 'N', 'E', 'N'),
			new AmexField("S-63.12", 30, 'N', 'E', 'N'),
			new AmexField("S-63.13", 6, 'N', 'B', 'N'),
			new AmexField("S-63.14", 36, 'N', 'E', 'N'),
			new AmexField("S-63.15", 9, 'N', 'E', 'N'),
			new AmexField("S-63.16", 0, 'N', 'N', 'N'),
			new AmexField("S-63.17", 0, 'N', 'N', 'N'),
			new AmexField("S-63.18", 2, 'N', 'B', 'N'),
			new AmexField("S-63.19", 3, 'N', 'E', 'N'),
			new AmexField("S-63.20", 0, 'N', 'N', 'N'),
			new AmexField("S-63.21", 1, 'N', 'E', 'N'),
			new AmexField("S-63.22", 0, 'N', 'N', 'N') };

	public static final AmexField VISAFIELDMAP126[] = {

	new AmexField("S-126.1", -2, 'H', 'E', 'N'),
			new AmexField("S-126.2", -2, 'H', 'E', 'N'),
			new AmexField("S-126.3", -2, 'H', 'E', 'N'),
			new AmexField("S-126.4", -2, 'H', 'E', 'N'),
			new AmexField("S-126.5", -2, 'B', 'B', 'N'),
			new AmexField("S-126.6", -2, 'B', 'B', 'N'),
			new AmexField("S-126.7", -2, 'B', 'B', 'N'),
			new AmexField("S-126.8", 40, 'B', 'B', 'N'),
			new AmexField("S-126.9", 40, 'B', 'B', 'N'),
			new AmexField("S-126.10", 6, 'H', 'E', 'N'),
			new AmexField("S-126.11", 0, 'N', 'N', 'N'),
			new AmexField("S-126.12", 6, 'B', 'B', 'N'),
			new AmexField("S-126.13", 1, 'H', 'E', 'N'),
			new AmexField("S-126.14", 0, 'H', 'E', 'N'),
			new AmexField("S-126.15", 1, 'H', 'E', 'N'),
			new AmexField("S-126.16", -2, 'H', 'E', 'N'),
			new AmexField("S-126.17", 0, 'N', 'N', 'N'),
			new AmexField("S-126.18", -2, 'H', 'E', 'N'),
			new AmexField("S-126.19", 1, 'H', 'E', 'N'),
			new AmexField("S-126.20", 0, 'N', 'N', 'N') };

}
