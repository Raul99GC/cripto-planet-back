package com.rcgraul.cripto_planet.security.response;

import lombok.Data;

@Data
public class AuthResponse {

    private String jwtToken;
    private String refreshToken;
    private boolean status;
    private String message;
    private boolean isTwoFactorAuthEnabled;
    private String session;
}
