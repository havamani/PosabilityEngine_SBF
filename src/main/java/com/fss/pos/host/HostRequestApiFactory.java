package com.fss.pos.host;

import java.lang.annotation.Annotation;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.fss.pos.base.api.host.HostApi;
import com.fss.pos.base.api.host.HostRequest;
import com.fss.pos.base.factory.Factories;

@Component
public class HostRequestApiFactory extends AbstractHostFactory {

	private HostRequestApiFactory() {
		;
	}

	@PostConstruct
	public void initApiCache() {
		init();
	}

	@Override
	protected Factories getPrefix() {
		return Factories.HOSTREQUEST;
	}

	@Override
	protected String getKey(Object object) {
		HostRequest hostRequest = object.getClass().getAnnotation(
				HostRequest.class);
		return hostRequest.HostType();
	}

	@Override
	protected Class<? extends Annotation> getAnnotatedClass() {
		return HostRequest.class;
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
