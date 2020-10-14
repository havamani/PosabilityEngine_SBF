package com.fss.pos.client.services.download.parameter;

public class IccData {

	private String tableNumber;
	private String aidIndex;
	private String addTagID;
	private String aidLength;
	private String aid;
	private String appSelectIndicator;
	private String terminalFloorLimit;
	private String appVersionNo;
	private String defaultTDOL;
	private String defaultDDOL;
	private String offApproveResCode;
	private String offDeclineResCode;
	private String unApproveResCode;
	private String unDeclineResCode;
	private String transactionLimit;
	private String cvmLimit;
	private String magStripeAppVersionNo;
	//new fields added for emv download
	private String cardScheme;
	private String terminalProfileName;
	private String transactionTypes;	
	private String thresholdSelection;
	private String targetSelection;
	private String maximumSelection;
	private String tacDenial;
	private String tacOnline;
	private String tacDefault;
	private String termianlRiskdata;
	private String terminalQualifiers;


	private String terminalCapabilities;
	private String defaultTDOLLength;
	private String defaultDDOLLength;
	private String terminalDataCount;	
	private String kernelConfiguration;
	private String mchipCapLimit;
	private String mchipCapBelowLimit;
	private String magstripeAboveLimit;
	private String magstripeBelowLimit;
	private String dcvTxnLimit;
	private String aidType;
	private String acquirerID;
	
	
	
	public String getKernelConfiguration() {
		return kernelConfiguration;
	}

	public void setKernelConfiguration(String kernelConfiguration) {
		this.kernelConfiguration = kernelConfiguration;
	}

	public String getMchipCapLimit() {
		return mchipCapLimit;
	}

	public void setMchipCapLimit(String mchipCapLimit) {
		this.mchipCapLimit = mchipCapLimit;
	}

	public String getMchipCapBelowLimit() {
		return mchipCapBelowLimit;
	}

	public void setMchipCapBelowLimit(String mchipCapBelowLimit) {
		this.mchipCapBelowLimit = mchipCapBelowLimit;
	}

	public String getMagstripeAboveLimit() {
		return magstripeAboveLimit;
	}

	public void setMagstripeAboveLimit(String magstripeAboveLimit) {
		this.magstripeAboveLimit = magstripeAboveLimit;
	}

	public String getMagstripeBelowLimit() {
		return magstripeBelowLimit;
	}

	public void setMagstripeBelowLimit(String magstripeBelowLimit) {
		this.magstripeBelowLimit = magstripeBelowLimit;
	}

	public String getDcvTxnLimit() {
		return dcvTxnLimit;
	}

	public void setDcvTxnLimit(String dcvTxnLimit) {
		this.dcvTxnLimit = dcvTxnLimit;
	}

	public String getAidType() {
		return aidType;
	}

	public void setAidType(String aidType) {
		this.aidType = aidType;
	}

	public String getAcquirerID() {
		return acquirerID;
	}

	public void setAcquirerID(String acquirerID) {
		this.acquirerID = acquirerID;
	}

	public String getAidIndex() {
		return aidIndex;
	}

	public void setAidIndex(String aidIndex) {
		this.aidIndex = aidIndex;
	}

	public String getAddTagID() {
		return addTagID;
	}

	public void setAddTagID(String addTagID) {
		this.addTagID = addTagID;
	}

	public String getAidLength() {
		return aidLength;
	}

	public void setAidLength(String aidLength) {
		this.aidLength = aidLength;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getAppSelectIndicator() {
		return appSelectIndicator;
	}

	public void setAppSelectIndicator(String appSelectIndicator) {
		this.appSelectIndicator = appSelectIndicator;
	}

	public String getTerminalFloorLimit() {
		return terminalFloorLimit;
	}

	public void setTerminalFloorLimit(String terminalFloorLimit) {
		this.terminalFloorLimit = terminalFloorLimit;
	}

	public String getAppVersionNo() {
		return appVersionNo;
	}

	public void setAppVersionNo(String appVersionNo) {
		this.appVersionNo = appVersionNo;
	}

	public String getDefaultTDOL() {
		return defaultTDOL;
	}

	public void setDefaultTDOL(String defaultTDOL) {
		this.defaultTDOL = defaultTDOL;
	}

	public String getDefaultDDOL() {
		return defaultDDOL;
	}

	public void setDefaultDDOL(String defaultDDOL) {
		this.defaultDDOL = defaultDDOL;
	}

	public String getOffApproveResCode() {
		return offApproveResCode;
	}

	public void setOffApproveResCode(String offApproveResCode) {
		this.offApproveResCode = offApproveResCode;
	}

	public String getOffDeclineResCode() {
		return offDeclineResCode;
	}

	public void setOffDeclineResCode(String offDeclineResCode) {
		this.offDeclineResCode = offDeclineResCode;
	}

	public String getUnApproveResCode() {
		return unApproveResCode;
	}

	public void setUnApproveResCode(String unApproveResCode) {
		this.unApproveResCode = unApproveResCode;
	}

	public String getUnDeclineResCode() {
		return unDeclineResCode;
	}

	public void setUnDeclineResCode(String unDeclineResCode) {
		this.unDeclineResCode = unDeclineResCode;
	}

	public String getTransactionLimit() {
		return transactionLimit;
	}

	public void setTransactionLimit(String transactionLimit) {
		this.transactionLimit = transactionLimit;
	}

	public String getCvmLimit() {
		return cvmLimit;
	}

	public void setCvmLimit(String cvmLimit) {
		this.cvmLimit = cvmLimit;
	}

	public String getMagStripeAppVersionNo() {
		return magStripeAppVersionNo;
	}

	public void setMagStripeAppVersionNo(String magStripeAppVersionNo) {
		this.magStripeAppVersionNo = magStripeAppVersionNo;
	}

	public String getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(String tableNumber) {
		this.tableNumber = tableNumber;
	}

	public String getTerminalProfileName() {
		return terminalProfileName;
	}

	public void setTerminalProfileName(String terminalProfileName) {
		this.terminalProfileName = terminalProfileName;
	}

	public String getTransactionTypes() {
		return transactionTypes;
	}

	public void setTransactionTypes(String transactionTypes) {
		this.transactionTypes = transactionTypes;
	}

	public String getThresholdSelection() {
		return thresholdSelection;
	}

	public void setThresholdSelection(String thresholdSelection) {
		this.thresholdSelection = thresholdSelection;
	}

	public String getTargetSelection() {
		return targetSelection;
	}

	public void setTargetSelection(String targetSelection) {
		this.targetSelection = targetSelection;
	}

	public String getMaximumSelection() {
		return maximumSelection;
	}

	public void setMaximumSelection(String maximumSelection) {
		this.maximumSelection = maximumSelection;
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

	public String getTermianlRiskdata() {
		return termianlRiskdata;
	}

	public void setTermianlRiskdata(String termianlRiskdata) {
		this.termianlRiskdata = termianlRiskdata;
	}

	public String getTerminalQualifiers() {
		return terminalQualifiers;
	}

	public void setTerminalQualifiers(String terminalQualifiers) {
		this.terminalQualifiers = terminalQualifiers;
	}

	public String getCardScheme() {
		return cardScheme;
	}

	public void setCardScheme(String cardScheme) {
		this.cardScheme = cardScheme;
	}

	public String getDefaultTDOLLength() {
		return defaultTDOLLength;
	}

	public void setDefaultTDOLLength(String defaultTDOLLength) {
		this.defaultTDOLLength = defaultTDOLLength;
	}

	public String getDefaultDDOLLength() {
		return defaultDDOLLength;
	}

	public void setDefaultDDOLLength(String defaultDDOLLength) {
		this.defaultDDOLLength = defaultDDOLLength;
	}

	public String getTerminalDataCount() {
		return terminalDataCount;
	}

	public void setTerminalDataCount(String terminalDataCount) {
		this.terminalDataCount = terminalDataCount;
	}
	public String getTerminalCapabilities() {
		return terminalCapabilities;
	}

	public void setTerminalCapabilities(String terminalCapabilities) {
		this.terminalCapabilities = terminalCapabilities;
	}
}
