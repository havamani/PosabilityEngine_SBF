package com.fss.pos.base.services.fssconnect;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * To serialize and deserialize the Fssconnect json message.
 * 
 * @author Priyan
 * @see JSONObject
 */
public class FssConnect extends JSONObject {

	private static final String FSSC_SOURCE = "source";
	private static final String FSSC_DESTINATION = "destination";
	private static final String FSSC_MESSAGE = "message";
	private static final String FSSC_UNIQUE_ID = "uniqueId";
	private static final String FSSC_TIMESTAMP = "timestamp";
	private static final String FSSC_ALT_SRC_ARRAY = "alternateSrcArray";
	private static final String FSSC_ALT_DEST_ARRAY = "alternateDestArray";
	private static final String FSSC_IIN = "iin";
	private static final String FSSC_RSP_REQUIRED = "responseRequired";
	private static final String FSSC_RSP_CODE = "respCode";
	private static final String FSSC_RSP_DESC = "respDescription";
	private static final String FSSC_STATION_NAME = "stationName";
	private static final String FSSC_STATUS = "status";
	private static final String FSSC_NOTIFY_DEST_ARRAY = "notifyDestArray";
	private static final String FSSC_MSG_ID = "msgId";
	

	public static final String RESPONSE_REQUIRED = "1";
	public static final String RESPONSE_NOT_REQUIRED = "0";

	/**
	 * Character set to be used for message encoding and decoding
	 */
	private Charset charset;

	private Object syncObject;
	
	private String plainPrivateKey;

	/**
	 * Just initialize
	 */
	public FssConnect() {
		super();
	}

	/**
	 * initialize with character set
	 * 
	 * @param charset
	 *            the character set to encode/decode
	 */
	public FssConnect(Charset charset) {
		super();
		this.charset = charset;
	}

	/**
	 * @param jsonObject
	 *            initialize using the base
	 * @throws JSONException
	 *             thrown when
	 */
	public FssConnect(JSONObject jsonObject) throws JSONException {
		super(jsonObject);
	}

	/**
	 * @param jsonString
	 * @throws JSONException
	 */
	public FssConnect(String jsonString) throws JSONException {
		super(jsonString);
	}

	/**
	 * @param jsonString
	 * @param charset
	 * @throws JSONException
	 */
	public FssConnect(String jsonString, Charset charset) throws JSONException {
		super(jsonString);
		this.charset = charset;
	}

	/**
	 * @return the source station name
	 * @throws JSONException
	 *             thrown when not available
	 */
	public String getSource() throws JSONException {
		return this.getString(FSSC_SOURCE);
	}

	/**
	 * @return the destination station name
	 * @throws JSONException
	 *             thrown when not available
	 */
	public String getDestination() throws JSONException {
		return this.getString(FSSC_DESTINATION);
	}

	/**
	 * @return the Base64 decoded message
	 * @throws JSONException
	 *             thrown when not available
	 * @throws UnsupportedEncodingException
	 *             thrown when unrecognized charset
	 */
	public String getMessage() throws JSONException,
			UnsupportedEncodingException {
		byte[] b;
		if (charset == null)
			b = this.getString(FSSC_MESSAGE).getBytes();
		else
			b = this.getString(FSSC_MESSAGE).getBytes(charset);
		return charset == null ? new String(Base64.decodeBase64(b))
				: new String(Base64.decodeBase64(b), charset);
	}

	/**
	 * @return unique id in message
	 * @throws JSONException
	 *             thrown when not available
	 */
	public String getUniqueId() throws JSONException {
		return this.getString(FSSC_UNIQUE_ID);
	}

	/**
	 * @return the timestamp in the message
	 * @throws JSONException
	 *             thrown when not available
	 */
	public String getTimeStamp() throws JSONException {
		return this.getString(FSSC_TIMESTAMP);
	}

	/**
	 * @return array of alternate source stations
	 * @throws JSONException
	 *             thrown when not available
	 */
	public String[] getAlternateSourceArray() throws JSONException {
		return (String[]) get(FSSC_ALT_SRC_ARRAY);
	}

	/**
	 * @return array of alternate destination stations
	 * @throws JSONException
	 *             thrown when not available
	 */
	public String[] getAlternateDestinationArray() throws JSONException {
		return (String[]) get(FSSC_ALT_DEST_ARRAY);
	}

	/**
	 * @return iin in the message
	 * @throws JSONException
	 *             thrown when not available
	 */
	public String getIIN() throws JSONException {
		return this.getString(FSSC_IIN);
	}

	/**
	 * @return the response required in message. 0 or 1
	 * @throws JSONException
	 *             thrown when not available
	 */
	public String getResponseRequired() throws JSONException {
		return this.getString(FSSC_RSP_REQUIRED);
	}

	/**
	 * @return response code from fss connect
	 * @throws JSONException
	 *             thrown when not available
	 */
	public String getResponseCode() throws JSONException {
		return this.getString(FSSC_RSP_CODE);
	}

	/**
	 * @return response description from fss connect
	 * @throws JSONException
	 *             thrown when not available
	 */
	public String getResponseDescription() throws JSONException {
		return this.getString(FSSC_RSP_DESC);
	}

	/**
	 * @return the station name in message
	 * @throws JSONException
	 *             thrown when not available
	 */
	public String getStationName() throws JSONException {
		return this.getString(FSSC_STATION_NAME);
	}

	/**
	 * @return status in the message
	 * @throws JSONException
	 *             thrown when not available
	 */
	public String getStatus() throws JSONException {
		return this.getString(FSSC_STATUS);
	}

	/**
	 * @param source
	 *            the source station where message is initiated
	 * @throws JSONException
	 *             thrown when not available
	 */
	public void setSource(String source) throws JSONException {
		this.put(FSSC_SOURCE, source);
	}

	/**
	 * @param dest
	 *            the destination station name need to be sent
	 * @throws JSONException
	 *             thrown when not available
	 */
	public void setDestination(String dest) throws JSONException {
		this.put(FSSC_DESTINATION, dest);
	}

	public void setMessage(String msg) throws JSONException,
			UnsupportedEncodingException {
		byte[] b;
		if (charset == null)
			b = msg.getBytes();
		else
			b = msg.getBytes(charset);
		this.put(FSSC_MESSAGE,
				charset == null ? new String(Base64.encodeBase64(b))
						: new String(Base64.encodeBase64(b), charset));
	}

	public void setUniqueId(String uniqueId) throws JSONException {
		this.put(FSSC_UNIQUE_ID, uniqueId);
	}

	public void setTimeStamp(String timestamp) throws JSONException {
		this.put(FSSC_TIMESTAMP, timestamp);
	}

	public void setAlternateSourceArray(String[] altSrcArray)
			throws JSONException {
		this.put(FSSC_ALT_SRC_ARRAY, altSrcArray);
	}

	public void setAlternateDestinationArray(String[] altDestArray)
			throws JSONException {
		this.put(FSSC_ALT_DEST_ARRAY, altDestArray);
	}

	public void setIIN(String iin) throws JSONException {
		this.put(FSSC_IIN, iin);
	}

	public void setResponseRequired(String respRequired) throws JSONException {
		this.put(FSSC_RSP_REQUIRED, respRequired);
	}

	public void setResponseCode(String respCode) throws JSONException {
		this.put(FSSC_RSP_CODE, respCode);
	}

	public void setResponseDescription(String respDesc) throws JSONException {
		this.put(FSSC_RSP_DESC, respDesc);
	}

	public void setStationName(String stationName) throws JSONException {
		this.put(FSSC_STATION_NAME, stationName);
	}

	public void setStatus(String status) throws JSONException {
		this.put(FSSC_STATUS, status);
	}

	@Override
	public JSONObject put(String key, Object value) throws JSONException {
		return super.put(key, value == null ? "" : value);
	}

	public Object getSyncObject() {
		return syncObject;
	}

	public void setSyncObject(Object syncObject) {
		this.syncObject = syncObject;
	}

	public void setMsgId(String msgId) throws JSONException {
		this.put(FSSC_MSG_ID, msgId);
	}

	public String getMsgId() throws JSONException {
		return this.getString(FSSC_MSG_ID);
	}

	public String[] getNotifyDestArray() throws JSONException {
		return (String[]) this.get(FSSC_NOTIFY_DEST_ARRAY);
	}

	public void setNotifyDestArray(String[] notifyDestArray)
			throws JSONException {
		this.put(FSSC_NOTIFY_DEST_ARRAY, notifyDestArray);
	}

	public String getPlainPrivateKey() {
		return plainPrivateKey;
	}

	public void setPlainPrivateKey(String plainPrivateKey) {
		this.plainPrivateKey = plainPrivateKey;
	}

}
