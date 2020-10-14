package com.fss.pos.client.services.download.remotekey;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.pos.base.api.db.storedprocedure.ResponseStatus;
import com.fss.pos.base.api.db.storedprocedure.StoredProcedureInfo;
import com.fss.pos.base.api.hsm.HsmApi;
import com.fss.pos.base.api.hsm.HsmResponse;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.factory.ApiFactory;

@Service
public class RemoteKeyDownloadService {

	@Autowired
	private ApiFactory apiFactory;

	public HsmResponse downloadKey(KeyDownloadClientData keyClientData,
			boolean isMasterKey, String terminalId, String mspAcr)
			throws PosException {
		try {
			KeyDownloadConfig keyConfig = getKeyConfiguration(mspAcr,
					terminalId, keyClientData.getType(), keyClientData.getId());
			String keyId = isMasterKey ? keyClientData.getId()
					: Constants.HSM_PIN_ENC_KEY;
			if (Constants.KEY_SCHEME_MASTER_SESSION.equals(keyClientData
					.getScheme())) {
				HsmApi hsmApi = apiFactory.getHsmApi(keyConfig.getHsmModel());
				hsmApi.setDestination(keyConfig.getStationName());
				HsmResponse hsmGenResponse = hsmApi.generateKey(
						keyConfig.getIndex(), keyId,
						keyConfig.getKeyLengthType(), keyConfig.getCheckSum(), mspAcr);
				updateKey(mspAcr, terminalId,
						hsmGenResponse.getLmkEncryptedKey(),
						hsmGenResponse.getChecksum(), keyClientData.getType(),
						isMasterKey);
				return hsmGenResponse;
			} else {
				// handle dukpt
				throw new PosException(Constants.ERR_RKD_INVALID_KEY_SCHEME);
			}
		} catch (PosException e) {
			throw e;
		} catch (SQLException e) {
			Log.error("RemoteKeyDownloadService process ", e);
			throw new PosException(Constants.ERR_DATABASE);
		} catch (Exception e) {
			Log.error("RemoteKeyDownloadService process ", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
	}

	@SuppressWarnings("unchecked")
	private KeyDownloadConfig getKeyConfiguration(String mspAcr,
			String terminalId, String keyType, String keyId)
			throws PosException {
		List<Object> params = new ArrayList<Object>();
		params.add(terminalId);
		params.add(keyType);
		params.add(keyId);
		List<Class<?>> classList = new ArrayList<Class<?>>();
		classList.add(ResponseStatus.class);
		classList.add(KeyDownloadConfig.class);
		List<Object> objList;
		try {
			objList = apiFactory.getStoredProcedureApi().getBean(params,
					StoredProcedureInfo.REMOTE_KEY_HSM_CONFIG, classList,
					mspAcr);
			String status;
			if (!Constants.SUCCESS
					.equals(status = ((List<ResponseStatus>) objList.get(0))
							.get(0).getStatus())) {
				Log.debug("Response from "
						+ StoredProcedureInfo.REMOTE_KEY_HSM_CONFIG, status);
				throw new PosException(status);
			}
			return ((List<KeyDownloadConfig>) objList.get(1)).get(0);
		} catch (PosException e) {
			throw e;
		} catch (Exception e) {
			Log.error("RKD getKeyConfiguration ", e);
			throw new PosException(Constants.ERR_FETCHING_HSM_CONFIG);
		}
	}

	private void updateKey(String mspAcr, String terminalId, String key,
			String checkSum, String keyType, boolean isMasterKey)
			throws Exception {
		List<Object> params = new ArrayList<Object>();
		params.add(terminalId);
		params.add(key);
		params.add(checkSum);
		params.add(isMasterKey ? "1" : "2");
		params.add(null);
		params.add(null);
		params.add(keyType);
		try {
			String respCode = apiFactory.getStoredProcedureApi().getString(
					params, StoredProcedureInfo.REMOTE_KEY_UPDATE, mspAcr);
			Log.debug("Response from " + StoredProcedureInfo.REMOTE_KEY_UPDATE,
					respCode);
			if (!Constants.SUCCESS.equals(respCode))
				throw new PosException(respCode);
		} catch (PosException e) {
			throw e;
		} catch (Exception e) {
			Log.error("RKD updateKey ", e);
			throw new PosException(Constants.ERR_UPDATING_HSM_KEY);
		}
	}
}
