//package seungchan.com.tobaccoach_5_2.webViewService;
//
//import java.net.CookieStore;
//import java.util.List;
//
//import org.apache.http.client.HttpClient;
//import org.apache.http.cookie.Cookie;
//import org.apache.http.impl.client.BasicCookieStore;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.protocol.BasicHttpContext;
//import org.apache.http.protocol.HttpContext;
//
//public class SessionControl
//{
//    static public DefaultHttpClient httpclient = null;
//    static public List<Cookie> cookies;
//
//    public static HttpClient getHttpclient()
//    {
//        if( httpclient == null){
//            SessionControl.setHttpclient(new DefaultHttpClient());
//        }
//        return httpclient;
//    }
//
//    public static void setHttpclient(DefaultHttpClient httpclient) {
//        SessionControl.httpclient = httpclient;
//    }
//}