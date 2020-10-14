package com.fss.pos.base.api.client;

import java.util.Map;

import com.fss.pos.base.commons.AbstractBeanData;
import com.fss.pos.base.commons.EmvTags;

public class ClientData extends AbstractBeanData {

	private String procCode;
	private String amount;
	private String stan;
	private String rrn;
	private String terminalId;
	private String invoice;
	private String enrtyMode;
	private String uti;
	private String date;
	private String time;
	private Map<EmvTags, String> emvResponseMap;
	private String txnCommunicationFlow;
	//DCC Monex Response
	private String merMarginValue;
	private String merchantExponent;
	private String isDCC;
	private String msgProtocol;
	
	private String respCode;
	private String mobileNo;
	private String emailId;
	private String accountNo;
	private String aadhaarNo;
	private String authId;
	private String customerId;
	private String customerName;

	// extra field for merchant info

	private String acctType;
	private String merchantName;
	private String addr1;
	private String addr2;
	private String city;
	private String state;
	private String zipcode;
	private String panNo;

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getAadhaarNo() {
		return aadhaarNo;
	}

	public void setAadhaarNo(String aadhaarNo) {
		this.aadhaarNo = aadhaarNo;
	}

	public String getAuthId() {
		return authId;
	}

	public void setAuthId(String authId) {
		this.authId = authId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getAcctType() {
		return acctType;
	}

	public void setAcctType(String acctType) {
		this.acctType = acctType;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getAddr1() {
		return addr1;
	}

	public void setAddr1(String addr1) {
		this.addr1 = addr1;
	}

	public String getAddr2() {
		return addr2;
	}

	public void setAddr2(String addr2) {
		this.addr2 = addr2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getPanNo() {
		return panNo;
	}

	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}

	public String getTxnCommunicationFlow() {
		return txnCommunicationFlow;
	}

	public void setTxnCommunicationFlow(String txnCommunicationFlow) {
		this.txnCommunicationFlow = txnCommunicationFlow;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getProcCode() {
		return procCode;
	}

	public void setProcCode(String procCode) {
		this.procCode = procCode;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getStan() {
		return stan;
	}

	public void setStan(String stan) {
		this.stan = stan;
	}

	public String getRrn() {
		return rrn;
	}

	public void setRrn(String rrn) {
		this.rrn = rrn;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getInvoice() {
		return invoice;
	}

	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}

	public String getEnrtyMode() {
		return enrtyMode;
	}

	public void setEnrtyMode(String enrtyMode) {
		this.enrtyMode = enrtyMode;
	}

	public String getUti() {
		return uti;
	}

	public void setUti(String uti) {
		this.uti = uti;
	}

	public Map<EmvTags, String> getEmvResponseMap() {
		return emvResponseMap;
	}

	public void setEmvResponseMap(Map<EmvTags, String> emvResponseMap) {
		this.emvResponseMap = emvResponseMap;
	}

	public String getMerchantExponent() {
		return merchantExponent;
	}

	public void setMerchantExponent(String merchantExponent) {
		this.merchantExponent = merchantExponent;
	}

	public String getMerMarginValue() {
		return merMarginValue;
	}

	public void setMerMarginValue(String merMarginValue) {
		this.merMarginValue = merMarginValue;
	}

	public String getIsDCC() {
		return isDCC;
	}

	public void setIsDCC(String isDCC) {
		this.isDCC = isDCC;
	}

	public String getMsgProtocol() {
		return msgProtocol;
	}

	public void setMsgProtocol(String msgProtocol) {
		this.msgProtocol = msgProtocol;
	}

}
