package com.example.consoleBank.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    private BigDecimal amount;
    private Long correspondentAccountId;
}
