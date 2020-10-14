package com.fss.pos.rest.settlement;

public class SettlementRequest {
	private String channel;
    private String stan;
    private Settlement settlement;
    private SettlementDeviceInfo deviceInfo;
    
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getStan() {
		return stan;
	}
	public void setStan(String stan) {
		this.stan = stan;
	}
	public Settlement getSettlement() {
		return settlement;
	}
	public void setSettlement(Settlement settlement) {
		this.settlement = settlement;
	}
	public SettlementDeviceInfo getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(SettlementDeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	@Override
	public String toString() {
		return "Request [channel=" + channel + ", stan=" + stan + ", settlement=" + settlement + ", deviceInfo="
				+ deviceInfo + "]";
	}	
}
