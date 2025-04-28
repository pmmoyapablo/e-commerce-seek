package com.vectora.transactionservice.application.service;

import com.vectora.transactionservice.domain.model.Transaction;
import com.vectora.transactionservice.domain.port.out.TransactionRepositoryPort;
import com.vectora.transactionservice.infrastructure.adapter.in.web.dto.TransactionResponse;
import com.vectora.transactionservice.infrastructure.adapter.in.web.dto.TransactionsRecordResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private UserRepositoryPort transactionRepositoryPort;

    @InjectMocks
    private UserServiceImpl transactionService;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = new Transaction(1L, 2L, 100.0);
    }

    @Test
    void testCreateTransaction() {
        // Arrange
        when(transactionRepositoryPort.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        TransactionResponse result = transactionService.createTransaction(transaction.getFromAccount(),
                transaction.getToAccount(), transaction.getMonto());

        // Assert
        assertNotNull(result);
        assertEquals("success", result.getStatus());
        verify(transactionRepositoryPort, times(1)).save(any(Transaction.class));
    }

    @Test
    void testGetTransactionsByAccount() {
        // Arrange
        Long accountId = 1L;
        List<Transaction> expectedTransactions = Arrays.asList(
                new Transaction(accountId, 2L, 100.0),
                new Transaction(accountId, 3L, 200.0));
        when(transactionRepositoryPort.getRecord(accountId)).thenReturn(expectedTransactions);

        // Act
        TransactionsRecordResponse result = transactionService.getTransactions(accountId);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getTransactions());
        assertEquals(expectedTransactions.size(), result.getTransactions().size());
        verify(transactionRepositoryPort, times(1)).getRecord(accountId);
    }
}