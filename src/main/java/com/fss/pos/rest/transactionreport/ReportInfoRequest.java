package com.fss.pos.rest.transactionreport;

public class ReportInfoRequest {
	private String fromDate;
	private String toDate;
	
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	@Override
	public String toString() {
		return "ReportInfoRequest [fromDate=" + fromDate + ", toDate=" + toDate + "]";
	}
	
	
}
