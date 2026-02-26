package com.franchise.infrastructure.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationManager(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        
        try {
            String username = jwtUtil.extractUsername(token);
            if (username != null && jwtUtil.validateToken(token)) {
                // Por simplicidad, asignamos ROLE_ADMIN a los tokens válidos
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );
                return Mono.just(auth);
            }
            return Mono.empty();
        } catch (Exception e) {
            return Mono.empty();
        }
    }
}