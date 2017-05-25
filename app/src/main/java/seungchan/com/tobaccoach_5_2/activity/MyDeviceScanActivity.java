package seungchan.com.tobaccoach_5_2.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import seungchan.com.tobaccoach_5_2.model.Tobacco;
import seungchan.com.tobaccoach_5_2.utils.AppSettingUtils;
import seungchan.com.tobaccoach_5_2.R;

public class MyDeviceScanActivity extends AppCompatActivity {
    private static String TAG = "MyDeviceScanActivity";

    private String ipAddress;
    private String deviceAddress; // 고객의 스마트 담배케이스 Mac 주소
//    private Tobacco mTobacco; // 고객의 담배 종류
    private String userId;

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private BluetoothDevice scannedDevice;

    @BindView(R.id.connect_btn) Button connectBtn;
    @BindView(R.id.my_device_name) TextView myDeviceName;
    @BindView(R.id.my_device_address) TextView myDeviceAddress;

    private static final long SCAN_PERIOD = 10000; // 10초

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_my_device_scan);
        ButterKnife.bind(this);

        getIntentFromPrevious();

        mHandler = new Handler();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    public void getIntentFromPrevious(){
        /* DevSettingActivty로 부터 IP 주소 얻기 */
        Intent inputAddressIntent = getIntent();
        ipAddress = inputAddressIntent.getStringExtra(AppSettingUtils.EXTRAS_SERVER_IP);
        deviceAddress = inputAddressIntent.getStringExtra(AppSettingUtils.EXTRAS_DEVICE_ADDRESS);
        userId = inputAddressIntent.getStringExtra(AppSettingUtils.EXTRAS_MY_ID);
//        mTobacco = inputAddressIntent.getParcelableExtra(AppSettingUtils.EXTRAS_MY_TOBACCO);
    }

    public Intent putIntentToNext(){
        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(AppSettingUtils.EXTRAS_DEVICE_NAME, scannedDevice.getName());
        intent.putExtra(AppSettingUtils.EXTRAS_DEVICE_ADDRESS, scannedDevice.getAddress());
        intent.putExtra(AppSettingUtils.EXTRAS_SERVER_IP, ipAddress);
        intent.putExtra(AppSettingUtils.EXTRAS_MY_ID, userId);
//        intent.putExtra(AppSettingUtils.EXTRAS_MY_TOBACCO, mTobacco);
        return intent;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
        }
        scannedDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress);
        if(scannedDevice == null){
            myDeviceName.setText("Failed to scan your Tobaccoach");
            connectBtn.setEnabled(false);
            // 가까이 간 후에 다시 시도할 수 있도록 코드 추가

        }
        else{
            Log.d(TAG, "onResume() 메서드, getRemoteDevice 메서드 이용해 " + deviceAddress + "에 대해 scan 완료");
            myDeviceName.setText(scannedDevice.getName());
            myDeviceAddress.setText(scannedDevice.getAddress());
            connectBtn.setEnabled(true); // 버튼 활성화
        }

//        Log.d("MyDeviceScanActivity", myDevice.getAddress() + "랑 " + deviceAddress + " 같니?"); // 응 같아
        scanMyLeDevice(true); // 10초 후 stopLeScan() 호출
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter.isEnabled()) {
            if (mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable();
            }
        }
        scanMyLeDevice(true);
    }
    @Override
    protected void onPause() {
        super.onPause();
        scanMyLeDevice(false);
        //mLeDeviceListAdapter.clear();
        connectBtn.setEnabled(false);
    }

    private void scanMyLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    //invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //mLeDeviceListAdapter.addDevice(device);
                    //mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    @OnClick(R.id.connect_btn) void onClickConnectButton(){
        if(scannedDevice == null)
            return ;

        if(mScanning){
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
        Intent intent = putIntentToNext();
        startActivity(intent);
    }
}
