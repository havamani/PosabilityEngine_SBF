package com.fss.pos.hsm;

import java.lang.annotation.Annotation;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.fss.pos.base.api.hsm.Hsm;
import com.fss.pos.base.api.hsm.HsmApi;
import com.fss.pos.base.factory.AbstractFactory;
import com.fss.pos.base.factory.Factories;

@Component
public class HsmApiFactory extends AbstractFactory {

	private HsmApiFactory() {

	}

	@PostConstruct
	public void initApiCache() {
		init();
	}

	@Override
	protected Factories getPrefix() {
		return Factories.HSM;
	}

	@Override
	protected String getKey(Object object) {
		return object.getClass().getAnnotation(Hsm.class).value();
	}

	@Override
	protected Class<? extends Annotation> getAnnotatedClass() {
		return Hsm.class;
	}

	@Override
	protected boolean isValid(Object key) {
		return true;
	}

	@Override
	protected Class<?> getInterface() {
		return HsmApi.class;
	}
}
