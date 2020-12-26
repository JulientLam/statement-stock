package com.statement.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.statement.domain.AccountTransactionQuantity;

public class AccountTransactionQuantityRowMapper implements RowMapper<AccountTransactionQuantity>{

	@Override
	public AccountTransactionQuantity mapRow(ResultSet rs, int rowNum) throws SQLException {
		AccountTransactionQuantity accountTransactionQuantity = new AccountTransactionQuantity();
		accountTransactionQuantity.setAccountNumber(rs.getString("accountNumber"));
		accountTransactionQuantity.setTransactionCount(rs.getLong("qty"));
		return accountTransactionQuantity;
	}

}
