package sp.phone.fragment.material;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sp.phone.fragment.MessageDetialListContainer;
import sp.phone.utils.StringUtils;

public class MessageDetailFragment extends MaterialCompatFragment {

    private Bundle mBundle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = getArguments();
//        String url = mActivity.getIntent().getDataString();
//        if (null != url) {
//            mMid = this.getUrlParameter(url, "mid");
//        } else {
//            mMid = mActivity.getIntent().getIntExtra("mid", 0);
//        }
    }

    @Override
    public View onCreateContainerView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentManager fm = getChildFragmentManager();
        Fragment fragment = fm.findFragmentById(getContainerId());// ok
        if (fragment == null) {
            fragment = new MessageDetialListContainer();
//            Bundle args = new Bundle();// (getIntent().getExtras());
//            if (null != mActivity.getIntent().getExtras()) {
//                args.putAll(mActivity.getIntent().getExtras());
//            }
//            args.putInt("mid", mMid);
            fragment.setArguments(getArguments());
            fm.beginTransaction().add(getContainerId(), fragment).commit();
        }
        return super.onCreateContainerView(inflater, container, savedInstanceState);
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
            Log.e(TAG, "invalid url:" + url);
        }
        return ret;
    }

    @Override
    protected View.OnClickListener getFabClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageDetialListContainer fragment = (MessageDetialListContainer) getChildFragmentManager().findFragmentById(getContainerId());
                fragment.startArticleReply();
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        MessageDetialListContainer fragment = (MessageDetialListContainer) getChildFragmentManager().findFragmentById(getContainerId());
        fragment.onActivityResult(requestCode,resultCode,data);
    }
}
