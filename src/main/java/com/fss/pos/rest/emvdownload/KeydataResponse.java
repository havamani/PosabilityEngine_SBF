package com.fss.pos.rest.emvdownload;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class KeydataResponse {
	
	private String keyActiveDate;
	private String keyHash;
	private String keyLength;
	private String keyExpiryDate;
	private String keyIndex;
	private String keyNumber;
	private String keyExponent;
	private String rid;
	private String key;
	private String status;
	
	public String getKeyActiveDate() {
		return keyActiveDate;
	}
	public void setKeyActiveDate(String keyActiveDate) {
		this.keyActiveDate = keyActiveDate;
	}
	public String getKeyHash() {
		return keyHash;
	}
	public void setKeyHash(String keyHash) {
		this.keyHash = keyHash;
	}
	public String getKeyLength() {
		return keyLength;
	}
	public void setKeyLength(String keyLength) {
		this.keyLength = keyLength;
	}
	public String getKeyExpiryDate() {
		return keyExpiryDate;
	}
	public void setKeyExpiryDate(String keyExpiryDate) {
		this.keyExpiryDate = keyExpiryDate;
	}
	public String getKeyIndex() {
		return keyIndex;
	}
	public void setKeyIndex(String keyIndex) {
		this.keyIndex = keyIndex;
	}
	public String getKeyNumber() {
		return keyNumber;
	}
	public void setKeyNumber(String keyNumber) {
		this.keyNumber = keyNumber;
	}
	public String getKeyExponent() {
		return keyExponent;
	}
	public void setKeyExponent(String keyExponent) {
		this.keyExponent = keyExponent;
	}
	public String getRid() {
		return rid;
	}
	public void setRid(String rid) {
		this.rid = rid;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
 
}
