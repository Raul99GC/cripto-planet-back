package com.rcgraul.cripto_planet.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rcgraul.cripto_planet.enums.UserRole;
import com.rcgraul.cripto_planet.models.Role;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByRoleName(UserRole userRole);

}
