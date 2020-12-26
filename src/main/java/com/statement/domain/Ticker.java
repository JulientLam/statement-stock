package com.statement.domain;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Ticker {
 private long id;
 private String ticker;
 private BigDecimal price;
}
