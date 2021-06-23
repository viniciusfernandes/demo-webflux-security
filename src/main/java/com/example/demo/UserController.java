package com.example.demo;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
class UserController {

	@Autowired
	private JwtSigner jwtSigner;
	private static Map<String, UserCredential> users = new HashMap<>();
	static {
		users.put("vinicius", new UserCredential("vinicius", "1234"));
	}

	@PutMapping("/signup")
	public Mono<ResponseEntity<Void>> signUp(@RequestBody UserCredential user) {
		users.put(user.getEmail(), user);

		return Mono.just(ResponseEntity.noContent().build());
	}

	@PostMapping("/login")
	public Mono<ResponseEntity<Object>> login(@RequestBody UserCredential user) {

		return Mono.justOrEmpty(users.get(user.getEmail())).filter(it -> it.getPassword().equals(user.getPassword()))
				.map(it -> {
					var jwt = jwtSigner.createJwt(it.getEmail());
					var cookie = ResponseCookie.fromClientResponse("X-Auth", jwt).maxAge(3600).httpOnly(true).path("/")
							.secure(false) // should be true in production
							.build();
					return ResponseEntity.noContent().header("Set-Cookie", cookie.toString()).build();
				}).switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(user)));
	}

	@GetMapping("/myself")
	public Mono<ResponseEntity<User>> getMyself(Principal principal) {

		return Mono.justOrEmpty(users.get(principal.getName())).map(it -> ResponseEntity.ok(new User(it.getEmail())))
				.switchIfEmpty(
						Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new User(principal.getName()))));
	}

}