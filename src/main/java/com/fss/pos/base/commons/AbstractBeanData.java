package com.fss.pos.base.commons;

public class AbstractBeanData implements Data {

	private boolean offline;
	private boolean reversal;
	private boolean keyImport;

	public boolean isOffline() {
		return offline;
	}

	public void setOffline(boolean offline) {
		this.offline = offline;
	}

	public boolean isReversal() {
		return reversal;
	}

	public void setReversal(boolean reversal) {
		this.reversal = reversal;
	}

	public boolean isKeyImport() {
		return keyImport;
	}

	public void setKeyImport(boolean keyImport) {
		this.keyImport = keyImport;
	}

}
