package com.seek.users.domain.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super("Transaction not found: " + message);
    }

    public UserNotFoundException(Long accountId) {
        super("Transactions not recorded for account: " + accountId);
    }
}