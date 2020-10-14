package com.fss.pos.base.commons;

import java.lang.invoke.MethodHandle;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fss.pos.base.api.db.DatabaseSchema;
import com.fss.pos.base.commons.utils.security.SecureData;

public class StaticStore {

	public static Map<String, Map<String, String>> hostMessageProtocols;
	public static Map<String, DatabaseSchema> schemaMap;
	public static Map<String, Integer[]> cardMaskConfig;
	public static Map<String, SecureData> deks;
	public static Map<String, String> kekCodes;
	public static Map<String, MethodHandle> setterMethodHandles;
	public static Map<String, Boolean> loggerConfig;
	public static Map<String, Map<String, String>> acquirerDetails;
	public static Map<String, Map<String, String>> fsscUrlDetails;
	public static Map<String, Map<String, String>> fsscSrcDetails;
	public static Map<String, Map<String,String>>  alterStationDetails;
	public static Map<String, Map<String,String>> alteracquirerDetails;
	static {
		hostMessageProtocols = new ConcurrentHashMap<String, Map<String, String>>();
		schemaMap = new ConcurrentHashMap<String, DatabaseSchema>();
		cardMaskConfig = new ConcurrentHashMap<String, Integer[]>();
		deks = new ConcurrentHashMap<String, SecureData>();
		kekCodes = new ConcurrentHashMap<String, String>();
		setterMethodHandles = new ConcurrentHashMap<String, MethodHandle>();
		loggerConfig = new ConcurrentHashMap<String, Boolean>();
		acquirerDetails = new ConcurrentHashMap<String, Map<String, String>>();
		fsscUrlDetails = new ConcurrentHashMap<String, Map<String, String>>();
		fsscSrcDetails = new ConcurrentHashMap<String, Map<String, String>>();
		alterStationDetails=new  ConcurrentHashMap<String, Map<String, String>>();
		alteracquirerDetails=new  ConcurrentHashMap<String, Map<String, String>>();
		
		
	}

}
