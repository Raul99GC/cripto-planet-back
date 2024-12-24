package com.rcgraul.cripto_planet.services.paymentDetails;

import com.rcgraul.cripto_planet.models.PaymentDetails;
import com.rcgraul.cripto_planet.models.User;

import java.util.UUID;

public interface IPaymentDetails {

    public PaymentDetails addPaymentDetails(String accountNumber, String accountHolderName, String ifsc, String bankName, User user);

    public PaymentDetails getUsersPaymentDetails(UUID userId);
}
