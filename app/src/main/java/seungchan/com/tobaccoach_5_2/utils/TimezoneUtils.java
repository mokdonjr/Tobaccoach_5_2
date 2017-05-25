package seungchan.com.tobaccoach_5_2.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by USER on 2017-05-07.
 */

public class TimezoneUtils {
    private static String TAG = "TimezoneUtils";
    private static String DS3231_PROTOCOL = "T";
    private static int DATE_TIME_TOKEN_NUM_FROM_HW = 6; // yy, MM, dd, HH, mm, ss
    private static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"; // "yyyy-MM-dd'T'HH:mm:ss.SSS+09:00"

    public static String getCurrentDateTime(){

        // 1. 현재 시간
        long now = System.currentTimeMillis();
        // 2. Date 타입
        Date date = new Date(now);
        // 3. Formatting
        SimpleDateFormat iso8601Format = new SimpleDateFormat(DATE_TIME_FORMAT); // format type
        String formatDate = iso8601Format.format(date);

        Log.d(TAG, "getCurrentDateTime메서드, 결과 : " + formatDate);
        return formatDate;
    }

    // 현재 시각 HH:MM:SS 만 반환
    public static String getCurrentTime(){

        String currentDateTime = getCurrentDateTime();
        String currentTime = currentDateTime.substring(12); // HH:mm:ss 반환
        Log.d(TAG, "getCurrentTime 결과 : " + currentTime);
        return currentTime;
    }

    // 현재로부터 last : yyyy-mm-dd hh:mm:ss 간 시간차이 반환시 HH, MM, SS를 3개의 String으로
    public static List<String> getElapsedTime(String last){
        List<String> returnTime = returnTime = new ArrayList<String>();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat iso8601Format = new SimpleDateFormat(DATE_TIME_FORMAT);

        long beginTime = 0;
        try {
            Date beginDateTime = iso8601Format.parse(getCurrentDateTime());
            Date endDateTime = iso8601Format.parse(last);

            long gap = (beginDateTime.getTime() - endDateTime.getTime()) / 1000; // 초단위
            Log.d(TAG, "getElapsedTime메서드, gap : " + gap);
            long hourGap = gap/60/60;
            Log.d(TAG, "getElapsedTime메서드, hourGap : " + hourGap);
            long minuteGap = ((long)(gap/60))%60;
            Log.d(TAG, "getElapsedTime메서드, minuteGap : " + minuteGap);
            long secondGap = gap % 60;
            Log.d(TAG, "getElapsedTime메서드, secondGap : " + secondGap);

            if(hourGap > 99){
                hourGap = (long)99;
            }

            returnTime.add(String.valueOf(hourGap));
            returnTime.add(String.valueOf(minuteGap));
            returnTime.add(String.valueOf(secondGap));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnTime;
    }

    public static Date parseStringToDateClass(String dateTime) {
        Log.d(TAG, "parseStringToDateClass메서드, dateTime문자열 : " + dateTime);
        SimpleDateFormat iso8601Format = new SimpleDateFormat(DATE_TIME_FORMAT);
        Date date = null;
        try {
            date = iso8601Format.parse(dateTime);
            Log.d(TAG, "parseStringToDateClass메서드, iso8601Format.parse()에 인자로 dateTime이 들어간 후 Date타입으로 변환된 결과 : " + date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getTodayDate(){ // yyyy-MM-dd 까지만
        String currentDateTime = getCurrentDateTime();
        StringTokenizer token = new StringTokenizer(currentDateTime, " ");
        String todayDate = token.nextToken();
        Log.d(TAG, "getTodayDate() 메서드 yyyy-MM-dd만 추출해서 where절에서 쓰임. 결과 : " + todayDate);
        return todayDate;
    }

    public static String getDS3231DateTime(){ // 안드로이드 시간 -> 아두이노 시간 포멧
        String finalDS3231DateTime = "";

        String getCurDateTime = getCurrentDateTime();
        Log.d(TAG, "getDS3231DateTime 메서드에서 이용할 현재 시각 : " + getCurDateTime);
        String yy = getCurDateTime.substring(2, 4); // yy
        String MM = getCurDateTime.substring(5, 7); // MM
        String dd = getCurDateTime.substring(8, 10); // dd
        String HH = getCurDateTime.substring(11, 13); // HH
        String mm = getCurDateTime.substring(14, 16); // mm
        String ss = getCurDateTime.substring(17, 19); // ss
        Log.d(TAG, "getDS3231DateTime 메서드에서 분리한 현재 시각 : " + yy + "." + MM + "." + dd + "." + HH + "." + mm + "." + ss);

        finalDS3231DateTime = finalDS3231DateTime.concat(DS3231_PROTOCOL); // 1. T
        finalDS3231DateTime = finalDS3231DateTime.concat(yy); // 2. yy
        finalDS3231DateTime = finalDS3231DateTime.concat(MM); // 3. MM
        finalDS3231DateTime = finalDS3231DateTime.concat(dd); // 4. dd
        finalDS3231DateTime = finalDS3231DateTime.concat(HH); // 5. HH
        finalDS3231DateTime = finalDS3231DateTime.concat(mm); // 6. mm
        finalDS3231DateTime = finalDS3231DateTime.concat(ss); // 7. ss
        finalDS3231DateTime = finalDS3231DateTime.concat(String.valueOf(doDayOfWeek())); // 8. 요일

        Log.d(TAG, "getDS3231DateTime 메서드 결과 : " + finalDS3231DateTime);
        return finalDS3231DateTime;
    }

    // 요일
    public static int doDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        String strWeek = null;

        int nWeek = cal.get(Calendar.DAY_OF_WEEK);

        Log.d(TAG, "doDayOfWeek 메서드. 오늘의 요일 번호 : " + nWeek);
        return nWeek;
    }

    // 아두이노는 현재 YY/MM/DD-HH:MM:SS 포멧이며
    // 앱과 웹은 현재 yy-MM-dd HH:mm:ss 포멧을 사용한다,
    public static String formatHwDateTime(String dateString){ // 아두이노 시간 -> 안드로이드 시간 포멧

        String finalDateTime = ""; // dateString standard form 변환

        // 1. parsing dateString(YY/MM/DD-HH:MM:SS) deli = '/', '-', ':'
        StringTokenizer tokensDateString = new StringTokenizer(dateString, "/-:");

        int numTokensDateString = tokensDateString.countTokens(); // 토큰 개수
        if (numTokensDateString == DATE_TIME_TOKEN_NUM_FROM_HW) {
            for (int i = 0; i < numTokensDateString; i++) {
                String token = tokensDateString.nextToken();
                Log.d(TAG, "insertLogDAte 메서드, 현재 토큰 : " + token + ", 반복 회수 : " + i);

                // '년'
                if (i == 0) {
                    finalDateTime = finalDateTime.concat("20");// '년' 은 20xx (HW의 EEPROM의 한 공간이 8bit이기에 256년을 넘을 수 없어 두 글자만 전송)
                    finalDateTime = finalDateTime.concat(token); // 년 삽입
                    finalDateTime = finalDateTime.concat("-");
                }
                // '월'
                if (i == 1) {
                    // '월' 이 1글자일때
                    if (token.length() == 1) {
                        finalDateTime = finalDateTime.concat("0");
                    }
                    finalDateTime = finalDateTime.concat(token);
                    finalDateTime = finalDateTime.concat("-");
                }
                // '일'
                if (i == 2) {
                    // '일' 이 1글자일때
                    if (token.length() == 1) {
                        finalDateTime = finalDateTime.concat("0");
                    }
                    finalDateTime = finalDateTime.concat(token);
                    finalDateTime = finalDateTime.concat(" "); // Date와 Time을 공백으로 분리
                }
                // '시'
                if (i == 3) {
                    // '시' 이 1글자일때
                    if (token.length() == 1) {
                        finalDateTime = finalDateTime.concat("0");
                    }
                    finalDateTime = finalDateTime.concat(token);
                    finalDateTime = finalDateTime.concat(":");
                }
                // '분'
                if (i == 4) {
                    // '분' 이 1글자일때
                    if (token.length() == 1) {
                        finalDateTime = finalDateTime.concat("0");
                    }
                    finalDateTime = finalDateTime.concat(token);
                    finalDateTime = finalDateTime.concat(":");
                }
                // '초'
                if (i == 5) {
                    // '시' 이 1글자일때
                    if (token.length() == 1) {
                        finalDateTime = finalDateTime.concat("0");
                    }
                    finalDateTime = finalDateTime.concat(token);
                }
                Log.d(TAG, "insertLogDAte 메서드, finalDateTime : " + finalDateTime);
            }

            return finalDateTime;
        } else {
            Log.d(TAG, "insertLogDate메서드, dateString의 countTokens결과가 " + DATE_TIME_TOKEN_NUM_FROM_HW
                    + " 이 아닙니다. ( " + numTokensDateString + " )");
            return null;
        }
    }

    // 아두이노와 안드로이드 날짜비교로 시간동기화 체크
    public static boolean checkTimeSyncState(String dateString){
        String hwDateTime = TimezoneUtils.formatHwOnlyDate(dateString); // 아두이노 yyyy-mm-dd
        String nowDateTime = TimezoneUtils.getTodayDate(); // 안드로이드 yyyy-MM-dd
        Log.d(TAG, "checkTimeSyncState메서드, hwDateTime은 : " + hwDateTime + " , nowDateTime은 : " + nowDateTime + " , 비교결과 : " + hwDateTime.equals(nowDateTime));

        return hwDateTime.equals(nowDateTime);
    }

    public static String formatHwOnlyDate(String dateString){
        String finalDateTime = ""; // dateString standard form 변환

        // 1. parsing dateString(YY/MM/DD-HH:MM:SS) deli = '/', '-', ':'
        StringTokenizer tokensDateString = new StringTokenizer(dateString, "/-:");

        int numTokensDateString = tokensDateString.countTokens(); // 토큰 개수
        if (numTokensDateString == DATE_TIME_TOKEN_NUM_FROM_HW) {
            for (int i = 0; i < numTokensDateString; i++) {
                String token = tokensDateString.nextToken();
                Log.d(TAG, "insertLogDAte 메서드, 현재 토큰 : " + token + ", 반복 회수 : " + i);

                // '년'
                if (i == 0) {
                    finalDateTime = finalDateTime.concat("20");// '년' 은 20xx (HW의 EEPROM의 한 공간이 8bit이기에 256년을 넘을 수 없어 두 글자만 전송)
                    finalDateTime = finalDateTime.concat(token); // 년 삽입
                    finalDateTime = finalDateTime.concat("-");
                }
                // '월'
                if (i == 1) {
                    // '월' 이 1글자일때
                    if (token.length() == 1) {
                        finalDateTime = finalDateTime.concat("0");
                    }
                    finalDateTime = finalDateTime.concat(token);
                    finalDateTime = finalDateTime.concat("-");
                }
                // '일'
                if (i == 2) {
                    // '일' 이 1글자일때
                    if (token.length() == 1) {
                        finalDateTime = finalDateTime.concat("0");
                    }
                    finalDateTime = finalDateTime.concat(token);
                }
                Log.d(TAG, "insertLogDAte 메서드, finalDateTime : " + finalDateTime);
            }

            return finalDateTime;
        } else {
            Log.d(TAG, "insertLogDate메서드, dateString의 countTokens결과가 " + DATE_TIME_TOKEN_NUM_FROM_HW
                    + " 이 아닙니다. ( " + numTokensDateString + " )");
            return null;
        }
    }

}
