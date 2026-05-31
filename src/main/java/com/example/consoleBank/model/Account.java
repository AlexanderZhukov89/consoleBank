package com.example.consoleBank.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Data
public class Account {

    @Version
    private Long version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client", nullable = false)
    private Client client;

    private Long number;
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private boolean active = true;

    public Account() {
        this.createdAt = LocalDateTime.now();
    }

    public Account(Client client) {
        this.client = client;
        this.createdAt = LocalDateTime.now();
    }

}
