package com.fss.pos.host;

import org.json.JSONException;
import org.json.JSONObject;

import com.fss.pos.base.commons.AbstractBeanData;

public class KeyImportRequest extends AbstractBeanData {

	private static final String MSP = "msp";
	private static final String DATE = "date";
	private static final String TIME = "time";
	private static final String HOST_ID = "hostId";
	private static final String HOST_STATION_NAME = "hostStationName";
	private static final String HOST_ALT_STATION_NAME = "hostAlternateStationName";
	private static final String HSM_ID = "hsmId";
	private static final String HSM_STATION_NAME = "hsmStationName";
	private static final String INDEX = "hsmAcquirerIndex";
	private static final String KEY_LENGTH = "hsmAcquirerKeyLength";
	private static final String ZMK_CHECKSUM = "zmkChecksum";

	private JSONObject json;

	@SuppressWarnings("unused")
	private KeyImportRequest() {

	}

	public KeyImportRequest(String request) throws JSONException {
		json = new JSONObject(request);
	}

	public String getMsp() throws JSONException {
		return json.getString(MSP);
	}

	public String getDate() throws JSONException {
		return json.getString(DATE);
	}

	public String getTime() throws JSONException {
		return json.getString(TIME);
	}

	public String getHostId() throws JSONException {
		return json.getString(HOST_ID);
	}

	public String getHostStationName() throws JSONException {
		return json.getString(HOST_STATION_NAME);
	}

	public String getHostAlternateStationName() throws JSONException {
		return json.getString(HOST_ALT_STATION_NAME);
	}

	public String getHsmId() throws JSONException {
		return json.getString(HSM_ID);
	}

	public String getHsmStationName() throws JSONException {
		return json.getString(HSM_STATION_NAME);
	}

	public String getHsmAcquirerIndex() throws JSONException {
		return json.getString(INDEX);
	}

	public String getKeyLength() throws JSONException {
		return json.getString(KEY_LENGTH);
	}

	public String getZmkCheckSum() throws JSONException {
		return json.getString(ZMK_CHECKSUM);
	}

}
