package com.example.demo;

import java.util.Arrays;

import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
class JwtAuthenticationManager implements ReactiveAuthenticationManager {
	private JwtSigner jwtSigner;

	public JwtAuthenticationManager(JwtSigner jwtSigner) {
		this.jwtSigner = jwtSigner;
	}

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		return Mono.just(authentication).map(auth -> {
			var credentials = auth.getCredentials();

			var token = credentials != null ? credentials.toString() : null;
			if (token == null) {
				return new UsernamePasswordAuthenticationToken(null, null);

			}
			var jws = jwtSigner.validateJwt(token);
			auth = new UsernamePasswordAuthenticationToken(jws.getBody().getSubject(), token );
			return auth;
		}).onErrorResume(ex -> {
			//LoggerFactory.getLogger(getClass()).error("Fail to autenticate", ex);
			return Mono.just(new UsernamePasswordAuthenticationToken(null, null));
		});

	}
}