package com.sample.login.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String status;
    private String userName;
}
