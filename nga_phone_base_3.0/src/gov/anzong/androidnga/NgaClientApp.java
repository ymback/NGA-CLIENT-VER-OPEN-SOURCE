package gov.anzong.androidnga;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.alibaba.android.arouter.launcher.ARouter;

import java.io.File;

import gov.anzong.androidnga.util.NetUtil;
import sp.phone.common.BoardManagerImpl;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.PreferenceKey;
import sp.phone.common.ThemeManager;
import sp.phone.common.UserManagerImpl;
import sp.phone.utils.ApplicationContextHolder;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.NLog;

public class NgaClientApp extends Application {

    private boolean mCheckNewVersion;

    @Override
    public void onCreate() {
        ApplicationContextHolder.setContext(this);
        NLog.d("app nga android start");
        PhoneConfiguration.getInstance();
        loadConfig();
        initPath();
        UserManagerImpl.getInstance().initialize(this);
        BoardManagerImpl.getInstance().initialize(this);
        // 注册crashHandler
        CrashHandler.getInstance().init(this);

        NetUtil.init(this);

        if (BuildConfig.DEBUG) {   // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this); // 尽可能早，推荐在Application中初始化

        super.onCreate();
    }

    private void initPath() {
        File baseDir = getExternalCacheDir();
        if (baseDir != null)
            HttpUtil.PATH = baseDir.getAbsolutePath();
        else
            HttpUtil.PATH = android.os.Environment
                    .getExternalStorageDirectory().getPath()
                    + "/Android/data/gov.anzong.androidnga";
        HttpUtil.PATH_AVATAR = HttpUtil.PATH + "/nga_cache";
        HttpUtil.PATH_NOMEDIA = HttpUtil.PATH + "/.nomedia";
        HttpUtil.PATH_IMAGES = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getAbsolutePath();
    }

    public void addToMeiziUserList(String uid, String sess) {
        String cookie = "uid=" + uid + "; sess=" + sess;
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString(PreferenceKey.MEIZI_COOLIE, cookie)
                .apply();
        PhoneConfiguration.getInstance().putData(PreferenceKey.MEIZI_COOLIE, cookie);
    }

    private void loadConfig() {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int oldVersion = sp.getInt(PreferenceKey.VERSION, 0);
        if (oldVersion < BuildConfig.VERSION_CODE) {
            sp.edit().putInt(PreferenceKey.VERSION, BuildConfig.VERSION_CODE).apply();
            mCheckNewVersion = true;
        }

        if (sp.getBoolean(PreferenceKey.NIGHT_MODE, false)) {
            ThemeManager.getInstance().setMode(1);
        }

    }

    public boolean isNewVersion() {
        return mCheckNewVersion;
    }

    public void setNewVersion(boolean value) {
        mCheckNewVersion = value;
    }
}
