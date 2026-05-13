package com.example.consoleBank;

import com.example.consoleBank.view.ConsoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConsoleBankApplication {

	@Autowired
	private static ConsoleMenu consoleMenu;

	public static void main(String[] args) {
		SpringApplication.run(ConsoleBankApplication.class, args);

		consoleMenu.run();
	}

}
