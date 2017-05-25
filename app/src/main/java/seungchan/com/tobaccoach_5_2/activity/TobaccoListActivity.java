package seungchan.com.tobaccoach_5_2.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import seungchan.com.tobaccoach_5_2.dao.TobaccoDaoService;
import seungchan.com.tobaccoach_5_2.model.User;
import seungchan.com.tobaccoach_5_2.utils.AppSettingUtils;
import seungchan.com.tobaccoach_5_2.R;
import seungchan.com.tobaccoach_5_2.model.Tobacco;
import seungchan.com.tobaccoach_5_2.dao.TobaccoDBHelper;
import seungchan.com.tobaccoach_5_2.webService.ApplicationController;
import seungchan.com.tobaccoach_5_2.webService.NetworkService;

public class TobaccoListActivity extends AppCompatActivity {
    private static String TAG = "TobaccoListActivity";

    @BindView(R.id.tobacco_expendable_list_view) ExpandableListView mTobaccoList;
    private List<String> tobaccoBrandNameList; // Parent 데이터
    private List<List<String>> tobaccoNameByAllBrandNameList; // 메모리로 가져옴
    private TobaccoDaoService mTobaccoDaoService;

    private AppSettingUtils mAppSettingUtils;
    private NetworkService networkService;

    private final String LIST_TOBACCO_BRAND = "TOBACCO_BRAND";
    private final String LIST_TOBACCO_NAME = "TOBACCO_NAME";

    private Tobacco myTobacco;
    private String ipAddress;
    private String deviceAddress;
    private String userId;
    private String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_tobacco_list);
        ButterKnife.bind(this);

        getIntentFromPrevious();

        mTobaccoList.setOnChildClickListener(mTobaccoListClickListener);
        mTobaccoDaoService = TobaccoDaoService.getInstance(getApplicationContext()); // 싱글톤
        displayTobaccoFromDB(mTobaccoDaoService);

        mAppSettingUtils = AppSettingUtils.getInstance();
        if(mAppSettingUtils != null){
            //ip , port 연결
            ApplicationController application= ApplicationController.getInstance();  //앱이 처음 실행될 때 인스턴스 생성.
            application.buildNetworkService(mAppSettingUtils.getWebApplicationServerUrl(ipAddress));  //다음의 url로 네트워크서비스 준비.
            networkService= ApplicationController.getInstance().getNetworkService();
        }
    }

    public void getIntentFromPrevious(){
        Intent addressIntent = getIntent();
        ipAddress = addressIntent.getStringExtra(AppSettingUtils.EXTRAS_SERVER_IP);
        deviceAddress = addressIntent.getStringExtra(AppSettingUtils.EXTRAS_DEVICE_ADDRESS);
        userId = addressIntent.getStringExtra(AppSettingUtils.EXTRAS_MY_ID);
        userPassword = addressIntent.getStringExtra(AppSettingUtils.EXTRAS_MY_PASSWORD);
    }

    public Intent putIntentToNext(){
        /* MyDeviceScanActivity */
        //Intent myTobaccoIntent = new Intent(getApplicationContext(), MyDeviceScanActivity.class);
        Intent myTobaccoIntent = new Intent(getApplicationContext(), LoginActivity.class);
        myTobaccoIntent.putExtra(AppSettingUtils.EXTRAS_DEVICE_ADDRESS, deviceAddress);
        myTobaccoIntent.putExtra(AppSettingUtils.EXTRAS_SERVER_IP, ipAddress);
        return myTobaccoIntent;
    }

    // Tobacco's ExpandableListView Event Listener
    private final ExpandableListView.OnChildClickListener mTobaccoListClickListener =
            new ExpandableListView.OnChildClickListener(){

                @Override
                public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {

                    if(tobaccoNameByAllBrandNameList != null){

                        // 추출
                        String myTobaccoBrand = tobaccoBrandNameList.get(groupPosition);
                        String myTobaccoName = tobaccoNameByAllBrandNameList.get(groupPosition).get(childPosition);
                        myTobacco = new Tobacco();
                        myTobacco.setTobaccoBrand(myTobaccoBrand);
                        myTobacco.setTobaccoName(myTobaccoName);
                        Toast.makeText(getApplicationContext(), "'" + myTobaccoBrand + "'사의 담배 '" + myTobaccoName + "'를 선택했습니다. ", Toast.LENGTH_SHORT).show();


                        /*
                            여기에 웹으로 담배이름, 담배브랜드 request
                         */
                        webAccessForRequestInsert();

                        Intent intent = putIntentToNext();
                        startActivity(intent);

                        return true;
                    }
                    return false;
                }
            };

    public void webAccessForRequestInsert(){
        User user = new User();
        user.setNick(userId);
        user.setPassword(userPassword);
        user.setTobac(myTobacco);
//        mInputSignupId.setText("");
//        mInputSignupPassword.setText("");

        Call<User> thumbnailCall = networkService.post_user(user);
        thumbnailCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()) {
                    Log.i(TAG, "응답코드 : " + "등록되었습니다.");
                    /*
                        가입 성공
                     */
                    Toast.makeText(getApplicationContext(), "서버로부터 허가 받았습니다.", Toast.LENGTH_LONG ).show();
                } else {
                    int statusCode = response.code();
                    Log.i(TAG, "응답코드 : " + statusCode);
                    /*
                        가입실패 내용
                        (아이디 중복)
                     */
                    Toast.makeText(getApplicationContext(), String.valueOf(statusCode), Toast.LENGTH_LONG ).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.i(TAG, "서버 onFailure 에러내용 : " + t.getMessage());
                Toast.makeText(getApplicationContext(), "onFailure메서드 : " + t.getMessage(), Toast.LENGTH_LONG ).show();
            }
        });
    }

    // display Tobaccos on ExpandableListView
    private void displayTobaccoFromDB(TobaccoDaoService tobaccoDaoService) {
        if(tobaccoDaoService == null){
            Log.d(TAG, "displayTobaccoFromDB 메서드, TobaccoDaoService == null !!");
        }

        // 1. 데이터 준비
        tobaccoBrandNameList = new ArrayList<String>(); // Parent Data 준비
        tobaccoBrandNameList = tobaccoDaoService.selectDistinctTobaccoBrandName();
        Log.d(TAG, "displayTobaccoFromDB - tobaccoDaoService.selectDistinctTobaccoBrandName() 수행");

        tobaccoNameByAllBrandNameList = new ArrayList<List<String>>(); // Child Data 준비
        for(int i=0; i<tobaccoBrandNameList.size(); i++){
//        for(String tobaccoBrandName : tobaccoBrandNameList){
            String tobaccoBrandName = tobaccoBrandNameList.get(i);
            List<String> tobaccoNameByBrandNameList = tobaccoDaoService.selectTobaccoNameByBrandName(tobaccoBrandName);
            Log.d(TAG, "displayTobaccoFromDB - tobaccoDBHelper.selectTobaccoNameByBrandName(" + tobaccoBrandName + ") 수행");
            tobaccoNameByAllBrandNameList.add(tobaccoNameByBrandNameList);
        }

        // 2. 데이터 삽입
        List<Map<String, String>> tobaccoBrandNameListData = new ArrayList<Map<String, String>>();
        List<List<Map<String, String>>> tobaccoNameByBrandNameListData = new ArrayList<List<Map<String,String>>>();
        for(int i=0; i<tobaccoBrandNameList.size(); i++){

            Map<String, String> tobaccoBrandName = new HashMap<String, String>();
            tobaccoBrandName.put(LIST_TOBACCO_BRAND, tobaccoBrandNameList.get(i));
            tobaccoBrandNameListData.add(tobaccoBrandName); // Parent Data 삽입

            List<Map<String, String>> children = new ArrayList<Map<String, String>>();
            for(int j=0; j<tobaccoNameByAllBrandNameList.get(i).size(); j++){
                Map<String, String> tobaccoNameByBrandName = new HashMap<String, String>();
                tobaccoNameByBrandName.put(LIST_TOBACCO_NAME, tobaccoNameByAllBrandNameList.get(i).get(j));
                children.add(tobaccoNameByBrandName);
            }
            tobaccoNameByBrandNameListData.add(children); // Child Data 삽입
        }

        // 3. 어댑터 설정
        SimpleExpandableListAdapter tobaccoBrandNameList_With_TobaccoNameByBrandNameList_Adapter = new SimpleExpandableListAdapter(
                this, tobaccoBrandNameListData, R.layout.tobacco_brand_list_view_item, new String[]{LIST_TOBACCO_BRAND}, new int[]{R.id.text_tobacco_brand_item},
                tobaccoNameByBrandNameListData, R.layout.tobacco_name_list_view_item, new String[] {LIST_TOBACCO_NAME}, new int[]{R.id.text_tobacco_name_item}
        );

        // 4. 어댑터에 데이터 공급
        mTobaccoList.setAdapter(tobaccoBrandNameList_With_TobaccoNameByBrandNameList_Adapter);

    }
}
