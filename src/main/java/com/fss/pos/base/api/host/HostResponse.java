package com.fss.pos.base.api.host;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to identify the host response object from the factory. The only
 * identification of the host response object to get registered in the factory.
 * 
 * @author Priyan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HostResponse {

	String HostType();

}
