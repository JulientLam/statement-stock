package com.demo.reader;

import java.util.ArrayList;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.demo.domain.Customer;
import com.demo.domain.Transaction;

public class CustomerFileReader implements ItemStreamReader<Customer> {

	private Object currentObject;
	private ItemStreamReader<Object> delegate;

	public void setDelegate(ItemStreamReader<Object> itemStreamReader) {
		this.delegate = itemStreamReader;
	}

	public Object peek() throws UnexpectedInputException, ParseException, NonTransientResourceException, Exception {
		return currentObject == null ? currentObject = delegate.read() : currentObject;
	}

	@Override
	public Customer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if (currentObject == null) {
			currentObject = delegate.read();
		}

		Customer item = (Customer) currentObject;
		currentObject = null;

		if (item != null) {
			item.getAccount().setTransactions(new ArrayList<Transaction>());
			while (peek() instanceof Transaction) {
				item.getAccount().getTransactions().add((Transaction) currentObject);
				currentObject = null;
			}
			return item;
		}
		return null;
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		delegate.open(executionContext);
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		delegate.update(executionContext);
	}

	@Override
	public void close() throws ItemStreamException {
		delegate.close();
	}

}
