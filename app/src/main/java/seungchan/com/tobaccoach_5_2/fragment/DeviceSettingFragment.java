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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import seungchan.com.tobaccoach_5_2.R;
import seungchan.com.tobaccoach_5_2.dao.TobaccoDBHelper;
import seungchan.com.tobaccoach_5_2.ble.BluetoothLeService;
import seungchan.com.tobaccoach_5_2.dao.LogDataAdapter;
import seungchan.com.tobaccoach_5_2.dao.TobaccoDaoService;
import seungchan.com.tobaccoach_5_2.utils.DBUtils;
import seungchan.com.tobaccoach_5_2.utils.TimezoneUtils;


public class DeviceSettingFragment extends Fragment {
    private static String TAG = "DeviceSettingFragment";

    private OnFragmentInteractionListener mListener;

    private TobaccoDaoService mTobaccoDaoService;
    private boolean mConnected = false;


    @BindView(R.id.message_from_hw) TextView mDataField;
//    TextView mConnectionState;

    @BindView(R.id.time_sync_state) TextView mTimeSyncState; // time sync
//    Button mTimeSyncButton;

    @BindView(R.id.listview_db_log) ListView mLogDataListView; // db log
    @BindView(R.id.reset_sample_log_data_btn) Button mResetSampleButton;
    @BindView(R.id.all_log_data_delete_btn) Button mDeleteAllLogButton;
    private List<String> mLogData;
    private LogDataAdapter mLogDataAdapter;


    public DeviceSettingFragment() { }

    public static DeviceSettingFragment newInstance() {
        DeviceSettingFragment fragment = new DeviceSettingFragment();
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
        mTobaccoDaoService = TobaccoDaoService.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View deviceSettingFragmentView = inflater.inflate(R.layout.fragment_device_setting, container, false);
        ButterKnife.bind(this, deviceSettingFragmentView);
        // db log
        mLogData = new ArrayList<String>();
        mLogData = mTobaccoDaoService.selectAllDateTimeLogData(); // 1. 로그 데이터 설정
        mLogDataAdapter = new LogDataAdapter(getActivity(), mLogData); // 2. 어댑터에 로그 데이터 설정
        mLogDataListView.setAdapter(mLogDataAdapter); // 3. 리스트뷰에 어댑터 설정

        updateLogDataList(mTobaccoDaoService.selectAllDateTimeLogData());

        return deviceSettingFragmentView;
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
//            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) { // ACTION_GATT_CONNECTED: connected to a GATT server.
//                mConnected = true;
//                updateConnectionState(R.string.connected);
//                //invalidateOptionsMenu();
//            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) { // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
//                mConnected = false;
//                updateConnectionState(R.string.disconnected);
//                //invalidateOptionsMenu();
//                clearUI();
//            } else
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) { // ACTION_DATA_AVAILABLE: received data from the device.
                Log.d(TAG, "ACTION_DATA_AVAILABLE 리시버 동작");

                String message = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);

                // 1. 블루투스 데이터 업데이트
                displayData(message);

                // 2. 시간동기화 상태 업데이트
                displayTimeSync(message);

                // 6. LogData ListView 업데이트
                updateLogDataList(mTobaccoDaoService.selectAllDateTimeLogData());

            }
        }
    };

//    private void updateConnectionState(final int resourceId) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mConnectionState.setText(resourceId);
//                Log.d(TAG, "updateConnectionState메서드 스레드 동작중");
//            }
//        });
//    }

    private void clearUI() {
        mDataField.setText(R.string.no_data); // mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
    }

    private void displayData(String message) {
        mDataField.setText(message); // mConnectionState.setText(state_data);
    }

    private void updateLogDataList(List<String> newData){
        mLogDataAdapter.updateLogData(newData);
    }


    private void displayTimeSync(String message){

        if(TimezoneUtils.checkTimeSyncState(message)){
            Log.d(TAG, "displayTimeSync메서드 hwDateTime과 nowDateTime이 일치 (동기화 된 상태)");
            mTimeSyncState.setText(R.string.time_sync_complete);
//            mTimeSyncButton.setEnabled(false);
        }
        else{
            Log.d(TAG, "displayTimeSync메서드 hwDateTime과 nowDateTime이 다름 (동기화 필요!)");
            mTimeSyncState.setText(R.string.time_sync_required);
//            mTimeSyncButton.setEnabled(true);
        }
    }

    public void sqliteAccessForResetLogData(Context context){
        Toast.makeText(getActivity(), "데이터 로딩중 ... ", Toast.LENGTH_SHORT).show();

        HashMap sampleLogDataMap = DBUtils.getSampleLogDataMap(); // 메모리에 생성
        mTobaccoDaoService.insertForResetAllLogData(sampleLogDataMap); // 다지우고 새로 생성
        Toast.makeText(getActivity(), DBUtils.LOG_TABLE_NAME + " 테이블 내 " + mTobaccoDaoService.getAllLogTableRowNum() + "개 데이터가 존재", Toast.LENGTH_SHORT).show();

        Toast.makeText(getActivity(), "데이터 로딩 완료", Toast.LENGTH_SHORT).show();
    }

    public void sqliteAccessForDeleteAllLogData(Context context){
        Toast.makeText(getActivity(), DBUtils.LOG_TABLE_NAME + "테이블 모두 삭제", Toast.LENGTH_SHORT).show();

        mTobaccoDaoService.deleteAllLogData();

        Toast.makeText(getActivity(), DBUtils.LOG_TABLE_NAME + " 테이블 내 " + mTobaccoDaoService.getAllLogTableRowNum() + "개 데이터가 존재", Toast.LENGTH_SHORT).show();
    }


    @OnClick(R.id.reset_sample_log_data_btn) void onClickResetSampleButton(){
        sqliteAccessForResetLogData(getActivity());
        updateLogDataList(mTobaccoDaoService.selectAllDateTimeLogData());
        /*
            TodaySmoked도 최신화 되어야
         */
    }

    @OnClick(R.id.all_log_data_delete_btn) void onClickDeleteAllLogButton(){
        sqliteAccessForDeleteAllLogData(getActivity());
        updateLogDataList(mTobaccoDaoService.selectAllDateTimeLogData());
        /*
            TodaySmoked도 최신화 되어야
         */
    }
}
