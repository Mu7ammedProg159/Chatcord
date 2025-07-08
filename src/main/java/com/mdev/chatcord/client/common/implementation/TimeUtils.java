package com.mdev.chatcord.client.common.implementation;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    public static String convertToHourTime(long timestampMillis) {
        ZonedDateTime zoned = Instant.ofEpochMilli(timestampMillis).atZone(ZoneId.systemDefault());
        return zoned.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    public static String convertToLocalTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        return dateTime.format(formatter);
    }
}
