package com.fss.pos.client.services.download;

import com.fss.pos.client.services.ServiceApi;
import com.fss.pos.client.services.download.parameter.ParameterDownloadApi;
import com.fss.pos.client.services.download.patchupdate.RemotePatchUpdateApi;
import com.fss.pos.client.services.download.remotekey.KeyDownloadApi;

/**
 * To handle the download services.
 * 
 * @author Priyan
 * @see ServiceApi
 * @see ParameterDownloadApi
 * @see KeyDownloadApi
 * @see RemotePatchUpdateApi
 */
public interface DownloadApi extends ParameterDownloadApi, KeyDownloadApi,
		RemotePatchUpdateApi, ServiceApi {

}
