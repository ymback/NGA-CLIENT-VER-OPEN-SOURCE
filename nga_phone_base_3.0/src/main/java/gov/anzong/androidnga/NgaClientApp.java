package gov.anzong.androidnga;

import android.app.Application;
import android.os.Process;
import android.webkit.WebView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.justwen.androidnga.cloud.CloudServerManager;

import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.PreferenceUtils;
import gov.anzong.androidnga.common.PreferenceKey;
import gov.anzong.androidnga.common.util.ReflectUtils;
import sp.phone.common.FilterKeywordsManagerImpl;
import sp.phone.common.UserManagerImpl;
import sp.phone.common.VersionUpgradeHelper;
import sp.phone.util.NLog;

public class NgaClientApp extends Application {

    private static final String TAG = NgaClientApp.class.getSimpleName();

    private static boolean sNewVersion;

    @Override
    public void onCreate() {
        NLog.w(TAG, "app nga android start");
        ContextUtils.setApplication(this);
        VersionUpgradeHelper.upgrade();
        initCoreModule();
        initRouter();
        super.onCreate();

        fixWebViewMultiProcessException();
        checkNewVersion();
        CloudServerManager.init(this);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandlerProxy(Thread.getDefaultUncaughtExceptionHandler()));
    }

    private void fixWebViewMultiProcessException() {
        Object obj = ReflectUtils.invokeMethod(Process.class, "myPpid");
        if (obj != null) {
            int ppid = (int) obj;
            if (ppid == 1) {
                WebView.setDataDirectorySuffix("_multi");
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
        FilterKeywordsManagerImpl.getInstance().initialize(this);
//        // 注册crashHandler
//        CrashHandler.getInstance().init(this);

    }

    private void checkNewVersion() {
        int versionCode = PreferenceUtils.getData(PreferenceKey.VERSION_CODE, 0);
        if (BuildConfig.VERSION_CODE > versionCode) {
            PreferenceUtils.putData(PreferenceKey.VERSION_CODE, BuildConfig.VERSION_CODE);
            sNewVersion = true;
        }
    }

    public static boolean isNewVersion() {
        return sNewVersion;
    }

    public static void setNewVersion(boolean newVersion) {
        sNewVersion = newVersion;
    }

}
