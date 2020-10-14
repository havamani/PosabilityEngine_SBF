package com.fss.pos.client.services.settlement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.pos.base.api.client.ClientApi;
import com.fss.pos.base.api.db.storedprocedure.BatchStatus;
import com.fss.pos.base.api.db.storedprocedure.ResponseStatus;
import com.fss.pos.base.api.db.storedprocedure.StoredProcedureInfo;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.PosUtil;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.base.factory.ApiFactory;

@Service
public class SettlementService {

	@Autowired
	private ApiFactory apiFactory;

	private static final String MISMATCH_RSP = "95";
	public static final String SETTLEMENT_TYPE_INITIAL = "S";
	public static final String SETTLEMENT_TYPE_FINAL = "F";
	public static final String SETTLEMENT_TYPE_ZERO = "Z";

	public void doBatchUpload(IsoBuffer isoBuffer, String userId, String msp,ClientApi clientApi ) {
		List<Object> params = new ArrayList<Object>();
		params.add(isoBuffer.get(Constants.DE41));
		params.add(isoBuffer.get(Constants.DE37));
		params.add(isoBuffer.get(Constants.DE3).substring(0, 2)
				+ isoBuffer.get(Constants.DE25));
		params.add(isoBuffer.get(Constants.DE4));
		params.add(isoBuffer.get(Constants.DE42));
		params.add(isoBuffer.get(Constants.DE62));
		params.add(userId);
		params.add(PosUtil.maskCardNumber(isoBuffer.get(Constants.DE2), msp));
		try {
			params.add(Util.hashSHA512(isoBuffer.get(Constants.DE2), ""));
		} catch (Exception e1) {
			Log.error("Card no hashing", e1);
		}
		params.add(isoBuffer.get(Constants.DE22));
		params.add(isoBuffer.get(Constants.DE11));
		params.add(isoBuffer.get(Constants.DE3).substring(2, 4));
		params.add(isoBuffer.isFieldEmpty(Constants.DE54) ? null : isoBuffer
				.get(Constants.DE54));
		params.add(isoBuffer.get(Constants.DE7));
		params.add(isoBuffer.get(Constants.DE38));
		try {
			String rspCode = apiFactory.getStoredProcedureApi().getString(
					params, StoredProcedureInfo.BATCH_UPLOAD, msp);
			Log.info("Response Code " , rspCode);
		/*	Log.info("Response from " + ((SettlementApi) clientApi).getSettlementBatchProcedureName(),
					rspCode);*/
			if (!Constants.SUCCESS.equals(rspCode))
				Log.debug("Batch upload Failed. Response from procedure",
						rspCode);
		} catch (Exception e) {
			Log.error("SettlementService doBatchUpload ", e);
		}
	}

	public String doSettlement(String terminalId, BatchTotals batchData,
			String mspAcr, String batchNo, String type, SettlementFormat format, String procedureName)
			throws PosException {
		if (SettlementFormat.TRANSACTION_FORMAT.equals(format)) {
			return doSettlementTrxnBased(terminalId,
					(TransactionBatchTotals) batchData, mspAcr, batchNo, type, procedureName);
		} else if (SettlementFormat.STANDARD_FORMAT.equals(format)) {
			return doStandardSettlement(terminalId,
					(StandardBatchTotals) batchData, mspAcr, batchNo, type, procedureName);
		} else {
			throw new PosException(null);
		}
	}

	@SuppressWarnings("unchecked")
	private String doSettlementTrxnBased(String terminalId,
			TransactionBatchTotals batchData, String mspAcr, String batchNo,
			String type, String procedureName) throws PosException {

		List<String> batchCounters = new ArrayList<String>();
		batchCounters.add(batchData.getSaleCount());
		batchCounters.add(batchData.getSaleAmount());
		batchCounters.add(batchData.getTipCount());
		batchCounters.add(batchData.getTipAmount());
		batchCounters.add(batchData.getVoidCount());
		batchCounters.add(batchData.getVoidAmount());
		batchCounters.add(batchData.getRefundCount());
		batchCounters.add(batchData.getRefundAmount());
		batchCounters.add(batchData.getCompletionCount());
		batchCounters.add(batchData.getCompletionAmount());
		batchCounters.add(batchData.getCashBackCount());
		batchCounters.add(batchData.getCashBackAmount());
		batchCounters.add(batchData.getCashAdvanceCount());
		batchCounters.add(batchData.getCashAdvanceAmount());
		batchCounters.add(batchData.getMotoCount());
		batchCounters.add(batchData.getMotoAmount());
		batchCounters.add(batchData.getCashDepositCount());
		batchCounters.add(batchData.getCashDepositAmount());

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
		classList.add(BatchStatus.class);
		try {
			List<Object> objList = apiFactory.getStoredProcedureApi().getBean(
					params, procedureName, classList, mspAcr);
			String status = ((List<ResponseStatus>) objList.get(0)).get(0)
					.getStatus();
			String batchNo1 = ((List<BatchStatus>) objList.get(1)).get(0)
					.getBatchNo();
			Log.info("Response from " + procedureName, status + "\t" + "BatchNo" + batchNo1);
			String[] arr = status.split(",");
			if (MISMATCH_RSP.equals(arr[0]))
				Log.info("Settlement Error - ProcCode : ", arr[1]
						+ " Type :  "
						+ new String("1".equals(arr[2]) ? "Count Mismatched"
								: "Amount Mismatched"));
			return arr[0] + "," + batchNo1;
		} catch (SQLException e) {
			Log.error("doSettlementTrxnBased ", e);
			throw new PosException(Constants.ERR_DATABASE);
		} catch (Exception e) {
			Log.error("doSettlementTrxnBased ", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}

	}

	@SuppressWarnings("unchecked")
	private String doStandardSettlement(String terminalId,
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
		classList.add(BatchStatus.class);
		try {
			List<Object> objList = apiFactory.getStoredProcedureApi().getBean(
					params, procedureName, classList, mspAcr);
			String status = ((List<ResponseStatus>) objList.get(0)).get(0)
					.getStatus();
			String batchNo1 = ((List<BatchStatus>) objList.get(1)).get(0)
					.getBatchNo();
			Log.info("Response from " + procedureName, status + "\t" + "BatchNo" + batchNo1);
			String[] arr = status.split(",");
			if (MISMATCH_RSP.equals(arr[0]))
				Log.info("Settlement Error - ProcCode : ", arr[1]
						+ " Type :  "
						+ new String("1".equals(arr[2]) ? "Count Mismatched"
								: "Amount Mismatched"));
			return arr[0] + "," + batchNo1;
		} catch (SQLException e) {
			Log.error("doStandardSettlement ", e);
			throw new PosException(Constants.ERR_DATABASE);
		} catch (Exception e) {
			Log.error("doStandardSettlement ", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}

	}

}
