package sp.phone.util;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * Created by Justwen on 2017/7/16.
 */

public class DeviceUtils {

    private static final String DEVICE_NAME_MEIZU = "Meizu";

    public static boolean isGreaterEqual_6_0() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isGreaterEqual_7_0() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    public static boolean isMeizu() {
        return DEVICE_NAME_MEIZU.equalsIgnoreCase(android.os.Build.MANUFACTURER);
    }

    public static boolean isGreaterEqual_8_0() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    public static boolean isGreaterEqual_9_0() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }

    public static boolean isTablet(Context context) {
        int screenLayout = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMan.getActiveNetworkInfo();
        return networkInfo != null
                && networkInfo.isConnected()
                && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}
