package com.sample.login.controller;

import com.sample.login.dto.LoginRequest;
import com.sample.login.dto.LoginResponse;
import com.sample.login.dto.QrDetailEntity;
import com.sample.login.dto.UserEntity;
import com.sample.login.exception.InternalException;
import com.sample.login.service.LoginServiceProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(allowedHeaders = "*")
public class LoginController {
    private final LoginServiceProvider loginServiceProvider;
    private SseEmitter sseEmitter;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @GetMapping(value = "/getCurrentUser")
    public ResponseEntity<UserEntity> getCurrentUser() {
        try {
            log.info("inside get current user controller method");
            UserEntity userEntity = UserEntity.builder()
                    .userName(loginServiceProvider.getLoggedUserName())
                    .build();
            return ResponseEntity.ok(userEntity);
        }
        catch (Exception e){
            throw new InternalException(e.getMessage());
        }
    }

    @GetMapping(value = "/generateQrCode", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateQrCode() {
        try {
            log.info("inside generate qr code controller method");
            QrDetailEntity entity = loginServiceProvider.generateQrCode();
            this.sseEmitter = new SseEmitter(Long.MAX_VALUE);
            executorService.execute(() -> {
                try {
                    this.sseEmitter.send(entity);
                } catch (Exception e) {
                    log.error("Exception occurred: ",e);
                    this.sseEmitter.completeWithError(e);
                }
            });
            return this.sseEmitter;
        }catch (Exception e){
            throw new InternalException(e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            log.info("inside get login controller method req: {}",request);
            LoginResponse loginResponse = loginServiceProvider.loginUser(request);
            try {
                QrDetailEntity.QrDetailEntityBuilder entity = QrDetailEntity.builder();
                if (loginResponse.getStatus().equalsIgnoreCase("200")) {
                    entity.authStatus(QrDetailEntity.AuthStatus.SUCCESS);
                } else {
                    entity.authStatus(QrDetailEntity.AuthStatus.FAILED);
                }
                this.sseEmitter.send(entity.build());
            } catch (Exception e) {
                this.sseEmitter.completeWithError(e);
            }
            sseEmitter.complete();
            return ResponseEntity.ok(loginResponse);
        }
        catch (Exception e){
            throw new InternalException(e.getMessage());
        }
    }


}
