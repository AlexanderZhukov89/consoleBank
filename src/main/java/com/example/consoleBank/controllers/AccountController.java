package com.example.consoleBank.controllers;

import com.example.consoleBank.dto.AccountDTO;
import com.example.consoleBank.model.*;
import com.example.consoleBank.service.AccountMapper;
import com.example.consoleBank.service.AccountService;
import com.example.consoleBank.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final ClientService clientService;

    private final AccountMapper accountMapper;

    @GetMapping
    public List<AccountDTO> findAll() {

        List<Account> accounts = accountService.findAll();
        return accountMapper.toDTOList(accounts);
    }

    @GetMapping(path = "/{id}")
    public AccountDTO findById(@PathVariable Long id) {

        Account account = accountService.getById(id);
        return accountMapper.toDTO(account);
    }

    @PostMapping
    public Account create(@RequestBody AccountDTO accountDTO) {

        Client client = clientService.getById(accountDTO.getClientId());

        Account account = new Account(client);

        return accountService.create(account);
    }

    @PatchMapping("/{id}/disable")
    public void disableAccount (@PathVariable Long id) {
        Account account = accountService.getById(id);
        accountService.disable(account);
    }

    @PostMapping(path = "/{id}/deposit")
    public Transaction deposit(@PathVariable Long id, @RequestBody AmountRequest amountRequest) {

        Account account = accountService.getById(id);
        return  accountService.deposit(account, amountRequest.getAmount());
    }

    @PostMapping(path = "/{id}/withdraw")
    public Transaction withdraw(@PathVariable Long id, @RequestBody AmountRequest amountRequest) {

        Account account = accountService.getById(id);
        return accountService.withdraw(account, amountRequest.getAmount());
    }

    @PostMapping(path = "/{id}/transfer")
    public Transaction transfer(@PathVariable Long id, @RequestBody TransferRequest transferRequest) {

        Account account = accountService.getById(id);
        Account correspondentAccount = accountService.getById(transferRequest.getCorrespondentAccountId());

        return accountService.transfer(account, correspondentAccount, transferRequest.getAmount());
    }


}
