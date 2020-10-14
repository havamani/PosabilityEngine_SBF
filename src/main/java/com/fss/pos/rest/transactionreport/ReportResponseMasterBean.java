package com.fss.pos.rest.transactionreport;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportResponseMasterBean {
	private ReportResponse response;

	public ReportResponse getResponse() {
		return response;
	}

	public void setResponse(ReportResponse response) {
		this.response = response;
	}

}
