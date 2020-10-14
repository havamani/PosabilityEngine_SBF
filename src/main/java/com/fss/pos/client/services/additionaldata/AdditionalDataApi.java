package com.fss.pos.client.services.additionaldata;

import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.client.services.ServiceApi;
import com.fss.pos.client.services.additionaldata.AdditionalDataInfo;;
public interface AdditionalDataApi extends ServiceApi {
	public AdditionalDataInfo getAdditionalDataInfo(IsoBuffer isoBuffer)
			throws PosException;
}
