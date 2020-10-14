package com.fss.pos.client.services.download.remotekey;

import com.fss.pos.base.api.hsm.HsmResponse;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.PosException;

public interface KeyDownloadApi {

	public KeyDownloadClientData parseKeyDownloadRequestData(String data)
			throws PosException;

	public void buildKeyDownloadResponseData(
			HsmResponse hsmGenResponse, IsoBuffer isoBuffer);

}
