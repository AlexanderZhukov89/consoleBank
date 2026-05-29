package com.example.consoleBank.view;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleMenu implements CommandLineRunner {

    private final Scanner scanner = new Scanner(System.in);

    @Autowired
    private AdministratorMenu administratorMenu;

    @Autowired
    private ClientMenu clientMenu;

    @Override
    public void run(@Nonnull String... args) {
        showMainMenu();
    }

    private void showMainMenu() {


        while (true) {
            System.out.println("ДОБРО ПОЖАЛОВАТЬ!");
            System.out.println("1. Войти в ЛК Администратора");
            System.out.println("2. Войти в ЛК Клиента");
            System.out.println("3. Выйти");

            int choice = getIntInput();

            switch (choice) {
                case 1 -> administratorMenu.showAdministratorMenu();
                case 2 -> clientMenu.showClientMenu();
                case 3 -> {
                    System.out.println("До свидания!");
                    System.exit(0);
                }
                default -> System.out.println("Неверный выбор");
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
