package com.fss.pos.host.iso8583.rupay;

/**
 * @author Paranthamanv
 *
 */

public class RupayMessageMap extends RupayMessage8583 {

	public RupayMessageMap() {
	}

	public byte[] pack() {
		return super.pack(RupayFieldMAP);
	}

	public boolean unpack(String message) {
		return super.unpack(message, RupayFieldMAP);
	}

	/**
	 * For all fields, the format can Ne: AN (alphanumeric, ENCDIC) ANS
	 * (alphanumeric/special characters, ENCDIC) N (Binary value) NCD (numeric,
	 * 4-Nit NCD = unsigned packed) Nit string N (numeric, 1 Nyte per character)
	 * indicated as 'T'
	 * 
	 * 
	 * Fieldlength indicates length of the data if length is fixed non negative
	 * value provided,zero indicates Nit is not in use Fieldlength data type
	 * indicates 'N' - Binary,'H'- Ascii , 'T' - Integer Nitdatatype indicates
	 * 'N' -Ninary , 'E' - ENCDIC SuNelement indicates 'N' - No suNelements ,
	 * 'S' - Yes suNlements availaNle
	 */
	public static final RupayField RupayFieldMAP[] = {

			new RupayField("F-1", 0, 'I', 'I', 'N'), // 0800
			new RupayField("F-2", -2, 'N', 'N', 'N'),
			new RupayField("F-3", 6, 'N', 'N', 'N'),
			new RupayField("F-4", 12, 'N', 'N', 'N'),
			new RupayField("F-5", 12, 'N', 'N', 'N'),
			new RupayField("F-6", 12, 'N', 'N', 'N'),
			new RupayField("F-7", 10, 'N', 'N', 'N'), // 0800
			new RupayField("F-8", 0, 'N', 'N', 'N'), // added
			new RupayField("F-9", 8, 'N', 'N', 'N'),
			new RupayField("F-10", 8, 'N', 'N', 'N'),
			new RupayField("F-11", 6, 'N', 'N', 'N'), // 0800
			new RupayField("F-12", 6, 'N', 'N', 'N'),
			new RupayField("F-13", 4, 'N', 'N', 'N'),
			new RupayField("F-14", 4, 'N', 'N', 'N'),
			new RupayField("F-15", 4, 'N', 'N', 'N'),
			new RupayField("F-16", 4, 'N', 'N', 'N'),
			new RupayField("F-17", 0, 'N', 'N', 'N'),
			new RupayField("F-18", 4, 'N', 'N', 'N'), // added //added
			new RupayField("F-19", 3, 'N', 'N', 'N'),
			new RupayField("F-20", 0, 'N', 'N', 'N'), // added
			new RupayField("F-21", 0, 'N', 'N', 'N'), // added
			new RupayField("F-22", 3, 'N', 'N', 'N'),
			new RupayField("F-23", 3, 'N', 'N', 'N'),
			new RupayField("F-24", 0, 'N', 'N', 'N'),
			new RupayField("F-25", 2, 'N', 'N', 'N'),
			new RupayField("F-26", 0, 'N', 'N', 'N'),
			new RupayField("F-27", 0, 'N', 'N', 'N'),
			new RupayField("F-28", 9, 'N', 'E', 'N'), //
			new RupayField("F-29", 0, 'N', 'N', 'N'),
			new RupayField("F-30", 0, 'N', 'N', 'N'),
			new RupayField("F-31", 0, 'N', 'N', 'N'),
			new RupayField("F-32", -2, 'H', 'N', 'N'),
			new RupayField("F-33", -2, 'H', 'N', 'N'),
			new RupayField("F-34", 0, 'N', 'N', 'N'), // 0800
			new RupayField("F-35", -2, 'H', 'N', 'N'),//
			new RupayField("F-36", 0, 'N', 'N', 'N'),
			new RupayField("F-37", 12, 'N', 'N', 'N'), //
			new RupayField("F-38", 6, 'N', 'N', 'N'), //
			new RupayField("F-39", 2, 'N', 'N', 'N'), //
			new RupayField("F-40", 3, 'N', 'A', 'N'), //
			new RupayField("F-41", 8, 'N', 'N', 'N'), //
			new RupayField("F-42", 15, 'N', 'A', 'N'), //
			new RupayField("F-43", 40, 'N', 'N', 'N'),
			new RupayField("F-44", -2, 'H', 'N', 'N'), //
			new RupayField("F-45", -2, 'H', 'A', 'N'), //
			new RupayField("F-46", 0, 'N', 'N', 'N'),
			new RupayField("F-47", 0, 'N', 'N', 'N'),
//			new RupayField("F-48", -3, 'H', 'A', 'N'), //-- Magnetic stripe
			new RupayField("F-48", -3, 'H', 'N', 'N'),// -- EMV
			new RupayField("F-49", 3, 'N', 'N', 'N'),
			new RupayField("F-50", 3, 'N', 'A', 'N'),
			new RupayField("F-51", 3, 'N', 'A', 'N'),//
			new RupayField("F-52", 16, 'H', 'A', 'N'),//
			new RupayField("F-53", 0, 'N', 'N', 'N'),
//			new RupayField("F-54", -3, 'H', 'A', 'N'),//--Pata
			new RupayField("F-54", -3, 'H', 'N', 'N'),//
//			new RupayField("F-55", -3, 'N', 'A', 'N'),//-- Magnetic stripe
			new RupayField("F-55", -3, 'N', 'B', 'N'),//-- EMV
			new RupayField("F-56", 0, 'N', 'N', 'N'),
			new RupayField("F-57", 0, 'N', 'N', 'N'),
			new RupayField("F-58", 0, 'N', 'N', 'N'),
			new RupayField("F-59", 0, 'N', 'N', 'N'),
			new RupayField("F-60", -2, 'H', 'A', 'N'),
			new RupayField("F-61", -3, 'H', 'A', 'N'), // 
			new RupayField("F-62", -3, 'H', 'A', 'N'),//
			new RupayField("F-63", -3, 'H', 'A', 'N'),
			new RupayField("F-64", 0, 'N', 'N', 'N'),
			new RupayField("F-65", 0, 'N', 'N', 'N'),
			new RupayField("F-66", 0, 'N', 'N', 'N'),
			new RupayField("F-67", 0, 'N', 'N', 'N'),
			new RupayField("F-68", 0, 'N', 'N', 'N'),
			new RupayField("F-69", 0, 'N', 'N', 'N'),
			new RupayField("F-70", 3, 'N', 'N', 'N'),
			new RupayField("F-71", 0, 'N', 'N', 'N'),
			new RupayField("F-72", 0, 'N', 'N', 'N'),
			new RupayField("F-73", 0, 'N', 'N', 'N'),
			new RupayField("F-74", 0, 'N', 'N', 'N'),
			new RupayField("F-75", 0, 'N', 'N', 'N'),
			new RupayField("F-76", 0, 'N', 'N', 'N'),
			new RupayField("F-77", 0, 'N', 'N', 'N'),
			new RupayField("F-78", 0, 'N', 'N', 'N'),
			new RupayField("F-79", 0, 'N', 'N', 'N'),
			new RupayField("F-80", 0, 'N', 'N', 'N'),
			new RupayField("F-81", 0, 'N', 'N', 'N'),
			new RupayField("F-82", 0, 'N', 'N', 'N'),
			new RupayField("F-83", 0, 'N', 'N', 'N'),
			new RupayField("F-84", 0, 'N', 'N', 'N'),
			new RupayField("F-85", 0, 'N', 'N', 'N'),
			new RupayField("F-86", 0, 'N', 'N', 'N'),
			new RupayField("F-97", 0, 'N', 'N', 'N'),
			new RupayField("F-88", 0, 'N', 'N', 'N'),
			new RupayField("F-89", 0, 'N', 'N', 'N'),
			new RupayField("F-90", 42, 'N', 'N', 'N'),
			new RupayField("F-91", 1, 'N', 'A', 'N'),
			new RupayField("F-92", 0, 'N', 'N', 'N'),
			new RupayField("F-93", 0, 'N', 'N', 'N'),
			new RupayField("F-94", 0, 'N', 'N', 'N'),
			new RupayField("F-95", 42, 'N', 'A', 'N'),
			new RupayField("F-96", 0, 'N', 'N', 'N'),
			new RupayField("F-97", 0, 'N', 'N', 'N'),
			new RupayField("F-98", 0, 'N', 'N', 'N'),
			new RupayField("F-99", 0, 'N', 'N', 'N'),
			new RupayField("F-100", 0, 'N', 'N', 'N'),
			new RupayField("F-101", -2, 'H', 'A', 'N'),
			new RupayField("F-102", -2, 'H', 'A', 'N'),
			new RupayField("F-103", -2, 'H', 'A', 'N'),
			new RupayField("F-104", 0, 'N', 'N', 'N'),
			new RupayField("F-105", 0, 'N', 'N', 'N'),
			new RupayField("F-106", 0, 'N', 'N', 'N'),
			new RupayField("F-107", 0, 'N', 'N', 'N'),
			new RupayField("F-108", 0, 'N', 'N', 'N'),
			new RupayField("F-109", 0, 'N', 'N', 'N'),
			new RupayField("F-110", 0, 'N', 'N', 'N'),
			new RupayField("F-111", 0, 'N', 'N', 'N'),
			new RupayField("F-112", 0, 'N', 'N', 'N'),
			new RupayField("F-113", 0, 'N', 'N', 'N'),
			new RupayField("F-114", 0, 'N', 'N', 'N'),
			new RupayField("F-115", 0, 'N', 'N', 'N'),
			new RupayField("F-116", 0, 'N', 'N', 'N'),
			new RupayField("F-117", 0, 'N', 'N', 'N'),
			new RupayField("F-118", 0, 'N', 'N', 'N'),
			new RupayField("F-119", 0, 'N', 'N', 'N'),
			new RupayField("F-120", -3, 'H', 'A', 'N'),
			new RupayField("F-121", -3, 'H', 'A', 'N'),
			new RupayField("F-122", -3, 'H', 'A', 'N'),
			new RupayField("F-123", -3, 'H', 'A', 'N'),
			new RupayField("F-124", -3, 'H', 'N', 'N'),
			new RupayField("F-125", 0, 'N', 'N', 'N'),
			new RupayField("F-126", -3, 'H', 'A', 'N'),
			new RupayField("F-127", -3, 'H', 'A', 'N'),

	};

}
