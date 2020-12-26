package com.statement.reader;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.database.JdbcCursorItemReader;

import com.statement.dao.TickerDao;
import com.statement.domain.Customer;
import com.statement.domain.Statement;


public class CustomerStatementReader implements ItemReader<Statement>, ItemStream{

	private TickerDao tickerDao;
	private JdbcCursorItemReader<Customer> customerReader;
	
	
	@Override
	public Statement read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		
		Customer customer = customerReader.read();
		System.out.println("CUSTOMER: "+customer);
		if(customer==null) {
			return null;
		}
		Statement statement = new Statement();
		statement.setCustomer(customer);
		statement.setSecurityTotal(tickerDao.getTotalValue(customer.getId()));
		statement.setStocks(tickerDao.getStocksForCustomer(customer.getId()));
		return statement;
	}

	

	public void setTickerDao(TickerDao tickerDao) {
		this.tickerDao = tickerDao;
	}



	public void setCustomerReader(JdbcCursorItemReader<Customer> customerReader) {
		this.customerReader = customerReader;
	}



	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		customerReader.open(executionContext);
		
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		customerReader.update(executionContext);
		
	}

	@Override
	public void close() throws ItemStreamException {
		customerReader.close();
	}

}
