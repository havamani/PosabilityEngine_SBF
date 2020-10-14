package com.fss.pos.rest.riskprofiledownload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fss.pos.client.services.download.parameter.IssuerData;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RiskProfileResponse {
		private String channel;		
	    private String batchNo;
	    private String responseCode;
	    private String responseDescription;
	    private RiskPrflDwnldDeviceInfoRequest deviceInfo;
	    private List<IssuerData> riskProfileInfo;
	    
		public String getChannel() {
			return channel;
		}
		public void setChannel(String channel) {
			this.channel = channel;
		}		
		public String getBatchNo() {
			return batchNo;
		}
		public void setBatchNo(String batchNo) {
			this.batchNo = batchNo;
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
		
		public RiskPrflDwnldDeviceInfoRequest getDeviceInfo() {
			return deviceInfo;
		}
		public void setDeviceInfo(RiskPrflDwnldDeviceInfoRequest deviceInfo) {
			this.deviceInfo = deviceInfo;
		}
		public List<IssuerData> getRiskProfileInfo() {
			return riskProfileInfo;
		}
		public void setRiskProfileInfo(List<IssuerData> riskProfileInfo) {
			this.riskProfileInfo = riskProfileInfo;
		}
		
}
