package com.justwen.androidnga.cloud.bugly;

import android.content.Context;

import com.justwen.androidnga.cloud.BuildConfig;
import com.tencent.bugly.crashreport.CrashReport;

public class BuglyWrapper {

    public static void init(Context context) {
        if (!BuildConfig.DEBUG) {
            int id = context.getResources().getIdentifier("bugly_app_id", "string", context.getOpPackageName());
            if (id > 0) {
                CrashReport.initCrashReport(context, context.getString(id), false);
            }
        }
    }
}
