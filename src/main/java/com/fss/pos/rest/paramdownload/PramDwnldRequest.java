package com.fss.pos.rest.paramdownload;

public class PramDwnldRequest {
	private ParamDwnldDeviceInfo deviceInfo;

	public ParamDwnldDeviceInfo getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(ParamDwnldDeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	@Override
	public String toString() {
		return "Request [deviceInfo=" + deviceInfo + "]";
	}	

}
