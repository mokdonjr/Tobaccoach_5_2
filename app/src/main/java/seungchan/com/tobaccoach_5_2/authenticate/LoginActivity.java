package seungchan.com.tobaccoach_5_2.authenticate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import seungchan.com.tobaccoach_5_2.R;
import seungchan.com.tobaccoach_5_2.deviceServiceController.MyDeviceScanActivity;
import seungchan.com.tobaccoach_5_2.model.ResultObject;
import seungchan.com.tobaccoach_5_2.model.User;
import seungchan.com.tobaccoach_5_2.utils.AppSettingUtils;
import seungchan.com.tobaccoach_5_2.webService.ApplicationController;
import seungchan.com.tobaccoach_5_2.webService.NetworkService;

public class LoginActivity extends AppCompatActivity {
    private static String TAG = "LoginActivity";

    private AppSettingUtils mAppSettingUtils;
    private boolean result_flag = false;
    private NetworkService networkService;

    // ui
    @BindView(R.id.login_result) TextView mLoginResult;
    @BindView(R.id.login_button) Button mLoginButton;
    @BindView(R.id.sign_up_btn) Button mSignupButton;
    @BindView(R.id.input_id) EditText mInputId;
    @BindView(R.id.input_password) EditText mInputPassword;
    @BindView(R.id.keep_login_check) CheckBox mKeepLoginCheck;
    private String inputId;
    private String inputPassword;
    private String ipAddress;
    private String deviceAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        ButterKnife.bind(this);

        getIntentFromPrevious(); // ipAddress 받음

        mAppSettingUtils = AppSettingUtils.getInstance();
        if(mAppSettingUtils != null){
            //ip , port 연결
            ApplicationController application= ApplicationController.getInstance();  //앱이 처음 실행될 때 인스턴스 생성.
            application.buildNetworkService(mAppSettingUtils.getWebApplicationServerUrl(ipAddress));  //다음의 url로 네트워크서비스 준비.
            networkService= ApplicationController.getInstance().getNetworkService();
        }
    }

    public void getIntentFromPrevious(){
        Intent addressIntent = getIntent();
        ipAddress = addressIntent.getStringExtra(AppSettingUtils.EXTRAS_SERVER_IP);
        deviceAddress = addressIntent.getStringExtra(AppSettingUtils.EXTRAS_DEVICE_ADDRESS);
    }

    public Intent putIntentToNext(){ // MyDeviceScanActivity
        /* MyDeviceScanActivity */
//        Intent intent = new Intent(getApplicationContext(), TobaccoListActivity.class);
        Intent intent = new Intent(getApplicationContext(), MyDeviceScanActivity.class);
        intent.putExtra(AppSettingUtils.EXTRAS_DEVICE_ADDRESS, deviceAddress);
        intent.putExtra(AppSettingUtils.EXTRAS_SERVER_IP, ipAddress);
        intent.putExtra(AppSettingUtils.EXTRAS_MY_ID, inputId);
        return intent;
    }

    public Intent putIntentToNext2(){ // SignupActivity
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        intent.putExtra(AppSettingUtils.EXTRAS_DEVICE_ADDRESS, deviceAddress);
        intent.putExtra(AppSettingUtils.EXTRAS_SERVER_IP, ipAddress);
        return intent;
    }

    @OnClick(R.id.login_button) void onClickLoginButton(){
        inputId = mInputId.getText().toString();
        inputPassword = mInputPassword.getText().toString();

        User user = new User();
        user.setNick(inputId);
        user.setPassword(inputPassword);

        Call<ResultObject> thumbnailCall = networkService.loginUser(user);
        thumbnailCall.enqueue(new Callback<ResultObject>() {
            @Override
            public void onResponse(Call<ResultObject> call, Response<ResultObject> response) {
                if(response.isSuccessful()) {
                    //Food food_temp= response.body();
                    mInputId.setText("");
                    mInputPassword.setText("");
                    if(response.body().getResult().equals("success")){
                        result_flag = true;

                        /*
                            여기에 Intent intent = putIntentToNext(); startActivity(intent);
                            해야 한번에
                         */
                        Intent intent = putIntentToNext();
                        startActivity(intent);
                    } else {
                        //Toast.makeText(this, "invalid Username and Password", Toast.LENGTH_SHORT).show();
                        mLoginResult.setText("Invalid Username and Password");
                    }

                } else {
                    int statusCode= response.code();
                    Log.i(TAG, "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<ResultObject> call, Throwable t) {
                Log.i(TAG, "서버 onFailure 에러내용 : " + t.getMessage());
            }
        });
        if(result_flag == true){
//            Intent intent = new Intent(this, RecordInsertActivity.class);
//            intent.putExtra("username",inputId);
//            startActivity(intent);

        }
    }

    @OnClick(R.id.sign_up_btn) void onClickSignupButton(){
        Intent intent = putIntentToNext2();
        startActivity(intent); // startResultActivity로 바꿔야
    }
}
