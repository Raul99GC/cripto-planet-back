package com.rcgraul.cripto_planet.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rcgraul.cripto_planet.models.TwoFactorOTP;

public interface TwoFactorOTPRepository extends JpaRepository<TwoFactorOTP, UUID> {

    TwoFactorOTP findByUserId(UUID userId);
}
