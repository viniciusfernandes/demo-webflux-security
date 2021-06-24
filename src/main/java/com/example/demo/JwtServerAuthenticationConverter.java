package com.example.demo;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
class JwtServerAuthenticationConverter implements ServerAuthenticationConverter {
	private static final String AUTH_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	@Override
	public Mono<Authentication> convert(ServerWebExchange exchange) {
		return Mono.justOrEmpty(exchange).map(it -> {

			var headers = exchange.getRequest().getHeaders().get(AUTH_HEADER);
			String jwt = null;
			if (headers != null && !headers.isEmpty()) {
				jwt = headers.get(0);
			}
			if (jwt != null && jwt.startsWith(BEARER_PREFIX)) {
				jwt = jwt.substring(7);
			}

			return new UsernamePasswordAuthenticationToken(jwt, jwt);
		});
	}
}
