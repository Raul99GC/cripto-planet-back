package com.rcgraul.cripto_planet.exceptions;

public class CoinNotFoundException extends RuntimeException {

    public CoinNotFoundException(String message) {
        super(message);
    }

}
