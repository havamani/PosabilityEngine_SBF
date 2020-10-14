package com.fss.pos.hsm;

import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.fss.pos.base.api.hsm.HsmApi;
import com.fss.pos.base.commons.Config;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.StaticStore;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.services.fssconnect.FssConnect;
import com.fss.pos.base.services.fssconnect.FssConnectService;

public abstract class AbstractHsmApi implements HsmApi {

	protected static final String DEFAULT_ERROR = "error Code Not Available.Please Refer the Manual....";
	protected static final HashMap<String, String> ERROR_MAP;

	static {

		ERROR_MAP = new HashMap<String, String>();
		ERROR_MAP.put("NULL", "NULL");
		ERROR_MAP.put("00", "No Error!. Success");
		ERROR_MAP.put("01", "DES Fault(System Disabled)");
		ERROR_MAP.put("02", "Illegal Function Code. PIN mailing not enabled");
		ERROR_MAP.put("03", "Incorrect Message Length");
		ERROR_MAP.put("04",
				"Invalid Data in message. Character not in range(0-9,A-F)");
		ERROR_MAP.put("08", "Verification failure");
		ERROR_MAP.put("20", "Invalid key specifier length");
		ERROR_MAP.put("21", "Invalid key specifier length");
		ERROR_MAP.put("0C",
				"Inconsistent Request Fields.Inconsistent Field Size");
		ERROR_MAP.put("23", "Invalid key specifier format");
		ERROR_MAP.put("53", "Public Key Pair Incompatible.");
		ERROR_MAP.put("3A", "Public Key Pair Generating........");
		ERROR_MAP.put("3B", "RSA cipher error");
		ERROR_MAP
				.put("0A",
						"Uninitiated key accessed. Key or decimalization table(DT) is not stored in the ProtectHost White");
		ERROR_MAP
				.put("07",
						"PIN format error: PIN does not comply with the AS2805.3 1985 specification, is in an invalid PIN/PAD format, or is in an invalid Docutel format");

	}

	@Autowired
	private Config config;

	@Autowired
	private FssConnectService fssConnectService;

	protected String destination;
	protected String alternateDestination;

	public String getDestination() {
		return destination;
	}

	public String getAlternateDestination() {
		return alternateDestination;
	}

	@Override
	public void setDestination(String destination) {
		this.destination = destination;
	}

	@Override
	public void setAlternateDestination(String alternateDestination) {
		this.alternateDestination = alternateDestination;
	}


	@Override
	public String process(FssConnect object) throws Exception {
		return null;
	}

	public String sendAndReceive(String reqData, String msp)
			throws PosException {
		long start = System.currentTimeMillis();
		String fsscSource = StaticStore.fsscSrcDetails.get(msp).get(msp);
		String fsscUrl = StaticStore.fsscUrlDetails.get(msp).get(msp);
		
		//FssConnectService fssConnectService=new FssConnectService();

		//String fsscSource = "POS_MDLWR=NODE_ONE=HTTP_SERVER";
		//String fsscUrl = "http://10.144.26.10:8040/";
		//destination="POS_MDLWR=NODE_ONE=HSM_SAFENET";
		try {
			Log.debug("Fssconnect Message Sending", reqData);

			if (this.destination == null) {
				Log.trace("URL/Destination null");
				throw new PosException(Constants.ERR_HSM_CONNECT);
			}

			FssConnect fssc = new FssConnect(StandardCharsets.UTF_8);
			fssc.setSource(fsscSource);

			fssc.setDestination(destination);
			fssc.setAlternateSourceArray(new String[0]);
			fssc.setResponseRequired(FssConnect.RESPONSE_REQUIRED);
			fssc.setIIN(Constants.EMPTY_STRING);
			fssc.setMessage(reqData);
			fssc.setUniqueId(Constants.EMPTY_STRING);
			fssc.setTimeStamp(Constants.EMPTY_STRING);
			fssc.setAlternateDestinationArray(
					new String[] { alternateDestination == null ? "" : alternateDestination });
			fssc.setNotifyDestArray(new String[0]);
			fssc.setMsgId("1");
			Log.trace("Hsm request successfully sent");
			fssc = fssConnectService.send(fssc, -1, fsscUrl);
			Log.info("Hsm response code", fssc.getResponseCode());
			Log.info("Time taken for HSM ",
					Long.toString(System.currentTimeMillis() - start));
			if (!Constants.FSSC_RESP_SUCCESS.equals(fssc.getResponseCode())) {
				Log.info("FSSCONNECT RESPONSE FOR HSM ", fssc.getResponseCode());
				throw new PosException(Constants.ERR_HSM_CONNECT);
			}
			return fssc.getMessage();
		} catch (Exception e) {
			try {
				if (e instanceof SocketTimeoutException) {
					FssConnect fssc = new FssConnect(StandardCharsets.UTF_8);
					fssc.setSource(fsscSource);
					fssc.setDestination(destination);
					fssc.setAlternateSourceArray(new String[0]);
					fssc.setResponseRequired(FssConnect.RESPONSE_REQUIRED);
					fssc.setIIN(Constants.EMPTY_STRING);
					fssc.setMessage(reqData);
					fssc.setUniqueId(Constants.EMPTY_STRING);
					fssc.setTimeStamp(Constants.EMPTY_STRING);
					fssc.setAlternateDestinationArray(new String[0]);
					fssc.setNotifyDestArray(new String[0]);
					fssc.setMsgId("1");
					fssc = fssConnectService.send(fssc, -1, fsscUrl);
					Log.info("Time taken for HSM ",
							Long.toString(System.currentTimeMillis() - start));
					if (!Constants.FSSC_RESP_SUCCESS.equals(fssc
							.getResponseCode())) {
						Log.info("FSSCONNECT RESPONSE FOR HSM ",
								fssc.getResponseCode());
						throw new PosException(Constants.ERR_HSM_CONNECT);
					}
					return fssc.getMessage();
				} else {
					Log.error(this.getClass() + " sendAndReceive ", e);
					throw new PosException(Constants.ERR_HSM_CONNECT);
				}
			} catch (Exception f) {
				Log.error(this.getClass() + " sendAndReceive ", f);
				throw new PosException(Constants.ERR_HSM_CONNECT);
			}
		}
	}
	
	
}
