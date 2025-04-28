package com.seek.users;

import com.seek.users.infrastructure.adapter.in.web.dto.TransactionCreateRequest;
import com.seek.users.infrastructure.adapter.in.web.dto.TransactionResponse;
import com.seek.users.infrastructure.adapter.in.web.dto.TransactionsRecordResponse;
import com.seek.users.infrastructure.adapter.in.web.dto.TokenRequest;
import com.seek.users.infrastructure.adapter.in.web.dto.TokenResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate; // Cliente REST para tests de integración
import org.springframework.boot.test.web.server.LocalServerPort; // Puerto aleatorio asignado
import org.springframework.http.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // Levanta el contexto completo en puerto

public class UserServiceApplicationTests {

    @LocalServerPort
    private int port;

    @Value("${server.servlet.context-path}")
    public String contextPath;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    public String urlAuth;

    @Autowired
    private TestRestTemplate restTemplate; // Cliente HTTP para interactuar con el servicio

    // NOTA: Para que este test funcione con seguridad JWT, necesitarías:
    // 1. Un servidor de autorización simulado (o real pero accesible) que emita
    // tokens válidos.
    // 2. Obtener un token válido antes de ejecutar las pruebas.
    // 3. Añadir el token como encabezado "Authorization: Bearer <token>" en las
    // peticiones.
    // Simplificaremos aquí asumiendo que la seguridad está desactivada o
    // configurada para permitir
    // estas rutas en un perfil de test específico, o que puedes obtener un token
    // fácilmente.

    // Método helper para crear encabezados con un token JWT (debes obtener este
    // token de alguna forma)
    private HttpHeaders createHeadersWithBearerToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null && !token.isEmpty()) {
            headers.setBearerAuth(token);
        }
        return headers;
    }

    @Test
    void createAndGetTransaction_IntegrationTest() {
        // --- Obtención de Token
        HttpHeaders headersAnonymous = createHeadersWithBearerToken("");
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setUsername("test-user");
        tokenRequest.setPassword("1234abcd");
        HttpEntity<TokenRequest> createToken = new HttpEntity<>(tokenRequest, headersAnonymous);
        ResponseEntity<TokenResponse> createTokenResponse = restTemplate.postForEntity(
                urlAuth,
                createToken,
                TokenResponse.class);
        // test.
        String fakeJwtToken = createTokenResponse.getBody().getToken(); // <-- NECESITAS UN
                                                                        // TOKEN VÁLIDO AQUÍ
        HttpHeaders headers = createHeadersWithBearerToken(fakeJwtToken);

        // --- Crear Cuenta ---
        UserCreateRequest createRequest = new UserCreateRequest();
        createRequest.setFromAccount(1L);
        createRequest.setFromAccount(2L);
        createRequest.setMonto(500.25);

        HttpEntity<UserCreateRequest> createEntity = new HttpEntity<>(createRequest, headers);

        ResponseEntity<TransactionResponse> createResponse = restTemplate.postForEntity(
                "http://localhost:" + port + contextPath + "/transactions",
                createEntity,
                TransactionResponse.class);

        // Verificar Creación
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getTransactionId());
        assertEquals("success", createResponse.getBody().getStatus());

        Long accountToId = createRequest.getToAccount();

        // --- Consultar Historial de Transacciones ---
        HttpEntity<Void> getEntity = new HttpEntity<>(headers); // Solo necesitamos los headers para GET

        ResponseEntity<TransactionsRecordResponse> getResponse = restTemplate.exchange(
                "http://localhost:" + port + contextPath + "/transactions/" + accountToId,
                HttpMethod.GET,
                getEntity,
                TransactionsRecordResponse.class);

        // Verificar Consulta
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertNotNull(getResponse.getBody().getTransactions());

    }
}
