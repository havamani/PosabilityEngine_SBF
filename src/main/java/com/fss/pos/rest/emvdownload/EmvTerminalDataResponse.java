package com.fss.pos.rest.emvdownload;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class EmvTerminalDataResponse {
	private String ddolLength;
	private String terminalCountryCode;
	private String txnCurrCode;
	private String txnCurrExponent;
	private String trcc;
	private String tdolLength;
	private String terminalType;
	private String additionalTerminalCapabilities;
	private String ddol;
	private String terminalDataCount;
	private String terminalCapabilities;
	private String tdol;
	private String terminalProfileName;
	
	public String getDdolLength() {
		return ddolLength;
	}
	public void setDdolLength(String ddolLength) {
		this.ddolLength = ddolLength;
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
	public String getTrcc() {
		return trcc;
	}
	public void setTrcc(String trcc) {
		this.trcc = trcc;
	}
	public String getTdolLength() {
		return tdolLength;
	}
	public void setTdolLength(String tdolLength) {
		this.tdolLength = tdolLength;
	}
	public String getTerminalType() {
		return terminalType;
	}
	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}
	public String getAdditionalTerminalCapabilities() {
		return additionalTerminalCapabilities;
	}
	public void setAdditionalTerminalCapabilities(String additionalTerminalCapabilities) {
		this.additionalTerminalCapabilities = additionalTerminalCapabilities;
	}
	public String getDdol() {
		return ddol;
	}
	public void setDdol(String ddol) {
		this.ddol = ddol;
	}
	public String getTerminalDataCount() {
		return terminalDataCount;
	}
	public void setTerminalDataCount(String terminalDataCount) {
		this.terminalDataCount = terminalDataCount;
	}
	public String getTerminalCapabilities() {
		return terminalCapabilities;
	}
	public void setTerminalCapabilities(String terminalCapabilities) {
		this.terminalCapabilities = terminalCapabilities;
	}
	public String getTdol() {
		return tdol;
	}
	public void setTdol(String tdol) {
		this.tdol = tdol;
	}
	public String getTerminalProfileName() {
		return terminalProfileName;
	}
	public void setTerminalProfileName(String terminalProfileName) {
		this.terminalProfileName = terminalProfileName;
	}
	
}
