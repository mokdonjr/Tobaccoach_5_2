package seungchan.com.tobaccoach_5_2.authenticate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import seungchan.com.tobaccoach_5_2.R;
import seungchan.com.tobaccoach_5_2.model.User;
import seungchan.com.tobaccoach_5_2.utils.AppSettingUtils;
import seungchan.com.tobaccoach_5_2.webService.ApplicationController;
import seungchan.com.tobaccoach_5_2.webService.NetworkService;

public class SignupActivity extends AppCompatActivity {
    private static String TAG = "SignupActivity";

    private String ipAddress;
    private String deviceAddress;

    @BindView(R.id.input_sign_up_id) TextView mInputSignupId;
    @BindView(R.id.input_sign_up_password) TextView mInputSignupPassword;
    @BindView(R.id.select_region_spinner) Spinner mSelectRegionSpinner;
    @BindView(R.id.sign_up_btn) Button mSignupButton;
    @BindView(R.id.sign_up_cancel_btn) Button mCancelSignupButton;
    private NetworkService networkService;
    private AppSettingUtils mAppSettingUtils;

    private String inputSignupId;
    private String inputSignupPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();
        ButterKnife.bind(this);

        getIntentFromPrevious();

        String[] str = getResources().getStringArray(R.array.spinnercity); // 배열을 목록에 담는다
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, str); //ArrayAdapter객체를 생성해서 레이아웃과 데이터 매핑
        Spinner s = (Spinner) findViewById(R.id.select_region_spinner);    //
        s.setAdapter(adapter);

        mAppSettingUtils = AppSettingUtils.getInstance();
        if(mAppSettingUtils != null){
            //ip , port 연결
            ApplicationController application= ApplicationController.getInstance();  //앱이 처음 실행될 때 인스턴스 생성.
            application.buildNetworkService(mAppSettingUtils.getWebApplicationServerUrl(ipAddress));  //다음의 url로 네트워크서비스 준비.
            networkService= ApplicationController.getInstance().getNetworkService();
        }
    }

    public void getIntentFromPrevious(){
        Intent intent = getIntent();
        ipAddress = intent.getStringExtra(AppSettingUtils.EXTRAS_SERVER_IP);
        deviceAddress = intent.getStringExtra(AppSettingUtils.EXTRAS_DEVICE_ADDRESS);
    }

    public Intent putIntentToNext(){
        /* MyDeviceScanActivity */
        Intent intent = new Intent(getApplicationContext(), TobaccoListActivity.class);
        intent.putExtra(AppSettingUtils.EXTRAS_DEVICE_ADDRESS, deviceAddress);
        intent.putExtra(AppSettingUtils.EXTRAS_SERVER_IP, ipAddress);
        intent.putExtra(AppSettingUtils.EXTRAS_MY_ID, inputSignupId);
        intent.putExtra(AppSettingUtils.EXTRAS_MY_PASSWORD, inputSignupPassword);
        return intent;
    }

    @OnClick(R.id.sign_up_btn) void onClickSignupButton(){
        inputSignupId = mInputSignupId.getText().toString();
        inputSignupPassword = mInputSignupPassword.getText().toString();
        Intent intent = putIntentToNext();
        startActivity(intent);

    }

    @OnClick(R.id.sign_up_cancel_btn) void onClickCancelSignupButton(){
        // 취소 버튼 LoginActivity로 복귀
        finish();
    }
}