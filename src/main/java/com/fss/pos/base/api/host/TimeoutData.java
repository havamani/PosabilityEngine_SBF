package com.fss.pos.base.api.host;

import java.io.Serializable;
import java.util.Map;

public class TimeoutData implements Serializable {

	private static final long serialVersionUID = 4500492204686037608L;

	private String threadLocalId;
	private String hostStationName;
	private Map<String, Object> isoBufferMap;
	private String iin;
	private String clientStationName;
	private String clientClass;
	private String uti;
	private String terminalId;
	private String merchantId;

	// key import
	private String transmissionDateAndTime;
	private String hostId;
	private String hsmId;
	private String hsmStationName;
	private String hsmAcquirerIndex;
	private String thisSource;
	private String hsmAcquirerKeyLength;
	private String txnCommunicationFlow;
	private String zmkCheckSum;

	// DCC Monex Response
	private String merMarginValue;
	private String merchantExponent;
	private String isDCC;
	private String dccStationName;
	private String dccAlternateStationName;

	private String rrn;
	private String quotationId;

	// TLE
	private String plainKey;
	// For ABSA
	private String respDe126;
	private String respDe48;

	public String getIsDCC() {
		return isDCC;
	}

	public void setIsDCC(String isDCC) {
		this.isDCC = isDCC;
	}

	public String getDccStationName() {
		return dccStationName;
	}

	public void setDccStationName(String dccStationName) {
		this.dccStationName = dccStationName;
	}

	// Vijayarumugam K
	private String myStation;

	private String tranId;

	public String getTranId() {
		return tranId;
	}

	public void setTranId(String tranId) {
		this.tranId = tranId;
	}

	public String getMyStation() {
		return myStation;
	}

	public void setMyStation(String myStation) {
		this.myStation = myStation;
	}

	public String getTransmissionDateAndTime() {
		return transmissionDateAndTime;
	}

	public void setTransmissionDateAndTime(String transmissionDateAndTime) {
		this.transmissionDateAndTime = transmissionDateAndTime;
	}

	public String getTxnCommunicationFlow() {
		return txnCommunicationFlow;
	}

	public void setTxnCommunicationFlow(String txnCommunicationFlow) {
		this.txnCommunicationFlow = txnCommunicationFlow;
	}

	public String getClientStationName() {
		return clientStationName;
	}

	public void setClientStationName(String clientStationName) {
		this.clientStationName = clientStationName;
	}

	public String getHostStationName() {
		return hostStationName;
	}

	public void setHostStationName(String hostStationName) {
		this.hostStationName = hostStationName;
	}

	public String getIin() {
		return iin;
	}

	public void setIin(String iin) {
		this.iin = iin;
	}

	public String getClientClass() {
		return clientClass;
	}

	public void setClientClass(String clientClass) {
		this.clientClass = clientClass;
	}

	public String getThreadLocalId() {
		return threadLocalId;
	}

	public void setThreadLocalId(String threadLocalId) {
		this.threadLocalId = threadLocalId;
	}

	public String getHostId() {
		return hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	public String getHsmId() {
		return hsmId;
	}

	public void setHsmId(String hsmId) {
		this.hsmId = hsmId;
	}

	public String getHsmStationName() {
		return hsmStationName;
	}

	public void setHsmStationName(String hsmStationName) {
		this.hsmStationName = hsmStationName;
	}

	public String getHsmAcquirerIndex() {
		return hsmAcquirerIndex;
	}

	public void setHsmAcquirerIndex(String hsmAcquirerIndex) {
		this.hsmAcquirerIndex = hsmAcquirerIndex;
	}

	public String getThisSource() {
		return thisSource;
	}

	public void setThisSource(String thisSource) {
		this.thisSource = thisSource;
	}

	public String getHsmAcquirerKeyLength() {
		return hsmAcquirerKeyLength;
	}

	public void setHsmAcquirerKeyLength(String hsmAcquirerKeyLength) {
		this.hsmAcquirerKeyLength = hsmAcquirerKeyLength;
	}

	public String getUti() {
		return uti;
	}

	public void setUti(String uti) {
		this.uti = uti;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getZmkCheckSum() {
		return zmkCheckSum;
	}

	public void setZmkCheckSum(String zmkCheckSum) {
		this.zmkCheckSum = zmkCheckSum;
	}

	public Map<String, Object> getIsoBufferMap() {
		return isoBufferMap;
	}

	public void setIsoBufferMap(Map<String, Object> isoBufferMap) {
		this.isoBufferMap = isoBufferMap;
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

	public String getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(String quotationId) {
		this.quotationId = quotationId;
	}

	public String getPlainKey() {
		return plainKey;
	}

	public void setPlainKey(String plainKey) {
		this.plainKey = plainKey;
	}

	public String getRespDe126() {
		return respDe126;
	}

	public void setRespDe126(String respDe126) {
		this.respDe126 = respDe126;
	}

	public String getRespDe48() {
		return respDe48;
	}

	public void setRespDe48(String respDe48) {
		this.respDe48 = respDe48;
	}

	public String getDccAlternateStationName() {
		return dccAlternateStationName;
	}

	public void setDccAlternateStationName(String dccAlternateStationName) {
		this.dccAlternateStationName = dccAlternateStationName;
	}

	public String getRrn() {
		return rrn;
	}

	public void setRrn(String rrn) {
		this.rrn = rrn;
	}
}
