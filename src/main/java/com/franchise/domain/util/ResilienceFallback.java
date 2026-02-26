package com.franchise.domain.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Component
public class ResilienceFallback {

    private static final Logger log = LoggerFactory.getLogger(ResilienceFallback.class);

    // Fallback genérico para Mono de cualquier objeto
    public <T> Mono<T> handleGenericMonoError(Throwable e) {
        log.error(">> Circuit Breaker/Retry activado. Error: {}", e.getMessage());
        return Mono.error(new RuntimeException("El servicio no está disponible temporalmente. Intente más tarde."));
    }

    // Fallback específico para flujos de listas (Flux)
    public <T> Flux<T> handleFluxError(Throwable e) {
        log.error(">> Circuit Breaker activado en reporte/listado: {}", e.getMessage());
        return Flux.empty(); // En reportes es mejor devolver vacío que romper la app
    }
}