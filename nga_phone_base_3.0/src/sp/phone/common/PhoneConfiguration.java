package sp.phone.common;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

import gov.anzong.androidnga.activity.ArticleListActivity;
import gov.anzong.androidnga.activity.FlexibleProfileActivity;
import gov.anzong.androidnga.activity.LoginActivity;
import gov.anzong.androidnga.activity.MessageDetailActivity;
import gov.anzong.androidnga.activity.MessageListActivity;
import gov.anzong.androidnga.activity.MessagePostActivity;
import gov.anzong.androidnga.activity.PostActivity;
import gov.anzong.androidnga.activity.RecentReplyListActivity;
import gov.anzong.androidnga.activity.SignPostActivity;
import gov.anzong.androidnga.activity.TopicListActivity;
import noname.activity.FlexibleNonameTopicListActivity;
import noname.activity.NonameArticleListActivity;
import noname.activity.NonamePostActivity;
import sp.phone.utils.ApplicationContextHolder;

public class PhoneConfiguration implements PreferenceKey {
    public int nikeWidth = 100;
    public boolean downAvatarNoWifi;
    public boolean downImgNoWifi;
    public boolean iconmode;
    public boolean refresh_after_post_setting_mode = true;
    public int blackgunsound = 0;    //0 = right, 1 = left
    public boolean notification;
    public boolean notificationSound;
    public long lastMessageCheck = 0;
    public boolean showSignature = true;
    public boolean showColortxt = false;
    public boolean fullscreen = false;
    public Class<?> topicActivityClass = TopicListActivity.class;
    public Class<?> articleActivityClass = ArticleListActivity.class;
    public Class<?> nonameArticleActivityClass = NonameArticleListActivity.class;
    public Class<?> postActivityClass = PostActivity.class;
    public Class<?> nonamePostActivityClass = NonamePostActivity.class;
    public Class<?> messagePostActivityClass = MessagePostActivity.class;
    public Class<?> signPostActivityClass = SignPostActivity.class;
    public Class<?> profileActivityClass = FlexibleProfileActivity.class;
    public Class<?> loginActivityClass = LoginActivity.class;
    public Class<?> recentReplyListActivityClass = RecentReplyListActivity.class;
    public Class<?> messageActivityClass = MessageListActivity.class;
    public Class<?> nonameActivityClass = FlexibleNonameTopicListActivity.class;
    public Class<?> messageDetialActivity = MessageDetailActivity.class;
    private boolean refreshAfterPost;
    private float textSize;
    private int webSize;

    private Map<String, Boolean> mBooleanMap = new HashMap<>();

    private Map<String, Integer> mIntegerMap = new HashMap<>();

    private static class PhoneConfigurationHolder {

        private static PhoneConfiguration sInstance = new PhoneConfiguration();
    }

    private PhoneConfiguration() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ApplicationContextHolder.getContext());
        initBooleanMap(sp);
        initIntegerMap(sp);
    }

    private void initBooleanMap(SharedPreferences sp) {
        mBooleanMap.put(PreferenceKey.SORT_BY_POST, sp.getBoolean(PreferenceKey.SORT_BY_POST, false));
        mBooleanMap.put(PreferenceKey.HARDWARE_ACCELERATED, sp.getBoolean(PreferenceKey.HARDWARE_ACCELERATED, true));
        mBooleanMap.put(PreferenceKey.BOTTOM_TAB, sp.getBoolean(PreferenceKey.BOTTOM_TAB, false));
        mBooleanMap.put(PreferenceKey.LEFT_HAND, sp.getBoolean(PreferenceKey.LEFT_HAND, false));
        mBooleanMap.put(PreferenceKey.FILTER_SUB_BOARD, sp.getBoolean(PreferenceKey.FILTER_SUB_BOARD, false));
    }

    private void initIntegerMap(SharedPreferences sp) {
        mIntegerMap.put(PreferenceKey.MATERIAL_THEME, Integer.parseInt(sp.getString(PreferenceKey.MATERIAL_THEME, "0")));
    }

    public void putData(String key, int data) {
        mIntegerMap.put(key, data);
    }

    public int getInt(String key) {
        return mIntegerMap.get(key);
    }

    public void putData(String key, boolean data) {
        mBooleanMap.put(key, data);
    }

    public boolean getBoolean(String key) {
        return mBooleanMap.get(key);
    }

    public static PhoneConfiguration getInstance() {
        return PhoneConfigurationHolder.sInstance;
    }

    public int getNikeWidth() {
        return nikeWidth;
    }

    public boolean isDownAvatarNoWifi() {
        return downAvatarNoWifi;
    }

    public void setDownAvatarNoWifi(boolean downAvatarNoWifi) {
        this.downAvatarNoWifi = downAvatarNoWifi;
    }

    public boolean isDownImgNoWifi() {
        return downImgNoWifi;
    }

    public void setDownImgNoWifi(boolean downImgNoWifi) {
        this.downImgNoWifi = downImgNoWifi;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getWebSize() {
        return webSize;
    }

    public void setWebSize(int webSize) {
        this.webSize = webSize;
    }

    public boolean isRefreshAfterPost() {
        return refreshAfterPost;
    }

    public void setRefreshAfterPost(boolean refreshAfterPost) {
        this.refreshAfterPost = refreshAfterPost;
    }

    public String getCookie() {
        return UserManagerImpl.getInstance().getCookie();
    }

}

