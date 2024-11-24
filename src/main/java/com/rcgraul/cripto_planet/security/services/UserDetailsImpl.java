package com.rcgraul.cripto_planet.security.services;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.rcgraul.cripto_planet.models.TwoFactorAuth;
import com.rcgraul.cripto_planet.models.TwoFactorOTP;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rcgraul.cripto_planet.models.User;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase `UserDetailsImpl`
 * <p>
 * Esta clase implementa la interfaz `UserDetails` de Spring Security.
 * Su propósito es actuar como un adaptador entre la entidad `User` de la aplicación
 * y el modelo de autenticación y autorización requerido por Spring Security.
 * <p>
 * Funciones principales:
 * - Proveer detalles del usuario autenticado (como email, roles, contraseña, etc.).
 * - Encapsular los roles del usuario en el formato requerido por Spring Security.
 * - Definir el estado del usuario (cuenta habilitada, bloqueada, expiración, etc.).
 * <p>
 * Esta clase es utilizada por Spring Security durante el proceso de autenticación
 * para determinar si un usuario puede acceder al sistema y qué permisos tiene.
 */

@NoArgsConstructor
@Data
public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    @JsonIgnore
    private String password;

    private String signUpMethod;

    private TwoFactorAuth twoFactorAuth; // Añadido

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(UUID id, String username, String email, String password,
                           String signUpMethod, TwoFactorAuth twoFactorAuth,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.signUpMethod = signUpMethod;
        this.twoFactorAuth = twoFactorAuth; // Inicializado
        this.authorities = authorities;
    }

    /**
     * Método estático `build`
     * <p>
     * Este método convierte una entidad `User` en una instancia de `UserDetailsImpl`,
     * asignando los datos relevantes del usuario y encapsulando su rol en un formato que
     * Spring Security puede interpretar.
     */
    public static UserDetailsImpl build(User user) {
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getRoleName().name());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getSignUpMethod(),
                user.getTwoFactorAuth(), // Pasando TwoFactorAuth desde User
                List.of(authority)
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UserDetailsImpl other = (UserDetailsImpl) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

}
