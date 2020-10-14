package com.fss.pos.rest.logon;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogonResponse {

		private String channel;
	    private String responseCode;
	    private String responseDescription;
	    private KeyInfoResponse keyInfo;
	    
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
		public KeyInfoResponse getKeyInfo() {
			return keyInfo;
		}
		public void setKeyInfo(KeyInfoResponse keyInfo) {
			this.keyInfo = keyInfo;
		}
		
}
