package com.fss.pos.client.services.download.patchupdate;

import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.fss.pos.base.api.db.OracleDataBaseApi;
import com.fss.pos.base.api.db.storedprocedure.StoredProcedureInfo;
import com.fss.pos.base.commons.Config;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;

@RemotePatchUpdate(Constants.DB_TYPE_ORACLE)
public class RemotePatchUpdateOracleDao extends OracleDataBaseApi implements
		RemotePatchUpdateDao {

	@Autowired
	private Config config;

	@Override
	protected Object assignValuesFromResultSet(Object resultSetObject,
			Object object) throws Exception {
		RemotePatchUpdateData rpud = (RemotePatchUpdateData) object;
		byte[] b = null;
		ResultSet rs = (ResultSet) resultSetObject;
		if (rs == null)
			return b;
		rs.next();
		String status = rs.getString(1);
		if (!Constants.SUCCESS.equals(status)) {
			rpud.setRespCode(status);
			return rpud;
		}
		rs.next();
		Blob blob = rs.getBlob(1);
		long blobLength = blob.length();
		int maxBytes = Integer.parseInt(config.getPatchUpdateMaxBytes());
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
