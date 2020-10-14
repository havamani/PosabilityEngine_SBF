package com.fss.pos.base.api.hsm;

public class HsmResponse {

	private String rspCode;
	private String errCode;
	private String rspDesc;
	private String lmkEncryptedKey;
	private String parentEncryptedKey;
	private String checksum;
	private String pinBlock;

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getPinBlock() {
		return pinBlock;
	}

	public void setPinBlock(String pinBlock) {
		this.pinBlock = pinBlock;
	}

	public String getRspCode() {
		return rspCode;
	}

	public void setRspCode(String rspCode) {
		this.rspCode = rspCode;
	}

	public String getRspDesc() {
		return rspDesc;
	}

	public void setRspDesc(String rspDesc) {
		this.rspDesc = rspDesc;
	}

	public String getLmkEncryptedKey() {
		return lmkEncryptedKey;
	}

	public void setLmkEncryptedKey(String lmkEncryptedKey) {
		this.lmkEncryptedKey = lmkEncryptedKey;
	}

	public String getParentEncryptedKey() {
		return parentEncryptedKey;
	}

	public void setParentEncryptedKey(String parentEncryptedKey) {
		this.parentEncryptedKey = parentEncryptedKey;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

}
