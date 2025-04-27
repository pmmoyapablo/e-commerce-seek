package com.vectora.transactionservice.infrastructure.adapter.out.persistence;

import com.vectora.transactionservice.domain.model.Transaction;
import com.vectora.transactionservice.domain.port.out.TransactionRepositoryPort;
import com.vectora.transactionservice.infrastructure.adapter.out.persistence.entity.TransactionEntity;
import com.vectora.transactionservice.infrastructure.adapter.out.persistence.mapper.TransactionMapper;
import com.vectora.transactionservice.infrastructure.adapter.out.persistence.repository.SpringDataJpaTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component; // O @Repository

import java.util.ArrayList;
import java.util.List;

@Component // Adaptador es un componente Spring
@RequiredArgsConstructor
public class JpaTransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final SpringDataJpaTransactionRepository jpaRepository;
    private final TransactionMapper TransactionMapper; // Inyecta el mapper

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity transactionEntity = TransactionMapper.toEntity(transaction);
        TransactionEntity savedEntity = jpaRepository.save(transactionEntity);
        return TransactionMapper.toDomain(savedEntity);
    }

    @Override
    public List<Transaction> getRecord(Long accountId) {
        List<TransactionEntity> entities = jpaRepository.findByFromAccount(accountId);
        List<Transaction> transactions = new ArrayList<>();

        entities.forEach(entity -> {
            Transaction transaction = TransactionMapper.toDomain(entity);
            transactions.add(transaction);
        });
        return transactions;
    }
}