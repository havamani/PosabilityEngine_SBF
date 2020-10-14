package com.fss.pos.client.services.operator;

public class TerminalInfo {

	private String macId;
	private String msgVersion;
	private String appVersion;
	private String mdlNo;

	public String getMacId() {
		return macId;
	}

	public void setMacId(String macId) {
		this.macId = macId;
	}

	public String getMsgVersion() {
		return msgVersion;
	}

	public void setMsgVersion(String msgVersion) {
		this.msgVersion = msgVersion;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getMdlNo() {
		return mdlNo;
	}

	public void setMdlNo(String mdlNo) {
		this.mdlNo = mdlNo;
	}

}
