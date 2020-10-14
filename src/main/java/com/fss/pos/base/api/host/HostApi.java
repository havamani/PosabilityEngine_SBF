package com.fss.pos.base.api.host;

import java.util.concurrent.ConcurrentHashMap;

import com.fss.pos.base.api.Api;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.PosException;

/**
 * An object that handles all types of host.
 * 
 * @author Priyan
 * @see Api
 */
public interface HostApi extends Api {

	/**
	 * To parse host message
	 * 
	 * @param message
	 *            the request/response message from host to be parsed
	 * @return IsoBuffer object the implementation of {@link ConcurrentHashMap}
	 * @throws PosException
	 *             thrown when an exception occurs during parsing
	 */
	public IsoBuffer parse(String message) throws PosException;

	public String modiyAndbuild(IsoBuffer isoBuffer, IsoBuffer clientIsoBuffer,
			Data data) throws PosException;

}
