package com.logiware.rates.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class WmResponse {
	@JsonProperty("chargeClass")
	String chargeClass; // Wmmodel charge class
	@JsonProperty("chargeDescription")
	String chargeDescription; // The description of the surcharge
	@JsonProperty("chargeType")
	String chargeType; // Wmmodel charge type
	boolean chargeTypeFlat; // Wmmodel flat charge type
	boolean chargeTypeWm; // Wmmodel charge type
	String contractId; // Wmmodel contract ID
	@JsonProperty("currency")
	String currency; // Currency code of the surcharge
	Double currencyRate; // Wmmodel currency type
	String effectiveDt; // Wmmodel effective date
	String expirationDt; // Wmmodel expiration date
	@JsonProperty("greater_10cbm")
	Double greater10Cbm; // Price for greater than 10 cbm
	@JsonProperty("greater_45")
	Double greater45; // Price for greater than 45 kg
	@JsonProperty("greater_100")
	Double greater100; // Price for greater than 100 kg
	@JsonProperty("greater_250")
	Double greater250; // Price for greater than 250 kg
	@JsonProperty("greater_300")
	Double greater300; // Price for greater than 300 kg
	@JsonProperty("greater_500")
	Double greater500; // Price for greater than 500 kg
	@JsonProperty("greater_1000")
	Double greater1000; // Price for greater than 1000 kg
	@JsonProperty("greater_2500")
	Double greater2500; // Price for greater than 2500 kg
	@JsonProperty("greater_10000kg")
	Double greater10000Kg; // Price for greater than 10 cbm
	@JsonProperty("less_10cbm")
	Double less10Cbm; // Price for less than or equal to 10 cbm
	@JsonProperty("less_45")
	Double less45; // Price for less than or equal to 45 kg
	@JsonProperty("less_10000kg")
	Double less10000Kg; // Price for less than or equal to 10 cbm
	@JsonProperty("max")
	Double max; // Maximum price if applicable
	@JsonProperty("min")
	Double min; // Minimum price
	String overPivotRate; // Wmmodel over pivot rate
	String pivotRate; // Wmmodel pivot rate
	String pivotWeight; // Wmmodel pivot weight
	String surchargeCode; // Wmmodel surcharge code
	String surchargeLineId; // Wmmodel surcharge line ID
	Double totalPrice; // Wmmodel total price
	@JsonProperty("uldModel")
	boolean uldModel; // Wmmodel uldModel flag
	@JsonProperty("unitText")
	String unitText; // Unit description Values: Per 1KG, Per 1 CBM, Per 1000 kg / Per 1 CBM, Per 1 CBM / Per 1000 kg, PC, EA

}
