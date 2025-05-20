package com.mdev.chatcord.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Slf4j
@Component
public class DeviceDto {
    private static final String FILE_NAME = "session.info";
    private static final Path STORAGE_PATH = Paths.get(System.getenv("APPDATA"), "Chatcord", FILE_NAME);
    @JsonProperty("DEVICE_ID")
    private String DEVICE_ID;
    @JsonProperty("DEVICE_NAME")
    private String DEVICE_NAME;
    @JsonProperty("OS")
    private String OS;
    @JsonProperty("OS_VERSION")
    private String OS_VERSION;

    public DeviceDto() {
        this.DEVICE_ID = getOrCreateDeviceId();
        try {
            this.DEVICE_NAME = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            this.DEVICE_NAME = "UNAVAILABLE";
            log.error(e.getMessage());
        }
        this.OS = System.getProperty("os.name");
        this.OS_VERSION = System.getProperty("os.version");
    }

    private String getOrCreateDeviceId() {
        try {
            if (Files.exists(STORAGE_PATH)) {
                return Files.readString(STORAGE_PATH);
            } else {
                String deviceId = UUID.randomUUID().toString();
                Files.createDirectories(STORAGE_PATH.getParent());
                Files.writeString(STORAGE_PATH, deviceId);
                return deviceId;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to retrieve or generate device ID", e);
        }
    }
}
