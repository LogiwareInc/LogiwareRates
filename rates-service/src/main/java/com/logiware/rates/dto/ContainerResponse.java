package com.logiware.rates.dto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
public class ContainerResponse {

	String size;
	String currency;
	Double totalPrice;
	Map<String, ChargeResponse> currentCharges;
	Map<String, ChargeResponse> futureCharges;

	public ContainerResponse(ShipQuoteResponse shipQuote, KeyValueResult freightCharge, String containerSize, List<String> chargeCodes) {
		this.size = containerSize;
		this.currency = shipQuote.getCurrency();
		this.currentCharges = new LinkedHashMap<>();
		this.futureCharges = new LinkedHashMap<>();
		shipQuote.getChargesCollection()
			.forEach(charge -> {
				if ("BR".equalsIgnoreCase(charge.getSurchargeCode())) {
					charge.setSurchargeCode((String) freightCharge.getKey());
					charge.setDescription((String) freightCharge.getValue());
				}
			});
		shipQuote.getChargesCollection().forEach(charge -> charge.setMissing(!chargeCodes.contains(charge.getSurchargeCode())));
		this.currentCharges.putAll(shipQuote.getChargesCollection()
			.stream()
			.filter(charge -> "CURRENT".equalsIgnoreCase(charge.getType()))
			.collect(Collectors.toMap(ChargeResponse::getSurchargeCode, charge -> charge)));
		this.futureCharges.putAll(shipQuote.getChargesCollection()
			.stream()
			.filter(charge -> "FUTURE".equalsIgnoreCase(charge.getType()))
			.collect(Collectors.toMap(ChargeResponse::getSurchargeCode, charge -> charge)));
		this.totalPrice = this.currentCharges.values()
			.stream()
			.filter(charge -> "CURRENT".equalsIgnoreCase(charge.getType()))
			.mapToDouble(charge -> charge.getPrice() / (charge.getCurrencyRate() != null ? charge.getCurrencyRate() : 1))
			.sum();
	}

}
