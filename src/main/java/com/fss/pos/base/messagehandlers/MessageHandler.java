package com.fss.pos.base.messagehandlers;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;

import com.fss.pos.base.api.client.ClientApi;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.services.fssconnect.FssConnect;

/**
 * To handle the different ISO message types.
 * 
 * @author Priyan
 *
 * @see NetworkManagementMessageHandler
 * @see AuthenticationMessageHandler
 * @see FinancialTransactionMessageHandler
 * @see SettlementMessageHandler
 * @see BatchMessageHandler
 * @see ReversalMessageHandler
 */
public interface MessageHandler {

	/**
	 * TO handle the messages by message types.
	 * 
	 * @param isoBuffer
	 *            buffer object of parsed request
	 * @param fssConnect
	 *            the {@link FssConnect} object received
	 * @param clientApi
	 *            current client api object
	 * @return response for the message
	 * @throws Exception
	 */
	public String handle(IsoBuffer isoBuffer, FssConnect fssConnect,
			ClientApi clientApi) throws Exception;

	/**
	 * To send to client
	 * 
	 * @param isoBuffer
	 *            buffer object of parsed request
	 * @param fssc
	 * @param clientApi
	 *            the current client api
	 * @return fssconnect response {@link String}
	 * @throws JSONException
	 * @throws UnsupportedEncodingException
	 */
	public String respondClient(IsoBuffer isoBuffer, FssConnect fssc,
			ClientApi clientApi) throws JSONException,
			UnsupportedEncodingException;

}
