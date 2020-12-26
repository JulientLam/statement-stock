package com.statement.writer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.item.file.ResourceSuffixCreator;
import org.springframework.stereotype.Component;

@Component
public class StatementSuffixGennerator implements ResourceSuffixCreator{

	@Override
	public String getSuffix(int index) {
		return format() + ".txt";
	}
	private String format() {
		
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
	}
	
}
