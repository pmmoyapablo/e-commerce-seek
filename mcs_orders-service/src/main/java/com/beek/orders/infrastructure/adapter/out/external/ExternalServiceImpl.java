package com.vectora.transactionservice.infrastructure.adapter.out.external;

import com.vectora.transactionservice.application.port.out.external.ExternalService;
import com.vectora.transactionservice.infrastructure.adapter.in.web.dto.AccountValidateRequest;
import com.vectora.transactionservice.infrastructure.adapter.in.web.dto.AccountValidationResponse;
import com.vectora.transactionservice.infrastructure.adapter.in.web.dto.TokenRequest;
import com.vectora.transactionservice.infrastructure.adapter.in.web.dto.TokenResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExternalServiceImpl implements ExternalService {
    private TestRestTemplate restTemplate;

    @Value("${server.external.accountUrl}")
    public String accoutPath;

    @Override
    public boolean validateAccount(Long accountId) {
        restTemplate = new TestRestTemplate();
        HttpHeaders headersAnonymous = createHeadersWithBearerToken("");
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setUsername("test-user");
        tokenRequest.setPassword("1234abcd");
        HttpEntity<TokenRequest> createToken = new HttpEntity<>(tokenRequest, headersAnonymous);
        ResponseEntity<TokenResponse> createTokenResponse = restTemplate.postForEntity(
                accoutPath + "/account-service/token/generate",
                createToken,
                TokenResponse.class);
        // test.
        String fakeJwtToken = createTokenResponse.getBody().getToken(); // <-- NECESITAS UN

        HttpHeaders headers = createHeadersWithBearerToken(fakeJwtToken);

        // --- Validar Cuenta ---
        AccountValidateRequest createRequest = new AccountValidateRequest();
        createRequest.setToAccount(accountId);
        createRequest.setFromAccount(1L);
        createRequest.setMonto(10.0);

        HttpEntity<AccountValidateRequest> validateEntity = new HttpEntity<>(createRequest, headers);

        ResponseEntity<AccountValidationResponse> validateResponse = restTemplate.postForEntity(
                accoutPath + "/account-service/accounts/validate",
                validateEntity,
                AccountValidationResponse.class);

        return validateResponse.getBody().isValid();
    }

    private HttpHeaders createHeadersWithBearerToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null && !token.isEmpty()) {
            headers.setBearerAuth(token);
        }
        return headers;
    }
}