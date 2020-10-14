package com.fss.pos.rest.transactionreport;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportResponse {
		
	private String channel;
	private String responseCode;
	private String responseDescription;
	private DeviceInfoResponse deviceInfo;
    private List<ReportInfoResponse> reportInfo;
    
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseDescription() {
		return responseDescription;
	}
	public void setResponseDescription(String responseDescription) {
		this.responseDescription = responseDescription;
	}
	public DeviceInfoResponse getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(DeviceInfoResponse deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	public List<ReportInfoResponse> getReportInfo() {
		return reportInfo;
	}
	public void setReportInfo(List<ReportInfoResponse> reportInfo) {
		this.reportInfo = reportInfo;
	}    

}
