package gov.anzong.androidnga;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.util.NetUtil;
import sp.phone.bean.Board;
import sp.phone.bean.Bookmark;
import sp.phone.common.BoardManagerImpl;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.PreferenceKey;
import sp.phone.common.ThemeManager;
import sp.phone.common.UserManagerImpl;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.NLog;
import sp.phone.utils.ResourceUtils;
import sp.phone.utils.StringUtils;

public class NgaClientApp extends Application implements PreferenceKey {
    public final static int version = BuildConfig.VERSION_CODE;
    final private static String TAG = NgaClientApp.class.getSimpleName();
    boolean newVersion = false;
    private PhoneConfiguration config = null;

    @Override
    public void onCreate() {
        NLog.w(TAG, "app nga android start");
        if (config == null)
            config = PhoneConfiguration.getInstance();
        loadConfig();
        initUserInfo();
        initPath();
        UserManagerImpl.getInstance().initialize(this);
        BoardManagerImpl.getInstance().initialize(this);
        ResourceUtils.setContext(this);
        CrashHandler crashHandler = CrashHandler.getInstance();
        // 注册crashHandler
        crashHandler.init(getApplicationContext());

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

        config.setDb_Cookie(share.getString(DBCOOKIE, ""));
    }

    @Deprecated
    public void addToUserList(String uid, String cid, String name,
                              String replyString, int replytotalnum, String blacklist) {
        UserManagerImpl.getInstance().addUser(uid, cid, name, replyString, replytotalnum, blacklist);
    }

    @Deprecated
    public void upgradeUserdata(String blacklist) {
        UserManagerImpl.getInstance().setBlackList(blacklist);
    }

    public void addToMeiziUserList(String uid, String sess) {
        SharedPreferences share = getSharedPreferences(PERFERENCE, MODE_PRIVATE);
        String cookie = "uid=" + uid + "; sess=" + sess;
        share.edit().putString(DBCOOKIE, cookie).apply();
        config.setDb_Cookie(cookie);
    }

    private void loadConfig() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        ThemeManager tm = ThemeManager.getInstance();
        PhoneConfiguration config = PhoneConfiguration.getInstance();

        tm.setTheme(Integer.parseInt(sp.getString(PreferenceKey.MATERIAL_THEME, "0")));
        config.setShowBottomTab(sp.getBoolean(PreferenceKey.BOTTOM_TAB, false));
        config.setLeftHandMode(sp.getBoolean(PreferenceKey.LEFT_HAND, false));
        config.setHardwareAcceleratedMode(sp.getBoolean(PreferenceKey.HARDWARE_ACCELERATED, true));

        SharedPreferences share = getSharedPreferences(PERFERENCE,
                MODE_PRIVATE);
        if (share.getBoolean(NIGHT_MODE, false))
            ThemeManager.getInstance().setMode(1);

        ThemeManager.getInstance().screenOrentation = share.getInt(
                SCREEN_ORENTATION, ActivityInfo.SCREEN_ORIENTATION_USER);

        int version_in_config = share.getInt(VERSION, 0);
        if (version_in_config < version) {
            newVersion = true;
            Editor editor = share.edit();
            editor.putInt(VERSION, version);
            editor.putBoolean(REFRESH_AFTER_POST, false);

            String recentStr = share.getString(RECENT_BOARD, "");
            List<Board> recentList = null;
            if (!StringUtils.isEmpty(recentStr)) {
                recentList = JSON.parseArray(recentStr, Board.class);
                if (recentList != null) {
                    for (int j = 0; j < recentList.size(); j++) {
                        //recentList.get(j).setIcon(R.drawable.pdefault);
                    }
                    recentStr = JSON.toJSONString(recentList);
                    editor.putString(RECENT_BOARD, recentStr);
                }
            }
            if (version_in_config < 2028) {
                editor.putString(USER_LIST, "");
            }
            editor.apply();

        }

        // refresh
        config.setRefreshAfterPost(false);

        config.showAnimation = share.getBoolean(SHOW_ANIMATION, false);
        config.refresh_after_post_setting_mode = share.getBoolean(REFRESH_AFTERPOST_SETTING_MODE, true);
        config.useViewCache = share.getBoolean(USE_VIEW_CACHE, true);
        config.showSignature = share.getBoolean(SHOW_SIGNATURE, false);
        config.uploadLocation = share.getBoolean(UPLOAD_LOCATION, false);
        config.showStatic = share.getBoolean(SHOW_STATIC, false);
        config.showReplyButton = share.getBoolean(SHOW_REPLYBUTTON, true);
        config.showColortxt = share.getBoolean(SHOW_COLORTXT, false);
        config.showNewweiba = share.getBoolean(SHOW_NEWWEIBA, false);
        config.showLajibankuai = share.getBoolean(SHOW_LAJIBANKUAI, true);
        config.imageQuality = share.getInt(DOWNLOAD_IMG_QUALITY_NO_WIFI, 0);
        config.HandSide = share.getInt(HANDSIDE, 0);
        config.fullscreen = share.getBoolean(FULLSCREENMODE, false);
        config.kitwebview = share.getBoolean(KITWEBVIEWMODE, false);
        config.blackgunsound = share.getInt(BLACKGUN_SOUND, 0);
        config.iconmode = share.getBoolean(SHOW_ICON_MODE, false);
        config.swipeBack = share.getBoolean(SWIPEBACK, true);
        config.swipeenablePosition = share.getInt(SWIPEBACKPOSITION, 2);
        config.materialMode = share.getBoolean(PreferenceKey.MATERIAL_MODE, true);

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

//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            int flag = share.getInt(UI_FLAG, 0);
//            if ((config.getUiFlag() & UI_FLAG_HA) != 0) {
//                flag = flag & ~UI_FLAG_HA;
//                Editor editor = share.edit();
//                editor.putInt(UI_FLAG, flag);
//                editor.apply();
//            }
//            PhoneConfiguration.getInstance().setUiFlag(flag);
//        } else {
//            int uiFlag = share.getInt(UI_FLAG, 0);
//            config.setUiFlag(uiFlag);
//        }
        config.setUiFlag(0);

        // bookmarks
        String bookmarkJson = share.getString(BOOKMARKS, "");
        List<Bookmark> bookmarks = new ArrayList<Bookmark>();
        try {
            if (!bookmarkJson.equals(""))
                bookmarks = JSON.parseArray(bookmarkJson, Bookmark.class);
        } catch (Exception e) {
            NLog.e("JSON_error", NLog.getStackTraceString(e));
        }
        PhoneConfiguration.getInstance().setBookmarks(bookmarks);

    }

    public boolean isNewVersion() {
        return newVersion;
    }

    public void setNewVersion(boolean newVersion) {
        this.newVersion = newVersion;
    }
}
