package com.fss.pos.client.services.download.patchupdate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.factory.ApiFactory;

@Service
public class RemotePatchUpdateService {

	@Autowired
	private ApiFactory apiFactory;

	public RemotePatchUpdateData getPatchData(String terminalId,
			RemotePatchUpdateData rpud, String mspAcr) throws PosException {
		try {
			return ((RemotePatchUpdateDao) apiFactory.getRemotePatchUpdateApi())
					.getFileBytes(terminalId, rpud, mspAcr);
			// File f = new File("/u01/sourcefiles/pos/app.bin");
			// FileInputStream fis = new FileInputStream(f);
			// BufferedInputStream bis = new BufferedInputStream(fis);
			// byte[] b = new byte[(int) f.length()];
			// bis.read(b);
			// bis.close();
			//
			// rpud.setFileName(f.getName());
			// rpud.setFileData(new String(b, StandardCharsets.ISO_8859_1));
			// rpud.setBytesTransferred(b.length);
			// rpud.setFileTransferIndex(1);
			// rpud.setTotalFileSize(b.length);
			// return rpud;
		} catch (PosException e) {
			throw e;
		} catch (Exception e) {
			Log.error("RemotePatchUpdateService getPatchData ", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
	}
}
