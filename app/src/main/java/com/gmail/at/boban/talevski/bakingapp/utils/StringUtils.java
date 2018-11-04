package com.gmail.at.boban.talevski.bakingapp.utils;

public final class StringUtils {
    public static String capitalizeFirstLetter(String string) {
        if (string == null || string.isEmpty()) {
            return string;
        }
        return string.substring(0,1).toUpperCase() + string.substring(1);
    }
}
