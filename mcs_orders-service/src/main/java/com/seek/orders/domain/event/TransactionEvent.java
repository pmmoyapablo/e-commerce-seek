package com.vectora.transactionservice.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.io.Serializable;
import java.lang.String;
import com.vectora.transactionservice.domain.model.Transaction;

@Getter
@AllArgsConstructor
public class TransactionEvent implements Serializable {

    public TransactionEvent() {
    }

    private Long transactionId;
    private Long fromAccount;
    private Long toAccount;
    private double amount;
    private String status;

    public void setData(Transaction transaction, String status) {
        this.transactionId = transaction.getId();
        this.fromAccount = transaction.getFromAccount();
        this.toAccount = transaction.getToAccount();
        this.amount = transaction.getMonto();
        this.status = status;
    }
}