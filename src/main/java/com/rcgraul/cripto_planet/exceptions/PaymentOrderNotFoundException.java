package com.rcgraul.cripto_planet.exceptions;

public class PaymentOrderNotFoundException extends RuntimeException {
    public PaymentOrderNotFoundException(String message) {
        super(message);
    }
}
