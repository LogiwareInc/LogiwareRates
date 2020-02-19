package com.logiware.rates.controller;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.logiware.rates.constant.SecurityConstants;
import com.logiware.rates.dto.FileDTO;
import com.logiware.rates.dto.KeyValueDTO;
import com.logiware.rates.dto.RatesDTO;
import com.logiware.rates.entity.Company;
import com.logiware.rates.service.CompanyService;
import com.logiware.rates.service.RatesService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/rates")
public class RatesController {

	@Value("${spring.security.jwt.token.name}")
	private String tokenName;
	
	@Autowired
	private CompanyService companyService;

	@Autowired
	private RatesService ratesService;

	@GetMapping(value = "/authenticate")
	@ApiOperation(value = "Authenticate")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Authenticated Successfully"), @ApiResponse(code = 400, message = "Invalid Company Name", response = Map.class) })
	public @ResponseBody ResponseEntity<?> getTokenByLogin(@RequestParam(name = "companyName", required = true) String companyName, HttpServletResponse res) {
		try {
			Company company = companyService.findByName(companyName);
			if (company == null) {
				return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid Company Name"));
			}
			String token = companyService.getTokenyByLogin(companyName);
			return ResponseEntity.ok().header(tokenName, token).header("Access-Control-Expose-Headers", tokenName)
					.body(Collections.singletonMap("companyName", companyName));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
		}
	}

	@GetMapping(value = "/partners")
	@ApiOperation(value = "Get Partners")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Partners Sent Successfully", response = List.class),
			@ApiResponse(code = 400, message = "Invalid Company Name", response = Map.class) })
	@ApiImplicitParams({ @ApiImplicitParam(name = SecurityConstants.TOKEN_NAME, required = true, dataType = "string", paramType = "header") })
	public @ResponseBody ResponseEntity<?> getSites(HttpServletRequest req) {
		try {
			Company company = companyService.whoami(req);
			if (company == null) {
				return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid Company Name"));
			}
			List<KeyValueDTO> partners = new ArrayList<>();
			partners.add(new KeyValueDTO(company.getId(), company.getName()));
			company.getPartners().forEach(partner -> {
				partners.add(new KeyValueDTO(partner.getPartner().getId(), partner.getPartner().getName()));
			});
			return ResponseEntity.ok().body(partners);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
		}
	}

	@PostMapping(value = "/load")
	@ApiOperation(value = "Load RatesDTO")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "RatesDTO are loaded Successfully", response = Map.class),
			@ApiResponse(code = 400, message = "Invalid Company Name", response = Map.class) })
	@ApiImplicitParams({ @ApiImplicitParam(name = SecurityConstants.TOKEN_NAME, required = true, dataType = "string", paramType = "header") })
	public @ResponseBody ResponseEntity<?> loadRates(@ModelAttribute RatesDTO rates, HttpServletRequest req) {
		try {
			Company company = companyService.whoami(req);
			if (company == null) {
				return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid Company Name"));
			}
			Map<String, List<KeyValueDTO>> errors = ratesService.loadRates(company, rates);
			Map<String, Object> response = new HashMap<>();
			response.put("message", "RatesDTO are loaded successfully");
			if (!errors.isEmpty()) {
				response.put("errors", errors);
			}
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
		}
	}

	@PostMapping(value = "/find/{loadedDate}")
	@ApiOperation(value = "Find RatesDTO")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "RatesDTO List", response = Map.class),
			@ApiResponse(code = 400, message = "Invalid Company Name", response = Map.class) })
	@ApiImplicitParams({ @ApiImplicitParam(name = SecurityConstants.TOKEN_NAME, required = true, dataType = "string", paramType = "header") })
	public @ResponseBody ResponseEntity<?> findRates(@PathVariable String loadedDate, HttpServletRequest req) {
		try {
			Company company = companyService.whoami(req);
			if (company == null) {
				return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid Company Name"));
			}
			List<FileDTO> files = ratesService.findRates(loadedDate);
			return ResponseEntity.ok().body(files);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
		}
	}	

	@PostMapping(value = "/download")
	@ApiOperation(value = "Download RatesDTO")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "RatesDTO are downloaded", response = Map.class),
			@ApiResponse(code = 400, message = "Invalid Company Name", response = Map.class) })
	@ApiImplicitParams({ @ApiImplicitParam(name = SecurityConstants.TOKEN_NAME, required = true, dataType = "string", paramType = "header") })
	public @ResponseBody ResponseEntity<?> downloadRates(Long id, HttpServletRequest req) {
		try {
			Company company = companyService.whoami(req);
			if (company == null) {
				return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid Company Name"));
			}
			//Map<String, List<KeyValueDTO>> errors = ratesService.loadRates(company, rates);
			Map<String, Object> response = new HashMap<>();
			response.put("message", "RatesDTO are loaded successfully");
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
		}
	}	
}
