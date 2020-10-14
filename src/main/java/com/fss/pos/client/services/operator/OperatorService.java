package com.fss.pos.client.services.operator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.pos.base.api.db.storedprocedure.ResponseStatus;
import com.fss.pos.base.api.db.storedprocedure.StoredProcedureInfo;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.Util;
import com.fss.pos.base.factory.ApiFactory;

@Service
public class OperatorService {

	@Autowired
	private ApiFactory apiFactory;

	@SuppressWarnings("unchecked")
	public OperatorInfo login(String userId, String code, String terminalId,
			String macId, String mspAcr) throws PosException {
		if (Util.isNullOrEmpty(userId)) {
			Log.trace("No user id");
			throw new PosException(Constants.ERR_INVALID_REQUEST);
		}
		List<Object> params = new ArrayList<Object>();
		params.add(userId);
		params.add(terminalId);
		params.add(macId);
		params.add(null);// ped id
		List<Class<?>> classList = new ArrayList<Class<?>>();
		classList.add(ResponseStatus.class);
		classList.add(OperatorInfo.class);
		try {
			List<Object> objList = apiFactory.getStoredProcedureApi().getBean(
					params, StoredProcedureInfo.OPERATOR_LOGIN, classList,
					mspAcr);
			String status = ((List<ResponseStatus>) objList.get(0)).get(0)
					.getStatus();
			if (!(Constants.SUCCESS.equals(status)) || "VI".equals(status)) {
				OperatorInfo o = new OperatorInfo();
				o.setResponseCode(status);
				return o;
			}

			OperatorInfo oi = ((List<OperatorInfo>) objList.get(1)).get(0);
			boolean codeMatched = oi.getOperatorCode().toUpperCase()
					.equals(code.toUpperCase());

			oi.setResponseCode(codeMatched ? status
					: Constants.ERR_OPERATOR_LOGIN_CODE_MISMATCH);
			updateCodeMismatch(userId, mspAcr, oi.getResponseCode());
			return oi;
		} catch (SQLException e) {
			Log.error("Operator login", e);
			throw new PosException(Constants.ERR_DATABASE);
		} catch (Exception e) {
			Log.error("OperatorService login ", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
	}

	private void updateCodeMismatch(String userId, String mspAcr,
			String responseCode) throws Exception {
		if (Util.isNullOrEmpty(userId)) {
			Log.trace("No user id");
			throw new PosException(Constants.ERR_INVALID_REQUEST);
		}
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(userId);
		params.add(Constants.SUCCESS.equals(responseCode) ? Constants.FLAG_YES
				: Constants.FLAG_NO);
		params.add(null);
		Log.info(
				"Password Mismatch update",
				apiFactory.getStoredProcedureApi().getString(params,
						StoredProcedureInfo.UPDATE_PASSWORD_MISMATCH, mspAcr));
	}

	public void logout(String userId, String sessionid, String logoutType,
			String mspAcr) {
		if (Util.isNullOrEmpty(userId))
			Log.trace("No user id");
		try {
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			params.add(logoutType);
			params.add(sessionid);
			String rspCode = apiFactory.getStoredProcedureApi().getString(
					params, StoredProcedureInfo.OPERATOR_LOGOUT, mspAcr);
			if (!Constants.SUCCESS.equals(rspCode))
				Log.debug("Operator logout failed. Response from db ", rspCode);
		} catch (Exception e) {
			Log.error("OperatorService logout ", e);
			Log.trace("Operator logout failed.");
		}
	}

	public String changeCode(String userId, String oldCode, String newCode,
			String mspAcr) throws PosException {
		if (Util.isNullOrEmpty(userId)) {
			Log.trace("No user id");
			throw new PosException(Constants.ERR_INVALID_REQUEST);
		}
		try {
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			params.add(oldCode);
			params.add(newCode);
			String rspCode = apiFactory.getStoredProcedureApi().getString(
					params, StoredProcedureInfo.OPERATOR_CHANGE_CODE, mspAcr);
			if (!Constants.SUCCESS.equals(rspCode))
				Log.debug("Change pwd failed. Response from db ", rspCode);
			return rspCode;
		} catch (SQLException e) {
			Log.error("Operator change pwd", e);
			throw new PosException(Constants.ERR_DATABASE);
		} catch (Exception e) {
			Log.error("OperatorService change pwd ", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
	}

}
