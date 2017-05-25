package seungchan.com.tobaccoach_5_2.webService;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Locale;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017-05-10.
 */

//3. 컨트롤러 클래스 만들어두기.
public class ApplicationController extends Application {
    private static ApplicationController instance;
    public static ApplicationController getInstance() {return instance;}

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationController.instance = this; //앱이 처음 실행될 때 인스턴스 생성.
    }

    private NetworkService networkService;
    public NetworkService getNetworkService() {return networkService;}

    private String baseUrl;

    public void buildNetworkService(String url) {  //받아온 ip, 포트넘버로 네트워크 접근.
        synchronized (ApplicationController.class) {
            if(networkService == null) {
                baseUrl= url;
                Gson gson= new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();
                GsonConverterFactory factory= GsonConverterFactory.create(gson);
                //서버로부터 json 형식으로 데이터를 받아오고 이를 파싱해서 받아오기 위해 사용

                Retrofit retrofit= new Retrofit.Builder().baseUrl(baseUrl)
                        .addConverterFactory(factory).build();

                networkService= retrofit.create(NetworkService.class);
            }
        }
    }
}
