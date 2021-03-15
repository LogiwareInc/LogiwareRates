package com.logiware.rates.dto;
	
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChargeResponse {
	
	Double baseRate; // Charge base rate
	@JsonProperty("bolCharge")
	Boolean bolCharge; // Indicates if the surcharge is BOL-based
	@JsonProperty("cbm")
	Double cbm; // Charge per cubic meters
	Double cbmMax; // Charge per cubic meters max
	Double cbmMin; // Charge per cubic meters min
	@JsonProperty("chargeMax")
	Double chargeMax; // Charges maximum
	@JsonProperty("chargeMin")
	Double chargeMin; // Charges minimum
	@JsonProperty("chargeNotes")
	String chargeNotes; // The notes of surcharge
	@JsonProperty("chargeType")
	String chargeType; // Charge type
	String chargeGroup; // Charge group for APPL eFT
	String contractId; // Charge contract ID
	@JsonProperty("currency")
	String currency; // Charge currency
	Double currencyRate; // Charge currency rate
	@JsonProperty("description")
	String description; // Charge description
	@JsonProperty("eFT_odf")
	String eftOdf; // Easy floating tariff origin destination freight identifier
	String effectiveDt; // Charge effective date format: (dd-MMM-yyyy)
	String expirationDt; // Charge expiration date format: (dd-MMM-yyyy)
	@JsonProperty("prepaidCollectChargeType")
	String prepaidCollectChargeType; // Charge prepaid collect charge type
	String prepaidCollect; // Charge prepaid collect
	@JsonProperty("price")
	Double price; // Charge price
	@JsonProperty("purgeSell")
	Boolean purgeSell; // Charge is purge sell flag
	String rateBasis; // Charge rate basis
	@JsonProperty("realPrice")
	Double realPrice; // Charge real price
	@JsonProperty("source")
	String source; // Charge source
	@JsonProperty("surchargeChargeMode")
	String surchargeChargeMode; // Shipment mode of the charge Values: AIR, LCL, FCL, GROUND
	@JsonProperty("surchargeNotes")
	String surchargeNotes; // Surcharge notes
	@JsonProperty("surchargeODApplication")
	String surchargeODApplication; // Surcharge application of the charge Values: O: Origin, D: Destination
	@JsonProperty("surchargeType")
	String surchargeType; // Charge surcharge type
	@JsonAlias("surcharge_code")
	@JsonProperty("chargeCode")
	String surchargeCode; // Charge surcharge code
	String surchargeContainerType; // Charge surcharge container type
	Boolean surchargeCurrent; // Current charge flag
	Boolean surchargeFuture; // Future surcharge flag
	Boolean surchargeIncluded; // Surcharge included in overall price flag
	String surchargeLineId; // Charge surcharge line ID
	String surchargeSize; // Charge surcharge size
	Boolean surchargeSubjectTo; // Surcharge subject to flag
	@JsonProperty("trueType")
	String trueType; // Charge true type
	@JsonProperty("type")
	String type; // FUTURE, CURRENT, SUBJECT TO, INCLUDED?
	String rateUnit;
	@JsonProperty("externalChargeCode")
	String externalChargeCode;
	boolean missing;
	Double less45;
	Double greater45;
	Double greater100;
	Double greater300;
	Double greater500;
	Double greater1000;

	
	public ChargeResponse(WmResponse wmobject, List<String> chargeCodes) {
		BeanUtils.copyProperties(wmobject, this);
		this.type = wmobject.getChargeType();
		this.surchargeCode = wmobject.getSurchargeCode();
		this.description = wmobject.getChargeDescription();
		this.missing = !chargeCodes.contains(wmobject.getSurchargeCode());
	}
}
	