package com.example.demo;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
class JwtServerAuthenticationConverter implements ServerAuthenticationConverter {
	@Override
	public Mono<Authentication> convert(ServerWebExchange exchange) {
		return Mono.justOrEmpty(exchange).flatMap(it -> Mono.justOrEmpty(it.getRequest().getCookies().get("X-Auth")))
				.filter(it -> !it.isEmpty()).map(it -> it.get(0).getValue())
				.map(it -> new UsernamePasswordAuthenticationToken(it, it));
	}
}
