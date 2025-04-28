package com.seek.users.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import

import java.util.List;

@Service // Spring bean
@RequiredArgsConstructor // Lombok: Constructor con todos los campos final
public class UserServiceImpl implements UserUseCase {
    private final UserRepositoryPort transactionRepositoryPort; // Inyección del puerto de salida
    private final UserEventPublisher eventProducer;
    private final EmailService externalService;

    @Override
    @Transactional // Asegura atomicidad en la creación
    public TransactionResponse createTransaction(Long fromAccount, Long toAccount, double amount) {
        Transaction transaction = new Transaction(fromAccount, toAccount, amount);
        // Validar que el monto sea positivo y mayor a 0
        if (!transaction.validateAmount()) {
            throw new IllegalArgumentException("Amount must be positive and major to 0.00.");
        }
        try {
            // Validar que las cuentas sean diferentes
            transaction.validateAccounts();
            // Vadidar existencia de cuenta destino
            boolean isValid = externalService.validateAccount(toAccount);
            if (!isValid) {
                throw new IllegalArgumentException("Account not found.");
            }
        } catch (TransactionNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        // Guardar la transacción
        Transaction createdTransaction = transactionRepositoryPort.save(transaction);
        TransactionResponse response = new TransactionResponse();
        response.setTransactionId(createdTransaction.getId());
        String status = "success";
        response.setStatus(status);

        // Publicar el evento
        TransactionEvent event = new TransactionEvent();
        event.setData(createdTransaction, status);
        eventProducer.publish(event);

        return response;
    }

    @Override
    @Transactional(readOnly = true) // Optimización para lecturas
    public TransactionsRecordResponse getTransactions(Long accountId) {
        try {
            List<Transaction> transactions = transactionRepositoryPort.getRecord(accountId);
            TransactionsRecordResponse response = new TransactionsRecordResponse(); // Mapea Dominio a DTO
            response.setTransactions(transactions);
            return response;
        } catch (Exception ex) {
            throw new TransactionNotFoundException(accountId);
        }
    }

}