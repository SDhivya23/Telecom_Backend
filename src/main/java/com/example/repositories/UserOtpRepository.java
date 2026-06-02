package com.example.repositories;

import com.example.entities.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserOtpRepository extends JpaRepository<UserOtp, Integer> {
    Optional<UserOtp> findByEmailAndIsVerifiedFalse(String email);
    Optional<UserOtp> findByEmailAndOtp(String email, String otp);
    Optional<UserOtp> findTopByEmailAndIsVerifiedTrueOrderByCreatedAtDesc(String email);

}