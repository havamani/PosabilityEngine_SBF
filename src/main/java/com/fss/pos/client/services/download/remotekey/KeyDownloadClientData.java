package com.fss.pos.client.services.download.remotekey;

public class KeyDownloadClientData {

	// DUKPT/MasterSession
	private String scheme;
	// PIN/p2pe
	private String type;
	// TMK/BDK
	private String id;
	private String ksn;

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKsn() {
		return ksn;
	}

	public void setKsn(String ksn) {
		this.ksn = ksn;
	}

}
