package com.franchise.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String testSecret = "mi_clave_secreta_de_prueba_con_mas_de_32_caracteres_longitud";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Inyectamos manualmente el valor que Spring pondría con @Value
        ReflectionTestUtils.setField(jwtUtil, "secret", testSecret);
        // Ejecutamos la inicialización de la llave HMAC
        jwtUtil.init();
    }

    @Test
    @DisplayName("Debe generar un token válido y extraer el username correctamente")
    void generateAndExtractTokenTest() {
        String username = "oscar.dev";
        
        String token = jwtUtil.generateToken(username);
        
        assertThat(token).isNotNull();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo(username);
    }

    @Test
    @DisplayName("Debe validar correctamente un token legítimo")
    void validateTokenSuccess() {
        String token = jwtUtil.generateToken("admin");
        
        Boolean isValid = jwtUtil.validateToken(token);
        
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Debe retornar false al validar un token mal formado o alterado")
    void validateTokenFailure() {
        String token = jwtUtil.generateToken("admin");
        String tamperedToken = token + "modified";
        
        Boolean isValid = jwtUtil.validateToken(tamperedToken);
        
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Debe retornar false si el token es nulo o vacío")
    void validateEmptyToken() {
        assertThat(jwtUtil.validateToken("")).isFalse();
        assertThat(jwtUtil.validateToken(null)).isFalse();
    }
}