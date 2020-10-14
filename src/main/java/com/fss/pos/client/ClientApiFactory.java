package com.fss.pos.client;

import java.lang.annotation.Annotation;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.fss.pos.base.api.client.Client;
import com.fss.pos.base.api.client.ClientApi;
import com.fss.pos.base.factory.AbstractFactory;
import com.fss.pos.base.factory.Factories;

/**
 * To initialize and register the {@link ClientApi} objects in spring context
 * 
 * @author Priyan
 */
@Component
public class ClientApiFactory extends AbstractFactory {

	/**
	 * Prevent the ClientApiFactory class from being instantiated.
	 */
	private ClientApiFactory() {
	}

	/**
	 * Initialize the factory and register beans
	 */
	@PostConstruct
	public void initApiCache() {
		init();
	}

	@Override
	protected String getKey(Object api) {
		return api.getClass().getCanonicalName();
	}

	@Override
	protected Class<? extends Annotation> getAnnotatedClass() {
		return Client.class;
	}

	@Override
	protected Factories getPrefix() {
		return Factories.CLIENT;
	}

	@Override
	protected boolean isValid(Object object) {
		return true;
	}

	@Override
	protected Class<?> getInterface() {
		return ClientApi.class;
	}

}
