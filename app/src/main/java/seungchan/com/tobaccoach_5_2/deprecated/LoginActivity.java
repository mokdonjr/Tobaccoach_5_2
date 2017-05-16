//package seungchan.com.tobaccoach_5_2;
//
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//
//import com.google.gson.Gson;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import seungchan.com.tobaccoach_5_2.authenticate.TobaccoListActivity;
//
//public class LoginActivity extends AppCompatActivity {
//    private static String TAG = "LoginActivity";
//
//    public static final String EXTRAS_SERVER_IP = "SERVER_IP";
//    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
//    public static final String SERVER_CONTEXT_ROOT = "CapServer";
//
//    private String mIpAddress;
//    private String mDeviceAddress;
//
//    private String myId;
//    private String myPassword;
//
//    // ui
//    private EditText mInputId;
//    private EditText mInputPassword;
//    private Button mLoginButton;
//
//    private LoginTask mLoginTask;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//        getSupportActionBar().hide();
//
//        getIntentFromDevSettingActivity();
//
//        mInputId = (EditText)findViewById(R.id.input_id);
//        mInputPassword = (EditText)findViewById(R.id.input_password);
//        mLoginButton = (Button)findViewById(R.id.login_button);
//
//
//    }
//
//    private Button.OnClickListener mLoginButtonListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            myId = mInputId.getText().toString();
//            myPassword = mInputPassword.getText().toString();
//            mLoginTask = new LoginTask();
//            Map<String, String> params = new HashMap<String, String>();
//            params.put("id", myId);
//            params.put("password", myPassword);
//            mLoginTask.execute(params);
//        }
//    }
//
//    public String getWebApplicationServerUrl(){
//        return "http://" + mIpAddress + ":8080/" + SERVER_CONTEXT_ROOT + "/";
//    }
//
//    private void getIntentFromDevSettingActivity(){
//        Intent addressIntent = getIntent();
//        mIpAddress = addressIntent.getStringExtra(EXTRAS_SERVER_IP);
//        mDeviceAddress = addressIntent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
//    }
//
//    public class LoginTask extends AsyncTask<Map<String, String>, Integer, String> {
//        // 안드로이드 --> 웹  (전송하고 싶은 파라미터)
//        @Override
//        protected String doInBackground(Map<String, String>... maps) {
//
//            // Http 요청 준비 작업
//            HttpClient.Builder http = new HttpClient.Builder("POST", getWebApplicationServerUrl() + "/login");
//
//            // Parameter 를 전송한다.
//            http.addAllParameters(maps[0]);
//
//            //Http 요청 전송
//            HttpClient post = http.create();
//            post.request();
//
//            // 응답 상태코드 가져오기
//            int statusCode = post.getHttpStatusCode();
//            if(statusCode == 1){ // 로그인 성공시
//                // 로그인 성공시
//                Intent intent = new Intent(getApplicationContext(), TobaccoListActivity.class);
//                intent.putExtra(LoginActivity.EXTRAS_SERVER_IP, mIpAddress);
//                intent.putExtra(LoginActivity.EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
//                startActivity(intent);
//            }
//            else{
//
//            }
//
//            // 응답 본문 가져오기
//            String body = post.getBody();
//
//            return body;
//        }
//
//        // 웹 --> 안드로이드
//        @Override
//        protected void onPostExecute(String s) {
//            Log.d("JSON_RESULT", s);
//            Gson gson = new Gson();
//
//            Data data = gson.fromJson(s, Data.class);
//            Log.d("JSON_RESULT", data.getData1());
//            Log.d("JSON_RESULT", data.getData2());
//
//
////            tvTitle.setText(data.getData1());
////            tvMemo.setText(data.getData2());
//        }
//    }
//}
