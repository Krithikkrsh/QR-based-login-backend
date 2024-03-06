package com.sample.login.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LoginRequest {
    private String userName;
    private String password;
    private String token;
}
