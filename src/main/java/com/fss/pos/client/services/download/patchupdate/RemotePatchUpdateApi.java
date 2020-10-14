package com.fss.pos.client.services.download.patchupdate;

import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.PosException;

/**
 * The interface to enable remote patch update functionality to the client.
 * Independent interface.
 * 
 * @author Priyan
 */
public interface RemotePatchUpdateApi {

	public RemotePatchUpdateData parsePatchUpdateRequest(IsoBuffer isoBuffer)
			throws PosException;

	public void buildPatchUpdateRequest(RemotePatchUpdateData rpud,
			IsoBuffer isoBuffer);

}
