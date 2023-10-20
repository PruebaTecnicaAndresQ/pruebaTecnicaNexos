package com.nexos.NexosPruebaTecnica.services;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nexos.NexosPruebaTecnica.dtos.JwtResponse;
import com.nexos.NexosPruebaTecnica.exceptions.ApiErrorException;

import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.Verifier;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACSigner;
import io.fusionauth.jwt.hmac.HMACVerifier;

@Component
public class JwtTokenWorker {

	@Value("${jwt.token.config.token.secret:pruebaAsesoftwareToken}")
	private String secretCode;
	@Value("${jwt.token.config.time-zone:UTC}")
	private String timeZone;
	@Value("${jwt.token.config.issuer:none}")
	private String issuer;
	@Value("${jwt.token.config.token.type}")
	private String token_type;
	@Value("${jwt.token.config.token.expired-in:3600}")
	private int expiredIn;

	public JwtResponse buildToken(String subject) throws ApiErrorException {
		JwtResponse response = null;
		try {
			Signer signer = HMACSigner.newSHA256Signer(secretCode);
			TimeZone tz = TimeZone.getTimeZone(timeZone);
			ZonedDateTime ztz = ZonedDateTime.now(tz.toZoneId()).plusSeconds(expiredIn);
			JWT jwt = new JWT().setIssuer(issuer).setIssuedAt(ZonedDateTime.now(tz.toZoneId())).setSubject(subject)
					.setExpiration(ztz);
			String token = JWT.getEncoder().encode(jwt, signer);
			response = JwtResponse.builder().accesstoken(token).client_id(subject).tokentype(token_type)
					.issuedAt(new Date().getTime() + "").expiresIn(expiredIn).build();
			return response;
		} catch (Exception e) {
			throw new ApiErrorException("JwtTokenWorker=>buildToken::Exception = " + e.getMessage());
		}
	}

	private JWT getJWT(String encodedJWT) {
		Verifier verifler = HMACVerifier.newVerifier(secretCode);
		return JWT.getDecoder().decode(encodedJWT, verifler);
	}

	public boolean validateToken(String encodedJWT) throws ApiErrorException {
		boolean result = false;
		try {
			JWT jwt = getJWT(encodedJWT);
			result = jwt.isExpired();
		} catch (Exception e) {
			result = true;
		}
		return result;
	}

}
