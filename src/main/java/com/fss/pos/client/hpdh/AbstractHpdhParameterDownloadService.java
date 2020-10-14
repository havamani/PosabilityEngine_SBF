package com.fss.pos.client.hpdh;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import javax.smartcardio.Card;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.springframework.beans.factory.annotation.Autowired;

import com.fss.pos.base.api.client.Validator;
import com.fss.pos.base.api.db.storedprocedure.ResponseStatus;
import com.fss.pos.base.commons.Config;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.PosStringBuilder;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.client.services.download.parameter.CardRange;
import com.fss.pos.client.services.download.parameter.EmvTerminalData;
import com.fss.pos.client.services.download.parameter.IccData;
import com.fss.pos.client.services.download.parameter.IssuerData;
import com.fss.pos.client.services.download.parameter.KeyData;
import com.fss.pos.client.services.download.parameter.ParameterDownloadService;
import com.fss.pos.client.services.download.parameter.TerminalConfig;
import com.fss.pos.rest.paramdownload.ParamDownloadResponseMasterBean;
import com.fss.pos.rest.paramdownload.ParamDwnldResponse;
import com.fss.pos.rest.paramdownload.TerminalInfoResponse;
import com.fss.pos.rest.supportedcarddownload.CardRangeInfoResponse;
import com.fss.pos.rest.supportedcarddownload.SupportedCardResponse;
import com.fss.pos.rest.supportedcarddownload.SupportedCardDwnldDeviceInfoResponse;
import com.fss.pos.rest.supportedcarddownload.SupportedCardDownloadResponse;

public abstract class AbstractHpdhParameterDownloadService {

	protected static final String ONE = "1";
	protected static final String ZERO = "0";
	protected static final String ONE_BYTE = "00";
	protected static final String TWO_BYTE = "0000";
	protected static final String THREE_BYTE = "000000";
	protected static final String FOUR_BYTE = "00000000";
	protected static final String FIVE_BYTE = "0000000000";
	protected static final String FFFFFF = "FFFFFF";

	protected static final String TABLE_ID_TCT = "01";
	protected static final String TABLE_ID_CARD_RANGE = "02";
	protected static final String TABLE_ID_ISSUER = "03";
	protected static final String TABLE_ID_LIMIT = "06";
	protected static final String TABLE_ID_ICC_CHIP_CB = "0B";
	protected static final String TABLE_ID_ICC_CHIP_CL = "0C";
	protected static final String TABLE_ID_ICC_TABLE_ENTRY = "0D";
	protected static final String TABLE_ID_KEYDATA = "0E";
	protected static final String TABLE_ID_ACQUIRER = "04";
	protected static final String TABLE_ID_TERMINAL_DATA = "0F";

	protected static final String TABLE_SEPARATOR = "TABLE_SEPARATOR";
	protected static final String RECORD_SEPARATOR = "RECORD_SEPARATOR";

	private static final int UNSAFE_MAX_ROTATIONS = 999999;

	protected static ConcurrentMap<String, Queue<String>> downloadDataMap;

	static {
		downloadDataMap = new ConcurrentHashMap<String, Queue<String>>();
	}

	@Autowired
	private ParameterDownloadService parameterDownloadService;

	@Autowired
	private Config config;

	public String getData(String mspAcr, IsoBuffer isoBuffer,  String procedureName)
			throws PosException {

		try {
			String terminalId = isoBuffer.get(Constants.DE41);
			if (isoBuffer.get(Constants.DE3).endsWith(ONE)) {
				if (!downloadDataMap.containsKey(mspAcr + terminalId))
					throw new PosException(Constants.ERR_NO_TID_DOWNLOAD);

			} else {
				loadData(terminalId, mspAcr, procedureName);
			}

			String currentData = downloadDataMap.get(mspAcr + terminalId)
					.poll().toString()
					.replace(TABLE_SEPARATOR, Constants.EMPTY_STRING)
					.replace(RECORD_SEPARATOR, Constants.EMPTY_STRING);

			if (downloadDataMap.get(mspAcr + terminalId).isEmpty()) {
				downloadDataMap.remove(mspAcr + terminalId);
				isoBuffer.put(Constants.DE3, Constants.PROC_CODE_DOWNLOAD
						+ Constants.ZERO + Constants.ZERO + Constants.ZERO
						+ Constants.ZERO);
			} else {
				isoBuffer.put(Constants.DE3,
						Constants.PROC_CODE_DOWNLOAD_CONTINUE);
			}
			return currentData;
		} catch (PosException e) {
			throw e;
		} catch (Exception e) {
			Log.error(this.getClass().getCanonicalName() + " getData()", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
	}

	@SuppressWarnings("unchecked")
	private void loadData(String terminalId, String mspAcr, String procedureName) throws Exception {

		List<Object> objList = parameterDownloadService.getDownloadBeans(
				terminalId, mspAcr, procedureName);
		String status;
		if (!Constants.SUCCESS.equals(status = ((List<ResponseStatus>) objList
				.get(0)).get(0).getStatus()))
			throw new PosException(status);

		String downloadData = initializeTerminal(objList, mspAcr, terminalId);
		if (Util.isNullOrEmpty(downloadData))
			throw new PosException(Constants.ERR_SYSTEM_ERROR);

		Queue<String> q = new ConcurrentLinkedQueue<String>();
		int maxBytes = Integer.parseInt(config.getParamDownloadMaxBytes());
		int i = 0;
		untilExist: while (!downloadData.isEmpty()) {
			if (i == UNSAFE_MAX_ROTATIONS) {
				Log.trace("Infine loop in download. Unsafe condition");
				throw new PosException(Constants.ERR_SYSTEM_ERROR);
			}
			i++;
			int recordCount = 0;
			int tableCount = 0;
			StringBuilder buffer = new StringBuilder();
			if (downloadData.length() > maxBytes) {
				tLoop: for (String tableData : downloadData
						.split(TABLE_SEPARATOR)) {
					if (buffer.length() + tableData.length() > maxBytes) {
						for (String record : tableData.split(RECORD_SEPARATOR)) {
							if (buffer.length() + record.length() > maxBytes)
								break tLoop;
							else {
								if (!record.isEmpty()) {
									buffer.append(record);
									recordCount++;
								}
							}
						}
					} else if (tableData.contains(RECORD_SEPARATOR)) {
						recordCount += StringUtils.countMatches(tableData,
								RECORD_SEPARATOR);
						buffer.append(tableData.replace(RECORD_SEPARATOR,
								Constants.EMPTY_STRING));
					} else
						buffer.append(tableData);
					tableCount++;
				}
			} else {
				buffer.append(downloadData);
			}

			if (buffer.length() == 0)
				break untilExist;

			downloadData = downloadData.substring(buffer.length()
					+ (RECORD_SEPARATOR.length() * (recordCount))
					+ (TABLE_SEPARATOR.length() * tableCount));

			q.add(buffer.toString());
		}
		downloadDataMap.put(mspAcr + terminalId, q);
	}

	protected abstract String initializeTerminal(List<Object> objList,
			String mspAcr, String terminalId) throws PosException;

	protected String getIccTableEntryData(List<IssuerData> issuers) {
		StringBuilder tempTableData = new StringBuilder();
		for (IssuerData i : issuers) {
			tempTableData.append(RECORD_SEPARATOR);
			tempTableData.append(getIccTableEntryRecords(i));
		}
		return tempTableData.toString();
	}

	@SuppressWarnings("unchecked")
	protected String getIccTableEntryRecords(IssuerData isd) {
		Log.debug("Icc Table Entry", new HashMap<Object, Object>(new BeanMap(
				isd)).toString());
		PosStringBuilder buffer = new PosStringBuilder();
		PosStringBuilder tBuffer = new PosStringBuilder();

		tBuffer.append(isd.getTableNumber(), ONE_BYTE, 2);
		tBuffer.append(isd.getRpIdPK(), TWO_BYTE, 4);
		tBuffer.append(isd.getIccOption1(), ONE_BYTE, 2);
		tBuffer.append(isd.getIccOption2(), ONE_BYTE, 2);
		tBuffer.append(isd.getIccOption3(), ONE_BYTE, 2);
		tBuffer.append(isd.getTargetPercentage(), ONE_BYTE, 2);
		tBuffer.append(isd.getMaxTargetPercentage(), ONE_BYTE, 2);
		tBuffer.append(isd.getThresholdValue(), TWO_BYTE, 4);
		tBuffer.append(isd.getTacDenial(), FIVE_BYTE, 10);
		tBuffer.append(isd.getTacOnline(), FIVE_BYTE, 10);
		tBuffer.append(isd.getTacDefault(), FIVE_BYTE, 10);
		tBuffer.append(isd.getTransactionCategoryCode(), ONE_BYTE, 2);
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		tBuffer.append(buffer.toString());
		buffer.clear();
		buffer.append(IsoUtil.hex2AsciiChar(TABLE_ID_ICC_TABLE_ENTRY));
		buffer.append(Util.getCompressedLength(tBuffer.toString(), 4));
		buffer.append(tBuffer.toString());
		return buffer.toString();
	}

	@SuppressWarnings("unchecked")
	protected String getKeyDataRecords(KeyData kd) {
		Log.debug("KeyData",
				new HashMap<Object, Object>(new BeanMap(kd)).toString());
		PosStringBuilder buffer = new PosStringBuilder();
		PosStringBuilder tBuffer = new PosStringBuilder();
		tBuffer.append(kd.getRid(), FIVE_BYTE, 10);
		//tBuffer.append(kd.getStatus(), ONE_BYTE, 2);
		tBuffer.append(kd.getStatus());
		tBuffer.append(kd.getKeyIndex(), ONE_BYTE, 2);
		tBuffer.append(kd.getKeyLength(), TWO_BYTE, 4);
		tBuffer.append(kd.getKey());
		tBuffer.append(kd.getKeyExponent(), TWO_BYTE,4);
		tBuffer.append(Util.appendChar(kd.getKeyHash(), '0', 40, false));	
		tBuffer.append(kd.getKeyActiveDate());
		tBuffer.append(kd.getKeyExpiryDate());
	//	buffer.append(TABLE_ID_KEYDATA);
		tBuffer.append("", FIVE_BYTE, 10);//RFUl
		buffer.append(IsoUtil.hex2AsciiChar(TABLE_ID_KEYDATA));

		buffer.append(Util.getCompressedLength(tBuffer.toString(), 4));
		buffer.append(tBuffer.toString());
		return buffer.toString();

	}

	protected String getIccDataContactBased(List<IccData> iccList)
			throws PosException {
		StringBuilder tempTableData = new StringBuilder();
		for (IccData i : iccList) {
			tempTableData.append(RECORD_SEPARATOR);
			tempTableData.append(getIccContactBased(i));
		}
		return tempTableData.toString();
	}

	protected String getIccDataContactLess(List<IccData> iccList)
			throws PosException {
		StringBuilder tempTableData = new StringBuilder();
		for (IccData o : iccList) {
			tempTableData.append(RECORD_SEPARATOR);
			tempTableData.append(getIccContactLess(o));
		}
		return tempTableData.toString();
	}

	@SuppressWarnings("unchecked")
	protected String getIccContactBased(IccData ic) throws PosException {
		Log.debug("Icc Contact based data", new HashMap<Object, Object>(
				new BeanMap(ic)).toString());
		PosStringBuilder buffer = new PosStringBuilder();
		PosStringBuilder tBuffer = new PosStringBuilder();
		tBuffer.append(ic.getTerminalDataCount());
		tBuffer.append(Util.appendChar(ic.getTerminalProfileName(), ' ', 20, false));//Terminal ICC Profile Name
		tBuffer.append(ic.getAidLength(), ONE_BYTE, 2);//AID Length
		tBuffer.append(ic.getAid());//AID
		tBuffer.append(Util.appendChar(ic.getCardScheme(), ' ',30, false));
		tBuffer.append(ic.getAppSelectIndicator(),ONE_BYTE, 2);
		tBuffer.append(ic.getAppVersionNo(), TWO_BYTE, 4);
		tBuffer.append(ic.getTransactionTypes());
		tBuffer.append(Util.appendChar(ic.getDefaultTDOLLength(), '0', 2,true));
		//tBuffer.append(ic.getDefaultTDOLLength(), ONE_BYTE, 2);
		tBuffer.append(ic.getDefaultTDOL());
		tBuffer.append(Util.appendChar(ic.getDefaultDDOLLength(), '0', 2,true));
	//	tBuffer.append(ic.getDefaultDDOLLength(), ONE_BYTE, 2);
		tBuffer.append(ic.getDefaultDDOL());//Defaul
		
		tBuffer.append(ic.getTerminalFloorLimit(), THREE_BYTE + THREE_BYTE, 12);
		tBuffer.append(Util.appendChar(ic.getThresholdSelection(), '0', 12,true));
		tBuffer.append(Util.appendChar(ic.getTargetSelection(), '0', 2,true));
		tBuffer.append(Util.appendChar(ic.getMaximumSelection(), '0', 2,true));
		tBuffer.append(ic.getTacDenial());//Terminal Action Code (TAC) – Denial
		tBuffer.append(ic.getTacOnline());//Terminal Action Code (TAC) – Online
		tBuffer.append(ic.getTacDefault());//Terminal Action Code (TAC) – Default
		tBuffer.append("", FIVE_BYTE, 10);//RFU
	//	buffer.append(TABLE_ID_ICC_CHIP_CB);
		buffer.append(IsoUtil.hex2AsciiChar(TABLE_ID_ICC_CHIP_CB));
		buffer.append(Util.getCompressedLength(tBuffer.toString(), 4));
		buffer.append(tBuffer.toString());
		return buffer.toString();
	}

	@SuppressWarnings("unchecked")
	protected String getIccContactLess(IccData ic) throws PosException {
		Log.debug("Icc Contact less data", new HashMap<Object, Object>(
				new BeanMap(ic)).toString());
		PosStringBuilder buffer = new PosStringBuilder();
		PosStringBuilder tBuffer = new PosStringBuilder();
		
		tBuffer.append(ic.getTerminalDataCount());
		tBuffer.append(Util.appendChar(ic.getTerminalProfileName(), ' ', 20, false));//Terminal ICC Profile Name		
		tBuffer.append(ic.getAidLength(), ONE_BYTE, 2);//AID Length	
		tBuffer.append(ic.getAid());//AID
		tBuffer.append(Util.appendChar(ic.getCardScheme(), ' ',30, false));	
		tBuffer.append(ic.getAppSelectIndicator(),ONE_BYTE, 2);
		tBuffer.append(ic.getAppVersionNo(), TWO_BYTE, 4);//App Version Number
		tBuffer.append(ic.getTransactionTypes());//Transaction Types		
		tBuffer.append(Util.appendChar(ic.getDefaultTDOLLength(), '0', 2,true));
	//	tBuffer.append(ic.getDefaultTDOLLength(), ONE_BYTE, 2);//AID Length	
		tBuffer.append(ic.getDefaultTDOL());
		tBuffer.append(ic.getTerminalFloorLimit(), THREE_BYTE + THREE_BYTE, 12);
		tBuffer.append(ic.getTacDenial(),FIVE_BYTE,10);//Terminal Action Code (TAC) – Denial
		tBuffer.append(ic.getTacOnline(),FIVE_BYTE,10);//Terminal Action Code (TAC) – Online
		tBuffer.append(ic.getTacDefault(),FIVE_BYTE,10);//Terminal Action Code (TAC) – Default
		tBuffer.append(ic.getTermianlRiskdata(),FOUR_BYTE+FOUR_BYTE,16);//Terminal Risk management data
		tBuffer.append(ic.getCvmLimit(),THREE_BYTE + THREE_BYTE, 12);
		tBuffer.append(ic.getTransactionLimit(), THREE_BYTE + THREE_BYTE, 12);
		tBuffer.append(ic.getMagStripeAppVersionNo(),TWO_BYTE,4);
		tBuffer.append(ic.getTerminalQualifiers(),FOUR_BYTE,8);
		tBuffer.append("", FIVE_BYTE, 10);//RFUl
		//buffer.append(TABLE_ID_ICC_CHIP_CL);
		buffer.append(IsoUtil.hex2AsciiChar(TABLE_ID_ICC_CHIP_CL));
		buffer.append(Util.getCompressedLength(tBuffer.toString(), 4));
		buffer.append(tBuffer.toString());
		return buffer.toString();
	}
	
	@SuppressWarnings("unchecked")
	protected String getTerminalDataRecords(EmvTerminalData etd) {
		Log.debug("EmvTerminalData",
				new HashMap<Object, Object>(new BeanMap(etd)).toString());
		PosStringBuilder buffer = new PosStringBuilder();
		PosStringBuilder tBuffer = new PosStringBuilder();
		tBuffer.append(etd.getTerminalDataCount());
		tBuffer.append(Util.appendChar(etd.getTerminalProfileName(), ' ', 20, false));
		tBuffer.append(etd.getTerminalType());
		tBuffer.append(etd.getTerminalCapabilities());
		tBuffer.append(etd.getAdditionalTerminalCapabilities());
		tBuffer.append(etd.getTerminalCountryCode());
		tBuffer.append(etd.getTxnCurrCode());
		tBuffer.append(etd.getTxnCurrExponent());
		tBuffer.append(Util.appendChar(etd.getDdolLength(), '0', 2,true));
		tBuffer.append(etd.getDdol());
		tBuffer.append(Util.appendChar(etd.getTdolLength(), '0', 2,true));
		tBuffer.append(etd.getTdol());
		tBuffer.append(FIVE_BYTE+FIVE_BYTE);		
	//	buffer.append((TABLE_ID_TERMINAL_DATA));
		buffer.append(IsoUtil.hex2AsciiChar(TABLE_ID_TERMINAL_DATA));
		buffer.append(Util.getCompressedLength(tBuffer.toString(), 4));
		buffer.append(tBuffer.toString());
		return buffer.toString();

	}


	protected String getIccOption1(IccData icc) throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(icc.getAppSelectIndicator());
		b.append(ZERO);
		b.append(ZERO);
		b.append(ZERO);
		b.append(ZERO);
		b.append(ZERO);
		b.append(ZERO);
		b.append(ZERO);
		return b.toString();
	}

	protected String getKeyData(List<KeyData> keyDataList) {
		StringBuilder tempTableData = new StringBuilder();
		for (KeyData o : keyDataList) {
			tempTableData.append(RECORD_SEPARATOR);
			tempTableData.append(getKeyDataRecords(o));
		}
		return tempTableData.toString();
	}

	protected static String binary2asciiCharLSB(String binary) {
		if (binary == null)
			return null;
		StringBuilder buffer = new StringBuilder();
		binary = new StringBuilder(binary).reverse().toString();
		if (binary == null)
			return null;
		for (int i = 0; i < binary.length(); i += 8) {
			String losgTemp = binary.substring(i, i + 8);
			int lonuIntValue = 0;
			for (int k = 0, j = losgTemp.length() - 1; j >= 0; j--, k++)
				lonuIntValue += Integer.parseInt(String.valueOf(losgTemp
						.charAt(j))) * Math.pow(2, k);
			buffer.append((char) lonuIntValue);
		}
		return buffer.toString();
	}
	
	protected String getTerminalData(List<EmvTerminalData> terminalDataList) {
		StringBuilder tempTableData = new StringBuilder();
		for (EmvTerminalData o : terminalDataList) {
			tempTableData.append(RECORD_SEPARATOR);
			tempTableData.append(getTerminalDataRecords(o));
		}
		return tempTableData.toString();
	}
	//For ParamDownload		
	@SuppressWarnings("unchecked")
	public ParamDownloadResponseMasterBean paramDownloadResponseData(String terminalId, String mspAcr, String channel,String procedureName) throws Exception {
		try {
		List<Object> objList = parameterDownloadService.getDownloadBeans(
				terminalId, mspAcr, procedureName);
		
		String responseCode=((List<ResponseStatus>) objList	.get(0)).get(0).getStatus();
		//String downloadData = initializeTerminal(objList, mspAcr, terminalId);
		
		//if (Util.isNullOrEmpty(downloadData))
			//throw new PosException(Constants.ERR_SYSTEM_ERROR);
		
		TerminalInfoResponse terminalInfo=new TerminalInfoResponse();
		ParamDwnldResponse response = new ParamDwnldResponse();
		ParamDownloadResponseMasterBean paramDownloadResponse = new ParamDownloadResponseMasterBean();
		
		if(responseCode.equals(Constants.SUCCESS)) {
		
		List<TerminalConfig> tcList = ((List<TerminalConfig>) objList.get(1));
		if (tcList.isEmpty()) {
			Log.trace("No tct data");
			throw new PosException(Constants.ERR_DATABASE);
		}
		TerminalConfig terminalConfig = tcList.get(0);
		
		terminalInfo.setMerchantId(terminalConfig.getMerchantId());
		terminalInfo.setStoreId("storeId-hc");
	    terminalInfo.setTerminalId("terminalId-hc");
	    terminalInfo.setStoreName("storeName-hc");
	    terminalInfo.setAddress("address-hc");
	    terminalInfo.setCity("city-hc");
	    terminalInfo.setPinCode("pinCode-hc");
	    terminalInfo.setState("state-hc");
	    terminalInfo.setCountry("country-hc");
	    terminalInfo.setTerminalType(terminalConfig.getTerminalType());
	    terminalInfo.setBatchNo(terminalConfig.getBatchNo());
	    terminalInfo.setNextBatchNo(terminalConfig.getNextBatchNo());
	    terminalInfo.setBusStartTime(terminalConfig.getBusStartTime());
	    terminalInfo.setBusEndTime(terminalConfig.getBusEndTime());
	    terminalInfo.setSettleTime(terminalConfig.getSettleTime());
	    terminalInfo.setMerchantCategoryCode(terminalConfig.getMerchantCategoryCode());
	    terminalInfo.setReceiptLine1("receiptLine1-hc");
	    terminalInfo.setReceiptLine2(terminalConfig.getReceiptLine2());
	    terminalInfo.setReceiptLine3(terminalConfig.getReceiptLine3());
	    terminalInfo.setHelpDeskPhone(terminalConfig.getHelpDeskPhone());
	    terminalInfo.setLanguageIndicator(terminalConfig.getLanguageIndicator());
	    terminalInfo.setDefaultTxn(terminalConfig.getDefaultTxn());
	    terminalInfo.setAdminCode(terminalConfig.getAdminCode());
	    terminalInfo.setSupervisorCode(terminalConfig.getSupervisorCode());
	    terminalInfo.setSettlementCurrencyDigits(terminalConfig.getSettlementCurrencyDigits());
	    terminalInfo.setMerchantDateTime(terminalConfig.getMerchantDateTime());
	    terminalInfo.setEnableEMI(terminalConfig.getEnableEMI());
	    terminalInfo.setAmexMerchantId(terminalConfig.getAmexMerchantId());
	    terminalInfo.setCurrencyName(terminalConfig.getCurrencyName());
	    terminalInfo.setCurrencyCode(terminalConfig.getCurrencyCode());
	    terminalInfo.setCurrencySymbol(terminalConfig.getCurrencySymbol());
	    terminalInfo.setCurrencyDigits(terminalConfig.getTxnCurrencyDigits());
	    terminalInfo.setCurrencyDecimal(terminalConfig.getCurrencyDecimal());
	    terminalInfo.setCountryCode(terminalConfig.getCountryCode());
	    terminalInfo.setBusinessDateFormat(terminalConfig.getBusinessDateFormat());
	    
	    response.setChannel(channel);
	    response.setBatchNo(terminalConfig.getBatchNo());
	    response.setResponseCode(responseCode);
	    if(responseCode.equals(Constants.SUCCESS))
	    	response.setResponseDescription("Success");	   
	    response.setTerminalInfo(terminalInfo);
	    
	    paramDownloadResponse.setResponse(response);
	    
		return paramDownloadResponse;
		}else {
			Log.debug("ParamDwnldresCode:else: ",responseCode);
			response.setResponseCode(responseCode);
			if(responseCode.equalsIgnoreCase(Constants.ERR_UE))				
				response.setResponseDescription(Validator.getErrorDescription(Constants.ERR_UE));
			else if(responseCode.equalsIgnoreCase(Constants.ERR_UI))
				response.setResponseDescription(Validator.getErrorDescription(Constants.ERR_UI));
			else if(responseCode.equalsIgnoreCase(Constants.ERR_UH))
				response.setResponseDescription(Validator.getErrorDescription(Constants.ERR_UH));
			else if(responseCode.equalsIgnoreCase(Constants.ERR_VQ))
				response.setResponseDescription(Validator.getErrorDescription(Constants.ERR_VQ));
			else 
				response.setResponseDescription("No data found");
			paramDownloadResponse.setResponse(response);
		    
			return paramDownloadResponse;
		}
		}catch (SQLException e) {
			Log.error("Risk Profile Download  ", e);
			throw new PosException(Constants.ERR_DATABASE);
		}
		}
	


//For SupportedCardDownload		
@SuppressWarnings("unchecked")
public SupportedCardDownloadResponse getCardDetails(String terminalId, String mspAcr, String channel,String procedureName) throws Exception {
	try {
		
	List<Object> objList = parameterDownloadService.getDownloadBeans(
			terminalId, mspAcr, procedureName);

	 SupportedCardDownloadResponse supportedcardresponse = new SupportedCardDownloadResponse();
	 
	String responseCode=((List<ResponseStatus>) objList	.get(0)).get(0).getStatus();
	
	
	SupportedCardResponse cardresponse = new SupportedCardResponse();

	if(responseCode.equals(Constants.SUCCESS)) {
		

		
	List<TerminalConfig> tcList = ((List<TerminalConfig>) objList.get(1));
	
	if (tcList.isEmpty()) {
		Log.trace("No tct data");
		throw new PosException(Constants.ERR_DATABASE);
	}

	TerminalConfig terminalConfig = tcList.get(0);
	
	SupportedCardDwnldDeviceInfoResponse deviceInfo = new SupportedCardDwnldDeviceInfoResponse();
	deviceInfo.setTerminalId(terminalId);
	
	List<CardRange> cardList = ((List<CardRange>) objList.get(2));
	List<CardRangeInfoResponse> cardRangeInfoList=new ArrayList<>();

    for(CardRange cardRange:cardList) {
    	
	    CardRangeInfoResponse	cardRangeInfo=new CardRangeInfoResponse();

	
	 cardRangeInfo.setCardName(cardRange.getCardName());
	 cardRangeInfo.setCardRangeId(cardRange.getCardRangeId());
	 cardRangeInfo.setBinLow(cardRange.getPanRangeLow());
	 cardRangeInfo.setBinHigh(cardRange.getPanRangeHigh());
	 cardRangeInfo.setDefaultAccType(cardRange.getDefaultAccType());
	 cardRangeInfo.setPanLength(cardRange.getPanLength());
	 cardRangeInfo.setEnableServiceCode(cardRange.getEnableServiceCode());
	 cardRangeInfo.setServiceCode(cardRange.getServiceCode());
	 cardRangeInfo.setEnableMod10Val(cardRange.getEnableMod10Val());
	 cardRangeInfo.setEmiEnabled(cardRange.getEmiEnabled());
	 cardRangeInfo.setOfflineFloorLmt(cardRange.getOfflineFloorLmt());
	 cardRangeInfo.setIssuerId(cardRange.getIssuerId());
	 cardRangeInfoList.add(cardRangeInfo);
     }

	 
	 cardresponse.setChannel(channel);
	 cardresponse.setStan("stan");
	 cardresponse.setInvoiceNo("invoiceNo");
	 cardresponse.setBatchNo(terminalConfig.getBatchNo());
	 cardresponse.setResponseCode(responseCode);
	 if(responseCode.equals(Constants.SUCCESS)) {
		 cardresponse.setResponseDescription("Success");
	 }
	 cardresponse.setDeviceInfo(deviceInfo);
	 cardresponse.setCardRangeInfo(cardRangeInfoList);
	 supportedcardresponse.setResponse(cardresponse);

	  return supportedcardresponse; 
	 
	}
	else {
		Log.debug("SupportCardresCode:",responseCode);
		cardresponse.setResponseCode(responseCode);
		if(responseCode.equalsIgnoreCase(Constants.ERR_UE))				
			cardresponse.setResponseDescription(Validator.getErrorDescription(Constants.ERR_UE));
		else if(responseCode.equalsIgnoreCase(Constants.ERR_UI))
			cardresponse.setResponseDescription(Validator.getErrorDescription(Constants.ERR_UI));
		else if(responseCode.equalsIgnoreCase(Constants.ERR_UH))
			cardresponse.setResponseDescription(Validator.getErrorDescription(Constants.ERR_UH));
		else if(responseCode.equalsIgnoreCase(Constants.ERR_VQ))
			cardresponse.setResponseDescription(Validator.getErrorDescription(Constants.ERR_VQ));
		else 
			cardresponse.setResponseDescription("No data found");
		supportedcardresponse.setResponse(cardresponse);
	    
		return supportedcardresponse;
	}
	}
	catch (SQLException e) {
		Log.error("Supportcard Download  ", e);
		throw new PosException(Constants.ERR_DATABASE);
	}
	}
}
