package seungchan.com.tobaccoach_5_2.dao;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import seungchan.com.tobaccoach_5_2.model.Tobacco;
import seungchan.com.tobaccoach_5_2.utils.TimezoneUtils;

/**
 * Created by USER on 2017-05-19.
 */

public class TobaccoDaoService {
    private static String TAG = "TobaccoDaoService";

    public static TobaccoDaoService mTobaccoDaoService = null; // singleton

    private static Context mContext;
    private static TobaccoDBHelper mTobaccoDBHelper; // daoService references tobaccoDBhelper(dao)

    public static TobaccoDaoService getInstance(Context context){
        mContext = context;
        mTobaccoDBHelper = TobaccoDBHelper.getInstance(mContext);
        if(mTobaccoDaoService == null){
            mTobaccoDaoService = new TobaccoDaoService();
        }
        return mTobaccoDaoService;
    }
    private TobaccoDaoService() { }

    // 총 담배 금액 계산
    public int getAllTobaccoMoney(Tobacco tobacco){
        int finalTobaccoMoney = 0; // smokedNumber * tobaccoPerMoney

        int smokedNumber = mTobaccoDBHelper.getAllLogTableRowNum();
        int tobaccoPerMoney = tobacco.getTobaccoPrice();

        finalTobaccoMoney = smokedNumber * tobaccoPerMoney;

        Log.d(TAG, "getAllTobaccoMoney메서드 : 핀 개수(" + smokedNumber + ") , " +  tobacco.getTobaccoBrand() + "-" + tobacco.getTobaccoBrand()
                + "담배 가격(" + tobaccoPerMoney + ")");
        return finalTobaccoMoney;
    }

    // 담배 id로 담배 객체 얻기
    public Tobacco selectTobaccoById(int tobaccoId){
        Log.d(TAG, "selectTobaccoById메서드, 선택한 담배id : " + tobaccoId + " / 담배 brand + name : " + mTobaccoDBHelper.selectTobaccoById(tobaccoId).getTobaccoBrand() + " - " + mTobaccoDBHelper.selectTobaccoById(tobaccoId).getTobaccoName());
        return mTobaccoDBHelper.selectTobaccoById(tobaccoId);
    }

    // DB 저장된 모든 날들에 대해 averageAmount 반환
    public int getAverageLogAmount() {
        Log.d(TAG, "getAverageLogAmount 메서드");

        int finalAverageLogAmount = 0;
        List<String> mDistinctDateTimeLogDataList = new ArrayList<String>();
        mDistinctDateTimeLogDataList = mTobaccoDBHelper.selectDistinctDateTimeLogData(); // 1. selectDistinctDateTimeLogData()로 서비스 이용한 날짜들을 얻는다(time은 없이)
        for(int i=0; i<mDistinctDateTimeLogDataList.size(); i++){
            finalAverageLogAmount += mTobaccoDBHelper.getAmountByDay(mDistinctDateTimeLogDataList.get(i)); // 2. 이용한 날짜들에 대한 로그 데이터 개수들을 getAmountByDay(date)로 얻는다.
            //Log.i("RRRRRR", mDistinctDateTimeLogDataList.get(i));
        }

        int allPeriod = mTobaccoDBHelper.getAllPeriod();
        if(allPeriod > 0){
            finalAverageLogAmount /= mTobaccoDBHelper.getAllPeriod(); // 3. 총 합을 getAllPeriod()로 나눈다
            return finalAverageLogAmount;
        }
        else{ // getAllPeriod() == 0
            Log.d(TAG, "getAverageLogAmount() 메서드, 총합을 나눌 getAllPeriod() 값 == 0 (divide zero 에러 방지, averageAmount = 0 으로 리턴하자.)");
            return 0; // 서비스 이용 기간이 없음 (0일)
        }
    }

    // DB 저장된 모든 날들 중 특정 날 까지의 averageAmount 반환
    public int getAverageLogAmountTillDate(String date){
        Log.d(TAG, "getAverageLogAmountTillDate(String date) 메서드, 인자로 입력된 date : " + date);
        int finalAverageLogAmountTillDate = 0;

        // 전체 로그 데이터 준비
        List<String> mDistinctDateTimeLogDataList = new ArrayList<String>();
        mDistinctDateTimeLogDataList = mTobaccoDBHelper.selectDistinctDateTimeLogData();

        // 전체 로그 데이터에서 오늘까지만
        for(int i=0; i<mDistinctDateTimeLogDataList.size(); i++){
            Integer dayOfInputDate = Integer.valueOf(mDistinctDateTimeLogDataList.get(i).substring(9, 11));
            Integer dayOfTodayDate = Integer.valueOf(TimezoneUtils.getTodayDate().substring(9, 11));
            if(dayOfInputDate > dayOfTodayDate)
                break; // 오늘을 넘어가면 break;
            /*
                평균 연산
             */
            finalAverageLogAmountTillDate += mTobaccoDBHelper.getAmountByDay(mDistinctDateTimeLogDataList.get(i));
        }

        return finalAverageLogAmountTillDate;
    }

    // DB 저장된 모든 날들 중 특정 날 까지의 averageAmount 반환


    public int getTodayLogAmount(){
        return mTobaccoDBHelper.getTodayLogAmount();
    }

    public boolean insertAllTobaccoData(HashMap tobaccoMap){
        return mTobaccoDBHelper.insertAllTobaccoData(tobaccoMap);
    }

    public int getAllTobaccoTableRowNum(){
        return mTobaccoDBHelper.getAllTobaccoTableRowNum();
    }

    public List<String> selectDistinctTobaccoBrandName(){
        return mTobaccoDBHelper.selectDistinctTobaccoBrandName();
    }

    public List<String> selectTobaccoNameByBrandName(String tobaccoBrandName){
        return mTobaccoDBHelper.selectTobaccoNameByBrandName(tobaccoBrandName);
    }

    public boolean insertLogData(String dateString){
        return mTobaccoDBHelper.insertLogData(dateString);
    }

    public int getAmountByDay(String date){
        return mTobaccoDBHelper.getAmountByDay(date);
    }

    public List<String> selectDistinctDateTimeLogData(){
        return mTobaccoDBHelper.selectDistinctDateTimeLogData();
    }

    public List<String> selectAllDateTimeLogData(){
        return mTobaccoDBHelper.selectAllDateTimeLogData();
    }

    public boolean insertForResetAllLogData(HashMap logDataMap){
        return mTobaccoDBHelper.insertForResetAllLogData(logDataMap);
    }

    public int getAllLogTableRowNum(){
        return mTobaccoDBHelper.getAllLogTableRowNum();
    }

    public void deleteAllLogData(){
        mTobaccoDBHelper.deleteAllLogData();
    }

    public List<String> selectTimeLogDataByDay(String date){
        return mTobaccoDBHelper.selectTimeLogDataByDay(date);
    }

    public String getLastLog(){
        return mTobaccoDBHelper.getLastLog();
    }

}
