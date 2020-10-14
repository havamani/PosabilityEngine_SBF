package com.fss.pos.client.hpdh;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.fss.pos.base.api.client.Client;
import com.fss.pos.base.api.client.ClientData;
import com.fss.pos.base.api.client.Validator;
import com.fss.pos.base.api.db.storedprocedure.StoredProcedureInfo;
import com.fss.pos.base.api.hsm.HsmResponse;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.EmvTags;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.client.AbstractClientApi;
import com.fss.pos.client.services.additionaldata.AdditionalDataInfo;
import com.fss.pos.client.services.download.patchupdate.RemotePatchUpdateData;
import com.fss.pos.client.services.download.remotekey.KeyDownloadClientData;
import com.fss.pos.client.services.operator.TerminalInfo;
import com.fss.pos.client.services.operator.TerminalOperator;
import com.fss.pos.client.services.settlement.BatchTotals;
import com.fss.pos.client.services.settlement.SettlementFormat;
import com.fss.pos.client.services.settlement.StandardBatchTotals;

@Client
public class ISO8583AggregatorClientApi extends AbstractClientApi {

	private static final List<String> EMV_TAG_LEN_2;
	private static final List<Method> BATCH_DATA_METHODS;
	private static final String JSON_KEY_DE48_RRN = "rrn";
	// private static final String CURRENCY_CODE_TAG = "5F2A";
	// private static final String COUNTRY_CODE_TAG = "9F1A";

	private static final char[] ISO8583bitmapLengthType = {

	'H', 'H', 'H', 'H', 'H', 'H', 'H', 'H', // 8
			'H', 'H', 'H', 'H', 'H', 'H', 'H', 'H', // 16
			'H', 'H', 'H', 'H', 'H', 'H', 'H', 'H', // 24
			'H', 'H', 'H', 'H', 'H', 'H', 'H', 'H', // 32
			'H', 'H', 'H', 'H', 'H', 'H', 'H', 'H', // 40
			'H', 'H', 'H', 'H', 'H', 'H', 'H', 'H', // 48
			'H', 'H', 'H', 'H', 'H', 'H', 'H', 'H', // 56
			'H', 'H', 'H', 'H', 'H', 'H', 'H', 'H', // 64
	};

	private static final char[] ISO8583bitmaptype = {

	'B', 'N', 'N', 'N', 'N', 'N', 'N', 'N', // 8
			'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', // 16
			'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', // 24
			'N', 'N', 'N', 'A', 'A', 'A', 'A', 'N', // 32
			'N', 'N', 'N', 'A', 'A', 'A', 'A', 'A', // 40
			'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', // 48
			'A', 'A', 'A', 'N', 'N', 'A', 'A', 'A', // 56
			'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', // 64
	};

	private static final int[] ISO8583bitmap = { 8, -2, 6, 12, 12, 12, 10, 8, // 8
			8, 8, 6, 6, 4, 4, 4, 4, // 16
			4, 4, 3, 3, 3, 3, 3, 3, // 24
			2, 2, 1, 9, 9, 9, 9, -2, // 32
			-2, -2, -2, -3, 12, 6, 2, 3, // 40
			8, 15, 40, -2, -2, -3, -3, -3, // 48
			3, 3, 3, 16, 16, -3, -3, -3, // 56
			-3, -3, -3, -3, -3, -3, -3, 16, // 64

	};

	private static final List<String> VALID_MSG_TYPES;
	private static final List<String> RESPONSE_FIELDS;
	private static final String MSG_TYP = "MSG-TYP";

	static {
		EMV_TAG_LEN_2 = new ArrayList<String>();
		EMV_TAG_LEN_2.add("82");
		EMV_TAG_LEN_2.add("84");
		EMV_TAG_LEN_2.add("95");
		EMV_TAG_LEN_2.add("9B");
		EMV_TAG_LEN_2.add("9A");
		EMV_TAG_LEN_2.add("9C");
		EMV_TAG_LEN_2.add("06");
		EMV_TAG_LEN_2.add("41");
		EMV_TAG_LEN_2.add("42");
		EMV_TAG_LEN_2.add("43");
		EMV_TAG_LEN_2.add("44");
		EMV_TAG_LEN_2.add("45");
		EMV_TAG_LEN_2.add("46");
		EMV_TAG_LEN_2.add("47");
		EMV_TAG_LEN_2.add("48");
		EMV_TAG_LEN_2.add("4D");
		EMV_TAG_LEN_2.add("4F");
		EMV_TAG_LEN_2.add("50");
		EMV_TAG_LEN_2.add("51");
		EMV_TAG_LEN_2.add("52");
		EMV_TAG_LEN_2.add("53");
		EMV_TAG_LEN_2.add("56");
		EMV_TAG_LEN_2.add("57");
		EMV_TAG_LEN_2.add("58");
		EMV_TAG_LEN_2.add("59");
		EMV_TAG_LEN_2.add("5A");
		EMV_TAG_LEN_2.add("5B");
		EMV_TAG_LEN_2.add("5C");
		EMV_TAG_LEN_2.add("5D");
		EMV_TAG_LEN_2.add("5E");

		LinkedList<String> batchFields = new LinkedList<String>();
		batchFields.add("captureCardsNoOfDebits");
		batchFields.add("captureCardsAmtOfDebits");
		batchFields.add("captureCardsNoOfCredits");
		batchFields.add("captureCardsAmtOfCredits");
		batchFields.add("debitCardsNoOfDebits");
		batchFields.add("debitCardsAmtOfDebits");
		batchFields.add("debitCardsNoOfCredits");
		batchFields.add("debitCardsAmtOfCredits");
		batchFields.add("authCardsNoOfDebits");
		batchFields.add("authCardsAmtOfDebits");
		batchFields.add("authCardsNoOfCredits");
		batchFields.add("authCardsAmtOfCredits");

		BATCH_DATA_METHODS = new LinkedList<Method>();
		try {
			for (String field : batchFields)
				BATCH_DATA_METHODS.add(StandardBatchTotals.class
						.getDeclaredMethod("set" + Util.capitalize(field),
								String.class));
		} catch (Exception e) {
			Log.error("Batch data static block", e);
		}

		List<String> vmt = new ArrayList<String>();
		vmt.add("0200");
		vmt.add("0210");
		vmt.add("0215");
		vmt.add("0220");
		vmt.add("0221");
		vmt.add("0230");
		vmt.add("0400");
		vmt.add("0500");
		vmt.add("0510");
		vmt.add("0410");
		vmt.add("0402");
		vmt.add("0412");
		vmt.add("0420");
		vmt.add("0421");
		vmt.add("0430");
		vmt.add("0320");
		vmt.add("0330");
		vmt.add("0800");
		vmt.add("0810");
		vmt.add("0100");
		vmt.add("0110");
		VALID_MSG_TYPES = Collections.unmodifiableList(vmt);

		List<String> rspFields = new ArrayList<String>();
		rspFields.add(Constants.DE3);
		rspFields.add(Constants.DE4);
		rspFields.add(Constants.DE11);
		rspFields.add(Constants.DE12);
		rspFields.add(Constants.DE13);
		rspFields.add(Constants.DE24);
		rspFields.add(Constants.DE37);
		rspFields.add(Constants.DE38);
		rspFields.add(Constants.DE39);
		rspFields.add(Constants.DE41);
		rspFields.add(Constants.DE54);
		rspFields.add(Constants.DE55);
		rspFields.add(Constants.DE63);
		rspFields.add(Constants.DE64);
		RESPONSE_FIELDS = Collections.unmodifiableList(rspFields);
	}

	@Override
	public IsoBuffer parse(String message) throws PosException {
		return parseMessage(message.substring(2));
	}

	@Override
	public String build(IsoBuffer isoBuffer) {
		return buildMessage(isoBuffer);
	}

	@Override
	public KeyDownloadClientData parseKeyDownloadRequestData(String data)
			throws PosException {
		KeyDownloadClientData k = new KeyDownloadClientData();
		k.setScheme("2");
		k.setType("1");
		k.setId("1");
		return k;
	}

	@Override
	public void buildKeyDownloadResponseData(HsmResponse hsmGenResponse,
			IsoBuffer isoBuffer) {
		isoBuffer.put(Constants.DE63,
				IsoUtil.hex2AsciiChar(hsmGenResponse.getParentEncryptedKey()));
	}

	@Override
	public Map<EmvTags, String> parseEmvData(IsoBuffer isoBuffer) {

		Map<EmvTags, String> tv = new LinkedHashMap<EmvTags, String>();

		if (isoBuffer.isFieldEmpty(Constants.DE55))
			return tv;

		String d = new String(IsoUtil.asciiChar2hex(
				isoBuffer.get(Constants.DE55)).toUpperCase());
		Log.debug("EMV Client Data : ", d);

		while (d.length() > 0) {
			int idx = 0;
			String tag = d.substring(0, idx + 2);
			if (EMV_TAG_LEN_2.contains(tag))
				idx += 2;
			else
				tag = d.substring(idx, idx += 4);
			int len = IsoUtil.hex2decimal(d.substring(idx, idx += 2));
			len *= 2;
			String value = d.substring(idx, idx += len);
			d = d.substring(idx);
			// if (value.length() > 0)
			tv.put(EmvTags.getEmvTag(tag), value);
		}
		return tv;
	}

	private void buildEmvData(Map<EmvTags, String> emvMap, IsoBuffer isoBuffer) {
		// Log.debug("EMV Response Map", emvMap.toString());
		StringBuilder emvRsp = new StringBuilder();
		for (EmvTags t : emvMap.keySet()) {
			emvRsp.append(IsoUtil.hex2AsciiChar(t.toString()));
			String asciiData = IsoUtil.hex2AsciiChar(emvMap.get(t));
			String hexLength = Util.appendChar(
					Util.decToHex(asciiData.length()), '0', 2, true);
			emvRsp.append(IsoUtil.hex2AsciiChar(hexLength));
			emvRsp.append(asciiData);
		}
		isoBuffer.put(Constants.DE55, emvRsp.toString());
		Log.debug("final p55 data after building",
				IsoUtil.asciiChar2hex(emvRsp.toString()));
	}

	@Override
	public BatchTotals parseSettlementRequest(Object batchData)
			throws PosException {
		String bd = (String) batchData;
		try {
			StandardBatchTotals batchDataObj = new StandardBatchTotals();
			int i = 0;
			while (i < BATCH_DATA_METHODS.size()) {
				Method m = BATCH_DATA_METHODS.get(i);
				m.invoke(batchDataObj, bd.substring(0, 3));
				m = BATCH_DATA_METHODS.get(i + 1);
				m.invoke(batchDataObj, bd.substring(3, 15));
				i += 2;
				bd = bd.substring(15);
			}
			return batchDataObj;
		} catch (Throwable e) {
			Log.error("HpdhClientApi parseSettlementRequest ", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
	}

	@Override
	public RemotePatchUpdateData parsePatchUpdateRequest(IsoBuffer isoBuffer)
			throws PosException {
		if (isoBuffer.isFieldEmpty(Constants.DE60)) {
			Log.debug("Patch update error", "no p60 data");
			throw new PosException(Constants.ERR_INVALID_REQUEST);
		}
		String data = isoBuffer.get(Constants.DE60);
		RemotePatchUpdateData rmd = new RemotePatchUpdateData();
		int idx = 0;
		rmd.setFileName(data.substring(idx, idx += 15).trim());
		rmd.setTotalFileSize(Integer.parseInt(data.substring(idx, idx += 10)));
		rmd.setFileTransferIndex(Integer.parseInt(data.substring(idx, idx += 6)));
		rmd.setBytesTransferred(Integer.parseInt(data.substring(idx, idx += 10)));
		return rmd;
	}

	@Override
	public void buildPatchUpdateRequest(RemotePatchUpdateData rpud,
			IsoBuffer isoBuffer) {
		StringBuilder buf = new StringBuilder();
		buf.append(Util.appendChar(rpud.getFileName(), ' ', 15, false));
		buf.append(Util.appendChar(String.valueOf(rpud.getTotalFileSize()),
				'0', 10, true));
		buf.append(Util.appendChar(String.valueOf(rpud.getFileTransferIndex()),
				'0', 6, true));
		buf.append(Util.appendChar(String.valueOf(rpud.getBytesTransferred()),
				'0', 10, true));
		buf.append(rpud.getFileData());
		isoBuffer.put(Constants.DE60, buf.toString());
	}

	public String parseBitmap(String iobsgBitmap) {
		StringBuilder parseBitmapResult = new StringBuilder();
		StringBuilder losgUpperBitmap = new StringBuilder();
		losgUpperBitmap.append("00000000000000000000000000000000");
		StringBuilder losgLowerBitmap = new StringBuilder();
		losgLowerBitmap.append("00000000000000000000000000000000");
		losgUpperBitmap.append(Long.toBinaryString(Long.parseLong(
				iobsgBitmap.substring(0, 8), 16)));
		losgLowerBitmap.append(Long.toBinaryString(Long.parseLong(
				iobsgBitmap.substring(8), 16)));
		parseBitmapResult.append(losgUpperBitmap.toString().substring(
				losgUpperBitmap.toString().length() - 32));
		parseBitmapResult.append(losgLowerBitmap.toString().substring(
				losgLowerBitmap.toString().length() - 32));
		return parseBitmapResult.toString();
	}

	public IsoBuffer parseMessage(final String message) throws PosException {

		//System.out.println(message);
		int offset = 4;
		IsoBuffer pthtHPDHBuffer = new IsoBuffer();
		
		pthtHPDHBuffer.put(Constants.TPDU_ID,"*");

		pthtHPDHBuffer.put(MSG_TYP, message.substring(0, 4));

		if (!VALID_MSG_TYPES.contains(pthtHPDHBuffer.get(MSG_TYP)))
			throw new PosException(Constants.ERR_INVALID_MSG_TYPE);

		int bit = 0;
		try {
			String pBitmap = parseBitmap(message.substring(offset, offset + 16));
			offset += 16;
			pthtHPDHBuffer.put("pBitmap", pBitmap);

			for (int i = 0; i < 64; i++) {
				bit = i;
				if ('1' == pBitmap.charAt(i)) {

					if (ISO8583bitmap[i] < 0) {

						if (ISO8583bitmaptype[i] == 'N'
								|| ISO8583bitmaptype[i] == 'A') {

							int size = Integer
									.parseInt(message.substring(offset, offset
											+ Math.abs(ISO8583bitmap[i])));

							//System.out
							//		.println("ith data" + i + "\tsize" + size);

							offset += -1 * ISO8583bitmap[i];

							String data = message.substring(offset, offset
									+ size);

							//System.out.println("ith data" + i + "\tdata "
								//	+ data);

							pthtHPDHBuffer.put(IsoBuffer.PREFIX_PRIMARY
									+ (i + 1), data.substring(0, size));

							offset += size;
						}
					} else {

						int size = ISO8583bitmap[i];

						if (ISO8583bitmaptype[i] == 'N'
								|| ISO8583bitmaptype[i] == 'A') {

							System.out
									.println("ith data" + i + "\tsize" + size);

							String data = message.substring(offset, offset
									+ size);

							//System.out.println("ith data" + i + "\tdata "
							//		+ data);

							pthtHPDHBuffer.put(IsoBuffer.PREFIX_PRIMARY
									+ (i + 1), data.substring(0, size));

							offset += size;
						}
					}
				} else {
					pthtHPDHBuffer.put(IsoBuffer.PREFIX_PRIMARY + (i + 1), "*");
				}

			}
			for (int i = 64; i < 129; i++) {
				pthtHPDHBuffer.put("S-" + (i + 1), "*");
			}
			if ("0000000000000000".equals(pthtHPDHBuffer.get(Constants.DE52))) {
				pthtHPDHBuffer.put(Constants.DE52, "*");
			}
		} catch (Exception e) {
			Log.error("HpdhClientApi parseHpdhMessage problem bit  : "
					+ (bit + 1), e);
			Log.info("Parsing error in bit  : " + (bit + 1),
					pthtHPDHBuffer.toString());
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
		return pthtHPDHBuffer;
	}

	public String buildMessage(IsoBuffer isoBuffer) {
		String messageType = IsoUtil.buildMessageType(isoBuffer.get(MSG_TYP)
				.toString().trim());
		String message = buildISO8583(messageType, isoBuffer);
		StringBuilder sBuffer = new StringBuilder(IsoUtil.pad(
				String.valueOf(IsoUtil.toGraphical(message.length(), 2)), ' ',
				2, true));
		sBuffer.append(message);
		return sBuffer.toString();
	}

	public String buildISO8583(final String msgType, final IsoBuffer isoBuffer) {

		StringBuilder message = new StringBuilder(msgType);
		StringBuilder primaryBitMap = new StringBuilder();
		for (int i = 0; i <= 63; i++) {

			if (!isoBuffer.get(IsoBuffer.PREFIX_PRIMARY + (i + 1)).toString()
					.equals("*")) {

				if (ISO8583bitmap[i] < 0) {

					if (ISO8583bitmaptype[i] == 'N') {
						message.append(Util.appendChar(String.valueOf(isoBuffer
								.get(IsoBuffer.PREFIX_PRIMARY + (i + 1))
								.toString().length()), '0', Math
								.abs(ISO8583bitmap[i]), true));
						message.append(new String(isoBuffer
								.get(IsoBuffer.PREFIX_PRIMARY + (i + 1))));

						primaryBitMap.append("1");
					} else if (ISO8583bitmaptype[i] == 'A') {
						/*
						 * message.append(
						 * calculateLength(isoBuffer.get(IsoBuffer
						 * .PREFIX_PRIMARY + (i + 1)).toString().length(),
						 * ISO8583bitmap[i], ISO8583bitmapLengthType[i]));
						 */message
								.append(Util.appendChar(
										String.valueOf(isoBuffer
												.get(IsoBuffer.PREFIX_PRIMARY
														+ (i + 1)).toString()
												.length()), '0',
										Math.abs(ISO8583bitmap[i]), true));
						message.append(new String(isoBuffer
								.get(IsoBuffer.PREFIX_PRIMARY + (i + 1))));

						primaryBitMap.append("1");
					} else {
						primaryBitMap.append("0");
					}
				} else {
					if (ISO8583bitmaptype[i] == 'N') {
						/*
						 * message.append(
						 * calculateLength(isoBuffer.get(IsoBuffer
						 * .PREFIX_PRIMARY + (i + 1)).toString().length(),
						 * ISO8583bitmap[i], ISO8583bitmapLengthType[i]));
						 */
						

						message.append(new String(isoBuffer
								.get(IsoBuffer.PREFIX_PRIMARY + (i + 1))));

						primaryBitMap.append("1");

					} else if (ISO8583bitmaptype[i] == 'A') {
					
						message.append(new String(isoBuffer
								.get(IsoBuffer.PREFIX_PRIMARY + (i + 1))));

						primaryBitMap.append("1");
					} else {
						primaryBitMap.append("0");
					}
				}
			} else {
				primaryBitMap.append("0");
			}
		}
		return message.substring(0, 4)
				+ IsoUtil.binary2hex(primaryBitMap.toString())
				+ message.substring(4);
	}

	@Override
	public TerminalOperator getTerminalOperatorData(IsoBuffer isoBuffer)
			throws PosException {
		return new TerminalOperator();
	}

	@Override
	public TerminalInfo getTerminalInfo(IsoBuffer isoBuffer)
			throws PosException {
		return new TerminalInfo();
	}

	@Override
	public void modifyBits4Response(IsoBuffer isoBuffer, Data data) {

		for (int i = 1; i < 65; i++) {
			String f = IsoBuffer.PREFIX_PRIMARY + i;
			if (!RESPONSE_FIELDS.contains(f))
				isoBuffer.disableField(f);
		}

		/*
		 * if (!Constants.SUCCESS.equals(isoBuffer.get(Constants.DE39))) {
		 * isoBuffer.disableField(Constants.DE55); }
		 */

		if (isoBuffer.isFieldEmpty(Constants.DE55)) {
			isoBuffer.disableField(Constants.DE55);
		} else {
			ClientData cd = (ClientData) data;
			if (cd != null) {
				if (cd.getEmvResponseMap() != null
						&& !cd.getEmvResponseMap().isEmpty())
					buildEmvData(cd.getEmvResponseMap(), isoBuffer);
			}

		}
	}

	@Override
	public String getStoredProcedure() {
		return StoredProcedureInfo.TRANSACTION_VALIDATION_AGG_ISO8583;
	}

	/*
	 * @Override public String getSettlementBatchProcedureName() { return
	 * StoredProcedureInfo.BATCH_UPLOAD_HPDH; }
	 */

	@Override
	public boolean errorDescriptionRequired() {
		return true;
	}

	@Override
	public SettlementFormat getBatchTotalsFormat() {
		return SettlementFormat.STANDARD_FORMAT;
	}

	/*
	 * @Override public String getSettlementProcedureName() { return
	 * StoredProcedureInfo.SETTLEMENT_HPDH; }
	 */

	@Override
	public String getSettlementStoredProcedure() {
		return StoredProcedureInfo.SETTLEMENT_HPDH;
	}

	@Override
	public Boolean checkHostToHost() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void downloadParameters(String mspAcr, IsoBuffer isoBuffer)
			throws PosException {
		// TODO Auto-generated method stub

	}

	private static String calculateLength(int dataLength, int messageSize,
			char fieldLengthDataType) {

		String strLength = "";
		if (messageSize < 0) {

			switch (fieldLengthDataType) {

			case 'B':
			case 'H':

				strLength = IsoUtil.binary2hex(IsoUtil.hex2binary(Integer
						.toHexString(dataLength)));
				break;

			case 'T':
				if (dataLength % 2 != 0)
					dataLength = dataLength + 1;
				strLength = IsoUtil.binary2hex(IsoUtil.hex2binary(Integer
						.toHexString(dataLength / 2)));

				break;

			default:

				strLength = Integer.toHexString(dataLength);

				break;
			}
		}

		return Math.abs(messageSize) > strLength.length() ? "0" + strLength
				: strLength;
	}

	@Override
	public String getSettlementStoredProcedureExt() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdditionalDataInfo getAdditionalDataInfo(IsoBuffer isoBuffer)
			throws PosException {
		try {
			AdditionalDataInfo addInfo = new AdditionalDataInfo();
			if (!isoBuffer.isFieldEmpty(Constants.DE48)) {
				if (Validator.isJSONValid(isoBuffer.get(Constants.DE48))) {
					JSONObject jo = new JSONObject(
							isoBuffer.get(Constants.DE48));
					addInfo.setRrn((String) jo.opt(JSON_KEY_DE48_RRN));
				} else {
					addInfo.setRrn(isoBuffer.get(Constants.DE48));
				}
			}
			return addInfo;
		} catch (JSONException e) {
			Log.error("HPDH getUserData ", e);
			throw new PosException(null);
		}
	}

}
