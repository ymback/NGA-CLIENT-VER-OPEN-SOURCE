package sp.phone.common;

import android.content.Context;
import android.content.SharedPreferences;

import gov.anzong.androidnga.activity.ArticleListActivity;
import gov.anzong.androidnga.activity.LoginActivity;
import gov.anzong.androidnga.activity.MessageDetailActivity;
import gov.anzong.androidnga.activity.MessagePostActivity;
import gov.anzong.androidnga.activity.PostActivity;
import gov.anzong.androidnga.activity.ProfileActivity;
import gov.anzong.androidnga.activity.SignPostActivity;
import gov.anzong.androidnga.activity.TopicListActivity;

public class PhoneConfiguration implements PreferenceKey, SharedPreferences.OnSharedPreferenceChangeListener {
    public Class<?> topicActivityClass = TopicListActivity.class;
    public Class<?> articleActivityClass = ArticleListActivity.class;
    public Class<?> postActivityClass = PostActivity.class;
    public Class<?> messagePostActivityClass = MessagePostActivity.class;
    public Class<?> signPostActivityClass = SignPostActivity.class;
    public Class<?> profileActivityClass = ProfileActivity.class;
    public Class<?> loginActivityClass = LoginActivity.class;
    public Class<?> messageDetialActivity = MessageDetailActivity.class;

    private boolean mNotificationEnabled;

    private boolean mNotificationSoundEnabled;

    private boolean mFullScreenMode;

    private boolean mDownAvatarNoWifi;

    private boolean mDownImgNoWifi;

    private boolean mShowSignature;

    private boolean mShowColorText;

    private boolean mUpdateAfterPost;

    private float mTopicTitleSize;

    private int mTopicContentSize;

    private int mAvatarWidth;

    private boolean mShowClassicIcon;

    private boolean mLeftHandMode;

    private boolean mShowBottomTab;

    private boolean mHardwareAcceleratedEnabled;

    private boolean mFilterSubBoard;

    private boolean mSortByPostOrder;

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        switch (key) {
            case PreferenceKey.NOTIFIACTION_SOUND:
                mNotificationSoundEnabled = sp.getBoolean(key, true);
                break;
            case PreferenceKey.ENABLE_NOTIFIACTION:
                mNotificationEnabled = sp.getBoolean(key, true);
                break;
            case PreferenceKey.FULLSCREENMODE:
                mFullScreenMode = sp.getBoolean(key, false);
                break;
            case PreferenceKey.DOWNLOAD_AVATAR_NO_WIFI:
                mDownAvatarNoWifi = sp.getBoolean(key, true);
                break;
            case PreferenceKey.DOWNLOAD_IMG_NO_WIFI:
                mDownImgNoWifi = sp.getBoolean(key, true);
                break;
            case PreferenceKey.SHOW_SIGNATURE:
                mShowSignature = sp.getBoolean(key, false);
                break;
            case PreferenceKey.SHOW_COLORTXT:
                mShowColorText = sp.getBoolean(key, false);
                break;
            case PreferenceKey.WEB_SIZE:
                mTopicContentSize = sp.getInt(key, 16);
                break;
            case PreferenceKey.TEXT_SIZE:
                mTopicTitleSize = sp.getFloat(key, Constants.DEFAULT_TEXT_SIZE);
                break;
            case PreferenceKey.REFRESH_AFTERPOST_SETTING_MODE:
                mUpdateAfterPost = sp.getBoolean(key, true);
                break;
            case PreferenceKey.NICK_WIDTH:
                mAvatarWidth = sp.getInt(key, 100);
                break;
            case PreferenceKey.SHOW_ICON_MODE:
                mShowClassicIcon = sp.getBoolean(key, false);
                break;
            case PreferenceKey.LEFT_HAND:
                mLeftHandMode = sp.getBoolean(key, false);
                break;
            case PreferenceKey.BOTTOM_TAB:
                mShowBottomTab = sp.getBoolean(key, false);
                break;
            case PreferenceKey.HARDWARE_ACCELERATED:
                mHardwareAcceleratedEnabled = sp.getBoolean(key, true);
                break;
            case PreferenceKey.FILTER_SUB_BOARD:
                mFilterSubBoard = sp.getBoolean(key, false);
                break;
            case PreferenceKey.SORT_BY_POST:
                mSortByPostOrder = sp.getBoolean(key, false);
                break;
            default:
                break;
        }

    }

    private static class PhoneConfigurationHolder {

        private static PhoneConfiguration sInstance = new PhoneConfiguration();
    }

    public static PhoneConfiguration getInstance() {
        return PhoneConfigurationHolder.sInstance;
    }


    private PhoneConfiguration() {
        initialize();
    }

    private void initialize() {
        SharedPreferences sp = ApplicationContextHolder.getContext().getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);
        sp.registerOnSharedPreferenceChangeListener(this);
        mNotificationSoundEnabled = sp.getBoolean(PreferenceKey.NOTIFIACTION_SOUND, true);
        mNotificationEnabled = sp.getBoolean(PreferenceKey.ENABLE_NOTIFIACTION, true);
        mFullScreenMode = sp.getBoolean(PreferenceKey.FULLSCREENMODE, false);
        mDownAvatarNoWifi = sp.getBoolean(PreferenceKey.DOWNLOAD_AVATAR_NO_WIFI, true);
        mDownImgNoWifi = sp.getBoolean(PreferenceKey.DOWNLOAD_IMG_NO_WIFI, true);
        mShowSignature = sp.getBoolean(PreferenceKey.SHOW_SIGNATURE, false);
        mShowColorText = sp.getBoolean(PreferenceKey.SHOW_COLORTXT, false);
        mTopicContentSize = sp.getInt(PreferenceKey.WEB_SIZE, 16);
        mTopicTitleSize = sp.getFloat(PreferenceKey.TEXT_SIZE, Constants.DEFAULT_TEXT_SIZE);
        mUpdateAfterPost = sp.getBoolean(PreferenceKey.REFRESH_AFTERPOST_SETTING_MODE, true);
        mAvatarWidth = sp.getInt(PreferenceKey.NICK_WIDTH, 100);
        mShowClassicIcon = sp.getBoolean(PreferenceKey.SHOW_ICON_MODE, false);
        mLeftHandMode = sp.getBoolean(PreferenceKey.LEFT_HAND, false);
        mShowBottomTab = sp.getBoolean(PreferenceKey.BOTTOM_TAB, false);
        mHardwareAcceleratedEnabled = sp.getBoolean(PreferenceKey.HARDWARE_ACCELERATED, true);
        mFilterSubBoard = sp.getBoolean(PreferenceKey.FILTER_SUB_BOARD, false);
        mSortByPostOrder = sp.getBoolean(PreferenceKey.SORT_BY_POST, false);
    }

    public boolean needSortByPostOrder() {
        return mSortByPostOrder;
    }

    public boolean isShowBottomTab() {
        return mShowBottomTab;
    }

    public boolean needFilterSubBoard() {
        return mFilterSubBoard;
    }

    public boolean isHardwareAcceleratedEnabled() {
        return mHardwareAcceleratedEnabled;
    }

    public boolean isLeftHandMode() {
        return mLeftHandMode;
    }

    public boolean needUpdateAfterPost() {
        return mUpdateAfterPost;
    }

    public boolean isShowClassicIcon() {
        return mShowClassicIcon;
    }

    public int getAvatarWidth() {
        return mAvatarWidth;
    }

    public float getTopicTitleSize() {
        return mTopicTitleSize;
    }

    public int getTopicContentSize() {
        return mTopicContentSize;
    }

    public boolean isShowSignature() {
        return mShowSignature;
    }

    public boolean isFullScreenMode() {
        return mFullScreenMode;
    }

    public boolean isShowColorText() {
        return mShowColorText;
    }

    public boolean isNotificationEnabled() {
        return mNotificationEnabled;
    }

    public boolean isNotificationSoundEnabled() {
        return mNotificationSoundEnabled;
    }

    public boolean isDownAvatarNoWifi() {
        return mDownAvatarNoWifi;
    }

    public boolean isDownImgNoWifi() {
        return mDownImgNoWifi;
    }

    public int getWebSize() {
        return getTopicContentSize();
    }

    public String getCookie() {
        return UserManagerImpl.getInstance().getCookie();
    }

}

