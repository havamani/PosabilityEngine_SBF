package com.fss.pos.client.services.download.patchupdate;

import com.fss.pos.base.api.db.Dao;
import com.fss.pos.base.commons.PosException;

public interface RemotePatchUpdateDao extends Dao {

	public RemotePatchUpdateData getFileBytes(String terminalId,
			RemotePatchUpdateData rpud, String mspAcr) throws PosException;

}
