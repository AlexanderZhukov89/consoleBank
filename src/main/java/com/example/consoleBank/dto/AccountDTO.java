package com.example.consoleBank.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountDTO {

    private String clientName;
    private Long number;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private boolean active;

}
