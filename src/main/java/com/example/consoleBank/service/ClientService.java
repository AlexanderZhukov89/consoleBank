package com.example.consoleBank.service;

import com.example.consoleBank.model.Client;
import com.example.consoleBank.repository.ClientRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client create(Client client) {

        Optional<Client> optionalClient = clientRepository.findByEmail(client.getEmail());

        if(optionalClient.isPresent()) {
            throw new IllegalStateException("Клиент с таким Емейлом уже существует");
        }

        if(!client.isValid()) {
            throw new IllegalStateException("Некорректно заполнен");
        }

        return clientRepository.save(client);

    }

    @Transactional
    public void update(Client updateClient) {

        Client findClient = clientRepository.findById(updateClient.getId())
                .orElseThrow(() -> new IllegalStateException("Клиент с таким ID не найден"));


        String updateName = updateClient.getName();
        if(updateName != null) {
            findClient.setName(updateName);
        }

        String updateEmail = updateClient.getEmail();
        if(updateEmail != null) {
            findClient.setEmail(updateEmail);
        }

        String updateTelNumber = updateClient.getTelNumber();
        if(updateTelNumber != null) {
            findClient.setTelNumber(updateTelNumber);
        }

        if(!findClient.isValid()) {
            throw new IllegalStateException("Некорректно заполнен");
        }

        clientRepository.save(findClient);

    }


}
