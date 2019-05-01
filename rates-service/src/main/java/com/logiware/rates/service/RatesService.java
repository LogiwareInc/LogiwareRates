package com.logiware.rates.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
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
		return dynamicRepository.getOptionResults(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), builder.toString(), params);
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
		return dynamicRepository.getTypeaheadResults(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), builder.toString(), input);
	}

	public void removeEmptyRates(Site site) throws SQLException {
		String query = "delete from `csvrates` where `carrier` = ''";
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		int count = dynamicRepository.executeUpdate(site.getDbUrl(), site.getDbUser(), site.getDbPassword(), query, parameterSource);
		System.out.println(count);
	}

	public void truncateRates(String carrier, Site site) throws SQLException {
		String query = "delete from `csvrates` where `carrier` = :carrier";
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("carrier", carrier, Types.VARCHAR);
		dynamicRepository.executeUpdate(site.getDbUrl(), site.getDbUser(), site.getDbPassword(), query, parameterSource);
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
		parameterSource.addValue("effectiveDate", DateUtils.formatToString(file.getEffectiveDate(), "yyyy-MM-dd"), Types.VARCHAR);
		parameterSource.addValue("expirationDate", DateUtils.formatToString(file.getExpirationDate(), "yyyy-MM-dd"), Types.VARCHAR);
		parameterSource.addValue("surchargeType", file.getSurchargeType(), Types.VARCHAR);
		parameterSource.addValue("surchargeCurrency", file.getSurchargeCurrency(), Types.VARCHAR);
		parameterSource.addValue("rateBasis", file.getRateBasis(), Types.VARCHAR);
		parameterSource.addValue("carrier", file.getCarrier(), Types.VARCHAR);
		dynamicRepository.executeUpdate(site.getDbUrl(), site.getDbUser(), site.getDbPassword(), builder.toString(), parameterSource);
	}
	
	public void insertOceanFreight(String carrier, Site site) {
		StringBuilder builder = new StringBuilder();
		builder.append("insert into `csvrates` (`quote_id`, `rate_id`, `contract_id`, `amendment_id`, `rate_effective_date`, `rate_expiration_date`, `origin_trade`, `origin_city`, `origin_country`, `origin_code`, `origin_via_city`, `destination_trade`, `destination_city`, `destination_country`, `destination_code`, `destination_via_city`, `carrier`, `price`, `service`, `contract_expire`, `currency`, `currency_rate`, `shipment_size`, `shipment_type`, `commodity_brief`, `commodity_code`, `commodity_exclusions`, `commodity_full`, `hazardous_charge`, `transit`, `contract_notes`, `customer`, `scac`, `surcharge_line_rate_id`, `surcharge_code`, `surcharge_desc`, `surcharge_price`, `surcharge_type`, `surcharge_currency`, `surcharge_expire_dt`, `surcharge_effective_dt`, `rate_basis`, `ship_date`, `gri_remarks`, `general_remarks`) ");
		builder.append("select `quote_id`, `rate_id`, `contract_id`, `amendment_id`, `rate_effective_date`, `rate_expiration_date`, `origin_trade`, `origin_city`, `origin_country`, `origin_code`, `origin_via_city`, `destination_trade`, `destination_city`, `destination_country`, `destination_code`, `destination_via_city`, `carrier`, `price`, `service`, `contract_expire`, `currency`, `currency_rate`, `shipment_size`, `shipment_type`, `commodity_brief`, `commodity_code`, `commodity_exclusions`, `commodity_full`, `hazardous_charge`, `transit`, `contract_notes`, `customer`, `scac`, `surcharge_line_rate_id`, 'OFR', 'OCEAN FREIGHT', `price`, `surcharge_type`, `surcharge_currency`, `surcharge_expire_dt`, `surcharge_effective_dt`, `rate_basis`, `ship_date`, `gri_remarks`, `general_remarks` from `csvrates` where `carrier` = :carrier and `surcharge_code` <> '' group by `origin_code`, `origin_via_city`, `destination_code`, `destination_via_city`, `commodity_brief`, `shipment_size`, `contract_notes`");
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("carrier", carrier, Types.VARCHAR);
		dynamicRepository.executeUpdate(site.getDbUrl(), site.getDbUser(), site.getDbPassword(), builder.toString(), parameterSource);
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
		return dynamicRepository.getOptionResults(site.getDbUrl(), site.getDbUser(), site.getDbPassword(), builder.toString(), params);
	}

	public Map<String, List<KeyValue>> loadRates(Company company, String carrier, String scac, String effectiveDate, String expirationDate, String surchargeType,
			String surchargeCurrency, String siteIds, MultipartFile sourceFile) throws IllegalStateException, IOException, ParseException, SQLException {
		List<Long> ids = Stream.of(siteIds.split(",")).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
		List<Site> sites = siteRepository.findAllById(ids);
		java.io.File directory = new java.io.File(uploadPath + "/" + company.getName().replaceAll("[^a-zA-Z0-9]", "_"));
		if (!directory.exists()) {
			directory.mkdirs();
		}
		String filename = FilenameUtils.getBaseName(sourceFile.getOriginalFilename()).replaceAll("[^a-zA-Z0-9]", "_");
		String extension = FilenameUtils.getExtension(sourceFile.getOriginalFilename());
		java.io.File targetFile = new java.io.File(directory, filename + "_" + System.currentTimeMillis() + "." + extension);
		sourceFile.transferTo(targetFile);
		File file = new File();
		file.setName(sourceFile.getOriginalFilename());
		file.setCarrier(carrier);
		file.setScac(scac);
		System.out.println(effectiveDate + " == " + DateUtils.parseToDate(effectiveDate, "MM/dd/yyyy"));
		file.setEffectiveDate(DateUtils.parseToDate(effectiveDate, "MM/dd/yyyy"));
		file.setExpirationDate(DateUtils.parseToDate(expirationDate, "MM/dd/yyyy"));
		file.setSurchargeType(surchargeType);
		file.setSurchargeCurrency(surchargeCurrency);
		file.setRateBasis("PC");
		file.setPath(targetFile.getAbsolutePath().replaceAll("\\\\", "/"));
		file.setCompany(company);
		file.setLoadedDate(new Date());
		fileRepository.save(file);
		InputStream is = FileUtils.openInputStream(targetFile);
		Map<String, List<KeyValue>> errors = new HashMap<>();
		for (Site site : sites) {
			SiteFile siteFile = new SiteFile();
			siteFile.setFile(file);
			siteFile.setSite(site);
			siteFileRepository.save(siteFile);
			loadRates(company, site, file, is);
			updateRates(file, site);
			insertOceanFreight(carrier, site);
			removeEmptyRates(site);
			List<KeyValue> error = findErrors(file.getCarrier(), site);
			if (error != null && !error.isEmpty()) {
				errors.put(site.getName(), error);
			}
		}
		return errors;
	}

	public List<ResultModel> searchRates(Company company, String carrier, String fromDate, String toDate) throws ParseException {
		return customRepository.findFilesByCarrierOrLoadedDate(company, carrier, fromDate, toDate);
	}
	
}
