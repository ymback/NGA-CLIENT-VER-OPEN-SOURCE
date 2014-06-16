package gov.anzong.androidnga2.activity;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;

import com.alibaba.fastjson.JSON;

import gov.anzong.androidnga2.R;
import sp.phone.bean.Board;
import sp.phone.bean.BoardHolder;
import sp.phone.bean.PerferenceConstant;
import sp.phone.fragment.AlertDialogFragment;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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

	private CompoundButton showIconMode;

	private CompoundButton split = null;
	private CompoundButton replysplit = null;
	private CompoundButton ha = null;
	private CompoundButton fullscreen = null;

	private RelativeLayout handsideQualityChooser;
	private RelativeLayout playModeOptionChooser;
	private RelativeLayout blackgunSoundChooser;
	private Toast toast;
	private SeekBar fontSizeBar;
	private float defaultFontSize;
	private TextView fontTextView;
	private int defaultWebSize;
	private SeekBar webSizebar;
	private WebView websizeView;
	private TextView avatarSizeTextView;
	private TextView imageOptionInfoTextView;
	private TextView imageOptionChoiceTextView;

	private TextView playModeOptionTextView;
	private TextView playModeOptionChoiceTextView;

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
	
	private boolean recentlychanged=false;

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
		ThemeManager.SetContextTheme(this);
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

		playModeOptionChoiceTextView = (TextView) findViewById(R.id.mediaplayer_choose_text);
		playModeOptionTextView = (TextView) findViewById(R.id.mediaplayer_choose_info);
		playModeOptionChooser = (RelativeLayout) findViewById(R.id.mediaplayer_choose_layout);
		updatePlayModeOptionChoiceText(config);
		PlayModeChooserListener playModeChooserListener = new PlayModeChooserListener();
		playModeOptionChooser.setOnClickListener(playModeChooserListener);

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
			replysplit
					.setOnCheckedChangeListener(new ReplySplitChangedListener());
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

		if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {// less
																			// than
																			// 4.4
			fullscreen.setVisibility(View.GONE);
			viewgone3.setVisibility(View.GONE);
		}
		updateThemeUI();
	}

	private void updatePlayModeOptionChoiceText(PhoneConfiguration config) {
		switch (config.playMode) {
		case 0:
			playModeOptionChoiceTextView.setText(R.string.play_all);
			break;
		case 1:
			playModeOptionChoiceTextView.setText(R.string.play_all_not_acfun);
			break;
		case 2:
			playModeOptionChoiceTextView
					.setText(R.string.play_all_not_bilibili);
			break;
		case 3:
			playModeOptionChoiceTextView.setText(R.string.play_all_not_acbili);
			break;
		case 4:
			playModeOptionChoiceTextView.setText(R.string.play_none);
			break;
		}
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
		int fgColor = getResources().getColor(
				ThemeManager.getInstance().getForegroundColor());
		checkBoxDownimgNowifi.setTextColor(fgColor);
		fullscreen.setTextColor(fgColor);
		checkBoxDownAvatarNowifi.setTextColor(fgColor);
		nightMode.setTextColor(fgColor);
		showAnimation.setTextColor(fgColor);

		showIconMode.setTextColor(fgColor);
		// useViewCache.setTextColor(fgColor);
		showSignature.setTextColor(fgColor);
		notification.setTextColor(fgColor);
		notificationSound.setTextColor(fgColor);
		// uploadLocation.setTextColor(fgColor);
		showStatic.setTextColor(fgColor);
		showReplyButton.setTextColor(fgColor);
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
		playModeOptionTextView.setTextColor(fgColor);
		playModeOptionChoiceTextView.setTextColor(fgColor);
		blackgunSoundTextView.setTextColor(fgColor);
		blackgunSoundChoiceTextView.setTextColor(fgColor);
		view.setBackgroundResource(ThemeManager.getInstance()
				.getBackgroundColor());

		picshowtitle.setTextColor(fgColor);
		optiontitle.setTextColor(fgColor);
		uishowtitle.setTextColor(fgColor);
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

	class NightModeListener implements OnCheckedChangeListener,
			PerferenceConstant {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			SharedPreferences share = getSharedPreferences(PERFERENCE,
					MODE_PRIVATE);

			Editor editor = share.edit();
			editor.putBoolean(NIGHT_MODE, arg1);
			editor.commit();
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
			editor.commit();

		}

	}

	class IconModeListener implements OnCheckedChangeListener,
			PerferenceConstant {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				final boolean isChecked) {

			if(recentlychanged==false){
				String alertString = getString(R.string.change_icon_string);
				final AlertDialogFragment f = AlertDialogFragment.create(alertString);
				f.setOkListener(new OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						PhoneConfiguration.getInstance().iconmode = isChecked;
						SharedPreferences share = getSharedPreferences(PERFERENCE,
								MODE_PRIVATE);
						Editor editor = share.edit();
						editor.putBoolean(SHOW_ICON_MODE, isChecked);
						editor.putString(RECENT_BOARD, "");
						editor.putString(ADD_FID, "");
						editor.commit();
						Intent iareboot = getBaseContext().getPackageManager()
								.getLaunchIntentForPackage(getBaseContext().getPackageName());
						iareboot.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						f.dismiss();
						startActivity(iareboot);
						
					}
					
				});
				f.setCancleListener(new OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						recentlychanged=true;	
						showIconMode.setChecked(!isChecked);
						f.dismiss();
					}
					
				});
				f.show(getSupportFragmentManager(), ALERT_DIALOG_TAG);
				
			}else{
				recentlychanged=false;
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
			editor.commit();

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
			editor.commit();

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
			editor.commit();
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
			editor.commit();

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
			editor.commit();

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
				if (toast != null) {
					toast.setText(R.string.showColortxtWarn);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.show();
				} else {
					toast = Toast.makeText(SettingsActivity.this,
							R.string.showColortxtWarn, Toast.LENGTH_SHORT);
					toast.show();
				}
			}
			Editor editor = share.edit();
			editor.putBoolean(SHOW_COLORTXT, isChecked);
			editor.commit();

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
				if (toast != null) {
					toast.setText(R.string.showNewweibaWarn);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.show();
				} else {
					toast = Toast.makeText(SettingsActivity.this,
							R.string.showNewweibaWarn, Toast.LENGTH_SHORT);
					toast.show();
				}
			}
			Editor editor = share.edit();
			editor.putBoolean(SHOW_NEWWEIBA, isChecked);
			editor.commit();

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
			editor.commit();

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
			editor.commit();
			if (isChecked) {
				ActivityUtil.getInstance().setFullScreen(view);
			} else {
				ActivityUtil.getInstance().setNormalScreen(view);
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
			editor.commit();

		}

	}

	class ImageQualityChooserListener implements
			android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					SettingsActivity.this);
			String[] items = new String[] {
					getString(R.string.image_quality_option_original),
					getString(R.string.image_quality_option_small),
					getString(R.string.image_quality_option_medium),
					getString(R.string.image_quality_option_large) };
			builder.setTitle(R.string.image_quality_chooser_prompt);
			builder.setItems(items, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					PhoneConfiguration.getInstance().imageQuality = which;
					SharedPreferences share = getSharedPreferences(PERFERENCE,
							MODE_PRIVATE);
					Editor editor = share.edit();
					editor.putInt(DOWNLOAD_IMG_QUALITY_NO_WIFI, which);
					editor.commit();
					updateImageQualityChoiceText(PhoneConfiguration
							.getInstance());
				}
			});
			final AlertDialog dialog = builder.create();
			dialog.show();
			dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface arg0) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					if (PhoneConfiguration.getInstance().fullscreen) {
						ActivityUtil.getInstance().setFullScreen(view);
					}
				}

			});
			if (toast != null) {
				toast.setText(R.string.image_quality_claim);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
			} else {
				toast = Toast.makeText(SettingsActivity.this,
						R.string.image_quality_claim, Toast.LENGTH_SHORT);
				toast.show();
			}
		}

	}

	class PlayModeChooserListener implements android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					SettingsActivity.this);
			String[] items = new String[] { getString(R.string.play_all),
					getString(R.string.play_all_not_acfun),
					getString(R.string.play_all_not_bilibili),
					getString(R.string.play_all_not_acbili),
					getString(R.string.play_none) };
			builder.setTitle(R.string.media_player_chooser_prompt);
			builder.setItems(items, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					PhoneConfiguration.getInstance().playMode = which;
					SharedPreferences share = getSharedPreferences(PERFERENCE,
							MODE_PRIVATE);
					Editor editor = share.edit();
					editor.putInt(PLAY_MODE, which);
					editor.commit();
					updatePlayModeOptionChoiceText(PhoneConfiguration
							.getInstance());
				}
			});
			final AlertDialog dialog = builder.create();
			dialog.show();
			dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface arg0) {
					// TODO Auto-generated method stub
					dialog.dismiss();

					if (PhoneConfiguration.getInstance().fullscreen) {
						ActivityUtil.getInstance().setFullScreen(view);
					}
				}

			});
		}

	}

	class BlackgunSoundChooserListener implements
			android.view.View.OnClickListener {

		MediaPlayer mp = new MediaPlayer();

		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					SettingsActivity.this);
			String[] items = new String[] {
					getString(R.string.blackgun_sound_0),
					getString(R.string.blackgun_sound_1),
					getString(R.string.blackgun_sound_2),
					getString(R.string.blackgun_sound_3) };
			builder.setTitle(R.string.blackgun_sound_chooser_prompt);
			builder.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
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
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
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
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
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
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
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
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
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
					editor.commit();
					updateBlackgunSoundChoiceText(PhoneConfiguration
							.getInstance());
				}
			});
			final AlertDialog dialog = builder.create();
			dialog.show();
			dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface arg0) {
					// TODO Auto-generated method stub
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
			String[] items = new String[] {
					getString(R.string.lefthand_option_original),
					getString(R.string.righthand_option_original) };
			builder.setTitle(R.string.lefthand_righthand_chooser_prompt);
			builder.setItems(items, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					PhoneConfiguration.getInstance().HandSide = Math
							.abs(1 - which);
					SharedPreferences share = getSharedPreferences(PERFERENCE,
							MODE_PRIVATE);
					Editor editor = share.edit();
					editor.putInt(HANDSIDE, Math.abs(1 - which));
					editor.commit();
					updateHandSideChoiceText(PhoneConfiguration.getInstance());
				}
			});
			final AlertDialog dialog = builder.create();
			dialog.show();
			dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface arg0) {
					// TODO Auto-generated method stub
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
			editor.commit();

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
			editor.commit();

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
			editor.commit();

		}

	}

	class FontSizeListener implements SeekBar.OnSeekBarChangeListener,
			PerferenceConstant {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			if (progress != 0)
				fontTextView.setTextSize(defaultFontSize * progress / 100.0f);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			float textSize = defaultFontSize * seekBar.getProgress() / 100.0f;
			SharedPreferences share = getSharedPreferences(PERFERENCE,
					MODE_PRIVATE);

			Editor editor = share.edit();
			editor.putFloat(TEXT_SIZE, textSize);
			editor.commit();
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
			// TODO Auto-generated method stub

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
			editor.commit();

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
			editor.commit();

		}

	}

	@Override
	protected void onResume() {
		if (PhoneConfiguration.getInstance().fullscreen) {
			ActivityUtil.getInstance().setFullScreen(view);
		}
		super.onResume();
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
			editor.commit();

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
			editor.commit();

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

			SharedPreferences share = getSharedPreferences(PERFERENCE,
					MODE_PRIVATE);

			Editor editor = share.edit();
			editor.putInt(UI_FLAG, flag);
			editor.commit();

		}

	}
	

	public BoardHolder loadDefaultBoardOld() {

		BoardHolder boards = new BoardHolder();

		int i = 0;

		boards.add(new Board(i, "7", "议事厅", R.drawable.oldp7));
		boards.add(new Board(i, "323", "国服以外讨论", R.drawable.oldp323));
		boards.add(new Board(i, "10", "银色黎明", R.drawable.oldp10));
		boards.add(new Board(i, "230", "艾泽拉斯风纪委员会", R.drawable.oldp230));
		boards.add(new Board(i, "387", "潘大力亚之迷雾", R.drawable.oldp387));
		boards.add(new Board(i, "430", "德拉诺之王", R.drawable.oldp430));
		boards.add(new Board(i, "305", "305权贵区", R.drawable.oldpdefault));
		boards.add(new Board(i, "11", "诺森德埋骨地", R.drawable.oldpdefault));
		boards.addCategoryName(i, "综合讨论");
		i++;

		boards.add(new Board(i, "-7", "大漩涡", R.drawable.oldp354));
		boards.add(new Board(i, "-362960", "艾欧泽亚", R.drawable.oldpdefault));
		boards.add(new Board(i, "-343809", "汽车俱乐部", R.drawable.oldpdefault));
		boards.add(new Board(i, "-81981", "生命之杯", R.drawable.oldpdefault));
		boards.add(new Board(i, "-576177", "影音讨论区", R.drawable.oldpdefault));
		boards.add(new Board(i, "-43", "军事历史", R.drawable.oldpdefault));
		boards.add(new Board(i, "414", "游戏综合讨论", R.drawable.oldp414));
		boards.add(new Board(i, "415", "主机游戏综合讨论", R.drawable.oldpdefault));
		boards.add(new Board(i, "431", "风暴英雄", R.drawable.oldp431));
		boards.add(new Board(i, "436", "消费电子 IT新闻", R.drawable.oldpdefault));
		boards.add(new Board(i, "-187579", "大漩涡历史博物馆", R.drawable.oldpdefault));
		boards.addCategoryName(i, "大漩涡系列");
		i++;

		boards.add(new Board(i, "390", "五晨寺", R.drawable.oldp390));
		boards.add(new Board(i, "320", "黑锋要塞", R.drawable.oldp320));
		boards.add(new Board(i, "181", "铁血沙场", R.drawable.oldp181));
		boards.add(new Board(i, "182", "魔法圣堂", R.drawable.oldp182));
		boards.add(new Board(i, "183", "信仰神殿", R.drawable.oldp183));
		boards.add(new Board(i, "185", "风暴祭坛", R.drawable.oldp185));
		boards.add(new Board(i, "186", "翡翠梦境", R.drawable.oldp186));
		boards.add(new Board(i, "187", "猎手大厅", R.drawable.oldp187));
		boards.add(new Board(i, "184", "圣光之力", R.drawable.oldp184));
		boards.add(new Board(i, "188", "恶魔深渊", R.drawable.oldp188));
		boards.add(new Board(i, "189", "暗影裂口", R.drawable.oldp189));
		boards.addCategoryName(i, "职业讨论区");
		i++;

		boards.add(new Board(i, "310", "精英议会", R.drawable.oldp310));
		boards.add(new Board(i, "190", "任务讨论", R.drawable.oldp190));
		boards.add(new Board(i, "213", "战争档案", R.drawable.oldp213));
		boards.add(new Board(i, "218", "副本专区", R.drawable.oldp218));
		boards.add(new Board(i, "258", "战场讨论", R.drawable.oldp258));
		boards.add(new Board(i, "272", "竞技场", R.drawable.oldp272));
		boards.add(new Board(i, "191", "地精商会", R.drawable.oldp191));
		boards.add(new Board(i, "200", "插件研究", R.drawable.oldp200));
		boards.add(new Board(i, "240", "BigFoot", R.drawable.oldp240));
		boards.add(new Board(i, "274", "插件发布", R.drawable.oldp274));
		boards.add(new Board(i, "315", "战斗统计", R.drawable.oldp315));
		boards.add(new Board(i, "333", "DKP系统", R.drawable.oldp333));
		boards.add(new Board(i, "327", "成就讨论", R.drawable.oldp327));
		boards.add(new Board(i, "388", "幻化讨论", R.drawable.oldp388));
		boards.add(new Board(i, "411", "宠物讨论", R.drawable.oldp411));
		boards.add(new Board(i, "255", "公会管理", R.drawable.oldp10));
		boards.add(new Board(i, "306", "人员招募", R.drawable.oldp10));
		boards.addCategoryName(i, "冒险心得");
		i++;

		boards.add(new Board(i, "264", "卡拉赞剧院", R.drawable.oldp264));
		boards.add(new Board(i, "8", "大图书馆", R.drawable.oldp8));
		boards.add(new Board(i, "102", "作家协会", R.drawable.oldp102));
		boards.add(new Board(i, "124", "壁画洞窟", R.drawable.oldpdefault));
		boards.add(new Board(i, "254", "镶金玫瑰", R.drawable.oldp254));
		boards.add(new Board(i, "355", "龟岩兄弟会", R.drawable.oldp355));
		boards.add(new Board(i, "116", "奇迹之泉", R.drawable.oldp116));
		boards.addCategoryName(i, "麦迪文之塔");
		i++;

		boards.add(new Board(i, "193", "帐号安全", R.drawable.oldp193));
		boards.add(new Board(i, "334", "PC软硬件", R.drawable.oldp334));
		boards.add(new Board(i, "201", "系统问题", R.drawable.oldp201));
		boards.add(new Board(i, "335", "网站开发", R.drawable.oldp335));
		boards.addCategoryName(i, "系统软硬件讨论");
		i++;

		boards.add(new Board(i, "414", "游戏综合讨论", R.drawable.oldp414));
		boards.add(new Board(i, "428", "手机游戏", R.drawable.oldp428));
		boards.add(new Board(i, "431", "风暴英雄", R.drawable.oldp431));
		boards.add(new Board(i, "-452227", "口袋妖怪", R.drawable.oldpdefault));
		boards.add(new Board(i, "426", "智龙迷城", R.drawable.oldp426));
		boards.add(new Board(i, "-51095", "部落战争", R.drawable.oldpdefault));
		boards.add(new Board(i, "-362960", "最终幻想14", R.drawable.oldp362960));
		boards.add(new Board(i, "-6194253", "战争雷霆", R.drawable.oldp6194253));
		boards.add(new Board(i, "427", "怪物猎人", R.drawable.oldp427));
		boards.add(new Board(i, "-47218", "地下城与勇士", R.drawable.oldpdefault));
		boards.add(new Board(i, "425", "行星边际2", R.drawable.oldp425));
		boards.add(new Board(i, "422", "炉石传说", R.drawable.oldp422));
		boards.add(new Board(i, "-65653", "剑灵", R.drawable.oldp65653));
		boards.add(new Board(i, "412", "巫师之怒", R.drawable.oldp412));
		boards.add(new Board(i, "-235147", "激战2", R.drawable.oldp235147));
		boards.add(new Board(i, "442", "逆战", R.drawable.oldp442));
		boards.add(new Board(i, "-46468", "坦克世界", R.drawable.oldp46468));
		boards.add(new Board(i, "432", "战机世界", R.drawable.oldp432));
		boards.add(new Board(i, "441", "战舰世界", R.drawable.oldpdefault));
		boards.add(new Board(i, "321", "DotA", R.drawable.oldp321));
		boards.add(new Board(i, "375", "DotA联赛饰品", R.drawable.oldpdefault));
		boards.add(new Board(i, "-2371813", "EVE", R.drawable.oldp2371813));
		boards.add(new Board(i, "-7861121", "剑叁 ", R.drawable.oldp7861121));
		boards.add(new Board(i, "448", "剑叁同人 ", R.drawable.oldpdefault));
		boards.add(new Board(i, "-793427", "斗战神", R.drawable.oldpdefault));
		boards.add(new Board(i, "332", "战锤40K", R.drawable.oldp332));
		boards.add(new Board(i, "416", "火炬之光2", R.drawable.oldpdefault));
		boards.add(new Board(i, "406", "星际争霸2", R.drawable.oldpdefault));
		boards.add(new Board(i, "420", "MT Online", R.drawable.oldp420));
		boards.add(new Board(i, "424", "圣斗士", R.drawable.oldpdefault));
		boards.add(new Board(i, "-1513130", "鲜血兄弟会", R.drawable.oldpdefault));
		boards.add(new Board(i, "433", "神雕侠侣", R.drawable.oldpdefault));
		boards.add(new Board(i, "434", "神鬼幻想", R.drawable.oldpdefault));
		boards.add(new Board(i, "435", "上古卷轴Online", R.drawable.oldp435));
		boards.add(new Board(i, "443", "FIFA Online 3", R.drawable.oldpdefault));
		boards.add(new Board(i, "444", "刀塔传奇", R.drawable.oldp444));
		boards.add(new Board(i, "445", "迷你西游", R.drawable.oldp445));
		boards.add(new Board(i, "447", "锁链战记", R.drawable.oldpdefault));
		boards.add(new Board(i, "-532408", "沃土", R.drawable.oldpdefault));
		boards.add(new Board(i, "353", "纽沃斯英雄传", R.drawable.oldpdefault));
		boards.add(new Board(i, "452", "天涯明月刀", R.drawable.oldpdefault));
		boards.addCategoryName(i, "其他游戏");
		i++;

		boards.add(new Board(i, "318", "暗黑破坏神3", R.drawable.oldp318));
		boards.add(new Board(i, "403", "购买/安装/共享", R.drawable.oldp403));
		boards.add(new Board(i, "393", "背景故事与文艺作品", R.drawable.oldp393));
		boards.add(new Board(i, "400", "职业讨论区", R.drawable.oldp29));
		boards.add(new Board(i, "395", "野蛮人", R.drawable.oldp395));
		boards.add(new Board(i, "396", "猎魔人", R.drawable.oldp396));
		boards.add(new Board(i, "397", "武僧", R.drawable.oldp397));
		boards.add(new Board(i, "398", "巫医", R.drawable.oldp398));
		boards.add(new Board(i, "399", "魔法师", R.drawable.oldp399));
		boards.add(new Board(i, "446", "圣教军", R.drawable.oldpdefault));
		boards.addCategoryName(i, "暗黑破坏神");
		i++;

		boards.add(new Board(i, "422", "炉石传说", R.drawable.oldp422));
		boards.add(new Board(i, "429", "战术讨论", R.drawable.oldpdefault));
		boards.add(new Board(i, "450", "文章存档", R.drawable.oldpdefault));
		boards.addCategoryName(i, "炉石传说");
		i++;

		boards.add(new Board(i, "-152678", "英雄联盟", R.drawable.oldp152678));
		boards.add(new Board(i, "418", "游戏视频", R.drawable.oldpdefault));
		boards.addCategoryName(i, "英雄联盟");
		i++;

		boards.add(new Board(i, "-447601", "二次元国家地理", R.drawable.oldp447601));
		boards.add(new Board(i, "-84", "模玩之魂", R.drawable.oldp84));
		boards.add(new Board(i, "-8725919", "小窗视界", R.drawable.oldp8725919));
		boards.add(new Board(i, "-965240", "树洞", R.drawable.oldpdefault));
		boards.add(new Board(i, "-131429", "红茶馆——小说馆", R.drawable.oldpdefault));
		boards.add(new Board(i, "-608808", "血腥厨房", R.drawable.oldpdefault));
		boards.add(new Board(i, "-469608", "影~视~秀", R.drawable.oldpdefault));
		boards.add(new Board(i, "-55912", "音乐讨论", R.drawable.oldpdefault));
		boards.add(new Board(i, "-522474", "综合体育讨论区", R.drawable.oldpdefault));
		boards.add(new Board(i, "-1068355", "晴风村", R.drawable.oldpdefault));
		boards.add(new Board(i, "-168888", "育儿版", R.drawable.oldpdefault));
		boards.add(new Board(i, "-54214", "时尚板", R.drawable.oldpdefault));
		boards.add(new Board(i, "-353371", "宠物养成", R.drawable.oldpdefault));
		boards.add(new Board(i, "-538800", "乙女向二次元", R.drawable.oldpdefault));
		boards.add(new Board(i, "-7678526", "麻将科学院", R.drawable.oldpdefault));
		boards.add(new Board(i, "-202020", "程序员职业交流", R.drawable.oldpdefault));
		boards.add(new Board(i, "-444012", "我们的骑迹", R.drawable.oldpdefault));
		boards.add(new Board(i, "-349066", "开心茶园", R.drawable.oldpdefault));
		boards.add(new Board(i, "-314508", "世界尽头的百货公司", R.drawable.oldpdefault));
		boards.add(new Board(i, "-2671", "耳机区", R.drawable.oldpdefault));
		boards.add(new Board(i, "-970841", "东方教主陈乔恩", R.drawable.oldpdefault));
		boards.add(new Board(i, "-3355501", "基腐版", R.drawable.oldpdefault));
		boards.add(new Board(i, "-403298", "怨灵图纸收藏室", R.drawable.oldpdefault));
		boards.add(new Board(i, "-3432136", "飘落的诗章", R.drawable.oldpdefault));
		boards.add(new Board(i, "-187628", "家居 装修", R.drawable.oldpdefault));
		boards.addCategoryName(i, "个人版面");

		return boards;
	}

	public BoardHolder loadDefaultBoard() {

		BoardHolder boards = new BoardHolder();

		int i = 0;


		boards.add(new Board(i, "7", "议事厅", R.drawable.p7));
		boards.add(new Board(i, "323", "国服以外讨论", R.drawable.p323));
		boards.add(new Board(i, "10", "银色黎明", R.drawable.p10));
		boards.add(new Board(i, "230", "艾泽拉斯风纪委员会", R.drawable.p230));
		boards.add(new Board(i, "387", "潘大力亚之迷雾", R.drawable.p387));
		boards.add(new Board(i, "430", "德拉诺之王", R.drawable.p430));
		boards.add(new Board(i, "305", "305权贵区", R.drawable.p305));
		boards.add(new Board(i, "11", "诺森德埋骨地", R.drawable.p11));
		boards.addCategoryName(i, "综合讨论");
		i++;

		boards.add(new Board(i, "-7", "大漩涡", R.drawable.p354));
		boards.add(new Board(i, "-362960", "艾欧泽亚", R.drawable.p362960));
		boards.add(new Board(i, "-343809", "汽车俱乐部", R.drawable.p343809));
		boards.add(new Board(i, "-81981", "生命之杯", R.drawable.p81981));
		boards.add(new Board(i, "-576177", "影音讨论区", R.drawable.p576177));
		boards.add(new Board(i, "-43", "军事历史", R.drawable.p43));
		boards.add(new Board(i, "414", "游戏综合讨论", R.drawable.p414));
		boards.add(new Board(i, "415", "主机游戏综合讨论", R.drawable.p415));
		boards.add(new Board(i, "431", "风暴英雄", R.drawable.p431));
		boards.add(new Board(i, "436", "消费电子 IT新闻", R.drawable.p436));
		boards.add(new Board(i, "-187579", "大漩涡历史博物馆", R.drawable.p187579));
		boards.addCategoryName(i, "大漩涡系列");
		i++;

		boards.add(new Board(i, "390", "五晨寺", R.drawable.p390));
		boards.add(new Board(i, "320", "黑锋要塞", R.drawable.p320));
		boards.add(new Board(i, "181", "铁血沙场", R.drawable.p181));
		boards.add(new Board(i, "182", "魔法圣堂", R.drawable.p182));
		boards.add(new Board(i, "183", "信仰神殿", R.drawable.p183));
		boards.add(new Board(i, "185", "风暴祭坛", R.drawable.p185));
		boards.add(new Board(i, "186", "翡翠梦境", R.drawable.p186));
		boards.add(new Board(i, "187", "猎手大厅", R.drawable.p187));
		boards.add(new Board(i, "184", "圣光之力", R.drawable.p184));
		boards.add(new Board(i, "188", "恶魔深渊", R.drawable.p188));
		boards.add(new Board(i, "189", "暗影裂口", R.drawable.p189));
		boards.addCategoryName(i, "职业讨论区");
		i++;

		boards.add(new Board(i, "310", "精英议会", R.drawable.p310));
		boards.add(new Board(i, "190", "任务讨论", R.drawable.p190));
		boards.add(new Board(i, "213", "战争档案", R.drawable.p213));
		boards.add(new Board(i, "218", "副本专区", R.drawable.p218));
		boards.add(new Board(i, "258", "战场讨论", R.drawable.p258));
		boards.add(new Board(i, "272", "竞技场", R.drawable.p272));
		boards.add(new Board(i, "191", "地精商会", R.drawable.p191));
		boards.add(new Board(i, "200", "插件研究", R.drawable.p200));
		boards.add(new Board(i, "240", "BigFoot", R.drawable.p240));
		boards.add(new Board(i, "274", "插件发布", R.drawable.p274));
		boards.add(new Board(i, "315", "战斗统计", R.drawable.p315));
		boards.add(new Board(i, "333", "DKP系统", R.drawable.p333));
		boards.add(new Board(i, "327", "成就讨论", R.drawable.p327));
		boards.add(new Board(i, "388", "幻化讨论", R.drawable.p388));
		boards.add(new Board(i, "411", "宠物讨论", R.drawable.p411));
		boards.add(new Board(i, "255", "公会管理", R.drawable.p255));
		boards.add(new Board(i, "306", "人员招募", R.drawable.p306));
		boards.addCategoryName(i, "冒险心得");
		i++;

		boards.add(new Board(i, "264", "卡拉赞剧院", R.drawable.p264));
		boards.add(new Board(i, "8", "大图书馆", R.drawable.p8));
		boards.add(new Board(i, "102", "作家协会", R.drawable.p102));
		boards.add(new Board(i, "124", "壁画洞窟", R.drawable.p124));
		boards.add(new Board(i, "254", "镶金玫瑰", R.drawable.p254));
		boards.add(new Board(i, "355", "龟岩兄弟会", R.drawable.p355));
		boards.add(new Board(i, "116", "奇迹之泉", R.drawable.p116));
		boards.addCategoryName(i, "麦迪文之塔");
		i++;

		boards.add(new Board(i, "193", "帐号安全", R.drawable.p193));
		boards.add(new Board(i, "334", "PC软硬件", R.drawable.p334));
		boards.add(new Board(i, "201", "系统问题", R.drawable.p201));
		boards.add(new Board(i, "335", "网站开发", R.drawable.p335));
		boards.addCategoryName(i, "系统软硬件讨论");
		i++;

		boards.add(new Board(i, "414", "游戏综合讨论", R.drawable.p414));
		boards.add(new Board(i, "428", "手机游戏", R.drawable.p428));
		boards.add(new Board(i, "431", "风暴英雄", R.drawable.p431));
		boards.add(new Board(i, "-452227", "口袋妖怪", R.drawable.p452227));
		boards.add(new Board(i, "426", "智龙迷城", R.drawable.p426));
		boards.add(new Board(i, "-51095", "部落战争", R.drawable.p51095));
		boards.add(new Board(i, "-362960", "最终幻想14", R.drawable.p362960));
		boards.add(new Board(i, "-6194253", "战争雷霆", R.drawable.p6194253));
		boards.add(new Board(i, "427", "怪物猎人", R.drawable.p427));
		boards.add(new Board(i, "-47218", "地下城与勇士", R.drawable.p47218));
		boards.add(new Board(i, "425", "行星边际2", R.drawable.p425));
		boards.add(new Board(i, "422", "炉石传说", R.drawable.p422));
		boards.add(new Board(i, "-65653", "剑灵", R.drawable.p65653));
		boards.add(new Board(i, "412", "巫师之怒", R.drawable.p412));
		boards.add(new Board(i, "-235147", "激战2", R.drawable.p235147));
		boards.add(new Board(i, "442", "逆战", R.drawable.p442));
		boards.add(new Board(i, "-46468", "坦克世界", R.drawable.p46468));
		boards.add(new Board(i, "432", "战机世界", R.drawable.p432));
		boards.add(new Board(i, "441", "战舰世界", R.drawable.p441));
		boards.add(new Board(i, "321", "DotA", R.drawable.p321));
		boards.add(new Board(i, "375", "DotA联赛饰品", R.drawable.p375));
		boards.add(new Board(i, "-2371813", "EVE", R.drawable.p2371813));
		boards.add(new Board(i, "-7861121", "剑叁 ", R.drawable.p7861121));
		boards.add(new Board(i, "448", "剑叁同人 ", R.drawable.p448));
		boards.add(new Board(i, "-793427", "斗战神", R.drawable.p793427));
		boards.add(new Board(i, "332", "战锤40K", R.drawable.p332));
		boards.add(new Board(i, "416", "火炬之光2", R.drawable.p416));
		boards.add(new Board(i, "406", "星际争霸2", R.drawable.p406));
		boards.add(new Board(i, "420", "MT Online", R.drawable.p420));
		boards.add(new Board(i, "424", "圣斗士", R.drawable.p424));
		boards.add(new Board(i, "-1513130", "鲜血兄弟会", R.drawable.p1513130));
		boards.add(new Board(i, "433", "神雕侠侣", R.drawable.p433));
		boards.add(new Board(i, "434", "神鬼幻想", R.drawable.p434));
		boards.add(new Board(i, "435", "上古卷轴Online", R.drawable.p435));
		boards.add(new Board(i, "443", "FIFA Online 3", R.drawable.p443));
		boards.add(new Board(i, "444", "刀塔传奇", R.drawable.p444));
		boards.add(new Board(i, "445", "迷你西游", R.drawable.p445));
		boards.add(new Board(i, "447", "锁链战记", R.drawable.p447));
		boards.add(new Board(i, "-532408", "沃土", R.drawable.p532408));
		boards.add(new Board(i, "353", "纽沃斯英雄传", R.drawable.p353));
		boards.add(new Board(i, "452", "天涯明月刀", R.drawable.p452));
		boards.addCategoryName(i, "其他游戏");
		i++;

		boards.add(new Board(i, "318", "暗黑破坏神3", R.drawable.p318));
		boards.add(new Board(i, "403", "购买/安装/共享", R.drawable.p403));
		boards.add(new Board(i, "393", "背景故事与文艺作品", R.drawable.p393));
		boards.add(new Board(i, "400", "职业讨论区", R.drawable.p400));
		boards.add(new Board(i, "395", "野蛮人", R.drawable.p395));
		boards.add(new Board(i, "396", "猎魔人", R.drawable.p396));
		boards.add(new Board(i, "397", "武僧", R.drawable.p397));
		boards.add(new Board(i, "398", "巫医", R.drawable.p398));
		boards.add(new Board(i, "399", "魔法师", R.drawable.p399));
		boards.add(new Board(i, "446", "圣教军", R.drawable.p446));
		boards.addCategoryName(i, "暗黑破坏神");
		i++;

		boards.add(new Board(i, "422", "炉石传说", R.drawable.p422));
		boards.add(new Board(i, "429", "战术讨论", R.drawable.p429));
		boards.add(new Board(i, "450", "文章存档", R.drawable.p450));
		boards.addCategoryName(i, "炉石传说");
		i++;

		boards.add(new Board(i, "-152678", "英雄联盟", R.drawable.p152678));
		boards.add(new Board(i, "418", "游戏视频", R.drawable.p418));
		boards.addCategoryName(i, "英雄联盟");
		i++;

		boards.add(new Board(i, "-447601", "二次元国家地理", R.drawable.p447601));
		boards.add(new Board(i, "-84", "模玩之魂", R.drawable.p84));
		boards.add(new Board(i, "-8725919", "小窗视界", R.drawable.p8725919));
		boards.add(new Board(i, "-965240", "树洞", R.drawable.p965240));
		boards.add(new Board(i, "-131429", "红茶馆——小说馆", R.drawable.p131429));
		boards.add(new Board(i, "-608808", "血腥厨房", R.drawable.p608808));
		boards.add(new Board(i, "-469608", "影~视~秀", R.drawable.p469608));
		boards.add(new Board(i, "-55912", "音乐讨论", R.drawable.p55912));
		boards.add(new Board(i, "-522474", "综合体育讨论区", R.drawable.p522474));
		boards.add(new Board(i, "-1068355", "晴风村", R.drawable.p1068355));
		boards.add(new Board(i, "-168888", "育儿版", R.drawable.p168888));
		boards.add(new Board(i, "-54214", "时尚板", R.drawable.p54214));
		boards.add(new Board(i, "-353371", "宠物养成", R.drawable.p353371));
		boards.add(new Board(i, "-538800", "乙女向二次元", R.drawable.p538800));
		boards.add(new Board(i, "-7678526", "麻将科学院", R.drawable.p7678526));
		boards.add(new Board(i, "-202020", "程序员职业交流", R.drawable.p202020));
		boards.add(new Board(i, "-444012", "我们的骑迹", R.drawable.p444012));
		boards.add(new Board(i, "-349066", "开心茶园", R.drawable.p349066));
		boards.add(new Board(i, "-314508", "世界尽头的百货公司", R.drawable.p314508));
		boards.add(new Board(i, "-2671", "耳机区", R.drawable.p2671));
		boards.add(new Board(i, "-970841", "东方教主陈乔恩", R.drawable.p970841));
		boards.add(new Board(i, "-3355501", "基腐版", R.drawable.p3355501));
		boards.add(new Board(i, "-403298", "怨灵图纸收藏室", R.drawable.p403298));
		boards.add(new Board(i, "-3432136", "飘落的诗章", R.drawable.p3432136));
		boards.add(new Board(i, "-187628", "家居 装修", R.drawable.p187628));
		boards.addCategoryName(i, "个人版面");
		// i++;


		return boards;
	}
	

}
