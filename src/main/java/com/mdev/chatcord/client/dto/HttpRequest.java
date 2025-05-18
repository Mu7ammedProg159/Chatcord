package com.mdev.chatcord.client.dto;

import lombok.*;
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
public class HttpRequest {

    @Value("${chatcord.server.domain.url}")
    private String domain;
    private String authUri = "/api/auth";
    private String requestUri = "/api/request";
    private String friendUri = requestUri + "/users/friend";
    private UserDTO userDTO;
    private String accessToken;
    private String refreshToken;


    public void setRefreshToken(String token, String deviceId) throws IOException {
        // Build path: %APPDATA%/Chatcord/<uuid>/refresh.token
        String appData = System.getenv("APPDATA"); // This resolves to AppData/Roaming on Windows
        if (appData == null) {
            throw new IllegalStateException("APPDATA environment variable not found. Are you on Windows?");
        }

        Path dir = Path.of(appData, "Chatcord", deviceId);
        Path tokenFile = dir.resolve("refresh.token");

        // Create directories if needed
        Files.createDirectories(dir);

        // Write the token to the file (overwrite mode)
        Files.writeString(tokenFile, token, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("Refresh token saved to: " + tokenFile);
    }
}
