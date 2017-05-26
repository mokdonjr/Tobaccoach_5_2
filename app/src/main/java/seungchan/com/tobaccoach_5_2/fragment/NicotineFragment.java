package seungchan.com.tobaccoach_5_2.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import seungchan.com.tobaccoach_5_2.R;
import seungchan.com.tobaccoach_5_2.ble.BluetoothLeService;
import seungchan.com.tobaccoach_5_2.dao.TobaccoDaoService;
import seungchan.com.tobaccoach_5_2.model.Tobacco;

/* 재혁이 니코틴 프로그래스 */
public class NicotineFragment extends Fragment {
    private static String TAG = "NicotineFragment";

    private static final String ARG_PARAM1 = "tobaccoId";

    private int mTobaccoId;

    private OnFragmentInteractionListener mListener;

    // 담배 니코틴 계산
    private Tobacco mTobacco = null;
    private TobaccoDaoService mTobaccoDaoService;
    // 정욱
    private int gage = 0;
    Handler handler = new Handler();

    @BindView(R.id.progressbar_nicotine_in_fragment)
    ProgressBar mProgressbarNicotineInFragment;

    public NicotineFragment() { }

    public static NicotineFragment newInstance(int tobaccoId) {
        NicotineFragment fragment = new NicotineFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, tobaccoId);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTobaccoId = getArguments().getInt(ARG_PARAM1);
        }
        mTobaccoDaoService = TobaccoDaoService.getInstance(getActivity());
        // 담배 정보 꺼내옴
        mTobacco = mTobaccoDaoService.selectTobaccoById(mTobaccoId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View nicotineFragmentView =  inflater.inflate(R.layout.fragment_nicotine, container, false);
        ButterKnife.bind(this, nicotineFragmentView);
        /*
            nicotineProgress
         */
        // 3. tobaccoPrice * allRowNum 계산해 출력
        displayNicotineProgress(mTobacco);


        return nicotineFragmentView;
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

                /*
                    displayNicotine 프로그래스
                 */
                displayNicotineProgress(mTobacco);
            }
        }
    };

    public void displayNicotineProgress(Tobacco tobacco){

        gage = gage + 5;
        mProgressbarNicotineInFragment.setProgress(gage);

        // 호출시 감소 상태 시작
        handler.post(initRunnable);

    }

    //init animation
    Runnable initRunnable = new Runnable() {
        @Override
        public void run() {
            if (gage <= 50) {
                gage++;
                mProgressbarNicotineInFragment.setProgress(gage);
                handler.postDelayed(initRunnable, 40);
            } else {
                handler.removeCallbacks(this);
                handler.post(decreaseRunnable);
            }
        }
    };

    //감소 상태 정의
    Runnable decreaseRunnable = new Runnable() {
        @Override
        public void run() {
            if (gage >= 0) {
                gage = gage - 1;
                mProgressbarNicotineInFragment.setProgress(gage);
            }
            // 0.6 초 후 재호출
            handler.postDelayed(decreaseRunnable, 100000);
        }
    };
}
