package com.mdev.chatcord.client.user.enums;

import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public enum EUserState {
    ONLINE(Color.web("#32cd32")),
    OFFLINE(Color.web("#5a5a68")),
    DONOTDISTURB(Color.web("#d73434")),
    IDLE(Color.web("#ff8d35"));

    private final Color color;

}
