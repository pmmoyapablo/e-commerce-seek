package com.vectora.transactionservice.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AccountValidateRequest {
    @NotNull(message = "FromAccount cannot be null")
    private Long fromAccount;

    @NotNull(message = "ToAccount cannot be null")
    private Long toAccount;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private Double monto; // Usar Double (objeto) para permitir validación @NotNull
}
