package com.example.services;

import com.example.entities.Engineer;
import com.example.entities.User;
import com.example.enums.UserRole;
import com.example.repositories.EngineerRepository;
import com.example.repositories.UserRepository;
import com.example.dto.auth.ForgotPasswordRequest;
import com.example.utils.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EngineerRepository engineerRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EncryptionUtil encryptionUtil;

    public User getProfileById(Integer userId, String loggedInEmail) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == UserRole.ENGINEER) {

            Engineer engineer = engineerRepository
                    .findById(user.getUserId())
                    .orElse(null);

            if (engineer != null) {
                user.setExperience(engineer.getExperience());
                user.setSkillSet(engineer.getSkillSet());
                user.setHomeLat(engineer.getHomeLat());
                user.setHomeLng(engineer.getHomeLng());
            }
        }

        return user;
    }

    public User updateProfile(String email, User updatedUser) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updatedUser.getName() != null)
            user.setName(updatedUser.getName());

        if (updatedUser.getPhone() != null)
            user.setPhone(updatedUser.getPhone());

        if (updatedUser.getAddress() != null)
            user.setAddress(updatedUser.getAddress());

        User savedUser = userRepository.save(user);

        if (user.getRole() == UserRole.ENGINEER) {

            Engineer engineer = engineerRepository
                    .findById(user.getUserId())
                    .orElseThrow(() -> new RuntimeException("Engineer not found"));

            if (updatedUser.getExperience() != null)
                engineer.setExperience(updatedUser.getExperience());

            if (updatedUser.getSkillSet() != null)
                engineer.setSkillSet(updatedUser.getSkillSet());

            if (updatedUser.getHomeLat() != null)
                engineer.setHomeLat(updatedUser.getHomeLat());

            if (updatedUser.getHomeLng() != null)
                engineer.setHomeLng(updatedUser.getHomeLng());

            engineerRepository.save(engineer);
        }

        emailService.sendProfileUpdateEmail(
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getUserId()
        );

        return savedUser;
    }

    public String resetPassword(ForgotPasswordRequest req) {

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getSecurityAnswer().equalsIgnoreCase(req.getAnswer())) {
            throw new RuntimeException("Invalid security answer");
        }

        String encryptedPwd = encryptionUtil.encodePassword(req.getNewPassword());
        user.setPassword(encryptedPwd);

        userRepository.save(user);

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                user.getName()
        );

        return "Password updated successfully";
    }

}