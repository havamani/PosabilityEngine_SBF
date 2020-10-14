package com.fss.pos.rest.emvdownload;

public class EmvDownloadRequest {
	private EmvRequest request;

	public EmvRequest getRequest() {
		return request;
	}

	public void setRequest(EmvRequest request) {
		this.request = request;
	}

	@Override
	public String toString() {
		return "EmvDownloadRequest [request=" + request + "]";
	}

}
