package com.logiware.rates.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuoteResponse {

	String message;
	Integer status;
	Map<String, List<ShipQuoteResponse>> dataMap;
	
}
