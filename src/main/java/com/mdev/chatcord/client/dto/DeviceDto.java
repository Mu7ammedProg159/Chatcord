package com.mdev.chatcord.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class DeviceDto {
    private String deviceId = UUID.randomUUID().toString();
    private String deviceName;
    private String os;
    private String osVersion;
    private String ip;

    public DeviceDto() {
        try {
            this.deviceId = InetAddress.getLocalHost().getHostName();
            this.ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        this.os = System.getProperty("os.name");
        this.osVersion = System.getProperty("os.version");
    }
}
