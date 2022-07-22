package com.capol.amis.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATE_YYYY_MM_FORMAT = "yyyy-MM";
    public static final String DEFAULT_DATE_YYYY_FORMAT = "yyyy";
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String CST_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

    /**
     * date类型进行格式化输出（返回类型：String）
     *
     * @param date
     * @return
     */
    public static String dateFormat(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
        String dateString = formatter.format(date);
        return dateString;
    }


    /**
     * 将"2015-08-31 21:08:06"型字符串转化为Date
     *
     * @param str
     * @return
     * @throws ParseException
     */
    public static Date StringToDate(String str) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
        Date date = (Date) formatter.parse(str);
        return date;
    }


    /**
     * 将CST时间类型字符串进行格式化输出
     *
     * @param str
     * @return
     * @throws ParseException
     */
    public static String CSTFormat(String str) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(CST_DATE_FORMAT, Locale.CHINA);
        Date date = (Date) formatter.parse(str);
        return dateFormat(date);
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 将long类型转化为Date
     *
     * @param str
     * @return
     * @throws ParseException
     */
    public static Date LongToDare(long str) throws ParseException {
        return new Date(str * 1000);
    }

    /**
     * 日期格式化按表达式
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String formatDate(Date date, Object... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, DEFAULT_DATE_FORMAT);
        }

        return formatDate;
    }
}
