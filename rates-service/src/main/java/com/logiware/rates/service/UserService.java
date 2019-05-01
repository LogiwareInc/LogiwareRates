package com.logiware.rates.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.logiware.rates.exception.CustomException;
import com.logiware.rates.repository.UserRepository;
import com.logiware.rates.entity.User;
import com.logiware.rates.security.jwt.JwtTokenProvider;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private AuthenticationManager authenticationManager;

	public String getTokenByLogin(String loginName, String password) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginName, password));
			return jwtTokenProvider.createToken(loginName, repository.findByUsername(loginName).getRole());
		} catch (AuthenticationException e) {
			throw new CustomException("bad credentials", HttpStatus.BAD_REQUEST);
		}
	}

	public User findByUsername(String username) {
		User user = repository.findByUsername(username);
		if (user == null) {
			throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
		}
		return user;
	}

	public User whoami(HttpServletRequest req) {
		try {
			return repository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
		} catch (Exception e) {
			throw new CustomException("Invalid token or user", HttpStatus.FORBIDDEN);
		}
	}


	public String refreshToken(String token, Date date) throws JsonProcessingException, IOException {
		return jwtTokenProvider.refreshToken(token, date);
	}

}
