package com.fss.pos.rest.emvdownload;

import java.util.List;
import com.fss.pos.client.services.download.parameter.IccData;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class EmvResponse {

	private String channel;
	private EmvIccContactlessdataResponse IccContactlessdata;
	private List<KeydataResponse> Keydata;
	private List<EmvTerminalDataResponse> EmvTerminalData;
	private String responsecode;
	private String responsedescription;
	public List<KeydataResponse> getKeydata() {
		return Keydata;
	}
	public void setKeydata(List<KeydataResponse> keydata) {
		Keydata = keydata;
	}
	
	public String getResponsecode() {
		return responsecode;
	}
	public void setResponsecode(String responsecode) {
		this.responsecode = responsecode;
	}
	public String getResponsedescription() {
		return responsedescription;
	}
	public void setResponsedescription(String responsedescription) {
		this.responsedescription = responsedescription;
	}

	
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}


	public EmvIccContactlessdataResponse getIccContactlessdata() {
		return IccContactlessdata;
	}
	public void setIccContactlessdata(EmvIccContactlessdataResponse iccContactlessdata) {
		IccContactlessdata = iccContactlessdata;
	}

	public List<EmvTerminalDataResponse> getEmvTerminalData() {
		return EmvTerminalData;
	}
	public void setEmvTerminalData(List<EmvTerminalDataResponse> emvTerminalData) {
		EmvTerminalData = emvTerminalData;
	}

}
