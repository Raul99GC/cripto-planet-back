package com.rcgraul.cripto_planet.repositories;

import com.rcgraul.cripto_planet.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    Wallet findByUserId(UUID userId);
}
