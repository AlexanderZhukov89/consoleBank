package com.example.consoleBank.view;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleMenu implements CommandLineRunner {

    private final Scanner scanner = new Scanner(System.in);


    @Override
    public void run(String... args) {
        showMainMenu();
    }

    private void showMainMenu() {


        System.out.println("ДОБРО ПОЖАЛОВАТЬ!");
        System.out.println("1. Войти");
        System.out.println("2. Зарегистрироваться");
        System.out.println("3. Выйти");

        int choice = getIntInput(scanner);

        switch (choice) {
            case 1 -> showLoginMenu();
            case 2 -> showRegistrationMenu();
            case 3 -> {
                System.out.println("Нажали 3");
                System.exit(0);
            }
            default -> {
                System.out.println("Неверный выбор");
                showMainMenu();
            }
        }


    }

    private void showLoginMenu() {

        System.out.println("Укажите Email");
        System.out.println("Или нажмите 1, что вернуться в главное меню");

        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            showMainMenu();
        } else {
            System.out.println("Неверный выбор");
            showLoginMenu();
        }


    }

    private void showRegistrationMenu() {

        System.out.println("Укажите Email");
        System.out.println("Или нажмите 1, что вернуться в главное меню");

        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            showMainMenu();
        } else {
            System.out.println("Неверный выбор");
            showRegistrationMenu();
        }


    }


    private int getIntInput(Scanner scanner) {

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
