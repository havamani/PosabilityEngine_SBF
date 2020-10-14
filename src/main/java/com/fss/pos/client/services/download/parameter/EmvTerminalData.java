package com.fss.pos.client.services.download.parameter;

public class EmvTerminalData {
	private String terminalDataCount;
	private String terminalProfileName;
	private String terminalType;
	private String terminalCapabilities;
	private String additionalTerminalCapabilities;
	private String ddol;
	private String tdol;
	private String ddolLength;
	private String tdolLength;
	private String terminalCountryCode;
	private String txnCurrCode;
	private String txnCurrExponent;
	private String trcc;
	public String getTrcc() {
		return trcc;
	}

	public void setTrcc(String trcc) {
		this.trcc = trcc;
	}

	public String getTerminalType() {
		return terminalType;
	}

	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}

	public String getTdol() {
		return tdol;
	}

	public void setTdol(String tdol) {
		this.tdol = tdol;
	}

	public String getDdol() {
		return ddol;
	}

	public void setDdol(String ddol) {
		this.ddol = ddol;
	}

	public String getTerminalCapabilities() {
		return terminalCapabilities;
	}

	public void setTerminalCapabilities(String terminalCapabilities) {
		this.terminalCapabilities = terminalCapabilities;
	}

	public String getAdditionalTerminalCapabilities() {
		return additionalTerminalCapabilities;
	}

	public void setAdditionalTerminalCapabilities(
			String additionalTerminalCapabilities) {
		this.additionalTerminalCapabilities = additionalTerminalCapabilities;
	}

	public String getTerminalProfileName() {
		return terminalProfileName;
	}

	public void setTerminalProfileName(String terminalProfileName) {
		this.terminalProfileName = terminalProfileName;
	}

	public String getTerminalDataCount() {
		return terminalDataCount;
	}

	public void setTerminalDataCount(String terminalDataCount) {
		this.terminalDataCount = terminalDataCount;
	}

	public String getDdolLength() {
		return ddolLength;
	}

	public void setDdolLength(String ddolLength) {
		this.ddolLength = ddolLength;
	}

	public String getTdolLength() {
		return tdolLength;
	}

	public void setTdolLength(String tdolLength) {
		this.tdolLength = tdolLength;
	}

	public String getTerminalCountryCode() {
		return terminalCountryCode;
	}

	public void setTerminalCountryCode(String terminalCountryCode) {
		this.terminalCountryCode = terminalCountryCode;
	}

	public String getTxnCurrCode() {
		return txnCurrCode;
	}

	public void setTxnCurrCode(String txnCurrCode) {
		this.txnCurrCode = txnCurrCode;
	}

	public String getTxnCurrExponent() {
		return txnCurrExponent;
	}

	public void setTxnCurrExponent(String txnCurrExponent) {
		this.txnCurrExponent = txnCurrExponent;
	}

}
