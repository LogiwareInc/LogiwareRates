package com.logiware.rates.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class RateRequest {
	@JsonInclude(Include.NON_NULL)
	@JsonAlias("originCode")
	@JsonProperty("request_origin_code")
	String originCode;
	@JsonInclude(Include.NON_NULL)
	@JsonAlias("destinationCode")
	@JsonProperty("request_destination_code")
	String destinationCode;
	@JsonInclude(Include.NON_NULL)
	@JsonAlias("shipDate")
	@JsonProperty("request_ship_date")
	String shipDate;
	@JsonInclude(Include.NON_NULL)
	@JsonAlias("expirationDate")
	@JsonProperty("request_expiration_date")
	String expirationDate;
	@JsonInclude(Include.NON_NULL)
	@JsonAlias("serviceType")
	@JsonProperty("request_service_type")
	String serviceType;
	@JsonInclude(Include.NON_NULL)
	@JsonAlias("shipmentType")
	@JsonProperty("request_shipment_type")
	String shipmentType;
	@JsonInclude(Include.NON_NULL)
	@JsonProperty("request_shipment_size")
	@JsonAlias("shipmentSize")
	String shipmentSize;
	@JsonInclude(Include.NON_NULL)
	@JsonAlias("dangerousGoods")
	@JsonProperty("request_dangerous_goods")
	Boolean dangerousGoods;
	@JsonInclude(Include.NON_NULL)
	@JsonAlias("weight")
	@JsonProperty("request_weight")
	Double weight;
	@JsonInclude(Include.NON_NULL)
	@JsonAlias("weightUnit")
	@JsonProperty("request_weight_unit")
	String weightUnit;
	@JsonInclude(Include.NON_NULL)
	@JsonAlias("cbm")
	@JsonProperty("request_cbm")
	Double cbm;
	@JsonInclude(Include.NON_NULL)
	Boolean multiSizeOcean;
	@JsonInclude(Include.NON_NULL)
	Boolean multi20;
	@JsonInclude(Include.NON_NULL)
	Boolean multi40;
	@JsonInclude(Include.NON_NULL)
	Boolean multi40hc;
	@JsonInclude(Include.NON_NULL)
	Boolean multi45hc;
	@JsonInclude(Include.NON_NULL)
	Boolean multi45;
	@JsonInclude(Include.NON_NULL)
	Boolean multi48;
	@JsonInclude(Include.NON_NULL)
	Boolean multi53;

}
