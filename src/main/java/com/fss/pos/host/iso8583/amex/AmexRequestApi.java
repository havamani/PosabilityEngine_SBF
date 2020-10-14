package com.fss.pos.host.iso8583.amex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.fss.pos.base.api.host.HostData;
import com.fss.pos.base.api.host.HostRequest;
import com.fss.pos.base.api.transactions.ModifyTransaction;
import com.fss.pos.base.api.transactions.Transactions;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.EmvTags;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.base.services.transactionlog.TransactionLogData;

@HostRequest(HostType = "20")
public class AmexRequestApi extends AmexApi {

	private static final List<EmvTags> SUPPORTED_TAGS;
	static {
		SUPPORTED_TAGS = new ArrayList<EmvTags>();
		SUPPORTED_TAGS.add(EmvTags.TAG_9F26);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F10);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F37);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F36);
		SUPPORTED_TAGS.add(EmvTags.TAG_95);
		SUPPORTED_TAGS.add(EmvTags.TAG_9A);
		SUPPORTED_TAGS.add(EmvTags.TAG_9C);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F02);
		SUPPORTED_TAGS.add(EmvTags.TAG_82);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F03);
		SUPPORTED_TAGS.add(EmvTags.TAG_5F34);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F27);

	}

	public static String genRRN(int len) {
		len = len / 2;
		Random random = new Random();
		char[] digits = new char[len];
		digits[0] = (char) (random.nextInt(9) + '1');
		for (int i = 1; i < len; i++)
			digits[i] = (char) (random.nextInt(10) + '0');
		StringBuilder sb = new StringBuilder(new String(digits));
		char[] digits1 = new char[len];
		digits1[0] = (char) (random.nextInt(9) + '1');
		for (int i = 1; i < len; i++)
			digits1[i] = (char) (random.nextInt(10) + '0');
		sb.append(new String(digits1));
		return sb.toString();
	}

	@ModifyTransaction(value = Transactions.DEFAULT)
	public void modifyAll(IsoBuffer buffer, Data data) throws Exception {
		HostData hostData = (HostData) data;

		buffer.fillBuffer(false, true, true);
		// String Msgtyp = buffer.get(AmexConstants.MSG_TYP);
		String p12Value = buffer.get(Constants.DE7);
		buffer.put(Constants.DE12, Util.getDateTimeInGMT("YY") + p12Value);
		// buffer.put(Constants.DE19, "048");
		buffer.put(Constants.DE19,
				Util.appendChar(hostData.getCurrencyCode(), '0', 3, true));
		String entryModeValue = buffer.get(Constants.DE22).substring(0, 2);

		String de22value = fillde22(entryModeValue, buffer);

		// buffer.put(Constants.DE26, "0742"); // for amex mcc is in 26
		buffer.put(Constants.DE26, hostData.getMcc());
		// buffer.put(Constants.DE32, "10000000076");
		buffer.put(Constants.DE32, hostData.getAcqInstId());
		buffer.put(Constants.DE35, buffer.get(Constants.DE35).replace("F", "=")); // not
																					// sure
		buffer.put(Constants.DE37, hostData.getRrn());
		buffer.put(Constants.DE41, hostData.getTerminalId());
		// buffer.put(Constants.DE42, "9769545450     "); // as per mail for local
		// buffer.put(Constants.DE42, "9764097069     "); // for afs setup
		StringBuilder p42Data= new StringBuilder();
		p42Data.append(Util.appendChar(hostData.getMerchantId().trim(), ' ', 15, true));
		buffer.put(Constants.DE42,p42Data.toString());
		// String Name = hostData.getBusinessName().replace(" ", "~");
		StringBuilder p43Data= new StringBuilder();
		p43Data.append(hostData.getBusinessName()).append("\\");
		p43Data.append(hostData.getMerchantAddress()).append("\\");
		p43Data.append(hostData.getCity()).append("\\");
		p43Data.append(Util.appendChar(hostData.getZipCode(), '~', 10, false));
		p43Data.append(Util.appendChar(hostData.getState(), '~', 3, false));
		p43Data.append(Util.appendChar(hostData.getCurrencyCode(), '0', 3, true));
		
		buffer.put(Constants.DE43, p43Data.toString());

		/*
		 * String currencyCode = Util.appendChar(hostData.getCurrencyCode(),
		 * '0', 3, true);
		 */
		// buffer.put(Constants.DE49, "048");
		buffer.put(Constants.DE49,
				Util.appendChar(hostData.getCurrencyCode(), '0', 3, true));

		if (Constants.ZERO.equals(hostData.getTxnCommunicationFlow())) {
			;
		}

	}

	private String fillde22(String entryModeValue, IsoBuffer buffer) {

		if(buffer.isFieldEmpty(Constants.DE52)){
			if (entryModeValue.equals("01")) {// manual entry
				buffer.put(Constants.DE22, "610101654041");
			} else if (entryModeValue.equals("02")) {// magstripe
				buffer.put(Constants.DE22, "500101254041");
			} else if (entryModeValue.equals("07")) {// contact less
				if(buffer.isFieldEmpty(Constants.DE55)){
					buffer.put(Constants.DE22, "51110X251041");
					}else{
					buffer.put(Constants.DE22, "51010X551041");	
					}
			} else if (entryModeValue.equals("09")) {// contact less
				if(buffer.isFieldEmpty(Constants.DE55)){
				buffer.put(Constants.DE22, "51110X251041");
				}else{
				buffer.put(Constants.DE22, "51010X551041");	
				}
			} else if (entryModeValue.equals("80")) { // fall back
				buffer.put(Constants.DE22, "510101951041");
			}
			/*else if (entryModeValue.equals("05")) {// chip contact based
				buffer.put(Constants.DE22, "5101015 1 1041");
			}*/
		}else{
			if (entryModeValue.equals("01")) {// manual entry
				buffer.put(Constants.DE22, "610101614041");
			} else if (entryModeValue.equals("02")) {// magstripe 
				buffer.put(Constants.DE22, "500101215041");
//				buffer.put(Constants.DE22, "500101115041"); 
			} else if (entryModeValue.equals("05")) {// chip contact based
				buffer.put(Constants.DE22, "510101511041");
			} else if (entryModeValue.equals("07")) {// contact less
				if(buffer.isFieldEmpty(Constants.DE55)){
					buffer.put(Constants.DE22, "51110X211041");
					}else{
					buffer.put(Constants.DE22, "51010X511041");	
					}
			} else if (entryModeValue.equals("09")) {// contact less
				if(buffer.isFieldEmpty(Constants.DE55)){
					buffer.put(Constants.DE22, "51110X211041");
					}else{
					buffer.put(Constants.DE22, "51010X511041");	
					}
			} else if (entryModeValue.equals("80")) { // fall back
				buffer.put(Constants.DE22, "510101915041");
			}
		}
		return null;
	}

	@ModifyTransaction(value = Transactions.PURCHASE)
	public void modifyPurchase(IsoBuffer buffer, Data data) {

		buffer.put(AmexConstants.MSG_TYP, "1100");

		// String entryModeValue = buffer.get(Constants.DE22).substring(6,7);

		buffer.put(Constants.DE3, "004000");
		buffer.put(Constants.DE24, "100"); // enabled newly
		buffer.put(Constants.DE25, "4000");
		// buffer.disableField(Constants.DE7);
		buffer.disableField(Constants.DE13);
		buffer.disableField(Constants.DE23);
		//buffer.disableField(Constants.DE25);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE63);
		buffer.disableField(Constants.DE44);
        buffer.disableField(Constants.DE48);
        buffer.disableField(Constants.DE54);
        buffer.disableField(Constants.DE60);
	}

	@ModifyTransaction(value = Transactions.PRE_AUTHORIZATION)
	public void modifyPreAuth(IsoBuffer buffer, Data data) {

		buffer.put(AmexConstants.MSG_TYP, "1100");

		// String entryModeValue = buffer.get(Constants.DE22).substring(6,7);

		buffer.put(Constants.DE3, "004000");
		// buffer.put(Constants.DE24, "100");
		// buffer.disableField(Constants.DE7);
		buffer.disableField(Constants.DE13);
		buffer.disableField(Constants.DE23);
		buffer.disableField(Constants.DE25);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE63);
		buffer.disableField(Constants.DE44);
		buffer.disableField(Constants.DE48);
	}

	@ModifyTransaction(value = Transactions.COMPLETION)
	public void modifyCompletion(IsoBuffer buffer, Data data) {

		buffer.put(AmexConstants.MSG_TYP, "1100");

		// String entryModeValue = buffer.get(Constants.DE22).substring(6,7);

		buffer.put(Constants.DE3, "004000");
		// buffer.put(Constants.DE24, "100");
		// buffer.disableField(Constants.DE7);
		buffer.disableField(Constants.DE13);
		buffer.disableField(Constants.DE23);
		buffer.disableField(Constants.DE25);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE63);
		buffer.disableField(Constants.DE44);

	}

	@ModifyTransaction(value = Transactions.TIP)
	public void modifyTipAdjustment(IsoBuffer buffer, Data data) {

		buffer.put(AmexConstants.MSG_TYP, "1100");

		// String entryModeValue = buffer.get(Constants.DE22).substring(6,7);

		buffer.put(Constants.DE3, "004000");
		// buffer.put(Constants.DE24, "100");
		// buffer.disableField(Constants.DE7);
		buffer.disableField(Constants.DE13);
		buffer.disableField(Constants.DE23);
		//buffer.disableField(Constants.DE25);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE63);
		buffer.disableField(Constants.DE44);
		buffer.disableField(Constants.DE54);

	}
	@ModifyTransaction(value = Transactions.REFUND)
	public void modifyRefund(IsoBuffer buffer, Data data) {

		buffer.put(AmexConstants.MSG_TYP, "1100");

		// String entryModeValue = buffer.get(Constants.DE22).substring(6,7);

		buffer.put(Constants.DE3, "004000");
		// buffer.put(Constants.DE24, "100");
		// buffer.disableField(Constants.DE7);
		buffer.disableField(Constants.DE13);
		buffer.disableField(Constants.DE23);
		//buffer.disableField(Constants.DE25);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE63);
		buffer.disableField(Constants.DE44);

	}


	/*
	 * @ModifyTransaction(value = Transactions.REFUND) public void
	 * modifyRefund(IsoBuffer buffer, Data data) {
	 * 
	 * HostData hd = (HostData) data; buffer.put(Constants.DE3, value);
	 * buffer.put(Constants.DE22, value); buffer.put(Constants.DE26, value);
	 * buffer.disableField(Constants.DE37); buffer.put(Constants.DE38, value);
	 * buffer.put(Constants.DE39, value); buffer.put(Constants.DE42, value);
	 * buffer.put(Constants.DE56, value); }
	 */

	@ModifyTransaction(value = Transactions.VOID)
	public void modifyVoid(IsoBuffer buffer, Data data) {

		HostData hd = (HostData) data;

		buffer.put(AmexConstants.MSG_TYP, "1420");
		buffer.put(Constants.DE3, "004000");
		//buffer.put(Constants.DE11,hd.getStan());
		//buffer.put(Constants.DE22, hd.getReqDe22());
		buffer.put(Constants.DE24, "400"); // as per doc
		buffer.put(Constants.DE25, "4000"); // as per doc
		//buffer.put(Constants.DE31, hd.getRespDe31());
		// buffer.put(Constants.DE32, "10000000076");
		// buffer.put(Constants.DE37, hd.getRrn());
		//buffer.put(Constants.DE33, hd.getOrgFiid());
		buffer.put(Constants.DE38, hd.getOrgAuthCode());
		// buffer.put(Constants.DE42, "9764097069     ");
		// buffer.put(Constants.DE49, "048");
		StringBuilder s56 = new StringBuilder("1100");
		s56.append(hd.getOrgStan());
		s56.append(hd.getOrgTransmissionDY());
		// s56.append("1110000000076");// de32 value with length
		s56.append(
				Util.appendChar(String.valueOf(hd.getAcqInstId().length()), '0', 2, true));
		s56.append(hd.getAcqInstId());
		buffer.put(Constants.DE56, s56.toString());
		buffer.disableField(Constants.DE43);
		buffer.disableField(Constants.DE44);
		buffer.disableField(Constants.DE13);
		buffer.disableField(Constants.DE24);
		buffer.disableField(Constants.DE38);
		buffer.disableField(Constants.DE31);
		//buffer.disableField(Constants.DE14);
		//buffer.disableField(Constants.DE19);
		//buffer.disableField(Constants.DE22);
		//buffer.disableField(Constants.DE26);
		//buffer.disableField(Constants.DE41);
		buffer.disableField(Constants.DE60);
		buffer.disableField(Constants.DE61);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE7);
	}

	@ModifyTransaction(value = Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer buffer, Data data) throws Exception {
		TransactionLogData trd = (TransactionLogData) data;

		buffer.put(AmexConstants.MSG_TYP, "1420");
		String p12Value = buffer.get(Constants.DE7);
		buffer.put(Constants.DE12, Util.getDateTimeInGMT("YY") + p12Value);
		// buffer.put(Constants.DE19, "048");
		buffer.put(Constants.DE19,
				Util.appendChar(trd.getCurrencyCode(), '0', 3, true));
		String entryModeValue = buffer.get(Constants.DE22).substring(0, 2);

		if (entryModeValue.equals("01")) {// manual entry
			buffer.put(Constants.DE22, "610101614041");
		} else if (entryModeValue.equals("02")) {// magstripe
			buffer.put(Constants.DE22, "500101254041");
		} else if (entryModeValue.equals("05")) {// chip contact based
			buffer.put(Constants.DE22, "510101511041");
		}  else if (entryModeValue.equals("07")) {// contact less
			if(buffer.isFieldEmpty(Constants.DE55)){
				buffer.put(Constants.DE22, "51110X211041");
				}else{
				buffer.put(Constants.DE22, "51010X511041");	
				}
		} else if (entryModeValue.equals("09")) {// contact less
			if(buffer.isFieldEmpty(Constants.DE55)){
				buffer.put(Constants.DE22, "51110X211041");
				}else{
				buffer.put(Constants.DE22, "51010X511041");	
				}
		}else if (entryModeValue.equals("80")) { // fall back
			buffer.put(Constants.DE22, "510101951041");
		}
		buffer.put(Constants.DE3, "004000");
		buffer.put(Constants.DE24, "400"); // as per doc
		buffer.put(Constants.DE25, "4007"); // as per doc
		// buffer.put(Constants.DE26, "0742");
		buffer.put(Constants.DE26, trd.getMcc());
		// buffer.put(Constants.DE32, "10000000076");
		/*if (Util.isNullOrEmpty(trd.getRespDe31())){
			buffer.disableField(Constants.DE31);
		}else{
		buffer.put(Constants.DE31, trd.getRespDe31());
		}*/
		buffer.put(Constants.DE32, trd.getAcqInstId());
		buffer.put(Constants.DE37, trd.getRrn());
		if (Util.isNullOrEmpty(trd.getOrgAuthCode())){
			buffer.disableField(Constants.DE38);
		}else{
			buffer.put(Constants.DE38, trd.getOrgAuthCode());
		}
		// buffer.put(Constants.DE42, "9764097069     ");
		buffer.put(Constants.DE42,
				Util.appendChar(trd.getMerchantId().trim(), ' ', 15, true));
		// buffer.put(Constants.DE49, "048");
		buffer.put(Constants.DE49,
				Util.appendChar(trd.getCurrencyCode(), '0', 3, true));
		StringBuilder s56 = new StringBuilder("1100");
		s56.append(trd.getOrgStan());
		s56.append(trd.getOrgTransmissionDY());
		// s56.append("1110000000076"); // de32 value with length
		s56.append(
				Util.appendChar(String.valueOf(trd.getAcqInstId().length()), '0', 2, true));
		s56.append(trd.getAcqInstId());
		buffer.put(Constants.DE56, s56.toString());
		buffer.disableField(Constants.DE43);
		buffer.disableField(Constants.DE44);
		buffer.disableField(Constants.DE48);
		buffer.disableField(Constants.DE13);
		buffer.disableField(Constants.DE23);
		buffer.disableField(Constants.DE24);
		buffer.disableField(Constants.DE31);
		buffer.disableField(Constants.DE38);
		//buffer.disableField(Constants.DE14);
		//buffer.disableField(Constants.DE19);
		//buffer.disableField(Constants.DE22);
		//buffer.disableField(Constants.DE26);
		//buffer.disableField(Constants.DE41);
		buffer.disableField(Constants.DE60);
		buffer.disableField(Constants.DE61);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE7);
		buffer.disableField(Constants.DE55);
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

		// Log.debug("emv data in method", tvMap.toString());
		StringBuilder emvDataBuffer = new StringBuilder();

		emvDataBuffer.append("C1C7D5E2");
		emvDataBuffer.append("0001");
		emvDataBuffer.append(tvMap.get(EmvTags.TAG_9F26));
		// emvDataBuffer.append(ISOUtil.binary2hex(IsoUtil.DecimalToBinary((tvMap.get(EmvTags.TAG_9F10).length()
		// / 2))));
		// emvDataBuffer.append(Integer.toHexString(tvMap.get(EmvTags.TAG_9F10).length()
		// / 2));
		String val = tvMap.get(EmvTags.TAG_9F10);
		emvDataBuffer.append(Util.appendChar(
				String.valueOf(Integer.toHexString(val.length() / 2)), '0', 2,
				true) + tvMap.get(EmvTags.TAG_9F10));
		emvDataBuffer.append(tvMap.get(EmvTags.TAG_9F37));
		emvDataBuffer.append(tvMap.get(EmvTags.TAG_9F36));
		emvDataBuffer.append(tvMap.get(EmvTags.TAG_95));
		emvDataBuffer.append(tvMap.get(EmvTags.TAG_9A));
		emvDataBuffer.append(tvMap.get(EmvTags.TAG_9C));
		emvDataBuffer.append(tvMap.get(EmvTags.TAG_9F02));
		emvDataBuffer.append(tvMap.get(EmvTags.TAG_5F2A));
		emvDataBuffer.append(tvMap.get(EmvTags.TAG_9F1A));
		emvDataBuffer.append(tvMap.get(EmvTags.TAG_82));
		emvDataBuffer.append("000000000000");
		emvDataBuffer.append(tvMap.get(EmvTags.TAG_5F34));
		emvDataBuffer.append(tvMap.get(EmvTags.TAG_9F27));

		buffer.put(Constants.DE55, emvDataBuffer.toString());

		if (tvMap.get(EmvTags.TAG_9F34).substring(0, 2).equalsIgnoreCase("5E")) {
			buffer.put(Constants.DE22, "510101551041"); // signature
		} else {
			buffer.put(Constants.DE22, "510101511041"); // pin
		}

	}
}
