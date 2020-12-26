package com.demo.listener;


import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.batch.item.file.FlatFileParseException;

import com.demo.domain.Customer;

public class CustomerItemListener extends ItemListenerSupport<Customer, Customer>{
//	private Logger logger = Logger.getLogger(CustomerItemListener.class);
	
	@Override
	public void onReadError(Exception ex) {
		if(ex instanceof FlatFileParseException) {
			FlatFileParseException flatFileParseException = (FlatFileParseException) ex;
			StringBuilder errorMessage = new  StringBuilder();
			errorMessage.append("An error occured while processing the"+flatFileParseException.getLineNumber()+" line of the file. Below was the fault input\n");
			errorMessage.append(flatFileParseException.getInput() + "\n");
//			logger.error(errorMessage.toString(),ex);
			System.out.println("[ERROR]"+ errorMessage.toString());
		}else {
			System.out.println("[ERROR] An error has occured");
//			logger.error("An error has occured", ex);
		}
	}
}
