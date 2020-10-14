package com.fss.pos.rest.riskprofiledownload;

import com.fss.pos.rest.paramdownload.ParamDwnldDeviceInfo;

public class RiskProfileRequest {
	private ParamDwnldDeviceInfo deviceInfo;

	public ParamDwnldDeviceInfo getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(ParamDwnldDeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	@Override
	public String toString() {
		return "RiskProfileRequest [deviceInfo=" + deviceInfo + "]";
	}
}
