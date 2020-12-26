CREATE DATABASE statemen_stock
GO
USE statemen_stock
GO
--create table.
CREATE TABLE Customer(
	id INT NOT NULL,
	firstName VARCHAR(45),
	lastName VARCHAR(45),
	ssn VARCHAR(9),
	address1 VARCHAR(45),
	address2 VARCHAR(45),
	city VARCHAR(45),
	[state] VARCHAR(2),
	zip VARCHAR(9)
)
GO
CREATE TABLE Account(
	id INT NOT NULL,
	accountNumber VARCHAR(16),
	cashBalance DECIMAL(15,2),
	tier INT,
	customer_id INT
)
GO
CREATE TABLE [Transaction](
	id INT NOT NULL,
	[type] INT,
	dollarAmount DECIMAL(15,2),
	qty INT,
	ticker_id INT,
	account_id INT
)
GO
CREATE TABLE Ticker(
	id INT NOT NULL,
	ticker VARCHAR(45),
	currentPrice DECIMAL(8,2)
)
GO

--add constraint for Customer table.
ALTER TABLE Customer ADD CONSTRAINT PK_Customer PRIMARY KEY (id)
GO

--add constraint for Account table.
ALTER TABLE Account ADD CONSTRAINT PK_Account PRIMARY KEY (id)
GO
ALTER TABLE Account ADD CONSTRAINT FK_Account_Customer FOREIGN KEY (Customer_id) REFERENCES Customer(id)
GO
ALTER TABLE Account ADD CONSTRAINT U_Customer_ID UNIQUE (Customer_id)
GO

--add constraint for Ticker table.
ALTER TABLE Ticker ADD CONSTRAINT PK_Ticker PRIMARY KEY (id)

--add constraint for Transaction table.
ALTER TABLE [Transaction] ADD CONSTRAINT PK_Transaction PRIMARY KEY (id)
GO
ALTER TABLE [Transaction] ADD CONSTRAINT FK_Transaction_Account FOREIGN KEY (Account_id) REFERENCES Account(id)
GO
ALTER TABLE [Transaction] ADD CONSTRAINT FK_Transaction_Ticker FOREIGN KEY (ticker_id) REFERENCES Ticker(id)





select * from Customer
select * from Account
select * from [Transaction]
select * from Ticker
delete from Customer where id = 1
insert into Customer(id,firstName,lastName,ssn,address1,city,[state],zip) values
(1,'Joshua','Thompson','205866465','3708 Park','Fairview','LA','58517')

insert into Account(id, accountNumber,cashBalance,tier,customer_id) values
(1,'3276793917668488', 122, 1,1)

insert into Ticker(id,ticker,currentPrice) values
(1,'SKT',1.1),
(2,'KSS',1.2),
(3,'CSR',1.3),
(4,'EMD',1.4),
(5,'SYY',1.5),
(6,'BYM',1.6)

