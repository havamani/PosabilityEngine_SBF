package com.fss.pos.client.services.operator;

import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.client.services.ServiceApi;

public interface OperatorApi extends ServiceApi {

	public TerminalOperator getTerminalOperatorData(IsoBuffer isoBuffer)
			throws PosException;

	public TerminalInfo getTerminalInfo(IsoBuffer isoBuffer)
			throws PosException;

}
