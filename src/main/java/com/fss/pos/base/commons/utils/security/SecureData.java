package com.fss.pos.base.commons.utils.security;

public class SecureData {

	private String kekCode;
	private String dek;
	private byte[] dekBytes;
	private String encryptType;

	public String getKekCode() {
		return kekCode;
	}

	public void setKekCode(String kekCode) {
		this.kekCode = kekCode;
	}

	public String getDek() {
		return dek;
	}

	public void setDek(String dek) {
		this.dek = dek;
	}

	public byte[] getDekBytes() {
		return dekBytes;
	}

	public void setDekBytes(byte[] dekBytes) {
		this.dekBytes = dekBytes;
	}

	public String getEncryptType() {
		return encryptType;
	}

	public void setEncryptType(String encryptType) {
		this.encryptType = encryptType;
	}

}
