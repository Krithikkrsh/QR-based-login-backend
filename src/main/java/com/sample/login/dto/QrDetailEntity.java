package com.sample.login.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class QrDetailEntity {
    public enum AuthStatus{
        STARTED,
        SUCCESS,
        FAILED
    }
    private String qrId;
    private String imageName;
    private String qrCodePath;
    private LocalDateTime qrGeneratedTime;
    private AuthStatus authStatus;
    private String token;
}
