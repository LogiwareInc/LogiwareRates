package com.logiware.rates.test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logiware.rates.dto.QuoteResponse;
import com.logiware.rates.dto.RateRequest;
import com.logiware.rates.dto.ShipQuoteGround;

public class CatapultTest {

	public static void main(String[] args) {

		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
			.credentials("Bruno.crelier@latinpros.com", "Catapult80")
			.build();
		ClientConfig config = new ClientConfig();
		config.register(feature);
		Client client = ClientBuilder.newClient(config);
		WebTarget target = client.target(UriBuilder.fromUri("http://api.gocatapult.com/qms-rest/qms/")
			.build());
		ShipQuoteGround requestGround = ShipQuoteGround.builder()
			.originGround(true)
			.volume20(1.00)
			.volume40(1.00)
			.volume40HC(1.00)
			.weight20(1000.00)
			.weight40(1000.00)
			.weight40HC(1000.00)
			.build();
		RateRequest request = RateRequest.builder()
			.originCode("USMIA")
			.destinationCode("BRSSZ")
			.useCityLinking(true)
			.shipDate("2021/03/16")
			.expirationDate("2021/04/16")
			.serviceType("ALL")
			.shipmentType("OXX")
			.dangerousGoods(false)
			.weight(1000.00)
			.cbm(1.00)
			.weightUnit("KG")
			.requestGround(requestGround)
//			.shipmentSize("AIR")
			.multiSizeOcean(true)
			.multi20(true)
			.multi40(true)
			.multi40hc(true)
//				.multi45hc(true)
//				.multi45(true)
//				.multi48(true)
//				.multi53(true)
			.build();
//		Response response = target.path("quotes")
//			.request(MediaType.APPLICATION_JSON_TYPE)
//			.post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE));
//		System.out.println(response.readEntity(String.class));

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		QuoteResponse response = target.path("quotes")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE), QuoteResponse.class);
		System.out.println("success");
	}

}
