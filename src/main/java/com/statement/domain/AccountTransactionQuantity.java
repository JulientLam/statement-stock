package com.statement.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountTransactionQuantity {
	private String accountNumber;
	private long transactionCount;
	private PricingTier tier;

	@Override
	public String toString() {
		return accountNumber + " has " + transactionCount + " transactions this month wich falls into tier " + tier;
	}
}
