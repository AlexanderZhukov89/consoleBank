package com.example.consoleBank.view;

import com.example.consoleBank.model.Client;
import jakarta.annotation.Nonnull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;
import java.util.regex.Pattern;

@Component
public class ConsoleMenu implements CommandLineRunner {

    private final Scanner scanner = new Scanner(System.in);


    @Override
    public void run(@Nonnull String... args) {
        showMainMenu();
    }

    private void showMainMenu() {


        while (true) {
            System.out.println("ДОБРО ПОЖАЛОВАТЬ!");
            System.out.println("1. Войти");
            System.out.println("2. Зарегистрироваться");
            System.out.println("3. Выйти");

            int choice = getIntInput();

            switch (choice) {
                case 1 -> showLoginMenu();
                case 2 -> showRegistrationMenu();
                case 3 -> {
                    System.out.println("До свидания!");
                    System.exit(0);
                }
                default -> System.out.println("Неверный выбор");
            }
        }

    }

    private void showLoginMenu() {

        while (true) {
            System.out.println("Укажите Email");
            System.out.println("Или нажмите 1, что бы вернуться в главное меню");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                return;
            } else {
                System.out.println("Неверный выбор");
            }
        }

    }

    private void showRegistrationMenu() {

        while (true) {
            System.out.println("Укажите Email");
            System.out.println("Или нажмите 1, что бы вернуться в главное меню");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                return;
            } else {
                if (Client.isValidEmail(choice)){
                    Client newClient = new Client();
                    newClient.setEmail(choice);



                } else {
                    System.out.println("Неверный выбор");
                }
            }
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
