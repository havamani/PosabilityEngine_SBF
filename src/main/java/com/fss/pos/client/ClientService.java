package com.fss.pos.client;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import com.fss.pos.base.api.db.DatabaseSchema;
import com.fss.pos.base.api.db.storedprocedure.ResponseStatus;
import com.fss.pos.base.api.db.storedprocedure.StoredProcedureInfo;
import com.fss.pos.base.commons.CardMaskConfig;
import com.fss.pos.base.commons.Config;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.StaticStore;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.security.SecureData;
import com.fss.pos.base.commons.utils.security.SecurityService;
import com.fss.pos.base.factory.ApiFactory;
import com.fss.pos.base.services.fssconnect.FssConnect;
import com.fss.pos.host.AcquirerConfig;
import com.fss.pos.host.FsscDetails;

@Service
public class ClientService {

	private static final String WARMBOOT_ID = "WARMBOOTID001";
	private static final String WARMBOOT_ACQ = "ACQUIRER";
	private static final String WARMBOOT_MSP = "MSP";
	private static final String WARMBOOT_DEK = "DEK";
	private static final String WARMBOOT_SECURITY = "SECURITYCONFIG";
	private static final String WARMBOOT_KEK_CODES = "KEKCODES";
	private static final String WARMBOOT_All = "All";

	private static final String WARMBOOT_JSON_UPDATE = "updateConfig";
	private static final String WARMBOOT_JSON_ID = "id";

	@Autowired
	private ApiFactory apiFactory;

	@Autowired
	private ConfigurableApplicationContext appContext;

	@Autowired
	private SecurityService securityUtils;

	@Autowired
	private Config config;
	
	/*@Autowired
	private GeneratePublicPrivateKeys generatePublicPrivateKeys;*/
	
	/*public boolean loadRSAKeys(){
		boolean b = false;
		try {
			b = generatePublicPrivateKeys.generatePublicAndPrivateKey();
		} catch (Exception e) {
			Log.trace("No Keys are Generated !!!!!!!!!!");
		}
		return b;
	}*/

	@SuppressWarnings("unchecked")
	public boolean loadSchemas() {
		Log.trace("Initializing all schema");
		try {
			List<Class<?>> classList = new ArrayList<Class<?>>();
			classList.add(ResponseStatus.class);
			classList.add(DatabaseSchema.class);
			List<Object> objList = apiFactory.getStoredProcedureApi().getBean(
					null, StoredProcedureInfo.MSP_CONFIG, classList,
					Constants.MASTER_INSTID);
			if (Constants.SUCCESS
					.equals(((List<ResponseStatus>) objList.get(0)).get(0)
							.getStatus())) {
				List<DatabaseSchema> dbSchemaList = (List<DatabaseSchema>) objList
						.get(1);
				for (DatabaseSchema ds : dbSchemaList)
					StaticStore.schemaMap.put(ds.getMspAcr(), ds);
				
			}
			Log.info("Schemas loaded ", StaticStore.schemaMap.keySet()
					.toString());
			loadDeks();
			loadKekCodes();
			if (StaticStore.schemaMap.isEmpty())
				Log.trace("No Schema available !!!!!!!!!!");
			return true;
		} catch (Exception e) {
			Log.error("In loadSchemas ", e);
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean loadDeks() {
		Log.trace("Loading deks");
		try {
			List<Class<?>> classList = new ArrayList<Class<?>>();
			classList.add(SecureData.class);
			for (String msp : StaticStore.schemaMap.keySet()) {
				try {
					List<Object> objList = apiFactory.getStoredProcedureApi()
							.getBean(null, StoredProcedureInfo.LOAD_DEKS,
									classList, msp);
					List<SecureData> sdList = (List<SecureData>) objList.get(0);
					SecureData sd = sdList.get(0);
					sd.setDekBytes(securityUtils.getDekBytes(sd.getDek(),sd.getEncryptType()));
					StaticStore.deks.put(msp, sd);
				} catch (Exception e) {
					Log.error("loadDeks for msp " + msp, e);
				}
			}
			Log.debug("Deks", StaticStore.deks.toString());
			return true;
		} catch (Exception e) {
			Log.error("In loadDeks ", e);
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean loadcardMaskingConfig() {
		Log.trace("Loading cardMaskingConfig");
		try {
			List<Class<?>> classList = new ArrayList<Class<?>>();
			classList.add(CardMaskConfig.class);
			for (String msp : StaticStore.schemaMap.keySet()) {
				try {
					List<Object> objList = apiFactory.getStoredProcedureApi()
							.getBean(null,
									StoredProcedureInfo.LOAD_CARD_MASK_CONFIG,
									classList, msp);
					List<CardMaskConfig> list = (List<CardMaskConfig>) objList
							.get(0);
					CardMaskConfig cm = list.get(0);
					Integer[] maskArr = new Integer[2];
					maskArr[0] = Integer.parseInt(cm.getCardMaskFormat()
							.substring(0, 1));
					maskArr[1] = Integer.parseInt(cm.getCardMaskFormat().substring(1, 2));
					StaticStore.cardMaskConfig.put(msp, maskArr);
				} catch (Exception e) {
					Log.error("cardMaskingConfig for msp " + msp, e);
				}
			}
			Log.debug("Card Masking", StaticStore.cardMaskConfig.toString());
			return true;
		} catch (Exception e) {
			Log.error("In loadDeks ", e);
			return false;
		}
	}

	public void loadKekCodes() {
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream(Config.SECURITY_FILE_PATH));
			for (Object k : prop.keySet()) {
				StaticStore.kekCodes.put((String) k,
						prop.getProperty((String) k));
				Log.debug("loaded kek coded", k.toString());
			}
		} catch (Exception e) {
			Log.error("In loadKekCodes ", e);
		}
	}

	@SuppressWarnings("unchecked")
	public boolean loadAcquirers() {
		Log.trace("Loading Host Message Protocols");
		try {
			List<Class<?>> classList = new ArrayList<Class<?>>();
			classList.add(AcquirerConfig.class);
			classList.add(FsscDetails.class);
			for (String msp : StaticStore.schemaMap.keySet()) {
				try {
					List<Object> objList = apiFactory
							.getStoredProcedureApi()
							.getBean(
									null,
									StoredProcedureInfo.LOAD_ACQUIRER_PROTOCOLS,
									classList, msp);
					List<AcquirerConfig> hmpList = (List<AcquirerConfig>) objList
							.get(0);
					List<FsscDetails> hsmList = (List<FsscDetails>) objList
							.get(1);
					Map<String, String> m = new ConcurrentHashMap<String, String>();
					Map<String, String> n = new ConcurrentHashMap<String, String>();
					Map<String, String> o = new ConcurrentHashMap<String, String>();
					Map<String, String> p = new ConcurrentHashMap<String, String>();
					Map<String, String> r = new ConcurrentHashMap<String, String>();
					Map<String, String> s = new ConcurrentHashMap<String, String>();

					for (AcquirerConfig hmp : hmpList) {
						m.put(hmp.getStationName(), hmp.getMsgProtocol());
						n.put(hmp.getStationName(), hmp.getAcqId());
						if(hmp.getAlternateStation()!=null)
						{
						r.put(hmp.getAlternateStation(),hmp.getMsgProtocol());
						s.put(hmp.getAlternateStation(),hmp.getAcqId());
						}
					}
					for (FsscDetails hsm : hsmList) {
						o.put(hsm.getMspAcr(), hsm.getFsscSource());
						p.put(hsm.getMspAcr(), hsm.getFsscUrl());
					}
					Log.debug("Host Message Protocol for msp : " + msp,
							m.toString());
					Log.debug("Host Acquirer Id for msp : " + msp, n.toString());
					Log.debug("fssc src Id for msp : " + msp, o.toString());
					Log.debug("fssc url for msp : " + msp, p.toString());

					StaticStore.hostMessageProtocols.put(msp, m);
					StaticStore.acquirerDetails.put(msp, n);
					StaticStore.fsscSrcDetails.put(msp, o);
					StaticStore.fsscUrlDetails.put(msp, p);
					StaticStore.alterStationDetails.put(msp, r);
					StaticStore.alteracquirerDetails.put(msp, s);
					
 
				} catch (Exception e) {
					Log.error("loading host config in msp " + msp, e);
				}
			}
			return true;
		} catch (Exception e) {
			Log.error("Error loading host msg protocols ", e);
			return false;
		}
	}

	public boolean loadLoggerConfig() {
		Log.trace("Loading Logger config");
		try {
			for (String msp : StaticStore.schemaMap.keySet()) {
				try {
					String resp = apiFactory.getStoredProcedureApi().getString(
							null, StoredProcedureInfo.LOAD_LOGGER_CONFIG, msp);
					Log.debug("Debug config for msp : " + msp, resp);
					StaticStore.loggerConfig.put(msp,
							Constants.ENABLE.equals(resp));
				} catch (Exception e) {
					Log.error("loading host config in msp " + msp, e);
					// Log.info("Debug enabled in config file", msp);
					// StaticStore.loggerConfig.put(msp,
					// Constants.FLAG_YES.equals(config.getDebugLogger()));
				}
			}
			return true;
		} catch (Exception e) {
			Log.error("Error loading logger config", e);
			return false;
		}
	}

	public void updateConfig(FssConnect fssc)
			throws UnsupportedEncodingException, JSONException {

		JSONObject json = new JSONObject(fssc.getMessage());
		String configType = json.getString(WARMBOOT_JSON_UPDATE);

		if (WARMBOOT_ID.equals(json.getString(WARMBOOT_JSON_ID))) {
			if (WARMBOOT_ACQ.equals(configType))
				loadAcquirers();
			else if (WARMBOOT_MSP.equals(configType))
				loadSchemas();
			else if (WARMBOOT_DEK.equals(configType)) {
				loadKekCodes();
				loadDeks();
			} else if (WARMBOOT_SECURITY.equals(configType)) {
				loadLoggerConfig();
				loadcardMaskingConfig();
			} else if (WARMBOOT_KEK_CODES.equals(configType))
				loadKekCodes();
			else if (WARMBOOT_All.equals(configType))
				appContext.refresh();
		} else {
			Log.trace("Invalid Id");
		}

	}

}
