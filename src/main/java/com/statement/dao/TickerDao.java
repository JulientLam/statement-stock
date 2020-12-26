package com.statement.dao;

import java.math.BigDecimal;
import java.util.List;

import com.statement.domain.Ticker;
import com.statement.domain.Transaction;

public interface TickerDao {
	public Ticker findTickerBySymbol(String symbol);
	public void saveTicker(Ticker ticker);
	public List<String> findTicker();
	public BigDecimal getTotalValue(long id);
	public List<Transaction> getStocksForCustomer(long id);
}
