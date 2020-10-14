package com.fss.pos.rest.settlementbatchupload;

public class SettlemtBatchUploadRequest {
	
	private StmntBatchUploadRq request;

	public StmntBatchUploadRq getRequest() {
		return request;
	}

	public void setRequest(StmntBatchUploadRq request) {
		this.request = request;
	}

	@Override
	public String toString() {
		return "SettlemtBatchUploadRequest [request=" + request + "]";
	}
	
   
}
