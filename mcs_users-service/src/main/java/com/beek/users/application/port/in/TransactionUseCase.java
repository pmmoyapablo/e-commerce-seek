package com.vectora.transactionservice.application.port.in;

import com.vectora.transactionservice.infrastructure.adapter.in.web.dto.TransactionResponse;
import com.vectora.transactionservice.infrastructure.adapter.in.web.dto.TransactionsRecordResponse;

public interface TransactionUseCase {
    TransactionResponse createTransaction(Long fromAccount, Long toAccount, double amount);

    TransactionsRecordResponse getTransactions(Long accountId);
}