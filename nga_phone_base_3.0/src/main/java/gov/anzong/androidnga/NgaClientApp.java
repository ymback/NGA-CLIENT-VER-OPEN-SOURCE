package gov.anzong.androidnga;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.WebView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.awp.webkit.AwpEnvironment;

import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.DeviceUtils;
import sp.phone.common.ApplicationContextHolder;
import sp.phone.common.BoardManagerImpl;
import sp.phone.common.FilterKeywordsManagerImpl;
import sp.phone.common.PreferenceKey;
import sp.phone.common.UserManagerImpl;
import sp.phone.debug.BlockCanaryWatcher;
import sp.phone.debug.LeakCanaryWatcher;
import sp.phone.task.DeviceStatisticsTask;
import sp.phone.util.NLog;

public class NgaClientApp extends Application {

    private static final String TAG = NgaClientApp.class.getSimpleName();

    private boolean mNewVersion;

    @Override
    public void onCreate() {
        NLog.w(TAG, "app nga android start");
        ApplicationContextHolder.setContext(this);
        ContextUtils.setContext(this);
        LeakCanaryWatcher.initialize(this);
        BlockCanaryWatcher.startWatching(this);
        checkNewVersion();
        initCoreModule();
        initRouter();
        super.onCreate();
        AwpEnvironment.init(this, true);
        registerActivityLifecycleCallbacks(new ActivityCallback(this));

        if (DeviceUtils.isGreaterEqual_9_0()) {
            String processName = getProcessName(this);
            if (!getPackageName().equals(processName) && processName != null) {
                WebView.setDataDirectorySuffix(processName);
            }
        }
    }

    private void initRouter() {
        if (BuildConfig.DEBUG) {   // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this); // 尽可能早，推荐在Application中初始化
    }

    private void initCoreModule() {
        UserManagerImpl.getInstance().initialize(this);
        BoardManagerImpl.getInstance().initialize(this);
        FilterKeywordsManagerImpl.getInstance().initialize(this);
        // 注册crashHandler
        CrashHandler.getInstance().init(this);

    }

    public  String getProcessName(Context context) {
        if (context == null) return null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }

    private void checkNewVersion() {

        SharedPreferences sp = getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);
        int versionCode = BuildConfig.VERSION_CODE;
        if (sp.getInt(PreferenceKey.VERSION, 0) < versionCode) {
            sp.edit().putInt(PreferenceKey.VERSION, versionCode).apply();
            mNewVersion = true;
        }
        if (mNewVersion) {
            DeviceStatisticsTask.execute();
        }
    }

    public boolean isNewVersion() {
        return mNewVersion;
    }

    public void setNewVersion(boolean newVersion) {
        mNewVersion = newVersion;
    }

}
