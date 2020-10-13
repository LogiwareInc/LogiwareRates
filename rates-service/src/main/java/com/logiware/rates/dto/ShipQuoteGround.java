package com.logiware.rates.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties({ "effective_date", "expiration_date" })
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ShipQuoteGround {

	@JsonProperty("chargesCollection")
	List<ChargeResponse> chargesCollection; // Collection of all applicable ground surcharges
	@JsonProperty("connectionContainerType")
	String connectionContainerType; // Ground shipment connection container type
	@JsonProperty("connectionType")
	String connectionType; // Ground shipment container type
	@JsonProperty("contractId")
	String contractId; // Ground shipment contract ID
	@JsonProperty("currency")
	String currency; // Ground shipment currency
	@JsonProperty("cyFlag")
	boolean cyFlag; // Ground shipment CY flag?
	@JsonProperty("cyVia")
	String cyVia; // Groung shipment CY via?
	@JsonProperty("destinationCarrier")
	String destinationCarrier; // Ground shipment carrier at destination side
	@JsonProperty("destinationCarrierRestriction")
	String destinationCarrierRestriction; // Special carrier restriction at destination side
	@JsonProperty("destinationCarrierScac")
	String destinationCarrierScac; // Ground shipment carrier standard carrier alpha code
	@JsonProperty("destinationConnectionCity")
	String destinationConnectionCity; // Ground shipment carrier city at destination side
	@JsonProperty("destinationConnectionUnloc")
	String destinationConnectionUnloc; // Location code of the connection station at destination side
	@JsonProperty("destinationEquipmentType")
	String destinationEquipmentType; // The equipment type of shipment at destination side
	@JsonProperty("destinationGround")
	boolean destinationGround; // Indicates if the ground charge at destination side is applied
	@JsonProperty("destinationMode")
	String destinationMode; // The ground shipping mode at destination side Values:	Truck, Rail, etc
	@JsonProperty("effDateString")
	String effDateString; // Effective date of surcharge in string format: yyyy/MM/dd
	@JsonProperty("effectiveDate")
	Date effectiveDate; // Effective date of surcharge
	@JsonProperty("expDateString")
	String expDateString; // Expiration date of surcharge in string format: yyyy/MM/dd
	@JsonProperty("expirationDate")
	Date expirationDate; // Expiration date of surcharge
	@JsonProperty("groundNamedAccount")
	String groundNamedAccount; // Ground shipment named account
	@JsonProperty("groundOD")
	String groundOD; // Ground shipment origin and destination pair
	@JsonProperty("groundRateId")
	String groundRateId; // Internal ground rate ID#
	@JsonProperty("groundSize")
	String groundSize; // Container size of the ground surcharge if applicable Values: 20, 40, 40HC, 45, 45HC, 53, LCL
	@JsonProperty("haz")
	boolean haz; // Hazardous flag for ground quote
	@JsonProperty("notes")
	String notes; // Ground quote notes
	@JsonProperty("originCarrier")
	String originCarrier; // Ground shipment carrier at origin side
	@JsonProperty("originCarrierRestriction")
	String originCarrierRestriction; // Special carrier restriction at origin side
	@JsonProperty("originCarrierScac")
	String originCarrierScac; // Origin carrier standard carrier alpha code
	@JsonProperty("originConnectionCity")
	String originConnectionCity; // Ground shipment carrier city at origin side
	@JsonProperty("originConnectionUnloc")
	String originConnectionUnloc; // Location code of the connection station at origin side
	@JsonProperty("originEquipmentType")
	String originEquipmentType; // The equipment type of shipment at origin side
	@JsonProperty("originGround")
	boolean originGround; // Indicates if the ground charge at origin side are applied
	@JsonProperty("originMode")
	String originMode; // The ground shipping mode at origin side Values: Truck, Rail, etc
	@JsonProperty("price")
	Double price; // Ground shipment price
	@JsonProperty("service")
	String service; // Ground shipment service type
	@JsonProperty("transit")
	String transit; // Estimated transit time in days if applicable
	@JsonProperty("namedAccount")
	String namedAccount;
	@JsonProperty("spotRateId")
	String spotRateId;
	@JsonProperty("amendmentId")
	String amendmentId;
	@JsonProperty("appUser")
	UserResponse appUser;
	@JsonProperty("doorLocationType")
	String doorLocationType;
	@JsonProperty("doorLocCode")
	String doorLocCode;
	@JsonProperty("doorLocCity")
	String doorLocCity;
	@JsonProperty("doorLocState")
	String doorLocState;
	@JsonProperty("doorLocCountry")
	String doorLocCountry;
	@JsonProperty("cyLocationType")
	String cyLocationType;
	@JsonProperty("cyLocCode")
	String cyLocCode;
	@JsonProperty("cyLocCity")
	String cyLocCity;
	@JsonProperty("cyLocState")
	String cyLocState;
	@JsonProperty("cyLocCountry")
	String cyLocCountry;
	@JsonProperty("internalNotes")
	String internalNotes;
	String commodityBrief; // Brief description of commodity
	String commodityCode; // Internal commodity code
	String commodityExclusion; // Commodity exclusions
	String commodityFull; // Full description of commodity
	
}
