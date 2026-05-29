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

    public Optional<Client> findByTelNumber(String telNumber) {
        return clientRepository.findByTelNumber(telNumber);
    }

    @Transactional
    public Client create(Client client) {

        if(isPhoneExists(client.getTelNumber())) {
            throw new IllegalStateException("Клиент с таким телефоном уже существует");
        }

        if(client.isValid()) {
            return clientRepository.save(client);
        } else {
            throw new IllegalStateException("Некорректно заполнен");
        }

    }

    @Transactional
    public void update(Client updateClient) {

        Client findClient = clientRepository.findById(updateClient.getId())
                .orElseThrow(() -> new IllegalStateException("Клиент с таким ID не найден"));


        String updateTelNumber = updateClient.getTelNumber();
        if(updateTelNumber != null) {
            findClient.setTelNumber(updateTelNumber);
        }

        String updateName = updateClient.getName();
        if(updateName != null) {
            findClient.setName(updateName);
        }

        String updateEmail = updateClient.getEmail();
        if(updateEmail != null) {
            findClient.setEmail(updateEmail);
        }



        if(findClient.isValid()) {
            clientRepository.save(findClient);
        } else {
            throw new IllegalStateException("Некорректно заполнен");
        }

    }

    public void delete(Client deleteClient) {

        Client findClient = clientRepository.findById(deleteClient.getId())
                .orElseThrow(() -> new IllegalStateException("Клиент с таким ID не найден"));

        clientRepository.delete(findClient);

    }

    public boolean isPhoneExists(String telNumber) {

        Optional<Client> optionalClient = clientRepository.findByTelNumber(telNumber);

        return optionalClient.isPresent();

    }


}
