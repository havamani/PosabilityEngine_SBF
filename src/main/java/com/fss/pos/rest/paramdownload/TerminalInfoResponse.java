package com.fss.pos.rest.paramdownload;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TerminalInfoResponse {
	private String merchantId;
    private String storeId;
    private String terminalId;
    private String storeName;
    private String address;
    private String city;
    private String pinCode;
    private String state;
    private String country;
    private String terminalType;
    private String batchNo;
    private String nextBatchNo;
    private String busStartTime;
    private String busEndTime;
    private String SettleTime;
    private String merchantCategoryCode;
    private String receiptLine1;
    private String receiptLine2;
    private String receiptLine3;
    private String helpDeskPhone;
    private String languageIndicator;
    private String defaultTxn;
    private String adminCode;
    private String supervisorCode;
    private String settlementCurrencyDigits;
    private String merchantDateTime;
    private String enableEMI;
    private String amexMerchantId;
    private String currencyName;
    private String currencyCode;
    private String currencySymbol;
    private String currencyDigits;
    private String currencyDecimal;
    private String countryCode;
    private String businessDateFormat;
    
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getTerminalId() {
		return terminalId;
	}
	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getPinCode() {
		return pinCode;
	}
	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getTerminalType() {
		return terminalType;
	}
	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getNextBatchNo() {
		return nextBatchNo;
	}
	public void setNextBatchNo(String nextBatchNo) {
		this.nextBatchNo = nextBatchNo;
	}
	public String getBusStartTime() {
		return busStartTime;
	}
	public void setBusStartTime(String busStartTime) {
		this.busStartTime = busStartTime;
	}
	public String getBusEndTime() {
		return busEndTime;
	}
	public void setBusEndTime(String busEndTime) {
		this.busEndTime = busEndTime;
	}
	public String getSettleTime() {
		return SettleTime;
	}
	public void setSettleTime(String settleTime) {
		SettleTime = settleTime;
	}
	public String getMerchantCategoryCode() {
		return merchantCategoryCode;
	}
	public void setMerchantCategoryCode(String merchantCategoryCode) {
		this.merchantCategoryCode = merchantCategoryCode;
	}
	public String getReceiptLine1() {
		return receiptLine1;
	}
	public void setReceiptLine1(String receiptLine1) {
		this.receiptLine1 = receiptLine1;
	}
	public String getReceiptLine2() {
		return receiptLine2;
	}
	public void setReceiptLine2(String receiptLine2) {
		this.receiptLine2 = receiptLine2;
	}
	public String getReceiptLine3() {
		return receiptLine3;
	}
	public void setReceiptLine3(String receiptLine3) {
		this.receiptLine3 = receiptLine3;
	}
	public String getHelpDeskPhone() {
		return helpDeskPhone;
	}
	public void setHelpDeskPhone(String helpDeskPhone) {
		this.helpDeskPhone = helpDeskPhone;
	}
	public String getLanguageIndicator() {
		return languageIndicator;
	}
	public void setLanguageIndicator(String languageIndicator) {
		this.languageIndicator = languageIndicator;
	}
	public String getDefaultTxn() {
		return defaultTxn;
	}
	public void setDefaultTxn(String defaultTxn) {
		this.defaultTxn = defaultTxn;
	}
	public String getAdminCode() {
		return adminCode;
	}
	public void setAdminCode(String adminCode) {
		this.adminCode = adminCode;
	}
	public String getSupervisorCode() {
		return supervisorCode;
	}
	public void setSupervisorCode(String supervisorCode) {
		this.supervisorCode = supervisorCode;
	}
	public String getSettlementCurrencyDigits() {
		return settlementCurrencyDigits;
	}
	public void setSettlementCurrencyDigits(String settlementCurrencyDigits) {
		this.settlementCurrencyDigits = settlementCurrencyDigits;
	}
	public String getMerchantDateTime() {
		return merchantDateTime;
	}
	public void setMerchantDateTime(String merchantDateTime) {
		this.merchantDateTime = merchantDateTime;
	}
	public String getEnableEMI() {
		return enableEMI;
	}
	public void setEnableEMI(String enableEMI) {
		this.enableEMI = enableEMI;
	}
	public String getAmexMerchantId() {
		return amexMerchantId;
	}
	public void setAmexMerchantId(String amexMerchantId) {
		this.amexMerchantId = amexMerchantId;
	}
	public String getCurrencyName() {
		return currencyName;
	}
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getCurrencySymbol() {
		return currencySymbol;
	}
	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}
	public String getCurrencyDigits() {
		return currencyDigits;
	}
	public void setCurrencyDigits(String currencyDigits) {
		this.currencyDigits = currencyDigits;
	}
	public String getCurrencyDecimal() {
		return currencyDecimal;
	}
	public void setCurrencyDecimal(String currencyDecimal) {
		this.currencyDecimal = currencyDecimal;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getBusinessDateFormat() {
		return businessDateFormat;
	}
	public void setBusinessDateFormat(String businessDateFormat) {
		this.businessDateFormat = businessDateFormat;
	}
    

}
