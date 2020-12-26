package com.statement.process;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.statement.dao.AccountDao;
import com.statement.dao.CustomerDao;
import com.statement.dao.TickerDao;
import com.statement.domain.Account;
import com.statement.domain.Customer;
import com.statement.domain.Ticker;
import com.statement.domain.Transaction;
import com.statement.domain.TransactionType;

/*
 * Update the customer item if it exists in the database.
 */
@Component
public class CustomerLookupItemProcessor implements ItemProcessor<Object, Object> {

	@Autowired
	private CustomerDao customerDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private TickerDao tickerDao;

	@Override
	public Object process(Object curItem) throws Exception {
		if (curItem instanceof Customer) {
			doCustomerUpdate((Customer) curItem);
		} else if (curItem instanceof Transaction) {
			doTransactionUpdate((Transaction) curItem);
		} else {
//			throw new InvalidItemException("An invalid item was received: " + curItem);
			throw new Exception("An invalid item was received: " + curItem);
		}
		return curItem;
	}

	private void doTransactionUpdate(Transaction curItem) {
		updateTicker(curItem);
		updateAccount(curItem);
		curItem.setType(TransactionType.STOCK);
	}


	private void updateAccount(Transaction curItem) {
		Account account = accountDao.findAccountByNumber(curItem.getAccountNumber());
		curItem.setAccountId(account.getId());

	}

	private void updateTicker(Transaction curItem) {
		Ticker ticker = tickerDao.findTickerBySymbol(curItem.getTicker());
		if (ticker == null) {
			Ticker newTicker = new Ticker();
			newTicker.setTicker(curItem.getTicker());
			tickerDao.saveTicker(newTicker);
			ticker = tickerDao.findTickerBySymbol(curItem.getTicker());
		}
		curItem.setTickerId(ticker.getId());
	}

	private void doCustomerUpdate(Customer curCustomer) {
		Customer storedCustomer = customerDao.findCustomerByTaxId(curCustomer.getTaxId());
		Account account = accountDao.findAccountByNumber(curCustomer.getAccount().getAccountNumber());
		curCustomer.setId(storedCustomer.getId());
		curCustomer.setAccount(account);

	}

}
