package com.fss.pos.host.iso8583.benefit;

import java.util.ArrayList;
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
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.base.services.transactionlog.TransactionLogData;

@HostRequest(HostType = "29")
public class BenefitRequestApi extends BenefitApi {

	// private static final String de17="0818";
	// private static final String de15="0816";

	private static final List<EmvTags> SUPPORTED_TAGS;
	// private static final String DE22_MAGSTRIPE_ID = "90";

	static {
		SUPPORTED_TAGS = new ArrayList<EmvTags>();
		SUPPORTED_TAGS.add(EmvTags.TAG_5F2A);
		SUPPORTED_TAGS.add(EmvTags.TAG_82);
		SUPPORTED_TAGS.add(EmvTags.TAG_84);
		SUPPORTED_TAGS.add(EmvTags.TAG_95);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F09);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F10);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F1A);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F26);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F27);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F33);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F34);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F35);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F36);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F37);
		SUPPORTED_TAGS.add(EmvTags.TAG_9C);
		SUPPORTED_TAGS.add(EmvTags.TAG_9A);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F02);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F03);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F1E);
		// contactless 25/03
		SUPPORTED_TAGS.add(EmvTags.TAG_9F6E);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F7C);
	}

	@ModifyTransaction(value = Transactions.DEFAULT)
	public void modifyAll(IsoBuffer isoBuffer, Data data) {

		HostData hostData = (HostData) data;
		isoBuffer.put(Constants.DE7, hostData.getTxnDateTime());
		isoBuffer.put(Constants.DE12, hostData.getMerchantTime());
		isoBuffer.put(Constants.DE13, hostData.getMerchantDate());
		isoBuffer.put(Constants.DE17, hostData.getReqDe17());
		// isoBuffer.put(Constants.DE17, de17);
		isoBuffer.put(Constants.DE32,
				Util.appendChar(hostData.getAcqInstId(), '0', 11, true));
		isoBuffer.put(Constants.DE41,
				Util.appendChar(hostData.getTerminalId(), ' ', 16, false));
		isoBuffer.put(Constants.DE42,
				Util.appendChar(hostData.getMerchantId(), ' ', 15, false));

		isoBuffer.put(Constants.DE49,
				Util.appendChar(hostData.getCurrencyCode(), '0', 3, true));
		String terminalOwner = hostData.getBusinessName();
		String city = hostData.getCity();
		if (terminalOwner.length() > 22) {
			terminalOwner = hostData.getBusinessName().substring(0, 22);
		}
		// String state = hostData.getState();
		// String country = hostData.getCountryCode();
		isoBuffer.put(
				Constants.DE43,
				Util.appendChar(terminalOwner, ' ', 22, false)
						+ Util.appendChar(city, ' ', 13, false) + "   " + "BH");
		isoBuffer.put(Constants.DE48, "0001000100010001000" + hostData.getMcc()
				+ Util.appendChar(hostData.getCountryCode(), '0', 4, true));
		
		//hardcoded for contactless certification on 11/04
		String entryMode = isoBuffer.get(Constants.DE22).substring(0, 2);
		isoBuffer.put(Constants.DE22, entryMode.concat("1"));
		
		if (isoBuffer.get(Constants.DE22).substring(0, 2)
				.equals(Constants.ENTRY_MODE_ICC_CL)) {
			isoBuffer.put(Constants.DE57, "310");
		} else {
			isoBuffer.put(Constants.DE57, "510"); // 310 contactless
		}
		isoBuffer.put(Constants.DE60, "00000000+0000000");
		isoBuffer.put(Constants.DE61, "0000000010000000000");
		isoBuffer.disableField(Constants.DE25);

		isoBuffer.disableField(Constants.DE44);

		
	}

	@ModifyTransaction(value = Transactions.PURCHASE)
	public void modifyPurchace(IsoBuffer buffer, Data data) {

		HostData hostData = (HostData) data;
		buffer.disableField(Constants.DE54);

		String entryMode = buffer.get(Constants.DE22);

		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE63);
		buffer.disableField(Constants.DE44);
		buffer.put(Constants.ISO_MSG_TYPE, "0200");
		buffer.put(Constants.DE18, hostData.getMcc());

		// manual entry
		if (entryMode.substring(0, 2).equals(Constants.ENTRY_MODE_MANUAL)) {
			// building p35 from p2 n p14
			StringBuilder track2 = new StringBuilder();
			track2.append(buffer.get(Constants.DE2));
			track2.append("=");
			track2.append(buffer.isFieldEmpty(Constants.DE14) ? "0000" : buffer
					.get(Constants.DE14));
			track2.append("000000");
			track2.append("?");
			buffer.put(Constants.DE35, track2.toString());
		}
		buffer.disableField(Constants.DE2);

		/*if (buffer.isFieldEmpty(Constants.DE52)) {
			buffer.put(Constants.DE52, "                ");
		}*/

	}

	@ModifyTransaction(value = Transactions.REFUND)
	public void modifyRefund(IsoBuffer isoBuffer, Data data) {
		HostData hd = (HostData) data;
		isoBuffer.put(Constants.ISO_MSG_TYPE, "0200");
		// isoBuffer.put(Constants.DE3, "");
		String entryMode = isoBuffer.get(Constants.DE22);
		isoBuffer.disableField(Constants.DE62);
		isoBuffer.disableField(Constants.DE63);
		isoBuffer.disableField(Constants.DE54);
		isoBuffer.put(Constants.ISO_MSG_TYPE, "0200");
		isoBuffer.put(Constants.DE18, hd.getMcc());
		Map<EmvTags, String> tvMap = hd.getEmvMap();
		if (tvMap.containsKey(EmvTags.TAG_5F34))
			isoBuffer.put(Constants.DE23,
					Util.appendChar(tvMap.get(EmvTags.TAG_5F34), '0', 3, true));

		// manual entry
		if (entryMode.substring(0, 2).equals(Constants.ENTRY_MODE_MANUAL)) {
			// building p35 from p2 n p14
			StringBuilder track2 = new StringBuilder();
			track2.append(isoBuffer.get(Constants.DE2));
			track2.append("=");
			track2.append(isoBuffer.isFieldEmpty(Constants.DE14) ? "0000"
					: isoBuffer.get(Constants.DE14));
			track2.append("000000");
			track2.append("?");
			isoBuffer.put(Constants.DE35, track2.toString());
		}
		isoBuffer.disableField(Constants.DE2);

		/*if (isoBuffer.isFieldEmpty(Constants.DE52)) {
			isoBuffer.put(Constants.DE52, "                ");
		}*/

		isoBuffer.disableField(Constants.DE44);
	}

	@ModifyTransaction(value = Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer isoBuffer, Data data) {

		TransactionLogData trd = (TransactionLogData) data;
		isoBuffer.put(Constants.DE12, trd.getMerchantTime());
		isoBuffer.put(Constants.DE13, trd.getMerchantDate());
		isoBuffer.put(Constants.DE17, trd.getReqDe17()); // hard code value for
															// cutover
		isoBuffer.put(Constants.DE32,
				Util.appendChar(trd.getAcqInstId(), '0', 11, true));
		//String terminalOwner = trd.getBusinessName();
		//String city = trd.getCity();

		/*
		 * isoBuffer.put( Constants.DE43, Util.appendChar(terminalOwner, ' ',
		 * 22, false) + Util.appendChar(city, ' ', 13, false) + "   " + "BH");
		 */
		isoBuffer.put(Constants.DE43, trd.getReqDe43());
		isoBuffer.put(Constants.DE41,
				Util.appendChar(trd.getTerminalId(), ' ', 16, false));
		isoBuffer.put(Constants.DE42,
				Util.appendChar(trd.getMerchantId(), ' ', 15, false));
		isoBuffer.put(Constants.DE48, "0001000100010001000" + trd.getMcc()
				+ Util.appendChar(trd.getCountryCode(), '0', 4, true));
		isoBuffer.put(Constants.DE60, "00000000+0000000");
		isoBuffer.put(Constants.DE61, "0000000010000000000");
		isoBuffer.disableField(Constants.DE62);
		isoBuffer.disableField(Constants.DE63);
		isoBuffer.put(Constants.ISO_MSG_TYPE, "0420");
		isoBuffer.put(Constants.DE3, trd.getOrgProcCode()); // original proc
															// code

		isoBuffer.put(Constants.DE15, trd.getReqDe15()); // need to take from db
															// from host
		// table

		isoBuffer.put(Constants.DE22, trd.getReqDe22());
		if(Util.isNullOrEmpty(trd.getOrgAuthCode())){
			isoBuffer.disableField(Constants.DE38);
		}else{
			isoBuffer.put(Constants.DE38, trd.getOrgAuthCode());
		}
		isoBuffer.put(Constants.DE39, "20"); // need to confirm
		isoBuffer.put(Constants.DE49,
				Util.appendChar(trd.getCurrencyCode(), '0', 3, true));
		/*if (isoBuffer.get(Constants.DE22).substring(0, 2)
				.equals(Constants.ENTRY_MODE_ICC_CL)) {
			isoBuffer.put(Constants.DE57, "310");
		} else {
			isoBuffer.put(Constants.DE57, "510"); // 310 contactless
		}*/

		/*
		 * Map<EmvTags, String> tvMap = trd.getEmvMap(); if
		 * (tvMap.containsKey(EmvTags.TAG_5F34)) isoBuffer.put(Constants.DE23,
		 * Util.appendChar(tvMap.get(EmvTags.TAG_5F34), '0', 3, true));
		 */

		String originalStan = Util.appendChar(trd.getOrgStan(), ' ', 12, false); // original
																					// stan

		String txnDateTime = Util.appendChar(trd.getOrgTransmissionDt()
				.substring(0, 4).concat(trd.getOrgTransmissionDY()), '0', 12,
				false);

		String originalCaptureDate = trd.getReqDe17(); // cutover date + 1
		// String originalCaptureDate = hd.getReqDe17();
		String filter = "0000000000";
		isoBuffer.put(
				Constants.DE90,
				"0200".concat(originalStan).concat(txnDateTime)
						.concat(originalCaptureDate).concat(filter));

		// isoBuffer.put(Constants.DE100, "04888000000");
		isoBuffer.put(Constants.DE100, trd.getRespDe100());

		// building p35 from p2 n p14
		StringBuilder track2 = new StringBuilder();
		track2.append(isoBuffer.get(Constants.DE2));
		track2.append("=");
		track2.append(isoBuffer.isFieldEmpty(Constants.DE14) ? "0000"
				: isoBuffer.get(Constants.DE14));
		track2.append(trd.getReqDe35());
		track2.append("00000000");
		isoBuffer.put(Constants.DE35, track2.toString());

		// Log.debug("p35 n p2 data",isoBuffer.get(Constants.DE35) +
		// isoBuffer.get(Constants.DE2));
		isoBuffer.disableField(Constants.DE2);
		isoBuffer.disableField(Constants.DE55);
		isoBuffer.disableField(Constants.DE52);
		isoBuffer.disableField(Constants.DE25);
		isoBuffer.disableField(Constants.DE44);
		isoBuffer.disableField(Constants.DE57); 
		isoBuffer.disableField(Constants.DE54); 

		/*
		 * if(trd.getEmvMap().isEmpty()){
		 * isoBuffer.disableField(Constants.DE55); }else{ modifyEmv(isoBuffer,
		 * trd.getEmvMap()); }
		 */
	}

	@ModifyTransaction(value = Transactions.VOID)
	public void modifyVoid(IsoBuffer isoBuffer, Data data) {

		HostData hd = (HostData) data;
		isoBuffer.disableField(Constants.DE62);
		isoBuffer.disableField(Constants.DE63);
		isoBuffer.disableField(Constants.DE54);
		isoBuffer.disableField(Constants.DE44);
		isoBuffer.put(Constants.ISO_MSG_TYPE, "0420");
		isoBuffer.put(Constants.DE3, hd.getOrgProcCode()); // original proc code
		isoBuffer.put(Constants.DE15, hd.getReqDe15()); // need to take from db
														// frm host
		// table
		// isoBuffer.put(Constants.DE15, hd.getReqDe15());
		isoBuffer.put(Constants.DE22, hd.getReqDe22());
		isoBuffer.put(Constants.DE38, hd.getOrgAuthCode());
		isoBuffer.put(Constants.DE39, "17");

		Map<EmvTags, String> tvMap = hd.getEmvMap();
		if (tvMap.containsKey(EmvTags.TAG_5F34))
			isoBuffer.put(Constants.DE23,
					Util.appendChar(tvMap.get(EmvTags.TAG_5F34), '0', 3, true));

		String originalStan = Util.appendChar(hd.getOrgStan(), ' ', 12, false); // original
																				// stan

		String txnDateTime = Util.appendChar(hd.getOrgTransmissionDt()
				.substring(0, 4).concat(hd.getOrgTransmissionDY()), '0', 12,
				false);

		String originalCaptureDate = hd.getReqDe17(); // cutover date + 1
		// String originalCaptureDate = hd.getReqDe17();
		String filter = "0000000000";
		isoBuffer.put(
				Constants.DE90,
				"0200".concat(originalStan).concat(txnDateTime)
						.concat(originalCaptureDate).concat(filter));

		// isoBuffer.put(Constants.DE100, "04888000000");// need to change to
		// config once confirmed

		isoBuffer.put(Constants.DE100, hd.getRespDe100());
		// building p35 from p2 n p14
		StringBuilder track2 = new StringBuilder();
		track2.append(isoBuffer.get(Constants.DE2));
		track2.append("=");
		track2.append(isoBuffer.isFieldEmpty(Constants.DE14) ? "0000"
				: isoBuffer.get(Constants.DE14));
		track2.append(hd.getReqDe35());// service code
		track2.append("00000000");
		isoBuffer.put(Constants.DE35, track2.toString());
		/*
		 * Log.debug("p35 n p2 data", isoBuffer.get(Constants.DE35) +
		 * isoBuffer.get(Constants.DE2));
		 */
		isoBuffer.disableField(Constants.DE2);
		isoBuffer.disableField(Constants.DE57);
		
		String entryMode = hd.getReqDe22().substring(0, 2);
		isoBuffer.put(Constants.DE22, entryMode.concat("1"));
		
		/*if (isoBuffer.get(Constants.DE22).substring(0, 2)
				.equals(Constants.ENTRY_MODE_ICC_CL)) {
			isoBuffer.put(Constants.DE57, "310");
		} else {
			isoBuffer.put(Constants.DE57, "510"); // 310 contactless
		} */

	}

	@ModifyTransaction(value = Transactions.EMV_DEFAULT)
	public void modifyEmv(IsoBuffer isoBuffer, Data data) {
		modifyEmv(isoBuffer, ((HostData) data).getEmvMap());
	}

	private void modifyEmv(IsoBuffer isoBuffer, Map<EmvTags, String> emvMap) {
		Log.debug("EMV Tags and values ", emvMap.toString());
		StringBuilder d = new StringBuilder();
		Map<EmvTags, String> tvMap = new HashMap<EmvTags, String>();
		
		/*if (emvMap.containsKey(EmvTags.TAG_5F34))
			isoBuffer.put(Constants.DE23,
					Util.appendChar(tvMap.get(EmvTags.TAG_5F34), '0', 3, true));*/
		
		//Changed
		
		
		if (emvMap.containsKey(EmvTags.TAG_5F34))
			isoBuffer.put(Constants.DE23,
					Util.appendChar(emvMap.get(EmvTags.TAG_5F34), '0', 3, true));
		else
			isoBuffer.put(Constants.DE23, "000");
		
		if(isoBuffer.get(Constants.DE22).substring(0, 2).equals(Constants.ENTRY_MODE_ICC_CB)){
			String vale = emvMap.get(EmvTags.TAG_9F34);
			vale = vale.substring(4, 6); //last bit 
			boolean cvm = false;
			if(vale.equals("02")) //offline pin entered
				cvm = true;
			
			if(cvm){
				isoBuffer.put(Constants.DE52, "                ");
			}
		}
		
		/*if(!emvMap.containsKey(EmvTags.TAG_9F34)){
			if(isoBuffer.isFieldEmpty(Constants.DE52) || isoBuffer.get(Constants.DE52).equals("                ")){
				isoBuffer.disableField(Constants.DE52);
			}
		}*/
		//isoBuffer.put(Constants.DE52, "                ");
		
		for (EmvTags tag : emvMap.keySet()) {
			if (SUPPORTED_TAGS.contains(tag)) {
				tvMap.put(tag, emvMap.get(tag));
			}
		}
		// Log.debug("new tv map::", tvMap.toString());
		for (EmvTags tag : tvMap.keySet()) {
			d.append(tag.toString());
			String val = tvMap.get(tag);
			d.append(Util.appendChar(
					String.valueOf(Integer.toHexString(val.length() / 2)), '0',
					2, true));
			d.append(val);

		}
		// Log.debug("string buffer emv data :::::::::::::", d.toString());
		isoBuffer.put(Constants.DE55, d.toString());
		// Log.debug("EMV Data 55::::::::::",
		// IsoUtil.asciiChar2hex(isoBuffer.get(Constants.DE55)));
	}

	@ModifyTransaction(value = Transactions.CUT_OVER)
	public void modifyCutOver(IsoBuffer buffer, Data data) {

		// Log.debug("inside cutover response method", buffer.toString());
		if (buffer.get(Constants.ISO_MSG_TYPE).equals(
				Constants.MSG_TYPE_NETWORK_MANAGEMENT)) {
			buffer.put(Constants.ISO_MSG_TYPE, "0810");
		} else if (buffer.get(Constants.ISO_MSG_TYPE).equals(
				Constants.MSG_TYPE_NETWORK_MANAGEMENT_ADVICE)
				|| buffer.get(Constants.ISO_MSG_TYPE).equals(
						Constants.MSG_TYPE_NETWORK_MANAGEMENT_REPEAT)) {
			buffer.put(Constants.ISO_MSG_TYPE, "0830");
		}

		buffer.put(Constants.DE1, "");
		buffer.put(Constants.DE39, "00");
		buffer.disableField(Constants.DE55);
	}

	@ModifyTransaction(value = Transactions.RECON)
	public void modifyReconciliation(IsoBuffer buffer, Data data) {

		// Log.debug("inside recon response method", buffer.toString());

		buffer.put(Constants.DE1, "");
		if (buffer.get(Constants.ISO_MSG_TYPE).equals(
				Constants.MSG_TYPE_SETTLEMENT)) {
			buffer.put(Constants.ISO_MSG_TYPE, "0510");

		} else if (buffer.get(Constants.ISO_MSG_TYPE).equals(
				Constants.MSG_TYPE_ACQ_RECON_ADVICE)) {
			buffer.put(Constants.ISO_MSG_TYPE, "0530");

		} else if (buffer.get(Constants.ISO_MSG_TYPE).equals(
				Constants.MSG_TYPE_ACQ_RECON_REPEAT)) {
			buffer.put(Constants.ISO_MSG_TYPE, "0530");

		} else if (buffer.get(Constants.ISO_MSG_TYPE).equals(
				Constants.MSG_TYPE_CARD_ISSUER_RECON)) {
			buffer.put(Constants.ISO_MSG_TYPE, "0512");

		} else if (buffer.get(Constants.ISO_MSG_TYPE).equals(
				Constants.MSG_TYPE_ISSUER_RECON_ADVICE)) {
			buffer.put(Constants.ISO_MSG_TYPE, "0532");

		} else if (buffer.get(Constants.ISO_MSG_TYPE).equals(
				Constants.MSG_TYPE_ISSUER_RECON_REPEAT)) {
			buffer.put(Constants.ISO_MSG_TYPE, "0532");
		}
		buffer.put(Constants.DE66, "9"); // Message received but will not be
											// checked against totals at the
											// present time. This response code
											// will always be expected by
											// BENEFIT, since reconciliation
											// totals are not validated on-line.

		buffer.disableField(Constants.DE55);
		buffer.disableField(Constants.DE74);
		buffer.disableField(Constants.DE75);
		buffer.disableField(Constants.DE76);
		buffer.disableField(Constants.DE77);
		buffer.disableField(Constants.DE78);
		buffer.disableField(Constants.DE79);
		buffer.disableField(Constants.DE80);
		buffer.disableField(Constants.DE81);
		buffer.disableField(Constants.DE86);
		buffer.disableField(Constants.DE87);
		buffer.disableField(Constants.DE88);
		buffer.disableField(Constants.DE89);
		buffer.disableField(Constants.DE97);
	}

}
