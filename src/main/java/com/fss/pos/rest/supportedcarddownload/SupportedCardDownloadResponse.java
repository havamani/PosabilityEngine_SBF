package com.fss.pos.rest.supportedcarddownload;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupportedCardDownloadResponse {
	private SupportedCardResponse response;

	
	public SupportedCardResponse getResponse() {
		return response;
	}

	public void setResponse(SupportedCardResponse response) {
		this.response = response;
	}


}
