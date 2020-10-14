package com.fss.pos.client.services.settlement;

import com.fss.pos.base.commons.PosException;
import com.fss.pos.client.services.ServiceApi;

public interface SettlementApi extends ServiceApi {

	public BatchTotals parseSettlementRequest(Object batchData)
			throws PosException;

	public SettlementFormat getBatchTotalsFormat();
	
	public String getSettlementStoredProcedure();
	
	public String getSettlementStoredProcedureExt();
	
	/*public String getSettlementProcedureName();
	public String getSettlementBatchProcedureName();*/

}
