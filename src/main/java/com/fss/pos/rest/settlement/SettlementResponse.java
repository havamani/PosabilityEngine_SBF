package com.fss.pos.rest.settlement;

public class SettlementResponse {
	private String channel;
    private String stan;
    private String invoiceNo;
    private String batchNo;
    private String responseCode;
    private String responseDescription;
    private SettlementDeviceInfo deviceInfo;
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
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseDescription() {
		return responseDescription;
	}
	public void setResponseDescription(String responseDescription) {
		this.responseDescription = responseDescription;
	}
	public SettlementDeviceInfo getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(SettlementDeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	@Override
	public String toString() {
		return "Response [channel=" + channel + ", stan=" + stan + ", invoiceNo=" + invoiceNo + ", batchNo=" + batchNo
				+ ", responseCode=" + responseCode + ", responseDescription=" + responseDescription + ", deviceInfo="
				+ deviceInfo + "]";
	}
    
	}
