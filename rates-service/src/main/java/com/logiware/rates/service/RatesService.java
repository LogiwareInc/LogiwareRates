package com.logiware.rates.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.logiware.rates.dto.KeyValue;
import com.logiware.rates.dto.ResultModel;
import com.logiware.rates.entity.Company;
import com.logiware.rates.entity.File;
import com.logiware.rates.entity.Site;
import com.logiware.rates.entity.SiteFile;
import com.logiware.rates.repository.CustomRepository;
import com.logiware.rates.repository.DynamicRepository;
import com.logiware.rates.repository.FileRepository;
import com.logiware.rates.repository.SiteFileRepository;
import com.logiware.rates.repository.SiteRepository;
import com.logiware.rates.util.DateUtils;

@Service
public class RatesService {

	@Value("${upload.path}")
	private String uploadPath;

	@Autowired
	private DynamicRepository dynamicRepository;

	@Autowired
	private SiteRepository siteRepository;

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private SiteFileRepository siteFileRepository;

	@Autowired
	private CustomRepository customRepository;

	@Value("${spring.datasource.url}")
	private String dbUrl;
	@Value("${spring.datasource.username}")
	private String dbUser;
	@Value("${spring.datasource.password}")
	private String dbPassword;

	public List<KeyValue> getOptionResults(Company company, String type) throws SQLException {
		StringBuilder builder = new StringBuilder();
		Map<String, Object> params = new HashMap<>();
		switch (type) {
		case "currencies":
			builder.append("select");
			builder.append("  gc.`code` as col1,");
			builder.append("  gc.`description` as col2 ");
			builder.append("from");
			builder.append("  `code_type` ct");
			builder.append("  join `generic_code` gc");
			builder.append("    on (gc.`code_type_id` = ct.`id`) ");
			builder.append("where ct.`description` = 'Currency' ");
			builder.append("order by coalesce(gc.`prior`, '0')");
			break;
		}
		return dynamicRepository.getOptionResults(company.getDbUrl(), company.getDbUser(), company.getDbPassword(),
				builder.toString(), params);
	}

	public List<KeyValue> getTypeaheadResults(Company company, String type, String input) throws SQLException {
		StringBuilder builder = new StringBuilder();
		switch (type) {
		case "carrier":
			builder.append("select");
			builder.append("  tp.`catapult_name` as col1,");
			builder.append("  mt.`value` as col2 ");
			builder.append("from");
			builder.append("  `trading_partner` tp");
			builder.append("  join `multi_table` mt");
			builder.append("    on ( ");
			builder.append("      mt.`trading_partner_id` = tp.`id`");
			builder.append("      and mt.`property` = '2'");
			builder.append("      and mt.`value` <> ''");
			builder.append("    ) ");
			builder.append("where tp.`account_type` like '%SS%'");
			builder.append("  and tp.`catapult_name` like :input");
			builder.append("  and tp.`active` = 1 ");
			builder.append("group by tp.`account_number`, mt.`value` ");
			builder.append("limit 10");
			break;
		case "scac":
			builder.append("select");
			builder.append("  mt.`value` as col1,");
			builder.append("  tp.`catapult_name` as col2 ");
			builder.append("from");
			builder.append("  `trading_partner` tp");
			builder.append("  join `multi_table` mt");
			builder.append("    on ( ");
			builder.append("      mt.`trading_partner_id` = tp.`id`");
			builder.append("      and mt.`property` = '2'");
			builder.append("      and mt.`value` <> ''");
			builder.append("    ) ");
			builder.append("where tp.`account_type` like '%SS%'");
			builder.append("  and mt.`value` like :input");
			builder.append("  and tp.`catapult_name` <> ''");
			builder.append("  and tp.`active` = 1 ");
			builder.append("group by tp.`account_number`, mt.`value` ");
			builder.append("limit 10");
			break;
		}
		return dynamicRepository.getTypeaheadResults(company.getDbUrl(), company.getDbUser(), company.getDbPassword(),
				builder.toString(), input);
	}

	public void removeEmptyRates(Site site) throws SQLException {
		String query = "delete from `csvrates` where `carrier` = ''";
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		int count = dynamicRepository.executeUpdate(site.getDbUrl(), site.getDbUser(), site.getDbPassword(), query,
				parameterSource);
		System.out.println(count);
	}

	public void truncateRates(String carrier, Site site) throws SQLException {
		String query = "delete from `csvrates` where `carrier` = :carrier";
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("carrier", carrier, Types.VARCHAR);
		dynamicRepository.executeUpdate(site.getDbUrl(), site.getDbUser(), site.getDbPassword(), query,
				parameterSource);
	}

	public void loadRates(Company company, Site site, File file, InputStream is) throws SQLException {
		truncateRates(file.getCarrier(), site);
		String query = company.getLoadingQuery().replace("${fileName}", file.getPath());
		dynamicRepository.executeUpdate(site.getDbUrl(), site.getDbUser(), site.getDbPassword(), query, is);
	}

	public void updateRates(File file, Site site) throws SQLException {
		StringBuilder builder = new StringBuilder();
		builder.append("update");
		builder.append("  `csvrates` ");
		builder.append("set ");
		builder.append("   `rate_effective_date` = :effectiveDate,");
		builder.append("   `rate_expiration_date` = :expirationDate,");
		builder.append("   `surcharge_effective_dt` = :effectiveDate,");
		builder.append("   `surcharge_expire_dt` = :expirationDate,");
		builder.append("   `contract_expire`= :expirationDate,");
		builder.append("   `ship_date` = current_date(),");
		builder.append("   `surcharge_type` = :surchargeType,");
		builder.append("   `surcharge_currency` = :surchargeCurrency,");
		builder.append("   `rate_basis` = :rateBasis ");
		builder.append("where `carrier` = :carrier");
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("effectiveDate", DateUtils.formatToString(file.getEffectiveDate(), "yyyy-MM-dd"),
				Types.VARCHAR);
		parameterSource.addValue("expirationDate", DateUtils.formatToString(file.getExpirationDate(), "yyyy-MM-dd"),
				Types.VARCHAR);
		parameterSource.addValue("surchargeType", file.getSurchargeType(), Types.VARCHAR);
		parameterSource.addValue("surchargeCurrency", file.getSurchargeCurrency(), Types.VARCHAR);
		parameterSource.addValue("rateBasis", file.getRateBasis(), Types.VARCHAR);
		parameterSource.addValue("carrier", file.getCarrier(), Types.VARCHAR);
		dynamicRepository.executeUpdate(site.getDbUrl(), site.getDbUser(), site.getDbPassword(), builder.toString(),
				parameterSource);
	}

	public void insertOceanFreight(String carrier, Site site) {
		StringBuilder builder = new StringBuilder();
		builder.append(
				"insert into `csvrates` (`quote_id`, `rate_id`, `contract_id`, `amendment_id`, `rate_effective_date`, `rate_expiration_date`, `origin_trade`, `origin_city`, `origin_country`, `origin_code`, `origin_via_city`, `destination_trade`, `destination_city`, `destination_country`, `destination_code`, `destination_via_city`, `carrier`, `price`, `service`, `contract_expire`, `currency`, `currency_rate`, `shipment_size`, `shipment_type`, `commodity_brief`, `commodity_code`, `commodity_exclusions`, `commodity_full`, `hazardous_charge`, `transit`, `contract_notes`, `customer`, `scac`, `surcharge_line_rate_id`, `surcharge_code`, `surcharge_desc`, `surcharge_price`, `surcharge_type`, `surcharge_currency`, `surcharge_expire_dt`, `surcharge_effective_dt`, `rate_basis`, `ship_date`, `gri_remarks`, `general_remarks`) ");
		builder.append(
				"select `quote_id`, `rate_id`, `contract_id`, `amendment_id`, `rate_effective_date`, `rate_expiration_date`, `origin_trade`, `origin_city`, `origin_country`, `origin_code`, `origin_via_city`, `destination_trade`, `destination_city`, `destination_country`, `destination_code`, `destination_via_city`, `carrier`, `price`, `service`, `contract_expire`, `currency`, `currency_rate`, `shipment_size`, `shipment_type`, `commodity_brief`, `commodity_code`, `commodity_exclusions`, `commodity_full`, `hazardous_charge`, `transit`, `contract_notes`, `customer`, `scac`, `surcharge_line_rate_id`, 'OFR', 'OCEAN FREIGHT', `price`, `surcharge_type`, `surcharge_currency`, `surcharge_expire_dt`, `surcharge_effective_dt`, `rate_basis`, `ship_date`, `gri_remarks`, `general_remarks` from `csvrates` where `carrier` = :carrier and `surcharge_code` <> '' group by `origin_code`, `origin_via_city`, `destination_code`, `destination_via_city`, `commodity_brief`, `shipment_size`, `contract_notes`");
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("carrier", carrier, Types.VARCHAR);
		dynamicRepository.executeUpdate(site.getDbUrl(), site.getDbUser(), site.getDbPassword(), builder.toString(),
				parameterSource);
	}

	public List<KeyValue> findErrors(String carrier, Site site) throws SQLException {
		StringBuilder builder = new StringBuilder();
		builder.append("select");
		builder.append("  r.`surcharge_code` as col1,");
		builder.append("  r.`surcharge_desc` as col2 ");
		builder.append("from");
		builder.append("  `csvrates` r");
		builder.append("  left join `generic_code` g");
		builder.append("    on (g.`code_type_id` = 36 and g.`code` = r.`surcharge_code`) ");
		builder.append("where r.`carrier` = :carrier");
		builder.append("  and g.`id` is null ");
		builder.append("group by r.`surcharge_code`");
		builder.append("");
		Map<String, Object> params = new HashMap<>();
		params.put("carrier", carrier);
		return dynamicRepository.getOptionResults(site.getDbUrl(), site.getDbUser(), site.getDbPassword(),
				builder.toString(), params);
	}

	public Map<String, List<KeyValue>> loadRates(Company company, String carrier, String scac, String effectiveDate,
			String expirationDate, String surchargeType, String surchargeCurrency, String siteIds,
			MultipartFile sourceFile) throws IllegalStateException, IOException, ParseException, SQLException {
		List<Long> ids = Stream.of(siteIds.split(",")).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
		List<Site> sites = siteRepository.findAllById(ids);
		java.io.File directory = new java.io.File(uploadPath + "/" + company.getName().replaceAll("[^a-zA-Z0-9]", "_"));
		if (!directory.exists()) {
			directory.mkdirs();
		}
		String filename = FilenameUtils.getBaseName(sourceFile.getOriginalFilename()).replaceAll("[^a-zA-Z0-9]", "_");
		String extension = FilenameUtils.getExtension(sourceFile.getOriginalFilename());
		java.io.File targetFile = new java.io.File(directory,
				filename + "_" + System.currentTimeMillis() + "." + extension);
		sourceFile.transferTo(targetFile);

		insertLocalTable(targetFile);
		String generateCsvpath = "E:/share/documents/TRANSBORDER/FCL/RATES/csvrates_" + System.currentTimeMillis()
				+ ".csv";
		getLocalTableRates(generateCsvpath);
		String deleteCsvRates = this.getDeleteCsvRates();
		System.out.println(deleteCsvRates);

		java.io.File loadFile = new java.io.File(generateCsvpath);

		File file = new File();
		file.setName(sourceFile.getOriginalFilename());
		// System.out.println(effectiveDate + " == " +
		// DateUtils.parseToDate(effectiveDate, "MM/dd/yyyy"));
		file.setCarrier("Tes");
		file.setScac("dsa");
		file.setEffectiveDate(DateUtils.parseToDate("05/07/2019", "MM/dd/yyyy"));
		file.setExpirationDate(DateUtils.parseToDate("05/07/2019", "MM/dd/yyyy"));
		file.setSurchargeType("R");
		file.setSurchargeCurrency("USD");
		file.setRateBasis("PC");
		file.setPath(loadFile.getAbsolutePath().replaceAll("\\\\", "/"));
		file.setCompany(company);
		file.setLoadedDate(new Date());
		fileRepository.save(file);
		InputStream is = FileUtils.openInputStream(targetFile);
		Map<String, List<KeyValue>> errors = new HashMap<>();
		for (Site site : sites) {
			this.deleteCsvRates(deleteCsvRates, site);
			this.loadRates(site, loadFile);
			SiteFile siteFile = new SiteFile();
			siteFile.setFile(file);
			siteFile.setSite(site);
			siteFileRepository.save(siteFile);
//			List<KeyValue> error = findErrors(file.getCarrier(), site);
//			if (error != null && !error.isEmpty()) {
//				errors.put(site.getName(), error);
//			}
		}
		return errors;
	}

	public List<ResultModel> searchRates(Company company, String carrier, String fromDate, String toDate)
			throws ParseException {
		return customRepository.findFilesByCarrierOrLoadedDate(company, carrier, fromDate, toDate);
	}

	public void loadRates(Site site, java.io.File file) throws SQLException {
		String query = "LOAD DATA LOCAL INFILE '" + file.getAbsolutePath().replaceAll("\\\\", "/")
				+ "' INTO TABLE csvrates FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '\r\n'  "
				+ "(QUOTE_ID,CONTRACT_ID,RATE_EFFECTIVE_DATE,RATE_EXPIRATION_DATE,"
				+ "ORIGIN_CITY,ORIGIN_COUNTRY,ORIGIN_CODE,ORIGIN_VIA_CITY,DESTINATION_CITY,DESTINATION_COUNTRY,DESTINATION_CODE,"
				+ "DESTINATION_VIA_CITY,carrier,PRICE,CURRENCY,SHIPMENT_SIZE,SHIPMENT_TYPE,TRANSIT,COMMODITY_FULL,SCAC,"
				+ "SURCHARGE_CODE,SURCHARGE_DESC,SURCHARGE_PRICE,SURCHARGE_CURRENCY,SURCHARGE_EFFECTIVE_DT,"
				+ "SURCHARGE_EXPIRE_DT,RATE_BASIS)";
		dynamicRepository.executeUpdate(site.getDbUrl(), site.getDbUser(), site.getDbPassword(), query);
	}

	public void insertLocalTable(java.io.File file) throws SQLException {
		String queryStr = "TRUNCATE TABLE fcl_freight_rate";
		dynamicRepository.executeUpdate(dbUrl, dbUser, dbPassword, queryStr);
		String query = "LOAD DATA LOCAL INFILE '" + file.getAbsolutePath().replaceAll("\\\\", "/")
				+ "' INTO TABLE fcl_freight_rate FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '\r\n' IGNORE 1 LINES "
				+ "(direction,overseas_agent,origin_country,origin,pol,destination_country,pod,"
				+ "destination,carrier,shipping_code,naviera_group,bill_policy_ship_line,type_of_rate,contract_number,"
				+ "contract_closed_by,currency,20_ofr_std,20_baf_std,20_isps_std,20_pre_std,20_ott_std,"
				+ "20_total_std,40_ofr_std,40_baf_std,40_isps_std,40_pre_std,40_ott_std,40_total_std,40hc_ofr_std,"
				+ "40hc_baf_std,40hc_isps_std,40hc_pre_std,40hc_ott_std,40hc_total_std,40nor_ofr_std,40nor_baf_std,"
				+ "40nor_isps_std,40nor_pre_std,40nor_ott_std,40nor_total_std,transit_time,frequency,route,comments,"
				+ "validity_from,validity_until,scac_code,pol_code,pod_code)";
		dynamicRepository.executeUpdate(dbUrl, dbUser, dbPassword, query);
	}

	public void getLocalTableRates(String path) {
		String queryStr = this.queryBuilder(path);
		dynamicRepository.executeUpdate(dbUrl, dbUser, dbPassword, queryStr);
	}

	private String queryBuilder(String path) {

		List<String> queryList = new ArrayList<String>();
		queryList.add("20$FCL - 20 / DC$OCNF$Ocean Freight$20_ofr_std$20_total_std");
		queryList.add("20$FCL - 20 / DC$BAF$BUNKER ADJUSTMENT FACTOR$20_baf_std$20_total_std");
		queryList.add("20$FCL - 20 / DC$ISPS$PORT SECURITY$20_isps_std$20_total_std");
		queryList.add("20$FCL - 20 / DC$PRE$PRECARRIAGE$20_pre_std$20_total_std");
		queryList.add("20$FCL - 20 / DC$OTT$OTHER$20_ott_std$20_total_std");

		StringBuilder queryStr = new StringBuilder();
		queryStr.append("select *from( ");
		for (String q : queryList) {
			String ele[] = q.split("\\$");
			queryStr.append(" (select concat(carrier,'-',pol_code,'-',pod_code,'-").append(ele[0])
					.append("','-',replace(STR_TO_DATE(validity_until,'%Y/%m/%d'),'-','')) as quote_id, ");
			queryStr.append(
					"contract_number,STR_TO_DATE(validity_from, '%d/%m/%Y') as rateEffDate,STR_TO_DATE(validity_until, '%d/%m/%Y') as rateExpDate, ");
			queryStr.append("if(origin <> '',origin,pol) as origin,origin_country,pol_code,  ");
			queryStr.append("if(origin <> '',pol,'') as originVia,  ");
			queryStr.append("if(destination <> '',destination,pod) as destination,destination_country,pod_code, ");
			queryStr.append("if(destination <> '',pod,'') as destVia,carrier,  ");
			queryStr.append(ele[5]).append(" as price,currency, ");
			queryStr.append("'").append(ele[0]).append("' as shipmentSize,");
			queryStr.append("'").append(ele[1]).append("'  as shipmentType, ");
			queryStr.append("transit_time as transitTime, type_of_rate as commdity,scac_code, ");
			queryStr.append("'").append(ele[2]).append("' as surchargeCode,");
			queryStr.append("'").append(ele[3]).append("' as surchargeDesc, ");
			queryStr.append(ele[4]).append(" as surchargePrice,currency as surchargeCurrency, ");
			queryStr.append(
					"STR_TO_DATE(validity_from, '%d/%m/%Y') as surEffDate,STR_TO_DATE(validity_until, '%d/%m/%Y') as surExpDate, ");
			queryStr.append("'PC' as rateBasis from fcl_freight_rate where ");
			queryStr.append(ele[4]).append(" <> 0 and ").append(ele[5]).append(" <> 0 ) UNION ALL");
		}
		queryStr = new StringBuilder(queryStr.toString().substring(0, queryStr.toString().length() - 10));
		queryStr.append(" ) as t  INTO OUTFILE '").append(path);
		queryStr.append(
				"' FIELDS ENCLOSED BY '\\\"' TERMINATED BY ',' ESCAPED BY '\\\"' LINES TERMINATED BY '\\r\\n' ");

		return queryStr.toString();
	}

	public String getDeleteCsvRates() {
		String queryStr = this.getDeleteQuoteIdQuery();
		return dynamicRepository.getSingleValueQuery(dbUrl, dbUser, dbPassword, queryStr);
	}

	private String getDeleteQuoteIdQuery() {
		List<String> queryList = new ArrayList<String>();
		queryList.add("20");
		queryList.add("40");
		queryList.add("40HC");
		queryList.add("40NOR");
		StringBuilder queryStr = new StringBuilder();
		queryStr.append("select concat(\"'\",group_concat(t.quoteId separator \"','\"),\"'\") as quoteId from ( ");
		for (String q : queryList) {
			queryStr.append(" (select concat(carrier,'-',pol_code,'-',pod_code,'-").append(q);
			queryStr.append("','-',replace(STR_TO_DATE(validity_until,'%Y/%m/%d'),'-','')) as quoteId ");
			queryStr.append(" from fcl_freight_rate  group by carrier)  UNION ALL");
		}

		queryStr = new StringBuilder(queryStr.toString().substring(0, queryStr.toString().length() - 10));
		queryStr.append(" ) as t ");
		return queryStr.toString();
	}

	public void deleteCsvRates(String deleteValues, Site site) {
		String queryStr = "delete from csvrates where QUOTE_ID IN(" + deleteValues + ")";
		int count = dynamicRepository.executeUpdateWithoutParam(site.getDbUrl(), site.getDbUser(), site.getDbPassword(),
				queryStr);
		System.out.println("Count---" + count);
	}

}
