package com.statement.dao.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.statement.dao.TickerDao;
import com.statement.domain.Ticker;
import com.statement.domain.Transaction;

@Component
public class TickerDaoJdbc extends JdbcTemplate implements TickerDao {

	private static final String FIND_BY_SYMBOL = "select * from ticker t where ticker = ?";
	private static final String SAVE_TICKER = "insert into ticker (ticker, currentPrice) values (?,?)";
	private static final String FIND_TICKER ="select ticker FROM Ticker";
    private static final String TOTAL_VALUE = "select SUM(t.qty*tik.currentPrice) as totalValue from Customer c inner join Account a on c.id = a.customer_id" + 
										    		" inner join [Transaction] t on t.account_id = a.customer_id" + 
										    		" inner join Ticker tik on tik.id = t.tickerId" + 
										    		" where c.id = ?";
    private static final String STOCKS_BY_CUSTOMER= "select ticker, SUM(t.qty) as qty, SUM(t.qty*tik.currentPrice) as value " + 
											    		"from Customer c inner join Account a on c.id = a.customer_id " + 
											    		"inner join [Transaction] t on t.account_id = a.customer_id " + 
											    		"inner join Ticker tik on tik.id = t.tickerId " + 
											    		"where c.id = ? " + 
											    		"group by tik.id,ticker";
	
	public TickerDaoJdbc(DataSource dataSource) {
		this.setDataSource(dataSource);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Ticker findTickerBySymbol(String symbol) {
		List<Ticker> tickers = query(FIND_BY_SYMBOL, new Object[] { symbol }, new RowMapper() {
			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				Ticker ticker = new Ticker();
				ticker.setId(rs.getLong("id"));
				ticker.setPrice(rs.getBigDecimal("currentPrice"));
				ticker.setTicker(rs.getString("ticker"));
				return ticker;
			}
		});
		if (tickers != null && tickers.size() > 0) {
			return tickers.get(0);
		} else {
			return null;
		}
	}

	@Override
	public void saveTicker(Ticker ticker) {
		update(SAVE_TICKER, new Object [] {ticker.getTicker(), ticker.getPrice()}); 
	}

	@Override
	public List<String> findTicker() {
		return queryForList(FIND_TICKER, String.class);
	}

	@Override
	public BigDecimal getTotalValue(long id) {
		 BigDecimal result = queryForObject(TOTAL_VALUE, new Object[]{id}, BigDecimal.class);
		 return result==null? new BigDecimal(0): result;
	}

	@Override
	public List<Transaction> getStocksForCustomer(long id) {
		
	List<Transaction> query = query(STOCKS_BY_CUSTOMER, new Object[] {id}, (resultSet,arg1)->{
			Transaction transaction = new Transaction();
			transaction.setTicker(resultSet.getString("ticker"));
			transaction.setDollarAmount(resultSet.getBigDecimal("value"));
			transaction.setQuantity(resultSet.getLong("qty"));
			return transaction;
		});
		
		return query;
	}

}
