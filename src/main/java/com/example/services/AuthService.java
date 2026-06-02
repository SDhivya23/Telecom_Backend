package com.example.services;

import com.example.dto.auth.*;
import com.example.entities.Engineer;
import com.example.entities.User;
import com.example.enums.EngineerStatus;
import com.example.enums.UserRole;
import com.example.repositories.EngineerRepository;
import com.example.repositories.UserOtpRepository;
import com.example.repositories.UserRepository;
import com.example.security.JwtTokenProvider;
import com.example.utils.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EngineerRepository engineerRepository;

    @Autowired
    private UserOtpRepository userOtpRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private EncryptionUtil encryptionUtil;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;


    public OtpResponse initiateRegistration(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return OtpResponse.builder()
                    .success(false)
                    .message("Email already exists")
                    .email(request.getEmail())
                    .build();
        }

        UserRole role = UserRole.valueOf(request.getRole().toUpperCase());

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password("")
                .role(role)
                .phone(request.getPhone())
                .address(request.getAddress())
                .securityQuestion(request.getSecurityQuestion())
                .securityAnswer(request.getSecurityAnswer())
                .build();

        User saved = userRepository.save(user);


        if (role == UserRole.ENGINEER) {
            Engineer engineer = Engineer.builder()
                    .user(saved)
                    .experience(request.getExperience() != null ? request.getExperience() : 0)
                    .skillSet(request.getSkillSet())
                    .status(EngineerStatus.AVAILABLE)
                    .homeLat(request.getHomeLat())
                    .homeLng(request.getHomeLng())
                    .build();
            engineerRepository.save(engineer);
        }


        otpService.generateAndSendOtp(request.getEmail());

        return OtpResponse.builder()
                .success(true)
                .message("Registered. OTP sent")
                .email(request.getEmail())
                .build();
    }

    public OtpResponse verifyOtp(VerifyOtpRequest request) {

        boolean valid = otpService.verifyOtp(request.getEmail(), request.getOtp());

        if (!valid) {
            return OtpResponse.builder()
                    .success(false)
                    .message("Invalid OTP")
                    .email(request.getEmail())
                    .build();
        }

        return OtpResponse.builder()
                .success(true)
                .message("OTP verified")
                .email(request.getEmail())
                .build();
    }


    @Transactional
    public RegisterResponse setPassword(SetPasswordRequest request) {

        var otp = userOtpRepository
                .findTopByEmailAndIsVerifiedTrueOrderByCreatedAtDesc(request.getEmail());
        if (otp.isEmpty()) {
            return RegisterResponse.builder()
                    .success(false)
                    .message("OTP not verified")
                    .build();
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return RegisterResponse.builder()
                    .success(false)
                    .message("Password mismatch")
                    .build();
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(encryptionUtil.encodePassword(request.getPassword()));

        userRepository.save(user);

        emailService.sendWelcomeEmail(
                user.getEmail(),
                user.getName(),
                user.getUserId()
        );
        return RegisterResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .success(true)
                .message("Password set successfully")
                .build();
    }


    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new RuntimeException("Password not set yet");
        }

        if (!encryptionUtil.matchPassword(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtTokenProvider.generateToken(
                user.getUserId(),
                user.getEmail(),
                user.getRole()
        );

        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .message("Login success")
                .build();
    }
}