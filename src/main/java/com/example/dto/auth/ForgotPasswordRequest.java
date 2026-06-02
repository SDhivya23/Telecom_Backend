package com.example.dto.auth;

import lombok.Data;

@Data
public class ForgotPasswordRequest {

    private String email;
    private String answer;
    private String newPassword;
}
