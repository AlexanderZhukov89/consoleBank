package com.example.consoleBank.service;

import com.example.consoleBank.dto.ClientDTO;
import com.example.consoleBank.model.Client;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientMapper {

    public ClientDTO toDTO(Client client) {

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName(client.getName());
        clientDTO.setEmail(client.getEmail());
        clientDTO.setTelNumber(client.getTelNumber());

        return clientDTO;
    }

    public List<ClientDTO> toDTOList(List<Client> clients) {

        return clients.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

    }

}
