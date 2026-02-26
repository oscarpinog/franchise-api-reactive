//package com.franchise.infrastructure.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.reactive.CorsWebFilter;
//import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
//
//import java.util.Arrays;
//import java.util.Collections;
//
//@Configuration
//public class CorsConfig {

//    @Bean
//    public CorsWebFilter corsWebFilter() {
//        CorsConfiguration corsConfig = new CorsConfiguration();
//        
//        // 1. Permitir orígenes (Local, Swagger y AWS)
//        corsConfig.setAllowedOriginPatterns(Arrays.asList(
//            "http://localhost:4200",
//            "https://*.awsapprunner.com",
//            "https://iz5f632zbj.us-east-2.awsapprunner.com",
//            "*" // Solo para pruebas, si sigue fallando usa "*"
//        ));
//        
//        // 2. Métodos permitidos
//        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
//        
//        // 3. Cabeceras (Swagger necesita estas para enviar el Token)
//        corsConfig.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "X-Requested-With", "Accept", "Origin"));
//        
//        // 4. Importante para Swagger: permitir que el navegador lea el Authorization Header
//        corsConfig.setExposedHeaders(Collections.singletonList("Authorization"));
//        
//        corsConfig.setAllowCredentials(true);
//        corsConfig.setMaxAge(3600L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", corsConfig);
//
//        return new CorsWebFilter(source);
//    }
//	@Bean
//	public CorsWebFilter corsWebFilter() {
//	    CorsConfiguration corsConfig = new CorsConfiguration();
//	    
//	    // Usa AllowedOriginPatterns para soportar comodines y credenciales al mismo tiempo
//	    corsConfig.setAllowedOriginPatterns(Arrays.asList(
//	        "http://localhost:4200",
//	        "https://*.awsapprunner.com"
//	    ));
//	    
//	    corsConfig.setAllowedMethods(Arrays.asList("*")); // Permite todos para descartar
//	    corsConfig.setAllowedHeaders(Arrays.asList("*")); // Permite todos para descartar
//	    corsConfig.setAllowCredentials(true);
//	    corsConfig.setExposedHeaders(Collections.singletonList("Authorization"));
//
//	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//	    source.registerCorsConfiguration("/**", corsConfig);
//	    return new CorsWebFilter(source);
//	}
//}