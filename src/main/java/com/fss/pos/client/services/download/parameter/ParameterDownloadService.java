package com.fss.pos.client.services.download.parameter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.pos.base.api.db.storedprocedure.ResponseStatus;
import com.fss.pos.base.factory.ApiFactory;

@Service
public class ParameterDownloadService {

	@Autowired
	private ApiFactory apiFactory;

	public List<Object> getDownloadBeans(String terminalId, String mspAcr, String procedureName)
			throws Exception {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(terminalId);
		List<Class<?>> classList = new ArrayList<Class<?>>();
		classList.add(ResponseStatus.class);
		classList.add(TerminalConfig.class);
		classList.add(CardRange.class);
		classList.add(IssuerData.class);
		classList.add(IccData.class);
		classList.add(IccData.class);
		classList.add(KeyData.class);
		classList.add(EmvTerminalData.class);
		return (List<Object>) apiFactory.getStoredProcedureApi().getBean(
				params, procedureName, classList,
				mspAcr);
	}

}
