package com.seek.users.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import com.vectora.transactionservice.domain.exception.UserNotFoundException;

@Data // Lombok: getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private Long fromAccount;
    private Long toAccount;
    private double monto;
    private Date fecha;

    // Constructor sin ID para creación
    public User(Long fromAccount, Long toAccount, double monto) {
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
            throw new UserNotFoundException("Accounts are required");
        }

        if (this.fromAccount.equals(this.toAccount)) {
            throw new UserNotFoundException("Accounts cannot be the same");
        }
    }
}