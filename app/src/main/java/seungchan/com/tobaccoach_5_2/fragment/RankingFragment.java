package seungchan.com.tobaccoach_5_2.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import seungchan.com.tobaccoach_5_2.R;
import seungchan.com.tobaccoach_5_2.utils.AppSettingUtils;


public class RankingFragment extends Fragment {
    private static String TAG = "RankingFragment";

    public static final String ARG_PARAM1 = "ipAddress";
    private String mIpAddress; // 액티비티로부터 받아옴

    private OnFragmentInteractionListener mListener;

    private AppSettingUtils mAppSettingUtils;

    @BindView(R.id.webview_tobaccoach) WebView mWebView;
    private WebSettings mWebSettings;

    public RankingFragment() { }

    public static RankingFragment newInstance(String param1) {
        RankingFragment fragment = new RankingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIpAddress = getArguments().getString(ARG_PARAM1); // 인수 받음
        }
        mAppSettingUtils = AppSettingUtils.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rankingFragmentView =  inflater.inflate(R.layout.fragment_ranking, container, false);
        ButterKnife.bind(this, rankingFragmentView);

        if(mAppSettingUtils != null){
            mWebView.setWebViewClient(new WebViewClient());
            mWebSettings = mWebView.getSettings();
            mWebSettings.setJavaScriptEnabled(true);
            mWebView.loadUrl(mAppSettingUtils.getWebApplicationServerRankingPageUrl(mIpAddress));
        }
        return rankingFragmentView;
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
}
