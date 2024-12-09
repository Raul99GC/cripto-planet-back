package com.rcgraul.cripto_planet.services.wallet;

import com.rcgraul.cripto_planet.models.Order;
import com.rcgraul.cripto_planet.models.User;
import com.rcgraul.cripto_planet.models.Wallet;

import java.util.UUID;

public interface IWalletService {
    Wallet getUserWallet(User user);

    Wallet addBalance(Wallet wallet, Long amount);

    Wallet findWlletById(UUID id) throws Exception;

    Wallet walletToWalletTransfer(User sender ,Wallet reciverWallet, Long amount) throws Exception;

    Wallet payOrderPayment(Order order, User user) throws Exception;
}
