package seungchan.com.tobaccoach_5_2.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import seungchan.com.tobaccoach_5_2.R;
import seungchan.com.tobaccoach_5_2.dao.TobaccoDBHelper;
import seungchan.com.tobaccoach_5_2.ble.BluetoothLeService;
import seungchan.com.tobaccoach_5_2.dao.TobaccoDaoService;
import seungchan.com.tobaccoach_5_2.model.Tobacco;
import seungchan.com.tobaccoach_5_2.utils.AppSettingUtils;
import seungchan.com.tobaccoach_5_2.utils.TimezoneUtils;
import seungchan.com.tobaccoach_5_2.webService.ApplicationController;
import seungchan.com.tobaccoach_5_2.webService.NetworkService;


/*
 * 프래그먼트 생명주기 (소속 액티비티가 실행 중일 때)
 * 1. Fragment is Added - onAttache() - onCreate() - "onCreateView()" - onActivityCreated() - onStart() - onResume() - Fragment is Active
 * 2. Fragment is Removed/Replaced - onPause() - onStop() - "onDestroyView()" - onDestroy() - onDetach() - Fragment is Destroyed
 * 프래그먼트 다시 보기
 * onDestroyView() - onCreateView()
 */
public class TobaccoFragment extends Fragment {
    private static String TAG = "TobaccoFragment";

    private OnFragmentInteractionListener mListener;

    private static final String ARG_PARAM1 = "ipAddress";
    private String ipAddress;
    private static final String ARG_PARAM2 = "userId";
    private String userId;

    private TobaccoDaoService mTobaccoDaoService;

    // 프로그래스 양
    private int averageAmount; // 전체
    private int todayAmount; // 채워짐

    @BindView(R.id.tobacco_today_and_nico_background) ImageView mBackground;
    @BindView(R.id.today_amount) TextView mTodayAmount;
    @BindView(R.id.average_amount) TextView mAverageAmount;
    @BindView(R.id.today_average_amount_progressbar) ProgressBar mProgressbar;

    private AppSettingUtils mAppSettingUtils;
    private NetworkService mNetworkService;
    private int mTobaccoId; // 웹에서 받아옴

    public TobaccoFragment() { }

    public static TobaccoFragment newInstance(String param1, String param2) {
        TobaccoFragment fragment = new TobaccoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    public void onPause(){
        super.onPause();
        getActivity().unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { // 프래그먼트 생성, 프래그먼트 중단/재개 시 유지하고자 하는 것 초기화
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ipAddress = getArguments().getString(ARG_PARAM1);
            userId = getArguments().getString(ARG_PARAM2);
        }

        mTobaccoDaoService = TobaccoDaoService.getInstance(getActivity());
        // 1. today amount
        todayAmount = mTobaccoDaoService.getTodayLogAmount();
        // 2. average amount
        averageAmount = mTobaccoDaoService.getAverageLogAmount();

        /* network connection */
        mAppSettingUtils = AppSettingUtils.getInstance();
        if(mAppSettingUtils != null){
            //ip , port 연결
            ApplicationController application= ApplicationController.getInstance();  //앱이 처음 실행될 때 인스턴스 생성.
            application.buildNetworkService(mAppSettingUtils.getWebApplicationServerUrl(ipAddress));  //다음의 url로 네트워크서비스 준비.
            mNetworkService= ApplicationController.getInstance().getNetworkService();
        }

        // TimerFragment 실행
        Fragment selectedFragment = null;
        FragmentTransaction timerFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        selectedFragment = TimerFragment.newInstance();
        timerFragmentTransaction.replace(R.id.content_timer_fragment, selectedFragment);
        timerFragmentTransaction.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { // 프래그먼트 UI
        View tobaccoFragmentView = inflater.inflate(R.layout.fragment_tobacco, container, false);
        ButterKnife.bind(this, tobaccoFragmentView);


        // onCreate메서드에 있으면 MainActivity로부터 받아옴에도 불구하고 userId가 null이라 어쩔 수 없이 onCreateView메서드에 위치
        // 3. 웹에서 myTobaccoId를 받아옴 + // 4. CoachFragment 수행 (tobaccoId를 넘겨줌)
        getMyTobaccoFromWeb(userId); // mTobaccoId 받아옴
        Log.d(TAG, "웹에서 받아온 내 아이디 " + userId + "에 대한 tobaccoId : " + mTobaccoId);


        mTodayAmount.setText(String.valueOf(todayAmount)); // 리시버 지속적인 업데이트 필요

        mAverageAmount.setText("/" + String.valueOf(averageAmount)); // 리시버 지속적인 업데이트 필요
        // progress bar
        displayProgressTodayByAverage(0, todayAmount, averageAmount);

        return tobaccoFragmentView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) { // ACTION_DATA_AVAILABLE: received data from the device.
                Log.d(TAG, "ACTION_DATA_AVAILABLE 리시버 동작");

                String message = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);

                // 4. db의 today amount, average amount 업데이트
                todayAmount = mTobaccoDaoService.getTodayLogAmount(); // onCreate에 있어도 리시버에 존재해야함
                averageAmount = mTobaccoDaoService.getAverageLogAmount(); // onCreate에 있어도 리시버에 존재해야함
                displayTodayAmount(todayAmount);
                displayAverageAmount(averageAmount);
                Log.d(TAG, "오늘 날짜 ( " + TimezoneUtils.getTodayDate() + " ) 의 흡연량은 " + todayAmount);

                // 5. 프로그래스바 업데이트
                displayProgressTodayByAverage(todayAmount-1, todayAmount, averageAmount);

            }
        }
    };

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

                for (int i = initialValue; i < smokePercentage; i++){
                    try {
                        Thread.sleep(20);
                        mProgressbar.setProgress(i);
//                        value = mProgressbar.getProgress();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    // 웹에서 tobacco id 받아옴
    public void getMyTobaccoFromWeb(final String username){
        Call<Integer> thumbnailCall = mNetworkService.getTobaccoId(username);
        thumbnailCall.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()) {
                    // 1. 웹에서 tobaccoId 받아옴
                    int tobaccoId = response.body().intValue();
                    Log.i(TAG, "getMyTobaccoFramWeb 메서드에 넣은 username : " + username + " / 웹에서 받아온 mTobaccoId : " + tobaccoId);

                    // 2. tobaccoId 받으면 CoachFragment를 실행하며 전달
                    Fragment selectedFragment = null;
                    FragmentTransaction coachFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    selectedFragment = CoachFragment.newInstance(ipAddress, username, tobaccoId);
                    coachFragmentTransaction.replace(R.id.content_coach_fragment, selectedFragment);
                    coachFragmentTransaction.commit();
                } else {
                    int statusCode= response.code();
                    Log.i(TAG, "응답코드 : " + statusCode);
                }
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.i(TAG, "서버 onFailure 에러내용 : " + t.getMessage());
            }
        });
    }
}
