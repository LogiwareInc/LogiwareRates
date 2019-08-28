package com.logiware.rates.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.logiware.rates.entity.Company;
import com.logiware.rates.exception.CustomException;
import com.logiware.rates.repository.CompanyRepository;
import com.logiware.rates.security.jwt.JwtTokenProvider;


@Service
public class CompanyService {

	@Autowired
	private CompanyRepository repository;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	public Company findByName(String name) {
		return repository.findByName(name);
	}

	public String getTokenyByLogin(String companyName) {
		try {
			return jwtTokenProvider.createToken(companyName);
		} catch (AuthenticationException e) {
			throw new CustomException("Invalid token or company", HttpStatus.FORBIDDEN);
		}
	}

	public Company whoami(HttpServletRequest req) {
		try {
			return this.findByName(jwtTokenProvider.getCompanyName(jwtTokenProvider.resolveToken(req)));
		} catch (Exception e) {
			throw new CustomException("Invalid token or company", HttpStatus.FORBIDDEN);
		}
	}

}
