package com.statement.domain;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Statement {
	private Customer customer;
	private BigDecimal securityTotal;
	private List<Transaction> stocks;
}
