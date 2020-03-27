package com.justwen.androidnga.cloud;

import android.content.Context;
import android.os.Build;

import com.justwen.androidnga.cloud.base.ICloudDataBase;
import com.justwen.androidnga.cloud.base.VersionBean;
import com.justwen.androidnga.cloud.lean.LeanDataBase;
import com.tencent.bugly.crashreport.CrashReport;

import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.PreferenceUtils;
import gov.anzong.androidnga.common.PreferenceKey;

/**
 * @author yangyihang
 */
public class CloudServerManager {

    private static ICloudDataBase sCloudDataBase;

    public static void init(Context context, boolean newVersion) {
        //bugly 初始化
        if (!BuildConfig.DEBUG) {
            CrashReport.initCrashReport(context, context.getString(R.string.bugly_app_id), false);
        }

        if (newVersion) {
            try {
                sCloudDataBase = new LeanDataBase(createVersionBean());
                sCloudDataBase.init(ContextUtils.getApplication());
                sCloudDataBase.uploadVersionInfo();
            } catch (Exception e) {
                sCloudDataBase = null;
            }
        }
    }

    public static void putCrashData(Context context, String key, String value) {
        CrashReport.putUserData(context, key, value);
    }

    private static VersionBean createVersionBean() {
        VersionBean versionBean = new VersionBean();
        int majorCode = PreferenceUtils.getData(PreferenceKey.VERSION_MAJOR_CODE, 0);
        int mirrorCode = PreferenceUtils.getData(PreferenceKey.VERSION_MIRROR_CODE, 0);
        versionBean.versionName = majorCode + "." + mirrorCode + ".0";
        versionBean.androidVersion = String.valueOf(Build.VERSION.SDK_INT);
        versionBean.versionCode = BuildConfig.VERSION_CODE;
        return versionBean;
    }

    public static void checkUpgrade() {
//        if (sCloudDataBase != null) {
//            sCloudDataBase.checkUpgrade();
//        }
    }
}
