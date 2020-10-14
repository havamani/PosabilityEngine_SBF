package com.fss.pos.client.services.download.parameter;

import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.PosException;

public interface ParameterDownloadApi {

	public void downloadParameters(String mspAcr, IsoBuffer isoBuffer)
			throws PosException;

}
