package com.vectora.bank.gateway.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vectora.bank.gateway.api.dto.ValidationRequest;
import com.vectora.bank.gateway.api.dto.ValidationResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class PreValidationGatewayFilterFactory
        extends AbstractGatewayFilterFactory<PreValidationGatewayFilterFactory.Config> {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    public PreValidationGatewayFilterFactory(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
        this.objectMapper = objectMapper;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // Solo procesar si es una solicitud POST
            if (!request.getMethod().equals(org.springframework.http.HttpMethod.POST)) {
                return chain.filter(exchange);
            }

            // Cachear el cuerpo de la solicitud
            return DataBufferUtils.join(request.getBody())
                    .flatMap(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        DataBufferUtils.release(dataBuffer);
                        String body = new String(bytes, StandardCharsets.UTF_8);
                        String token = request.getHeaders().getFirst("Authorization");

                        try {
                            // Validar el cuerpo
                            ValidationRequest validationRequest = objectMapper.readValue(body, ValidationRequest.class);
                            log.info("Validando solicitud: {}", validationRequest);

                            // Realizar la validación
                            return webClientBuilder.build()
                                    .post()
                                    .uri("http://account-service:8081" + config.getValidationEndpoint()) // config.getValidationServiceName()
                                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", token)
                                    .bodyValue(validationRequest)
                                    .retrieve()
                                    .bodyToMono(ValidationResponse.class)
                                    .flatMap(validationResponse -> {
                                        if (validationResponse.isValid()) {
                                            log.info("Validación exitosa");
                                            // Crear una nueva solicitud con el cuerpo original
                                            ServerHttpRequest newRequest = request.mutate()
                                                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                                    .header("Authorization", token)
                                                    .build();

                                            // Crear un nuevo exchange con la solicitud modificada
                                            return chain.filter(exchange.mutate().request(newRequest).build());
                                        } else {
                                            log.error("Validación fallida: {}");
                                            response.setStatusCode(HttpStatus.BAD_REQUEST);
                                            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                                            try {
                                                return response.writeWith(Mono.just(response.bufferFactory().wrap(
                                                        objectMapper.writeValueAsBytes(validationResponse))));
                                            } catch (JsonProcessingException e) {
                                                log.error("Error al serializar la respuesta: {}", e.getMessage());
                                                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                                                return response.setComplete();
                                            }
                                        }
                                    });
                        } catch (Exception e) {
                            log.error("Error al procesar la solicitud: {}", e.getMessage());
                            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                            return response.setComplete();
                        }
                    });
        };
    }

    @Data
    public static class Config {
        private String validationServiceName;
        private String validationEndpoint;
    }
}