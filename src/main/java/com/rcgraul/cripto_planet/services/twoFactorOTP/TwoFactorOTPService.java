package com.rcgraul.cripto_planet.services.twoFactorOTP;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rcgraul.cripto_planet.models.TwoFactorOTP;
import com.rcgraul.cripto_planet.models.User;
import com.rcgraul.cripto_planet.repositories.TwoFactorOTPRepository;

@Service
public class TwoFactorOTPService implements ITwoFactorOTPService {

    @Autowired
    private TwoFactorOTPRepository twoFactorOTPRepository;

    @Override
    public TwoFactorOTP createTwoFactorOtp(User user, int otpCode, String jwt) {

        TwoFactorOTP twoFactorOTP = new TwoFactorOTP();
        twoFactorOTP.setUser(user);
        twoFactorOTP.setOtpCode(otpCode);
        twoFactorOTP.setJwt(jwt);

        return twoFactorOTPRepository.save(twoFactorOTP);

    }

    @Override
    public TwoFactorOTP findByUser(UUID userId) {
        return twoFactorOTPRepository.findByUserId(userId);
    }

    @Override
    public TwoFactorOTP findById(String id) {
        Optional<TwoFactorOTP> opt = twoFactorOTPRepository.findById(UUID.fromString(id));
        return opt.orElse(null);
    }

    @Override
    public boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOtp, int otpCode) {
        return twoFactorOtp.getOtpCode() == otpCode;
    }

    @Override
    public void deleteTwofactorOtp(TwoFactorOTP twoFactorOtp) {
        twoFactorOTPRepository.delete(twoFactorOtp);
    }

}
