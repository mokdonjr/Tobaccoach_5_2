package seungchan.com.tobaccoach_5_2.activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

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
import seungchan.com.tobaccoach_5_2.R;
import seungchan.com.tobaccoach_5_2.ble.BluetoothLeService;
import seungchan.com.tobaccoach_5_2.dao.TobaccoDBHelper;
import seungchan.com.tobaccoach_5_2.dao.TobaccoDaoService;
import seungchan.com.tobaccoach_5_2.fragment.CoachFragment;
import seungchan.com.tobaccoach_5_2.fragment.DailyChartFragment;
import seungchan.com.tobaccoach_5_2.fragment.DeviceSettingFragment;
import seungchan.com.tobaccoach_5_2.fragment.RankingFragment;
import seungchan.com.tobaccoach_5_2.fragment.TimelyChartFragment;
import seungchan.com.tobaccoach_5_2.fragment.TimerFragment;
import seungchan.com.tobaccoach_5_2.fragment.TobaccoFragment;
import seungchan.com.tobaccoach_5_2.ble.TobaccoachGattAttributes;
import seungchan.com.tobaccoach_5_2.model.Record;
import seungchan.com.tobaccoach_5_2.model.ResultObject;
import seungchan.com.tobaccoach_5_2.model.Tobacco;
import seungchan.com.tobaccoach_5_2.model.User;
import seungchan.com.tobaccoach_5_2.utils.AppSettingUtils;
import seungchan.com.tobaccoach_5_2.utils.TimezoneUtils;
import seungchan.com.tobaccoach_5_2.webService.ApplicationController;
import seungchan.com.tobaccoach_5_2.webService.NetworkService;

public class MainActivity extends AppCompatActivity implements
        TobaccoFragment.OnFragmentInteractionListener , DailyChartFragment.OnFragmentInteractionListener , TimelyChartFragment.OnFragmentInteractionListener
        , RankingFragment.OnFragmentInteractionListener, DeviceSettingFragment.OnFragmentInteractionListener, CoachFragment.OnFragmentInteractionListener, TimerFragment.OnFragmentInteractionListener {
    private static String TAG = "MainActivity";

    // intent data from MyDeviceScanActivity
    private String mDeviceName;
    private String mDeviceAddress;
    private String mIpAddress;
    private String userId;
    private Tobacco mTobacco;

    // ble connection
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mWriteCharacteristic; // BLE로 response (시간동기화)
    private BluetoothGattService mBluetoothGattService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    // db connection
    private TobaccoDaoService mTobaccoDaoService;

    // web connection
    private AppSettingUtils mAppSettingUtils;
    private NetworkService networkService;

    private ActionBar mActionBar;

    private KakaoLink mKakaoLink;

    private Context mContext;

    @BindView(R.id.bottom_nav) BottomNavigationView bottomNavigationView;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.fab_kakao) FloatingActionButton fabKakao;
    @BindView(R.id.fab_time_sync) FloatingActionButton fabTimeSync;
    @BindView(R.id.fab_device_setting) FloatingActionButton fabDeviceSetting;

    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    boolean isFabOpened = false; // fab 플로팅 버튼 상태

    public void getIntentFromPrevious(){
        /* DeviceScanActivity 로 부터 디바이스 정보, IP 주소 얻기 */
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(AppSettingUtils.EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(AppSettingUtils.EXTRAS_DEVICE_ADDRESS);
        mIpAddress = intent.getStringExtra(AppSettingUtils.EXTRAS_SERVER_IP);
        userId = intent.getStringExtra(AppSettingUtils.EXTRAS_MY_ID);
//        mTobacco = intent.getParcelableExtra(AppSettingUtils.EXTRAS_MY_TOBACCO);
        Log.d(TAG, "onCreate 메서드, DeviceScanActivity로 부터 getIntent데이터 : "
                + mDeviceName + "," + mDeviceAddress + "," + mIpAddress + "," + userId
//                + "," + mTobacco.toString()
            );
    }

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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_my_tobacco:
                    mActionBar.setTitle(R.string.title_my_tobacco);
                    FragmentTransaction tobaccoFragmentTransaction = getSupportFragmentManager().beginTransaction();
                    selectedFragment = TobaccoFragment.newInstance(mIpAddress, userId);
                    tobaccoFragmentTransaction.replace(R.id.content, selectedFragment);
                    tobaccoFragmentTransaction.commit();
                    return true;

                case R.id.navigation_daily_chart:
                    mActionBar.setTitle(R.string.title_daily_chart);
                    FragmentTransaction coachFragmentTransaction = getSupportFragmentManager().beginTransaction();
                    selectedFragment = DailyChartFragment.newInstance(mIpAddress); // ip주소로 DailyChart에 AllUserAverageAmount 라인을 추가
                    coachFragmentTransaction.replace(R.id.content, selectedFragment);
                    coachFragmentTransaction.commit();
                    return true;

                case R.id.navigation_ranking:
                    mActionBar.setTitle(R.string.title_ranking);
                    FragmentTransaction rankingFragmentTransaction = getSupportFragmentManager().beginTransaction();
                    selectedFragment = RankingFragment.newInstance(mIpAddress); // 인수 전달
                    rankingFragmentTransaction.replace(R.id.content, selectedFragment, RankingFragment.ARG_PARAM1);
                    rankingFragmentTransaction.commit();

                    return true;
            }
            return false;
        }

    };

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getIntentFromPrevious();

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mTobaccoDaoService = TobaccoDaoService.getInstance(getApplicationContext());
        mAppSettingUtils = AppSettingUtils.getInstance();
        mContext = this; // 다른 앱에 intent flag는 상위 액티비티 context

        /* Set First Fragment */
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(R.string.title_my_tobacco);
        FragmentTransaction firstFragmentTransaction = getSupportFragmentManager().beginTransaction();
        firstFragmentTransaction.replace(R.id.content, TobaccoFragment.newInstance(mIpAddress, userId));
        firstFragmentTransaction.commit();

        /* Kakao Service */
        try {
            mKakaoLink = KakaoLink.getKakaoLink(MainActivity.this);
        } catch (KakaoParameterException e) {
            e.printStackTrace();
        }

        /* binding Local Ble Service */
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        /* Network Service */
        if(mAppSettingUtils != null){ //ip , port 연결
            ApplicationController application= ApplicationController.getInstance();  //앱이 처음 실행될 때 인스턴스 생성.
            application.buildNetworkService(mAppSettingUtils.getWebApplicationServerUrl(mIpAddress));  //다음의 url로 네트워크서비스 준비.
            networkService= ApplicationController.getInstance().getNetworkService();
        }
    }

    @OnClick(R.id.fab) void onClickFabFloatingButton(){
        animateFabOpening();
    }

    @OnClick(R.id.fab_kakao) void onClickFabKakaoFloatingButton(){
        Toast.makeText(getBaseContext(), "카카오톡을 연결합니다", Toast.LENGTH_LONG).show();
        final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder
                = mKakaoLink.createKakaoTalkLinkMessageBuilder();
        try {
            kakaoTalkLinkMessageBuilder.addText("토바코치");//링크 객체에 카카오톡 보낼 메시지
        } catch (KakaoParameterException e) {
            e.printStackTrace();
        }
        try {
            mKakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, mContext);// 메시지 전송
        } catch (KakaoParameterException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.fab_time_sync) void onClickFabTimeSyncFloatingButton(){
        Toast.makeText(getBaseContext(), "담배케이스의 시간정보를 동기화 합니다", Toast.LENGTH_LONG).show();
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

        Toast.makeText(getBaseContext(), "하드웨어의 시간정보 동기화가 진행됩니다. 앱을 다시 실행해 주세요.", Toast.LENGTH_LONG).show();
        finish();

    }

    @OnClick(R.id.fab_device_setting) void onClickFabDeviceSettingFloatingButton(){
        Toast.makeText(getBaseContext(), "개발자 화면이 나타납니다", Toast.LENGTH_LONG).show();
        mActionBar.setTitle(R.string.title_device_setting);
        Fragment selectedFragment = null;
        FragmentTransaction deviceSettingFragmentTransaction = getSupportFragmentManager().beginTransaction();
        selectedFragment = DeviceSettingFragment.newInstance();
        deviceSettingFragmentTransaction.replace(R.id.content, selectedFragment);
        deviceSettingFragmentTransaction.commit();
    }

    public void animateFabOpening() {

        if (isFabOpened) {

            fab.startAnimation(rotate_backward);
            fabKakao.startAnimation(fab_close);
            fabTimeSync.startAnimation(fab_close);
            fabDeviceSetting.startAnimation(fab_close);

            fabKakao.setClickable(false);
            fabTimeSync.setClickable(false);
            fabDeviceSetting.setClickable(false);
            isFabOpened = false;
            Log.d(TAG, "플로팅바 close");

        } else {

            fab.startAnimation(rotate_forward);
            fabKakao.startAnimation(fab_open);
            fabTimeSync.startAnimation(fab_open);
            fabDeviceSetting.startAnimation(fab_open);

            fabKakao.setClickable(true);
            fabTimeSync.setClickable(true);
            fabDeviceSetting.setClickable(true);
            isFabOpened = true;
            Log.d(TAG, "플로팅바 open");
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

    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) { // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
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
                Log.d(TAG, "ACTION_DATA_AVAILABLE 리시버 동작");

                String message = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                if(message.length() < 18 && message.length() > 11){ // 17/5/1-6:6:2 ~ 17/10/10-10:10:10
                    // 3. db에 업데이트
                    if(mTobaccoDaoService.insertLogData(message) != false) { // insert 성공
                        Toast.makeText(getApplicationContext(), "DB에 " + message + " 완료", Toast.LENGTH_SHORT).show();
                    }

                    // 5. Web에 전송
                    String parsedMessage = TimezoneUtils.formatHwDateTime(message); //.formatHwDateTime(message);

                    webAccessForRequestInsert(parsedMessage);
                }
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

    public void webAccessForRequestInsert(String dateTime){
        Record record = new Record();
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
