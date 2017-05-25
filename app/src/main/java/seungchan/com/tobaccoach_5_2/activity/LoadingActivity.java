package seungchan.com.tobaccoach_5_2.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import seungchan.com.tobaccoach_5_2.R;
import seungchan.com.tobaccoach_5_2.dao.TobaccoDBHelper;
import seungchan.com.tobaccoach_5_2.dao.TobaccoDaoService;
import seungchan.com.tobaccoach_5_2.utils.DBUtils;

public class LoadingActivity extends AppCompatActivity {
    private static String TAG = "LoadingActivity";

    private final int milli1 = 1000; // 1초
    private final int milli2 = 400; // 0.4초
    private final int milli3 = 1000; // 1초

    private TobaccoDaoService mTobaccoDaoService;

    private Handler mhandler;
    @BindView(R.id.tobaccoach_logo_level_1) ImageView mLogoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_loading);
        ButterKnife.bind(this);

        mTobaccoDaoService = TobaccoDaoService.getInstance(getApplicationContext());

        sqliteAccessForTobaccoData(getApplicationContext());

        mhandler = new Handler();

        loadingViewer2();
    }

    public void loadingViewer2(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "tobaccoach_logo_level_2");

//                mLogoView.setBackgroundResource(R.drawable.tobaccoach_logo_level_2);
                mLogoView.setImageDrawable(getResources().getDrawable(R.drawable.tobaccoach_logo_level_2));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "tobaccoach_logo_level_2");
//                        mLogoView.setBackgroundResource(R.drawable.tobaccoach_logo_level_3);
                        mLogoView.setImageDrawable(getResources().getDrawable(R.drawable.tobaccoach_logo_level_3));
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "tobaccoach_logo_level_2");
//                                mLogoView.setBackgroundResource(R.drawable.tobaccoach_logo_level_4);
                                mLogoView.setImageDrawable(getResources().getDrawable(R.drawable.tobaccoach_logo_level_4));
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d(TAG, "tobaccoach_logo_level_2");
//                                        mLogoView.setBackgroundResource(R.drawable.tobaccoach_logo);
                                        mLogoView.setImageDrawable(getResources().getDrawable(R.drawable.tobaccoach_logo));
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                /*
                                                    애니메이션
                                                 */
                                                Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_slide_up);
                                                mLogoView.startAnimation(animSlide);

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        /*
                                                            로그인 액티비티
                                                         */
                                                        Intent intent = new Intent(getApplicationContext(), DevSettingActivity.class);
                                                        startActivity(intent);
                                                        finish();

                                                    }
                                                }, milli3);
                                            }
                                        }, 800);
                                    }
                                }, milli2);
                            }
                        }, milli2);
                    }
                }, milli2);
            }
        }, milli1);
    }

    public void sqliteAccessForTobaccoData(Context context){
        Toast.makeText(this, "데이터 로딩중 ... ", Toast.LENGTH_SHORT).show();

        HashMap tobaccoMap = DBUtils.getTobaccoMap(); // 메모리에 생성
        mTobaccoDaoService.insertAllTobaccoData(tobaccoMap); // (레코드가 하나라도 없으면) 삽입
        Toast.makeText(this, mTobaccoDaoService.getAllTobaccoTableRowNum() + "개 데이터가 존재", Toast.LENGTH_SHORT).show();

        Toast.makeText(this, "데이터 로딩 완료", Toast.LENGTH_SHORT).show();
    }

}
