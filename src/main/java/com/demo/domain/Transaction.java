package com.demo.domain;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction {
	private long id;
	private long accountId;
	private String accountNumber;
	private String ticker;
	private long tickerId;
	private long quantity;
	private Date tradeTimestamp;
	private BigDecimal dollarAmount;
	private TransactionType type;

	@Override
	public String toString() {
		return "Sold " + quantity + " of " + ticker;
	}
}
