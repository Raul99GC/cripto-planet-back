package com.rcgraul.cripto_planet.models;

import com.rcgraul.cripto_planet.enums.VerificationType;

import lombok.Data;

@Data
public class TwoFactorAuth {

    private boolean isEAbled;
    private VerificationType sentTo;
}
