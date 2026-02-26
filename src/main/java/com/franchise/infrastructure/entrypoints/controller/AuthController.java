package com.franchise.infrastructure.entrypoints.controller;

import com.franchise.infrastructure.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final MapReactiveUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtUtil jwtUtil, MapReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, String>>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        return userDetailsService.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> ResponseEntity.ok(Map.of("token", jwtUtil.generateToken(username))))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }
}