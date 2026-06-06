package com.example.consoleBank.repository;

import com.example.consoleBank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TransactionRepository extends JpaRepository<Transaction, String> {

}
