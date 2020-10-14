package com.fss.pos.rest.transactionreport;

import com.fss.pos.rest.paramdownload.ParamDwnldDeviceInfo;

public class ReportRequest {
	
	private ParamDwnldDeviceInfo deviceInfo;
	private ReportInfoRequest reportInfo;
	
	public ParamDwnldDeviceInfo getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(ParamDwnldDeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	public ReportInfoRequest getReportInfo() {
		return reportInfo;
	}
	public void setReportInfo(ReportInfoRequest reportInfo) {
		this.reportInfo = reportInfo;
	}
	@Override
	public String toString() {
		return "ReportRequestMasterBean [deviceInfo=" + deviceInfo + ", reportInfo=" + reportInfo + "]";
	}
  
}
