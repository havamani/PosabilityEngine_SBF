package com.fss.pos.base.api.hsm;

import com.fss.pos.base.api.Api;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.PosException;

/**
 * An object to handle all the HSM commands and operations.
 * 
 * @author Priyan
 * @see Api
 */
public interface HsmApi extends Api {

	/**
	 * To generate a command to get a new key from hsm.
	 * 
	 * @param ktmSpec
	 *            the index configured
	 * @param keyType
	 *            the type of the key required
	 * @param keyLengthType
	 *            Single/Double/Triple length key
	 * @param mspAcr
	 * @param fsscUrl
	 * @param fsscSource
	 * @return the {@link HsmResponse} object contains the hsm response
	 * @throws PosException
	 *             thrown when exception occurs at runtime
	 */
	public HsmResponse generateKey(String ktmSpec, String keyType,
			String keyLengthType, String checkDigit, String mspAcr)
			throws PosException;

	public HsmResponse translatePIN(String encPINBlock, String terminalPPK,
			String tpkCheckValue, String cardNo, String keyLengthType,
			String instPPK, String checkDigit, String mspAcr,
			IsoBuffer isoBuffer, String keyEntry) throws PosException;

	public String verifyPIN(String encPINBlock, String instPPK, String cardNo,
			String instPVK, String validationData, String offset,
			String checkLen) throws PosException;

	public HsmResponse importZonalPinKey(String kirSpec, String zpkKey,
			String zmkCheckValue, String keyLengthType, String checkDigit)
			throws PosException;

	public void setDestination(String destination);
	
	public HsmResponse importZonalPinKey73Cmd(String kirSpec, String zpkKey,
			String zmkCheckValue, String keyLengthType, String mspAcr)
			throws PosException;

	// P2P
	public String translateP2Pcmd(IsoBuffer isoBuffer, HsmData hsmData, String mspAcr) throws PosException;
	public void setAlternateDestination(String alternateDestination);
}
