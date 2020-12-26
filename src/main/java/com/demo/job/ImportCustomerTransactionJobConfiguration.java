package com.demo.job;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.FormatterLineAggregator;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.demo.common.SqlQuery;
import com.demo.domain.Customer;
import com.demo.listener.CustomerItemListener;
import com.demo.reader.CustomerFieldMapper;
import com.demo.reader.CustomerFileReader;
import com.demo.reader.TransactionFieldSetMapper;
import com.demo.writter.CustomerItemPreparedStatementSetter;

@Configuration
public class ImportCustomerTransactionJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Value("classpath*:/data/customerTransaction*.csv")
	private Resource[] resources;

	@Value("${output-resources}")
	private String ouputRsources;

	@Autowired
	private DataSource dataSource;


	@Bean
	public MultiResourceItemReader<Customer> multiResourceItemReader() {
		MultiResourceItemReader<Customer> multiResourceReader = new MultiResourceItemReader<Customer>();
		System.out.println(resources.length);
		multiResourceReader.setResources(resources);
		multiResourceReader.setDelegate(customerFileReader());
		return multiResourceReader;
	}

	@Bean
	public CustomerFileReader customerFileReader() {
		CustomerFileReader fileReader = new CustomerFileReader();
		fileReader.setDelegate(fileItemReader());
		return fileReader;
	}

	@Bean
	public FlatFileItemReader<Object> fileItemReader() {
		FlatFileItemReader<Object> reader = new FlatFileItemReader<Object>();

		PatternMatchingCompositeLineMapper lineMapper = new PatternMatchingCompositeLineMapper<>();

		DelimitedLineTokenizer tokenizerForCustomer = new DelimitedLineTokenizer();
		tokenizerForCustomer.setDelimiter(",");
		tokenizerForCustomer.setNames("taxId", "firstName", "lastName", "address", "city", "state", "zip",
				"accountNumber");

		DelimitedLineTokenizer tokenizerForTransaction = new DelimitedLineTokenizer();
		tokenizerForTransaction.setDelimiter(",");
		tokenizerForTransaction.setNames("accountNumber", "stockTicker", "price", "quantity", "timestamp");

		Map<String, LineTokenizer> tokenizers = new HashMap<String, LineTokenizer>();
		tokenizers.put("CU*", tokenizerForCustomer);
		tokenizers.put("TRAN*", tokenizerForTransaction);

		Map<String, FieldSetMapper> fieldSetMappers = new HashMap<String, FieldSetMapper>();
		fieldSetMappers.put("CU*", new CustomerFieldMapper());
		fieldSetMappers.put("TRAN*", new TransactionFieldSetMapper());

		lineMapper.setTokenizers(tokenizers);
		lineMapper.setFieldSetMappers(fieldSetMappers);

		reader.setLineMapper(lineMapper);

		return reader;
	}

	@Bean
	public CustomerItemListener customerItemListener() {
		return new CustomerItemListener();
	}

	@Bean
	public JdbcBatchItemWriter<Customer> batchItemWriter() {
		JdbcBatchItemWriter<Customer> batchItemWriter = new JdbcBatchItemWriter<>();
		batchItemWriter.setDataSource(dataSource);
		batchItemWriter.setSql(SqlQuery.CUS_INSERT);
		batchItemWriter.setItemPreparedStatementSetter(itemPreparedStatementSetter());
		return batchItemWriter;
	}

	@Bean
	public CustomerItemPreparedStatementSetter itemPreparedStatementSetter() {
		return new CustomerItemPreparedStatementSetter();
	}

	@Bean
	public Step step() {
		return stepBuilderFactory.get("importCustomerAndTransactionData")
				.<Object, Customer>chunk(2)
				.reader(multiResourceItemReader())
				.faultTolerant()
				.skipLimit(100)
				.skip(Exception.class)
				.listener(new CustomerItemListener())
				.writer(batchItemWriter())
				.build();
	}

	@Bean
	public Job job() {
		return jobBuilderFactory.get("job04").start(step()).build();
	}

//	@Bean
//	public CustomerLineAggregator lineAggregator() {
//		CustomerLineAggregator aggregator =new CustomerLineAggregator();
//		aggregator.setCustomerLineAggregator(customerLineAggregator());
//		aggregator.setTransactionLineAggregator(transactionLineAggregator());
//		return aggregator;
//	}
//	
//	@Bean
//	public FormatterLineAggregator<Customer> customerLineAggregator() {
//		FormatterLineAggregator lineAggregator =new FormatterLineAggregator<>();
//		BeanWrapperFieldExtractor<Customer> fieldExtractor =new BeanWrapperFieldExtractor<>();
//		fieldExtractor.setNames(new String[] {"taxId","firstName","lastName","address","city","state","zip","accountNumber"});
//		lineAggregator.setFormat("%s %s. %s, %s, %s %s %s %s");
//		return lineAggregator ;
//	}
//	
//	@Bean
//	public FormatterLineAggregator<Transaction> transactionLineAggregator(){
//		FormatterLineAggregator lineAggregator =new FormatterLineAggregator<>();
//		BeanWrapperFieldExtractor<Transaction> fieldExtractor =new BeanWrapperFieldExtractor<>();
//		fieldExtractor.setNames(new String[] {"accountNumber","stockTicker","price","quantity","timestamp"});
//		lineAggregator.setFormat("%s %s %s %s %s");
//		return lineAggregator ;
//	}
}
