package com.rcgraul.cripto_planet.services.user;

import com.rcgraul.cripto_planet.models.User;
import com.rcgraul.cripto_planet.security.request.SignupRequest;

public interface IUserService {

    User registerUser(SignupRequest signupRequest);
}
