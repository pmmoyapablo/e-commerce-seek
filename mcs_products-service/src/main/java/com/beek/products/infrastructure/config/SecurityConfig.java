package com.vectora.transactionservice.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilita @PreAuthorize
public class SecurityConfig {

        // Clave secreta para firmar los tokens (en producción debe ser más larga y
        // segura)
        @Value("${spring.security.oauth2.resourceserver.jwt.secret-key}")
        private String secretKey;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable) // Deshabilitar CSRF para APIs stateless
                                .authorizeHttpRequests(authz -> authz
                                                // Permite acceso público a endpoints de salud y generación de tokens
                                                .requestMatchers("/actuator/health", "/actuator/info",
                                                                "/token/generate")
                                                .permitAll()
                                                // Requiere autenticación para cualquier request a /accounts/**
                                                .requestMatchers("/transactions/**").authenticated()
                                                // Cualquier otra request debe ser autenticada (puedes ajustar esto)
                                                .anyRequest().authenticated())
                                // Configura el servidor de recursos OAuth2 para validar JWTs
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt.decoder(jwtDecoder())))
                                // Configura la política de sesión como STATELESS (no crear sesiones HTTP)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                return http.build();
        }

        @Bean
        public JwtDecoder jwtDecoder() {
                // Crear la clave secreta para HMAC
                SecretKey key = new SecretKeySpec(
                                secretKey.getBytes(StandardCharsets.UTF_8),
                                "HmacSHA256");

                // Crear el decodificador con la clave secreta
                return NimbusJwtDecoder.withSecretKey(key).build();
        }
}