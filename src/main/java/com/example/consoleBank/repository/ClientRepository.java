package com.example.consoleBank.repository;

import com.example.consoleBank.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

//    @Query(value = "SELECT * FROM clients WHERE \"telNumber\" = :telNumber", nativeQuery = true)
//    Optional<Client> findByTelNumber(String telNumber);

    @Query("SELECT c FROM Client c WHERE c.telNumber = :telNumber")
    Optional<Client> findByTelNumber(String telNumber);

}
