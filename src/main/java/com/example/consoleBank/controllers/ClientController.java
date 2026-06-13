package com.example.consoleBank.controllers;

import com.example.consoleBank.dto.ClientDTO;
import com.example.consoleBank.model.Client;
import com.example.consoleBank.service.ClientMapper;
import com.example.consoleBank.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper clientMapper;

    @GetMapping
    public List<ClientDTO> findAll() {
        List<Client> clients = clientService.findAll();
        return clientMapper.toDTOList(clients);
    }

    @GetMapping(path = "/{id}")
    public ClientDTO getById(@PathVariable Long id) {
        Client client = clientService.getById(id);
        return clientMapper.toDTO(client);
    }

    @PostMapping
    public Client create(@RequestBody ClientDTO clientDTO) {

        Client client = new Client(clientDTO.getName(), clientDTO.getEmail(), clientDTO.getTelNumber());
        return clientService.create(client);
    }

    @PutMapping(path = "/{id}")
    public void update(@PathVariable Long id, @RequestBody ClientDTO clientDTO) {

        Client client = new Client(clientDTO.getName(), clientDTO.getEmail(), clientDTO.getTelNumber());
        client.setId(id);
        clientService.update(client);
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        Client client = clientService.getById(id);
        clientService.delete(client);
    }

}
