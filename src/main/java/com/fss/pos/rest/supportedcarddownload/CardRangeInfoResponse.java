package com.fss.pos.rest.supportedcarddownload;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardRangeInfoResponse {
	
	private String cardRangeId;
    private String cardName;
    private String binLow;
    private String binHigh;
    private String defaultAccType;
    private String panLength;
    private String enableServiceCode;
    private String serviceCode;
    private String enableMod10Val;
    private String emiEnabled;
    private String offlineFloorLmt;
    private String issuerId;
    
	public String getCardRangeId() {
		return cardRangeId;
	}
	public void setCardRangeId(String cardRangeId) {
		this.cardRangeId = cardRangeId;
	}
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public String getBinLow() {
		return binLow;
	}
	public void setBinLow(String binLow) {
		this.binLow = binLow;
	}
	public String getBinHigh() {
		return binHigh;
	}
	public void setBinHigh(String binHigh) {
		this.binHigh = binHigh;
	}
	public String getDefaultAccType() {
		return defaultAccType;
	}
	public void setDefaultAccType(String defaultAccType) {
		this.defaultAccType = defaultAccType;
	}
	public String getPanLength() {
		return panLength;
	}
	public void setPanLength(String panLength) {
		this.panLength = panLength;
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
	public String getOfflineFloorLmt() {
		return offlineFloorLmt;
	}
	public void setOfflineFloorLmt(String offlineFloorLmt) {
		this.offlineFloorLmt = offlineFloorLmt;
	}
	public String getIssuerId() {
		return issuerId;
	}
	public void setIssuerId(String issuerId) {
		this.issuerId = issuerId;
	}

}
