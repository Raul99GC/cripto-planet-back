package com.rcgraul.cripto_planet.repositories;

import com.rcgraul.cripto_planet.models.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
}
