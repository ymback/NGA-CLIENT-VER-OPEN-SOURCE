package sp.phone.common;

import android.content.SharedPreferences;
import android.location.Location;
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
import sp.phone.utils.StringUtils;

public class PhoneConfiguration implements PreferenceKey {
    public String userName;
    public int nikeWidth = 100;
    public boolean downAvatarNoWifi;
    public boolean downImgNoWifi;
    public boolean iconmode;
    public boolean refresh_after_post_setting_mode = true;
    public int imageQuality = 0;    //0 = original, 1 = small, 2= medium, 3 = large
    public int swipeenablePosition = 2;    //0 = left, 1 = right, 2= L&R, 3 = L&R&B
    public int HandSide = 0;    //0 = right, 1 = left
    public int blackgunsound = 0;    //0 = right, 1 = left
    public boolean notification;
    public boolean notificationSound;
    public long lastMessageCheck = 0;
    public String cid;
    public String uid;
    public boolean showAnimation = false;
    public boolean showSignature = true;
    public boolean useViewCache;
    public Location location = null;
    public boolean uploadLocation = false;
    public boolean showStatic = false;
    public boolean showReplyButton = true;
    public boolean swipeBack = true;
    public boolean showColortxt = false;
    public boolean showNewweiba = false;
    public boolean showLajibankuai = true;
    public boolean fullscreen = false;
    public boolean kitwebview = false;
    public boolean materialMode;
    public int replytotalnum = 0;
    public String db_cookie;
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
    public Class<?> messageDetialActivity = MessageDetailActivity.class;
    public String replyString;
    private boolean refreshAfterPost;
    private float textSize;
    private int webSize;
    private int uiFlag = 0;

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

    public String getDb_Cookie() {
        return db_cookie;
    }

    public void setDb_Cookie(String db_cookie) {
        this.db_cookie = db_cookie;
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

    public boolean isMaterialMode() {
        return materialMode;
    }

    public void setMaterialMode(boolean materialMode) {
        this.materialMode = materialMode;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public void setNickname(String userName) {
        this.userName = userName;
    }

    public int getReplyTotalNum() {
        return replytotalnum;
    }

    public void setReplyTotalNum(int replytotalnum) {
        this.replytotalnum = replytotalnum;
    }

    public String getReplyString() {
        return replyString;
    }

    public void setReplyString(String replyString) {
        this.replyString = replyString;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
        if (!StringUtils.isEmpty(uid) && !StringUtils.isEmpty(cid)) {
            return "ngaPassportUid=" + uid +
                    "; ngaPassportCid=" + cid;
        }
        return "";
    }


    public int getUiFlag() {
        return uiFlag;
    }

    public void setUiFlag(int uiFlag) {
        this.uiFlag = uiFlag;
        topicActivityClass = TopicListActivity.class;
        articleActivityClass = ArticleListActivity.class;
        nonameArticleActivityClass = NonameArticleListActivity.class;
        messageDetialActivity = MessageDetailActivity.class;
        postActivityClass = PostActivity.class;
        signPostActivityClass = SignPostActivity.class;
        messagePostActivityClass = MessagePostActivity.class;
        signActivityClass = FlexibleSignActivity.class;
        profileActivityClass = FlexibleProfileActivity.class;
        recentReplyListActivityClass = RecentReplyListActivity.class;
        MeiziMainActivityClass = MeiziMainActivity.class;
        MeiziTopicActivityClass = MeiziTopicActivity.class;
        messageActivityClass = MessageListActivity.class;
        nonameActivityClass = FlexibleNonameTopicListActivity.class;
        nonamePostActivityClass = NonamePostActivity.class;
    }
}

