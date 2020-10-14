package com.fss.pos.client.services.download.parameter;

public class KeyData {

	private String keyNumber;
	private String keyIndex;
	private String keyLength;
	private String key;
	private String rid;
	private String keyExponent;
	private String keyHash;
	private String status;
	private String keyActiveDate;
	private String KeyExpiryDate;

	public String getKeyActiveDate() {
		return keyActiveDate;
	}

	public void setKeyActiveDate(String keyActiveDate) {
		this.keyActiveDate = keyActiveDate;
	}

	public String getKeyExpiryDate() {
		return KeyExpiryDate;
	}

	public void setKeyExpiryDate(String keyExpiryDate) {
		KeyExpiryDate = keyExpiryDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getKeyIndex() {
		return keyIndex;
	}

	public void setKeyIndex(String keyIndex) {
		this.keyIndex = keyIndex;
	}

	public String getKeyLength() {
		return keyLength;
	}

	public void setKeyLength(String keyLength) {
		this.keyLength = keyLength;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getKeyExponent() {
		return keyExponent;
	}

	public void setKeyExponent(String keyExponent) {
		this.keyExponent = keyExponent;
	}

	public String getKeyHash() {
		return keyHash;
	}

	public void setKeyHash(String keyHash) {
		this.keyHash = keyHash;
	}

	public String getKeyNumber() {
		return keyNumber;
	}

	public void setKeyNumber(String keyNumber) {
		this.keyNumber = keyNumber;
	}

}
