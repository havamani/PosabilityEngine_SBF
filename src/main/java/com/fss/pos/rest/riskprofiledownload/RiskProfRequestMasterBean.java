package com.fss.pos.rest.riskprofiledownload;

public class RiskProfRequestMasterBean {
	private RiskProfileRequest request;

	public RiskProfileRequest getRequest() {
		return request;
	}

	public void setRequest(RiskProfileRequest request) {
		this.request = request;
	}
	@Override
	public String toString() {
		return "RiskProfRequestMasterBean [request=" + request + "]";
	}
}
