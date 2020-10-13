package com.logiware.rates.controller;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.logiware.rates.constant.SecurityConstants;
import com.logiware.rates.dto.RateRequest;
import com.logiware.rates.entity.Company;
import com.logiware.rates.service.CatapultService;
import com.logiware.rates.service.CompanyService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/catapult")
public class CatapultController {
	
	@Value("${spring.security.jwt.token.name}")
	private String tokenName;
	
	@Autowired
	private CompanyService companyService;

	@Autowired
	private CatapultService catapultService;

	@PostMapping(value = "/rates")
	@ApiOperation(value = "Get Rates")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Rates are sent Successfully", response = List.class),
			@ApiResponse(code = 400, message = "Invalid Company Name", response = List.class) })
	@ApiImplicitParams({ @ApiImplicitParam(name = SecurityConstants.TOKEN_NAME, required = true, dataType = "string", paramType = "header") })
	public @ResponseBody ResponseEntity<?> getRates(@RequestBody RateRequest request, HttpServletRequest req) {
		try {
			Company company = companyService.whoami(req);
			if (company == null) {
				return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid Company Name"));
			}
			return ResponseEntity.ok().body(catapultService.getRates(company, request));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
		}
	}
}
