package gov.anzong.androidnga.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

import gov.anzong.androidnga.R;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import sp.phone.bean.Board;
import sp.phone.bean.BoardCategory;
import sp.phone.bean.PerferenceConstant;
import sp.phone.fragment.AlertDialogFragment;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

public class SettingsActivity extends SwipeBackAppCompatActivity implements
        PerferenceConstant {

    final private String ALERT_DIALOG_TAG = "alertdialog";
    private View view;
    private CompoundButton checkBoxDownimgNowifi;
    private RelativeLayout imageQualityChooser;
    private CompoundButton checkBoxDownAvatarNowifi;
    private CompoundButton nightMode;
    private CompoundButton showAnimation;
    private CompoundButton showSignature;
    private CompoundButton notification;
    private CompoundButton notificationSound;
    private CompoundButton showStatic;
    private CompoundButton showColortxt;
    private CompoundButton showNewweiba;
    private CompoundButton showLajibankuai;
    private CompoundButton showReplyButton;

    private CompoundButton swipeback;
    private RelativeLayout swipebackChooer;
    private TextView swipebackOptionInfoTextView;
    private TextView swipebackOptionChoiceTextView;

    private CompoundButton showIconMode;
    private CompoundButton refresh_after_post_setting_mode;

    private CompoundButton split = null;
    private CompoundButton replysplit = null;
    private CompoundButton ha = null;
    private CompoundButton fullscreen = null;
    private CompoundButton kitwebview = null;

    private RelativeLayout handsideQualityChooser;
    private RelativeLayout blackgunSoundChooser;
    private SeekBar fontSizeBar;
    private float defaultFontSize;
    private TextView fontTextView;
    private int defaultWebSize;
    private SeekBar webSizebar;
    private WebView websizeView;
    private TextView avatarSizeTextView;
    private TextView imageOptionInfoTextView;
    private TextView imageOptionChoiceTextView;

    private TextView blackgunSoundTextView;
    private TextView blackgunSoundChoiceTextView;

    private TextView handsideOptionInfoTextView;
    private TextView handsideOptionChoiceTextView;

    private TextView picshowtitle;
    private TextView optiontitle;
    private TextView uishowtitle;
    private View viewgone1, viewgone2, viewgone3;

    private ImageView avatarImage;
    private SeekBar avatarSeekBar;

    private boolean recentlychanged = false;

    // private MyGestureListener gestureListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // gestureListener = new MyGestureListener(this);

        initView();

    }

    void initView() {

        int orentation = ThemeManager.getInstance().screenOrentation;
        if (orentation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                || orentation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(orentation);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        //  ThemeManager.SetContextTheme(this);
        int layoutId = R.layout.settings;
        if (ActivityUtil.isMeizu())
            layoutId = R.layout.settings_meizu;
        view = getLayoutInflater().inflate(layoutId, null);

        getSupportActionBar().setTitle("设置");

        this.setContentView(view);
        PhoneConfiguration config = PhoneConfiguration.getInstance();

        picshowtitle = (TextView) findViewById(R.id.picshowtitle);
        optiontitle = (TextView) findViewById(R.id.optiontitle);
        uishowtitle = (TextView) findViewById(R.id.uishowtitle);
        viewgone1 = (View) findViewById(R.id.viewgone1);
        viewgone2 = (View) findViewById(R.id.viewgone2);
        viewgone3 = (View) findViewById(R.id.viewgone3);

        checkBoxDownimgNowifi = (CompoundButton) findViewById(R.id.checkBox_down_img_no_wifi);
        checkBoxDownimgNowifi.setChecked(config.downImgNoWifi);
        DownImgNoWifiChangedListener listener = new DownImgNoWifiChangedListener();
        checkBoxDownimgNowifi.setOnCheckedChangeListener(listener);

        imageOptionChoiceTextView = (TextView) findViewById(R.id.image_quality_text);
        imageOptionInfoTextView = (TextView) findViewById(R.id.image_quality_info);
        imageQualityChooser = (RelativeLayout) findViewById(R.id.image_quality_chooser);
        updateImageQualityChoiceText(config);
        ImageQualityChooserListener imageQualityChooserListener = new ImageQualityChooserListener();
        imageQualityChooser.setOnClickListener(imageQualityChooserListener);

        blackgunSoundChoiceTextView = (TextView) findViewById(R.id.blackgun_sound_text);
        blackgunSoundTextView = (TextView) findViewById(R.id.blackgun_sound_info);
        blackgunSoundChooser = (RelativeLayout) findViewById(R.id.blackgun_sound_chooser);
        updateBlackgunSoundChoiceText(config);
        BlackgunSoundChooserListener blackgunSoundChooserListener = new BlackgunSoundChooserListener();
        blackgunSoundChooser.setOnClickListener(blackgunSoundChooserListener);

        handsideOptionInfoTextView = (TextView) findViewById(R.id.lefthand_righthand_text);
        handsideOptionChoiceTextView = (TextView) findViewById(R.id.lefthand_righthand_info);
        handsideQualityChooser = (RelativeLayout) findViewById(R.id.lefthand_righthand_chooser);
        updateHandSideChoiceText(config);
        HandSideChooserListener handsideChooserListener = new HandSideChooserListener();
        handsideQualityChooser.setOnClickListener(handsideChooserListener);

        checkBoxDownAvatarNowifi = (CompoundButton) findViewById(R.id.checkBox_download_avatar_no_wifi);
        checkBoxDownAvatarNowifi.setChecked(config.downAvatarNoWifi);
        DownAvatarNowifiChangedListener AvatarListener = new DownAvatarNowifiChangedListener();
        checkBoxDownAvatarNowifi.setOnCheckedChangeListener(AvatarListener);

        nightMode = (CompoundButton) findViewById(R.id.checkBox_night_mode);
        nightMode
                .setChecked(ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT);
        nightMode.setOnCheckedChangeListener(new NightModeListener());

        showIconMode = (CompoundButton) findViewById(R.id.checkBox_icon_mode);
        showIconMode.setChecked(config.iconmode);
        showIconMode.setOnCheckedChangeListener(new IconModeListener());

        refresh_after_post_setting_mode = (CompoundButton) findViewById(R.id.refresh_after_post_setting_mode);
        refresh_after_post_setting_mode
                .setChecked(config.refresh_after_post_setting_mode);
        refresh_after_post_setting_mode
                .setOnCheckedChangeListener(new SettingRefreshAfterPostListener());

        showAnimation = (CompoundButton) findViewById(R.id.checkBox_show_animation);
        showAnimation.setChecked(config.showAnimation);
        showAnimation.setOnCheckedChangeListener(new ShowAnimationListener());
        /*
         * useViewCache = (CompoundButton)
		 * findViewById(R.id.checkBox_use_view_cache);
		 * useViewCache.setChecked(config.useViewCache);
		 * useViewCache.setOnCheckedChangeListener(new UseViewCacheListener());
		 */

        showSignature = (CompoundButton) findViewById(R.id.checkBox_show_signature);
        showSignature.setChecked(config.showSignature);
        showSignature.setOnCheckedChangeListener(new ShowSignatureListener());

        notificationSound = (CompoundButton) findViewById(R.id.checkBox_notification_sound);
        notificationSound
                .setOnCheckedChangeListener(new NotificationSoundChangedListener());
        notificationSound.setChecked(config.notificationSound);
        notificationSound.setEnabled(config.notification);

        notification = (CompoundButton) findViewById(R.id.checkBox_notification);
        notification
                .setOnCheckedChangeListener(new NotificationChangedListener(
                        notificationSound));
        notification.setChecked(config.notification);

		/*
         * uploadLocation = (CompoundButton)
		 * findViewById(R.id.checkBox_upload_location);
		 * uploadLocation.setChecked(config.uploadLocation);
		 * uploadLocation.setOnCheckedChangeListener(new
		 * UploadLocationListener());
		 */

        showStatic = (CompoundButton) findViewById(R.id.checkBox_show_static);
        showStatic.setChecked(config.showStatic);
        showStatic.setOnCheckedChangeListener(new ShowStaticListener());

        showReplyButton = (CompoundButton) findViewById(R.id.checkBox_addreplybutton);
        showReplyButton
                .setOnCheckedChangeListener(new ShowReplyButtonListener());
        showReplyButton.setChecked(config.showReplyButton);

        swipebackOptionChoiceTextView = (TextView) findViewById(R.id.swipe_textview);
        swipebackOptionInfoTextView = (TextView) findViewById(R.id.swipeback_position);
        swipebackChooer = (RelativeLayout) findViewById(R.id.swiperback_chooser);
        swipeback = (CompoundButton) findViewById(R.id.checkBox_swipeback);
        swipeback.setOnCheckedChangeListener(new SwipeBackButtonListener());
        swipeback.setChecked(config.swipeBack);
        updateSwipeBackChoiceText(config);
        SwipeBackChooserListener swipebackChooserListener = new SwipeBackChooserListener();
        swipebackChooer.setOnClickListener(swipebackChooserListener);


        showColortxt = (CompoundButton) findViewById(R.id.checkBox_color_txt);
        showColortxt.setChecked(config.showColortxt);
        showColortxt.setOnCheckedChangeListener(new showColortxtListener());

        showNewweiba = (CompoundButton) findViewById(R.id.checkBox_new_weiba);
        showNewweiba.setChecked(config.showNewweiba);
        showNewweiba.setOnCheckedChangeListener(new showNewweibaListener());

        showLajibankuai = (CompoundButton) findViewById(R.id.checkBox_show_lajibankuai);
        showLajibankuai.setChecked(config.showLajibankuai);
        showLajibankuai
                .setOnCheckedChangeListener(new showLajibankuaiListener());

        split = (CompoundButton) findViewById(R.id.checkBox_split);
        if (split != null) {
            boolean checked = true;
            if ((config.getUiFlag() & UI_FLAG_SPLIT) == 0) {
                checked = false;
            }
            split.setChecked(checked);
            split.setOnCheckedChangeListener(new SplitChangedListener());
        }
        replysplit = (CompoundButton) findViewById(R.id.checkBox_replysplit);
        if (replysplit != null) {
            boolean checked = true;
            if ((config.getUiFlag() & UI_FLAG_REPLYSPLIT) == 0) {
                checked = false;
            }
            replysplit.setChecked(checked);
            replysplit.setOnCheckedChangeListener(new ReplySplitChangedListener());
        }

        ha = (CompoundButton) findViewById(R.id.checkBox_ha);
        if (ha != null) {
            boolean checked = true;
            if ((config.getUiFlag() & UI_FLAG_HA) == 0) {
                checked = false;
            }
            ha.setChecked(checked);
            ha.setOnCheckedChangeListener(new HaChangedListener());
        }

        fullscreen = (CompoundButton) findViewById(R.id.checkBox_fullscreen);
        fullscreen.setChecked(config.fullscreen);
        fullscreen.setOnCheckedChangeListener(new fullscreenListener());

        kitwebview = (CompoundButton) findViewById(R.id.checkBox_kitwebview);
        kitwebview.setChecked(config.kitwebview);
        kitwebview.setOnCheckedChangeListener(new kitwebviewListener());

        fontTextView = (TextView) findViewById(R.id.textView_font_size);
        defaultFontSize = fontTextView.getTextSize();

        fontSizeBar = (SeekBar) findViewById(R.id.fontsize_seekBar);
        fontSizeBar.setMax(300);
        final float textSize = config.getTextSize();
        int progress = (int) (100.0f * textSize / defaultFontSize);
        fontSizeBar.setProgress(progress);
        fontSizeBar.setOnSeekBarChangeListener(new FontSizeListener());
        fontTextView.setTextSize(textSize);

        websizeView = (WebView) findViewById(R.id.websize_view);
        defaultWebSize = websizeView.getSettings().getDefaultFontSize();
        webSizebar = (SeekBar) findViewById(R.id.webszie_bar);
        webSizebar.setMax(300);
        final int webSize = config.getWebSize();
        progress = 100 * webSize / defaultWebSize;
        webSizebar.setProgress(progress);
        websizeView.getSettings().setDefaultFontSize(webSize);
        websizeView.setBackgroundColor(0);
        webSizebar.setOnSeekBarChangeListener(new WebSizeListener());

        progress = config.nikeWidth;
        avatarSizeTextView = (TextView) findViewById(R.id.textView_avatarsize);
        avatarImage = (ImageView) findViewById(R.id.avatarsize);
        Drawable defaultAvatar = getResources().getDrawable(
                R.drawable.default_avatar);
        Bitmap bitmap = ImageUtil.zoomImageByWidth(defaultAvatar, progress);
        avatarImage.setImageBitmap(bitmap);

        avatarSeekBar = (SeekBar) findViewById(R.id.avatarsize_seekBar);
        avatarSeekBar.setMax(200);
        avatarSeekBar.setProgress(progress);
        avatarSeekBar.setOnSeekBarChangeListener(new AvatarSizeListener());
        if (!split.isChecked() && !replysplit.isChecked()) {
            handsideQualityChooser.setVisibility(View.GONE);
        }
        if (!checkBoxDownimgNowifi.isChecked()) {
            imageQualityChooser.setVisibility(View.GONE);
        }
        if (!swipeback.isChecked()) {
            swipebackChooer.setVisibility(View.GONE);
        }
        if (!notificationSound.isChecked() || !notification.isChecked()) {
            blackgunSoundChooser.setVisibility(View.GONE);
        }
        if (!notification.isChecked()) {
            notificationSound.setVisibility(View.GONE);
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            handsideQualityChooser.setVisibility(View.GONE);
            split.setVisibility(View.GONE);
            replysplit.setVisibility(View.GONE);
            ha.setVisibility(View.GONE);
            viewgone1.setVisibility(View.GONE);
            viewgone2.setVisibility(View.GONE);
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            handsideQualityChooser.setVisibility(View.GONE);
            viewgone2.setVisibility(View.GONE);
            if ((config.getUiFlag() & UI_FLAG_HA) != 0) {
                int flag = PhoneConfiguration.getInstance().getUiFlag();
                flag = flag & ~UI_FLAG_HA;
                PhoneConfiguration.getInstance().setUiFlag(flag);
                SharedPreferences share = getSharedPreferences(PERFERENCE,
                        MODE_PRIVATE);
                Editor editor = share.edit();
                editor.putInt(UI_FLAG, flag);
                editor.apply();
            }
        }

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {// less
            // than
            // 4.4
            fullscreen.setVisibility(View.GONE);
            viewgone3.setVisibility(View.GONE);
        }
        updateThemeUI();
    }

    private void updateImageQualityChoiceText(PhoneConfiguration config) {
        switch (config.imageQuality) {
            case 0:
                imageOptionChoiceTextView
                        .setText(R.string.image_quality_option_original);
                break;
            case 1:
                imageOptionChoiceTextView
                        .setText(R.string.image_quality_option_small);
                break;
            case 2:
                imageOptionChoiceTextView
                        .setText(R.string.image_quality_option_medium);
                break;
            case 3:
                imageOptionChoiceTextView
                        .setText(R.string.image_quality_option_large);
                break;
        }
    }

    private void updateSwipeBackChoiceText(PhoneConfiguration config) {
        switch (config.swipeenablePosition) {
            case 0:
                swipebackOptionInfoTextView
                        .setText(R.string.swipeback_left);
                break;
            case 1:
                swipebackOptionInfoTextView
                        .setText(R.string.swipeback_right);
                break;
            case 2:
                swipebackOptionInfoTextView
                        .setText(R.string.swipeback_left_right);
                break;
            default:
                swipebackOptionInfoTextView
                        .setText(R.string.swipeback_left_right_bottom);
                break;
        }
    }

    private void updateHandSideChoiceText(PhoneConfiguration config) {
        switch (config.HandSide) {
            case 0:
                handsideOptionInfoTextView
                        .setText(R.string.righthand_option_original);
                break;
            case 1:
                handsideOptionInfoTextView
                        .setText(R.string.lefthand_option_original);
                break;
        }
    }

    private void updateBlackgunSoundChoiceText(PhoneConfiguration config) {
        switch (config.blackgunsound) {
            case 0:
                blackgunSoundChoiceTextView.setText(R.string.blackgun_sound_0);
                break;
            case 1:
                blackgunSoundChoiceTextView.setText(R.string.blackgun_sound_1);
                break;
            case 2:
                blackgunSoundChoiceTextView.setText(R.string.blackgun_sound_2);
                break;
            case 3:
                blackgunSoundChoiceTextView.setText(R.string.blackgun_sound_3);
                break;
        }
    }

    private void updateThemeUI() {
        if (nightMode.isChecked()) {
            websizeView.loadDataWithBaseURL(null,
                    "<font style='color:#424952;'>"
                            + getString(R.string.websize_sample_text)
                            + "</font>", "text/html", "utf-8", "");
        } else {
            websizeView.loadDataWithBaseURL(null,
                    "<font style='color:#000000;'>"
                            + getString(R.string.websize_sample_text)
                            + "</font>", "text/html", "utf-8", "");
        }
        int fgColor = getResources().getColor(ThemeManager.getInstance().getForegroundColor());
        checkBoxDownimgNowifi.setTextColor(fgColor);
        fullscreen.setTextColor(fgColor);
        kitwebview.setTextColor(fgColor);
        checkBoxDownAvatarNowifi.setTextColor(fgColor);
        nightMode.setTextColor(fgColor);
        showAnimation.setTextColor(fgColor);

        showIconMode.setTextColor(fgColor);
        refresh_after_post_setting_mode.setTextColor(fgColor);
        // useViewCache.setTextColor(fgColor);
        showSignature.setTextColor(fgColor);
        notification.setTextColor(fgColor);
        notificationSound.setTextColor(fgColor);
        // uploadLocation.setTextColor(fgColor);
        showStatic.setTextColor(fgColor);
        showReplyButton.setTextColor(fgColor);
        swipeback.setTextColor(fgColor);
        showColortxt.setTextColor(fgColor);
        showNewweiba.setTextColor(fgColor);
        showLajibankuai.setTextColor(fgColor);
        if (split != null)
            split.setTextColor(fgColor);
        if (ha != null)
            ha.setTextColor(fgColor);
        if (replysplit != null)
            replysplit.setTextColor(fgColor);

        fontTextView.setTextColor(fgColor);
        avatarSizeTextView.setTextColor(fgColor);
        imageOptionChoiceTextView.setTextColor(fgColor);
        imageOptionInfoTextView.setTextColor(fgColor);
        handsideOptionInfoTextView.setTextColor(fgColor);
        handsideOptionChoiceTextView.setTextColor(fgColor);
        blackgunSoundTextView.setTextColor(fgColor);
        blackgunSoundChoiceTextView.setTextColor(fgColor);
        swipebackOptionInfoTextView.setTextColor(fgColor);
        swipebackOptionChoiceTextView.setTextColor(fgColor);
        view.setBackgroundResource(ThemeManager.getInstance().getBackgroundColor());

        picshowtitle.setTextColor(fgColor);
        optiontitle.setTextColor(fgColor);
        uishowtitle.setTextColor(fgColor);
        if (checkBoxDownimgNowifi instanceof Switch) {
            XmlResourceParser xrp = getResources().getXml(ThemeManager.getInstance().getSwitchBackground());
            try {
                ColorStateList list = ColorStateList.createFromXml(getResources(), xrp);
                for (View view : new View[]{
                        checkBoxDownimgNowifi,
                        checkBoxDownAvatarNowifi,
                        showReplyButton,
                        nightMode,
                        swipeback,
                        showAnimation,
                        showSignature,
                        notification,
                        notificationSound,
                        showStatic,
                        showColortxt,
                        showLajibankuai,
                        showNewweiba,
                        showIconMode,
                        refresh_after_post_setting_mode,
                        split,
                        replysplit,
                        ha,
                        fullscreen,
                        kitwebview}) {
                    DrawableCompat.setTintList(((Switch)view).getTrackDrawable(), list);
                }
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
            xrp.close();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int flags = 15;
        /*
		 * ActionBar.DISPLAY_SHOW_HOME; flags |= ActionBar.DISPLAY_USE_LOGO;
		 * flags |= ActionBar.DISPLAY_SHOW_TITLE; flags |=
		 * ActionBar.DISPLAY_HOME_AS_UP; flags |= ActionBar.DISPLAY_SHOW_CUSTOM;
		 */
        // final ActionBar bar = getActionBar();
        // bar.setDisplayOptions(flags);
        ReflectionUtil.actionBar_setDisplayOption(this, flags);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        if (PhoneConfiguration.getInstance().fullscreen) {
            ActivityUtil.getInstance().setFullScreen(view);
        }
        super.onResume();
    }

    class NightModeListener implements OnCheckedChangeListener,
            PerferenceConstant {

        @Override
        public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);

            Editor editor = share.edit();
            editor.putBoolean(NIGHT_MODE, arg1);
            editor.apply();
            int mode = ThemeManager.MODE_NORMAL;
            if (arg1)
                mode = ThemeManager.MODE_NIGHT;
            ThemeManager.getInstance().setMode(mode);
            updateThemeUI();
        }

    }

    class ShowAnimationListener implements OnCheckedChangeListener,
            PerferenceConstant {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            PhoneConfiguration.getInstance().showAnimation = isChecked;
            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);

            Editor editor = share.edit();
            editor.putBoolean(SHOW_ANIMATION, isChecked);
            editor.apply();

        }

    }

    class SettingRefreshAfterPostListener implements OnCheckedChangeListener,
            PerferenceConstant {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            PhoneConfiguration.getInstance().refresh_after_post_setting_mode = isChecked;
            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);
            PhoneConfiguration.getInstance().setRefreshAfterPost(false);
            Editor editor = share.edit();
            editor.putBoolean(REFRESH_AFTERPOST_SETTING_MODE, isChecked);
            editor.apply();

        }

    }

    class IconModeListener implements OnCheckedChangeListener,
            PerferenceConstant {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     final boolean isChecked) {

            if (!recentlychanged) {
                String alertString = getString(R.string.change_icon_string);
                final AlertDialogFragment f = AlertDialogFragment
                        .create(alertString);
                f.setOkListener(new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PhoneConfiguration.getInstance().iconmode = isChecked;
                        SharedPreferences share = getSharedPreferences(
                                PERFERENCE, MODE_PRIVATE);
                        String addFidStr = share.getString(ADD_FID, "");
                        List<Board> addFidList = null;
                        BoardCategory addFid = new BoardCategory();
                        int iconint;
                        if (isChecked) {
                            iconint = R.drawable.oldpdefault;
                        } else {
                            iconint = R.drawable.pdefault;
                        }
                        if (!StringUtil.isEmpty(addFidStr)) {
                            addFidList = JSON
                                    .parseArray(addFidStr, Board.class);
                            if (addFidList != null) {
                                int i = 11;// 新增大板块后此处+1
                                for (int j = 0; j < addFidList.size(); j++) {
                                    addFid.add(new Board(i, addFidList.get(j)
                                            .getUrl(), addFidList.get(j)
                                            .getName(), iconint));
                                }
                                addFidStr = JSON.toJSONString(addFid
                                        .getBoardList());
                            }
                        }
                        Editor editor = share.edit();
                        editor.putBoolean(SHOW_ICON_MODE, isChecked);
                        editor.putString(RECENT_BOARD, "");
                        editor.putString(ADD_FID, addFidStr);
                        editor.apply();

                    }

                });
                f.setCancleListener(new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recentlychanged = true;
                        showIconMode.setChecked(!isChecked);
                        f.dismiss();
                    }

                });
                f.show(getSupportFragmentManager(), ALERT_DIALOG_TAG);

            } else {
                recentlychanged = false;
            }
        }

    }

    class UseViewCacheListener implements OnCheckedChangeListener,
            PerferenceConstant {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            PhoneConfiguration.getInstance().useViewCache = isChecked;
            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);

            Editor editor = share.edit();
            editor.putBoolean(USE_VIEW_CACHE, isChecked);
            editor.apply();

            if (isChecked) {
                new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle(R.string.prompt)
                        .setMessage(R.string.view_cache_tips)
                        .setPositiveButton(R.string.i_know, null).show();
            }

        }

    }

    class ShowSignatureListener implements OnCheckedChangeListener,
            PerferenceConstant {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            PhoneConfiguration.getInstance().showSignature = isChecked;
            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);

            Editor editor = share.edit();
            editor.putBoolean(SHOW_SIGNATURE, isChecked);
            editor.apply();

        }

    }

    class UploadLocationListener implements OnCheckedChangeListener,
            PerferenceConstant {
        private final String TAG = "UploadLocationAlert";

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            if (!isChecked) {
                changeTo(isChecked);
            } else {

                final CompoundButton b = buttonView;
                String alertString = getString(R.string.set_upload_location_alert);
                AlertDialogFragment f = AlertDialogFragment.create(alertString);
                f.setOkListener(new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        changeTo(true);

                    }

                });
                f.setCancleListener(new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        b.setChecked(false);

                    }

                });

                FragmentActivity a = (FragmentActivity) buttonView.getContext();
                f.show(a.getSupportFragmentManager(), TAG);
            }

        }

        private void changeTo(boolean isChecked) {
            PhoneConfiguration.getInstance().uploadLocation = isChecked;
            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);

            Editor editor = share.edit();
            editor.putBoolean(UPLOAD_LOCATION, isChecked);
            editor.apply();
        }

    }

    class ShowStaticListener implements OnCheckedChangeListener,
            PerferenceConstant {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            PhoneConfiguration.getInstance().showStatic = isChecked;
            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);

            Editor editor = share.edit();
            editor.putBoolean(SHOW_STATIC, isChecked);
            editor.apply();

        }

    }

    class ShowReplyButtonListener implements OnCheckedChangeListener,
            PerferenceConstant {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            PhoneConfiguration.getInstance().showReplyButton = isChecked;
            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);

            Editor editor = share.edit();
            editor.putBoolean(SHOW_REPLYBUTTON, isChecked);
            editor.apply();

        }

    }

    class SwipeBackButtonListener implements OnCheckedChangeListener,
            PerferenceConstant {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            PhoneConfiguration.getInstance().swipeBack = isChecked;
            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);

            Editor editor = share.edit();
            editor.putBoolean(SWIPEBACK, isChecked);
            editor.apply();
            if (isChecked) {
                swipebackChooer.setVisibility(View.VISIBLE);
                final float density = getResources().getDisplayMetrics().density;// 获取屏幕密度PPI
                getSwipeBackLayout().setEdgeSize(
                        (int) (MY_EDGE_SIZE * density + 0.5f));// 10dp
                int pos = SwipeBackLayout.EDGE_ALL;
                switch (PhoneConfiguration.getInstance().swipeenablePosition) {
                    case 0:
                        pos = SwipeBackLayout.EDGE_LEFT;
                        break;
                    case 1:
                        pos = SwipeBackLayout.EDGE_RIGHT;
                        break;
                    case 2:
                        pos = SwipeBackLayout.EDGE_LEFT | SwipeBackLayout.EDGE_RIGHT;
                        break;
                    default:
                        pos = SwipeBackLayout.EDGE_ALL;
                        break;
                }
                getSwipeBackLayout().setEdgeTrackingEnabled(pos);
            } else {
                swipebackChooer.setVisibility(View.GONE);
                getSwipeBackLayout().setEdgeSize(0);
            }
        }

    }

    class showColortxtListener implements OnCheckedChangeListener,
            PerferenceConstant {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            PhoneConfiguration.getInstance().showColortxt = isChecked;
            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);
            if (isChecked) {
                showToast(R.string.showColortxtWarn);
            }
            Editor editor = share.edit();
            editor.putBoolean(SHOW_COLORTXT, isChecked);
            editor.apply();

        }
    }

    class showNewweibaListener implements OnCheckedChangeListener,
            PerferenceConstant {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            PhoneConfiguration.getInstance().showNewweiba = isChecked;
            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);
            if (isChecked) {
                showToast(R.string.showNewweibaWarn);
            }
            Editor editor = share.edit();
            editor.putBoolean(SHOW_NEWWEIBA, isChecked);
            editor.apply();

        }
    }

    class showLajibankuaiListener implements OnCheckedChangeListener,
            PerferenceConstant {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            PhoneConfiguration.getInstance().showLajibankuai = isChecked;
            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);

            Editor editor = share.edit();
            editor.putBoolean(SHOW_LAJIBANKUAI, isChecked);
            editor.apply();

        }
    }

    class fullscreenListener implements OnCheckedChangeListener,
            PerferenceConstant {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            PhoneConfiguration.getInstance().fullscreen = isChecked;
            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);

            Editor editor = share.edit();
            editor.putBoolean(FULLSCREENMODE, isChecked);
            editor.apply();
            if (isChecked) {
                ActivityUtil.getInstance().setFullScreen(view);
            } else {
                ActivityUtil.getInstance().setNormalScreen(view);
            }
        }
    }

    class kitwebviewListener implements OnCheckedChangeListener,
            PerferenceConstant {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            PhoneConfiguration.getInstance().kitwebview = isChecked;
            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);

            Editor editor = share.edit();
            editor.putBoolean(KITWEBVIEWMODE, isChecked);
            editor.apply();
            if (isChecked) {
                showToast(R.string.kitwebviewinfo);
            }
        }
    }

    class DownImgNoWifiChangedListener implements OnCheckedChangeListener,
            PerferenceConstant {

        @Override
        public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
            if (arg1) {
                imageQualityChooser.setVisibility(View.VISIBLE);
            } else {
                imageQualityChooser.setVisibility(View.GONE);
            }
            PhoneConfiguration.getInstance().downImgNoWifi = arg1;
            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);

            Editor editor = share.edit();
            editor.putBoolean(DOWNLOAD_IMG_NO_WIFI, arg1);
            editor.apply();

        }

    }

    class ImageQualityChooserListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    SettingsActivity.this);
            String[] items = new String[]{
                    getString(R.string.image_quality_option_original),
                    getString(R.string.image_quality_option_small),
                    getString(R.string.image_quality_option_medium),
                    getString(R.string.image_quality_option_large)};
            builder.setTitle(R.string.image_quality_chooser_prompt);
            builder.setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    PhoneConfiguration.getInstance().imageQuality = which;
                    SharedPreferences share = getSharedPreferences(PERFERENCE,
                            MODE_PRIVATE);
                    Editor editor = share.edit();
                    editor.putInt(DOWNLOAD_IMG_QUALITY_NO_WIFI, which);
                    editor.apply();
                    updateImageQualityChoiceText(PhoneConfiguration
                            .getInstance());
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface arg0) {
                    dialog.dismiss();
                    if (PhoneConfiguration.getInstance().fullscreen) {
                        ActivityUtil.getInstance().setFullScreen(view);
                    }
                }

            });
            showToast(R.string.image_quality_claim);
        }

    }

    class BlackgunSoundChooserListener implements
            android.view.View.OnClickListener {

        MediaPlayer mp = new MediaPlayer();

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    SettingsActivity.this);
            String[] items = new String[]{
                    getString(R.string.blackgun_sound_0),
                    getString(R.string.blackgun_sound_1),
                    getString(R.string.blackgun_sound_2),
                    getString(R.string.blackgun_sound_3)};
            builder.setTitle(R.string.blackgun_sound_chooser_prompt);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AudioManager audioManager = (AudioManager) view
                            .getContext().getSystemService(
                                    Context.AUDIO_SERVICE);
                    AssetFileDescriptor afd = null;
                    switch (which) {
                        case 0:
                            afd = null;
                            Uri ringToneUri = null;
                            ringToneUri = RingtoneManager
                                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            if (ringToneUri != null
                                    && audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                                try {
                                    mp.reset();
                                    mp.setDataSource(view.getContext(), ringToneUri);
                                    mp.prepare();
                                    mp.start();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case 1:
                            afd = getResources().openRawResourceFd(R.raw.taijun);
                            if (afd != null
                                    && audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
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
                            break;
                        case 2:
                            afd = getResources().openRawResourceFd(
                                    R.raw.balckgunoftaijun);
                            if (afd != null
                                    && audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
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
                            break;
                        case 3:
                            afd = getResources().openRawResourceFd(
                                    R.raw.balckgunofyou);
                            if (afd != null
                                    && audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
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
                            break;
                    }
                    PhoneConfiguration.getInstance().blackgunsound = which;
                    SharedPreferences share = getSharedPreferences(PERFERENCE,
                            MODE_PRIVATE);
                    Editor editor = share.edit();
                    editor.putInt(BLACKGUN_SOUND, which);
                    editor.apply();
                    updateBlackgunSoundChoiceText(PhoneConfiguration
                            .getInstance());
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface arg0) {
                    dialog.dismiss();

                    if (PhoneConfiguration.getInstance().fullscreen) {
                        ActivityUtil.getInstance().setFullScreen(view);
                    }
                }

            });
        }

    }

    class SwipeBackChooserListener implements android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    SettingsActivity.this);
            String[] items = new String[]{
                    getString(R.string.swipeback_left),
                    getString(R.string.swipeback_right),
                    getString(R.string.swipeback_left_right),
                    getString(R.string.swipeback_left_right_bottom)};
            builder.setTitle(R.string.swipeback_chooser_prompt);
            builder.setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PhoneConfiguration.getInstance().swipeenablePosition = which;
                    SharedPreferences share = getSharedPreferences(PERFERENCE,
                            MODE_PRIVATE);
                    Editor editor = share.edit();
                    editor.putInt(SWIPEBACKPOSITION, which);
                    editor.apply();
                    int pos = SwipeBackLayout.EDGE_ALL;
                    switch (which) {
                        case 0:
                            pos = SwipeBackLayout.EDGE_LEFT;
                            break;
                        case 1:
                            pos = SwipeBackLayout.EDGE_RIGHT;
                            break;
                        case 2:
                            pos = SwipeBackLayout.EDGE_LEFT | SwipeBackLayout.EDGE_RIGHT;
                            break;
                        default:
                            pos = SwipeBackLayout.EDGE_ALL;
                            break;
                    }
                    getSwipeBackLayout().setEdgeTrackingEnabled(pos);
                    updateSwipeBackChoiceText(PhoneConfiguration.getInstance());
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface arg0) {

                    dialog.dismiss();

                    if (PhoneConfiguration.getInstance().fullscreen) {
                        ActivityUtil.getInstance().setFullScreen(view);
                    }
                }

            });
        }

    }

    class HandSideChooserListener implements android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    SettingsActivity.this);
            String[] items = new String[]{
                    getString(R.string.lefthand_option_original),
                    getString(R.string.righthand_option_original)};
            builder.setTitle(R.string.lefthand_righthand_chooser_prompt);
            builder.setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    PhoneConfiguration.getInstance().HandSide = Math
                            .abs(1 - which);
                    SharedPreferences share = getSharedPreferences(PERFERENCE,
                            MODE_PRIVATE);
                    Editor editor = share.edit();
                    editor.putInt(HANDSIDE, Math.abs(1 - which));
                    editor.apply();
                    updateHandSideChoiceText(PhoneConfiguration.getInstance());
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface arg0) {

                    dialog.dismiss();

                    if (PhoneConfiguration.getInstance().fullscreen) {
                        ActivityUtil.getInstance().setFullScreen(view);
                    }
                }

            });
        }

    }

    class DownAvatarNowifiChangedListener implements OnCheckedChangeListener,
            PerferenceConstant {

        @Override
        public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

            PhoneConfiguration.getInstance().downAvatarNoWifi = arg1;
            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);

            Editor editor = share.edit();
            editor.putBoolean(DOWNLOAD_AVATAR_NO_WIFI, arg1);
            editor.apply();

        }

    }

    class NotificationChangedListener implements OnCheckedChangeListener,
            PerferenceConstant {
        final CompoundButton child;

        public NotificationChangedListener(CompoundButton child) {
            this.child = child;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            if (isChecked) {
                notificationSound.setVisibility(View.VISIBLE);
                if (notificationSound.isChecked()) {
                    blackgunSoundChooser.setVisibility(View.VISIBLE);
                } else {
                    blackgunSoundChooser.setVisibility(View.GONE);
                }
            } else {
                notificationSound.setVisibility(View.GONE);
                blackgunSoundChooser.setVisibility(View.GONE);
            }
            PhoneConfiguration.getInstance().notification = isChecked;
            child.setEnabled(isChecked);
            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);

            Editor editor = share.edit();
            editor.putBoolean(ENABLE_NOTIFIACTION, isChecked);
            editor.apply();

        }

    }

    class NotificationSoundChangedListener implements OnCheckedChangeListener,
            PerferenceConstant {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            if (isChecked) {
                blackgunSoundChooser.setVisibility(View.VISIBLE);
            } else {
                blackgunSoundChooser.setVisibility(View.GONE);
            }
            PhoneConfiguration.getInstance().notificationSound = isChecked;

            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);

            Editor editor = share.edit();
            editor.putBoolean(NOTIFIACTION_SOUND, isChecked);
            editor.apply();

        }

    }

    class FontSizeListener implements SeekBar.OnSeekBarChangeListener,
            PerferenceConstant {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

            if (progress != 0)
                fontTextView.setTextSize(defaultFontSize * progress / 100.0f);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {


        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            float textSize = defaultFontSize * seekBar.getProgress() / 100.0f;
            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);

            Editor editor = share.edit();
            editor.putFloat(TEXT_SIZE, textSize);
            editor.apply();
            PhoneConfiguration.getInstance().setTextSize(textSize);
        }

    }

    class WebSizeListener implements SeekBar.OnSeekBarChangeListener,
            PerferenceConstant {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if (progress != 0)
                websizeView.getSettings().setDefaultFontSize(
                        (int) (defaultWebSize * progress / 100.0f));

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {


        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // int textSize = (int)
            // (defaultWebSize*seekBar.getProgress()/100.0f);

            int webSize = (int) (defaultWebSize * seekBar.getProgress() / 100.0f);
            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);

            Editor editor = share.edit();

            editor.putInt(WEB_SIZE, webSize);
            editor.apply();

            PhoneConfiguration.getInstance().setWebSize(webSize);

        }

    }

    class AvatarSizeListener implements SeekBar.OnSeekBarChangeListener,
            PerferenceConstant {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

            if (2 > progress)
                progress = 2;
            Drawable defaultAvatar = getResources().getDrawable(
                    R.drawable.default_avatar);
            Bitmap bitmap = ImageUtil.zoomImageByWidth(defaultAvatar, progress);
            try {
                ImageUtil.recycleImageView(avatarImage);
                avatarImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            if (2 > progress)
                progress = 2;
            PhoneConfiguration.getInstance().nikeWidth = progress;
            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);

            Editor editor = share.edit();
            editor.putInt(NICK_WIDTH, progress);
            editor.apply();

        }

    }

    class HaChangedListener implements OnCheckedChangeListener,
            PerferenceConstant {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {

            int flag = PhoneConfiguration.getInstance().getUiFlag();
            if (isChecked) {
                flag |= UI_FLAG_HA;
            } else {
                flag = flag & ~UI_FLAG_HA;
            }

            PhoneConfiguration.getInstance().setUiFlag(flag);

            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);

            Editor editor = share.edit();
            editor.putInt(UI_FLAG, flag);
            editor.apply();

        }

    }

    class SplitChangedListener implements OnCheckedChangeListener,
            PerferenceConstant {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {

            int flag = PhoneConfiguration.getInstance().getUiFlag();
            if (isChecked) {
                flag |= UI_FLAG_SPLIT;
                handsideQualityChooser.setVisibility(View.VISIBLE);
            } else {
                flag = flag & ~UI_FLAG_SPLIT;
                if (!replysplit.isChecked()) {
                    handsideQualityChooser.setVisibility(View.GONE);
                }
            }

            PhoneConfiguration.getInstance().setUiFlag(flag);

            SharedPreferences share = getSharedPreferences(PERFERENCE,
                    MODE_PRIVATE);

            Editor editor = share.edit();
            editor.putInt(UI_FLAG, flag);
            editor.apply();

        }

    }

    class ReplySplitChangedListener implements OnCheckedChangeListener,
            PerferenceConstant {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {

            int flag = PhoneConfiguration.getInstance().getUiFlag();
            if (isChecked) {
                flag |= UI_FLAG_REPLYSPLIT;
                handsideQualityChooser.setVisibility(View.VISIBLE);
            } else {
                flag = flag & ~UI_FLAG_REPLYSPLIT;
                if (!split.isChecked()) {
                    handsideQualityChooser.setVisibility(View.GONE);
                }
            }

            PhoneConfiguration.getInstance().setUiFlag(flag);

            SharedPreferences share = getSharedPreferences(PERFERENCE, MODE_PRIVATE);

            Editor editor = share.edit();
            editor.putInt(UI_FLAG, flag);
            editor.apply();

        }

    }

}
