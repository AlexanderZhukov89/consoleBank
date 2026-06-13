package com.example.consoleBank.service;

import com.example.consoleBank.model.Transaction;
import com.example.consoleBank.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction create(Transaction transaction) {

        return transactionRepository.save(transaction);

    }

}
