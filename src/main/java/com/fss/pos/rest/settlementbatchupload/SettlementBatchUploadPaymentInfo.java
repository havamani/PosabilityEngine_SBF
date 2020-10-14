package com.fss.pos.rest.settlementbatchupload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)

public class SettlementBatchUploadPaymentInfo {	
	
	private String paymentNo;	
	private String paymentTrack;
	private String expiryDate;
	private String entryMode;
	private String conditionCode;
	private String transactionAmount;
	private String transactionType;
	private String tipAmount;
	private String authData;
	private String emvData;
	private String authCode;
	public String getPaymentNo() {
		return paymentNo;
	}
	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}
	public String getPaymentTrack() {
		return paymentTrack;
	}
	public void setPaymentTrack(String paymentTrack) {
		this.paymentTrack = paymentTrack;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getEntryMode() {
		return entryMode;
	}
	public void setEntryMode(String entryMode) {
		this.entryMode = entryMode;
	}
	public String getConditionCode() {
		return conditionCode;
	}
	public void setConditionCode(String conditionCode) {
		this.conditionCode = conditionCode;
	}
	public String getTransactionAmount() {
		return transactionAmount;
	}
	public void setTransactionAmount(String transactionAmount) {
		this.transactionAmount = transactionAmount;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getTipAmount() {
		return tipAmount;
	}
	public void setTipAmount(String tipAmount) {
		this.tipAmount = tipAmount;
	}
	public String getAuthData() {
		return authData;
	}
	public void setAuthData(String authData) {
		this.authData = authData;
	}
	public String getEmvData() {
		return emvData;
	}
	public void setEmvData(String emvData) {
		this.emvData = emvData;
	}
	public String getAuthCode() {
		return authCode;
	}
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
	@Override
	public String toString() {
		return "SettlementBatchUploadPaymentInfo [paymentNo=" + paymentNo + ", paymentTrack=" + paymentTrack
				+ ", expiryDate=" + expiryDate + ", entryMode=" + entryMode + ", conditionCode=" + conditionCode
				+ ", transactionAmount=" + transactionAmount + ", transactionType=" + transactionType + ", tipAmount="
				+ tipAmount + ", authData=" + authData + ", emvData=" + emvData + ", authCode=" + authCode + "]";
	}
	

}
