package com.logiware.rates.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.logiware.rates.entity.Company;
import com.logiware.rates.repository.DynamicRepository;
import com.logiware.rates.util.StringUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@EqualsAndHashCode
@ToString(includeFieldNames = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RateResponse {

	UUID id;
	String carrier;
	String commodityBrief;
	String commodityCode;
	String commodityExclusion;
	String commodityFull;
	String commodityNamedAccount;
	String contractExpire;
	String contractId;
	String contractNotes;
	String destinationCity;
	String destinationCode;
	String destinationCountry;
	String destinationIsoCountry;
	String destinationStateCode;
	String destinationTrade;
	String destinationViaCity;
	String destinationViaCode;
	String destinationViaStateCode;
	String hazardousCharge;
	String originCity;
	String originCode;
	String originCountry;
	String originIsoCountry;
	String originStateCode;
	String originTrade;
	String originViaCity;
	String originViaCode;
	String originViaStateCode;
	String quoteCreationDt;
	String quoteExpirationDate;
	String quoteId;
	String rateEffectiveDate;
	String rateExpirationDate;
	String rateId;
	String scac;
	String transit;
	String incoTerm;
	Map<String, ContainerResponse> containers;
	Map<String, ChargeResponse> currentCharges;
	Map<String, ChargeResponse> futureCharges;
	List<String> containerSizes;
	List<String> currentChargeCodes;
	List<String> futureChargeCodes;
	List<String> missingChargeCodes;
	String carrierNumber;
	double totalLess45;
	double totalGreater45;
	double totalGreater100;
	double totalGreater300;
	double totalGreater500;
	double totalGreater1000;
	double totalPrice;
	String currency;

	public RateResponse(ShipQuoteResponse shipQuote) {
		BeanUtils.copyProperties(shipQuote, this);
	}

	public RateResponse(List<ShipQuoteResponse> shipQuotes, Company company, DynamicRepository dynamicRepository, String tpSql, Map<String, String> containerSizes,
			KeyValueResult freightCharge, List<String> chargeCodes) {
		this.id = UUID.randomUUID();
		BeanUtils.copyProperties(shipQuotes.get(0), this);
		String sql = tpSql.replace("<scac>", shipQuotes.get(0)
			.getScac());
		KeyValueResult tp = dynamicRepository.getKeyValueResult(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), sql);
		if (tp != null) {
			this.carrierNumber = (String) tp.getKey();
			this.carrier = (String) tp.getValue();
		}
		if (shipQuotes.size() > 1) {
			this.contractNotes = shipQuotes.stream()
				.map(ShipQuoteResponse::getContractNotes)
				.reduce((a, b) -> {
					if (StringUtils.isNotEmpty(a) && StringUtils.isNotEmpty(b) && !a.contains(b) && !b.contains(a)) {
						return a + "\r\n" + b;
					} else if (StringUtils.isNotEmpty(a) && a.contains(b)) {
						return a;
					} else if (StringUtils.isNotEmpty(b) && b.contains(a)) {
						return b;
					} else if (StringUtils.isNotEmpty(a)) {
						return a;
					} else {
						return b;
					}
				})
				.orElse("");
		}
		this.containers = shipQuotes.stream()
			.filter(shipQuote -> containerSizes.containsKey(shipQuote.getShipmentSize()))
			.map(shipQuote -> new ContainerResponse(shipQuote, freightCharge, containerSizes.get(shipQuote.getShipmentSize()), chargeCodes))
			.collect(Collectors.toMap(ContainerResponse::getSize, container -> container));
		this.currentChargeCodes = containers.values()
			.stream()
			.map(container -> container.getCurrentCharges()
				.keySet())
			.flatMap(Collection::stream)
			.distinct()
			.collect(Collectors.toList());
		this.futureChargeCodes = containers.values()
			.stream()
			.map(container -> container.getFutureCharges()
				.keySet())
			.flatMap(Collection::stream)
			.distinct()
			.collect(Collectors.toList());
		this.missingChargeCodes = containers.values()
			.stream()
			.map(container -> container.getCurrentCharges()
				.values())
			.flatMap(Collection::stream)
			.filter(ChargeResponse::isMissing)
			.map(ChargeResponse::getSurchargeCode)
			.collect(Collectors.toList());
		this.missingChargeCodes.addAll(containers.values()
			.stream()
			.map(container -> container.getFutureCharges()
				.values())
			.flatMap(Collection::stream)
			.filter(ChargeResponse::isMissing)
			.map(ChargeResponse::getSurchargeCode)
			.collect(Collectors.toList()));
	}

	public RateResponse(ShipQuoteResponse shipQuote, Company company, DynamicRepository dynamicRepository, String tpSql, KeyValueResult freightCharge, List<String> chargeCodes) {
		this.id = UUID.randomUUID();
		BeanUtils.copyProperties(shipQuote, this);
		String sql = tpSql.replace("<scac>", shipQuote.getScac());
		KeyValueResult tp = dynamicRepository.getKeyValueResult(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), sql);
		if (tp != null) {
			this.carrierNumber = (String) tp.getKey();
			this.carrier = (String) tp.getValue();
		}
		shipQuote.getWmobject()
			.forEach(wmobject -> {
				if ("BR".equalsIgnoreCase(wmobject.getSurchargeCode())) {
					this.currency = wmobject.getCurrency();
					wmobject.setSurchargeCode((String) freightCharge.getKey());
					wmobject.setChargeDescription((String) freightCharge.getValue());
				}
			});
		this.currentCharges = new LinkedHashMap<>();
		this.futureCharges = new LinkedHashMap<>();
		List<ChargeResponse> charges = shipQuote.getWmobject()
			.stream()
			.map(wmobject -> new ChargeResponse(wmobject, chargeCodes))
			.collect(Collectors.toList());
		this.currentCharges.putAll(charges.stream()
			.filter(charge -> "CURRENT".equalsIgnoreCase(charge.getType()))
			.collect(Collectors.toMap(ChargeResponse::getSurchargeCode, charge -> charge)));
		this.futureCharges.putAll(charges.stream()
			.filter(charge -> "FUTURE".equalsIgnoreCase(charge.getType()))
			.collect(Collectors.toMap(ChargeResponse::getSurchargeCode, charge -> charge)));
		this.currentChargeCodes = new ArrayList<>();
		this.futureChargeCodes = new ArrayList<>();
		this.missingChargeCodes = new ArrayList<>();
		charges.forEach(charge -> {
			this.totalLess45 += charge.getLess45() / charge.getCurrencyRate();
			this.totalGreater45 += charge.getGreater45() / charge.getCurrencyRate();
			this.totalGreater100 += charge.getGreater100() / charge.getCurrencyRate();
			this.totalGreater300 += charge.getGreater300() / charge.getCurrencyRate();
			this.totalGreater500 += charge.getGreater500() / charge.getCurrencyRate();
			this.totalGreater1000 += charge.getGreater1000() / charge.getCurrencyRate();
			if ("CURRENT".equalsIgnoreCase(charge.getType())) {
				this.currentChargeCodes.add(charge.getSurchargeCode());
			} else {
				this.futureChargeCodes.add(charge.getSurchargeCode());
			}
			if(charge.isMissing()) {
				this.missingChargeCodes.add(charge.getSurchargeCode());
			}
		});
	}

}
