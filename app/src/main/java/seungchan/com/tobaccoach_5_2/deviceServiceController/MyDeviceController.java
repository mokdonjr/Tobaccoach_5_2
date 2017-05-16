package seungchan.com.tobaccoach_5_2.deviceServiceController;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import seungchan.com.tobaccoach_5_2.webService.NetworkService;
import seungchan.com.tobaccoach_5_2.model.Record;
import seungchan.com.tobaccoach_5_2.model.User;
import seungchan.com.tobaccoach_5_2.dao.TobaccoDBHelper;
import seungchan.com.tobaccoach_5_2.utils.AppSettingUtils;
import seungchan.com.tobaccoach_5_2.utils.TimezoneUtils;
import seungchan.com.tobaccoach_5_2.R;
import seungchan.com.tobaccoach_5_2.genericAttributeProfile.TobaccoachGattAttributes;
import seungchan.com.tobaccoach_5_2.utils.DBUtils;
import seungchan.com.tobaccoach_5_2.model.Tobacco;
import seungchan.com.tobaccoach_5_2.webService.ApplicationController;

public class MyDeviceController extends AppCompatActivity {
    private int value = 0; // progress 저장
    private final static String TAG = MyDeviceController.class.getSimpleName();

    // intent data from MyDeviceScanActivity
    private String mDeviceName;
    private String mDeviceAddress;
    private String mIpAddress;
    private String userId;

    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mWriteCharacteristic; // BLE로 response (시간동기화)
    private BluetoothGattService mBluetoothGattService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    // db connection
    private TobaccoDBHelper mTobaccoDBHelper; //private UserLogDBHelper mUserLogDBHelper;
    private AppSettingUtils mAppSettingUtils;
    private NetworkService networkService;

    // 프로그래스 양
    private int averageAmount; // 전체
    private int todayAmount; // 채워짐

    @BindView(R.id.navigation) BottomNavigationView navigation; // 1. Home Framelayout
    @BindView(R.id.frame_navigation_home) FrameLayout mFrameHome;
    @BindView(R.id.webview_tobaccoach) WebView mWebView;
    private WebSettings mWebSettings;

    @BindView(R.id.frame_navigation_today) FrameLayout mFrameToday; // 2. Today Framelayout
    @BindView(R.id.today_amount) TextView mTodayAmount;
    @BindView(R.id.average_amount) TextView mAverageAmount;
    @BindView(R.id.today_average_amount_progressbar) ProgressBar mProgressbar;

    @BindView(R.id.frame_navigation_dev) FrameLayout mFrameDev; // 3. Dev Framelayout
    @BindView(R.id.message_from_hw) TextView mDataField;
    @BindView(R.id.connection_state_hw) TextView mConnectionState;

    @BindView(R.id.time_sync_state) TextView mTimeSyncState; // time sync
    @BindView(R.id.time_sync_button) Button mTimeSyncButton;

    @BindView(R.id.listview_db_log) ListView mLogDataListView; // db log
    @BindView(R.id.reset_sample_log_data_btn) Button mResetSampleButton;
    @BindView(R.id.all_log_data_delete_btn) Button mDeleteAllLogButton;
    private List<String> mLogData;
    private LogDataAdapter mLogDataAdapter;

    @BindView(R.id.result_from_web) TextView mResultFromWeb; // request result

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) { // ACTION_GATT_CONNECTED: connected to a GATT server.
                mConnected = true;
                updateConnectionState(R.string.connected);
                //invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) { // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                //invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) { // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
                Log.d(TAG, "mGattUpdateReceiver ACTION_GATT_SERVICES_DISCOVERED");
                // ExpandedListView를 보여주지않고 자료구조 저장만
                displayGattServices(mBluetoothLeService.getSupportedGattServices());

                // 이벤트스레드가 아닌 순차적 수행
                if(mGattCharacteristics != null){
                    final BluetoothGattCharacteristic characteristic = mGattCharacteristics.get(2).get(0);
                    final int charaProp = characteristic.getProperties();

                    // READ
                    if((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0){
                        if(mNotifyCharacteristic != null){
                            mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
                            mNotifyCharacteristic = null;
                        }
                        mBluetoothLeService.readCharacteristic(characteristic);
                    }

                    // NOTIFY
                    if((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0){
                        mNotifyCharacteristic = characteristic;
                        mBluetoothLeService.setCharacteristicNotification(characteristic, true);
                    }
                }

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) { // ACTION_DATA_AVAILABLE: received data from the device.

                String message = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);

                // 1. 블루투스 데이터 업데이트
                displayData(message);

                // 2. 시간동기화 상태 업데이트
                displayTimeSync(message);

                // 3. db에 업데이트
                if(mTobaccoDBHelper.insertLogData(message) != false) { // insert 성공
                    Toast.makeText(getApplicationContext(), "DB에 " + message + " 완료", Toast.LENGTH_SHORT).show();
                }

                // 4. db의 today amount, average amount 업데이트
                todayAmount = mTobaccoDBHelper.getTodayLogAmount(); // onCreate에 있어도 리시버에 존재해야함
                averageAmount = mTobaccoDBHelper.getAverageLogAmount(); // onCreate에 있어도 리시버에 존재해야함
                displayTodayAmount(todayAmount);
                displayAverageAmount(averageAmount);
                Log.d(TAG, "오늘 날짜 ( " + TimezoneUtils.getTodayDate() + " ) 의 흡연량은 " + todayAmount);

                // 5. 프로그래스바 업데이트
                displayProgressTodayByAverage(todayAmount-1, todayAmount, averageAmount);

                // 6. LogData ListView 업데이트
                updateLogDataList(mTobaccoDBHelper.selectAllDateTimeLogData());

                // 5. Web에 전송
//                new JsonInsertTask().execute(message); //Async스레드를 시작
                String parsedMessage = TimezoneUtils.formatHwDateTime(message); //.formatHwDateTime(message);
                webAccessForRequestInsert(parsedMessage);

            }
        }
    };



    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, TobaccoachGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, TobaccoachGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

//    private void updateLogDataList(){
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mLogDataAdapter.notifyDataSetChanged();
//            }
//        });
//    }
    private void updateLogDataList(List<String> newData){
        mLogDataAdapter.updateLogData(newData);
    }


    private void displayTimeSync(String message){

        if(TimezoneUtils.checkTimeSyncState(message)){
            Log.d(TAG, "displayTimeSync메서드 hwDateTime과 nowDateTime이 일치 (동기화 된 상태)");
            mTimeSyncState.setText(R.string.time_sync_complete);
            mTimeSyncButton.setEnabled(false);
        }
        else{
            Log.d(TAG, "displayTimeSync메서드 hwDateTime과 nowDateTime이 다름 (동기화 필요!)");
            mTimeSyncState.setText(R.string.time_sync_required);
            mTimeSyncButton.setEnabled(true);
        }
    }

    private void clearUI() {
        mDataField.setText(R.string.no_data); // mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
    }

    private void displayData(String message) {
        mDataField.setText(message); // mConnectionState.setText(state_data);
    }

    private void displayTodayAmount(int todayAmount){
        mTodayAmount.setText(String.valueOf(todayAmount));
    }

    private void displayAverageAmount(int averageAmount) {
        mAverageAmount.setText("/" + String.valueOf(averageAmount)); // 리시버 지속적인 업데이트 필요
    }

    private void displayProgressTodayByAverage(final int initialValue, int progressValue, final int maxValue){
        Log.d(TAG, "displayProgressTodayByAverage() 메서드");



        final double smokePercentage = ((double)progressValue/maxValue) * 100;
        new Thread(new Runnable() {

            @Override
            public void run() {

                for (int i = 0; i < smokePercentage; i++){
                    try {
                        Thread.sleep(50);
                        mProgressbar.setProgress(i);
                        value = mProgressbar.getProgress();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

//    private void displayProgressTodayByAverage(int progressValue, final int maxValue){
//        Log.d(TAG, "displayProgressTodayByAverage() 메서드");
//
//        // 1. progress 값 초기화
//        int progress = progressValue;
//        mProgressbar.setProgress(progress); // 초기값 todayAmount로 설정
//        int standard = mProgressbar.getProgress(); // 위 setProgress없으면 0
//
//        // 2. max값 초기화
//        int max = maxValue;
//        mProgressbar.setMax(max);
//
//        Log.d(TAG, "displayProgressTodayByAverage() 메서드, mProgressbar.getProgress() 초기값은 : " + standard
//                + " todayAmount값 ( " + progress + " ) 과 같아야 한다. cf. averageAmount값 : " + averageAmount);
//
//    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            /* GATT Server (스마트 담배케이스)와 연결 connectGatt. BluetoothLeService내 connect메서드 이용 */
            mBluetoothLeService.connect(mDeviceAddress); // Automatically connects to the device upon successful start-up initialization.
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(TAG, "onServiceDisconnected 메서드 호출");
            mBluetoothLeService = null;
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.navigation_today:
                    mFrameHome.setVisibility(View.GONE);
                    mFrameToday.setVisibility(View.VISIBLE);
                    mFrameDev.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_home:
                    mFrameHome.setVisibility(View.VISIBLE);
                    mFrameToday.setVisibility(View.GONE);
                    mFrameDev.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_dev:
                    mFrameHome.setVisibility(View.GONE);
                    mFrameToday.setVisibility(View.GONE);
                    //updateLogDataList(mTobaccoDBHelper.selectAllDateTimeLogData());
                    mFrameDev.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }
    };

    public void getIntentFromPrevious(){
        /* DeviceScanActivity 로 부터 디바이스 정보, IP 주소 얻기 */
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(AppSettingUtils.EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(AppSettingUtils.EXTRAS_DEVICE_ADDRESS);
        mIpAddress = intent.getStringExtra(AppSettingUtils.EXTRAS_SERVER_IP);
        userId = intent.getStringExtra(AppSettingUtils.EXTRAS_MY_ID);
        Log.d(TAG, "onCreate 메서드, DeviceScanActivity로 부터 getIntent데이터 : "
                + mDeviceName + "," + mDeviceAddress + "," + mIpAddress + "," + userId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_my_device_controller);
        ButterKnife.bind(this);

        getIntentFromPrevious();

        // db
        mTobaccoDBHelper = TobaccoDBHelper.getInstance(getApplicationContext());
        mAppSettingUtils = AppSettingUtils.getInstance();

        // 1. Home Framelayout
        if(mAppSettingUtils != null){
            mWebView.setWebViewClient(new WebViewClient());
            mWebSettings = mWebView.getSettings();
            mWebSettings.setJavaScriptEnabled(true);
            mWebView.loadUrl(mAppSettingUtils.getWebApplicationServerUrl(mIpAddress)); //mWebView.loadUrl("http://www.naver.com/"); //
        }

        // 2. Today Framelayout
        todayAmount = mTobaccoDBHelper.getTodayLogAmount();
        mTodayAmount.setText(String.valueOf(todayAmount)); // 리시버 지속적인 업데이트 필요
        averageAmount = mTobaccoDBHelper.getAverageLogAmount();
        mAverageAmount.setText("/" + String.valueOf(averageAmount)); // 리시버 지속적인 업데이트 필요
        // progress bar
        displayProgressTodayByAverage(0, todayAmount, averageAmount);

        // 3. Dev Framelayout
        mLogData = new ArrayList<String>();
        mLogData = mTobaccoDBHelper.selectAllDateTimeLogData(); // 1. 로그 데이터 설정
        mLogDataAdapter = new LogDataAdapter(this, mLogData); // 2. 어댑터에 로그 데이터 설정
        mLogDataListView.setAdapter(mLogDataAdapter); // 3. 리스트뷰에 어댑터 설정

        updateLogDataList(mTobaccoDBHelper.selectAllDateTimeLogData());

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        /* binding Local Ble Service */
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        if(mAppSettingUtils != null){ //ip , port 연결
            ApplicationController application= ApplicationController.getInstance();  //앱이 처음 실행될 때 인스턴스 생성.
            application.buildNetworkService(mAppSettingUtils.getWebApplicationServerUrl(mIpAddress));  //다음의 url로 네트워크서비스 준비.
            networkService= ApplicationController.getInstance().getNetworkService();
        }
    }

    public void sqliteAccessForResetLogData(Context context){
        Toast.makeText(this, "데이터 로딩중 ... ", Toast.LENGTH_SHORT).show();

        HashMap sampleLogDataMap = DBUtils.getSampleLogDataMap(); // 메모리에 생성
        mTobaccoDBHelper.insertForResetAllLogData(sampleLogDataMap); // 다지우고 새로 생성
        Toast.makeText(this, DBUtils.LOG_TABLE_NAME + " 테이블 내 " + mTobaccoDBHelper.getAllLogTableRowNum() + "개 데이터가 존재", Toast.LENGTH_SHORT).show();

        Toast.makeText(this, "데이터 로딩 완료", Toast.LENGTH_SHORT).show();
    }

    public void sqliteAccessForDeleteAllLogData(Context context){
        Toast.makeText(this, DBUtils.LOG_TABLE_NAME + "테이블 모두 삭제", Toast.LENGTH_SHORT).show();

        mTobaccoDBHelper.deleteAllLogData();

        Toast.makeText(this, DBUtils.LOG_TABLE_NAME + " 테이블 내 " + mTobaccoDBHelper.getAllLogTableRowNum() + "개 데이터가 존재", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }


    @OnClick(R.id.time_sync_button) void onClickTimeSyncButton(){
        if(mGattCharacteristics != null){
            final BluetoothGattCharacteristic characteristic_write = mGattCharacteristics.get(2).get(0);
            final int charaProp_write = characteristic_write.getProperties();
            if(((charaProp_write | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) && (characteristic_write != null)) {
                if(mNotifyCharacteristic != null){
                    mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
                    mNotifyCharacteristic = null;
                }
                characteristic_write.setValue(TimezoneUtils.getDS3231DateTime());
                characteristic_write.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                mBluetoothLeService.writeCharacteristic(characteristic_write);
            }
        }
    }

    @OnClick(R.id.reset_sample_log_data_btn) void onClickResetSampleButton(){
        sqliteAccessForResetLogData(getApplicationContext());
        updateLogDataList(mTobaccoDBHelper.selectAllDateTimeLogData());
        /*
            TodaySmoked도 최신화 되어야
         */
    }

    @OnClick(R.id.all_log_data_delete_btn) void onClickDeleteAllLogButton(){
        sqliteAccessForDeleteAllLogData(getApplicationContext());
        updateLogDataList(mTobaccoDBHelper.selectAllDateTimeLogData());
        /*
            TodaySmoked도 최신화 되어야
         */
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void webAccessForRequestInsert(String dateTime){
        Record record = new Record();
//        Date dateObject = new Date();
        Date dateObject = TimezoneUtils.parseStringToDateClass(dateTime);
        User user = new User();
        user.setNick(userId);

        record.setDate(dateObject);
        record.setUser(user);

        Call<Record> thumbnailCall = networkService.post_record(record);
        thumbnailCall.enqueue(new Callback<Record>() {
            @Override
            public void onResponse(Call<Record> call, Response<Record> response) {
                if(response.isSuccessful()) {
                    Log.i(TAG, "응답코드 : " + "등록되었습니다.");
                } else {
                    int statusCode= response.code();
                    Log.i(TAG, "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<Record> call, Throwable t) {
                Log.i(TAG, "서버 onFailure 에러내용 : " + t.getMessage());
            }
        });
    }

}