package seungchan.com.tobaccoach_5_2.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.CombinedData;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import seungchan.com.tobaccoach_5_2.R;
import seungchan.com.tobaccoach_5_2.ble.BluetoothLeService;
import seungchan.com.tobaccoach_5_2.dao.TobaccoDBHelper;
import seungchan.com.tobaccoach_5_2.dao.TobaccoDaoService;
import seungchan.com.tobaccoach_5_2.model.Tobacco;
import seungchan.com.tobaccoach_5_2.utils.AppSettingUtils;
import seungchan.com.tobaccoach_5_2.webService.ApplicationController;
import seungchan.com.tobaccoach_5_2.webService.NetworkService;

public class CoachFragment extends Fragment {
    private static String TAG = "CoachFragment";

//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM1 = "ipAddress";
//    private String ipAddress;
//    private static final String ARG_PARAM2 = "userId";
//    private String userId;
//    private static final String ARG_PARAM3 = "myTobaccoId";
    private static final String ARG_PARAM = "myTobaccoId";
    private int mTobaccoId;

    // 총 담배 금액 계산
    private Tobacco mTobacco = null;
    private TobaccoDaoService mTobaccoDaoService;

    @BindView(R.id.total_money_you_smoked) TextView mTextTotalMoneyYouSmoked;

    private OnFragmentInteractionListener mListener;

    public CoachFragment() { }

//    public static CoachFragment newInstance(String param1, String param2, int param3) {
    public static CoachFragment newInstance(int param) {
        CoachFragment fragment = new CoachFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        args.putInt(ARG_PARAM3, param3);
        args.putInt(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            ipAddress = getArguments().getString(ARG_PARAM1);
//            userId = getArguments().getString(ARG_PARAM2);
            // 1. tobaccoId 받아오기
//            mTobaccoId = getArguments().getInt(ARG_PARAM3);
            mTobaccoId = getArguments().getInt(ARG_PARAM);
        }
        mTobaccoDaoService = TobaccoDaoService.getInstance(getActivity());

        // 2. tobaccoId로 Tobacco 꺼내옴
        mTobacco = getMyTobaccoByTobaccoId(mTobaccoId);

    }

    public Tobacco getMyTobaccoByTobaccoId(int tobaccoId){
        Log.d(TAG, "getMyTobaccoPrice메서드, mTobaccoId 웹으로 부터 받아옴 : " + tobaccoId);
        return mTobaccoDaoService.selectTobaccoById(tobaccoId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View coachFragmentView = inflater.inflate(R.layout.fragment_coach, container, false);
        ButterKnife.bind(this, coachFragmentView);

        // 3. tobaccoPrice * allRowNum 계산해 출력
        displayTotalMoneyYouSmoked(mTobacco);

        return coachFragmentView;
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
                Log.d(TAG, "ACTION_DATA_AVAILABLE 리시버 동작, mTobacco : " + mTobacco.toString());

                displayTotalMoneyYouSmoked(mTobacco);
            }
        }
    };

    public void displayTotalMoneyYouSmoked(Tobacco tobacco){
        String finalTotalMoneyYouSmoked = ""; // 돈을 세자리당 , 표시한 결과

        int totalMoneyYouSmoked = 0; // 돈 결과

        if(tobacco != null){
            totalMoneyYouSmoked = mTobaccoDaoService.getAllTobaccoMoney(tobacco); // tobaccoPrice * allRowNum
            Log.d(TAG, "displayTotalMoneyYouSmoked 메서드, db에서 가져온 totalMoneyYouSmoked : " + totalMoneyYouSmoked);

            finalTotalMoneyYouSmoked = convertMoneyBundle(totalMoneyYouSmoked);

            mTextTotalMoneyYouSmoked.setText("￦" + finalTotalMoneyYouSmoked);
        }

        else{
            Toast.makeText(getActivity(), "담배 정보가 없습니다", Toast.LENGTH_SHORT).show();
        }

    }



    public String convertMoneyBundle(int money){
        String finalMoney = "";
        String temp = String.valueOf(money);
        int lengthString = temp.length();

        if(lengthString > 3 ) { // 3자리마다 , 삽입
            String subTemp1 = temp.substring(0, lengthString - 3); // '천'의 자리
            String subTemp2 = temp.substring(lengthString - 3, lengthString); // '백,십,일' 의 자리
            String concatTemp12 = subTemp1.concat(",").concat(subTemp2);
            Log.d(TAG, "convertMoneyBundle 메서드, 받은 int형 money : " + money
                    + " / money의 문자열 길이는 : " + lengthString + " / 천의 자리 수는 : " + subTemp1 + " / 백,십,일의 자리 수는 : " + subTemp2
                    + " / ,를 구분하면 : " + concatTemp12);
            finalMoney = concatTemp12;
        }

        return finalMoney;
    }




}
