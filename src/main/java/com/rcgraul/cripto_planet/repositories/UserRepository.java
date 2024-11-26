package com.rcgraul.cripto_planet.repositories;

import com.rcgraul.cripto_planet.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    public boolean existsByEmail(String email);

    public boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
