package com.fss.pos.rest.settlement;

public class Settlement {
	private String batchNo;
    private String purchaseCount;
    private String purchaseAmount;
    private String tipCount;
    private String tipAmount;
    private String voidCount;
    private String voidAmount;
    private String refundCount;
    private String refundAmount;
    private String completionCount;
    private String completionAmount;
    private String cashbackCount;
    private String cashbackAmount;
    private String cashadvanceCount;
    private String cashadvanceAmount;
    
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getPurchaseCount() {
		return purchaseCount;
	}
	public void setPurchaseCount(String purchaseCount) {
		this.purchaseCount = purchaseCount;
	}
	public String getPurchaseAmount() {
		return purchaseAmount;
	}
	public void setPurchaseAmount(String purchaseAmount) {
		this.purchaseAmount = purchaseAmount;
	}
	public String getTipCount() {
		return tipCount;
	}
	public void setTipCount(String tipCount) {
		this.tipCount = tipCount;
	}
	public String getTipAmount() {
		return tipAmount;
	}
	public void setTipAmount(String tipAmount) {
		this.tipAmount = tipAmount;
	}
	public String getVoidCount() {
		return voidCount;
	}
	public void setVoidCount(String voidCount) {
		this.voidCount = voidCount;
	}
	public String getVoidAmount() {
		return voidAmount;
	}
	public void setVoidAmount(String voidAmount) {
		this.voidAmount = voidAmount;
	}
	public String getRefundCount() {
		return refundCount;
	}
	public void setRefundCount(String refundCount) {
		this.refundCount = refundCount;
	}
	public String getRefundAmount() {
		return refundAmount;
	}
	public void setRefundAmount(String refundAmount) {
		this.refundAmount = refundAmount;
	}
	public String getCompletionCount() {
		return completionCount;
	}
	public void setCompletionCount(String completionCount) {
		this.completionCount = completionCount;
	}
	public String getCompletionAmount() {
		return completionAmount;
	}
	public void setCompletionAmount(String completionAmount) {
		this.completionAmount = completionAmount;
	}
	public String getCashbackCount() {
		return cashbackCount;
	}
	public void setCashbackCount(String cashbackCount) {
		this.cashbackCount = cashbackCount;
	}
	public String getCashbackAmount() {
		return cashbackAmount;
	}
	public void setCashbackAmount(String cashbackAmount) {
		this.cashbackAmount = cashbackAmount;
	}
	public String getCashadvanceCount() {
		return cashadvanceCount;
	}
	public void setCashadvanceCount(String cashadvanceCount) {
		this.cashadvanceCount = cashadvanceCount;
	}
	public String getCashadvanceAmount() {
		return cashadvanceAmount;
	}
	public void setCashadvanceAmount(String cashadvanceAmount) {
		this.cashadvanceAmount = cashadvanceAmount;
	}
	@Override
	public String toString() {
		return "Settlement [batchNo=" + batchNo + ", purchaseCount=" + purchaseCount + ", purchaseAmount="
				+ purchaseAmount + ", tipCount=" + tipCount + ", tipAmount=" + tipAmount + ", voidCount=" + voidCount
				+ ", voidAmount=" + voidAmount + ", refundCount=" + refundCount + ", refundAmount=" + refundAmount
				+ ", completionCount=" + completionCount + ", completionAmount=" + completionAmount + ", cashbackCount="
				+ cashbackCount + ", cashbackAmount=" + cashbackAmount + ", cashadvanceCount=" + cashadvanceCount
				+ ", cashadvanceAmount=" + cashadvanceAmount + "]";
	}    
    
}
