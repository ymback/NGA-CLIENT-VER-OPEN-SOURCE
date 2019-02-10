package gov.anzong.androidnga;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.activity.MainActivity;
import sp.phone.util.NLog;

public class ActivityCallback implements Application.ActivityLifecycleCallbacks {

    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";

    private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

    private List<Activity> mActivityList = new ArrayList<>();

    private NgaClientApp mApp;

    public ActivityCallback(NgaClientApp app) {
        mApp = app;
    }

    private void finishAllActivities() {
        for (Activity activity : mActivityList) {
            activity.finish();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activity instanceof MainActivity && !mActivityList.isEmpty()) {
            mApp.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String intentAction = intent.getAction();
                    if (TextUtils.equals(intentAction, Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                        String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                        NLog.d("reason = " + reason);
                        if (TextUtils.equals(SYSTEM_DIALOG_REASON_HOME_KEY, reason)) {
                            finishAllActivities();
                            mApp.unregisterReceiver(this);
                        }
                    }
                }
            }, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        }
        mActivityList.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mActivityList.remove(activity);
    }
}
