package com.fss.pos.base.messagehandlers;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fss.pos.base.api.client.ClientApi;
import com.fss.pos.base.api.db.storedprocedure.StoredProcedureInfo;
import com.fss.pos.base.api.hsm.HsmResponse;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.services.fssconnect.FssConnect;
import com.fss.pos.client.hpdh.ExtendedHpdhParameterDownloadService;
import com.fss.pos.client.hpdh.HpdhParameterDownloadService;
import com.fss.pos.client.services.download.patchupdate.RemotePatchUpdateApi;
import com.fss.pos.client.services.download.patchupdate.RemotePatchUpdateData;
import com.fss.pos.client.services.download.patchupdate.RemotePatchUpdateService;
import com.fss.pos.client.services.download.remotekey.KeyDownloadApi;
import com.fss.pos.client.services.download.remotekey.KeyDownloadClientData;
import com.fss.pos.client.services.download.remotekey.RemoteKeyDownloadService;
import com.fss.pos.client.services.operator.OperatorApi;
import com.fss.pos.client.services.operator.OperatorInfo;
import com.fss.pos.client.services.operator.OperatorService;
import com.fss.pos.client.services.operator.TerminalInfo;
import com.fss.pos.client.services.operator.TerminalOperator;

@Handler(Constants.MSG_TYPE_NETWORK_MANAGEMENT)
public class NetworkManagementMessageHandler extends AbstractMessageHandler {

	@Autowired
	private OperatorService operatorService;

	@Autowired
	private RemoteKeyDownloadService remoteKeyDownloadService;

	@Autowired
	private RemotePatchUpdateService remotePatchUpdateService;

	
	@Autowired
	private HpdhParameterDownloadService hpdhDownloadService;

	@Autowired
	private ExtendedHpdhParameterDownloadService extHpdhDownloadService;
	
	@Override
	public String handleMessage(IsoBuffer isoBuffer, FssConnect fssConnect,
			ClientApi clientApi) throws UnsupportedEncodingException,
			PosException, JSONException {
		return manageNetwork(isoBuffer, fssConnect, clientApi);
	}

	private String manageNetwork(IsoBuffer isoBuffer, FssConnect fssc,
			ClientApi clientApi) throws PosException, JSONException,
			UnsupportedEncodingException {
		
	//Dukpt Validation
		
		if(!isoBuffer.isFieldEmpty(Constants.DE44))
		{
			if(isoBuffer.get(Constants.DE3).equals(Constants.PROC_CODE_TMK_DOWNLOAD) || isoBuffer.get(Constants.DE3).equals(Constants.PROC_CODE_SESSION_KEY_DOWNLOAD))
				isoBuffer.put(Constants.DE39, Constants.LOGON_NOT_ALLOWED);
			return respondClient(isoBuffer, fssc, clientApi);
		}

		 if (Constants.PROC_CODE_DOWNLOAD.equals(isoBuffer.get(
				Constants.DE3).substring(0, 2))
				&& (Constants.COND_CODE_DOWNLOAD.equals(isoBuffer
						.get(Constants.DE25)))) {

			String downloadData = extHpdhDownloadService.getData(fssc.getIIN(),
					isoBuffer, StoredProcedureInfo.INITIAL_DOWNLOAD);
			isoBuffer.put(Constants.DE39, Constants.SUCCESS);
			isoBuffer.put(Constants.DE60, downloadData);
			isoBuffer.disableField(Constants.DE61);

		}else if (Constants.PROC_CODE_DOWNLOAD.equals(isoBuffer.get(Constants.DE3)
				.substring(0, 2)))
			{
			String downloadData = hpdhDownloadService.getData(fssc.getIIN(),
					isoBuffer, StoredProcedureInfo.INITIAL_DOWNLOAD_HPDH);
			isoBuffer.put(Constants.DE39, Constants.SUCCESS);
			isoBuffer.put(Constants.DE60, downloadData);
			isoBuffer.disableField(Constants.DE61);

			/*
			 * ((DownloadApi) clientApi).downloadParameters(fssc.getIIN(),
			 * isoBuffer);
			 */

		}else if (Constants.PROC_CODE_OPERATOR_LOGIN.equals(isoBuffer.get(
				Constants.DE3).substring(0, 2))) {

			TerminalOperator ud = ((OperatorApi) clientApi)
					.getTerminalOperatorData(isoBuffer);
			TerminalInfo ti = ((OperatorApi) clientApi)
					.getTerminalInfo(isoBuffer);

			OperatorInfo opInfo = operatorService.login(ud.getUserId(),
					ud.getCode(), isoBuffer.get(Constants.DE41), ti.getMacId(),
					fssc.getIIN());
			if (Constants.SUCCESS.equals(opInfo.getResponseCode()))
				isoBuffer.put(Constants.DE63, opInfo.getSessionId());
			isoBuffer.put(Constants.DE39, opInfo.getResponseCode());

		} else if (Constants.PROC_CODE_OPERATOR_LOGOUT.equals(isoBuffer.get(
				Constants.DE3).substring(0, 2))) {

			TerminalOperator ud = ((OperatorApi) clientApi)
					.getTerminalOperatorData(isoBuffer);
			operatorService.logout(ud.getUserId(), ud.getSessionId(),
					ud.getIndicator(), fssc.getIIN());
			isoBuffer.put(Constants.DE39, Constants.SUCCESS);

		} else if (Constants.PROC_CODE_OPERATOR_CHANGE_CODE.equals(isoBuffer
				.get(Constants.DE3).substring(0, 2))) {

			TerminalOperator ud = ((OperatorApi) clientApi)
					.getTerminalOperatorData(isoBuffer);
			String rspCode = operatorService.changeCode(ud.getUserId(),
					ud.getOldCode(), ud.getNewCode(), fssc.getIIN());
			isoBuffer.put(Constants.DE39, rspCode);
			isoBuffer.disableField(Constants.DE63);

		} else if (Constants.PROC_CODE_TMK_DOWNLOAD.equals(isoBuffer.get(
				Constants.DE3).substring(0, 2))
				|| Constants.PROC_CODE_SESSION_KEY_DOWNLOAD.equals(isoBuffer
						.get(Constants.DE3).substring(0, 2))) {

			KeyDownloadClientData kcd = ((KeyDownloadApi) clientApi)
					.parseKeyDownloadRequestData(isoBuffer.get(Constants.DE63));
			HsmResponse hsmGenResponse = remoteKeyDownloadService.downloadKey(
					kcd,
					Constants.PROC_CODE_TMK_DOWNLOAD.equals(isoBuffer.get(
							Constants.DE3).substring(0, 2)),
					isoBuffer.get(Constants.DE41), fssc.getIIN());
			((KeyDownloadApi) clientApi).buildKeyDownloadResponseData(
					hsmGenResponse, isoBuffer);
			isoBuffer.put(Constants.DE39, Constants.SUCCESS);

		} else if (Constants.PROC_CODE_REMOTE_PATCH_UPDATE.equals(isoBuffer
				.get(Constants.DE3).substring(0, 2))) {

			boolean isContinuation = isoBuffer.get(Constants.DE3).endsWith("1");
			RemotePatchUpdateData rpud = isContinuation ? ((RemotePatchUpdateApi) clientApi)
					.parsePatchUpdateRequest(isoBuffer)
					: new RemotePatchUpdateData();
			rpud.setContinuation(isContinuation);
			rpud = remotePatchUpdateService.getPatchData(
					isoBuffer.get(Constants.DE41), rpud, fssc.getIIN());
			String updatedProcCode = rpud.isContinuation() ? Constants.PROC_CODE_REMOTE_PATCH_UPDATE_CONT
					: Constants.PROC_CODE_REMOTE_PATCH_UPDATE + "0000";
			isoBuffer.put(Constants.DE3, updatedProcCode);
			((RemotePatchUpdateApi) clientApi).buildPatchUpdateRequest(rpud,
					isoBuffer);
			isoBuffer.put(Constants.DE39, rpud.getRespCode());
			isoBuffer.disableField(Constants.DE61);

		} else {
			throw new PosException(Constants.ERR_INVALID_PROC_CODE);
		}
		return respondClient(isoBuffer, fssc, clientApi);
	}

}
