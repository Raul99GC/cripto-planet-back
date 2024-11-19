package com.rcgraul.cripto_planet.services.user;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rcgraul.cripto_planet.enums.UserRole;
import com.rcgraul.cripto_planet.exceptions.EmailAlreadyExistsException;
import com.rcgraul.cripto_planet.exceptions.RoleNotFoundException;
import com.rcgraul.cripto_planet.exceptions.UsernameAlreadyExistsException;
import com.rcgraul.cripto_planet.models.Role;
import com.rcgraul.cripto_planet.models.User;
import com.rcgraul.cripto_planet.repositories.RoleRepository;
import com.rcgraul.cripto_planet.repositories.UserRepository;
import com.rcgraul.cripto_planet.security.request.SignupRequest;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public User registerUser(SignupRequest signupRequest) {

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email already taken");
        }
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already taken");
        }

        User newUser = new User(signupRequest.getUsername(), signupRequest.getEmail(), passwordEncoder.encode(signupRequest.getPassword()));
        Role role;

        Set<String> roles = signupRequest.getRoles();

        if (roles == null || roles.isEmpty()) {
            role = roleRepository.findByRoleName(UserRole.ROLE_ADMIN)
                    .orElseThrow(() -> new RoleNotFoundException("Role " + UserRole.ROLE_COSTUMER + " not found"));
        } else {
            String roleSrt = roles.iterator().next();
            if (roleSrt.equals("admin")) {
                role = roleRepository.findByRoleName(UserRole.ROLE_ADMIN)
                        .orElseThrow(() -> new RoleNotFoundException("Role " + UserRole.ROLE_ADMIN + " not found"));
            } else {
                role = roleRepository.findByRoleName(UserRole.ROLE_COSTUMER)
                        .orElseThrow(() -> new RoleNotFoundException("Role " + UserRole.ROLE_COSTUMER + " not found"));
            }
        }

        newUser.setFirstName(signupRequest.getFirstName());
        newUser.setLastName(signupRequest.getLastName());
        newUser.setRole(role);
        newUser.setAccountNonLocked(true);
        newUser.setAccountNonExpired(true);
        newUser.setCredentialsNonExpired(true);
        newUser.setEnabled(true);
        newUser.setCredentialsExpiryDate(LocalDate.now().plusYears(30));
        newUser.setAccountExpiryDate(LocalDate.now().plusYears(30));
        newUser.setSignUpMethod("email");

        return userRepository.save(newUser);
    }

}
