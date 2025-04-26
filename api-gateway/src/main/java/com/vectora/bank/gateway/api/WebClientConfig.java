package com.vectora.bank.gateway.api;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced // ¡Importante! Habilita el balanceo de carga con Eureka/Consul/etc.
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}
