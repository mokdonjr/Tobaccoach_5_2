package seungchan.com.tobaccoach_5_2.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import seungchan.com.tobaccoach_5_2.R;
import seungchan.com.tobaccoach_5_2.dao.TobaccoDBHelper;
import seungchan.com.tobaccoach_5_2.ble.BluetoothLeService;
import seungchan.com.tobaccoach_5_2.dao.TobaccoDaoService;
import seungchan.com.tobaccoach_5_2.graph.MyValueFormatter;
import seungchan.com.tobaccoach_5_2.graph.MyYAxisValueFormatter;


public class TimelyChartFragment extends Fragment {
    private static String TAG = "TimelyChartFragment";

    private static final String ARG_PARAM1 = "selected_date";

    private String mParam1;
    private OnFragmentInteractionListener mListener;

    private TobaccoDaoService mTobaccoDaoService;

    @BindView(R.id.timely_chart) LineChart mLineChart;
    private YAxis mYAxis;


    public TimelyChartFragment() {    }

    public static TimelyChartFragment newInstance(String param1) {
        TimelyChartFragment fragment = new TimelyChartFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        mTobaccoDaoService = TobaccoDaoService.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View timelyChartFragmentView = inflater.inflate(R.layout.fragment_timely_chart, container, false);
        ButterKnife.bind(this, timelyChartFragmentView);

        //remove grid line
        mLineChart.getAxisLeft().setDrawGridLines(false);
        mLineChart.getXAxis().setDrawGridLines(false);

        mYAxis = mLineChart.getAxisLeft();

        //set YAxis maxvalue and minvalue
        mYAxis.setAxisMaxValue(0);
        mYAxis.setAxisMaxValue(10);

        //left Y axis labels to be integers
        mYAxis.setValueFormatter(new MyYAxisValueFormatter());

        //right Y axis labels to be setEnable
        mLineChart.getAxisRight().setEnabled(false);

        //erase description
        mLineChart.setDescription("");

        //prevent zoom
        mLineChart.setScaleEnabled(true);

        mLineChart.setData(lineData(mParam1));
        mLineChart.invalidate();
        mLineChart.animateY(3000);

        return timelyChartFragmentView;
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

    //creating list of x-axis values
    public List<String> getXAxisValues() {
        List<String> hours = getFormedHH();
        return hours;
    }

    public List<Integer> getLogDataNumByXAxisValues(String date){
        // x축 0시~23시
        List<String> hours = getFormedHH(); // 00시, 01시 ,,, 23시

        // 해당 날짜에 대한 HH:mm:ss 로그 Time 리스트
        List<String> logDataByXAxisValues = mTobaccoDaoService.selectTimeLogDataByDay(date);

        // 해당 날짜에 대한 HH 리스트
        List<String> logDataByXAxisValuesOnlyHH = new ArrayList<String>();
        for(int i=0; i<logDataByXAxisValues.size(); i++){ // HH:mm:ss 를 HH 까지만 잘라내기
            String onlyHH = logDataByXAxisValues.get(i).substring(0, 2); // HH
            logDataByXAxisValuesOnlyHH.add(onlyHH);
        }

        // 시간에 따른 개수 - 0개 초기화 ( x축 시간에 따른 logDataByXAxisValues의 개수 )
        List<Integer> logDataNumByXAxisValues = new ArrayList<Integer>();
        for(int i=0; i<24; i++){
            logDataNumByXAxisValues.add(0);
        }

        // 시간에 따른 개수 채워넣기
        for(int j=0; j<logDataNumByXAxisValues.size(); j++){
            for(int i=0; i<logDataByXAxisValuesOnlyHH.size(); i++){
                if( logDataByXAxisValuesOnlyHH.get(i).equals( hours.get(j) ) ) {
                    logDataNumByXAxisValues.set(j, logDataNumByXAxisValues.get(j) + 1);
                    Log.d(TAG, "getLogDataNumByXAxisValues()메서드, 시간에 따른 개수 채워 넣을 때 값 : " + logDataNumByXAxisValues.get(j) + " / 시간 : " + hours.get(j));
                }
//                Log.d(TAG, "getLogDataNumByXAxisValues()메서드, " + logDataByXAxisValuesOnlyHH.get(i));
            }
        }

        return logDataNumByXAxisValues;
    }

    public List<String> getFormedHH(){
        List<String> formedHH = new ArrayList<>();
        for(int i=0; i<24; i++){
            formedHH.add(String.valueOf(i));
            Log.d(TAG, "getFormedHH메서드, 시간 만들기 : " + String.valueOf(i) + " / 실제 formedHH에 더해진 시간 : " + formedHH.get(i));
        }
        for(int i=0; i<formedHH.size(); i++){
            if(formedHH.get(i).length() == 1){
                formedHH.set(i, "0" + formedHH.get(i));
                Log.d(TAG, "getFormedHH메서드, 시간 한 자리인 경우 두 자리수로 변경 완료 : " + formedHH.get(i));
            }
            else Log.d(TAG, "getFormedHH메서드, 시간 두 자리 수 인 경우 : " + formedHH.get(i));
        }
        return formedHH;
    }

    //this method is used to create data for Line graph
    public LineData lineData(String date) {

        List<Integer> logDataNumByHours = getLogDataNumByXAxisValues(date);

        ArrayList<Entry> lineEntries = new ArrayList<>();
        for(int i=0; i<logDataNumByHours.size(); i++){
            Entry entry = new Entry(logDataNumByHours.get(i), i);
            lineEntries.add(entry);
        }

        LineDataSet lineDataSet = new LineDataSet(lineEntries,"My Smoking Count Per Time");
        lineDataSet.setColors(new int[]{Color.rgb(34, 116, 28)});
        lineDataSet.setValueTextSize(10);
        lineDataSet.setHighlightEnabled(false);

        LineData lineData = new LineData(getXAxisValues(),lineDataSet);
        //char value format integer
        lineData.setValueFormatter(new MyValueFormatter());

        return lineData;
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
                    displayTimelyChart
                 */

                mLineChart.setData(lineData(mParam1));
                mLineChart.invalidate();
//                mLineChart.animateY(3000);

                mLineChart.notifyDataSetChanged();
                mLineChart.invalidate();

            }
        }
    };
}
