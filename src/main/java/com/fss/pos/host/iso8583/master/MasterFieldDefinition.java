package com.fss.pos.host.iso8583.master;

public class MasterFieldDefinition extends Master8583Message {

	public MasterFieldDefinition() {
		super("");
	}

	public byte[] pack() {
		return super.pack(FIELDMAP);
	}

	public boolean unpack(byte message[]) {
		return super.unpack(message, FIELDMAP);
	}

	private static final MasterField FIELDMAP[] = {
			new MasterField("Primary Bit Map", 0, 16, 16, 0),
			new MasterField("Secondary Bit Map", 0, 16, 16, 0),
			new MasterField("Primary Account Number", 1, 0, 19, 2),
			new MasterField("Processing Code", 1, 6, 6, 0),
			new MasterField("Transaction Amount", 1, 12, 12, 0),
			new MasterField("Settlement Amount", 1, 12, 12, 0),
			new MasterField("Cardholder Billing Amount", 1, 12, 12, 0),
			new MasterField("Transmission DateTime", 1, 10, 10, 0),
			new MasterField("Cardholder Billing Fee", 1, 8, 8, 0),
			new MasterField("Settlement Conversion Rate", 1, 8, 8, 0),
			new MasterField("Cardholder Billing Conversion Rate", 1, 8, 8, 0),
			new MasterField("Systems Trace Audit Number", 1, 6, 6, 0),
			new MasterField("Local Transaction Time", 1, 6, 6, 0),
			new MasterField("Local Transaction Date", 1, 4, 4, 0),
			new MasterField("Expiration Date", 1, 4, 4, 0),
			new MasterField("Settlement Date", 1, 4, 4, 0),
			new MasterField("Conversion Date", 1, 4, 4, 0),
			new MasterField("Capture Date", 1, 4, 4, 0),
			new MasterField("Merchant Type", 1, 4, 4, 0),
			new MasterField("Acquirer Country Code", 1, 3, 3, 0),
			new MasterField("Country Code Primary Account Number Extended", 1,
					3, 3, 0),
			new MasterField("Forwarding Institution Country Code", 1, 3, 3, 0),
			new MasterField("Point of Service Data Code", 0, 3, 3, 0),
			new MasterField("Card Sequence Number", 1, 3, 3, 0),
			new MasterField("Function Code", 1, 3, 3, 0),
			new MasterField("Message Reason Code", 1, 2, 2, 0),
			new MasterField("Point of Service PIN Capture Code", 1, 2, 2, 0),
			new MasterField("Authorization Identification Response Length", 1,
					1, 1, 0),
			new MasterField("Transaction Fee Amount", 0, 9, 9, 0),
			new MasterField("Settlement Fee Amount", 0, 9, 9, 0),
			new MasterField("Original Amounts", 0, 9, 9, 0),
			new MasterField("Settlement Processing Fee Amount", 0, 9, 9, 0),
			new MasterField("Acquiring Institution Identification Code", 1, 0,
					6, 2),
			new MasterField("Forwarding Institution Identification Code", 1, 0,
					6, 2),
			new MasterField("Extended Primary Account Number", 2, 0, 28, 2),
			new MasterField("Track 2 Data", 2, 0, 37, 2),
			new MasterField("Track 3 Data", 2, 0, 104, 3),
			new MasterField("Retrieval Reference Number", 0, 12, 12, 0),
			new MasterField("Approval Code", 2, 6, 6, 0),
			new MasterField("Response Code", 0, 2, 2, 0),
			new MasterField("Service Restriction Code", 0, 3, 3, 0),
			new MasterField("Card Acceptor Terminal Identification", 2, 8, 8, 0),
			new MasterField("Card Acceptor Identification Code", 2, 15, 15, 0),
			new MasterField("Card Acceptor Name/Location", 2, 40, 40, 0),
			new MasterField("Addl Resp Data", 2, 0, 25, 2),
			new MasterField("Track 1 Data", 2, 0, 76, 2),
			new MasterField("Expanded Addl Amt", 2, 0, 999, 3),
			new MasterField("National Additional Data", 2, 0, 999, 3),
			new MasterField("Additional Data, Private", 2, 0, 999, 3),
			new MasterField("Transaction Currency Code", 1, 3, 3, 0),
			new MasterField("Settlement Currency Code", 1, 3, 3, 0),
			new MasterField("Cardholder Billing Currency Code", 1, 3, 3, 0),
			new MasterField("Personal Identification Number (PIN) Data", 0, 16,
					16, 0),
			new MasterField("Security Related Control Information", 1, 16, 16,
					0),
			new MasterField("Additional Amounts", 0, 0, 120, 3),
			new MasterField("ICC System Data", 3, 0, 510, 3),
			new MasterField("ISO Reserved", 2, 0, 999, 3),
			new MasterField("National Reserved", 2, 0, 999, 3),
			new MasterField("National Reserved", 2, 0, 999, 3),
			new MasterField("National Reserved", 2, 0, 999, 3),
			new MasterField("Advice Reason Code", 2, 0, 999, 3),
			new MasterField("POS Data", 2, 0, 26, 3),
			new MasterField("Intermediate N/W Facility Data", 2, 0, 100, 3),
			new MasterField("Network Data", 2, 0, 50, 3),
			new MasterField("Primary Message Authentication Code", 0, 16, 16, 0),
			new MasterField("Extended Bit Map", 0, 16, 16, 0),
			new MasterField("Settlement Code", 1, 1, 1, 0),
			new MasterField("Extended Payment Code", 1, 2, 2, 0),
			new MasterField("Receiving Institution Country Code", 1, 3, 3, 0),
			new MasterField("Settlement Institution Country Code", 1, 3, 3, 0),
			new MasterField("Network Management Information Code", 1, 3, 3, 0),
			new MasterField("Message Number", 1, 4, 4, 0),
			new MasterField("Message Number Last", 1, 0, 999, 3),
			new MasterField("Action Date", 1, 6, 6, 0),
			new MasterField("Number Credits", 1, 10, 10, 0),
			new MasterField("Reversal Number Credits", 1, 10, 10, 0),
			new MasterField("Number Debits", 1, 10, 10, 0),
			new MasterField("Reversal Number Debits", 1, 10, 10, 0),
			new MasterField("Number Transfer", 1, 10, 10, 0),
			new MasterField("Reversal Number Transfer", 1, 10, 10, 0),
			new MasterField("Number Inquiries", 1, 10, 10, 0),
			new MasterField("Number Authorizations", 1, 10, 10, 0),
			new MasterField("Processing Fee Amount Credits", 1, 12, 12, 0),
			new MasterField("Transaction Fee Amount Credits", 1, 12, 12, 0),
			new MasterField("Processing Fee Amount Debits", 1, 12, 12, 0),
			new MasterField("Transaction Fee Amount Debits", 1, 12, 12, 0),
			new MasterField("Amount Credits", 1, 16, 16, 0),
			new MasterField("Reversal Amount Credits", 1, 16, 16, 0),
			new MasterField("Amount Debits", 1, 16, 16, 0),
			new MasterField("Reversal Amount Debits", 1, 16, 16, 0),
			new MasterField("Original Data Elements", 1, 42, 42, 0),
			new MasterField("File Update Code", 1, 1, 1, 0),
			new MasterField("File Security Code", 0, 2, 2, 0),
			new MasterField("Response Indicator", 1, 5, 5, 0),
			new MasterField("Service Indicator", 2, 7, 7, 0),
			new MasterField("Replacement Amounts", 1, 42, 42, 0),
			new MasterField("Message Security Code", 0, 16, 16, 0),
			new MasterField("Net Settlement Amount", 0, 17, 17, 0),
			new MasterField("Payee", 2, 25, 25, 0),
			new MasterField("Settlement Institution Identification Code", 1, 0,
					11, 2),
			new MasterField("Receiving Institution Identification Code", 1, 0,
					11, 2),
			new MasterField("File Name", 2, 0, 17, 2),
			new MasterField("Account Identification 1", 2, 0, 28, 2),
			new MasterField("Account Identification 2", 2, 0, 28, 2),
			new MasterField("Transaction Description", 2, 0, 100, 3),
			new MasterField("MasterCard Reserved", 2, 0, 999, 3),
			new MasterField("MasterCard Reserved", 2, 0, 999, 3),
			new MasterField("MasterCard Reserved", 2, 0, 999, 3),
			new MasterField("ISO Reserved", 2, 0, 999, 3),
			new MasterField("ISO Reserved", 2, 0, 999, 3),
			new MasterField("ISO Reserved", 2, 0, 999, 3),
			new MasterField("ISO Reserved", 2, 0, 999, 3),
			new MasterField("Additional Data", 2, 0, 100, 3),
			new MasterField("Reserved National", 2, 0, 999, 3),
			new MasterField("Reserved National", 2, 0, 999, 3),
			new MasterField("Reserved National", 2, 0, 999, 3),
			new MasterField("Reserved National", 2, 0, 999, 3),
			new MasterField("Reserved National", 2, 0, 999, 3),
			new MasterField("Reserved National", 2, 0, 999, 3),
			new MasterField("Reserved National", 2, 0, 999, 3),
			new MasterField("Record Data", 2, 0, 299, 3),
			new MasterField("Authorizing Agent ID", 1, 0, 6, 3),
			new MasterField("Additional Record Data", 2, 0, 999, 3),
			new MasterField("Receipt Free Text", 2, 0, 999, 3),
			new MasterField("Member Defined Data", 2, 0, 299, 3),
			new MasterField("New PIN Data", 0, 0, 999, 3),
			new MasterField("Private Data", 2, 0, 100, 3),
			new MasterField("Private Data", 2, 0, 100, 3),
			new MasterField("Secondary Message Authentication Code", 0, 16, 16,
					0) };

}
