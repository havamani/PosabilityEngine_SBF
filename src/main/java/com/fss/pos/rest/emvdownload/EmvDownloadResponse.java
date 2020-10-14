package com.fss.pos.rest.emvdownload;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class EmvDownloadResponse {
	private EmvResponse response;

	public EmvResponse getResponse() {
		return response;
	}

	public void setResponse(EmvResponse response) {
		this.response = response;
	}



}
