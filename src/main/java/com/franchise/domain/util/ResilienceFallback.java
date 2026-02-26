package com.franchise.domain.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Component
public class ResilienceFallback {

    private static final Logger log = LoggerFactory.getLogger(ResilienceFallback.class);

    
    

    public <T> Flux<T> handleFluxError(Throwable e) {
        // 1. Si es una excepción de "Not Found", la relanzamos sin tocarla
        // Esto permite que el GlobalExceptionHandler devuelva el 404 original
        if (e.getMessage() != null && e.getMessage().contains("no encontrada")) {
            return Flux.error(e); 
        }

        // 2. Si es un error técnico real (Timeout, DB caída), aplicamos resiliencia
        log.error(">> Error técnico detectado. Aplicando fallback de lista vacía: {}", e.getMessage());
        return Flux.empty(); 
    }

    public <T> Mono<T> handleGenericMonoError(Throwable e) {
        // 1. Logueamos el error real para saber qué pasó
        log.error(">> Analizando error en Fallback: {}", e.getMessage());

        // 2. Si el mensaje contiene "no existe" o "no encontrada", RELANZAR
        // Usamos minúsculas para que la comparación sea más segura
        String message = (e.getMessage() != null) ? e.getMessage().toLowerCase() : "";
        
        if (message.contains("no existe") || message.contains("no encontrada")) {
            return Mono.error(e); // Devolvemos el error original (404)
        }

        // 3. Si es por Rate Limit
        if (e instanceof io.github.resilience4j.ratelimiter.RequestNotPermitted) {
            return Mono.error(new RuntimeException("Límite de peticiones excedido. Intenta más tarde."));
        }

        // 4. SOLO si no es nada de lo anterior, devolvemos el error genérico 500
        log.error(">> Aplicando fallback genérico por fallo técnico");
        return Mono.error(new RuntimeException("El servicio no está disponible temporalmente."));
    }
    
    
}