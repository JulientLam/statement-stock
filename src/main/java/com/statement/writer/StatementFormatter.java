package com.statement.writer;

import org.springframework.batch.item.file.transform.LineAggregator;

import com.statement.domain.Statement;
import com.statement.util.FormatStatement;

public class StatementFormatter implements LineAggregator<Statement> {
	@Override
	public String aggregate(Statement statement) {

		return FormatStatement.format(statement);
	}
}
