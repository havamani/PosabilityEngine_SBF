package com.fss.pos.rest.transactioninquiry;


public class TransactionInqRequest {
     private String channel;
     private String Stan;
     private String rrn;
     private String invoiceNo;
     private TransactionInqDeviceInfo deviceInfo;
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getStan() {
		return Stan;
	}
	public void setStan(String stan) {
		Stan = stan;
	}
	public String getRrn() {
		return rrn;
	}
	public void setRrn(String rrn) {
		this.rrn = rrn;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public TransactionInqDeviceInfo getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(TransactionInqDeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	@Override
	public String toString() {
		return "Request [channel=" + channel + ", Stan=" + Stan + ", rrn=" + rrn + ", invoiceNo=" + invoiceNo
				+ ", deviceInfo=" + deviceInfo + "]";
	}
     

}
