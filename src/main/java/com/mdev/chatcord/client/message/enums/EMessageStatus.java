package com.mdev.chatcord.client.message.enums;

public enum EMessageStatus {
    SENT, // 1 Check grayed out (message is sent to the sender by not the receiver)
    SEEN, // 2 Checks blue (both read and received the message)
    DELIVERED, // 2 Checks both grayed out (receiver didn't see it but received it)
    UNDELIVERED, // Clock (sender doesn't have connection)
    FAILED; // Message failed to send and will be lost.
}
