package com.fss.pos.base.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.fss.pos.base.api.client.ClientApi;
import com.fss.pos.base.api.db.Dao;
import com.fss.pos.base.api.db.storedprocedure.StoredProcedureApi;
import com.fss.pos.base.api.host.HostApi;
import com.fss.pos.base.api.hsm.HsmApi;
import com.fss.pos.base.commons.Config;
import com.fss.pos.base.messagehandlers.MessageHandler;

@Component
public class ApiFactory {

	@Autowired
	private ApplicationContext appContext;

	@Autowired
	private Config config;

	public ClientApi getClientApi(String apiId) {
		return (ClientApi) appContext.getBean(Factories.CLIENT + apiId);
	}

	public HostApi getHostRequestApi(String apiId) {
		return (HostApi) appContext.getBean(Factories.HOSTREQUEST + apiId);
	}

	public HostApi getHostResponseApi(String apiId) {
		return (HostApi) appContext.getBean(Factories.HOSTRESPONSE + apiId);
	}

	public HsmApi getHsmApi(String apiId) {
		return (HsmApi) appContext.getBean(Factories.HSM + apiId);
	}

	public StoredProcedureApi getStoredProcedureApi() {
		return (StoredProcedureApi) appContext
				.getBean(Factories.STOREDPROCEDURE + config.getDbType());
	}

	public Dao getRemotePatchUpdateApi() {
		return (Dao) appContext.getBean(Factories.REMOTEPATCHUPDATEDAO
				+ config.getDbType());
	}

	public MessageHandler getMessageHandler(String handlerId) {
		return (MessageHandler) appContext.getBean(Factories.MESSAGEHANDLER
				+ handlerId);
	}

	public boolean containsMessageHandler(String handlerId) {
		return appContext.containsBean(Factories.MESSAGEHANDLER + handlerId);
	}

	public boolean containsHostRequestApi(String apiId) {
		return appContext.containsBean(Factories.HOSTREQUEST + apiId);
	}

	public boolean containsHostResponseApi(String apiId) {
		return appContext.containsBean(Factories.HOSTRESPONSE + apiId);
	}

}
