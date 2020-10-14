package com.fss.pos.rest.riskprofiledownload;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RiskProfResponseMasterBean {
	private RiskProfileResponse response;

	public RiskProfileResponse getResponse() {
		return response;
	}

	public void setResponse(RiskProfileResponse response) {
		this.response = response;
	}
	
}
