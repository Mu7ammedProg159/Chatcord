package com.mdev.chatcord.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Slf4j
@Component
public class DeviceDto {
    private final String DEVICE_ID = UUID.randomUUID().toString();
    private String DEVICE_NAME;
    private String OS;
    private String OS_VERSION;

    public DeviceDto() {
        try {
            this.DEVICE_NAME = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            this.DEVICE_NAME = "UNAVAILABLE";
            log.error(e.getMessage());
        }
        this.OS = System.getProperty("os.name");
        this.OS_VERSION = System.getProperty("os.version");
    }
}
