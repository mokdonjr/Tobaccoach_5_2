//package seungchan.com.tobaccoach_5_2.utils;
//
//import android.util.Log;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by USER on 2017-05-18.
// */
//
//public class ServerMock {
//    private static String TAG = "ServerMock";
//
//    private List<Integer> mockAllUserAverageByDate;
//
//    public static ServerMock mServerMock = null; // singleton
//
//    public static ServerMock getInstance(List<String> servicedDate){
//        if(mServerMock == null){
//            mServerMock = new ServerMock(servicedDate);
//        }
//        return mServerMock;
//    }
//
//    private ServerMock(List<String> servicedDate){
//        mockAllUserAverageByDate = new ArrayList<Integer>();
//        for(int i=0; i<servicedDate.size(); i++){
//            int mockAllUserAverageAmountFromServer = (int) (Math.random() * (12 - 8 + 1)) + 8; // 8~12개 범위 랜덤
//            Log.d(TAG, "ServerMock 생성자에서 mockAllUserAverageByDate 리스트에 추가하는 데이터 : " + mockAllUserAverageAmountFromServer);
//            mockAllUserAverageByDate.add(mockAllUserAverageAmountFromServer);
//        }
//    }
//
//
//
//    // DailyChartFragment 에서 해당 일의 전체 사용자 평균 받아옴 (추후에 네트워크서 받아오기)
//    public List<Integer> getServerMockAllUserAverageByDate(){
//        return mockAllUserAverageByDate;
//    }
//
//}
