package com.franchise.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationManagerTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private JwtAuthenticationManager authenticationManager;

    @Test
    @DisplayName("Debe retornar Mono.empty() cuando el token es inválido")
    void authenticateInvalidToken() {
        // GIVEN
        String token = "invalid.token";
        Authentication authRequest = new UsernamePasswordAuthenticationToken(token, token);

        Mockito.when(jwtUtil.extractUsername(token)).thenReturn("user");
        Mockito.when(jwtUtil.validateToken(token)).thenReturn(false);

        // WHEN
        var resultMono = authenticationManager.authenticate(authRequest);

        // THEN
        StepVerifier.create(resultMono)
                .verifyComplete(); // StepVerifier confirma que el flujo terminó sin emitir nada (Mono.empty)
    }

    @Test
    @DisplayName("Debe retornar Mono.empty() cuando ocurre una excepción en la validación")
    void authenticateWithException() {
        // GIVEN
        String token = "error.token";
        Authentication authRequest = new UsernamePasswordAuthenticationToken(token, token);

        Mockito.when(jwtUtil.extractUsername(anyString())).thenThrow(new RuntimeException("JWT expired"));

        // WHEN
        var resultMono = authenticationManager.authenticate(authRequest);

        // THEN
        StepVerifier.create(resultMono)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar Mono.empty() si el username extraído es null")
    void authenticateNullUsername() {
        // GIVEN
        String token = "token.null.user";
        Authentication authRequest = new UsernamePasswordAuthenticationToken(token, token);

        Mockito.when(jwtUtil.extractUsername(token)).thenReturn(null);

        // WHEN
        var resultMono = authenticationManager.authenticate(authRequest);

        // THEN
        StepVerifier.create(resultMono)
                .verifyComplete();
    }
}