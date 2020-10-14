package com.fss.pos.base.commons;

public class DccParams  extends AbstractBeanData{
	
	
	


	private String orgTxnAmount;
	private String orgCardTxnAmount;
	private String cmplTxnAmount;
	private String dccAckRRN;
	private String isDCC;
	private String invoice;
	private String orgCardAddAmount;
	
	
	public String getOrgTxnAmount() {
		return orgTxnAmount;
	}
	public void setOrgTxnAmount(String orgTxnAmount) {
		this.orgTxnAmount = orgTxnAmount;
	}
	public String getOrgCardTxnAmount() {
		return orgCardTxnAmount;
	}
	public void setOrgCardTxnAmount(String orgCardTxnAmount) {
		this.orgCardTxnAmount = orgCardTxnAmount;
	}
	public String getCmplTxnAmount() {
		return cmplTxnAmount;
	}
	public void setCmplTxnAmount(String cmplTxnAmount) {
		this.cmplTxnAmount = cmplTxnAmount;
	}
	public String getCmplCardTxnAmount() {
		return cmplCardTxnAmount;
	}
	public void setCmplCardTxnAmount(String cmplCardTxnAmount) {
		this.cmplCardTxnAmount = cmplCardTxnAmount;
	}
	private String cmplCardTxnAmount;
	
	
	public String getOrgCardAddAmount() {
		return orgCardAddAmount;
	}
	public void setOrgCardAddAmount(String orgCardAddAmount) {
		this.orgCardAddAmount = orgCardAddAmount;
	}
	public String getDccAlternateStationName() {
		return dccAlternateStationName;
	}
	public void setDccAlternateStationName(String dccAlternateStationName) {
		this.dccAlternateStationName = dccAlternateStationName;
	}
	public String getDccStationName() {
		return dccStationName;
	}
	public void setDccStationName(String dccStationName) {
		this.dccStationName = dccStationName;
	}
	private String msgProtocol;
	///Newly added///
	private String quotationId;
	private String currencyCodeA;
	private String currencyCodeN;
	private String cardExponent;
	private String merchantExponent;
	private String markUp;
	private String commission;
	private String cardMarginValue;
	private String merMarginValue;
	private String exchangeRate;
	private String quotationRate;
	/// For Monex ///
	private String terminalId;
	private String countryCode;
	private String acqInstId;
	private String currencyCode;
	private String cardAcceptId;
	private String merchantDate;
	private String merchantTime;
	private String tranId;
	///unused///
	private String rrn;
	private String txnAmount;
	private String exchangeExponent;
	private String txnDtTime;
	private String dccAlternateStationName;
	private String dccStationName;


	
	public String getExchangeRate() {
		return exchangeRate;
	}
	public void setExchangeRate(String exchangeRate) {
		this.exchangeRate = exchangeRate;
	}
	
	public String getMsgProtocol() {
		return msgProtocol;
	}
	public void setMsgProtocol(String msgProtocol) {
		this.msgProtocol = msgProtocol;
	}
	
	public String getTerminalId() {
		return terminalId;
	}
	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getAcqInstId() {
		return acqInstId;
	}
	public void setAcqInstId(String acqInstId) {
		this.acqInstId = acqInstId;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getQuotationId() {
		return quotationId;
	}
	public void setQuotationId(String quotationId) {
		this.quotationId = quotationId;
	}
	
	public String getMarkUp() {
		return markUp;
	}
	public void setMarkUp(String markUp) {
		this.markUp = markUp;
	}
	public String getCommission() {
		return commission;
	}
	public void setCommission(String commission) {
		this.commission = commission;
	}
	public String getCurrencyCodeA() {
		return currencyCodeA;
	}
	public void setCurrencyCodeA(String currencyCodeA) {
		this.currencyCodeA = currencyCodeA;
	}
	public String getCurrencyCodeN() {
		return currencyCodeN;
	}
	public void setCurrencyCodeN(String currencyCodeN) {
		this.currencyCodeN = currencyCodeN;
	}
	public String getCardExponent() {
		return cardExponent;
	}
	public void setCardExponent(String cardExponent) {
		this.cardExponent = cardExponent;
	}
	
	public String getMerchantExponent() {
		return merchantExponent;
	}
	public void setMerchantExponent(String merchantExponent) {
		this.merchantExponent = merchantExponent;
	}
	public String getCardMarginValue() {
		return cardMarginValue;
	}
	public void setCardMarginValue(String cardMarginValue) {
		this.cardMarginValue = cardMarginValue;
	}
	public String getMerMarginValue() {
		return merMarginValue;
	}
	public void setMerMarginValue(String merMarginValue) {
		this.merMarginValue = merMarginValue;
	}
	public String getQuotationRate() {
		return quotationRate;
	}
	public void setQuotationRate(String quotationRate) {
		this.quotationRate = quotationRate;
	}
	public String getCardAcceptId() {
		return cardAcceptId;
	}
	public void setCardAcceptId(String cardAcceptId) {
		this.cardAcceptId = cardAcceptId;
	}
	public String getRrn() {
		return rrn;
	}
	public void setRrn(String rrn) {
		this.rrn = rrn;
	}
	public String getTxnAmount() {
		return txnAmount;
	}
	public void setTxnAmount(String txnAmount) {
		this.txnAmount = txnAmount;
	}
	public String getExchangeExponent() {
		return exchangeExponent;
	}
	public void setExchangeExponent(String exchangeExponent) {
		this.exchangeExponent = exchangeExponent;
	}
	public String getMerchantDate() {
		return merchantDate;
	}
	public void setMerchantDate(String merchantDate) {
		this.merchantDate = merchantDate;
	}
	public String getMerchantTime() {
		return merchantTime;
	}
	public void setMerchantTime(String merchantTime) {
		this.merchantTime = merchantTime;
	}
	public String getTranId() {
		return tranId;
	}
	public void setTranId(String tranId) {
		this.tranId = tranId;
	}
	public String getDccAckRRN() {
		return dccAckRRN;
	}
	public void setDccAckRRN(String dccAckRRN) {
		this.dccAckRRN = dccAckRRN;
	}
	public String getTxnDtTime() {
		return txnDtTime;
	}
	public void setTxnDtTime(String txnDtTime) {
		this.txnDtTime = txnDtTime;
	}
	public String getIsDCC() {
		return isDCC;
	}
	public void setIsDCC(String isDCC) {
		this.isDCC = isDCC;
	}
	public String getInvoice() {
		return invoice;
	}
	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}

}
