package com.fss.pos.rest.settlement;

public class SettlementResponseMasterBean {
	 private SettlementResponse response;

	public SettlementResponse getResponse() {
		return response;
	}

	public void setResponse(SettlementResponse response) {
		this.response = response;
	}

	@Override
	public String toString() {
		return "SettlementResponseMasterBean [response=" + response + "]";
	}
}
