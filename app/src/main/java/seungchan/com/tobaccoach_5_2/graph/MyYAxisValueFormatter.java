package seungchan.com.tobaccoach_5_2.graph;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by jojungwook on 2017. 5. 16..
 */

public class MyYAxisValueFormatter implements YAxisValueFormatter {

    private DecimalFormat mFormat;

    public MyYAxisValueFormatter() {
        // format values to 1 decimal digit
        mFormat = new DecimalFormat("###,###,##0");
    }

    public String getFormattedValue(float value, YAxis yAxis) {
        return mFormat.format(value) + "";
    }
}
