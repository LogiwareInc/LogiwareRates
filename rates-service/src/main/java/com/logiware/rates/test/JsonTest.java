package com.logiware.rates.test;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logiware.rates.dto.RateRequest;

public class JsonTest {
	public static void main(String[] args) throws IOException {
		RateRequest request = RateRequest.builder()
			.originCode("CNSHA")
			.destinationCode("USATL")
			.shipDate("2020/10/06")
			.expirationDate("2020/11/06")
			.serviceType("ALL")
			.shipmentType("OXX")
			.dangerousGoods(false)
			.weight(1000.00)
			.cbm(1.00)
			.weightUnit("KG")
			.multiSizeOcean(true)
			.multi20(true)
			.multi40(true)
			.multi40hc(true)
			.multi45hc(true)
			.multi45(true)
			.multi48(true)
			.multi53(true)
			.build();
		ObjectMapper objectMapper = new ObjectMapper();
		System.out.println(objectMapper.writeValueAsString(request));
//		String content = new String(Files.readAllBytes(Paths.get("C:\\Users\\Lakshmi Narayanan\\Documents\\catapult.txt")));
//		ObjectMapper objectMapper = new ObjectMapper();
//		QuoteResponse response = objectMapper.readValue(content, QuoteResponse.class);
//		System.out.println(response.getStatus());
	}
}
