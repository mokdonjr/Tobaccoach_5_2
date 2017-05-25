package seungchan.com.tobaccoach_5_2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import seungchan.com.tobaccoach_5_2.utils.AppSettingUtils;
import seungchan.com.tobaccoach_5_2.R;

/* SnapShot 단계 어플리케이션
 * HTTP 통신시 IP주소 작성 */
public class DevSettingActivity extends AppCompatActivity {
    private static String TAG = "DevSettingActivity";

    @BindView(R.id.edit_ip_address) EditText inputIpAddress;
    @BindView(R.id.edit_device_address) EditText inputDeviceAddress;
    @BindView(R.id.submit_btn) Button submitBtn;
    private String ipAddress;
    private String deviceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_dev_setting);
        ButterKnife.bind(this);
    }

    public Intent putIntentToNext(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra(AppSettingUtils.EXTRAS_SERVER_IP, ipAddress);
        intent.putExtra(AppSettingUtils.EXTRAS_DEVICE_ADDRESS, deviceAddress);
        return intent;
    }

    @OnClick(R.id.submit_btn) void onClickSubmitBtn(){
        ipAddress = inputIpAddress.getText().toString();
        deviceAddress = inputDeviceAddress.getText().toString();

        Intent intent = putIntentToNext();
        startActivity(intent);
    }


}
