package com.fss.pos.host.iso8583.jcb;

public class JcbField {

	public JcbField(String bitpos, int fieldlength, char fieldlengthtype,
			char bitdatatype, char subelements) {
		this.bitpos = bitpos;
		this.fieldlength = fieldlength;
		this.fieldlengthtype = fieldlengthtype;
		this.bitdatatype = bitdatatype;
		this.subelements = subelements;
	}

	private String bitpos;
	private int fieldlength;
	private char fieldlengthtype;
	private char bitdatatype;
	private char subelements;

	public String getBitpos() {
		return bitpos;
	}

	public void setBitpos(String bitpos) {
		this.bitpos = bitpos;
	}

	public int getFieldlength() {
		return fieldlength;
	}

	public void setFieldlength(int fieldlength) {
		this.fieldlength = fieldlength;
	}

	public char getFieldlengthtype() {
		return fieldlengthtype;
	}

	public void setFieldlengthtype(char fieldlengthtype) {
		this.fieldlengthtype = fieldlengthtype;
	}

	public char getBitdatatype() {
		return bitdatatype;
	}

	public void setBitdatatype(char bitdatatype) {
		this.bitdatatype = bitdatatype;
	}

	public char getSubelements() {
		return subelements;
	}

	public void setSubelements(char subelements) {
		this.subelements = subelements;
	}
}
