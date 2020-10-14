package com.fss.pos.host.iso8583.monex;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fss.pos.base.api.client.ClientData;
import com.fss.pos.base.api.host.HostResponse;
import com.fss.pos.base.api.transactions.ModifyTransaction;
import com.fss.pos.base.api.transactions.Transactions;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.constants.Constants;

@HostResponse(HostType = "27")
public class MonexResponseApi extends MonexApi {

	@ModifyTransaction(value = Transactions.DCC_ENQUIRY)
	public void modifyAll(IsoBuffer isobuffer, Data data) throws PosException {
		ClientData clientData = (ClientData) data;
		List<Object> listObj = new ArrayList<Object>();
		try {
			if (isobuffer.get(Constants.DE39).equals(Constants.SUCCESS)) {
				String p63 = isobuffer.get(Constants.DE63).substring(2);
				String arr[] = p63.split(";");
				JSONObject jsonDcc = new JSONObject();
				jsonDcc.put("CACD", arr[0].substring(3));
				jsonDcc.put("CNCD", arr[1].substring(3));
				jsonDcc.put("CEXP",
						arr[2].substring(3).toString().substring(0, 1));
				jsonDcc.put("CVAL", arr[2].substring(3).toString().substring(1));
				jsonDcc.put("EXCR", arr[3].substring(3).toString());
				jsonDcc.put("COMM", arr[4].substring(3));
				jsonDcc.put("MARK", arr[5].substring(3));
				jsonDcc.put("AEXP", clientData.getMerchantExponent());
				jsonDcc.put("ACMV", arr[6].substring(3));
				jsonDcc.put("CAMV", arr[7].substring(3));
				jsonDcc.put("QUOT", arr[8].substring(3));

				// //////exponent////////////////////////////
				jsonDcc.put("EXXP",
						arr[3].substring(3).toString().substring(0, 1));
				jsonDcc.put("CAXP",
						arr[7].substring(3).toString().substring(0, 1));
				jsonDcc.put("ACXP",
						arr[6].substring(3).toString().substring(0, 1));
				listObj.add(jsonDcc);
				JSONArray jsonArr = new JSONArray(listObj);
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("DCC", jsonArr);
				isobuffer.put(Constants.DE63, jsonObj.toString());

				isobuffer.put(Constants.DE3, "001000");
				isobuffer.put(Constants.DE39, Constants.SUCCESS);
			} else
				isobuffer.put(Constants.DE3, "001000");

		} catch (StringIndexOutOfBoundsException e) {
			Log.error("Error in Monex Response Api ", e);
			throw new StringIndexOutOfBoundsException();
		} catch (JSONException e) {
			Log.error("JSON Exception while parsing Monex Response P-63", e);
		}
	}

	@ModifyTransaction(value = Transactions.DCC_COMPLETION)
	public void modifyCompletion(IsoBuffer isobuffer, Data data) {
		isobuffer.put(Constants.DE3, "001000");
	}
@ModifyTransaction(value = Transactions.REVERSAL_DEFAULT)
	public void modifyReversal(IsoBuffer isobuffer, Data data) {
		isobuffer.put(Constants.DE3, "001000");
	}

}
