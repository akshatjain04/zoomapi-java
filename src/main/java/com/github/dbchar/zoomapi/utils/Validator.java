package com.github.dbchar.zoomapi.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Validator {
    public static boolean stringIsNullOrEmpty(String... inputs) {
        for (String input : inputs) {
            if (input == null || input.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidEmail(String email) {
        final String regex = "^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$";
        if (!email.matches(regex)) {
            System.out.println("Email format is invalid");
            return false;
        }

        return true;
    }

    public static boolean isValidDateString(String dateString, String format) {
        if (stringIsNullOrEmpty(dateString)) {
            System.out.println("Date is empty");
            return false;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(format);
        formatter.setLenient(false);

        try {
            formatter.parse(dateString);
        } catch (ParseException e) {
            System.out.println(dateString + " is an invalid date format");
            return false;
        }
        return true;
    }
}
