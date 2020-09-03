package com.demo.reader;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.demo.domain.Customer;

@Configuration
public class JobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public CustomerFileReader customerFileReader() {
		CustomerFileReader fileReader = new CustomerFileReader();
		fileReader.setDelegate(fileItemReader());
		return fileReader;
	}
	
	
	@Bean
	public FlatFileItemReader<Object> fileItemReader(){
		FlatFileItemReader<Object> reader =new FlatFileItemReader<Object>();
		
		reader.setResource(new ClassPathResource("/data/customerTransaction.csv"));		
		PatternMatchingCompositeLineMapper lineMapper=new PatternMatchingCompositeLineMapper<>();
				
		DelimitedLineTokenizer tokenizerForCustomer =new DelimitedLineTokenizer();
		tokenizerForCustomer.setDelimiter(",");
		tokenizerForCustomer.setNames("taxId","firstName","lastName","address","city","state","zip","accountNumber");
		
		DelimitedLineTokenizer tokenizerForTransaction =new DelimitedLineTokenizer();
		tokenizerForTransaction.setDelimiter(",");
		tokenizerForTransaction.setNames("accountNumber","stockTicker","price","quantity","timestamp");
		
		Map<String, LineTokenizer> tokenizers =new HashMap<String, LineTokenizer>();
		tokenizers.put("CU*",tokenizerForCustomer);
		tokenizers.put("TRAN*",tokenizerForTransaction);

		Map<String, FieldSetMapper> fieldSetMappers = new HashMap<String, FieldSetMapper>();
		fieldSetMappers.put("CU*",new CustomerFieldMapper());
		fieldSetMappers.put("TRAN*", new TransactionFieldSetMapper());
		
		lineMapper.setTokenizers(tokenizers);
		lineMapper.setFieldSetMappers(fieldSetMappers);
		
		reader.setLineMapper(lineMapper);
		
		return reader;
	}
	
	
	@Bean
	public ItemWriter<Customer> itemWriter(){
		return (items)->{
			for(Customer obj : items) {
				System.out.println(obj);
			}
		};
	}
	
	@Bean
	public Step step() {
		return stepBuilderFactory.get("step1")
				.<Object,Customer>chunk(1)
				.reader(customerFileReader())
				.writer(itemWriter())
				.build();
	}
	
	@Bean
	public Job job() {
		return jobBuilderFactory.get("job6")
				.start(step())
				.build();
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
