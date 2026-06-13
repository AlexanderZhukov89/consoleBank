package com.example.consoleBank.service;

import com.example.consoleBank.model.Account;
import com.example.consoleBank.model.Client;
import com.example.consoleBank.model.OperationType;
import com.example.consoleBank.model.Transaction;
import com.example.consoleBank.repository.AccountRepository;
import com.example.consoleBank.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

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

    @Transactional
    public void delete(Account account) {

        Account findAccount = accountRepository.findById(account.getId())
                .orElseThrow(() -> new IllegalStateException("Счет с таким ID не найден"));

        BigDecimal accountBalance = findAccount.getBalance();

        if (accountBalance.compareTo(BigDecimal.ZERO) > 0){
            throw new IllegalStateException("Счет имеет ненулевой баланс. Удаление невозможно.");
        }

        accountRepository.delete(findAccount);
    }

    @Transactional
    public void disable(Account account) {

        Account findAccount = accountRepository.findById(account.getId())
                .orElseThrow(() -> new IllegalStateException("Счет с таким ID не найден"));

        BigDecimal accountBalance = findAccount.getBalance();

        if (accountBalance.compareTo(BigDecimal.ZERO) > 0){
            throw new IllegalStateException("Счет имеет ненулевой баланс. Закрытие невозможно.");
        }

        findAccount.setActive(false);

        accountRepository.save(findAccount);
    }

    @Transactional
    public void deposit(Account thisAccount, BigDecimal amount) {

        if(amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Сумма должна быть больше ноля");
        }

        thisAccount.setBalance(thisAccount.getBalance().add(amount));
        update(thisAccount);

        Transaction newTransaction = new Transaction();
        newTransaction.setAccount(thisAccount);
        newTransaction.setAmount(amount);
        newTransaction.setOperationType(OperationType.DEPOSIT);
        newTransaction.setBalanceAfter(thisAccount.getBalance());

        transactionRepository.save(newTransaction);

    }

    @Transactional
    public void withdraw(Account thisAccount, BigDecimal amount) {

        if(amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Сумма должна быть больше ноля");
        }

        if (thisAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Недостаточно средств");
        }

        thisAccount.setBalance(thisAccount.getBalance().subtract(amount));
        update(thisAccount);

        Transaction newTransaction = new Transaction();
        newTransaction.setAccount(thisAccount);
        newTransaction.setAmount(amount);
        newTransaction.setOperationType(OperationType.WITHDRAW);
        newTransaction.setBalanceAfter(thisAccount.getBalance());

        transactionRepository.save(newTransaction);

    }

    @Transactional
    public void transfer(Account thisAccount, Account correspondentAccount, BigDecimal amount) {

        if (amount == null) {
            throw new IllegalStateException("Сумма не может быть null");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Сумма должна быть больше 0");
        }

        Account foundAccount = accountRepository.findById(thisAccount.getId())
                .orElseThrow(() -> new IllegalStateException("Счет отправителя не найден"));
        Account foundCorrespondentAccount = accountRepository.findById(correspondentAccount.getId())
                .orElseThrow(() -> new IllegalStateException("Счет получателя не найден"));


        if (foundAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Недостаточно средств");
        }

        foundAccount.setBalance(foundAccount.getBalance().subtract(amount));
        foundCorrespondentAccount.setBalance(foundCorrespondentAccount.getBalance().add(amount));

        accountRepository.save(foundAccount);
        accountRepository.save(foundCorrespondentAccount);

        thisAccount.setBalance(foundAccount.getBalance());
        correspondentAccount.setBalance(foundCorrespondentAccount.getBalance());

        Transaction newTransaction = new Transaction();
        newTransaction.setAccount(foundAccount);
        newTransaction.setAmount(amount);
        newTransaction.setOperationType(OperationType.TRANSFER);
        newTransaction.setCorrespondentAccount(foundCorrespondentAccount);
        newTransaction.setBalanceAfter(foundAccount.getBalance());

        transactionRepository.save(newTransaction);

        Transaction newCorrespondentTransaction = new Transaction();
        newCorrespondentTransaction.setAccount(foundCorrespondentAccount);
        newCorrespondentTransaction.setAmount(amount);
        newCorrespondentTransaction.setOperationType(OperationType.TRANSFER);
        newCorrespondentTransaction.setCorrespondentAccount(foundAccount);
        newCorrespondentTransaction.setBalanceAfter(foundCorrespondentAccount.getBalance());

        transactionRepository.save(newCorrespondentTransaction);
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public List<Account> getAllByClient(Client client) {
        return accountRepository.findByClientAndActiveTrue(client);
    }

    public Account getByNumber(Long number) {
        return accountRepository.findByNumber(number)
                .orElseThrow(() -> new IllegalStateException("Счет с таким номером не найден"));

    }

    public Account getById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Счет с таким id не найден"));
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
