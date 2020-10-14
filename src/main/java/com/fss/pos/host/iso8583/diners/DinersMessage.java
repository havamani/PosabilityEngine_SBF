package com.fss.pos.host.iso8583.diners;



public class DinersMessage extends DinersMessage8583 {
	public DinersMessage() {
	}

	public byte[] pack() {
		return super.pack(VISAFIELDMAP);
	}

	public boolean unpack(byte messageBytes[]) {
		return super.unpack(messageBytes, VISAFIELDMAP);
	}

	/*
	 * public static void main(String args[]) {
	 * 
	 * for (int i = 0; i <= 19; i++) {
	 * 
	 * // FIELD_LENGTH_INDICATOR44
	 * 
	 * // new VisaField("S-44.1",-2,'H','E','S');
	 * 
	 * } }
	 */

	public static final DinersField VISAFIELDMAP[] = {

			new DinersField("F-1", 8, 'I', 'I', 'N'),
			new DinersField("F-2", -2, 'B', 'B', 'N'),
			new DinersField("F-3", 6, 'B', 'B', 'N'), //
			new DinersField("F-4", 12, 'B', 'B', 'N'),
			new DinersField("F-5", 12, 'B', 'B', 'N'),
			new DinersField("F-6", 12, 'B', 'B', 'N'),
			new DinersField("F-7", 10, 'B', 'B', 'N'),
			new DinersField("F-8", 8, 'B', 'B', 'N'),
			new DinersField("F-9", 8, 'B', 'B', 'N'),
			new DinersField("F-10", 8, 'B', 'B', 'N'),
			new DinersField("F-11", 6, 'B', 'B', 'N'),//
			new DinersField("F-12", 12, 'B', 'B', 'N'),//
			new DinersField("F-13", 4, 'B', 'B', 'N'),
			new DinersField("F-14", 4, 'B', 'B', 'N'),
			new DinersField("F-15", 4, 'B', 'B', 'N'),
			new DinersField("F-16", 4, 'B', 'B', 'N'),
			new DinersField("F-17", 4, 'T', 'B', 'N'),
			new DinersField("F-18", 4, 'B', 'B', 'N'),
			new DinersField("F-19", 3, 'B', 'B', 'N'),
			new DinersField("F-20", 3, 'B', 'B', 'N'),
			new DinersField("F-21", 0, 'B', 'B', 'N'),
			new DinersField("F-22", 12, 'B', 'B', 'N'),
			new DinersField("F-23", 3, 'B', 'B', 'N'),
			new DinersField("F-24", 3, 'B', 'B', 'N'),//
			new DinersField("F-25", 2, 'B', 'B', 'N'),
			new DinersField("F-26", 4, 'B', 'B', 'N'),
			new DinersField("F-27", 0, 'B', 'B', 'N'),
			new DinersField("F-28", 9, 'B', 'E', 'N'),
			new DinersField("F-29", 0, 'B', 'E', 'N'),
			new DinersField("F-30", 0, 'B', 'E', 'N'),
			new DinersField("F-31", 0, 'B', 'E', 'N'),
			new DinersField("F-32", -2, 'B', 'B', 'N'),
			new DinersField("F-33", -2, 'B', 'B', 'N'),//
			new DinersField("F-34", -2, 'B', 'B', 'N'),
			new DinersField("F-35", -2, 'B', 'B', 'N'),
			new DinersField("F-36", -2, 'B', 'B', 'N'),
			new DinersField("F-37", 12, 'B', 'E', 'N'),
			new DinersField("F-38", 6, 'B', 'B', 'N'),
			new DinersField("F-39", 3, 'B', 'B', 'N'),
			new DinersField("F-40", 3, 'B', 'E', 'N'),
			new DinersField("F-41", 8, 'B', 'B', 'N'),
			new DinersField("F-42", 15, 'B', 'B', 'N'),
			new DinersField("F-43", -2, 'B', 'B', 'N'),
			new DinersField("F-44", -2, 'B', 'B', 'N'),
			new DinersField("F-45", -2, 'B', 'B', 'N'),
			new DinersField("F-46", -2, 'B', 'B', 'N'),
			new DinersField("F-47", -2, 'H', 'E', 'N'),
			new DinersField("F-48", -2, 'B', 'B', 'N'),
			new DinersField("F-49", 3, 'B', 'B', 'N'),
			new DinersField("F-50", 3, 'B', 'B', 'N'),
			new DinersField("F-51", 3, 'B', 'B', 'N'),
			new DinersField("F-52", 16, 'B', 'B', 'N'),
			new DinersField("F-53", 16, 'B', 'B', 'N'),
			new DinersField("F-54", -3, 'B', 'B', 'N'),
			new DinersField("F-55", -3, 'A', 'A', 'N'),
			new DinersField("F-56", -2, 'B', 'B', 'N'),
			new DinersField("F-57", -2, 'H', 'E', 'N'),
			new DinersField("F-58", -2, 'H', 'E', 'N'),
			new DinersField("F-59", -3, 'B', 'B', 'N'),
			new DinersField("F-60", -2, 'T', 'B', 'S'),
			new DinersField("F-61", -2, 'T', 'B', 'N'),
			new DinersField("F-62", -3, 'B', 'B', 'S'),
			new DinersField("F-63", -2, 'T', 'B', 'S'),
			new DinersField("F-64", 8, 'B', 'B', 'N'),
			new DinersField("F-65", 0, 'I', 'I', 'N'),
			new DinersField("F-66", 1, 'T', 'B', 'N'),
			new DinersField("F-67", 0, 'T', 'B', 'N'),
			new DinersField("F-68", 3, 'T', 'B', 'N'),
			new DinersField("F-69", 0, 'B', 'B', 'N'),
			new DinersField("F-70", 3, 'T', 'B', 'N'),
			new DinersField("F-71", 0, 'B', 'B', 'N'),
			new DinersField("F-72", -3, 'B', 'B', 'N'),
			new DinersField("F-73", 6, 'T', 'B', 'N'),
			new DinersField("F-74", 10, 'T', 'B', 'N'),
			new DinersField("F-75", 10, 'T', 'B', 'N'),
			new DinersField("F-76", 10, 'T', 'B', 'N'),
			new DinersField("F-77", 10, 'T', 'B', 'N'),
			new DinersField("F-78", 0, 'T', 'B', 'N'),
			new DinersField("F-79", 0, 'T', 'B', 'N'),
			new DinersField("F-80", 0, 'B', 'B', 'N'),
			new DinersField("F-81", 0, 'B', 'B', 'N'),
			new DinersField("F-82", 0, 'B', 'B', 'N'),
			new DinersField("F-83", 0, 'B', 'B', 'N'),
			new DinersField("F-84", 0, 'B', 'B', 'N'),
			new DinersField("F-85", 0, 'B', 'B', 'N'),
			new DinersField("F-86", 16, 'T', 'B', 'N'),
			new DinersField("F-87", 16, 'T', 'B', 'N'),
			new DinersField("F-88", 16, 'T', 'B', 'N'),
			new DinersField("F-89", 16, 'T', 'B', 'N'),
			new DinersField("F-90", 42, 'T', 'B', 'N'),
			new DinersField("F-91", 1, 'H', 'E', 'N'),
			new DinersField("F-92", 3, 'B', 'B', 'N'),
			new DinersField("F-93", -2, 'B', 'B', 'N'),
			new DinersField("F-94", -2, 'B', 'B', 'N'),
			new DinersField("F-95", 42, 'H', 'E', 'N'),
			new DinersField("F-96", 16, 'B', 'B', 'N'),
			new DinersField("F-97", 17, 'H', 'E', 'N'),
			new DinersField("F-98", 0, 'H', 'E', 'N'),
			new DinersField("F-99", -2, 'B', 'B', 'N'),
			new DinersField("F-100", -2, 'B', 'B', 'N'),
			new DinersField("F-101", -2, 'H', 'E', 'N'),
			new DinersField("F-102", -2, 'H', 'E', 'N'),
			new DinersField("F-103", -2, 'H', 'E', 'N'),
			new DinersField("F-104", -2, 'H', 'B', 'N'),
			new DinersField("F-105", 32, 'B', 'B', 'N'),
			new DinersField("F-106", -3, 'B', 'B', 'N'),
			new DinersField("F-107", 0, 'B', 'B', 'N'),
			new DinersField("F-108", 0, 'B', 'B', 'N'),
			new DinersField("F-109", 0, 'B', 'B', 'N'),
			new DinersField("F-110", 0, 'B', 'B', 'N'),
			new DinersField("F-111", 0, 'B', 'B', 'N'),
			new DinersField("F-112", 0, 'B', 'B', 'N'),
			new DinersField("F-113", 0, 'B', 'B', 'N'),
			new DinersField("F-114", 0, 'B', 'B', 'N'),
			new DinersField("F-115", -2, 'H', 'E', 'N'),
			new DinersField("F-116", -2, 'H', 'B', 'N'),
			new DinersField("F-117", -2, 'H', 'E', 'N'),
			new DinersField("F-118", -2, 'H', 'E', 'N'),
			new DinersField("F-119", -2, 'H', 'E', 'N'),
			new DinersField("F-120", 0, 'T', 'B', 'N'),
			new DinersField("F-121", 0, 'H', 'E', 'N'),
			new DinersField("F-122", -3, 'B', 'B', 'N'),
			new DinersField("F-123", -3, 'B', 'B', 'N'),
			new DinersField("F-124", -3, 'B', 'B', 'N'),
			new DinersField("F-125", -3, 'B', 'B', 'N'),
			new DinersField("F-126", -2, 'T', 'B', 'S'),
			new DinersField("F-127", -2, 'B', 'B', 'N'),
			new DinersField("F-128", 0, 'H', 'B', 'N'),
			new DinersField("F-129", 8, 'B', 'B', 'N'),
			new DinersField("F-130", 24, 'B', 'B', 'N'),
			new DinersField("F-131", 40, 'B', 'B', 'N'),
			new DinersField("F-132", 4, 'B', 'B', 'N'),
			new DinersField("F-133", 8, 'H', 'E', 'N'),
			new DinersField("F-134", -1, 'T', 'B', 'N'),
			new DinersField("F-135", -1, 'T', 'B', 'N'),
			new DinersField("F-136", 16, 'H', 'B', 'N'),
			new DinersField("F-137", 4, 'H', 'B', 'N'),
			new DinersField("F-138", 16, 'H', 'B', 'N'),
			new DinersField("F-139", 20, 'H', 'B', 'N'),
			new DinersField("F-140", -1, 'T', 'B', 'N'),
			new DinersField("F-141", -1, 'T', 'B', 'N'),
			new DinersField("F-142", -1, 'T', 'B', 'N'),
			new DinersField("F-143", -1, 'T', 'B', 'N'),
			new DinersField("F-144", 2, 'B', 'B', 'N'),
			new DinersField("F-145", 3, 'B', 'B', 'N'),
			new DinersField("F-146", 6, 'B', 'B', 'N'),
			new DinersField("F-147", 12, 'B', 'B', 'N'),
			new DinersField("F-148", 3, 'B', 'B', 'N'),
			new DinersField("F-149", 6, 'B', 'B', 'N'),
			new DinersField("F-150", 8, 'B', 'B', 'N'),
			new DinersField("F-151", 12, 'B', 'B', 'N'),
			new DinersField("F-152", 8, 'H', 'B', 'N'),
			new DinersField("F-153", 2, 'B', 'B', 'N'),
			new DinersField("F-154", 1, 'B', 'B', 'N'),
			new DinersField("F-155", 1, 'B', 'B', 'N'),
			new DinersField("F-156", 9, 'B', 'B', 'N'),
			new DinersField("F-157", 9, 'B', 'B', 'N'),
			new DinersField("F-158", 9, 'B', 'B', 'N'),
			new DinersField("F-159", 9, 'B', 'B', 'N'),
			new DinersField("F-160", -1, 'B', 'B', 'N'),
			new DinersField("F-161", -1, 'B', 'B', 'N'),
			new DinersField("F-162", -1, 'B', 'B', 'N'),
			new DinersField("F-163", -1, 'B', 'B', 'N'),
			new DinersField("F-164", -1, 'B', 'B', 'N'),
			new DinersField("F-165", -2, 'B', 'B', 'N'),
			new DinersField("F-166", 6, 'B', 'B', 'N'),
			new DinersField("F-167", 2, 'B', 'B', 'N'),
			new DinersField("F-168", -3, 'B', 'B', 'N'),
			new DinersField("F-169", 8, 'B', 'B', 'N'),
			new DinersField("F-170", 15, 'B', 'B', 'N'),
			new DinersField("F-171", 40, 'B', 'B', 'N'),
			new DinersField("F-172", 20, 'B', 'B', 'N'),
			new DinersField("F-173", -1, 'B', 'B', 'N'),
			new DinersField("F-174", -1, 'B', 'B', 'N'),
			new DinersField("F-175", -1, 'B', 'B', 'N'),
			new DinersField("F-176", -1, 'B', 'B', 'N'),
			new DinersField("F-177", 3, 'B', 'B', 'N'),
			new DinersField("F-178", 3, 'B', 'B', 'N'),
			new DinersField("F-179", 3, 'B', 'B', 'N'),
			new DinersField("F-180", 16, 'B', 'B', 'N'),
			new DinersField("F-181", 16, 'B', 'B', 'N'),
			new DinersField("F-182", -2, 'B', 'B', 'N'),
			new DinersField("F-183", -1, 'H', 'E', 'N'),
			new DinersField("F-184", -1, 'B', 'B', 'N'),
			new DinersField("F-185", -1, 'B', 'B', 'N'),
			new DinersField("F-186", -1, 'B', 'B', 'N'),
			new DinersField("F-187", -1, 'H', 'E', 'N'),
			new DinersField("F-188", -1, 'B', 'B', 'N'),
			new DinersField("F-189", -2, 'B', 'B', 'N'),
			new DinersField("F-190", 10, 'B', 'B', 'N'),
			new DinersField("F-191", -2, 'B', 'B', 'N'),
			new DinersField("F-192", 8, 'B', 'B', 'N')

	};

	/*
	 * For all fields, the format can be: AN (alphanumeric, EBCDIC) ANS
	 * (alphanumeric/special characters, EBCDIC) B (binary value) BCD (numeric,
	 * 4-bit BCD = unsigned packed) Bit string N (numeric, 1 byte per character)
	 * indicated as 'T'
	 * 
	 * 
	 * Fieldlength indicates length of the data if length is fixed non negative
	 * value provided,zero indicates bit is not in use Fieldlength data type
	 * indicates 'B' - binary,'H'- Anscii , 'T' - Integer Bitdatatype indicates
	 * 'B' -binary , 'E' - EBCDIC Subelement indicates 'N' - No subelements ,
	 * 'S' - Yes sublements available
	 */

	public static final DinersField VISAFIELDMAP44[] = {

	new DinersField("S-44.1", 1, 'N', 'E', 'N'),
			new DinersField("S-44.2", 1, 'N', 'E', 'N'),
			new DinersField("S-44.3", 0, 'N', 'N', 'N'),
			new DinersField("S-44.4", 0, 'N', 'N', 'N'),
			new DinersField("S-44.5", 1, 'N', 'E', 'N'),
			new DinersField("S-44.6", 2, 'N', 'E', 'N'),
			new DinersField("S-44.7", 1, 'N', 'E', 'N'),
			new DinersField("S-44.8", 1, 'N', 'E', 'N'),
			new DinersField("S-44.9", 0, 'N', 'N', 'N'),
			new DinersField("S-44.10", 1, 'N', 'E', 'N'),
			new DinersField("S-44.11", 2, 'N', 'E', 'N'),
			new DinersField("S-44.12", 1, 'N', 'E', 'N'),
			new DinersField("S-44.13", 1, 'N', 'E', 'N'),
			new DinersField("S-44.14", 4, 'N', 'E', 'N')

	};

	/*
	 * For all fields, the format can be: AN (alphanumeric, EBCDIC) ANS
	 * (alphanumeric/special characters, EBCDIC) B (binary value) BCD (numeric,
	 * 4-bit BCD = unsigned packed) Bit string N (numeric, 1 byte per character)
	 * indicated as 'T'
	 * 
	 * 
	 * Fieldlength indicates length of the data if length is fixed non negative
	 * value provided,zero indicates bit is not in use Fieldlength data type
	 * indicates 'B' - binary,'H'- Anscii , 'T' - Integer Bitdatatype indicates
	 * 'B' -binary , 'E' - EBCDIC Subelement indicates 'N' - No subelements ,
	 * 'S' - Yes sublements available
	 */

	public static final DinersField VISAFIELDMAP60[] = {

	new DinersField("S-60.1", 1, 'N', 'T', 'N'),
			new DinersField("S-60.2", 1, 'N', 'T', 'N'),
			new DinersField("S-60.3", 1, 'N', 'T', 'N'),
			new DinersField("S-60.4", 1, 'N', 'T', 'N'),
			new DinersField("S-60.5", 2, 'N', 'T', 'N'),
			new DinersField("S-60.6", 1, 'N', 'T', 'N'),
			new DinersField("S-60.7", 1, 'N', 'T', 'N'),
			new DinersField("S-60.8", 2, 'N', 'T', 'N'),
			new DinersField("S-60.9", 1, 'N', 'T', 'N'),
			new DinersField("S-60.10", 1, 'N', 'T', 'N') };

	/*
	 * For all fields, the format can be: AN (alphanumeric, EBCDIC) ANS
	 * (alphanumeric/special characters, EBCDIC) B (binary value) BCD (numeric,
	 * 4-bit BCD = unsigned packed) Bit string N (numeric, 1 byte per character)
	 * indicated as 'T'
	 * 
	 * 
	 * Fieldlength indicates length of the data if length is fixed non negative
	 * value provided,zero indicates bit is not in use Fieldlength data type
	 * indicates 'B' - binary,'H'- Anscii , 'T' - Integer Bitdatatype indicates
	 * 'B' -binary , 'E' - EBCDIC Subelement indicates 'N' - No subelements ,
	 * 'S' - Yes sublements available
	 */

	public static final DinersField VISAFIELDMAP62[] = {

	new DinersField("S-62.1", 1, 'N', 'E', 'N'),
			new DinersField("S-62.2", 15, 'N', 'T', 'N'),
			new DinersField("S-62.3", 4, 'N', 'E', 'N'),
			new DinersField("S-62.4", 1, 'N', 'E', 'N'),
			new DinersField("S-62.5", 2, 'N', 'T', 'N'),
			new DinersField("S-62.6", 1, 'N', 'E', 'N'),
			new DinersField("S-62.7", 26, 'N', 'E', 'N'),
			new DinersField("S-62.8", 6, 'N', 'T', 'N'),
			new DinersField("S-62.9", 1, 'N', 'E', 'N'),
			new DinersField("S-62.10", 6, 'N', 'T', 'N'),
			new DinersField("S-62.11", 2, 'N', 'T', 'N'),
			new DinersField("S-62.12", 2, 'N', 'T', 'N'),
			new DinersField("S-62.13", 1, 'N', 'E', 'N'),
			new DinersField("S-62.14", 12, 'N', 'T', 'N'),
			new DinersField("S-62.15", 1, 'N', 'E', 'N'),
			new DinersField("S-62.16", 2, 'N', 'E', 'N'),
			new DinersField("S-62.17", 15, 'N', 'E', 'N'),
			new DinersField("S-62.18", 1, 'N', 'E', 'N'),
			new DinersField("S-62.19", 2, 'N', 'E', 'N'),
			new DinersField("S-62.20", 10, 'N', 'T', 'N'),
			new DinersField("S-62.21", 4, 'N', 'E', 'N'),
			new DinersField("S-62.22", 6, 'N', 'E', 'N'),
			new DinersField("S-62.23", 2, 'N', 'E', 'N'),
			new DinersField("S-62.24", 6, 'N', 'E', 'N'),
			new DinersField("S-62.25", 1, 'N', 'E', 'N'),
			new DinersField("S-62.26", 1, 'N', 'E', 'N') };

	/*
	 * For all fields, the format can be: AN (alphanumeric, EBCDIC) ANS
	 * (alphanumeric/special characters, EBCDIC) B (binary value) BCD (numeric,
	 * 4-bit BCD = unsigned packed) Bit string N (numeric, 1 byte per character)
	 * indicated as 'T'
	 * 
	 * 
	 * Fieldlength indicates length of the data if length is fixed non negative
	 * value provided,zero indicates bit is not in use Fieldlength data type
	 * indicates 'B' - binary,'H'- Anscii , 'T' - Integer Bitdatatype indicates
	 * 'B' -binary , 'E' - EBCDIC Subelement indicates 'N' - No subelements ,
	 * 'S' - Yes sublements available
	 */

	public static final DinersField VISAFIELDMAP63[] = {

	new DinersField("S-63.1", 4, 'N', 'B', 'N'),
			new DinersField("S-63.2", 4, 'N', 'B', 'N'),
			new DinersField("S-63.3", 4, 'N', 'B', 'N'),
			new DinersField("S-63.4", 4, 'N', 'B', 'N'),
			new DinersField("S-63.5", 0, 'N', 'N', 'N'),
			new DinersField("S-63.6", 7, 'N', 'E', 'N'),
			new DinersField("S-63.7", 0, 'N', 'N', 'N'),
			new DinersField("S-63.8", 8, 'N', 'B', 'N'),
			new DinersField("S-63.9", 14, 'N', 'E', 'N'),
			new DinersField("S-63.10", 13, 'N', 'E', 'N'),
			new DinersField("S-63.11", 1, 'N', 'E', 'N'),
			new DinersField("S-63.12", 30, 'N', 'E', 'N'),
			new DinersField("S-63.13", 6, 'N', 'B', 'N'),
			new DinersField("S-63.14", 36, 'N', 'E', 'N'),
			new DinersField("S-63.15", 9, 'N', 'E', 'N'),
			new DinersField("S-63.16", 0, 'N', 'N', 'N'),
			new DinersField("S-63.17", 0, 'N', 'N', 'N'),
			new DinersField("S-63.18", 2, 'N', 'B', 'N'),
			new DinersField("S-63.19", 3, 'N', 'E', 'N'),
			new DinersField("S-63.20", 0, 'N', 'N', 'N'),
			new DinersField("S-63.21", 1, 'N', 'E', 'N'),
			new DinersField("S-63.22", 0, 'N', 'N', 'N') };

	/*
	 * For all fields, the format can be: AN (alphanumeric, EBCDIC) ANS
	 * (alphanumeric/special characters, EBCDIC) B (binary value) BCD (numeric,
	 * 4-bit BCD = unsigned packed) Bit string N (numeric, 1 byte per character)
	 * indicated as 'T'
	 * 
	 * 
	 * Fieldlength indicates length of the data if length is fixed non negative
	 * value provided,zero indicates bit is not in use Fieldlength data type
	 * indicates 'B' - binary,'H'- Anscii , 'T' - Integer Bitdatatype indicates
	 * 'B' -binary , 'E' - EBCDIC Subelement indicates 'N' - No subelements ,
	 * 'S' - Yes sublements available
	 */

	public static final DinersField VISAFIELDMAP126[] = {

	new DinersField("S-126.1", -2, 'H', 'E', 'N'),
			new DinersField("S-126.2", -2, 'H', 'E', 'N'),
			new DinersField("S-126.3", -2, 'H', 'E', 'N'),
			new DinersField("S-126.4", -2, 'H', 'E', 'N'),
			new DinersField("S-126.5", -2, 'B', 'B', 'N'),
			new DinersField("S-126.6", -2, 'B', 'B', 'N'),
			new DinersField("S-126.7", -2, 'B', 'B', 'N'),
			new DinersField("S-126.8", 40, 'B', 'B', 'N'),
			new DinersField("S-126.9", 40, 'B', 'B', 'N'),
			new DinersField("S-126.10", 6, 'H', 'E', 'N'),
			new DinersField("S-126.11", 0, 'N', 'N', 'N'),
			new DinersField("S-126.12", 6, 'B', 'B', 'N'),
			new DinersField("S-126.13", 1, 'H', 'E', 'N'),
			new DinersField("S-126.14", 0, 'H', 'E', 'N'),
			new DinersField("S-126.15", 1, 'H', 'E', 'N'),
			new DinersField("S-126.16", -2, 'H', 'E', 'N'),
			new DinersField("S-126.17", 0, 'N', 'N', 'N'),
			new DinersField("S-126.18", -2, 'H', 'E', 'N'),
			new DinersField("S-126.19", 1, 'H', 'E', 'N'),
			new DinersField("S-126.20", 0, 'N', 'N', 'N') };

}
