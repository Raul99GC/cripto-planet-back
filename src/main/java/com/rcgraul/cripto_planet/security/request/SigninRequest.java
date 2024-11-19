package com.rcgraul.cripto_planet.security.request;

import com.rcgraul.cripto_planet.models.TwoFactorAuth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SigninRequest {

    @NotBlank
    @Email
    @Size(max = 50)
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private TwoFactorAuth twoFactorAuth;
}
