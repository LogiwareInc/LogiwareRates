package com.logiware.rates.security.jwt;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logiware.rates.entity.Role;
import com.logiware.rates.entity.User;
import com.logiware.rates.service.CustomUserDetailsService;
import com.logiware.rates.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenProvider {

	@Value("${security.jwt.token.secret-key:secret}")
	private String secretKey = "secret";

	@Value("${security.jwt.token.expire-length:864000000}")
	private long validityInMilliseconds = 864000000; // 10days

	@Autowired
	private CustomUserDetailsService userDetailsService;
	@Autowired
	private UserService userService;

	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	public String createToken(String username, Role role) {

		Claims claims = Jwts.claims().setSubject(username);
		claims.put("roles", role.getName());

		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMilliseconds);

		return Jwts.builder()//
				.setClaims(claims)//
				.setIssuedAt(now)//
				.setExpiration(validity)//
				.signWith(SignatureAlgorithm.HS256, secretKey)//
				.compact();
	}

	public Authentication getAuthentication(String token) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public String getUsername(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}

	public String resolveToken(HttpServletRequest req) {
		Enumeration<String> headerNames = req.getHeaderNames();
		String ratesToken = req.getHeader("rates-token");
		if (ratesToken != null) {
			return ratesToken;
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
			// Expired or invalid JWT token
		}
		User user = userService.findByUsername(username);
		return createToken(username, user.getRole());
	}

}
