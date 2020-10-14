package com.fss.pos.host.iso8583.base24.ahi;

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
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.base.services.transactionlog.TransactionLogData;
import com.fss.pos.host.iso8583.base24.Base24Api;

@HostRequest(HostType = "11")
public class AhiRequestApi extends Base24Api {

	private static final String DE100_DEFAULT = "00";

	@Autowired
	private Config config;

	@ModifyTransaction(Transactions.BALANCE_INQUIRY)
	public void modifyBalanceInquiry(IsoBuffer isoBuffer, Data hostClientData) {
		isoBuffer.put(Constants.DE4, BALANCE_INQ_AMOUNT);
	}

	@ModifyTransaction(Transactions.TIP)
	public void modifyTip(IsoBuffer isoBuffer, Data hostClientData) {
		isoBuffer.disableField(Constants.DE64);
	}

	@ModifyTransaction(Transactions.VOID)
	public void modifyVoid(IsoBuffer isoBuffer, Data hostClientData) {
		isoBuffer.disableField(Constants.DE64);
	}

	@ModifyTransaction(Transactions.EMV_DEFAULT)
	public void modifyEmv(IsoBuffer isoBuffer, Data data) throws PosException, JSONException {

		HostData hostData = (HostData) data;
		Map<EmvTags, String> tvMapTemp = hostData.getEmvMap();

		if (tvMapTemp.isEmpty())
			return;

		Map<String, String> tvMap = new HashMap<String, String>();
		for (Entry<EmvTags, String> te : tvMapTemp.entrySet())
			tvMap.put(te.getKey().toString(), te.getValue());

		int tokenLenth = 1;
		int bufferLen = 0;
		String b2 = getB2Token(tvMap,data,isoBuffer.get(Constants.ISO_MSG_TYPE));
		//String b2 = getB2Token(tvMap, hostData.getMsgProtocol());
		String b3 = getB3Token(tvMap, isoBuffer.get(Constants.DE41));
		String c4 = getC4Token(tvMap, isoBuffer, data);
		String b4 = getB4Token(tvMap, isoBuffer, data);

		//bufferLen += b2.length();
		bufferLen += b3.length();
		bufferLen += c4.length();
		bufferLen += b4.length();
		tokenLenth += 4;

		StringBuilder emvData = new StringBuilder();
		emvData.append(AMPERSAND);
		emvData.append(SPACE);
		emvData.append(Util.appendChar(Integer.toString(tokenLenth), '0', 5,
				true));
		int finalLen = 12 + bufferLen;
		emvData.append(Util.appendChar(Integer.toString(finalLen), '0', 5, true));
	    emvData.append(b2);
		emvData.append(b3);
		emvData.append(b4);
		emvData.append(c4);

		isoBuffer.put(Constants.DE63, emvData.toString());
	}

	@ModifyTransaction(Transactions.DEFAULT)
	public void modify4All(IsoBuffer isoBuffer, Data hostClientData) {

		HostData hostData = (HostData) hostClientData;
		isoBuffer.put(Constants.DE7, hostData.getTxnDateTime());
		isoBuffer.putIfAbsent(Constants.DE12, hostData.getMerchantTime());
		isoBuffer.putIfAbsent(Constants.DE13, hostData.getMerchantDate());
		isoBuffer.put(Constants.DE17, hostData.getMerchantDate());
		isoBuffer.put(Constants.DE18,
				Util.appendChar(hostData.getMcc(), ' ', 4, true));
		isoBuffer.put(Constants.DE32,
				Util.appendChar(hostData.getAcqInstId(), ' ', 11, true));

		isoBuffer.put(Constants.DE41,
				Util.appendChar(hostData.getTerminalId(), ' ', 16, false));

		StringBuilder p43Data = new StringBuilder();
		p43Data.append(hostData.getBusinessName()).append(
				hostData.getMerchantAddress());		
		StringBuilder de43 = new StringBuilder();
		de43.append((p43Data.toString().length() <= 38) ? Util.appendChar(
				p43Data.toString(), ' ', 38, false) : p43Data.substring(0, 38));		
		de43.append(hostData.getCountryCode().substring(0, 2));		 
		isoBuffer.put(Constants.DE43, de43.toString());
		String currencyCode = Util.appendChar(hostData.getCurrencyCode(), '0',
				3, true);
		isoBuffer.put(Constants.DE48, P_48_HARDCODED);
		isoBuffer.put(Constants.DE49, currencyCode);
		isoBuffer.put(Constants.DE50, currencyCode);
		isoBuffer.put(Constants.DE51, currencyCode);
		isoBuffer.put(Constants.DE60, config.getHostFiid());

		isoBuffer.disableField(Constants.DE47);
		isoBuffer.disableField(Constants.DE61);
		isoBuffer.disableField(Constants.DE62);
		isoBuffer.disableField(Constants.DE55);
		isoBuffer.disableField(Constants.DE24);

	}

	@ModifyTransaction(Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer isoBuffer, Data data) {

		TransactionLogData trd = (TransactionLogData) data;

		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_REV_420);

		isoBuffer.put(Constants.DE39, Constants.SUCCESS);
		StringBuilder s90 = new StringBuilder(Constants.MSG_TYPE_RSP_TXN);
		s90.append(Util.appendChar(trd.getRrn(), '0', 12, true));
		s90.append(trd.getMerchantDate());
		s90.append(trd.getMerchantTime());
		s90.append(Constants.ZERO + Constants.ZERO);
		s90.append(trd.getMerchantDate());
		s90.append(S90_FILLER);
		isoBuffer.put(Constants.DE90, s90.toString());

		isoBuffer.putIfAbsent(Constants.DE12, trd.getCurrentMerchantTime());
		isoBuffer.putIfAbsent(Constants.DE13, trd.getCurrentMerchantDate());
		isoBuffer.put(Constants.DE17, trd.getCurrentMerchantDate());
		isoBuffer.put(Constants.DE18,
				Util.appendChar(trd.getMcc(), ' ', 4, true));
		isoBuffer.put(Constants.DE32,
				Util.appendChar(trd.getAcqInstId(), ' ', 11, true));

		isoBuffer.put(Constants.DE41,
				Util.appendChar(trd.getTerminalId(), ' ', 16, false));
		isoBuffer.put(Constants.DE42,
				Util.appendChar(trd.getMerchantId(), ' ', 15, false));
		StringBuilder p43Data = new StringBuilder();
		p43Data.append(trd.getBusinessName()).append(
				trd.getMerchantAddress());		
		StringBuilder de43 = new StringBuilder();
		de43.append((p43Data.toString().length() <= 38) ? Util.appendChar(
				p43Data.toString(), ' ', 38, false) : p43Data.substring(0, 38));		
		de43.append(trd.getCountryCode().substring(0, 2));		 
		isoBuffer.put(Constants.DE43, de43.toString());	
		String currencyCode = Util.appendChar(trd.getCurrencyCode(), '0',
				3, true);
		isoBuffer.put(Constants.DE48, P_48_HARDCODED);
		isoBuffer.put(Constants.DE49, currencyCode);
		isoBuffer.put(Constants.DE50, currencyCode);
		isoBuffer.put(Constants.DE51, currencyCode);
		isoBuffer.put(Constants.DE38, trd.getAuthCode());
		isoBuffer.put(Constants.DE60, config.getHostFiid());
		isoBuffer.put(Constants.DE100, DE100_DEFAULT);
		isoBuffer.put(Constants.DE61, "0000000000000000000");
		isoBuffer.put(Constants.DE121, "020987321            00");
		isoBuffer.put(Constants.DE124, "000015001");
		isoBuffer.put(Constants.DE123, "00000000000000000000");
		isoBuffer.put(Constants.DE125, "HOSTB24 10");
		isoBuffer.put(Constants.DE126, "000000000000000000012345678912");
		isoBuffer.put(Constants.DE62, "0100000000000");

		isoBuffer.disableField(Constants.DE52);
		isoBuffer.disableField(Constants.DE47);
	}

	@ModifyTransaction(Transactions.OFFLINE_DEFAULT)
	public void modifyOffline(IsoBuffer isoBuffer, Data data) {
		// bits hardcoded for offline for host
		isoBuffer.put(Constants.DE61, "0000000000000000000");
		isoBuffer.put(Constants.DE100, "00000000000");
		isoBuffer.put(Constants.DE121, "020987321            00");
		isoBuffer.put(Constants.DE124, "000015001");
		isoBuffer.put(Constants.DE125, "HOSTB24 10");
		isoBuffer.put(Constants.DE126, "000000000000000000012345678912");
	}

}
