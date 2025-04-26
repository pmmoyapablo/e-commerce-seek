package com.vectora.bank.gateway.api.dto;

import lombok.Data;

@Data
public class ValidationRequest {
    private Long fromAccount;
    private Long toAccount;
    private Double monto;
}