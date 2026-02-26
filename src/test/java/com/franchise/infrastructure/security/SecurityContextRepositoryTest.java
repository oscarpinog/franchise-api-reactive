package com.franchise.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class SecurityContextRepositoryTest {

    @Mock
    private JwtAuthenticationManager authenticationManager;

    @InjectMocks
    private SecurityContextRepository securityContextRepository;

    @Test
    @DisplayName("Debe cargar el contexto de seguridad cuando el header Authorization es válido")
    void loadContextSuccess() {
        // GIVEN
        String token = "valid.token.here";
        // Simulamos un request con Header: Authorization: Bearer <token>
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .build()
        );

        Authentication mockAuth = new UsernamePasswordAuthenticationToken("admin", null);
        Mockito.when(authenticationManager.authenticate(any())).thenReturn(Mono.just(mockAuth));

        // WHEN
        Mono<SecurityContext> result = securityContextRepository.load(exchange);

        // THEN
        StepVerifier.create(result)
                .assertNext(context -> {
                    assertThat(context.getAuthentication()).isEqualTo(mockAuth);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar Mono.empty() cuando no hay header de Authorization")
    void loadContextNoHeader() {
        // GIVEN: Request sin headers
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").build());

        // WHEN
        Mono<SecurityContext> result = securityContextRepository.load(exchange);

        // THEN
        StepVerifier.create(result)
                .verifyComplete(); // No debe emitir nada
    }

    @Test
    @DisplayName("Debe retornar Mono.empty() cuando el header no empieza con 'Bearer '")
    void loadContextInvalidHeaderFormat() {
        // GIVEN: Header mal formado (sin "Bearer ")
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/")
                        .header(HttpHeaders.AUTHORIZATION, "Basic user:pass")
                        .build()
        );

        // WHEN
        Mono<SecurityContext> result = securityContextRepository.load(exchange);

        // THEN
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("El método save debe lanzar UnsupportedOperationException")
    void saveMethodThrowsException() {
        StepVerifier.create(securityContextRepository.save(null, null))
                .expectError(UnsupportedOperationException.class)
                .verify();
    }
}