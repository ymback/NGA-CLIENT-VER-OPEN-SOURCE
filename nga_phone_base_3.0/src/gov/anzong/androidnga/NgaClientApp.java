package gov.anzong.androidnga;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Environment;

import java.io.File;

import gov.anzong.androidnga.util.NetUtil;
import sp.phone.common.BoardManagerImpl;
import sp.phone.common.Constants;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.PreferenceKey;
import sp.phone.theme.ThemeManager;
import sp.phone.common.UserManagerImpl;
import sp.phone.utils.ApplicationContextHolder;
import sp.phone.utils.DeviceUtils;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.NLog;

public class NgaClientApp extends Application implements PreferenceKey {
    public final static int version = BuildConfig.VERSION_CODE;
    final private static String TAG = NgaClientApp.class.getSimpleName();
    boolean newVersion = false;
    private PhoneConfiguration config = null;

    @Override
    public void onCreate() {
        ApplicationContextHolder.setContext(this);
        //gov.anzong.meizi.common.ApplicationContextHolder.setContext(this);
        NLog.w(TAG, "app nga android start");
        if (config == null)
            config = PhoneConfiguration.getInstance();
        loadConfig();
        initUserInfo();
        initPath();
        UserManagerImpl.getInstance().initialize(this);
        BoardManagerImpl.getInstance().initialize(this);
        // 注册crashHandler
        CrashHandler.getInstance().init(this);

        NetUtil.init(this);

//        if (BuildConfig.DEBUG) {   // 这两行必须写在init之前，否则这些配置在init过程中将无效
//            ARouter.openLog();     // 打印日志
//            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
//        }
//        ARouter.init(this); // 尽可能早，推荐在Application中初始化

        createNotificationChannel();
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

    private void initUserInfo() {
        PhoneConfiguration config = PhoneConfiguration.getInstance();

        SharedPreferences share = this.getSharedPreferences(PERFERENCE,
                MODE_PRIVATE);

        boolean downImgWithoutWifi = share.getBoolean(DOWNLOAD_IMG_NO_WIFI,
                false);
        config.setDownImgNoWifi(downImgWithoutWifi);
        boolean downAvatarNoWifi = share.getBoolean(DOWNLOAD_AVATAR_NO_WIFI,
                false);
        config.setDownAvatarNoWifi(downAvatarNoWifi);

    }

    private void loadConfig() {

        PhoneConfiguration config = PhoneConfiguration.getInstance();

        SharedPreferences share = getSharedPreferences(PERFERENCE,
                MODE_PRIVATE);
        ThemeManager.getInstance().setNighMode(share.getBoolean(NIGHT_MODE, false));

        ThemeManager.getInstance().screenOrentation = share.getInt(
                SCREEN_ORENTATION, ActivityInfo.SCREEN_ORIENTATION_USER);

        int version_in_config = share.getInt(VERSION, 0);
        if (version_in_config < version) {
            newVersion = true;
            Editor editor = share.edit();
            editor.putInt(VERSION, version);
            editor.putBoolean(REFRESH_AFTER_POST, false);
            editor.apply();
        }

        // refresh
        config.setRefreshAfterPost(false);

        config.refresh_after_post_setting_mode = share.getBoolean(REFRESH_AFTERPOST_SETTING_MODE, true);
        config.showSignature = share.getBoolean(SHOW_SIGNATURE, false);
        config.showColortxt = share.getBoolean(SHOW_COLORTXT, false);
        config.fullscreen = share.getBoolean(FULLSCREENMODE, false);
        config.blackgunsound = share.getInt(BLACKGUN_SOUND, 0);
        config.iconmode = share.getBoolean(SHOW_ICON_MODE, false);

        // font
        final float defTextSize = 21.0f;// new TextView(this).getTextSize();
        final int defWebSize = 16;// new
        // WebView(this).getSettings().getDefaultFontSize();

        final float textSize = share.getFloat(TEXT_SIZE, defTextSize);
        final int webSize = share.getInt(WEB_SIZE, defWebSize);
        config.setTextSize(textSize);
        config.setWebSize(webSize);

        boolean notification = share.getBoolean(ENABLE_NOTIFIACTION, true);
        boolean notificationSound = share.getBoolean(NOTIFIACTION_SOUND, true);
        config.notification = notification;
        config.notificationSound = notificationSound;

        config.nikeWidth = share.getInt(NICK_WIDTH, 100);

    }

    public boolean isNewVersion() {
        return newVersion;
    }

    public void setNewVersion(boolean newVersion) {
        this.newVersion = newVersion;
    }

    @TargetApi(26)
    private void createNotificationChannel() {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (!DeviceUtils.isGreaterEqual_8_0() || notificationManager == null) {
            return;
        }
        String id = Constants.NOTIFICATION_ID;
        CharSequence name = Constants.NOTIFICATION_NAME;
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.enableLights(true); //是否在桌面icon右上角展示小红点
        channel.setLightColor(Color.GREEN); //小红点颜色
        channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
        notificationManager.createNotificationChannel(channel);
    }
}
