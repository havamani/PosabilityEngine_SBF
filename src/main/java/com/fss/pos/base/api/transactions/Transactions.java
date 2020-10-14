package com.fss.pos.base.api.transactions;

public enum Transactions {

	DEFAULT("DEFAULT"), PURCHASE("0000"), CASH_ADVANCE("0100"), REFUND("2000"), TIP(
			"0200"), CASH_BACK("0900"), MOTO("0008"), COMPLETION("0006"), CASH_DEPOSIT(
			"2100"), VOID("2200"), BALANCE_INQUIRY("3100"), PRE_AUTHORIZATION(
			"3000"), OFFLINE_DEFAULT("OFFLINE"), REVERSAL_DEFAULT("REVERSAL"), EMV_DEFAULT(
			"EMV"), KEY_IMPORT("KEYIMPORT"), CUT_OVER("CUTOVER"), RECON("RECON"), DCC_ENQUIRY("0095"),
			DCC_COMPLETION("0096"), MANUAL_AUTH("0094"), ADVICE("0095"),LOAD_MONEY("2800"),SERVICE_CREATION("8300"),BALANCE_UPDATE("2900");

	private String transactionType;

	private Transactions(String txnType) {
		this.transactionType = txnType;
	}

	@Override
	public String toString() {
		return transactionType;
	}

}
