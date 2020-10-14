package com.fss.pos.rest.logon;

public class LogonRequestMasterBean {
	private LogonRequest request;

	public LogonRequest getRequest() {
		return request;
	}

	public void setRequest(LogonRequest request) {
		this.request = request;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LogonRequestMasterBean [request=");
		builder.append(request);
		builder.append("]");
		return builder.toString();
	}
	
}
