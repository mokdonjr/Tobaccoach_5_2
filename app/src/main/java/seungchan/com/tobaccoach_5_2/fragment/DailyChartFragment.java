package seungchan.com.tobaccoach_5_2.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import seungchan.com.tobaccoach_5_2.R;
import seungchan.com.tobaccoach_5_2.dao.TobaccoDBHelper;
import seungchan.com.tobaccoach_5_2.ble.BluetoothLeService;
import seungchan.com.tobaccoach_5_2.dao.TobaccoDaoService;
import seungchan.com.tobaccoach_5_2.graph.MyValueFormatter;
import seungchan.com.tobaccoach_5_2.utils.AppSettingUtils;
import seungchan.com.tobaccoach_5_2.utils.ServerMock;
import seungchan.com.tobaccoach_5_2.webService.ApplicationController;
import seungchan.com.tobaccoach_5_2.webService.NetworkService;


/* 정욱이 그래프 */
public class DailyChartFragment extends Fragment {
    private static String TAG = "DailyChartFragment";

    // 네트워크 연결 위해 MainActivity에서 get Bundle
    private static final String ARG_PARAM1 = "ipAddress";
    private String ipAddress;

    // Daily Chart
    private TobaccoDaoService mTobaccoDaoService;
    @BindView(R.id.daily_chart) CombinedChart mCombinedChart;
    private CombinedData mData;

    private List<String> mServicedDateFromMyDB;

    @BindView(R.id.text_you_can_see_timely_chart) TextView mTextYouCanSeeTimelyChart; // text_you_can_see_timely_chart

    // 서버 Mock --> 추후 바꾸기 네트워크로
    private List<Integer> mServerMockAllUserAverageByDate;
    private ServerMock mServerMock;
    // 네트워크로
    private AppSettingUtils mAppSettingUtils;
    private NetworkService mNetworkService;
    int finalAllUserAverageAmountByDateFromWeb;


    private OnFragmentInteractionListener mListener;

    public DailyChartFragment() { }

    public static DailyChartFragment newInstance(String param1) {
        DailyChartFragment fragment = new DailyChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    public void onPause(){
        super.onPause();
        getActivity().unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ipAddress = getArguments().getString(ARG_PARAM1);
        }

        mTobaccoDaoService = TobaccoDaoService.getInstance(getActivity());
        mServicedDateFromMyDB = getXAxisValues();

        // 서버로부터 전체 사용자들의 평균 랜덤 ---> 추후에 네트워크로 바꾸기
//        mServerMock = ServerMock.getInstance(mServicedDateFromMyDB);
//        mServerMockAllUserAverageByDate = mServerMock.getServerMockAllUserAverageByDate(); // lineData함수 내에서 mServerMockAllUserAverageByDate 이 객체 이용

        mAppSettingUtils = AppSettingUtils.getInstance();
        if(mAppSettingUtils != null){
            //ip , port 연결
            ApplicationController application= ApplicationController.getInstance();  //앱이 처음 실행될 때 인스턴스 생성.
            application.buildNetworkService(mAppSettingUtils.getWebApplicationServerUrl(ipAddress));  //다음의 url로 네트워크서비스 준비.
            mNetworkService= ApplicationController.getInstance().getNetworkService();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dailyFragmentView = inflater.inflate(R.layout.fragment_daily_chart, container, false);
        ButterKnife.bind(this, dailyFragmentView);

        // 1. set date in graph
        mData = new CombinedData(getXAxisValues());

        // 2. remove grid line
        mCombinedChart.getAxisLeft().setDrawGridLines(false);
        mCombinedChart.getXAxis().setDrawGridLines(false);

        // 3. right Y axis labels to be setEnable
        mCombinedChart.getAxisRight().setEnabled(false);


        // 4. set bar data and line data in combined chart
        mData.setData(barData(mServicedDateFromMyDB));
        mData.setData(lineData(mServicedDateFromMyDB));

        //erase description
        mCombinedChart.setDescription("");

        //set data
        mCombinedChart.setData(mData);

        //initial animation
        mCombinedChart.animateY(3000);

        //prevent zoom
        mCombinedChart.setScaleEnabled(true);

        //click event
        mCombinedChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

                //if clicked graph, changed graph per time
                Toast.makeText(getActivity(), getXAxisValues().get(e.getXIndex()),Toast.LENGTH_LONG).show();

                // 1.
                mTextYouCanSeeTimelyChart.setVisibility(View.GONE);
                // 2.
                Fragment selectedFragment = null;
                FragmentTransaction timelyChartFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                selectedFragment = TimelyChartFragment.newInstance(getXAxisValues().get(e.getXIndex()));
                timelyChartFragmentTransaction.replace(R.id.content_daily_add_timely, selectedFragment);// .replace(R.id.content_daily_add_timely, selectedFragment);
                timelyChartFragmentTransaction.commit();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        return dailyFragmentView;
    }

    //creating list of x-axis values
    private List<String> getXAxisValues() {
        List<String> labels = mTobaccoDaoService.selectDistinctDateTimeLogData();
        return labels;
    }

    //this method is used to create data for Line graph
    public LineData lineData(List<String> servicedDate) {
        ArrayList<Entry> lineEntries = new ArrayList<>();
        for(int i=0; i<servicedDate.size(); i++){
            Log.d(TAG, "lineData 메서드, servicedDate 총 size : " + servicedDate.size() + "(" + i + ") - " + servicedDate.get(i));

            // 랜덤 <- 서버에서 받아와야함
            //int mockAllUserAverageAmountFromServer = (int) (Math.random() * (12 - 8 + 1)) + 8; // 8~12개 범위 랜덤

            // DailyChartFragment 에서 해당 일의 전체 사용자 평균 받아옴 (추후에 네트워크서 받아오기)
            // ServerMock 싱글턴 객체의 데이터가 프래그먼트 업데이트시 변하지 않음
//            int random = mServerMockAllUserAverageByDate.get(i);
            Entry allUserAverageAmountByDate = new Entry(getAllUserAverageAmountByDateFromWeb(servicedDate.get(i)), i); // web 에 여러번 요청함
            lineEntries.add(allUserAverageAmountByDate);
            //lineEntries.add(new Entry(mockAllUserAverageAmountFromServer, i));
        }

        LineDataSet lineDataSet = new LineDataSet(lineEntries,"User Average Smoking Count");
        lineDataSet.setColors(new int[]{Color.rgb(255, 0, 0)});
        lineDataSet.setValueTextSize(10);

        //line chart not click
        lineDataSet.setHighlightEnabled(false);

        LineData lineData = new LineData(getXAxisValues(),lineDataSet);

        //char value format integer
        lineData.setValueFormatter(new MyValueFormatter());
        return lineData;
    }

    //this method is used to create data for Bar graph
    public BarData barData(List<String> servicedDate) {

        ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();

        List<Integer> servicedDateAmountList = new ArrayList<Integer>();
        for(int i=0; i<servicedDate.size(); i++){
            Integer servicedDateAmount = mTobaccoDaoService.getAmountByDay(servicedDate.get(i));
            float servicedDateAmountFloatType = servicedDateAmount.floatValue();
            BarEntry barEntry = new BarEntry(servicedDateAmountFloatType, i);
            barEntries.add(barEntry);
        }

        // 막대 그래프 범례
        BarDataSet barDataSet = new BarDataSet(barEntries, "My Smoking Count");
        barDataSet.setColors(new int[]{Color.rgb(93, 93, 93)});
        barDataSet.setValueTextSize(10);
        barDataSet.setBarSpacePercent(50f); //modify bar scale

        BarData barData = new BarData(getXAxisValues(), barDataSet);

        //char value format integer
        barData.setValueFormatter(new MyValueFormatter());

        return barData;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) { // ACTION_DATA_AVAILABLE: received data from the device.
                Log.d(TAG, "ACTION_DATA_AVAILABLE 리시버 동작");

                String message = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                /*
                    displayDailyChart
                 */
                // 1. set date in graph
                CombinedData newData = new CombinedData(getXAxisValues());

                // 4. set bar data and line data in combined chart
                newData.setData(barData(getXAxisValues()));
                newData.setData(lineData(getXAxisValues()));

                //set data
                mCombinedChart.setData(newData);

                mCombinedChart.notifyDataSetChanged();
                mCombinedChart.invalidate();
            }
        }
    };



    public int getAllUserAverageAmountByDateFromWeb(String date){

        Call<Long> thumbnailCall = mNetworkService.getAvg(date);
        thumbnailCall.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if(response.isSuccessful()) {
                    Log.i("MyTag", "응답코드 : " + "등록되었습니다.");
//                    text_result.append(response.body().intValue()+"\n");
                    finalAllUserAverageAmountByDateFromWeb =  response.body().intValue();
                } else {
                    int statusCode= response.code();
                    Log.i("MyTag", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.i("MyTag", "서버 onFailure 에러내용 : " + t.getMessage());
            }
        });

        Log.d(TAG, date + "날에 전체 사용자 averageAmount인 getAllUserAverageAmountByDateFromWeb 메서드 결과 : " + finalAllUserAverageAmountByDateFromWeb);
        return finalAllUserAverageAmountByDateFromWeb;
    }
}
