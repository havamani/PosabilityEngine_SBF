package com.fss.pos.base.commons;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@Configuration
public class Config {

	private static final String CONFIG_FILE_PATH;
	public static final String SECURITY_FILE_PATH;

	static {
		java.util.Properties _properties = new java.util.Properties();
		java.io.InputStream stream = (Config.class).getClassLoader()
				.getResourceAsStream("location.properties");
		try {
			_properties.load(stream);
		} catch (Exception e) {
			Log.trace("Error loading property file !!!");
			Log.error("Loading property file !!!", e);
		}
		if (stream == null)
			Log.trace("property file is not loaded");
		CONFIG_FILE_PATH = _properties.getProperty("configLocation");
		SECURITY_FILE_PATH = _properties.getProperty("securityLocation");
	}
	
	@Value("${DB_UNAME}")
	private String dbName;

	@Value("${DB_CODE}")
	private String dbCode;

	@Value("${DB_URL}")
	private String dbUrl;

	@Value("${DB_TYPE}")
	private String dbType;

	/*@Value("${FSSCONNECT_URL}")
	private String fssConnectUrl;

	@Value("${SOURCE_POS_EXTERNAL}")
	private String fssConnectSource;

	@Value("${ALTERNATE_SOURCE}")
	private String fssConnectAltSource;*/
	
	@Value("${ALTERNATE_SOURCE}")
	private String fssConnectAltSource;

	@Value("${CONNECT_TIMEOUT}")
	private String fssConnectTimeout;

	@Value("${READ_TIMEOUT}")
	private String fssConnectReadTimeout;

	@Value("${PPOS_DOWNLOAD_MAX_BYTES}")
	private String paramDownloadMaxBytes;

	@Value("${PATCH_UPDATE_MAX_BYTES}")
	private String patchUpdateMaxBytes;

	@Value("${HOST_TIMEOUT}")
	private String hostTimeout;

	@Value("${HOST_FIID}")
	private String hostFiid;

	@Value("${ENABLE_TRANSACTION_TIMEOUT}")
	private String timeoutEnabled;

	@Value("${POS_ALIASNAME}")
	private String aliasNamePrefix;

	@Value("${POS_KEY_STORE_LOCATION}")
	private String keyStoreLocation;

	@Value("${POS_KEY_STORE_TYPE}")
	private String keyStoreType;

	@Value("${POS_KEK_KEY_STORE_FILE_NAME}")
	private String keyStoreFileName;

	@Value("${CONN_TYPE}")
	private String masterDbConnectionType;

	@Value("${DB_JNDI_NAME}")
	private String masterJndi;

	@Value("${ENABLE_DEBUG_LOGGER}")
	private String debugLogger;

	@Value("${ENABLE_TIMEOUT_REVERSAL}")
	private String enableTimeoutReversal;
	
	/*@Value("${VISA_SOURCE_STATION}")
	private String visaSourceStation;*/
     
	@Value("${CUP_SOURCE}")
	private String cupSource;

	@Value("${CUP_DESTINATION}")
	private String cupDestination;
	
	@Value("${RSA_PUBLIC_LOCATION}")
	private String rsaPublic;
	
	
	public String getFssConnectAltSource() {
		return fssConnectAltSource;
	}

	public void setFssConnectAltSource(String fssConnectAltSource) {
		this.fssConnectAltSource = fssConnectAltSource;
	}
	
	public String getRsaPublic() {
		return rsaPublic;
	}

	public void setRsaPublic(String rsaPublic) {
		this.rsaPublic = rsaPublic;
	}

	public String getRsaPrivate() {
		return rsaPrivate;
	}

	public void setRsaPrivate(String rsaPrivate) {
		this.rsaPrivate = rsaPrivate;
	}

	@Value("${RSA_PRIVATE_LOCATION}")
	private String rsaPrivate;
	
	
	public String getCupSource() {
		return cupSource;
	}

	public void setCupSource(String cupSource) {
		this.cupSource = cupSource;
	}

	public String getCupDestination() {
		return cupDestination;
	}

	public void setCupDestination(String cupDestination) {
		this.cupDestination = cupDestination;
	}
	public String getDebugLogger() {
		return debugLogger;
	}

	public void setDebugLogger(String debugLogger) {
		this.debugLogger = debugLogger;
	}

	public String getMasterJndi() {
		return masterJndi;
	}

	public void setMasterJndi(String masterJndi) {
		this.masterJndi = masterJndi;
	}

	public String getKeyStoreFileName() {
		return keyStoreFileName;
	}

	public void setKeyStoreFileName(String keyStoreFileName) {
		this.keyStoreFileName = keyStoreFileName;
	}

	public String getKeyStoreType() {
		return keyStoreType;
	}

	public void setKeyStoreType(String keyStoreType) {
		this.keyStoreType = keyStoreType;
	}

	public String getTimeoutEnabled() {
		return timeoutEnabled;
	}

	public void setTimeoutEnabled(String timeoutEnabled) {
		this.timeoutEnabled = timeoutEnabled;
	}

	public String getHostFiid() {
		return hostFiid;
	}

	public void setHostFiid(String hostFiid) {
		this.hostFiid = hostFiid;
	}

	public String getPatchUpdateMaxBytes() {
		return patchUpdateMaxBytes;
	}

	public void setPatchUpdateMaxBytes(String patchUpdateMaxBytes) {
		this.patchUpdateMaxBytes = patchUpdateMaxBytes;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbCode() {
		return dbCode;
	}

	public void setDbCode(String dbCode) {
		this.dbCode = dbCode;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}


	public String getFssConnectTimeout() {
		return fssConnectTimeout;
	}

	public void setFssConnectTimeout(String fssConnectTimeout) {
		this.fssConnectTimeout = fssConnectTimeout;
	}

	public String getFssConnectReadTimeout() {
		return fssConnectReadTimeout;
	}

	public void setFssConnectReadTimeout(String fssConnectReadTimeout) {
		this.fssConnectReadTimeout = fssConnectReadTimeout;
	}

	public String getParamDownloadMaxBytes() {
		return paramDownloadMaxBytes;
	}

	public void setParamDownloadMaxBytes(String paramDownloadMaxBytes) {
		this.paramDownloadMaxBytes = paramDownloadMaxBytes;
	}

	public String getHostTimeout() {
		return hostTimeout;
	}

	public void setHostTimeout(String hostTimeout) {
		this.hostTimeout = hostTimeout;
	}

	public String getAliasNamePrefix() {
		return aliasNamePrefix;
	}

	public void setAliasNamePrefix(String aliasNamePrefix) {
		this.aliasNamePrefix = aliasNamePrefix;
	}

	public String getKeyStoreLocation() {
		return keyStoreLocation;
	}

	public void setKeyStoreLocation(String keyStoreLocation) {
		this.keyStoreLocation = keyStoreLocation;
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer p = new PropertySourcesPlaceholderConfigurer();
		final List<Resource> resourceLst = new ArrayList<Resource>();
		resourceLst.add(new FileSystemResource(CONFIG_FILE_PATH));
		p.setLocations(resourceLst.toArray(new Resource[] {}));
		return p;
	}

	public String getMasterDbConnectionType() {
		return masterDbConnectionType;
	}

	public void setMasterDbConnectionType(String masterDbConnectionType) {
		this.masterDbConnectionType = masterDbConnectionType;
	}

	public String getEnableTimeoutReversal() {
		return enableTimeoutReversal;
	}

	public void setEnableTimeoutReversal(String enableTimeoutReversal) {
		this.enableTimeoutReversal = enableTimeoutReversal;
	}

}
