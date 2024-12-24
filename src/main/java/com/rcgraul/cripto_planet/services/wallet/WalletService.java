package com.rcgraul.cripto_planet.services.wallet;

import com.rcgraul.cripto_planet.enums.OrderType;
import com.rcgraul.cripto_planet.enums.WalletTransactionType;
import com.rcgraul.cripto_planet.models.Order;
import com.rcgraul.cripto_planet.models.User;
import com.rcgraul.cripto_planet.models.Wallet;
import com.rcgraul.cripto_planet.repositories.WalletRepository;
import com.rcgraul.cripto_planet.services.transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class WalletService implements IWalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionService transactionService;

    @Override
    public Wallet createWallet(User user) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        walletRepository.save(wallet);

        return wallet;
    }

    @Override
    public Wallet getUserWallet(User user) {
        return walletRepository.findByUserId(user.getId());
    }

    @Override
    public Wallet addBalance(Wallet wallet, Long amount) {

        BigDecimal balance = wallet.getBalance();
        BigDecimal newBalance = balance.add(BigDecimal.valueOf(amount));
        wallet.setBalance(newBalance);

        return walletRepository.save(wallet);

    }

    @Override
    public Wallet findWlletById(UUID id) throws Exception {

        Optional<Wallet> wallet = walletRepository.findById(id);

        if (wallet.isEmpty()) throw new Exception("Wallet not found");

        return wallet.get();
    }

    @Override
    public Wallet walletToWalletTransfer(User sender, Wallet reciverWallet, Long amount) throws Exception {

        Wallet senderWallet = getUserWallet(sender);

        if (senderWallet.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
            throw new Exception("Not enough balance");
        }

        BigDecimal senderBalance = senderWallet.getBalance()
                .subtract(BigDecimal.valueOf(amount));
        senderWallet.setBalance(senderBalance);

        walletRepository.save(senderWallet);

        BigDecimal reciverBalance = reciverWallet.getBalance()
                .add(BigDecimal.valueOf(amount));
        reciverWallet.setBalance(reciverBalance);

        transactionService.createTransaction(
                reciverWallet,
                WalletTransactionType.WALLET_TRANSFER,
                reciverWallet.getId(),
                "wallet transfer",
                LocalDateTime.now(),
                amount);

        walletRepository.save(reciverWallet);

        return senderWallet;
    }

    @Override
    public Wallet payOrderPayment(Order order, User user) throws Exception {

        Wallet wallet = getUserWallet(user);

        if (order.getOrderType().equals(OrderType.BUY)) {
            BigDecimal newBalance = wallet.getBalance().subtract(order.getPrice());

            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new Exception("Insufficient funds to complete the order");
            }
            wallet.setBalance(newBalance);
        } else {
            BigDecimal newBalance = wallet.getBalance().add(order.getPrice());
            wallet.setBalance(newBalance);
        }
        walletRepository.save(wallet);

        return wallet;
    }
}
