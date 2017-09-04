package sp.phone.utils;

import android.util.Log;

import gov.anzong.androidnga.BuildConfig;

/**
 * 带开关的Log
 * Created by elrond on 2017/9/4.
 */

public class NLog {
    public static int v(String tag, String msg) {
        if (BuildConfig.DEBUG)
            return NLog.v(tag, msg);
        else
            return 0;
    }

    public static int d(String tag, String msg) {
        if (BuildConfig.DEBUG)
            return NLog.d(tag, msg);
        else
            return 0;
    }

    public static int i(String tag, String msg) {
        if (BuildConfig.DEBUG)
            return NLog.i(tag, msg);
        else
            return 0;
    }

    public static int w(String tag, String msg) {
        if (BuildConfig.DEBUG)
            return NLog.w(tag, msg);
        else
            return 0;
    }

    public static int e(String tag, String msg) {
        if (BuildConfig.DEBUG)
            return NLog.e(tag, msg);
        else
            return 0;
    }

    public static int e(String tag, String msg, Throwable throwable) {
        if (BuildConfig.DEBUG)
            return NLog.e(tag, msg, throwable);
        else
            return 0;
    }

    public static String getStackTraceString(Throwable throwable) {
        return Log.getStackTraceString(throwable);
    }
}
