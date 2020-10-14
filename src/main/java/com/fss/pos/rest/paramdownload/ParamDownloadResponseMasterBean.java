package com.fss.pos.rest.paramdownload;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParamDownloadResponseMasterBean {
	private ParamDwnldResponse response;

	public ParamDwnldResponse getResponse() {
		return response;
	}

	public void setResponse(ParamDwnldResponse response) {
		this.response = response;
	}
}
