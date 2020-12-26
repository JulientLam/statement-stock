package com.demo.writter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import com.demo.domain.Customer;

public class CustomerItemPreparedStatementSetter implements ItemPreparedStatementSetter<Customer>{

	@Override
	public void setValues(Customer item, PreparedStatement ps) throws SQLException {
		//firstName , lastName, address1, city, state, zip
		ps.setInt(1, 1);
		ps.setString(2, item.getFirstName());
		ps.setString(3, item.getLastName());
		ps.setString(4, item.getAddress().getAddress1());
		ps.setString(5, item.getAddress().getCity());
		ps.setString(6, item.getAddress().getState());
		ps.setString(7, item.getAddress().getZip());
		
	}

}
