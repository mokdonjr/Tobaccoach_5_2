package seungchan.com.tobaccoach_5_2.fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.stacktips.view.CalendarListener;
import com.stacktips.view.CustomCalendarView;
import com.stacktips.view.DayDecorator;
import com.stacktips.view.DayView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import seungchan.com.tobaccoach_5_2.R;
import seungchan.com.tobaccoach_5_2.dao.TobaccoDaoService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalendarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment {
    private static String TAG = "CalendarFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "ipAddress";
    private static final String ARG_PARAM2 = "userId";

    // TODO: Rename and change types of parameters
    private String ipAddress;
    private String userId;

    private TobaccoDaoService tobaccoDaoService;

    @BindView(R.id.calendar_view)
    CustomCalendarView calendarView;

    private OnFragmentInteractionListener mListener;

    public CalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ipAddress = getArguments().getString(ARG_PARAM1);
            userId = getArguments().getString(ARG_PARAM2);
        }
        tobaccoDaoService = TobaccoDaoService.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View calendarFragmentView=  inflater.inflate(R.layout.fragment_calendar, container, false);
        ButterKnife.bind(this, calendarFragmentView);

        //뭔가를 출력 후 리턴
//Initialize calendar with date
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());

        //Show sunday as first date of week
        calendarView.setFirstDayOfWeek(Calendar.SUNDAY);

        //Show/hide overflow days of a month
        calendarView.setShowOverflowDate(false);

        //call refreshCalendar to update calendar the view
        calendarView.refreshCalendar(currentCalendar);

        //Handling custom calendar events
        calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                //if (!CalendarUtils.isPastDay(date)) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                //(df.format(date), Toast.LENGTH_SHORT);
                Toast.makeText(getContext(), df.format(date) + " dayAmount : " + tobaccoDaoService
                        .getAmountByDay(df.format(date)) + " ||| avarage : " +  tobaccoDaoService
                        .getAverageLogAmount(), Toast.LENGTH_SHORT)
                        .show();
                //} else {
                //    selectedDateTv.setText("Selected date is disabled!");
                //}
            }

            @Override
            public void onMonthChanged(Date date) {
                //SimpleDateFormat df = new SimpleDateFormat("MM-yyyy");
                //Toast.makeText(CalendarDayDecoratorActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
            }
        });


        //adding calendar day decorators
        List<DayDecorator> decorators = new ArrayList<>();
        decorators.add(new OveredColorDecorator());
        calendarView.setDecorators(decorators);
        calendarView.refreshCalendar(currentCalendar);

        return calendarFragmentView;
    }

    private class OveredColorDecorator implements DayDecorator {
        @Override
        public void decorate(DayView dayView) {

            SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd", Locale.getDefault() );
            String dateStr= formatter.format ( dayView.getDate() );
            int todayAmount= tobaccoDaoService.getAmountByDay(dateStr);
            int averageAmount= tobaccoDaoService.getAverageLogAmount();

            if (todayAmount > averageAmount) { //특정 날짜가 평균을 넘었다면 색칠
                int color = Color.parseColor("#FF1A5B");
                dayView.setBackgroundColor(color);
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
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




    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
