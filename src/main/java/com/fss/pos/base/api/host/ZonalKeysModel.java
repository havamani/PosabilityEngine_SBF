package com.fss.pos.base.api.host;


public class ZonalKeysModel {
	private String mspAcr;
	private String mspDate;
	private String mspTime;
	private String stationName;
	private String alterStationName;
	private String msgProtocol;
	private String hsmModelType;
	private String hsmStationName;
	private String acquirerIndex;
	private String keyLength;
	private String zmkChecksum;
	private String acqId;
	
	public String getMspAcr() {
		return mspAcr;
	}
	public void setMspAcr(String mspAcr) {
		this.mspAcr = mspAcr;
	}
	public String getMspDate() {
		return mspDate;
	}
	public void setMspDate(String mspDate) {
		this.mspDate = mspDate;
	}
	public String getMspTime() {
		return mspTime;
	}
	public void setMspTime(String mspTime) {
		this.mspTime = mspTime;
	}
	
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public String getAlterStationName() {
		return alterStationName;
	}
	public void setAlterStationName(String alterStationName) {
		this.alterStationName = alterStationName;
	}

	public String getHsmStationName() {
		return hsmStationName;
	}
	public void setHsmStationName(String hsmStationName) {
		this.hsmStationName = hsmStationName;
	}
	public String getAcquirerIndex() {
		return acquirerIndex;
	}
	public void setAcquirerIndex(String acquirerIndex) {
		this.acquirerIndex = acquirerIndex;
	}

	public String getZmkChecksum() {
		return zmkChecksum;
	}
	public void setZmkChecksum(String zmkChecksum) {
		this.zmkChecksum = zmkChecksum;
	}
	public String getAcqId() {
		return acqId;
	}
	public void setAcqId(String acqId) {
		this.acqId = acqId;
	}
	public String getMsgProtocol() {
		return msgProtocol;
	}
	public void setMsgProtocol(String msgProtocol) {
		this.msgProtocol = msgProtocol;
	}
	public String getHsmModelType() {
		return hsmModelType;
	}
	public void setHsmModelType(String hsmModelType) {
		this.hsmModelType = hsmModelType;
	}
	public String getKeyLength() { 
		return keyLength;
	}
	public void setKeyLength(String keyLength) {
		this.keyLength = keyLength;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ZonalKeysModel [mspAcr=").append(mspAcr)
				.append(", mspDate=").append(mspDate).append(", mspTime=")
				.append(mspTime).append(", stationName=").append(stationName)
				.append(", alterStationName=").append(alterStationName)
				.append(", msgProtocol=").append(msgProtocol)
				.append(", hsmModelType=").append(hsmModelType)
				.append(", hsmStationName=").append(hsmStationName)
				.append(", acquirerIndex=").append(acquirerIndex)
				.append(", keyLength=").append(keyLength)
				.append(", zmkChecksum=").append(zmkChecksum)
				.append(", acqId=").append(acqId).append("]");
		return builder.toString();
	}
	 


}
