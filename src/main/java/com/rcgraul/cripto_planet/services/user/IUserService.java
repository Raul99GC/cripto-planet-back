package com.rcgraul.cripto_planet.services.user;

import java.util.Optional;

import com.rcgraul.cripto_planet.models.User;
import com.rcgraul.cripto_planet.security.request.SignupRequest;

public interface IUserService {

    User registerUser(SignupRequest signupRequest);

    User createUser(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
}
