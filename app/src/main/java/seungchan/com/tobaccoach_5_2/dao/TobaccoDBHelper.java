package seungchan.com.tobaccoach_5_2.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import seungchan.com.tobaccoach_5_2.model.DeviceLog;
import seungchan.com.tobaccoach_5_2.model.Tobacco;
import seungchan.com.tobaccoach_5_2.utils.DBUtils;
import seungchan.com.tobaccoach_5_2.utils.TimezoneUtils;

/**
 * id(auto), 브랜드(1), 이름(2), 타르(3), 니코틴(4), 가격(5)
 */


public class TobaccoDBHelper extends SQLiteOpenHelper {
    private static String TAG = "TobaccoDBHelper";

    public static TobaccoDBHelper mTobaccoDBHelper = null; // 싱글턴
    private static Context mContext;
    private SQLiteDatabase db; // db access

    public static TobaccoDBHelper getInstance(Context context) {
        mContext = context;
        if (mTobaccoDBHelper == null) {
            mTobaccoDBHelper = new TobaccoDBHelper(context);
        }
        return mTobaccoDBHelper;
    }

    private TobaccoDBHelper(Context context) {
        super(context, DBUtils.DATABASE_NAME, null, DBUtils.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate 메서드");
        createTobaccoTable(db); // TOBACCO 테이블 생성
        createLogTable(db); // LOG 테이블 생성
    }

    @Override // 우선순위 would
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade 메서드");
        db.execSQL("DROP TABLE IF EXISTS " + DBUtils.TOBACCO_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBUtils.LOG_TABLE_NAME);
        onCreate(db);
    }

    /* 담배 종류 테이블 */
    private void createTobaccoTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DBUtils.TOBACCO_TABLE_NAME + " ("
                + DBUtils.TOBACCO_IDX + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DBUtils.TOBACCO_COL_BRAND + " TEXT, "
                + DBUtils.TOBACCO_COL_NAME + " TEXT, "
                + DBUtils.TOBACCO_COL_TAR + " REAL, "
                + DBUtils.TOBACCO_COL_NICOTINE + " REAL, "
                + DBUtils.TOBACCO_COL_PRICE + " INTEGER"
                + ")");
        Log.i(TAG, "onCreate 메서드, " + DBUtils.TOBACCO_TABLE_NAME + " 테이블 생성 완료");
    }

    public int getAllTobaccoTableRowNum(){
        db = getReadableDatabase();
        String sqlStatement = "select * from " + DBUtils.TOBACCO_TABLE_NAME + ";";
        Cursor result = db.rawQuery(sqlStatement, null);
        return result.getCount();
    }

    public boolean insertAllTobaccoData(HashMap tobaccoMap) {
        db = getWritableDatabase();
        Log.i(TAG, "insertAllTobaccoData 메서드");
       if(getAllTobaccoTableRowNum() != DBUtils.ALL_TOBACCO_TABLE_ROW_COUNT_FROM_CSV) { // 온전히 삽입이 안된 경우

           deleteAllTobaccoData(); // 1. 다 지우고

           Log.d(TAG, "insertAllTobaccoData 에서 인자로 받은 tobaccoMap의 size : " + tobaccoMap.size());
           Collection<Tobacco> tobaccoSet = tobaccoMap.values();
           Log.d(TAG, "insertAllTobaccoData 에서 변환한 tobaccoSet의 size : " + tobaccoSet.size());
           Iterator<Tobacco> iteratorTobaccoSet = tobaccoSet.iterator();

           for(int i=0; iteratorTobaccoSet.hasNext(); i++) {
               Tobacco temp = iteratorTobaccoSet.next();
//               int tobaccoId = temp.getTobaccoId();
               String tobaccoBrand = temp.getTobaccoBrand();
               String tobaccoName = temp.getTobaccoName();
               double tobaccoNicotine = temp.getTobaccoNicotine();
               double tobaccoTar = temp.getTobaccoTar();
               int tobaccoPrice = temp.getTobaccoPrice();

               ContentValues tobaccoRecord = new ContentValues();
//               tobaccoRecord.put(DBUtils.TOBACCO_IDX, tobaccoId);
               tobaccoRecord.put(DBUtils.TOBACCO_COL_BRAND, tobaccoBrand);
               tobaccoRecord.put(DBUtils.TOBACCO_COL_NAME, tobaccoName);
               tobaccoRecord.put(DBUtils.TOBACCO_COL_TAR, tobaccoTar);
               tobaccoRecord.put(DBUtils.TOBACCO_COL_NICOTINE, tobaccoNicotine);
               tobaccoRecord.put(DBUtils.TOBACCO_COL_PRICE, tobaccoPrice);

               long result = db.insert(DBUtils.TOBACCO_TABLE_NAME, null, tobaccoRecord); // 2. 삽입

               if (result == -1) {
                   Log.d(TAG, "db.insert 실패... index(" + i + ")");
                   return false;
               }
               else {
                   Log.d(TAG, "db.insert 성공! index(" + i + ") - " + temp.getTobaccoBrand() + "," + temp.getTobaccoName());
               }
           }
       }
       else // 현재 테이블 내 레코드가 74개면 삽입 수행하지 않음
           return true;

        return true;
    }

    public void deleteAllTobaccoData() {
        db = getWritableDatabase();
        Log.i(TAG, "deleteAllTobaccoData 메서드");

        db.delete(DBUtils.TOBACCO_TABLE_NAME, null, null); // 모두 삭제

    }

    // index에 해당하는 Tobacco 리턴
    public Tobacco selectTobaccoDataById(int index) {
        db = getReadableDatabase();
        Log.i(TAG, "selectTobaccoDataById 메서드");
        Tobacco myTobacco = null;
        String sqlStatement = "select * from " + DBUtils.TOBACCO_TABLE_NAME + " where " + DBUtils.TOBACCO_IDX + " = " + index + ";";
        Cursor result = db.rawQuery(sqlStatement, null); // 결과의 위치

        // result(Cursor)객체가 비어있으면 false 리턴
        if (result.moveToFirst()) {
            int col = 0;
            myTobacco = new Tobacco();
            myTobacco.setTobaccoBrand(result.getString(col++));
            myTobacco.setTobaccoName(result.getString(col++));
            myTobacco.setTobaccoTar(result.getDouble(col++));
            myTobacco.setTobaccoNicotine(result.getDouble(col++));
            myTobacco.setTobaccoPrice(result.getInt(col++));

            result.close();
            return myTobacco;
        }
        result.close();
        return myTobacco;
    }

    // 전체 담배 데이터 조회
    public List<Tobacco> selectAllTobaccoData() {
        db = getReadableDatabase();
        Log.i(TAG, "selectAllTobaccoData 메서드");
        List<Tobacco> allTobaccoList = new ArrayList<Tobacco>();
        String sql = "select * from " + DBUtils.TOBACCO_TABLE_NAME + ";";
        Cursor results = db.rawQuery(sql, null);

        if (results.moveToFirst()) {
            do {
                int col = 0;
                Tobacco tobacco = new Tobacco();
                tobacco.setTobaccoBrand(results.getString(col++));
                tobacco.setTobaccoName(results.getString(col++));
                tobacco.setTobaccoTar(results.getDouble(col++));
                tobacco.setTobaccoNicotine(results.getDouble(col++));
                tobacco.setTobaccoPrice(results.getInt(col++));
                allTobaccoList.add(tobacco);
            } while (results.moveToNext());
        }
        return allTobaccoList;
    }

    // 담배 브랜드 데이터 조회 (ExpandableListView의 dimension1 에 공급)
    public List<String> selectDistinctTobaccoBrandName(){
        db = getReadableDatabase();
        Log.i(TAG, "selectDistinctTobaccoBrandName 메서드");
        List<String> tobaccoBrandNameList = new ArrayList<String>();
        String sql = "select distinct " + DBUtils.TOBACCO_COL_BRAND + " from " + DBUtils.TOBACCO_TABLE_NAME + ";";
        Cursor results = db.rawQuery(sql, null);

        if (results.moveToFirst()) {
            do {
                int col = 0; // 0이 brand
                String tobaccoBrandName = new String(results.getString(col)); // col이 1아니고 0부터야?
                tobaccoBrandNameList.add(tobaccoBrandName);
            } while (results.moveToNext());
        }
        return tobaccoBrandNameList;
    }

    // 담배 브랜드별 데이터 조회 (ExpandableListView의 dimension2 에 공급)
    public List<String> selectTobaccoNameByBrandName(String tobaccoBrandName){
        db = getReadableDatabase();
        Log.i(TAG, "selectTobaccoNameByBrandName 메서드");
        List<String> tobaccoNameByBrandNameList = new ArrayList<String>();
        String sql = "select " + DBUtils.TOBACCO_COL_NAME + " from " + DBUtils.TOBACCO_TABLE_NAME + " where " + DBUtils.TOBACCO_COL_BRAND + " = '" + tobaccoBrandName + "';";
        Cursor results = db.rawQuery(sql, null);

        if (results.moveToFirst()) {
            do {
                int col = 0; // 0이 Brand, 1이 Name
                String tobaccoNameByBrandName = new String(results.getString(col));
                tobaccoNameByBrandNameList.add(tobaccoNameByBrandName);

            } while (results.moveToNext());
        }
        return tobaccoNameByBrandNameList;
    }

    // 담배id로 담배 가격 얻기
    public Tobacco selectTobaccoById(int tobaccoId){
        db = getReadableDatabase();
        Log.i(TAG, "selectTobaccoById 메서드");
        Tobacco tobaccoById = new Tobacco();
        String sql = "select * from " + DBUtils.TOBACCO_TABLE_NAME + " where " + DBUtils.TOBACCO_IDX + " = " + tobaccoId + ";";
        Cursor results = db.rawQuery(sql, null);

        if(results.moveToFirst()){
            do{
                int col = 1; // 원래 0이어야되는데 brand가 id로, name이 brand로 출력되더군..
                tobaccoById.setTobaccoBrand(results.getString(col++));
                tobaccoById.setTobaccoName(results.getString(col++));
                tobaccoById.setTobaccoTar(results.getDouble(col++));
                tobaccoById.setTobaccoNicotine(results.getDouble(col++));
                tobaccoById.setTobaccoPrice(results.getInt(col++));
            }while(results.moveToNext());
        }

        return tobaccoById;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 사용자 LOG 테이블 */
    public void createLogTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ DBUtils.LOG_TABLE_NAME +" ( "
                + DBUtils.LOG_IDX + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DBUtils.LOG_COL_DATETIME + " TEXT "
                + ")");
        Log.i(TAG, "onCreate 메서드, " + DBUtils.LOG_TABLE_NAME + " 테이블 생성 완료");
    }

    public boolean insertLogData(String dateString) {
        db = getWritableDatabase();
        Log.d(TAG, "insertLogData 메서드. 인자로 받은 dateString : " + dateString);

        // 1. date time 포멧
        String finalDateTime = TimezoneUtils.formatHwDateTime(dateString);
        Log.d(TAG, "insertLogData 메서드. 변환된 date time : " + finalDateTime);

        if(finalDateTime != null){

            // 2. 레코드 생성
            ContentValues logRecord = new ContentValues(); // 레코드 TEXT 타입 yyyy-MM-dd HH:mm:ss 으로 삽입
            logRecord.put(DBUtils.LOG_COL_DATETIME, finalDateTime);

            // 3. 레코드 삽입
            long result = 0;
            try{
                db.beginTransaction();
                result = db.insert(DBUtils.LOG_TABLE_NAME, null, logRecord); // 2. 삽입
                db.setTransactionSuccessful();
            } catch (SQLException e){
            } finally {
                db.endTransaction();
            }
//            long result = db.insert(LOG_TABLE_NAME, null, logRecord); // 2. 삽입

            if (result == -1) { // insert 실패
                return false;
            }
        }

        return true; // insert 성공
    }

    public int getTodayLogAmount(){
        SQLiteDatabase db = getReadableDatabase(); // 새 db 생성
        Log.d(TAG, "getTodayLogAmount 메서드");

        String todayDate = TimezoneUtils.getTodayDate(); // yyyy-MM-dd 오늘 날짜

        int finalTodayLogAmount = 0;

        String sql = "select * from " + DBUtils.LOG_TABLE_NAME + " where " + DBUtils.LOG_COL_DATETIME + " like '%" + todayDate + "%'";

        Cursor result = db.rawQuery(sql, null);
        if(result.moveToFirst()){
            finalTodayLogAmount = result.getCount();
            Log.d(TAG, "getTodayLogAmount 메서드 result.getCount() 반환값 : " + finalTodayLogAmount);
        }
        result.close();

        return finalTodayLogAmount;
    }

    // 날짜에 대한 로그 데이터 개수 (selectDistinctDateTimeLogData 메서드를 통해 인자로 날짜를 받아 이용)
    public int getAmountByDay(String date){
        SQLiteDatabase db = getReadableDatabase();
        Log.d(TAG, "getAmountByDay 메서드");
        int finalAmountByDay = 0;
        String sql = "select " + DBUtils.LOG_COL_DATETIME + " from " + DBUtils.LOG_TABLE_NAME + " where " + DBUtils.LOG_COL_DATETIME  + " like '%" + date + "%';";
        Log.d("RRRR", sql);
        Cursor results = db.rawQuery(sql, null);

        if(results.moveToFirst()){
            int col = 0;
            finalAmountByDay = results.getCount();
        }
        results.close();
        return finalAmountByDay;
    }

//    public int getAverageLogAmountTillDay(String date){
//        Log.d(TAG, "getAverageLogAmountTilDay 메서드");
//
//        int finalAverageLogAmount = 0;
//        List<String> mDistinctDateTimeLogDataList = new ArrayList<String>();
//        mDistinctDateTimeLogDataList = selectDistinctDateTimeLogDataTillDay(date);
//        for(int i=0; i<mDistinctDateTimeLogDataList.size(); i++){
//            finalAverageLogAmount += getAmountByDay(mDistinctDateTimeLogDataList.get(i)); // 2. 이용한 날짜들에 대한 로그 데이터 개수들을 getAmountByDay(date)로 얻는다.
//        }
//        int tillPeriod = getTillPeriod();
//        if(tillPeriod > 0){
//            finalAverageLogAmount /= getTillPeriod(); // 3. 총 합을 getAllPeriod()로 나눈다
//            return finalAverageLogAmount;
//        }
//        else{ // getTillPeriod() == 0
//            return 0; // 서비스 이용 기간이 없음 (0일)
//        }
//    }



    public int getAllPeriod(){ // 서비스 이용한 날짜 일 수 - averageAmount 구할때 이용
        SQLiteDatabase db = getReadableDatabase();
        Log.d(TAG, "getAllPeriod 메서드");

        int finalAllPeriod = 0;
        String sql = "select distinct substr(" + DBUtils.LOG_COL_DATETIME + ", 1, 10) from " + DBUtils.LOG_TABLE_NAME + ";";
        Cursor result = db.rawQuery(sql, null);
        if(result.moveToFirst()){
            finalAllPeriod = result.getCount();
            Log.d(TAG, "getAllPeriod메서드, substr() 결과로 서비스 이용한 일자 수, result.getCount() : " + finalAllPeriod);
        }
        return finalAllPeriod;
    }

    // 로그 데이터내 날짜 (서비스를 이용한 날들) 조회
    public List<String> selectDistinctDateTimeLogData(){
        db = getReadableDatabase();
        Log.i(TAG, "selectDistinctDateTimeLogData 메서드");
        List<String> dateTimeLogDataList = new ArrayList<String>();
        String sql = "select distinct substr(" + DBUtils.LOG_COL_DATETIME + ", 1, 10) from " + DBUtils.LOG_TABLE_NAME + ";";
        Cursor results = db.rawQuery(sql, null);

        if (results.moveToFirst()) {
            do {
                int col = 0; // 0이 brand
                String dateTimeLogData = new String(results.getString(col)); // col이 1아니고 0부터야?
                dateTimeLogDataList.add(dateTimeLogData);
            } while (results.moveToNext());
        }
        return dateTimeLogDataList;
    }

    // 날짜별 로그 데이터 조회 (인자에 selectDistinctDateTimeLogData메서드 이용해 해당 날짜에 대해 로그데이터들 리턴)
    public List<String> selectDateTimeLogDataByDay(String date){ // yyyy-MM-dd HH:mm:ss
        SQLiteDatabase db = getReadableDatabase();
        Log.d(TAG, "selectDateTimeLogDataByDay 메서드");
        List<String> dateTimeLogDataByDayList = new ArrayList<String>();
        String sql = "select " + DBUtils.LOG_COL_DATETIME + " from " + DBUtils.LOG_TABLE_NAME + " where " + DBUtils.LOG_COL_DATETIME  + " like '%" + date + "%';";
        Cursor results = db.rawQuery(sql, null);

        if(results.moveToFirst()){
            do{
                int col = 0;
                String dateTimeLogDataByDay = new String(results.getString(col));
                dateTimeLogDataByDayList.add(dateTimeLogDataByDay);
            } while(results.moveToNext());
        }
        return dateTimeLogDataByDayList;
    }

    // 날짜별 로그 데이터 Time 만 조회
    public List<String> selectTimeLogDataByDay(String date){ // yyyy-MM-dd HH:mm:ss
        SQLiteDatabase db = getReadableDatabase();
        Log.d(TAG, "selectTimeLogDataByDay 메서드");
        List<String> timeLogDataByDayList = new ArrayList<String>();
        String sql = "select substr(" + DBUtils.LOG_COL_DATETIME + ", 12, 19) from " + DBUtils.LOG_TABLE_NAME + " where " + DBUtils.LOG_COL_DATETIME  + " like '%" + date + "%';";
        Cursor results = db.rawQuery(sql, null);

        if(results.moveToFirst()){
            do{
                int col = 0;
                String timeLogDataByDay = new String(results.getString(col));
                timeLogDataByDayList.add(timeLogDataByDay);
            } while(results.moveToNext());
        }
        return timeLogDataByDayList;
    }

    public List<String> selectAllDateTimeLogData(){
        SQLiteDatabase db = getReadableDatabase();
        Log.d(TAG, "selectAllLogData 메서드");

        List<String> allLogDataList = new ArrayList<String>();
        String sql = "select * from " + DBUtils.LOG_TABLE_NAME + ";";
        Cursor results = db.rawQuery(sql, null);

        if (results.moveToFirst()) {
            do {
                int col = 1; // 0이 index였음
                String log = new String(results.getString(col++));

                allLogDataList.add(log);
            } while (results.moveToNext());
        }
        return allLogDataList;
    }

    public boolean insertForResetAllLogData(HashMap logDataMap) {
        db = getWritableDatabase();
        Log.i(TAG, "insertForResetAllLogData 메서드");

        deleteAllLogData(); // 1. 다 지우고

        Log.d(TAG, "insertAllLogData 에서 인자로 받은 logDataMap size : " + logDataMap.size());
        Collection<DeviceLog> logDataSet = logDataMap.values();
        Log.d(TAG, "insertAllLogData 에서 변환한 logDataSet의 size : " + logDataSet.size());
        Iterator<DeviceLog> iteratorLogDataSet = logDataSet.iterator();

        for(int i=0; iteratorLogDataSet.hasNext(); i++) {
            DeviceLog temp = iteratorLogDataSet.next();
            String dateTime = temp.getDateTime();

            ContentValues logDataRecord = new ContentValues();
            logDataRecord.put(DBUtils.LOG_COL_DATETIME, dateTime);

            long result = db.insert(DBUtils.LOG_TABLE_NAME, null, logDataRecord); // 2. 삽입

            if (result == -1) {
                Log.d(TAG, "db.insert 실패... index(" + i + ")");
                return false;
            }
            else {
                Log.d(TAG, "db.insert 성공! index(" + i + ") - " + temp.getDateTime());
            }
        }

        return true;
    }

    public int getAllLogTableRowNum(){
        db = getReadableDatabase();
        String sqlStatement = "select * from " + DBUtils.LOG_TABLE_NAME + ";";
        Cursor result = db.rawQuery(sqlStatement, null);
        return result.getCount();
    }

    // 가장 최신의 Log DateTime을 반환
//    public String getLastLog(){
//        db = getReadableDatabase();
//        String lastLog = null;
//        String sqlStatement = "select " + DBUtils.LOG_COL_DATETIME + " from " + DBUtils.LOG_TABLE_NAME + " where " + DBUtils.LOG_IDX + " = " + (getAllLogTableRowNum()-1) + ";";
//
//        Cursor results = db.rawQuery(sqlStatement, null);
//        if (results.moveToFirst()) {
//            do {
//                int col = 1; // 0이 index였음
//                lastLog = new String(results.getString(0));
//            } while (results.moveToNext());
//        }
//
//        Log.d(TAG, "getLastLog메서드 결과 : " + lastLog);
//        return lastLog;
//    }
    public String getLastLog(){ // yyyy-mm-dd hh:mm:ss 일때, select max(datetime) from tableName; 가능
        db = getReadableDatabase();
        String lastLog = null;
        String sqlStatement = "select max(" + DBUtils.LOG_COL_DATETIME + ") from " + DBUtils.LOG_TABLE_NAME + ";";

        Cursor results = db.rawQuery(sqlStatement, null);
        if (results.moveToFirst()) {
            do {
                int col = 1; // 0이 index였음
                lastLog = new String(results.getString(0));
            } while (results.moveToNext());
        }

        Log.d(TAG, "getLastLog메서드 결과 : " + lastLog);
        return lastLog;
    }

    public void deleteAllLogData() {
        db = getWritableDatabase();
        Log.i(TAG, "deleteAllLogData 메서드");

        db.delete(DBUtils.LOG_TABLE_NAME, null, null); // 모두 삭제

    }


}