package com.statement.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Customer{
	private long id = -1l;
	private String firstName;
	private String lastName;
	private Address address;
	private Account account;
	private String taxId;
	private String email="pdao1097@gmail.com";

	@Override
	public String toString() {
		String output = "Customer number " + id + ", " + firstName + " " + lastName;
		if (address != null) {
			output = output + " who lives in " + address.getCity() + "," + address.getState();
		}
		if (account != null && account.getTransactions() != null) {
			output = output + " has " + account.getTransactions().size() + " transactions.";
		}
		return output;
	}
}
