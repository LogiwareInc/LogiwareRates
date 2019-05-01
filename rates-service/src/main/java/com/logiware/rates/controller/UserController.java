package com.logiware.rates.controller;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.logiware.rates.constant.ErrorMessageConstant;
import com.logiware.rates.dto.Credential;
import com.logiware.rates.dto.ErrorMsg;
import com.logiware.rates.dto.UserDetail;
import com.logiware.rates.entity.User;
import com.logiware.rates.exception.CustomException;
import com.logiware.rates.service.UserService;
import com.logiware.rates.util.StringUtils;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/auth")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/login")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "successfuly authorization", response = UserDetail.class),
			@ApiResponse(code = 400, message = "bad credentials", response = ErrorMsg.class),
			@ApiResponse(code = 401, message = "input validation failed", response = ErrorMsg.class) })
	@ApiOperation(value = "Authorization")
	public @ResponseBody ResponseEntity<?> login(@RequestBody Credential credential, HttpServletResponse res) {
		try {
			if (credential == null || StringUtils.isAllEmpty(credential.getUsername(), credential.getPassword())) {
				return new ResponseEntity<ErrorMsg>(new ErrorMsg("input validation failed"), HttpStatus.UNAUTHORIZED);
			}
			String ratesToken = userService.getTokenByLogin(credential.getUsername(), credential.getPassword());
			User user = userService.findByUsername(credential.getUsername());
			String logo = Base64.encodeBase64String(user.getCompany().getLogo());
			UserDetail userDetail = new UserDetail(user.getFirstName(), user.getCompany().getName(), user.getRole().getName(), logo);
			res.addHeader("rates-token", ratesToken);
			res.setHeader("Access-Control-Expose-Headers", "rates-token");
			return new ResponseEntity<UserDetail>(userDetail, HttpStatus.OK);
		} catch (CustomException e) {
			if (e.getHttpStatus().equals(HttpStatus.BAD_REQUEST)) {
				return new ResponseEntity<ErrorMsg>(new ErrorMsg(ErrorMessageConstant.LOGIN_400),
						HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			return new ResponseEntity<ErrorMsg>(new ErrorMsg(ErrorMessageConstant.LOGIN_400), HttpStatus.BAD_REQUEST);

		}
		return new ResponseEntity<ErrorMsg>(new ErrorMsg(ErrorMessageConstant.LOGIN_400), HttpStatus.FORBIDDEN);
	}

	@PostMapping("/refresh")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfuly refreshed", response = UserDetail.class),
			@ApiResponse(code = 400, message = "Invalid token", response = ErrorMsg.class) })
	@ApiOperation(value = "Refresh token")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "rates-token", required = true, dataType = "string", paramType = "header") })
	public @ResponseBody ResponseEntity<?> refresh(HttpServletRequest req, HttpServletResponse res) {
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			String token = userService.refreshToken(req.getHeader("rates-token"), cal.getTime());
			User user = userService.whoami(req);
			String logo = Base64.encodeBase64String(user.getCompany().getLogo());
			UserDetail userDetail = new UserDetail(user.getFirstName(), user.getCompany().getName(), user.getRole().getName(), logo);
			res.addHeader("rates-token", token);
			res.setHeader("Access-Control-Expose-Headers", "rates-token");
			return new ResponseEntity<UserDetail>(userDetail, HttpStatus.OK);
		} catch (CustomException e) {
			if (e.getHttpStatus().equals(HttpStatus.BAD_REQUEST)) {
				return new ResponseEntity<ErrorMsg>(new ErrorMsg(ErrorMessageConstant.LOGIN_400),
						HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			return new ResponseEntity<ErrorMsg>(new ErrorMsg(ErrorMessageConstant.LOGIN_400), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<ErrorMsg>(new ErrorMsg(ErrorMessageConstant.LOGIN_400), HttpStatus.FORBIDDEN);
	}
}
