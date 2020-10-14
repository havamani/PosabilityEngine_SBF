package com.fss.pos.host.iso8583.jcb;


import java.util.Map;

public class JcbMessage extends JcbMessage8583 {

	public JcbMessage() {

	}

	public byte[] pack() {
		return super.pack(JCBFIELDMAP1);
	}

	public Map<String, String> unpack(String message) {
		return  super.unpack(message, JCBFIELDMAP);
	}

	/*
	 * Fieldlength indicates length of the data if length is fixed non negative
	 * value provided,zero indicates bit is not in use Fieldlengthtype data type
	 * indicates 'B' - binary,'H'- Anscii , 'I' - Integer Bitdatatype indicates
	 * 'B' -binary , 'E' - EBCDIC Subelement indicates 'N' - No subelements ,
	 * 'S' - Yes sublements available
	 */
	public static final JcbField JCBFIELDMAP[] = {

			new JcbField("F-1", 8, 'B', 'B', 'N'), // 0800
			new JcbField("F-2", -1, 'H', 'B', 'N'),
			new JcbField("F-3", 3, 'B', 'B', 'N'),
			new JcbField("F-4", 6, 'B', 'B', 'N'),
			new JcbField("F-5", 0, 'B', 'B', 'N'), // Not supported
			new JcbField("F-6", 6, 'B', 'B', 'N'),
			new JcbField("F-7", 5, 'B', 'B', 'N'), // 0800
			new JcbField("F-8", 0, 'B', 'B', 'N'), // Not supported
			new JcbField("F-9", 0, 'B', 'B', 'N'), // Not supported
			new JcbField("F-10", 4, 'B', 'B', 'N'),
			new JcbField("F-11", 3, 'B', 'B', 'N'), // 0800
			new JcbField("F-12", 3, 'B', 'B', 'N'),
			new JcbField("F-13", 2, 'B', 'B', 'N'),
			new JcbField("F-14", 2, 'B', 'B', 'N'),
			new JcbField("F-15", 0, 'B', 'B', 'N'), // Not supported
			new JcbField("F-16", 2, 'B', 'B', 'N'),
			new JcbField("F-17", 0, 'B', 'B', 'N'),// Not supported
			new JcbField("F-18", 2, 'B', 'B', 'N'),
			new JcbField("F-19", 0, 'B', 'B', 'N'),// Not supported
			new JcbField("F-20", 0, 'B', 'B', 'N'),// Not supported
			new JcbField("F-21", 0, 'B', 'B', 'N'), // Not supported
			new JcbField("F-22", 2, 'B', 'B', 'N'),
			new JcbField("F-23", 2, 'B', 'B', 'N'),
			new JcbField("F-24", 0, 'B', 'B', 'N'),// Not supported
			new JcbField("F-25", 1, 'B', 'B', 'N'),
			new JcbField("F-26", 1, 'B', 'B', 'N'),
			new JcbField("F-27", 0, 'B', 'B', 'N'),// Not supported
			new JcbField("F-28", 9, 'B', 'E', 'N'), // Doubt
			new JcbField("F-29", 0, 'B', 'E', 'N'), // Doubt-//Not supported
			new JcbField("F-30", 0, 'B', 'E', 'N'), // Doubt-//Not supported
			new JcbField("F-31", 0, 'B', 'E', 'N'), // Doubt-//Not supported
			new JcbField("F-32", -1, 'B', 'B', 'N'),
			new JcbField("F-33", -1, 'B', 'B', 'N'), // 0800
			new JcbField("F-34", 0, 'B', 'B', 'N'),// Not supported

			new JcbField("F-35", -1, 'H', 'B', 'N'),
			new JcbField("F-36", -2, 'B', 'B', 'N'),
			new JcbField("F-37", 12, 'B', 'E', 'N'), // Doubt
			new JcbField("F-38", 6, 'B', 'E', 'N'), // Doubt
			new JcbField("F-39", 2, 'B', 'E', 'N'), // Doubt
			new JcbField("F-40", 0, 'B', 'E', 'N'), // Doubt-//Not supported
			new JcbField("F-41", 8, 'B', 'E', 'N'), // Doubt
			new JcbField("F-42", 15, 'B', 'E', 'N'), // Doubt
			new JcbField("F-43", 40, 'B', 'E', 'N'),
			new JcbField("F-44", -1, 'B', 'B', 'N'), // Doubt ----Subfields
			new JcbField("F-45", -2, 'B', 'E', 'N'), // Doubt
			new JcbField("F-46", 0, 'B', 'E', 'N'),// Doubt-//Not supported
			new JcbField("F-47", 0, 'B', 'E', 'N'),// Doubt-//Not supported
			new JcbField("F-48", -2, 'B', 'E', 'N'),// Doubt
			new JcbField("F-49", 3, 'B', 'E', 'N'),
			new JcbField("F-50", 0, 'B', 'E', 'N'),// Doubt-//Not supported
			new JcbField("F-51", 3, 'B', 'E', 'N'),// Doubt
			new JcbField("F-52", 8, 'B', 'B', 'N'),// Doubt
			new JcbField("F-53", 16, 'B', 'B', 'N'), // 0800
			new JcbField("F-54", -1, 'T', 'E', 'N'),// Doubt
			new JcbField("F-55", -2, 'T', 'B', 'N'),// Doubt
			new JcbField("F-56", 0, 'B', 'B', 'N'),// Not Supported
			new JcbField("F-57", 0, 'B', 'E', 'N'),// Not Supported
			new JcbField("F-58", 0, 'B', 'E', 'N'),// Not Supported
			new JcbField("F-59", 0, 'B', 'E', 'N'),// Doubt-//Not Supported
			new JcbField("F-60", -2, 'B', 'B', 'N'),// Doubt
			new JcbField("F-61", -1, 'B', 'E', 'N'),
			new JcbField("F-62", 0, 'B', 'B', 'N'),// Doubt--Not Supported
			new JcbField("F-63", -2, 'B', 'B', 'N'),
			new JcbField("F-64", 0, 'B', 'B', 'N'),// Doubt--Not Supported
			new JcbField("F-65", 0, 'B', 'I', 'N'),// Doubt--Not Supported
			new JcbField("F-66", 0, 'B', 'B', 'N'),// Doubt--Not Supported
			new JcbField("F-67", 0, 'B', 'B', 'N'),// Doubt--Not Supported
			new JcbField("F-68", 0, 'B', 'B', 'N'),// Doubt--Not Supported
			new JcbField("F-69", 0, 'B', 'B', 'N'),// Not Supported
			new JcbField("F-70", 2, 'B', 'B', 'N'), // 0800
			new JcbField("F-71", 0, 'B', 'B', 'N'),// Not Supported
			new JcbField("F-72", 0, 'B', 'B', 'N'),// Not Supported
			new JcbField("F-73", 0, 'B', 'B', 'N'),// Not Supported
			new JcbField("F-74", 0, 'B', 'B', 'N'),// Not Supported
			new JcbField("F-75", 0, 'B', 'B', 'N'),// Not Supported
			new JcbField("F-76", 0, 'B', 'B', 'N'),// Not Supported
			new JcbField("F-77", 0, 'B', 'B', 'N'),// Not Supported
			new JcbField("F-78", 0, 'B', 'B', 'N'),// Not Supported
			new JcbField("F-79", 0, 'B', 'B', 'N'),// Not Supported
			new JcbField("F-80", 0, 'B', 'B', 'N'),// Not Supported
			new JcbField("F-81", 0, 'B', 'B', 'N'),// Not Supported
			new JcbField("F-82", 0, 'B', 'B', 'N'),// Not Supported
			new JcbField("F-83", 0, 'B', 'B', 'N'),// Not Supported
			new JcbField("F-84", 0, 'B', 'B', 'N'),// Not Supported
			new JcbField("F-85", 0, 'B', 'B', 'N'),// Not Supported
			new JcbField("F-86", 0, 'B', 'B', 'N'),// Not Supported
			new JcbField("F-87", 0, 'B', 'B', 'N'),// Not Supported
			new JcbField("F-88", 0, 'B', 'B', 'N'),// Not Supported
			new JcbField("F-89", 0, 'B', 'B', 'N'),// Not Supported
			new JcbField("F-90", 21, 'B', 'B', 'N'),
			new JcbField("F-91", 0, 'B', 'E', 'N'), // Not Supported
			new JcbField("F-92", 0, 'B', 'E', 'N'), // Doubt--Not Supported
			new JcbField("F-93", 0, 'B', 'E', 'N'),// Not Supported
			new JcbField("F-94", 0, 'B', 'E', 'N'), // Not Supported
			new JcbField("F-95", 42, 'B', 'E', 'N'),
			new JcbField("F-96", 64, 'B', 'B', 'N'), // Doubt //0800
			new JcbField("F-97", 0, 'B', 'E', 'N'), // Doubt--Not Supported
			new JcbField("F-98", 0, 'B', 'E', 'N'), // Not Supported
			new JcbField("F-99", 0, 'B', 'B', 'N'), // Not Supported
			new JcbField("F-100", -1, 'B', 'B', 'N'), // 0800
			new JcbField("F-101", -2, 'B', 'E', 'N'),
			new JcbField("F-102", 0, 'B', 'E', 'N'), // Not Supported
			new JcbField("F-103", 0, 'B', 'E', 'N'), // Not Supported

			new JcbField("F-104", 0, 'B', 'B', 'N'), // Not Supported
			new JcbField("F-105", 16, 'B', 'B', 'N'), // Doubt //0800
			new JcbField("F-106", 0, 'B', 'B', 'N'), // Not Supported
			new JcbField("F-107", 0, 'B', 'B', 'N'), // Not Supported
			new JcbField("F-108", 0, 'B', 'B', 'N'), // Not Supported
			new JcbField("F-109", 0, 'B', 'B', 'N'), // Not Supported
			new JcbField("F-110", 0, 'B', 'B', 'N'), // Not Supported
			new JcbField("F-111", 0, 'B', 'B', 'N'), // Not Supported
			new JcbField("F-112", 0, 'B', 'B', 'N'), // Not Supported
			new JcbField("F-113", 0, 'B', 'B', 'N'), // Not Supported
			new JcbField("F-114", 0, 'B', 'B', 'N'), // Not Supported
			new JcbField("F-115", 0, 'B', 'E', 'N'), // Not Supported
			new JcbField("F-116", 0, 'B', 'B', 'N'), // Not Supported
			new JcbField("F-117", 0, 'B', 'E', 'N'), // Not Supported
			new JcbField("F-118", 0, 'B', 'E', 'N'), // Not Supported
			new JcbField("F-119", 0, 'B', 'E', 'N'), // Not Supported

			new JcbField("F-120", -3, 'B', 'E', 'N'), // Doubt
			new JcbField("F-121", 0, 'B', 'E', 'N'), // Not Supported
			new JcbField("F-122", 0, 'B', 'E', 'N'), // Not Supported
			new JcbField("F-123", 0, 'B', 'E', 'N'), // Not Supported
			new JcbField("F-124", 0, 'B', 'E', 'N'), // Not Supported
			new JcbField("F-125", 0, 'B', 'E', 'N'), // Not Supported
			new JcbField("F-126", 0, 'B', 'B', 'N'), // Not Supported
			new JcbField("F-127", -3, 'B', 'B', 'N'),
			new JcbField("F-128", 0, 'B', 'B', 'N') // Not Supported

	};
	
	
	
	public static final JcbField JCBFIELDMAP1[] = {

		new JcbField("F-1", 8, 'B', 'B', 'N'), // 0800
		new JcbField("F-2", -2, 'H', 'B', 'N'),
		new JcbField("F-3", 3, 'B', 'B', 'N'),
		new JcbField("F-4", 6, 'B', 'B', 'N'),
		new JcbField("F-5", 0, 'B', 'B', 'N'), // Not supported
		new JcbField("F-6", 6, 'B', 'B', 'N'),
		new JcbField("F-7", 5, 'B', 'B', 'N'), // 0800
		new JcbField("F-8", 0, 'B', 'B', 'N'), // Not supported
		new JcbField("F-9", 0, 'B', 'B', 'N'), // Not supported
		new JcbField("F-10", 4, 'B', 'B', 'N'),
		new JcbField("F-11", 3, 'B', 'B', 'N'), // 0800
		new JcbField("F-12", 3, 'B', 'B', 'N'),
		new JcbField("F-13", 2, 'B', 'B', 'N'),
		new JcbField("F-14", 2, 'B', 'B', 'N'),
		new JcbField("F-15", 0, 'B', 'B', 'N'), // Not supported
		new JcbField("F-16", 2, 'B', 'B', 'N'),
		new JcbField("F-17", 0, 'B', 'B', 'N'),// Not supported
		new JcbField("F-18", 2, 'B', 'B', 'N'),
		new JcbField("F-19", 0, 'B', 'B', 'N'),// Not supported
		new JcbField("F-20", 0, 'B', 'B', 'N'),// Not supported
		new JcbField("F-21", 0, 'B', 'B', 'N'), // Not supported
		new JcbField("F-22", 2, 'B', 'B', 'N'),
		new JcbField("F-23", 2, 'B', 'B', 'N'),
		new JcbField("F-24", 0, 'B', 'B', 'N'),// Not supported
		new JcbField("F-25", 1, 'B', 'B', 'N'),
		new JcbField("F-26", 1, 'B', 'B', 'N'),
		new JcbField("F-27", 0, 'B', 'B', 'N'),// Not supported
		new JcbField("F-28", 9, 'B', 'E', 'N'), // Doubt
		new JcbField("F-29", 0, 'B', 'E', 'N'), // Doubt-//Not supported
		new JcbField("F-30", 0, 'B', 'E', 'N'), // Doubt-//Not supported
		new JcbField("F-31", 0, 'B', 'E', 'N'), // Doubt-//Not supported
		new JcbField("F-32", -2, 'B', 'B', 'N'),
		new JcbField("F-33", -2, 'B', 'B', 'N'), // 0800
		new JcbField("F-34", 0, 'B', 'B', 'N'),// Not supported

		new JcbField("F-35", -2, 'H', 'B', 'N'),
		new JcbField("F-36", -2, 'B', 'B', 'N'),
		new JcbField("F-37", 12, 'B', 'E', 'N'), // Doubt
		new JcbField("F-38", 6, 'B', 'E', 'N'), // Doubt
		new JcbField("F-39", 2, 'B', 'E', 'N'), // Doubt
		new JcbField("F-40", 0, 'B', 'E', 'N'), // Doubt-//Not supported
		new JcbField("F-41", 8, 'B', 'E', 'N'), // Doubt
		new JcbField("F-42", 15, 'B', 'E', 'N'), // Doubt
		new JcbField("F-43", 40, 'B', 'E', 'N'),
		new JcbField("F-44", -2, 'B', 'E', 'N'), // Doubt ----Subfields
		new JcbField("F-45", -2, 'B', 'E', 'N'), // Doubt
		new JcbField("F-46", 0, 'B', 'E', 'N'),// Doubt-//Not supported
		new JcbField("F-47", 0, 'B', 'E', 'N'),// Doubt-//Not supported
		new JcbField("F-48", -2, 'H', 'E', 'N'),// Doubt
		new JcbField("F-49", 3, 'B', 'E', 'N'),
		new JcbField("F-50", 0, 'B', 'E', 'N'),// Doubt-//Not supported
		new JcbField("F-51", 3, 'B', 'E', 'N'),// Doubt
		new JcbField("F-52", 8, 'B', 'B', 'N'),// Doubt
		new JcbField("F-53", 8, 'B', 'B', 'N'), // 0800
		new JcbField("F-54", -2, 'T', 'E', 'N'),// Doubt

		new JcbField("F-55", -2, 'T', 'B', 'N'),// Doubt
		new JcbField("F-56", 0, 'B', 'B', 'N'),// Not Supported
		new JcbField("F-57", 0, 'B', 'E', 'N'),// Not Supported
		new JcbField("F-58", 0, 'B', 'E', 'N'),// Not Supported
		new JcbField("F-59", 0, 'B', 'E', 'N'),// Doubt-//Not Supported
		new JcbField("F-60", -2, 'B', 'B', 'N'),// Doubt
		new JcbField("F-61", -2, 'B', 'E', 'N'),
		new JcbField("F-62", 0, 'B', 'B', 'N'),// Doubt--Not Supported
		new JcbField("F-63", -2, 'B', 'B', 'N'),
		new JcbField("F-64", 0, 'B', 'B', 'N'),// Doubt--Not Supported
		new JcbField("F-65", 0, 'B', 'I', 'N'),// Doubt--Not Supported
		new JcbField("F-66", 0, 'B', 'B', 'N'),// Doubt--Not Supported
		new JcbField("F-67", 0, 'B', 'B', 'N'),// Doubt--Not Supported
		new JcbField("F-68", 0, 'B', 'B', 'N'),// Doubt--Not Supported
		new JcbField("F-69", 0, 'B', 'B', 'N'),// Not Supported
		new JcbField("F-70", 2, 'B', 'B', 'N'), // 0800
		new JcbField("F-71", 0, 'B', 'B', 'N'),// Not Supported
		new JcbField("F-72", 0, 'B', 'B', 'N'),// Not Supported
		new JcbField("F-73", 0, 'B', 'B', 'N'),// Not Supported
		new JcbField("F-74", 0, 'B', 'B', 'N'),// Not Supported
		new JcbField("F-75", 0, 'B', 'B', 'N'),// Not Supported
		new JcbField("F-76", 0, 'B', 'B', 'N'),// Not Supported
		new JcbField("F-77", 0, 'B', 'B', 'N'),// Not Supported
		new JcbField("F-78", 0, 'B', 'B', 'N'),// Not Supported
		new JcbField("F-79", 0, 'B', 'B', 'N'),// Not Supported
		new JcbField("F-80", 0, 'B', 'B', 'N'),// Not Supported
		new JcbField("F-81", 0, 'B', 'B', 'N'),// Not Supported
		new JcbField("F-82", 0, 'B', 'B', 'N'),// Not Supported
		new JcbField("F-83", 0, 'B', 'B', 'N'),// Not Supported
		new JcbField("F-84", 0, 'B', 'B', 'N'),// Not Supported
		new JcbField("F-85", 0, 'B', 'B', 'N'),// Not Supported
		new JcbField("F-86", 0, 'B', 'B', 'N'),// Not Supported
		new JcbField("F-87", 0, 'B', 'B', 'N'),// Not Supported
		new JcbField("F-88", 0, 'B', 'B', 'N'),// Not Supported
		new JcbField("F-89", 0, 'B', 'B', 'N'),// Not Supported
		new JcbField("F-90", 42, 'B', 'B', 'N'),
		new JcbField("F-91", 0, 'B', 'E', 'N'), // Not Supported
		new JcbField("F-92", 0, 'B', 'E', 'N'), // Doubt--Not Supported
		new JcbField("F-93", 0, 'B', 'E', 'N'),// Not Supported
		new JcbField("F-94", 0, 'B', 'E', 'N'), // Not Supported
		new JcbField("F-95", 42, 'B', 'E', 'N'),
		new JcbField("F-96", 64, 'B', 'B', 'N'), // Doubt //0800
		new JcbField("F-97", 0, 'B', 'E', 'N'), // Doubt--Not Supported
		new JcbField("F-98", 0, 'B', 'E', 'N'), // Not Supported
		new JcbField("F-99", 0, 'B', 'B', 'N'), // Not Supported
		new JcbField("F-100", -2, 'B', 'B', 'N'), // 0800
		new JcbField("F-101", -2, 'B', 'E', 'N'),
		new JcbField("F-102", 0, 'B', 'E', 'N'), // Not Supported
		new JcbField("F-103", 0, 'B', 'E', 'N'), // Not Supported

		new JcbField("F-104", 0, 'B', 'B', 'N'), // Not Supported
		new JcbField("F-105", 16, 'B', 'B', 'N'), // Doubt //0800
		new JcbField("F-106", 0, 'B', 'B', 'N'), // Not Supported
		new JcbField("F-107", 0, 'B', 'B', 'N'), // Not Supported
		new JcbField("F-108", 0, 'B', 'B', 'N'), // Not Supported
		new JcbField("F-109", 0, 'B', 'B', 'N'), // Not Supported
		new JcbField("F-110", 0, 'B', 'B', 'N'), // Not Supported
		new JcbField("F-111", 0, 'B', 'B', 'N'), // Not Supported
		new JcbField("F-112", 0, 'B', 'B', 'N'), // Not Supported
		new JcbField("F-113", 0, 'B', 'B', 'N'), // Not Supported
		new JcbField("F-114", 0, 'B', 'B', 'N'), // Not Supported
		new JcbField("F-115", 0, 'B', 'E', 'N'), // Not Supported
		new JcbField("F-116", 0, 'B', 'B', 'N'), // Not Supported
		new JcbField("F-117", 0, 'B', 'E', 'N'), // Not Supported
		new JcbField("F-118", 0, 'B', 'E', 'N'), // Not Supported
		new JcbField("F-119", 0, 'B', 'E', 'N'), // Not Supported

		new JcbField("F-120", -3, 'B', 'E', 'N'), // Doubt
		new JcbField("F-121", 0, 'B', 'E', 'N'), // Not Supported
		new JcbField("F-122", 0, 'B', 'E', 'N'), // Not Supported
		new JcbField("F-123", 0, 'B', 'E', 'N'), // Not Supported
		new JcbField("F-124", 0, 'B', 'E', 'N'), // Not Supported
		new JcbField("F-125", 0, 'B', 'E', 'N'), // Not Supported
		new JcbField("F-126", 0, 'B', 'B', 'N'), // Not Supported
		new JcbField("F-127", -3, 'B', 'B', 'N'),
		new JcbField("F-128", 0, 'B', 'B', 'N') // Not Supported

};

}
