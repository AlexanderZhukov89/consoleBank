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
                .orElseThrow(() -> new IllegalStateException("Счет с таким ID не найден"));


        BigDecimal updateBalance = updateAccount.getBalance();
        if(updateBalance != null) {
            findAccount.setBalance(updateBalance);
        }

        accountRepository.save(findAccount);

    }

    public void delete(Account account) {

        Account findAccount = accountRepository.findById(account.getId())
                .orElseThrow(() -> new IllegalStateException("Счет с таким ID не найден"));

        BigDecimal accountBalance = findAccount.getBalance();

        if (accountBalance.doubleValue() > 0){
            throw new IllegalStateException("Счет имеет на нулевой баланс. Удаление невозможно");
        }

        accountRepository.delete(findAccount);
    }

    @Transactional
    public void transfer(Account thisAccount, Account corespondentAccount, BigDecimal amount) {

        Account foundAccount = accountRepository.findById(thisAccount.getId())
                .orElseThrow(() -> new IllegalStateException("Счет отправителя не найден"));
        Account foundCorespondentAccount = accountRepository.findById(corespondentAccount.getId())
                .orElseThrow(() -> new IllegalStateException("Счет получателя не найден"));


        if (foundAccount.getBalance().compareTo(amount) < 0) {
            System.out.println("Недостаточно средств");
            return;
        }


        foundAccount.setBalance(foundAccount.getBalance().subtract(amount));
        foundCorespondentAccount.setBalance(foundCorespondentAccount.getBalance().add(amount));

        accountRepository.save(foundAccount);
        accountRepository.save(foundCorespondentAccount);

        thisAccount.setBalance(foundAccount.getBalance());
        corespondentAccount.setBalance(foundCorespondentAccount.getBalance());
    }

    public List<Account> getAllByClient(Client client) {
        return accountRepository.findByClientAndActiveTrue(client);
    }

    public Account getByNumber(Long number) {
        return accountRepository.findByNumber(number)
                .orElseThrow(() -> new IllegalStateException("Счет с таким номером не найден"));

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
