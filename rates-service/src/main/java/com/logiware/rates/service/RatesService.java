package com.logiware.rates.service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.logiware.rates.dto.FileDTO;
import com.logiware.rates.dto.KeyValueDTO;
import com.logiware.rates.dto.RatesDTO;
import com.logiware.rates.entity.Company;
import com.logiware.rates.entity.File;
import com.logiware.rates.entity.History;
import com.logiware.rates.repository.CompanyRepository;
import com.logiware.rates.repository.DynamicRepository;
import com.logiware.rates.repository.FileRepository;
import com.logiware.rates.repository.HistoryRepository;
import com.logiware.rates.util.DateUtils;
import com.logiware.rates.util.ExcelUtils;
import com.logiware.rates.util.StringUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import javassist.NotFoundException;

@Service
public class RatesService {

	@Value("${spring.servlet.multipart.location}")
	private String uploadLocation;

	@Autowired
	private DynamicRepository dynamicRepository;

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private HistoryRepository historyRepository;

	private static final char SEPARATOR = ',';
	private static final char QUOTE_CHARACTER = '"';
	private static final char ESCAPE_CHARACTER = '"';
	private static final String LINE_END = "\r\n";
	private static final String ALPHANUMERIC = "[^a-zA-Z0-9]";

	private java.io.File xls2csv(java.io.File xlsFile) throws IOException {
		String path = xlsFile.getPath();
		java.io.File csvFile = new java.io.File(FilenameUtils.getFullPath(path) + FilenameUtils.getName(path).replace(".xlsx", ".csv"));
		try (HSSFWorkbook wb = new HSSFWorkbook(FileUtils.openInputStream(xlsFile));
				CSVWriter cw = new CSVWriter(new FileWriter(csvFile), SEPARATOR, QUOTE_CHARACTER, ESCAPE_CHARACTER, LINE_END);) {
			HSSFSheet sheet = wb.getSheetAt(0);
			HSSFRow row;
			HSSFCell cell;
			List<String[]> lines = new ArrayList<>();
			FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
			for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
				row = sheet.getRow(rowIndex);
				if (row != null) {
					String[] line = new String[row.getLastCellNum()];
					for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
						cell = row.getCell(cellIndex);
						if (null != cell) {
							line[cellIndex] = ExcelUtils.getCellValue(cell, evaluator, "MM/dd/yyyy");
						} else {
							line[cellIndex] = "";
						}
					}
					lines.add(line);
				}
			}
			cw.writeAll(lines);
		}
		return csvFile;
	}

	private java.io.File xlsx2csv(java.io.File xlsFile) throws IOException {
		String path = xlsFile.getPath();
		java.io.File csvFile = new java.io.File(FilenameUtils.getFullPath(path) + FilenameUtils.getName(path).replace(".xlsx", ".csv"));
		try (XSSFWorkbook wb = new XSSFWorkbook(FileUtils.openInputStream(xlsFile));
				CSVWriter cw = new CSVWriter(new FileWriter(csvFile), SEPARATOR, QUOTE_CHARACTER, ESCAPE_CHARACTER, LINE_END);) {
			XSSFSheet sheet = wb.getSheetAt(0);
			XSSFRow row;
			XSSFCell cell;
			List<String[]> lines = new ArrayList<>();
			FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
			for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
				row = sheet.getRow(rowIndex);
				if (row != null && row.getLastCellNum() > 0) {
					String[] line = new String[row.getLastCellNum()];
					for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
						cell = row.getCell(cellIndex);
						if (null != cell) {
							line[cellIndex] = ExcelUtils.getCellValue(cell, evaluator, "MM/dd/yyyy");
						} else {
							line[cellIndex] = "";
						}
					}
					lines.add(line);
				}
			}
			cw.writeAll(lines);
		}
		return csvFile;
	}

	public void loadFclFreightRates(Company company, java.io.File file) throws IOException, SQLException, NotFoundException {
		StringBuilder tableBuilder = new StringBuilder();
		tableBuilder.append("create table `fcl_rate_temp` (");
		tableBuilder.append("  `id` int(10) not null auto_increment,");

		StringBuilder loadBuilder = new StringBuilder();
		loadBuilder.append("load data local infile '").append(file.getPath().replace("\\", "/")).append("'");
		loadBuilder.append("  into ");
		loadBuilder.append("table `fcl_rate_temp` character set 'utf8mb4'");
		loadBuilder.append("  fields terminated by ','");
		loadBuilder.append("  optionally enclosed by '\"'");
		loadBuilder.append("  lines terminated by '\\r\\n'");
		loadBuilder.append("  ignore 1 lines");
		loadBuilder.append("  (");
		Map<String, List<String>> freightRates = new HashMap<>();
		List<String> containerSizes = Arrays.asList(company.getContainerSizes().split(","));
		try (CSVReader reader = new CSVReader(new FileReader(file));) {
			String[] nextLine;
			List<String> headers = new ArrayList<>();
			while ((nextLine = reader.readNext()) != null) {
				headers.addAll(Arrays.asList(nextLine));
				break;
			}
			for (String header : headers) {
				if (StringUtils.isNotEmpty(header)) {
					tableBuilder.append("  `").append(header.toLowerCase().replaceAll(ALPHANUMERIC, "_")).append("`");
					loadBuilder.append(" `").append(header.toLowerCase().replaceAll(ALPHANUMERIC, "_")).append("`,");
					if (containerSizes.stream().anyMatch(header::contains)) {
						String chargeCode = StringUtils.substringBefore(header, " ");
						if (StringUtils.isNotEqual(chargeCode, "Total")) {
							String containerSize = StringUtils.substringAfter(header, " ");
							if (!freightRates.containsKey(containerSize)) {
								freightRates.put(containerSize, new ArrayList<>());
							}
							freightRates.get(containerSize).add(chargeCode);
						}
						tableBuilder.append(" decimal(10,2) not null default 0.00,");
					} else if (header.contains("Comments")) {
						tableBuilder.append(" text,");
					} else {
						tableBuilder.append(" varchar (100) null,");
					}
				}
			}
			tableBuilder.append("  primary key (`id`)");
			tableBuilder.append(") engine=innodb charset=utf8mb4 collate=utf8mb4_general_ci");
			loadBuilder.setLength(loadBuilder.length() - 1);
			loadBuilder.append(")");
			String dropQuery = "drop table if exists `fcl_rate_temp`";
			String alterQuery = "alter table `fcl_rate_temp` add index `fcl_rate_temp_carrier_type_of_rate_idx` (`carrier`, `type_of_rate`)";
			dynamicRepository.executeUpdate(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), dropQuery);
			dynamicRepository.executeUpdate(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), alterQuery);
			dynamicRepository.executeUpdate(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), tableBuilder.toString());
			dynamicRepository.loadLocalInfile(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), loadBuilder.toString(), FileUtils.openInputStream(file));

			StringBuilder builder = new StringBuilder();
			builder.append("select");
			builder.append("  r.`carrier` as col1,");
			builder.append("  r.`type_of_rate` as col2 ");
			builder.append("from");
			builder.append("  `fcl_rate_temp` r");
			builder.append("  left join `trading_partner` t");
			builder.append("    on (t.`account_name` = concat(r.`carrier`, ' - ', r.`type_of_rate`)) ");
			builder.append("where r.`carrier` <> '' and t.`id` is null ");
			builder.append("group by r.`carrier`, r.`type_of_rate`");
			List<KeyValueDTO> carriers = dynamicRepository.getKeyValueResults(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), builder.toString());
			if (Objects.nonNull(carriers) && !carriers.isEmpty()) {
				StringBuilder errors = new StringBuilder();
				errors.append("The following carriers does not exist in TP, cannot load rates.");
				AtomicInteger counter = new AtomicInteger(1);
				carriers.forEach(kv -> {
					errors.append("<br>" + counter.get() + ") ").append(kv.getKey() + " - " + kv.getValue());
					counter.getAndIncrement();
				});
				errors.append("<br>Please use the name that exists in TP setup for " + company.getName());
				throw new NotFoundException(errors.toString());
			}
			builder.setLength(0);
			builder.append("insert ignore into `fcl_rate` (");
			builder.append("  `carrier`,");
			builder.append("  `scac`,");
			builder.append("  `contract_number`,");
			builder.append("  `type_of_rate`,");
			builder.append("  `entered_date`");
			builder.append(") ");
			builder.append("select");
			builder.append("  temp.`carrier`,");
			builder.append("  temp.`scac_code`,");
			builder.append("  temp.`contract_number`,");
			builder.append("  temp.`type_of_rate`,");
			builder.append("  now() as entered_date ");
			builder.append("from");
			builder.append("  `fcl_rate_temp` temp ");
			builder.append("where temp.`carrier` <> '' ");
			builder.append("group by temp.`carrier`, temp.`scac_code`, temp.`contract_number`, temp.`type_of_rate`");
			dynamicRepository.executeUpdate(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), builder.toString());

			builder.setLength(0);
			builder.append("delete");
			builder.append("  route ");
			builder.append("from");
			builder.append("  `fcl_rate_temp` temp");
			builder.append("  join `fcl_rate` rate");
			builder.append("    on (");
			builder.append("      rate.`carrier` = temp.`carrier` ");
			builder.append("      and rate.`scac` = temp.`scac_code` ");
			builder.append("      and rate.`contract_number` = temp.`contract_number`");
			builder.append("      and rate.`type_of_rate` = temp.`type_of_rate`");
			builder.append("    )");
			builder.append("  join `fcl_rate_route` route");
			builder.append("    on (");
			builder.append("      route.`rate_id` = rate.`id`");
			builder.append("      and route.`pol_code` = temp.`pol_code`");
			builder.append("      and route.`pod_code` = temp.`pod_code`");
			builder.append("      and route.`validity_from` = str_to_date(temp.`validity_from`, '%m/%d/%Y')");
			builder.append("      and route.`validity_to` = str_to_date(temp.`validity_until`, '%m/%d/%Y')");
			builder.append("    ) ");
			builder.append("where temp.`carrier` <> '' ");
			dynamicRepository.executeUpdate(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), builder.toString());

			builder.setLength(0);
			builder.append("insert into `fcl_rate_route` (");
			builder.append("  `rate_id`,");
			builder.append("  `origin_country`,");
			builder.append("  `origin`,");
			builder.append("  `pol`,");
			builder.append("  `pol_code`,");
			builder.append("  `destination_country`,");
			builder.append("  `pod`,");
			builder.append("  `pod_code`,");
			builder.append("  `destination`,");
			builder.append("  `currency`,");
			builder.append("  `transit_time`,");
			builder.append("  `frequency`,");
			builder.append("  `route`,");
			builder.append("  `comments`,");
			builder.append("  `validity_from`,");
			builder.append("  `validity_to`,");
			builder.append("  `direction`,");
			builder.append("  `overseas_agent`,");
			builder.append("  `shipping_code`,");
			builder.append("  `naviera_group`,");
			builder.append("  `bill_policy`,");
			builder.append("  `contract_closed_by`,");
			builder.append("  `entered_date`");
			builder.append(") ");
			builder.append("select");
			builder.append("  rate.`id` as `rate_id`,");
			builder.append("  temp.`origin_country`,");
			builder.append("  temp.`origin`,");
			builder.append("  temp.`pol`,");
			builder.append("  temp.`pol_code`,");
			builder.append("  temp.`destination_country`,");
			builder.append("  temp.`pod`,");
			builder.append("  temp.`pod_code`,");
			builder.append("  temp.`destination`,");
			builder.append("  temp.`currency`,");
			builder.append("  temp.`transit_time`,");
			builder.append("  temp.`frequency`,");
			builder.append("  temp.`route`,");
			builder.append("  temp.`comments`,");
			builder.append("  str_to_date(temp.`validity_from`, '%m/%d/%Y'),");
			builder.append("  str_to_date(temp.`validity_until`, '%m/%d/%Y'),");
			builder.append("  temp.`direction`,");
			builder.append("  temp.`overseas_agent`,");
			builder.append("  temp.`shipping_code`,");
			builder.append("  temp.`naviera_group`,");
			builder.append("  temp.`bill_policy_shipping_line`,");
			builder.append("  temp.`contract_closed_by`,");
			builder.append("  now() as entered_date ");
			builder.append("from");
			builder.append("  `fcl_rate_temp` temp");
			builder.append("  join `fcl_rate` rate");
			builder.append("    on (");
			builder.append("      rate.`carrier` = temp.`carrier` ");
			builder.append("      and rate.`scac` = temp.`scac_code` ");
			builder.append("      and rate.`contract_number` = temp.`contract_number`");
			builder.append("      and rate.`type_of_rate` = temp.`type_of_rate`");
			builder.append("    ) ");
			builder.append("where temp.`carrier` <> '' ");
			builder.append("group by temp.`carrier`, temp.`scac_code`, temp.`contract_number`, temp.`type_of_rate`, temp.`pol_code`, temp.`pod_code`");
			dynamicRepository.executeUpdate(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), builder.toString());

			builder.setLength(0);
			builder.append("insert ignore into `fcl_freight_rate` (");
			builder.append("  `route_id`,");
			builder.append("  `container_size`,");
			builder.append("  `charge_code`,");
			builder.append("  `rate`,");
			builder.append("  `entered_date`");
			builder.append(") ");
			boolean union = false;
			for (String containerSize : freightRates.keySet()) {
				for (String chargeCode : freightRates.get(containerSize)) {
					String rateField = chargeCode.toLowerCase() + "_" + containerSize.toLowerCase().replaceAll(ALPHANUMERIC, "_");
					if (union) {
						builder.append(" union all");
					}
					builder.append("(select");
					builder.append("  route.`id` as `route_id`,");
					builder.append("  '").append(containerSize).append("' as `container_size`,");
					builder.append("  '").append(chargeCode).append("' as `charge_code`,");
					builder.append("  temp.`").append(rateField).append("`,");
					builder.append("  now() as entered_date ");
					builder.append("from");
					builder.append("  `fcl_rate_temp` temp");
					builder.append("  join `fcl_rate` rate");
					builder.append("    on (");
					builder.append("      rate.`scac` = temp.`scac_code`");
					builder.append("      and rate.`contract_number` = temp.`contract_number`");
					builder.append("      and rate.`type_of_rate` = temp.`type_of_rate`");
					builder.append("    ) ");
					builder.append("  join `fcl_rate_route` route");
					builder.append("    on (");
					builder.append("      route.`rate_id` = rate.`id`");
					builder.append("      and route.`pol_code` = temp.`pol_code`");
					builder.append("      and route.`pod_code` = temp.`pod_code`");
					builder.append("      and route.`validity_from` = str_to_date(temp.`validity_from`, '%m/%d/%Y')");
					builder.append("      and route.`validity_to` = str_to_date(temp.`validity_until`, '%m/%d/%Y')");
					builder.append("    ) ");
					builder.append("where temp.`carrier` <> '' ");
					builder.append("  and temp.`").append(rateField).append("` <> 0.00)");
					union = true;
				}
			}
			dynamicRepository.executeUpdate(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), builder.toString());

		}
	}

	public void loadFclOtherRates(Company company, java.io.File file) throws FileNotFoundException, IOException, SQLException {
		StringBuilder tableBuilder = new StringBuilder();
		tableBuilder.append("create table `fcl_rate_temp` (");
		tableBuilder.append("  `id` int(10) not null auto_increment,");

		StringBuilder loadBuilder = new StringBuilder();
		loadBuilder.append("load data local infile '").append(file.getPath().replace("\\", "/")).append("'");
		loadBuilder.append("  into ");
		loadBuilder.append("table `fcl_rate_temp` character set 'utf8mb4'");
		loadBuilder.append("  fields terminated by ','");
		loadBuilder.append("  optionally enclosed by '\"'");
		loadBuilder.append("  lines terminated by '\\r\\n'");
		loadBuilder.append("  ignore 1 lines");
		loadBuilder.append("  (");
		List<String> carriers = new ArrayList<>();
		List<String> costTypes = Arrays.asList(company.getCostTypes().split(","));
		try (CSVReader reader = new CSVReader(new FileReader(file));) {
			String[] nextLine;
			List<String> headers = new ArrayList<>();
			while ((nextLine = reader.readNext()) != null) {
				headers.addAll(Arrays.asList(nextLine));
				break;
			}
			for (String header : headers) {
				if (StringUtils.isNotEmpty(header)) {
					tableBuilder.append("  `").append(header.toLowerCase().replaceAll(ALPHANUMERIC, "_")).append("` varchar (100) null,");
					loadBuilder.append("`").append(header.toLowerCase().replaceAll(ALPHANUMERIC, "_")).append("`,");
					if (StringUtils.isNotEqual(header, "Matrix")) {
						carriers.add(header);
					}
				}
			}
			tableBuilder.append("  primary key (`id`)");
			tableBuilder.append(") engine=innodb charset=utf8mb4 collate=utf8mb4_general_ci");
			loadBuilder.setLength(loadBuilder.length() - 1);
			loadBuilder.append(")");
			String dropQuery = "drop table if exists `fcl_rate_temp`";
			dynamicRepository.executeUpdate(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), dropQuery);
			dynamicRepository.executeUpdate(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), tableBuilder.toString());
			dynamicRepository.loadLocalInfile(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), loadBuilder.toString(), FileUtils.openInputStream(file));

			StringBuilder builder = new StringBuilder();
			builder.append("update");
			builder.append("  `fcl_rate` rate");
			builder.append("  join  (");
			boolean union = false;

			for (String carrier : carriers) {
				String field = carrier.toLowerCase().replaceAll(ALPHANUMERIC, "_");
				if (union) {
					builder.append("    union all");
				}
				builder.append("    (select");
				builder.append("      '").append(carrier).append("' as carrier,");
				builder.append("      group_concat(case when temp.`matrix` = 'Deposit' then temp.`").append(field).append("` else null end) as deposit,");
				builder.append("      group_concat(case when temp.`matrix` = 'Free Days' then temp.`").append(field).append("` else null end) as free_days");
				builder.append("    from");
				builder.append("     `fcl_rate_temp` temp");
				builder.append("    where temp.`").append(field).append("` is not null");
				builder.append("      and temp.`matrix` in ('Deposit', 'Free Days')");
				builder.append("    )");
				union = true;
			}
			builder.append("    ) as temp");
			builder.append("    on (rate.`carrier` = temp.`carrier`) ");
			builder.append("set rate.`deposit` = temp.`deposit`,");
			builder.append("  rate.`free_days` = temp.`free_days`");
			dynamicRepository.executeUpdate(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), builder.toString());

			for (String carrier : carriers) {
				builder.setLength(0);
				String field = carrier.toLowerCase().replaceAll(ALPHANUMERIC, "_");
				builder.append("delete");
				builder.append("  other ");
				builder.append("from");
				builder.append("  `fcl_rate` rate");
				builder.append("  join `fcl_other_rate` other");
				builder.append("    on (");
				builder.append("      other.`rate_id` = rate.`id`");
				builder.append("      and other.`validity_from` = (select str_to_date(validity.`").append(field)
						.append("`, '%m/%d/%Y') from `fcl_rate_temp` validity where validity.`matrix` = 'Validity From' limit 1)");
				builder.append("      and other.`validity_to` = (select str_to_date(validity.`").append(field)
						.append("`, '%m/%d/%Y') from `fcl_rate_temp` validity where validity.`matrix` = 'Validity Until' limit 1)");
				builder.append("    ) ");
				builder.append("where rate.`carrier` = '").append(carrier).append("'");
				dynamicRepository.executeUpdate(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), builder.toString());
			}

			builder.setLength(0);
			builder.append("insert into `fcl_other_rate` (");
			builder.append("  `rate_id`,");
			builder.append("  `drop_off`,");
			builder.append("  `container_size`,");
			builder.append("  `charge_code`,");
			builder.append("  `rate`,");
			builder.append("  `validity_from`,");
			builder.append("  `validity_to`,");
			builder.append("  `entered_date`");
			builder.append(") ");
			union = false;
			for (String carrier : carriers) {
				String field = carrier.toLowerCase().replaceAll(ALPHANUMERIC, "_");
				if (union) {
					builder.append(" union all");
				}
				builder.append("(select");
				builder.append("  rate.`id` as rate_id,");
				builder.append("  substring_index(temp.`matrix`, ' DROPOFF', 1) as drop_off,");
				builder.append("  substring_index(temp.`matrix`, 'DROPOFF ', -1) as container_size,");
				builder.append("  'DROPOFF' as charge_code,");
				builder.append("  temp.`").append(field).append("` as `rate`,");
				builder.append("  (select str_to_date(validity.`").append(field)
						.append("`, '%m/%d/%Y') from `fcl_rate_temp` validity where validity.`matrix` = 'Validity From' limit 1) as validity_from,");
				builder.append("  (select str_to_date(validity.`").append(field)
						.append("`, '%m/%d/%Y') from `fcl_rate_temp` validity where validity.`matrix` = 'Validity Until' limit 1) as validity_to,");
				builder.append("  now() as entered_date ");
				builder.append("from");
				builder.append("  `fcl_rate_temp` temp ");
				builder.append("  join `fcl_rate` rate");
				builder.append("    on (rate.`carrier` = '").append(carrier).append("')");
				builder.append("where temp.`").append(field).append("` is not null");
				builder.append("  and temp.`matrix` like '%DROPOFF%'");
				builder.append(")");
				union = true;
			}
			dynamicRepository.executeUpdate(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), builder.toString());

			builder.setLength(0);
			builder.append("insert into `fcl_other_rate` (");
			builder.append("  `rate_id`,");
			builder.append("  `cost_type`,");
			builder.append("  `charge_code`,");
			builder.append("  `rate`,");
			builder.append("  `validity_from`,");
			builder.append("  `validity_to`,");
			builder.append("  `entered_date`");
			builder.append(") ");
			union = false;
			for (String carrier : carriers) {
				String field = carrier.toLowerCase().replaceAll(ALPHANUMERIC, "_");
				for (String costType : costTypes) {
					if (union) {
						builder.append(" union all");
					}
					builder.append("(select");
					builder.append("  rate.`id` as rate_id,");
					builder.append("  '").append(costType).append("' as cost_type,");
					builder.append("  substring_index(temp.`matrix`, ' ").append(costType).append("', 1) as charge_code,");
					builder.append("  temp.`").append(field).append("` as `rate`,");
					builder.append("  (select str_to_date(validity.`").append(field)
							.append("`, '%m/%d/%Y') from `fcl_rate_temp` validity where validity.`matrix` = 'Validity From' limit 1) as validity_from,");
					builder.append("  (select str_to_date(validity.`").append(field)
							.append("`, '%m/%d/%Y') from `fcl_rate_temp` validity where validity.`matrix` = 'Validity Until' limit 1) as validity_to,");
					;
					builder.append("  now() as entered_date ");
					builder.append("from");
					builder.append("  `fcl_rate_temp` temp ");
					builder.append("  join `fcl_rate` rate");
					builder.append("    on (rate.`carrier` = '").append(carrier).append("')");
					builder.append("where temp.`").append(field).append("` is not null");
					builder.append("  and temp.`matrix` like '%").append(costType).append("'");
					builder.append(")");
					union = true;
				}
			}
			dynamicRepository.executeUpdate(company.getDbUrl(), company.getDbUser(), company.getDbPassword(), builder.toString());
		}
	}

	public void loadRates(Company company, RatesDTO rates) throws Exception {
		List<Long> ids = Stream.of(rates.getPartnerIds().split(",")).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
		List<Company> partners = companyRepository.findAllById(ids);
		java.io.File directory = new java.io.File(uploadLocation + "/" + company.getName().replaceAll(ALPHANUMERIC, "_"));
		if (!directory.exists()) {
			directory.mkdirs();
		}
		MultipartFile sourceFile = rates.getFile();
		String filename = FilenameUtils.getBaseName(sourceFile.getOriginalFilename()).replaceAll(ALPHANUMERIC, "_");
		String extension = FilenameUtils.getExtension(sourceFile.getOriginalFilename());
		java.io.File targetFile = new java.io.File(directory, filename + "_" + System.currentTimeMillis() + "." + extension);
		sourceFile.transferTo(targetFile);
		File file = new File();
		file.setName(sourceFile.getOriginalFilename());
		file.setPath(targetFile.getAbsolutePath().replaceAll("\\\\", "/"));
		file.setCompany(company);
		file.setShipmentType(rates.getShipmentType());
		file.setRatesType(rates.getRateType());
		file.setLoadedDate(new Date());
		fileRepository.save(file);
		java.io.File csvFile;
		if (FilenameUtils.isExtension(file.getName(), "xls")) {
			csvFile = xls2csv(targetFile);
		} else if (FilenameUtils.isExtension(file.getName(), "xlsx")) {
			csvFile = xlsx2csv(targetFile);
		} else {
			csvFile = targetFile;
		}
		for (Company partner : partners) {
			History history = new History();
			history.setFile(file);
			history.setCompany(partner);
			historyRepository.save(history);
			if (StringUtils.isEqual(rates.getRateType(), "F")) {
				loadFclFreightRates(partner, csvFile);
			} else {
				loadFclOtherRates(partner, csvFile);
			}
		}
	}

	public List<FileDTO> findRates(String loadedDate) throws ParseException {
		List<File> files;
		if (StringUtils.isNotEmpty(loadedDate)) {
			Date start = DateUtils.parseToDate(loadedDate, "MM/dd/yyyy 00:00:00");
			Date end = DateUtils.parseToDate(loadedDate, "MM/dd/yyyy 23:59:59");
			files = fileRepository.findByLoadedDateBetween(start, end);
		} else {
			files = fileRepository.findAll();
		}
		return files.stream().map(FileDTO::new).collect(Collectors.toList());
	}

	public File findById(Long id) {
		return fileRepository.getOne(id);
	}
}
