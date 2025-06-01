package com.mdev.chatcord.client.token.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityMonitor {

    private volatile long lastActivityTimestamp = System.currentTimeMillis();

    public void markActivity() {
        lastActivityTimestamp = System.currentTimeMillis();
    }

    public boolean isUserActiveWithinMinutes(int minutes) {
        long now = System.currentTimeMillis();
        return (now - lastActivityTimestamp) < (long) minutes * 60 * 1000;
    }
}
