package com.statement.dao;

import com.statement.domain.Account;

public interface AccountDao {
	public Account findAccountByNumber(String accountNumber);
}
