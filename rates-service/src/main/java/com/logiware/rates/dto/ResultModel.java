package com.logiware.rates.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ResultModel {

	private String carrier;
	private String scac;
	private String effectiveDate;
	private String expirationDate;
	private String filename;
	private String loadedDate;
	private String sites;
	private Long id;
	
}
