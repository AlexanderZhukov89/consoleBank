package com.example.consoleBank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private String messageId;
    private String type; // TRANSFER, DEPOSIT, WITHDRAW
    private String recipientEmail;
    private String recipientPhone;
    private String clientName;
    private Long accountNumber;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String correspondentAccountNumber;
    private LocalDateTime timestamp;
    private String status; // SUCCESS, FAILED

    public static NotificationMessage successTransfer(String clientName, Long fromAccount,
                                                      Long toAccount, BigDecimal amount,
                                                      BigDecimal newBalance) {
        NotificationMessage msg = new NotificationMessage();
        msg.setMessageId(java.util.UUID.randomUUID().toString());
        msg.setType("TRANSFER");
        msg.setClientName(clientName);
        msg.setAccountNumber(fromAccount);
        msg.setAmount(amount);
        msg.setBalanceAfter(newBalance);
        msg.setCorrespondentAccountNumber(String.valueOf(toAccount));
        msg.setTimestamp(LocalDateTime.now());
        msg.setStatus("SUCCESS");
        return msg;
    }
}