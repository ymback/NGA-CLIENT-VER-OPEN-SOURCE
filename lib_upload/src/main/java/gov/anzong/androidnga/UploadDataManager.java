package gov.anzong.androidnga;

import android.content.Context;

import com.tencent.bugly.crashreport.CrashReport;

import gov.anzong.androidnga.upload.BuildConfig;

public class UploadDataManager {

    public static void init(Context context) {
        CrashReport.initCrashReport(context, "a5e8d07750", BuildConfig.DEBUG);
    }
}
