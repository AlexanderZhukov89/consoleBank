package com.example.consoleBank.view;

import com.example.consoleBank.model.Account;
import com.example.consoleBank.model.Client;
import com.example.consoleBank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class AccountMenu {

    private final AccountService accountService;
    private final Scanner scanner = new Scanner(System.in);

    public void showMainAccountMenu(Client thisClient) {

        List<Account> accountList = accountService.getAllByClient(thisClient);

        System.out.println("===ВЫБЕРИТЕ СЧЕТ===");


        Integer i = 1;
        for (Account account: accountList) {

            System.out.println(i + ". " + account.getNumber());

            i++;
        }

        System.out.println(i + ". Назад");

        int choice = getIntInput();

        if(choice > 0 && choice < i) {

            Account thisAccount = accountList.get(choice - 1);
            showAccountMenu(thisAccount, thisClient);

        } else if (choice != i){
            System.out.println("Неверный выбор");
        }

    }

    public void showAccountMenu(Account thisAccount, Client thisClient) {

        while (true) {

            System.out.println("===Счет "+ thisAccount.getNumber() + "===");
            System.out.println("1. Пополнить счет");
            System.out.println("2. Снять средства");
            System.out.println("3. Перевод между счетами");
            System.out.println("4. Перевод по номеру счета");
            System.out.println("5. Удалить счет");
            System.out.println("6. Вернуться в главное меню");

            int choice = getIntInput();

            if(choice == 1){
                showDepositWithdrawMenu(thisAccount, "deposit");
            } else if (choice == 2) {
                showDepositWithdrawMenu(thisAccount, "withdraw");
            } else if (choice == 3) {
                showInnerTransferMenu(thisAccount, thisClient);
            } else if (choice == 4) {
                showTransferMenu(thisAccount);
            } else if (choice == 5) {
                boolean accountDeleted = deleteAccount(thisAccount);
                if (accountDeleted) {
                    return;
                }
            } else if (choice == 6) {
                return;
            } else {
                System.out.println("Неверный выбор");
            }

        }



    }

    private void showDepositWithdrawMenu(Account thisAccount, String operationType) {

        System.out.println("Введите сумму");

        String amountStr = scanner.nextLine().trim();
        BigDecimal depositWithdrawAmount;

        try {
            depositWithdrawAmount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат суммы");
            return;
        }

        if(depositWithdrawAmount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Сумма должна быть больше 0");
            return;
        }

        BigDecimal newBalance;

        if(operationType.equals("deposit")) {
            newBalance = thisAccount.getBalance().add(depositWithdrawAmount);

        }else {
            newBalance = thisAccount.getBalance().subtract(depositWithdrawAmount);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0){
                System.out.println("Недостаточно средств");
                return;
            }
        }

        thisAccount.setBalance(newBalance);

        try {
            accountService.update(thisAccount);
            System.out.println("Баланс счета изменен. Текущий баланс " + thisAccount.getBalance());
        } catch (IllegalStateException e) {
            System.out.println("Не удалось изменить баланс счета. " + e.getMessage());
        }


    }

    private void showInnerTransferMenu(Account thisAccount, Client thisClient) {

        List<Account> accountList = accountService.getAllByClient(thisClient);

        System.out.println("===ВЫБЕРИТЕ СЧЕТ===");


        Integer i = 1;
        for (Account account: accountList) {

            if(thisAccount.getNumber().equals(account.getNumber())){
                continue;
            }
            System.out.println(i + ". " + account.getNumber());

            i++;
        }

        System.out.println(i + ". Назад");

        int choice = getIntInput();

        if(choice > 0 && choice < i) {

            Account corespondentAccount = accountList.get(choice - 1);

            System.out.println("Введите сумму");

            String amountStr = scanner.nextLine().trim();
            BigDecimal depositWithdrawAmount;

            try {
                depositWithdrawAmount = new BigDecimal(amountStr);
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат суммы");
                return;
            }

            if(depositWithdrawAmount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Сумма должна быть больше 0");
                return;
            }

            try {
                accountService.transfer(thisAccount, corespondentAccount, depositWithdrawAmount);
                System.out.println("Перевод успешно выполнен");
            } catch (IllegalStateException e) {
                System.out.println("Не удалось выполнить перевод средств. " + e.getMessage());
            }


        } else if (choice != i){
            System.out.println("Неверный выбор");
        }


    }

    private void showTransferMenu(Account thisAccount) {

        System.out.println("Укажите номер счета");
        System.out.println("Или введите Назад, для выхода в предыдущее меню");

        Long number = getLongInput();

        Account corespondentAccount;

        try {
            corespondentAccount = accountService.getByNumber(number);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Введите сумму");

        String amountStr = scanner.nextLine().trim();
        BigDecimal depositWithdrawAmount;

        try {
            depositWithdrawAmount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат суммы");
            return;
        }

        if (depositWithdrawAmount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Сумма должна быть больше 0");
            return;
        }

        try {
            accountService.transfer(thisAccount, corespondentAccount, depositWithdrawAmount);
            System.out.println("Перевод успешно выполнен");
        } catch (IllegalStateException e) {
            System.out.println("Не удалось выполнить перевод средств. " + e.getMessage());
        }



    }

    private boolean deleteAccount(Account thisAccount) {

        if(thisAccount.getBalance().doubleValue() > 0) {
            System.out.println("На счете есть средства, удаление не возможно.");
            return false;
        }



        System.out.println("Уверены, что хотите удалить счет " + thisAccount.getNumber());
        System.out.println("1. Да");
        System.out.println("2. Нет");

        int choice = getIntInput();

        if (choice == 1) {

            try {
                accountService.delete(thisAccount);
                System.out.println("Счет успешно удален");
                return true;
            } catch (IllegalStateException e) {
                System.out.println("Не удалось удалить счет. " + e.getMessage());
            }

        } else if (choice == 2) {
            return false;
        } else {
            System.out.println("Неверный выбор");
        }

        return false;
    }

    private int getIntInput() {

        while (true) {

            String input = scanner.nextLine();

            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Указано неверное значение");
            }

        }
    }

    private Long getLongInput() {

        while (true) {

            String input = scanner.nextLine();

            try {
                return Long.parseLong(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Указано неверное значение");
            }

        }
    }


}
