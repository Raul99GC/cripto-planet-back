package com.rcgraul.cripto_planet.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rcgraul.cripto_planet.models.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    public boolean existsByEmail(String email);

    public boolean existsByUsername(String username);

}
