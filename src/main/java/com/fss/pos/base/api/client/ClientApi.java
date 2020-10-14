package com.fss.pos.base.api.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fss.pos.base.api.Api;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.EmvTags;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.PosException;

/**
 * An object that handles all types of client. Depends on annotation
 * <i>Client</i>.
 * 
 * @author Priyan
 * @see Api
 */
public interface ClientApi extends Api {

	/**
	 * To parse client request
	 * 
	 * @param message
	 *            the request message from client to be parsed
	 * @return IsoBuffer object the implementation of {@link ConcurrentHashMap}
	 * @throws PosException
	 *             thrown when an exception occurs during parsing
	 */
	public IsoBuffer parse(String message) throws PosException;

	/**
	 * To parse emv data
	 * 
	 * @param isoBuffer
	 *            the parsed buffer containing emv data
	 * @return Map object containing tags and data as key value pair
	 */
	public Map<EmvTags, String> parseEmvData(IsoBuffer isoBuffer);

	/**
	 * To modify the {@link IsoBuffer} object for client response.
	 * 
	 * @param isoBuffer
	 *            the buffer need to modified for response
	 */
	public void modifyBits4Response(IsoBuffer isoBuffer, Data data);

	/**
	 * To build the client message
	 * 
	 * @param isoBuffer
	 *            the buffer holding all fields and data need to be formed as
	 *            response
	 * @return response formed response {@link String} object need to responded
	 *         to client
	 */
	public String build(IsoBuffer isoBuffer);

	public String getStoredProcedure();
	
	public Boolean checkHostToHost();

	public boolean errorDescriptionRequired();

}
