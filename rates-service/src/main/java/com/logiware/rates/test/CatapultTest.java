package com.logiware.rates.test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import com.logiware.rates.dto.RateRequest;

public class CatapultTest {

	public static void main(String[] args) {

		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
			.credentials("SOAP_TEST_USER", "Change1234")
			.build();
		ClientConfig config = new ClientConfig();
		config.register(feature);
		Client client = ClientBuilder.newClient(config);
		WebTarget target = client.target(UriBuilder.fromUri("http://api.gocatapult.com/qms-rest/qms/")
			.build());
		RateRequest request = RateRequest.builder()
			.originCode("USLGB")
			.destinationCode("HKHKG")
			.useCityLinking(true)
			.shipDate("2020/10/17")
			.expirationDate("2020/11/17")
			.serviceType("ALL")
			.shipmentType("OXX")
			.dangerousGoods(false)
			.weight(1000.00)
			.cbm(1.00)
			.weightUnit("KG")
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
		Response response = target.path("quotes")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE));
		System.out.println(response.readEntity(String.class));
	}

}
