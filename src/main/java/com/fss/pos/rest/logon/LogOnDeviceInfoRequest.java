package com.fss.pos.rest.logon;

public class LogOnDeviceInfoRequest {

		private String terminalId;
	    private String deviceModel;
	    private String deviceSerialNo;
	    
		public String getTerminalId() {
			return terminalId;
		}
		public void setTerminalId(String terminalId) {
			this.terminalId = terminalId;
		}
		public String getDeviceModel() {
			return deviceModel;
		}
		public void setDeviceModel(String deviceModel) {
			this.deviceModel = deviceModel;
		}
		
		public String getDeviceSerialNo() {
			return deviceSerialNo;
		}
		public void setDeviceSerialNo(String deviceSerialNo) {
			this.deviceSerialNo = deviceSerialNo;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("DeviceInfoRequest [terminalId=");
			builder.append(terminalId);
			builder.append(", deviceModel=");
			builder.append(deviceModel);
			builder.append(", deviceSerialNo=");
			builder.append(deviceSerialNo);
			builder.append("]");
			return builder.toString();
		}
	    
}
