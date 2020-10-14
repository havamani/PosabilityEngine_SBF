package com.fss.pos.base.api.hsm;

import com.fss.pos.base.commons.AbstractBeanData;

public class HsmData extends AbstractBeanData {

	private String enableP2Pe;
	private String enablePIN;
	private String hsmModel;
	private String stationName;
	private String msgProtocol;
	private String terminalPPK;
	private String cryptoType;
	private String checkSum;
	private String fsscUrl;
	private String fsscSource;
	private String p2peCheckSum;
	private String p2peTerminalPPK;
	private String alternateStationName;


	public String getEnableP2Pe() {
		return enableP2Pe;
	}

	public void setEnableP2Pe(String enableP2Pe) {
		this.enableP2Pe = enableP2Pe;
	}

	public String getEnablePIN() {
		return enablePIN;
	}

	public void setEnablePIN(String enablePIN) {
		this.enablePIN = enablePIN;
	}

	public String getHsmModel() {
		return hsmModel;
	}

	public void setHsmModel(String hsmModel) {
		this.hsmModel = hsmModel;
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

	public String getTerminalPPK() {
		return terminalPPK;
	}

	public void setTerminalPPK(String terminalPPK) {
		this.terminalPPK = terminalPPK;
	}

	public String getCryptoType() {
		return cryptoType;
	}

	public void setCryptoType(String cryptoType) {
		this.cryptoType = cryptoType;
	}

	public String getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}

	public String getFsscUrl() {
		return fsscUrl;
	}

	public void setFsscUrl(String fsscUrl) {
		this.fsscUrl = fsscUrl;
	}

	public String getFsscSource() {
		return fsscSource;
	}

	public void setFsscSource(String fsscSource) {
		this.fsscSource = fsscSource;
	}

	public String getP2peTerminalPPK() {
		return p2peTerminalPPK;
	}

	public void setP2peTerminalPPK(String p2peTerminalPPK) {
		this.p2peTerminalPPK = p2peTerminalPPK;
	}

	public String getP2peCheckSum() {
		return p2peCheckSum;
	}

	public void setP2peCheckSum(String p2peCheckSum) {
		this.p2peCheckSum = p2peCheckSum;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HsmData [enableP2Pe=").append(enableP2Pe)
				.append(", enablePIN=").append(enablePIN).append(", hsmModel=")
				.append(hsmModel).append(", stationName=").append(stationName)
				.append(", msgProtocol=").append(msgProtocol)
				.append(", terminalPPK=").append(terminalPPK)
				.append(", cryptoType=").append(cryptoType)
				.append(", checkSum=").append(checkSum).append(", fsscUrl=")
				.append(fsscUrl).append(", fsscSource=").append(fsscSource)
				.append(", p2peCheckSum=").append(p2peCheckSum)
				.append(", p2peTerminalPPK=").append(p2peTerminalPPK)
				.append("]");
		return builder.toString();
	}

	public String getAlternateStationName() {
		return alternateStationName;
	}

	public void setAlternateStationName(String alternateStationName) {
		this.alternateStationName = alternateStationName;
	}

	 

	

}
