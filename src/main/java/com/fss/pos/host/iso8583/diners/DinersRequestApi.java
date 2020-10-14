package com.fss.pos.host.iso8583.diners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fss.pos.base.api.host.HostData;
import com.fss.pos.base.api.host.HostRequest;
import com.fss.pos.base.api.transactions.ModifyTransaction;
import com.fss.pos.base.api.transactions.Transactions;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.EmvTags;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.base.services.transactionlog.TransactionLogData;

@HostRequest(HostType = "32")
public class DinersRequestApi extends DinersApi {
	
	private static final boolean EMV_TERTIARY_ENABLED = false;
	// private static final String DE100_DEFAULT = "00";
	private static final String EMV_DATASET_ID = "01";
	private static final String DE90_FIID_DEFAULT = "00000000000";

	private static final Map<EmvTags, String> EMV_TAG_FIELD_MAP;

	static {
		HashMap<EmvTags, String> tempMap = new HashMap<EmvTags, String>();
		tempMap.put(EmvTags.TAG_9F33, Constants.DE130);
		tempMap.put(EmvTags.TAG_95, Constants.DE131);
		tempMap.put(EmvTags.TAG_9F37, Constants.DE132);
		tempMap.put(EmvTags.TAG_9F10, Constants.DE134);
		tempMap.put(EmvTags.TAG_9F26, Constants.DE136);
		tempMap.put(EmvTags.TAG_9F36, Constants.DE137);
		tempMap.put(EmvTags.TAG_82, Constants.DE138);
		tempMap.put(EmvTags.TAG_91, Constants.DE140);
		tempMap.put(EmvTags.TAG_9C, Constants.DE144);
		tempMap.put(EmvTags.TAG_9F1A, Constants.DE145);
		tempMap.put(EmvTags.TAG_9A, Constants.DE146);
		tempMap.put(EmvTags.TAG_9F02, Constants.DE147);
		tempMap.put(EmvTags.TAG_5F2A, Constants.DE148);
		tempMap.put(EmvTags.TAG_9F03, Constants.DE149);
		EMV_TAG_FIELD_MAP = Collections.unmodifiableMap(tempMap);
	}
	
	private static final List<EmvTags> SUPPORTED_TAGS = new ArrayList();

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
		//contactless 25/03
		SUPPORTED_TAGS.add(EmvTags.TAG_9F6E); 
		SUPPORTED_TAGS.add(EmvTags.TAG_9F7C);
	}

	@ModifyTransaction(Transactions.DEFAULT)
	public void modifyAll(IsoBuffer isoBuffer, Data data) {
		HostData hostData = (HostData) data;
		isoBuffer.put(Constants.DE7, hostData.getTxnDateTime());
		isoBuffer.put(Constants.DE12,
				Util.getFormattedDateTime("yy") + hostData.getMerchantDate()
						+ hostData.getMerchantTime());
		String entrymode = isoBuffer.get(Constants.DE22).substring(0, 2);
		if(entrymode == "05" || entrymode == "07")
		{
			isoBuffer.put(Constants.DE13,
					Util.getFormattedDateTime("yy") + hostData.getMerchantDate());
		}
		else if(entrymode == "02" && isoBuffer.isFieldEmpty(Constants.DE35) && isoBuffer.isFieldEmpty(Constants.DE45))
		{
			isoBuffer.put(Constants.DE13,
					Util.getFormattedDateTime("yy") + hostData.getMerchantDate());
		}
		else
			isoBuffer.disableField(Constants.DE13);
		
		if (!Util.isNullOrEmpty(isoBuffer.get(Constants.DE35))) {
			isoBuffer.disableField(Constants.DE14);
		}
		String de22S1 = "";
		String de22S2 = "";
		String de22S3 = "";
		String de22S4 = "";
		String de22S5 = "";
		String de22S6 = "";
		String de22S7 = "";
		String de22S8 = "";
		String de22S9 = "";
		String de22S10 = "";
		String de22S11 = "";
		String de22S12 = "";
		
		StringBuilder de22 = new StringBuilder();
		if(isoBuffer.isFieldEmpty(Constants.DE52))
				{
			de22S2 = "0";
			de22S8 = "0";
			de22S9 = "1";
				}
		else
		{
			de22S2 = "1";
		de22S8 = "1";
		de22S9 = "1";
		}
		String entryMode = isoBuffer.get(Constants.DE22).substring(0, 2);
		switch(entryMode)
		{
		case "01":
			de22S1 = "1";
			de22S3 = "0";
			de22S4 = "1";
			de22S5 = "0";
			de22S6 = "1";
			de22S7 = "1";
			de22S10 = "9";
			de22S11 = "4";
			de22S12 = "1";
			break;
		case "02":
			de22S1 = "2";
			de22S3 = "1";
			de22S4 = "1";
			de22S5 = "0";
			de22S6 = "1";
			de22S7 = "2";
			de22S10 = "9";
			de22S11 = "4";
			de22S12 = "1";
			break;
		case "05":
			de22S1 = "5";
			de22S3 = "1";
			de22S4 = "1";
			de22S5 = "0";
			de22S6 = "1";
			de22S7 = "5";
			de22S10 = "9";
			de22S11 = "4";
			de22S12 = "1";
			break;
		case "07":
			de22S1 = "8";
			de22S3 = "1";
			de22S4 = "1";
			de22S5 = "0";
			de22S6 = "1";
			de22S7 = "5";
			de22S10 = "9";
			de22S11 = "4";
			de22S12 = "1";
			break;
		case "80":
			de22S1 = "2";
			de22S3 = "1";
			de22S4 = "1";
			de22S5 = "0";
			de22S6 = "1";
			de22S7 = "9";
			de22S10 = "9";
			de22S11 = "4";
			de22S12 = "1";
			break;
		case "90":
			de22.append("9");
			break;
		case "91":
			de22.append("9");
			break;
		}
		
		de22.append(de22S1);
		de22.append(de22S2);
		de22.append(de22S3);
		de22.append(de22S4);
		de22.append(de22S5);
		de22.append(de22S6);
		de22.append(de22S7);
		de22.append(de22S8);
		de22.append(de22S9);
		de22.append(de22S10);
		de22.append(de22S11);
		de22.append(de22S12);
		isoBuffer.put(Constants.DE22, de22.toString());
		isoBuffer.put(Constants.DE35, isoBuffer.get(Constants.DE35).replace("=", "D"));

		isoBuffer.put(Constants.DE32, hostData.getAcqInstId());
		isoBuffer.put(Constants.DE33, hostData.getFiid());
		isoBuffer.put(Constants.DE37, hostData.getRrn());
		isoBuffer.put(Constants.DE41,
				hostData.getTerminalId());
		isoBuffer.put(Constants.DE42,
				hostData.getMerchantId());			
		StringBuilder de43 = new StringBuilder();
		de43.append(hostData.getBusinessName()).append("\\");
		de43.append(hostData.getMerchantAddress()).append("\\");
		de43.append(hostData.getCity()).append("\\");
		de43.append(Util.appendChar(hostData.getZipCode(), ' ', 10, false));
		de43.append(Util.appendChar(hostData.getState(), ' ', 3, false));
		de43.append(hostData.getCountryCode());		
		isoBuffer.put(Constants.DE43, de43.toString());
		isoBuffer.put(Constants.DE45, isoBuffer.get(Constants.DE45));
		isoBuffer.put(Constants.DE49, hostData.getCurrencyCode());
		/*isoBuffer.put("P-53", "010101");*/
		isoBuffer.put(Constants.DE55, isoBuffer.get(Constants.DE55));
		/*isoBuffer.disableField("P-13");*/
		isoBuffer.put(Constants.DE35, isoBuffer.get(Constants.DE35).replace("=", "D"));
		/*if (!isoBuffer.isFieldEmpty("P-52"))
			isoBuffer.put("P-53", "2001010100000000");*/
		
		isoBuffer.put(Constants.DE92, hostData.getCountryCode());
		isoBuffer.disableField(Constants.DE63);
		isoBuffer.disableField(Constants.DE60);
		isoBuffer.disableField(Constants.DE62);
		isoBuffer.disableField(Constants.DE25);
		isoBuffer.disableField(Constants.DE44);
		
	}

	@ModifyTransaction(value = Transactions.EMV_DEFAULT)
	public void modifyEmv(IsoBuffer buffer, Data data) {

		HostData hd = (HostData) data;
		Map<EmvTags, String> tvMap = hd.getEmvMap();
		emvDetails(tvMap, buffer);
	}

	private void emvDetails(Map<EmvTags, String> tvMap, IsoBuffer buffer) {  
		if (tvMap == null || tvMap.isEmpty())
			return;

		if (EMV_TERTIARY_ENABLED) {
			for (EmvTags tag : EMV_TAG_FIELD_MAP.keySet()) {
				String val = tvMap.get(tag);
				if (EmvTags.TAG_9F1A.equals(tag)
						|| EmvTags.TAG_5F2A.equals(tag)) {
					
					buffer.put(EMV_TAG_FIELD_MAP.get(tag), val == null ? "*"
							: (Constants.ZERO + val));
					continue;
				}
				buffer.put(EMV_TAG_FIELD_MAP.get(tag), val == null ? "*" : val);
			}
			buffer.disableField(Constants.DE55);
		} else {

			if (Util.isNullOrEmpty(tvMap.get(EmvTags.TAG_9F34))) {
				tvMap.remove(EmvTags.TAG_9F34);
			}
			if (Util.isNullOrEmpty(tvMap.get(EmvTags.TAG_9F03))) {
				tvMap.remove(EmvTags.TAG_9F03);
			}
			tvMap.remove(EmvTags.TAG_9F07);
			
			tvMap.remove(EmvTags.TAG_9F09);
			tvMap.remove(EmvTags.TAG_9F41);
			tvMap.remove(EmvTags.TAG_9F35);
			tvMap.remove(EmvTags.TAG_4F);
			//tvMap.remove(EmvTags.TAG_9F37);
			tvMap.remove(EmvTags.TAG_9F27);
			tvMap.remove(EmvTags.TAG_9F03);
			tvMap.remove(EmvTags.TAG_9F34);
			tvMap.remove(EmvTags.TAG_9F06);
			/*
			 * contactless changes on 07/01
			 * 
			 * if (Util.isNullOrEmpty(tvMap.get(EmvTags.TAG_9F6E))) {
			 * tvMap.remove(EmvTags.TAG_9F6E); }
			 */

			String entryMode = buffer.get(Constants.DE22).substring(0, 2);
			if (Constants.ENTRY_MODE_ICC_CL.equals(entryMode)
					|| Constants.ENTRY_MODE_MAG_STRIPE_CL.equals(entryMode)) {
				tvMap.remove(EmvTags.TAG_57);
				tvMap.remove(EmvTags.TAG_9F1E);
			}

			String cardSeqNo = tvMap.get(EmvTags.TAG_5F34);
			if (Util.isNullOrEmpty(cardSeqNo)) {
				tvMap.remove(EmvTags.TAG_5F34);
			} else {
				buffer.put(Constants.DE23,
						Util.appendChar(cardSeqNo, '0', 3, true));
			}
			tvMap.remove(EmvTags.TAG_5F34);
			// Log.debug("Visa EMV", tvMap.toString());
			StringBuilder d = new StringBuilder();
			for (EmvTags tag : tvMap.keySet()) {
				String val = tvMap.get(tag);
				d.append(tag.toString());
				d.append(Util.appendChar(
						String.valueOf(Integer.toHexString(val.length() / 2)),
						'0', 2, true));
				d.append(val);
			}

			String emvDataAscii = IsoUtil.hex2AsciiChar(d.toString());
			int emvLen = emvDataAscii.length();
			String emvHexLen = Util.appendChar(Integer.toHexString(emvLen),
					'0', 4, true);

			StringBuilder emvDataBuffer = new StringBuilder();
			/*emvDataBuffer.append(EMV_DATASET_ID);
			emvDataBuffer.append(emvHexLen);*/
			emvDataBuffer.append(IsoUtil.asciiChar2hex(emvDataAscii));
			buffer.put(Constants.DE55, d.toString());
		}

	}

	@ModifyTransaction(Transactions.REFUND)
	public void modifyRefund(IsoBuffer isoBuffer, Data data) {
		HostData hd = (HostData) data;
		isoBuffer.put(Constants.ISO_MSG_TYPE,Constants.MSG_TYPE_1200);
		isoBuffer.put(Constants.DE25, "1500");
		isoBuffer.put(Constants.DE26, Util.appendChar(hd.getMcc(), '0', 4, true));
		isoBuffer.disableField(Constants.DE47);
		isoBuffer.disableField(Constants.DE61);
		isoBuffer.disableField(Constants.DE38);
		isoBuffer.disableField(Constants.DE39);
	}

	@ModifyTransaction(Transactions.CASH_ADVANCE)
	public void modifyCashAdvance(IsoBuffer isoBuffer, Data data) {
		/*HostData hd = (HostData) data;
		isoBuffer.put(Constants.ISO_MSG_TYPE,Constants.MSG_TYPE_1200);
		isoBuffer.put(Constants.DE24, "200");
		isoBuffer.put(Constants.DE26, Util.appendChar(hd.getMcc(), '0', 4, true));
		isoBuffer.disableField(Constants.DE47);
		isoBuffer.disableField(Constants.DE61);
		isoBuffer.disableField(Constants.DE38);
		isoBuffer.disableField(Constants.DE39);*/
		HostData hostData = (HostData) data;
		isoBuffer.put(Constants.ISO_MSG_TYPE,Constants.MSG_TYPE_1100);
		isoBuffer.put(Constants.DE24, "100");
		isoBuffer.disableField(Constants.DE25);
		isoBuffer.put(Constants.DE26, Util.appendChar(hostData.getMcc(), '0', 4, true));
		isoBuffer.disableField(Constants.DE37);
		isoBuffer.disableField(Constants.DE47);
		isoBuffer.disableField(Constants.DE53);
		isoBuffer.disableField(Constants.DE61);
		isoBuffer.disableField(Constants.DE38);
		isoBuffer.disableField(Constants.DE39);
		isoBuffer.disableField(Constants.DE60);
	}

	@ModifyTransaction(Transactions.CASH_BACK)
	public void modifyCashBack(IsoBuffer isoBuffer, Data data) {
		HostData hd = (HostData) data;
		isoBuffer.put(Constants.DE26, Util.appendChar(hd.getMcc(), '0', 4, true));
		isoBuffer.put(Constants.ISO_MSG_TYPE,Constants.MSG_TYPE_1200 );
		isoBuffer.put(Constants.DE24, "200");
		isoBuffer.disableField(Constants.DE47);
		isoBuffer.disableField(Constants.DE61);
		isoBuffer.disableField(Constants.DE38);
		isoBuffer.disableField(Constants.DE39);
	}

	@ModifyTransaction(Transactions.BALANCE_INQUIRY)
	public void modifyBalanceEnquire(IsoBuffer isoBuffer, Data data) {
		/*HostData hd = (HostData) data;
		isoBuffer.put(Constants.DE26, Util.appendChar(hd.getMcc(), '0', 4, true));
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_1200);
		isoBuffer.disableField(Constants.DE47);
		isoBuffer.disableField(Constants.DE61);
		isoBuffer.disableField(Constants.DE38);
		isoBuffer.disableField(Constants.DE39);
		isoBuffer.disableField(Constants.DE54);*/
		HostData hostData = (HostData) data;
		isoBuffer.put(Constants.ISO_MSG_TYPE,Constants.MSG_TYPE_1100);
		isoBuffer.put(Constants.DE24, "100");
		isoBuffer.disableField(Constants.DE25);
		isoBuffer.put(Constants.DE26, Util.appendChar(hostData.getMcc(), '0', 4, true));
		isoBuffer.disableField(Constants.DE37);
		isoBuffer.disableField(Constants.DE47);
		isoBuffer.disableField(Constants.DE53);
		isoBuffer.disableField(Constants.DE61);
		isoBuffer.disableField(Constants.DE38);
		isoBuffer.disableField(Constants.DE39);
		isoBuffer.disableField(Constants.DE60);
		
	}

	@ModifyTransaction(Transactions.PRE_AUTHORIZATION)
	public void modifyPreAuthorization(IsoBuffer isoBuffer, Data data) {
		HostData hd = (HostData) data;
		isoBuffer.put(Constants.DE26, Util.appendChar(hd.getMcc(), '0', 4, true));
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_1100);
		StringBuilder de3 = new StringBuilder();
		de3.append("00");
		de3.append(Constants.DE3.substring(2,6));
		isoBuffer.put(Constants.DE3, de3.toString());
		/*isoBuffer.put("P-3", "00" + isoBuffer.get("P-3").substring(2, 6));*/
		isoBuffer.put(Constants.DE24, "100");
		isoBuffer.put(Constants.DE57, "105");
		isoBuffer.put(Constants.DE39, "00");
		isoBuffer.disableField(Constants.DE47);
		isoBuffer.disableField(Constants.DE61);
		isoBuffer.disableField(Constants.DE38);
		isoBuffer.disableField(Constants.DE39);
	}

	@ModifyTransaction(Transactions.COMPLETION)
	public void modifyCompletion(IsoBuffer isoBuffer, Data data) {
		HostData hd = (HostData) data;
		/*isoBuffer.put("P-3", "00" + isoBuffer.get("P-3").substring(2, 6));*/
		StringBuilder de3 = new StringBuilder();
		de3.append("00");
		de3.append(Constants.DE3.substring(2,6));
		isoBuffer.put(Constants.DE3, de3.toString());
		if ((hd.getOrgAuthCode() != null) && (!"0".equals(hd.getOrgAuthCode()))) {
			isoBuffer.put(Constants.DE38, hd.getOrgAuthCode());
		}
		StringBuilder de35 = new StringBuilder();
		de35.append(Constants.DE2);
		de35.append("=");
		de35.append(Constants.DE14);
		de35.append("600");
		isoBuffer.put(Constants.DE35, de35.toString());
		/*isoBuffer.put("P-35",
				isoBuffer.get("P-2") + "=" + isoBuffer.get("P-14") + "600");*/

		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_RSP_AUTH_ADVICE_93);
		isoBuffer.put(Constants.DE24, "201");
		isoBuffer.put(Constants.DE22, hd.getReqDe22());
		isoBuffer.put(Constants.DE39, hd.getRespDe39());
		isoBuffer.put(Constants.DE26, Util.appendChar(hd.getMcc(), '0', 4, true));
		StringBuilder s56 = new StringBuilder(hd.getOrgMessageType());
		s56.append(hd.getOrgStan());
		s56.append(hd.getOrgTransmissionDt());
		s56.append((hd.getAcqInstId().length() > 10 ? hd.getAcqInstId()
				.length() : new StringBuilder("0").append(
				hd.getAcqInstId().length()).toString())
				+ hd.getAcqInstId());
		isoBuffer.put(Constants.DE56, s56.toString());
		isoBuffer.disableField(Constants.DE47);
		isoBuffer.disableField(Constants.DE25);
		isoBuffer.disableField(Constants.DE61);
		isoBuffer.disableField(Constants.DE2);
		isoBuffer.disableField(Constants.DE14);
	}

	@ModifyTransaction(Transactions.VOID)
	public void modifyVoid(IsoBuffer isoBuffer, Data data) {
		HostData hostData = (HostData) data;
		isoBuffer.put(Constants.ISO_MSG_TYPE,Constants.MSG_TYPE_1120);
		isoBuffer.put(Constants.DE24, "100");
		isoBuffer.disableField(Constants.DE25);
		isoBuffer.put(Constants.DE26, Util.appendChar(hostData.getMcc(), '0', 4, true));
		isoBuffer.put(Constants.DE38, Util.appendChar(hostData.getOrgAuthCode(), '0', 6, true));
		isoBuffer.put(Constants.DE39, Util.appendChar(hostData.getRespDe39(), '0', 3, true));
		StringBuilder de56 = new StringBuilder();
		de56.append(hostData.getOrgMessageType());
		de56.append(hostData.getOrgStan());
		de56.append(hostData.getOrgTransmissionDY());
		de56.append(IsoUtil.leftPadZeros(hostData.getOrgAcquirerId(), 11));
		isoBuffer.put(Constants.DE56, de56.toString());
		isoBuffer.disableField(Constants.DE37);
		isoBuffer.disableField(Constants.DE47);
		isoBuffer.disableField(Constants.DE53);
		isoBuffer.disableField(Constants.DE61);
		//isoBuffer.disableField(Constants.DE39);
		isoBuffer.disableField(Constants.DE60);
	}

	@ModifyTransaction(Transactions.TIP)
	public void modifyTIP(IsoBuffer isoBuffer, Data data) {
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_1200);
		isoBuffer.put(Constants.DE24, "200");
		HostData hd = (HostData) data;
		isoBuffer.put(Constants.DE26, Util.appendChar(hd.getMcc(), '0', 4, true));
		isoBuffer.disableField(Constants.DE2);
		isoBuffer.disableField(Constants.DE14);
		isoBuffer.disableField(Constants.DE25);
		isoBuffer.disableField(Constants.DE47);
		isoBuffer.disableField(Constants.DE61);
		isoBuffer.disableField(Constants.DE38);
		isoBuffer.disableField(Constants.DE39);
	}

	@ModifyTransaction(Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer isoBuffer, Data data) {
		TransactionLogData trd = (TransactionLogData) data;
		isoBuffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_1420);
		//isoBuffer.put("P-19", trd.getCurrencyCode());
		isoBuffer.put(Constants.DE3, "22" + isoBuffer.get(Constants.DE3).substring(2, 6));
		isoBuffer.put(Constants.DE24, "400");
		isoBuffer.put(Constants.DE32, trd.getAcqInstId());
		isoBuffer.put(Constants.DE33, trd.getFiid());
		if ((trd.getOrgAuthCode() != null) && (!"0".equals(trd.getOrgAuthCode()))) {
			isoBuffer.put(Constants.DE38, Util.appendChar(trd.getOrgAuthCode(), '0', 6, true));
			isoBuffer.put(Constants.DE39, "081");
		}
		else
		{
			isoBuffer.disableField(Constants.DE38);
			isoBuffer.disableField(Constants.DE39);
		}
		
		isoBuffer.put(Constants.DE12, trd.getReqDe12());
		//isoBuffer.put("P-17", trd.getMerchantDate());
		isoBuffer.put(Constants.DE26, Util.appendChar(trd.getMcc(), '0', 4, true));
		//isoBuffer.put("P-53", "010101");
		//isoBuffer.put("P-39", "000");
		isoBuffer.put(Constants.DE49, trd.getCurrencyCode());
		
		/*Map<EmvTags, String> tvMap = trd.getEmvMap();
		emvDetails(tvMap, isoBuffer);*/
		
		StringBuilder de56 = new StringBuilder();
		de56.append(trd.getOriginalMsgType());
		de56.append(trd.getOrgStan());
		de56.append(trd.getOrgTransmissionDY());
		de56.append(IsoUtil.leftPadZeros(trd.getAcqInstId(), 11));
		isoBuffer.put(Constants.DE56, de56.toString());
		
		isoBuffer.put(Constants.DE92, trd.getCountryCode());
		//isoBuffer.put("P-25", "4020");
		isoBuffer.disableField(Constants.DE22);
		isoBuffer.disableField(Constants.DE23);
		isoBuffer.disableField(Constants.DE25);
		isoBuffer.disableField(Constants.DE37);
		
		isoBuffer.disableField(Constants.DE41);
		isoBuffer.disableField(Constants.DE42);
		isoBuffer.disableField(Constants.DE55);
		isoBuffer.disableField(Constants.DE47);
		isoBuffer.disableField(Constants.DE61);
		isoBuffer.disableField(Constants.DE62);
		isoBuffer.disableField(Constants.DE63);
		isoBuffer.disableField(Constants.DE14);
		isoBuffer.disableField(Constants.DE44);
		
	}

	@ModifyTransaction(Transactions.PURCHASE)
	public void modifyPurchace(IsoBuffer isoBuffer, Data data) {
		HostData hostData = (HostData) data;
		isoBuffer.put(Constants.ISO_MSG_TYPE,Constants.MSG_TYPE_1100);
		isoBuffer.put(Constants.DE24, "100");
		isoBuffer.disableField(Constants.DE25);
		isoBuffer.put(Constants.DE26, Util.appendChar(hostData.getMcc(), '0', 4, true));
		isoBuffer.disableField(Constants.DE37);
		isoBuffer.disableField(Constants.DE47);
		isoBuffer.disableField(Constants.DE53);
		isoBuffer.disableField(Constants.DE61);
		isoBuffer.disableField(Constants.DE38);
		isoBuffer.disableField(Constants.DE39);
		isoBuffer.disableField(Constants.DE60);
	}
}