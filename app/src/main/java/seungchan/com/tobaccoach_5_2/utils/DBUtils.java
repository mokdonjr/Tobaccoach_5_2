package seungchan.com.tobaccoach_5_2.utils;

import android.util.Log;

import java.util.HashMap;

import seungchan.com.tobaccoach_5_2.model.DeviceLog;
import seungchan.com.tobaccoach_5_2.model.Tobacco;

/**
 * Created by USER on 2017-05-08.
 */

public class DBUtils {
    private static String TAG = "DBUtils";

    // 데이터 베이스 네이밍
    public static final String DATABASE_NAME = "tobaccoach.db";
    public static final int DB_VERSION = 4;

    // 담배 종류 테이블 네이밍
    public static final String TOBACCO_TABLE_NAME = "tobacco";
    public static final String TOBACCO_IDX = "tobacco_id";
    public static final String TOBACCO_COL_BRAND = "tobacco_brand";
    public static final String TOBACCO_COL_NAME = "tobacco_name";
    public static final String TOBACCO_COL_TAR = "tar_per_one";
    public static final String TOBACCO_COL_NICOTINE = "nicotine_per_one";
    public static final String TOBACCO_COL_PRICE = "price_per_one";

    // 사용자 로그 테이블 네이밍
    public static final String LOG_TABLE_NAME = "smoked";
    public static final String LOG_IDX = "smoked_id";
    public static final String LOG_COL_DATETIME = "date_time"; // yyyy-MM-dd HH:mm:ss

    public static int ALL_TOBACCO_TABLE_ROW_COUNT_FROM_CSV; // csv -> hashmap 담배종류 총 개수
    public static int ALL_LOG_TABLE_ROW_COUNT_FROM_CSV;

    // version 1 최초 db oncreate시 수행
    public static HashMap getTobaccoMap(){
        Log.i(TAG, "getTobaccoMap 메서드 - from csv to memory");
        HashMap<Integer, Tobacco> tobaccoMap = new HashMap<Integer, Tobacco>();
        int tobacco_id = 0;
        // id, 브랜드, 이름, 타르, 니코틴, 가격
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"디스"	,	"오리진"	,	6.5	,	0.6	,	200	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"디스"	,	"플러스"	,	5.5	,	0.55	,	205	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"디스"	,	"아프리카몰라"	,	5	,	0.4	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"디스"	,	"아프리카룰라"	,	5	,	0.4	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"디스"	,	"아프리카마파초"	,	5	,	0.4	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"레종"	,	"블루"	,	3	,	0.3	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"레종"	,	"그린"	,	3	,	0.3	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"레종"	,	"블랙"	,	1	,	0.1	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"레종"	,	"프렌치블랙"	,	3	,	0.2	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"레종"	,	"프레쏘"	,	1	,	0.1	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"레종"	,	"에어로"	,	1	,	0.1	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"레종"	,	"아이스프레쏘"	,	6	,	0.5	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"레종"	,	"썬프레쏘"	,	6	,	0.5	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"레종"	,	"시즌"	,	2	,	0.2	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"레종"	,	"프렌치요고"	,	1	,	0.1	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"보헴시가"	,	"No.6"	,	6	,	0.5	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"보헴시가"	,	"No.3"	,	3	,	0.3	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"보헴시가"	,	"No.1"	,	1	,	0.1	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"보헴시가"	,	"마스터"	,	6	,	0.6	,	350	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"보헴시가"	,	"쿠바나더블6mg"	,	6	,	0.5	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"보헴시가"	,	"쿠바나6mg"	,	6	,	0.5	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"보헴시가"	,	"쿠바나1mg"	,	1	,	0.1	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"보헴시가"	,	"미니5mg"	,	5	,	0.5	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"보헴시가"	,	"미니1mg"	,	1	,	0.1	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"보헴시가"	,	"쉐이크6mg"	,	6	,	0.5	,	235	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"보헴시가"	,	"슬림핏브라운/화이트1mg"	,	1	,	0.1	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"보헴시가"	,	"매그넘5mg"	,	5	,	0.5	,	250	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"보헴시가"	,	"리브레5mg"	,	5	,	0.4	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"더원"	,	"블루"	,	1	,	0.1	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"더원"	,	"오렌지"	,	0.5	,	0.05	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"더원"	,	"화이트"	,	0.1	,	0.01	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"더원"	,	"그린"	,	1	,	0.1	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"더원"	,	"임팩트"	,	1	,	0.1	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"더원"	,	"에티팩"	,	1	,	0.1	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"더원"	,	"에티켓"	,	1	,	0.1	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"더원"	,	"체인지"	,	1	,	0.1	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"말보로"	,	"레드"	,	8	,	0.7	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"말보로"	,	"미디움"	,	6	,	0.5	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"말보로"	,	"골드6mg"	,	6	,	0.5	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"말보로"	,	"실버3mg"	,	3	,	0.3	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"말보로"	,	"터치"	,	5	,	0.4	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"말보로"	,	"블랙후레쉬"	,	6	,	0.5	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"말보로"	,	"화이트후레쉬"	,	6	,	0.5	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"말보로"	,	"하이브리드5mg"	,	5	,	0.4	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"말보로"	,	"하이브리드1mg"	,	1	,	0.1	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"말보로"	,	"아이스블래스트6mg"	,	6	,	0.5	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"말보로"	,	"아이스블래스트1mg"	,	1	,	0.1	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"말보로"	,	"더블버스트"	,	6	,	0.4	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"말보로"	,	"징퓨전"	,	6	,	0.5	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"말보로"	,	"빈티지"	,	6	,	0.6	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"말보로"	,	"제로애디티브"	,	1	,	0.1	,	215	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"팔리아멘트"	,	"아쿠아5mg"	,	5	,	0.4	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"팔리아멘트"	,	"아쿠아3mg"	,	3	,	0.3	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"팔리아멘트"	,	"원"	,	1	,	0.1	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"팔리아멘트"	,	"하이브리드5mg"	,	5	,	0.4	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"팔리아멘트"	,	"하이브리드1mg"	,	1	,	0.1	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"팔리아멘트"	,	"하이브리드Tropic5"	,	5	,	0.5	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"팔리아멘트"	,	"캐럿"	,	5	,	0.4	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"팔리아멘트"	,	"듀얼 센세이션1"	,	1	,	0.1	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"던힐"	,	"6mg"	,	6	,	0.6	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"던힐"	,	"3mg"	,	3	,	0.3	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"던힐"	,	"1mg"	,	1	,	0.1	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"던힐"	,	"프로스트"	,	1	,	0.1	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"던힐"	,	"스위치6mg"	,	6	,	0.5	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"던힐"	,	"스위치one"	,	1.5	,	1.15	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"던힐"	,	"6mg 14개비"	,	6	,	0.6	,	214	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"던힐"	,	"1mg 14개비"	,	1	,	0.1	,	214	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"메비우스"	,	"오리지날"	,	8	,	0.6	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"메비우스"	,	"스카이블루(구 마일드세븐 라이트)"	,	6	,	0.5	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"메비우스"	,	"윈드블루"	,	3	,	0.3	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"메비우스"	,	"LSS윈드블루"	,	3	,	0.3	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"메비우스"	,	"LSS원"	,	1	,	0.1	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"메비우스"	,	"옵션퍼플"	,	6	,	0.5	,	225	)	)	;
        tobaccoMap.put(tobacco_id++	,	new Tobacco	(	"카멜"	,	"필터스"	,	8	,	0.6	,	200	)	)	;

        ALL_TOBACCO_TABLE_ROW_COUNT_FROM_CSV = tobaccoMap.size(); // 총 개수 74개
        Log.d(TAG, "getTobaccoMap메서드에서 저장한 ALL_TOBACCO_TABLE_ROW_COUNT_FROM_CSV : " + ALL_TOBACCO_TABLE_ROW_COUNT_FROM_CSV);
        return tobaccoMap;
    }

    // MyDeviceController 의 DevSetting 프레임에서 RESET SAMPLE 버튼 클릭시 수행
    public static HashMap getSampleLogDataMap(){
        Log.i(TAG, "getSampleLogDataMap 메서드 - from csv to memory");
        HashMap<Integer, DeviceLog> sampleDataMap = new HashMap<Integer, DeviceLog>();

        int sample_data_id = 0;

        // id, date_time
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-03 17:55:00"));// yyyy-MM-dd HH:mm:ss 으로 저장
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-03 17:58:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-03 19:47:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-03 21:09:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-03 21:38:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-03 22:00:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-03 22:28:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-03 23:17:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-04 01:30:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-04 02:41:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-04 07:29:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-04 11:08:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-04 12:00:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-04 13:58:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-04 14:50:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-04 15:55:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-04 17:21:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-04 18:13:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-04 20:15:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-04 21:23:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-04 22:41:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-04 22:55:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-04 23:45:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-05 00:10:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-05 02:22:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-05 07:50:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-05 08:05:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-06 14:43:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-06 14:45:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-06 15:27:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-06 16:33:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-06 17:09:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-06 17:59:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-06 19:21:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-06 20:58:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-06 22:30:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-06 23:10:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-07 00:40:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-07 02:53:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-07 03:51:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-07 10:43:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-07 11:20:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-07 12:08:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-07 12:30:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-07 13:02:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-07 14:13:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-07 15:03:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-07 16:00:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-07 17:00:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-07 17:56:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-08 17:05:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-08 19:45:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-08 21:05:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-08 23:54:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-09 00:45:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-09 02:01:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-09 02:56:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-09 04:11:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-09 09:54:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-09 11:36:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-09 12:29:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-09 13:45:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-09 16:57:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-09 17:15:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-09 17:16:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-09 19:00:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-09 20:10:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-09 22:00:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-09 22:15:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-09 23:09:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-10 00:25:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-10 02:26:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-10 14:49:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-10 15:15:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-10 16:30:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-10 17:54:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-10 19:30:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-10 21:37:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-10 23:15:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-10 23:46:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-11 00:28:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-11 01:14:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-11 05:19:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-11 11:15:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-11 13:20:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-11 13:21:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-11 14:25:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-11 15:15:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-11 17:10:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-11 17:58:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-11 19:23:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-11 20:33:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-11 21:30:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-11 22:40:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-11 23:57:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-12 01:00:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-12 02:30:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-12 03:32:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-12 08:20:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-12 09:59:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-12 12:01:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-12 13:15:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-12 15:30:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-12 16:30:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-12 16:35:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-12 19:10:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-12 19:12:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-12 19:16:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-12 21:01:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-12 21:46:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-12 22:09:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-12 23:25:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-13 17:10:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-13 17:50:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-13 18:38:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-13 20:21:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-13 21:30:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-13 22:00:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-13 23:10:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-14 00:41:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-14 02:21:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-14 02:55:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-14 04:35:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-14 05:35:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-14 10:23:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-14 11:30:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-14 11:35:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-14 11:36:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-14 12:30:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-14 15:44:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-14 17:10:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-14 17:47:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-14 18:00:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-14 18:05:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-14 20:42:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-14 22:01:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-14 22:59:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-15 13:23:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-15 13:25:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-15 14:50:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-15 17:31:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-15 18:40:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-15 19:30:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-15 21:50:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-16 00:21:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-16 08:10:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-16 08:40:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-16 11:10:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-16 11:15:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-16 11:25:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-16 12:09:00"));
        sampleDataMap.put(sample_data_id++, new DeviceLog("2017-04-16 15:05:00"));


        ALL_LOG_TABLE_ROW_COUNT_FROM_CSV = sampleDataMap.size(); // 총 개수 152개
        Log.d(TAG, "getSampleLogDataMap메서드에서 저장한 ALL_LOG_TABLE_ROW_COUNT_FROM_CSV : " + ALL_LOG_TABLE_ROW_COUNT_FROM_CSV);
        return sampleDataMap;
    }
}
