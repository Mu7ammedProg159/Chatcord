package com.mdev.chatcord.client.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Slf4j
public class HttpRequest {

    @Value("${chatcord.server.domain.url}")
    private String domain;
    private String authUri = "/api/auth";
    private String requestUri = "/api/request";
    private String friendUri = requestUri + "/users/friend";
    private Profile userDTO;
    private String accessToken;
    private String refreshToken;

    @Autowired
    private DeviceDto deviceDto;

    public String getRefreshToken() {
        String appData = System.getenv("APPDATA"); // This resolves to AppData/Roaming on Windows
        if (appData == null) {
            throw new IllegalStateException("APPDATA environment variable not found. Are you on Windows?");
        }

        Path dir = Path.of(appData, "Chatcord", String.valueOf(this.userDTO.getUuid()));
        Path tokenFile = dir.resolve(deviceDto.getDEVICE_ID() + "-refresh.key");

        if (tokenFile.toFile().exists()) {
            String refreshToken = null;
            try {
                refreshToken = Files.readString(tokenFile);
            } catch (IOException e) {
                log.info("REFRESH KEY NOT FOUND - CONSIDERED FIRST LAUNCH.");
            }
            log.info("Key loaded on App Running as a Refresh Key: {}", refreshToken);
            return refreshToken;
        }
        return null;
    }

    public void setRefreshToken(String token, String deviceId, String uuid) throws IOException {
        // Build path: %APPDATA%/Chatcord/<uuid>/refresh.key
        String appData = System.getenv("APPDATA"); // This resolves to AppData/Roaming on Windows
        if (appData == null) {
            throw new IllegalStateException("APPDATA environment variable not found. Are you on Windows?");
        }

        String refreshToken = getRefreshToken();
        if (refreshToken != null)
            return;

        Path dir = Path.of(appData, "Chatcord", uuid);
        Path tokenFile = dir.resolve(deviceId + "-refresh.key");

        // Create directories if needed
        Files.createDirectories(dir);

        // Write the token to the file (overwrite mode)
        Files.writeString(tokenFile, token, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        log.info("Refresh token saved to: {}", tokenFile);
    }

    public void deleteRefreshKeyFile(String deviceId, String uuid){
        // Build path: %APPDATA%/Chatcord/<uuid>/refresh.key
        String appData = System.getenv("APPDATA"); // This resolves to AppData/Roaming on Windows
        if (appData == null) {
            throw new IllegalStateException("APPDATA environment variable not found. Are you on Windows?");
        }

        String refreshToken = getRefreshToken();

        Path dir = Path.of(appData, "Chatcord", uuid);
        Path tokenFile = dir.resolve(deviceId + "-refresh.key");

        try {
            Files.delete(tokenFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Refresh token has been deleted from: {}", tokenFile);
    }
}
