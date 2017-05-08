package gov.anzong.androidnga.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.alibaba.fastjson.JSON;
import com.astuetz.PagerSlidingTabStrip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import gov.anzong.meizi.MeiziMainActivity;
import sp.phone.adapter.BoardPagerAdapter;
import sp.phone.bean.AvatarTag;
import sp.phone.bean.Board;
import sp.phone.bean.BoardCategory;
import sp.phone.bean.BoardHolder;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.User;
import sp.phone.fragment.LoginFragment;
import sp.phone.fragment.ProfileSearchDialogFragment;
import sp.phone.fragment.TopicListContainer;
import sp.phone.interfaces.PageCategoryOwnner;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

public class MainActivity extends BaseActivity implements
        PerferenceConstant, PageCategoryOwnner, OnItemClickListener {
    static final String TAG = MainActivity.class.getSimpleName();
    private ActivityUtil activityUtil = ActivityUtil.getInstance();
    private MyApp app;
    private List<Object> items = new ArrayList<Object>();
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private View headview;
    private MenuAdapter mAdapter;
    private int mActivePosition = 0;
    private BoardHolder boardInfo;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager pager;
    private View view;
    private LinearLayout mLinearLayout;
    private boolean tabletloginfragmentshowed = false;
    private ViewFlipper flipper;
    private SharedPreferences share;
    private int dragonballnum = 0;
    private MediaPlayer mp = new MediaPlayer();
    private Animation rightInAnimation;
    private Animation rightOutAnimation;
    private int secondstart;
    private int secondnow;
    private int menucishu = 0;
    private String fulimode = "0";
    private ThemeManager tm = ThemeManager.getInstance();
    private OnItemClickListener onItemClickListenerlistener = new EnterToplistLintener();
    private boolean mIsItemClicked;

    public static Bitmap toRoundCorner(Bitmap bitmap, float ratio) { // 绝无问题
        if (bitmap.getWidth() > bitmap.getHeight()) {
            bitmap = Bitmap.createBitmap(bitmap,
                    (int) (bitmap.getWidth() - bitmap.getHeight()) / 2, 0,
                    bitmap.getHeight(), bitmap.getHeight());
        } else if (bitmap.getWidth() < bitmap.getHeight()) {
            bitmap = Bitmap.createBitmap(bitmap, 0,
                    (int) (bitmap.getHeight() - bitmap.getWidth()) / 2,
                    bitmap.getWidth(), bitmap.getWidth());
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, bitmap.getWidth() / ratio,
                bitmap.getHeight() / ratio, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTheme(R.style.AppTheme);
        share = getSharedPreferences(PERFERENCE, Activity.MODE_PRIVATE);
        dragonballnum = Integer.parseInt(share.getString(DRAGON_BALL, "0"));
        app = ((MyApp) getApplication());
        fulimode = share.getString(CAN_SHOW_FULI, "0");
        initDate();
        setitem();
        initView();
        getSupportActionBar().setTitle(R.string.start_title);
        checknewversion();
    }

    //OK
    private void checknewversion() {
        if (app.isNewVersion()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.prompt).setMessage(StringUtil.getTips())
                    .setPositiveButton(R.string.i_know, null);
            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface arg0) {
                    // TODO Auto-generated method stub
                    dialog.dismiss();
                    if (PhoneConfiguration.getInstance().fullscreen) {
                        activityUtil.setFullScreen(view);
                    }
                }

            });
            app.setNewVersion(false);
            showToast("播放器现已插件化,请到关于中下载或PLAY商店搜索BambooPlayer安装");
        }
    }

    private void initView() {
        view = LayoutInflater.from(this).inflate(R.layout.mainfragment, null);

        setContentView(view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mLinearLayout = (LinearLayout) findViewById(R.id.drawer_linearlayout);

        headview = LayoutInflater.from(this).inflate(R.layout.maindrawer_viewlipper, null);
        mDrawerList.addHeaderView(headview, null, false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open, /* "open drawer" description for accessibility */
                R.string.drawer_close /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                if (mIsItemClicked) {
                    selectItem(mActivePosition, (Item) mAdapter.getItem(mActivePosition - 1));
                    mIsItemClicked = false;
                }
                getSupportActionBar().setTitle(R.string.start_title);
                if (ActivityUtil.isLessThan_3_0())
                    supportInvalidateOptionsMenu();
                else
                    invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                if (tabletloginfragmentshowed) {
                    refreshheadview();
                    tabletloginfragmentshowed = false;
                }
                getSupportActionBar().setTitle("赞美片总");
                if (ActivityUtil.isLessThan_3_0())
                    supportInvalidateOptionsMenu();
                else
                    invalidateOptionsMenu();
            }
        };
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        pager = (ViewPager) findViewById(R.id.pager);
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.pagerslidingtab);
    }

    public void updatemDrawerList() {
        mAdapter = new MenuAdapter(this, items);
        mDrawerList.setCacheColorHint(0x00000000);
        if (tm.getMode() == ThemeManager.MODE_NIGHT) {
            ColorDrawable sage = new ColorDrawable(mDrawerList.getResources()
                    .getColor(R.color.night_link_color));
            mDrawerList.setBackgroundResource(R.color.night_bg_color);
            mDrawerList.setDivider(sage);
            mDrawerList.setDividerHeight(1);
        } else {
            ColorDrawable sage = new ColorDrawable(mDrawerList.getResources()
                    .getColor(R.color.white));
            mDrawerList.setBackgroundResource(R.color.drawerlistactiveddeep);
            mDrawerList.setDivider(sage);
            mDrawerList.setDividerHeight(1);
        }
        mDrawerList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        if (mActivePosition <= mAdapter.getCount()) {
            mDrawerList.setSelection(mActivePosition);
        } else {
            mDrawerList.setSelection(mAdapter.getCount());
        }
        if (mActivePosition <= mAdapter.getCount() && mActivePosition >= 1) {
            mDrawerList.setItemChecked(mActivePosition, true);
        }
    }

    public void updateView(int itemIndex) {
        Item target = (Item) mDrawerList.getItemAtPosition(itemIndex);
        int start = mDrawerList.getFirstVisiblePosition();
        for (int i = start, j = mDrawerList.getLastVisiblePosition(); i <= j; i++)
            if (target == mDrawerList.getItemAtPosition(i)) {
                View view = mDrawerList.getChildAt(i - start);
                mDrawerList.getAdapter().getView(i, view, mDrawerList);
                break;
            }
    }

    public void updatepager() {
        mLinearLayout.setBackgroundResource(ThemeManager.getInstance()
                .getBackgroundColor());
        int width = getResources().getInteger(R.integer.page_category_width);
        pager.setAdapter(new BoardPagerAdapter(getSupportFragmentManager(),
                this, width));
        mPagerSlidingTabStrip.setViewPager(pager);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content
        // view
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.mainmenu_setting:
                jumpToSetting();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectItem(int position, Item item) {
        if (item.mTitle.equals("登录账号")) {
            jumpToLogin();
        } else if (item.mTitle.equals("Yoooo~")) {
            jumpToNearby();
        } else if (item.mTitle.equals("最近被喷")) {
            jumpToRecentReply();
        } else if (item.mTitle.equals("我的主题")) {
            jumpToMyPost(false);
        } else if (item.mTitle.equals("我的回复")) {
            jumpToMyPost(true);
        } else if (item.mTitle.equals("收藏夹")) {
            jumpToBookmark();
        } else if (item.mTitle.equals("签到任务")) {
            signmission();
        } else if (item.mTitle.equals("短消息")) {
            mymessage();
        } else if (item.mTitle.equals("大漩涡匿名版")) {
            noname();
        } else if (item.mTitle.equals("搜索用户")) {
            search_profile();
        } else if (item.mTitle.equals("添加版面")) {
            add_fid_dialog();
        } else if (item.mTitle.equals("由URL读取")) {
            useurltoactivity_dialog();
        } else if (item.mTitle.equals("清空最近访问")) {
            clear_recent_board();
        } else if (item.mTitle.equals("我要龙珠~撸~")) {
            collect_dragon_ball();
        } else if (item.mTitle.equals("关于")) {
            about_ngaclient();
        }
        mDrawerList.setItemChecked(position, true);
        if (!item.mTitle.equals("我要龙珠~撸~")) {
            Log.e(TAG,"closeDrawer");
            mDrawerLayout.closeDrawer(mDrawerList);
        }
        mActivePosition = position;
    }

    private void search_profile() {

        Bundle arg = new Bundle();
        DialogFragment df = new ProfileSearchDialogFragment();
        df.setArguments(arg);
        final String dialogTag = "searchpaofile_dialog";
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(dialogTag);
        if (prev != null) {
            ft.remove(prev);
        }

        try {
            df.show(ft, dialogTag);
        } catch (Exception e) {
            Log.e(TopicListContainer.class.getSimpleName(),
                    Log.getStackTraceString(e));

        }
    }

    private void signmission() {
        Intent intent = new Intent();
        PhoneConfiguration config = PhoneConfiguration.getInstance();
        intent.setClass(MainActivity.this, config.signActivityClass);
        startActivity(intent);
        if (PhoneConfiguration.getInstance().showAnimation) {
            overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        }

    }

    private void mymessage() {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        PhoneConfiguration config = PhoneConfiguration.getInstance();
        intent.setClass(MainActivity.this, config.messageActivityClass);

        startActivity(intent);
        if (PhoneConfiguration.getInstance().showAnimation) {
            overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        }

    }

    private void noname() {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        PhoneConfiguration config = PhoneConfiguration.getInstance();
        intent.setClass(MainActivity.this, config.nonameActivityClass);

        startActivity(intent);
        if (PhoneConfiguration.getInstance().showAnimation) {
            overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        }

    }

    public void setLocItem(int loc, String itemname, int id) {
        // set item on loc position
        items.add(loc, new Item(itemname, id));
        // reset menu
        if (loc + 1 <= mActivePosition) {
            mActivePosition++;
        }
        updatemDrawerList();
    }

    private void about_ngaclient() {
        LayoutInflater layoutInflater = getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.client_dialog, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(view);
        alert.setTitle("关于");
        String versionName = null;
        int versionvalue = 0;
        TextView textview = (TextView) view
                .findViewById(R.id.client_device_dialog);
        try {
            PackageManager pm = MainActivity.this.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(
                    MainActivity.this.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                versionName = pi.versionName == null ? "null" : pi.versionName;
                versionvalue = pi.versionCode;
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        String textviewtext = String.format(MainActivity.this
                .getString(R.string.about_client), versionName, versionvalue);
        textview.setText(Html.fromHtml(textviewtext));

        textview.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence text = textview.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) textview.getText();
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.clearSpans();// should clear old spans
            for (URLSpan url : urls) {
                MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
                style.setSpan(myURLSpan, sp.getSpanStart(url),
                        sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            textview.setText(style);
        }
        alert.setPositiveButton("知道了", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (PhoneConfiguration.getInstance().fullscreen) {
                    activityUtil.setFullScreen(view);
                }
            }
        });
        final AlertDialog dialog = alert.create();
        dialog.show();
        dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface arg0) {
                dialog.dismiss();
                if (PhoneConfiguration.getInstance().fullscreen) {
                    activityUtil.setFullScreen(view);
                }
            }

        });
    }

    public void setitem() {
        items.add(new Item("登录账号", R.drawable.ic_login));
        items.add(new Category("论坛功能"));
        items.add(new Item("短消息", R.drawable.ic_action_email));
//  items.add(new Item("签到任务", R.drawable.ic_action_go_to_today));
        items.add(new Item("搜索用户", R.drawable.action_search));
        items.add(new Item("由URL读取", R.drawable.ic_action_forward));
        items.add(new Item("添加版面", R.drawable.ic_action_add_to_queue));
        items.add(new Item("清空最近访问", R.drawable.ic_action_warning));
        items.add(new Item("最近被喷", R.drawable.ic_action_gun));
        items.add(new Category("我的"));
        items.add(new Item("我的主题", R.drawable.action_search));
        items.add(new Item("我的回复", R.drawable.action_search));
        items.add(new Item("收藏夹", R.drawable.action_search));
        items.add(new Category("私货"));
        items.add(new Item("Yoooo~", R.drawable.ic_menu_mylocation));
        items.add(new Item("大漩涡匿名版", R.drawable.ic_action_noname));
        if (!fulimode.equals("0")) {
            items.add(new Item("我要龙珠~撸~", R.drawable.ic_action_dragon_ball));
        }
        items.add(new Item("关于", R.drawable.ic_action_about));
    }

    void updateflipper(String userListString) {
        flipper.removeAllViews();
        BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
        bfoOptions.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.userdrawback, bfoOptions);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        width = (int) (width * 0.8);
        if (width < 800) {
            bmp = Bitmap.createBitmap(bmp, (int) 400 - width / 2, 0, width, 55);
        }
        @SuppressWarnings("deprecation")
        BitmapDrawable bd = new BitmapDrawable(bmp);
        List<User> userList;
        if (StringUtil.isEmpty(userListString)) {
            flipper.addView(getUserView(null, 0, bd));// 传递回一个未登入的
        } else {
            userList = JSON.parseArray(userListString, User.class);
            if (userList.size() == 0) {
                flipper.addView(getUserView(null, 0, bd));// 传递回一个未登入的
            } else {
                for (int i = 1; i <= userList.size(); i++) {
                    flipper.addView(getUserView(userList, i - 1, bd));// 传递回一个未登入的
                }
            }
        }

        // 动画效果
        rightInAnimation = AnimationUtils.loadAnimation(this, R.anim.right_in);
        rightOutAnimation = AnimationUtils
                .loadAnimation(this, R.anim.right_out);
    }

    @SuppressWarnings("deprecation")
    public View getUserView(List<User> userList, int position, BitmapDrawable bd) {
        View privateview = getLayoutInflater().inflate(
                R.layout.drawerloginuser, null);
        RelativeLayout mRelativeLayout = (RelativeLayout) privateview
                .findViewById(R.id.mainlisthead);
        mRelativeLayout.setBackgroundDrawable(bd);
        TextView loginstate = (TextView) privateview
                .findViewById(R.id.loginstate);
        TextView loginnameandid = (TextView) privateview
                .findViewById(R.id.loginnameandid);
        ImageView avatarImage = (ImageView) privateview
                .findViewById(R.id.avatarImage);
        ImageView nextImage = (ImageView) privateview
                .findViewById(R.id.nextImage);
        if (userList == null) {
            loginstate.setText("未登录");
            loginnameandid.setText("点击下面的登录账号登录");
            avatarImage.setImageDrawable(getResources().getDrawable(
                    R.drawable.drawerdefaulticon));
            nextImage.setVisibility(View.GONE);
        } else {
            if (userList.size() <= 1) {
                nextImage.setVisibility(View.GONE);
            }
            if (userList.size() == 1) {
                loginstate.setText("已登录1个账户");
            } else {
                loginstate.setText("已登录"
                        + String.valueOf(userList.size() + "个账户,点击切换"));
            }
            if (userList.size() > 0) {
                User user = userList.get(position);
                loginnameandid.setText("当前:" + user.getNickName() + "("
                        + user.getUserId() + ")");
                handleUserAvatat(avatarImage, user.getUserId());
            }
        }
        return privateview;
    }

    public void refreshheadview() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        width = (int) (width * 0.8);
        if (width >= 800) {
            mDrawerList.getLayoutParams().width = 800;
        } else {
            mDrawerList.getLayoutParams().width = width;
        }
        flipper = (ViewFlipper) this.findViewById(R.id.viewFlipper);
        String userListString = share.getString(USER_LIST, "");
        updateflipper(userListString);
        headview.setOnClickListener(new HeadViewClickListener(userListString));

    }

    @SuppressWarnings("ResourceType")
    public void handleUserAvatat(ImageView avatarIV, String userId) {// 绝无问题
        Bitmap defaultAvatar = null, bitmap = null;
        if (PhoneConfiguration.getInstance().nikeWidth < 3) {
            return;
        }
        if (defaultAvatar == null
                || defaultAvatar.getWidth() != PhoneConfiguration.getInstance().nikeWidth) {
            Resources res = getLayoutInflater().getContext().getResources();
            InputStream is = res.openRawResource(R.drawable.default_avatar);
            InputStream is2 = res.openRawResource(R.drawable.default_avatar);
            defaultAvatar = ImageUtil.loadAvatarFromStream(is, is2);
        }
        Object tagObj = avatarIV.getTag();
        if (tagObj instanceof AvatarTag) {
            AvatarTag origTag = (AvatarTag) tagObj;
            if (origTag.isDefault == false) {
                ImageUtil.recycleImageView(avatarIV);
            }
        }
        AvatarTag tag = new AvatarTag(Integer.parseInt(userId), true);
        avatarIV.setImageBitmap(defaultAvatar);
        avatarIV.setTag(tag);
        String avatarPath = HttpUtil.PATH_AVATAR + "/" + userId;
        String[] extension = {".jpg", ".png", ".gif", ".jpeg", ".bmp"};
        for (int i = 0; i < 5; i++) {
            File f = new File(avatarPath + extension[i]);
            if (f.exists()) {

                bitmap = ImageUtil.loadAvatarFromSdcard(avatarPath
                        + extension[i]);
                if (bitmap == null) {
                    f.delete();
                }
                long date = f.lastModified();
                if ((System.currentTimeMillis() - date) / 1000 > 30 * 24 * 3600) {
                    f.delete();
                }
                break;
            }
        }
        if (bitmap != null) {
            avatarIV.setImageBitmap(toRoundCorner(bitmap, 2));
            tag.isDefault = false;
        } else {
            avatarIV.setImageDrawable(getResources().getDrawable(
                    R.drawable.drawerdefaulticon));
            tag.isDefault = true;
        }

    }

    void loadConfig(Intent intent) {
        // initUserInfo(intent);
        this.boardInfo = this.loadDefaultBoard();

    }

    private BoardHolder loadDefaultBoard() {
        if (PhoneConfiguration.getInstance().iconmode) {
            return app.loadDefaultBoardOld();
        } else {
            return app.loadDefaultBoard();
        }
    }

    private void delay(String text) {
        final String msg = text;
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                showToast(msg);
            }

        });
    }

    private void initDate() {

        new Thread() {
            public void run() {

                File filebase = new File(HttpUtil.PATH);
                if (!filebase.exists()) {
                    delay(getString(R.string.create_cache_dir));
                    filebase.mkdirs();
                }
                if (ActivityUtil.isGreaterThan_2_1()) {
                    File f = new File(HttpUtil.PATH_AVATAR_OLD);
                    if (f.exists()) {
                        f.renameTo(new File(HttpUtil.PATH_AVATAR));
                        delay(getString(R.string.move_avatar));
                    }
                }

                File file = new File(HttpUtil.PATH_NOMEDIA);
                if (!file.exists()) {
                    Log.i(getClass().getSimpleName(), "create .nomedia");
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();

    }

    public boolean isTablet() {
        boolean xlarge = ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 0x04);// Configuration.SCREENLAYOUT_SIZE_XLARGE);
        boolean large = ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large) && ActivityUtil.isGreaterThan_2_3_3();
    }

    private void jumpToLogin() {
        if (isTablet()) {
            tabletloginfragmentshowed = true;
            DialogFragment df = new LoginFragment();
            df.show(getSupportFragmentManager(), "login");
            return;
        }

        Intent intent = new Intent();
        intent.setClass(MainActivity.this, LoginActivity.class);
        try {
            startActivity(intent);
            if (PhoneConfiguration.getInstance().showAnimation) {
                overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
            }
        } catch (Exception e) {

        }

    }

    private void collect_dragon_ball() {
        if (dragonballnum < 6) {
            dragonballnum++;
            Editor editor = share.edit();
            editor.putString(DRAGON_BALL, String.valueOf(dragonballnum));
            editor.apply();
            showToast("你收集到了" + String.valueOf(dragonballnum) + "颗龙珠");
        } else if (dragonballnum == 6) {
            showToast("你收集到了7颗龙珠");
            AudioManager audioManager = (AudioManager) view.getContext()
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                AssetFileDescriptor afd = getResources().openRawResourceFd(
                        R.raw.dragon_ball);
                try {
                    mp.reset();
                    mp.setDataSource(afd.getFileDescriptor(),
                            afd.getStartOffset(), afd.getLength());
                    mp.prepare();
                    mp.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            final Editor editor = share.edit();

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    mp.release();
                    mp = new MediaPlayer();
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            dragonballnum = 7;
                            editor.putString(DRAGON_BALL,
                                    String.valueOf(dragonballnum));
                            editor.apply();
                            Intent intent = new Intent();
                            intent.setClass(
                                    MainActivity.this,
                                    PhoneConfiguration.getInstance().MeiziMainActivityClass);
                            startActivity(intent);
                            if (PhoneConfiguration.getInstance().showAnimation)
                                overridePendingTransition(R.anim.zoom_enter,
                                        R.anim.zoom_exit);

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            // Do nothing
                            dragonballnum = 0;
                            editor.putString(DRAGON_BALL,
                                    String.valueOf(dragonballnum));
                            editor.apply();
                            showToast("你选择了不使用,龙珠又散落四方了");
                            break;
                    }
                }
            };

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(
                    this.getString(R.string.dragon_ball_collect_complete))
                    .setPositiveButton(R.string.confirm, dialogClickListener)
                    .setNegativeButton(R.string.cancle, dialogClickListener);
            builder.setOnCancelListener(new AlertDialog.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface arg0) {
                    // TODO Auto-generated method stub
                }

            });
            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface arg0) {
                    mp.release();
                    mp = new MediaPlayer();
                    dialog.dismiss();
                    if (PhoneConfiguration.getInstance().fullscreen) {
                        activityUtil.setFullScreen(view);
                    }
                }

            });

        } else if (dragonballnum >= 7) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MeiziMainActivity.class);
            startActivity(intent);
            if (PhoneConfiguration.getInstance().showAnimation)
                overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);

        }
    }

    private void jumpToSetting() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        if (PhoneConfiguration.getInstance().showAnimation)
            overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    void jumpToNearby() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, NearbyUserActivity.class);

        startActivity(intent);
        if (PhoneConfiguration.getInstance().showAnimation)
            overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);

    }

    private void jumpToRecentReply() {
        Intent intent = new Intent();
        intent.putExtra("recentmode", "recentmode");
        intent.setClass(MainActivity.this, PhoneConfiguration.getInstance().recentReplyListActivityClass);

        startActivity(intent);
        if (PhoneConfiguration.getInstance().showAnimation)
            overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    private void jumpToMyPost(boolean isReply) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, PhoneConfiguration.getInstance().topicActivityClass);
        String userName = PhoneConfiguration.getInstance().userName;
        if (TextUtils.isEmpty(userName)) {
            showToast("你还没有登录");
            return;
        }

        if (isReply) {
            intent.putExtra("author", userName + "&searchpost=1");
        } else {
            intent.putExtra("author", userName);
        }
        startActivity(intent);
        if (PhoneConfiguration.getInstance().showAnimation)
            overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    private void jumpToBookmark() {
        Intent intent_bookmark = new Intent(this, PhoneConfiguration.getInstance().topicActivityClass);
        intent_bookmark.putExtra("favor", 1);
        startActivity(intent_bookmark);
    }

    private void clear_recent_board() {
        Editor editor = share.edit();
        editor.putString(RECENT_BOARD, "");
        editor.apply();
        onResume();
    }

    @SuppressWarnings("deprecation")
    private void useurltoactivity_dialog() {
        LayoutInflater layoutInflater = getLayoutInflater();
        final View view = layoutInflater
                .inflate(R.layout.useurlto_dialog, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(view);
        alert.setTitle(R.string.urlto_title_hint);
        final EditText urladd = (EditText) view.findViewById(R.id.urladd);
        urladd.requestFocus();
        String clipdata = null;
        if (ActivityUtil.isLessThan_3_0()) {
            android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboardManager.hasText()) {
                clipdata = clipboardManager.getText().toString();
            }
        } else {
            android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            if (clipboardManager.hasPrimaryClip()) {
                @SuppressWarnings("unused")
                android.content.ClipData.Item item = clipboardManager
                        .getPrimaryClip().getItemAt(0);
                try {
                    clipdata = clipboardManager.getPrimaryClip().getItemAt(0)
                            .getText().toString();
                } catch (Exception e) {
                    clipdata = "";
                }

            }
        }
        if (!StringUtil.isEmpty(clipdata)) {
            urladd.setText(clipdata);
            urladd.selectAll();
        }

        alert.setPositiveButton("进入", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = urladd.getText().toString().trim();
                if (StringUtil.isEmpty(url)) {// 空
                    showToast("请输入URL地址");
                    urladd.setFocusable(true);
                    try {
                        Field field = dialog.getClass().getSuperclass()
                                .getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (url.toLowerCase(Locale.US).indexOf("dbmeizi.com") >= 0
                            || url.indexOf("豆瓣妹子") >= 0 || url.equals("1024")) {
                        showToast("恭喜你找到了一种方法,知道就是知道了,不要去论坛宣传,自己用就行了,为了开发者的安全");
                        Intent intent = new Intent();
                        intent.setClass(
                                MainActivity.this,
                                PhoneConfiguration.getInstance().MeiziMainActivityClass);
                        startActivity(intent);
                        if (PhoneConfiguration.getInstance().showAnimation)
                            overridePendingTransition(R.anim.zoom_enter,
                                    R.anim.zoom_exit);
                    } else {
                        PhoneConfiguration conf = PhoneConfiguration
                                .getInstance();
                        url = url.toLowerCase(Locale.US).trim();
                        if (url.indexOf("thread.php") > 0) {
                            url = url
                                    .replaceAll(
                                            "(?i)[^\\[|\\]]+fid=(-{0,1}\\d+)[^\\[|\\]]{0,}",
                                            Utils.getNGAHost() + "thread.php?fid=$1");
                            Intent intent = new Intent();
                            intent.setData(Uri.parse(url));
                            intent.setClass(view.getContext(),
                                    conf.topicActivityClass);
                            view.getContext().startActivity(intent);
                        } else if (url.indexOf("read.php") > 0) {
                            if (url.indexOf("tid") > 0
                                    && url.indexOf("pid") > 0) {
                                if (url.indexOf("tid") < url.indexOf("pid"))
                                    url = url
                                            .replaceAll(
                                                    "(?i)[^\\[|\\]]+tid=(\\d+)[^\\[|\\]]+pid=(\\d+)[^\\[|\\]]{0,}",
                                                    Utils.getNGAHost() + "read.php?pid=$2&tid=$1");
                                else
                                    url = url
                                            .replaceAll(
                                                    "(?i)[^\\[|\\]]+pid=(\\d+)[^\\[|\\]]+tid=(\\d+)[^\\[|\\]]{0,}",
                                                    Utils.getNGAHost() + "read.php?pid=$1&tid=$2");
                            } else if (url.indexOf("tid") > 0
                                    && url.indexOf("pid") <= 0) {
                                url = url
                                        .replaceAll(
                                                "(?i)[^\\[|\\]]+tid=(\\d+)[^\\[|\\]]{0,}",
                                                Utils.getNGAHost() + "read.php?tid=$1");
                            } else if (url.indexOf("pid") > 0
                                    && url.indexOf("tid") <= 0) {
                                url = url
                                        .replaceAll(
                                                "(?i)[^\\[|\\]]+pid=(\\d+)[^\\[|\\]]{0,}",
                                                Utils.getNGAHost() + "read.php?pid=$1");
                            }
                            Intent intent = new Intent();
                            intent.setData(Uri.parse(url));
                            intent.setClass(view.getContext(),
                                    conf.articleActivityClass);
                            view.getContext().startActivity(intent);
                        } else {
                            showToast("输入的地址并非NGA的板块地址或帖子地址,或缺少fid/pid/tid信息,请检查后再试");
                            urladd.setFocusable(true);
                            try {
                                Field field = dialog.getClass().getSuperclass()
                                        .getDeclaredField("mShowing");
                                field.setAccessible(true);
                                field.set(dialog, false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                try {
                    Field field = dialog.getClass().getSuperclass()
                            .getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        final AlertDialog dialog = alert.create();
        dialog.show();
        Date d = new Date();
        int hours = d.getHours();
        if (hours > 22 || hours < 6) {
            String toastdata = "1024";
            boolean showtextbool = new Random().nextBoolean();
            if (showtextbool) {
                showtextbool = new Random().nextBoolean();
                if (showtextbool) {
                    toastdata = "MENU×7";
                } else {
                    toastdata = "豆瓣妹子";
                }
            } else {
                showtextbool = new Random().nextBoolean();
                if (showtextbool) {
                    toastdata = "dbmeizi.com";
                }
            }
            showToast(toastdata);
        }
        dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface arg0) {
                dialog.dismiss();
                if (PhoneConfiguration.getInstance().fullscreen) {
                    activityUtil.setFullScreen(view);
                }
            }

        });
    }

    private void add_fid_dialog() {
        LayoutInflater layoutInflater = getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.addfid_dialog, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(view);
        alert.setTitle(R.string.addfid_title_hint);
        final EditText addfid_name = (EditText) view
                .findViewById(R.id.addfid_name);
        final EditText addfid_id = (EditText) view.findViewById(R.id.addfid_id);
        alert.setPositiveButton("添加", new DialogInterface.OnClickListener() {

            @SuppressWarnings("unused")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                String name = addfid_name.getText().toString();
                String fid = addfid_id.getText().toString();
                if (name.equals("")) {
                    showToast("请输入版面名称");
                    addfid_name.setFocusable(true);
                    try {
                        Field field = dialog.getClass().getSuperclass()
                                .getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                    Pattern pattern = Pattern.compile("-{0,1}[0-9]*");
                    Matcher match = pattern.matcher(fid);
                    boolean fidisnotint = false;
                    try {
                        int checkint = Integer.parseInt(fid);
                    } catch (Exception e) {
                        fidisnotint = true;
                    }
                    if (match.matches() == false || fid.equals("") || fidisnotint) {
                        addfid_id.setText("");
                        addfid_id.setFocusable(true);
                        showToast("请输入正确的版面ID(个人版面要加负号)");
                        try {
                            Field field = dialog.getClass().getSuperclass()
                                    .getDeclaredField("mShowing");
                            field.setAccessible(true);
                            field.set(dialog, false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {// CHECK PASS, READY TO ADD FID
                        boolean FidAllreadyExist = false;
                        int i = 0;
                        for (i = 0; i < boardInfo.getCategoryCount(); i++) {
                            BoardCategory curr = boardInfo.getCategory(i);
                            for (int j = 0; j < curr.size(); j++) {
                                String URL = curr.get(j).getUrl();
                                if (URL.equals(fid)) {
                                    FidAllreadyExist = true;
                                    addfid_id.setText("");
                                    addfid_id.setFocusable(true);
                                    showToast("该版面已经存在于列表" + boardInfo.getCategoryName(i) + "中");
                                    try {
                                        Field field = dialog.getClass()
                                                .getSuperclass()
                                                .getDeclaredField("mShowing");
                                        field.setAccessible(true);
                                        field.set(dialog, false);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                            }// for j
                        }// for i
                        if (!FidAllreadyExist) {
                            addToaddFid(name, fid);
                            showToast("添加成功" + boardInfo.getCategoryName(i) + "中");
                            try {
                                Field field = dialog.getClass().getSuperclass()
                                        .getDeclaredField("mShowing");
                                field.setAccessible(true);
                                field.set(dialog, true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            }
        });
        alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                try {
                    Field field = dialog.getClass().getSuperclass()
                            .getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        final AlertDialog dialog = alert.create();
        dialog.show();
        dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface arg0) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                if (PhoneConfiguration.getInstance().fullscreen) {
                    activityUtil.setFullScreen(view);
                }
            }

        });
    }

    private void addToaddFid(String Name, String Fid) {
        boolean addFidAlreadExist = false;
        BoardCategory addFid = null;
        int i = 0;
        for (i = 0; i < boardInfo.getCategoryCount(); i++) {
            if (boardInfo.getCategoryName(i).equals(getString(R.string.addfid))) {
                addFidAlreadExist = true;
                addFid = boardInfo.getCategory(i);
                break;
            }
            ;
        }
        if (!addFidAlreadExist) {// 没有
            List<Board> boardList = new ArrayList<Board>();
            Board b;
            if (PhoneConfiguration.getInstance().iconmode) {
                b = new Board(i + 1, Fid, Name, R.drawable.oldpdefault);
            } else {
                b = new Board(i + 1, Fid, Name, R.drawable.pdefault);
            }
            boardList.add(b);
            saveaddFid(boardList);
            onResume();
            pager.setCurrentItem(i + 1);
        } else {// 有了
            Board b;
            if (PhoneConfiguration.getInstance().iconmode) {
                b = new Board(i, Fid, Name, R.drawable.oldpdefault);
            } else {
                b = new Board(i, Fid, Name, R.drawable.pdefault);
            }
            addFid.add(b);
            saveaddFid(addFid.getBoardList());
        }
    }

    private void saveaddFid(List<Board> boardList) {
        // TODO Auto-generated method stub
        String addFidStr = JSON.toJSONString(boardList);
        Editor editor = share.edit();
        editor.putString(ADD_FID, addFidStr);
        editor.apply();
    }

    @Override
    protected void onStop() {
        mp.release();
        mp = new MediaPlayer();
        super.onStop();
    }

    @SuppressWarnings("WrongConstant")
    @Override
    protected void onResume() {
        int orentation = ThemeManager.getInstance().screenOrentation;
        if (orentation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                || orentation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                setRequestedOrientation(orentation);
        } else {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    || getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        if (PhoneConfiguration.getInstance().fullscreen) {
            activityUtil.setFullScreen(view);
        }
        Intent intent = getIntent();
        loadConfig(intent);
        if (pager.getAdapter() != null) {
            mPagerSlidingTabStrip.notifyDataSetChanged();
        }
        updatemDrawerList();
        refreshheadview();
        updatepager();
        super.onResume();
    }

    @Override
    public int getCategoryCount() {
        if (boardInfo == null)
            return 0;
        return boardInfo.getCategoryCount();
    }

    @Override
    public String getCategoryName(int position) {
        if (boardInfo == null)
            return "";
        return boardInfo.getCategoryName(position);
    }

    @Override
    public BoardCategory getCategory(int category) {
        if (boardInfo == null)
            return null;
        return boardInfo.getCategory(category);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        this.onItemClickListenerlistener
                .onItemClick(parent, view, position, id);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
            if (fulimode.equals("0")) {
                if (menucishu == 0) {
                    secondstart = (int) System.currentTimeMillis() / 1000;
                    menucishu++;
                } else {
                    secondnow = (int) System.currentTimeMillis() / 1000;
                    if (secondnow - secondstart < 10) {
                        menucishu++;
                        if (menucishu >= 7) {
                            Editor editor = share.edit();
                            editor.putString(CAN_SHOW_FULI, "1");
                            editor.apply();
                            setLocItem(15,
                                    "我要龙珠~撸~", R.drawable.ic_action_dragon_ball);
                            showToast("你根本不知道发生了什么\n如果你知道了,不要去论坛宣传,自己用就行了,为了开发者的安全");
                            fulimode = "1";
                            menucishu = 0;
                        }
                    } else {
                        secondstart = (int) System.currentTimeMillis() / 1000;
                        secondnow = 0;
                        menucishu = 0;
                    }
                }
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            mActivePosition = position;
            mIsItemClicked = true;
            mDrawerLayout.closeDrawers();
        }
    }

    public class MyURLSpan extends ClickableSpan {

        private String mUrl;

        MyURLSpan(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {

            Intent intent = new Intent();
            intent.putExtra("path", mUrl);
            intent.setClass(MainActivity.this, WebViewerActivity.class);
            startActivity(intent);
        }
    }

    class HeadViewClickListener implements OnClickListener {
        List<User> userList;

        public HeadViewClickListener(String userListString) {
            // TODO Auto-generated constructor stub
            if (!StringUtil.isEmpty(userListString)) {
                this.userList = JSON.parseArray(userListString, User.class);
            }
        }

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            if (userList != null) {
                if (userList.size() > 1) {
                    flipper.setInAnimation(rightInAnimation);
                    flipper.setOutAnimation(rightOutAnimation);
                    flipper.showPrevious();
                    User u = userList.get(flipper.getDisplayedChild());
                    app.addToUserList(u.getUserId(), u.getCid(),
                            u.getNickName(), u.getReplyString(),
                            u.getReplyTotalNum(), u.getBlackList());
                    PhoneConfiguration.getInstance().setUid(u.getUserId());
                    PhoneConfiguration.getInstance().setNickname(
                            u.getNickName());
                    PhoneConfiguration.getInstance().setCid(u.getCid());
                    PhoneConfiguration.getInstance().setReplyString(
                            u.getReplyString());
                    PhoneConfiguration.getInstance().setReplyTotalNum(
                            u.getReplyTotalNum());
                    PhoneConfiguration.getInstance().blacklist = StringUtil
                            .blackliststringtolisttohashset(u.getBlackList());
                    showToast("切换账户成功,当前账户名:" + u.getNickName());
                }
                updateView(5);
            }

        }

    }

    class EnterToplistLintener implements OnItemClickListener, OnClickListener {
        int position;
        String fidString;

        public EnterToplistLintener(int position, String fidString) {
            super();
            this.position = position;
            this.fidString = fidString;
        }

        public EnterToplistLintener() {// constructoer
        }

        public void onClick(View v) {

            if (position != 0 && !HttpUtil.HOST_PORT.equals("")) {
                HttpUtil.HOST = HttpUtil.HOST_PORT + HttpUtil.Servlet_timer;
            }
            int fid = 0;
            try {
                fid = Integer.parseInt(fidString);
            } catch (Exception e) {
                final String tag = this.getClass().getSimpleName();
                Log.e(tag, Log.getStackTraceString(e));
                Log.e(tag, "invalid fid " + fidString);
            }
            if (fid == 0) {
                String tip = fidString + "绝对不存在";
                showToast(tip);
                return;
            }

            Log.i(this.getClass().getSimpleName(), "set host:" + HttpUtil.HOST);

            String url = HttpUtil.Server + "/thread.php?fid=" + fidString + "&rss=1";
            PhoneConfiguration config = PhoneConfiguration.getInstance();
            if (!StringUtil.isEmpty(config.getCookie())) {
                url = url + "&" + config.getCookie().replace("; ", "&");
            } else if (fid < 0) {
                jumpToLogin();
                return;
            }
            addToRecent();
            if (!StringUtil.isEmpty(url)) {
                Intent intent = new Intent();
                intent.putExtra("tab", "1");
                intent.putExtra("fid", fid);
                intent.setClass(MainActivity.this, config.topicActivityClass);
                // intent.setClass(MainActivity.this, TopicListActivity.class);
                startActivity(intent);
                if (PhoneConfiguration.getInstance().showAnimation) {
                    overridePendingTransition(R.anim.zoom_enter,
                            R.anim.zoom_exit);
                }
            }
        }

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            position = arg2;
            fidString = (String) arg0.getItemAtPosition(position);
            onClick(arg1);

        }

        private void saveRecent(List<Board> boardList) {
            String rescentStr = JSON.toJSONString(boardList);
            Editor editor = share.edit();
            editor.putString(RECENT_BOARD, rescentStr);
            editor.apply();
        }

        private void addToRecent() {

            boolean recentAlreadExist = boardInfo.getCategoryName(0).equals(
                    getString(R.string.recent));

            BoardCategory recent = boardInfo.getCategory(0);
            if (recent != null && recentAlreadExist)
                recent.remove(fidString);
            // int i = 0;
            for (int i = 0; i < boardInfo.getCategoryCount(); i++) {
                BoardCategory curr = boardInfo.getCategory(i);
                for (int j = 0; j < curr.size(); j++) {
                    Board b = curr.get(j);
                    if (b.getUrl().equals(fidString)) {
                        Board b1 = new Board(0, b.getUrl(), b.getName(),
                                b.getIcon());

                        if (!recentAlreadExist) {
                            List<Board> boardList = new ArrayList<Board>();
                            boardList.add(b1);
                            saveRecent(boardList);
                            onResume();
                            return;
                        } else {
                            recent.addFront(b1);
                            recent = boardInfo.getCategory(0);
                            saveRecent(recent.getBoardList());
                        }

                        return;
                    }// if
                }// for j

            }// for i

        }

    }

}
