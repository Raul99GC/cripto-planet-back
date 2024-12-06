package com.rcgraul.cripto_planet.services.user;

import com.rcgraul.cripto_planet.enums.UserRole;
import com.rcgraul.cripto_planet.exceptions.EmailAlreadyExistsException;
import com.rcgraul.cripto_planet.exceptions.ExpiredTokenException;
import com.rcgraul.cripto_planet.exceptions.RoleNotFoundException;
import com.rcgraul.cripto_planet.exceptions.UsernameAlreadyExistsException;
import com.rcgraul.cripto_planet.models.PasswordResetToken;
import com.rcgraul.cripto_planet.models.Role;
import com.rcgraul.cripto_planet.models.User;
import com.rcgraul.cripto_planet.repositories.PasswordResetTokenRepository;
import com.rcgraul.cripto_planet.repositories.RoleRepository;
import com.rcgraul.cripto_planet.repositories.UserRepository;
import com.rcgraul.cripto_planet.security.request.SignupRequest;
import com.rcgraul.cripto_planet.utils.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService implements IUserService {

    @Value("${frontend.url}")
    String frontendUrl;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

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
            role = roleRepository.findByRoleName(UserRole.ROLE_ADMIN).orElseThrow(() -> new RoleNotFoundException("Role " + UserRole.ROLE_COSTUMER + " not found"));
        } else {
            String roleSrt = roles.iterator().next();
            if (roleSrt.equals("admin")) {
                role = roleRepository.findByRoleName(UserRole.ROLE_ADMIN).orElseThrow(() -> new RoleNotFoundException("Role " + UserRole.ROLE_ADMIN + " not found"));
            } else {
                role = roleRepository.findByRoleName(UserRole.ROLE_COSTUMER).orElseThrow(() -> new RoleNotFoundException("Role " + UserRole.ROLE_COSTUMER + " not found"));
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

        return userRepository.save(newUser);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void generatePasswordResetToken(String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isOAuth()) {
            throw new IllegalArgumentException("Password reset is not allowed for this account");
        }

        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plus(2, ChronoUnit.HOURS);

        PasswordResetToken resetToken = new PasswordResetToken(token, expiryDate, user);

        passwordResetTokenRepository.save(resetToken);

        String resetUrl = frontendUrl + "/reset-password?token=" + token;

        emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
    }


    @Override
    public void resetPassword(String token, String password) {

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Password reset token"));

        if (resetToken.isUsed()) {
            throw new IllegalStateException("Password reset token already used");
        }

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new ExpiredTokenException("Password reset token expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

    }
}
