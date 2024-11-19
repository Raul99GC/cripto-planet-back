package com.rcgraul.cripto_planet.services.twoFactorOTP;

import java.util.UUID;

import com.rcgraul.cripto_planet.models.TwoFactorOTP;
import com.rcgraul.cripto_planet.models.User;

public interface ITwoFactorOTPService {

    TwoFactorOTP createTwoFactorOtp(User user, int otpCode, String jwt);

    TwoFactorOTP findByUser(UUID userId);

    TwoFactorOTP findById(String id);

    boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOtp, int otpCode);

    void deleteTwofactorOtp(TwoFactorOTP twoFactorOtp);
}
