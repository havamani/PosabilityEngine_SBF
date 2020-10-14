package com.fss.pos.host.iso8583.postilion.nedbank;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fss.commons.utils.RRN;
import com.fss.pos.base.api.host.HostData;
import com.fss.pos.base.api.host.HostRequest;
import com.fss.pos.base.api.transactions.ModifyTransaction;
import com.fss.pos.base.api.transactions.Transactions;
import com.fss.pos.base.commons.Config;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.EmvTags;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.base.services.transactionlog.TransactionLogData;
import com.fss.pos.host.KeyImportRequest;
import com.fss.pos.host.iso8583.postilion.PostilionApi;

@HostRequest(HostType = "13")
public class NedbankRequestApi extends PostilionApi {

	private static final String DE100_DEFAULT = "21";
	private static final String DE26_DEFAULT = "12";
	private static final String DE22_MAGSTRIPE_ID = "90";
	private static final String GUID_ID = "TenderDetailGUID";

	private static final Map<EmvTags, String> EMV_TAG_MAP;
	private static final Map<String, String> FIELD_127_22;

	static {
		HashMap<EmvTags, String> tempMap = new HashMap<EmvTags, String>();
		tempMap.put(EmvTags.TAG_9F02, Constants.DE2);
		tempMap.put(EmvTags.TAG_9F03, Constants.DE3);
		tempMap.put(EmvTags.TAG_9F06, Constants.DE4);
		tempMap.put(EmvTags.TAG_82, Constants.DE5);
		tempMap.put(EmvTags.TAG_9F36, Constants.DE6);
		tempMap.put(EmvTags.TAG_9F07, Constants.DE7);
		tempMap.put(EmvTags.TAG_9F26, Constants.DE12);
		tempMap.put(EmvTags.TAG_9F27, Constants.DE13);
		tempMap.put(EmvTags.TAG_8E, Constants.DE14);
		tempMap.put(EmvTags.TAG_9F34, Constants.DE15);
		tempMap.put(EmvTags.TAG_9F1E, Constants.DE16);
		tempMap.put(EmvTags.TAG_9F10, Constants.DE18);
		tempMap.put(EmvTags.TAG_9F09, Constants.DE20);
		tempMap.put(EmvTags.TAG_9F33, Constants.DE21);
		tempMap.put(EmvTags.TAG_9F1A, Constants.DE22);
		tempMap.put(EmvTags.TAG_9F35, Constants.DE23);
		tempMap.put(EmvTags.TAG_95, Constants.DE24);
		tempMap.put(EmvTags.TAG_5F2A, Constants.DE26);
		tempMap.put(EmvTags.TAG_9A, Constants.DE27);
		tempMap.put(EmvTags.TAG_9C, Constants.DE29);
		tempMap.put(EmvTags.TAG_9F37, Constants.DE30);
		EMV_TAG_MAP = Collections.unmodifiableMap(tempMap);

		FIELD_127_22 = new LinkedHashMap<String, String>();
		FIELD_127_22.put("Postilion:MetaData", "216TenderDetailGUID111");

	}

	@Autowired
	private Config config;

	@ModifyTransaction(Transactions.DEFAULT)
	public void modify4All(IsoBuffer isoBuffer, Data data) {

		HostData hostData = (HostData) data;

		isoBuffer.put(Constants.DE7,
				Util.getDateTimeInGMT(FORMAT_TXN_DATE_TIME));
		isoBuffer.putIfAbsent(Constants.DE12, hostData.getMerchantTime());
		isoBuffer.putIfAbsent(Constants.DE13, hostData.getMerchantDate());
		isoBuffer.put(Constants.DE15, hostData.getMerchantDate());
		isoBuffer.put(Constants.DE18,
				Util.appendChar(hostData.getMcc(), '0', 4, true));
		isoBuffer.put(Constants.DE32, hostData.getAcqInstId());

		StringBuilder p43Data = new StringBuilder();
		p43Data.append(hostData.getBusinessName()).append(
				hostData.getMerchantAddress());
		StringBuilder de43 = new StringBuilder();
		
		
		de43.append((p43Data.toString().length() < 23)? Util.appendChar(
				p43Data.toString(), ' ', 23, false) : p43Data.substring(0, 23)); 
		de43.append((hostData.getCity().length() > 13) ? hostData.getCity()
				.substring(0, 13) : Util.appendChar(hostData.getCity(), ' ',
				13, false));
		 
		de43.append(hostData.getState().substring(0, 2)).append(' ');
		de43.append(hostData.getIsoCountryCode());

		isoBuffer.put(Constants.DE43, de43.toString());

		String currencyCode = Util.appendChar(hostData.getCurrencyCode(), '0',
				3, true);
		isoBuffer.put(Constants.DE49, currencyCode);
		isoBuffer.put(Constants.DE123, getField123(isoBuffer));

		if (Constants.ENTRY_MODE_MANUAL.equals(isoBuffer.get(Constants.DE22)
				.substring(0, 2))) {
			isoBuffer.disableField(Constants.DE35);
		} else if (Constants.ENTRY_MODE_MAG_STRIPE.equals(isoBuffer.get(
				Constants.DE22).substring(0, 2))) {
			isoBuffer.put(Constants.DE22,
					DE22_MAGSTRIPE_ID
							+ isoBuffer.get(Constants.DE22).substring(2, 3));
			isoBuffer.disableField(Constants.DE2);
			isoBuffer.disableField(Constants.DE14);
		}

		if (!isoBuffer.isFieldEmpty(Constants.DE52))
			isoBuffer.put(Constants.DE52,
					IsoUtil.hex2AsciiChar(isoBuffer.get(Constants.DE52)));

		if (hostData.getUti() != null)
			FIELD_127_22.put(GUID_ID, hostData.getUti());

		IsoBuffer isoBuffer127 = new IsoBuffer();
		isoBuffer127.fillBuffer(true, false, false);
		isoBuffer127.put(Constants.DE22, buildField127_22(FIELD_127_22));
		isoBuffer.putBuffer(Constants.DE127, isoBuffer127);

		isoBuffer.put(Constants.DE26, DE26_DEFAULT);
		isoBuffer.put(Constants.DE100, DE100_DEFAULT);

		isoBuffer.disableField(Constants.DE47);
		isoBuffer.disableField(Constants.DE61);
		isoBuffer.disableField(Constants.DE62);
		isoBuffer.disableField(Constants.DE55);
		isoBuffer.disableField(Constants.DE60);
		isoBuffer.disableField(Constants.DE63);

	}

	@ModifyTransaction(value = Transactions.PURCHASE)
	public void modifyPurchace(IsoBuffer buffer, Data data) {

		HostData hostData = (HostData) data;

		if (!buffer.isFieldEmpty(Constants.DE54)) {
			StringBuilder de54 = new StringBuilder(buffer.get(Constants.DE3)
					.substring(2, 4)); // account type
			de54.append("40"); // amount type
			de54.append(hostData.getCurrencyCode());
			de54.append("D");
			de54.append(buffer.get(Constants.DE54));
			buffer.put(Constants.DE54, de54.toString());
		}
	}

	@ModifyTransaction(Transactions.MOTO)
	public void modifyMoto(IsoBuffer isoBuffer, Data data) {
		StringBuilder field123 = new StringBuilder(
				isoBuffer.get(Constants.DE123));
		field123.setCharAt(4, '2');
		isoBuffer.put(Constants.DE123, field123.toString());
	}

	@ModifyTransaction(value = Transactions.CASH_BACK)
	public void modifyCashback(IsoBuffer buffer, Data data) {
		HostData hd = (HostData) data;
		buffer.put(Constants.DE3,
				"09" + buffer.get(Constants.DE3).substring(2, 6));
		StringBuilder de54 = new StringBuilder(buffer.get(Constants.DE3)
				.substring(2, 4)); // account type
		de54.append("40"); // amount type
		de54.append(hd.getCurrencyCode());
		de54.append("C");
		de54.append(buffer.get(Constants.DE54));
		buffer.put(Constants.DE54, de54.toString());
	}

	@ModifyTransaction(value = Transactions.TIP)
	public void modifyTip(IsoBuffer buffer, Data data) {
		HostData hd = (HostData) data;
		buffer.put(Constants.DE3,
				"02" + buffer.get(Constants.DE3).substring(2, 6));
		StringBuilder de54 = new StringBuilder(buffer.get(Constants.DE3)
				.substring(2, 4)); // account type
		de54.append("40"); // amount type
		de54.append(hd.getCurrencyCode());
		de54.append("D");
		de54.append(buffer.get(Constants.DE54));
		buffer.put(Constants.DE54, de54.toString());
	}

	@ModifyTransaction(Transactions.OFFLINE_DEFAULT)
	public void modifyOffline(IsoBuffer isoBuffer, Data data) {
		isoBuffer.disableField(Constants.DE26);
		isoBuffer.disableField(Constants.DE27);
		isoBuffer.disableField(Constants.DE52);
		isoBuffer.disableField(Constants.DE53);
	}

	@ModifyTransaction(Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer isoBuffer, Data data) {

		TransactionLogData trd = (TransactionLogData) data;
		isoBuffer.put(Constants.DE39, Constants.SUCCESS);

		isoBuffer.putIfAbsent(Constants.DE12, trd.getMerchantTime());
		isoBuffer.putIfAbsent(Constants.DE13, trd.getMerchantDate());
		isoBuffer.put(Constants.DE15, trd.getMerchantDate());
		isoBuffer.put(Constants.DE18,
				Util.appendChar(trd.getMcc(), '0', 4, true));
		isoBuffer.put(Constants.DE32, trd.getAcqInstId());

		
		StringBuilder p43Data=new StringBuilder(); 
		p43Data.append(trd.getBusinessName());
		p43Data.append(trd.getMerchantAddress());		
		StringBuilder de43=new StringBuilder();		
		de43.append((p43Data.toString().length()<=38)? Util.appendChar(
				p43Data.toString(), ' ', 38, false) :p43Data.substring(0, 38)); 		
		de43.append(trd.getCountryCode().substring(0, 2));
		isoBuffer.put(Constants.DE43, de43.toString());
		String currencyCode = Util.appendChar(trd.getCurrencyCode(), '0', 3,
				true);
		isoBuffer.put(Constants.DE49, currencyCode);

		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_REV_420);

		StringBuilder s90 = new StringBuilder(Constants.MSG_TYPE_TXN);// Added
																		// by
																		// Rashmi
		s90.append(isoBuffer.get(Constants.DE11));
		s90.append(trd.getTransmissionDt());
		s90.append(Util.appendChar(trd.getAcqInstId(), '0', 11, true));
		s90.append(Util.appendChar(config.getHostFiid(), '0', 11, true));
		isoBuffer.put(Constants.DE90, s90.toString());
		isoBuffer.put(Constants.DE123, getField123(isoBuffer));

		isoBuffer.put(Constants.DE95,
				"000000000000000000000000C00000000C00000000"); // Added by
																// Rashmi
		isoBuffer.put(Constants.DE123, getField123(isoBuffer));

		IsoBuffer isoBuffer127 = new IsoBuffer();
		isoBuffer127.fillBuffer(true, false, false);
		isoBuffer127.put(Constants.DE22, buildField127_22(FIELD_127_22));
		isoBuffer.putBuffer(Constants.DE127, isoBuffer127);
		modifyEmv(isoBuffer, trd.getEmvMap());

		isoBuffer.disableField(Constants.DE52);
		isoBuffer.disableField(Constants.DE35);
		isoBuffer.disableField(Constants.DE47);
		isoBuffer.disableField(Constants.DE61);
		isoBuffer.disableField(Constants.DE62);
		isoBuffer.disableField(Constants.DE63);
		isoBuffer.disableField(Constants.DE55);
		isoBuffer.disableField(Constants.DE60);
	}

	@ModifyTransaction(Transactions.EMV_DEFAULT)
	public void modifyEmv(IsoBuffer isoBuffer, Data data) {
		modifyEmv(isoBuffer, ((HostData) data).getEmvMap());
	}

	private void modifyEmv(IsoBuffer isoBuffer, Map<EmvTags, String> tvMap) {
		Log.info("EMV Tags and values ", tvMap.toString());
		if (!tvMap.isEmpty()) {
			IsoBuffer isoBuffer127_25 = new IsoBuffer();
			isoBuffer127_25.fillBuffer(true, false, false);
			IsoBuffer isoBuffer127 = isoBuffer.getBuffer(Constants.DE127);

			for (EmvTags t : EMV_TAG_MAP.keySet()) {
				String val = tvMap.get(t);
				if (val == null)
					continue;
				if (EmvTags.TAG_9F1E.equals(t)) {
					isoBuffer127_25.put(EMV_TAG_MAP.get(t),
							IsoUtil.hex2AsciiChar(val));
					continue;
				}
				isoBuffer127_25.put(EMV_TAG_MAP.get(t), val);
			}
			isoBuffer127_25.put(Constants.DE14, "00000000000000001E0302031F00");
			isoBuffer127_25.put(Constants.DE17, "0D84000A800");
			isoBuffer127_25.putIfAbsent(Constants.DE3, "000000000000");
			isoBuffer127.putBuffer(Constants.DE25, isoBuffer127_25);

			if (tvMap.containsKey(EmvTags.TAG_5F34))
				isoBuffer.put(Constants.DE23, Util.appendChar(
						tvMap.get(EmvTags.TAG_5F34), '0', 3, true));
		}
	}

	private String getField123(IsoBuffer isoBuffer) {
		StringBuilder sb = new StringBuilder();
		String entryMode = isoBuffer.get(Constants.DE22);
		char entryModePosition2 = entryMode.charAt(1);
		char entryModePosition3 = entryMode.charAt(2);

		// if ((entryMode.substring(0, 2)).equals("07"))
		// sb.append("A");
		// else if ((entryMode.substring(0, 2)).equals("91"))
		// sb.append("B");
		// else
		// sb.append("5");
		sb.append("B");
		sb.append(entryModePosition3);
		sb.append("0"); // Card capture capability
		sb.append("1");// Operating environment
		sb.append("0"); // Card holder is present - moto will be changed
		// Card is present
		sb.append(Constants.ENTRY_MODE_MANUAL.equals(entryMode.substring(0, 2)) ? "0"
				: "1");
		// Card data input mode
		sb.append(entryModePosition2);
		// Card holder authentication method
		sb.append(isoBuffer.isFieldEmpty(Constants.DE52) ? "0" : "1");
		sb.append("4"); // Card holder authentication entity
		sb.append("0"); // Card data output capability
		sb.append("4"); // Terminal output capability
		sb.append("C"); // PIN capture capability
		sb.append("1"); // Terminal operator
		sb.append("01"); // Terminal type
		return sb.toString();
	}

	@ModifyTransaction(Transactions.KEY_IMPORT)
	public void buildKeyImportRequest(IsoBuffer isoBuffer, Data data)
			throws JSONException {
		KeyImportRequest keyImportRequest = (KeyImportRequest) data;
		isoBuffer.put(Constants.ISO_MSG_TYPE,
				Constants.MSG_TYPE_NETWORK_MANAGEMENT);
		isoBuffer.put(Constants.DE7,
				Util.getDateTimeInGMT(FORMAT_TXN_DATE_TIME));
		isoBuffer.put(Constants.DE11, RRN.genRRN(6));
		isoBuffer.put(Constants.DE12, keyImportRequest.getTime());
		isoBuffer.put(Constants.DE13, keyImportRequest.getDate());
		isoBuffer.put(Constants.DE70, KEY_IMPORT_DE70);
	}

}
