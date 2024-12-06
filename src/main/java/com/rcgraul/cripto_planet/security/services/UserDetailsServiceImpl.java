package com.rcgraul.cripto_planet.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rcgraul.cripto_planet.models.User;
import com.rcgraul.cripto_planet.repositories.UserRepository;

/**
 * Clase `UserDetailsServiceImpl`
 * <p>
 * Esta clase implementa la interfaz `UserDetailsService` de Spring Security.
 * Es responsable de cargar los datos del usuario desde la base de datos (o cualquier otra fuente de datos).
 * <p>
 * Funciones principales:
 * - Buscar un usuario por su email en la base de datos utilizando el `UserRepository`.
 * - Convertir el usuario encontrado en una instancia de `UserDetailsImpl` para que pueda ser utilizado por Spring Security.
 * - Lanzar una excepción si no se encuentra el usuario.
 * <p>
 * Contexto de uso:
 * Spring Security invoca esta clase automáticamente durante el proceso de autenticación
 * para buscar al usuario en la base de datos y verificar sus credenciales.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return UserDetailsImpl.build(user);
    }

}
