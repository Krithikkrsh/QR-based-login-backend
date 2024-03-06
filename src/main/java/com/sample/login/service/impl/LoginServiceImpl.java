package com.sample.login.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sample.login.dto.LoginRequest;
import com.sample.login.dto.LoginResponse;
import com.sample.login.dto.QrDetailEntity;
import com.sample.login.dto.UserEntity;
import com.sample.login.service.LoginServiceProvider;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
public class LoginServiceImpl implements LoginServiceProvider {

    private Map<String, UserEntity> users = new HashMap<>();
    private String currentUser;

    @PostConstruct
    public void setUsers() {
        UserEntity user1 = UserEntity.builder()
                .userName("user 1")
                .password("pass 1")
                .build();
        UserEntity user2 = UserEntity.builder()
                .userName("user 2")
                .password("pass 2")
                .build();
        UserEntity user3 = UserEntity.builder()
                .userName("user 3")
                .password("pass 3")
                .build();
        users.put(user1.getUserName(), user1);
        users.put(user2.getUserName(), user2);
        users.put(user3.getUserName(), user3);
    }

    /**
     * Generate Qr method generated qr code with login api as its value,
     * it is a stream api
     * @return response based on its login response
     */
    @Override
    public QrDetailEntity generateQrCode() {
        log.info("generate qr code method called.");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYmmDDHHmmSS");
        Random random = new Random();
        String qrId = String.valueOf(random.nextInt(0000, 9999));
        String imageName = new StringBuilder("qr-")
                .append(LocalDateTime.now().format(formatter))
                .append(".png").toString();
        String path = "C:\\Users\\krshk\\Downloads\\LoginApp\\loginapp\\src\\images\\" + imageName;
        try {
            String data = "http://192.168.76.211:3000/loginPage";
            String charset = "UTF-8";
            Map<EncodeHintType, ErrorCorrectionLevel> hashMap
                    = new HashMap<>();
            hashMap.put(EncodeHintType.ERROR_CORRECTION,
                    ErrorCorrectionLevel.L);
            BitMatrix matrix = new MultiFormatWriter().encode(
                    new String(data.getBytes(charset), charset),
                    BarcodeFormat.QR_CODE, 300, 300);
            Path filePath = new File(path).toPath();
            MatrixToImageWriter.writeToPath(matrix, path.substring(path.lastIndexOf('.') + 1), filePath);
        } catch (Exception e) {
            log.error("[Error in mtd: [generateQrCode] cause: ", e);
        }
        log.info("Qr code generated..");
        String token = Base64.getEncoder().encodeToString("qr-token".getBytes());
        return QrDetailEntity.builder()
                .qrId(qrId)
                .qrCodePath(path)
                .imageName(imageName)
                .qrGeneratedTime(LocalDateTime.now())
                .authStatus(QrDetailEntity.AuthStatus.STARTED)
                .token(token)
                .build();
    }

    /**
     * login method verifies username and password with the in memory variables
     * @param request gets username and password
     * @return {@link LoginResponse} contains status
     */
    @Override
    public LoginResponse loginUser(LoginRequest request) {
        log.info("login user method called.");
        if (!users.containsKey(request.getUserName()) ||
                !users.get(request.getUserName()).getPassword().equalsIgnoreCase(request.getPassword())) {
            log.info("log in failed for user name: {}", request.getUserName());
            return LoginResponse.builder()
                    .status("404").build();
        }
        log.info("log in success for user name: {}", request.getUserName());
        this.currentUser = request.getUserName();
        return LoginResponse.builder()
                .status("200")
                .userName(request.getUserName())
                .build();
    }

    /**
     * This method returns the current username
     * @return
     */
    @Override
    public String getLoggedUserName() {
        log.info("Current logged in user: {}", this.currentUser);
        return this.currentUser;
    }
}
