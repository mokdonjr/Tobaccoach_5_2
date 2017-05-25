package seungchan.com.tobaccoach_5_2.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import seungchan.com.tobaccoach_5_2.R;
import seungchan.com.tobaccoach_5_2.ble.BluetoothLeService;
import seungchan.com.tobaccoach_5_2.dao.TobaccoDaoService;
import seungchan.com.tobaccoach_5_2.utils.BleUtils;
import seungchan.com.tobaccoach_5_2.utils.TimezoneUtils;


public class TimerFragment extends Fragment {
    private static String TAG = "TimerFragment";

    private OnFragmentInteractionListener mListener;

    private TobaccoDaoService mTobaccoDaoService;

    @BindView(R.id.timer_hour) TextView mTimerHour;
    @BindView(R.id.timer_min) TextView mTimerMin;
    @BindView(R.id.timer_sec) TextView mTimerSec;
//    @BindView(R.id.timer_average_hour) TextView mTimerAverageHour;
//    @BindView(R.id.timer_average_min) TextView mTimerAverageMin;
//    @BindView(R.id.timer_average_sec) TextView mTimerAverageSec;
    private String currentTime;
    private String lastLogTime;
    private String elapsedTimeHour;
    private String elapsedTimeMin;
    private String elapsedTimSec;
    private Thread timerThread;

    public TimerFragment() { }

    public static TimerFragment newInstance() {
        TimerFragment fragment = new TimerFragment();
        return fragment;
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().registerReceiver(mGattUpdateReceiver, BleUtils.makeGattUpdateIntentFilter());
    }

    @Override
    public void onPause(){
        super.onPause();
        getActivity().unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTobaccoDaoService = TobaccoDaoService.getInstance(getActivity());



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View timerFragmentView = inflater.inflate(R.layout.fragment_timer, container, false);
        ButterKnife.bind(this, timerFragmentView);

        // 1. 타이머 초기화
        setTimer();

        // 2. 타이머 동작
        loopTimer();

        return timerFragmentView;
    }

    // 타이머 초기화
    public void setTimer(){

        // 2. 가장 최근 시각
        if(mTobaccoDaoService.getAllLogTableRowNum() > 0){ // 새 유저의 경우 없음
            lastLogTime = mTobaccoDaoService.getLastLog(); // 2017-04-16 15:05:00 반환
        }

        // 3. 현재시각과 가장 최근 시각간 '시간차'
        if(lastLogTime != null){ // 새 유저의 경우 없음
            List<String> elapsedTime = TimezoneUtils.getElapsedTime(lastLogTime);

            elapsedTimeHour = elapsedTime.get(0); // hour
            elapsedTimeMin = elapsedTime.get(1); // min
            elapsedTimSec = elapsedTime.get(2); // sec

            // Timer 초기화
            mTimerHour.setText(elapsedTimeHour);
            mTimerMin.setText(elapsedTimeMin);
            mTimerSec.setText(elapsedTimSec);
        }
    }

    public void loopTimer(){
        if(lastLogTime != null) { // 새 유저의 경우 없음
            final int offset = 1;
            timerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (Thread.currentThread() == timerThread) {
                        mTimerSec.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "loopTimer 메서드 " + mTimerSec.getText()); //로그로 '초'를 출력
                                int sec = Integer.parseInt(mTimerSec.getText().toString()) + offset; // 초
                                int min = Integer.parseInt(mTimerMin.getText().toString()) + offset; // 분
                                int hour = Integer.parseInt(mTimerHour.getText().toString()) + offset; // 시

                                String _sec = Integer.toString(sec % 60);
                                if (_sec.length() == 1) {
                                    _sec = "0" + _sec;
                                } // 한자리일 경우
                                mTimerSec.setText(_sec);

                                if (sec % 60 == 0) {
                                    String _min = Integer.toString(min % 60);
                                    if (_min.length() == 1) {
                                        _min = "0" + _min;
                                    }  // 한자리일 경우
                                    mTimerMin.setText(_min);

                                    if (min % 60 == 0) {
                                        String _hour = Integer.toString(hour);
                                        if (_hour.length() == 1) {
                                            _hour = "0" + _hour;
                                        } // 한자리일 경우
                                        Log.d(TAG, "시/분/초 : " + _hour + "/" + _min + "/" + _sec);
                                        mTimerHour.setText(_hour);
                                    }
                                }
                            }
                        });
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            timerThread.start();
        }
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

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) { // ACTION_DATA_AVAILABLE: received data from the device.
                Log.d(TAG, "ACTION_DATA_AVAILABLE 리시버 동작");

                String message = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                /*
                    displayTimerFromLast
                 */
                // 1. 타이머 초기화
                setTimer();

                // 2. 타이머 동작
                loopTimer();

            }
        }
    };
}
