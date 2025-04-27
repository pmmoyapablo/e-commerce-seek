package com.vectora.transactionservice.application.port.out.messaging;

import com.vectora.transactionservice.domain.event.TransactionEvent;

public interface TransactionEventPublisher {
    void publish(TransactionEvent event);
}