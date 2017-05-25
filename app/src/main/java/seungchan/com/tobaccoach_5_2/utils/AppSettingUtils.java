package seungchan.com.tobaccoach_5_2.utils;

import android.content.Context;

/**
 * Created by USER on 2017-05-11.
 */

public class AppSettingUtils {
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String EXTRAS_SERVER_IP = "SERVER_IP";
    public static final String EXTRAS_MY_TOBACCO = "MY_TOBACCO";
    public static final String SERVER_CONTEXT_ROOT = "CapServer_boot_template"; // CapServer_boot_template // CapServer
    public static final String EXTRAS_MY_ID = "MY_ID";
    public static final String EXTRAS_MY_PASSWORD = "MY_PASSWORD";
    public static final String SERVER_RANKING_PAGE = "userpage/ranking";

    private String mIpAddress;
    private String mDeviceAddress;
    private String mBrandName;
    private String mTobaccoName;
    private String mMyId;

    public static AppSettingUtils mAppSettingUtils = null; // 싱글턴

    public static AppSettingUtils getInstance(){
        if(mAppSettingUtils == null){
            mAppSettingUtils = new AppSettingUtils();
        }
        return mAppSettingUtils;
    }

    private AppSettingUtils(){

    }

    public String getWebApplicationServerUrl(String ipAddress){
        return "http://" + ipAddress + ":8080/" + AppSettingUtils.SERVER_CONTEXT_ROOT + "/";
    }

    public String getWebApplicationServerRankingPageUrl(String mIpAddress){
        return getWebApplicationServerUrl(mIpAddress) + SERVER_RANKING_PAGE;
    }
}
