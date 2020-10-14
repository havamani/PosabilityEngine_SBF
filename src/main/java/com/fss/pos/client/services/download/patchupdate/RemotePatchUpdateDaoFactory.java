package com.fss.pos.client.services.download.patchupdate;

import java.lang.annotation.Annotation;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fss.pos.base.commons.Config;
import com.fss.pos.base.factory.AbstractFactory;
import com.fss.pos.base.factory.Factories;

@Component
public class RemotePatchUpdateDaoFactory extends AbstractFactory {

	@Autowired
	private Config config;

	private RemotePatchUpdateDaoFactory() {

	}

	@PostConstruct
	public void initApiCache() {
		init();
	}

	@Override
	protected Factories getPrefix() {
		return Factories.REMOTEPATCHUPDATEDAO;
	}

	@Override
	protected String getKey(Object object) {
		return object.getClass().getAnnotation(RemotePatchUpdate.class).value();
	}

	@Override
	protected Class<? extends Annotation> getAnnotatedClass() {
		return RemotePatchUpdate.class;
	}

	@Override
	protected boolean isValid(Object key) {
		return config.getDbType().equals(key);
	}

	@Override
	protected Class<?> getInterface() {
		return RemotePatchUpdateDao.class;
	}
}
