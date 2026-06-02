package com.example.services;

import com.example.entities.UserOtp;
import com.example.repositories.UserOtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private UserOtpRepository userOtpRepository;

    @Autowired
    private EmailService emailService;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;


    public void generateAndSendOtp(String email) {

        String otp = generateOtp();

        userOtpRepository.findByEmailAndIsVerifiedFalse(email)
                .ifPresent(userOtpRepository::delete);

        UserOtp userOtp = UserOtp.builder()
                .email(email)
                .otp(otp)
                .isVerified(false)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .build();
        userOtpRepository.save(userOtp);

        emailService.sendOtpEmail(email, otp);
    }

    /**
     * Verify OTP entered by user
     */
    public boolean verifyOtp(String email, String otp) {
        var userOtpOptional = userOtpRepository.findByEmailAndOtp(email, otp);

        if (userOtpOptional.isEmpty()) {
            return false;
        }

        UserOtp userOtp = userOtpOptional.get();

        // Check if OTP is already verified
        if (userOtp.getIsVerified()) {
            return false;
        }

        // Check if OTP has expired
        if (LocalDateTime.now().isAfter(userOtp.getExpiresAt())) {
            return false;
        }

        // Mark OTP as verified
        userOtp.setIsVerified(true);
        userOtpRepository.save(userOtp);

        return true;
    }

    /**
     * Generate random 6-digit OTP
     */
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}