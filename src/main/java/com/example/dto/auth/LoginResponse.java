package com.example.dto.auth;

import com.example.enums.UserRole;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String token;
    private String type;
    private Integer userId;
    private String name;
    private String email;
    private UserRole role;
    private String message;
}