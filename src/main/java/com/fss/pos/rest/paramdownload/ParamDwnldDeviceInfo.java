package com.fss.pos.rest.paramdownload;

public class ParamDwnldDeviceInfo {
private String terminalId;
private String deviceModel;
private String deviceSerialNo;
private String channel;

public String getTerminalId() {
	return terminalId;
}
public String getDeviceModel() {
	return deviceModel;
}
public String getDeviceSerialNo() {
	return deviceSerialNo;
}
public String getChannel() {
	return channel;
}
public void setTerminalId(String terminalId) {
	this.terminalId = terminalId;
}
public void setDeviceModel(String deviceModel) {
	this.deviceModel = deviceModel;
}
public void setDeviceSerialNo(String deviceSerialNo) {
	this.deviceSerialNo = deviceSerialNo;
}
public void setChannel(String channel) {
	this.channel = channel;
}
@Override
public String toString() {
	return "DeviceInfo [terminalId=" + terminalId + ", deviceModel=" + deviceModel + ", deviceSerialNo="
			+ deviceSerialNo + ", channel=" + channel + "]";
}

}
