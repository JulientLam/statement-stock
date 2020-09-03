package com.demo.reader;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.demo.domain.Account;
import com.demo.domain.Address;
import com.demo.domain.Customer;

public class CustomerFieldMapper implements FieldSetMapper<Customer> {

	@Override
	public Customer mapFieldSet(FieldSet fieldSet) throws BindException {
		Customer customer = new Customer();
		customer.setFirstName(fieldSet.readString("firstName"));
		customer.setLastName(fieldSet.readString("lastName"));
		customer.setTaxId(fieldSet.readString("taxId"));
		customer.setAddress(buildAddress(fieldSet));
		customer.setAccount(buildAccount(customer, fieldSet));
		return customer;
	}

	private Address buildAddress(FieldSet fieldSet) {
		Address address = new Address();
		address.setAddress1(fieldSet.readString("address"));
		address.setCity(fieldSet.readString("city"));
		address.setState(fieldSet.readString("state"));
		address.setZip(fieldSet.readString("zip"));
		return address;
	}

	private Account buildAccount(Customer customer, FieldSet fieldSet) {
		Account account = new Account();
		account.setAccountNumber(fieldSet.readString("accountNumber"));
		account.setCustomer(customer);
		return account;
	}
}
