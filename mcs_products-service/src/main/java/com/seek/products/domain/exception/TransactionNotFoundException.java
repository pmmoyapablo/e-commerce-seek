package com.vectora.transactionservice.domain.exception;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(String message) {
        super("Transaction not found: " + message);
    }

    public TransactionNotFoundException(Long accountId) {
        super("Transactions not recorded for account: " + accountId);
    }
}