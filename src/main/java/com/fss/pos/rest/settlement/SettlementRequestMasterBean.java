package com.fss.pos.rest.settlement;

public class SettlementRequestMasterBean {
	 private SettlementRequest request;

	public SettlementRequest getRequest() {
		return request;
	}

	public void setRequest(SettlementRequest request) {
		this.request = request;
	}
	@Override
	public String toString() {
		return "SettlementRequestMasterBean [request=" + request + "]";
	}
}
