package com.fss.pos.host.iso8583.absa;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fss.pos.base.api.host.HostData;
import com.fss.pos.base.api.host.HostRequest;
import com.fss.pos.base.api.transactions.ModifyTransaction;
import com.fss.pos.base.api.transactions.Transactions;
import com.fss.pos.base.commons.Config;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.EmvTags;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.base.factory.ApiFactory;
import com.fss.pos.base.services.transactionlog.TransactionLogData;
import com.fss.pos.host.iso8583.base24.Base24Api;

@HostRequest(HostType = "14")
public class AbsaRequestApi extends Base24Api {

	@Autowired
	private Config config;

	@Autowired
	private ApiFactory apiFactory;

	private static final String DE22_MAGSTRIPE_ID = "90";
	private static final String DE22_FALL_BACK = "80";

	@ModifyTransaction(Transactions.DEFAULT)
	public void modify4All(IsoBuffer isoBuffer, Data hostClientData) {

		HostData hostData = (HostData) hostClientData;
		isoBuffer.put(Constants.DE7, hostData.getTxnDateTime());
		isoBuffer.putIfAbsent(Constants.DE12, hostData.getMerchantTime());
		isoBuffer.putIfAbsent(Constants.DE13, hostData.getMerchantDate());
		isoBuffer.put(Constants.DE32, hostData.getAcqInstId());

		isoBuffer.put(Constants.DE37, hostData.getRrn());

		StringBuilder p43Data = new StringBuilder();
		p43Data.append(hostData.getBusinessName()).append(
				hostData.getMerchantAddress());
		
		StringBuilder de43 = new StringBuilder();
		de43.append((p43Data.toString().length() < 22) ? Util.appendChar(
				p43Data.toString(), ' ', 22, false) : p43Data.substring(0, 22));
		de43.append((hostData.getCity().length() > 13) ? hostData.getCity()
				.substring(0, 13) : Util.appendChar(hostData.getCity(), ' ',
				13, false));	
		de43.append(hostData.getState().substring(0, 2)).append(' ');
		de43.append(hostData.getIsoCountryCode());

		isoBuffer.put(Constants.DE43, de43.toString());

		String currencyCode = Util.appendChar(hostData.getCurrencyCode(), '0',
				3, true);
		isoBuffer.put(Constants.DE49, currencyCode);

		isoBuffer.put(Constants.DE18, hostData.getMcc());
		isoBuffer.put(Constants.DE17, hostData.getMerchantDate()); // Capture
																	// date
		isoBuffer.put(Constants.DE41,
				Util.appendChar(hostData.getTerminalId(), ' ', 16, false));

		HostData hd = (HostData) hostClientData;
		String p47 = isoBuffer.get(Constants.DE47);
		
		StringBuilder de48=new StringBuilder();
		de48.append(Util.appendChar(hd.getMerchantId(), ' ', 19, false));
		de48.append(hd.getMcc());
		de48.append(hd.getState());
		de48.append(hd.getIsoCountryCode());		
		isoBuffer.put(Constants.DE48,de48.toString());
		isoBuffer.put(Constants.DE60, config.getHostFiid() + "000000000000"); // terminal
																				// logical
																				// network,
																				// offset
																				// and
																				// pseudo
																				// id
		String p3 = isoBuffer.get(Constants.DE3);
		String p61 = "000000000" + p3.substring(2, 4);
		isoBuffer.put(Constants.DE61, Util.appendChar(p61, ' ', 19, false));
		/*
		isoBuffer.put(Constants.DE60, "NMADPRO1+00000  ");
		isoBuffer.put(Constants.DE61, "NMADDPRO1000       ");*/

		isoBuffer.put(Constants.DE121, Util.appendChar("020", '0', 23, false));
		//isoBuffer.put(Constants.DE121, "02000000001000000000000");
		isoBuffer.put(Constants.DE123, Util.appendChar("020", '0', 23, false));
		isoBuffer.put(Constants.DE124, Util.appendChar("009", '0', 12, false));
	//	isoBuffer.put(Constants.DE125, "012" + "000000000000");
		isoBuffer.put(Constants.DE125, "012000000010000");
		isoBuffer.put(Constants.DE126, "038210" + isoBuffer.get(Constants.DE3)
				+ "000000" + Util.appendChar(p47, '0', 20, false) + "000");
		
		//For Dukpt
		isoBuffer.disableField(Constants.DE44);
	}

	@ModifyTransaction(Transactions.PURCHASE)
	public void modifyPurchace(IsoBuffer buffer, Data data) {

		if (Constants.ENTRY_MODE_MAG_STRIPE.equals(buffer.get(Constants.DE22)
				.substring(0, 2))) {
			
			String[] arr = buffer.get(Constants.DE35).split("=");
			if(String.valueOf(arr[1].charAt(4)).equals("2"))
				buffer.put(Constants.DE22, DE22_FALL_BACK + buffer.get(Constants.DE22).substring(2, 3));
			else
				buffer.put(Constants.DE22, DE22_MAGSTRIPE_ID + buffer.get(Constants.DE22).substring(2, 3));
			
			buffer.put(Constants.DE63, get_token_63(buffer, data));
		}
		// /Disabling Fields
		buffer.disableField(Constants.DE47);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE55);
		
	}
	
	@ModifyTransaction(Transactions.BALANCE_INQUIRY)
	public void modifyBalanceEnquiry(IsoBuffer buffer, Data data) {
		
		if (Constants.ENTRY_MODE_MAG_STRIPE.equals(buffer.get(Constants.DE22)
				.substring(0, 2))) {
			
			String[] arr = buffer.get(Constants.DE35).split("=");
			if(String.valueOf(arr[1].charAt(4)).equals("2"))
				buffer.put(Constants.DE22, DE22_FALL_BACK + buffer.get(Constants.DE22).substring(2, 3));
			else
				buffer.put(Constants.DE22, DE22_MAGSTRIPE_ID + buffer.get(Constants.DE22).substring(2, 3));
			
			buffer.put(Constants.DE63, get_token_63(buffer, data));
		}
		
		buffer.disableField(Constants.DE55);
		buffer.disableField(Constants.DE25);
	}

	private String get_token_63(IsoBuffer isoBuffer, Data data) {
		
		if(isoBuffer.get(Constants.ISO_MSG_TYPE).equals(Constants.MSG_TYPE_REV_420))
		{
			TransactionLogData tranLogData = (TransactionLogData) data;

			Map<String, String> tvMap = new HashMap<String, String>();

			
			int tokenLenth = 1;
			int bufferLen = 0;

			//String b0 = "! B000078 000000000000000000000000000000000000000000000000000000000000010000000000000000";
			String ci = "! CI00070 0000000000000000251528070000000000000000000000000000000000000000000000";
			String qk = getQKToken(tranLogData.getUti());
			String c0 = "! C000026                   0    00 ";
			String c4 = getC4Token(tvMap, isoBuffer, data);
			String b4 = getB4Token(tvMap, isoBuffer, data);

		//	bufferLen += b0.length();//78
			bufferLen += ci.length();// 70
			bufferLen += qk.length();// 36
			bufferLen += c0.length();// 26
			bufferLen += c4.length();
			bufferLen += b4.length();
			
			tokenLenth += 5;

			StringBuilder tokenData = new StringBuilder();
			tokenData.append(AMPERSAND);
			tokenData.append(SPACE);
			tokenData.append(Util.appendChar(Integer.toString(tokenLenth), '0', 5,
					true));
			int finalLen = 12 + bufferLen;
			tokenData.append(Util.appendChar(Integer.toString(finalLen), '0', 5,
					true));
		//	tokenData.append(b0);
			tokenData.append(ci);
			tokenData.append(qk);
			tokenData.append(c0);
			tokenData.append(c4);
			tokenData.append(b4);

			return tokenData.toString();
		}
			
		else
		{
			HostData hostData = (HostData) data;

			Map<String, String> tvMap = new HashMap<String, String>();
			
			int tokenLenth = 1;
			int bufferLen = 0;

			//String b0 = "! B000078 000000000000000000000000000000000000000000000000000000000000010000000000000000";
			String ci = "! CI00070 0000000000000000251528070000000000000000000000000000000000000000000000";
			String qk =  getQKToken(hostData.getUti());
			String c0 = "! C000026                   0    00 "; //changed for absa production earlier all zeros
			String c4 = getC4Token(tvMap, isoBuffer, data);
			String b4 = getB4Token(tvMap, isoBuffer, data);

			//bufferLen += b0.length();
			bufferLen += ci.length();// 70
			bufferLen += qk.length();// 36
			bufferLen += c0.length();// 26
			bufferLen += c4.length();
			bufferLen += b4.length();
			tokenLenth += 5;

			StringBuilder tokenData = new StringBuilder();
			tokenData.append(AMPERSAND);
			tokenData.append(SPACE);
			tokenData.append(Util.appendChar(Integer.toString(tokenLenth), '0', 5,
					true));
			int finalLen = 12 + bufferLen;
			tokenData.append(Util.appendChar(Integer.toString(finalLen), '0', 5,
					true));
			//tokenData.append(b0);
			tokenData.append(ci);
			tokenData.append(qk);
			tokenData.append(c0);
			tokenData.append(c4);
			tokenData.append(b4);

			return tokenData.toString();
		}

	}

	@ModifyTransaction(Transactions.EMV_DEFAULT)
	public void modifyEmv(IsoBuffer isoBuffer, Data data) throws PosException, JSONException {
		
		if(isoBuffer.get(Constants.ISO_MSG_TYPE).equals(Constants.MSG_TYPE_REV_420))
		{
			TransactionLogData tranLogData = (TransactionLogData) data;
			Log.debug("EMV DATA", tranLogData.toString());
			Map<EmvTags, String> tvMapTemp = tranLogData.getEmvMap();

			if (tvMapTemp.isEmpty())
				return;

			Map<String, String> tvMap = new HashMap<String, String>();
			for (Entry<EmvTags, String> te : tvMapTemp.entrySet())
				tvMap.put(te.getKey().toString(), te.getValue());

			int tokenLenth = 1;
			int bufferLen = 0;
			//String b2 = getB2Token(tvMap);
		//	String b0 = "! B000078 000000000000000000000000000000000000000000000000000000000000010000000000000000";
			String b2 = getB2Token(tvMap, data, isoBuffer.get(Constants.ISO_MSG_TYPE));
			String b3 = getB3Token(tvMap, tranLogData.getMsgProtocol());
			String c4 = getC4Token(tvMap, isoBuffer, data);
			String b4 = getB4Token(tvMap, isoBuffer, data);
			String qk = getQKToken(tranLogData.getUti());
			String c0 = "! C000026                   0    00 ";

	//		bufferLen += b0.length();//78
			bufferLen += b2.length();// 158
			bufferLen += b3.length();// 80
			bufferLen += c4.length();// 20
			bufferLen += b4.length();// 12
			bufferLen += qk.length();// 36
			bufferLen += c0.length();// 26
			tokenLenth += 6;

			StringBuilder emvData = new StringBuilder();
			emvData.append(AMPERSAND);
			emvData.append(SPACE);
			emvData.append(Util.appendChar(Integer.toString(tokenLenth), '0', 5,
					true));
			int finalLen = 12 + bufferLen;
			emvData.append(Util.appendChar(Integer.toString(finalLen), '0', 5, true));
		//	emvData.append(b0);
			emvData.append(b2);
			emvData.append(b3);
			emvData.append(b4);
			emvData.append(c4);
			emvData.append(qk);
			emvData.append(c0);

			isoBuffer.put(Constants.DE63, emvData.toString());
		
		}

		else{
			HostData hostData = (HostData) data;
			Log.debug("EMV DATA", hostData.toString());
			Map<EmvTags, String> tvMapTemp = hostData.getEmvMap();

			if (tvMapTemp.isEmpty())
				return;

			Map<String, String> tvMap = new HashMap<String, String>();
			for (Entry<EmvTags, String> te : tvMapTemp.entrySet())
				tvMap.put(te.getKey().toString(), te.getValue());

			int tokenLenth = 1;
			int bufferLen = 0;
		//	String b2 = getB2Token(tvMap);
		//	String b0 = "! B000078 000000000000000000000000000000000000000000000000000000000000010000000000000000";
			String b2 = getB2Token(tvMap, data, isoBuffer.get(Constants.ISO_MSG_TYPE));
			String b3 = getB3Token(tvMap, hostData.getMsgProtocol());
			String c4 = getC4Token(tvMap, isoBuffer, data);
			String b4 = getB4Token(tvMap, isoBuffer, data);
			String qk = getQKToken(hostData.getUti());
			String c0 = "! C000026                   0    00 ";

			//bufferLen += b0.length();//78
			bufferLen += b2.length();// 158
			bufferLen += b3.length();// 80
			bufferLen += c4.length();// 20
			bufferLen += b4.length();// 12
			bufferLen += qk.length();// 36
			bufferLen += c0.length();//26
			tokenLenth += 6;

			StringBuilder emvData = new StringBuilder();
			emvData.append(AMPERSAND);
			emvData.append(SPACE);
			emvData.append(Util.appendChar(Integer.toString(tokenLenth), '0', 5,
					true));
			int finalLen = 12 + bufferLen;
			emvData.append(Util.appendChar(Integer.toString(finalLen), '0', 5, true));
		//	emvData.append(b0);
			emvData.append(b2);
			emvData.append(b3);
			emvData.append(b4);
			emvData.append(c4);
			emvData.append(qk);
			emvData.append(c0);

			isoBuffer.put(Constants.DE63, emvData.toString());
		}
	}

	@ModifyTransaction(value = Transactions.CASH_BACK)
	public void modifyCashback(IsoBuffer buffer, Data data) {

		// disabling fields
		buffer.disableField(Constants.DE47);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE55);

	}

	@ModifyTransaction(value = Transactions.TIP)
	public void modifyTipAdjustment(IsoBuffer buffer, Data data) {

		// disabling fields
		buffer.disableField(Constants.DE47);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE55);

	}

	@ModifyTransaction(Transactions.REFUND)
	public void modifyRefund(IsoBuffer isoBuffer, Data hostClientData) {

		// disabling fields
		isoBuffer.disableField(Constants.DE47);
		isoBuffer.disableField(Constants.DE62);
		isoBuffer.disableField(Constants.DE55);

	}

	@ModifyTransaction(Transactions.KEY_IMPORT)
	public void buildKeyImportRequest(IsoBuffer isoBuffer, Data data)
			throws JSONException {
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_NETWORK_MANAGEMENT);
		
		if(!(isoBuffer.get(Constants.DE39).equals(Constants.SUCCESS)))
			isoBuffer.put(Constants.DE123, "CSM(MCL/ESM RCV/ABSA ORG/NMAD ERF/C )");
		else
			isoBuffer.put(Constants.DE123, "CSM(MCL/RSM RCV/ABSA ORG/NMAD )");
	}

	@ModifyTransaction(Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer isoBuffer, Data data) throws PosException, JSONException {
		
		Log.debug("Inside Reversal", isoBuffer.toString());
		TransactionLogData tranLogData = (TransactionLogData) data;
		Log.trace(" " + tranLogData);
		
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_REV_420);
		isoBuffer.put(Constants.DE39, Constants.REVERSAL_DE_39_VALUE);
		StringBuilder s90 = new StringBuilder(Constants.MSG_TYPE_TXN);
		s90.append(Util.appendChar(tranLogData.getRrn(), '0', 12, true));
		s90.append(tranLogData.getMerchantDate());
		s90.append(tranLogData.getMerchantTime()+"00");
		s90.append(tranLogData.getMerchantDate());
		isoBuffer.put(Constants.DE90, Util.appendChar(s90.toString(), '0', 42, false));
		if (Constants.ENTRY_MODE_MAG_STRIPE.equals(isoBuffer.get(Constants.DE22)
				.substring(0, 2))) {
			String[] arr = isoBuffer.get(Constants.DE35).split("=");
			if(String.valueOf(arr[1].charAt(4)).equals("2"))
				isoBuffer.put(Constants.DE22, DE22_FALL_BACK + isoBuffer.get(Constants.DE22).substring(2, 3));
			else
				isoBuffer.put(Constants.DE22, DE22_MAGSTRIPE_ID + isoBuffer.get(Constants.DE22).substring(2, 3));
			isoBuffer.put(Constants.DE63, get_token_63(isoBuffer, data));
		}

		isoBuffer.put(Constants.DE7, tranLogData.getTransmissionDt());
		isoBuffer.putIfAbsent(Constants.DE12, tranLogData.getMerchantTime());
		
		isoBuffer.putIfAbsent(Constants.DE13, tranLogData.getMerchantDate());
		isoBuffer.put(Constants.DE32, tranLogData.getAcqInstId());

		isoBuffer.put(Constants.DE37, Util.appendChar(tranLogData.getRrn(), '0', 12, true));

		isoBuffer.put(Constants.DE43, tranLogData.getReqDe43());

		String currencyCode = Util.appendChar(tranLogData.getCurrencyCode(), '0',
				3, true);
		isoBuffer.put(Constants.DE49, currencyCode);

		isoBuffer.put(Constants.DE18, tranLogData.getMcc());
		isoBuffer.put(Constants.DE17, tranLogData.getMerchantDate()); // Capture
																	// date
		isoBuffer.put(Constants.DE41,
				Util.appendChar(tranLogData.getTerminalId(), ' ', 16, false));

		isoBuffer.put(Constants.DE48, tranLogData.getRespDe48());

		isoBuffer.put(Constants.DE60, config.getHostFiid() + "000000000000"); // terminal
																				// logical
																				// network,
																				// offset
																				// and
																				// pseudo
																				// id
		String p3 = isoBuffer.get(Constants.DE3);
		String p61 = "000000000" + p3.substring(2, 4);
		isoBuffer.put(Constants.DE61, Util.appendChar(p61, ' ', 19, false));
		isoBuffer.put(Constants.DE121, Util.appendChar("020", '0', 23, false));
		isoBuffer.put(Constants.DE123, Util.appendChar("020", '0', 23, false));
		isoBuffer.put(Constants.DE124, Util.appendChar("009", '0', 12, false));
		isoBuffer.put(Constants.DE125, "012" + "000000000000");
		isoBuffer.put(Constants.DE126, tranLogData.getRespDe126());
		isoBuffer.put(Constants.DE15, tranLogData.getRespDe15());
		isoBuffer.put(Constants.DE38, tranLogData.getAuthCode());
		isoBuffer.put(Constants.DE100, tranLogData.getRespDe100());
		
		if(Constants.ENTRY_MODE_ICC_CB.equals(isoBuffer.get(Constants.DE22).substring(0, 2)))
			modifyEmv(isoBuffer,data);
		
		isoBuffer.disableField(Constants.DE47);
		isoBuffer.disableField(Constants.DE62);
		isoBuffer.disableField(Constants.DE55);
		
		
		Log.debug("Final Reversal Request", isoBuffer.toString());
		//	isoBuffer.put(Constants.DE100, "");
	}

}
