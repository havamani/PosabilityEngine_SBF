package com.fss.pos.base.commons;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.codec.binary.Base64;

import com.fss.pos.base.commons.constants.Constants;

/**
 * An object to hold the ISO fields.
 * 
 * @author Priyan
 */
public class IsoBuffer {

	/**
	 * Represents the empty field in ISO message
	 */
	private transient static final String DISABLED = "*";
	private transient static final String ENABLE_BITMAP = "1";
	private transient static final String DISABLE_BITMAP = "0";

	public static final String PREFIX_PRIMARY = "P-";
	public static final String PREFIX_SECONDARY = "S-";
	public static final String PREFIX_TERTIARY = "T-";

	private static final List<String> HIDDEN_FIELDS;

	static {
		HIDDEN_FIELDS = new ArrayList<String>();
		HIDDEN_FIELDS.add(Constants.DE2);
		HIDDEN_FIELDS.add(Constants.DE14);
		HIDDEN_FIELDS.add(Constants.DE35);
		HIDDEN_FIELDS.add(Constants.DE52);
		//HIDDEN_FIELDS.add(Constants.DE62);
	}

	/**
	 * The buffer to put message after parsing
	 */

	private Map<String, Object> buffer;

	/**
	 * The bitmap for this IsoBuffer
	 */
	private Map<String, String> bitmap;

	private String tranId;

	public String getTranId() {
		return tranId;
	}

	public void setTranId(String tranId) {
		this.tranId = tranId;
	}

	/**
	 * Instantiates the IsoBuffer
	 */
	public IsoBuffer() {
		buffer = new ConcurrentHashMap<String, Object>(250);
		bitmap = new LinkedHashMap<String, String>();
		for (int i = 1; i < 65; i++)
			disableBitmap(PREFIX_PRIMARY + i);
		for (int j = 65; j < 129; j++)
			disableBitmap(PREFIX_SECONDARY + j);
		for (int j = 129; j < 193; j++)
			disableBitmap(PREFIX_TERTIARY + j);
	}

	/**
	 * Instantiates the IsoBuffer with existing buffer
	 */
	public IsoBuffer(IsoBuffer isoBuffer) {
		buffer = new ConcurrentHashMap<String, Object>(isoBuffer.buffer);
		bitmap = new LinkedHashMap<String, String>(isoBuffer.bitmap);
		this.tranId = isoBuffer.tranId;
	}

	public IsoBuffer(Map<String, Object> map) {
		buffer = new ConcurrentHashMap<String, Object>(map);
		bitmap = new LinkedHashMap<String, String>();
	}

	/**
	 * To put in the buffer object
	 * 
	 * @param key
	 *            the key for the buffer value
	 * @param value
	 *            parsed message value
	 * @return old value
	 */
	public void put(String key, String value) {
		buffer.put(key, value);
		if (DISABLED.equals(value))
			disableBitmap(key);
		else
			enableBitmap(key);
	}

	/**
	 * To get from the buffer object
	 * 
	 * @param key
	 *            the key to identify the field data
	 * @return the value
	 */
	public String get(String key) {
		return (String) buffer.get(key);
	}

	/**
	 * To make the iso field unavailable
	 * 
	 * @param k
	 *            the field key
	 */
	public void disableField(String k) {
		buffer.put(k, DISABLED);
		disableBitmap(k);
	}

	/**
	 * To check whether the field is disabled
	 * 
	 * @param k
	 *            the field key
	 * @return returns true when the field is disabled
	 */
	public boolean isFieldEmpty(String k) {
		return DISABLED.equals(buffer.get(k)) ? true : false;
	}

	/**
	 * Get the field value using key, if the field is disabled it will return
	 * the default value.
	 * 
	 * @param key
	 *            the field key
	 * @param defaultValue
	 *            value to be returned when field is disabled
	 * @return the value
	 */
	public String get(String key, String defaultValue) {
		return (String) (this.isFieldEmpty(key) ? defaultValue : buffer
				.get(key));
	}

	/**
	 * Put int the buffer object only if the field is disabled.
	 * 
	 * @param k
	 *            the field key
	 * @param v
	 *            the value to be put
	 * @return the old value object
	 */
	public void putIfAbsent(String k, String v) {
		if (DISABLED.equals(buffer.get(k))) {
			buffer.put(k, v);
			enableBitmap(k);
		}
	}

	/**
	 * To get the buffer object from current buffer object.
	 * 
	 * @param k
	 *            the field key
	 * @return the buffer object for the key
	 */
	public IsoBuffer getBuffer(String k) {
		return (IsoBuffer) buffer.get(k);
	}

	/**
	 * 
	 * @param k
	 * @param isoBuffer
	 * @return
	 */
	public Object putBuffer(String k, IsoBuffer isoBuffer) {
		return buffer.put(k, isoBuffer);
	}

	/**
	 * The Keyset
	 * 
	 * @return keySet of Map
	 */
	public Set<String> keySet() {
		return buffer.keySet();
	}

	/**
	 * To fill the buffer with all disabled fields.
	 * 
	 * @param secondaryBits
	 * @param tertiaryBits
	 */
	public void fillBuffer(boolean primary, boolean secondaryBits,
			boolean tertiaryBits) {
		if (primary)
			fill(1, 64, PREFIX_PRIMARY);
		if (secondaryBits)
			fill(65, 128, PREFIX_SECONDARY);
		if (tertiaryBits)
			fill(129, 192, PREFIX_TERTIARY);
	}

	private void fill(int start, int end, String prefix) {
		end += 1;
		for (int j = start; j < end; j++) {
			String k = prefix + j;
			put(k, DISABLED);
			disableBitmap(k);
		}
	}

	/**
	 * Enable the bitmap for the field
	 * 
	 * @param key
	 */
	private void enableBitmap(String key) {
		bitmap.put(key, ENABLE_BITMAP);
	}

	/**
	 * Disable the bitmap for the field
	 * 
	 * @param key
	 */
	private void disableBitmap(String key) {
		bitmap.put(key, DISABLE_BITMAP);
	}

	/**
	 * Returns the bitmap of the current ISO object
	 * 
	 * @param secondary
	 * @param tertiary
	 * @return bitmap {@link String}
	 */
	public String getBitmap(boolean secondary, boolean tertiary) {
		StringBuilder sb = new StringBuilder();
		for (int j = 1; j < 65; j++)
			sb.append(bitmap.get(PREFIX_PRIMARY + j));
		if (secondary)
			for (int j = 65; j < 129; j++)
				sb.append(bitmap.get(PREFIX_SECONDARY + j));
		if (tertiary)
			for (int j = 129; j < 193; j++)
				sb.append(bitmap.get(PREFIX_TERTIARY + j));
		return sb.toString();
	}

	/**
	 * TO check the field has sub fields
	 * 
	 * @param key
	 *            the key
	 * @return true if the de has sub fields
	 */
	public boolean hasSubFields(String key) {
		return buffer.get(key) instanceof IsoBuffer;
	}

	/**
	 * Returns true if all fields are disabled in this object
	 * 
	 * @param secondary
	 * @param tertiary
	 * @return boolean
	 */
	public boolean isAllFieldsEmpty(boolean secondary, boolean tertiary) {
		return !getBitmap(secondary, tertiary).contains("1");
	}

	public Map<String, Object> getMap() {
		return buffer;
	}

	public String logData() {
		Map<String, Object> m = new HashMap<String, Object>();
		for (String k : buffer.keySet()) {
			Object v = buffer.get(k);
			if (!DISABLED.equals(v)){
				m.put(k, HIDDEN_FIELDS.contains(k) ? "XXXXX" : v);
				if(k.equals(Constants.DE55)){
					m.put(k, Base64.encodeBase64String(((String) v)
							.getBytes(StandardCharsets.ISO_8859_1)));
				}
				if(k.equals(Constants.DE62)){
					if(v.toString().contains(",")){
						Map<String, String> m1 = new HashMap<String, String>();
						String[] pairs = v.toString().split(", ");
						for (String pair : pairs) {
							String[] keyValue = pair.split("=");
							m1.put(keyValue[0], keyValue[1]);
						}
						if(m1.containsKey(Constants.DE2)){
							m1.put(Constants.DE2, "XXXXX");
						}
						m.put(k, m1);
					}
					
				}
			}
		}
		m = sortByKeys(m);
		return m.toString();
	}



	private Map<String, Object> sortByKeys(Map<String, Object> m) {
		
		List<String> keys = new ArrayList<String>(m.keySet());
		
		Map<String, Object> linkedMap = new LinkedHashMap<String, Object>();
		List<Integer> l1 = new ArrayList<Integer>();
		for(String key : keys){
			if(key.substring(0, 2).equals(IsoBuffer.PREFIX_PRIMARY)){
				int k1 = Integer.parseInt(key.substring(2));
				l1.add(k1);
			}
			
		}
		Object[] array = l1.toArray();
		Arrays.sort(array);
		for(int i=0;i < array.length ; i++){
			linkedMap.put(IsoBuffer.PREFIX_PRIMARY + array[i], m.get(IsoBuffer.PREFIX_PRIMARY + array[i]));
		}
		l1 = new ArrayList<Integer>();
		for(String key : keys){
			if(key.substring(0, 2).equals(IsoBuffer.PREFIX_SECONDARY)){
				int k1 = Integer.parseInt(key.substring(2));
				l1.add(k1);
			}
			
		}
		Object[] array1 = l1.toArray();
		Arrays.sort(array1);
		for(int i=0;i < array1.length ; i++){
			linkedMap.put(IsoBuffer.PREFIX_SECONDARY + array1[i], m.get(IsoBuffer.PREFIX_SECONDARY + array1[i]));
		}
		
		linkedMap.put(Constants.ISO_MSG_TYPE, m.get(Constants.ISO_MSG_TYPE));
		return linkedMap;
	}

	public Map<String, Object> getSecuredMap() {
		TreeMap<String, Object> m = new TreeMap<String, Object>();
		for (String k : buffer.keySet()) {
			Object v = buffer.get(k);
			if (!DISABLED.equals(v))
				m.put(k, v);
		}
		return m;
	}

	@Override
	public String toString() {
		return getSecuredMap().toString();
	}

	public Object getObject(String key) {
		return buffer.get(key);
	}
}
