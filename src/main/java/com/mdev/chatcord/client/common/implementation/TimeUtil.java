package com.mdev.chatcord.client.common.implementation;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public interface TimeUtil {
    public default String convertToHourTime(long timestampMillis) {
        ZonedDateTime zoned = Instant.ofEpochMilli(timestampMillis).atZone(ZoneId.systemDefault());
        return zoned.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }
}
