package gov.anzong.androidnga.activity;

import gov.anzong.androidnga.R;
import sp.phone.bean.PerferenceConstant;
import sp.phone.fragment.AlertDialogFragment;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.ThemeManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends SwipeBackAppCompatActivity implements
		PerferenceConstant {

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

	private CompoundButton split = null;
	private CompoundButton replysplit = null;
	private CompoundButton ha = null;
	private CompoundButton fullscreen = null;
	
	private RelativeLayout handsideQualityChooser;
	
	private Button button_clear_recent_board;

	private SeekBar fontSizeBar;
	private float defaultFontSize;
	private TextView fontTextView;
	private int defaultWebSize;
	private SeekBar webSizebar;
	private WebView websizeView;
	private TextView avatarSizeTextView;
	private TextView imageOptionInfoTextView;
	private TextView imageOptionChoiceTextView;

	private TextView handsideOptionInfoTextView;
	private TextView handsideOptionChoiceTextView;
	
	private ImageView avatarImage;
	private SeekBar avatarSeekBar;

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

		this.setContentView(view);
		PhoneConfiguration config = PhoneConfiguration.getInstance();

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
		showReplyButton.setOnCheckedChangeListener(new ShowReplyButtonListener());
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
			replysplit.setOnCheckedChangeListener(new ReplySplitChangedListener());
		}
		button_clear_recent_board = (Button) findViewById(R.id.clear_recent_board);
		button_clear_recent_board
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method
						//  BUG√ª¡À

						SharedPreferences share = getSharedPreferences(
								PERFERENCE, Activity.MODE_PRIVATE);
						Editor editor = share.edit();
						editor.putString(RECENT_BOARD, "");
						editor.commit();
						Intent iareboot = getBaseContext().getPackageManager()
								.getLaunchIntentForPackage(
										getBaseContext().getPackageName());
						iareboot.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(iareboot);
					}

				});

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
		websizeView.loadDataWithBaseURL(null,
				getString(R.string.websize_sample_text), "text/html", "utf-8",
				"");
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
		if(!split.isChecked()&&!replysplit.isChecked()){
			handsideQualityChooser.setVisibility(View.GONE);
		}
		if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){//less than 4.0
			handsideQualityChooser.setVisibility(View.GONE);
			split.setVisibility(View.GONE);
			replysplit.setVisibility(View.GONE);
			ha.setVisibility(View.GONE);
		}
		if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){//less than 4.4
			fullscreen.setVisibility(View.GONE);
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
	
	private void updateThemeUI() {
		int fgColor = getResources().getColor(
				ThemeManager.getInstance().getForegroundColor());
		checkBoxDownimgNowifi.setTextColor(fgColor);
		fullscreen.setTextColor(fgColor);
		checkBoxDownAvatarNowifi.setTextColor(fgColor);
		nightMode.setTextColor(fgColor);
		showAnimation.setTextColor(fgColor);
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

		view.setBackgroundResource(ThemeManager.getInstance()
				.getBackgroundColor());
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
			// case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
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
			if(isChecked){
			Toast.makeText(SettingsActivity.this, R.string.showColortxtWarn, Toast.LENGTH_LONG).show();
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
			if(isChecked){
				Toast.makeText(SettingsActivity.this, R.string.showNewweibaWarn, Toast.LENGTH_LONG).show();
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

		}
	}
	
	class DownImgNoWifiChangedListener implements OnCheckedChangeListener,
			PerferenceConstant {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

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
					getString(R.string.image_quality_option_large)};
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
					updateImageQualityChoiceText(PhoneConfiguration.getInstance());
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();
			Toast.makeText(SettingsActivity.this, R.string.image_quality_claim, Toast.LENGTH_LONG).show();
		}

	}
	
	
	
	class HandSideChooserListener implements
	android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
			SettingsActivity.this);
			String[] items = new String[] {
			getString(R.string.lefthand_option_original),
			getString(R.string.righthand_option_original)};
			builder.setTitle(R.string.lefthand_righthand_chooser_prompt);
			builder.setItems(items, new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			PhoneConfiguration.getInstance().HandSide = Math.abs(1-which);
			SharedPreferences share = getSharedPreferences(PERFERENCE,
					MODE_PRIVATE);
			Editor editor = share.edit();
			editor.putInt(HANDSIDE, Math.abs(1-which));
			editor.commit();
			updateHandSideChoiceText(PhoneConfiguration.getInstance());
		}
	});
	AlertDialog dialog = builder.create();
	dialog.show();
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
				if(!replysplit.isChecked()){
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
				if(!split.isChecked()){
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

}
