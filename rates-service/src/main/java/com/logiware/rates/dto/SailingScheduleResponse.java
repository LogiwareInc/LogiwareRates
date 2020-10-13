package com.logiware.rates.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SailingScheduleResponse {

	@JsonProperty("aircraftType")
	String aircraftType; // Sailing schedule Aircraft type if it is Air
	@JsonProperty("arrival")
	String arrival; // City name of arrival port
	String arrivalDate; // Estimated arrival date
	String arrivalMode; // Arrival mode
	@JsonProperty("arrivalTime")
	String arrivalTime; // Air flight arrival time
	Integer averageTransit; // Average point to point transit value
	String companyName; // Company name of the carrier
	String companyType; // Company type
	@JsonProperty("daysOfOperation")
	String daysOfOperation; // Air days of operation
	@JsonProperty("departure")
	String departure; // City name of departure port
	String departureDate; // Estimated arrival date
	String departureMode; // Departure mode
	@JsonProperty("departureTime")
	String departureTime; // Air flight departure time
	String destinationCode; // Location code of destination
	@JsonProperty("destViaCode")
	String destViaCode; // Destination via code for Air
	@JsonProperty("distance")
	String distance; // Air flight distance
	String domesticInlandTransit; // Inland transit value
	@JsonProperty("elapsedTime")
	String elapsedTime; // Air elapsed time
	@JsonProperty("elapsedTimeDisplay")
	String elapsedTimeDisplay; // Air elapsed time delay
	@JsonProperty("flightNum")
	String flightNum; // Air flight number
	@JsonProperty("frequency")
	String frequency; // Air flight frequency
	@JsonProperty("imo")
	String imo; // IMO number
	Integer inlandTransit; // Foreign inland transit value
	@JsonProperty("layoverTime")
	String layoverTime; // Air layover time
	@JsonProperty("layoverTimeDisplay")
	String layoverTimeDisplay; // Air layover time display
	String originCode; // Location code of origin
	@JsonProperty("origViaCode")
	String origViaCode; // Origin via code for Air
	@JsonProperty("scac")
	String scac; // Carrier SCAC code
	@JsonProperty("selected")
	Boolean selected; // Sailing schedule flag if schdule is selected
	@JsonProperty("service")
	String service; // Sailing schedule service type
	@JsonProperty("transit")
	String transit; // Total transit
	String vesselCapacity; // Capacity of vessel
	String vesselFlag; // Flag of vessel
	String vesselName; // Name of vessel
	String vesselYearBuilt; // The year of build of vessel
	String voyageId; // ID code of voyage
	String voyageNumber; // Number of voyage
	String voyageNumber2;
}
