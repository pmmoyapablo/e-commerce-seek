package com.vectora.transactionservice.infrastructure.adapter.in.web;

import com.vectora.transactionservice.application.port.in.TransactionUseCase;
import com.vectora.transactionservice.infrastructure.adapter.in.web.dto.TransactionCreateRequest;
import com.vectora.transactionservice.infrastructure.adapter.in.web.dto.TransactionResponse;
import com.vectora.transactionservice.infrastructure.adapter.in.web.dto.TransactionsRecordResponse;
import com.vectora.transactionservice.infrastructure.adapter.out.persistence.mapper.TransactionMapper; // Usamos el mapper
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Opcional para seguridad a nivel de método
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions") // Ruta base para las cuentas
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionUseCase transactionUseCase; // Inyecta el Caso de Uso (Input Port)
    private final TransactionMapper transactionMapper; // Inyecta el Mapper para DTOs

    @PostMapping
    // @PreAuthorize("hasAuthority('SCOPE_Transactions:write')") // Ejemplo:
    // Requiere scope JWT específico
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionCreateRequest request) {
        TransactionResponse response = transactionUseCase.createTransaction(request.getFromAccount(),
                request.getToAccount(), request.getMonto());
        // Devuelve 201 Created con la cuenta creada en el body
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{accountId}")
    // @PreAuthorize("hasAuthority('SCOPE_Transactions:read')") // Ejemplo: Requiere
    // scope JWT específico
    public ResponseEntity<TransactionsRecordResponse> getTransactionsRecord(@PathVariable Long accountId) {
        TransactionsRecordResponse response = transactionUseCase.getTransactions(accountId); // Lanza //
                                                                                             // TransactionNotFoundException
        return ResponseEntity.ok(response);
    }
}