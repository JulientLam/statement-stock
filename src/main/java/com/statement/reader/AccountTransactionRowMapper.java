package com.statement.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.statement.domain.AccountTransaction;
import com.statement.domain.PricingTier;

public class AccountTransactionRowMapper implements RowMapper<AccountTransaction> {

	@Override
	public AccountTransaction mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		AccountTransaction accountTransaction = new AccountTransaction();
		accountTransaction.setAccountId(resultSet.getLong("accountId"));
		accountTransaction.setAccountNumber(resultSet.getString("accountNumber"));
		accountTransaction.setId(resultSet.getLong("transactionId"));
		accountTransaction.setQuantity(resultSet.getLong("qty"));
		accountTransaction.setTicker(resultSet.getString("ticker"));
		accountTransaction.setTier(PricingTier.convert(resultSet.getInt("tier")));
		accountTransaction.setTradeTimestamp(resultSet.getDate("executedTime"));
		accountTransaction.setPrice(resultSet.getBigDecimal("dollarAmount"));
		return accountTransaction;
	}

}
