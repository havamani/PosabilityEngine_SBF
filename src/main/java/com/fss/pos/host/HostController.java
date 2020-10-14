package com.fss.pos.host;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fss.commons.utils.RRN;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.StaticStore;
import com.fss.pos.base.commons.utils.ThreadLocalUtil;
import com.fss.pos.base.factory.ApiFactory;
import com.fss.pos.base.services.fssconnect.FssConnect;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
//@Api(value="abc", description="Route the transaction to a specific host")
public class HostController {

	@Autowired
	private ApiFactory apiFactory;

	@Autowired
	private HostService hostService;
	//@ApiOperation(value = "To process Host response")
	@RequestMapping(value = "/processHostResponse", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String processHostResponse(@RequestBody String fsscJsonMsg) {
		long start = System.currentTimeMillis();
		String response = null;
		try {
			ThreadLocalUtil.setRRN(RRN.genRRN(12));
			Log.fssc(fsscJsonMsg);
			FssConnect fssc = new FssConnect(fsscJsonMsg,
					StandardCharsets.ISO_8859_1);
			ThreadLocalUtil.setMsp(fssc.getIIN());
			StringBuilder apiId=new StringBuilder();
			apiId.append(((StaticStore.hostMessageProtocols
					.get(fssc.getIIN()).get(fssc.getSource())) == null) ? (StaticStore.alterStationDetails.get(fssc.getIIN()).get(fssc.getSource()))
					: (StaticStore.hostMessageProtocols.get(fssc.getIIN())
							.get(fssc.getSource())));
			if (!apiFactory.containsHostResponseApi(apiId.toString())) {
				Log.trace("Invalid Acquirer Message Protocol. Unable to process transaction "
						+ apiId);
				return null;
			}
			response = apiFactory.getHostResponseApi(apiId.toString()).process(fssc);
		} catch (Exception e) {
			Log.error("HostController processHostResponse ", e);
		} finally {
			Log.fssc(response);
			Log.debug("Elapsed time ",
					Long.toString(System.currentTimeMillis() - start));
			ThreadLocalUtil.unset();
		}
		return response;
	}
	//@ApiOperation(value = "Import zonal keys")
	@RequestMapping(value = "/importKey", method = RequestMethod.POST)
	public String importKey(@RequestBody String fsscJsonMsg) {
		long start = System.currentTimeMillis();
		String response = null;
		try {
			ThreadLocalUtil.setRRN(RRN.genRRN(12));
			Log.fssc(fsscJsonMsg);
			FssConnect fssc = new FssConnect(fsscJsonMsg,
					StandardCharsets.ISO_8859_1);
			ThreadLocalUtil.setMsp(fssc.getIIN());
			KeyImportRequest keyImportReq = new KeyImportRequest(
					fssc.getMessage());
			fssc.setSyncObject(keyImportReq);
			response = hostService.importKey(fssc);
		} catch (Exception e) {
			Log.error("importKey ", e);
		} finally {
			Log.fssc(response);
			Log.debug("Elapsed time ", Long.toString(System.currentTimeMillis() - start));
			ThreadLocalUtil.unset();
		}
		return response;
	}

}
