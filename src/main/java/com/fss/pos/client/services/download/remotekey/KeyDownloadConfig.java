package com.fss.pos.client.services.download.remotekey;

public class KeyDownloadConfig {

	private String index;
	private String hsmModel;
	private String stationName;
	private String keyLengthType;
	private String checkSum;

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

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getKeyLengthType() {
		return keyLengthType;
	}

	public void setKeyLengthType(String keyLengthType) {
		this.keyLengthType = keyLengthType;
	}

	public String getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}

}
