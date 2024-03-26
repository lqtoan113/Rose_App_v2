package com.rose.utils;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Component
public class XDateUtils {
    public static String convertToPattern(Date yourDate, String pattern) {
        return new SimpleDateFormat(pattern).format(yourDate);
    }

    public static String getCurrentTime(){
        Calendar CALENDAR = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        return convertToPattern(CALENDAR.getTime(), "yyyyMMddHHmmss");
    }

    public static String getTimeAfterFifteenMinutes(){
        Calendar CALENDAR = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        CALENDAR.add(Calendar.MINUTE, 15);
        return convertToPattern(CALENDAR.getTime(), "yyyyMMddHHmmss");
    }
}
