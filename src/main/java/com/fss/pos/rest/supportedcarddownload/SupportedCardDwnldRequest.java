package com.fss.pos.rest.supportedcarddownload;

import com.fss.pos.rest.paramdownload.ParamDwnldDeviceInfo;

public class SupportedCardDwnldRequest {
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
