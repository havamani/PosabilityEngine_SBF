package com.fss.pos.base.services.transactionlog;

import java.util.Map;

import com.fss.pos.base.commons.AbstractBeanData;
import com.fss.pos.base.commons.EmvTags;

public class TransactionLogData extends AbstractBeanData {

	private String decimalLength; // merDecLength
	private String cardDecimal; // cusDecLength

	private String status;
	private String stationName;

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

	private String msgProtocol;
	private String acqInstId;
	private String acquirerName;
	private String rrn;
	private String alternateStationName;
	private String cardInputCapability;
	private String cardHolderAuthentication;
	private String operatingEnvironment;
	private String merchantDate;
	private String merchantTime;
	private String stan;
	private String transmissionDt;
	private String businessName;
	private String merchantAddress;
	private String countryCode;
	private String currencyCode;
	private String mcc;
	private String terminalId;
	private String merchantId;
	private String currentMerchantDate;
	private String currentMerchantTime;
	private String authCode;
	private String originalMsgType;
	private String pinEnabledTxn;
	private String respDe48;
	private String respDe63;
	private String respDe62;
	// current txn datetime in GMT
	private String txnDateTime;
	private String reqDe12;
	private String reqDe23;
	private String tranId;

	// added by yogita for benefit
	private String reqDe22;
	private String orgTransmissionDY;
	private String orgTransmissionDt;
	private String orgStan;
	private String orgAuthCode;
	private String orgProcCode;
	private String city;
	private String reqDe35;
	private String reqDe15;
	private String reqDe25;
	private String reqDe17;
	private String respDe31;
	private String respDe100;
	private String srcStationId;
	private String destStationId;
	private String fiid;
	private String reqDe43;
	private String quotationId;
	private String cntryName;

	// for DCC
	private String isDCC;
	private String stateCode;

	// For ABSA
	private String uti;

	private String isoCountryCode;

	public String getIsoCountryCode() {
		return isoCountryCode;
	}

	public void setIsoCountryCode(String isoCountryCode) {
		this.isoCountryCode = isoCountryCode;
	}

	public String getDccStationName() {
		return dccStationName;
	}

	public void setDccStationName(String dccStationName) {
		this.dccStationName = dccStationName;
	}

	public String getDccAlternateStationName() {
		return dccAlternateStationName;
	}

	public void setDccAlternateStationName(String dccAlternateStationName) {
		this.dccAlternateStationName = dccAlternateStationName;
	}

	private String dccStationName;
	private String dccAlternateStationName;

	public String getUti() {
		return uti;
	}

	public void setUti(String uti) {
		this.uti = uti;
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

	public String getCardHolderVerification() {
		return cardHolderVerification;
	}

	public void setCardHolderVerification(String cardHolderVerification) {
		this.cardHolderVerification = cardHolderVerification;
	}

	public String getEnableEMV() {
		return enableEMV;
	}

	public void setEnableEMV(String enableEMV) {
		this.enableEMV = enableEMV;
	}

	public String getRespDe15() {
		return respDe15;
	}

	public void setRespDe15(String respDe15) {
		this.respDe15 = respDe15;
	}

	public String getReqDe4() {
		return reqDe4;
	}

	public void setReqDe4(String reqDe4) {
		this.reqDe4 = reqDe4;
	}

	public String getCaptureMode() {
		return captureMode;
	}

	public void setCaptureMode(String captureMode) {
		this.captureMode = captureMode;
	}

	public String getRespDe2() {
		return respDe2;
	}

	public void setRespDe2(String respDe2) {
		this.respDe2 = respDe2;
	}

	public String getRespDe3() {
		return respDe3;
	}

	public void setRespDe3(String respDe3) {
		this.respDe3 = respDe3;
	}

	public String getRespDe4() {
		return respDe4;
	}

	public void setRespDe4(String respDe4) {
		this.respDe4 = respDe4;
	}

	public String getRespDe24() {
		return respDe24;
	}

	public void setRespDe24(String respDe24) {
		this.respDe24 = respDe24;
	}

	public String getRespDe123() {
		return respDe123;
	}

	public void setRespDe123(String respDe123) {
		this.respDe123 = respDe123;
	}

	public String getRespDe126() {
		return respDe126;
	}

	public void setRespDe126(String respDe126) {
		this.respDe126 = respDe126;
	}

	public String getRespDe56() {
		return respDe56;
	}

	public void setRespDe56(String respDe56) {
		this.respDe56 = respDe56;
	}

	public String getRespDe25() {
		return respDe25;
	}

	public void setRespDe25(String respDe25) {
		this.respDe25 = respDe25;
	}

	public String getRespDe54() {
		return respDe54;
	}

	public void setRespDe54(String respDe54) {
		this.respDe54 = respDe54;
	}

	public String getIin() {
		return iin;
	}

	public void setIin(String iin) {
		this.iin = iin;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getMerchantAccNo() {
		return merchantAccNo;
	}

	public void setMerchantAccNo(String merchantAccNo) {
		this.merchantAccNo = merchantAccNo;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getTxnAmt() {
		return txnAmt;
	}

	public void setTxnAmt(String txnAmt) {
		this.txnAmt = txnAmt;
	}

	public String getSmsDT() {
		return smsDT;
	}

	public void setSmsDT(String smsDT) {
		this.smsDT = smsDT;
	}

	public String getTerSerialNo() {
		return terSerialNo;
	}

	public void setTerSerialNo(String terSerialNo) {
		this.terSerialNo = terSerialNo;
	}

	private String keyEntry;
	private String magneticStripe;
	private String icContact;
	private String cardHolderVerification;
	private String enableEMV;
	private String respDe15;

	private String reqDe4;
	private String captureMode;

	// Zameer Katwal
	private String respDe2;
	private String respDe3;
	private String respDe4;
	private String respDe24;
	private String respDe123;
	private String respDe126;
	private String respDe56;
	private String respDe25;
	private String respDe54;
	private String iin;
	private String accountNo;
	private String merchantAccNo;
	private String cardNo;

	private String txnAmt;
	private String smsDT;

	// adding Terminal serial number
	private String terSerialNo;

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getTranId() {
		return tranId;
	}

	public void setTranId(String tranId) {
		this.tranId = tranId;
	}

	public String getRespDe62() {
		return respDe62;
	}

	public void setRespDe62(String respDe62) {
		this.respDe62 = respDe62;
	}

	public String getReqDe25() {
		return reqDe25;
	}

	public void setReqDe25(String reqDe25) {
		this.reqDe25 = reqDe25;
	}

	private Map<EmvTags, String> emvMap;

	public String getCurrentMerchantDate() {
		return currentMerchantDate;
	}

	public void setCurrentMerchantDate(String currentMerchantDate) {
		this.currentMerchantDate = currentMerchantDate;
	}

	public String getCurrentMerchantTime() {
		return currentMerchantTime;
	}

	public void setCurrentMerchantTime(String currentMerchantTime) {
		this.currentMerchantTime = currentMerchantTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getMsgProtocol() {
		return msgProtocol;
	}

	public void setMsgProtocol(String msgProtocol) {
		this.msgProtocol = msgProtocol;
	}

	public String getAcqInstId() {
		return acqInstId;
	}

	public void setAcqInstId(String acqInstId) {
		this.acqInstId = acqInstId;
	}

	public String getAcquirerName() {
		return acquirerName;
	}

	public void setAcquirerName(String acquirerName) {
		this.acquirerName = acquirerName;
	}

	public String getRrn() {
		return rrn;
	}

	public void setRrn(String rrn) {
		this.rrn = rrn;
	}

	public String getAlternateStationName() {
		return alternateStationName;
	}

	public void setAlternateStationName(String alternateStationName) {
		this.alternateStationName = alternateStationName;
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

	public String getOperatingEnvironment() {
		return operatingEnvironment;
	}

	public void setOperatingEnvironment(String operatingEnvironment) {
		this.operatingEnvironment = operatingEnvironment;
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

	public String getStan() {
		return stan;
	}

	public void setStan(String stan) {
		this.stan = stan;
	}

	public String getTransmissionDt() {
		return transmissionDt;
	}

	public void setTransmissionDt(String transmissionDt) {
		this.transmissionDt = transmissionDt;
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

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public Map<EmvTags, String> getEmvMap() {
		return emvMap;
	}

	public void setEmvMap(Map<EmvTags, String> emvMap) {
		this.emvMap = emvMap;
	}

	public String getOriginalMsgType() {
		return originalMsgType;
	}

	public void setOriginalMsgType(String originalMsgType) {
		this.originalMsgType = originalMsgType;
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

	public String getRespDe48() {
		return respDe48;
	}

	public void setRespDe48(String respDe48) {
		this.respDe48 = respDe48;
	}

	public String getTxnDateTime() {
		return txnDateTime;
	}

	public void setTxnDateTime(String txnDateTime) {
		this.txnDateTime = txnDateTime;
	}

	public String getReqDe12() {
		return reqDe12;
	}

	public void setReqDe12(String reqDe12) {
		this.reqDe12 = reqDe12;
	}

	public String getReqDe22() {
		return reqDe22;
	}

	public void setReqDe22(String reqDe22) {
		this.reqDe22 = reqDe22;
	}

	public String getOrgTransmissionDY() {
		return orgTransmissionDY;
	}

	public void setOrgTransmissionDY(String orgTransmissionDY) {
		this.orgTransmissionDY = orgTransmissionDY;
	}

	public String getOrgTransmissionDt() {
		return orgTransmissionDt;
	}

	public void setOrgTransmissionDt(String orgTransmissionDt) {
		this.orgTransmissionDt = orgTransmissionDt;
	}

	public String getOrgStan() {
		return orgStan;
	}

	public void setOrgStan(String orgStan) {
		this.orgStan = orgStan;
	}

	public String getOrgAuthCode() {
		return orgAuthCode;
	}

	public void setOrgAuthCode(String orgAuthCode) {
		this.orgAuthCode = orgAuthCode;
	}

	public String getOrgProcCode() {
		return orgProcCode;
	}

	public void setOrgProcCode(String orgProcCode) {
		this.orgProcCode = orgProcCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getReqDe35() {
		return reqDe35;
	}

	public void setReqDe35(String reqDe35) {
		this.reqDe35 = reqDe35;
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

	public String getRespDe100() {
		return respDe100;
	}

	public void setRespDe100(String respDe100) {
		this.respDe100 = respDe100;
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

	public String getFiid() {
		return fiid;
	}

	public void setFiid(String fiid) {
		this.fiid = fiid;
	}

	public String getIsDCC() {
		return isDCC;
	}

	public void setIsDCC(String isDCC) {
		this.isDCC = isDCC;
	}

	public String getReqDe43() {
		return reqDe43;
	}

	public void setReqDe43(String reqDe43) {
		this.reqDe43 = reqDe43;
	}

	public String getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(String quotationId) {
		this.quotationId = quotationId;
	}

	public String getCntryName() {
		return cntryName;
	}

	public void setCntryName(String cntryName) {
		this.cntryName = cntryName;
	}

	public String getReqDe23() {
		return reqDe23;
	}

	public void setReqDe23(String reqDe23) {
		this.reqDe23 = reqDe23;
	}

	public String getRespDe31() {
		return respDe31;
	}

	public void setRespDe31(String respDe31) {
		this.respDe31 = respDe31;
	}
}
