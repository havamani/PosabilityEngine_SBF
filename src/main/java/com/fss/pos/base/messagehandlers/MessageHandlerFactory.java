package com.fss.pos.base.messagehandlers;

import java.lang.annotation.Annotation;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.fss.pos.base.factory.AbstractFactory;
import com.fss.pos.base.factory.Factories;

@Component
public class MessageHandlerFactory extends AbstractFactory {

	private MessageHandlerFactory() {

	}

	@PostConstruct
	public void initApiCache() {
		init();
	}

	@Override
	protected Factories getPrefix() {
		return Factories.MESSAGEHANDLER;
	}

	@Override
	protected String getKey(Object object) {
		return object.getClass().getAnnotation(Handler.class).value();
	}

	@Override
	protected Class<? extends Annotation> getAnnotatedClass() {
		return Handler.class;
	}

	@Override
	protected Class<?> getInterface() {
		return MessageHandler.class;
	}

	@Override
	protected boolean isValid(Object key) {
		return true;
	}

}
