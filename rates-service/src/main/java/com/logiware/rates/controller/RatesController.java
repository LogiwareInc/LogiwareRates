package com.logiware.rates.controller;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.net.HttpHeaders;
import com.logiware.rates.constant.SecurityConstants;
import com.logiware.rates.dto.FileResponse;
import com.logiware.rates.dto.KeyValueResult;
import com.logiware.rates.dto.UploadRequest;
import com.logiware.rates.entity.Company;
import com.logiware.rates.entity.File;
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
			List<KeyValueResult> partners = new ArrayList<>();
			partners.add(new KeyValueResult(company.getId(), company.getName()));
			company.getPartners().forEach(partner -> {
				partners.add(new KeyValueResult(partner.getPartner().getId(), partner.getPartner().getName()));
			});
			return ResponseEntity.ok().body(partners);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
		}
	}

	@PostMapping(value = "/load")
	@ApiOperation(value = "Load Rates")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Rates are loaded Successfully", response = Map.class),
			@ApiResponse(code = 400, message = "Invalid Company Name", response = Map.class) })
	@ApiImplicitParams({ @ApiImplicitParam(name = SecurityConstants.TOKEN_NAME, required = true, dataType = "string", paramType = "header") })
	public @ResponseBody ResponseEntity<?> loadRates(@ModelAttribute UploadRequest rates, HttpServletRequest req) {
		try {
			Company company = companyService.whoami(req);
			if (company == null) {
				return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid Company Name"));
			}
			ratesService.loadRates(company, rates);
			return ResponseEntity.ok().body(Collections.singletonMap("message", "Rates are loaded successfully"));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
		}
	}

	@PostMapping(value = "/find/{loadedDate}")
	@ApiOperation(value = "Find Rates")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Rates List", response = Map.class),
			@ApiResponse(code = 400, message = "Invalid Company Name", response = Map.class) })
	@ApiImplicitParams({ @ApiImplicitParam(name = SecurityConstants.TOKEN_NAME, required = true, dataType = "string", paramType = "header") })
	public @ResponseBody ResponseEntity<?> findRates(@PathVariable String loadedDate, HttpServletRequest req) {
		try {
			Company company = companyService.whoami(req);
			if (company == null) {
				return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid Company Name"));
			}
			List<FileResponse> files = ratesService.findRates(loadedDate);
			return ResponseEntity.ok().body(files);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
		}
	}	

	@PostMapping(value = "/download")
	@ApiOperation(value = "Download Rates")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Rates are downloaded", response = Map.class),
			@ApiResponse(code = 400, message = "Invalid Company Name", response = Map.class) })
	@ApiImplicitParams({ @ApiImplicitParam(name = SecurityConstants.TOKEN_NAME, required = true, dataType = "string", paramType = "header") })
	public @ResponseBody ResponseEntity<?> downloadRates(Long id, HttpServletRequest req) {
		try {
			Company company = companyService.whoami(req);
			if (company == null) {
				return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid Company Name"));
			}
			File file = ratesService.findById(id);
			Path path = Paths.get(file.getPath());
			Resource resource = new UrlResource(path.toUri());
			return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType(Files.probeContentType(path)))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
		}
	}	
}
