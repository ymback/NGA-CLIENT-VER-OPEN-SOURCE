package gov.anzong.androidnga.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by elrond on 2016/12/22.
 */

public class NetUtil {

    private static NetUtil mInstance;

    public static void init(Context context) {
        mInstance = new NetUtil(context);
    }

    public static NetUtil getInstance() {
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
