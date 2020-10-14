package com.fss.pos.rest.txnsummaryreport;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TxnSummaryResponseMasterBean {

	private TxnSummaryResponse response;

	public TxnSummaryResponse getResponse() {
		return response;
	}

	public void setResponse(TxnSummaryResponse response) {
		this.response = response;
	}
}
