package com.fss.pos.base.api;

import com.fss.pos.base.services.fssconnect.FssConnect;

/**
 * The root interface in the <i>api</i> hierarchy
 * 
 * @author Priyan
 */
public interface Api {

	/**
	 * @param fssConnect
	 *            the message from Fssconnect need to be processed
	 * @return the message to Fssconnect need to be sent
	 * @throws Exception
	 *             thrown when unknown runtime exceptions occurs
	 */
	public String process(FssConnect fssConnect) throws Exception;

}
