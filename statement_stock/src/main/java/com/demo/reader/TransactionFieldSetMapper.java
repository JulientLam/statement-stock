package com.demo.reader;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.demo.domain.Transaction;

public class TransactionFieldSetMapper implements FieldSetMapper<Transaction> {

	@Override
	public Transaction mapFieldSet(FieldSet fieldSet) throws BindException {
		Transaction transaction = new Transaction();
		transaction.setAccountNumber(fieldSet.readString("accountNumber"));
		transaction.setQuantity(fieldSet.readLong("quantity"));
		transaction.setTicker(fieldSet.readString("stockTicker"));
		transaction.setTradeTimestamp(fieldSet.readDate("timestamp", "yyyy-MM-dd HH:mm:ss"));
		transaction.setDollarAmount(fieldSet.readBigDecimal("price"));
		return transaction;
	}

}
