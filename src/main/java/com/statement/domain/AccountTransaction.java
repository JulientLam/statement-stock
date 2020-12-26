package com.statement.domain;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountTransaction extends Transaction {
	private String accountNumber;
	private PricingTier tier;
	private BigDecimal fee;
	private long quantity;
	private BigDecimal price;

	@Override
	public String toString() {
		return getId() + ":" + accountNumber + ":" + getTicker() + ":" + getTradeTimestamp().getTime() + ":" + fee;
	}
}
