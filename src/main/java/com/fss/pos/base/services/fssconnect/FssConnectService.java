package com.fss.pos.base.services.fssconnect;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.pos.base.commons.Config;
import com.fss.pos.base.commons.Log;

@Service
public class FssConnectService {

	@Autowired
	private Config config;

	public FssConnect send(FssConnect fssc, int timeout, String fsscUrl)
			throws Exception {
		if (-1 == timeout) {
			timeout = Integer.parseInt(config.getFssConnectReadTimeout());
			// timeout=15000;
		}
		Log.fssc(fssc.toString());
		byte[] fsscResponse = HttpClient
				.send(fsscUrl, fssc.toString().getBytes(), HttpClient.POST,
						null, FssConnect.RESPONSE_REQUIRED.equals(fssc
								.getResponseRequired()) ? false : true, Integer
								.parseInt(config.getFssConnectReadTimeout()),
						timeout);
		if (FssConnect.RESPONSE_REQUIRED.equals(fssc.getResponseRequired())) {
			String hostRsp = new String(fsscResponse);
			// Log.fssc(hostRsp);
			return new FssConnect(hostRsp, StandardCharsets.ISO_8859_1);
		}
		return null;
	}
	
	
}
