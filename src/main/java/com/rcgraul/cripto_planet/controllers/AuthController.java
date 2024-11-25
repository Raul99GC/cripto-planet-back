package com.rcgraul.cripto_planet.controllers;

import com.rcgraul.cripto_planet.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rcgraul.cripto_planet.exceptions.EmailAlreadyExistsException;
import com.rcgraul.cripto_planet.exceptions.UsernameAlreadyExistsException;
import com.rcgraul.cripto_planet.security.MessageResponse;
import com.rcgraul.cripto_planet.security.jwt.JwtUtils;
import com.rcgraul.cripto_planet.security.request.SigninRequest;
import com.rcgraul.cripto_planet.security.request.SignupRequest;
import com.rcgraul.cripto_planet.security.response.AuthResponse;
import com.rcgraul.cripto_planet.security.services.UserDetailsServiceImpl;
import com.rcgraul.cripto_planet.services.user.UserService;

import jakarta.validation.Valid;

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

    @PostMapping("/signup")
    public ResponseEntity<?> register(@Valid @RequestBody SignupRequest userReq) {

        try {
            // Registrar el usuario
            userService.registerUser(userReq);

        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("An unexpected error occurred"));
        }

        // Cargar los detalles del usuario recién registrado
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(userReq.getEmail());

        Authentication auth = new UsernamePasswordAuthenticationToken(userReq.getEmail(), userReq.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = jwtUtils.generateTokenFromEmail(userDetails);

        AuthResponse res = new AuthResponse();
        res.setJwtToken(jwt);
        res.setStatus(true);
        res.setMessage("User registered successfully!");

        return new ResponseEntity<>(res, HttpStatus.CREATED);

    }

    @PostMapping("/signin")
    public ResponseEntity<?> login(@Valid @RequestBody SigninRequest userReq) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userReq.getEmail(), userReq.getPassword()));
        } catch (AuthenticationException e) {
            System.out.println(e);
            throw new BadCredentialsException("Invalid credentials", e);
        }

        // set the authentication
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // extrae informacion del usuario de la actual sesion
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromEmail(userDetails);

        AuthResponse res = new AuthResponse();
        res.setJwtToken(jwtToken);
        res.setStatus(true);
        res.setMessage("User authenticated successfully!");

        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);

    }

}