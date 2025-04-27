package com.vectora.transactionservice.infrastructure.adapter.in.web.dto;

import com.vectora.transactionservice.domain.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionsRecordResponse {
    private List<Transaction> transactions;
}