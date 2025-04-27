package com.vectora.transactionservice.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "transactions", schema = "transaction_db") // Nombre de la tabla en la BD
@Data // Lombok
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transactions_seq")
    @SequenceGenerator(name = "transactions_seq", sequenceName = "transaction_db.transactions_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, name = "from_account") // VARCHAR(100), no nulo
    private Long fromAccount;

    @Column(nullable = false, name = "to_account") // VARCHAR(100), no nulo
    private Long toAccount;

    @Column(nullable = false)
    private double monto;

    @Column(nullable = false)
    private Date fecha;
}