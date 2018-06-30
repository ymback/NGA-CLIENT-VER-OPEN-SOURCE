package sp.phone.mvp.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;

import gov.anzong.androidnga.R;
import sp.phone.common.ApplicationContextHolder;
import sp.phone.common.PreferenceKey;
import sp.phone.mvp.contract.BaseContract;

/**
 * Created by Justwen on 2017/11/25.
 */

public abstract class BaseModel implements BaseContract.Model {

    private LifecycleProvider<FragmentEvent> mProvider;

    private String mDomain;

    public BaseModel() {
        Context context = ApplicationContextHolder.getContext();
        SharedPreferences sp = context.getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);
        int index = Integer.parseInt(sp.getString(PreferenceKey.KEY_NGA_DOMAIN,"0"));
        mDomain = context.getResources().getStringArray(R.array.nga_domain)[index];
    }

    @Override
    public void detach() {
        mProvider = null;
    }

    @Override
    public void setLifecycleProvider(LifecycleProvider<FragmentEvent> provider) {
        mProvider = provider;
    }

    protected LifecycleProvider<FragmentEvent> getLifecycleProvider() {
        return mProvider;
    }

    protected String getAvailableDomain() {
        return mDomain;
    }
}
