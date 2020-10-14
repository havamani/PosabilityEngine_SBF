package com.fss.pos.rest.logon;

public class LogonRequest {
	
	private String channel;
    private LogOnDeviceInfoRequest deviceInfo;
    private KeyRequest key;
    
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public LogOnDeviceInfoRequest getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(LogOnDeviceInfoRequest deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	public KeyRequest getKey() {
		return key;
	}
	public void setKey(KeyRequest key) {
		this.key = key;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LogonRequest [channel=");
		builder.append(channel);
		builder.append(", deviceInfo=");
		builder.append(deviceInfo);
		builder.append(", key=");
		builder.append(key);
		builder.append("]");
		return builder.toString();
	}   
    
}
