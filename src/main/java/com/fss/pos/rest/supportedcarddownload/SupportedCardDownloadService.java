package com.fss.pos.rest.supportedcarddownload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fss.pos.base.api.db.storedprocedure.StoredProcedureInfo;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.client.hpdh.HpdhParameterDownloadService;


@Service
public class SupportedCardDownloadService {
	@Autowired
	private HpdhParameterDownloadService hpdhDownloadService;
	
	public SupportedCardDownloadResponse supportedcardDownloadResponse(String mspAcr, IsoBuffer isoBuffer) throws PosException {		
		
		SupportedCardDownloadResponse response = new SupportedCardDownloadResponse();
		
	try {
		 String terminalId = isoBuffer.get(Constants.DE41);

	      response = hpdhDownloadService.getCardDetails(terminalId,mspAcr,isoBuffer.get("channel"), StoredProcedureInfo.INITIAL_DOWNLOAD);
		}catch (Exception e) {			
			Log.error("SupportedCard download error::", e);
		}
		
		return response;
	}
}


