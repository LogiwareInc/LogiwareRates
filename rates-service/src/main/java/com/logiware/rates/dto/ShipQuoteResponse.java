package com.logiware.rates.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

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
@JsonIgnoreProperties({ "user_request" })
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(includeFieldNames = true, onlyExplicitlyIncluded = true)
public class ShipQuoteResponse {

	@JsonProperty("actualWeightK")
	Double actualWeightK; // Quote actual weight in Kilograms
	@JsonProperty("actualWeightP")
	Double actualWeightP; // Quote actual weight in Pounds
	Double airBasePrice; // Air base price
	Double airBaseRate; // Air base rate
	Double airBrMin; // Air base rate minimum?
	String airDimRatio; // Air dimension ratio
	String airFrequency; // Air schedule frequency? Example: M,T,W,Th,F,S
	String airFuel; // Air fuel charge
	String airFuelLabel; // Air fuel label
	Double airG45Price; // Air g45 price
	Double airG100Price; // Air g100 price
	Double airG250Price; // Air g250 price
	Double airG300Price; // Air g300 price
	Double airG500Price; // Air g500 price
	Double airG1000Price; // Air g1000 price
	Double airL45Price; // Air g145 price
	String airSecurity; // Air security
	String airSecurityLabel; // Air security label
	String airService; // Air service
	@JsonProperty("allinIMS")
	String allinIMS; // All in ISM transit information for Vanguard extraction
	String amendmentId; // Amendment ID number of the shipment quote
	Double buyPrice; // Quote buy price
	Double buySellMargin; // Quote buy sell margin
	Double buySellMarginPct; // Quote buy sell margin in percent
	@JsonProperty("carrier")
	@EqualsAndHashCode.Include
	@ToString.Include
	String carrier; // Carrier name of the shipment quote
	@JsonProperty("chargeableWeightK")
	Double chargeableWeightK; // Chargeable weight (kg)
	@JsonProperty("chargeableWeightP")
	Double chargeableWeightP; // Chargeable weight (pounds)
	@JsonProperty("chargesCollection")
	List<ChargeResponse> chargesCollection; // Collection of all applicable surcharges for the quote
	@EqualsAndHashCode.Include
	@ToString.Include
	String commodityBrief; // Brief description of commodity
	@EqualsAndHashCode.Include
	@ToString.Include
	String commodityCode; // Internal commodity code
	@EqualsAndHashCode.Include
	@ToString.Include
	String commodityExclusion; // Commodity exclusions
	@EqualsAndHashCode.Include
	@ToString.Include
	String commodityFull; // Full description of commodity
	String commodityNamedAccount; // Commodity named accounts
	@EqualsAndHashCode.Include
	@ToString.Include
	String contractExpire; // Expiration date of the contract format: yyyy/MM/dd
	@EqualsAndHashCode.Include
	@ToString.Include
	String contractId; // Contract ID code
	String contractNotes; // The notes of the contract
	@JsonProperty("crmConsignee")
	String crmConsignee; // CRM consignee
	@JsonProperty("currency")
	String currency; // Currency code of quote total price
	@JsonProperty("customer")
	String customer; // The named account of given quote if applicable
	@JsonProperty("darbRateId")
	String darbRateId; // Destination arbitrary RateResponse ID
	@JsonProperty("darbTransit")
	String darbTransit; // Destination arbitrary transit
	@JsonProperty("destinationGroundRate")
	ShipQuoteGround destinationGroundRate; // An object consisted of ground rate information at destination side if available
	String destinationCity; // City name of the ultimate destination for your shipment
	@EqualsAndHashCode.Include
	@ToString.Include
	String destinationCode; // Location code of destination city
	String destinationCountry; // Country of the ultimate destination for your shipment
	String destinationIsoCountry; // Quote destination country ISO code
	String destinationStateCode; // State code of the ultimate destination for your shipment
	String destinationTrade; // Trade lane at destination side
	String destinationViaCity; // City name of the inland stops for your shipment (destination side)
	String destinationViaCode; // Location code of destination via city
	String destinationViaStateCode; // State code of the inland stops for your shipment (destination side)
	@JsonProperty("dimensionalWeightK")
	Double dimensionalWeightK; // Dimensional weight of shipQuote in Kilos
	@JsonProperty("dimensionalWeightP")
	Double dimensionalWeightP; // Dimensional weight of shipQuote in Pounds
	@JsonProperty("freeTimeSurcharges")
	List<ChargeResponse> freeTimeSurcharges; // List of free time surcharges
	String hazardousCharge; // The note of hazardous charge if applicable
	@JsonProperty("inProgress")
	boolean inProgress; // Flag if quote is in progress
	@JsonProperty("masqueradeQuoteId")
	String masqueradeQuoteId; // Quote masquerade ID
	@JsonProperty("oarbRateId")
	String oarbRateId; // Origin arbitrary RateResponse ID
	@JsonProperty("oarbTransit")
	String oarbTransit; // The origin arbitrary transit
	@JsonProperty("originGroundRate")
	ShipQuoteGround originGroundRate; // An object consisted of ground rate	information at origin side if available
	String originCity; // City of origin port
	@EqualsAndHashCode.Include
	@ToString.Include
	String originCode; // Location code of origin city
	String originCountry; // Country of origin port
	String originIsoCountry; // Quote origin country ISO code
	String originStateCode; // State code of origin port
	String originTrade; // Trade lane at origin side
	String originViaCity; // City name of inland stops for your shipment (origin side)
	String originViaCode; // Location code of origin via city
	String originViaStateCode; // State code of inland stops for your shipment (origin side)
	@JsonProperty("originalRateService")
	String originalRateService; // Old rate service from surcharges tab before construction
	@JsonProperty("originalRateTransit")
	String originalRateTransit; // The transit of origin rate without construction
	@JsonProperty("paymentInfo")
	String paymentInfo; // Flag for Scoular"s Oracle OTM booking Values: P: prepaid, C: collect, A: all-in
	@JsonProperty("price")
	Double price; // Total price for the quote
	String quoteCreationDt; // Creation date of the quote format: yyyy/MM/dd
	String quoteExpirationDate; // Expiration date of the quote format: yyyy/MM/dd
	String quoteId; // ID code for the quote
	@EqualsAndHashCode.Include
	@ToString.Include
	String rateEffectiveDate; // Effective date of the rate format: yyyy/MM/dd
	@EqualsAndHashCode.Include
	@ToString.Include
	String rateExpirationDate; // Expiration date of the rate format: yyyy/MM/dd
	@EqualsAndHashCode.Include
	@ToString.Include
	String rateId; // Internal rate ID#
	Integer requestId; // Request ID of Quote
	@JsonProperty("sailingSchedule")
	String sailingSchedule; // Detail information of sailing schedule if applicable in string
	@JsonProperty("sailingScheduleObject")
	List<SailingScheduleResponse> sailingScheduleObject; // Detail information of sailing schedule if applicable
	@JsonProperty("scac")
	@EqualsAndHashCode.Include
	@ToString.Include
	String scac; // SCAC code of carrier
	@JsonProperty("sellOveride")
	boolean sellOveride; // Sell overide flag for Quote
	Double sellPrice; // Quote sell price
	@JsonProperty("service")
	String service; // Service type of the rate Values:	CY/CY, CY/D, D/CY, R/D, CY/RM, D/D,	R/CY, R/O, CY/M, CY/R, CFS/CFS, ATA, DTA, ATD, DTD
	@EqualsAndHashCode.Include
	@ToString.Include
	String shipDt; // The quote ship date format: yyyy/MM/dd
	@JsonProperty("shipmentMethod")
	String shipmentMethod; // The shipment method of rate Values: FCL, LCL, AIR, LTL
	@JsonProperty("shipmentSize")
	String shipmentSize; // The shipment size of rate Values: 20, 40, 40HC, 45, 45HC, 53, LCL, AIR
	@JsonProperty("shipmentSizeType")
	String shipmentSizeType; // The shipment container type of rate Values: DC, RE, TK, OT, FR, NOR, GC, PL, TR
	@JsonProperty("shipmentType")
	String shipmentType; // A summary description of shipment method, size, and type. format: [method] – [size] / [type]
	String spotRateId; // ID code for the spot rate quote
	@JsonProperty("totalDestinationCombinedFreeTime")
	String totalDestinationCombinedFreeTime; // Quote total calculation of destination combined free time
	@JsonProperty("totalDestinationDemurrageFreeTime")
	String totalDestinationDemurrageFreeTime; // Quote total calculation of destination	demurrage free time
	@JsonProperty("totalDestinationDetentionFreeTime")
	String totalDestinationDetentionFreeTime; // Quote total calculation of destination	detention free time
	@JsonProperty("totalDestinationFreeTime")
	String totalDestinationFreeTime; // Quote total calculation of destination free time
	@JsonProperty("totalFreeTime")
	String totalFreeTime; // Quote total calculation of free time
	@JsonProperty("totalOriginCombinedFreeTime")
	String totalOriginCombinedFreeTime; // Quote total calculation of origin combined free time
	@JsonProperty("totalOriginDemurrageFreeTime")
	String totalOriginDemurrageFreeTime; // Quote total calculation of origin demurrage free time
	@JsonProperty("totalOriginDetentionFreeTime")
	String totalOriginDetentionFreeTime; // Quote total calculation of origin detention free time
	@JsonProperty("totalOriginFreeTime")
	String totalOriginFreeTime; // Quote total calculation of origin free time
	@JsonProperty("transit")
	@EqualsAndHashCode.Include
	@ToString.Include
	String transit; // Estimated transit time in days if applicable
	String transitActual; // Actual transit field for UTI
	String transitDestination; // Destination transit field for UTI
	String transitOrigin; // Origin transit field for UTI
	@JsonProperty("wmbasis")
	String wmbasis; // Shipment weight/measure breakdown
	@JsonProperty("sellChargesCollection")
	List<ChargeResponse> sellChargesCollection; // Collection of all applicable sell charges for the quote
	String customCommodityDesc;
	List<WmResponse> wmobject;
	String givenQuotesConcatType;
	@JsonProperty("inco_term")
	@EqualsAndHashCode.Include
	@ToString.Include
	String incoterm;
	String mode;
	String customerId;
	String customerName;
	@JsonProperty("originCityLinkings")
	String originCityLinkings;
	@JsonProperty("destinationCityLinkings")
	String destinationCityLinkings;

}
