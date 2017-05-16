package seungchan.com.tobaccoach_5_2.webService;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import seungchan.com.tobaccoach_5_2.model.Record;
import seungchan.com.tobaccoach_5_2.model.ResultObject;
import seungchan.com.tobaccoach_5_2.model.User;


//1. 사용할 명령들..  POST : create...  GET : select 명령 수행.
public interface NetworkService {

     @POST("api/users")
     Call<User> post_user(@Body User user);

     @POST("api/records")
     Call<Record> post_record(@Body Record record);

     @GET("api/users")
     Call<List<User>> getAllUser();

     @POST("android/login")
     Call<ResultObject> loginUser(@Body User user);
}