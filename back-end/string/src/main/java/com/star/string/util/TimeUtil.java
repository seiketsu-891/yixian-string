package com.star.string.util;

import com.star.string.exception.DataException;
import com.star.string.exception.ValidatorException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {
    private static final String YMD_FORMAT = "yyyy-MM-dd";

    /***
     * 判断字符串是否是2022-11-03形式
     */
    public static boolean isYMDDate(String s ){
        boolean res= true;
        SimpleDateFormat format = new SimpleDateFormat(YMD_FORMAT);
        try{
            format.setLenient(false);
            Date date = format.parse(s);
        }catch (Exception e){
            res = false;
        }
        return res;
    }

    /**
     * 将指定时区的yyyy-MM-dd HH:mm:ss字符串转化为毫秒数
     */
    public static Long convertTimeTzToMills(String s, String tz ){
        Date date;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(tz));
        try {
             date = sdf.parse(s);
        }catch (Exception e){
            throw new DataException("时间格式有误");
        }
        return date.getTime();
    }


    public static String[] spitYMD(String date) {
        return date.split("-");
    }
}
