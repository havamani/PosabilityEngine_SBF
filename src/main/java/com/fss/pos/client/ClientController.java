package com.fss.pos.client;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fss.commons.utils.CipherUtil;
import com.fss.commons.utils.JSSecurityUtil;
import com.fss.commons.utils.RRN;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.utils.IsoUtil;
import com.fss.pos.base.commons.utils.ThreadLocalUtil;
import com.fss.pos.base.commons.utils.security.SecurityService;
import com.fss.pos.base.factory.ApiFactory;
import com.fss.pos.base.services.fssconnect.FssConnect;
import com.fss.pos.client.hpdh.ExtendedHpdhClientApi;
import com.fss.pos.client.hpdh.HpdhClientApi;
import com.fss.pos.client.hpdh.ISO8583AggregatorClientApi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * To expose a REST api for client request
 * 
 * @author Priyan
 */
@RestController
//@Api(value="abc", description="Transaction entry point")
public class ClientController {

	private static final String extHpdhClassName;
	private static final String hpdhClassName;
	private static final String iso8583Aggregator;

	static {
		extHpdhClassName = ExtendedHpdhClientApi.class.getCanonicalName();
		hpdhClassName = HpdhClientApi.class.getCanonicalName();
		iso8583Aggregator = ISO8583AggregatorClientApi.class.getCanonicalName();
	}

	@Autowired
	private ApiFactory apiFactory;

	@Autowired
	private ClientService clientService;

	@Autowired
	private SecurityService securityService;

	@PostConstruct
	public void init() {
		clientService.loadSchemas();
		clientService.loadLoggerConfig();
		clientService.loadKekCodes();
		clientService.loadDeks();
		clientService.loadcardMaskingConfig();
		clientService.loadAcquirers();
		//clientService.loadRSAKeys();
	}

	/**
	 * 
	 * @param fsscJsonMsg
	 *            the message from Fssconnect
	 * @return response the message to Fssconnect
	 */
	//@ApiOperation(value = "To process HPDH transaction", response = Iterable.class)
	@RequestMapping(value = "/processHpdhClient", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String processHpdhClient(@RequestBody String fsscJsonMsg) {
		long start = System.currentTimeMillis();
		String response = null;
		try {
			ThreadLocalUtil.setRRN(RRN.genRRN(12));
			Log.fssc(fsscJsonMsg);
			FssConnect fssc = new FssConnect(fsscJsonMsg,
					StandardCharsets.ISO_8859_1);
			ThreadLocalUtil.setMsp(fssc.getIIN());
			response = apiFactory.getClientApi(hpdhClassName).process(fssc);
		} catch (Exception e) {
			Log.error("processHpdhClient ", e);
		} finally {
			if (response != null && !response.isEmpty())
				Log.fssc(response);

			Log.debug("Elapsed time ",
					Long.toString(System.currentTimeMillis() - start));
			ThreadLocalUtil.unset();
		}
		return response;
	}
	//@ApiOperation(value = "To process Extended HPDH transaction")
	@RequestMapping(value = "/processExtHpdhClient", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String processExtHpdhClient(@RequestBody String fsscJsonMsg) {
		long start = System.currentTimeMillis();
		String response = null;
		try {
			ThreadLocalUtil.setRRN(RRN.genRRN(12));
			Log.fssc(fsscJsonMsg);
			FssConnect fssc = new FssConnect(fsscJsonMsg,
					StandardCharsets.ISO_8859_1);
			ThreadLocalUtil.setMsp(fssc.getIIN());
			response = apiFactory.getClientApi(extHpdhClassName).process(fssc);
		} catch (Exception e) {
			Log.error("processExtHpdhClient ", e);
		} finally {
			if (response != null && !response.isEmpty())
				Log.fssc(response);
			Log.debug("Elapsed time ",
					Long.toString(System.currentTimeMillis() - start));
			ThreadLocalUtil.unset();
		}
		return response;
	}
	
	@RequestMapping(value = "/iso8583Aggregator", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String iso8583Aggregator(@RequestBody String fsscJsonMsg) {
		long start = System.currentTimeMillis();
		String response = null;
		try {
			ThreadLocalUtil.setRRN(RRN.genRRN(12));
			Log.fssc(fsscJsonMsg);
			FssConnect fssc = new FssConnect(fsscJsonMsg,
					StandardCharsets.ISO_8859_1);
			ThreadLocalUtil.setMsp(fssc.getIIN());
			response = apiFactory.getClientApi(iso8583Aggregator).process(fssc);
		} catch (Exception e) {
			Log.error("processExtHpdhClient ", e);
		} finally {
			if (response != null && !response.isEmpty())
				Log.fssc(response);
			Log.debug("Elapsed time ",
					Long.toString(System.currentTimeMillis() - start));
			ThreadLocalUtil.unset();
		}
		return response;
	}
	

	/*
	 * @RequestMapping(value = "/processEncHpdhClient", method =
	 * RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE) public
	 * String processEncHpdhClient(@RequestBody String fsscJsonMsg) {
	 * 
	 * long start = System.currentTimeMillis(); String response = null;
	 * List<String> list = null; try { ThreadLocalUtil.setRRN(RRN.genRRN(12));
	 * Log.fssc(fsscJsonMsg); FssConnect fssc = new FssConnect(fsscJsonMsg,
	 * StandardCharsets.ISO_8859_1); ThreadLocalUtil.setMsp(fssc.getIIN());
	 * String fsscMsg = fssc.getMessage().substring(2); JSONObject fssJson = new
	 * JSONObject(fsscMsg); list =
	 * getDecryptedDataClient(fssJson.get("data").toString(),
	 * fssJson.get("datacode").toString(), fssc.getIIN()); String
	 * plainKeyEncoded = new String(Base64.encodeBase64(list.get(0)
	 * .getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1);
	 * Log.debug("Plain key in Hex Controller",
	 * IsoUtil.asciiChar2hex(plainKeyEncoded));
	 * fssc.setPlainPrivateKey(plainKeyEncoded); fssc.setMessage(list.get(1));
	 * response = apiFactory.getClientApi(extHpdhClassName).process(fssc); }
	 * catch (Exception e) { Log.error("processExtHpdhEncClient ", e); response
	 * = "Unable to Parse!"; } finally { Log.fssc(response);
	 * Log.debug("Elapsed time ", Long.toString(System.currentTimeMillis() -
	 * start)); ThreadLocalUtil.unset(); } return response;
	 * 
	 * }
	 */

	// added by bebismita
	@RequestMapping(value = "/processEncHpdhClient", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String processEncHpdhClient(@RequestBody String fsscJsonMsg) {

		long start = System.currentTimeMillis();
		String response = null;
		List<String> list = null;
		try {
			ThreadLocalUtil.setRRN(RRN.genRRN(12));
			Log.fssc(fsscJsonMsg);
			FssConnect fssc = new FssConnect(fsscJsonMsg,
					StandardCharsets.ISO_8859_1);
			ThreadLocalUtil.setMsp(fssc.getIIN());
			String fsscMsg = fssc.getMessage().substring(2);
			JSONObject fssJson = new JSONObject(fsscMsg);
			list = getDecryptedDataClient(fssJson.get("data").toString(),
					fssJson.get("dataCode").toString(), fssc.getIIN());
			String plainKeyEncoded = new String(Base64.encodeBase64(list.get(0)
					.getBytes(StandardCharsets.ISO_8859_1)),
					StandardCharsets.ISO_8859_1);
			Log.debug("Plain key in Hex Controller",
					IsoUtil.asciiChar2hex(plainKeyEncoded));
			fssc.setPlainPrivateKey(plainKeyEncoded);
			fssc.setMessage(list.get(1));
			response = apiFactory.getClientApi(extHpdhClassName).process(fssc);
		} catch (Exception e) {
			Log.error("processExtHpdhEncClient ", e);
			response = "Unable to Parse!";
		} finally {
			Log.fssc(response);
			Log.debug("Elapsed time ",
					Long.toString(System.currentTimeMillis() - start));
			ThreadLocalUtil.unset();
		}
		return response;

	}

	@RequestMapping(value = "/updateConfig", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String updateConfig(@RequestBody String fsscJsonMsg) {
		try {
			ThreadLocalUtil.setRRN(RRN.genRRN(12));
			Log.fssc(fsscJsonMsg);
			FssConnect fssc = new FssConnect(fsscJsonMsg,
					StandardCharsets.ISO_8859_1);
			ThreadLocalUtil.setMsp(fssc.getIIN());
			clientService.updateConfig(fssc);
		} catch (Exception e) {
			Log.error("updateConfig ", e);
		}
		return null;
	}

	/*private List<String> getDecryptedDataClient(String data, String dataCode,
			String mspAcr) throws Exception {
		Log.debug("datacode", IsoUtil.asciiChar2hex(dataCode));
		List<String> dataValues = new ArrayList<String>();
		PrivateKey privateKey = securityService.getSecretKeyForEncy(mspAcr);
		String requestDataCode = JSSecurityUtil.decrypt(dataCode, privateKey);
		String requestData = CipherUtil.deCryptString(data, requestDataCode);
		Log.debug("data decrypt", IsoUtil.asciiChar2hex(requestData));
		dataValues.add(requestDataCode);
		dataValues.add(requestData);
		return dataValues;
	}*/
	
	private List<String> getDecryptedDataClient(String data, String dataCode, String mspAcr)
			throws Exception {
		Log.debug("datacode" ,IsoUtil.asciiChar2hex(dataCode));
		List<String> dataValues = new ArrayList<String>();
		PrivateKey privateKey = securityService.getSecretKeyForEncy(mspAcr);
		String requestDataCode = JSSecurityUtil.decrypt(dataCode, privateKey);
		String requestData = CipherUtil.deCryptString(data,requestDataCode);
		Log.debug("data decrypt" ,IsoUtil.asciiChar2hex(requestData));
		dataValues.add(requestDataCode);
		dataValues.add(requestData);
		return dataValues;
	}
}
