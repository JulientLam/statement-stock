package com.statement.domain;

public enum TransactionType {
	UNDEFINED,STOCK;
	public int intValue;
	
	private TransactionType() {
		this.intValue = 1;
	}
	public static TransactionType fromIntValue(Integer i) {
		if (i != null && i >= 0) {
			return values()[i];
		}
		return UNDEFINED;
	}
}
