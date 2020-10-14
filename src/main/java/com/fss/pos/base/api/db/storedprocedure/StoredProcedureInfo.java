package com.fss.pos.base.api.db.storedprocedure;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.fss.pos.base.api.host.HostData;
import com.fss.pos.base.api.hsm.HsmData;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.StaticStore;
import com.fss.pos.base.services.transactionlog.TransactionResponse;

public class StoredProcedureInfo {

	public static final String MSP_CONFIG = "PSP_GETMSP";
	// public static final String TRANSACTION_VALIDATION_EXT_HPDH =
	// "PSP_TRANSACTION_NEW";
	public static final String TRANSACTION_VALIDATION_EXT_HPDH = "PSP_MERGETRANSACTION";
	public static final String TXN_RESPONSE_UPDATE = "PSP_TRANSACTIONRESPONSE";
	public static final String TXN_RESPONSE_UPDATE_AGGISO8583 = "PSP_AGGISOTRANSACTIONRESPONSE";
	public static final String INITIAL_DOWNLOAD = "PSP_PPOSDOWNLOAD";
	public static final String INITIAL_DOWNLOAD_HPDH = "PSP_HPDHPPOSDOWNLOAD";
	public static final String REMOTE_KEY_HSM_CONFIG = "PSP_GETHSMKEYDOWNLOAD";
	public static final String REMOTE_KEY_UPDATE = "PSP_REMOTEKEY_UPDATE";
	public static final String OPERATOR_LOGIN = "PSP_TERMINALLOGIN";
	public static final String OPERATOR_LOGOUT = "PSP_TERMINALLOGOUT";
	public static final String OPERATOR_CHANGE_CODE = "PSP_TERMINALCHANGEPASSWORD";
	public static final String SETTLEMENT = "PSP_SETTTLEMENT";
	public static final String SETTLEMENT_HPDH = "PSP_HPDHSETTLEMENT";
	public static final String BATCH_UPLOAD = "PSP_SETTLEMENTBATCHUPLOAD";
	public static final String BATCH_UPLOAD_HPDH = "PSP_HPDHSETTLEMENTBATCHUPLOAD";
	public static final String ISO8583_TERMINAL_REVERSAL = "PSP_AGGISO8583REVERSAL";
	public static final String TERMINAL_REVERSAL = "PSP_TERMINALREVERSAL";
	public static final String UPDATE_PAN_AFTER_P2PE = "PSP_TRANSACTIONUPDATE";
	public static final String REMOTE_PATCH_UPDATE = "PSP_TERMINALPATCHDOWNLOAD";
	public static final String LOAD_ACQUIRER_PROTOCOLS = "PSP_GETACQUIRERSTATION";
	public static final String LOAD_DEKS = "PSP_GETKEYCUSTODIAN";
	public static final String LOAD_LOGGER_CONFIG = "PSP_GETVERBOSEGEDUG";
	public static final String LOAD_CARD_MASK_CONFIG = "PSP_GETCARDMASKFORMAT";
	public static final String UPDATE_PASSWORD_MISMATCH = "PSP_PASSWORDINVALIDENTRY";
	public static final String UPDATE_REVERSAL = "PSP_UPDATEREVERSALRESPONSE";	// public static final String TRANSACTION_VALIDATION_HPDH =
	// "PSP_HPDHTRANSACTION";
	public static final String TRANSACTION_VALIDATION_HPDH = "PSP_MERGETRANSACTION";
	public static final String TRANSACTION_VALIDATION_AGG_ISO8583 = "PSP_AGGISO8583TRANSACTION";
	public static final String LOG_HOST_REQUEST = "PSP_HOSTREQUEST";
	public static final String LOG_HOST_RESPONSE = "PSP_HOSTRESPONSE";
	public static final String BENEFIT_CUTOVER_REQ = "PSP_ACQUIRER_NETWORK_MGMNT_MSG";
	public static final String DCC_VALIDATE = "PSP_DCCINQUIRY";
	public static final String DCC_REFUND_ENQUIRY = "PSP_DCCREFUND";
	public static final String DCC_RESPONSE_UPDATE = "PSP_UPDATEDCCRESPONSE";
	public static final String LOG_DCC_DETAILS = "PSP_GETDCCACKDATA";
	public static final String GENERIC_ENQUIRY = "PSP_TRANSACTIONINQUIRY";
	public static final String AUTO_REVERSAL = "PSP_GETTRANSACTIONTIMERDATA";

	// To get Hsm Details
	public static final String HSM_DETAILS = "PSP_GETACQUIRERZPKDETAILS";
	public static final String UPDATE_KEY_DETAILS = "PSP_UPDATEACQUIRERZPK";

	static {

		List<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(ResponseStatus.class);
		classes.add(HsmData.class);
		classes.add(HostData.class);
		classes.add(TransactionResponse.class);

		try {
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			for (Class<?> clazz : classes) {
				Field[] fields = clazz.getDeclaredFields();
				for (Field f : fields) {
					if (!Modifier.isStatic(f.getModifiers())) {
						f.setAccessible(true);
					}
					StaticStore.setterMethodHandles.put(f.getDeclaringClass()
							.getCanonicalName() + f.getName(),
							lookup.unreflectSetter(f));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("StoredProcedureConstants static block", e);
		}
	}

}
