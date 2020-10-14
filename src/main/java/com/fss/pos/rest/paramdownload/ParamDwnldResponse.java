package com.fss.pos.rest.paramdownload;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParamDwnldResponse {
	   private String channel;
	    private String batchNo;
	    private String responseCode;
	    private String responseDescription;
	    private TerminalInfoResponse terminalInfo;
	    
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
		public TerminalInfoResponse getTerminalInfo() {
			return terminalInfo;
		}
		public void setTerminalInfo(TerminalInfoResponse terminalInfo) {
			this.terminalInfo = terminalInfo;
		}
		
}
