package com.logiware.rates.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.logiware.rates.entity.File;
import com.logiware.rates.util.DateUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class FileResponse {

	private String shipmentType;
	private String ratesType;
	private String name;
	private List<String> companies;
	private String loadedDate;
	private String errors;
	private Long id;

	public FileResponse(File file) {
		switch (file.getShipmentType()) {
		case "F":
			this.shipmentType = "FCL";
			break;
		case "L":
			this.shipmentType = "LCL";
			break;
		default:
			this.shipmentType = "AIR";
			break;
		}
		this.ratesType = "F".equalsIgnoreCase(file.getRatesType()) ? "Freight" : "Other";
		this.name = file.getName();
		this.loadedDate = DateUtils.formatToString(file.getLoadedDate(), "MM/dd/yyyy kk:mm");
		this.companies = file.getHistories().stream().map(h -> h.getCompany().getName()).collect(Collectors.toList());
		this.id = file.getId();
	}

}
