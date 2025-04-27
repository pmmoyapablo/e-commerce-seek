package com.vectora.transactionservice.domain.model;

import com.vectora.transactionservice.domain.exception.TransactionNotFoundException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void testCreateTransaction() {
        // Arrange
        Long fromAccount = 1L;
        Long toAccount = 2L;
        double amount = 100.0;

        // Act
        Transaction transaction = new Transaction(fromAccount, toAccount, amount);

        // Assert
        assertNotNull(transaction);
        assertEquals(fromAccount, transaction.getFromAccount());
        assertEquals(toAccount, transaction.getToAccount());
        assertEquals(amount, transaction.getMonto());
    }

    @Test
    void testTransactionWithNegativeAmount() {
        // Arrange
        Long fromAccount = 1L;
        Long toAccount = 2L;
        double negativeAmount = -100.0;

        // Act
        Transaction transaction = new Transaction(fromAccount, toAccount, negativeAmount);

        // Assert
        assertFalse(transaction.validateAmount());
    }

    @Test
    void testTransactionWithSameAccounts() {
        // Arrange
        Long sameAccount = 1L;
        double amount = 100.0;

        // Act & Assert
        assertThrows(TransactionNotFoundException.class, () -> {
            Transaction transaction = new Transaction(sameAccount, sameAccount, amount);
            transaction.validateAccounts();
        });
    }
}