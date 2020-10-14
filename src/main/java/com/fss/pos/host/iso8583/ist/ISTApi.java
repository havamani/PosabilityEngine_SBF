package com.fss.pos.host.iso8583.ist;

import org.springframework.beans.factory.annotation.Autowired;

import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.host.AbstractHostApi;

public class ISTApi extends AbstractHostApi {

	@Autowired
	private ISTIso93Api istIso93Api;

	@Override
	public IsoBuffer parse(String message) throws PosException {
		ISTIso93Api istIso93Api = new ISTIso93Api();
		return istIso93Api.parseIso87(message.substring(4), 20);
	}

	@Override
	public String build(IsoBuffer isoBuffer) {
		ISTIso93Api istIso93Api = new ISTIso93Api();
		return istIso93Api.buildIso87Request(
				isoBuffer.get(Constants.ISO_MSG_TYPE), isoBuffer);
	}
}
