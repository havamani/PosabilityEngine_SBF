package com.fss.pos.host.iso8583.master;

public class MasterField {

	public MasterField(String name, int type, int minLgth, int maxLgth,
			int lgthHdr) {
		this.name = name;
		this.type = type;
		this.minLgth = minLgth;
		this.maxLgth = maxLgth;
		this.lgthHdr = lgthHdr;
	}

	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}

	public int getMinLength() {
		return minLgth;
	}

	public int getMaxLength() {
		return maxLgth;
	}

	public int getLengthHeader() {
		return lgthHdr;
	}

	private String name;
	private int type;
	private int minLgth;
	private int maxLgth;
	private int lgthHdr;
}
