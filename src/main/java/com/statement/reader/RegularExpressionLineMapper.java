package com.statement.reader;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.InitializingBean;

public class RegularExpressionLineMapper implements LineMapper<Object>, InitializingBean {

	private Map<String, LineTokenizer> tokenizers;
	private Map<String, FieldSetMapper<Object>> mappers;
	private Map<Pattern, LineTokenizer> patternTokenizers;
	private Map<LineTokenizer, FieldSetMapper<Object>> patternMappers;

	public void setTokenizers(Map<String, LineTokenizer> tokenizers) {
		this.tokenizers = tokenizers;
	}
	
	public void setMappers(Map<String, FieldSetMapper<Object>> mappers) {
		this.mappers = mappers;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	
		patternTokenizers = new HashMap<Pattern, LineTokenizer>();
		patternMappers = new HashMap <LineTokenizer, FieldSetMapper<Object>>();
		
		for(Map.Entry<String, LineTokenizer> entry : tokenizers.entrySet()) {
			Pattern pattern = Pattern.compile(entry.getKey());
			patternTokenizers.put(pattern, entry.getValue());
			patternMappers.put(entry.getValue(),mappers.get(entry.getKey()));
		}
	}

	private LineTokenizer findLineTokenizer(String input) {
		LineTokenizer tokenizer = null;
		for (Map.Entry<Pattern, LineTokenizer> entry : patternTokenizers.entrySet()) {
			Matcher matcher = entry.getKey().matcher(input);
			if (matcher.find()) {
				tokenizer = entry.getValue();
				break;
			}
		}

		if (tokenizer != null) {
			return tokenizer;
		}
		throw new ParseException("Unable to locate a tokenizer for " + input);
	}

	@Override
	public Object mapLine(String line, int lineNumber) throws Exception {
		LineTokenizer tokenizer = findLineTokenizer(line);
		FieldSet fieldSet = tokenizer.tokenize(line);
		FieldSetMapper<Object> fieldSetMapper = patternMappers.get(tokenizer);
		if(fieldSetMapper!=null) {
			return fieldSetMapper.mapFieldSet(fieldSet);
		}
		throw new ParseException("Unable to locate a tokenizer for"+line);
	}

	
}
