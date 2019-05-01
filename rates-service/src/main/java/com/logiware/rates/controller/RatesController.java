package com.logiware.rates.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.logiware.rates.constant.ErrorMessageConstant;
import com.logiware.rates.dto.ErrorMsg;
import com.logiware.rates.dto.KeyValue;
import com.logiware.rates.dto.ResultModel;
import com.logiware.rates.entity.Company;
import com.logiware.rates.entity.User;
import com.logiware.rates.service.RatesService;
import com.logiware.rates.service.UserService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/rates")
public class RatesController {

	@Autowired
	private UserService userService;
	@Autowired
	private RatesService ratesService;

	@GetMapping("/typeahead")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "typeahead data", response = List.class),
			@ApiResponse(code = 400, message = "bad credentials", response = ErrorMsg.class) })
	@ApiOperation(value = "Get Typeahead Results")
	@ApiImplicitParams({ @ApiImplicitParam(name = "rates-token", dataType = "string", paramType = "header") })
	public @ResponseBody ResponseEntity<?> getTypeaheadResults(@RequestParam String type, @RequestParam String input, HttpServletRequest req) {
		try {
			User user = userService.whoami(req);
			List<KeyValue> results = ratesService.getTypeaheadResults(user.getCompany(), type, input);
			return new ResponseEntity<List<KeyValue>>(results, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<ErrorMsg>(new ErrorMsg(ErrorMessageConstant.LOGIN_400), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/options")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "typeahead data", response = List.class),
			@ApiResponse(code = 400, message = "bad credentials", response = ErrorMsg.class) })
	@ApiOperation(value = "Get Options Results")
	@ApiImplicitParams({ @ApiImplicitParam(name = "rates-token", dataType = "string", paramType = "header") })
	public @ResponseBody ResponseEntity<?> getOptionResults(@RequestParam String type, HttpServletRequest req) {
		try {
			User user = userService.whoami(req);
			List<KeyValue> results = ratesService.getOptionResults(user.getCompany(), type);
			return new ResponseEntity<List<KeyValue>>(results, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<ErrorMsg>(new ErrorMsg(ErrorMessageConstant.LOGIN_400), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/sites")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "typeahead data", response = List.class),
			@ApiResponse(code = 400, message = "bad credentials", response = ErrorMsg.class) })
	@ApiOperation(value = "Get Sites")
	@ApiImplicitParams({ @ApiImplicitParam(name = "rates-token", dataType = "string", paramType = "header") })
	public @ResponseBody ResponseEntity<?> getSites(HttpServletRequest req) {
		try {
			User user = userService.whoami(req);
			Company company = user.getCompany();
			List<KeyValue> sites = new ArrayList<>();
			company.getSites().forEach(site -> {
				sites.add(new KeyValue(site.getId(), site.getName()));
			});
			return new ResponseEntity<List<KeyValue>>(sites, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<ErrorMsg>(new ErrorMsg(ErrorMessageConstant.LOGIN_400), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/load")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "rates", response = Map.class), @ApiResponse(code = 400, message = "bad credentials", response = ErrorMsg.class) })
	@ApiOperation(value = "Load Rates")
	@ApiImplicitParams({ @ApiImplicitParam(name = "rates-token", dataType = "string", paramType = "header") })
	public @ResponseBody ResponseEntity<?> saveTemplate(@RequestParam("carrier") String carrier, @RequestParam("scac") String scac,
			@RequestParam("effectiveDate") String effectiveDate, @RequestParam("expirationDate") String expirationDate, @RequestParam("surchargeType") String surchargeType,
			@RequestParam("surchargeCurrency") String surchargeCurrency, @RequestParam("sites") String sites, @RequestParam("file") MultipartFile file, HttpServletRequest req) {
		try {
			User user = userService.whoami(req);
			Map<String, List<KeyValue>> errors = ratesService.loadRates(user.getCompany(), carrier, scac, effectiveDate, expirationDate, surchargeType, surchargeCurrency, sites,
					file);
			Map<String, Object> response = new HashMap<>();
			response.put("message", "Rates are loaded successfully");
			if (!errors.isEmpty()) {
				response.put("errors", errors);
			}
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<ErrorMsg>(new ErrorMsg(ErrorMessageConstant.LOGIN_400), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/search")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "rates", response = List.class), @ApiResponse(code = 400, message = "bad credentials", response = ErrorMsg.class) })
	@ApiOperation(value = "Search Rates")
	@ApiImplicitParams({ @ApiImplicitParam(name = "rates-token", dataType = "string", paramType = "header") })
	public @ResponseBody ResponseEntity<?> saveTemplate(@RequestParam("carrier") String carrier, @RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate,
			HttpServletRequest req) {
		try {
			User user = userService.whoami(req);
			List<ResultModel> results = ratesService.searchRates(user.getCompany(), carrier, fromDate, toDate);
			return new ResponseEntity<List<ResultModel>>(results, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<ErrorMsg>(new ErrorMsg(ErrorMessageConstant.LOGIN_400), HttpStatus.BAD_REQUEST);
		}
	}

}
