package com.fss.pos.rest.logon;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogonResponseMasterBean {

	private LogonResponse response;

	public LogonResponse getResponse() {
		return response;
	}

	public void setResponse(LogonResponse response) {
		this.response = response;
	}
	
}
