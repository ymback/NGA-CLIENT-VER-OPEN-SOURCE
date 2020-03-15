package com.justwen.androidnga.cloud;

import android.content.Context;
import android.os.Build;

import com.justwen.androidnga.cloud.base.ICloudDataBase;
import com.justwen.androidnga.cloud.base.VersionBean;
import com.justwen.androidnga.cloud.lean.LeanDataBase;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * @author yangyihang
 */
public class CloudServerManager {

    private static ICloudDataBase sCloudDataBase;

    public static void init(Context context) {
        //bugly 初始化
        if (!BuildConfig.DEBUG) {
            CrashReport.initCrashReport(context, context.getString(R.string.bugly_app_id), false);
        }

        sCloudDataBase = new LeanDataBase();
        sCloudDataBase.init(context);
    }

    public static void putCrashData(Context context, String key, String value) {
        CrashReport.putUserData(context, key, value);
    }

    public static void uploadNewVersionInfo() {
        VersionBean versionBean = new VersionBean();
        versionBean.versionName = BuildConfig.VERSION_NAME;
        versionBean.androidVersion = String.valueOf(Build.VERSION.SDK_INT);
        sCloudDataBase.uploadVersionInfo(versionBean);
    }
}
