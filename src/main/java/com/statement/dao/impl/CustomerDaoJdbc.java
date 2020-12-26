package com.statement.dao.impl;

import java.sql.ResultSet;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.statement.dao.CustomerDao;
import com.statement.domain.Address;
import com.statement.domain.Customer;
@Component
public class CustomerDaoJdbc extends JdbcTemplate implements CustomerDao {
	
	private static final String FIND_BY_TAX_ID = "select * from customer c where ssn = ?";

	public CustomerDaoJdbc(DataSource dataSource) {
		this.setDataSource(dataSource);
	}
	
	@Override
	public Customer findCustomerByTaxId(String taxId) {
		RowMapper<Customer> mapper = (ResultSet rs, int rowNum) -> {

			Address address = new Address();
			address.setAddress1(rs.getString("address1"));
			address.setCity(rs.getString("city"));
			address.setState(rs.getString("state"));
			address.setZip(rs.getString("zip"));

			Customer customer = new Customer();
			customer.setId(rs.getLong("id"));
			customer.setFirstName(rs.getString("firstName"));
			customer.setLastName(rs.getString("lastName"));
			customer.setTaxId(rs.getString("ssn"));
			customer.setAddress(address);

			return customer;
		};
		
		List<Customer> customers = query(FIND_BY_TAX_ID, new Object[] { taxId }, mapper);
		return customers != null && !customers.isEmpty() ? customers.get(0) : null;
	}
}
