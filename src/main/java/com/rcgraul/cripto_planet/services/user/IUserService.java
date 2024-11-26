package com.rcgraul.cripto_planet.services.user;

import com.rcgraul.cripto_planet.models.User;
import com.rcgraul.cripto_planet.security.request.SignupRequest;

import java.util.Optional;

public interface IUserService {

    User registerUser(SignupRequest signupRequest);

    User createUser(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    void resetPassword(String token, String password);
}
