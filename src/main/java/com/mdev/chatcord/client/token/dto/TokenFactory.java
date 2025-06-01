package com.mdev.chatcord.client.token.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class TokenFactory {
    private volatile String accessToken;
    private volatile String refreshToken;


    public String retrieveRefreshToken(String userUuid, String deviceId) {
        String appData = System.getenv("APPDATA"); // This resolves to AppData/Roaming on Windows
        if (appData == null) {
            throw new IllegalStateException("APPDATA environment variable not found. Are you on Windows?");
        }

        Path dir = Path.of(appData, "Chatcord", String.valueOf(userUuid));
        Path tokenFile = dir.resolve(deviceId + "-refresh.key");

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
        // Build path: %APPDATA%/Chatcord/<uuid>/<device_id>-refresh.key
        String appData = System.getenv("APPDATA"); // This resolves to AppData/Roaming on Windows
        if (appData == null) {
            throw new IllegalStateException("APPDATA environment variable not found. Are you on Windows?");
        }

        String refreshToken = retrieveRefreshToken(uuid, deviceId);
        if (refreshToken != null)
            return;

        Path dir = Path.of(appData, "Chatcord", uuid);
        Path tokenFile = dir.resolve(deviceId + "-refresh.key");

        // Create directories if needed
        Files.createDirectories(dir);

        // Write the token to the file (overwrite mode)
        Files.writeString(tokenFile, token, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        this.refreshToken = token;

        log.info("Refresh token saved to: {}", tokenFile);
    }

    public void deleteRefreshKeyFile(String deviceId, String uuid){
        // Build path: %APPDATA%/Chatcord/<uuid>/refresh.key
        String appData = System.getenv("APPDATA"); // This resolves to AppData/Roaming on Windows
        if (appData == null) {
            throw new IllegalStateException("APPDATA environment variable not found. Are you on Windows?");
        }

        String refreshToken = retrieveRefreshToken(uuid, deviceId);

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
