package com.fss.pos.base.factory;

import java.lang.annotation.Annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.fss.pos.base.commons.Log;

/**
 * Abstraction to register the objects in {@link BeanFactory}.
 * 
 * @author Priyan
 */
public abstract class AbstractFactory {

	@Autowired
	private ConfigurableListableBeanFactory beanFactory;

	/**
	 * Initialization of factory objects.
	 */
	protected void init() {
		try {
			ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
					false);
			scanner.addIncludeFilter(new AnnotationTypeFilter(
					getAnnotatedClass()));
			for (BeanDefinition bd : scanner.findCandidateComponents("com.fss")) {

				Class<?> clazz = Class.forName(bd.getBeanClassName());
				Object api = clazz.newInstance();

				if (!getInterface().isInstance(api)) {
					Log.info("Cannot create instance. Not an instance of "
							+ api.getClass(), getInterface().getCanonicalName());
					continue;
				}
				String key = getKey(api);
				if (!isValid(key))
					continue;
				registerMethods(clazz, api);
				beanFactory.registerSingleton(getPrefix() + key, api);
				beanFactory.autowireBean(api);
			}
			Log.debug("Initializing api factory", this.getClass()
					.getCanonicalName());
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(this.getClass() + " initApiCache ", e);
		}
	}

	/**
	 * To get the factory prefix identification.
	 * 
	 * @return Factories the type of factory
	 */
	protected abstract Factories getPrefix();

	/**
	 * To get the unique key for registering
	 * 
	 * @param object
	 *            the api object
	 * @return key to register
	 */
	protected abstract String getKey(Object object);

	/**
	 * To find all annotated objects
	 * 
	 * @return the annotated class
	 */
	protected abstract Class<? extends Annotation> getAnnotatedClass();

	protected abstract Class<?> getInterface();

	protected abstract boolean isValid(Object key);

	protected void registerMethods(Class<?> clazz, Object api) {

	}

}
