package com.seek.products.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import com.vectora.transactionservice.domain.exception.TransactionNotFoundException;

@Data // Lombok: getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;
    private Long fromAccount;
    private Long toAccount;
    private double monto;
    private Date fecha;

    // Constructor sin ID para creación
    public Product(Long fromAccount, Long toAccount, double monto) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.monto = monto;
        this.fecha = new Date();
    }

    public boolean validateAmount() {
        return this.monto > 0;
    }

    public void validateAccounts() {
        if (!(this.fromAccount != null && this.toAccount != null)) {
            throw new TransactionNotFoundException("Accounts are required");
        }

        if (this.fromAccount.equals(this.toAccount)) {
            throw new TransactionNotFoundException("Accounts cannot be the same");
        }
    }
}