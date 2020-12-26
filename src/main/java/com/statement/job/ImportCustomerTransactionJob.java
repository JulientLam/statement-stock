package com.statement.job;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.mail.SimpleMailMessageItemWriter;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.classify.SubclassClassifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.statement.dao.TickerDao;
import com.statement.domain.AccountTransaction;
import com.statement.domain.AccountTransactionQuantity;
import com.statement.domain.Customer;
import com.statement.domain.Statement;
import com.statement.domain.Ticker;
import com.statement.domain.Transaction;
import com.statement.process.CustomerLookupItemProcessor;
import com.statement.process.FeesItemProcessor;
import com.statement.process.PriceTierItemProcessor;
import com.statement.process.StatementEmailConverter;
import com.statement.reader.AccountTransactionQuantityRowMapper;
import com.statement.reader.AccountTransactionRowMapper;
import com.statement.reader.CustomerFieldMapper;
import com.statement.reader.CustomerStatementReader;
import com.statement.reader.CustomerStatementRowMapper;
import com.statement.reader.RegularExpressionLineMapper;
import com.statement.reader.TransactionFieldSetMapper;
import com.statement.util.BatchUtil;
import com.statement.writer.StatementFormatter;
import com.statement.writer.StatementHeaderCallback;
import com.statement.writer.StatementSuffixGennerator;

@Configuration
public class ImportCustomerTransactionJob {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private CustomerLookupItemProcessor itemProcessor;
	@Autowired
	private DataSource dataSource;
	@Autowired
	private TickerDao tickerDao;
	@Autowired
	private StatementSuffixGennerator statementSuffixGennerator;
	@Autowired
	private JavaMailSender javaMailSenderImpl;
	
	@Value("${path.file.output}")
	String pathSatementOutput;
	
	@Value("classpath*:/data/input/customer*.csv")
	private Resource[] inputFile;

	@Value("classpath*:/data/input/stockFile*.csv")
	private Resource[] priceFile;
	

	@Bean
	public DelimitedLineTokenizer customerLineTokenizer() {
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setDelimiter(",");
		tokenizer.setNames(BatchUtil.CUS_TOKENIZER_NAMES);
		return tokenizer;
	}

	@Bean
	public DelimitedLineTokenizer transactionLineTokenizer() {
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setDelimiter(",");
		tokenizer.setNames(BatchUtil.TRAN_TOKENIZER_NAMES);
		return tokenizer;
	}

	@Bean
	public RegularExpressionLineMapper customerTransactionLineMapper() {
		RegularExpressionLineMapper lineMapper = new RegularExpressionLineMapper();

		Map<String, FieldSetMapper<Object>> mappers = new HashMap<String, FieldSetMapper<Object>>();
		mappers.put(BatchUtil.CUS_TOKENIZER_PATTERN, new CustomerFieldMapper());
		mappers.put(BatchUtil.TRAN_TOKENIZER_PATTERN, new TransactionFieldSetMapper());

		Map<String, LineTokenizer> tokenizers = new HashMap<String, LineTokenizer>();
		tokenizers.put(BatchUtil.CUS_TOKENIZER_PATTERN, customerLineTokenizer());
		tokenizers.put(BatchUtil.TRAN_TOKENIZER_PATTERN, transactionLineTokenizer());

		lineMapper.setMappers(mappers);
		lineMapper.setTokenizers(tokenizers);

		return lineMapper;
	}

	@Bean
	public FlatFileItemReader<Object> customerTransactionReader() {
		FlatFileItemReader<Object> itemReader = new FlatFileItemReader<Object>();
		itemReader.setResource(inputFile[0]);
		itemReader.setLineMapper(customerTransactionLineMapper());
		return itemReader;
	}

	@Bean
	public JdbcBatchItemWriter<Object> customerImportWriter() {
		JdbcBatchItemWriter<Object> batchItemWriter = new JdbcBatchItemWriter<Object>();
		batchItemWriter.setDataSource(dataSource);
		/*
		 * Nhanh nhu bi phu thuoc vo Kieu Object batchItemWriter.setSql(
		 * "update customer set firstName = :firstName, lastName = :lastName, address1 = :address.address1,"
		 * +
		 * " city = :address.city, state = :address.state, zip = :address.zip where ssn = :taxId"
		 * ); batchItemWriter.setItemSqlParameterSourceProvider(new
		 * BeanPropertyItemSqlParameterSourceProvider<Customer>());
		 */
		batchItemWriter.setSql("update customer set firstName = ?, lastName = ?, address1 = ?,"
				+ " city = ?, state = ?, zip = ? where ssn = ?");
		batchItemWriter.setItemPreparedStatementSetter(new CustomerItemPreparedStatementSetter());
		return batchItemWriter;
	}

	private final class CustomerItemPreparedStatementSetter implements ItemPreparedStatementSetter<Object> {

		@Override
		public void setValues(Object item, PreparedStatement ps) throws SQLException {
			Customer customer = (Customer) item;
			ps.setString(1, customer.getFirstName());
			ps.setString(2, customer.getLastName());
			ps.setString(3, customer.getAddress().getAddress1());
			ps.setString(4, customer.getAddress().getCity());
			ps.setString(5, customer.getAddress().getState());
			ps.setString(6, customer.getAddress().getZip());
			ps.setString(7, customer.getTaxId());
		}
	}

	@Bean
	public JdbcBatchItemWriter<Object> transactionImportWriter() {
		JdbcBatchItemWriter<Object> batchItemWriter = new JdbcBatchItemWriter<Object>();
		batchItemWriter.setDataSource(dataSource);
		batchItemWriter.setSql(
				"insert into [Transaction] (transactionType, executedTime, dollarAmount, qty,tickerId, account_id) "
						+ "values (?,?,?,?,?,?)");
		batchItemWriter.setItemPreparedStatementSetter(new TransactionItemPreparedStatementSetter());
		return batchItemWriter;
	}

	private final class TransactionItemPreparedStatementSetter implements ItemPreparedStatementSetter<Object> {
		@Override
		public void setValues(Object item, PreparedStatement ps) throws SQLException {
			Transaction transaction = (Transaction) item;
			ps.setInt(1, transaction.getType().intValue);
			ps.setDate(2, new Date(transaction.getTradeTimestamp().getTime()));
			ps.setBigDecimal(3, transaction.getDollarAmount());
			ps.setLong(4, transaction.getQuantity());
			ps.setLong(5, transaction.getTickerId());
			ps.setLong(6, transaction.getAccountId());
		}
	}

	@Bean
	public ClassifierCompositeItemWriter<Object> customerTransactionItemWriter() {
		ClassifierCompositeItemWriter<Object> itemWriter = new ClassifierCompositeItemWriter<>();
		SubclassClassifier<Object, ItemWriter<? super Object>> classifier = new SubclassClassifier<>();
		Map<Class<? extends Object>, ItemWriter<? super Object>> map = new HashMap<>();
		map.put(Transaction.class, transactionImportWriter());
		map.put(Customer.class, customerImportWriter());
		classifier.setTypeMap(map);
		itemWriter.setClassifier(classifier);
		return itemWriter;
	}

	@Bean
	public ItemWriter<Object> itemWriter() {
		return (items) -> {
			int i = 0;
			for (Object item : items) {
				System.out.println("i= " + i++);
				System.out.println(item);
			}
		};
	}

	// -------------------------------------------------------------------------
	// read price
	@Bean
	public FlatFileItemReader<Ticker> currentPriceFileItemReader() {
		FlatFileItemReader<Ticker> flatFileItem = new FlatFileItemReader<Ticker>();
		flatFileItem.setResource(priceFile[0]);
		DefaultLineMapper<Ticker> lineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setDelimiter(",");
		tokenizer.setNames("tiker", "price");
		lineMapper.setLineTokenizer(tokenizer);

		lineMapper.setFieldSetMapper(fieldSet -> {
			Ticker ticker = new Ticker();
			ticker.setTicker(fieldSet.readString("tiker"));
			ticker.setPrice(fieldSet.readBigDecimal("price"));
			System.out.println("[TICKER] " + ticker);
			return ticker;
		});
		System.out.println("FILE: " + priceFile[0].getFilename());
		flatFileItem.setLineMapper(lineMapper);
		return flatFileItem;
	}

	@Bean
	public JdbcBatchItemWriter<Ticker> priceStockJdbcBatchItemWriter() {
		JdbcBatchItemWriter<Ticker> batchItemWriter = new JdbcBatchItemWriter<Ticker>();
		batchItemWriter.setDataSource(dataSource);
		batchItemWriter.setSql("update ticker set currentPrice = :price where ticker = :ticker");
		batchItemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Ticker>());
		return batchItemWriter;
	}

	// ---------------Caculater tier---------------------------------

	@Bean
	public JdbcCursorItemReader<AccountTransactionQuantity> accountTransactionQtyItemReader() {
		JdbcCursorItemReader<AccountTransactionQuantity> itemReader = new JdbcCursorItemReader<>();
		itemReader.setDataSource(dataSource);
		itemReader.setSql(
				"SELECT a.accountNumber, COUNT(*) AS qty FROM Account a INNER JOIN [Transaction] t ON t.account_id = a.id GROUP BY a.accountNumber");
		itemReader.setRowMapper(new AccountTransactionQuantityRowMapper());
		return itemReader;
	}

	@Bean
	public JdbcBatchItemWriter<AccountTransactionQuantity> tiersUpdateWriter() {
		JdbcBatchItemWriter<AccountTransactionQuantity> itemWriter = new JdbcBatchItemWriter<AccountTransactionQuantity>();
		itemWriter.setDataSource(dataSource);
		itemWriter.setSql("update account set tier = :tier.value where accountNumber = :accountNumber");
		itemWriter.setItemSqlParameterSourceProvider(
				new BeanPropertyItemSqlParameterSourceProvider<AccountTransactionQuantity>());
		return itemWriter;
	}

	// -----------------Caculator fee for each Transaction------------

	@Bean
	public JdbcCursorItemReader<AccountTransaction> transactionPricingItemReader() {
		JdbcCursorItemReader<AccountTransaction> itemReader = new JdbcCursorItemReader<AccountTransaction>();
		itemReader.setDataSource(dataSource);
		itemReader.setSql(
				"select a.id as accountId, a.accountNumber, t.id as transactionId, t.qty, tk.ticker,a.tier, t.executedTime, t.dollarAmount from account a inner join [transaction] t on a.id =t.account_id inner join ticker tk on t.tickerId = tk.id  order by t.executedTime");
		itemReader.setRowMapper(new AccountTransactionRowMapper());
		return itemReader;
	}

	/**
	 * Update fee for each transaction.
	 * 
	 * @return
	 */
	@Bean
	public JdbcBatchItemWriter<AccountTransaction> feesItemWriter() {
		JdbcBatchItemWriter<AccountTransaction> itemWriter = new JdbcBatchItemWriter<AccountTransaction>();
		itemWriter.setDataSource(dataSource);
		itemWriter.setSql("update [transaction] set fee = :fee where id = :id");
		itemWriter.setItemSqlParameterSourceProvider(
				new BeanPropertyItemSqlParameterSourceProvider<AccountTransaction>());
		return itemWriter;
	}

	/**
	 * Update balance after minus fees.
	 * 
	 * @return
	 */
	@Bean
	public JdbcBatchItemWriter<AccountTransaction> cashBalanceUpdateWriter() {
		JdbcBatchItemWriter<AccountTransaction> itemWriter = new JdbcBatchItemWriter<AccountTransaction>();
		itemWriter.setDataSource(dataSource);
		itemWriter.setSql("update account set cashBalance = (cashBalance - :fee) where accountNumber = :accountNumber");
		itemWriter.setItemSqlParameterSourceProvider(
				new BeanPropertyItemSqlParameterSourceProvider<AccountTransaction>());
		return itemWriter;
	}

	@Bean
	public CompositeItemWriter<AccountTransaction> applyFeeWriter() {
		CompositeItemWriter<AccountTransaction> compositeItemWriter = new CompositeItemWriter<>();
		List<ItemWriter<? super AccountTransaction>> delegates = new ArrayList<>();
		delegates.add(cashBalanceUpdateWriter());
		delegates.add(feesItemWriter());
		compositeItemWriter.setDelegates(delegates);
		return compositeItemWriter;
	}

	//----------Gennerate Statement-----------------------------------
	
	/**
	 * Get information for customer.
	 * @return
	 */
	@Bean
	public JdbcCursorItemReader<Customer> customerReader(){
		JdbcCursorItemReader<Customer> itemReader = new JdbcCursorItemReader<Customer>();
		itemReader.setDataSource(dataSource);
		itemReader.setSql("select a.id as account_id, a.accountNumber, a.cashBalance, a.tier, c.address1 as " + 
				"address, c.city, c.state, c.zip, c.id as customer_id, c.firstName, c.lastName from customer c " + 
				"left outer join account a on a.customer_id = c.id order by c.id");
		itemReader.setRowMapper(new CustomerStatementRowMapper());
		return itemReader;
	}
	
	/**
	 * Build Statement info.
	 * @return
	 */
	@Bean
	public CustomerStatementReader customerStatementReader() {
		CustomerStatementReader reader = new CustomerStatementReader();
		reader.setTickerDao(tickerDao);
		reader.setCustomerReader(customerReader());
		return reader;
	}
	
	/**
	 * Write 1 Statement info into the file.
	 * @return
	 */
	@Bean
	public FlatFileItemWriter<Statement> statementWriter(){
		FlatFileItemWriter<Statement> itemWriter = new FlatFileItemWriter<Statement>();
		itemWriter.setAppendAllowed(true);
		itemWriter.setHeaderCallback(new StatementHeaderCallback());
		itemWriter.setLineAggregator(new StatementFormatter());
		return itemWriter;
	}
	
	/**
	 * Write many Statements info into the files.
	 * @return
	 */
	@Bean
	public MultiResourceItemWriter<Statement> statementsWriter(){
		MultiResourceItemWriter<Statement> writer = new MultiResourceItemWriter<>();
		
		writer.setResource(new FileSystemResource(pathSatementOutput));
		writer.setResourceSuffixCreator(statementSuffixGennerator);
		writer.setItemCountLimitPerResource(1);
		writer.setDelegate(statementWriter());
		return writer;
	}

	//-------Send mail----------------------------
//	@Bean 
//	public JavaMailSenderImpl statementMailSender() {
//		JavaMailSenderImpl impl = new JavaMailSenderImpl();
//		impl.setHost("smtp.gmail.com");
//		impl.setPort(587);
//		impl.setPassword("Huong!2345");
//		impl.setUsername("huonglam.julient@gmail.com");
////		Properties javaMailProperties = impl.getJavaMailProperties();
////		javaMailProperties.put("mail.transport.protocol", "smtp");
////		javaMailProperties.put("mail.smtp.auth", "true");
////		javaMailProperties.put("mail.smtp.starttls.enable", "true");
////		javaMailProperties.put("mail.debug", "true");
//		return impl;
//	}
	@Bean
	public SimpleMailMessageItemWriter satementslMessageMailItemWriter() {
		SimpleMailMessageItemWriter itemWriter = new SimpleMailMessageItemWriter();
		itemWriter.setMailSender(javaMailSenderImpl);
		return itemWriter;
	}
	// ---------Step--------------------------------------------------
	@Bean
	public Step importCustomerTransaction() {
		return stepBuilderFactory.get("step1")
				.chunk(1)
				.reader(customerTransactionReader())
				.processor(itemProcessor)
				.writer(customerTransactionItemWriter()).build();
	}

	// import curent price
	@Bean
	public Step tickerUpdateWriter() {
		return stepBuilderFactory.get("step2")
				.<Ticker, Ticker>chunk(1)
				.reader(currentPriceFileItemReader())
				.writer(priceStockJdbcBatchItemWriter())
				.build();
	}

	@Bean
	public Step calculateTiers() {
		return stepBuilderFactory.get("step3")
				.<AccountTransactionQuantity, AccountTransactionQuantity>chunk(1)
				.reader(accountTransactionQtyItemReader())
				.processor(new PriceTierItemProcessor())
				.writer(tiersUpdateWriter())
				.build();
	}

	@Bean
	public Step calculateTransactionFees() {
		return stepBuilderFactory.get("step4").<AccountTransaction, AccountTransaction>chunk(1)
				.reader(transactionPricingItemReader())
				.processor(new FeesItemProcessor())
				.writer(applyFeeWriter())
				.build();
	}

	@Bean
	public Step generateMonthlyStatements() {
		return stepBuilderFactory.get("step5")
				.<Statement,Statement>chunk(1)
				.reader(customerStatementReader())
				.writer(statementsWriter())
				.build();
	}
	
	@Bean
	public Step sendMail() {
		return stepBuilderFactory.get("step6")
				.<Statement,SimpleMailMessage>chunk(1)
				.reader(customerStatementReader())
				.processor(new StatementEmailConverter())
				.writer(satementslMessageMailItemWriter())
				.build();
	}
	@Bean
	public Job job() {
		return jobBuilderFactory.get("job_statement")
				.start(importCustomerTransaction())
				.next(tickerUpdateWriter())
				.next(calculateTiers())
				.next(calculateTransactionFees())
				.next(generateMonthlyStatements())
				.next(sendMail())
				.build();
	}

}
