package seungchan.com.tobaccoach_5_2.authenticate;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.HashMap;

import seungchan.com.tobaccoach_5_2.R;
import seungchan.com.tobaccoach_5_2.authenticate.DevSettingActivity;
import seungchan.com.tobaccoach_5_2.dao.TobaccoDBHelper;
import seungchan.com.tobaccoach_5_2.utils.DBUtils;

public class LoadingActivity extends AppCompatActivity {
    private static String TAG = "LoadingActivity";

    private final int milli = 400; // 0.4초
    private final int milli2 = 1000; // 1초

    protected TobaccoDBHelper mTobaccoDBHelper;

    private Handler mhandler;
    private ImageView mLogoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_loading);

        sqliteAccessForTobaccoData(getApplicationContext());

        mLogoView = (ImageView) findViewById(R.id.imageView_logo);

        mhandler = new Handler();

        loadingViewer(milli);
    }

    public void loadingViewer(final int milli){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(milli);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mhandler.post(new Runnable() {
                    public void run() {
                        Log.d(TAG, "tobaccoach_logo_level_2");
                        mLogoView.setBackgroundResource(R.drawable.tobaccoach_logo_level_2);
                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(milli);
                        } catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        mhandler.post(new Runnable() {
                            public void run() {
                                Log.d(TAG, "tobaccoach_logo_level_3");
                                mLogoView.setBackgroundResource(R.drawable.tobaccoach_logo_level_3);
                            }
                        });
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Thread.sleep(milli);
                                } catch (InterruptedException e){
                                    e.printStackTrace();
                                }

                                mhandler.post(new Runnable() {
                                    public void run() {
                                        Log.d(TAG, "tobaccoach_logo_level_4");
                                        mLogoView.setBackgroundResource(R.drawable.tobaccoach_logo_level_4);
                                    }
                                });
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
                                            Thread.sleep(milli);
                                        } catch (InterruptedException e){
                                            e.printStackTrace();
                                        }
                                        mhandler.post(new Runnable() {
                                            public void run() {
                                                Log.d(TAG, "tobaccoach_logo");
                                                mLogoView.setBackgroundResource(R.drawable.tobaccoach_logo);

                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            Thread.sleep(milli2);
                                                        } catch (InterruptedException e){
                                                            e.printStackTrace();
                                                        }
                                                        Intent intent = new Intent(getApplicationContext(), DevSettingActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }).start();
                                            }
                                        });
                                    }
                                }).start();
                            }
                        }).start();
                    }
                }).start();
            }
        }).start();
    }

    public void sqliteAccessForTobaccoData(Context context){
        Toast.makeText(this, "데이터 로딩중 ... ", Toast.LENGTH_SHORT).show();

        mTobaccoDBHelper = TobaccoDBHelper.getInstance(context); // helper의 onCreate메서드 수행 - Tobacco테이블 (없으면)생성
        HashMap tobaccoMap = DBUtils.getTobaccoMap(); // 메모리에 생성
        mTobaccoDBHelper.insertAllTobaccoData(tobaccoMap); // (레코드가 하나라도 없으면) 삽입
        Toast.makeText(this, mTobaccoDBHelper.getAllTobaccoTableRowNum() + "개 데이터가 존재", Toast.LENGTH_SHORT).show();

        Toast.makeText(this, "데이터 로딩 완료", Toast.LENGTH_SHORT).show();
    }

//    public void doLogoAnimation(){
//        Animation anim = AnimationUtils.loadAnimation
//                (getApplicationContext(), // 현재화면의 제어권자
//                        R.anim.translate_anim);   // 에니메이션 설정 파일
//        mLogoImage.startAnimation(anim);
//    }

}
