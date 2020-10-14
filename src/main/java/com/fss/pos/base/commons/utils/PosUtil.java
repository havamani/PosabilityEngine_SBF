package com.fss.pos.base.commons.utils;

import java.util.HashMap;
import java.util.Map;

import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.StaticStore;
import com.fss.pos.base.commons.constants.Constants;

public class PosUtil {

	public static String maskCardNumber(String cardNo, String msp) {
		try {
			Integer[] maskArr = StaticStore.cardMaskConfig.get(msp);
			return Util.maskCardNumber(cardNo, maskArr[0], maskArr[1]);
		} catch (Exception e) {
			Log.error("Card Masking", e);
			return null;
		}
	}

	public static String getIsoFsscKeyImportUId(IsoBuffer isoBuffer) {
		return new StringBuilder(isoBuffer.get(Constants.DE7)).append(
				isoBuffer.get(Constants.DE11)).toString();
	}

	public static Map<String, String> parseBuffer(String data) {
		Map<String, String> m = new HashMap<String, String>();
		data = data.substring(1, data.length() - 1);
		if (data.isEmpty())
			return m;
		String[] pairs = data.split(", ");
		for (String pair : pairs) {
			String[] keyValue = pair.split("=");
			m.put(keyValue[0], keyValue[1]);
		}
		return m;
	}

}
