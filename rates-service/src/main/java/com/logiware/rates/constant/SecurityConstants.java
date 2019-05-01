package com.logiware.rates.constant;

public interface SecurityConstants {
	public static final String SECRET = "SecretKeyToGenJWTs";
	public static final long EXPIRATION_TIME = 864_000_000;
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "rates-token";
}
