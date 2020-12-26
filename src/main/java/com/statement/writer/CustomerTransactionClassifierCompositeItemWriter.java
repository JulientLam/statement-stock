package com.statement.writer;

import java.util.List;

import org.springframework.batch.item.support.ClassifierCompositeItemWriter;

public class CustomerTransactionClassifierCompositeItemWriter extends ClassifierCompositeItemWriter<Object> {
	@Override
	public void write(List<? extends Object> items) throws Exception {
		for (Object item : items) {
			System.out.println(item);
		}
		super.write(items);
	}
}
