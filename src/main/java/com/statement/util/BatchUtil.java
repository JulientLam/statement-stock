package com.statement.util;

public class BatchUtil {
  public static final String CUS_TOKENIZER_PATTERN = "^\\d+,[A-Z][a-zA-Z]+,[A-Z][a-zA-Z]+,.*";
  public static final String[] CUS_TOKENIZER_NAMES = new String[] {"taxId","firstName","lastName","address","city","state","zip","accountNumber"};
  
  public static final String TRAN_TOKENIZER_PATTERN = "^\\d+,[A-Z\\.\\ ]+,\\d+.*";
  public static final String[] TRAN_TOKENIZER_NAMES = new String[] {"accountNumber","stockTicker","price","quantity","timestamp"};
  
  
}
