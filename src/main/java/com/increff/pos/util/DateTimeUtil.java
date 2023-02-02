package com.increff.pos.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    public static ZonedDateTime getDateByString(String date) {
        date = date + " 00:00:00";
        String pattern = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter Parser = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(date, Parser).atZone(ZoneId.systemDefault());
    }
}
