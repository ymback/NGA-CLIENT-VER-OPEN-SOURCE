package gov.anzong.androidnga.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import gov.anzong.androidnga.R;
import sp.phone.fragment.material.MessageDetailFragment;
import sp.phone.utils.NLog;
import sp.phone.utils.StringUtils;

public class MessageDetailActivity extends SwipeBackAppCompatActivity {

    private static final String TAG = MessageDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        initActionBar();
        initFragment();
    }

    private void initActionBar() {
        if (getSupportActionBar() == null) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                ActionBar actionBar = getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
            }
        }
    }

    private void initFragment() {
        Fragment fragment = new MessageDetailFragment();
        fragment.setHasOptionsMenu(true);
        Bundle bundle = new Bundle();
        String url = getIntent().getDataString();
        int mid;
        if (null != url) {
            mid = getUrlParameter(url, "mid");
        } else {
            mid = getIntent().getIntExtra("mid", 0);
        }
        bundle.putInt("mid", mid);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    private int getUrlParameter(String url, String paraName) {
        if (StringUtils.isEmpty(url)) {
            return 0;
        }
        final String pattern = paraName + "=";
        int start = url.indexOf(pattern);
        if (start == -1)
            return 0;
        start += pattern.length();
        int end = url.indexOf("&", start);
        if (end == -1)
            end = url.length();
        String value = url.substring(start, end);
        int ret = 0;
        try {
            ret = Integer.parseInt(value);
        } catch (Exception e) {
            NLog.e(TAG, "invalid url:" + url);
        }
        return ret;
    }


//    @Override
//    public void jsonfinishLoad(SignData result) {// 给左边SIGN信息用的
//        Fragment SignContainer = getSupportFragmentManager().findFragmentById(
//                R.id.item_list);
//
//        OnSignPageLoadFinishedListener listener = null;
//        try {
//            listener = (OnSignPageLoadFinishedListener) SignContainer;
//            if (listener != null)
//                listener.jsonfinishLoad(result);
//        } catch (ClassCastException e) {
//            NLog.e(TAG, "topicContainer should implements "
//                    + OnSignPageLoadFinishedListener.class.getCanonicalName());
//        }
//    }

}