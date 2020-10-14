package com.fss.pos.host.iso8583.amex;

public interface AmexConstants {

	String AUTH_PROCESS = "AP";

	String ENDPOINT_PROCESS = "EP";

	String LOG = "LOG";

	String DC_ID = "DC-ID";

	long HISO_MAX_TIMEOUT = 20;

	long IH_MAX_TIMEOUT = 25;

	String CORE = "CORE";

	String HSM_PROCESS = "SP";

	String AUDIT_PROCESS = "ADP";

	String INTERCHANGE_PROCESS = "IP";

	String HOST_PROCESS = "HP";

	String NO_ROUTING = "DOWN";

	String UP = "UP";

	String DOWN = "DOWN";

	String SAF = "SAF";

	String INTERCHANGE_DOWN = "INTERCHANGE_DOWN";

	String INTERCHANGE_STATION_DOWN = "INTERCHANGE_STATION_DOWN";

	String AUTH_DOWN = "AUTH_DOWN";

	String HSM_DOWN = "HSM_DOWN";

	String TIME_OUT = "T-OUT";

	String HSM_STATION_DOWN = "HSM_STATION_DOWN";

	String HOST_DOWN = "HOST_DOWN";

	String HOST_STATION_DOWN = "HOST_STATION_DOWN";

	String TIMEDOUT = "TIMEOUT_PROC";

	String REQ_INITIATED_BY = "REQ-INITIATED-BY";

	String HISO = "H";
	String AUTH = "A";
	String INTERFACE = "I";

	String TIMEDOUT_HANDLER = "TIMEDOUT_HANDLER";

	String LATE_RESPONSE = "LATE_RESPONSE";

	int TOT_BITMAP_LENGTH = 192;

	String ISO_0420 = "0420";
	String ISO_0421 = "0421";
	String ISO_0430 = "0430";
	String ISO_0800 = "0800";
	String ISO_0810 = "0810";
	String ISO_1200 = "1200";
	String ISO_1210 = "1210";
	String ISO_1400 = "1400";
	String ISO_1410 = "1410";
	String ISO_1800 = "1800";
	String ISO_1810 = "1810";
	String ISO_9200 = "9200";
	String ISO_9210 = "9210";
	String ISO_9230 = "9230";
	String ISO_9430 = "9430";
	String ISO_9810 = "9810";

	String[] messageStatus = { "REQ", "RSP", "REQ_LOG_ON", "RSP_LOG_OFF",
			"CDOWN_RSP", "CDOWN_RSP_LOG", "CDOWN_RSP_LOG_OFF",
			"CDOWN_RSP_LOG_OFF_LOG", "CDOWN_RVSL_REQ", "HDOWN_REQ",
			"HDOWN_RSP", "HCDOWN_REQ_LOG", "HCDOWN_RSP", "HCDOWN_RSP_LOG",
			"CHDOWN_RVSL_REQ", "SUSPECT_RVSL_LOG", "TIME_OUT_RSP",
			"TIME_OUT_RSP_LOG", "COMPLETE_RVSL_LOG", "INVALID_MSG_LOG",
			"INVALID_STATUS_LOG", "MQREQ", "MQRSP", "MQ_CDOWN_REQ" };

	String[] messageTypeGroup = { "0100", "0110", "0200", "0210", "0205",
			"0205", "0220", "0221", "0230", "0402", "0412", "0420", "0421",
			"0430", "0800", "0810", "1200", "1210", "1400", "1410", "1800",
			"1810", "9200", "9210", "9230", "9430", "9810" };

	String INTERCHANGE_FIID = "INTCHNG-FIID";

	String PAN_FIID = "PAN-FIID";

	String TRACK_DATA = "TRACK-DATA";

	String KEY_INDEX = "KEY-INDEX";

	// Transaction Request message
	String TRAN_REQ_MSG = "TRAN-REQ-MSG";

	// Transaction Response message
	String TRAN_RSP_MSG = "TRAN-RSP-MSG";

	String TXN_STATUS = "TXN-STATUS";

	String HOST_ID = "HOST-ID";

	String ZPK_FROM = "ZPK-FROM";

	String ZPK_TO = "ZPK-TO";

	String PIN_BLOCK = "PIN_BLOCK";

	String ACC_NO = "ACC_NO";

	// Host id
	String ACM = "ACM";

	/* ISO Header */
	String ISO_HEADER = "ISO016000050";

	/* Accepter */
	String ACCEPTER = "0";

	// Message status
	String STATUS = "STATUS";

	String PAN_LENGTH = "PAN-LEN";

	String CARD_PREFIX = "CARD-PREFIX";

	// station
	String STATION = "STATION";

	// HOST Id
	String HOST_STATION_ID = "HOST-ID";

	// Transaction message
	String TRAN_MSG = "TOT-MSG-LNT";

	// Handler type
	String VISA = "VISA";

	// Message type
	String MSG_TYP = "MSG-TYP";

	// Transaction code
	String TRAN_CODE = "TRAN-CODE";

	// Request type
	String REQ_TYPE = "REQ-TYPE";

	// Issuer Institution id
	String ISS_INSTITUTION_ID = "ISSU_INST-ID";

	// Acquirer Institution id
	String ACQ_INSTITUTION_ID = "ACQ_INST-ID";

	// Terminal type
	String MERCHANT_TYPE_CODE = "MERCH-TYPE";

	// Currency code
	String CURRENCY_CODE = "CURENCY-CODE";

	// Terminal Id
	String TERMINAL_ID = "TERM-ID";

	String ATM_DCID = "01";
	String POS_DCID = "02";

	// Aquirer transaction profile
	String ACQ_TXN_PROFILE = "ACQ-TXN-PROFILE";

	// ISSUE transaction profile
	String ISS_TXN_PROFILE = "ISS-TXN-PROFILE";
	String DCID_ATM = "01";
	String DCID_POS = "02";

	// Request message
	String REQ = "REQ";

	// Response message
	String RSP = "RSP";

	// String BAD_RSP = "BAD-RSP";
	String INT_RSP = "INT-RSP";

	// Client down message
	String C_DOWN = "C-DOWN";

	// HISO down message
	String HISO_DOWN = "HISO-DOWN";

	// Invalid message log
	String INV_MSG_LOG = "INV-MSG-LOG";

	String TXN_ERROR = "E";

	String YES = "Y";
	String NO = "N";

	String ATM_TXN = "6011";
	String POS_TXN = "5999";

	// Header 1
	String HDR_LNT = "HDR-LNT";
	// Header 2
	String HDR_FLAG_FORMAT = "HDR-FLAG-FORMAT";
	// Header 3
	String TXT_FORMAT = "TXT-FORMAT";
	// Header 4
	String TOT_MSG_LNT = "TOT-MSG-LNT";
	// Header 5
	String DST_STAT_ID = "DST-STAT-ID";
	// Header 6
	String SRC_STAT_ID = "SRC-STAT-ID";
	// Header 7
	String RND_CON_INF = "RND-CON-INF";
	// Header 8
	String BASE_1_FLAG = "BASE-1-FLAG";
	// Header 9
	String MSG_STATUS_FLAG = "MSG-STATUS-FLAG";
	// Header 10
	String BAT_NO = "BAT-NO";
	// Header 11
	String RESERVED = "R";
	// Header 12
	String USR_INFO = "USR-INFO";
	// Header 13
	String HEADER_13 = "HEADER-13";
	// Header 14
	String HEADER_14 = "HEADER-14";

	// Common fields
	String CUST_CARD_NO = "CARD-NO";

	// ICFE-GENERAL
	String SETTLEMENT_HOUR = "SETLMNT-HOUR";

	String SETTLEMENT_MINUTE = "SETLMNT-MINT";

	String SETTLEMENT_DAYS = "SETLMNT-DAYS";

	String SWT_POST_DATE = "SWT-POST-DT";

	String HOLYDAY_DATE = "HOLYDAY-DT";

	String DATA_MASK_FLAG = "DATA-MASK-FLG";

	String RIGHT_UNMASK_DIGITS = "RIGHT-UNMASK-DIGITS";

	String MIN_MASKED_DIGITS = "MIN-MASKED-DIGITS";

	String MAX_LEFT_UNMASK_DIGITS = "MAX-LEFT-UNMASK-DIGITS";

	String NMM_WAIT_TIME = "NMM-WAIT-TIME";

	String NMM_EXT_NETWORK = "NMM-EXT-NETWORK";

	String IH_WAIT_FOR_TRAFIC = "IH-WAIT-FOR-TRAFIC";

	String PERFORMANCE_MONITOR_PERIOD = "PERFORMANCE-MONITOR-PERIOD";

	String MAX_OUTBOUND = "MAX-OUTBOUND";

	String MAX_INBOUND = "MAX-INBOUND";

	String PROCESS_MODE = "PROCESS-MODE";

	String AUTO_SIGNON_START = "AUTO-SIGNON-START";

	String IH_MAX_TIMEOUTS = "IH-MAX-TIMEOUTS";

	String IH_MAX_SAF_RETRY = "IH-MAX-SAF-RETRY";

	String ACK_TO_SWT = "ACK-TO-SWT";

	String ACK_FROM_SWT = "ACK-FROM-SWT";

	String MULTI_CURRENCY = "MULTI-CURRENCY";

	String IH_NMM_ENABLED = "IH-NMM-ENABLED";

	String ILF_EXTRACT_NO = "ILF-EXTRACT-NO";

	// ICFE-ATM

	String ATM_DEF_ROUTE_GRP = "ATMDEFROUTEGRP";

	String ATM_ACQ_TXN_PROFILE = "ATM-ACQ-TXN-PROFILE";

	String ATM_ISS_TXN_PROFILE = "ATM-ISS-TXN-PROFILE";

	String ATM_DEF_MERCH_TYPE = "ATM-DEF-MERCH-TYPE";

	String APPROVAL_CODE_LEN = "APPROVAL-CODE-LEN";

	String ATM_SAF = "ATM-SAF";

	String ATM_OUTBOUND = "ATM-OUTBOUND";

	String ATM_INBOUND = "ATM-INBOUND";

	String ATM_COMPLETION = "ATM-COMPLETION";

	String ATM_COMPLETION_ACK = "ATM-COMPLETION-ACK";

	String ATM_SHARING_GRP = "ATM-SHARING-GRP";

	// ICFE-POS

	String REF_PHONE_NO = "REF-PHONE-NO";

	String RETAILER_ID_DEF = "RETAILER-ID-DEF";

	String TIMEOUT_ACTION = "TIMEOUT-ACTION";

	String SETTLE_ENTITY = "SETTLE-ENTITY";

	String POS_ACQ_TXN_PROFILE = "POS-ACQ-TXN-PROFILE";

	String POS_ISS_TXN_PROFILE = "POS-ISS-TXN-PROFILE";

	String ADJUSTMENT_FLAG = "ADJ-FLAG";

	String CHARGEBACK_FLAG = "CHARGEBACK_FLAG";

	String POS_SAF = "POS-SAF";

	String POS_OUTBOUND = "POS-OUTBOUND";

	String POS_INBOUND = "POS-INBOUND";

	String POS_COMPLETION = "POS-COMPLETION";

	String POS_COMPLETION_ACK = "POS-COMPLETION-ACK";

	String IH_DEF_PREAUTH_AMT = "IH-DEF-PREAUTH-AMT";

	String IH_APROVL_CODE_LEN = "IH-APROVL-CODE-LEN";

	String IH_PREAUTH_HOLD_INCRMNT = "IH-PREAUTH-HOLD-INCRMNT";

	String IH_PREAUTH_HOLD_TIME = "IH-PREAUTH-HOLD-TIME";

	String ALLOWED_SERVICES = "ALLOWED-SERVICES";

	// Key6 Data s
	String ENCRYPT_TYPE = "ENCRYPT-TYPE";

	String PINBLOCK_FORMAT = "PINBLOCK-FORMAT";

	String ANSIPAN_FORMAT = "ANSIPAN-FORMAT";

	String MAC_ENCRYPT_TYPE = "MAC-ENCRYPT-TYPE";

	String PIN_PAD_CHAR = "PIN-PAD-CHAR";

	String MAC_DATA_TYPE = "MAC-DATA-TYPE";

	String NO_OF_KEYS = "NO-OF-KEYS";

	String KEY_LENGTH = "KEY-LENGTH";

	String FULL_MSG_MAC = "FULL-MSG-MAC";

	String MAC_KEY_LEN = "MAC-KEY-LEN";

	String CLEAR = "CLEAR";

	String CHECK_DIGITS = "CHECK-DIGITS";

	String ENCRYPTED = "ENCRYPTED";

	String PIN_EXNG_KEY = "PIN-EXNG-KEY";

	String PIN_EXNG_CHECK_DIG = "PIN-EXNG-CHECK-DIG";

	String MAC_EXNG_KEY = "MAC-EXNG-KEY";

	String MAC_EXNG_CHECK_DIG = "MAC-EXNG-CHECK-DIG";

	String PIN_KEY1 = "PIN-KEY1";

	String PIN_CHECK_DIGITS1 = "PIN_CHECK-DIGITS1";

	String PIN_CURRENT_INDEX = "PIN-CURRENT-INDEX";

	String PIN_KEY2 = "PIN-KEY2";

	String PIN_CHECK_DIGITS2 = "PIN-CHECK-DIGITS2";

	String PIN_KEY_COUNTER = "PIN-KEY-COUNTER";

	String MAC_KEY1 = "MAC-KEY1";

	String MAC_CHECK_DIGITS1 = "MAC_CHECK-DIGITS1";

	String MAC_CURRENT_INDEX = "MAC-CURRENT-INDEX";

	String MAC_KEY2 = "MAC-KEY2";

	String MAC_CHECK_DIGITS2 = "MAC-CHECK-DIGITS2";

	String MAC_KEY_COUNTER = "MAC-KEY-COUNTER";

	String PIN_KEY_VARIANT = "PIN-KEY-VARIANT";

	String MAC_KEY_VARIANT = "MAC-KEY-VARIANT";

	String PIN_KEY_TIMER_VAL = "PIN-KEY-TIMER-VAL";

	String MAC_KEY_TIMER_VAL = "MAC-KEY-TIMER-VAL";

	String PIN_KEY_TIMER_INTVL = "PIN-KEY-TIMER-INTVL";

	String MAC_KEY_TIMER_INTVL = "MAC-KEY-TIMER-INTVL";

	String PIN_KEY_TRAN = "PIN-KEY-TRAN";

	String MAC_KEY_TRAN = "MAC-KEY-TRAN";

	String PIN_KEY_ERROR = "PIN-KEY-ERROR";

	String MAC_KEY_ERROR = "MAC-KEY-ERROR";

	String CONSECUTIVE_PIN_KEY_ERROR = "CONSECUTIVE-PIN-KEY-ERROR";

	String CONSECUTIVE_MAC_KEY_ERROR = "CONSECUTIVE-MAC-KEY-ERROR";

	String KMAC_SYNC_ERROR = "KMAC-SYNC-ERROR";

	String CLEAR_OLD_KEY_TIMER_VAL = "CLEAR-OLD-KEY-TIMER-VAL";

	String ORIGINATING_ID = "ORIGINATING-ID";

	String RECEIVING_ID = "RECEIVING-ID";

	String KEY_PROCESSING_TYPE = "KEY-PROCESSING-TYPE";

	String NOTARIZATION_SUPPORT = "NOTARIZATION-SUPPORT";

	// HCF data s

	String DPC = "DPC";

	String TOKEN_GROUP = "TOKEN-GROUP";

	String NETWORK_MANAGEMENT = "NETWORK-MANAGEMENT";

	String DPC_TYPE = "DPC-TYPE";

	String STORE_AND_FWD = "STORE-AND-FWD";

	String EXTND_NETWORK = "EXTND-NETWORK";

	String ACK_TO_DPC = "ACK-TO-DPC";

	String HISO_WAIT_FOR_TRAFIC = "HISO-WAIT-FOR-TRAFIC";

	String ACK_FROM_DPC = "ACK-FROM-SWITCH";

	String PERFORMANCE_PERIOD = "PERFORMANCE-PERIOD";

	String HISO_MAX_TIMEOUTS = "HISO-MAX-TIMEOUTS";

	String HISO_NMM_ENABLED = "HISO-NMM-ENABLED";

	String MAX_OUTSTAND_SAF = "MAX-OUTSTAND-SAF";

	String MAX_SAF_RETRY = "MAX-SAF-RETRY";

	String PROTOCOL_TYPE = "PROTOCOL-TYPE";

	String HISO_OUTBOUND = "HISO-OUTBOUND";

	String HISO_INBOUND = "HISO-INBOUND";

	String MSG_SEQ_FLAG = "MSG-SEQ-FLAG";

	String TIME_DISCREPANCY_CHK = "TIME-DISCREPANCY-CHK";

	String RELEASE_INDICATOR = "RELEASE_IND";

	String MSG_FORMAT = "MSG-FORMAT";

	String CHARACTER_FORMAT = "CHARACTER-FORMAT";

	String ENHANCED_STATUS = "ENHANCED-STATUS";

	String DATA_PREFIX_CHAR = "DATA-PREFIX-CHAR";

	String STATION_TYPE = "STATION-TYPE";

	String STATION_DESC = "STATION-DESC";

	String HISO_OUTBOUND_LMT = "HISO_OUTBOUND_LMT";

	String HISO_DEF_PREAUTH_AMT = "HISO-DEF-PREAUTH-AMT";

	String HISO_APPRVL_CODE_LEN = "HISO-APPRVL-CODE-LEN";

	String HISO_PREAUTH_HOLD_INC = "HISO-PREAUTH-HOLD-INC";

	String HISO_PREAUTH_HOLD_TIME = "HISO-PREAUTH-HOLD-TIME";

	// CPF General data s
	String PREFIX_ROUTING = "PREFIX-ROUTING";
	String EXP_CHECK_TYPE = "EXP-CHECK-TYPE";
	String MOD10_CHECK = "MOD10-CHECK";

	// Data Elements
	String FIELD_1 = "F-1";
	String FIELD_2 = "F-2";
	String FIELD_3 = "F-3";
	String FIELD_4 = "F-4";
	String FIELD_5 = "F-5";
	String FIELD_6 = "F-6";
	String FIELD_7 = "F-7";
	String FIELD_8 = "F-8";
	String FIELD_9 = "F-9";
	String FIELD_10 = "F-10";
	String FIELD_11 = "F-11";
	String FIELD_12 = "F-12";
	String FIELD_13 = "F-13";
	String FIELD_14 = "F-14";
	String FIELD_15 = "F-15";
	String FIELD_16 = "F-16";
	String FIELD_17 = "F-17";
	String FIELD_18 = "F-18";
	String FIELD_19 = "F-19";
	String FIELD_20 = "F-20";
	String FIELD_21 = "F-21";
	String FIELD_22 = "F-22";
	String FIELD_23 = "F-23";
	String FIELD_24 = "F-24";
	String FIELD_25 = "F-25";
	String FIELD_26 = "F-26";
	String FIELD_27 = "F-27";
	String FIELD_28 = "F-28";
	String FIELD_29 = "F-29";
	String FIELD_30 = "F-30";
	String FIELD_31 = "F-31";
	String FIELD_32 = "F-32";
	String FIELD_33 = "F-33";
	String FIELD_34 = "F-34";
	String FIELD_35 = "F-35";
	String FIELD_36 = "F-36";
	String FIELD_37 = "F-37";
	String FIELD_38 = "F-38";
	String FIELD_39 = "F-39";
	String FIELD_40 = "F-40";
	String FIELD_41 = "F-41";
	String FIELD_42 = "F-42";
	String FIELD_43 = "F-43";
	String FIELD_44 = "F-44";
	String FIELD_45 = "F-45";
	String FIELD_46 = "F-46";
	String FIELD_47 = "F-47";
	String FIELD_48 = "F-48";
	String FIELD_49 = "F-49";
	String FIELD_50 = "F-50";
	String FIELD_51 = "F-51";
	String FIELD_52 = "F-52";
	String FIELD_53 = "F-53";
	String FIELD_54 = "F-54";
	String FIELD_55 = "F-55";
	String FIELD_56 = "F-56";
	String FIELD_57 = "F-57";
	String FIELD_58 = "F-58";
	String FIELD_59 = "F-59";
	String FIELD_60 = "F-60";
	String FIELD_61 = "F-61";
	String FIELD_62 = "F-62";
	String FIELD_63 = "F-63";
	String FIELD_64 = "F-64";
	String FIELD_65 = "F-65";
	String FIELD_66 = "F-66";
	String FIELD_67 = "F-67";
	String FIELD_68 = "F-68";
	String FIELD_69 = "F-69";
	String FIELD_70 = "F-70";
	String FIELD_71 = "F-71";
	String FIELD_72 = "F-72";
	String FIELD_73 = "F-73";
	String FIELD_74 = "F-74";
	String FIELD_75 = "F-75";
	String FIELD_76 = "F-76";
	String FIELD_77 = "F-77";
	String FIELD_78 = "F-78";
	String FIELD_79 = "F-79";
	String FIELD_80 = "F-80";
	String FIELD_81 = "F-81";
	String FIELD_82 = "F-82";
	String FIELD_83 = "F-83";
	String FIELD_84 = "F-84";
	String FIELD_85 = "F-85";
	String FIELD_86 = "F-86";
	String FIELD_87 = "F-87";
	String FIELD_88 = "F-88";
	String FIELD_89 = "F-89";
	String FIELD_90 = "F-90";
	String FIELD_91 = "F-91";
	String FIELD_92 = "F-92";
	String FIELD_93 = "F-93";
	String FIELD_94 = "F-94";
	String FIELD_95 = "F-95";
	String FIELD_96 = "F-96";
	String FIELD_97 = "F-97";
	String FIELD_98 = "F-98";
	String FIELD_99 = "F-99";
	String FIELD_100 = "F-100";
	String FIELD_101 = "F-101";
	String FIELD_102 = "F-102";
	String FIELD_103 = "F-103";
	String FIELD_104 = "F-104";
	String FIELD_105 = "F-105";
	String FIELD_106 = "F-106";
	String FIELD_107 = "F-107";
	String FIELD_108 = "F-108";
	String FIELD_109 = "F-109";
	String FIELD_110 = "F-110";
	String FIELD_111 = "F-111";
	String FIELD_112 = "F-112";
	String FIELD_113 = "F-113";
	String FIELD_114 = "F-114";
	String FIELD_115 = "F-115";
	String FIELD_116 = "F-116";
	String FIELD_117 = "F-117";
	String FIELD_118 = "F-118";
	String FIELD_119 = "F-119";
	String FIELD_120 = "F-120";
	String FIELD_121 = "F-121";
	String FIELD_122 = "F-122";
	String FIELD_123 = "F-123";
	String FIELD_124 = "F-124";
	String FIELD_125 = "F-125";
	String FIELD_126 = "F-126";
	String FIELD_127 = "F-127";
	String FIELD_128 = "F-128";
	String FIELD_129 = "F-129";
	String FIELD_130 = "F-130";
	String FIELD_131 = "F-131";
	String FIELD_132 = "F-132";
	String FIELD_133 = "F-133";
	String FIELD_134 = "F-134";
	String FIELD_135 = "F-135";
	String FIELD_136 = "F-136";
	String FIELD_137 = "F-137";
	String FIELD_138 = "F-138";
	String FIELD_139 = "F-139";
	String FIELD_140 = "F-140";
	String FIELD_141 = "F-141";
	String FIELD_142 = "F-142";
	String FIELD_143 = "F-143";
	String FIELD_144 = "F-144";
	String FIELD_145 = "F-145";
	String FIELD_146 = "F-146";
	String FIELD_147 = "F-147";
	String FIELD_148 = "F-148";
	String FIELD_149 = "F-149";
	String FIELD_150 = "F-150";
	String FIELD_151 = "F-151";
	String FIELD_152 = "F-152";
	String FIELD_153 = "F-153";
	String FIELD_154 = "F-154";
	String FIELD_155 = "F-155";
	String FIELD_156 = "F-156";
	String FIELD_157 = "F-157";
	String FIELD_158 = "F-158";
	String FIELD_159 = "F-159";
	String FIELD_160 = "F-160";
	String FIELD_161 = "F-161";
	String FIELD_162 = "F-162";
	String FIELD_163 = "F-163";
	String FIELD_164 = "F-164";
	String FIELD_165 = "F-165";
	String FIELD_166 = "F-166";
	String FIELD_167 = "F-167";
	String FIELD_168 = "F-168";
	String FIELD_169 = "F-169";
	String FIELD_170 = "F-170";
	String FIELD_171 = "F-171";
	String FIELD_172 = "F-172";
	String FIELD_173 = "F-173";
	String FIELD_174 = "F-174";
	String FIELD_175 = "F-175";
	String FIELD_176 = "F-176";
	String FIELD_177 = "F-177";
	String FIELD_178 = "F-178";
	String FIELD_179 = "F-179";
	String FIELD_180 = "F-180";
	String FIELD_181 = "F-181";
	String FIELD_182 = "F-182";
	String FIELD_183 = "F-183";
	String FIELD_184 = "F-184";
	String FIELD_185 = "F-185";
	String FIELD_186 = "F-186";
	String FIELD_187 = "F-187";
	String FIELD_188 = "F-188";
	String FIELD_189 = "F-189";
	String FIELD_190 = "F-190";
	String FIELD_191 = "F-191";
	String FIELD_192 = "F-192";

	String ORG_TRACE_NUMB = "ORG-F11";
	String ORG_TXN_DATE_TIME = "ORG-F7";
	String ORG_MSG_TYPE = "ORG-MSG-TYP";

	int STANDARD_HEADER_LENGTH = 22;
	int REJECT_HEADER_LENGTH = 26;

	// IH Exception codes

	String CARD_PRFIX_NOTFOUND = "001";
	String CURRENCY_CODE_MISMATCH = "002";
	String NOT_ACCEPTABLE_INTERCAHNGE = "003";
	String TXN_NOT_SUPPORTED = "004";
	String COFIGURATION_ERROE = "005";
	String INVALID_PAN_LENGTH = "006";
	String INVALID_CARD_NO = "007";

	String DB_DOWN_ERROR = "009";
	String SYSTEM_ERROR = "010";
	String MESSAGE_FORMAT_ERROR = "011";
	String OBJECT_NULL = "012";
	String INVALID_MSG_TYPE_ERROR = "013";
	String DO_NOT_HONOR = "124";
	String INVALID_TRANSACTION = "129";
	String NO_ACTION_TAKEN = "134";
	String UNABLE_TO_LOCATE_RECORD_IN_DB = "135";
	String TXN_NOT_PERMIT_CARDHOLDER = "145";
	String ALREADY_REVERSED = "154";
	String DESTINATION_UNAVAILABLE = "161";
	String SYSTEM_MALFUNCTION = "165";
	String CARD_AUTHENTICATION_FAILED = "174";

	// Field 39 response code //define it in DB
	String APPROVED_RESPONSE = "00";
	String RSPCODE_HISO_DOWN = "01";
	String RSPCODE_IH_DOWN = "02";
	String RSPCODE_TIME_OUT = "30";

	// Visa header constant parameters
	String H_LENGTH = "HDR-LNT";
	String H_FLAG_FORMAT = "HDR-FLAG-FORMAT";
	String H_TXT_FORMAT = "TXT-FORMAT";
	String H_TOT_MSG_LNT = "TOT-MSG-LNT";
	String H_DST_STAT_ID = "DST-STAT-ID";
	String H_SRC_STAT_ID = "SRC-STAT-ID";
	String H_RND_CON_INF = "RND-CON-INF";
	String H_BASE_1_FLAG = "BASE-1-FLAG";
	String H_MSG_STATUS_FLAG = "MSG-STATUS-FLAG";
	String H_BAT_NO = "BAT-NO";
	String H_RESERVED = "RESERVED";
	String H_USR_INFO = "USR-INFO";

	// VISA Reject codes

	// Header length
	String H1_INVALID = "0012";
	// Header flag and format
	String H2_INVALID = "0013";
	// Text format
	String H3_INVALID = "0015";
	// Total message length
	String H4_INVALID = "0016";
	// Destination station Id
	String H5_INVALID = "0003";
	// Source station Id
	String H6_INVALID = "0004";
	// Round trip control information
	String H7_INVALID = "0022";
	// Message status flag
	String H9_INVALID = "0147";
	// Batch number
	String H10_INVALID = "0030";
	// Reserver field
	String H11_INVALID = "0031";

	// Internal transaction code reference
	String BAL_INQUIRY = "BI";
	String WITHDRAWL = "WL";

	// Txn status
	String TXN_REVERSED = "R";

	String SOURCE_KEY = "SOURCE-KEY";

	String DEST_KEY = "DEST-KEY";

	String BOUND = "BOUND";

	String IN_BOUND_TYPE = "I";

	String SYS_RESP_CODE = "SYS-RSP";

	String OUT_BOUND_TYPE = "O";

	String SLAVE = "S";
	String MASTER = "M";
	String NONE = "N";

	String OUTPUT_LOGGER_NAME = "output";

	String AUDIT_LOGGER_NAME = "audit";

	String MESSAGE_LOGGER_NAME = "message";

	// Mail
	String MAIL_TO = "MAIL-TO";
	String MAIL_FROM = "MAIL-FROM";
	String MAIL_HOST = "MAIL-HOST";
	String MAIL_SUBJECT = "MAIL-SUBJECT";
	String MAIL_CONTENT = "MAIL-CONTENT";

	boolean TRACELOG = true;

	boolean DETAILEDLOG = true;

	String STAR = "*";

	String SUB441 = "S-44.1";
	String SUB442 = "S-44.2";
	String SUB443 = "S-44.3";
	String SUB444 = "S-44.4";
	String SUB445 = "S-44.5";
	String SUB446 = "S-44.6";
	String SUB447 = "S-44.7";
	String SUB448 = "S-44.8";
	String SUB449 = "S-44.9";
	String SUB4410 = "S-44.10";
	String SUB4411 = "S-44.11";
	String SUB4412 = "S-44.12";
	String SUB4413 = "S-44.13";
	String SUB4414 = "S-44.14";

	String SUB601 = "S-60.1";
	String SUB602 = "S-60.2";
	String SUB603 = "S-60.3";
	String SUB604 = "S-60.4";
	String SUB605 = "S-60.5";
	String SUB606 = "S-60.6";
	String SUB607 = "S-60.7";
	String SUB608 = "S-60.8";
	String SUB609 = "S-60.9";
	String SUB6010 = "S-60.10";

	String SUB621 = "S-62.1";
	String SUB622 = "S-62.2";
	String SUB623 = "S-62.3";
	String SUB624 = "S-62.4";
	String SUB625 = "S-62.5";
	String SUB626 = "S-62.6";
	String SUB627 = "S-62.7";
	String SUB628 = "S-62.8";
	String SUB629 = "S-62.9";
	String SUB6210 = "S-62.10";
	String SUB6211 = "S-62.11";
	String SUB6212 = "S-62.12";
	String SUB6213 = "S-62.13";
	String SUB6214 = "S-62.14";
	String SUB6215 = "S-62.15";
	String SUB6216 = "S-62.16";
	String SUB6217 = "S-62.17";
	String SUB6218 = "S-62.18";
	String SUB6219 = "S-62.19";
	String SUB6220 = "S-62.20";
	String SUB6221 = "S-62.21";
	String SUB6222 = "S-62.22";
	String SUB6223 = "S-62.23";
	String SUB6224 = "S-62.24";
	String SUB6225 = "S-62.25";
	String SUB6226 = "S-62.26";

	String SUB631 = "S-63.1";
	String SUB632 = "S-63.2";
	String SUB633 = "S-63.3";
	String SUB634 = "S-63.4";
	String SUB635 = "S-63.5";
	String SUB636 = "S-63.6";
	String SUB637 = "S-63.7";
	String SUB638 = "S-63.8";
	String SUB639 = "S-63.9";
	String SUB6310 = "S-63.10";
	String SUB6311 = "S-63.11";
	String SUB6312 = "S-63.12";
	String SUB6313 = "S-63.13";
	String SUB6314 = "S-63.14";
	String SUB6315 = "S-63.15";
	String SUB6316 = "S-63.16";
	String SUB6317 = "S-63.17";
	String SUB6318 = "S-63.18";
	String SUB6319 = "S-63.19";
	String SUB6320 = "S-63.20";
	String SUB6321 = "S-63.21";
	String SUB6322 = "S-63.22";

	String SUB1261 = "S-126.1";
	String SUB1262 = "S-126.2";
	String SUB1263 = "S-126.3";
	String SUB1264 = "S-126.4";
	String SUB1265 = "S-126.5";
	String SUB1266 = "S-126.6";
	String SUB1267 = "S-126.7";
	String SUB1268 = "S-126.8";
	String SUB1269 = "S-126.9";
	String SUB12610 = "S-126.10";
	String SUB12611 = "S-126.11";
	String SUB12612 = "S-126.12";
	String SUB12613 = "S-126.13";
	String SUB12614 = "S-126.14";
	String SUB12615 = "S-126.15";
	String SUB12616 = "S-126.16";
	String SUB12617 = "S-126.17";
	String SUB12618 = "S-126.18";
	String SUB12619 = "S-126.19";
	String SUB12620 = "S-126.20";
	// Added for VISA ends
}
