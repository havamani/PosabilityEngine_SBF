package com.fss.pos.rest.logon;

public class KeyRequest {
	private String keyScheme;
    private String keyType;
    private String keyId;
    
	public String getKeyScheme() {
		return keyScheme;
	}
	public void setKeyScheme(String keyScheme) {
		this.keyScheme = keyScheme;
	}
	public String getKeyType() {
		return keyType;
	}
	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}
	public String getKeyId() {
		return keyId;
	}
	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KeyRequest [keyScheme=");
		builder.append(keyScheme);
		builder.append(", keyType=");
		builder.append(keyType);
		builder.append(", keyId=");
		builder.append(keyId);
		builder.append("]");
		return builder.toString();
	}    
	
}
