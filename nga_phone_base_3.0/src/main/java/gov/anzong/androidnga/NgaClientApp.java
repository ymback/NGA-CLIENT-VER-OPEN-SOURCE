package gov.anzong.androidnga;

import android.app.Application;
import android.os.Process;
import android.webkit.WebView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.justwen.androidnga.cloud.CloudServerManager;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private boolean mNewVersion;

    private boolean mMirrorVersionUpgrade;

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
        CloudServerManager.init(this, mMirrorVersionUpgrade);
    }

    private void fixWebViewMultiProcessException() {
        Object obj = ReflectUtils.invokeMethodAndGetResult(Process.class, "myPpid");
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
        try {
            Matcher matcher = Pattern.compile("([0-9]+)\\.([0-9]+)\\.([0-9]+)").matcher(BuildConfig.VERSION_NAME);
            if (matcher.find()) {
                int majorCode = Integer.parseInt(Objects.requireNonNull(matcher.group(1)));
                int mirrorCode = Integer.parseInt(Objects.requireNonNull(matcher.group(2)));

                if (majorCode > PreferenceUtils.getData(PreferenceKey.VERSION_MAJOR_CODE, 0)
                        || mirrorCode > PreferenceUtils.getData(PreferenceKey.VERSION_MIRROR_CODE, 0)) {
                    PreferenceUtils.putData(PreferenceKey.VERSION_MAJOR_CODE, majorCode);
                    PreferenceUtils.putData(PreferenceKey.VERSION_MIRROR_CODE, mirrorCode);
                    mMirrorVersionUpgrade = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int versionCode = PreferenceUtils.getData(PreferenceKey.VERSION_CODE, 0);
        if (BuildConfig.VERSION_CODE > versionCode) {
            PreferenceUtils.putData(PreferenceKey.VERSION_CODE, BuildConfig.VERSION_CODE);
            mNewVersion = true;
        }
    }

    public boolean isNewVersion() {
        return mNewVersion;
    }

    public void setNewVersion(boolean newVersion) {
        mNewVersion = newVersion;
    }

}
