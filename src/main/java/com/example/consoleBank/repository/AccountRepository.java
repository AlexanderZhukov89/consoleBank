package com.example.consoleBank.repository;

import com.example.consoleBank.model.Account;
import com.example.consoleBank.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a WHERE a.number = :number")
    Optional<Account> findByNumber(Long number);

    List<Account> findByClientAndActiveTrue(Client client);


}
