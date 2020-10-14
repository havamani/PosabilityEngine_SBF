package com.fss.pos.client.services.download.patchupdate;

import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.fss.pos.base.api.db.MsSqlDatabaseApi;
import com.fss.pos.base.api.db.storedprocedure.StoredProcedureInfo;
import com.fss.pos.base.commons.Config;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;

@RemotePatchUpdate(Constants.DB_TYPE_MSSQL)
public class RemotePatchUpdateMsSqlDao extends MsSqlDatabaseApi implements
		RemotePatchUpdateDao {

	@Autowired
	private Config config;

	@Autowired
	private RemotePatchUpdateService remotePatchUpdateService;

	@SuppressWarnings("unchecked")
	@Override
	protected Object assignValuesFromResultSet(Object resultSetObject,
			Object object) throws Exception {

		RemotePatchUpdateData rpud = (RemotePatchUpdateData) object;
		List<Object> ol = (List<Object>) resultSetObject;
		boolean results = (boolean) ol.get(0);
		CallableStatement cst = (CallableStatement) ol.get(1);
		if (results) {
			ResultSet rs = cst.getResultSet();
			if (rs == null)
				return null;
			rs.next();
			String status = rs.getString("status");
			Log.debug("Remote patch update Status", status);

			if (!Constants.SUCCESS.equals(status)) {
				rpud.setRespCode(status);
				rs.close();
				Log.debug("Remote patch update response", rpud.toString());
				return rpud;
			}
			rpud.setRespCode(status);
			Blob blob = rs.getBlob("fileDataBytes");
			long blobLength = blob.length();
			int maxBytes = Integer.parseInt(config.getPatchUpdateMaxBytes());
			byte[] b = null;
			if (rpud.isContinuation()) {
				if (blobLength < (rpud.getBytesTransferred() + maxBytes)) {
					b = blob.getBytes(rpud.getBytesTransferred() + 1,
							(((int) blobLength) - rpud.getBytesTransferred()));
					rpud.setContinuation(false);
				} else {
					b = blob.getBytes(rpud.getBytesTransferred() + 1, maxBytes);
					rpud.setContinuation(true);
				}
			} else {
				if (blobLength < maxBytes) {
					b = blob.getBytes(1, (int) blobLength);
					rpud.setContinuation(false);
				} else {
					b = blob.getBytes(1, maxBytes);
					rpud.setContinuation(true);
				}
			}

			rpud.setFileName(rs.getString("fileName"));
			rpud.setFileData(new String(b, StandardCharsets.ISO_8859_1));
			rpud.setBytesTransferred(rpud.getBytesTransferred() + b.length);
			rpud.setFileTransferIndex(rpud.getFileTransferIndex() + 1);
			rpud.setTotalFileSize(blobLength);
			rs.close();
		}
		return rpud;
	}

	@Override
	public RemotePatchUpdateData getFileBytes(String terminalId,
			RemotePatchUpdateData rpud, String mspAcr) throws PosException {
		try {
			ArrayList<Object> params = new ArrayList<Object>();
			params.add(terminalId);
			return (RemotePatchUpdateData) executeStoredProcedure(mspAcr,
					StoredProcedureInfo.REMOTE_PATCH_UPDATE, 1, params, rpud);
		} catch (SQLException e) {
			Log.error("PatchUpdateMsSqlDao getFileBytes ", e);
			throw new PosException(Constants.ERR_DATABASE);
		} catch (Exception e) {
			Log.error("PatchUpdateMsSqlDao getFileBytes ", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
	}

}
