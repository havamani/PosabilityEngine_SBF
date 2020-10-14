package com.fss.pos.rest.paramdownload;

public class ParamDownloadRequestMasterBean {
private PramDwnldRequest request;

public PramDwnldRequest getRequest() {
	return request;
}

public void setRequest(PramDwnldRequest request) {
	this.request = request;
}

@Override
public String toString() {
	return "ParamDownloadRequestMasterBean [request=" + request + "]";
}

}
