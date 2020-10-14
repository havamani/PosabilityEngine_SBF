package com.fss.pos.rest.settlementbatchupload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


public class StmntBatchUploadRq {
	
	private String channel;
	private String stan;
	private String invoiceNo;
	private String transactionDT;
	private String rrn;
	private SettlementBatchUploadDeviceInfo deviceInfo;
	private SettlementBatchUploadPaymentInfo paymentInfo;
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
	public String getTransactionDT() {
		return transactionDT;
	}
	public void setTransactionDT(String transactionDT) {
		this.transactionDT = transactionDT;
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
	@Override
	public String toString() {
		return "StmntBatchUploadRq [channel=" + channel + ", stan=" + stan + ", invoiceNo=" + invoiceNo
				+ ", transactionDT=" + transactionDT + ", rrn=" + rrn + ", deviceInfo=" + deviceInfo + ", paymentInfo="
				+ paymentInfo + "]";
	}
	
	

}
