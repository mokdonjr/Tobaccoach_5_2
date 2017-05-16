//package seungchan.com.tobaccoach_5_2.tobaccoDBInit;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//
//import seungchan.com.tobaccoach_5_2.model.DeviceLog;
//import seungchan.com.tobaccoach_5_2.utils.DBUtils;
//import seungchan.com.tobaccoach_5_2.utils.TimezoneUtils;
//
///**
// * Created by USER on 2017-05-11.
// */
//
//public class UserLogDBHelper extends SQLiteOpenHelper {
//    private static String TAG = "UserLogDBHelper";
//
//    public static UserLogDBHelper mUserLogDBHelper = null; // singleton
//    private static Context mContext;
//    private SQLiteDatabase db; // db access
//
//    public static UserLogDBHelper getInstance(Context context){
//        mContext = context;
//        if(mUserLogDBHelper == null){
//            mUserLogDBHelper = new UserLogDBHelper(context);
//        }
//        return mUserLogDBHelper;
//    }
//
//    private UserLogDBHelper(Context context){
//        super(context, DBUtils.DATABASE_NAME, null, DBUtils.DB_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        Log.d(TAG, "onCreate");
//        createLogTable(db); // LOG 테이블 생성
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + DBUtils.LOG_TABLE_NAME);
//        onCreate(db);
//    }
//
//    /* 사용자 LOG 테이블 */
//    public void createLogTable(SQLiteDatabase db){
//        db.execSQL("CREATE TABLE IF NOT EXISTS "+ DBUtils.LOG_TABLE_NAME +" ( "
//                + DBUtils.LOG_IDX + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//                + DBUtils.LOG_COL_DATETIME + " TEXT "
//                + ")");
//        Log.i(TAG, "onCreate 메서드, " + DBUtils.LOG_TABLE_NAME + " 테이블 생성 완료");
//    }
//
//    public boolean insertLogData(String dateString) {
//        db = getWritableDatabase();
//        Log.d(TAG, "insertLogData 메서드. 인자로 받은 dateString : " + dateString);
//
//        // 1. date time 포멧
//        String finalDateTime = TimezoneUtils.formatHwDateTime(dateString);
//        Log.d(TAG, "insertLogData 메서드. 변환된 date time : " + finalDateTime);
//
//        if(finalDateTime != null){
//
//            // 2. 레코드 생성
//            ContentValues logRecord = new ContentValues(); // 레코드 TEXT 타입 yyyy-MM-dd HH:mm:ss 으로 삽입
//            logRecord.put(DBUtils.LOG_COL_DATETIME, finalDateTime);
//
//            // 3. 레코드 삽입
//            long result = 0;
//            try{
//                db.beginTransaction();
//                result = db.insert(DBUtils.LOG_TABLE_NAME, null, logRecord); // 2. 삽입
//                db.setTransactionSuccessful();
//            } catch (SQLException e){
//            } finally {
//                db.endTransaction();
//            }
////            long result = db.insert(LOG_TABLE_NAME, null, logRecord); // 2. 삽입
//
//            if (result == -1) { // insert 실패
//                return false;
//            }
//        }
//
//        return true; // insert 성공
//    }
//
//    public int getTodayLogAmount(){
//        SQLiteDatabase db = getReadableDatabase(); // 새 db 생성
//        Log.d(TAG, "getTodayLogAmount 메서드");
//
//        String todayDate = TimezoneUtils.getTodayDate(); // yyyy-MM-dd 오늘 날짜
//
//        int finalTodayLogAmount = 0;
//
//        String sql = "select * from " + DBUtils.LOG_TABLE_NAME + " where " + DBUtils.LOG_COL_DATETIME + " like '%" + todayDate + "%'";
//
//        Cursor result = db.rawQuery(sql, null);
//        if(result.moveToFirst()){
//            finalTodayLogAmount = result.getCount();
//            Log.d(TAG, "getTodayLogAmount 메서드 result.getCount() 반환값 : " + finalTodayLogAmount);
//        }
//        result.close();
//
//        return finalTodayLogAmount;
//    }
//
//    // 날짜에 대한 로그 데이터 개수 (selectDistinctDateTimeLogData 메서드를 통해 인자로 날짜를 받아 이용)
//    public int getAmountByDay(String date){
//        SQLiteDatabase db = getReadableDatabase();
//        Log.d(TAG, "getAmountByDay 메서드");
//        int finalAmountByDay = 0;
//        String sql = "select " + DBUtils.LOG_COL_DATETIME + " from " + DBUtils.LOG_TABLE_NAME + " where " + DBUtils.LOG_COL_DATETIME  + " like '%" + date + "%';";
//        Cursor results = db.rawQuery(sql, null);
//
//        if(results.moveToFirst()){
//            int col = 0;
//            finalAmountByDay = results.getCount();
//        }
//        return finalAmountByDay;
//    }
//
//    public int getAverageLogAmount() {
//        Log.d(TAG, "getAverageLogAmount 메서드");
//
//        int finalAverageLogAmount = 0;
//        List<String> mDistinctDateTimeLogDataList = new ArrayList<String>();
//        mDistinctDateTimeLogDataList = selectDistinctDateTimeLogData(); // 1. selectDistinctDateTimeLogData()로 서비스 이용한 날짜들을 얻는다(time은 없이)
//        for(int i=0; i<mDistinctDateTimeLogDataList.size(); i++){
//            finalAverageLogAmount += getAmountByDay(mDistinctDateTimeLogDataList.get(i)); // 2. 이용한 날짜들에 대한 로그 데이터 개수들을 getAmountByDay(date)로 얻는다.
//        }
//
//        int allPeriod = getAllPeriod();
//        if(allPeriod > 0){
//            finalAverageLogAmount /= getAllPeriod(); // 3. 총 합을 getAllPeriod()로 나눈다
//            return finalAverageLogAmount;
//        }
//        else{ // getAllPeriod() == 0
//            Log.d(TAG, "getAverageLogAmount() 메서드, 총합을 나눌 getAllPeriod() 값 == 0 (divide zero 에러 방지, averageAmount = 0 으로 리턴하자.)");
//            return 0; // 서비스 이용 기간이 없음 (0일)
//        }
//    }
//
//    public int getAllPeriod(){ // 서비스 이용한 날짜 일 수 - averageAmount 구할때 이용
//        SQLiteDatabase db = getReadableDatabase();
//        Log.d(TAG, "getAllPeriod 메서드");
//
//        int finalAllPeriod = 0;
//        String sql = "select distinct substr(" + DBUtils.LOG_COL_DATETIME + ", 1, 10) from " + DBUtils.LOG_TABLE_NAME + ";";
//        Cursor result = db.rawQuery(sql, null);
//        if(result.moveToFirst()){
//            finalAllPeriod = result.getCount();
//            Log.d(TAG, "getAllPeriod메서드, substr() 결과로 서비스 이용한 일자 수, result.getCount() : " + finalAllPeriod);
//        }
//        return finalAllPeriod;
//    }
//
//    // 로그 데이터내 날짜 (서비스를 이용한 날들) 조회
//    public List<String> selectDistinctDateTimeLogData(){
//        db = getReadableDatabase();
//        Log.i(TAG, "selectDistinctDateTimeLogData 메서드");
//        List<String> dateTimeLogDataList = new ArrayList<String>();
//        String sql = "select distinct substr(" + DBUtils.LOG_COL_DATETIME + ", 1, 10) from " + DBUtils.LOG_TABLE_NAME + ";";
//        Cursor results = db.rawQuery(sql, null);
//
//        if (results.moveToFirst()) {
//            do {
//                int col = 0; // 0이 brand
//                String dateTimeLogData = new String(results.getString(col)); // col이 1아니고 0부터야?
//                dateTimeLogDataList.add(dateTimeLogData);
//            } while (results.moveToNext());
//        }
//        return dateTimeLogDataList;
//    }
//
//    // 날짜별 로그 데이터 조회 (인자에 selectDistinctDateTimeLogData메서드 이용해 해당 날짜에 대해 로그데이터들 리턴)
//    public List<String> selectDateTimeLogDataByDay(String date){
//        SQLiteDatabase db = getReadableDatabase();
//        Log.d(TAG, "selectDateTimeLogDataByDay 메서드");
//        List<String> dateTimeLogDataByDayList = new ArrayList<String>();
//        String sql = "select " + DBUtils.LOG_COL_DATETIME + " from " + DBUtils.LOG_TABLE_NAME + " where " + DBUtils.LOG_COL_DATETIME  + " like '%" + date + "%';";
//        Cursor results = db.rawQuery(sql, null);
//
//        if(results.moveToFirst()){
//            do{
//                int col = 0;
//                String dateTimeLogDataByDay = new String(results.getString(col));
//                dateTimeLogDataByDayList.add(dateTimeLogDataByDay);
//            } while(results.moveToNext());
//        }
//        return dateTimeLogDataByDayList;
//    }
//
//    public List<String> selectAllDateTimeLogData(){
//        SQLiteDatabase db = getReadableDatabase();
//        Log.d(TAG, "selectAllLogData 메서드");
//
//        List<String> allLogDataList = new ArrayList<String>();
//        String sql = "select * from " + DBUtils.LOG_TABLE_NAME + ";";
//        Cursor results = db.rawQuery(sql, null);
//
//        if (results.moveToFirst()) {
//            do {
//                int col = 1; // 0이 index였음
//                String log = new String(results.getString(col++));
//
//                allLogDataList.add(log);
//            } while (results.moveToNext());
//        }
//        return allLogDataList;
//    }
//
//    public boolean insertForResetAllLogData(HashMap logDataMap) {
//        db = getWritableDatabase();
//        Log.i(TAG, "insertForResetAllLogData 메서드");
//
//        deleteAllLogData(); // 1. 다 지우고
//
//        Log.d(TAG, "insertAllLogData 에서 인자로 받은 logDataMap size : " + logDataMap.size());
//        Collection<DeviceLog> logDataSet = logDataMap.values();
//        Log.d(TAG, "insertAllLogData 에서 변환한 logDataSet의 size : " + logDataSet.size());
//        Iterator<DeviceLog> iteratorLogDataSet = logDataSet.iterator();
//
//        for(int i=0; iteratorLogDataSet.hasNext(); i++) {
//            DeviceLog temp = iteratorLogDataSet.next();
//            String dateTime = temp.getDateTime();
//
//            ContentValues logDataRecord = new ContentValues();
//            logDataRecord.put(DBUtils.LOG_COL_DATETIME, dateTime);
//
//            long result = db.insert(DBUtils.LOG_TABLE_NAME, null, logDataRecord); // 2. 삽입
//
//            if (result == -1) {
//                Log.d(TAG, "db.insert 실패... index(" + i + ")");
//                return false;
//            }
//            else {
//                Log.d(TAG, "db.insert 성공! index(" + i + ") - " + temp.getDateTime());
//            }
//        }
//
//        return true;
//    }
//
//    public int getAllLogTableRowNum(){
//        db = getReadableDatabase();
//        String sqlStatement = "select * from " + DBUtils.LOG_TABLE_NAME + ";";
//        Cursor result = db.rawQuery(sqlStatement, null);
//        return result.getCount();
//    }
//
//    public void deleteAllLogData() {
//        db = getWritableDatabase();
//        Log.i(TAG, "deleteAllLogData 메서드");
//
//        db.delete(DBUtils.LOG_TABLE_NAME, null, null); // 모두 삭제
//
//    }
//
//
//}
