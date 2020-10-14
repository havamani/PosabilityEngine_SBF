package com.fss.pos.client.services.reversal;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.pos.base.api.db.storedprocedure.StoredProcedureInfo;
import com.fss.pos.base.commons.Config;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.StaticStore;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.PosUtil;
import com.fss.pos.base.commons.utils.security.SecureData;
import com.fss.pos.base.commons.utils.security.SecurityService;
import com.fss.pos.base.factory.ApiFactory;
import com.fss.pos.base.services.transactionlog.TransactionLogData;


@Service
public class ReversalService {

	@Autowired
	private ApiFactory apiFactory;

	@Autowired
	private SecurityService securityUtils;

	@Autowired
	private Config config;

	@SuppressWarnings("unchecked")
	public TransactionLogData getReversalData(IsoBuffer isoBuffer,
			String clientStationName, String sessionId, String mspAcr)
			throws Exception {

		List<Object> params = new ArrayList<Object>();
		params.add(isoBuffer.get(Constants.DE11, null));
		params.add(isoBuffer.get(Constants.DE62, null));
		String pan = isoBuffer.get(Constants.DE2);
		SecureData sd = StaticStore.deks.get(mspAcr);
		try {
			byte[] encPan;
			if(mspAcr.equals("AFP") || mspAcr.equals("AUB")){ //afs 
				encPan = securityUtils.encryptText(pan.getBytes(), 
					        this.config.getAliasNamePrefix() + "AHB", sd.getKekCode(), 
					        sd.getDekBytes(), mspAcr, sd.getEncryptType());
			}else{
				 encPan = securityUtils.encryptText(pan.getBytes(),
						config.getAliasNamePrefix() + mspAcr, sd.getKekCode(),
						sd.getDekBytes(), mspAcr, sd.getEncryptType());
			}
			params.add(new String(Base64.encodeBase64(encPan)));
		} catch (Exception e) {
			Log.error("Card Encryption", e);
			params.add(null);
		}
		params.add(PosUtil.maskCardNumber(isoBuffer.get(Constants.DE2), mspAcr));
		params.add(isoBuffer.get(Constants.DE22, null));
		params.add(isoBuffer.get(Constants.DE3).substring(2, 4));
		params.add(isoBuffer.get(Constants.DE7, null));
		params.add(isoBuffer.get(Constants.DE3).substring(0, 2)
				+ isoBuffer.get(Constants.DE25));
		params.add(isoBuffer.get(Constants.DE4, Constants.ZERO));
		params.add(isoBuffer.get(Constants.DE54, Constants.ZERO));
		params.add(isoBuffer.get(Constants.DE42, null));
		params.add(isoBuffer.get(Constants.DE47, null));
		params.add(isoBuffer.get(Constants.DE41));
		params.add(sessionId == null ? Constants.ZERO : sessionId);
		params.add(isoBuffer.get(Constants.ISO_MSG_TYPE));
		params.add(clientStationName);
		
		
		List<Class<?>> classList = new ArrayList<Class<?>>();
		classList.add(TransactionLogData.class);
		List<Object> objList = apiFactory.getStoredProcedureApi().getBean(
				params, StoredProcedureInfo.TERMINAL_REVERSAL, classList,
				mspAcr);
		Log.debug("Response from " + StoredProcedureInfo.TERMINAL_REVERSAL,
				((List<TransactionLogData>) objList.get(0)).get(0).getStatus());
		return ((List<TransactionLogData>) objList.get(0)).get(0);

	}
	
	
	@SuppressWarnings("unchecked")
	public TransactionLogData getISO8583ReversalData(IsoBuffer isoBuffer,
			String clientStationName, String sessionId, String mspAcr)
			throws Exception {
		
		String pan = isoBuffer.get(Constants.DE2, null);
		String txnType = isoBuffer.get(Constants.DE3).substring(0, 2)
				+ isoBuffer.get(Constants.DE25);

		List<Object> params = new ArrayList<Object>();

		params.add(isoBuffer.get(Constants.DE2, null));
		params.add(isoBuffer.get(Constants.DE4, null));
		params.add(isoBuffer.get(Constants.DE7, null));
		params.add(isoBuffer.get(Constants.DE11, null));
		params.add(isoBuffer.get(Constants.DE12, null));
		params.add(isoBuffer.get(Constants.DE13, null));
		params.add(isoBuffer.get(Constants.DE18, null));
		params.add(isoBuffer.get(Constants.DE22, null));
		params.add(isoBuffer.get(Constants.DE37, null));
		params.add(isoBuffer.get(Constants.DE41, null));
		params.add(isoBuffer.get(Constants.DE43, null));
		params.add(isoBuffer.get(Constants.DE49, null));
		params.add(isoBuffer.isFieldEmpty(Constants.DE54) ? "" : isoBuffer
				.get(Constants.DE54));
		params.add(isoBuffer.get(Constants.DE62, null));
		params.add(isoBuffer.get(Constants.DE3).substring(2, 4));
		params.add(txnType);
		if (!(isoBuffer.get(Constants.DE6).equals(Constants.DISABLED_FIELD)))
			params.add(isoBuffer.get(Constants.DE63));
		else {
			params.add(Constants.ZERO);
		}

		params.add(clientStationName);
		params.add(isoBuffer.get(Constants.ISO_MSG_TYPE));
		

		SecureData sd = StaticStore.deks.get(mspAcr);

		try {
			byte[] encPan;
			if (mspAcr.equals("AFP") || mspAcr.equals("AUB")) {// afs
				encPan = securityUtils.encryptText(pan.getBytes(),
						config.getAliasNamePrefix() + "AHB", sd.getKekCode(),
						sd.getDekBytes(), mspAcr, sd.getEncryptType());
			} else {
				encPan = securityUtils.encryptText(pan.getBytes(),
						config.getAliasNamePrefix() + mspAcr, sd.getKekCode(),
						sd.getDekBytes(), mspAcr, sd.getEncryptType());
			}
			params.add(new String(Base64.encodeBase64(encPan)));
		} catch (Exception e) {
			Log.error("Card Encryption", e);
			params.add(null);
		}
		

		// Card Masking Number//
		if (pan != null) {
			String maskedno = PosUtil.maskCardNumber(pan, mspAcr);
			params.add(maskedno);
		} else
			params.add(null);

		
		List<Class<?>> classList = new ArrayList<Class<?>>();
		classList.add(TransactionLogData.class);
		List<Object> objList = apiFactory.getStoredProcedureApi().getBean(
				params, StoredProcedureInfo.ISO8583_TERMINAL_REVERSAL, classList,
				mspAcr);
		Log.debug("Response from " + StoredProcedureInfo.ISO8583_TERMINAL_REVERSAL,
				((List<TransactionLogData>) objList.get(0)).get(0).getStatus());
		return ((List<TransactionLogData>) objList.get(0)).get(0);

	}
	
}