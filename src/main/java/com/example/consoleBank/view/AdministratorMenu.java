package com.example.consoleBank.view;

import com.example.consoleBank.model.Client;
import com.example.consoleBank.service.ClientService;
import org.springframework.stereotype.Component;


import java.util.Optional;
import java.util.Scanner;
import java.util.function.Predicate;

@Component
public class AdministratorMenu {


    private final ClientService clientService;

    private final Scanner scanner = new Scanner(System.in);

    public AdministratorMenu(ClientService clientService) {
        this.clientService = clientService;
    }

    public void showAdministratorMenu() {

        while (true) {
            System.out.println("1. Создать клиента");
            System.out.println("2. Обновить клиента");
            System.out.println("3. Удалить клиента");
            System.out.println("4. Вернуться в главное меню");

            int choice = getIntInput();

            switch (choice) {
                case 1 -> createClient();
                case 2 -> updateClient();
                case 3 -> deleteClient();
                case 4 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор");
            }
        }

    }

    private void createClient() {

        Client newClient = new Client();

        String telNumber = inputInformation("Номер телефона", Client::isValidTelNumber, true);
        if (telNumber == null) {
            return;
        }
        newClient.setTelNumber(telNumber);


        String mail = inputInformation("E-mail", Client::isValidEmail, false);
        if (mail == null) {
            return;
        }
        newClient.setEmail(mail);


        String name = inputInformation("ФИО", s -> s != null && s.length() > 2, false);
        if (name == null) {
            return;
        }
        newClient.setName(name);

        try {
            Client savedClient = clientService.create(newClient);
            System.out.println("Клиент " + name + " успешно создан. ID - " + savedClient.getId());
        } catch (IllegalStateException e) {
            System.out.println("Не удалось создать клиента. " + e.getMessage());
        }
    }

    private void updateClient() {

        String telNumber = inputInformation("Номер телефона", Client::isValidTelNumber, false);
        if (telNumber == null) {
            return;
        }
        Optional<Client> optionalClient =  clientService.findByTelNumber(telNumber);
        if (optionalClient.isEmpty()){
            System.out.println("Не найден клиент с номером телефона " + telNumber);
            return;
        }

        Client updatedClient = optionalClient.get();


        while (true) {
            System.out.println("Клиент " + updatedClient.getName());
            System.out.println("1. Изменить E-mail");
            System.out.println("2. Изменить ФИО");
            System.out.println("3. Назад");

            int choice = getIntInput();

            if (choice == 1) {

                String mail = inputInformation("E-mail", Client::isValidEmail, false);
                if (mail == null) {
                    return;
                }
                updatedClient.setEmail(mail);

                try {
                    clientService.update(updatedClient);
                    System.out.println("Клиент " + updatedClient.getName() + " успешно обновлен");
                } catch (IllegalStateException e) {
                    System.out.println("Не удалось обновить клиента. " + e.getMessage());
                }

            } else if (choice == 2) {

                String name = inputInformation("ФИО", s -> s != null && s.length() > 2, false);
                if (name == null) {
                    return;
                }
                updatedClient.setName(name);

                try {
                    clientService.update(updatedClient);
                    System.out.println("Клиент " + updatedClient.getName() + " успешно обновлен");
                } catch (IllegalStateException e) {
                    System.out.println("Не удалось обновить клиента. " + e.getMessage());
                }

            }else if (choice == 3) {
                return;
            } else {
                System.out.println("Неверный выбор");
            }

        }

    }

    private void deleteClient() {

        String telNumber = inputInformation("Номер телефона", Client::isValidTelNumber, false);
        if (telNumber == null) {
            return;
        }
        Optional<Client> optionalClient =  clientService.findByTelNumber(telNumber);
        if (optionalClient.isEmpty()){
            System.out.println("Не найден клиент с номером телефона " + telNumber);
            return;
        }

        Client deletedClient = optionalClient.get();

        System.out.println("Уверены, что хотите удалить клиента " + deletedClient.getName());
        System.out.println("1. Да");
        System.out.println("2. Нет");

        int choice = getIntInput();

        if (choice == 1) {

            try {
                clientService.delete(deletedClient);
                System.out.println("Клиент успешно удален");
            } catch (IllegalStateException e) {
                System.out.println("Не удалось удалить клиента. " + e.getMessage());
            }

        } else if (choice == 2) {
            return;
        } else {
            System.out.println("Неверный выбор");
        }

    }

    private String inputInformation(String field, Predicate<String> validator, boolean checkDuplicate) {

        while (true) {
            System.out.println("Укажите " + field);
            System.out.println("Или введите Назад, для выхода в предыдущее меню");

            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("Назад")){
                return null;
            }

            if(!validator.test(input)) {
                System.out.println("Неверный формат");
                continue;
            }

            if(checkDuplicate) {

                if(clientService.isPhoneExists(input)) {
                    System.out.println("Клиент с таким телефоном уже существует");
                    continue;
                }
            }

            return input;
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
