package com.logiware.rates.service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.logiware.rates.dto.KeyValueResult;
import com.logiware.rates.dto.QuoteResponse;
import com.logiware.rates.dto.RateRequest;
import com.logiware.rates.dto.RateResponse;
import com.logiware.rates.dto.ShipQuoteResponse;
import com.logiware.rates.entity.Company;
import com.logiware.rates.repository.DynamicRepository;
import com.logiware.rates.util.StringUtils;

@Service
public class CatapultService {

	@Value("${catapult.service.url}")
	private String url;
	@Value("${query.select.charge-code-all}")
	private String chargeCodeAllSql;
	@Value("${query.select.charge-code-by-code}")
	private String chargeCodeByCodeSql;
	@Value("${query.select.container-size-all}")
	private String containerSizeSql;
	@Value("${query.select.trading-partner-by-scac}")
	private String tpSql;

	@Autowired
	private DynamicRepository dynamicRepository;

	public List<RateResponse> getRates(Company company, RateRequest request) throws JsonProcessingException {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
			.credentials(company.getCatapultUser(), company.getCatapultPassword())
			.build();
		ClientConfig config = new ClientConfig();
		config.register(feature);
		Client client = ClientBuilder.newClient(config);
		WebTarget target = client.target(UriBuilder.fromUri(url)
			.build());
		request.setServiceType("ALL");
		request.setShipmentType("OXX");
		request.setUseCityLinking(true);
		String shipmentSize = request.getShipmentSize();
		List<String> chargeCodes = dynamicRepository.getSingleValueList(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), chargeCodeAllSql);
		Map<String, String> containerSizes = dynamicRepository.getKeyValueResults(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), containerSizeSql)
			.stream()
			.collect(Collectors.toMap(k -> ((String) k.getKey()).replace(" ", ""), k -> (String) k.getValue()));
		if (StringUtils.isEqual(shipmentSize, "FCL")) {
			request.setShipmentSize(null);
			request.setMultiSizeOcean(true);
			request.setMulti20(containerSizes.containsKey("20"));
			request.setMulti40(containerSizes.containsKey("40"));
			request.setMulti40hc(containerSizes.containsKey("40HC"));
			request.setMulti45hc(containerSizes.containsKey("45HC"));
			request.setMulti45(containerSizes.containsKey("45"));
			request.setMulti48(containerSizes.containsKey("48"));
			request.setMulti53(containerSizes.containsKey("53"));
		}
		QuoteResponse response = target.path("quotes")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE), QuoteResponse.class);
		boolean isAir = StringUtils.isEqual(shipmentSize, "AIR");
		String sql = chargeCodeByCodeSql.replace("<charge-code>", isAir ? "AFR" : "OFR");
		KeyValueResult freightCharge = dynamicRepository.getKeyValueResult(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), sql);
		if (isAir) {
			return response.getDataMap()
				.get("quotes")
				.stream()
				.map(shipQuote -> new RateResponse(shipQuote, company, dynamicRepository, tpSql, freightCharge, chargeCodes))
				.filter(rate -> rate.getId() != null)
				.collect(Collectors.toList());
		} else {
			return response.getDataMap()
				.get("quotes")
				.stream()
				.collect(Collectors.groupingBy(ShipQuoteResponse::toString, TreeMap::new, Collectors.toList()))
				.entrySet()
				.stream()
				.map(entry -> new RateResponse(entry.getValue(), company, dynamicRepository, tpSql, containerSizes, freightCharge, chargeCodes))
				.filter(rate -> rate.getId() != null)
				.collect(Collectors.toList());
		}
	}

}
