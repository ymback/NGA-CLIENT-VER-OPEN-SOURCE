package com.justwen.androidnga.upload;

import android.content.Context;

import com.tencent.bugly.crashreport.CrashReport;

public class UploadDataManager {

    public static void init(Context context) {
        CrashReport.initCrashReport(context, "a5e8d07750", BuildConfig.DEBUG);
    }

    public static void putCrashData(Context context, String key, String value) {
        CrashReport.putUserData(context, key, value);
    }
}
