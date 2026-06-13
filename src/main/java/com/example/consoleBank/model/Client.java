package com.example.consoleBank.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Entity
@Table(name = "clients")
@Data
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @Column(name = "tel_number")
    private String telNumber;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Account> accounts = new ArrayList<>();

    static private final Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    static private final Pattern telNumberPattern = Pattern.compile("^8\\d{10}$");

    public Client() {
    }

    public Client(String name, String email, String telNumber) {
        this.name = name;
        this.email = email;
        this.telNumber = telNumber;
    }

    static public boolean isValidTelNumber(String telNumber) {
        return telNumberPattern.matcher(telNumber).matches();
    }

    static public boolean isValidEmail(String email) {
        return emailPattern.matcher(email).matches();
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", telNumber='" + telNumber + '\'' +
                ", accounts=" + accounts +
                '}';
    }

    public boolean isValid() {

        boolean isValid = true;

        if(name == null || name.length() < 2) {
            System.out.println("Неверно указано имя");
            isValid = false;
        }

        if(telNumber == null || !isValidTelNumber(telNumber)) {
            System.out.println("Неверный формат номера телефона");
            isValid = false;
        }

        if(email == null || !isValidEmail(email)) {
            System.out.println("Неверный формат Емейла");
            isValid = false;
        }

        return isValid;
    }


}
