package com.fss.pos.rest.supportedcarddownload;

import java.util.List;

import com.fss.pos.rest.supportedcarddownload.CardRangeInfoResponse;

//import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupportedCardResponse {
	/* @ApiModelProperty(notes = "The database generated product ID") */
	   private String channel;
	   private String stan;
	   private String invoiceNo;
	    private String batchNo;
	    private String responseCode;
	    private String responseDescription;
	    private SupportedCardDwnldDeviceInfoResponse deviceInfo;
	    private List<CardRangeInfoResponse> cardRangeInfo;
	
	   public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getStan() {
		return stan;
	}
	public void setStan(String stan) {
		this.stan = stan;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseDescription() {
		return responseDescription;
	}
	public void setResponseDescription(String responseDescription) {
		this.responseDescription = responseDescription;
	}

	public SupportedCardDwnldDeviceInfoResponse getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(SupportedCardDwnldDeviceInfoResponse deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	public List<CardRangeInfoResponse> getCardRangeInfo() {
		return cardRangeInfo;
	}
	public void setCardRangeInfo(List<CardRangeInfoResponse> cardRangeInfo) {
		this.cardRangeInfo = cardRangeInfo;
	}
	
	 


}
