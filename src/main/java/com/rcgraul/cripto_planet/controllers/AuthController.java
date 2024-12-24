package com.rcgraul.cripto_planet.controllers;

import com.rcgraul.cripto_planet.models.User;
import com.rcgraul.cripto_planet.security.MessageResponse;
import com.rcgraul.cripto_planet.security.jwt.JwtUtils;
import com.rcgraul.cripto_planet.security.request.SigninRequest;
import com.rcgraul.cripto_planet.security.request.SignupRequest;
import com.rcgraul.cripto_planet.security.response.AuthResponse;
import com.rcgraul.cripto_planet.security.services.UserDetailsImpl;
import com.rcgraul.cripto_planet.security.services.UserDetailsServiceImpl;
import com.rcgraul.cripto_planet.services.user.UserService;
import com.rcgraul.cripto_planet.services.wallet.WalletService;
import com.rcgraul.cripto_planet.services.watchlist.WatchlistService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private WatchlistService watchlistService;

    @Autowired
    private WalletService walletService;

    @PostMapping("/signup")
    public ResponseEntity<?> register(@Valid @RequestBody SignupRequest userReq) {

        // Registrar el usuario
        User savedUser = userService.registerUser(userReq);

        // Crear Wallet
        walletService.createWallet(savedUser);

        // Crear Watchlist
        watchlistService.createWatchlist(savedUser);

        // Cargar los detalles del usuario recién registrado
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(userReq.getEmail());

        Authentication auth = new UsernamePasswordAuthenticationToken(userReq.getEmail(), userReq.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = jwtUtils.generateTokenFromUserDetails(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        AuthResponse res = new AuthResponse();
        res.setJwtToken(jwt);
        res.setRefreshToken(refreshToken);
        res.setStatus(true);
        res.setMessage("User registered successfully!");

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> login(@Valid @RequestBody SigninRequest userReq) {
        Authentication authentication;

        authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userReq.getEmail(), userReq.getPassword()));

        // set the authentication
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // extrae informacion del usuario de la actual sesion
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUserDetails(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        AuthResponse res = new AuthResponse();
        res.setJwtToken(jwtToken);
        res.setRefreshToken(refreshToken);
        res.setStatus(true);
        res.setMessage("User authenticated successfully!");

        return new ResponseEntity<>(res, HttpStatus.OK);

    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) throws MessagingException {

        userService.generatePasswordResetToken(email);
        return ResponseEntity.ok(new MessageResponse("password reset email sent"));

    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String password) {

        userService.resetPassword(token, password);
        return ResponseEntity.ok("Password reset successfully.");

    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        String jwtToken = jwtUtils.generateTokenFromRefreshToken(refreshToken);
        AuthResponse res = new AuthResponse();
        res.setJwtToken(jwtToken);
        res.setStatus(true);
        res.setMessage("Token refreshed successfully!");
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
