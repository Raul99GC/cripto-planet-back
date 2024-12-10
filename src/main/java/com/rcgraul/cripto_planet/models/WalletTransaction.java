package com.rcgraul.cripto_planet.models;

import com.rcgraul.cripto_planet.enums.WalletTransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Wallet wallet;

    private WalletTransactionType transactionType;

    private LocalDate date;

    private String transferId;

    private Long amount;

    private String purpose;
}
