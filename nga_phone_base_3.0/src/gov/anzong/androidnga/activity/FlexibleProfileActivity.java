package gov.anzong.androidnga.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;

import gov.anzong.androidnga.R;
import sp.phone.bean.AvatarTag;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.ProfileData;
import sp.phone.bean.ReputationData;
import sp.phone.bean.adminForumsData;
import sp.phone.interfaces.AvatarLoadCompleteCallBack;
import sp.phone.interfaces.OnProfileLoadFinishedListener;
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.task.AvatarLoadTask;
import sp.phone.task.JsonProfileLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ArticleListWebClient;
import sp.phone.utils.FunctionUtil;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;

public class FlexibleProfileActivity extends SwipeBackAppCompatActivity
        implements OnProfileLoadFinishedListener, AvatarLoadCompleteCallBack, PullToRefreshAttacherOnwer,
        PerferenceConstant {
    private static final String TAG = "FlexibleProfileActivity";
    private final Object lock = new Object();
    private final HashSet<String> urlSet = new HashSet<String>();
    ProfileData resulttmp;
    PullToRefreshAttacher attacher = null;
    ThemeManager tm = ThemeManager.getInstance();
    private String mode, params, trueusername;
    private View view;
    @SuppressWarnings("unused")
    private Object mActionModeCallback = null;
    private TextView basedata_title, user_id, user_name, user_email_title,
            user_email, user_tel_title, user_tel, user_group, user_posttotal, message_title;
    private TextView user_money_gold, user_money_silver, user_money_copper,
            user_title, user_state, user_registertime, user_lastlogintime;
    @SuppressWarnings("unused")
    private ImageView avatargold, avatarsilver, avatarcopper, avatarImage;
    private TextView avatar_title, sign_title, admin_title, fame_title,
            search_title, admin2_title, fame2_title, user_shutup_title,
            user_shutup, iplog_title, iplog;
    private WebView signwebview, adminwebview, famewebview;
    private Button topic_button, reply_button, message_button;
    private RelativeLayout avahahahb, iplogrelativelayout;
    private TextView change_avatar_button, change_sign_button;
    private PullToRefreshAttacher mPullToRefreshAttacher;
    private Bitmap defaultAvatar = null;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        activeActionMode();
        Intent intent = this.getIntent();
        mode = intent.getStringExtra("mode");
        if (!StringUtil.isEmpty(mode)) {
            if (mode.equals("uid")) {
                params = "uid=" + intent.getStringExtra("uid");
            } else {
                params = "username="
                        + StringUtil.encodeUrl(
                        intent.getStringExtra("username"), "gbk");
            }
        } else {
            params = "uid=0";
        }
        if (tm.getMode() == ThemeManager.MODE_NIGHT) {
            this.setContentView(R.layout.profile_night);
        } else {
            this.setContentView(R.layout.profile);
        }
        getSupportActionBar().setTitle("用户信息");
        view = findViewById(R.id.scroll_profile);
        view.setVisibility(View.GONE);
        basedata_title = (TextView) view.findViewById(R.id.basedata_title);
        user_id = (TextView) view.findViewById(R.id.user_id);
        user_name = (TextView) view.findViewById(R.id.user_name);
        user_email_title = (TextView) view.findViewById(R.id.user_email_title);
        avahahahb = (RelativeLayout) view.findViewById(R.id.avahahahb);
        iplogrelativelayout = (RelativeLayout) view.findViewById(R.id.iplogrelativelayout);
        user_email = (TextView) view.findViewById(R.id.user_email);
        user_tel_title = (TextView) view.findViewById(R.id.user_tel_title);
        user_tel = (TextView) view.findViewById(R.id.user_tel);
        user_group = (TextView) view.findViewById(R.id.user_group);
        user_posttotal = (TextView) view.findViewById(R.id.user_posttotal);
        message_title = (TextView) view.findViewById(R.id.message_title);
        user_money_gold = (TextView) view.findViewById(R.id.user_money_gold);
        user_money_silver = (TextView) view
                .findViewById(R.id.user_money_silver);
        user_money_copper = (TextView) view
                .findViewById(R.id.user_money_copper);
        user_title = (TextView) view.findViewById(R.id.user_title);
        user_state = (TextView) view.findViewById(R.id.user_state);
        user_registertime = (TextView) view
                .findViewById(R.id.user_registertime);
        user_lastlogintime = (TextView) view
                .findViewById(R.id.user_lastlogintime);
        avatar_title = (TextView) view.findViewById(R.id.avatar_title);
        sign_title = (TextView) view.findViewById(R.id.sign_title);
        admin_title = (TextView) view.findViewById(R.id.admin_title);
        admin2_title = (TextView) view.findViewById(R.id.admin2_title);
        fame_title = (TextView) view.findViewById(R.id.fame_title);
        fame2_title = (TextView) view.findViewById(R.id.fame2_title);
        search_title = (TextView) view.findViewById(R.id.search_title);
        avatargold = (ImageView) view.findViewById(R.id.avatargold);
        avatarsilver = (ImageView) view.findViewById(R.id.avatarsilver);
        avatarcopper = (ImageView) view.findViewById(R.id.avatarcopper);
        avatarImage = (ImageView) view.findViewById(R.id.avatarImage);
        signwebview = (WebView) view.findViewById(R.id.signwebview);
        adminwebview = (WebView) view.findViewById(R.id.adminwebview);
        famewebview = (WebView) view.findViewById(R.id.famewebview);
        topic_button = (Button) view.findViewById(R.id.topic_button);
        reply_button = (Button) view.findViewById(R.id.reply_button);
        message_button = (Button) view.findViewById(R.id.message_button);
        change_sign_button = (TextView) view
                .findViewById(R.id.change_sign_button);
        change_avatar_button = (TextView) view
                .findViewById(R.id.change_avatar_button);
        user_shutup_title = (TextView) view
                .findViewById(R.id.user_shutup_title);
        user_shutup = (TextView) view.findViewById(R.id.user_shutup);
        iplog_title = (TextView) view.findViewById(R.id.iplog_title);
        iplog = (TextView) view.findViewById(R.id.iplog);
        PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
        options.refreshScrollDistance = 0.3f;
        options.refreshOnUp = true;
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this, options);
        try {
            PullToRefreshAttacherOnwer attacherOnwer = (PullToRefreshAttacherOnwer) this;
            attacher = attacherOnwer.getAttacher();

        } catch (ClassCastException e) {
            Log.e(TAG,
                    "father activity should implement PullToRefreshAttacherOnwer");
        }
        refresh();
    }

    void refresh() {
        JsonProfileLoadTask task = new JsonProfileLoadTask(this, this);
        if (PhoneConfiguration.getInstance().fullscreen) {
            refresh_saying();
        } else {
            ActivityUtil.getInstance().noticeSaying(this);
        }
        task.execute(params);
    }// 读取JSON了

    private void refresh_saying() {
        DefaultHeaderTransformer transformer = null;

        if (attacher != null) {
            uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.HeaderTransformer headerTransformer;
            headerTransformer = attacher.getHeaderTransformer();
            if (headerTransformer != null
                    && headerTransformer instanceof DefaultHeaderTransformer)
                transformer = (DefaultHeaderTransformer) headerTransformer;
        } else {
        }

        if (transformer == null)
            ActivityUtil.getInstance().noticeSaying(this);
        else
            transformer.setRefreshingText(ActivityUtil.getSaying());
        if (attacher != null)
            attacher.setRefreshing(true);
    }

    void writetopage(final ProfileData ret) {
        final String username = ret.get_username();
        final String iplogdata = ret.get_iplog();
        trueusername = username;
        getSupportActionBar().setTitle(username + "的用户信息");
        basedata_title.setText(":: " + username + " 的基础信息 ::");
        avatar_title.setText(":: " + username + " 的头像 ::");
        sign_title.setText(":: " + username + " 的签名 ::");
        admin_title.setText(":: " + username + " 的管理权限 ::");
        admin2_title.setText(username + " 拥有管理员权限 所属版面的主题管理权限 在以下版面担任版主 ");
        fame_title.setText(":: " + username + " 的声望 ::");
        fame2_title.setText("表示 论坛/某版面/某用户 对 " + username + " 的关系");
        search_title.setText(":: " + username + " 发布的贴子  ::");
        topic_button.setText("[搜索 " + username + " 发布的主题]");
        reply_button.setText("[搜索 " + username + " 发布的回复]");
        if (StringUtil.isEmpty(iplogdata)) {
            iplog_title.setVisibility(View.GONE);
            iplog.setVisibility(View.GONE);
            iplogrelativelayout.setVisibility(View.GONE);
        } else {
            iplog_title.setVisibility(View.VISIBLE);
            iplog.setVisibility(View.VISIBLE);
            iplogrelativelayout.setVisibility(View.VISIBLE);
            iplog_title.setText(":: " + username + " 的IP ::");
            iplog.setText(Html.fromHtml(iplogdata));
        }
        if (PhoneConfiguration.getInstance().userName == null) {
            PhoneConfiguration.getInstance().userName = "";
        }
        if (!PhoneConfiguration.getInstance().userName.equals(username)) {
            message_button.setText("[向 " + username + " 发送论坛短消息]");
            message_button.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    Intent intent_bookmark = new Intent();
                    intent_bookmark.putExtra("to", username);
                    intent_bookmark.putExtra("action", "new");
                    intent_bookmark.putExtra("messagemode", "yes");
                    if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
                        intent_bookmark
                                .setClass(view.getContext(),
                                        PhoneConfiguration.getInstance().messagePostActivityClass);
                    } else {
                        intent_bookmark.setClass(view.getContext(),
                                PhoneConfiguration.getInstance().loginActivityClass);
                    }
                    startActivity(intent_bookmark);
                }

            });
        } else {
            avahahahb.setVisibility(View.GONE);
            message_title.setVisibility(View.GONE);
            change_avatar_button.setVisibility(View.VISIBLE);
            change_sign_button.setVisibility(View.VISIBLE);

            change_sign_button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    Intent intent_bookmark = new Intent();
                    intent_bookmark.putExtra("prefix", ret.get_sign());

                    intent_bookmark
                            .setClass(view.getContext(),
                                    PhoneConfiguration.getInstance().signPostActivityClass);
                    startActivityForResult(intent_bookmark, 321);
                }

            });

            change_avatar_button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    Intent intent_bookmark = new Intent();
                    intent_bookmark.putExtra("prefix", ret.get_sign());

                    intent_bookmark
                            .setClass(view.getContext(), AvatarPostActivity.class);
                    startActivityForResult(intent_bookmark, 123);
                }

            });
        }
        user_id.setText(ret.get_uid());
        user_name.setText(username);
        if (ret.get_hasemail()) {
            user_email.setText(ret.get_email());
        } else {
            user_email.setVisibility(View.GONE);
            user_email_title.setVisibility(View.GONE);
        }
        if (ret.get_hastel()) {
            user_tel.setText(ret.get_tel());
        } else {
            user_tel.setVisibility(View.GONE);
            user_tel_title.setVisibility(View.GONE);
        }
        user_group.setText(ret.get_group());
        user_posttotal.setText(ret.get_posts());
        if (ret.get_money().equals("0")) {
            user_money_gold.setVisibility(View.GONE);
            avatargold.setVisibility(View.GONE);
            user_money_silver.setVisibility(View.GONE);
            avatarsilver.setVisibility(View.GONE);
            user_money_copper.setText("0");
        } else {
            int moneytotal = Integer.parseInt(ret.get_money());
            int moneygold = (int) moneytotal / 10000;
            int moneysilver = (int) (moneytotal - moneygold * 10000) / 100;
            int moneycopper = (int) (moneytotal - moneygold * 10000 - moneysilver * 100);
            if (moneygold > 0) {
                user_money_gold.setText(String.valueOf(moneygold));
                user_money_silver.setText(String.valueOf(moneysilver));
                user_money_copper.setText(String.valueOf(moneycopper));
            } else {
                if (moneysilver > 0) {
                    user_money_gold.setVisibility(View.GONE);
                    avatargold.setVisibility(View.GONE);
                    user_money_silver.setText(String.valueOf(moneysilver));
                    user_money_copper.setText(String.valueOf(moneycopper));
                } else {
                    user_money_gold.setVisibility(View.GONE);
                    avatargold.setVisibility(View.GONE);
                    user_money_silver.setVisibility(View.GONE);
                    avatarsilver.setVisibility(View.GONE);
                    user_money_copper.setText(String.valueOf(moneycopper));
                }
            }
        }
        int verified = Integer.parseInt(ret.get_verified());
        if (verified > 0) {
            if (ret.get_muteTime().equals("-1")) {
                user_shutup_title.setVisibility(View.GONE);
                user_shutup.setVisibility(View.GONE);
                user_state.setText("已激活");
                if (tm.getMode() != ThemeManager.MODE_NIGHT) {
                    user_state.setTextColor(this.getResources().getColor(
                            R.color.activecolor));
                }
            } else {
                user_shutup.setText(ret.get_muteTime());
                if (tm.getMode() != ThemeManager.MODE_NIGHT) {
                    user_shutup.setTextColor(this.getResources().getColor(
                            R.color.mutedcolor));
                }
                user_state.setText("已禁言");
                if (tm.getMode() != ThemeManager.MODE_NIGHT) {
                    user_state.setTextColor(this.getResources().getColor(
                            R.color.mutedcolor));
                }
            }
        } else if (verified == 0) {
            user_state.setText("未激活(?)");
            if (tm.getMode() != ThemeManager.MODE_NIGHT) {
                user_state.setTextColor(this.getResources().getColor(
                        R.color.unactivecolor));
            }
            user_shutup_title.setVisibility(View.GONE);
            user_shutup.setVisibility(View.GONE);
        } else if (verified == -1) {
            user_state.setText("NUKED(?)");
            if (tm.getMode() != ThemeManager.MODE_NIGHT) {
                user_state.setTextColor(this.getResources().getColor(
                        R.color.nukedcolor));
            }
            if (ret.get_muteTime().equals("-1")) {
                user_shutup_title.setVisibility(View.GONE);
                user_shutup.setVisibility(View.GONE);
            } else {
                user_shutup.setText(ret.get_muteTime());
                if (tm.getMode() != ThemeManager.MODE_NIGHT) {
                    user_shutup.setTextColor(this.getResources().getColor(
                            R.color.mutedcolor));
                }
            }
        } else {
            user_state.setText("已禁言");
            if (tm.getMode() != ThemeManager.MODE_NIGHT) {
                user_state.setTextColor(this.getResources().getColor(
                        R.color.mutedcolor));
            }
            if (ret.get_muteTime().equals("-1")) {
                user_shutup_title.setVisibility(View.GONE);
                user_shutup.setVisibility(View.GONE);
            } else {
                user_shutup.setText(ret.get_muteTime());
                if (tm.getMode() != ThemeManager.MODE_NIGHT) {
                    user_shutup.setTextColor(this.getResources().getColor(
                            R.color.mutedcolor));
                }
            }
        }
        user_title.setText(ret.get_title());
        user_registertime.setText(ret.get_regdate());
        user_lastlogintime.setText(ret.get_lastpost());
        handleAvatar(avatarImage, ret);
        handleSignWebview(signwebview, ret);
        handleadminWebview(adminwebview, ret);
        handlefameWebview(famewebview, ret);
        topic_button.setOnClickListener(new OnClickListener() {

            Intent intent_search = new Intent(view.getContext(),
                    PhoneConfiguration.getInstance().topicActivityClass);

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                intent_search.putExtra("fid", "-7");
                intent_search.putExtra("author", ret.get_username());
                intent_search.putExtra("authorid", ret.get_uid());
                startActivity(intent_search);
            }

        });

        reply_button.setOnClickListener(new OnClickListener() {

            Intent intent_search = new Intent(view.getContext(),
                    PhoneConfiguration.getInstance().topicActivityClass);

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                intent_search.putExtra("fid", "-7");
                intent_search.putExtra("author", ret.get_username()
                        + "&searchpost=1");
                intent_search.putExtra("authorid", ret.get_uid());
                startActivity(intent_search);
            }

        });


        view.setVisibility(View.VISIBLE);
    }

    private String createHTMLofadmin(ProfileData ret) {
        int i;
        String rest = "";
        List<adminForumsData> adminForumsEntryList = ret
                .get_adminForumsEntryList();
        for (i = 0; i < ret.get_adminForumsEntryListrows(); i++) {
            rest += "<a style=\"color:#551200;\" href=\"http://nga.178.com/thread.php?fid="
                    + adminForumsEntryList.get(i).get_fid()
                    + "\">["
                    + adminForumsEntryList.get(i).get_fname() + "]</a>&nbsp;";
        }
        if (rest == "") {
            return "无管理板块";
        } else {
            return rest + "<br>";
        }
    }

    private String createHTMLoffame(ProfileData ret, String color) {
        int i;
        String rest = "<ul style=\"padding: 0px; margin: 0px;\">";
        String fame = ret.get_fame();
        double famenum = (double) Double.parseDouble(fame) / 10;
        rest += "<li style=\"display: block;float: left;width: 33%;\">"
                + "<label style=\"float: left;color: " + color + ";\">威望</label>"
                + "<span style=\"float: left; color: #808080;\">:</span>"
                + "<span style=\"float: left; color: #808080;\">"
                + Double.toString(famenum) + "</span></li>";
        List<ReputationData> ReputationEntryList = ret
                .get_ReputationEntryList();
        for (i = 0; i < ret.get_ReputationEntryListrows(); i++) {
            rest += "<li style=\"display: block;float: left;width: 33%;\">"
                    + "<label style=\"float: left;color: " + color + ";\">"
                    + ReputationEntryList.get(i).get_name() + "</label>"
                    + "<span style=\"float: left; color: #808080;\">:</span>"
                    + "<span style=\"float: left; color: #808080;\">"
                    + ReputationEntryList.get(i).get_data() + "</span></li>";
        }
        return rest + "</ul><br>";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 321) {
            String signdata = data.getStringExtra("sign");
            resulttmp.set_sign(signdata);
            signwebview.requestLayout();
            handleSignWebview(signwebview, resulttmp);
        }
        if (resultCode == 123) {
            String avatardata = data.getStringExtra("avatar");
            resulttmp.set_avatar(avatardata);
            signwebview.requestLayout();
            handleAvatar(avatarImage, resulttmp);
        }
    }

    private void handleSignWebview(WebView contentTV, ProfileData ret) {
        ThemeManager theme = ThemeManager.getInstance();
        int bgColor, fgColor = getResources().getColor(theme.getForegroundColor());
        if (tm.getMode() == ThemeManager.MODE_NIGHT) {
            bgColor = getResources().getColor(theme.getBackgroundColor(0));
        } else {
            bgColor = getResources().getColor(R.color.profilebgcolor);
        }
        bgColor = bgColor & 0xffffff;
        final String bgcolorStr = String.format("%06x", bgColor);

        int htmlfgColor = fgColor & 0xffffff;
        final String fgColorStr = String.format("%06x", htmlfgColor);

        WebViewClient client = new ArticleListWebClient(this);
        contentTV.setBackgroundColor(0);
        contentTV.setFocusableInTouchMode(false);
        contentTV.setFocusable(false);
        if (ActivityUtil.isGreaterThan_2_2()) {
            contentTV.setLongClickable(false);
        }
        WebSettings setting = contentTV.getSettings();
        setting.setDefaultFontSize(PhoneConfiguration.getInstance()
                .getWebSize());
        setting.setJavaScriptEnabled(false);
        contentTV.setWebViewClient(client);
        contentTV.loadDataWithBaseURL(
                null,
                signatureToHtmlText(ret, FunctionUtil.isShowImage(), FunctionUtil.showImageQuality(), fgColorStr, bgcolorStr),
                "text/html", "utf-8", null);
    }

    private void handleadminWebview(WebView contentTV, ProfileData ret) {
        int bgColor, fgColor;
        ThemeManager theme = ThemeManager.getInstance();
        if (tm.getMode() == ThemeManager.MODE_NIGHT) {
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

        WebViewClient client = new ArticleListWebClient(this);
        contentTV.setBackgroundColor(0);
        contentTV.setFocusableInTouchMode(false);
        contentTV.setFocusable(false);
        if (ActivityUtil.isGreaterThan_2_2()) {
            contentTV.setLongClickable(false);
        }
        WebSettings setting = contentTV.getSettings();
        setting.setDefaultFontSize(PhoneConfiguration.getInstance()
                .getWebSize());
        setting.setJavaScriptEnabled(false);
        contentTV.setWebViewClient(client);
        contentTV.loadDataWithBaseURL(
                null,
                adminToHtmlText(ret, FunctionUtil.isShowImage(), FunctionUtil.showImageQuality(), fgColorStr, bgcolorStr), "text/html", "utf-8", null);
    }

    private void handlefameWebview(WebView contentTV, ProfileData ret) {
        int bgColor, fgColor;
        ThemeManager theme = ThemeManager.getInstance();
        if (tm.getMode() == ThemeManager.MODE_NIGHT) {
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

        WebViewClient client = new ArticleListWebClient(this);
        contentTV.setBackgroundColor(0);
        contentTV.setFocusableInTouchMode(false);
        contentTV.setFocusable(false);
        if (ActivityUtil.isGreaterThan_2_2()) {
            contentTV.setLongClickable(false);
        }
        WebSettings setting = contentTV.getSettings();
        setting.setDefaultFontSize(PhoneConfiguration.getInstance()
                .getWebSize());
        setting.setJavaScriptEnabled(false);
        contentTV.setWebViewClient(client);
        contentTV.loadDataWithBaseURL(
                null,
                fameToHtmlText(ret, FunctionUtil.isShowImage(), FunctionUtil.showImageQuality(), fgColorStr, bgcolorStr), "text/html", "utf-8", null);
    }

    public String fameToHtmlText(final ProfileData ret, boolean showImage,
                                 int imageQuality, final String fgColorStr, final String bgcolorStr) {
        HashSet<String> imageURLSet = new HashSet<String>();
        String color = "#121C46";
        if (tm.getMode() == ThemeManager.MODE_NIGHT) {
            color = "#712D08";
        }
        String ngaHtml = createHTMLoffame(ret, color);
        if (imageURLSet.size() == 0) {
            imageURLSet = null;
        }
        ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">"
                + "<body bgcolor= '#"
                + bgcolorStr
                + "'>"
                + "<font color='#"
                + fgColorStr + "' size='2'>" + ngaHtml + "</font></body>";

        return ngaHtml;
    }

    public String adminToHtmlText(final ProfileData ret, boolean showImage,
                                  int imageQuality, final String fgColorStr, final String bgcolorStr) {
        HashSet<String> imageURLSet = new HashSet<String>();
        String ngaHtml = createHTMLofadmin(ret);
        if (imageURLSet.size() == 0) {
            imageURLSet = null;
        }
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
        String ngaHtml = StringUtil.decodeForumTag(ret.get_sign(), showImage,
                imageQuality, imageURLSet);
        if (imageURLSet.size() == 0) {
            imageURLSet = null;
        }
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

    @SuppressWarnings("ResourceType")
    private void handleAvatar(ImageView avatarIV, ProfileData row) {

        final String avatarUrl = FunctionUtil.parseAvatarUrl(row.get_avatar());//
        final String userId = String.valueOf(row.get_uid());
        if (PhoneConfiguration.getInstance().nikeWidth < 3) {
            avatarIV.setImageBitmap(null);
            return;
        }
        if (defaultAvatar == null
                || defaultAvatar.getWidth() != PhoneConfiguration.getInstance().nikeWidth) {
            Resources res = avatarIV.getContext().getResources(); InputStream is = res.openRawResource(R.drawable.default_avatar);
            InputStream is2 = res.openRawResource(R.drawable.default_avatar);
            this.defaultAvatar = ImageUtil.loadAvatarFromStream(is, is2);
        }

        Object tagObj = avatarIV.getTag();
        if (tagObj instanceof AvatarTag) {
            AvatarTag origTag = (AvatarTag) tagObj;
            if (!origTag.isDefault) {
                ImageUtil.recycleImageView(avatarIV);
            }
        }
        AvatarTag tag = new AvatarTag(0, true);
        avatarIV.setImageBitmap(defaultAvatar);
        avatarIV.setTag(tag);
        if (!StringUtil.isEmpty(avatarUrl)) {
            final String avatarPath = ImageUtil.newImage(avatarUrl, userId);
            if (avatarPath != null) {
                File f = new File(avatarPath);
                if (f.exists() && !isPending(avatarUrl)) {

                    Bitmap bitmap = ImageUtil.loadAvatarFromSdcard(avatarPath);
                    if (bitmap != null) {
                        avatarIV.setImageBitmap(bitmap);
                        tag.isDefault = false;
                    } else
                        f.delete();
                    long date = f.lastModified();
                    if ((System.currentTimeMillis() - date) / 1000 > 30 * 24 * 3600) {
                        f.delete();
                    }

                } else {
                    new AvatarLoadTask(avatarIV, null, FunctionUtil.isShowImage(), 0, this).execute(avatarUrl, avatarPath, userId);
                }
            }
        }

    }

    private boolean isPending(String url) {
        boolean ret = false;
        synchronized (lock) {
            ret = urlSet.contains(url);
        }
        return ret;
    }

    @TargetApi(11)
    private void activeActionMode() {
        mActionModeCallback = new ActionMode.Callback() {

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                onContextItemSelected(item);
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // int position = listview.getCheckedItemPosition();
                // listview.setItemChecked(position, false);

            }

            @Override
            public boolean onCreateActionMode(ActionMode arg0, Menu arg1) {
                // TODO Auto-generated method stub
                return false;
            }

        };
    }

    @SuppressWarnings("WrongConstant")
    @Override
    protected void onResume() {
        int orentation = ThemeManager.getInstance().screenOrentation;
        if (orentation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                || orentation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(orentation);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        if (PhoneConfiguration.getInstance().fullscreen) {
            ActivityUtil.getInstance().setFullScreen(view);
        }
        if (!StringUtil.isEmpty(trueusername)) {
            if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {
                if (trueusername.equals(PhoneConfiguration.getInstance().userName)) {
                    avahahahb.setVisibility(View.GONE);
                    message_title.setVisibility(View.GONE);
                    change_avatar_button.setVisibility(View.VISIBLE);
                    change_sign_button.setVisibility(View.VISIBLE);
                }
            }
        }
        super.onResume();
    }

    @Override
    public void jsonfinishLoad(ProfileData result) {
        // TODO Auto-generated method stub
        attacher.setRefreshComplete();
        this.resulttmp = result;
        if (result != null) {
            writetopage(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final int flags = ThemeManager.ACTION_BAR_FLAG;
        ReflectionUtil.actionBar_setDisplayOption(this, flags);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                finish();
        }
        return true;
    }

    @Override
    public void OnAvatarLoadStart(String url) {
        synchronized (lock) {
            this.urlSet.add(url);
        }

    }

    @Override
    public void OnAvatarLoadComplete(String url) {
        synchronized (lock) {
            this.urlSet.remove(url);
        }

    }

    @Override
    public PullToRefreshAttacher getAttacher() {
        // TODO Auto-generated method stub
        return mPullToRefreshAttacher;
    }
}
