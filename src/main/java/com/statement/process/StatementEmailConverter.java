package com.statement.process;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.mail.SimpleMailMessage;

import com.statement.domain.Statement;
import com.statement.util.FormatStatement;

public class StatementEmailConverter implements ItemProcessor<Statement, SimpleMailMessage> {

	@Override
	public SimpleMailMessage process(Statement item) throws Exception {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom("lamhuong97.utt@gmail.com");
		mailMessage.setTo(item.getCustomer().getEmail());
		mailMessage.setSubject("[Stock] Report mounthly Statement Stock");
		mailMessage.setText(FormatStatement.format(item));
		return mailMessage;
	}

}
