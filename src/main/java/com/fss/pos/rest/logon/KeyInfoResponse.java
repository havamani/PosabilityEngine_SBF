package com.fss.pos.rest.logon;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class KeyInfoResponse {
	
		private String terminalId;
	    private String lmkEncryptedKey;
	    private String parentEncryptedKey;
	    private String checksum;
	    private String pinBlock;
	    
		public String getTerminalId() {
			return terminalId;
		}
		public void setTerminalId(String terminalId) {
			this.terminalId = terminalId;
		}
		public String getLmkEncryptedKey() {
			return lmkEncryptedKey;
		}
		public void setLmkEncryptedKey(String lmkEncryptedKey) {
			this.lmkEncryptedKey = lmkEncryptedKey;
		}
		public String getParentEncryptedKey() {
			return parentEncryptedKey;
		}
		public void setParentEncryptedKey(String parentEncryptedKey) {
			this.parentEncryptedKey = parentEncryptedKey;
		}
		public String getChecksum() {
			return checksum;
		}
		public void setChecksum(String checksum) {
			this.checksum = checksum;
		}
		public String getPinBlock() {
			return pinBlock;
		}
		public void setPinBlock(String pinBlock) {
			this.pinBlock = pinBlock;
		}
}
