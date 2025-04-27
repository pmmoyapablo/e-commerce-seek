package com.vectora.transactionservice.infrastructure.adapter.out.persistence.mapper;

import com.vectora.transactionservice.domain.model.Transaction;
import com.vectora.transactionservice.infrastructure.adapter.out.persistence.entity.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionEntity toEntity(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        TransactionEntity entity = new TransactionEntity();
        if (transaction.getId() != null) {
            entity.setId(transaction.getId());
        }
        entity.setFromAccount(transaction.getFromAccount());
        entity.setToAccount(transaction.getToAccount());
        entity.setMonto(transaction.getMonto());
        entity.setFecha(transaction.getFecha());

        return entity;
    }

    public Transaction toDomain(TransactionEntity entity) {
        if (entity == null) {
            return null;
        }

        Transaction transaction = new Transaction(
                entity.getFromAccount(),
                entity.getToAccount(),
                entity.getMonto());
        transaction.setId(entity.getId());
        transaction.setFecha(entity.getFecha());

        return transaction;
    }
}