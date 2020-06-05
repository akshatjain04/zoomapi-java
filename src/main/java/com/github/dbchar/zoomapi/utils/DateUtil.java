package com.github.dbchar.zoomapi.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Wen-Chia, Yang on 2020-06-01.
 */
public class DateUtil {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static Date stringToDate(String dateString) throws ParseException {
        return new SimpleDateFormat(DATE_FORMAT).parse(dateString);
    }

    public static String dateToString(Date date) {
        return new SimpleDateFormat(DATE_FORMAT).format(date);
    }

    public static String getLocalDateTime(String dateString) {
        try {
            var gmtFormatter = new SimpleDateFormat(DATETIME_FORMAT);
            gmtFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));

            var localFormatter = new SimpleDateFormat(DATETIME_FORMAT);
            localFormatter.setTimeZone(TimeZone.getDefault());

            return localFormatter.format(gmtFormatter.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
