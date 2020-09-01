package com.demo.domain;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Account {
	private long id = -1;
	private String accountNumber;
	private Customer cust;
	private BigDecimal cashBalance;
	private PricingTier tier;
	private List<Transaction> transactions;
	
}
