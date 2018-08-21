package com.example.android.newsapp1;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class ChUtils {

    public static Date chgFromISO8601UTC(String dateISO8601UtcObject, String dateFormat){
        Date date = null;
        SimpleDateFormat formatDateObjectISO8601 = new SimpleDateFormat(dateFormat);
        try {
            date = formatDateObjectISO8601.parse(dateISO8601UtcObject);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return date;
    }
}
