package com.example.demo;

import java.security.KeyPair;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
class JwtSigner {
	KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);

	public String createJwt(String userId) {
		return Jwts.builder().signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256).setSubject(userId)
				.setIssuer("identity").setExpiration(Date.from(Instant.now().plus(Duration.ofMinutes(15))))
				.setIssuedAt(Date.from(Instant.now())).compact();
	}

	/**
	 * Validate the JWT where it will throw an exception if it isn't valid.
	 */
	public Jws<Claims> validateJwt(String jwt) {
		return Jwts.parserBuilder().setSigningKey(keyPair.getPublic()).build().parseClaimsJws(jwt);
	}
}