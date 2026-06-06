package com.example.consoleBank.model;

import lombok.Getter;

@Getter
public enum OperationType {

    DEPOSIT("Пополнение"),
    WITHDRAW("Снятие"),
    TRANSFER("Перевод");

    private final String description;

    OperationType(String description) {
        this.description = description;
    }

}
