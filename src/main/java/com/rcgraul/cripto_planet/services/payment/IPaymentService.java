package com.rcgraul.cripto_planet.services.payment;

import com.rcgraul.cripto_planet.enums.PaymentMethod;
import com.rcgraul.cripto_planet.models.PaymentOrder;
import com.rcgraul.cripto_planet.models.User;
import com.rcgraul.cripto_planet.response.PaymentResponse;
import com.stripe.exception.StripeException;

import java.util.UUID;

public interface IPaymentService {

    PaymentOrder createPaymentOrder(User user, Long amount, PaymentMethod paymentMethod);

    PaymentOrder getPaymentOrderById(UUID paymentOrderId);

    Boolean proccedPaymentOrder(PaymentOrder paymentOrder, String transactionId);

    PaymentResponse createStripePaymentLink(User user, Long amount, UUID orderId) throws StripeException;
}
