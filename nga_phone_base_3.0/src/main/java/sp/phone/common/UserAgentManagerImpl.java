package sp.phone.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.common.PreferenceKey;

public class UserAgentManagerImpl implements UserAgentManager {

    private SharedPreferences mPrefs;

    private List<UserAgent> mUserAgents;

    private static class SingletonHolder {
        static UserAgentManagerImpl sInstance = new UserAgentManagerImpl();
    }

    public static UserAgentManagerImpl getInstance() {
        return SingletonHolder.sInstance;
    }

    private UserAgentManagerImpl() {
    }

    @Override
    public void initialize(Context context) {
        Context mContext = context.getApplicationContext();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        String uaListStr = mPrefs.getString(PreferenceKey.USER_AGENT_LIST, "");
        Log.d("uaListStr",uaListStr);
        if (TextUtils.isEmpty(uaListStr)) {
            mUserAgents = new ArrayList<>();
        } else {
            mUserAgents = JSON.parseArray(uaListStr, UserAgent.class);
            if (mUserAgents == null) {
                mUserAgents = new ArrayList<>();
            }
        }
        if(mUserAgents.size()==0){
            UserAgent ua = new UserAgent("自动");
            ua.setEnabled(true);
            mUserAgents.add(0,ua);
        }
        versionUpgrade();
    }

    private void versionUpgrade() {
    }

    @Override
    public void toggleUserAgent(int position) {
        UserAgent userAgent = mUserAgents.get(position);
        userAgent.setEnabled(true);
        closeOthers(position);
        commit();
    }

    public void closeOthers(UserAgent userAgent){
        mUserAgents.forEach((ua)->{
            if(ua!=userAgent&&ua.isEnabled()){
                ua.setEnabled(false);
            }
        });
    }

    public void closeOthers(int pos){
        UserAgent userAgent = mUserAgents.get(pos);
        mUserAgents.forEach((ua)->{
            if(ua!=userAgent&&ua.isEnabled()){
                ua.setEnabled(false);
            }
        });
    }

    @Override
    public void addUserAgent(UserAgent userAgent) {
        mUserAgents.add(userAgent);
        closeOthers(userAgent);
        commit();
    }

    @Override
    public List<UserAgent> getUserAgents() {
        return mUserAgents;
    }

    @Override
    public void removeUserAgent(int index) {
        if(index>0){
            if(mUserAgents.get(index).isEnabled()){
                mUserAgents.get(0).setEnabled(true);
            }
            mUserAgents.remove(index);
            commit();
        }
    }

    private void commit() {
        mPrefs.edit()
                .putString(PreferenceKey.USER_AGENT_LIST, JSON.toJSONString(mUserAgents))
                .apply();
    }
}
