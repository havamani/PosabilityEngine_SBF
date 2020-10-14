package com.fss.pos.rest.settlementbatchupload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)

public class StmnttBatchUploadRs {
	
	private String authCode;
	private String responseDescription;
	private String channel;
	private String stan;
	private String invoiceNo;
	private String rrn;
	private String responseCode;
	private SettlementBatchUploadDeviceInfo deviceInfo;
	private SettlementBatchUploadPaymentInfo paymentInfo;
	public String getAuthCode() {
		return authCode;
	}
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
	public String getResponseDescription() {
		return responseDescription;
	}
	public void setResponseDescription(String responseDescription) {
		this.responseDescription = responseDescription;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getStan() {
		return stan;
	}
	public void setStan(String stan) {
		this.stan = stan;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public String getRrn() {
		return rrn;
	}
	public void setRrn(String rrn) {
		this.rrn = rrn;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public SettlementBatchUploadDeviceInfo getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(SettlementBatchUploadDeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	public SettlementBatchUploadPaymentInfo getPaymentInfo() {
		return paymentInfo;
	}
	public void setPaymentInfo(SettlementBatchUploadPaymentInfo paymentInfo) {
		this.paymentInfo = paymentInfo;
	}
	
}
