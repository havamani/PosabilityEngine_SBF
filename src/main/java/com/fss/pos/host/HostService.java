package com.fss.pos.host;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.pos.base.api.db.storedprocedure.StoredProcedureInfo;
import com.fss.pos.base.api.host.TimeoutData;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.PosUtil;
import com.fss.pos.base.commons.utils.ThreadLocalUtil;
import com.fss.pos.base.factory.ApiFactory;
import com.fss.pos.base.services.fssconnect.FssConnect;
import com.fss.pos.base.services.transactionlog.TransactionLogService;

@Service
public class HostService {

	private static ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private ApiFactory apiFactory;

	@Autowired
	private TransactionLogService txnLogService;

	public String importKey(FssConnect fssc) {
		try {
			KeyImportRequest kir = (KeyImportRequest) fssc.getSyncObject();
			IsoBuffer isoBuffer = new IsoBuffer();
			isoBuffer.fillBuffer(true, true, false);
			kir.setKeyImport(true);
			if (!apiFactory.containsHostRequestApi(kir.getHostId())) {
				Log.trace("Unhandled host key import");
				return null;
			}
			String hostRequest = apiFactory.getHostRequestApi(kir.getHostId())
					.modiyAndbuild(isoBuffer, null, kir);
			String destination = fssc.getDestination();
			String source = fssc.getSource();
			Log.info("Key Import Message", isoBuffer.toString());
			fssc.setMessage(hostRequest);
			fssc.setSource(destination);
			fssc.setDestination(kir.getHostStationName());
			fssc.setAlternateDestinationArray(new String[] { kir
					.getHostAlternateStationName() });
			String fsscUniqueId = PosUtil.getIsoFsscKeyImportUId(isoBuffer);
			updateTimer(fsscUniqueId, kir, source, destination);
			return fssc.toString();
		} catch (PosException e) {
			Log.error("importKey", e);
		} catch (Exception e) {
			Log.error("importKey", e);
		}
		return null;
	}

	protected void updateTimer(String fsscUniqueId, KeyImportRequest kir,
			String src, String dest) throws PosException, JSONException {
		try {
			TimeoutData td = new TimeoutData();
			td.setIin(kir.getMsp());
			td.setThreadLocalId(ThreadLocalUtil.getRRN());
			td.setHostStationName(kir.getHostStationName());
			td.setHsmAcquirerIndex(kir.getHsmAcquirerIndex());
			td.setHsmAcquirerKeyLength(kir.getKeyLength());
			td.setHsmStationName(kir.getHsmStationName());
			td.setHsmId(kir.getHsmId());
			td.setZmkCheckSum(kir.getZmkCheckSum());
			td.setClientStationName(src);
			td.setThisSource(dest);
			StringBuilder timerJson=new StringBuilder();
			timerJson.append( objectMapper.writeValueAsString(td));
			txnLogService.updateHostLog(null, new IsoBuffer(),
					StoredProcedureInfo.LOG_HOST_REQUEST, kir.getMsp(),
					fsscUniqueId, timerJson.toString(), null);
		} catch (Exception e) {
			Log.error("Update timer ", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
	}

}
