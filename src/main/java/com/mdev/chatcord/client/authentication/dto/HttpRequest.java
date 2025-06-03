package com.mdev.chatcord.client.authentication.dto;

import com.mdev.chatcord.client.device.dto.DeviceDto;
import com.mdev.chatcord.client.token.dto.TokenFactory;
import com.mdev.chatcord.client.user.dto.ProfileDetails;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

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
    private String friendUri = requestUri + "/users/friends";

    public String uriParams(String path, String... keyValueParams){

        if (keyValueParams.length % 2 != 0) {
            throw new IllegalArgumentException("HttpUriError: Key-value pairs must be even.");
        }

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(domain)
                .path(path);

        for (int i = 0; i < keyValueParams.length; i += 2) {
            builder.queryParam(keyValueParams[i], keyValueParams[i + 1]);
        }

        return builder.build().toUriString();
    }
}
