package com.fss.pos.host;

import com.fss.pos.base.commons.AbstractBeanData;

public class KeyImportResponse extends AbstractBeanData {

	private String respCode;
	private String zpk;
	private String checkDigit;

	public String getCheckDigit() {
		return checkDigit;
	}

	public void setCheckDigit(String checkDigit) {
		this.checkDigit = checkDigit;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public String getZpk() {
		return zpk;
	}

	public void setZpk(String zpk) {
		this.zpk = zpk;
	}

}
