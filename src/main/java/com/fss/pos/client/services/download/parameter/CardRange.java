package com.fss.pos.client.services.download.parameter;

public class CardRange {

	private String cardName;
	private String cardRangeId;
	private String panRangeLow;
	private String panRangeHigh;
	private String issuerId;
	private String panLength;
	private String enableServiceCode;
	private String debitKey;
	private String debitPINPad;
	private String serviceCode;
	private String enableMod10Val;
	private String emiEnabled;
	private String defaultAccType;
	private String offlineFloorLmt;

	public String getEnableMod10Val() {
		return enableMod10Val;
	}

	public void setEnableMod10Val(String enableMod10Val) {
		this.enableMod10Val = enableMod10Val;
	}

	public String getEmiEnabled() {
		return emiEnabled;
	}

	public void setEmiEnabled(String emiEnabled) {
		this.emiEnabled = emiEnabled;
	}

	public String getDefaultAccType() {
		return defaultAccType;
	}

	public void setDefaultAccType(String defaultAccType) {
		this.defaultAccType = defaultAccType;
	}

	public String getOfflineFloorLmt() {
		return offlineFloorLmt;
	}

	public void setOfflineFloorLmt(String offlineFloorLmt) {
		this.offlineFloorLmt = offlineFloorLmt;
	}

	public String getCardRangeId() {
		return cardRangeId;
	}

	public void setCardRangeId(String cardRangeId) {
		this.cardRangeId = cardRangeId;
	}

	public String getPanRangeLow() {
		return panRangeLow;
	}

	public void setPanRangeLow(String panRangeLow) {
		this.panRangeLow = panRangeLow;
	}

	public String getPanRangeHigh() {
		return panRangeHigh;
	}

	public void setPanRangeHigh(String panRangeHigh) {
		this.panRangeHigh = panRangeHigh;
	}

	public String getIssuerId() {
		return issuerId;
	}

	public void setIssuerId(String issuerId) {
		this.issuerId = issuerId;
	}

	public String getPanLength() {
		return panLength;
	}

	public void setPanLength(String panLength) {
		this.panLength = panLength;
	}

	public String getDebitKey() {
		return debitKey;
	}

	public void setDebitKey(String debitKey) {
		this.debitKey = debitKey;
	}

	public String getDebitPINPad() {
		return debitPINPad;
	}

	public void setDebitPINPad(String debitPINPad) {
		this.debitPINPad = debitPINPad;
	}

	public String getEnableServiceCode() {
		return enableServiceCode;
	}

	public void setEnableServiceCode(String enableServiceCode) {
		this.enableServiceCode = enableServiceCode;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

}
