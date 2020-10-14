package com.fss.pos.client.services.download.patchupdate;

public class RemotePatchUpdateData {

	private boolean isContinuation;
	private String fileName;
	private long totalFileSize;
	private int fileTransferIndex;
	private int bytesTransferred;
	private String fileData;
	private String respCode;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return "RemotePatchUpdateData [isContinuation=" + isContinuation
				+ ", fileName=" + fileName + ", totalFileSize=" + totalFileSize
				+ ", fileTransferIndex=" + fileTransferIndex
				+ ", bytesTransferred=" + bytesTransferred + ", fileData="
				+ fileData + ", respCode=" + respCode + "]";
	}

	public void setTotalFileSize(int totalFileSize) {
		this.totalFileSize = totalFileSize;
	}

	public int getFileTransferIndex() {
		return fileTransferIndex;
	}

	public void setFileTransferIndex(int fileTransferIndex) {
		this.fileTransferIndex = fileTransferIndex;
	}

	public int getBytesTransferred() {
		return bytesTransferred;
	}

	public void setBytesTransferred(int bytesTransferred) {
		this.bytesTransferred = bytesTransferred;
	}

	public String getFileData() {
		return fileData;
	}

	public void setFileData(String fileData) {
		this.fileData = fileData;
	}

	public long getTotalFileSize() {
		return totalFileSize;
	}

	public void setTotalFileSize(long totalFileSize) {
		this.totalFileSize = totalFileSize;
	}

	public boolean isContinuation() {
		return isContinuation;
	}

	public void setContinuation(boolean isContinuation) {
		this.isContinuation = isContinuation;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

}
