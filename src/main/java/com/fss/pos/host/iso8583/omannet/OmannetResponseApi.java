package com.fss.pos.host.iso8583.omannet;

import com.fss.pos.base.api.client.ClientData;
import com.fss.pos.base.api.host.HostResponse;
import com.fss.pos.base.api.transactions.ModifyTransaction;
import com.fss.pos.base.api.transactions.Transactions;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.IsoBuffer;
import java.io.UnsupportedEncodingException;

@HostResponse(HostType="24")
public class OmannetResponseApi extends OmannetApi
{
  @ModifyTransaction(Transactions.DEFAULT)
  public void modify4All(IsoBuffer isoBuffer, Data data)
  {
    ClientData cd = (ClientData)data;
    isoBuffer.put("P-62", cd.getInvoice());
    isoBuffer.put("P-41", cd.getTerminalId());
    isoBuffer.put("P-3", cd.getProcCode());

    isoBuffer.put("P-12", isoBuffer.get("P-7").substring(4));
    isoBuffer.put("P-13", cd.getDate());
    isoBuffer.put("P-12", cd.getTime());
    isoBuffer.disableField("P-53");
    isoBuffer.disableField("P-25");
    isoBuffer.disableField("P-26");
    isoBuffer.disableField("P-43");
    isoBuffer.disableField("P-45");
    isoBuffer.disableField("P-46");
    isoBuffer.disableField("S-100");
    isoBuffer.disableField("S-128");
  }

  @ModifyTransaction(Transactions.PURCHASE)
  public void modifyPurchace(IsoBuffer isoBuffer, Data data)
  {
    isoBuffer.put("MSG-TYP", "0210");
  }

  @ModifyTransaction(Transactions.EMV_DEFAULT)
  public void modifyEmv(IsoBuffer isoBuffer, Data data) throws UnsupportedEncodingException
  {
  }

  @ModifyTransaction(Transactions.REVERSAL_DEFAULT)
  public void modifyReversal(IsoBuffer isoBuffer, Data data)
  {
    ClientData cd = (ClientData)data;
    isoBuffer.put("P-62", cd.getInvoice());
    isoBuffer.put("P-41", cd.getTerminalId());
    isoBuffer.put("P-3", cd.getProcCode());
    isoBuffer.put("P-12", cd.getTime());
    isoBuffer.put("MSG-TYP", "0410");
    isoBuffer.disableField("S-100");
    isoBuffer.disableField("S-128");
  }

  @ModifyTransaction(Transactions.BALANCE_INQUIRY)
  public void modifyBalanceEnquire(IsoBuffer isoBuffer, Data data) {
    isoBuffer.put("MSG-TYP", "0210");
  }
  @ModifyTransaction(Transactions.REFUND)
  public void modifyRefund(IsoBuffer isoBuffer, Data data) {
    isoBuffer.put("MSG-TYP", "0210");
  }
  @ModifyTransaction(Transactions.PRE_AUTHORIZATION)
  public void modifyPreAuthorization(IsoBuffer isoBuffer, Data data) {
    isoBuffer.put("MSG-TYP", "0110");
  }
  @ModifyTransaction(Transactions.COMPLETION)
  public void modifyCompletion(IsoBuffer isoBuffer, Data data) {
    isoBuffer.put("MSG-TYP", "0210");
  }
}