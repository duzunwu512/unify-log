package com.gzl.log.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String LONG_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static String MILLISECOND_FORMAT = "yyyy-MM-dd HH:mm:ss SSS";

    public static Date parseDate(String dateStr, String format) {
        Date date=null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            date = dateFormat.parse(dateStr);
        } catch (Exception e) {

        }
        return date;
    }

    public static String format(Date date, String format) {
        if(date==null){
            date = new Date();
        }
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
}
