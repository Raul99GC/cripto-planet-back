package com.rcgraul.cripto_planet.models;

import com.rcgraul.cripto_planet.enums.VerificationType;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class TwoFactorAuth {

    private boolean isEAbled;
    private VerificationType sentTo;
}
