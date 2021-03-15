package com.logiware.rates.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
public class ShipQuoteGround {

	@JsonProperty("chargesCollection")
	@JsonInclude(Include.NON_NULL)
	List<ChargeResponse> chargesCollection; // Collection of all applicable ground surcharges
	@JsonProperty("connectionContainerType")
	@JsonInclude(Include.NON_NULL)
	String connectionContainerType; // Ground shipment connection container type
	@JsonProperty("connectionType")
	@JsonInclude(Include.NON_NULL)
	String connectionType; // Ground shipment container type
	@JsonProperty("contractId")
	@JsonInclude(Include.NON_NULL)
	String contractId; // Ground shipment contract ID
	@JsonProperty("currency")
	@JsonInclude(Include.NON_NULL)
	String currency; // Ground shipment currency
	@JsonProperty("cyFlag")
	@JsonInclude(Include.NON_NULL)
	boolean cyFlag; // Ground shipment CY flag?
	@JsonProperty("cyVia")
	@JsonInclude(Include.NON_NULL)
	String cyVia; // Groung shipment CY via?
	@JsonProperty("destinationCarrier")
	@JsonInclude(Include.NON_NULL)
	String destinationCarrier; // Ground shipment carrier at destination side
	@JsonProperty("destinationCarrierRestriction")
	@JsonInclude(Include.NON_NULL)
	String destinationCarrierRestriction; // Special carrier restriction at destination side
	@JsonProperty("destinationCarrierScac")
	@JsonInclude(Include.NON_NULL)
	String destinationCarrierScac; // Ground shipment carrier standard carrier alpha code
	@JsonProperty("destinationConnectionCity")
	@JsonInclude(Include.NON_NULL)
	String destinationConnectionCity; // Ground shipment carrier city at destination side
	@JsonProperty("destinationConnectionUnloc")
	@JsonInclude(Include.NON_NULL)
	String destinationConnectionUnloc; // Location code of the connection station at destination side
	@JsonProperty("destinationEquipmentType")
	@JsonInclude(Include.NON_NULL)
	String destinationEquipmentType; // The equipment type of shipment at destination side
	@JsonProperty("destinationGround")
	boolean destinationGround; // Indicates if the ground charge at destination side is applied
	@JsonProperty("destinationMode")
	@JsonInclude(Include.NON_NULL)
	String destinationMode; // The ground shipping mode at destination side Values:	Truck, Rail, etc
	@JsonProperty("effDateString")
	@JsonInclude(Include.NON_NULL)
	String effDateString; // Effective date of surcharge in string format: yyyy/MM/dd
	@JsonProperty("effectiveDate")
	@JsonInclude(Include.NON_NULL)
	Date effectiveDate; // Effective date of surcharge
	@JsonProperty("expDateString")
	@JsonInclude(Include.NON_NULL)
	String expDateString; // Expiration date of surcharge in string format: yyyy/MM/dd
	@JsonProperty("expirationDate")
	@JsonInclude(Include.NON_NULL)
	Date expirationDate; // Expiration date of surcharge
	@JsonProperty("groundNamedAccount")
	@JsonInclude(Include.NON_NULL)
	String groundNamedAccount; // Ground shipment named account
	@JsonProperty("groundOD")
	@JsonInclude(Include.NON_NULL)
	String groundOD; // Ground shipment origin and destination pair
	@JsonProperty("groundRateId")
	@JsonInclude(Include.NON_NULL)
	String groundRateId; // Internal ground rate ID#
	@JsonProperty("groundSize")
	@JsonInclude(Include.NON_NULL)
	String groundSize; // Container size of the ground surcharge if applicable Values: 20, 40, 40HC, 45, 45HC, 53, LCL
	@JsonProperty("haz")
	boolean haz; // Hazardous flag for ground quote
	@JsonProperty("notes")
	@JsonInclude(Include.NON_NULL)
	String notes; // Ground quote notes
	@JsonProperty("originCarrier")
	@JsonInclude(Include.NON_NULL)
	String originCarrier; // Ground shipment carrier at origin side
	@JsonProperty("originCarrierRestriction")
	@JsonInclude(Include.NON_NULL)
	String originCarrierRestriction; // Special carrier restriction at origin side
	@JsonProperty("originCarrierScac")
	@JsonInclude(Include.NON_NULL)
	String originCarrierScac; // Origin carrier standard carrier alpha code
	@JsonProperty("originConnectionCity")
	@JsonInclude(Include.NON_NULL)
	String originConnectionCity; // Ground shipment carrier city at origin side
	@JsonProperty("originConnectionUnloc")
	@JsonInclude(Include.NON_NULL)
	String originConnectionUnloc; // Location code of the connection station at origin side
	@JsonProperty("originEquipmentType")
	@JsonInclude(Include.NON_NULL)
	String originEquipmentType; // The equipment type of shipment at origin side
	@JsonProperty("originGround")
	boolean originGround; // Indicates if the ground charge at origin side are applied
	@JsonProperty("originMode")
	@JsonInclude(Include.NON_NULL)
	String originMode; // The ground shipping mode at origin side Values: Truck, Rail, etc
	@JsonProperty("price")
	@JsonInclude(Include.NON_NULL)
	Double price; // Ground shipment price
	@JsonProperty("service")
	@JsonInclude(Include.NON_NULL)
	String service; // Ground shipment service type
	@JsonProperty("transit")
	@JsonInclude(Include.NON_NULL)
	String transit; // Estimated transit time in days if applicable
	@JsonProperty("namedAccount")
	@JsonInclude(Include.NON_NULL)
	String namedAccount;
	@JsonProperty("spotRateId")
	@JsonInclude(Include.NON_NULL)
	String spotRateId;
	@JsonProperty("amendmentId")
	@JsonInclude(Include.NON_NULL)
	String amendmentId;
	@JsonProperty("appUser")
	@JsonInclude(Include.NON_NULL)
	UserResponse appUser;
	@JsonProperty("doorLocationType")
	@JsonInclude(Include.NON_NULL)
	String doorLocationType;
	@JsonProperty("doorLocCode")
	@JsonInclude(Include.NON_NULL)
	String doorLocCode;
	@JsonProperty("doorLocCity")
	@JsonInclude(Include.NON_NULL)
	String doorLocCity;
	@JsonProperty("doorLocState")
	@JsonInclude(Include.NON_NULL)
	String doorLocState;
	@JsonProperty("doorLocCountry")
	@JsonInclude(Include.NON_NULL)
	String doorLocCountry;
	@JsonProperty("cyLocationType")
	@JsonInclude(Include.NON_NULL)
	String cyLocationType;
	@JsonProperty("cyLocCode")
	@JsonInclude(Include.NON_NULL)
	String cyLocCode;
	@JsonProperty("cyLocCity")
	@JsonInclude(Include.NON_NULL)
	String cyLocCity;
	@JsonProperty("cyLocState")
	@JsonInclude(Include.NON_NULL)
	String cyLocState;
	@JsonProperty("cyLocCountry")
	@JsonInclude(Include.NON_NULL)
	String cyLocCountry;
	@JsonProperty("internalNotes")
	@JsonInclude(Include.NON_NULL)
	String internalNotes;
	@JsonInclude(Include.NON_NULL)
	String commodityBrief; // Brief description of commodity
	@JsonInclude(Include.NON_NULL)
	String commodityCode; // Internal commodity code
	@JsonInclude(Include.NON_NULL)
	String commodityExclusion; // Commodity exclusions
	@JsonInclude(Include.NON_NULL)
	String commodityFull; // Full description of commodity
	@JsonInclude(Include.NON_NULL)
	Double weight20;
	@JsonInclude(Include.NON_NULL)
	Double weight40;
	@JsonInclude(Include.NON_NULL)
	Double weight40HC;
	@JsonInclude(Include.NON_NULL)
	Double weight45;
	@JsonInclude(Include.NON_NULL)
	Double volume20;
	@JsonInclude(Include.NON_NULL)
	Double volume40;
	@JsonInclude(Include.NON_NULL)
	Double volume40HC;
	@JsonInclude(Include.NON_NULL)
	Double volume45;
	
}
