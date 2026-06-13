package com.example.consoleBank.service;

import com.example.consoleBank.dto.AccountDTO;
import com.example.consoleBank.model.Account;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccountMapper {

    public AccountDTO toDTO(Account account) {

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setClientName(account.getClient().getName());
        accountDTO.setNumber(account.getNumber());
        accountDTO.setBalance(account.getBalance());
        accountDTO.setCreatedAt(account.getCreatedAt());
        accountDTO.setActive(account.isActive());

        return accountDTO;
    }

    public List<AccountDTO> toDTOList(List<Account> accounts) {

        return accounts.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

    }

}
