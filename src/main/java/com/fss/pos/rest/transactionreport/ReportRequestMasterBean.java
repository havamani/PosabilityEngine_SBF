package com.fss.pos.rest.transactionreport;

public class ReportRequestMasterBean {
	
	private ReportRequest request;

	public ReportRequest getRequest() {
		return request;
	}

	public void setRequest(ReportRequest request) {
		this.request = request;
	}

	@Override
	public String toString() {
		return "ReportRequestMasterBean [request=" + request + "]";
	}

}
