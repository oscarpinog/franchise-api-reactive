package com.franchise.infrastructure.entrypoints.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import jakarta.validation.ConstraintViolationException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralizador de excepciones para la arquitectura reactiva.
 * Captura errores en cualquier punto del flujo y los devuelve como JSON.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja errores de validación (ej. campos obligatorios vacíos o formatos inválidos).
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidationErrors(WebExchangeBindException ex) {
        log.warn(">> Error de validación detectado: {} errores encontrados", ex.getBindingResult().getErrorCount());
        
        Map<String, Object> response = new HashMap<>();
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Dato inválido"
                ));

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validación Fallida");
        response.put("details", errors);
        
        return Mono.just(ResponseEntity.badRequest().body(response));
    }

    /**
     * Maneja errores cuando no se encuentra un recurso o los datos son inválidos (404).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleIllegalArgument(IllegalArgumentException ex) {
        log.info(">> Recurso no encontrado o solicitud inválida: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "No encontrado");
        response.put("message", ex.getMessage());
        
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(response));
    }

    /**
     * Maneja cualquier error de ejecución (500).
     */
    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleRuntimeException(RuntimeException ex) {
        log.error("!! Excepción de tiempo de ejecución: ", ex);
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Error Interno del Servidor");
        response.put("message", "Ocurrió un error inesperado al procesar la solicitud.");
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }

    /**
     * Captura cualquier otra excepción no controlada específicamente.
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleAllExceptions(Exception ex) {
        log.error("!! Error crítico no controlado: ", ex);
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Error Crítico");
        response.put("message", "Fallo general en el sistema.");
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validación Fallida");
        
        // Extraemos el mensaje de la validación
        String message = ex.getConstraintViolations().iterator().next().getMessage();
        body.put("message", message);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}