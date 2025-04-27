package com.vectora.transactionservice.domain.port.out;

import com.vectora.transactionservice.domain.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);

    List<Transaction> getRecord(Long accountId);
}