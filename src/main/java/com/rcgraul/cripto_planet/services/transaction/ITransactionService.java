package com.rcgraul.cripto_planet.services.transaction;

import com.rcgraul.cripto_planet.enums.WalletTransactionType;
import com.rcgraul.cripto_planet.models.Wallet;
import com.rcgraul.cripto_planet.models.WalletTransaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ITransactionService {

    List<WalletTransaction> getTransactionsByWallet(Wallet wallet);

    void addTransaction(Wallet wallet, WalletTransaction transaction);

    WalletTransaction createTransaction(Wallet userWallet, WalletTransactionType walletTransactionType, UUID tId, String purpose, LocalDateTime date, Long amount);
}
