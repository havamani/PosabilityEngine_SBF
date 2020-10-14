package com.fss.pos.host;

import java.lang.annotation.Annotation;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.fss.pos.base.api.host.HostApi;
import com.fss.pos.base.api.host.HostResponse;
import com.fss.pos.base.factory.Factories;

@Component
public class HostResponseApiFactory extends AbstractHostFactory {

	private HostResponseApiFactory() {
		;
	}

	@PostConstruct
	public void initApiCache() {
		init();
	}

	@Override
	protected Factories getPrefix() {
		return Factories.HOSTRESPONSE;
	}

	@Override
	protected String getKey(Object object) {
		return object.getClass().getAnnotation(HostResponse.class).HostType();
	}

	@Override
	protected Class<? extends Annotation> getAnnotatedClass() {
		return HostResponse.class;
	}

	@Override
	protected boolean isValid(Object key) {
		return true;
	}

	@Override
	protected Class<?> getInterface() {
		return HostApi.class;
	}

}
