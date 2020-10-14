package com.fss.pos.client.services.download.patchupdate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to identify the {@link RemotePatchUpdateApi} object from the
 * factory.
 * 
 * @author Priyan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RemotePatchUpdate {

	String value();

}
