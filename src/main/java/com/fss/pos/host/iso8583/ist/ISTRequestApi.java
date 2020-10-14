package com.fss.pos.host.iso8583.ist;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.fss.pos.base.api.host.HostData;
import com.fss.pos.base.api.host.HostRequest;
import com.fss.pos.base.api.transactions.ModifyTransaction;
import com.fss.pos.base.api.transactions.Transactions;
import com.fss.pos.base.commons.Config;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.EmvTags;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.base.services.transactionlog.TransactionLogData;

@HostRequest(HostType = "23")
public class ISTRequestApi extends ISTApi {
	protected static final String P_48_HARDCODED = "000000000000000000000000000";
	private static final String DE23_DEFAULT = "000";
	private static final List<EmvTags> SUPPORTED_TAGS = new ArrayList();

	@Autowired
	private Config config;

	static {
		SUPPORTED_TAGS.add(EmvTags.TAG_9F26);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F27);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F37);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F36);
		SUPPORTED_TAGS.add(EmvTags.TAG_95);
		SUPPORTED_TAGS.add(EmvTags.TAG_9A);
		SUPPORTED_TAGS.add(EmvTags.TAG_9C);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F02);
		SUPPORTED_TAGS.add(EmvTags.TAG_5F2A);
		SUPPORTED_TAGS.add(EmvTags.TAG_82);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F1A);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F03);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F33);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F35);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F1E);
		SUPPORTED_TAGS.add(EmvTags.TAG_4F);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F09);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F41);
		SUPPORTED_TAGS.add(EmvTags.TAG_91);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F10);
	}

	@ModifyTransaction(Transactions.DEFAULT)
	public void modify4All(IsoBuffer isoBuffer, Data hostClientData) {
		HostData hostData = (HostData) hostClientData;
		isoBuffer.putIfAbsent(Constants.DE12, hostData.getMerchantTime());
		isoBuffer.putIfAbsent(Constants.DE13, hostData.getMerchantDate());
		isoBuffer.put(Constants.DE18,
				Util.appendChar(hostData.getMcc(), ' ', 4, true));
		isoBuffer.put(Constants.DE32, hostData.getAcqInstId());
		isoBuffer.put(Constants.DE41, hostData.getTerminalId());
		isoBuffer.put(Constants.DE35,
				isoBuffer.get(Constants.DE35).replace("=", "D"));

		isoBuffer.put(Constants.DE42,
				Util.appendChar(hostData.getMerchantId(), ' ', 15, false));

		StringBuilder p43Data = new StringBuilder();
		p43Data.append(hostData.getBusinessName()).append(
				hostData.getMerchantAddress());		
		StringBuilder de43=new StringBuilder();		
		de43.append((p43Data.toString().length() <= 38) ? Util.appendChar(
				p43Data.toString(), ' ', 38, false) : p43Data.substring(0, 38)); 				
		de43.append(hostData.getCountryCode().substring(0, 2));		
		isoBuffer.put(Constants.DE43, de43.toString());				
		if (hostData.getCurrencyCode().length() <= 2) {
			isoBuffer.put(Constants.DE49,
					Util.appendChar(hostData.getCurrencyCode(), '0', 3, true));
		} else {
			isoBuffer.put(Constants.DE49, hostData.getCurrencyCode());
		}
		isoBuffer.put(Constants.DE48, "000000000000000000000000000");

		if ("02".equals(isoBuffer.get(Constants.DE22))) {
			isoBuffer.disableField(Constants.DE2);
			isoBuffer.disableField(Constants.DE14);
		}

		isoBuffer.disableField(Constants.DE44);
		isoBuffer.disableField(Constants.DE47);
		isoBuffer.disableField(Constants.DE61);
		isoBuffer.disableField(Constants.DE62);
		isoBuffer.disableField(Constants.DE63);
		isoBuffer.disableField(Constants.DE24);
	}

	@ModifyTransaction(Transactions.EMV_DEFAULT)
	public void modifyEmv(IsoBuffer buffer, Data data)
			throws UnsupportedEncodingException {
		HostData hd = (HostData) data;
		Map<EmvTags, String> tvMap = hd.getEmvMap();

		if (Util.isNullOrEmpty(buffer.get(Constants.DE55))|| buffer.get(Constants.DE55).equals("*")) {
			buffer.disableField(Constants.DE55);
		} else {
			String de23 = (tvMap.containsKey(EmvTags.TAG_5F34) ? Util
					.appendChar((String) tvMap.get(EmvTags.TAG_5F34), '0', 3,
							true) : "000");
			buffer.put(Constants.DE23, de23);

			StringBuilder d = new StringBuilder();
			for (EmvTags tag : tvMap.keySet()) {
				if (SUPPORTED_TAGS.contains(tag)) {
					d.append(tag.toString());
					String val = (String) tvMap.get(tag);
					d.append(Util.appendChar(String.valueOf(val.length()), '0',
							2, true));
					d.append(val);
				}
			}

			buffer.put(Constants.DE55, d.toString());
		}

	}

	@ModifyTransaction(Transactions.TIP)
	public void modifyTipAdjustment(IsoBuffer buffer, Data data) {
		buffer.put(Constants.ISO_MSG_TYPE, "0220");
		buffer.put(Constants.DE3,
				"02" + buffer.get(Constants.DE3).substring(2, 6));
		HostData hd = (HostData) data;
		StringBuilder s90 = new StringBuilder(hd.getOrgMessageType());
		s90.append(hd.getOrgStan());
		s90.append(hd.getOrgTransmissionDt());
		s90.append(Util.appendChar(hd.getOrgAcquirerId(), '0', 11, true));
		s90.append(00000000000);
		buffer.put(Constants.DE90, s90.toString());
		buffer.put(Constants.DE39, "00");
		buffer.put(Constants.DE38, hd.getOrgAuthCode());

		if (hd.getCurrencyCode().length() <= 2) {
			buffer.put(Constants.DE51,
					Util.appendChar(hd.getCurrencyCode(), '0', 3, true));
		} else {
			buffer.put(Constants.DE51, hd.getCurrencyCode());
		}
		/*
		 * String currencyCode = Util.appendChar(hd.getCurrencyCode(), '0', 3,
		 * true); buffer.put(Constants.DE51, currencyCode);
		 */

		buffer.disableField(Constants.DE44);
		buffer.disableField(Constants.DE48);
	}

	@ModifyTransaction(Transactions.VOID)
	public void modifyVoid(IsoBuffer buffer, Data data) {

		HostData hd = (HostData) data;

		buffer.put(Constants.ISO_MSG_TYPE, "0200");

		buffer.put(Constants.DE3, "220000");

		buffer.put(Constants.DE39, "00");
		if ((hd.getOrgAuthCode() != null)) {
			buffer.put(Constants.DE38, hd.getOrgAuthCode());
		}
		StringBuilder s90 = new StringBuilder(hd.getOrgMessageType());
		s90.append(hd.getOrgStan());
		s90.append(hd.getOrgTransmissionDt());
		s90.append(Util.appendChar(hd.getAcqInstId(), ' ', 11, true));
		s90.append("00000000000");
		buffer.put(Constants.DE90, s90.toString());

		buffer.putIfAbsent(Constants.DE12, hd.getMerchantTime());
		buffer.putIfAbsent(Constants.DE13, hd.getMerchantDate());
		buffer.put(Constants.DE18, Util.appendChar(hd.getMcc(), ' ', 4, true));

		StringBuilder p43Data = new StringBuilder();
		p43Data.append(hd.getBusinessName()).append(
				hd.getMerchantAddress());
		
		StringBuilder de43 = new StringBuilder();
		de43.append((p43Data.toString().length() <= 38) ? Util.appendChar(
				p43Data.toString(), ' ', 38, false) : p43Data.substring(0, 38));
		de43.append(hd.getCountryCode().substring(0, 2)); 		
		buffer.put(Constants.DE43, de43.toString());
		String currencyCode = Util.appendChar(hd.getCurrencyCode(), '0', 3,
				true);
		buffer.put(Constants.DE49, currencyCode);
		buffer.put(Constants.DE50, currencyCode);
		buffer.put(Constants.DE51, currencyCode);

		// buffer.disableField(Constants.DE38);
		buffer.disableField(Constants.DE39);
		buffer.disableField(Constants.DE44);
		buffer.disableField(Constants.DE50);
		buffer.disableField(Constants.DE51);
		//buffer.disableField(Constants.DE90);
		buffer.disableField(Constants.DE52);
		buffer.disableField(Constants.DE55);
		buffer.disableField(Constants.DE63);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE47);
	}

	@ModifyTransaction(Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer isoBuffer, Data data) {
		TransactionLogData trd = (TransactionLogData) data;

		isoBuffer.put(Constants.ISO_MSG_TYPE, "0200");
		isoBuffer.put(Constants.DE3, "220000");
		isoBuffer.put(Constants.DE35,
				isoBuffer.get(Constants.DE35).replace("=", "D"));
		isoBuffer.put(Constants.DE39, "00");
		if ((trd.getAuthCode() != null) && (!"0".equals(trd.getAuthCode()))) {
			isoBuffer.put(Constants.DE38, trd.getAuthCode());
		}
		StringBuilder s90 = new StringBuilder(trd.getOriginalMsgType());
		s90.append(trd.getOrgStan());
		s90.append(trd.getReqDe12());
		s90.append(Util.appendChar(trd.getAcqInstId(), ' ', 11, true));
		s90.append("00000000000");
		isoBuffer.put(Constants.DE90, s90.toString());

		isoBuffer.putIfAbsent(Constants.DE12, trd.getCurrentMerchantTime());
		isoBuffer.putIfAbsent(Constants.DE13, trd.getCurrentMerchantDate());
		isoBuffer.put(Constants.DE18,
				Util.appendChar(trd.getMcc(), ' ', 4, true));
		isoBuffer.put(Constants.DE32, trd.getAcqInstId());

		isoBuffer.put(Constants.DE41, trd.getTerminalId());
		isoBuffer.put(Constants.DE42,
				Util.appendChar(trd.getMerchantId(), ' ', 15, false));

		isoBuffer.put(Constants.DE43, trd.getReqDe43());
		String currencyCode = Util.appendChar(trd.getCurrencyCode(), '0', 3,
				true);
		isoBuffer.put(Constants.DE48, "000000000000000000000000000");
		isoBuffer.put(Constants.DE49, currencyCode);
		isoBuffer.put(Constants.DE50, currencyCode);
		isoBuffer.put(Constants.DE51, currencyCode);

		isoBuffer.disableField(Constants.DE52);
		isoBuffer.disableField(Constants.DE51);
		isoBuffer.disableField(Constants.DE55);
		isoBuffer.disableField(Constants.DE63);
		isoBuffer.disableField(Constants.DE62);
		isoBuffer.disableField(Constants.DE47);
		isoBuffer.disableField(Constants.DE50);
		isoBuffer.disableField(Constants.DE44);
		isoBuffer.disableField(Constants.DE90); // 08/03/19
		isoBuffer.disableField(Constants.DE39);

		/*
		 * isoBuffer.disableField(Constants.DE38);
		 * isoBuffer.disableField(Constants.DE39);
		 * isoBuffer.disableField(Constants.DE50);
		 * isoBuffer.disableField(Constants.DE51);
		 * isoBuffer.disableField(Constants.DE90);
		 * isoBuffer.disableField(Constants.DE52);
		 * isoBuffer.disableField(Constants.DE55);
		 * isoBuffer.disableField(Constants.DE63);
		 * isoBuffer.disableField(Constants.DE62);
		 * isoBuffer.disableField(Constants.DE47);
		 */
	}

	@ModifyTransaction(Transactions.CASH_ADVANCE)
	public void modifyCashAdvance(IsoBuffer buffer, Data data) {
		HostData hostData = (HostData) data;
		buffer.put(Constants.DE48, "C");
		buffer.put(Constants.DE3, 17 + buffer.get(Constants.DE3)
				.substring(2, 6));
		StringBuilder de54 = new StringBuilder(buffer.get(Constants.DE3)
				.substring(2, 4));
		de54.append("40");
		de54.append(hostData.getCurrencyCode());
		de54.append("C");
		de54.append(buffer.get(Constants.DE54));
		buffer.put(Constants.DE54, de54.toString());

		buffer.disableField(Constants.DE44);
	}
}