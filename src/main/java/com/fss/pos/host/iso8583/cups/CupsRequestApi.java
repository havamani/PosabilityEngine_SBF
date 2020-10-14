package com.fss.pos.host.iso8583.cups;

import java.util.ArrayList;
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

@HostRequest(HostType = "21")
public class CupsRequestApi extends CupsApi {

	private static final List<EmvTags> SUPPORTED_TAGS;
	private static final String Max_Num_Pin = "06";
	private static final String DE_25 = "06";
	private static final String Proc_code="03";
	private static final String Void_proc_code="20";
	private static final String Mag_Stripe = "80";
	//private static final string 

	static {
		SUPPORTED_TAGS = new ArrayList<EmvTags>();

		SUPPORTED_TAGS.add(EmvTags.TAG_9F26);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F27);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F10);
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
		SUPPORTED_TAGS.add(EmvTags.TAG_9F34);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F35);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F1E);
		SUPPORTED_TAGS.add(EmvTags.TAG_84);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F09);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F41);
		SUPPORTED_TAGS.add(EmvTags.TAG_9F63);
	}

	@ModifyTransaction(value = Transactions.DEFAULT)
	public void modifyAll(IsoBuffer buffer, Data data) {

		HostData hostData = (HostData) data;
		buffer.put(Constants.DE18, hostData.getMcc());
		buffer.put(Constants.DE19, Util.appendChar(hostData.getCurrencyCode(), '0', 3, true));
		
		if(buffer.get(Constants.DE22).substring(0, 2).toString().equals("80"))
		{

			buffer.put(Constants.DE22,
					"02" + buffer.get(Constants.DE22).substring(2));
		}

		if (!buffer.isFieldEmpty(Constants.DE52))
		{
			buffer.put(Constants.DE22,
					buffer.get(Constants.DE22).substring(0, 2)+"1");
			buffer.put(Constants.DE26, Max_Num_Pin);
			buffer.put(Constants.DE53, "2600000000000000");
		}
		else
		{
			buffer.put(Constants.DE22,
					buffer.get(Constants.DE22).substring(0, 2)+"2");
		}
		buffer.put(Constants.DE32, hostData.getAcqInstId());
		buffer.put(Constants.DE33, hostData.getFiid()); 
		buffer.put(Constants.DE37,hostData.getRrn());
		buffer.put(Constants.DE41,Util.appendChar(hostData.getTerminalId(), ' ', 8,
				false));

		buffer.put(Constants.DE42, Util.appendChar(hostData.getMerchantId(), ' ', 15,
				false));
		StringBuilder de43= new StringBuilder();
		de43.append(Util.appendChar(hostData.getBusinessName(), ' ', 25,
				false));
		de43.append(Util.appendChar(hostData.getCity(), ' ', 12, false));
		de43.append(Util.appendChar(hostData.getCurrencyCode(), ' ', 3, false));
		
		buffer.put(Constants.DE43, de43.toString());
		buffer.put(Constants.DE49,
				Util.appendChar(hostData.getCurrencyCode(), '0', 3, true));
	}

	@ModifyTransaction(value = Transactions.PURCHASE)
	public void modifyPurchace(IsoBuffer buffer, Data data) {
		StringBuffer field60 = new StringBuffer();
		String msgResCode = "0000";
		String Field2 = getBuildP60_2(buffer);
		String Field3 = getBuildP60_3(buffer);
		field60.append(msgResCode);
		field60.append(Field2);
		field60.append(Field3);
		buffer.put(Constants.DE3,"000000");
		buffer.put(Constants.DE60, field60.toString());
		buffer.disableField(Constants.DE44);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE63);
		buffer.disableField(Constants.DE61);
	}

	@ModifyTransaction(value = Transactions.PRE_AUTHORIZATION)
	public void modifyPre_auth(IsoBuffer buffer, Data data) {

		buffer.put(Constants.DE3,
				"03" + buffer.get(Constants.DE3).substring(2, 6));
		buffer.put(Constants.DE25, DE_25);
		StringBuffer field60 = new StringBuffer();
		String msgResCode = "0000";
		String Field2 = getBuildP60_2(buffer);
		String Field3 = getBuildP60_3(buffer);
		field60.append(msgResCode);
		field60.append(Field2);
		field60.append(Field3);

		buffer.put(Constants.DE60, field60.toString());
		buffer.disableField(Constants.DE44);
		buffer.disableField(Constants.DE55);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE63);
	}

	@ModifyTransaction(value = Transactions.COMPLETION)
	public void modifyCompletion(IsoBuffer buffer, Data data) throws Exception {
		HostData hd = (HostData) data;
		buffer.put(Constants.DE25, DE_25);

		if (null != hd.getOrgAuthCode()
				&& !Constants.ZERO.equals(hd.getOrgAuthCode()))
			buffer.put(Constants.DE38, hd.getOrgAuthCode());
		StringBuffer field60 = new StringBuffer();
		String msgResCode = "0000";
		String Field2 = getBuildP60_2(buffer);
		String Field3 = getBuildP60_3(buffer);
		field60.append(msgResCode);
		field60.append(Field2);
		field60.append(Field3);
		buffer.put(Constants.DE60, field60.toString());
		buffer.disableField(Constants.DE44);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE63);

	}

	@ModifyTransaction(value = Transactions.VOID)
	public void modifyVoid(IsoBuffer buffer, Data data) {
		HostData hd = (HostData) data;
		if((hd.getOrgProcCode().substring(0,2).equals(Proc_code))&&(hd.getReqDe25().equals(DE_25))&&(hd.getOrgMessageType().equals(Constants.MSG_TYPE_AUTH))) //pre-auth void
		{   
			buffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_AUTH);
			buffer.put(Constants.DE3,                                                                                           
					"20" + buffer.get(Constants.DE3).substring(2, 6)); 
			buffer.put(Constants.DE25,hd.getReqDe25());
		}
		else if((hd.getOrgProcCode().substring(0,2).equals(Constants.CONDITION_CODE_SALE))&&(hd.getReqDe25().equals(DE_25))&&(hd.getOrgMessageType().equals(Constants.MSG_TYPE_TXN)))//pre-auth completion void
		{
			buffer.put(Constants.DE3,
					"20" + buffer.get(Constants.DE3).substring(2, 6)); 
			buffer.put(Constants.DE25,hd.getReqDe25());
		}
		else
		{
			buffer.put(Constants.DE3,                                                   
					"20" + buffer.get(Constants.DE3).substring(2, 6));                                 //sale void
			buffer.put(Constants.DE25,hd.getReqDe25());
		}

		if (null != hd.getOrgAuthCode()
				&& !Constants.ZERO.equals(hd.getOrgAuthCode()))
			buffer.put(Constants.DE38, hd.getOrgAuthCode());

		StringBuffer field60 = new StringBuffer();
		String msgResCode = "0000";
		String Field2 = getBuildP60_2(buffer);
		String Field3 = getBuildP60_3(buffer);
		field60.append(msgResCode);
		field60.append(Field2);
		field60.append(Field3);

		buffer.put(Constants.DE60, field60.toString());
		StringBuilder s90 = new StringBuilder(hd.getOrgMessageType());
		s90.append(hd.getOrgStan());
		s90.append(hd.getOrgTransmissionDt());
		s90.append(Util.appendChar(hd.getAcqInstId(), '0', 11, true));
		s90.append(Util.appendChar(hd.getFiid(), '0', 11, true));// 38210048
		buffer.put(Constants.DE90, s90.toString());
		buffer.disableField(Constants.DE44);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE63);
		buffer.disableField(Constants.DE14);

	}

	@ModifyTransaction(value = Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer buffer, Data data) {

		TransactionLogData logData = (TransactionLogData) data;
		buffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_REV_420);
		buffer.put(Constants.DE3, logData.getOrgProcCode());
		buffer.put(Constants.DE7,  logData.getTxnDateTime());
		buffer.put(Constants.DE11, logData.getStan());
		buffer.put(Constants.DE12, logData.getMerchantTime());
		buffer.put(Constants.DE13, logData.getMerchantDate());
		buffer.put(Constants.DE18, logData.getMcc());
		buffer.put(Constants.DE19, logData.getCurrencyCode());

		if (!buffer.isFieldEmpty(Constants.DE52))
		{
			buffer.put(Constants.DE22,
					buffer.get(Constants.DE22).substring(0, 2)+"1");
		}
		else
		{
			buffer.put(Constants.DE22,
					buffer.get(Constants.DE22).substring(0, 2)+"2");
		}
		buffer.put(Constants.DE25,logData.getReqDe25());
		buffer.put(Constants.DE32, logData.getAcqInstId());
		buffer.put(Constants.DE33, logData.getFiid()); // 38210048
		buffer.put(Constants.DE37,logData.getRrn());
		buffer.put(Constants.DE41,Util.appendChar(logData.getTerminalId(), ' ', 8,
				false));
		buffer.put(Constants.DE42, Util.appendChar(logData.getMerchantId(), ' ', 15,
				false));
		if (null != logData.getOrgAuthCode()
				&& !Constants.ZERO.equals(logData.getOrgAuthCode()))
			buffer.put(Constants.DE38, logData.getOrgAuthCode());

		StringBuilder de43= new StringBuilder();
		de43.append(Util.appendChar(logData.getBusinessName(), ' ', 25,
				false));
		de43.append(Util.appendChar(logData.getCity(), ' ', 12, false));
		de43.append(Util.appendChar(logData.getCurrencyCode(), ' ', 3, false));

		buffer.put(Constants.DE43, de43.toString());
		buffer.put(Constants.DE49,
				Util.appendChar(logData.getCurrencyCode(), '0', 3, true));

		if (buffer.get(Constants.DE22).substring(0, 2).toString().equals("05")) {
			modifyEmv(buffer, data);

		}
		
		StringBuilder s90 = new StringBuilder(logData.getOriginalMsgType());
		s90.append(logData.getOrgStan());
		s90.append(logData.getOrgTransmissionDt());
		s90.append(Util.appendChar(logData.getAcqInstId(), '0', 11, true));
		s90.append(Util.appendChar(logData.getFiid(), '0', 11, true)); // 38210048
		buffer.put(Constants.DE90, s90.toString());
		StringBuffer field60 = new StringBuffer();
		String msgResCode = "4021";
		String Field2 = getBuildP60_2(buffer);
		String Field3 = getBuildP60_3(buffer);
		field60.append(msgResCode);
		field60.append(Field2);
		field60.append(Field3);

		buffer.put(Constants.DE60, field60.toString());
		if(buffer.get(Constants.DE22).substring(0, 2).toString().equals("80"))
		{

			buffer.put(Constants.DE22,
					"02" + buffer.get(Constants.DE22).substring(2));
		}

		buffer.disableField(Constants.DE14);
		buffer.disableField(Constants.DE35);
		buffer.disableField(Constants.DE44);
		buffer.disableField(Constants.DE52);
		buffer.disableField(Constants.DE62);
		buffer.disableField(Constants.DE63);

	}

	//	@ModifyTransaction(value = Transactions.REFUND)
	//	public void modifyRefund(IsoBuffer buffer, Data data) {
	//
	//		buffer.put(Constants.ISO_MSG_TYPE, Constants.MSG_TYPE_OFFLINE);
	//		//	buffer.put(Constants.DE22, buffer.get(Constants.DE22).substring(0, 2)+"0");
	//		StringBuffer field60 = new StringBuffer();
	//		String msgResCode = "0000";
	//		String Field2 = getBuildP60_2(buffer);
	//		String Field3 = getBuildP60_3(buffer);
	//		field60.append(msgResCode);
	//		field60.append(Field2);
	//		field60.append(Field3);
	//		buffer.put(Constants.DE60, field60.toString());
	//		
	//		buffer.disableField(Constants.DE14);
	//		buffer.disableField(Constants.DE26);
	//		buffer.disableField(Constants.DE44);
	//		buffer.disableField(Constants.DE53);
	//		buffer.disableField(Constants.DE52);
	//		buffer.disableField(Constants.DE62);
	//		buffer.disableField(Constants.DE63);
	//	}


	@ModifyTransaction(value = Transactions.CUT_OVER)
	public void modifyCutOver(IsoBuffer buffer, Data data) {

		Log.debug("inside cutover response method", buffer.toString());
		if(buffer.get(Constants.ISO_MSG_TYPE).equals(Constants.MSG_TYPE_NETWORK_MANAGEMENT)){
			buffer.put(Constants.ISO_MSG_TYPE, "0810");
		}else if(buffer.get(Constants.ISO_MSG_TYPE).equals(Constants.MSG_TYPE_NETWORK_MANAGEMENT_ADVICE) ||
				buffer.get(Constants.ISO_MSG_TYPE).equals(Constants.MSG_TYPE_NETWORK_MANAGEMENT_REPEAT)){
			buffer.put(Constants.ISO_MSG_TYPE, "0830");
		}

		buffer.put(Constants.DE1, "");
		buffer.put(Constants.DE39, "00");
	}
	@ModifyTransaction(value = Transactions.EMV_DEFAULT)
	public void modifyEmv(IsoBuffer isoBuffer, Data data) {

		StringBuilder d = new StringBuilder();

		if (isoBuffer.get(Constants.ISO_MSG_TYPE).equals(
				Constants.MSG_TYPE_REV_420)) {
			TransactionLogData hd = (TransactionLogData) data;
			Map<EmvTags, String> tvMap = hd.getEmvMap();
			String cardSeqNo = tvMap.get(EmvTags.TAG_5F34);
			isoBuffer.put(Constants.DE23,
					Util.appendChar(cardSeqNo, '0', 3, true));

			tvMap.remove(EmvTags.TAG_5F34);
			tvMap.remove(EmvTags.TAG_84);
			tvMap.remove(EmvTags.TAG_9F26);
			tvMap.remove(EmvTags.TAG_9F27);
			tvMap.remove(EmvTags.TAG_9F37);
			tvMap.remove(EmvTags.TAG_9A);
			tvMap.remove(EmvTags.TAG_9C);
			tvMap.remove(EmvTags.TAG_9F02);
			tvMap.remove(EmvTags.TAG_5F2A);
			tvMap.remove(EmvTags.TAG_9F1A);
			tvMap.remove(EmvTags.TAG_9F03);
			tvMap.remove(EmvTags.TAG_9F33);
			tvMap.remove(EmvTags.TAG_9F34);
			tvMap.remove(EmvTags.TAG_9F35);
			tvMap.remove(EmvTags.TAG_9F09);
			tvMap.remove(EmvTags.TAG_82);
			tvMap.remove(EmvTags.TAG_9F41);
			for (EmvTags tag : tvMap.keySet()) {
				if (SUPPORTED_TAGS.contains(tag)) {
					d.append(tag.toString());
					String val = tvMap.get(tag);
					d.append(Util.appendChar(String.valueOf(Integer
							.toHexString(val.length() / 2)), '0', 2, true));
					d.append(val);
				}
			}
		}else {
			HostData hd = (HostData) data;
			Map<EmvTags, String> tvMap = hd.getEmvMap();
			String cardSeqNo = tvMap.get(EmvTags.TAG_5F34);
			isoBuffer.put(Constants.DE23,
					Util.appendChar(cardSeqNo, '0', 3, true));

			tvMap.remove(EmvTags.TAG_5F34);
			d = new StringBuilder();
			for (EmvTags tag : tvMap.keySet()) {
				if (SUPPORTED_TAGS.contains(tag)) {
					d.append(tag.toString());
					String val = tvMap.get(tag);
					d.append(Util.appendChar(String.valueOf(Integer
							.toHexString(val.length() / 2)), '0', 2, true));
					d.append(val);
				}
			}
		}
		if ((isoBuffer.get(Constants.ISO_MSG_TYPE).equals(
				Constants.MSG_TYPE_TXN)) && (isoBuffer.get(Constants.DE3).substring(0, 2).equals(Void_proc_code)) && (isoBuffer.get(Constants.DE25).equals(Constants.CONDITION_CODE_SALE))//sale void
				|| (isoBuffer.get(Constants.ISO_MSG_TYPE).equals(Constants.MSG_TYPE_AUTH))&&(isoBuffer.get(Constants.DE3).substring(0, 2).equals(Void_proc_code))&& (isoBuffer.get(Constants.DE25).equals(DE_25))//pre-Auth void
				||	(isoBuffer.get(Constants.ISO_MSG_TYPE).equals(Constants.MSG_TYPE_TXN))&&(isoBuffer.get(Constants.DE3).substring(0, 2).equals(Void_proc_code)) && (isoBuffer.get(Constants.DE25).equals(DE_25))//pre-Auth completion void
				||(isoBuffer.get(Constants.ISO_MSG_TYPE).equals(Constants.MSG_TYPE_OFFLINE))//refund
				|| (isoBuffer.get(Constants.ISO_MSG_TYPE).equals(Constants.MSG_TYPE_TXN))&&(isoBuffer.get(Constants.DE3).substring(0, 2).equals(Constants.CONDITION_CODE_SALE))&& (isoBuffer.get(Constants.DE25).equals(DE_25)))//pre-Auth Completion
		{
			isoBuffer.disableField(Constants.DE55);
		}
		else{

			isoBuffer.put(Constants.DE55, d.toString());
		}

	}

	// building for field 60.2
	public String getBuildP60_2(IsoBuffer buffer) {
		StringBuilder s60_2 = new StringBuilder("0");// account holder type
		s60_2.append("6");//terminal entry capability
		s60_2.append("0");// chip condition code
		s60_2.append("0");// reserved
		s60_2.append("03");// Terminal type
		
		if (!buffer.isFieldEmpty(Constants.DE52))
		{
			s60_2.append("0");    //	s60_2.append("1");// signature only indicator or Receiver's currency// indicator
		}
		else
		{
			s60_2.append("1");
			
		}
		
		s60_2.append("0");// ic card authentication
		s60_2.append("00");// e-commerce identifier
		s60_2.append("0");// interative mode identifier
		return s60_2.toString();

	}

	// build for field 60.3

	public String getBuildP60_3(IsoBuffer buffer) {
		StringBuilder s60_3 = new StringBuilder("00");// special pricing type
		s60_3.append("0");// special pricing level
		s60_3.append("000");// minor unit of transaction currency//
		s60_3.append("0");// partial approval indicator
		s60_3.append("1");// Transaction initiation mode
		//		if (buffer.get(Constants.DE22).substring(0, 2).equals("05")) {
		//			s60_3.append("2");
		//		} else if (buffer.get(Constants.DE22).substring(0, 2).equals("02")) // transaction
		//			// medium
		//		{
		//			s60_3.append("3");
		//		} else {
		//			s60_3.append("0");
		//		}
		//		s60_3.append("2");// ic card application type
		//		s60_3.append("00");// account attribute //need to ask
		//		s60_3.append("0");// card level
		//		s60_3.append("00");// card product
		return s60_3.toString();

	}
}
