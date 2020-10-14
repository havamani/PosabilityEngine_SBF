package com.fss.pos.client.hpdh;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.beanutils.BeanMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.pos.base.commons.Config;
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
public class ExtendedHpdhParameterDownloadService extends
		AbstractHpdhParameterDownloadService {

	@Autowired
	private Config config;

	@Override
	@SuppressWarnings("unchecked")
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
		List<IccData> iccListCb = (List<IccData>) beanList.get(4);
		List<IccData> iccListCl = (List<IccData>) beanList.get(5);
		List<KeyData> keyDataList = (List<KeyData>) beanList.get(6);
		List<EmvTerminalData> terminalDataList = (List<EmvTerminalData>) beanList.get(7);

		String tctData = getTctData(terminalConfig, mspAcr);
		String cardRangeData = getCardRangeData(cardRanges);
		String issuerData = getIssuerData(issuers);
		String limitData = getLimitData(terminalConfig);

		if (Util.isNullOrEmpty(tctData))
			throw new PosException(Constants.ERR_SYSTEM_ERROR);

		if (tctData.length() > Integer.parseInt(config
				.getParamDownloadMaxBytes())) {
			Log.trace("Max bytes less than TCT data");
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}

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
		if (!Util.isNullOrEmpty(limitData)) {
			tableData.append(limitData);
			tableData.append(TABLE_SEPARATOR);
		}

		if (Constants.ENABLE.equals(terminalConfig.getEnableEMV())) {

			String iccTableEntry = getIccTableEntryData(issuers);
			String iccDataCB = getIccDataContactBased(iccListCb);
			String iccDataCL = getIccDataContactLess(iccListCl);
			String keyData = getKeyData(keyDataList);
			String terminalData = getTerminalData(terminalDataList);
//
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

	private String getIssuerData(List<IssuerData> issuers) throws PosException {
		StringBuilder tempTableData = new StringBuilder();
		for (IssuerData rp : issuers) {
			tempTableData.append(RECORD_SEPARATOR);
			tempTableData.append(getIssuers(rp));
		}
		return tempTableData.toString();
	}

	private String getLimitData(TerminalConfig tc) {

		PosStringBuilder buffer = new PosStringBuilder();
		PosStringBuilder tBuffer = new PosStringBuilder();

		tBuffer.append(tc.getCashbackLimit(), FIVE_BYTE + ONE_BYTE, 12);
		tBuffer.append(tc.getCompletionLimit(), ONE_BYTE, 2);
		tBuffer.append(tc.getTipAdjustmentLimit(), ONE_BYTE, 2);
		tBuffer.append(tc.getMerchantSurchargeLimit(), ONE_BYTE, 2);
		tBuffer.append(tc.getCashDepositLimit(), FIVE_BYTE + ONE_BYTE, 12);
		tBuffer.append(tc.getCashAdvanceLimit(), FIVE_BYTE + ONE_BYTE, 12);
		tBuffer.append(tc.getPurchaseLimit(), FIVE_BYTE + ONE_BYTE, 12);
		tBuffer.append(tc.getMotoLimit(), FIVE_BYTE + ONE_BYTE, 12);
		tBuffer.append(tc.getRefundLimit(), FIVE_BYTE + ONE_BYTE, 12);
		tBuffer.append(tc.getDebitTxnCount(), THREE_BYTE, 6);
		tBuffer.append(tc.getCreditTxnCount(), THREE_BYTE, 6);
		tBuffer.append(tc.getOfflineTxnCount(), ONE_BYTE, 2);
		tBuffer.append(tc.getMaxReversalTxn(), ONE_BYTE, 2);
		tBuffer.append(tc.getTxnVelocity(), TWO_BYTE, 4);
		tBuffer.append(tc.getTxnInflightTime(), TWO_BYTE, 4);
		tBuffer.append(tc.getMagPIN(), FIVE_BYTE + ONE_BYTE, 12);
		tBuffer.append(tc.getContactLessPIN(), FIVE_BYTE + ONE_BYTE, 12);
		tBuffer.append(tc.getContactBasedPIN(), FIVE_BYTE + ONE_BYTE, 12);
		tBuffer.append(tc.getEnableGeo(), FIVE_BYTE + ONE_BYTE, 12);
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		buffer.append(Util.appendChar(
				String.valueOf(tc.getAdminCode().length()), '0', 3, true));
		buffer.append(tc.getAdminCode());

		String[] spArray = tc.getSupervisorCode().split(",");
		buffer.append(String.valueOf(spArray.length), ONE_BYTE, 2);
		for (String sp : spArray) {
			buffer.append(Util.appendChar(String.valueOf(sp.length()), '0', 3,
					true));
			buffer.append(sp);
		}
		tBuffer.append(buffer.toString());
		buffer.clear();
		buffer.append(IsoUtil.hex2AsciiChar(TABLE_ID_LIMIT));
		buffer.append(Util.getCompressedLength(tBuffer.toString(), 4));
		buffer.append(tBuffer.toString());
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
	private String getIssuers(IssuerData riskProfile) throws PosException {
		Log.debug("Issuer data", new HashMap<Object, Object>(new BeanMap(
				riskProfile)).toString());
		PosStringBuilder buffer = new PosStringBuilder();
		PosStringBuilder tBuffer = new PosStringBuilder();

		tBuffer.append(riskProfile.getRpIdPK(), TWO_BYTE, 4);
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		buffer.append(riskProfile.getRpName().length() > 10 ? riskProfile
				.getRpName().substring(0, 10) : Util.appendChar(
				riskProfile.getRpName(), ' ', 10, false));
		if (riskProfile.getEmiRange() == null) {
			tBuffer.append(FFFFFF + FFFFFF + FFFFFF + FFFFFF);
		} else {
			PosStringBuilder emiSlabs = new PosStringBuilder();
			for (String e : riskProfile.getEmiRange().split(","))
				emiSlabs.append(e, ONE_BYTE, 2);
			tBuffer.append(Util.appendChar(emiSlabs.toString(), 'F', 24, false));
		}
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		buffer.append(binary2asciiCharLSB(getIssuerOption1Bits(riskProfile)));
		buffer.append(binary2asciiCharLSB(getIssuerTxnAllowedOpt1(riskProfile)));
		buffer.append(binary2asciiCharLSB(getIssuerTxnAllowedOpt2(riskProfile)));
		buffer.append(binary2asciiCharLSB(getIssuerOfflineAllowedOpt1(riskProfile)));
		buffer.append(binary2asciiCharLSB(getIssuerOfflineAllowedOpt2(riskProfile)));
		buffer.append(binary2asciiCharLSB(getIssuerManualAllowedOpt1(riskProfile)));
		buffer.append(binary2asciiCharLSB(getIssuerManualAllowedOpt2(riskProfile)));
		buffer.append(binary2asciiCharLSB(getIssuerPinEntryTxnsOpt1(riskProfile)));
		buffer.append(binary2asciiCharLSB(getIssuerPinEntryTxnsOpt2(riskProfile)));

		tBuffer.append(buffer.toString());
		buffer.clear();
		buffer.append(IsoUtil.hex2AsciiChar(TABLE_ID_ISSUER));
		buffer.append(Util.getCompressedLength(tBuffer.toString(), 4));
		buffer.append(tBuffer.toString());
		return buffer.toString();
		
	}

	@SuppressWarnings("unchecked")
	private String getCardRanges(CardRange cardRange) throws PosException {
		Log.debug("Card Range data", new HashMap<Object, Object>(new BeanMap(
				cardRange)).toString());
		PosStringBuilder buffer = new PosStringBuilder();
		PosStringBuilder tBuffer = new PosStringBuilder();

		tBuffer.append(cardRange.getCardRangeId(), TWO_BYTE, 4);
		tBuffer.append(cardRange.getPanRangeLow(), FIVE_BYTE, 10);
		tBuffer.append(cardRange.getPanRangeHigh(), FIVE_BYTE, 10);
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();
		buffer.append(Util.appendChar(cardRange.getCardName(), ' ', 20, false));
		tBuffer.append(cardRange.getIssuerId(), TWO_BYTE, 4);
		tBuffer.append(cardRange.getPanLength(), ONE_BYTE, 2);
		tBuffer.append(cardRange.getOfflineFloorLmt(), THREE_BYTE + THREE_BYTE,
				12);
		tBuffer.append(cardRange.getDefaultAccType(), ONE_BYTE, 2);
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();
		buffer.append(binary2asciiCharLSB(getCardRangeOption1Bits(cardRange)));

		if (ONE.equals(cardRange.getEnableServiceCode())) {
			String[] sc = cardRange.getServiceCode().split(",");
			tBuffer.append(sc[0], TWO_BYTE, 4);
			if (sc.length == 1) {
				tBuffer.append(TWO_BYTE + TWO_BYTE);
			} else if (sc.length == 2) {
				tBuffer.append(sc[1], TWO_BYTE, 4);
				tBuffer.append(TWO_BYTE);
			} else if (sc.length == 3) {
				tBuffer.append(sc[1], TWO_BYTE, 4);
				tBuffer.append(sc[2], TWO_BYTE, 4);
			} else {
				tBuffer.append(TWO_BYTE + TWO_BYTE + TWO_BYTE);
			}
		} else {
			tBuffer.append(TWO_BYTE + TWO_BYTE + TWO_BYTE);
		}

		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		tBuffer.append(buffer.toString());
		buffer.clear();
		buffer.append(IsoUtil.hex2AsciiChar(TABLE_ID_CARD_RANGE));
		buffer.append(Util.getCompressedLength(tBuffer.toString(), 4));
		buffer.append(tBuffer.toString());
		System.out.println(IsoUtil.asciiChar2hex(buffer.toString()));
		return buffer.toString();
	}

	@SuppressWarnings("unchecked")
	private String getTctData(TerminalConfig terminalConfig, String mspAcr)
			throws PosException {
		Log.debug("TCT data", new HashMap<Object, Object>(new BeanMap(
				terminalConfig)).toString());
		PosStringBuilder buffer = new PosStringBuilder();
		PosStringBuilder tBuffer = new PosStringBuilder();

		tBuffer.append(terminalConfig.getTerminalCategory(), ONE_BYTE, 2);
		tBuffer.append(terminalConfig.getMessageFormat(), ONE_BYTE, 2);
		tBuffer.append(ONE_BYTE); // ECR Ref No
		tBuffer.append(terminalConfig.getMerchantDateTime());
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		buffer.append(Util.appendChar(terminalConfig.getMerchantId(), ' ', 15,
				false));
		buffer.append(binary2asciiCharLSB(getTctOption1Bits(terminalConfig)));
		buffer.append(binary2asciiCharLSB(getTctOption2Bits(terminalConfig)));

		buffer.append(Util.appendChar(terminalConfig.getReceiptLine2(), ' ',
				23, false));
		buffer.append(Util.appendChar(terminalConfig.getReceiptLine3(), ' ',
				23, false));
		buffer.append(Util.appendChar(terminalConfig.getDefaultMerchantName(),
				' ', 23, false));
		buffer.append(Util.appendChar(terminalConfig.getCurrencySymbol(), ' ',
				2, false));
		tBuffer.append(terminalConfig.getTxnCurrencyDigits(), ONE_BYTE, 2);
		tBuffer.append(terminalConfig.getCurrencyDecimal(), ONE_BYTE, 2);
		tBuffer.append(terminalConfig.getLanguageIndicator(), ONE_BYTE, 2);
		tBuffer.append(terminalConfig.getSettlementCurrencyDigits(), ONE_BYTE,
				2);
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		buffer.append(Util.appendChar(terminalConfig.getCurrencyName(), ' ', 3,
				false));

		tBuffer.append(terminalConfig.getDefaultTxn(), TWO_BYTE, 4);
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		buffer.append(binary2asciiCharLSB(getTctManualProcessingOpt1(terminalConfig)));
		buffer.append(binary2asciiCharLSB(getTctManualProcessingOpt2(terminalConfig)));

		buffer.append(binary2asciiCharLSB(getTxnAllowedOptions1(terminalConfig)));
		buffer.append(binary2asciiCharLSB(getTxnAllowedOptions2(terminalConfig)));
		buffer.append(binary2asciiCharLSB(getPINRequiredOptions1(terminalConfig)));
		buffer.append(binary2asciiCharLSB(getPINRequiredOptions2(terminalConfig)));
		buffer.append(binary2asciiCharLSB(getOfflineAllowedOptions1(terminalConfig)));
		buffer.append(binary2asciiCharLSB(getOfflineAllowedOptions2(terminalConfig)));

		tBuffer.append(terminalConfig.getTerminalType(), ONE_BYTE, 2);
		tBuffer.append(terminalConfig.getBatchNo(), THREE_BYTE, 6);
		tBuffer.append(terminalConfig.getNextBatchNo(), THREE_BYTE, 6);
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		buffer.append(binary2asciiCharLSB(getSupervisoryCodeOptions(terminalConfig)));

		tBuffer.append(terminalConfig.getCurrencyCode(), TWO_BYTE, 4);
		tBuffer.append(terminalConfig.getCurrencyCode(), TWO_BYTE, 4); // country
																		// code
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		buffer.append(binary2asciiCharLSB(getTerminalCapabilityEntry(terminalConfig)));
		buffer.append(binary2asciiCharLSB(getCardHolderVerification(terminalConfig)));
		buffer.append(binary2asciiCharLSB(getSecurityCapability(terminalConfig)));
		buffer.append(binary2asciiCharLSB(getTxnTypeCapabilityByte1(terminalConfig)));
		buffer.append(binary2asciiCharLSB(getTxnTypeCapabilityByte2(terminalConfig)));
		buffer.append(binary2asciiCharLSB(FOUR_BYTE));
		buffer.append(binary2asciiCharLSB(FOUR_BYTE));
		buffer.append(binary2asciiCharLSB(FOUR_BYTE));

		tBuffer.append(terminalConfig.getMerchantCategoryCode(), TWO_BYTE, 4);
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		buffer.append(Util.appendChar(mspAcr, ' ', 3, false));

		tBuffer.append(terminalConfig.getSettleTime(), THREE_BYTE, 6);
		tBuffer.append(ZERO.equals(terminalConfig.getBusStartTime()) ? FFFFFF
				: terminalConfig.getBusStartTime(), THREE_BYTE, 6);
		tBuffer.append(ZERO.equals(terminalConfig.getBusEndTime()) ? FFFFFF
				: terminalConfig.getBusEndTime(), THREE_BYTE, 6);
		buffer.append(IsoUtil.hex2AsciiChar(tBuffer.toString()));
		tBuffer.clear();

		buffer.append(Util.appendChar(terminalConfig.getReceiptType(), ' ', 1,
				false));
		buffer.append(Util.appendChar(
				terminalConfig.getPinEncryptionKeyScheme(), ' ', 1, false));
		buffer.append(Util.appendChar(
				terminalConfig.getP2peEncryptionKeyScheme(), ' ', 1, false));
		buffer.append(terminalConfig.getScheduleKeyInterval() == null ? " "
				: Util.appendChar(terminalConfig.getScheduleKeyInterval(), ' ',
						1, false));
		buffer.append(Util.appendChar(
				terminalConfig.getCardHolderVerification(), ' ', 1, false));

		tBuffer.append(buffer.toString());
		buffer.clear();
		buffer.append(IsoUtil.hex2AsciiChar(TABLE_ID_TCT));
		buffer.append(Util.getCompressedLength(tBuffer.toString(), 4));
		buffer.append(tBuffer.toString());
		
		Log.debug("TCT DATa", buffer.toString());
		
		return buffer.toString();
	}

	private String getCardRangeOption1Bits(CardRange cr) throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(cr.getEnableServiceCode());
		b.appendOptionBit(cr.getEnableMod10Val());
		b.appendOptionBit(cr.getEmiEnabled());
		b.append(ZERO + ZERO + ZERO + ZERO + ZERO);
		return b.toString();
	}

	private String getTxnTypeCapabilityByte1(TerminalConfig tc)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(tc.getEtCashAdvance());
		b.appendOptionBit(tc.getEtPurchase());
		b.append(ONE);
		b.appendOptionBit(tc.getEtPurchasewithCashback());
		b.appendOptionBit(tc.getEtBalanceInquiry());
		b.append(ZERO + ZERO + ZERO);
		return b.toString();
	}

	private String getTxnTypeCapabilityByte2(TerminalConfig tc)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(tc.getEtCashDeposit());
		b.append(ZERO + ZERO + ZERO + ZERO + ZERO + ZERO + ZERO);
		return b.toString();
	}

	private String getTerminalCapabilityEntry(TerminalConfig tc)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.append(ZERO + ZERO + ZERO + ZERO + ZERO);
		b.appendOptionBit(tc.getIcContact());
		b.appendOptionBit(tc.getMagneticStripe());
		b.appendOptionBit(tc.getKeyEntry());
		return b.toString();
	}

	private String getCardHolderVerification(TerminalConfig tc)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.append(ZERO + ZERO + ZERO + ZERO);
		b.appendOptionBit(tc.getEncipherOffline());
		b.appendOptionBit(tc.getSignature());
		b.appendOptionBit(tc.getEncipherOnline());
		b.appendOptionBit(tc.getPlainPINICC());
		return b.toString();
	}

	private String getSecurityCapability(TerminalConfig tc) throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.append(ZERO + ZERO + ZERO + ZERO + ZERO);
		b.appendOptionBit(tc.getCda());
		b.appendOptionBit(tc.getDda());
		b.appendOptionBit(tc.getSda());
		return b.toString();
	}

	private String getIssuerPinEntryTxnsOpt1(IssuerData riskProfile)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(riskProfile.getPinPurchase());
		b.appendOptionBit(riskProfile.getPinTIP());
		b.appendOptionBit(riskProfile.getPinVoid());
		b.appendOptionBit(riskProfile.getPinRefund());
		b.appendOptionBit(riskProfile.getPinPreAuth());
		b.appendOptionBit(riskProfile.getPinCompletion());
		b.appendOptionBit(riskProfile.getPinCashback());
		b.appendOptionBit(riskProfile.getPinCompletion());
		return b.toString();
	}

	private String getIssuerPinEntryTxnsOpt2(IssuerData riskProfile)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(riskProfile.getPinBalnceInq());
		b.appendOptionBit(riskProfile.getPinMOTO());
		b.appendOptionBit(riskProfile.getPinCashDeposit());
		b.append(riskProfile.getPinPreAuth());
		b.append(ZERO + ZERO + ZERO + ZERO);
		return b.toString();
	}

	private String getIssuerManualAllowedOpt1(IssuerData riskProfile)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(riskProfile.getManAlwdPurchase());
		b.appendOptionBit(riskProfile.getManAlwdVoid());
		b.appendOptionBit(riskProfile.getManAlwdTip());
		b.appendOptionBit(riskProfile.getManAlwdRefund());
		b.appendOptionBit(riskProfile.getManAlwdPreAuth());
		b.appendOptionBit(riskProfile.getManAlwdCompletion());
		b.appendOptionBit(riskProfile.getManAlwdCashback());
		b.appendOptionBit(riskProfile.getManAlwdCashAdv());
		return b.toString();
	}

	private String getIssuerManualAllowedOpt2(IssuerData riskProfile)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(riskProfile.getManAlwdBalanceInq());
		b.append(riskProfile.getManAlwdMoto());
		b.appendOptionBit(riskProfile.getManAlwdCashDepst());
		b.append(riskProfile.getManAlwdCashRecording());
		b.append(ZERO + ZERO + ZERO + ZERO);
		return b.toString();
	}

	private String getIssuerOfflineAllowedOpt1(IssuerData riskProfile)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(riskProfile.getOfflineAlwdPurchase());
		b.appendOptionBit(riskProfile.getOfflineAlwdip()); // TIP
		b.appendOptionBit(riskProfile.getOfflineAlwdVoid());
		b.appendOptionBit(riskProfile.getOfflineAlwdRefund());
		b.appendOptionBit(riskProfile.getOfflineAlwdPreAuth());
		b.appendOptionBit(riskProfile.getOfflineAlwdCompl());
		b.appendOptionBit(riskProfile.getOfflineAlwdcashback());
		b.appendOptionBit(riskProfile.getOfflineAlwdCashAdv());
		return b.toString();
	}

	private String getIssuerOfflineAllowedOpt2(IssuerData riskProfile)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(riskProfile.getOfflineAlwdBalanceInquiry());
		b.appendOptionBit(riskProfile.getOfflineAlwdMoto());
		b.appendOptionBit(riskProfile.getOfflineAlwdCashDeposit());
		b.appendOptionBit(riskProfile.getOfflineAlwdCashRecording());
		b.append(ZERO + ZERO + ZERO + ZERO);
		return b.toString();
	}

	private String getIssuerOption1Bits(IssuerData riskProfile)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(riskProfile.getSelectAccType());
		b.appendOptionBit(riskProfile.getExpdtReqforMan());
		b.appendOptionBit(riskProfile.getChkExpiryDate());
		b.append(ZERO);
		b.appendOptionBit(riskProfile.getVerifylast4Pan());
		b.append(ZERO + ZERO + ZERO);
		return b.toString();
	}

	private String getIssuerTxnAllowedOpt1(IssuerData riskProfile)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(riskProfile.getPurchaseAlwd());
		b.appendOptionBit(riskProfile.getTipAlwd());
		b.appendOptionBit(riskProfile.getVoidAlwd());
		b.appendOptionBit(riskProfile.getRefundAlwd());
		b.appendOptionBit(riskProfile.getPreAuthAlwd());
		b.appendOptionBit(riskProfile.getCompletionAlwd());
		b.appendOptionBit(riskProfile.getCashBackAlwd());
		b.appendOptionBit(riskProfile.getCashAdvanceAlwd());
		return b.toString();
	}

	private String getIssuerTxnAllowedOpt2(IssuerData riskProfile)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(riskProfile.getBalanceInqAlwd());
		b.appendOptionBit(riskProfile.getMotoAlwd());
		b.appendOptionBit(riskProfile.getCashDepositAlwd());
		b.appendOptionBit(riskProfile.getCashRecordingAlwd());
		b.append(ZERO + ZERO + ZERO + ZERO);
		return b.toString();
	}

	private String getTctOption1Bits(TerminalConfig terminalConfig)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(terminalConfig.getAmountDualEntry());
		b.appendOptionBit(terminalConfig.getDisplayMagneticStrip());
		b.appendOptionBit(terminalConfig.getTipProcessing());
		b.appendOptionBit(terminalConfig.getPrintReceipt());
		b.append(terminalConfig.getEnableEMI());
		b.appendOptionBit(terminalConfig.getEnableLoginOTP());
		b.appendOptionBit(terminalConfig.getBusinessDateFormat());
		b.append(terminalConfig.getEnableDCC());
		return b.toString();
	}

	private String getTctOption2Bits(TerminalConfig terminalConfig)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(terminalConfig.getEnableGEOLimit());
		b.appendOptionBit(terminalConfig.getInvoiceNumberRequired());
		b.append(ZERO);
		b.appendOptionBit(terminalConfig.getEnableP2PE());
		b.append(ZERO);
		b.appendOptionBit(terminalConfig.getEnableRemoteKeyUpdate());
		b.appendOptionBit(terminalConfig.getCardReadfromPINPad());
		b.append(ZERO);
		return b.toString();
	}

	private String getTctManualProcessingOpt1(TerminalConfig terminalConfig)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(terminalConfig.getEmPurchase());
		b.appendOptionBit(terminalConfig.getEmTIP());
		b.appendOptionBit(terminalConfig.getEmVoid());
		b.appendOptionBit(terminalConfig.getEmRefund());
		b.appendOptionBit(terminalConfig.getEmPreAuth());
		b.appendOptionBit(terminalConfig.getEmCompletion());
		b.appendOptionBit(terminalConfig.getEmPurchasewithCashback());
		b.appendOptionBit(terminalConfig.getEmCashAdvance());
		return b.toString();
	}

	private String getTctManualProcessingOpt2(TerminalConfig terminalConfig)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(terminalConfig.getEmBalanceInquiry());
		b.appendOptionBit(terminalConfig.getEmMOTO());
		b.appendOptionBit(terminalConfig.getEmCashDeposit());
		b.appendOptionBit(terminalConfig.getEmCashRecording());
		b.append(ZERO + ZERO + ZERO + ZERO);
		return b.toString();
	}

	private String getTxnAllowedOptions1(TerminalConfig tc) throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(tc.getEtPurchase());
		b.appendOptionBit(tc.getEtTIP());
		b.appendOptionBit(tc.getEtVoid());
		b.appendOptionBit(tc.getEtRefund());
		b.appendOptionBit(tc.getEtPreAuth());
		b.appendOptionBit(tc.getEtCompletion());
		b.appendOptionBit(tc.getEtPurchasewithCashback());
		b.appendOptionBit(tc.getEtCashAdvance());
		return b.toString();
	}

	private String getTxnAllowedOptions2(TerminalConfig tc) throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(tc.getEtBalanceInquiry());
		b.appendOptionBit(tc.getEtMOTO());
		b.appendOptionBit(tc.getEtCashDeposit());
		b.appendOptionBit(tc.getEtCashRecording());
		b.append(ZERO + ZERO + ZERO + ZERO);
		return b.toString();
	}

	private String getOfflineAllowedOptions1(TerminalConfig tc)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(tc.getEoPurchase());
		b.appendOptionBit(tc.getEoTIP());
		b.appendOptionBit(tc.getEoVoid());
		b.appendOptionBit(tc.getEoRefund());
		b.appendOptionBit(tc.getEoPreAuth());
		b.appendOptionBit(tc.getEoCompletion());
		b.appendOptionBit(tc.getEoPurchasewithCashback());
		b.appendOptionBit(tc.getEoCashAdvance());
		return b.toString();
	}

	private String getOfflineAllowedOptions2(TerminalConfig tc)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(tc.getEoBalanceInquiry());
		b.appendOptionBit(tc.getEoMOTO());
		b.appendOptionBit(tc.getEoCashDeposit());
		b.appendOptionBit(tc.getEoCashRecording());
		b.append(ZERO + ZERO + ZERO + ZERO);
		return b.toString();
	}

	private String getPINRequiredOptions2(TerminalConfig tc)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(tc.getEpBalanceInquiry());
		b.appendOptionBit(tc.getEpMOTO());
		b.appendOptionBit(tc.getEpCashDeposit());
		b.append(ZERO + ZERO + ZERO + ZERO + ZERO);
		return b.toString();
	}

	private String getPINRequiredOptions1(TerminalConfig tc)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(tc.getEpPurchase());
		b.appendOptionBit(tc.getEpTIP());
		b.appendOptionBit(tc.getEpVoid());
		b.appendOptionBit(tc.getEpRefund());
		b.appendOptionBit(tc.getEpPreAuth());
		b.appendOptionBit(tc.getEpCompletion());
		b.appendOptionBit(tc.getEpPurchasewithCashback());
		b.appendOptionBit(tc.getEpCashAdvance());
		return b.toString();
	}

	private String getSupervisoryCodeOptions(TerminalConfig tc)
			throws PosException {
		PosStringBuilder b = new PosStringBuilder();
		b.appendOptionBit(tc.getEspManual());
		b.appendOptionBit(tc.getEspOffline());
		b.appendOptionBit(tc.getEspMOTO());
		b.appendOptionBit(tc.getEspCashTxn());
		b.appendOptionBit(tc.getEspVoid());
		b.appendOptionBit(tc.getEspRefund());
		b.appendOptionBit(tc.getEspTIP());
		b.append(ZERO);
		return b.toString();
	}

}
