package com.vectora.transactionservice.infrastructure.adapter.out.persistence;

import com.vectora.transactionservice.domain.model.Transaction;
import com.vectora.transactionservice.infrastructure.adapter.out.persistence.mapper.TransactionMapper;
import com.vectora.transactionservice.infrastructure.adapter.out.persistence.repository.SpringDataJpaTransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class JpaTransactionRepositoryAdapterTest {

    @Autowired
    private SpringDataJpaTransactionRepository jpaRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    private JpaTransactionRepositoryAdapter adapter;

    @Test
    void testSaveTransaction() {
        // Arrange
        adapter = new JpaTransactionRepositoryAdapter(jpaRepository, transactionMapper);
        Transaction transaction = new Transaction(1L, 2L, 100.0);

        // Act
        Transaction savedTransaction = adapter.save(transaction);

        // Assert
        assertNotNull(savedTransaction);
        assertNotNull(savedTransaction.getId());
        assertEquals(transaction.getFromAccount(), savedTransaction.getFromAccount());
        assertEquals(transaction.getToAccount(), savedTransaction.getToAccount());
        assertEquals(transaction.getMonto(), savedTransaction.getMonto());
    }

    @Test
    void testGetTransactionsByAccount() {
        // Arrange
        adapter = new JpaTransactionRepositoryAdapter(jpaRepository, transactionMapper);
        Long accountId = 1L;

        // Save some test transactions
        adapter.save(new Transaction(accountId, 2L, 100.0));
        adapter.save(new Transaction(accountId, 3L, 200.0));
        adapter.save(new Transaction(4L, accountId, 300.0)); // This one shouldn't be returned

        // Act
        List<Transaction> transactions = adapter.getRecord(accountId);

        // Assert
        assertNotNull(transactions);
        assertEquals(2, transactions.size());
        assertTrue(transactions.stream().allMatch(t -> t.getFromAccount().equals(accountId)));
    }
}