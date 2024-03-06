package com.sample.login.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserEntity {
    private String userName;
    private String password;
}
