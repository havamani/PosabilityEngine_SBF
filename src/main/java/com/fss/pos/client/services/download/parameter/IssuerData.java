package com.fss.pos.client.services.download.parameter;

public class IssuerData {

	private String rpIdPK;
	private String rpName;
	private String selectAccType;
	private String pinEntry;
	private String manualPanAlwd;
	private String expdtReqforMan;
	private String offlineEntryAlwd;
	private String motoAlwd;
	private String purchaseAlwd;
	private String tipAlwd;
	private String invoiceReq;
	private String printReceipt;
	private String captureTxn;
	private String chkExpiryDate;
	private String balanceInqAlwd;
	private String blockRefund;
	private String blockCardVerfy;
	private String voidAlwd;
	private String cashBackAlwd;
	private String preAuthAlwd;
	private String completionAlwd;
	private String refundProcsOffline;
	private String voidProcsOffline;
	private String allwCashOlyTxn;
	private String verifylast4Pan;
	private String authOvrFloorLmt;
	private String reprintAlwd;
	private String cashAdvanceAlwd;
	private String cashDepositAlwd;
	private String promptProdCodes;
	private String offlineAlwdPurchase;
	private String offlineAlwdip;
	private String offlineAlwdCompl;
	private String offlineAlwdcashback;
	private String offlineAlwdCashAdv;
	private String offlineAlwdMoto;
	private String offlineAlwdPreAuth;
	private String offlineAlwdBalanceInquiry;
	private String offlineAlwdCashRecording;
	private String manAlwdPurchase;
	private String manAlwdRefund;
	private String manAlwdPreAuth;
	private String manAlwdCompletion;
	private String manAlwdCashback;
	private String manAlwdCashAdv;
	private String manAlwdBalanceInq;
	private String manAlwdCashDepst;
	private String manAlwdMoto;
	private String manAlwdCashRecording;
	private String pinDebit;
	private String pinReturn;
	private String pinVoid;
	private String pinTIP;
	private String pinCashback;
	private String pinBalnceInq;
	private String pinPreAuth;
	private String pinPurchase;
	private String pinCompletion;
	private String pinCashAdvance;
	private String pinMOTO;
	private String pinCashDeposit;
	private String cardType;
	private String dnotCapdebitTxn;
	private String ebtProcess;
	private String numberFormat;
	private String addRefundAmtPrompts;
	private String purchaseOrdrNoFormat;
	private String barNoFormat;
	private String disablrPrntngRRN;
	private String emiRange;
	private String authCode;
	private String debitTxn;
	private String floorLimit;
	private String manAlwdVoid;
	private String manAlwdTip;
	private String pinRefund;
	private String offlineAlwdVoid;
	private String offlineAlwdRefund;
	private String offlineAlwdCashDeposit;
	private String cashRecordingAlwd;
	private String refundAlwd;

	// ICC table entry
	private String tableNumber;
	private String iccOption1;
	private String iccOption2;
	private String iccOption3;
	private String targetPercentage;
	private String maxTargetPercentage;
	private String thresholdValue;
	private String tacDenial;
	private String tacOnline;
	private String tacDefault;
	private String transactionCategoryCode;

	public String getIccOption1() {
		return iccOption1;
	}

	public void setIccOption1(String iccOption1) {
		this.iccOption1 = iccOption1;
	}

	public String getIccOption2() {
		return iccOption2;
	}

	public void setIccOption2(String iccOption2) {
		this.iccOption2 = iccOption2;
	}

	public String getIccOption3() {
		return iccOption3;
	}

	public void setIccOption3(String iccOption3) {
		this.iccOption3 = iccOption3;
	}

	public String getTargetPercentage() {
		return targetPercentage;
	}

	public void setTargetPercentage(String targetPercentage) {
		this.targetPercentage = targetPercentage;
	}

	public String getMaxTargetPercentage() {
		return maxTargetPercentage;
	}

	public void setMaxTargetPercentage(String maxTargetPercentage) {
		this.maxTargetPercentage = maxTargetPercentage;
	}

	public String getThresholdValue() {
		return thresholdValue;
	}

	public void setThresholdValue(String thresholdValue) {
		this.thresholdValue = thresholdValue;
	}

	public String getTacDenial() {
		return tacDenial;
	}

	public void setTacDenial(String tacDenial) {
		this.tacDenial = tacDenial;
	}

	public String getTacOnline() {
		return tacOnline;
	}

	public void setTacOnline(String tacOnline) {
		this.tacOnline = tacOnline;
	}

	public String getTacDefault() {
		return tacDefault;
	}

	public void setTacDefault(String tacDefault) {
		this.tacDefault = tacDefault;
	}

	public String getTransactionCategoryCode() {
		return transactionCategoryCode;
	}

	public void setTransactionCategoryCode(String transactionCategoryCode) {
		this.transactionCategoryCode = transactionCategoryCode;
	}

	public String getRpIdPK() {
		return rpIdPK;
	}

	public void setRpIdPK(String rpIdPK) {
		this.rpIdPK = rpIdPK;
	}

	public String getRpName() {
		return rpName;
	}

	public void setRpName(String rpName) {
		this.rpName = rpName;
	}

	public String getSelectAccType() {
		return selectAccType;
	}

	public void setSelectAccType(String selectAccType) {
		this.selectAccType = selectAccType;
	}

	public String getPinEntry() {
		return pinEntry;
	}

	public void setPinEntry(String pinEntry) {
		this.pinEntry = pinEntry;
	}

	public String getManualPanAlwd() {
		return manualPanAlwd;
	}

	public void setManualPanAlwd(String manualPanAlwd) {
		this.manualPanAlwd = manualPanAlwd;
	}

	public String getExpdtReqforMan() {
		return expdtReqforMan;
	}

	public void setExpdtReqforMan(String expdtReqforMan) {
		this.expdtReqforMan = expdtReqforMan;
	}

	public String getOfflineEntryAlwd() {
		return offlineEntryAlwd;
	}

	public void setOfflineEntryAlwd(String offlineEntryAlwd) {
		this.offlineEntryAlwd = offlineEntryAlwd;
	}

	public String getMotoAlwd() {
		return motoAlwd;
	}

	public void setMotoAlwd(String motoAlwd) {
		this.motoAlwd = motoAlwd;
	}

	public String getPurchaseAlwd() {
		return purchaseAlwd;
	}

	public void setPurchaseAlwd(String purchaseAlwd) {
		this.purchaseAlwd = purchaseAlwd;
	}

	public String getTipAlwd() {
		return tipAlwd;
	}

	public void setTipAlwd(String tipAlwd) {
		this.tipAlwd = tipAlwd;
	}

	public String getInvoiceReq() {
		return invoiceReq;
	}

	public void setInvoiceReq(String invoiceReq) {
		this.invoiceReq = invoiceReq;
	}

	public String getPrintReceipt() {
		return printReceipt;
	}

	public void setPrintReceipt(String printReceipt) {
		this.printReceipt = printReceipt;
	}

	public String getCaptureTxn() {
		return captureTxn;
	}

	public void setCaptureTxn(String captureTxn) {
		this.captureTxn = captureTxn;
	}

	public String getChkExpiryDate() {
		return chkExpiryDate;
	}

	public void setChkExpiryDate(String chkExpiryDate) {
		this.chkExpiryDate = chkExpiryDate;
	}

	public String getBalanceInqAlwd() {
		return balanceInqAlwd;
	}

	public void setBalanceInqAlwd(String balanceInqAlwd) {
		this.balanceInqAlwd = balanceInqAlwd;
	}

	public String getBlockRefund() {
		return blockRefund;
	}

	public void setBlockRefund(String blockRefund) {
		this.blockRefund = blockRefund;
	}

	public String getBlockCardVerfy() {
		return blockCardVerfy;
	}

	public void setBlockCardVerfy(String blockCardVerfy) {
		this.blockCardVerfy = blockCardVerfy;
	}

	public String getVoidAlwd() {
		return voidAlwd;
	}

	public void setVoidAlwd(String voidAlwd) {
		this.voidAlwd = voidAlwd;
	}

	public String getCashBackAlwd() {
		return cashBackAlwd;
	}

	public void setCashBackAlwd(String cashBackAlwd) {
		this.cashBackAlwd = cashBackAlwd;
	}

	public String getPreAuthAlwd() {
		return preAuthAlwd;
	}

	public void setPreAuthAlwd(String preAuthAlwd) {
		this.preAuthAlwd = preAuthAlwd;
	}

	public String getCompletionAlwd() {
		return completionAlwd;
	}

	public void setCompletionAlwd(String completionAlwd) {
		this.completionAlwd = completionAlwd;
	}

	public String getRefundProcsOffline() {
		return refundProcsOffline;
	}

	public void setRefundProcsOffline(String refundProcsOffline) {
		this.refundProcsOffline = refundProcsOffline;
	}

	public String getVoidProcsOffline() {
		return voidProcsOffline;
	}

	public void setVoidProcsOffline(String voidProcsOffline) {
		this.voidProcsOffline = voidProcsOffline;
	}

	public String getAllwCashOlyTxn() {
		return allwCashOlyTxn;
	}

	public void setAllwCashOlyTxn(String allwCashOlyTxn) {
		this.allwCashOlyTxn = allwCashOlyTxn;
	}

	public String getVerifylast4Pan() {
		return verifylast4Pan;
	}

	public void setVerifylast4Pan(String verifylast4Pan) {
		this.verifylast4Pan = verifylast4Pan;
	}

	public String getAuthOvrFloorLmt() {
		return authOvrFloorLmt;
	}

	public void setAuthOvrFloorLmt(String authOvrFloorLmt) {
		this.authOvrFloorLmt = authOvrFloorLmt;
	}

	public String getReprintAlwd() {
		return reprintAlwd;
	}

	public void setReprintAlwd(String reprintAlwd) {
		this.reprintAlwd = reprintAlwd;
	}

	public String getCashAdvanceAlwd() {
		return cashAdvanceAlwd;
	}

	public void setCashAdvanceAlwd(String cashAdvanceAlwd) {
		this.cashAdvanceAlwd = cashAdvanceAlwd;
	}

	public String getCashDepositAlwd() {
		return cashDepositAlwd;
	}

	public void setCashDepositAlwd(String cashDepositAlwd) {
		this.cashDepositAlwd = cashDepositAlwd;
	}

	public String getPromptProdCodes() {
		return promptProdCodes;
	}

	public void setPromptProdCodes(String promptProdCodes) {
		this.promptProdCodes = promptProdCodes;
	}

	public String getOfflineAlwdPurchase() {
		return offlineAlwdPurchase;
	}

	public void setOfflineAlwdPurchase(String offlineAlwdPurchase) {
		this.offlineAlwdPurchase = offlineAlwdPurchase;
	}

	public String getOfflineAlwdip() {
		return offlineAlwdip;
	}

	public void setOfflineAlwdip(String offlineAlwdip) {
		this.offlineAlwdip = offlineAlwdip;
	}

	public String getOfflineAlwdCompl() {
		return offlineAlwdCompl;
	}

	public void setOfflineAlwdCompl(String offlineAlwdCompl) {
		this.offlineAlwdCompl = offlineAlwdCompl;
	}

	public String getOfflineAlwdcashback() {
		return offlineAlwdcashback;
	}

	public void setOfflineAlwdcashback(String offlineAlwdcashback) {
		this.offlineAlwdcashback = offlineAlwdcashback;
	}

	public String getOfflineAlwdCashAdv() {
		return offlineAlwdCashAdv;
	}

	public void setOfflineAlwdCashAdv(String offlineAlwdCashAdv) {
		this.offlineAlwdCashAdv = offlineAlwdCashAdv;
	}

	public String getOfflineAlwdMoto() {
		return offlineAlwdMoto;
	}

	public void setOfflineAlwdMoto(String offlineAlwdMoto) {
		this.offlineAlwdMoto = offlineAlwdMoto;
	}

	public String getManAlwdPurchase() {
		return manAlwdPurchase;
	}

	public void setManAlwdPurchase(String manAlwdPurchase) {
		this.manAlwdPurchase = manAlwdPurchase;
	}

	public String getManAlwdRefund() {
		return manAlwdRefund;
	}

	public void setManAlwdRefund(String manAlwdRefund) {
		this.manAlwdRefund = manAlwdRefund;
	}

	public String getManAlwdPreAuth() {
		return manAlwdPreAuth;
	}

	public void setManAlwdPreAuth(String manAlwdPreAuth) {
		this.manAlwdPreAuth = manAlwdPreAuth;
	}

	public String getManAlwdCompletion() {
		return manAlwdCompletion;
	}

	public void setManAlwdCompletion(String manAlwdCompletion) {
		this.manAlwdCompletion = manAlwdCompletion;
	}

	public String getManAlwdCashback() {
		return manAlwdCashback;
	}

	public void setManAlwdCashback(String manAlwdCashback) {
		this.manAlwdCashback = manAlwdCashback;
	}

	public String getManAlwdCashAdv() {
		return manAlwdCashAdv;
	}

	public void setManAlwdCashAdv(String manAlwdCashAdv) {
		this.manAlwdCashAdv = manAlwdCashAdv;
	}

	public String getManAlwdBalanceInq() {
		return manAlwdBalanceInq;
	}

	public void setManAlwdBalanceInq(String manAlwdBalanceInq) {
		this.manAlwdBalanceInq = manAlwdBalanceInq;
	}

	public String getManAlwdCashDepst() {
		return manAlwdCashDepst;
	}

	public void setManAlwdCashDepst(String manAlwdCashDepst) {
		this.manAlwdCashDepst = manAlwdCashDepst;
	}

	public String getPinDebit() {
		return pinDebit;
	}

	public void setPinDebit(String pinDebit) {
		this.pinDebit = pinDebit;
	}

	public String getPinReturn() {
		return pinReturn;
	}

	public void setPinReturn(String pinReturn) {
		this.pinReturn = pinReturn;
	}

	public String getPinVoid() {
		return pinVoid;
	}

	public void setPinVoid(String pinVoid) {
		this.pinVoid = pinVoid;
	}

	public String getPinTIP() {
		return pinTIP;
	}

	public void setPinTIP(String pinTIP) {
		this.pinTIP = pinTIP;
	}

	public String getPinCashback() {
		return pinCashback;
	}

	public void setPinCashback(String pinCashback) {
		this.pinCashback = pinCashback;
	}

	public String getPinBalnceInq() {
		return pinBalnceInq;
	}

	public void setPinBalnceInq(String pinBalnceInq) {
		this.pinBalnceInq = pinBalnceInq;
	}

	public String getPinPreAuth() {
		return pinPreAuth;
	}

	public void setPinPreAuth(String pinPreAuth) {
		this.pinPreAuth = pinPreAuth;
	}

	public String getPinPurchase() {
		return pinPurchase;
	}

	public void setPinPurchase(String pinPurchase) {
		this.pinPurchase = pinPurchase;
	}

	public String getPinCompletion() {
		return pinCompletion;
	}

	public void setPinCompletion(String pinCompletion) {
		this.pinCompletion = pinCompletion;
	}

	public String getPinCashAdvance() {
		return pinCashAdvance;
	}

	public void setPinCashAdvance(String pinCashAdvance) {
		this.pinCashAdvance = pinCashAdvance;
	}

	public String getPinMOTO() {
		return pinMOTO;
	}

	public void setPinMOTO(String pinMOTO) {
		this.pinMOTO = pinMOTO;
	}

	public String getPinCashDeposit() {
		return pinCashDeposit;
	}

	public void setPinCashDeposit(String pinCashDeposit) {
		this.pinCashDeposit = pinCashDeposit;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getDnotCapdebitTxn() {
		return dnotCapdebitTxn;
	}

	public void setDnotCapdebitTxn(String dnotCapdebitTxn) {
		this.dnotCapdebitTxn = dnotCapdebitTxn;
	}

	public String getEbtProcess() {
		return ebtProcess;
	}

	public void setEbtProcess(String ebtProcess) {
		this.ebtProcess = ebtProcess;
	}

	public String getNumberFormat() {
		return numberFormat;
	}

	public void setNumberFormat(String numberFormat) {
		this.numberFormat = numberFormat;
	}

	public String getAddRefundAmtPrompts() {
		return addRefundAmtPrompts;
	}

	public void setAddRefundAmtPrompts(String addRefundAmtPrompts) {
		this.addRefundAmtPrompts = addRefundAmtPrompts;
	}

	public String getPurchaseOrdrNoFormat() {
		return purchaseOrdrNoFormat;
	}

	public void setPurchaseOrdrNoFormat(String purchaseOrdrNoFormat) {
		this.purchaseOrdrNoFormat = purchaseOrdrNoFormat;
	}

	public String getBarNoFormat() {
		return barNoFormat;
	}

	public void setBarNoFormat(String barNoFormat) {
		this.barNoFormat = barNoFormat;
	}

	public String getDisablrPrntngRRN() {
		return disablrPrntngRRN;
	}

	public void setDisablrPrntngRRN(String disablrPrntngRRN) {
		this.disablrPrntngRRN = disablrPrntngRRN;
	}

	public String getEmiRange() {
		return emiRange;
	}

	public void setEmiRange(String emiRange) {
		this.emiRange = emiRange;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getDebitTxn() {
		return debitTxn;
	}

	public void setDebitTxn(String debitTxn) {
		this.debitTxn = debitTxn;
	}

	public String getFloorLimit() {
		return floorLimit;
	}

	public void setFloorLimit(String floorLimit) {
		this.floorLimit = floorLimit;
	}

	public String getManAlwdVoid() {
		return manAlwdVoid;
	}

	public void setManAlwdVoid(String manAlwdVoid) {
		this.manAlwdVoid = manAlwdVoid;
	}

	public String getManAlwdTip() {
		return manAlwdTip;
	}

	public void setManAlwdTip(String manAlwdTip) {
		this.manAlwdTip = manAlwdTip;
	}

	public String getPinRefund() {
		return pinRefund;
	}

	public void setPinRefund(String pinRefund) {
		this.pinRefund = pinRefund;
	}

	public String getOfflineAlwdVoid() {
		return offlineAlwdVoid;
	}

	public void setOfflineAlwdVoid(String offlineAlwdVoid) {
		this.offlineAlwdVoid = offlineAlwdVoid;
	}

	public String getOfflineAlwdRefund() {
		return offlineAlwdRefund;
	}

	public void setOfflineAlwdRefund(String offlineAlwdRefund) {
		this.offlineAlwdRefund = offlineAlwdRefund;
	}

	public String getOfflineAlwdCashDeposit() {
		return offlineAlwdCashDeposit;
	}

	public void setOfflineAlwdCashDeposit(String offlineAlwdCashDeposit) {
		this.offlineAlwdCashDeposit = offlineAlwdCashDeposit;
	}

	public String getCashRecordingAlwd() {
		return cashRecordingAlwd;
	}

	public void setCashRecordingAlwd(String cashRecordingAlwd) {
		this.cashRecordingAlwd = cashRecordingAlwd;
	}

	public String getRefundAlwd() {
		return refundAlwd;
	}

	public void setRefundAlwd(String refundAlwd) {
		this.refundAlwd = refundAlwd;
	}

	public String getManAlwdMoto() {
		return manAlwdMoto;
	}

	public void setManAlwdMoto(String manAlwdMoto) {
		this.manAlwdMoto = manAlwdMoto;
	}

	public String getManAlwdCashRecording() {
		return manAlwdCashRecording;
	}

	public void setManAlwdCashRecording(String manAlwdCashRecording) {
		this.manAlwdCashRecording = manAlwdCashRecording;
	}

	public String getOfflineAlwdPreAuth() {
		return offlineAlwdPreAuth;
	}

	public void setOfflineAlwdPreAuth(String offlineAlwdPreAuth) {
		this.offlineAlwdPreAuth = offlineAlwdPreAuth;
	}

	public String getOfflineAlwdBalanceInquiry() {
		return offlineAlwdBalanceInquiry;
	}

	public void setOfflineAlwdBalanceInquiry(String offlineAlwdBalanceInquiry) {
		this.offlineAlwdBalanceInquiry = offlineAlwdBalanceInquiry;
	}

	public String getOfflineAlwdCashRecording() {
		return offlineAlwdCashRecording;
	}

	public void setOfflineAlwdCashRecording(String offlineAlwdCashRecording) {
		this.offlineAlwdCashRecording = offlineAlwdCashRecording;
	}

	public String getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(String tableNumber) {
		this.tableNumber = tableNumber;
	}

}
