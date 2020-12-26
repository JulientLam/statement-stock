package com.statement.process;

import org.springframework.batch.item.ItemProcessor;

import com.statement.domain.AccountTransactionQuantity;
import com.statement.domain.PricingTier;

public class PriceTierItemProcessor implements ItemProcessor<AccountTransactionQuantity, AccountTransactionQuantity> {

	@Override
	public AccountTransactionQuantity process(AccountTransactionQuantity item) throws Exception {
		if (item.getTransactionCount() <= 1000) {
			item.setTier(PricingTier.I);
		} else if (item.getTransactionCount() > 1000 && item.getTransactionCount() <= 100000) {
			item.setTier(PricingTier.II);
		} else if (item.getTransactionCount() > 100000 && item.getTransactionCount() <= 1000000) {
			item.setTier(PricingTier.III);
		} else {
			item.setTier(PricingTier.IV);
		}
		return item;
	}

}
