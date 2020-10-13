package com.logiware.rates.dto;

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
public class UserResponse {

	@JsonProperty("username")
	String username; // UserResponse name of user
	String userType; // UserResponse type of user
	@JsonProperty("organization")
	String organization; // Organization of user
	String organizationRegion; // Region of the organization of the UserResponse
	String masterOrg; // Master Organization of the UserResponse
	String userFirstName; // First name of the UserResponse
	String userLastName; // Last name of the UserResponse
	String userEmail; // Email address of the UserResponse
	String primaryPhone; // Primary phone number of UserResponse
	String secondaryPhone; // Secondary phone number of UserResponse
	String masterOrgDesc; // Master Organization description of the UserResponse
	String orgAddress; // Oranization address
	String orgAddress2; // Second organization address
	String orgCity; // City of UserResponse organization
	String orgState; // State of UserResponse organization
	String orgZip; // Zip code of UserResponse organization
	String orgCountry; // Country of UserResponse oraganization
	String orgPhone; // Phone number of UserResponse organization
	String orgFax; // Fax number of UserResponse organization
	String userTitle; // UserResponse title

}
