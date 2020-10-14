package com.fss.pos.host.iso8583.postilion;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.host.AbstractHostApi;

public abstract class PostilionApi extends AbstractHostApi {

	@Autowired
	private PostilionIso87Api postilionIso87Api;

	private static final int[] BIT_127 = { 0, -2, 48, 22, 73, 2, -2, -3,// 8
			-3, 3, -2, -2, 17, 8, -2, 1,// 16
			-2, -2, 31, 8, -2, -5, 253, -2, // 24
			-4, -2, 1, 4, 40, 1, -2, -2, // 32
			4, 2, -2, -2, 4, -2, 2, 0,// 40
			0, 0, 0, 0, 0, 0, 0, 0,// 48
			0, 0, 0, 0, 0, 0, 0, 0,// 56
			0, 0, 0, 0, 0, 0, 0, 0 };// 64

	private static final int[] BIT_127_25 = { 0, 12, 12, -2, 4, 4, 4, 2,// 8
			1, 1, 1, 16, 2, -3, 6, 8,// 16
			11, -2, -4, 4, 6, 3, 2, 10, // 24
			1, 3, 6, -1, 2, 8, -2, -4, // 32
			-4, 0, 0, 0, 0, 0, 0, 0,// 40
			0, 0, 0, 0, 0, 0, 0, 0,// 48
			0, 0, 0, 0, 0, 0, 0, 0,// 56
			0, 0, 0, 0, 0, 0, 0, 0 };// 64

	@Override
	public IsoBuffer parse(String message) throws PosException {
		IsoBuffer isoBuffer = postilionIso87Api.parseIso87(
				message.substring(2), 4 + 8);
		Log.debug("Postilion Response ", isoBuffer.toString());
		if (!isoBuffer.isFieldEmpty(Constants.DE127)) {
			if (!isoBuffer.get(Constants.DE127).isEmpty()) {
				IsoBuffer isoBuffer127 = postilionIso87Api.parseDE127(
						isoBuffer.get(Constants.DE127), BIT_127);
				if (!isoBuffer127.isFieldEmpty(Constants.DE25)) {
					IsoBuffer isoBuffer127_25 = postilionIso87Api
							.parseDE127_25(isoBuffer127.get(Constants.DE25),
									BIT_127_25);
					isoBuffer127.putBuffer(Constants.DE25, isoBuffer127_25);
				}
				isoBuffer.putBuffer(Constants.DE127, isoBuffer127);
			}
		}
		return isoBuffer;
	}

	@Override
	public String build(IsoBuffer isoBuffer) {
		if (!isoBuffer.isFieldEmpty(Constants.DE127)) {
			IsoBuffer buffer127 = isoBuffer.getBuffer(Constants.DE127);
			if (!buffer127.isAllFieldsEmpty(false, false)) {
				if (!buffer127.isFieldEmpty(Constants.DE25)) {
					IsoBuffer buffer127_25 = buffer127
							.getBuffer(Constants.DE25);
					if (!buffer127_25.isAllFieldsEmpty(false, false)) {
						Log.debug("Postilion EMV", buffer127_25.toString());
						buffer127.put(Constants.DE25, postilionIso87Api
								.buildDE127_25(buffer127_25, BIT_127_25));
					}
				}
				isoBuffer.put(Constants.DE127,
						postilionIso87Api.buildDE127(buffer127, BIT_127));
			}
		}
		Log.debug("Postilion Host Request", isoBuffer.toString());
		return postilionIso87Api.buildIso87Request(
				isoBuffer.get(Constants.ISO_MSG_TYPE), isoBuffer);
	}

	protected String buildField127_22(Map<String, String> m) {
		StringBuilder sb = new StringBuilder();
		for (String k : m.keySet()) {
			String val = m.get(k);
			String length = String.valueOf(k.length());
			sb.append(length.length());
			sb.append(length);
			sb.append(k);
			length = String.valueOf(val.length());
			sb.append(length.length());
			sb.append(length);
			sb.append(val);
		}
		return sb.toString();
	}

	protected Map<String, String> parseField127_22(String data) {
		Map<String, String> m = new HashMap<String, String>();
		int idx = 0;
		while (data.length() > idx) {
			data.substring(0, 1);
			int lenOfLen = Integer.parseInt(data.substring(idx, idx += 1));
			int lenOfData = Integer.parseInt(data.substring(idx,
					idx += lenOfLen));
			String key = data.substring(idx, idx += lenOfData);
			lenOfLen = Integer.parseInt(data.substring(idx, idx += 1));
			lenOfData = Integer.parseInt(data.substring(idx, idx += lenOfLen));
			String val = data.substring(idx, idx += lenOfData);
			m.put(key, val);
		}
		return m;
	}

}
