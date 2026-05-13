package com.example.consoleBank.model;

import lombok.Data;

import java.util.Objects;

@Data
public class Client {

    private String id;
    private String name;
    private String email;
    private String telNumber;





    //Код, если бы не использовали @Data
//    public Client(String id, String name, String email, String telNumber) {
//        this.id = id;
//        this.name = name;
//        this.email = email;
//        this.telNumber = telNumber;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getTelNumber() {
//        return telNumber;
//    }
//
//    public void setTelNumber(String telNumber) {
//        this.telNumber = telNumber;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (o == null || getClass() != o.getClass()) return false;
//        Client client = (Client) o;
//        return Objects.equals(id, client.id) && Objects.equals(name, client.name) && Objects.equals(email, client.email) && Objects.equals(telNumber, client.telNumber);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, name, email, telNumber);
//    }
}
