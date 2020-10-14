package com.fss.pos.rest.transactioninquiry;

public class TransactionInquiryRequest {
	private TransactionInqRequest request;

	public TransactionInqRequest getRequest() {
		return request;
	}

	public void setRequest(TransactionInqRequest request) {
		this.request = request;
	}

	@Override
	public String toString() {
		return "TransactionInquiryRequest [request=" + request + "]";
	}
	

}
