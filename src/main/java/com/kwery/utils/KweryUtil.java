package com.kwery.utils;

import java.text.SimpleDateFormat;

public class KweryUtil {
    public static final String CSV_FILE_NAME_DATE_PART = "EEE-MMM-dd";

    public static String fileName(String title, long epoch) {
        return (title + " " + new SimpleDateFormat(CSV_FILE_NAME_DATE_PART).format(epoch)).toLowerCase().trim().replaceAll("\\s+", "-") + ".csv";
    }
}
