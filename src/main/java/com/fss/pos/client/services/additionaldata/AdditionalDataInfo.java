package com.fss.pos.client.services.additionaldata;

public class AdditionalDataInfo {
	private String rrn;
	QSPARC QSPARCObject;
	P2pe P2peObject;
	private String pinKSN;
	private String reversalType;

	// Getter Methods
	public String getRrn() {
		return rrn;
	}

	public QSPARC getQSPARC() {
		return QSPARCObject;
	}

	public P2pe getP2pe() {
		return P2peObject;
	}

	public String getPinKSN() {
		return pinKSN;
	}

	public String getReversalType() {
		return reversalType;
	}

	// Setter Methods

	public void setRrn(String rrn) {
		this.rrn = rrn;
	}

	public void setQSPARC(QSPARC qSPARCObject) {
		this.QSPARCObject = qSPARCObject;
	}

	public void setP2pe(P2pe p2peObject) {
		this.P2peObject = p2peObject;
	}

	public void setPinKSN(String pinKSN) {
		this.pinKSN = pinKSN;
	}

	public void setReversalType(String reversalType) {
		this.reversalType = reversalType;
	}
}
