package com.fss.pos.base.api.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to identify the {@link ClientApi} object from the factory. The
 * only identification of the client object to get registered in the factory.
 * 
 * @author Priyan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Client {

}
