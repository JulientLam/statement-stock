package com.statement.dao;

import com.statement.domain.Customer;

public interface CustomerDao {
 public Customer findCustomerByTaxId(String taxId);
}
