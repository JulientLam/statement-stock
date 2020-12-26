package com.statement.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.statement.domain.Account;
import com.statement.domain.Address;
import com.statement.domain.Customer;
import com.statement.domain.PricingTier;

public class CustomerStatementRowMapper implements RowMapper<Customer> {

	/**
	 * select a.id as account_id, a.accountNumber, a.cashBalance, a.tier, c.address1
	 * as address, c.city, c.state, c.zip, c.id as customer_id, c.firstName,
	 * c.lastName from customer c left outer join account a on a.customer_id = c.id
	 * order by c.id
	 */
	@Override
	public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
		Customer customer = new Customer();
		customer.setId(rs.getInt("customer_id"));
		customer.setFirstName(rs.getString("firstName"));
		customer.setLastName(rs.getString("lastName"));
		customer.setAccount(buildAccount(rs));
		customer.setAddress(buildAddress(rs));
		return customer;
	}

	private Account buildAccount(ResultSet resultSet) throws SQLException {
		Account account = new Account();
		account.setId(resultSet.getLong("account_id"));
		account.setAccountNumber(resultSet.getString("accountNumber"));
		account.setTier(PricingTier.convert(resultSet.getInt("tier")));
		account.setCashBalance(resultSet.getBigDecimal("cashBalance"));
		return account;
	}

	private Address buildAddress(ResultSet resultSet) throws SQLException {
		Address address = new Address();
		address.setAddress1(resultSet.getString("address"));
		address.setCity(resultSet.getString("city"));
		address.setState(resultSet.getString("state"));
		address.setZip(resultSet.getString("zip"));
		return address;
	}
}
