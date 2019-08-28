package com.logiware.rates.security.jwt;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logiware.rates.entity.Company;
import com.logiware.rates.service.CompanyService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenProvider {

	@Value("${spring.security.jwt.token.secret-key}")
	private String secretKey;

	@Value("${spring.security.jwt.token.expire-length}")
	private long expiryLength; // 1h

	@Value("${spring.security.jwt.token.granted-roles}")
	private String grantedRoles;

	@Value("${spring.security.jwt.token.name}")
	private String tokenName;
	
	@Autowired
	private CompanyService companyService;

	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	public String createToken(String username) {
		Claims claims = Jwts.claims().setSubject(username);
		claims.put("grantedRoles", grantedRoles);
		Date now = new Date();
		Date validity = new Date(now.getTime() + expiryLength);
		return Jwts.builder()//
				.setClaims(claims)//
				.setIssuedAt(now)//
				.setExpiration(validity)//
				.signWith(SignatureAlgorithm.HS256, secretKey)//
				.compact();
	}

	public Authentication getAuthentication(String token) {
		Company company = companyService.findByName(getCompanyName(token));
		return new UsernamePasswordAuthenticationToken(company, null, AuthorityUtils.createAuthorityList(grantedRoles));
	}

	public String getCompanyName(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}

	public String resolveToken(HttpServletRequest req) {
		String token = req.getHeader(tokenName);
		if (token != null) {
			return token;
		}
		return null;
	}

	public boolean validateToken(String token) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			if (claims.getBody().getExpiration().before(new Date())) {
				return false;
			}
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			throw new InvalidJwtAuthenticationException("Expired or invalid JWT token");
		}
	}

	public String refreshToken(String token, Date date) throws JsonProcessingException, IOException {
		Base64.Decoder decoder = Base64.getUrlDecoder();
		String[] parts = token.split("\\.");
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(new String(decoder.decode(parts[1])));// Payload
		String username = rootNode.path("sub").asText();// Subject
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().setExpiration(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return createToken(username);
	}

}
