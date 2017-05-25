package seungchan.com.tobaccoach_5_2.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import seungchan.com.tobaccoach_5_2.R;
import seungchan.com.tobaccoach_5_2.model.ResultObject;
import seungchan.com.tobaccoach_5_2.model.User;
import seungchan.com.tobaccoach_5_2.utils.AppSettingUtils;
import seungchan.com.tobaccoach_5_2.webService.ApplicationController;
import seungchan.com.tobaccoach_5_2.webService.NetworkService;

public class LoginActivity extends AppCompatActivity {
    private static String TAG = "LoginActivity";

    private AppSettingUtils mAppSettingUtils;
    private NetworkService networkService;

    // ui
    @BindView(R.id.tobacco_logo_login_activity) ImageView mLogoView;
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

        loadingViewer();
    }

    public void getIntentFromPrevious(){
        Intent addressIntent = getIntent();
        ipAddress = addressIntent.getStringExtra(AppSettingUtils.EXTRAS_SERVER_IP);
        deviceAddress = addressIntent.getStringExtra(AppSettingUtils.EXTRAS_DEVICE_ADDRESS);
    }

    public Intent putIntentToNext(){ // MyDeviceScanActivity
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

    public void loadingViewer(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                /*
                    애니메이션
                 */
                Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_slide_down);

                mLogoView.startAnimation(animSlide);

                mLogoView.setVisibility(View.VISIBLE);

            }
        }, 1);
    }

    @OnClick(R.id.login_button) void onClickLoginButton(){
        inputId = mInputId.getText().toString();
        inputPassword = mInputPassword.getText().toString();

        // 1. user 객체 (id와 password)
        User user = new User();
        user.setNick(inputId);
        user.setPassword(inputPassword);

        // 2. user객체와 함께 login 요청
        Call<ResultObject> thumbnailCall = networkService.loginUser(user);
        thumbnailCall.enqueue(new Callback<ResultObject>() {
            @Override
            public void onResponse(Call<ResultObject> call, Response<ResultObject> response) {
                if(response.isSuccessful()) {
                    //Food food_temp= response.body();
                    mInputId.setText("");
                    mInputPassword.setText("");
                    if(response.body().getResult().equals("success")){
                        Toast.makeText(getApplicationContext(), "Welcome " + inputId, Toast.LENGTH_SHORT).show();
                        Intent intent = putIntentToNext();
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "invalid Username and Password", Toast.LENGTH_SHORT).show();
                        mLoginResult.setText("Invalid Username and Password");
                    }

                } else {
                    int statusCode= response.code();
                    Toast.makeText(getApplicationContext(), String.valueOf(statusCode), Toast.LENGTH_LONG ).show();
                    Log.i(TAG, "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<ResultObject> call, Throwable t) {
                Log.i(TAG, "서버 onFailure 에러내용 : " + t.getMessage());
                Toast.makeText(getApplicationContext(), "onFailure메서드 : " + t.getMessage(), Toast.LENGTH_LONG ).show();
            }
        });
    }

    @OnClick(R.id.sign_up_btn) void onClickSignupButton(){
        Intent intent = putIntentToNext2();
        startActivity(intent); // startResultActivity로 바꿔야
    }
}
