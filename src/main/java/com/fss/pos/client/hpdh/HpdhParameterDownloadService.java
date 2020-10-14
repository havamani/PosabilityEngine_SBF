package com.fss.pos.client.hpdh;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.beanutils.BeanMap;
import org.springframework.stereotype.Service;

import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.PosStringBuilder;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.client.services.download.parameter.CardRange;
import com.fss.pos.client.services.download.parameter.EmvTerminalData;
import com.fss.pos.client.services.download.parameter.IccData;
import com.fss.pos.client.services.download.parameter.IssuerData;
import com.fss.pos.client.services.download.parameter.KeyData;
import com.fss.pos.client.services.download.parameter.TerminalConfig;

@Service
public class HpdhParameterDownloadService extends
		AbstractHpdhParameterDownloadService {

	private static final String TELEPHONE_NUMBER = "FFFFFFFFFFFFFFFFFFFFFFFF";
	private static final String VISA_TERMINAL_ID = "        ";
	private static final String SETTLEMENT_LOGON_ID = "          ";

	@SuppressWarnings("unchecked")
	@Override
	protected String initializeTerminal(List<Object> beanList, String mspAcr,
			String terminalId) throws PosException {

		List<TerminalConfig> tcList = ((List<TerminalConfig>) beanList.get(1));

		if (tcList.isEmpty()) {
			Log.trace("No tct data");
			throw new PosException(Constants.ERR_DATABASE);
		}
		TerminalConfig terminalConfig = tcList.get(0);
		List<CardRange> cardRanges = (List<CardRange>) beanList.get(2);
		List<IssuerData> issuers = (List<IssuerData>) beanList.get(3);

		String tctData = getTctData(terminalConfig);
		String cardRangeData = getCardRangeData(cardRanges);
		String issuerData = getIssuerData(issuers);
		String acquirerData = getAcquirerData(terminalId,
				terminalConfig.getMerchantId(), terminalConfig.getBatchNo(),
				terminalConfig.getNextBatchNo(),
				terminalConfig.getSettleTime(),
				terminalConfig.getAmexMerchantId());

		List<IccData> iccListCb = (List<IccData>) beanList.get(4);
		List<IccData> iccListCl = (List<IccData>) beanList.get(5);
		List<KeyData> keyDataList = (List<KeyData>) beanList.get(6);
		List<EmvTerminalData> terminalDataList = (List<EmvTerminalData>) beanList.get(7);

		if (Util.isNullOrEmpty(tctData))
			throw new PosException(Constants.ERR_SYSTEM_ERROR);

		StringBuilder tableData = new StringBuilder();
		tableData.append(tctData);
		tableData.append(TABLE_SEPARATOR);

		if (!Util.isNullOrEmpty(cardRangeData)) {
			tableData.append(cardRangeData);
			tableData.append(TABLE_SEPARATOR);
		}
		if (!Util.isNullOrEmpty(issuerData)) {
			tableData.append(issuerData);
			tableData.append(TABLE_SEPARATOR);
		}
		if (!Util.isNullOrEmpty(acquirerData)) {
			tableData.append(acquirerData);
			tableData.append(TABLE_SEPARATOR);
		}

		if (Constants.ENABLE.equals(terminalConfig.getEnableEMV())) {

			String iccTableEntry = getIccTableEntryData(issuers);
			String iccDataCB = getIccDataContactBased(iccListCb);
			String iccDataCL = getIccDataContactLess(iccListCl);
			String keyData = getKeyData(keyDataList);
			String terminalData = getTerminalData(terminalDataList);

			if (!Util.isNullOrEmpty(iccTableEntry)) {
				tableData.append(iccTableEntry);
				tableData.append(TABLE_SEPARATOR);
			}

			if (!Util.isNullOrEmpty(iccDataCB)) {
				tableData.append(iccDataCB);
				tableData.append(TABLE_SEPARATOR);
			}
			if (!Util.isNullOrEmpty(iccDataCL)) {
				tableData.append(iccDataCL);
				tableData.append(TABLE_SEPARATOR);
			}
			if (!Util.isNullOrEmpty(keyData)) {
				tableData.append(keyData);
				tableData.append(TABLE_SEPARATOR);
			}
		}
		return tableData.toString();
	}

	public String getAcquirerData(String terminalId, String merchId,
			String batchNo, String nextBatchNo, String settleTime,
			String amexMerchId) {

		PosStringBuilder buffer = new PosStringBuilder();
		PosStringBuilder tBuffer = new PosStringBuilder();

		tBuffer.append(ZERO + ONE);// acq no
		tBuffer.append(ZERO + ONE); // acq id
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		buffer.append(Util.appendChar("", ' ', 10, false));// acq program name
		buffer.append(Util.appendChar("", ' ', 10, false));// acq name

		tBuffer.append(TELEPHONE_NUMBER);
		tBuffer.append(ONE_BYTE);
		tBuffer.append(ONE_BYTE);

		tBuffer.append(TELEPHONE_NUMBER);
		tBuffer.append(ONE_BYTE);
		tBuffer.append(ONE_BYTE);

		tBuffer.append(TELEPHONE_NUMBER);
		tBuffer.append(ONE_BYTE);
		tBuffer.append(ONE_BYTE);

		tBuffer.append(TELEPHONE_NUMBER);
		tBuffer.append(ONE_BYTE);
		tBuffer.append(ONE_BYTE);

		tBuffer.append(ONE_BYTE);// modem mode
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		buffer.append(binary2asciiCharLSB(FOUR_BYTE));
		buffer.append(binary2asciiCharLSB(FOUR_BYTE));
		buffer.append(binary2asciiCharLSB(FOUR_BYTE));
		buffer.append(binary2asciiCharLSB(FOUR_BYTE));

		tBuffer.append(TWO_BYTE);// niid
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		buffer.append(terminalId);
		buffer.append(Util.appendChar(merchId, ' ', 15, false));

		tBuffer.append("30");// timeout
		tBuffer.append(batchNo, THREE_BYTE, 6);// batch no
		tBuffer.append(nextBatchNo, THREE_BYTE, 6);// next batch no
		 tBuffer.append(FOUR_BYTE);// settle time
//		tBuffer.append(settleTime);// settle time
		tBuffer.append(TWO_BYTE);// settle day
		tBuffer.append(FIVE_BYTE + THREE_BYTE);// enc PIN key
		tBuffer.append(ONE_BYTE);// master PIN Key
		tBuffer.append(FIVE_BYTE + THREE_BYTE);// enc MAC key
		tBuffer.append(ONE_BYTE);// master MAC Key
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		buffer.append(Util.appendChar(amexMerchId, ' ', 15, false)); //for amex
		buffer.append(VISA_TERMINAL_ID); // for visa

		tBuffer.append(ONE_BYTE);// modem mode settlement
		tBuffer.append(ONE_BYTE);// max auto settlemnt attempts
		tBuffer.append(THREE_BYTE);// settlement logon id
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		buffer.append(SETTLEMENT_LOGON_ID);// settlement protocol name

		tBuffer.append(THREE_BYTE + THREE_BYTE);// acq inst id
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		tBuffer.append(buffer.toString());
		buffer.clear();
		buffer.append(IsoUtil.hex2AsciiChar(TABLE_ID_ACQUIRER));
		buffer.append(Util.getCompressedLength(tBuffer.toString(), 4));
		buffer.append(tBuffer.toString());

		return buffer.toString();
	}

	private String getIssuerData(List<IssuerData> issuers) throws PosException {
		StringBuilder tempTableData = new StringBuilder();
		for (IssuerData rp : issuers) {
			tempTableData.append(RECORD_SEPARATOR);
			tempTableData.append(getIssuers(rp));
		}
		return tempTableData.toString();
	}

	@SuppressWarnings("unchecked")
	private String getIssuers(IssuerData riskProfile) throws PosException {
		Log.debug("Issuer", new HashMap<Object, Object>(
				new BeanMap(riskProfile)).toString());
		PosStringBuilder buffer = new PosStringBuilder();
		PosStringBuilder tBuffer = new PosStringBuilder();

		tBuffer.append(riskProfile.getRpIdPK(), ONE_BYTE, 2);
		tBuffer.append(riskProfile.getRpIdPK(), ONE_BYTE, 2);
		// tBuffer.append("01", ONE_BYTE, 2);
		// tBuffer.append("01", ONE_BYTE, 2);

		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		if (riskProfile.getRpName().length() > 10) {
			riskProfile.setRpName(riskProfile.getRpName().substring(0, 10));
		}
		// buffer.append(riskProfile.getRpName(), FIVE_BYTE + FIVE_BYTE, 10);
		buffer.append(Util.appendChar(riskProfile.getRpName(), ' ', 10, true));

		tBuffer.append(FIVE_BYTE + FIVE_BYTE + TWO_BYTE);
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		// Option Bits 1,2,3,4
		buffer.append(binary2asciiCharLSB(getIssuerOption1Bits(riskProfile)));
		buffer.append(binary2asciiCharLSB(getIssuerOption2Bits(riskProfile)));
		buffer.append(binary2asciiCharLSB(getIssuerOption3Bits(riskProfile)));
		buffer.append(binary2asciiCharLSB(getIssuerOption4Bits(riskProfile)));

		tBuffer.append(ZERO + ONE);// default account
		tBuffer.append(TWO_BYTE);// RFU
		tBuffer.append(riskProfile.getFloorLimit(), TWO_BYTE, 4);
		tBuffer.append(ONE_BYTE);// reauth margin %
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		// Pin Entry Txn
		buffer.append(binary2asciiCharLSB(getIssuerPinEntryTxns(riskProfile)));
		buffer.append(binary2asciiCharLSB(FOUR_BYTE));// additional prompts
		buffer.append(riskProfile.getCardType());

		tBuffer.append(ONE_BYTE);// download payment indicator
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		// Option Bits 5,6
		buffer.append(binary2asciiCharLSB(FOUR_BYTE));
		buffer.append(binary2asciiCharLSB(FOUR_BYTE));

		tBuffer.append(FOUR_BYTE);// RFU
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		tBuffer.append(buffer.toString());
		buffer.clear();
		buffer.append(IsoUtil.hex2AsciiChar(TABLE_ID_ISSUER));
		buffer.append(Util.getCompressedLength(tBuffer.toString(), 4));
		buffer.append(tBuffer.toString());
		return buffer.toString();
	}

	@SuppressWarnings("unchecked")
	private String getTctData(TerminalConfig terminalConfig)
			throws PosException {
		Log.debug("TCT data", new HashMap<Object, Object>(new BeanMap(
				terminalConfig)).toString());
		PosStringBuilder buffer = new PosStringBuilder();
		PosStringBuilder tBuffer = new PosStringBuilder();

		tBuffer.append(ONE_BYTE); // DLL Tracking
		tBuffer.append(ONE_BYTE); // Initialization Control
		tBuffer.append(ONE_BYTE); // Printer Baud Rate
		tBuffer.append(ONE_BYTE); // ECR Baud Rate
		tBuffer.append(terminalConfig.getMerchantDateTime());
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		// Telephone Dial Options
		buffer.append(binary2asciiCharLSB(FOUR_BYTE));

		tBuffer.append(TWO_BYTE); // Password
		tBuffer.append(terminalConfig.getHelpDeskPhone() == null ? TELEPHONE_NUMBER
				: Util.appendChar(terminalConfig.getHelpDeskPhone(), 'F', 24,
						false)); // Help Desk Phone
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		buffer.append(binary2asciiCharLSB(getTctOption1Bits(terminalConfig)));
		buffer.append(binary2asciiCharLSB(getTctOption2Bits(terminalConfig)));
		buffer.append(binary2asciiCharLSB(getTctOption3Bits(terminalConfig)));
		buffer.append(binary2asciiCharLSB(getTctOption4Bits(terminalConfig)));

		buffer.append(Util.appendChar(terminalConfig.getReceiptLine2(), ' ',
				23, false));
		buffer.append(Util.appendChar(terminalConfig.getReceiptLine3(), ' ',
				23, false));
		buffer.append(Util.appendChar(terminalConfig.getDefaultMerchantName(),
				' ', 23, false));

		buffer.append(terminalConfig.getCurrencySymbol().substring(0, 1));// currency
																			// symbol

		tBuffer.append(terminalConfig.getTxnCurrencyDigits(), ONE_BYTE, 2);
		tBuffer.append(terminalConfig.getCurrencyDecimal(), ONE_BYTE, 2);
		tBuffer.append(terminalConfig.getLanguageIndicator(), ONE_BYTE, 2);
		tBuffer.append(terminalConfig.getSettlementCurrencyDigits(), ONE_BYTE,
				2);
		tBuffer.append(ONE_BYTE);// check service
		tBuffer.append(ONE_BYTE);// guarantee issuer table id
		tBuffer.append(ONE_BYTE);// guarantee acquirer table id
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		buffer.append(binary2asciiCharLSB(getLocalTerminalOptionsBits(terminalConfig)));

		tBuffer.append(ONE_BYTE, ONE_BYTE, 2);// default txn
		tBuffer.append(terminalConfig.getDebitIssuerId(), ONE_BYTE, 2);
		tBuffer.append(terminalConfig.getDebitAcquirerId(), ONE_BYTE, 2);
		tBuffer.append(terminalConfig.getPhoneLinehold(), ONE_BYTE, 2);
		tBuffer.append(TWO_BYTE);// RFU
		tBuffer.append(ONE_BYTE);// pin pad type
		tBuffer.append(ZERO + ONE);// printer type
		tBuffer.append(terminalConfig.getCashbackLimit(), TWO_BYTE, 4);
		tBuffer.append(ONE_BYTE);
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		// check prompts
		buffer.append(binary2asciiCharLSB(FOUR_BYTE));
		buffer.append(binary2asciiCharLSB(FOUR_BYTE));
		buffer.append(binary2asciiCharLSB(FOUR_BYTE));

		tBuffer.append(terminalConfig.getMerchantSurcharge(), TWO_BYTE, 4);
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		buffer.append(binary2asciiCharLSB(FOUR_BYTE)); // opt 5
		tBuffer.append(FOUR_BYTE + FOUR_BYTE); // RFU
		tBuffer.append(
				terminalConfig.getSupervisorCode()
						+ terminalConfig.getSupervisorCode(), THREE_BYTE
						+ THREE_BYTE, 12);
		tBuffer.append(FOUR_BYTE + FOUR_BYTE);// RFU
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		buffer.append(binary2asciiCharLSB(FOUR_BYTE)); // opt 7

		tBuffer.append(FOUR_BYTE);// check services flag
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		buffer.append(binary2asciiCharLSB(getTctOption8Bits(terminalConfig)));
		tBuffer.append(terminalConfig.getCurrencyCode(), TWO_BYTE, 4);
		tBuffer.append(terminalConfig.getCurrencyCode(), TWO_BYTE, 4);
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		// terminal capabilities
		tBuffer.append(THREE_BYTE);
		tBuffer.append(FIVE_BYTE);
		tBuffer.append(FIVE_BYTE); // RFU
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		tBuffer.append(buffer.toString());
		buffer.clear();
		buffer.append(IsoUtil.hex2AsciiChar(TABLE_ID_TCT));
		buffer.append(Util.getCompressedLength(tBuffer.toString(), 4));
		buffer.append(tBuffer.toString());
		Log.debug("After TCT", "");
		return buffer.toString();
	}

	private String getCardRangeData(List<CardRange> cardRangeList)
			throws PosException {
		StringBuilder tempTableData = new StringBuilder();
		for (CardRange cr : cardRangeList) {
			tempTableData.append(RECORD_SEPARATOR);
			tempTableData.append(getCardRanges(cr));
		}
		return tempTableData.toString();
	}

	@SuppressWarnings("unchecked")
	private String getCardRanges(CardRange cardRange) throws PosException {

		Log.debug("Card Range", new HashMap<Object, Object>(new BeanMap(
				cardRange)).toString());
		PosStringBuilder buffer = new PosStringBuilder();
		PosStringBuilder tBuffer = new PosStringBuilder();

		tBuffer.append(cardRange.getCardRangeId(), ONE_BYTE, 2);
		tBuffer.append(cardRange.getPanRangeLow(), FIVE_BYTE, 10);
		tBuffer.append(cardRange.getPanRangeHigh(), FIVE_BYTE, 10);
		tBuffer.append(cardRange.getIssuerId(), ONE_BYTE, 2);
		tBuffer.append(ZERO + ONE); // acquirer table id
		tBuffer.append(cardRange.getPanLength(), ONE_BYTE, 2);
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		// Option bits 1,2,3,4
		buffer.append(binary2asciiCharLSB(FOUR_BYTE));
		buffer.append(binary2asciiCharLSB(FOUR_BYTE));
		buffer.append(binary2asciiCharLSB(FOUR_BYTE));
		buffer.append(binary2asciiCharLSB(FOUR_BYTE));

		tBuffer.append("01", ONE_BYTE, 2);
		tBuffer.append(ZERO + ONE);// acquirer table id 2
		tBuffer.append(TWO_BYTE);
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		tBuffer.append(buffer.toString());
		buffer.clear();
		buffer.append(IsoUtil.hex2AsciiChar(TABLE_ID_CARD_RANGE));
		buffer.append(Util.getCompressedLength(tBuffer.toString(), 4));
		buffer.append(tBuffer.toString());
		return buffer.toString();

	}

	private String getTctOption1Bits(TerminalConfig tc) throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(tc.getAmountDualEntry());
		b.appendOptionBit(tc.getDisplayMagneticStrip());
		b.appendOptionBit(tc.getTipProcessing());
		b.append(ZERO); // Cashier Processing
		b.append(ZERO);// Lodging Processing
		b.append(ZERO); // Print Time on Receipt
		b.append(ZERO); // Business Date Format
		b.append(ZERO); // Reconciliation Method
		return b.toString();
	}

	private String getTctOption2Bits(TerminalConfig tc) throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.append(ZERO); // RFU
		b.append("1");// printer used
		b.append(ZERO); // Print Total on Receipt
		b.append(ZERO); // Till Processing
		b.appendOptionBit(tc.getEoRefund());
		b.appendOptionBit(tc.getEoVoid());
		b.append(ZERO); // Enable Receipt Preprint
		b.append(ZERO); // Card Verify Key or Debit Key
		return b.toString();
	}

	private String getTctOption3Bits(TerminalConfig tc) throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.append(ZERO); // Unused
		b.append(ZERO); // Electronic Card Reader Interface
		b.append(ZERO);// amt verify on pin pad
		b.append(ZERO);// Print Card Verify Receipt
		b.append(ZERO);
		b.append(ZERO);
		b.append(ZERO);
		b.append(ZERO);
		return b.toString();
	}

	private String getTctOption4Bits(TerminalConfig tc) throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.append(ONE); // adjust by cashier
		b.append(ZERO);
		b.append(ZERO);
		b.append(ONE); // tender cash prompt
		b.append(ZERO);
		b.append(ZERO);
		b.append(ZERO);
		b.append(ZERO);
		return b.toString();
	}

	private String getLocalTerminalOptionsBits(TerminalConfig tc)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.append(ZERO);
		b.append(tc.getVoidCode());
		b.append(tc.getRefundCode());
		b.append(tc.getAdjustmentCode());
		b.append(ZERO);
		b.append(ZERO);
		b.append(ZERO);
		b.append(ZERO);
		return b.toString();
	}

	private String getTctOption8Bits(TerminalConfig tc) throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.append(ZERO);
		b.append(ZERO);// terminal online capable
		b.append(tc.getOnlinePINVerification());
		b.append(tc.getOfflinePINVerification()); // tender cash prompt
		b.append(ZERO);// term with SCR
		b.append(tc.getEnableEMV());
		b.append(tc.getAllowPINBypass());
		b.append(ZERO);// early amt
		return b.toString();
	}

	private String getIssuerOption1Bits(final IssuerData riskProfile)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(riskProfile.getSelectAccType());
		b.appendOptionBit(riskProfile.getPinEntry());
		b.appendOptionBit(riskProfile.getManualPanAlwd());
		b.appendOptionBit(riskProfile.getExpdtReqforMan());
		b.appendOptionBit(riskProfile.getOfflineEntryAlwd());
		b.append(ZERO);// voice referral allowed
		b.append(ONE);// desciption required
		b.appendOptionBit(riskProfile.getTipAlwd());
		return b.toString();
	}

	private String getIssuerOption2Bits(final IssuerData riskProfile)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(ZERO);
		b.appendOptionBit(riskProfile.getInvoiceReq());
		b.appendOptionBit("1");// print receipt
		b.appendOptionBit(riskProfile.getCaptureTxn());
		b.appendOptionBit(riskProfile.getChkExpiryDate());
		b.append(ZERO); // Be my Guest
		b.appendOptionBitNot(riskProfile.getRefundAlwd());
		b.append(ZERO);// block card verify
		return b.toString();
	}

	private String getIssuerOption3Bits(final IssuerData riskProfile)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBitNot(riskProfile.getVoidAlwd());
		b.appendOptionBit(riskProfile.getCashBackAlwd());
		b.append(ZERO + ZERO);
		b.appendOptionBit(riskProfile.getOfflineAlwdRefund());
		b.appendOptionBit(riskProfile.getOfflineAlwdVoid());
		b.append(ZERO + ZERO);
		return b.toString();
	}

	private String getIssuerOption4Bits(final IssuerData riskProfile)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.append(ZERO);
		b.appendOptionBit(riskProfile.getVerifylast4Pan());
		b.append(ZERO + ZERO + ZERO + ZERO + ZERO + ZERO);
		return b.toString();
	}

	private String getIssuerPinEntryTxns(final IssuerData riskProfile)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBitNot(riskProfile.getPinPurchase());
		b.appendOptionBitNot(riskProfile.getPinReturn());
		b.appendOptionBitNot(riskProfile.getPinVoid());
		b.appendOptionBitNot(riskProfile.getPinTIP());
		b.appendOptionBitNot(riskProfile.getPinCashback());
		b.appendOptionBitNot(riskProfile.getPinBalnceInq());
		b.appendOptionBitNot(riskProfile.getPinPreAuth());
		b.append(ZERO);
		return b.toString();
	}

}
