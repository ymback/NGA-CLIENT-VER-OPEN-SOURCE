package gov.anzong.meizi.common;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MeiziCookieManager {

    private String mCookie;

    private static final String KEY_MEIZI_COOKIE = "meizi_cookie";

    private static class SingleTonHolder {

        private static MeiziCookieManager sInstance = new MeiziCookieManager();
    }

    private MeiziCookieManager() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ApplicationContextHolder.getContext());
        mCookie = sp.getString(KEY_MEIZI_COOKIE, "");
    }


    public static MeiziCookieManager getInstance() {
        return SingleTonHolder.sInstance;
    }

    public String getMeiziCookie() {
        return mCookie;
    }

    public void addToMeiziUserList(String uid, String sess) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ApplicationContextHolder.getContext());
        mCookie = "uid=" + uid + "; sess=" + sess;
        sp.edit().putString(KEY_MEIZI_COOKIE, mCookie).apply();
    }


}

