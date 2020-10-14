package com.fss.pos.client.services.download.parameter;

public class TerminalConfig {

	private String terminalCategory;
	private String messageFormat;
	private String transactionCode;
	private String adminCode;
	private String amountDualEntry;
	private String displayMagneticStrip;
	private String tipProcessing;
	private String printReceipt;
	private String receiptType;
	private String enableLoginOTP;
	private String businessDateFormat;
	private String cardHolderVerification;
	private String ecrRef;
	private String printerUsed;
	private String invoiceNumberRequired;
	private String emPurchase;
	private String emTIP;
	private String emVoid;
	private String emRefund;
	private String emPreAuth;
	private String emCompletion;
	private String emPurchasewithCashback;
	private String emCashAdvance;
	private String emBalanceInquiry;
	private String emMOTO;
	private String emCashRecording;
	private String emCashDeposit;
	private String enableGEOLimit;
	private String enableDUKPTforPIN;
	private String amountVerifyonPINPad;
	private String enableP2PE;
	private String enableRemoteKeyUpdate;
	private String cardReadfromPINPad;
	private String receiptLine2;
	private String receiptLine3;
	private String defaultMerchantName;
	private String currencySymbol;
	private String txnCurrencyDigits;
	private String currencyDecimal;
	private String languageIndicator;
	private String settlementCurrencyDigits;
	private String currencyName;
	private String keyboardLocked;
	private String voidCode;
	private String refundCode;
	private String adjustmentCode;
	private String defaultTxn;
	private String debitIssuerId;
	private String debitAcquirerId;
	private String phoneLinehold;
	private String etPurchase;
	private String etTIP;
	private String etVoid;
	private String etRefund;
	private String etPreAuth;
	private String etCompletion;
	private String etPurchasewithCashback;
	private String etCashAdvance;
	private String etBalanceInquiry;
	private String etMOTO;
	private String etCashRecording;
	private String etCashDeposit;
	private String epPurchase;
	private String epTIP;
	private String epVoid;
	private String epRefund;
	private String epPreAuth;
	private String epCompletion;
	private String epPurchasewithCashback;
	private String epCashAdvance;
	private String epBalanceInquiry;
	private String epMOTO;
	private String epCashRecording;
	private String epCashDeposit;
	private String eoPurchase;
	private String eoTIP;
	private String eoCashDeposit;
	private String eoVoid;
	private String eoPreAuth;
	private String eoBalanceInquiry;
	private String eoRefund;
	private String eoCompletion;
	private String eoCashRecording;
	private String eoPurchasewithCashback;
	private String eoCashAdvance;
	private String eoMOTO;
	private String merchantSurcharge;
	private String terminalType;
	private String supervisorCode;
	private String espManual;
	private String espOffline;
	private String espMOTO;
	private String espCashTxn;
	private String espTIP;
	private String espVoid;
	private String espRefund;
	private String customerActivated;
	private String termOnlineCapable;
	private String onlinePINVerification;
	private String offlinePINVerification;
	private String enableEMV;
	private String allowPINBypass;
	private String earlyAmountEMV;
	private String currencyCode;
	private String countryCode;
	private String merchantCategoryCode;
	private String keyEntry;
	private String magneticStripe;
	private String icContact;
	private String sda;
	private String dda;
	private String cda;
	private String plainPINICC;
	private String encipherOnline;
	private String encipherOffline;
	private String signature;
	private String merchantId;
	private String pinEncryptionKeyScheme;
	private String p2peEncryptionKeyScheme;
	private String scheduleKeyInterval;
	private String batchNo;
	private String nextBatchNo;
	private String SettleTime;
	private String busStartTime;
	private String busEndTime;
	private String merchantDateTime;
	private String enableEMI;
	private String helpDeskPhone;

	// Txn Limit
	private String cashbackLimit;
	private String completionLimit;
	private String tipAdjustmentLimit;
	private String merchantSurchargeLimit;
	private String cashDepositLimit;
	private String cashAdvanceLimit;
	private String purchaseLimit;
	private String motoLimit;
	private String debitTxnCount;
	private String creditTxnCount;
	private String offlineTxnCount;
	private String maxReversalTxn;
	private String txnVelocity;
	private String magPIN;
	private String contactLessPIN;
	private String contactBasedPIN;
	private String refundLimit;
	private String txnInflightTime;
	private String offlineFloorLmt;
	private String enableGeo;
	private String enableDCC;
	private String amexMerchantId;

	public String getTerminalCategory() {
		return terminalCategory;
	}

	public void setTerminalCategory(String terminalCategory) {
		this.terminalCategory = terminalCategory;
	}

	public String getMessageFormat() {
		return messageFormat;
	}

	public void setMessageFormat(String messageFormat) {
		this.messageFormat = messageFormat;
	}

	public String getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}

	public String getAdminCode() {
		return adminCode;
	}

	public void setAdminCode(String adminCode) {
		this.adminCode = adminCode;
	}

	public String getAmountDualEntry() {
		return amountDualEntry;
	}

	public void setAmountDualEntry(String amountDualEntry) {
		this.amountDualEntry = amountDualEntry;
	}

	public String getDisplayMagneticStrip() {
		return displayMagneticStrip;
	}

	public void setDisplayMagneticStrip(String displayMagneticStrip) {
		this.displayMagneticStrip = displayMagneticStrip;
	}

	public String getTipProcessing() {
		return tipProcessing;
	}

	public void setTipProcessing(String tipProcessing) {
		this.tipProcessing = tipProcessing;
	}

	public String getPrintReceipt() {
		return printReceipt;
	}

	public void setPrintReceipt(String printReceipt) {
		this.printReceipt = printReceipt;
	}

	public String getReceiptType() {
		return receiptType;
	}

	public void setReceiptType(String receiptType) {
		this.receiptType = receiptType;
	}

	public String getEnableLoginOTP() {
		return enableLoginOTP;
	}

	public void setEnableLoginOTP(String enableLoginOTP) {
		this.enableLoginOTP = enableLoginOTP;
	}

	public String getBusinessDateFormat() {
		return businessDateFormat;
	}

	public void setBusinessDateFormat(String businessDateFormat) {
		this.businessDateFormat = businessDateFormat;
	}

	public String getCardHolderVerification() {
		return cardHolderVerification;
	}

	public void setCardHolderVerification(String cardHolderVerification) {
		this.cardHolderVerification = cardHolderVerification;
	}

	public String getEcrRef() {
		return ecrRef;
	}

	public void setEcrRef(String ecrRef) {
		this.ecrRef = ecrRef;
	}

	public String getPrinterUsed() {
		return printerUsed;
	}

	public void setPrinterUsed(String printerUsed) {
		this.printerUsed = printerUsed;
	}

	public String getInvoiceNumberRequired() {
		return invoiceNumberRequired;
	}

	public void setInvoiceNumberRequired(String invoiceNumberRequired) {
		this.invoiceNumberRequired = invoiceNumberRequired;
	}

	public String getEmPurchase() {
		return emPurchase;
	}

	public void setEmPurchase(String emPurchase) {
		this.emPurchase = emPurchase;
	}

	public String getEmTIP() {
		return emTIP;
	}

	public void setEmTIP(String emTIP) {
		this.emTIP = emTIP;
	}

	public String getEmVoid() {
		return emVoid;
	}

	public void setEmVoid(String emVoid) {
		this.emVoid = emVoid;
	}

	public String getEmRefund() {
		return emRefund;
	}

	public void setEmRefund(String emRefund) {
		this.emRefund = emRefund;
	}

	public String getEmPreAuth() {
		return emPreAuth;
	}

	public void setEmPreAuth(String emPreAuth) {
		this.emPreAuth = emPreAuth;
	}

	public String getEmCompletion() {
		return emCompletion;
	}

	public void setEmCompletion(String emCompletion) {
		this.emCompletion = emCompletion;
	}

	public String getEmPurchasewithCashback() {
		return emPurchasewithCashback;
	}

	public void setEmPurchasewithCashback(String emPurchasewithCashback) {
		this.emPurchasewithCashback = emPurchasewithCashback;
	}

	public String getEmCashAdvance() {
		return emCashAdvance;
	}

	public void setEmCashAdvance(String emCashAdvance) {
		this.emCashAdvance = emCashAdvance;
	}

	public String getEmBalanceInquiry() {
		return emBalanceInquiry;
	}

	public void setEmBalanceInquiry(String emBalanceInquiry) {
		this.emBalanceInquiry = emBalanceInquiry;
	}

	public String getEmMOTO() {
		return emMOTO;
	}

	public void setEmMOTO(String emMOTO) {
		this.emMOTO = emMOTO;
	}

	public String getEmCashRecording() {
		return emCashRecording;
	}

	public void setEmCashRecording(String emCashRecording) {
		this.emCashRecording = emCashRecording;
	}

	public String getEmCashDeposit() {
		return emCashDeposit;
	}

	public void setEmCashDeposit(String emCashDeposit) {
		this.emCashDeposit = emCashDeposit;
	}

	public String getEnableGEOLimit() {
		return enableGEOLimit;
	}

	public void setEnableGEOLimit(String enableGEOLimit) {
		this.enableGEOLimit = enableGEOLimit;
	}

	public String getEnableDUKPTforPIN() {
		return enableDUKPTforPIN;
	}

	public void setEnableDUKPTforPIN(String enableDUKPTforPIN) {
		this.enableDUKPTforPIN = enableDUKPTforPIN;
	}

	public String getAmountVerifyonPINPad() {
		return amountVerifyonPINPad;
	}

	public void setAmountVerifyonPINPad(String amountVerifyonPINPad) {
		this.amountVerifyonPINPad = amountVerifyonPINPad;
	}

	public String getEnableP2PE() {
		return enableP2PE;
	}

	public void setEnableP2PE(String enableP2PE) {
		this.enableP2PE = enableP2PE;
	}

	public String getEnableRemoteKeyUpdate() {
		return enableRemoteKeyUpdate;
	}

	public void setEnableRemoteKeyUpdate(String enableRemoteKeyUpdate) {
		this.enableRemoteKeyUpdate = enableRemoteKeyUpdate;
	}

	public String getCardReadfromPINPad() {
		return cardReadfromPINPad;
	}

	public void setCardReadfromPINPad(String cardReadfromPINPad) {
		this.cardReadfromPINPad = cardReadfromPINPad;
	}

	public String getReceiptLine2() {
		return receiptLine2;
	}

	public void setReceiptLine2(String receiptLine2) {
		this.receiptLine2 = receiptLine2;
	}

	public String getReceiptLine3() {
		return receiptLine3;
	}

	public void setReceiptLine3(String receiptLine3) {
		this.receiptLine3 = receiptLine3;
	}

	public String getDefaultMerchantName() {
		return defaultMerchantName;
	}

	public void setDefaultMerchantName(String defaultMerchantName) {
		this.defaultMerchantName = defaultMerchantName;
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

	public String getTxnCurrencyDigits() {
		return txnCurrencyDigits;
	}

	public void setTxnCurrencyDigits(String txnCurrencyDigits) {
		this.txnCurrencyDigits = txnCurrencyDigits;
	}

	public String getCurrencyDecimal() {
		return currencyDecimal;
	}

	public void setCurrencyDecimal(String currencyDecimal) {
		this.currencyDecimal = currencyDecimal;
	}

	public String getLanguageIndicator() {
		return languageIndicator;
	}

	public void setLanguageIndicator(String languageIndicator) {
		this.languageIndicator = languageIndicator;
	}

	public String getSettlementCurrencyDigits() {
		return settlementCurrencyDigits;
	}

	public void setSettlementCurrencyDigits(String settlementCurrencyDigits) {
		this.settlementCurrencyDigits = settlementCurrencyDigits;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public String getKeyboardLocked() {
		return keyboardLocked;
	}

	public void setKeyboardLocked(String keyboardLocked) {
		this.keyboardLocked = keyboardLocked;
	}

	public String getVoidCode() {
		return voidCode;
	}

	public void setVoidCode(String voidCode) {
		this.voidCode = voidCode;
	}

	public String getRefundCode() {
		return refundCode;
	}

	public void setRefundCode(String refundCode) {
		this.refundCode = refundCode;
	}

	public String getAdjustmentCode() {
		return adjustmentCode;
	}

	public void setAdjustmentCode(String adjustmentCode) {
		this.adjustmentCode = adjustmentCode;
	}

	public String getDefaultTxn() {
		return defaultTxn;
	}

	public void setDefaultTxn(String defaultTxn) {
		this.defaultTxn = defaultTxn;
	}

	public String getDebitIssuerId() {
		return debitIssuerId;
	}

	public void setDebitIssuerId(String debitIssuerId) {
		this.debitIssuerId = debitIssuerId;
	}

	public String getDebitAcquirerId() {
		return debitAcquirerId;
	}

	public void setDebitAcquirerId(String debitAcquirerId) {
		this.debitAcquirerId = debitAcquirerId;
	}

	public String getPhoneLinehold() {
		return phoneLinehold;
	}

	public void setPhoneLinehold(String phoneLinehold) {
		this.phoneLinehold = phoneLinehold;
	}

	public String getEtPurchase() {
		return etPurchase;
	}

	public void setEtPurchase(String etPurchase) {
		this.etPurchase = etPurchase;
	}

	public String getEtTIP() {
		return etTIP;
	}

	public void setEtTIP(String etTIP) {
		this.etTIP = etTIP;
	}

	public String getEtVoid() {
		return etVoid;
	}

	public void setEtVoid(String etVoid) {
		this.etVoid = etVoid;
	}

	public String getEtRefund() {
		return etRefund;
	}

	public void setEtRefund(String etRefund) {
		this.etRefund = etRefund;
	}

	public String getEtPreAuth() {
		return etPreAuth;
	}

	public void setEtPreAuth(String etPreAuth) {
		this.etPreAuth = etPreAuth;
	}

	public String getEtCompletion() {
		return etCompletion;
	}

	public void setEtCompletion(String etCompletion) {
		this.etCompletion = etCompletion;
	}

	public String getEtPurchasewithCashback() {
		return etPurchasewithCashback;
	}

	public void setEtPurchasewithCashback(String etPurchasewithCashback) {
		this.etPurchasewithCashback = etPurchasewithCashback;
	}

	public String getEtCashAdvance() {
		return etCashAdvance;
	}

	public void setEtCashAdvance(String etCashAdvance) {
		this.etCashAdvance = etCashAdvance;
	}

	public String getEtBalanceInquiry() {
		return etBalanceInquiry;
	}

	public void setEtBalanceInquiry(String etBalanceInquiry) {
		this.etBalanceInquiry = etBalanceInquiry;
	}

	public String getEtMOTO() {
		return etMOTO;
	}

	public void setEtMOTO(String etMOTO) {
		this.etMOTO = etMOTO;
	}

	public String getEtCashRecording() {
		return etCashRecording;
	}

	public void setEtCashRecording(String etCashRecording) {
		this.etCashRecording = etCashRecording;
	}

	public String getEtCashDeposit() {
		return etCashDeposit;
	}

	public void setEtCashDeposit(String etCashDeposit) {
		this.etCashDeposit = etCashDeposit;
	}

	public String getEpPurchase() {
		return epPurchase;
	}

	public void setEpPurchase(String epPurchase) {
		this.epPurchase = epPurchase;
	}

	public String getEpTIP() {
		return epTIP;
	}

	public void setEpTIP(String epTIP) {
		this.epTIP = epTIP;
	}

	public String getEpVoid() {
		return epVoid;
	}

	public void setEpVoid(String epVoid) {
		this.epVoid = epVoid;
	}

	public String getEpRefund() {
		return epRefund;
	}

	public void setEpRefund(String epRefund) {
		this.epRefund = epRefund;
	}

	public String getEpPreAuth() {
		return epPreAuth;
	}

	public void setEpPreAuth(String epPreAuth) {
		this.epPreAuth = epPreAuth;
	}

	public String getEpCompletion() {
		return epCompletion;
	}

	public void setEpCompletion(String epCompletion) {
		this.epCompletion = epCompletion;
	}

	public String getEpPurchasewithCashback() {
		return epPurchasewithCashback;
	}

	public void setEpPurchasewithCashback(String epPurchasewithCashback) {
		this.epPurchasewithCashback = epPurchasewithCashback;
	}

	public String getEpCashAdvance() {
		return epCashAdvance;
	}

	public void setEpCashAdvance(String epCashAdvance) {
		this.epCashAdvance = epCashAdvance;
	}

	public String getEpBalanceInquiry() {
		return epBalanceInquiry;
	}

	public void setEpBalanceInquiry(String epBalanceInquiry) {
		this.epBalanceInquiry = epBalanceInquiry;
	}

	public String getEpMOTO() {
		return epMOTO;
	}

	public void setEpMOTO(String epMOTO) {
		this.epMOTO = epMOTO;
	}

	public String getEpCashRecording() {
		return epCashRecording;
	}

	public void setEpCashRecording(String epCashRecording) {
		this.epCashRecording = epCashRecording;
	}

	public String getEpCashDeposit() {
		return epCashDeposit;
	}

	public void setEpCashDeposit(String epCashDeposit) {
		this.epCashDeposit = epCashDeposit;
	}

	public String getEoPurchase() {
		return eoPurchase;
	}

	public void setEoPurchase(String eoPurchase) {
		this.eoPurchase = eoPurchase;
	}

	public String getEoTIP() {
		return eoTIP;
	}

	public void setEoTIP(String eoTIP) {
		this.eoTIP = eoTIP;
	}

	public String getEoCashDeposit() {
		return eoCashDeposit;
	}

	public void setEoCashDeposit(String eoCashDeposit) {
		this.eoCashDeposit = eoCashDeposit;
	}

	public String getEoVoid() {
		return eoVoid;
	}

	public void setEoVoid(String eoVoid) {
		this.eoVoid = eoVoid;
	}

	public String getEoRefund() {
		return eoRefund;
	}

	public void setEoRefund(String eoRefund) {
		this.eoRefund = eoRefund;
	}

	public String getEoCompletion() {
		return eoCompletion;
	}

	public void setEoCompletion(String eoCompletion) {
		this.eoCompletion = eoCompletion;
	}

	public String getEoPurchasewithCashback() {
		return eoPurchasewithCashback;
	}

	public void setEoPurchasewithCashback(String eoPurchasewithCashback) {
		this.eoPurchasewithCashback = eoPurchasewithCashback;
	}

	public String getEoCashAdvance() {
		return eoCashAdvance;
	}

	public void setEoCashAdvance(String eoCashAdvance) {
		this.eoCashAdvance = eoCashAdvance;
	}

	public String getEoMOTO() {
		return eoMOTO;
	}

	public void setEoMOTO(String eoMOTO) {
		this.eoMOTO = eoMOTO;
	}

	public String getMerchantSurcharge() {
		return merchantSurcharge;
	}

	public void setMerchantSurcharge(String merchantSurcharge) {
		this.merchantSurcharge = merchantSurcharge;
	}

	public String getTerminalType() {
		return terminalType;
	}

	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}

	public String getSupervisorCode() {
		return supervisorCode;
	}

	public void setSupervisorCode(String supervisorCode) {
		this.supervisorCode = supervisorCode;
	}

	public String getEspManual() {
		return espManual;
	}

	public void setEspManual(String espManual) {
		this.espManual = espManual;
	}

	public String getEspOffline() {
		return espOffline;
	}

	public void setEspOffline(String espOffline) {
		this.espOffline = espOffline;
	}

	public String getEspMOTO() {
		return espMOTO;
	}

	public void setEspMOTO(String espMOTO) {
		this.espMOTO = espMOTO;
	}

	public String getEspCashTxn() {
		return espCashTxn;
	}

	public void setEspCashTxn(String espCashTxn) {
		this.espCashTxn = espCashTxn;
	}

	public String getEspTIP() {
		return espTIP;
	}

	public void setEspTIP(String espTIP) {
		this.espTIP = espTIP;
	}

	public String getEspVoid() {
		return espVoid;
	}

	public void setEspVoid(String espVoid) {
		this.espVoid = espVoid;
	}

	public String getEspRefund() {
		return espRefund;
	}

	public void setEspRefund(String espRefund) {
		this.espRefund = espRefund;
	}

	public String getCustomerActivated() {
		return customerActivated;
	}

	public void setCustomerActivated(String customerActivated) {
		this.customerActivated = customerActivated;
	}

	public String getTermOnlineCapable() {
		return termOnlineCapable;
	}

	public void setTermOnlineCapable(String termOnlineCapable) {
		this.termOnlineCapable = termOnlineCapable;
	}

	public String getOnlinePINVerification() {
		return onlinePINVerification;
	}

	public void setOnlinePINVerification(String onlinePINVerification) {
		this.onlinePINVerification = onlinePINVerification;
	}

	public String getOfflinePINVerification() {
		return offlinePINVerification;
	}

	public void setOfflinePINVerification(String offlinePINVerification) {
		this.offlinePINVerification = offlinePINVerification;
	}

	public String getEnableEMV() {
		return enableEMV;
	}

	public void setEnableEMV(String enableEMV) {
		this.enableEMV = enableEMV;
	}

	public String getAllowPINBypass() {
		return allowPINBypass;
	}

	public void setAllowPINBypass(String allowPINBypass) {
		this.allowPINBypass = allowPINBypass;
	}

	public String getEarlyAmountEMV() {
		return earlyAmountEMV;
	}

	public void setEarlyAmountEMV(String earlyAmountEMV) {
		this.earlyAmountEMV = earlyAmountEMV;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getMerchantCategoryCode() {
		return merchantCategoryCode;
	}

	public void setMerchantCategoryCode(String merchantCategoryCode) {
		this.merchantCategoryCode = merchantCategoryCode;
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

	public String getSda() {
		return sda;
	}

	public void setSda(String sda) {
		this.sda = sda;
	}

	public String getDda() {
		return dda;
	}

	public void setDda(String dda) {
		this.dda = dda;
	}

	public String getCda() {
		return cda;
	}

	public void setCda(String cda) {
		this.cda = cda;
	}

	public String getPlainPINICC() {
		return plainPINICC;
	}

	public void setPlainPINICC(String plainPINICC) {
		this.plainPINICC = plainPINICC;
	}

	public String getEncipherOnline() {
		return encipherOnline;
	}

	public void setEncipherOnline(String encipherOnline) {
		this.encipherOnline = encipherOnline;
	}

	public String getEncipherOffline() {
		return encipherOffline;
	}

	public void setEncipherOffline(String encipherOffline) {
		this.encipherOffline = encipherOffline;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getPinEncryptionKeyScheme() {
		return pinEncryptionKeyScheme;
	}

	public void setPinEncryptionKeyScheme(String pinEncryptionKeyScheme) {
		this.pinEncryptionKeyScheme = pinEncryptionKeyScheme;
	}

	public String getP2peEncryptionKeyScheme() {
		return p2peEncryptionKeyScheme;
	}

	public void setP2peEncryptionKeyScheme(String p2peEncryptionKeyScheme) {
		this.p2peEncryptionKeyScheme = p2peEncryptionKeyScheme;
	}

	public String getScheduleKeyInterval() {
		return scheduleKeyInterval;
	}

	public void setScheduleKeyInterval(String scheduleKeyInterval) {
		this.scheduleKeyInterval = scheduleKeyInterval;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getNextBatchNo() {
		return nextBatchNo;
	}

	public void setNextBatchNo(String nextBatchNo) {
		this.nextBatchNo = nextBatchNo;
	}

	public String getSettleTime() {
		return SettleTime;
	}

	public void setSettleTime(String settleTime) {
		SettleTime = settleTime;
	}

	public String getBusStartTime() {
		return busStartTime;
	}

	public void setBusStartTime(String busStartTime) {
		this.busStartTime = busStartTime;
	}

	public String getBusEndTime() {
		return busEndTime;
	}

	public void setBusEndTime(String busEndTime) {
		this.busEndTime = busEndTime;
	}

	public String getCashbackLimit() {
		return cashbackLimit;
	}

	public void setCashbackLimit(String cashbackLimit) {
		this.cashbackLimit = cashbackLimit;
	}

	public String getCompletionLimit() {
		return completionLimit;
	}

	public void setCompletionLimit(String completionLimit) {
		this.completionLimit = completionLimit;
	}

	public String getTipAdjustmentLimit() {
		return tipAdjustmentLimit;
	}

	public void setTipAdjustmentLimit(String tipAdjustmentLimit) {
		this.tipAdjustmentLimit = tipAdjustmentLimit;
	}

	public String getMerchantSurchargeLimit() {
		return merchantSurchargeLimit;
	}

	public void setMerchantSurchargeLimit(String merchantSurchargeLimit) {
		this.merchantSurchargeLimit = merchantSurchargeLimit;
	}

	public String getCashDepositLimit() {
		return cashDepositLimit;
	}

	public void setCashDepositLimit(String cashDepositLimit) {
		this.cashDepositLimit = cashDepositLimit;
	}

	public String getCashAdvanceLimit() {
		return cashAdvanceLimit;
	}

	public void setCashAdvanceLimit(String cashAdvanceLimit) {
		this.cashAdvanceLimit = cashAdvanceLimit;
	}

	public String getPurchaseLimit() {
		return purchaseLimit;
	}

	public void setPurchaseLimit(String purchaseLimit) {
		this.purchaseLimit = purchaseLimit;
	}

	public String getMotoLimit() {
		return motoLimit;
	}

	public void setMotoLimit(String motoLimit) {
		this.motoLimit = motoLimit;
	}

	public String getDebitTxnCount() {
		return debitTxnCount;
	}

	public void setDebitTxnCount(String debitTxnCount) {
		this.debitTxnCount = debitTxnCount;
	}

	public String getCreditTxnCount() {
		return creditTxnCount;
	}

	public void setCreditTxnCount(String creditTxnCount) {
		this.creditTxnCount = creditTxnCount;
	}

	public String getOfflineTxnCount() {
		return offlineTxnCount;
	}

	public void setOfflineTxnCount(String offlineTxnCount) {
		this.offlineTxnCount = offlineTxnCount;
	}

	public String getMaxReversalTxn() {
		return maxReversalTxn;
	}

	public void setMaxReversalTxn(String maxReversalTxn) {
		this.maxReversalTxn = maxReversalTxn;
	}

	public String getTxnVelocity() {
		return txnVelocity;
	}

	public void setTxnVelocity(String txnVelocity) {
		this.txnVelocity = txnVelocity;
	}

	public String getMagPIN() {
		return magPIN;
	}

	public void setMagPIN(String magPIN) {
		this.magPIN = magPIN;
	}

	public String getContactLessPIN() {
		return contactLessPIN;
	}

	public void setContactLessPIN(String contactLessPIN) {
		this.contactLessPIN = contactLessPIN;
	}

	public String getContactBasedPIN() {
		return contactBasedPIN;
	}

	public void setContactBasedPIN(String contactBasedPIN) {
		this.contactBasedPIN = contactBasedPIN;
	}

	public String getRefundLimit() {
		return refundLimit;
	}

	public void setRefundLimit(String refundLimit) {
		this.refundLimit = refundLimit;
	}

	public String getTxnInflightTime() {
		return txnInflightTime;
	}

	public void setTxnInflightTime(String txnInflightTime) {
		this.txnInflightTime = txnInflightTime;
	}

	public String getOfflineFloorLmt() {
		return offlineFloorLmt;
	}

	public void setOfflineFloorLmt(String offlineFloorLmt) {
		this.offlineFloorLmt = offlineFloorLmt;
	}

	public String getEnableGeo() {
		return enableGeo;
	}

	public void setEnableGeo(String enableGeo) {
		this.enableGeo = enableGeo;
	}

	public String getEoPreAuth() {
		return eoPreAuth;
	}

	public void setEoPreAuth(String eoPreAuth) {
		this.eoPreAuth = eoPreAuth;
	}

	public String getEoBalanceInquiry() {
		return eoBalanceInquiry;
	}

	public void setEoBalanceInquiry(String eoBalanceInquiry) {
		this.eoBalanceInquiry = eoBalanceInquiry;
	}

	public String getEoCashRecording() {
		return eoCashRecording;
	}

	public void setEoCashRecording(String eoCashRecording) {
		this.eoCashRecording = eoCashRecording;
	}

	public String getMerchantDateTime() {
		return merchantDateTime;
	}

	public void setMerchantDateTime(String merchantDateTime) {
		this.merchantDateTime = merchantDateTime;
	}

	public String getEnableEMI() {
		return enableEMI;
	}

	public void setEnableEMI(String enableEMI) {
		this.enableEMI = enableEMI;
	}

	public String getHelpDeskPhone() {
		return helpDeskPhone;
	}

	public void setHelpDeskPhone(String helpDeskPhone) {
		this.helpDeskPhone = helpDeskPhone;
	}

	public String getEnableDCC() {
		return enableDCC;
	}

	public void setEnableDCC(String enableDCC) {
		this.enableDCC = enableDCC;
	}

	public String getAmexMerchantId() {
		return amexMerchantId;
	}

	public void setAmexMerchantId(String amexMerchantId) {
		this.amexMerchantId = amexMerchantId;
	}

}
