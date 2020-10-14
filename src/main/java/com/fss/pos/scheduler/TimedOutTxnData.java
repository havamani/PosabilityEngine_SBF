package com.fss.pos.scheduler;

public class TimedOutTxnData {

	private String transactionId;
	private String transactionTimerData;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTransactionTimerData() {
		return transactionTimerData;
	}

	public void setTransactionTimerData(String transactionTimerData) {
		this.transactionTimerData = transactionTimerData;
	}
	
}
