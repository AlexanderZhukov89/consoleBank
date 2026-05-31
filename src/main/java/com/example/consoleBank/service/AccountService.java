package com.example.consoleBank.service;

import com.example.consoleBank.model.Account;
import com.example.consoleBank.model.Client;
import com.example.consoleBank.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account create(Account account) {

        account.setNumber(getNewAccountNumber());
        return accountRepository.save(account);

    }

    @Transactional
    public void update(Account updateAccount) {

        Account findAccount = accountRepository.findById(updateAccount.getId())
                .orElseThrow(() -> new IllegalStateException("Аккаунт с таким ID не найден"));


        BigDecimal updateBalance = updateAccount.getBalance();
        if(updateBalance != null) {
            findAccount.setBalance(updateBalance);
        }

        accountRepository.save(findAccount);

    }

    public void delete(Account account) {

        Account findAccount = accountRepository.findById(account.getId())
                .orElseThrow(() -> new IllegalStateException("Счёт с таким ID не найден"));

        BigDecimal accountBalance = findAccount.getBalance();

        if (accountBalance.doubleValue() > 0){
            throw new IllegalStateException("Счёт имеет на нулевой баланс. Удаление невозможно");
        }

        accountRepository.delete(findAccount);
    }

    public List<Account> getAllByClient(Client client) {
        return accountRepository.findByClientAndActiveTrue(client);
    }

    private Long getNewAccountNumber() {
       while (true) {
           Long newNumber = System.currentTimeMillis() % 1_000_000_000_000L;

           Optional<Account> optionalAccount = accountRepository.findByNumber(newNumber);
           if(optionalAccount.isEmpty()){
               return newNumber;
           }
       }
    }
}
