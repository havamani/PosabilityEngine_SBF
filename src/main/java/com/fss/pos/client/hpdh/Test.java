package com.fss.pos.client.hpdh;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.fss.pos.base.api.db.storedprocedure.ResponseStatus;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.base.factory.ApiFactory;
import com.fss.pos.client.services.settlement.BatchTotals;
import com.fss.pos.client.services.settlement.SettlementFormat;
import com.fss.pos.client.services.settlement.StandardBatchTotals;

public class Test {
	

	@Autowired
	private static ApiFactory apiFactory;

	private static final List<Method> BATCH_DATA_METHODS;
	public static final String SETTLEMENT_TYPE_ZERO = "Z";
	private static final String MISMATCH_RSP = "95";

	static {

		LinkedList<String> batchFields = new LinkedList<String>();
		batchFields.add("captureCardsNoOfDebits");
		batchFields.add("captureCardsAmtOfDebits");
		batchFields.add("captureCardsNoOfCredits");
		batchFields.add("captureCardsAmtOfCredits");
		batchFields.add("debitCardsNoOfDebits");
		batchFields.add("debitCardsAmtOfDebits");
		batchFields.add("debitCardsNoOfCredits");
		batchFields.add("debitCardsAmtOfCredits");
		batchFields.add("authCardsNoOfDebits");
		batchFields.add("authCardsAmtOfDebits");
		batchFields.add("authCardsNoOfCredits");
		batchFields.add("authCardsAmtOfCredits");

		BATCH_DATA_METHODS = new LinkedList<Method>();
		try {
			for (String field : batchFields)
				BATCH_DATA_METHODS.add(StandardBatchTotals.class
						.getDeclaredMethod("set" + Util.capitalize(field),
								String.class));
		} catch (Exception e) {
			Log.error("Batch data static block", e);
		}
	}

	/*
	 * public static void main(String[] args) throws Exception { BatchTotals bd =
	 * parseSettlementRequest(
	 * "007000000021100000000000000000000000000000000000000000000000000000000000000000000000000000"
	 * );
	 * 
	 * String rspCode = doSettlement( "00045021", bd, "ALB", "000038", "S",
	 * SettlementFormat.STANDARD_FORMAT, "PSP_HPDHSETTLEMENT");
	 * 
	 * }
	 */
	
	public static String doSettlement(String terminalId, BatchTotals batchData,
			String mspAcr, String batchNo, String type, SettlementFormat format, String procedureName)
			throws PosException {
		if (SettlementFormat.STANDARD_FORMAT.equals(format)) {
			return doStandardSettlement(terminalId,
					(StandardBatchTotals) batchData, mspAcr, batchNo, type, procedureName);
		} else {
			throw new PosException(null);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static String doStandardSettlement(String terminalId,
			StandardBatchTotals batchData, String mspAcr, String batchNo,
			String type, String procedureName) throws PosException {

		List<String> batchCounters = new ArrayList<String>();
		batchCounters.add(batchData.getCaptureCardsNoOfDebits());
		batchCounters.add(batchData.getCaptureCardsAmtOfDebits());
		batchCounters.add(batchData.getCaptureCardsNoOfCredits());
		batchCounters.add(batchData.getCaptureCardsAmtOfCredits());
		batchCounters.add(batchData.getDebitCardsNoOfDebits());
		batchCounters.add(batchData.getDebitCardsAmtOfDebits());
		batchCounters.add(batchData.getDebitCardsNoOfCredits());
		batchCounters.add(batchData.getDebitCardsAmtOfCredits());
		batchCounters.add(batchData.getAuthCardsNoOfDebits());
		batchCounters.add(batchData.getAuthCardsAmtOfDebits());
		batchCounters.add(batchData.getAuthCardsNoOfCredits());
		batchCounters.add(batchData.getAuthCardsAmtOfCredits());

		int countOfAll = 0;
		for (String c : batchCounters)
			countOfAll += Long.parseLong(c);
		if (countOfAll == 0)
			type = SETTLEMENT_TYPE_ZERO;

		List<Object> params = new ArrayList<Object>();
		params.add(terminalId);
		params.add(batchNo);
		params.add(type);
		for (String c : batchCounters)
			params.add(c);

		List<Class<?>> classList = new ArrayList<Class<?>>();
		classList.add(ResponseStatus.class);
		try {
			List<Object> objList = apiFactory.getStoredProcedureApi().getBean(
					params, procedureName, classList, mspAcr);
			String status = ((List<ResponseStatus>) objList.get(0)).get(0)
					.getStatus();
			Log.info("Response from " + procedureName, status);
			String[] arr = status.split(",");
			if (MISMATCH_RSP.equals(arr[0]))
				Log.info("Settlement Error - ProcCode : ", arr[1]
						+ " Type :  "
						+ new String("1".equals(arr[2]) ? "Count Mismatched"
								: "Amount Mismatched"));
			return arr[0];
		} catch (SQLException e) {
			Log.error("doStandardSettlement ", e);
			throw new PosException(Constants.ERR_DATABASE);
		} catch (Exception e) {
			Log.error("doStandardSettlement ", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}

	}

	public static BatchTotals parseSettlementRequest(Object batchData)
			throws PosException {
		String bd = (String) batchData;
		try {
			StandardBatchTotals batchDataObj = new StandardBatchTotals();
			int i = 0;
			while (i < BATCH_DATA_METHODS.size()) {
				Method m = BATCH_DATA_METHODS.get(i);
				m.invoke(batchDataObj, bd.substring(0, 3));
				m = BATCH_DATA_METHODS.get(i + 1);
				m.invoke(batchDataObj, bd.substring(3, 15));
				i += 2;
				bd = bd.substring(15);
			}
			return batchDataObj;
		} catch (Throwable e) {
			Log.error("HpdhClientApi parseSettlementRequest ", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
	}
}
