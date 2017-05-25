package seungchan.com.tobaccoach_5_2.dao;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

import seungchan.com.tobaccoach_5_2.R;

public class LogDataAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mData;
    private LayoutInflater mLayoutInflater;

    public LogDataAdapter(Context context, List<String> data){
        mContext = context;
        mData = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View itemLayout = convertView;
        if(itemLayout == null){
            itemLayout = mLayoutInflater.inflate(R.layout.log_data_list_view_item, null);
        }

        TextView logData = (TextView)itemLayout.findViewById(R.id.text_log_data_item);

        logData.setText(mData.get(i));

        return itemLayout;
    }

    public void updateLogData(List<String> newData){
        mData.clear();
        mData.addAll(newData); // http://stackoverflow.com/questions/15422120/notifydatasetchange-not-working-from-custom-adapter
        this.notifyDataSetChanged();
    }
}
