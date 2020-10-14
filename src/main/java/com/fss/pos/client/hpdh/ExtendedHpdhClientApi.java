package com.fss.pos.client.hpdh;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

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
import com.fss.pos.client.services.settlement.SettlementFormat;
import com.fss.pos.client.services.settlement.TransactionBatchTotals;

@Client
public class ExtendedHpdhClientApi extends AbstractClientApi {

	private static final List<String> EMV_TAG_LEN_2;
	private static final List<Method> BATCH_DATA_METHODS;
	private static final String CURRENCY_CODE_TAG = "5F2A";
	private static final String COUNTRY_CODE_TAG = "9F1A";

	private static final String JSON_KEY_USER_ID = "USERID";
	private static final String JSON_KEY_CODE = "PASSWORD";
	private static final String JSON_KEY_SESSION_ID = "SESSIONID";
	private static final String JSON_KEY_INDICATOR = "INDICATOR";
	private static final String JSON_KEY_OLD_CODE = "OLDPASSWORD";
	private static final String JSON_KEY_NEW_CODE = "NEWPASSWORD";
	private static final String JSON_KEY_MSG_VER = "MSGVER";
	private static final String JSON_KEY_APP_VER = "APPVER";
	private static final String JSON_KEY_MAC_ID = "MCID";
	private static final String JSON_KEY_MDL_NO = "MDLNO";
	private static final String JSON_KEY_DE48_RRN = "rrn";

	private static final String ISO = "ISO";
	private static final String DC_ID = "DC-ID";
	private static final String MSG_TYP = "MSG-TYP";
	private static final String TPDU_ID = "TPDU-ID";
	private static final String REL_ID = "REL-ID";
	private static final String REASON_CODE = "REASON-CODE";
	private static final String ORIGINATOR = "ORIGINATOR";
	private static final String AUTHORIZOR = "AUTHORIZOR";

	private static final char[] HPDHbitmapPadAttribute = {

	'F', 'B', 'F', 'F', 'F', 'F', 'F', 'F', // 8
			'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', // 16
			'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', // 24
			'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', // 32
			'F', 'F', 'B', 'F', 'F', 'F', 'F', 'F', // 40
			'F', 'F', 'F', 'F', 'F', 'F', 'B', 'F', // 48
			'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', // 56
			'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', // 64
	};

	private static final char[] HPDHbitmaptype = {

	'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', // 8
			'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', // 16
			'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', // 24
			'N', 'N', 'N', 'A', 'A', 'A', 'A', 'N', // 32
			'N', 'N', 'N', 'A', 'A', 'A', 'A', 'A', // 40
			'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', // 48
			'N', 'N', 'N', 'N', 'N', 'A', 'A', 'A', // 56
			'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', // 64
	};

	private static final int[] HPDHbitmap = {

	8, -1, 6, 12, 12, 12, 10, 8, // 8
			8, 8, 6, 6, 4, 4, 4, 4, // 16
			4, 4, 3, 3, 3, 3, 3, 3, // 24
			2, 2, 1, 9, 9, 9, 9, -1, // 32
			-1, -1, -1, -1, 12, 6, 2, -3, // 40
			8, 15, 40, 20, -1, -1, -2, -2, // 48  from parandaman
			3, 3, 3, 16, 8, -2, -2, -1, // 56  from parandaman
		//	8, 15, 40, -1, -1, -1, -2, -2, // 48
		//	3, 3, 3, 16, 16, -2, -2, -1, // 56
			-1, -1, -1, -2, -2, -2, -2, -2, // 64
	};

	private static final List<String> VALID_MSG_TYPES;

	static {
		EMV_TAG_LEN_2 = new ArrayList<String>();
		EMV_TAG_LEN_2.add("82");
		EMV_TAG_LEN_2.add("84");
		EMV_TAG_LEN_2.add("95");
		EMV_TAG_LEN_2.add("9A");
		EMV_TAG_LEN_2.add("9C");
		EMV_TAG_LEN_2.add("4F");

		LinkedList<String> batchFields = new LinkedList<String>();
		batchFields.add("saleCount");
		batchFields.add("saleAmount");
		batchFields.add("tipCount");
		batchFields.add("tipAmount");
		batchFields.add("voidCount");
		batchFields.add("voidAmount");
		batchFields.add("refundCount");
		batchFields.add("refundAmount");
		batchFields.add("completionCount");
		batchFields.add("completionAmount");
		batchFields.add("cashBackCount");
		batchFields.add("cashBackAmount");
		batchFields.add("cashAdvanceCount");
		batchFields.add("cashAdvanceAmount");
		batchFields.add("motoCount");
		batchFields.add("motoAmount");
		batchFields.add("cashDepositCount");
		batchFields.add("cashDepositAmount");

		BATCH_DATA_METHODS = new LinkedList<Method>();
		try {
			for (String field : batchFields)
				BATCH_DATA_METHODS.add(TransactionBatchTotals.class
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
	}

	@Autowired
	private ExtendedHpdhParameterDownloadService extHpdhDownloadService;

	@Override
	public IsoBuffer parse(String message) throws PosException {
		return parseHpdhMessage(message.substring(2));
	}

	@Override
	public String build(IsoBuffer isoBuffer) {
		return buildHpdhMessage(isoBuffer);
	}

	@Override
	public KeyDownloadClientData parseKeyDownloadRequestData(String data)
			throws PosException {
		try {
			int idx = 0;
			String keyScheme = data.substring(idx, idx += 1);
			String keyType = data.substring(idx, idx += 1);
			String keyId = data.substring(idx, idx += 1);
			KeyDownloadClientData k = new KeyDownloadClientData();
			k.setScheme(keyScheme);
			k.setType(keyType);
			k.setId(keyId);
			k.setKsn(Constants.KEY_SCHEME_DUKPT.equals(keyScheme) ? data
					.substring(idx, idx + 10) : null);
			return k;
		} catch (Exception e) {
			Log.error("parseKeyDownloadRequestData ", e);
			throw new PosException(Constants.ERR_KEY_DOWNLOAD_PARSING);
		}

	}

	@Override
	public void buildKeyDownloadResponseData(HsmResponse hsmGenResponse,
			IsoBuffer isoBuffer) {
		StringBuilder buf = new StringBuilder(isoBuffer.get(Constants.DE63));
		buf.append(String.valueOf(hsmGenResponse.getParentEncryptedKey()
				.length()));
		buf.append(hsmGenResponse.getParentEncryptedKey());
		buf.append(hsmGenResponse.getChecksum().substring(0, 6));
		isoBuffer.put(Constants.DE63, buf.toString());
	}

	@Override
	public Map<EmvTags, String> parseEmvData(IsoBuffer isoBuffer) {

		Map<EmvTags, String> tv = new HashMap<EmvTags, String>();
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
			/*if (CURRENCY_CODE_TAG.equals(tag) || COUNTRY_CODE_TAG.equals(tag))
				value = value.substring(1);*/
			tv.put(EmvTags.getEmvTag(tag), value);
		}
		return tv;
	}

	private void buildEmvData(Map<EmvTags, String> emvMap, IsoBuffer isoBuffer) {
		Log.debug("EMV Response Map", emvMap.toString());
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
	}

	@Override
	public TransactionBatchTotals parseSettlementRequest(Object batchData)
			throws PosException {
		String bd = (String) batchData;
		try {
			TransactionBatchTotals batchDataObj = new TransactionBatchTotals();
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

	public IsoBuffer parseHpdhMessage(final String message) throws PosException {

		
		//System.out.println("extended"+message);
		int offset = 7;
		IsoBuffer pthtHPDHBuffer = new IsoBuffer();

		pthtHPDHBuffer.put(ISO, ISO);
		pthtHPDHBuffer.put(DC_ID, "01");
		pthtHPDHBuffer.put(REL_ID, "01");
		pthtHPDHBuffer.put(REASON_CODE, "001");
		pthtHPDHBuffer.put(ORIGINATOR, "1");
		pthtHPDHBuffer.put(AUTHORIZOR, "2");

		pthtHPDHBuffer.put(TPDU_ID, IsoUtil.binary2hex(IsoUtil
				.asciiChar2binary(message.substring(0, 5))));
		pthtHPDHBuffer.put(MSG_TYP, IsoUtil.binary2hex(IsoUtil
				.asciiChar2binary(message.substring(5, 7))));

		if (!VALID_MSG_TYPES.contains(pthtHPDHBuffer.get(MSG_TYP)))
			throw new PosException(Constants.ERR_INVALID_MSG_TYPE);

		int bit = 0;
		try {
			StringBuilder pBitmap = new StringBuilder();
			pBitmap.append(parseBitmap(IsoUtil.binary2hex(IsoUtil
					.asciiChar2binary(message.substring(offset, offset + 8)))));
			offset += 8;
			pthtHPDHBuffer.put("pBitmap", pBitmap.toString());

			for (int i = 0; i < 64; i++) {
				bit = i;
				if ('1' == pBitmap.charAt(i)) {

					if (HPDHbitmap[i] < 0) {

						if (HPDHbitmaptype[i] == 'N') {

							int size = Integer
									.parseInt(IsoUtil.binary2hex(IsoUtil.asciiChar2binary(message
											.substring(offset, offset
													+ (-1 * HPDHbitmap[i])))));
							offset += -1 * HPDHbitmap[i];

							if (size % 2 != 0) {
								StringBuilder data=new StringBuilder();
								data.append(IsoUtil.binary2hex(IsoUtil
										.asciiChar2binary(message.substring(
												offset, offset
														+ ((size + 1) / 2)))));

								if (HPDHbitmapPadAttribute[i] == 'F') {
									pthtHPDHBuffer.put("P-" + (i + 1), data
											.substring(data.length() - size,
													data.length()));
								} else {
									pthtHPDHBuffer.put("P-" + (i + 1),
											data.substring(0, size));
								}
								offset += ((size + 1) / 2);
							} else {
								StringBuilder data=new StringBuilder();
								data.append(IsoUtil.binary2hex(IsoUtil
										.asciiChar2binary(message.substring(
												offset, offset + (size / 2)))));

								pthtHPDHBuffer.put("P-" + (i + 1), data.toString());
								offset += size / 2;
							}
						} else if (HPDHbitmaptype[i] == 'A') {

							int size = Integer
									.parseInt(IsoUtil.binary2hex(IsoUtil.asciiChar2binary(message
											.substring(offset, offset
													+ (-1 * HPDHbitmap[i])))));

							offset += -1 * HPDHbitmap[i];
							pthtHPDHBuffer.put("P-" + (i + 1),
									message.substring(offset, offset + size));
							offset += size;
						}
					} else {

						if (HPDHbitmaptype[i] == 'N') {

							int size = HPDHbitmap[i];

							if (size % 2 != 0) {
								StringBuilder data=new StringBuilder();
								data.append(IsoUtil.binary2hex(IsoUtil
										.asciiChar2binary(message.substring(
												offset, offset
														+ ((size + 1) / 2)))));

								if (HPDHbitmapPadAttribute[i] == 'F') {
									pthtHPDHBuffer.put("P-" + (i + 1), data
											.substring(data.length() - size,
													data.length()));
								} else {
									pthtHPDHBuffer.put("P-" + (i + 1),
											data.substring(0, size));
								}

								offset += ((size + 1) / 2);
							} else {
								StringBuilder data=new StringBuilder();
								data.append(IsoUtil.binary2hex(IsoUtil
										.asciiChar2binary(message.substring(
												offset, offset + (size / 2)))));

								pthtHPDHBuffer.put("P-" + (i + 1), data.toString());
								offset += size / 2;
							}
						} else if (HPDHbitmaptype[i] == 'A') {
							pthtHPDHBuffer.put(
									"P-" + (i + 1),
									message.substring(offset, offset
											+ HPDHbitmap[i]));
							offset += HPDHbitmap[i];
						}
					}
				} else {
					pthtHPDHBuffer.put("P-" + (i + 1), "*");
				}

			}
			for (int i = 64; i < 128; i++) {
				pthtHPDHBuffer.put("S-" + (i + 1), "*");
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

	public String buildHpdhMessage(IsoBuffer isoBuffer) {
		String messageType = IsoUtil.buildMessageType(isoBuffer.get(MSG_TYP)
				.toString().trim());
		String message = buildHpdh(messageType, isoBuffer);
		StringBuilder sBuffer = new StringBuilder(IsoUtil.pad(
				String.valueOf(IsoUtil.toGraphical(message.length(), 2)), ' ',
				2, true));
		sBuffer.append(message);
		return sBuffer.toString();
	}

	public String buildHpdh(final String msgType, final IsoBuffer isoBuffer) {
		StringBuilder messageResult = new StringBuilder();
		isoBuffer.put(TPDU_ID, "6000080000");
		StringBuilder message = new StringBuilder(
				IsoUtil.binary2asciiChar(IsoUtil.hex2binary(isoBuffer
						.get(TPDU_ID).toString().trim()))
						+ IsoUtil.binary2asciiChar(IsoUtil.hex2binary(msgType)));
		StringBuilder primaryBitMap = new StringBuilder();
		for (int i = 0; i <= 63; i++) {

			if (!isoBuffer.get("P-" + (i + 1)).toString().equals("*")) {

				if (HPDHbitmap[i] < 0) {

					if (HPDHbitmaptype[i] == 'N') {

						int size = isoBuffer.get("P-" + (i + 1)).toString()
								.length();
						message.append(IsoUtil.binary2asciiChar(IsoUtil
								.hex2binary(IsoUtil.pad111(
										String.valueOf(size), -1
												* HPDHbitmap[i] * 2, 'F'))));

						if (size % 2 != 0) {
							message.append(IsoUtil.binary2asciiChar(IsoUtil
									.hex2binary(IsoUtil.pad111(
											isoBuffer.get("P-" + (i + 1))
													.toString(),
											isoBuffer.get("P-" + (i + 1))
													.toString().length() + 1,
											HPDHbitmapPadAttribute[i]))));
						} else {
							message.append(IsoUtil.binary2asciiChar(IsoUtil
									.hex2binary(isoBuffer.get("P-" + (i + 1))
											.toString())));
						}
						primaryBitMap.append("1");
					} else if (HPDHbitmaptype[i] == 'A') {
						int size = isoBuffer.get("P-" + (i + 1)).length();
						message.append(IsoUtil.binary2asciiChar(IsoUtil
								.hex2binary(IsoUtil.pad111(
										String.valueOf(size), -1
												* HPDHbitmap[i] * 2, 'F'))));
						message.append(isoBuffer.get("P-" + (i + 1)));

						primaryBitMap.append("1");
					} else {
						primaryBitMap.append("0");
					}
				} else {
					if (HPDHbitmaptype[i] == 'N') {

						if (isoBuffer.get("P-" + (i + 1)).toString().length() % 2 != 0) {
							message.append(IsoUtil.binary2asciiChar(IsoUtil
									.hex2binary(IsoUtil.pad111(
											isoBuffer.get("P-" + (i + 1))
													.toString(),
											isoBuffer.get("P-" + (i + 1))
													.toString().length() + 1,
											HPDHbitmapPadAttribute[i]))));
						} else {
							message.append(IsoUtil.binary2asciiChar(IsoUtil
									.hex2binary(isoBuffer.get("P-" + (i + 1))
											.toString())));
						}
						primaryBitMap.append("1");

					} else if (HPDHbitmaptype[i] == 'A') {
						message.append(isoBuffer.get("P-" + (i + 1)));
						primaryBitMap.append("1");
					} else {
						primaryBitMap.append("0");
					}
				}
			} else {
				primaryBitMap.append("0");
			}
		}
		messageResult.append(message.substring(0, 7));
		messageResult
				.append(IsoUtil.binary2asciiChar(primaryBitMap.toString()));
		messageResult.append(message.substring(7));
		return messageResult.toString();
		/*
		 * return message.substring(0, 7) +
		 * IsoUtil.binary2asciiChar(primaryBitMap.toString()) +
		 * message.substring(7);
		 */
	}

	@Override
	public TerminalOperator getTerminalOperatorData(IsoBuffer isoBuffer)
			throws PosException {
		try {
			TerminalOperator ud = new TerminalOperator();
			if (!isoBuffer.isFieldEmpty(Constants.DE47)) {
				try {
					JSONObject p47 = new JSONObject(
							isoBuffer.get(Constants.DE47));
					ud.setUserId((String) p47.opt(JSON_KEY_USER_ID));
					ud.setSessionId((String) p47.opt(JSON_KEY_SESSION_ID));
				} catch (JSONException e) {
					ud.setUserId(isoBuffer.get(Constants.DE47));
				}
			}
			if (Validator.isOperatorTxn(isoBuffer)
					&& !isoBuffer.isFieldEmpty(Constants.DE63)) {
				JSONObject p63 = new JSONObject(isoBuffer.get(Constants.DE63));
				if (null == ud.getUserId())
					ud.setUserId((String) p63.opt(JSON_KEY_USER_ID));
				ud.setCode((String) p63.opt(JSON_KEY_CODE));
				ud.setOldCode((String) p63.opt(JSON_KEY_OLD_CODE));
				ud.setNewCode((String) p63.opt(JSON_KEY_NEW_CODE));
				ud.setIndicator((String) p63.opt(JSON_KEY_INDICATOR));
			}
			if (ud.getUserId() != null)
				isoBuffer.put(Constants.DE47, ud.getUserId());
			return ud;
		} catch (JSONException e) {
			Log.error("HPDH getUserData ", e);
			throw new PosException(null);
		}
	}

	@Override
	public TerminalInfo getTerminalInfo(IsoBuffer isoBuffer)
			throws PosException {
		try {
			TerminalInfo ti = new TerminalInfo();
			if (!isoBuffer.isFieldEmpty(Constants.DE61)) {
				JSONObject jo = new JSONObject(isoBuffer.get(Constants.DE61));
				ti.setMsgVersion((String) jo.opt(JSON_KEY_MSG_VER));
				ti.setAppVersion((String) jo.opt(JSON_KEY_APP_VER));
				ti.setMacId((String) jo.opt(JSON_KEY_MAC_ID));
				ti.setMdlNo((String) jo.opt(JSON_KEY_MDL_NO));
			}
			return ti;
		} catch (JSONException e) {
			Log.error("HPDH getUserData ", e);
			throw new PosException(null);
		}
	}

	@Override
	public AdditionalDataInfo getAdditionalDataInfo(IsoBuffer isoBuffer)
			throws PosException {
		try {
			AdditionalDataInfo addInfo = new AdditionalDataInfo();
			if (!isoBuffer.isFieldEmpty(Constants.DE48)) {
				if (Validator.isJSONValid(isoBuffer.get(Constants.DE48))) {
				JSONObject jo = new JSONObject(isoBuffer.get(Constants.DE48));
				addInfo.setRrn((String) jo.opt(JSON_KEY_DE48_RRN));
				}
				else
				{	
					addInfo.setRrn(isoBuffer.get(Constants.DE48));
				}
			}
			return addInfo;
		} catch (JSONException e) {
			Log.error("HPDH getUserData ", e);
			throw new PosException(null);
		}
	}
	
	@Override
	public void downloadParameters(String mspAcr, IsoBuffer isoBuffer)
			throws PosException {
		String downloadData = extHpdhDownloadService.getData(mspAcr, isoBuffer,
				StoredProcedureInfo.INITIAL_DOWNLOAD);
		isoBuffer.put(Constants.DE39, Constants.SUCCESS);
		isoBuffer.put(Constants.DE60, downloadData);
	}

	@Override
	public void modifyBits4Response(IsoBuffer isoBuffer, Data data) {

		isoBuffer.disableField(Constants.DE1);
		isoBuffer.disableField(Constants.DE17);
		isoBuffer.disableField(Constants.DE15);
		isoBuffer.disableField(Constants.DE18);
		isoBuffer.disableField(Constants.DE49);
		isoBuffer.disableField(Constants.DE50);
		isoBuffer.disableField(Constants.DE2);
		isoBuffer.disableField(Constants.DE14);
		isoBuffer.disableField(Constants.DE22);
		isoBuffer.disableField(Constants.DE51);
		isoBuffer.disableField(Constants.DE7);
		isoBuffer.disableField(Constants.DE32);
		isoBuffer.disableField(Constants.DE42);
		isoBuffer.disableField(Constants.DE47);
		isoBuffer.disableField(Constants.DE43);
		isoBuffer.disableField(Constants.DE44);
		isoBuffer.disableField(Constants.DE48);
		isoBuffer.disableField(Constants.DE35);
		isoBuffer.disableField(Constants.DE52);
		isoBuffer.disableField(Constants.DE61);
		isoBuffer.disableField(Constants.DE60);
		isoBuffer.disableField(Constants.DE54);
	//	if (!Constants.SUCCESS.equals(isoBuffer.get(Constants.DE39))) {
	//		isoBuffer.disableField(Constants.DE55);
	//	} else {
			ClientData cd = (ClientData) data;
			if (cd != null) {
				if (cd.getEmvResponseMap() != null
						&& !cd.getEmvResponseMap().isEmpty()) {
					buildEmvData(cd.getEmvResponseMap(), isoBuffer);
				}
				if (!Util.isNullOrEmpty(cd.getUti()))
					isoBuffer.put(Constants.DE64, cd.getUti());
	//		}
		}
	}

	@Override
	public String getStoredProcedure() {
		return StoredProcedureInfo.TRANSACTION_VALIDATION_EXT_HPDH;
	}

	
	/* @Override 
	 public String getSettlementBatchProcedureName() { 
	 return StoredProcedureInfo.BATCH_UPLOAD;
	  }*/
	 

	@Override
	public boolean errorDescriptionRequired() {
		return true;
	}

	@Override
	public SettlementFormat getBatchTotalsFormat() {
		return SettlementFormat.TRANSACTION_FORMAT;
	}

	@Override
	public String getSettlementStoredProcedure() {
		return StoredProcedureInfo.SETTLEMENT;
	}
	
	@Override
	public String getSettlementStoredProcedureExt() {
		return StoredProcedureInfo.SETTLEMENT;
	}

	@Override
	public Boolean checkHostToHost() {
		// TODO Auto-generated method stub
		return false;
	}

	

}
