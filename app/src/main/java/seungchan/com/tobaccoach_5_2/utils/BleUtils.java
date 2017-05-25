package seungchan.com.tobaccoach_5_2.utils;

import android.content.IntentFilter;

import seungchan.com.tobaccoach_5_2.ble.BluetoothLeService;

/**
 * Created by USER on 2017-05-16.
 */

public class BleUtils {

    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
