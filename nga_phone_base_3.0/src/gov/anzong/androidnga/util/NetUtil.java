package gov.anzong.androidnga.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import sp.phone.common.ApplicationContextHolder;

/**
 * Created by elrond on 2016/12/22.
 */

public class NetUtil {

    private static NetUtil mInstance;

    public static NetUtil getInstance() {
        if (mInstance == null) {
            mInstance = new NetUtil(ApplicationContextHolder.getContext());
        }
        return mInstance;
    }

    private Context context;

    private NetUtil(Context context) {
        this.context = context;
    }

    public boolean isInWifi() {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        return wifi == NetworkInfo.State.CONNECTED;
    }
}
