package com.fss.pos.rest.supportedcarddownload;

public class SupportedCardDownloadRequest {
	private SupportedCardDwnldRequest request;
	
	public SupportedCardDwnldRequest getRequest() {
		return request;
	}

	public void setRequest(SupportedCardDwnldRequest request) {
		this.request = request;
	}

	@Override
	public String toString() {
		return "SupportedCardDownloadRequest [request=" + request + "]";
	}


	
}
