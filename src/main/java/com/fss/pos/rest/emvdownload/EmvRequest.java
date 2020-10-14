package com.fss.pos.rest.emvdownload;

public class EmvRequest {
	private EmvDeviceInfo deviceInfo;

	public EmvDeviceInfo getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(EmvDeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	@Override
	public String toString() {
		return "EmvRequest [deviceInfo=" + deviceInfo + "]";
	}

	

	}

		
	


