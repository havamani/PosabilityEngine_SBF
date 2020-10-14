package com.fss.pos.host;

public class AcquirerConfig {

	private String stationName;
	private String msgProtocol;
	private String acqId;
	private String alternateStation;

	public String getAlternateStation() {
		return alternateStation;
	}

	public void setAlternateStation(String alternateStation) {
		this.alternateStation = alternateStation;
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

	public String getAcqId() {
		return acqId;
	}

	public void setAcqId(String acqId) {
		this.acqId = acqId;
	}



}
