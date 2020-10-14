package com.fss.pos.rest.emvdownload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fss.commons.utils.RRN;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.utils.ThreadLocalUtil;

@Component
public class EmvDownloadController {
	@Autowired
	private EmvDownloadService emvdownloadService;
	private static final String EMV_DWNLD = "PSP_ICCDOWNLOAD";
	
	public EmvDownloadResponse emvdownloadresponse(EmvDownloadRequest emvdownloadrequest)throws Exception {
	
		ThreadLocalUtil.setRRN(RRN.genRRN(12));
		ThreadLocalUtil.setMsp("ALB");
		Log.trace("EMV download : " +emvdownloadrequest.toString());
		EmvDownloadResponse response=new EmvDownloadResponse();	
		try {
			String channel=emvdownloadrequest.getRequest().getDeviceInfo().getChannel();
			String terminalId=emvdownloadrequest.getRequest().getDeviceInfo().getTerminalId();
			response=emvdownloadService.emvdownload(terminalId, channel, "ALB", EMV_DWNLD);
			//Log.trace("EMV Download Response : " + response.toString());		
		} catch (Exception e) {
			Log.error("EmvDownload Controller:", e);
			return null;
		}

		return response;
}
}
