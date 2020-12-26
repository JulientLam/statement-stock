package com.statement.process;

import java.math.BigDecimal;

import org.springframework.batch.item.ItemProcessor;

import com.statement.domain.AccountTransaction;
import com.statement.domain.PricingTier;

public class FeesItemProcessor implements ItemProcessor<AccountTransaction, AccountTransaction> {

	@Override
	public AccountTransaction process(AccountTransaction item) throws Exception {
		if (item.getTier() == PricingTier.I) {
			item.setFee(priceTierOneTransaction(item.getPrice()));
			return item;
		}
		if (item.getTier() == PricingTier.II) {
			item.setFee(new BigDecimal(3.00));
			return item;
		}
		if (item.getTier() == PricingTier.III) {
			item.setFee(new BigDecimal(2.00));
			return item;
		}
		
		item.setFee(new BigDecimal(1.00));
		return item;
	}

	private BigDecimal priceTierOneTransaction(BigDecimal priceTransaction) {
		return priceTransaction.multiply(new BigDecimal(0.001)).add(new BigDecimal(9.00));
	}
}
