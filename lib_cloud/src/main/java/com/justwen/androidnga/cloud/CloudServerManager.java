package com.justwen.androidnga.cloud;

import android.content.Context;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * @author yangyihang
 */
public class CloudServerManager {

    public static void init(Context context) {
        //bugly 初始化
        CrashReport.initCrashReport(context, context.getString(R.string.bugly_app_id), BuildConfig.DEBUG);
    }

    public static void putCrashData(Context context, String key, String value) {
        CrashReport.putUserData(context, key, value);
    }
}
