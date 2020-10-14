package com.fss.pos.base.api.host.keyimport;

import com.fss.pos.base.api.host.HostApi;
import com.fss.pos.base.commons.IsoBuffer;

/**
 * An interface to handle the key import messages to the host.
 * 
 * @author Priyan
 *
 */
public interface HostKeyImportApi extends HostApi {

	/**
	 * To modify the fields for key import from host.
	 * 
	 * @param isoBuffer
	 */
	public void importKey(IsoBuffer isoBuffer);

}
