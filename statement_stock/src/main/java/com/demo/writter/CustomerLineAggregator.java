package com.demo.writter;

import org.springframework.batch.item.file.transform.LineAggregator;

import com.demo.domain.Customer;
import com.demo.domain.Transaction;

public class CustomerLineAggregator implements LineAggregator<Object> {

	private LineAggregator<Customer> customerLineAggregator;
	private LineAggregator<Transaction> transactionLineAggregator;

	@Override
	public String aggregate(Object item) {
		return item instanceof Customer ? customerLineAggregator.aggregate((Customer) item)
				: customerLineAggregator.aggregate((Customer) item);
	}

	public void setCustomerLineAggregator(LineAggregator<Customer> customerLineAggregator) {
		this.customerLineAggregator = customerLineAggregator;
	}

	public void setTransactionLineAggregator(LineAggregator<Transaction> transactionLineAggregator) {
		this.transactionLineAggregator = transactionLineAggregator;
	}

}
