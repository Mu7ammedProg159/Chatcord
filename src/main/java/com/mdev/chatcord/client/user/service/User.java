package com.mdev.chatcord.client.user.service;

import com.mdev.chatcord.client.user.dto.ProfileDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

    private String uuid;
    private String email;
    private String username;
    private String tag;
    private String status;
    private String pfpUrl;
    private String avatarColor;
    private String about;
    private String quote;

    public void build(ProfileDetails profileDetails, String email) {
            this.uuid = profileDetails.getUuid();
            this.email = email;
            this.username = profileDetails.getUsername();
            this.tag = profileDetails.getTag();
            this.pfpUrl = profileDetails.getPfpUrl();
            this.avatarColor = profileDetails.getAvatarColor();
            this.about = profileDetails.getAbout();
            this.quote = profileDetails.getQuote();
            this.status = profileDetails.getStatus();
    }

}
