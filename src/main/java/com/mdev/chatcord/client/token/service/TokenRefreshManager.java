package com.mdev.chatcord.client.token.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdev.chatcord.client.authentication.dto.HttpRequest;
import com.mdev.chatcord.client.device.dto.DeviceDto;
import com.mdev.chatcord.client.token.dto.TokenFactory;
import com.mdev.chatcord.client.user.dto.ProfileDetails;
import com.mdev.chatcord.client.user.service.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.http.HttpResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenRefreshManager {

    private final TokenFactory tokenHolder;
    private final TokenService tokenService;
    private final User userProfile;
    private final HttpRequest httpRequest;
    private final DeviceDto deviceDto;
    private final UserActivityMonitor userActivityMonitor;

    @Autowired
    private ObjectMapper mapper;

    // ✅ Proactive Token Refresh
    @Scheduled(fixedRate = 14 * 60 * 1000, initialDelay = 14 * 60 * 1000)
    public void refreshIfActive() {
        if (userActivityMonitor.isUserActiveWithinMinutes(15)) {
            try {
                String newAccessToken = tokenService.refreshAccessToken(tokenHolder
                        .retrieveRefreshToken(userProfile.getUuid(), deviceDto.getDEVICE_ID()));
                tokenHolder.setAccessToken(newAccessToken);
                log.info("✅ Access token proactively refreshed.");
            } catch (Exception e) {
                log.error("❌ Proactive refresh failed: {}", e.getMessage());
            }
        } else {
            log.info("⏳ Skipped proactive refresh (user inactive).");
        }
    }

    // ✅ Reactive Token Refresh
    public boolean handle401AndRefreshIfExpired(HttpResponse<?> response) {
        if (response.statusCode() == 401 && isTokenExpired(response)) {
            try {
                String newAccessToken = tokenService.refreshAccessToken(tokenHolder
                        .retrieveRefreshToken(userProfile.getUuid(), deviceDto.getDEVICE_ID()));
                tokenHolder.setAccessToken(newAccessToken);
                log.info("🔁 Access token reactively refreshed.");
                return true; // caller can now retry
            } catch (Exception e) {
                log.error("❌ Reactive refresh failed: {}", e.getMessage());
            }
        }
        return false;
    }

    private boolean isTokenExpired(HttpResponse<?> response) {
        try {
            // assuming response body is JSON: { "code": "0002", ... }
            JsonNode root = mapper.readTree(response.body().toString());
            return root.has("code") && "0002".equals(root.get("code").asText());
        } catch (Exception e) {
            return false;
        }
    }
}