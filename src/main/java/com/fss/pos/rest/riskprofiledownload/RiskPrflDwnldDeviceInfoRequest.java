package com.fss.pos.rest.riskprofiledownload;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RiskPrflDwnldDeviceInfoRequest {
	private String terminalId;
	private String deviceModel;
	private String deviceSerialNo;
	private String channel;
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
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	@Override
	public String toString() {
		return "DeviceInfoRequest [terminalId=" + terminalId + ", deviceModel=" + deviceModel + ", deviceSerialNo="
				+ deviceSerialNo + ", channel=" + channel + "]";
	}
}
