package com.sample.login.service;

import com.sample.login.dto.LoginRequest;
import com.sample.login.dto.LoginResponse;
import com.sample.login.dto.QrDetailEntity;

public interface LoginServiceProvider {

    QrDetailEntity generateQrCode();

    LoginResponse loginUser(LoginRequest request);

    String getLoggedUserName();

}
