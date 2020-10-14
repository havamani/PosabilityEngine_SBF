package com.fss.pos.client;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fss.commons.utils.CipherUtil;
import com.fss.pos.base.api.client.ClientApi;
import com.fss.pos.base.api.client.Validator;
import com.fss.pos.base.commons.Config;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.base.factory.ApiFactory;
import com.fss.pos.base.messagehandlers.MessageHandler;
import com.fss.pos.base.services.fssconnect.FssConnect;
import com.fss.pos.client.services.download.DownloadApi;
import com.fss.pos.client.services.operator.OperatorApi;
import com.fss.pos.client.services.additionaldata.AdditionalDataApi;
import com.fss.pos.client.services.settlement.SettlementApi;
import org.apache.commons.codec.binary.Base64;

/**
 * Abstraction to handle all clients.
 * 
 * @author Priyan
 * @see ClientApi
 * @see DownloadApi
 * @see SettlementApi
 * @see OperatorApi
 * @see AdditionalDataApi
 */
public abstract class AbstractClientApi implements ClientApi, DownloadApi,
		SettlementApi, OperatorApi, AdditionalDataApi {

	@Autowired
	private ApiFactory apiFactory;

	@Autowired
	private Config config;

	@Override
	public String process(FssConnect fssc) throws JSONException,
			UnsupportedEncodingException {
		IsoBuffer isoBuffer;
		IsoBuffer tempIsoBuffer;
		
		if(fssc.getPlainPrivateKey() == null)
		{
		try {
			isoBuffer = parse(fssc.getMessage());
			tempIsoBuffer = new IsoBuffer(isoBuffer);
		} catch (Exception e) {
			Log.error("Client Parsing Failed", e);
			return null;
		}
		try {
			Log.info("Client Request", isoBuffer.logData());
			Log.trace("Iin | "+fssc.getIIN());
			isoBuffer.put(Constants.DE3, isoBuffer.get(Constants.DE3)
					.toUpperCase());

			if (isoBuffer.get(Constants.ISO_MSG_TYPE).equals(
					Constants.MSG_TYPE_RSP_REV_410)) {
				Log.trace("Converting 410 to 400");
				isoBuffer.put(Constants.ISO_MSG_TYPE,
						Constants.MSG_TYPE_REV_400);
			}

			if (apiFactory.containsMessageHandler(isoBuffer
					.get(Constants.ISO_MSG_TYPE))) {
				MessageHandler msgHandler = apiFactory
						.getMessageHandler(isoBuffer
								.get(Constants.ISO_MSG_TYPE));
				return msgHandler.handle(isoBuffer, fssc, this);
			} else {
				Log.trace("Unhandled Message Type");
				return null;
			}
		} catch (PosException pe) {
			isoBuffer.put(Constants.DE39, pe.getMessage());
			return respondClient(isoBuffer, fssc, this);
		} catch (Exception e) {
			Log.error("PosService process ", e);
			return respondClient(isoBuffer, fssc, this);
		}
		}
		else
		{
			try {
				isoBuffer = parse(fssc.getMessage());
				tempIsoBuffer = new IsoBuffer(isoBuffer);
			} catch (Exception e) {
				String source = fssc.getSource();
				Log.error("Client Parsing Failed", e);
				String finalMsg = new String(Base64.encodeBase64(Validator.getErrorDescription(e.getMessage()).getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1);
				String plainKey = new String(Base64.decodeBase64(fssc.getPlainPrivateKey().getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1);
				String encryptClientResp = CipherUtil.enCryptString(finalMsg, plainKey);
				fssc.setMessage(encryptClientResp);
				fssc.setSource(fssc.getDestination());
				fssc.setDestination(source);
				return fssc.toString();
			}
			
			try {
				Log.debug("Client Request : " + fssc.getIIN(), "  Request:::" +  isoBuffer.toString());
				isoBuffer.put(Constants.DE3, isoBuffer.get(Constants.DE3)
						.toUpperCase());

				if (isoBuffer.get(Constants.ISO_MSG_TYPE).equals(
						Constants.MSG_TYPE_RSP_REV_410)) {
					Log.trace("Converting 410 to 400");
					isoBuffer.put(Constants.ISO_MSG_TYPE,
							Constants.MSG_TYPE_REV_400);
				}

				if (apiFactory.containsMessageHandler(isoBuffer
						.get(Constants.ISO_MSG_TYPE))) {
					MessageHandler msgHandler = apiFactory
							.getMessageHandler(isoBuffer
									.get(Constants.ISO_MSG_TYPE));
					return msgHandler.handle(isoBuffer, fssc, this);
				} else {
					Log.trace("Unhandled Message Type");
					return null;
				}
			} catch (PosException pe) {
				isoBuffer.put(Constants.DE39, pe.getMessage());
				return respondClientEnc(tempIsoBuffer, fssc, this);
			} catch (Exception e) {
				Log.error("PosService process ", e);
				return respondClientEnc(tempIsoBuffer, fssc, this);
			}
		}
	}

	public String respondClient(IsoBuffer isoBuffer, FssConnect fssc,
			ClientApi clientApi) throws JSONException,
			UnsupportedEncodingException {
		Log.info("Client Response", isoBuffer.logData());

		if (isoBuffer.isFieldEmpty(Constants.DE39))
			isoBuffer.put(Constants.DE39, Constants.ERR_SYSTEM_ERROR);

		String clientRspCode;
		if (!Constants.SUCCESS.equals(clientRspCode = isoBuffer
				.get(Constants.DE39)) && errorDescriptionRequired()) {
			isoBuffer.put(Constants.DE63,
					Validator.getErrorDescription(clientRspCode));
		}
		Log.info("Client Response", isoBuffer.logData());
		String destReceived = fssc.getDestination();
		String srcReceived = fssc.getSource();
		fssc.setDestination(srcReceived);
		fssc.setSource(destReceived);
		fssc.setMessage(clientApi.build(isoBuffer));
		fssc.setAlternateSourceArray(new String[] { config
				.getFssConnectAltSource() });
		return fssc.toString();

	}
	
	public String respondClientEnc(IsoBuffer isoBuffer, FssConnect fssc,
			ClientApi clientApi) throws JSONException,
			UnsupportedEncodingException {

		if (isoBuffer.isFieldEmpty(Constants.DE39))
			isoBuffer.put(Constants.DE39, Constants.ERR_SYSTEM_ERROR);

		String clientRspCode;
		if (!Constants.SUCCESS.equals(clientRspCode = isoBuffer
				.get(Constants.DE39)) && errorDescriptionRequired()) {
			isoBuffer.put(Constants.DE63,
					Validator.getErrorDescription(clientRspCode));
		}
		Log.debug("Client Response", isoBuffer.toString());
		String destReceived = fssc.getDestination();
		String srcReceived = fssc.getSource();
		fssc.setDestination(srcReceived);
		fssc.setSource(destReceived);
		String finalMsg = new String(Base64.encodeBase64(clientApi.build(isoBuffer).getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1);
		String plainKey = new String(Base64.decodeBase64(fssc.getPlainPrivateKey().getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1);
		
		String encyMsg = CipherUtil.enCryptString(finalMsg, plainKey);
		Log.debug("Encryted Msg ", encyMsg + "\n" + IsoUtil.asciiChar2hex(encyMsg));
		StringBuilder sBuffer = new StringBuilder(IsoUtil.pad(
				String.valueOf(IsoUtil.toGraphical(encyMsg.length(), 2)), ' ',
				2, true));
		sBuffer.append(encyMsg);
		Log.debug("Final Msg to Client", sBuffer.toString());
		
		fssc.setMessage(sBuffer.toString());
		fssc.setAlternateSourceArray(new String[] { config
				.getFssConnectAltSource() });
		return fssc.toString();

	}

}
