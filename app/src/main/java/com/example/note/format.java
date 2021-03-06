package com.example.note;

import android.annotation.SuppressLint;


import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class format {

    public static String timeFormat(int hour,int minute,int second){
        if (hour>23||hour<0||minute>59||minute<0||second>59||second<0) {
            return null;
        }
        String result;
        String strHour = String.valueOf(hour);
        String strMinute = String.valueOf(minute);
        String strSecond = String.valueOf(second);
        if (hour<10) {
            strHour = "0" + strHour;
        }
        if (minute<10) {
            strMinute = "0" + strMinute;
        }
        if (second<10) {
            strSecond = "0" + strSecond;
        }
        result = strHour + ":" + strMinute + ":" + strSecond;
        return result;
    }


    public static String timeFormat(int hour, int minute){
        if (hour>23||hour<0||minute>59||minute<0) {
            return null;
        }
        String result;
        String strHour = String.valueOf(hour);
        String strMinute = String.valueOf(minute);
        if (hour<10) {
            strHour = "0" + strHour;
        }
        if (minute<10) {
            strMinute = "0" + strMinute;
        }
        result = strHour + ":" + strMinute;
        return result;
    }


    @SuppressLint("SimpleDateFormat")
    public static String myDateFormat(Date date, @Nullable date dateFormatType){

        SimpleDateFormat dateFormat;
        if (dateFormatType == null){
            dateFormatType = com.example.note.date.NORMAL_TIME;
        }

        switch (dateFormatType){
            case NORMAL_TIME:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                break;
            case REMOVE_YEAR_TIME:
                dateFormat = new SimpleDateFormat("MM-dd HH:mm");
                break;
            case NORMAL_DATE:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                break;
            case REMOVE_YEAR_DATE:
                dateFormat = new SimpleDateFormat("MM-dd");
                break;
            case SPECIAL_TYPE:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                break;
            default:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                break;
        }

        return dateFormat.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static Date myDateFormat(String str,@Nullable date dateFormatType){
        SimpleDateFormat dateFormat;
        if (dateFormatType == null){
            dateFormatType = date.NORMAL_TIME;
        }
        switch (dateFormatType){
            case NORMAL_TIME:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                break;
            case REMOVE_YEAR_TIME:
                dateFormat = new SimpleDateFormat("MM-dd HH:mm");
                break;
            case NORMAL_DATE:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                break;
            case REMOVE_YEAR_DATE:
                dateFormat = new SimpleDateFormat("MM-dd");
                break;
            case SPECIAL_TYPE:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                break;
            default:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                break;
        }
        try {
            return dateFormat.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static Date myDateFormat(Integer year,
                                    Integer month,
                                    Integer day,
                                    Integer hour,
                                    Integer minute,
                                    @Nullable date dateFormatType){
        SimpleDateFormat dateFormat;
        String str;
        if (dateFormatType == null){
            dateFormatType = date.NORMAL_TIME;
        }
        switch (dateFormatType){
            case NORMAL_TIME:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                str = year+"-"+month+"-"+day+" "+hour+":"+minute;
                break;
            case REMOVE_YEAR_TIME:
                dateFormat = new SimpleDateFormat("MM-dd HH:mm");
                str = month+"-"+day+" "+hour+":"+minute;
                break;
            case NORMAL_DATE:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                str = year+"-"+month+"-"+day;
                break;
            case REMOVE_YEAR_DATE:
                dateFormat = new SimpleDateFormat("MM-dd");
                str = month+"-"+day;
                break;
            case SPECIAL_TYPE:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                str = year+"-"+month+"-"+day+"-"+hour+"-"+minute;
                break;
            default:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                str = year+"-"+month+"-"+day+" "+hour+":"+minute;
                break;
        }
        try {
            return dateFormat.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }



    public static String getTimeStr(Date date){
        int nowYear = new time(new Date(System.currentTimeMillis())).getYear();
        int targetYear = new time(date).getYear();
        if (nowYear == targetYear){

            return myDateFormat(date, com.example.note.date.REMOVE_YEAR_TIME);
        }
        return myDateFormat(date, com.example.note.date.NORMAL_TIME);
    }
}
