package com.example.consoleBank.view;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@RequiredArgsConstructor
@Profile("!test")
//public class ConsoleMenu implements CommandLineRunner {
public class ConsoleMenu {

    private final AdministratorMenu administratorMenu;
    private final ClientMenu clientMenu;
    private final Scanner scanner;


//    @Override
//    public void run(@Nonnull String... args) {
//        try {
//            showMainMenu();
//        } finally {
//            scanner.close();
//        }
//
//    }

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
