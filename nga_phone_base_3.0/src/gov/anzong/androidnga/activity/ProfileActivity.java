package gov.anzong.androidnga.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gov.anzong.androidnga.R;
import sp.phone.bean.ProfileData;
import sp.phone.bean.ReputationData;
import sp.phone.bean.adminForumsData;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.UserManager;
import sp.phone.common.UserManagerImpl;
import sp.phone.forumoperation.ParamKey;
import sp.phone.interfaces.OnProfileLoadFinishedListener;
import sp.phone.task.JsonProfileLoadTask;
import sp.phone.theme.ThemeManager;
import sp.phone.util.ActivityUtils;
import sp.phone.view.webview.WebViewClientEx;
import sp.phone.util.FunctionUtils;
import sp.phone.util.ImageUtils;
import sp.phone.util.StringUtils;
import sp.phone.view.webview.WebViewEx;

public class ProfileActivity extends SwipeBackAppCompatActivity implements OnProfileLoadFinishedListener {

    private static final String TAG = "ProfileActivity";

    private ProfileData mProfileData;

    private ThemeManager mThemeManager = ThemeManager.getInstance();

    private String mParams;

    private boolean mCurrentUser;

    @BindView(R.id.tv_user_money_copper)
    public TextView mMoneyCopperTv;

    @BindView(R.id.tv_user_money_silver)
    public TextView mMoneySilverTv;

    @BindView(R.id.tv_user_money_gold)
    public TextView mMoneyGoldTv;

    @BindView(R.id.tv_user_state)
    public TextView mUserStateTv;

    @BindView(R.id.tv_user_mute_time)
    public TextView mUserMuteTime;

    @BindView(R.id.tv_user_group)
    public TextView mUserGroupTv;

    @BindView(R.id.tv_user_register_time)
    public TextView mRegisterTimeTv;

    @BindView(R.id.tv_user_last_login_time)
    public TextView mLastLoginTv;

    @BindView(R.id.tv_user_title)
    public TextView mUserTitleTv;

    @BindView(R.id.tv_user_tel)
    public TextView mUserTelTv;

    @BindView(R.id.tv_user_email)
    public TextView mUserEmailTv;

    @BindView(R.id.tv_post_count)
    public TextView mPostCountTv;

    @BindView(R.id.btn_modify_sign)
    public TextView mModifySignBtn;

    @BindView(R.id.iv_avatar)
    public ImageView mAvatarIv;

    @BindView(R.id.toolbar_layout)
    public CollapsingToolbarLayout mToolbarLayout;

    @BindView(R.id.tv_uid)
    public TextView mUidTv;

    @BindView(R.id.wv_admin)
    public WebViewEx mAdminWebView;

    @BindView(R.id.wv_fame)
    public WebViewEx mFameWebView;

    @BindView(R.id.wv_sign)
    public WebViewEx mSignWebView;

    @Override
    protected void updateThemeUi() {
        setTheme(mThemeManager.getNoActionBarTheme());
        if (mThemeManager.isNightMode()) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    /**
     * 利用反射获取状态栏高度
     */
    public int getStatusBarHeight() {
        int result;
        Resources res = getResources();
        //获取状态栏高度的资源id
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        } else {
            result = res.getDimensionPixelSize(R.dimen.status_bar_height);
        }
        return result;
    }

    @Override
    protected void updateWindowFlag() {
        if (mConfig.isFullScreenMode()) {
            super.updateWindowFlag();
        } else {
            Window window = getWindow();
            View decorView = window.getDecorView();
            //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            int flag = WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
            if (mConfig.isHardwareAcceleratedEnabled()) {
                flag = flag | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
            }
            window.addFlags(flag);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void updateToolbarLayout() {
        if (!mConfig.isFullScreenMode()) {
            int statusBarHeight = getStatusBarHeight();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) toolbar.getLayoutParams();
            lp.setMargins(0, statusBarHeight, 0, 0);

            View parentView = (View) mAvatarIv.getParent();
            parentView.setPadding(0, statusBarHeight, 0, 0);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        UserManager um = UserManagerImpl.getInstance();
        if (intent.hasExtra("uid")) {
            String uid = intent.getStringExtra("uid");
            mCurrentUser = uid.equals(um.getUserId());
            mParams = "uid=" + uid;
        } else if (intent.hasExtra("username")) {
            String userName = intent.getStringExtra("username");
            mCurrentUser = userName.endsWith(um.getUserName());
            mParams = "username=" + StringUtils.encodeUrl(userName, "gbk");
        }

        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        setupActionBar();
        updateToolbarLayout();
        refresh();
    }

    private void refresh() {
        refreshSaying();
        JsonProfileLoadTask task = new JsonProfileLoadTask(this, this);
        task.execute(mParams);
    }// 读取JSON了

    private void refreshSaying() {
        ActivityUtils.getInstance().noticeSaying(this);
    }

    private void loadBasicProfile(ProfileData profileInfo) {
        String userName = profileInfo.get_username();
        mToolbarLayout.setTitle(userName);
        mUidTv.setText(String.format("用户ID : %s", profileInfo.get_uid()));
        mPostCountTv.setText(profileInfo.get_posts());
        if (profileInfo.get_hasemail()) {
            mUserEmailTv.setText(profileInfo.get_email());
        }
        if (profileInfo.get_hastel()) {
            mUserTelTv.setText(profileInfo.get_tel());
        }
        mUserGroupTv.setText(profileInfo.get_group());

        if (mCurrentUser) {
            mModifySignBtn.setVisibility(View.VISIBLE);
        } else {
            mModifySignBtn.setVisibility(View.GONE);
        }

        mRegisterTimeTv.setText(profileInfo.get_regdate());
        mLastLoginTv.setText(profileInfo.get_lastpost());
        mUserTitleTv.setText(profileInfo.get_title());

        handleAvatar(profileInfo);
        handleUserState(profileInfo);
        handleUserMoney(profileInfo);
    }

    private void handleUserState(ProfileData profileInfo) {
        int verified = Integer.parseInt(profileInfo.get_verified());
        if (verified > 0) {
            if (profileInfo.get_muteTime().equals("-1")) {
                mUserMuteTime.setVisibility(View.GONE);
                mUserStateTv.setText("已激活");
                mUserStateTv.setTextColor(mThemeManager.getActiveColor());
            } else {
                mUserMuteTime.setText(profileInfo.get_muteTime());
                mUserStateTv.setText("已禁言");
                mUserStateTv.setTextColor(mThemeManager.getMutedColor());
            }
        } else if (verified == 0) {
            mUserStateTv.setText("未激活(?)");
            mUserStateTv.setTextColor(mThemeManager.getInactiveColor());
            mUserMuteTime.setVisibility(View.GONE);
        } else if (verified == -1) {
            mUserStateTv.setText("NUKED(?)");
            mUserStateTv.setTextColor(mThemeManager.getNukedColor());
            if (profileInfo.get_muteTime().equals("-1")) {
                mUserMuteTime.setVisibility(View.GONE);
            } else {
                mUserMuteTime.setText(profileInfo.get_muteTime());
            }
        } else {
            mUserStateTv.setText("已禁言");
            mUserStateTv.setTextColor(mThemeManager.getMutedColor());
            if (profileInfo.get_muteTime().equals("-1")) {
                mUserMuteTime.setVisibility(View.GONE);
            } else {
                mUserMuteTime.setText(profileInfo.get_muteTime());
            }
        }
    }

    private void handleUserMoney(ProfileData profileInfo) {

        int money = Integer.parseInt(profileInfo.get_money());
        int gold = money / 10000;
        int silver = (money - gold * 10000) / 100;
        int copper = (money - gold * 10000 - silver * 100);
        mMoneyGoldTv.setText(String.valueOf(gold));
        mMoneySilverTv.setText(String.valueOf(silver));
        mMoneyCopperTv.setText(String.valueOf(copper));
    }

    protected String getUrl() {
        return "http://bbs.ngacn.cc/nuke.php?func=ucp&" + mParams;
    }

    private String createAdminHtml(ProfileData ret) {
        StringBuilder builder = new StringBuilder();
        List<adminForumsData> adminForumsEntryList = ret.get_adminForumsEntryList();
        for (int i = 0; i < ret.get_adminForumsEntryListrows(); i++) {
            builder.append("<a style=\"color:#551200;\" href=\"http://nga.178.com/thread.php?fid=")
                    .append(adminForumsEntryList.get(i).get_fid())
                    .append("\">[")
                    .append(adminForumsEntryList.get(i).get_fname()).append("]</a>&nbsp;");
        }
        if (builder.length() == 0) {
            builder.append("无管理板块");
        } else {
            builder.append("<br>");
        }
        return builder.toString();
    }

    @OnClick(R.id.btn_modify_sign)
    public void startChangeSignActivity() {
        Intent intent = new Intent();
        intent.putExtra("prefix", mProfileData.get_sign());
        intent.setClass(this, PhoneConfiguration.getInstance().signPostActivityClass);
        startActivityForResult(intent, 321);
    }

    private void loadProfileInfo(ProfileData profileInfo) {
        loadBasicProfile(profileInfo);
        handleSignWebView(mSignWebView, profileInfo);
        handleAdminWebView(mAdminWebView, profileInfo);
        handleFameWebView(mFameWebView, profileInfo);
    }


    private String createFameHtml(ProfileData ret, String color) {
        StringBuilder builder = new StringBuilder("<ul style=\"padding: 0px; margin: 0px;\">");
        String fame = ret.get_fame();
        double fameCount = Double.parseDouble(fame) / 10;
        builder.append("<li style=\"display: block;float: left;width: 33%;\">")
                .append("<label style=\"float: left;color: ").append(color).append(";\">威望</label>")
                .append("<span style=\"float: left; color: #808080;\">:</span>")
                .append("<span style=\"float: left; color: #808080;\">")
                .append(Double.toString(fameCount)).append("</span></li>");
        List<ReputationData> reputationEntryList = ret.get_ReputationEntryList();
        for (int i = 0; i < ret.get_ReputationEntryListrows(); i++) {
            builder.append("<li style=\"display: block;float: left;width: 33%;\">")
                    .append("<label style=\"float: left;color: ").append(color).append(";\">")
                    .append(reputationEntryList.get(i).get_name()).append("</label>")
                    .append("<span style=\"float: left; color: #808080;\">:</span>")
                    .append("<span style=\"float: left; color: #808080;\">")
                    .append(reputationEntryList.get(i).get_data()).append("</span></li>");
        }
        builder.append("</ul><br>");
        return builder.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mCurrentUser) {
            menu.findItem(R.id.menu_send_message).setVisible(false);
        } else {
            menu.findItem(R.id.menu_modify_avatar).setVisible(false);
        }
        menu.findItem(R.id.menu_copy_url).setVisible(true);
        menu.findItem(R.id.menu_open_by_browser).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_send_message:
                sendShortMessage();
                break;
            case R.id.menu_search_post:
                searchPost();
                break;
            case R.id.menu_search_reply:
                searchReply();
                break;
            case R.id.menu_modify_avatar:
                startModifyAvatar();
                break;
            case R.id.menu_copy_url:
                FunctionUtils.copyToClipboard(this, getUrl());
                break;
            case R.id.menu_open_by_browser:
                FunctionUtils.openUrlByDefaultBrowser(this, getUrl());
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }

    private void startModifyAvatar() {
        Intent intent = new Intent();
        intent.putExtra("prefix", mProfileData.get_sign());
        intent.setClass(this, AvatarPostActivity.class);
        startActivity(intent);
    }

    private void sendShortMessage() {
        Intent intent = new Intent();
        intent.putExtra("to", mProfileData.get_username());
        intent.putExtra("action", "new");
        intent.putExtra("messagemode", "yes");
        intent.setClass(this, PhoneConfiguration.getInstance().messagePostActivityClass);
        startActivity(intent);
    }

    private void searchPost() {
        Intent intent = new Intent(this, PhoneConfiguration.getInstance().topicActivityClass);
        intent.putExtra(ParamKey.KEY_AUTHOR_ID, Integer.parseInt(mProfileData.get_uid()));
        intent.putExtra(ParamKey.KEY_AUTHOR, mProfileData.get_username());
        startActivity(intent);
    }

    private void searchReply() {
        Intent intent = new Intent(this, PhoneConfiguration.getInstance().topicActivityClass);
        intent.putExtra(ParamKey.KEY_AUTHOR_ID, Integer.parseInt(mProfileData.get_uid()));
        intent.putExtra(ParamKey.KEY_SEARCH_POST, 1);
        intent.putExtra(ParamKey.KEY_AUTHOR, mProfileData.get_username());
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 321) {
            String signData = data.getStringExtra("sign");
            mProfileData.set_sign(signData);
            mSignWebView.requestLayout();
            handleSignWebView(mSignWebView, mProfileData);
        }
        if (resultCode == 123) {
            String avatarData = data.getStringExtra("avatar");
            mProfileData.set_avatar(avatarData);
            mSignWebView.requestLayout();
            //  handleAvatar(avatarImage, mProfileData);
        }
    }

    private void handleSignWebView(WebView contentTV, ProfileData ret) {
        ThemeManager theme = ThemeManager.getInstance();
        int bgColor, fgColor = getResources().getColor(theme.getForegroundColor());
        if (mThemeManager.isNightMode()) {
            bgColor = getResources().getColor(theme.getBackgroundColor(0));
        } else {
            bgColor = getResources().getColor(R.color.profilebgcolor);
        }
        bgColor = bgColor & 0xffffff;
        final String bgcolorStr = String.format("%06x", bgColor);

        int htmlfgColor = fgColor & 0xffffff;
        final String fgColorStr = String.format("%06x", htmlfgColor);

        WebViewClient client = new WebViewClientEx(this);
        contentTV.setBackgroundColor(0);
        contentTV.setFocusableInTouchMode(false);
        contentTV.setFocusable(false);
        contentTV.setLongClickable(false);
        WebSettings setting = contentTV.getSettings();
        setting.setDefaultFontSize(PhoneConfiguration.getInstance()
                .getWebSize());
        setting.setJavaScriptEnabled(false);
        contentTV.setWebViewClient(client);
        contentTV.loadDataWithBaseURL(
                null,
                signatureToHtmlText(ret, FunctionUtils.isShowImage(), FunctionUtils.showImageQuality(), fgColorStr, bgcolorStr),
                "text/html", "utf-8", null);
    }

    private void handleAdminWebView(WebView contentTV, ProfileData ret) {
        int bgColor, fgColor;
        ThemeManager theme = ThemeManager.getInstance();
        if (mThemeManager.isNightMode()) {
            bgColor = getResources().getColor(theme.getBackgroundColor(0));
            fgColor = getResources().getColor(theme.getForegroundColor());
        } else {
            bgColor = getResources().getColor(R.color.profilebgcolor);
            fgColor = getResources().getColor(R.color.profilefcolor);
        }
        bgColor = bgColor & 0xffffff;
        final String bgcolorStr = String.format("%06x", bgColor);

        int htmlfgColor = fgColor & 0xffffff;
        final String fgColorStr = String.format("%06x", htmlfgColor);

        WebViewClient client = new WebViewClientEx(this);
        contentTV.setBackgroundColor(0);
        contentTV.setFocusableInTouchMode(false);
        contentTV.setFocusable(false);
        contentTV.setLongClickable(false);
        WebSettings setting = contentTV.getSettings();
        setting.setDefaultFontSize(PhoneConfiguration.getInstance()
                .getWebSize());
        setting.setJavaScriptEnabled(false);
        contentTV.setWebViewClient(client);
        contentTV.loadDataWithBaseURL(
                null,
                adminToHtmlText(ret, fgColorStr, bgcolorStr), "text/html", "utf-8", null);
    }

    private void handleFameWebView(WebView contentTV, ProfileData ret) {
        int bgColor, fgColor;
        ThemeManager theme = ThemeManager.getInstance();
        if (mThemeManager.isNightMode()) {
            bgColor = getResources().getColor(theme.getBackgroundColor(0));
            fgColor = getResources().getColor(theme.getForegroundColor());
        } else {
            bgColor = getResources().getColor(R.color.profilebgcolor);
            fgColor = getResources().getColor(R.color.profilefcolor);
        }
        bgColor = bgColor & 0xffffff;
        final String bgcolorStr = String.format("%06x", bgColor);

        int htmlfgColor = fgColor & 0xffffff;
        final String fgColorStr = String.format("%06x", htmlfgColor);

        WebViewClient client = new WebViewClientEx(this);
        contentTV.setBackgroundColor(0);
        contentTV.setFocusableInTouchMode(false);
        contentTV.setFocusable(false);
        contentTV.setLongClickable(false);
        WebSettings setting = contentTV.getSettings();
        setting.setDefaultFontSize(PhoneConfiguration.getInstance()
                .getWebSize());
        setting.setJavaScriptEnabled(false);
        contentTV.setWebViewClient(client);
        contentTV.loadDataWithBaseURL(
                null,
                fameToHtmlText(ret, fgColorStr, bgcolorStr), "text/html", "utf-8", null);
    }

    public String fameToHtmlText(final ProfileData ret, final String fgColorStr, final String bgcolorStr) {
        String color = "#121C46";
        if (mThemeManager.isNightMode()) {
            color = "#712D08";
        }
        String ngaHtml = createFameHtml(ret, color);
        ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">"
                + "<body bgcolor= '#"
                + bgcolorStr
                + "'>"
                + "<font color='#"
                + fgColorStr + "' size='2'>" + ngaHtml + "</font></body>";

        return ngaHtml;
    }

    public String adminToHtmlText(final ProfileData ret, final String fgColorStr, final String bgcolorStr) {
        String ngaHtml = createAdminHtml(ret);
        ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">"
                + "<body bgcolor= '#"
                + bgcolorStr
                + "'>"
                + "<font color='#"
                + fgColorStr + "' size='2'>" + ngaHtml + "</font></body>";

        return ngaHtml;
    }

    public String signatureToHtmlText(final ProfileData ret, boolean showImage,
                                      int imageQuality, final String fgColorStr, final String bgcolorStr) {
        HashSet<String> imageURLSet = new HashSet<String>();
        String ngaHtml = StringUtils.decodeForumTag(ret.get_sign(), showImage,
                imageQuality, imageURLSet);
        ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">"
                + "<body bgcolor= '#"
                + bgcolorStr
                + "'>"
                + "<font color='#"
                + fgColorStr
                + "' size='2'>"
                + "<div style=\"border: 3px solid rgb(204, 204, 204);padding: 2px; \">"
                + ngaHtml + "</div>" + "</font></body>";

        return ngaHtml;
    }

    private void handleAvatar(ProfileData row) {
        final String avatarUrl = FunctionUtils.parseAvatarUrl(row.get_avatar());//
        ImageUtils.loadRoundCornerAvatar(mAvatarIv, avatarUrl);
        ImageUtils.loadDefaultAvatar((ImageView) findViewById(R.id.iv_toolbar_layout_bg), avatarUrl);

    }

    @Override
    public void jsonfinishLoad(ProfileData result) {
        mProfileData = result;
        if (result != null) {
            loadProfileInfo(result);
        }
    }
}
