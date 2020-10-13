package com.logiware.rates.dto;

import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class QuoteResponse {

	String message;
	Integer status;
	Map<String, List<ShipQuoteResponse>> dataMap;
	
}
