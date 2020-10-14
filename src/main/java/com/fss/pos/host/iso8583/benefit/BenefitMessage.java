package com.fss.pos.host.iso8583.benefit;



public class BenefitMessage extends BenefitMessage8583
{
	public BenefitMessage()
    {
    }

    public String pack()
    {
        return super.pack(VISAFIELDMAP);
    }

    public BenefitMessage unpack(String message)
    {
        return super.unpack(message, VISAFIELDMAP); 
    }
    
    
	/*
	 * public static void main(String args[]) {
	 * 
	 * for(int i=0;i<=19;i++) {
	 * 
	 * //FIELD_LENGTH_INDICATOR44
	 * 
	 * //new VisaField("S-44.1",-2,'H','E','S');
	 * 
	 * 
	 * } }
	 */
    
    /*
     * 
     *  For all fields, the format can be:
	 *	AN (alphanumeric, EBCDIC)
	 *	ANS (alphanumeric/special characters, EBCDIC)
	 *	B (binary value)
	 *	BCD (numeric, 4-bit BCD = unsigned packed)
	 *	Bit string
	 *	N (numeric, 1 byte per character) indicated as 'T'
     * 
     * 
     * Fieldlength indicates length of the data if length is fixed non negative value provided,zero indicates bit is not in use
     * Fieldlength data type indicates 'B' - binary,'H'- Anscii , 'I' - Integer 
     * Bitdatatype indicates 'B' -binary , 'E' - EBCDIC
     * Subelement indicates 'N' - No subelements , 'S' - Yes sublements available
     * 
     * 
     * 
     */
    
    

    public static final BenefitField VISAFIELDMAP[] = {
    	new BenefitField("F-1",16,'I','I','N'),
    	new BenefitField("F-2",-2,'B','B','N'),
    	new BenefitField("F-3",6,'B','B','N'),
    	new BenefitField("F-4",12,'B','B','N'),
    	new BenefitField("F-5",12,'B','B','N'),
    	new BenefitField("F-6",12,'B','B','N'),
    	new BenefitField("F-7",10,'B','B','N'),
    	new BenefitField("F-8",8,'B','B','N'),
    	new BenefitField("F-9",8,'B','B','N'),
    	new BenefitField("F-10",8,'B','B','N'),
    	new BenefitField("F-11",6,'B','B','N'),
    	new BenefitField("F-12",6,'B','B','N'),
    	new BenefitField("F-13",4,'B','B','N'),
    	new BenefitField("F-14",4,'B','B','N'),
    	new BenefitField("F-15",4,'B','B','N'),
    	new BenefitField("F-16",4,'B','B','N'),
    	new BenefitField("F-17",4,'T','B','N'),
    	new BenefitField("F-18",4,'B','B','N'),
    	new BenefitField("F-19",3,'B','B','N'),
    	new BenefitField("F-20",3,'B','B','N'),
    	new BenefitField("F-21",0,'B','B','N'),
    	new BenefitField("F-22",3,'B','B','N'),
    	new BenefitField("F-23",3,'B','B','N'),
    	new BenefitField("F-24",3,'B','B','N'),
    	new BenefitField("F-25",2,'B','B','N'),
    	new BenefitField("F-26",2,'B','B','N'),
    	new BenefitField("F-27",0,'B','B','N'),
    	new BenefitField("F-28",9,'B','E','N'),
    	new BenefitField("F-29",0,'B','E','N'),
    	new BenefitField("F-30",0,'B','E','N'),
    	new BenefitField("F-31",0,'B','E','N'),
    	new BenefitField("F-32",-2,'B','B','N'),
    	new BenefitField("F-33",-2,'B','B','N'),
    	new BenefitField("F-34",-2,'B','B','N'),
    	new BenefitField("F-35",-2,'B','B','N'),
    	new BenefitField("F-36",-2,'B','B','N'),
    	new BenefitField("F-37",12,'H','E','N'),
    	new BenefitField("F-38",6,'H','E','N'),
    	new BenefitField("F-39",2,'H','E','N'),
    	new BenefitField("F-40",0,'H','E','N'),
    	new BenefitField("F-41",16,'H','E','N'),
    	new BenefitField("F-42",15,'H','E','N'),
    	new BenefitField("F-43",40,'H','E','N'),
    	new BenefitField("F-44",-2,'H','E','S'),
    	new BenefitField("F-45",-2,'H','E','N'),
    	new BenefitField("F-46",-2,'H','E','N'),
    	new BenefitField("F-47",-2,'H','E','N'),
    	new BenefitField("F-48",-3,'H','E','N'),
    	new BenefitField("F-49",3,'B','B','N'),
    	new BenefitField("F-50",3,'B','B','N'),
    	new BenefitField("F-51",3,'B','B','N'),
    	new BenefitField("F-52",16,'B','B','N'),
    	new BenefitField("F-53",16,'B','B','N'),
    	new BenefitField("F-54",-2,'H','E','N'),
    	new BenefitField("F-55",-3,'H','B','N'),
    	new BenefitField("F-56",-2,'H','E','N'),
    	new BenefitField("F-57",-3,'H','E','N'),
    	new BenefitField("F-58",-2,'H','E','N'),
    	new BenefitField("F-59",-2,'H','E','N'),
    	new BenefitField("F-60",-3,'T','B','S'),
    	new BenefitField("F-61",-3,'T','B','N'),
    	new BenefitField("F-62",-2,'T','B','S'),
    	new BenefitField("F-63",-2,'T','B','S'),
    	new BenefitField("F-64",8,'B','B','N'),
    	new BenefitField("F-65",0,'I','I','N'),
    	new BenefitField("F-66",1,'T','B','N'),
    	new BenefitField("F-67",0,'T','B','N'),
    	new BenefitField("F-68",3,'T','B','N'),
    	new BenefitField("F-69",0,'B','B','N'),
    	new BenefitField("F-70",3,'T','B','N'),
    	new BenefitField("F-71",0,'B','B','N'),
    	new BenefitField("F-72",0,'B','B','N'),
    	new BenefitField("F-73",6,'T','B','N'),
    	new BenefitField("F-74",10,'T','B','N'),
    	new BenefitField("F-75",10,'T','B','N'),
    	new BenefitField("F-76",10,'T','B','N'),
    	new BenefitField("F-77",10,'T','B','N'),
    	new BenefitField("F-78",10,'T','B','N'),
    	new BenefitField("F-79",10,'T','B','N'),
    	new BenefitField("F-80",10,'B','B','N'),
    	new BenefitField("F-81",10,'B','B','N'),
    	new BenefitField("F-82",0,'B','B','N'),
    	new BenefitField("F-83",0,'B','B','N'),
    	new BenefitField("F-84",0,'B','B','N'),
    	new BenefitField("F-85",0,'B','B','N'),
    	new BenefitField("F-86",16,'T','B','N'),
    	new BenefitField("F-87",16,'T','B','N'),
    	new BenefitField("F-88",16,'T','B','N'),
    	new BenefitField("F-89",16,'T','B','N'),
    	new BenefitField("F-90",42,'T','B','N'),
    	new BenefitField("F-91",1,'H','E','N'),
    	new BenefitField("F-92",2,'H','E','N'),
    	new BenefitField("F-93",0,'H','E','N'),
    	new BenefitField("F-94",0,'B','E','N'),
    	new BenefitField("F-95",42,'H','E','N'),
    	new BenefitField("F-96",16,'B','B','N'),
    	new BenefitField("F-97",17,'H','E','N'),
    	new BenefitField("F-98",0,'H','E','N'),
    	new BenefitField("F-99",-2,'B','B','N'),
    	new BenefitField("F-100",-2,'B','B','N'),
    	new BenefitField("F-101",-2,'H','E','N'),
    	new BenefitField("F-102",-2,'H','E','N'),
    	new BenefitField("F-103",-2,'H','E','N'),
    	new BenefitField("F-104",-2,'H','B','N'),
    	new BenefitField("F-105",32,'B','B','N'),
    	new BenefitField("F-106",0,'B','B','N'),
    	new BenefitField("F-107",0,'B','B','N'),
    	new BenefitField("F-108",0,'B','B','N'),
    	new BenefitField("F-109",0,'B','B','N'),
    	new BenefitField("F-110",0,'B','B','N'),
    	new BenefitField("F-111",0,'B','B','N'),
    	new BenefitField("F-112",0,'B','B','N'),
    	new BenefitField("F-113",0,'B','B','N'),
    	new BenefitField("F-114",0,'B','B','N'),
    	new BenefitField("F-115",-2,'H','E','N'),
    	new BenefitField("F-116",-2,'H','B','N'),
    	new BenefitField("F-117",-2,'H','E','N'),
    	new BenefitField("F-118",-2,'H','E','N'),
    	new BenefitField("F-119",-2,'H','E','N'),
    	new BenefitField("F-120",0,'T','B','N'),
    	new BenefitField("F-121",0,'H','E','N'),
    	new BenefitField("F-122",0,'H','E','N'),
    	new BenefitField("F-123",-2,'H','E','N'),
    	new BenefitField("F-124",0,'H','E','N'),
    	new BenefitField("F-125",-2,'H','E','N'),
    	new BenefitField("F-126",-2,'T','B','S'),
    	new BenefitField("F-127",-2,'B','B','N'),
    	new BenefitField("F-128",0,'H','B','N'),
    	new BenefitField("F-129",8,'B','B','N'),
    	new BenefitField("F-130",24,'B','B','N'),
    	new BenefitField("F-131",40,'B','B','N'),
    	new BenefitField("F-132",4,'B','B','N'),
    	new BenefitField("F-133",8,'H','E','N'),
    	new BenefitField("F-134",-1,'T','B','N'),
    	new BenefitField("F-135",-1,'T','B','N'),
    	new BenefitField("F-136",16,'H','B','N'),
    	new BenefitField("F-137",4,'H','B','N'),
    	new BenefitField("F-138",16,'H','B','N'),
    	new BenefitField("F-139",20,'H','B','N'),
    	new BenefitField("F-140",-1,'T','B','N'),
    	new BenefitField("F-141",-1,'T','B','N'),
    	new BenefitField("F-142",-1,'T','B','N'),
    	new BenefitField("F-143",-1,'T','B','N'),
    	new BenefitField("F-144",2,'B','B','N'),
    	new BenefitField("F-145",3,'B','B','N'),
    	new BenefitField("F-146",6,'B','B','N'),
    	new BenefitField("F-147",12,'B','B','N'),
    	new BenefitField("F-148",3,'B','B','N'),
    	new BenefitField("F-149",6,'B','B','N'),
    	new BenefitField("F-150",8,'B','B','N'),
    	new BenefitField("F-151",12,'B','B','N'),
    	new BenefitField("F-152",8,'H','B','N'),
    	new BenefitField("F-153",2,'B','B','N'),
    	new BenefitField("F-154",1,'B','B','N'),
    	new BenefitField("F-155",1,'B','B','N'),
    	new BenefitField("F-156",9,'B','B','N'),
    	new BenefitField("F-157",9,'B','B','N'),
    	new BenefitField("F-158",9,'B','B','N'),
    	new BenefitField("F-159",9,'B','B','N'),
    	new BenefitField("F-160",-1,'B','B','N'),
    	new BenefitField("F-161",-1,'B','B','N'),
    	new BenefitField("F-162",-1,'B','B','N'),
    	new BenefitField("F-163",-1,'B','B','N'),
    	new BenefitField("F-164",-1,'B','B','N'),
    	new BenefitField("F-165",-2,'B','B','N'),
    	new BenefitField("F-166",6,'B','B','N'),
    	new BenefitField("F-167",2,'B','B','N'),
    	new BenefitField("F-168",-3,'B','B','N'),
    	new BenefitField("F-169",8,'B','B','N'),
    	new BenefitField("F-170",15,'B','B','N'),
    	new BenefitField("F-171",40,'B','B','N'),
    	new BenefitField("F-172",20,'B','B','N'),
    	new BenefitField("F-173",-1,'B','B','N'),
    	new BenefitField("F-174",-1,'B','B','N'),
    	new BenefitField("F-175",-1,'B','B','N'),
    	new BenefitField("F-176",-1,'B','B','N'),
    	new BenefitField("F-177",3,'B','B','N'),
    	new BenefitField("F-178",3,'B','B','N'),
    	new BenefitField("F-179",3,'B','B','N'),
    	new BenefitField("F-180",16,'B','B','N'),
    	new BenefitField("F-181",16,'B','B','N'),
    	new BenefitField("F-182",-2,'B','B','N'),
    	new BenefitField("F-183",-1,'H','E','N'),
    	new BenefitField("F-184",-1,'B','B','N'),
    	new BenefitField("F-185",-1,'B','B','N'),
    	new BenefitField("F-186",-1,'B','B','N'),
    	new BenefitField("F-187",-1,'H','E','N'),
    	new BenefitField("F-188",-1,'B','B','N'),
    	new BenefitField("F-189",-2,'B','B','N'),
    	new BenefitField("F-190",10,'B','B','N'),
    	new BenefitField("F-191",-2,'B','B','N'),
    	new BenefitField("F-192",8,'B','B','N')

    };
    
    /*
     *  For all fields, the format can be:
	 *	AN (alphanumeric, EBCDIC)
	 *	ANS (alphanumeric/special characters, EBCDIC)
	 *	B (binary value)
	 *	BCD (numeric, 4-bit BCD = unsigned packed)
	 *	Bit string
	 *	N (numeric, 1 byte per character) indicated as 'T'
     * 
     * 
     * Fieldlength indicates length of the data if length is fixed non negative value provided,zero indicates bit is not in use
     * Fieldlength data type indicates 'B' - binary,'H'- Anscii , 'T' - Integer 
     * Bitdatatype indicates 'B' -binary , 'E' - EBCDIC
     * Subelement indicates 'N' - No subelements , 'S' - Yes sublements available
     * 
     */
    
  public static final BenefitField VISAFIELDMAP44[] = {
	   
	new BenefitField("S-44.1",1,'N','E','N'),
   	new BenefitField("S-44.2",1,'N','E','N'),
   	new BenefitField("S-44.3",0,'N','N','N'),
   	new BenefitField("S-44.4",0,'N','N','N'),
   	new BenefitField("S-44.5",1,'N','E','N'),
   	new BenefitField("S-44.6",2,'N','E','N'),
   	new BenefitField("S-44.7",1,'N','E','N'),
   	new BenefitField("S-44.8",1,'N','E','N'),
   	new BenefitField("S-44.9",0,'N','N','N'),
   	new BenefitField("S-44.10",1,'N','E','N'),
   	new BenefitField("S-44.11",2,'N','E','N'),
   	new BenefitField("S-44.12",1,'N','E','N'),
   	new BenefitField("S-44.13",1,'N','E','N'),
   	new BenefitField("S-44.14",4,'N','E','N')
    	
    };

  /*
   *  For all fields, the format can be:
	 *	AN (alphanumeric, EBCDIC)
	 *	ANS (alphanumeric/special characters, EBCDIC)
	 *	B (binary value)
	 *	BCD (numeric, 4-bit BCD = unsigned packed)
	 *	Bit string
	 *	N (numeric, 1 byte per character) indicated as 'T'
   * 
   * 
   * Fieldlength indicates length of the data if length is fixed non negative value provided,zero indicates bit is not in use
   * Fieldlength data type indicates 'B' - binary,'H'- Anscii , 'T' - Integer 
   * Bitdatatype indicates 'B' -binary , 'E' - EBCDIC
   * Subelement indicates 'N' - No subelements , 'S' - Yes sublements available
   * 
   */

public static final BenefitField VISAFIELDMAP60[] = {
    	
	
	new BenefitField("S-60.1",1,'N','T','N'),
	new BenefitField("S-60.2",1,'N','T','N'),
	new BenefitField("S-60.3",1,'N','T','N'),
	new BenefitField("S-60.4",1,'N','T','N'),
	new BenefitField("S-60.5",2,'N','T','N'),
	new BenefitField("S-60.6",1,'N','T','N'),
	new BenefitField("S-60.7",1,'N','T','N'),
	new BenefitField("S-60.8",2,'N','T','N'),
	new BenefitField("S-60.9",1,'N','T','N'),
	new BenefitField("S-60.10",1,'N','T','N')
    };

/*
 *  For all fields, the format can be:
 *	AN (alphanumeric, EBCDIC)
 *	ANS (alphanumeric/special characters, EBCDIC)
 *	B (binary value)
 *	BCD (numeric, 4-bit BCD = unsigned packed)
 *	Bit string
 *	N (numeric, 1 byte per character) indicated as 'T'
 * 
 * 
 * Fieldlength indicates length of the data if length is fixed non negative value provided,zero indicates bit is not in use
 * Fieldlength data type indicates 'B' - binary,'H'- Anscii , 'T' - Integer 
 * Bitdatatype indicates 'B' -binary , 'E' - EBCDIC
 * Subelement indicates 'N' - No subelements , 'S' - Yes sublements available
 * 
 */

public static final BenefitField VISAFIELDMAP62[] = {
	
	
	new BenefitField("S-62.1",1,'N','E','N'),
	new BenefitField("S-62.2",15,'N','T','N'),
	new BenefitField("S-62.3",4,'N','E','N'),
	new BenefitField("S-62.4",1,'N','E','N'),
	new BenefitField("S-62.5",2,'N','T','N'),
	new BenefitField("S-62.6",1,'N','E','N'),
	new BenefitField("S-62.7",26,'N','E','N'),
	new BenefitField("S-62.8",6,'N','T','N'),
	new BenefitField("S-62.9",1,'N','E','N'),
	new BenefitField("S-62.10",6,'N','T','N'),
	new BenefitField("S-62.11",2,'N','T','N'),
	new BenefitField("S-62.12",2,'N','T','N'),
	new BenefitField("S-62.13",1,'N','E','N'),
	new BenefitField("S-62.14",12,'N','T','N'),
	new BenefitField("S-62.15",1,'N','E','N'),
	new BenefitField("S-62.16",2,'N','E','N'),
	new BenefitField("S-62.17",15,'N','E','N'),
	new BenefitField("S-62.18",1,'N','E','N'),
	new BenefitField("S-62.19",2,'N','E','N'),
	new BenefitField("S-62.20",10,'N','T','N'),
	new BenefitField("S-62.21",4,'N','E','N'),
	new BenefitField("S-62.22",6,'N','E','N'),
	new BenefitField("S-62.23",2,'N','E','N'),
	new BenefitField("S-62.24",6,'N','E','N'),
	new BenefitField("S-62.25",1,'N','E','N'),
	new BenefitField("S-62.26",1,'N','E','N')
};

/*
 *  For all fields, the format can be:
 *	AN (alphanumeric, EBCDIC)
 *	ANS (alphanumeric/special characters, EBCDIC)
 *	B (binary value)
 *	BCD (numeric, 4-bit BCD = unsigned packed)
 *	Bit string
 *	N (numeric, 1 byte per character) indicated as 'T'
 * 
 * 
 * Fieldlength indicates length of the data if length is fixed non negative value provided,zero indicates bit is not in use
 * Fieldlength data type indicates 'B' - binary,'H'- Anscii , 'T' - Integer 
 * Bitdatatype indicates 'B' -binary , 'E' - EBCDIC
 * Subelement indicates 'N' - No subelements , 'S' - Yes sublements available
 * 
 */

public static final BenefitField VISAFIELDMAP63[] = {
	
	
	new BenefitField("S-63.1",4,'N','B','N'),
	new BenefitField("S-63.2",4,'N','B','N'),
	new BenefitField("S-63.3",4,'N','B','N'),
	new BenefitField("S-63.4",4,'N','B','N'),
	new BenefitField("S-63.5",0,'N','N','N'),
	new BenefitField("S-63.6",7,'N','E','N'),
	new BenefitField("S-63.7",0,'N','N','N'),
	new BenefitField("S-63.8",8,'N','B','N'),
	new BenefitField("S-63.9",14,'N','E','N'),
	new BenefitField("S-63.10",13,'N','E','N'),
	new BenefitField("S-63.11",1,'N','E','N'),
	new BenefitField("S-63.12",30,'N','E','N'),
	new BenefitField("S-63.13",6,'N','B','N'),
	new BenefitField("S-63.14",36,'N','E','N'),
	new BenefitField("S-63.15",9,'N','E','N'),
	new BenefitField("S-63.16",0,'N','N','N'),
	new BenefitField("S-63.17",0,'N','N','N'),
	new BenefitField("S-63.18",2,'N','B','N'),
	new BenefitField("S-63.19",3,'N','E','N'),
	new BenefitField("S-63.20",0,'N','N','N'),
	new BenefitField("S-63.21",1,'N','E','N'),
	new BenefitField("S-63.22",0,'N','N','N')
};

/*
 *  For all fields, the format can be:
 *	AN (alphanumeric, EBCDIC)
 *	ANS (alphanumeric/special characters, EBCDIC)
 *	B (binary value)
 *	BCD (numeric, 4-bit BCD = unsigned packed)
 *	Bit string
 *	N (numeric, 1 byte per character) indicated as 'T'
 * 
 * 
 * Fieldlength indicates length of the data if length is fixed non negative value provided,zero indicates bit is not in use
 * Fieldlength data type indicates 'B' - binary,'H'- Anscii , 'T' - Integer 
 * Bitdatatype indicates 'B' -binary , 'E' - EBCDIC
 * Subelement indicates 'N' - No subelements , 'S' - Yes sublements available
 * 
 */

public static final BenefitField VISAFIELDMAP126[] = {
	
	new BenefitField("S-126.1",-2,'H','E','N'),
	new BenefitField("S-126.2",-2,'H','E','N'),
	new BenefitField("S-126.3",-2,'H','E','N'),
	new BenefitField("S-126.4",-2,'H','E','N'),
	new BenefitField("S-126.5",-2,'B','B','N'),
	new BenefitField("S-126.6",-2,'B','B','N'),
	new BenefitField("S-126.7",-2,'B','B','N'),
	new BenefitField("S-126.8",40,'B','B','N'),
	new BenefitField("S-126.9",40,'B','B','N'),
	new BenefitField("S-126.10",6,'H','E','N'),
	new BenefitField("S-126.11",0,'N','N','N'),
	new BenefitField("S-126.12",6,'B','B','N'),
	new BenefitField("S-126.13",1,'H','E','N'),
	new BenefitField("S-126.14",0,'H','E','N'),
	new BenefitField("S-126.15",1,'H','E','N'),
	new BenefitField("S-126.16",-2,'H','E','N'),
	new BenefitField("S-126.17",0,'N','N','N'),
	new BenefitField("S-126.18",-2,'H','E','N'),
	new BenefitField("S-126.19",1,'H','E','N'),
	new BenefitField("S-126.20",0,'N','N','N')
};    
    
    
}
