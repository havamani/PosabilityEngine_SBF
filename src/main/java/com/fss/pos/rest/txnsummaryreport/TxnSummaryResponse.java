package com.fss.pos.rest.txnsummaryreport;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TxnSummaryResponse {
	
		private String channel;
	    private String responseCode;
	    private String responseDescription;
	    private TxnSummaryDeviceInfo deviceInfo;
	    private List<TxnSummaryReportInfo> reportInfo;
	    
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
		public TxnSummaryDeviceInfo getDeviceInfo() {
			return deviceInfo;
		}
		public void setDeviceInfo(TxnSummaryDeviceInfo deviceInfo) {
			this.deviceInfo = deviceInfo;
		}
		public List<TxnSummaryReportInfo> getReportInfo() {
			return reportInfo;
		}
		public void setReportInfo(List<TxnSummaryReportInfo> reportInfo) {
			this.reportInfo = reportInfo;
		}

}
