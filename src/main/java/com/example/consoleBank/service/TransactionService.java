package com.example.consoleBank.service;

import com.example.consoleBank.model.Transaction;
import com.example.consoleBank.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction create(Transaction transaction) {

        return transactionRepository.save(transaction);

    }

}
