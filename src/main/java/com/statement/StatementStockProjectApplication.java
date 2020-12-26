package com.statement;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class StatementStockProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(StatementStockProjectApplication.class, args);
	}

}
