package com.example.consoleBank.view;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleMenu implements CommandLineRunner {

    private final Scanner scanner = new Scanner(System.in);


    @Override
    public void run(String... args) {
        showMainManu();
    }

    private void showMainManu() {

        while (true){
            System.out.println("Нажмите 1");
            System.out.println("Нажмите 2");
            System.out.println("Нажмите 3, что бы Выйти");

            int choice = scanner.nextInt();

            switch (choice){
                case 1 -> System.out.println("Нажали 1");
                case 2 -> System.out.println("Нажали 2");
                case 3 -> {
                    System.out.println("Нажали 3");
                    System.exit(0);
                }
                default -> System.out.println("Неверный выбор");
            }

        }

    }
}
