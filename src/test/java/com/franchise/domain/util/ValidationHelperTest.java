package com.franchise.domain.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidationHelperTest {

    @Test
    @DisplayName("requireNotNull debe lanzar IllegalArgumentException cuando el objeto es nulo")
    void requireNotNullThrowsException() {
        String argName = "productName";
        
        assertThatThrownBy(() -> ValidationHelper.requireNotNull(null, argName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(argName);
    }

    @Test
    @DisplayName("requireNotNull no debe hacer nada cuando el objeto es válido")
    void requireNotNullDoesNothing() {
        // No debe lanzar excepción
        ValidationHelper.requireNotNull("Valid Object", "testField");
    }

    @Test
    @DisplayName("onErrorNotFound debe emitir un Mono con IllegalArgumentException")
    void onErrorNotFoundEmitsError() {
        Long id = 123L;
        String template = "No se encontró el recurso con ID: %d";

        var errorMono = ValidationHelper.onErrorNotFound(id, template);

        StepVerifier.create(errorMono)
                .expectErrorMatches(throwable -> 
                        throwable instanceof IllegalArgumentException && 
                        throwable.getMessage().equals("No se encontró el recurso con ID: 123")
                )
                .verify();
    }
}