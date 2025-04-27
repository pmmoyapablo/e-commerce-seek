package com.vectora.transactionservice.infrastructure.adapter.out.persistence.repository;

import com.vectora.transactionservice.infrastructure.adapter.out.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository // Aunque JpaRepository ya es un bean, es buena práctica anotarlo
public interface SpringDataJpaTransactionRepository extends JpaRepository<TransactionEntity, Long> {
    // Spring Data JPA genera las implementaciones CRUD básicas
    // y métodos findBy...
    List<TransactionEntity> findByFromAccount(Long fromAccount);
}