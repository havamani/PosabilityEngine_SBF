package com.fss.pos.rest.paramdownload;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.pos.base.api.db.storedprocedure.StoredProcedureInfo;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.client.hpdh.HpdhParameterDownloadService;

@Service
public class ParamDownloadService {
	@Autowired
	private HpdhParameterDownloadService hpdhDownloadService;
	
	protected static ConcurrentMap<String, Queue<String>> downloadDataMap;
	static {
		downloadDataMap = new ConcurrentHashMap<String, Queue<String>>();
	}
	protected static final String ONE = "1";
	

	public ParamDownloadResponseMasterBean paramDownloadResponse(String mspAcr, IsoBuffer isoBuffer) throws PosException {		
		ParamDownloadResponseMasterBean response = new ParamDownloadResponseMasterBean();
		 try {
			 String terminalId = isoBuffer.get(Constants.DE41);
				if (isoBuffer.get(Constants.DE3).endsWith(ONE)) {
					if (!downloadDataMap.containsKey(mspAcr + terminalId))
						throw new PosException(Constants.ERR_NO_TID_DOWNLOAD);

				} else {
					response = hpdhDownloadService.paramDownloadResponseData(terminalId,mspAcr,isoBuffer.get("channel"), StoredProcedureInfo.INITIAL_DOWNLOAD);
				}				
		} catch (Exception e) {			
			Log.error("Terminal param download error::", e);
		}
		 
		return response;
	}
}
