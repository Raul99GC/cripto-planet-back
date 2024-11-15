package com.rcgraul.cripto_planet.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rcgraul.cripto_planet.models.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    public User findByEmail(String email);

    public boolean existsByEmail(String email);

}
