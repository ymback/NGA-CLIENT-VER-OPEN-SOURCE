package sp.phone.common;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

import gov.anzong.androidnga.activity.ArticleListActivity;
import gov.anzong.androidnga.activity.FlexibleNonameTopicListActivity;
import gov.anzong.androidnga.activity.FlexibleProfileActivity;
import gov.anzong.androidnga.activity.FlexibleSignActivity;
import gov.anzong.androidnga.activity.LoginActivity;
import gov.anzong.androidnga.activity.MessageDetailActivity;
import gov.anzong.androidnga.activity.MessageListActivity;
import gov.anzong.androidnga.activity.MessagePostActivity;
import gov.anzong.androidnga.activity.NonameArticleListActivity;
import gov.anzong.androidnga.activity.NonamePostActivity;
import gov.anzong.androidnga.activity.PostActivity;
import gov.anzong.androidnga.activity.RecentReplyListActivity;
import gov.anzong.androidnga.activity.SignPostActivity;
import gov.anzong.androidnga.activity.TopicListActivity;
import gov.anzong.meizi.MeiziMainActivity;
import gov.anzong.meizi.MeiziTopicActivity;
import sp.phone.utils.ApplicationContextHolder;

public class PhoneConfiguration implements PreferenceKey {
    public long lastMessageCheck = 0;
    public Class<?> topicActivityClass = TopicListActivity.class;
    public Class<?> articleActivityClass = ArticleListActivity.class;
    public Class<?> nonameArticleActivityClass = NonameArticleListActivity.class;
    public Class<?> postActivityClass = PostActivity.class;
    public Class<?> nonamePostActivityClass = NonamePostActivity.class;
    public Class<?> messagePostActivityClass = MessagePostActivity.class;
    public Class<?> signPostActivityClass = SignPostActivity.class;
    public Class<?> signActivityClass = FlexibleSignActivity.class;
    public Class<?> profileActivityClass = FlexibleProfileActivity.class;
    public Class<?> loginActivityClass = LoginActivity.class;
    public Class<?> recentReplyListActivityClass = RecentReplyListActivity.class;
    public Class<?> MeiziMainActivityClass = MeiziMainActivity.class;
    public Class<?> MeiziTopicActivityClass = MeiziTopicActivity.class;
    public Class<?> messageActivityClass = MessageListActivity.class;
    public Class<?> nonameActivityClass = FlexibleNonameTopicListActivity.class;
    public Class<?> messageDetailActivity = MessageDetailActivity.class;

    private Map<String, Boolean> mBooleanMap = new HashMap<>();

    private Map<String, Integer> mIntegerMap = new HashMap<>();

    private Map<String, String> mStringMap = new HashMap<>();

    private static class PhoneConfigurationHolder {

        private static PhoneConfiguration sInstance = new PhoneConfiguration();
    }

    private PhoneConfiguration() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ApplicationContextHolder.getContext());
        initBooleanMap(sp);
        initIntegerMap(sp);
        initStringMap(sp);
    }

    private void initBooleanMap(SharedPreferences sp) {
        mBooleanMap.put(PreferenceKey.SORT_BY_POST, sp.getBoolean(PreferenceKey.SORT_BY_POST, false));
        mBooleanMap.put(PreferenceKey.HARDWARE_ACCELERATED, sp.getBoolean(PreferenceKey.HARDWARE_ACCELERATED, true));
        mBooleanMap.put(PreferenceKey.BOTTOM_TAB, sp.getBoolean(PreferenceKey.BOTTOM_TAB, false));
        mBooleanMap.put(PreferenceKey.LEFT_HAND, sp.getBoolean(PreferenceKey.LEFT_HAND, false));
        mBooleanMap.put(PreferenceKey.FILTER_SUB_BOARD, sp.getBoolean(PreferenceKey.FILTER_SUB_BOARD, false));
        mBooleanMap.put(PreferenceKey.SHOW_REPLYBUTTON, sp.getBoolean(PreferenceKey.SHOW_REPLYBUTTON, true));
        mBooleanMap.put(PreferenceKey.SHOW_SIGNATURE, sp.getBoolean(PreferenceKey.SHOW_SIGNATURE, false));
        mBooleanMap.put(PreferenceKey.SHOW_COLORTXT, sp.getBoolean(PreferenceKey.SHOW_COLORTXT, false));
        mBooleanMap.put(PreferenceKey.FULLSCREENMODE, sp.getBoolean(PreferenceKey.FULLSCREENMODE, false));
        mBooleanMap.put(PreferenceKey.DOWNLOAD_AVATAR_NO_WIFI, sp.getBoolean(PreferenceKey.DOWNLOAD_AVATAR_NO_WIFI, false));
        mBooleanMap.put(PreferenceKey.REFRESH_AFTER_POST, sp.getBoolean(PreferenceKey.REFRESH_AFTER_POST, false));
        mBooleanMap.put(PreferenceKey.SWIPEBACK, sp.getBoolean(PreferenceKey.SWIPEBACK, true));
        mBooleanMap.put(PreferenceKey.ENABLE_NOTIFIACTION, sp.getBoolean(PreferenceKey.ENABLE_NOTIFIACTION, true));
        mBooleanMap.put(PreferenceKey.NOTIFIACTION_SOUND, sp.getBoolean(NOTIFIACTION_SOUND, true));
        mBooleanMap.put(PreferenceKey.SHOW_ICON_MODE, sp.getBoolean(PreferenceKey.SHOW_ICON_MODE, false));
        mBooleanMap.put(PreferenceKey.DOWNLOAD_IMG_NO_WIFI, sp.getBoolean(PreferenceKey.DOWNLOAD_IMG_NO_WIFI, false));
    }

    private void initIntegerMap(SharedPreferences sp) {
        mIntegerMap.put(PreferenceKey.MATERIAL_THEME, Integer.parseInt(sp.getString(PreferenceKey.MATERIAL_THEME, "0")));
        mIntegerMap.put(PreferenceKey.WEB_SIZE, sp.getInt(PreferenceKey.WEB_SIZE, 16));
        mIntegerMap.put(PreferenceKey.TEXT_SIZE, sp.getInt(PreferenceKey.TEXT_SIZE, 21));
        mIntegerMap.put(PreferenceKey.NICK_WIDTH, sp.getInt(PreferenceKey.NICK_WIDTH, 100));
        mIntegerMap.put(PreferenceKey.SWIPEBACKPOSITION, Integer.parseInt(sp.getString(PreferenceKey.SWIPEBACKPOSITION, "2")));
        mIntegerMap.put(PreferenceKey.BLACKGUN_SOUND, Integer.parseInt(sp.getString(PreferenceKey.BLACKGUN_SOUND, "0")));
    }

    private void initStringMap(SharedPreferences sp) {
        mStringMap.put(PreferenceKey.MEIZI_COOLIE, sp.getString(PreferenceKey.MEIZI_COOLIE, ""));
    }

    public void putData(String key, int data) {
        mIntegerMap.put(key, data);
    }

    public void putData(String key, String data) {
        mStringMap.put(key, data);
    }

    public void putData(String key, boolean data) {
        mBooleanMap.put(key, data);
    }

    public int getInt(String key) {
        return mIntegerMap.get(key);
    }

    public String getString(String key) {
        return mStringMap.get(key);
    }

    public boolean getBoolean(String key) {
        return mBooleanMap.get(key);
    }

    public static PhoneConfiguration getInstance() {
        return PhoneConfigurationHolder.sInstance;
    }

    public String getCookie() {
        return UserManagerImpl.getInstance().getCookie();
    }

}

