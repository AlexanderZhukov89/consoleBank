package com.example.consoleBank.view;

import com.example.consoleBank.model.Account;
import com.example.consoleBank.model.Client;
import com.example.consoleBank.service.AccountService;
import com.example.consoleBank.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class ClientMenu {

    private final ClientService clientService;
    private final AccountService accountService;
    private final AccountMenu accountMenu;

    private final Scanner scanner = new Scanner(System.in);


    public void showClientMenu() {

        Client thisClient = showLoginMenu();

        if(thisClient == null) {
            return;
        }

        System.out.println("Добро пожаловать, " + thisClient.getName());

        while (true) {

            System.out.println("1. Создать счёт");
            System.out.println("2. Открыть меню счёта");
            System.out.println("3. Вернуться в главное меню");

            int choice = getIntInput();

            switch (choice) {
                case 1 -> createAccount(thisClient);
                case 2 -> accountMenu.showMainAccountMenu(thisClient);
                case 3 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор");
            }
        }

    }

    private Client showLoginMenu() {

        while (true) {
            System.out.println("Для входа в ЛК клиента укажите Номер телефона");
            System.out.println("Или введите Назад, для выхода в предыдущее меню");

            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("Назад")){
                return null;
            }

            Optional<Client> optionalClient =  clientService.findByTelNumber(input);
            if(optionalClient.isEmpty()){
                System.out.println("Не найден Клиент с номером телефона " + input);
            } else {
                return optionalClient.get();
            }

        }

    }

    private void createAccount(Client thisClient) {

        System.out.println("Вы уверены, что хотите открыть новый счет?");
        System.out.println("1. Да");
        System.out.println("2. Нет");

        int choice = getIntInput();

        if(choice == 2) {
            return;
        }


        Account newAccount = new Account(thisClient);

        try {
            Account savedAccount = accountService.create(newAccount);
            System.out.println("Счет успешно создан. Номер счёта - " + savedAccount.getNumber());
        }catch (IllegalStateException e){
            System.out.println("Не удалось создать счет. " + e.getMessage());
        }


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

}
