package com.franchise.infrastructure.entrypoints.controller;

import com.franchise.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Excluimos la auto-configuración de seguridad para que el test no sea interceptado 
 * por los filtros de JWT y nos devuelva 401 antes de entrar al método.
 */
@WebFluxTest(controllers = AuthController.class, excludeAutoConfiguration = {
    ReactiveSecurityAutoConfiguration.class,
    ReactiveUserDetailsServiceAutoConfiguration.class
})
public class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private MapReactiveUserDetailsService userDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Debe retornar token 200 OK cuando las credenciales coinciden")
    void loginSuccess() {
        // Datos de prueba
        String username = "admin2";
        String rawPassword = "admin";
        String encodedPassword = "password_encriptado";
        String mockToken = "token.jwt.generado";

        UserDetails userMock = User.withUsername(username)
                .password(encodedPassword)
                .roles("ADMIN")
                .build();

        // Configuración de Mocks
        Mockito.when(userDetailsService.findByUsername(username))
                .thenReturn(Mono.just(userMock));
        
        Mockito.when(passwordEncoder.matches(eq(rawPassword), eq(encodedPassword)))
                .thenReturn(true);
        
        Mockito.when(jwtUtil.generateToken(username))
                .thenReturn(mockToken);

        // Ejecución
        webTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("username", username, "password", rawPassword))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token").isEqualTo(mockToken);
    }

    @Test
    @DisplayName("Debe retornar 401 UNAUTHORIZED cuando la contraseña es incorrecta")
    void loginWrongPassword() {
        String username = "admin";
        String rawPassword = "wrongPassword";
        String encodedPassword = "password_encriptado";

        UserDetails userMock = User.withUsername(username)
                .password(encodedPassword)
                .roles("ADMIN")
                .build();

        Mockito.when(userDetailsService.findByUsername(username))
                .thenReturn(Mono.just(userMock));
        
        // Simulamos que la contraseña no coincide
        Mockito.when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(false);

        webTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("username", username, "password", rawPassword))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("Debe retornar 401 UNAUTHORIZED cuando el usuario no existe")
    void loginUserNotFound() {
        Mockito.when(userDetailsService.findByUsername(anyString()))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("username", "desconocido", "password", "123"))
                .exchange()
                .expectStatus().isUnauthorized();
    }
}