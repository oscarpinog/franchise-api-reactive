package com.franchise.infrastructure.entrypoints.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.Set;

import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }
    
    @Test
    @DisplayName("Debe manejar WebExchangeBindException (errores de @Valid) con detalles de campos")
    void handleWebExchangeBindExceptionTest() throws Exception {
        // 1. Necesitamos un MethodParameter para el constructor de la excepción
        Method method = this.getClass().getMethods()[0];
        MethodParameter parameter = new MethodParameter(method, -1);

        // 2. Creamos un BindingResult con un error de campo (ej. el campo "name" está vacío)
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "product");
        bindingResult.addError(new FieldError("product", "name", "El nombre es obligatorio"));

        // 3. Instanciamos la excepción
        WebExchangeBindException ex = new WebExchangeBindException(parameter, bindingResult);

        // 4. Ejecutamos el handler
        Mono<ResponseEntity<Map<String, Object>>> responseMono = handler.handleValidationErrors(ex);

        // 5. Verificamos con StepVerifier
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(response.getBody()).containsEntry("error", "Validación Fallida");
                    
                    // Verificamos que los detalles contengan el error del campo "name"
                    Map<String, String> details = (Map<String, String>) response.getBody().get("details");
                    assertThat(details).containsEntry("name", "El nombre es obligatorio");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar IllegalArgumentException como 404 NOT FOUND")
    void handleIllegalArgumentTest() {
        IllegalArgumentException ex = new IllegalArgumentException("Recurso no existe");

        Mono<ResponseEntity<Map<String, Object>>> responseMono = handler.handleIllegalArgument(ex);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(response.getBody()).containsEntry("error", "No encontrado");
                    assertThat(response.getBody()).containsEntry("message", "Recurso no existe");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar RuntimeException como 500 INTERNAL SERVER ERROR")
    void handleRuntimeExceptionTest() {
        RuntimeException ex = new RuntimeException("Error de lógica");

        Mono<ResponseEntity<Map<String, Object>>> responseMono = handler.handleRuntimeException(ex);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                    assertThat(response.getBody()).containsEntry("error", "Error Interno del Servidor");
                    assertThat(response.getBody().get("message")).toString().contains("error inesperado");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar ConstraintViolationException como 400 BAD REQUEST")
    void handleConstraintViolationTest() {
        // Mockeamos la violación de restricción de Jakarta
        ConstraintViolation<?> violation = Mockito.mock(ConstraintViolation.class);
        Mockito.when(violation.getMessage()).thenReturn("El nombre es obligatorio");
        
        ConstraintViolationException ex = new ConstraintViolationException("Error", Set.of(violation));

        ResponseEntity<Map<String, Object>> response = handler.handleConstraintViolationException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "Validación Fallida");
        assertThat(response.getBody()).containsEntry("message", "El nombre es obligatorio");
    }

    @Test
    @DisplayName("Debe manejar Exception genérica como 500 Error Crítico")
    void handleAllExceptionsTest() {
        Exception ex = new Exception("Fallo de sistema");

        Mono<ResponseEntity<Map<String, Object>>> responseMono = handler.handleAllExceptions(ex);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                    assertThat(response.getBody()).containsEntry("error", "Error Crítico");
                })
                .verifyComplete();
    }
}