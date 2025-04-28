package com.seek.users.infrastructure.adapter.out.persistence;

import com.seek.users.domain.model.Transaction;
import com.seek.users.domain.port.out.TransactionRepositoryPort;
import com.seek.users.infrastructure.adapter.out.persistence.entity.TransactionEntity;
import com.seek.users.infrastructure.adapter.out.persistence.mapper.TransactionMapper;
import com.seek.users.infrastructure.adapter.out.persistence.repository.SpringDataJpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component; // O @Repository

import java.util.ArrayList;
import java.util.List;

@Component // Adaptador es un componente Spring
@RequiredArgsConstructor
public class JpaUserRepositoryAdapter implements UserRepositoryPort {

    private final SpringDataJpaUserRepository jpaRepository;
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