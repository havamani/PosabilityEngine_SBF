package com.fss.pos.base.api.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;

/**
 * A class to validate the message.
 *
 * @author Priyan
 */
public class Validator {

	private static final List<String> CARD_INTERACTED_TXNS;
	private static final Map<String, String> FIELD_VALIDATION_ERRORS;
	private static final List<String> COMMON_VALIDATION_BITS;
	private static final List<String> COMMON_CARD_INTERACTION_BITS;
	private static final Map<String, List<String>> TXN_VALIDATION_BITS;
	private static final List<String> NO_REVERSAL_TRANSACTIONS;
	private static final Map<String, String> ERROR_DESCRIPTIONS;

	// Newly Added
	private static final List<String> TXN_ENQUIRY;

	private static final String ERR_DESCRIPTION_COMMON = "Internal Processing Error";

	static {

		/**
		 * Reversal Not required
		 */
		NO_REVERSAL_TRANSACTIONS = new ArrayList<String>();
		NO_REVERSAL_TRANSACTIONS.add(Constants.PROC_CODE_BALANCE_INQUIRY);
		NO_REVERSAL_TRANSACTIONS.add(Constants.PROC_CODE_CASH_RECORDING);

		/**
		 * Card interacted Transactions
		 */
		CARD_INTERACTED_TXNS = new ArrayList<String>();
		CARD_INTERACTED_TXNS.add(Constants.PROC_CODE_SALE_MOTO_COMPLETION);
		CARD_INTERACTED_TXNS.add(Constants.PROC_CODE_REFUND);
		CARD_INTERACTED_TXNS.add(Constants.PROC_CODE_PREAUTH);
		CARD_INTERACTED_TXNS.add(Constants.PROC_CODE_CASHBACK);
		CARD_INTERACTED_TXNS.add(Constants.PROC_CODE_CASHADVANCE);
		CARD_INTERACTED_TXNS.add(Constants.PROC_CODE_CASHDEPOSIT);

		/**
		 * Error code mapped with iso fields
		 */
		FIELD_VALIDATION_ERRORS = new HashMap<String, String>();
		FIELD_VALIDATION_ERRORS.put(Constants.ISO_MSG_TYPE,
				Constants.ERR_NO_MSG_TYPE);
		FIELD_VALIDATION_ERRORS.put(Constants.DE3, Constants.ERR_NO_PROC_CODE);
		FIELD_VALIDATION_ERRORS.put(Constants.DE41, Constants.ERR_NO_TERMINAL);
		FIELD_VALIDATION_ERRORS.put(Constants.DE2, Constants.ERR_NO_PAN);
		FIELD_VALIDATION_ERRORS.put(Constants.DE14, Constants.ERR_NO_EXP_DATE);
		FIELD_VALIDATION_ERRORS.put(Constants.DE11, Constants.ERR_NO_STAN);
		FIELD_VALIDATION_ERRORS
				.put(Constants.DE22, Constants.ERR_NO_ENTRY_MODE);
		FIELD_VALIDATION_ERRORS.put(Constants.DE35, Constants.ERR_NO_TRACK2);
		FIELD_VALIDATION_ERRORS.put(Constants.DE25,
				Constants.ERR_NO_CONDITION_CODE);
		FIELD_VALIDATION_ERRORS.put(Constants.DE42,
				Constants.ERR_NO_MERCHANT_ID);
		FIELD_VALIDATION_ERRORS.put(Constants.DE62, Constants.ERR_NO_P62);
		FIELD_VALIDATION_ERRORS.put(Constants.DE7,
				Constants.ERR_NO_TXN_DATETIME);
		FIELD_VALIDATION_ERRORS.put(Constants.DE4, Constants.ERR_NO_AMOUNT);
		FIELD_VALIDATION_ERRORS.put(Constants.DE37, Constants.ERR_NO_RRN);
		FIELD_VALIDATION_ERRORS.put(Constants.DE38, Constants.ERR_NO_AUTH_CODE);
		FIELD_VALIDATION_ERRORS.put(Constants.DE39,
				Constants.ERR_NO_RESPONSE_CODE);
		FIELD_VALIDATION_ERRORS.put(Constants.DE55, Constants.ERR_NO_EMV_DATA);
		FIELD_VALIDATION_ERRORS.put(Constants.DE52, Constants.ERR_NO_PIN_BLOCK);
		FIELD_VALIDATION_ERRORS.put(Constants.DE54,
				Constants.ERR_NO_ADDITIONAL_AMT);
		FIELD_VALIDATION_ERRORS.put(Constants.DE63, Constants.ERR_NO_P63);

		/**
		 * Mandatory bits for all ISO requests
		 */
		COMMON_VALIDATION_BITS = new ArrayList<String>();
		COMMON_VALIDATION_BITS.add(Constants.ISO_MSG_TYPE);
		COMMON_VALIDATION_BITS.add(Constants.DE11);
		COMMON_VALIDATION_BITS.add(Constants.DE25);
		COMMON_VALIDATION_BITS.add(Constants.DE41);

		/**
		 * Fields Mandatory for card involved txns
		 */
		COMMON_CARD_INTERACTION_BITS = new ArrayList<String>(
				COMMON_VALIDATION_BITS);
		COMMON_CARD_INTERACTION_BITS.add(Constants.DE62);
		COMMON_CARD_INTERACTION_BITS.add(Constants.DE4);
		COMMON_CARD_INTERACTION_BITS.add(Constants.DE22);
		COMMON_CARD_INTERACTION_BITS.add(Constants.DE42);

		/**
		 * Fields Mandatory for txns enquiry(newly added)
		 */

		TXN_ENQUIRY = new ArrayList<String>(COMMON_VALIDATION_BITS);
		TXN_ENQUIRY.add(Constants.DE3);
		TXN_ENQUIRY.add(Constants.DE7);

		/**
		 * Mandatory bits mapped with processing codes
		 */
		Map<String, List<String>> txnValidationBits = new HashMap<String, List<String>>();

		/**
		 * Hpdh Initial Parameter Download
		 */
		ArrayList<String> li = new ArrayList<String>();
		li.add(Constants.ISO_MSG_TYPE);
		li.add(Constants.DE11);
		li.add(Constants.DE41);
		txnValidationBits.put(Constants.PROC_CODE_DOWNLOAD, li);
		li = null;

		/**
		 * Extended Initial Parameter Download
		 */
		li = new ArrayList<String>();
		li.add(Constants.ISO_MSG_TYPE);
		li.add(Constants.DE11);
		li.add(Constants.DE41);
		txnValidationBits.put(Constants.COND_CODE_DOWNLOAD, li);
		li = null;

		/**
		 * Json Download Parameters
		 */

		li = new ArrayList<String>();
		li.add(Constants.ISO_MSG_TYPE);
		li.add(Constants.DE11);
		li.add(Constants.DE41);
		txnValidationBits.put(Constants.JSON_PROC_CODE_DOWNLOAD, li);
		li = null;

		/**
		 * Logon
		 */
		li = new ArrayList<String>();
		li.add(Constants.ISO_MSG_TYPE);
		li.add(Constants.DE11);
		li.add(Constants.DE41);
		txnValidationBits.put(Constants.PROC_CODE_SESSION_KEY_DOWNLOAD, li);
		li = null;

		/**
		 * Txns Enquiry
		 */
		txnValidationBits.put(Constants.PROC_CODE_GENERIC_ENQUIRY, TXN_ENQUIRY);

		/**
		 * Sale, Moto and Completion
		 */
		txnValidationBits.put(Constants.PROC_CODE_SALE_MOTO_COMPLETION,
				COMMON_CARD_INTERACTION_BITS);

		/**
		 * DCC Enquiry
		 */
		txnValidationBits.put(Constants.PROC_CODE_DCC_ENQUIRY,
				COMMON_CARD_INTERACTION_BITS);

		/**
		 * Initial Settlement
		 */
		// li = new ArrayList<String>();
		// li.add(Constants.ISO_MSG_TYPE);
		// li.add(Constants.DE11);
		// li.add(Constants.DE41);
		// li.add(Constants.DE7);
		// li.add(Constants.DE42);
		// li.add(Constants.DE61);
		// li.add(Constants.DE47);
		// li.add(Constants.DE63);
		// txnValidationBits.put(Constants.PROC_CODE_INIT_SETTLEMENT, li);
		// li = null;

		/**
		 * Final Settlement
		 */
		li = new ArrayList<String>();
		li.add(Constants.ISO_MSG_TYPE);
		li.add(Constants.DE11);
		li.add(Constants.DE41);
		li.add(Constants.DE7);
		li.add(Constants.DE42);
		li.add(Constants.DE61);
		li.add(Constants.DE47);
		li.add(Constants.DE63);
		txnValidationBits.put(Constants.PROC_CODE_FINAL_SETTLEMENT, li);
		li = null;

		/**
		 * Void
		 */
		li = new ArrayList<String>(COMMON_VALIDATION_BITS);
		li.add(Constants.DE62);
		li.add(Constants.DE4);
		li.add(Constants.DE42);
		li.add(Constants.DE12);
		li.add(Constants.DE13);
		txnValidationBits.put(Constants.PROC_CODE_VOID, li);
		li = null;

		/**
		 * Tip
		 */
		li = new ArrayList<String>(COMMON_VALIDATION_BITS);
		li.add(Constants.DE62);
		li.add(Constants.DE4);
		li.add(Constants.DE42);
		li.add(Constants.DE12);
		li.add(Constants.DE13);
		li.add(Constants.DE54);
		li.add(Constants.DE60);
		txnValidationBits.put(Constants.PROC_CODE_TIP, li);
		li = null;

		/**
		 * Cashback
		 */
		li = new ArrayList<String>(COMMON_CARD_INTERACTION_BITS);
		li.add(Constants.DE54);
		txnValidationBits.put(Constants.PROC_CODE_CASHBACK, li);
		li = null;

		/**
		 * Pre Auth
		 */
		txnValidationBits.put(Constants.PROC_CODE_PREAUTH,
				COMMON_CARD_INTERACTION_BITS);

		/**
		 * Refund
		 */
		txnValidationBits.put(Constants.PROC_CODE_REFUND,
				COMMON_CARD_INTERACTION_BITS);

		/**
		 * Cash Advance
		 */
		txnValidationBits.put(Constants.PROC_CODE_CASHADVANCE,
				COMMON_CARD_INTERACTION_BITS);

		/**
		 * Cash Deposit
		 */
		txnValidationBits.put(Constants.PROC_CODE_CASHDEPOSIT,
				COMMON_CARD_INTERACTION_BITS);

		/**
		 * Load Money
		 */
		txnValidationBits.put(Constants.PROC_CODE_LOAD_MONEY,
				COMMON_CARD_INTERACTION_BITS);

		/**
		 * Service Creation
		 */
		txnValidationBits.put(Constants.PROC_CODE_SERVICE_CREATION,
				COMMON_CARD_INTERACTION_BITS);

		/**
		 * Balance Update
		 */
		txnValidationBits.put(Constants.PROC_CODE_BALANCE_UPDATE,
				COMMON_CARD_INTERACTION_BITS);

		/**
		 * Balance Inquiry
		 */
		li = new ArrayList<String>();
		li.add(Constants.ISO_MSG_TYPE);
		li.add(Constants.DE11);
		li.add(Constants.DE4);
		li.add(Constants.DE22);
		li.add(Constants.DE42);
		li.add(Constants.DE25);
		li.add(Constants.DE41);
		txnValidationBits.put(Constants.PROC_CODE_BALANCE_INQUIRY, li);
		li = null;

		/**
		 * Cash Recording
		 */
		li = new ArrayList<String>(COMMON_VALIDATION_BITS);
		li.add(Constants.DE61);
		txnValidationBits.put(Constants.PROC_CODE_CASH_RECORDING, li);
		li = null;

		/**
		 * Operator Login
		 */
		li = new ArrayList<String>(COMMON_VALIDATION_BITS);
		li.add(Constants.DE18);
		li.add(Constants.DE61);
		li.add(Constants.DE63);
		txnValidationBits.put(Constants.PROC_CODE_OPERATOR_LOGIN, li);
		li = null;

		/**
		 * Operator Logout
		 */
		li = new ArrayList<String>(COMMON_VALIDATION_BITS);
		li.add(Constants.DE18);
		li.add(Constants.DE47);
		li.add(Constants.DE61);
		li.add(Constants.DE63);
		txnValidationBits.put(Constants.PROC_CODE_OPERATOR_LOGOUT, li);
		li = null;

		/**
		 * Operator change code
		 */
		li = new ArrayList<String>(COMMON_VALIDATION_BITS);
		li.add(Constants.DE18);
		li.add(Constants.DE47);
		li.add(Constants.DE61);
		li.add(Constants.DE63);
		txnValidationBits.put(Constants.PROC_CODE_OPERATOR_CHANGE_CODE, li);
		li = null;

		/**
		 * Remote Key Download
		 */
		li = new ArrayList<String>(COMMON_VALIDATION_BITS);
		li.add(Constants.DE42);
		li.add(Constants.DE47);
		li.add(Constants.DE61);
		li.add(Constants.DE63);
		txnValidationBits.put(Constants.PROC_CODE_TMK_DOWNLOAD, li);
		li = null;

		/**
		 * Remote Patch Update
		 */
		li = new ArrayList<String>();
		li.add(Constants.ISO_MSG_TYPE);
		li.add(Constants.DE11);
		li.add(Constants.DE25);
		li.add(Constants.DE41);
		li.add(Constants.DE60);
		txnValidationBits.put(Constants.PROC_CODE_REMOTE_PATCH_UPDATE, li);
		li = null;

		/**
		 * Remote Patch Update Continuation
		 */
		li = new ArrayList<String>();
		li.add(Constants.ISO_MSG_TYPE);
		li.add(Constants.DE11);
		li.add(Constants.DE25);
		li.add(Constants.DE41);
		li.add(Constants.DE60);
		txnValidationBits.put(Constants.PROC_CODE_REMOTE_PATCH_UPDATE_CONT, li);
		li = null;

		TXN_VALIDATION_BITS = Collections.unmodifiableMap(txnValidationBits);

		/**
		 * Error Descriptions
		 */
		ERROR_DESCRIPTIONS = new HashMap<String, String>();
		ERROR_DESCRIPTIONS.put(Constants.SUCCESS, "Success");
		ERROR_DESCRIPTIONS.put(Constants.ERR_SYSTEM_ERROR, "System Error");
		ERROR_DESCRIPTIONS.put(Constants.ERR_IN_PARSING,
				"Error in parsing request");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_MSG_TYPE, "No message type");
		ERROR_DESCRIPTIONS
				.put(Constants.ERR_NO_PROC_CODE, "No processing code");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_PAN, "No Pan");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_EXP_DATE, "No Expiry date");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_STAN, "No stan");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_TERMINAL, "No Terminal Id");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_ENTRY_MODE, "No Entry Mode");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_TRACK2, "No Track 2");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_CONDITION_CODE,
				"No Condition code");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_AMOUNT, "No Amount");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_MERCHANT_ID, "No Merchant Id");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_P62, "No Private field P-62 ");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_TXN_DATETIME,
				"No Transmission Date and time");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_RRN, "No RRN");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_AUTH_CODE, "No Auth code");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_RESPONSE_CODE,
				"No Response code");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_EMV_DATA, "No chip card data");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_PIN_BLOCK, "No Pin Block");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_ADDITIONAL_AMT,
				"No additional amount");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_BATCH_NO, "No Batch Number");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_P63, "No Private field P-63");
		ERROR_DESCRIPTIONS.put(Constants.ERR_HSM_CONNECT,
				"Error in HSM connectivity and message exchange");
		ERROR_DESCRIPTIONS.put(Constants.ERR_INVALID_ENTRY_MODE,
				"Invalid Entry Mode");
		ERROR_DESCRIPTIONS.put(Constants.ERR_INVALID_MSG_TYPE,
				"Invalid Message Type");
		ERROR_DESCRIPTIONS.put("WT", "Invalid User Id/Password");
		ERROR_DESCRIPTIONS.put(Constants.ERR_INVALID_PROC_CODE,
				"Invalid Processing code");
		ERROR_DESCRIPTIONS
				.put(Constants.ERR_NO_TID_DOWNLOAD,
						"No remaining terminal data for parameter download continuation");
		ERROR_DESCRIPTIONS.put(Constants.ERR_TXN_TIMEDOUT,
				"Transaction Timed Out");
		ERROR_DESCRIPTIONS.put(Constants.ERR_OPERATOR_LOGIN_CODE_MISMATCH,
				"Operator login password mismatched");
		ERROR_DESCRIPTIONS.put(Constants.ERR_RKD_INVALID_KEY_SCHEME,
				"Invalid Key scheme in remote key download");
		ERROR_DESCRIPTIONS.put(Constants.ERR_FETCHING_HSM_CONFIG,
				"Error in fetching hsm config");
		ERROR_DESCRIPTIONS.put(Constants.ERR_UPDATING_HSM_KEY,
				"Error in updating lmk key");
		ERROR_DESCRIPTIONS.put(Constants.ERR_DATABASE, "Database error");
		ERROR_DESCRIPTIONS.put(Constants.ERR_KEY_DOWNLOAD_PARSING,
				"Key download request parsing error");
		ERROR_DESCRIPTIONS.put(Constants.ERR_DONT_CARE, "Don't care");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_RSP_CODE_FROM_HOST,
				"No Response code from Host");
		ERROR_DESCRIPTIONS.put(Constants.ERR_GETTING_HOST_CONFIG,
				"Error getting host info");
		ERROR_DESCRIPTIONS.put(Constants.ERR_GETTING_HSM_CONFIG,
				"Error getting hsm info");
		ERROR_DESCRIPTIONS.put(Constants.ERR_P2PE, "Error in p2pe");
		ERROR_DESCRIPTIONS.put(Constants.ERR_PIN_CONFIG,
				"Check Pin Configuration");
		ERROR_DESCRIPTIONS.put(Constants.ERR_HOST_EMV_PARSING,
				"Host Emv data parsing error");
		ERROR_DESCRIPTIONS.put(Constants.ERR_INVALID_MSP, "MSP not registered");
		ERROR_DESCRIPTIONS
				.put(Constants.ERR_INVALID_REQUEST, "Invalid request");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_ZONAL_KEY,
				"No Zonal Key Configured or Active");
		ERROR_DESCRIPTIONS.put(Constants.ERR_INVALID_ACQUIRER_PROTOCOL,
				"Invalid Acquirer Protocol");

		// procedure
		ERROR_DESCRIPTIONS.put("UA", "Message Version No");
		ERROR_DESCRIPTIONS.put("UB", "Message Age");
		ERROR_DESCRIPTIONS.put("UC",
				"Duplicate Message(Terminal Id, Stan and Batch No)");
		ERROR_DESCRIPTIONS.put("UD", "Message Frequency");
		ERROR_DESCRIPTIONS.put("UE", "Check Terminal Id Configured and active");
		ERROR_DESCRIPTIONS.put("UF", "Check POS Device configured and  active");
		ERROR_DESCRIPTIONS
				.put("UG", "Check PED Device configured and   active");
		ERROR_DESCRIPTIONS.put("UH", "Check Store configured and  active");
		ERROR_DESCRIPTIONS.put("UI", "Check Merchant configured and active");
		ERROR_DESCRIPTIONS.put("UJ", "Check operator configured and active");
		ERROR_DESCRIPTIONS.put("UK", "Check Transaction business hours");
		ERROR_DESCRIPTIONS.put("UL", "Check last transaction status");
		ERROR_DESCRIPTIONS.put("UM", "Check Settlement time");
		ERROR_DESCRIPTIONS.put("UN", "Check Parameter Download Pending");
		ERROR_DESCRIPTIONS.put("UO", "Check Key download pending");
		ERROR_DESCRIPTIONS.put("UP", "Check Terminal application pending");
		ERROR_DESCRIPTIONS.put("UQ", "Check transaction terminal limit");
		ERROR_DESCRIPTIONS
				.put("UR",
						"Check sale with tip enabled, if tip amount present in the sale request");
		ERROR_DESCRIPTIONS.put("US",
				"Check sale txn is present in the same batch, for void txn");
		ERROR_DESCRIPTIONS
				.put("UT",
						"Check sale txn is present in the same batch, for void txn and should not be voided");
		ERROR_DESCRIPTIONS.put("UU",
				"Check sale txn is present in the same batch, for tip txn");
		ERROR_DESCRIPTIONS
				.put("UV",
						"Check sale txn is present in the same batch, for tip txn and should not be voided");
		ERROR_DESCRIPTIONS.put("UW", "Card listed in the negative card");
		ERROR_DESCRIPTIONS.put("UV", "Offline Transaction");
		ERROR_DESCRIPTIONS.put("UY", "Manual Transaction");
		ERROR_DESCRIPTIONS.put("UZ", "PIN Block");
		ERROR_DESCRIPTIONS.put("VA",
				"Check acquirer configured, Mapped and active");
		ERROR_DESCRIPTIONS.put("VB", "Check invoice No duplicate");
		ERROR_DESCRIPTIONS.put("VC", "Stored Procedure Error");
		ERROR_DESCRIPTIONS.put("VD", "Check current batch no");
		ERROR_DESCRIPTIONS.put("VE",
				"If PIN enabled in terminal and PIN disabled in DB");
		ERROR_DESCRIPTIONS.put("VF", "Card Range not configured");
		ERROR_DESCRIPTIONS.put("VG", "Terminal id not mapped to this operator");
		ERROR_DESCRIPTIONS.put("VH",
				"Terminal old password invalid - change password");
		ERROR_DESCRIPTIONS.put("VI", "Change Operator Password");
		ERROR_DESCRIPTIONS.put("VJ", "Duplicate Stan");
		ERROR_DESCRIPTIONS.put("VK", "Operator already logged in");
		ERROR_DESCRIPTIONS.put("VL", "No Record for reversal");
		ERROR_DESCRIPTIONS.put("VM", "Session timed out Please do login");
		ERROR_DESCRIPTIONS.put("VN", "Transaction not allowed by the terminal");
		ERROR_DESCRIPTIONS.put("VO",
				"Offline Transaction not allowed by the terminal");
		ERROR_DESCRIPTIONS.put("VP",
				"Manual Transaction not allowed by the terminal");
		ERROR_DESCRIPTIONS.put("VQ", "Store admin not activated");
		ERROR_DESCRIPTIONS.put("VR", "No transaction are there to settle");
		ERROR_DESCRIPTIONS.put("VS", "Invalid Request");
		ERROR_DESCRIPTIONS.put("VT", "Acquirer Zonal Key not available");
		ERROR_DESCRIPTIONS.put("VU", "Batch No Mismatch");
		ERROR_DESCRIPTIONS.put("VV", "Tip with Purchase not allowed");
		ERROR_DESCRIPTIONS.put("VW",
				"Max Debit Transaction reached by the Terminal");
		ERROR_DESCRIPTIONS.put("VX",
				"Max Credit Transaction reached by the Terminal");
		ERROR_DESCRIPTIONS.put("VY",
				"Max Offline Transaction reached by the Terminal");
		ERROR_DESCRIPTIONS.put("VZ",
				"Max Reversal Transaction reached by the Terminal");
		ERROR_DESCRIPTIONS.put("WA", "Purchase Limit Exceeded");
		ERROR_DESCRIPTIONS.put("WB", "Cashback Limit Exceeded");
		ERROR_DESCRIPTIONS.put("WC", "Cash Advance Limit Exceeded");
		ERROR_DESCRIPTIONS.put("WD", "Cash Deposit Limit Exceeded");
		ERROR_DESCRIPTIONS.put("WE", "Moto Limit Exceeded");
		ERROR_DESCRIPTIONS.put("WF", "Refund Limit Exceeded");
		ERROR_DESCRIPTIONS.put("WG", "Terminal Risk Inactive");
		ERROR_DESCRIPTIONS.put("WH", "Card Group Inactive");
		ERROR_DESCRIPTIONS.put("WI", "P2pe not configured");
		ERROR_DESCRIPTIONS.put("WJ", "Pin not configured");
		ERROR_DESCRIPTIONS.put("WK", "Acquirer not found");
		ERROR_DESCRIPTIONS.put("WL", "Password Expired");
		ERROR_DESCRIPTIONS.put("WM", "HSM Inactive");
		ERROR_DESCRIPTIONS.put("WN", "Operator locked");
		// For DCC
		ERROR_DESCRIPTIONS.put(Constants.NON_DCC_TRANSACTION,
				"Terminal is not DCC Enabled");
		ERROR_DESCRIPTIONS.put(Constants.QUOTATIONID_DOESNOT_EXISTS,
				"Quotation does not exist");
		ERROR_DESCRIPTIONS
				.put(Constants.EXCHANGERATE_DOESNOT_EXISTS,
						"Exchange rate not available for the card / Card not listed in the DCC BIN");
		ERROR_DESCRIPTIONS
				.put(Constants.DCC_ENQUIRY_RESPONSE_ERR,
						"DCC is not available. Perform authorisation in merchant currency");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_TRANSACTION,
				"No Transaction Found");
		ERROR_DESCRIPTIONS.put(Constants.ERR_REFUND, "Refund Already Done");
		ERROR_DESCRIPTIONS.put(Constants.ERR_PREAUTH_NOT_FOUND,
				"No Transaction Found");
		ERROR_DESCRIPTIONS.put(Constants.ERR_PREAUTH_CMPL,
				"PreAuth Already Done");
		ERROR_DESCRIPTIONS.put(Constants.ERR_NO_RESPONSE_MONEX,
				"No Response from Monex");
		ERROR_DESCRIPTIONS.put(Constants.LOGON_NOT_ALLOWED,
				"Logon not Applicable for DUKPT Key type");
	}

	/**
	 * To validate the client request
	 *
	 * @param isoBuffer
	 *            the buffer to be validated for transaction
	 * @throws PosException
	 *             thrown when validation failed
	 */
	public static void validate(IsoBuffer isoBuffer) throws PosException {

		if (isoBuffer.isFieldEmpty(Constants.DE3)) {
			throw new PosException(Constants.ERR_NO_PROC_CODE);
		}

		List<String> bits = TXN_VALIDATION_BITS.get(isoBuffer
				.get(Constants.DE3).substring(0, 2));
		String error = null;
		if (bits == null) {
			throw new PosException(Constants.ERR_NO_PROC_CODE);
		} else {
			for (String bit : bits) {
				if (isoBuffer.isFieldEmpty(bit)) {
					error = FIELD_VALIDATION_ERRORS.get(bit);
				}
			}
		}
		// if (error == null)
		// error = validateConditionalBits(isoBuffer);
		if (error != null) {
			throw new PosException(error);
		}

		if (Constants.MSG_TYPE_OFFLINE.equals(isoBuffer
				.get(Constants.ISO_MSG_TYPE))) {
			if (isoBuffer.isFieldEmpty(Constants.DE39)) {
				throw new PosException(Constants.ERR_NO_RESPONSE_CODE);
			}
			if (isoBuffer.isFieldEmpty(Constants.DE38)) {
				throw new PosException(Constants.ERR_NO_AUTH_CODE);
			}
		}
	}

	public static String validateConditionalBits(IsoBuffer isoBuffer) {

		if (CARD_INTERACTED_TXNS.contains(isoBuffer.get(Constants.DE3)
				.substring(0, 2))
				&& !Constants.MSG_TYPE_BATCHUPLOAD.equals(isoBuffer
						.get(Constants.ISO_MSG_TYPE))
				&& !Constants.MSG_TYPE_OFFLINE.equals(isoBuffer
						.get(Constants.ISO_MSG_TYPE))) {

			if (!Constants.MSG_TYPE_REV_400.equals(isoBuffer
					.get(Constants.ISO_MSG_TYPE))) {
				String entryMode = isoBuffer.get(Constants.DE22)
						.substring(0, 2);

				if (Constants.ENTRY_MODE_MANUAL.equals(entryMode)) {
					if (isoBuffer.isFieldEmpty(Constants.DE2)) {
						return Constants.ERR_NO_PAN;
					}
				} else if (Constants.ENTRY_MODE_MAG_STRIPE.equals(entryMode)
						|| Constants.ENTRY_MODE_ICC_TO_MAG_FALLBACK
								.equals(entryMode)) {
					if (isoBuffer.isFieldEmpty(Constants.DE35)) {
						return Constants.ERR_NO_TRACK2;
					}
				} else if (Constants.ENTRY_MODE_ICC_CB.equals(entryMode)
						|| Constants.ENTRY_MODE_ICC_CL.equals(entryMode)) {
					if (isoBuffer.isFieldEmpty(Constants.DE55)) {
						return Constants.ERR_NO_EMV_DATA;
					}
				}
			}
		}
		return null;
	}

	public static boolean isChipCardTxn(IsoBuffer isoBuffer) {
		if (!isoBuffer.isFieldEmpty(Constants.DE22)) {
			String e = isoBuffer.get(Constants.DE22).substring(0, 2);
			return Constants.ENTRY_MODE_ICC_CB.equals(e)
					|| Constants.ENTRY_MODE_ICC_CL.equals(e);
		}
		return false;
	}

	public static boolean isPinEnabledTransaction(IsoBuffer isoBuffer) {
		return !isoBuffer.isFieldEmpty(Constants.DE52);
	}

	public static String getErrorDescription(String errorCode) {
		String desc = ERROR_DESCRIPTIONS.containsKey(errorCode) ? ERROR_DESCRIPTIONS
				.get(errorCode) : ERR_DESCRIPTION_COMMON;
		Log.info(errorCode, desc);
		return desc;
	}

	public static boolean isReversalRequired(IsoBuffer isoBuffer) {
		return !(NO_REVERSAL_TRANSACTIONS.contains(isoBuffer.get(Constants.DE3)
				.substring(0, 2)));
	}

	public static boolean isOperatorTxn(IsoBuffer isoBuffer) {
		String p = isoBuffer.get(Constants.DE3).substring(0, 2);
		return Constants.PROC_CODE_OPERATOR_LOGIN.equals(p)
				|| Constants.PROC_CODE_OPERATOR_CHANGE_CODE.equals(p)
				|| Constants.PROC_CODE_OPERATOR_LOGOUT.equals(p);
	}

	public static boolean validatePinConfig(IsoBuffer isoBuffer)
			throws PosException {
		boolean pinEnabledTxn = isPinEnabledTransaction(isoBuffer);

		return pinEnabledTxn;
	}

	public static boolean isJSONValid(String jsonvalid) {
		try {
			new JSONObject(jsonvalid);
		} catch (JSONException ex) {
			// edited, to include @Arthur's comment
			// e.g. in case JSONArray is valid as well...
			try {
				new JSONArray(jsonvalid);
			} catch (JSONException ex1) {
				return false;
			}
		}
		return true;
	}

}
