package com.fss.pos.rest.settlement;

public class SettlementDeviceInfo {
	private String terminalId;
    private String deviceModel;
    private String deviceSerialNo;    
	
	public String getTerminalId() {
		return terminalId;
	}
	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}
	public String getDeviceModel() {
		return deviceModel;
	}
	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}
	public String getDeviceSerialNo() {
		return deviceSerialNo;
	}
	public void setDeviceSerialNo(String deviceSerialNo) {
		this.deviceSerialNo = deviceSerialNo;
	}
	@Override
	public String toString() {
		return "DeviceInfo [terminalId=" + terminalId + ", deviceModel=" + deviceModel + ", deviceSerialNo="
				+ deviceSerialNo + "]";
	}
}
