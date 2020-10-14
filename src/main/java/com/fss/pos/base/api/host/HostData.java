package com.fss.pos.base.api.host;

import java.util.Map;

import com.fss.pos.base.commons.AbstractBeanData;
import com.fss.pos.base.commons.EmvTags;

public class HostData extends AbstractBeanData {

	private String businessName;
	private String merchantAddress;
	private String countryCode;
	private String currencyCode;
	private String mcc;
	private String acqInstId;
	private String stationName;
	private String alternateStationName;
	private String msgProtocol;
	private String msgLengthType;
	private String msgLength;
	private String uti;
	private String rrn;
	private String enableEMV;
	private String cardHolderVerification;
	private String keyEntry;
	private String magneticStripe;
	private String icContact;
	private String zonalKey;
	private String cardInputCapability;
	private String cardHolderAuthentication;
	private String magStripePinByPassLimit;
	private String merchantDate;
	private String merchantTime;
	private String merchantId;
	private String terminalId;
	private String dccEnabled;
	private String txnCommunicationFlow;
	private String txnDateTime;
	private String stan;
	private String state;
	private String city;
	private String isoCountryCode;
	private String zpkChecksum;
	
	// original txn details for adjustments
	private String orgMessageType;
	private String orgStan;
	private String orgTransmissionDt;
	private String orgAcquirerId;
	private String orgFiid;
	private String orgAuthCode;
	private String orgProcCode;

	private String respDe48;
	private String pinEnabledTxn;
	private String respDe63;
	private String orgTransmissionDY;
	private String respDe39;
	private String reqDe22;
	private String reqDe23;
	private String reqDe62;
	private String reqDe15;
	private String reqDe25;
	private String reqDe17;
	private String reqDe35;
	private String reqDe55;
	private String respDe100;
	private String respDe15;
	private String orgRrn;

	private Map<EmvTags, String> emvMap;
	private String tranId;
	private String zipCode;
	private String fiid;
	private String srcStationId;
	private String destStationId;
	private String currencyName;

	// for DCC
	private String isDCC;
	private String dccStationName;
	private String quotationId;
	private String currencyCodeA;
private String respDe31;

	// decimal length
	private String decimalLength; // merDecLength
	private String cardDecimal; // cusDecLength

	// adding Terminal serial number(FNB)
	private String terSerialNo;
	private String dccAlternateStationName;

public String getRespDe31() {
		return respDe31;
	}

	public void setRespDe31(String respDe31) {
		this.respDe31 = respDe31;
	}

	public String getCurrencyCodeA() {
		return currencyCodeA;
	}

	public void setCurrencyCodeA(String currencyCodeA) {
		this.currencyCodeA = currencyCodeA;
	}

	public String getReqDe22() {
		return reqDe22;
	}

	public void setReqDe22(String reqDe22) {
		this.reqDe22 = reqDe22;
	}

	public String getReqDe25() {
		return reqDe25;
	}

	public void setReqDe25(String reqDe25) {
		this.reqDe25 = reqDe25;
	}

	public String getReqDe55() {
		return reqDe55;
	}

	public void setReqDe55(String reqDe55) {
		this.reqDe55 = reqDe55;
	}
	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getOrgTransmissionDY() {
		return orgTransmissionDY;
	}

	public void setOrgTransmissionDY(String orgTransmissionDY) {
		this.orgTransmissionDY = orgTransmissionDY;
	}

	public String getTranId() {
		return tranId;
	}

	public void setTranId(String tranId) {
		this.tranId = tranId;
	}

	public String getRespDe48() {
		return respDe48;
	}

	public void setRespDe48(String respDe48) {
		this.respDe48 = respDe48;
	}

	public String getPinEnabledTxn() {
		return pinEnabledTxn;
	}

	public void setPinEnabledTxn(String pinEnabledTxn) {
		this.pinEnabledTxn = pinEnabledTxn;
	}

	public String getRespDe63() {
		return respDe63;
	}

	public void setRespDe63(String respDe63) {
		this.respDe63 = respDe63;
	}

	public String getCardInputCapability() {
		return cardInputCapability;
	}

	public void setCardInputCapability(String cardInputCapability) {
		this.cardInputCapability = cardInputCapability;
	}

	public String getCardHolderAuthentication() {
		return cardHolderAuthentication;
	}

	public void setCardHolderAuthentication(String cardHolderAuthentication) {
		this.cardHolderAuthentication = cardHolderAuthentication;
	}

	public String getEnableEMV() {
		return enableEMV;
	}

	public void setEnableEMV(String enableEMV) {
		this.enableEMV = enableEMV;
	}

	public String getCardHolderVerification() {
		return cardHolderVerification;
	}

	public void setCardHolderVerification(String cardHolderVerification) {
		this.cardHolderVerification = cardHolderVerification;
	}

	public String getKeyEntry() {
		return keyEntry;
	}

	public void setKeyEntry(String keyEntry) {
		this.keyEntry = keyEntry;
	}

	public String getMagneticStripe() {
		return magneticStripe;
	}

	public void setMagneticStripe(String magneticStripe) {
		this.magneticStripe = magneticStripe;
	}

	public String getIcContact() {
		return icContact;
	}

	public void setIcContact(String icContact) {
		this.icContact = icContact;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getMerchantAddress() {
		return merchantAddress;
	}

	public void setMerchantAddress(String merchantAddress) {
		this.merchantAddress = merchantAddress;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getMcc() {
		return mcc;
	}

	public void setMcc(String mcc) {
		this.mcc = mcc;
	}

	public String getAcqInstId() {
		return acqInstId;
	}

	public void setAcqInstId(String acqInstId) {
		this.acqInstId = acqInstId;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getAlternateStationName() {
		return alternateStationName;
	}

	public void setAlternateStationName(String alternateStationName) {
		this.alternateStationName = alternateStationName;
	}

	public String getMsgProtocol() {
		return msgProtocol;
	}

	public void setMsgProtocol(String msgProtocol) {
		this.msgProtocol = msgProtocol;
	}

	public String getMsgLengthType() {
		return msgLengthType;
	}

	public void setMsgLengthType(String msgLengthType) {
		this.msgLengthType = msgLengthType;
	}

	public String getMsgLength() {
		return msgLength;
	}

	public void setMsgLength(String msgLength) {
		this.msgLength = msgLength;
	}

	public String getUti() {
		return uti;
	}

	public void setUti(String uti) {
		this.uti = uti;
	}

	public String getRrn() {
		return rrn;
	}

	public void setRrn(String rrn) {
		this.rrn = rrn;
	}

	public String getZonalKey() {
		return zonalKey;
	}

	public void setZonalKey(String zonalKey) {
		this.zonalKey = zonalKey;
	}

	public String getMagStripePinByPassLimit() {
		return magStripePinByPassLimit;
	}

	public void setMagStripePinByPassLimit(String magStripePinByPassLimit) {
		this.magStripePinByPassLimit = magStripePinByPassLimit;
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

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public Map<EmvTags, String> getEmvMap() {
		return emvMap;
	}

	public void setEmvMap(Map<EmvTags, String> emvMap) {
		this.emvMap = emvMap;
	}

	public String getDccEnabled() {
		return dccEnabled;
	}

	public void setDccEnabled(String dccEnabled) {
		this.dccEnabled = dccEnabled;
	}

	public String getTxnCommunicationFlow() {
		return txnCommunicationFlow;
	}

	public void setTxnCommunicationFlow(String txnCommunicationFlow) {
		this.txnCommunicationFlow = txnCommunicationFlow;
	}

	public String getTxnDateTime() {
		return txnDateTime;
	}

	public void setTxnDateTime(String txnDateTime) {
		this.txnDateTime = txnDateTime;
	}

	public String getStan() {
		return stan;
	}

	public void setStan(String stan) {
		this.stan = stan;
	}

	public String getOrgMessageType() {
		return orgMessageType;
	}

	public void setOrgMessageType(String orgMessageType) {
		this.orgMessageType = orgMessageType;
	}

	public String getOrgStan() {
		return orgStan;
	}

	public void setOrgStan(String orgStan) {
		this.orgStan = orgStan;
	}

	public String getOrgTransmissionDt() {
		return orgTransmissionDt;
	}

	public void setOrgTransmissionDt(String orgTransmissionDt) {
		this.orgTransmissionDt = orgTransmissionDt;
	}

	public String getOrgAcquirerId() {
		return orgAcquirerId;
	}

	public void setOrgAcquirerId(String orgAcquirerId) {
		this.orgAcquirerId = orgAcquirerId;
	}

	public String getOrgFiid() {
		return orgFiid;
	}

	public void setOrgFiid(String orgFiid) {
		this.orgFiid = orgFiid;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getIsoCountryCode() {
		return isoCountryCode;
	}

	public void setIsoCountryCode(String isoCountryCode) {
		this.isoCountryCode = isoCountryCode;
	}

	public String getZpkChecksum() {
		return zpkChecksum;
	}

	public void setZpkChecksum(String zpkChecksum) {
		this.zpkChecksum = zpkChecksum;
	}

	public String getOrgAuthCode() {
		return orgAuthCode;
	}

	public void setOrgAuthCode(String orgAuthCode) {
		this.orgAuthCode = orgAuthCode;
	}

	public String getRespDe39() {
		return respDe39;
	}

	public void setRespDe39(String respDe39) {
		this.respDe39 = respDe39;
	}

	public String getOrgProcCode() {
		return orgProcCode;
	}

	public void setOrgProcCode(String orgProcCode) {
		this.orgProcCode = orgProcCode;
	}

	public String getReqDe62() {
		return reqDe62;
	}

	public void setReqDe62(String reqDe62) {
		this.reqDe62 = reqDe62;
	}

	public String getReqDe15() {
		return reqDe15;
	}

	public void setReqDe15(String reqDe15) {
		this.reqDe15 = reqDe15;
	}

	public String getReqDe17() {
		return reqDe17;
	}

	public void setReqDe17(String reqDe17) {
		this.reqDe17 = reqDe17;
	}

	public String getReqDe35() {
		return reqDe35;
	}

	public void setReqDe35(String reqDe35) {
		this.reqDe35 = reqDe35;
	}

	public String getRespDe100() {
		return respDe100;
	}

	public void setRespDe100(String respDe100) {
		this.respDe100 = respDe100;
	}

	public String getFiid() {
		return fiid;
	}

	public void setFiid(String fiid) {
		this.fiid = fiid;
	}

	public String getSrcStationId() {
		return srcStationId;
	}

	public void setSrcStationId(String srcStationId) {
		this.srcStationId = srcStationId;
	}

	public String getDestStationId() {
		return destStationId;
	}

	public void setDestStationId(String destStationId) {
		this.destStationId = destStationId;
	}

	public String getDccStationName() {
		return dccStationName;
	}

	public void setDccStationName(String dccStationName) {
		this.dccStationName = dccStationName;
	}

	public String getIsDCC() {
		return isDCC;
	}

	public void setIsDCC(String isDCC) {
		this.isDCC = isDCC;
	}

	public String getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(String quotationId) {
		this.quotationId = quotationId;
	}

	public String getDecimalLength() {
		return decimalLength;
	}

	public void setDecimalLength(String decimalLength) {
		this.decimalLength = decimalLength;
	}

	public String getCardDecimal() {
		return cardDecimal;
	}

	public void setCardDecimal(String cardDecimal) {
		this.cardDecimal = cardDecimal;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public String getReqDe23() {
		return reqDe23;
	}

	public void setReqDe23(String reqDe23) {
		this.reqDe23 = reqDe23;
	}

	public String getTerSerialNo() {
		return terSerialNo;
	}

	public void setTerSerialNo(String terSerialNo) {
		this.terSerialNo = terSerialNo;
	}

	public String getRespDe15() {
		return respDe15;
	}

	public void setRespDe15(String respDe15) {
		this.respDe15 = respDe15;
	}

	public String getOrgRrn() {
		return orgRrn;
	}

	public void setOrgRrn(String orgRrn) {
		this.orgRrn = orgRrn;
	}

	public String getDccAlternateStationName() {
		return dccAlternateStationName;
	}

	public void setDccAlternateStationName(String dccAlternateStationName) {
		this.dccAlternateStationName = dccAlternateStationName;
	}

	
	}
