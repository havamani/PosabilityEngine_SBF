package com.fss.pos.host.iso8583.omannet;

import org.springframework.beans.factory.annotation.Autowired;

import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.host.AbstractHostApi;

public class OmannetApi extends AbstractHostApi {

	@Autowired
	private OmannetIso93Api omannetIso93Api;

	public IsoBuffer parse(String message) throws PosException {
		return this.omannetIso93Api.parseIso87(message.substring(4), 20);
	}

	public String build(IsoBuffer isoBuffer) {
		return this.omannetIso93Api.buildIso87Request(isoBuffer.get("MSG-TYP"),
				isoBuffer);
	}
}