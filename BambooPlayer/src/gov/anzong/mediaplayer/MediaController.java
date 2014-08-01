/* 操控UI */

package gov.anzong.mediaplayer;

import gov.anzong.mediaplayer.CommonGestures.TouchListener;
import gov.anzong.mediaplayer.R;

import io.vov.vitamio.MediaPlayer;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MediaController extends FrameLayout {
	private MediaPlayerControl mPlayer;
	private static Activity mContext;
	private PopupWindow mWindow;
	private PopupWindow videoqualityWindow;
	private View mAnchor;
	private View mRoot;
	private ImageButton mLock;
	private ImageButton mScreenToggle;
	private SeekBar mProgress;
	private TextView mTime;
	private long mDuration;
	private boolean mShowing;
	private boolean mScreenLocked = false;
	private boolean mDragging;
	private boolean mInstantSeeking = true;
	private static final int DEFAULT_TIME_OUT = 3000;
	private static final int DEFAULT_LONG_TIME_SHOW = 120000;
	private static final int DEFAULT_SEEKBAR_VALUE = 1000;
	private static final int TIME_TICK_INTERVAL = 1000;
	private ImageButton mPauseButton, mediacontroller_back;
	private int selectedposition = 1;

	private View mMediaController;
	private long toposition = -1l;
	private View mControlsLayout;
	private View mSystemInfoLayout;
	private TextView mDateTime;
	private TextView mDownloadRate;
	private static TextView mWifiRate;
	private TextView mFileName;
	private TextView mBatteryLevel;

	private Button mModeButton;

	private TextView mOperationInfo;
	private RelativeLayout relativeLayout_volume, relativeLayout_brightness;
	private ProgressBar volumeProgressBar, brightnessProgressBar;

	private AudioManager mAM;
	private int mMaxVolume;
	private float mBrightness = 0.01f;
	private int mVolume = 0;
	private Handler mHandler;

	private Animation mAnimSlideInTop;
	private Animation mAnimSlideInBottom;
	private Animation mAnimSlideOutTop;
	private Animation mAnimSlideOutBottom;

	private CommonGestures mGestures;
	private int mVideoMode;
	private int mSpeed = 0;
	private boolean istoanotherposition = false;
	private int pressbacktime = 0;

	public MediaController(Context context) {
		super(context);
		mContext = (Activity) context;
		initFloatingWindow();
		initResources();
	}

	public MediaController(Context context, boolean locked) {
		this(context);
		mScreenLocked = locked;
		lock(mScreenLocked);
	}

	private void initFloatingWindow() {
		mWindow = new PopupWindow(mContext);
		mWindow.setFocusable(true);
		mWindow.setBackgroundDrawable(null);
		mWindow.setOutsideTouchable(true);
	}

	@TargetApi(11)
	public void setWindowLayoutType() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			try {
				mAnchor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
				Method setWindowLayoutType = PopupWindow.class.getMethod(
						"setWindowLayoutType", new Class[] { int.class });
				setWindowLayoutType
						.invoke(mWindow,
								WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG);
			} catch (Exception e) {
			}
		}
	}

	public static boolean isInWifi() {
		ConnectivityManager conMan = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		return wifi == State.CONNECTED;
	}

	@SuppressLint("NewApi")
	private void initResources() {
		mHandler = new MHandler(this);
		mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mGestures = new CommonGestures(mContext);
		mGestures.setTouchListener(mTouchListener, true);

		mAnimSlideOutBottom = AnimationUtils.loadAnimation(mContext,
				R.anim.slide_out_bottom);
		mAnimSlideOutTop = AnimationUtils.loadAnimation(mContext,
				R.anim.slide_out_top);
		mAnimSlideInBottom = AnimationUtils.loadAnimation(mContext,
				R.anim.slide_in_bottom);
		mAnimSlideInTop = AnimationUtils.loadAnimation(mContext,
				R.anim.slide_in_top);
		mAnimSlideOutBottom.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mMediaController.setVisibility(View.INVISIBLE);
				showButtons(false);
				mHandler.removeMessages(MSG_HIDE_SYSTEM_UI);
				mHandler.sendEmptyMessageDelayed(MSG_HIDE_SYSTEM_UI,
						DEFAULT_TIME_OUT);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});

		removeAllViews();

		mRoot = inflateLayout();
		mWindow.setContentView(mRoot);
		mWindow.setWidth(android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		mWindow.setHeight(android.view.ViewGroup.LayoutParams.MATCH_PARENT);

		findViewItems(mRoot);
		showSystemUi(false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mRoot.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
				public void onSystemUiVisibilityChange(int visibility) {
					if ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
						mHandler.sendEmptyMessageDelayed(MSG_HIDE_SYSTEM_UI,
								DEFAULT_TIME_OUT);
					}
				}
			});
		}
	}

	private View inflateLayout() {
		return ((LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.mediacontroller, this);
	}

	private void findViewItems(View v) {
		mMediaController = v.findViewById(R.id.mediacontroller);

		mSystemInfoLayout = v.findViewById(R.id.info_panel);

		mTime = (TextView) v.findViewById(R.id.mediacontroller_time_total);

		mFileName = (TextView) v.findViewById(R.id.mediacontroller_file_name);
		mDateTime = (TextView) v.findViewById(R.id.date_time);
		mDownloadRate = (TextView) v.findViewById(R.id.download_rate);
		mWifiRate = (TextView) v.findViewById(R.id.wifi_rate);
		mBatteryLevel = (TextView) v.findViewById(R.id.battery_level);

		mControlsLayout = v.findViewById(R.id.mediacontroller_controls);

		mLock = (ImageButton) v.findViewById(R.id.mediacontroller_lock);
		mLock.setOnClickListener(mLockClickListener);

		mScreenToggle = (ImageButton) v
				.findViewById(R.id.mediacontroller_screen_size);
		mScreenToggle.setOnClickListener(mScreenToggleListener);

		mPauseButton = (ImageButton) v
				.findViewById(R.id.mediacontroller_play_pause);
		mPauseButton.setOnClickListener(mPauseListener);

		mediacontroller_back = (ImageButton) v
				.findViewById(R.id.mediacontroller_back);
		mediacontroller_back.setOnClickListener(mMediacontroller_backListener);

		mProgress = (SeekBar) v.findViewById(R.id.mediacontroller_seekbar);
		mProgress.setOnSeekBarChangeListener(mSeekListener);
		mProgress.setMax(DEFAULT_SEEKBAR_VALUE);

		mOperationInfo = (TextView) v.findViewById(R.id.operation_info);
		mModeButton = (Button) v.findViewById(R.id.mode_spinner);
		mModeButton.setText("普通");
		mModeButton.setOnClickListener(videoquality());

		relativeLayout_volume = (RelativeLayout) findViewById(R.id.relativeLayout_volume);
		relativeLayout_brightness = (RelativeLayout) findViewById(R.id.relativeLayout_brightness);
		volumeProgressBar = (ProgressBar) findViewById(R.id.volumeProgressBar);
		brightnessProgressBar = (ProgressBar) findViewById(R.id.brightnessProgressBar);
	}

	public OnClickListener videoquality() {
		OnClickListener videoqualityonclick = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				show();
				final View mLayout; // 下拉列表的布局
				final ListView mListView; // 下拉列表控件
				final BaseAdapter adaptervideoQuality;
				LayoutInflater inflater = ((LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
				mLayout = inflater.inflate(R.layout.spinnerlistview, null);
				mListView = (ListView) mLayout.findViewById(R.id.mlistview);
				mListView.setCacheColorHint(Color.TRANSPARENT);
				adaptervideoQuality = adaptervideoQuality(inflater);
				mListView.setAdapter(adaptervideoQuality);
				videoqualityWindow = new PopupWindow(mLayout,
						mModeButton.getWidth(),
						ViewGroup.LayoutParams.WRAP_CONTENT);
				videoqualityWindow.setContentView(mLayout);
				videoqualityWindow.setFocusable(true);
				videoqualityWindow.setBackgroundDrawable(new BitmapDrawable());
				videoqualityWindow.setOutsideTouchable(true);
				int[] location = new int[2];
				mModeButton.getLocationOnScreen(location);
				videoqualityWindow.showAtLocation(mLayout, Gravity.TOP
						| Gravity.LEFT, location[0],
						mSystemInfoLayout.getHeight());
				mListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						show();
						selectedposition = position;
						mModeButton.setText((String) mListView
								.getItemAtPosition(position));
						switch (position) {
						case 1:
							mPlayer.setVideoQuality(MediaPlayer.VIDEOQUALITY_MEDIUM);
							break;
						case 2:
							mPlayer.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
							break;
						case 0:
						default:
							mPlayer.setVideoQuality(MediaPlayer.VIDEOQUALITY_LOW);
							break;
						}
						setOperationInfo(
								"视频质量："
										+ (String) mListView
												.getItemAtPosition(position),
								1500);
						adaptervideoQuality.notifyDataSetChanged();
					}
				});
			}

		};
		return videoqualityonclick;

	}

	public BaseAdapter adaptervideoQuality(final LayoutInflater inflater) {
		BaseAdapter adaptervideoQuality = new BaseAdapter() {

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mContext.getResources().getStringArray(
						R.array.videoquality).length; // 选项总个数
			}

			@Override
			public String getItem(int arg0) {
				// TODO Auto-generated method stub
				return mContext.getResources().getStringArray(
						R.array.videoquality)[arg0];
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public View getView(int arg0, View convertView, ViewGroup arg2) {
				// TODO Auto-generated method stub
				View v = convertView;
				if (v == null) {
					v = inflater.inflate(R.layout.listtextview, null);
				}
				TextView TV = (TextView) v
						.findViewById(R.id.spinner_dropdown_item_textview);
				TV.setText(mContext.getResources().getStringArray(
						R.array.videoquality)[arg0]);
				if (selectedposition == arg0) {
					TV.setTextColor(mContext.getResources().getColor(
							R.color.listviewtextcolorseleted));
				} else {
					TV.setTextColor(Color.WHITE);
				}
				return v;
			}
		};
		return adaptervideoQuality;
	}

	public void setAnchorView(View view) {
		mAnchor = view;
		int[] location = new int[2];
		mAnchor.getLocationOnScreen(location);
		Rect anchorRect = new Rect(location[0], location[1], location[0]
				+ mAnchor.getWidth(), location[1] + mAnchor.getHeight());
		setWindowLayoutType();
		mWindow.showAtLocation(mAnchor, Gravity.NO_GRAVITY, anchorRect.left,
				anchorRect.bottom);
	}

	public void release() {
		if (mWindow != null) {
			mWindow.dismiss();
			mWindow = null;
		}
	}

	private void setOperationInfo(String info, long time) {
		mOperationInfo.setText(info);
		mOperationInfo.setVisibility(View.VISIBLE);
		mHandler.removeMessages(MSG_HIDE_OPERATION_INFO);
		mHandler.sendEmptyMessageDelayed(MSG_HIDE_OPERATION_INFO, time);
	}

	public void setFileName(String name) {
		mFileName.setText(name);
	}

	public void setDownloadRate(String rate) {
		mDownloadRate.setVisibility(View.VISIBLE);
		mDownloadRate.setText(rate);
	}

	public static String getNetworkClass() {
		if (isInWifi()) {
			return "WIFI";
		}
		if (!isConnected()) {
			return "无网络";
		}
		TelephonyManager mTelephonyManager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		int networkType = mTelephonyManager.getNetworkType();
		switch (networkType) {
		case TelephonyManager.NETWORK_TYPE_GPRS:
		case TelephonyManager.NETWORK_TYPE_EDGE:
		case TelephonyManager.NETWORK_TYPE_CDMA:
		case TelephonyManager.NETWORK_TYPE_1xRTT:
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return "2G";
		case TelephonyManager.NETWORK_TYPE_UMTS:
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
		case TelephonyManager.NETWORK_TYPE_HSDPA:
		case TelephonyManager.NETWORK_TYPE_HSUPA:
		case TelephonyManager.NETWORK_TYPE_HSPA:
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
		case TelephonyManager.NETWORK_TYPE_EHRPD:
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return "3G";
		case TelephonyManager.NETWORK_TYPE_LTE:
			return "4G";
		default:
			return "未知网络";

		}
	}

	public static boolean isConnected() {
		ConnectivityManager conMan = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conMan.getActiveNetworkInfo();
		return (info != null && info.isConnected());
	}

	public void setBatteryLevel(String level) {
		mBatteryLevel.setVisibility(View.VISIBLE);
		mBatteryLevel.setText(level);
	}

	public void setMediaPlayer(MediaPlayerControl player) {
		mPlayer = player;
		updatePausePlay();
	}

	public void show() {
		show(DEFAULT_TIME_OUT);
	}

	public void show(int timeout) {
		if (timeout != 0) {
			mHandler.removeMessages(MSG_FADE_OUT);
			mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_FADE_OUT),
					timeout);
		}
		if (!mShowing) {
			showButtons(true);
			mHandler.removeMessages(MSG_HIDE_SYSTEM_UI);
			showSystemUi(true);

			mPauseButton.requestFocus();

			mControlsLayout.startAnimation(mAnimSlideInTop);
			mSystemInfoLayout.startAnimation(mAnimSlideInBottom);
			mMediaController.setVisibility(View.VISIBLE);

			updatePausePlay();
			mHandler.sendEmptyMessage(MSG_TIME_TICK);
			mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS);

			mShowing = true;
		}
	}

	public void hide() {
		if (mShowing) {
			try {
				mHandler.removeMessages(MSG_TIME_TICK);
				mHandler.removeMessages(MSG_SHOW_PROGRESS);
				mControlsLayout.startAnimation(mAnimSlideOutTop);
				mSystemInfoLayout.startAnimation(mAnimSlideOutBottom);
				if (videoqualityWindow != null) {
					videoqualityWindow.dismiss();
				}
			} catch (IllegalArgumentException ex) {
			}
			mShowing = false;
		}
	}

	private void toggleVideoMode(boolean larger, boolean recycle) {
		if (larger) {
			if (mVideoMode < VideoView.VIDEO_LAYOUT_ZOOM)
				mVideoMode++;
			else if (recycle)
				mVideoMode = VideoView.VIDEO_LAYOUT_ORIGIN;
		} else {
			if (mVideoMode > VideoView.VIDEO_LAYOUT_ORIGIN)
				mVideoMode--;
			else if (recycle)
				mVideoMode = VideoView.VIDEO_LAYOUT_ZOOM;
		}

		switch (mVideoMode) {
		case VideoView.VIDEO_LAYOUT_ORIGIN:
			setOperationInfo(mContext.getString(R.string.video_original), 500);
			mScreenToggle
					.setImageResource(R.drawable.mediacontroller_sreen_size_100);
			break;
		case VideoView.VIDEO_LAYOUT_SCALE:
			setOperationInfo(mContext.getString(R.string.video_fit_screen), 500);
			mScreenToggle
					.setImageResource(R.drawable.mediacontroller_screen_fit);
			break;
		case VideoView.VIDEO_LAYOUT_STRETCH:
			setOperationInfo(mContext.getString(R.string.video_stretch), 500);
			mScreenToggle
					.setImageResource(R.drawable.mediacontroller_screen_size);
			break;
		case VideoView.VIDEO_LAYOUT_ZOOM:
			setOperationInfo(mContext.getString(R.string.video_crop), 500);
			mScreenToggle
					.setImageResource(R.drawable.mediacontroller_sreen_size_crop);
			break;
		}

		mPlayer.toggleVideoMode(mVideoMode);
	}

	private void lock(boolean toLock) {
		if (toLock) {
			mLock.setImageResource(R.drawable.mediacontroller_lock);
			mProgress.setEnabled(false);
			mPauseButton.setClickable(false);
			mModeButton.setClickable(false);
			mediacontroller_back.setClickable(false);
			mScreenToggle.setClickable(false);
			if (mScreenLocked != toLock)
				setOperationInfo(
						mContext.getString(R.string.video_screen_locked), 1000);
			if (videoqualityWindow != null)
				videoqualityWindow.dismiss();
		} else {
			mLock.setImageResource(R.drawable.mediacontroller_unlock);
			mProgress.setEnabled(true);
			mPauseButton.setClickable(true);
			mModeButton.setClickable(true);
			mediacontroller_back.setClickable(true);
			mScreenToggle.setClickable(true);
			if (mScreenLocked != toLock)
				setOperationInfo(
						mContext.getString(R.string.video_screen_unlocked),
						1000);
		}
		mScreenLocked = toLock;
		mGestures.setTouchListener(mTouchListener, !mScreenLocked);
	}

	public boolean isLocked() {
		return mScreenLocked;
	}

	private static final int MSG_FADE_OUT = 1;
	private static final int MSG_SHOW_PROGRESS = 2;
	private static final int MSG_HIDE_SYSTEM_UI = 3;
	private static final int MSG_TIME_TICK = 4;
	private static final int MSG_HIDE_OPERATION_INFO = 5;
	private static final int MSG_HIDE_OPERATION_VOLLUM = 6;

	private static class MHandler extends Handler {
		private WeakReference<MediaController> mc;

		public MHandler(MediaController mc) {
			this.mc = new WeakReference<MediaController>(mc);
		}

		@Override
		public void handleMessage(Message msg) {
			MediaController c = mc.get();
			if (c == null)
				return;

			switch (msg.what) {
			case MSG_FADE_OUT:
				c.hide();
				break;
			case MSG_SHOW_PROGRESS:
				long pos = c.setProgress();
				if (!c.mDragging && c.mShowing) {
					msg = obtainMessage(MSG_SHOW_PROGRESS);
					sendMessageDelayed(msg, 1000 - (pos % 1000));
					c.updatePausePlay();
				}
				break;
			case MSG_HIDE_SYSTEM_UI:
				if (!c.mShowing)
					c.showSystemUi(false);
				break;
			case MSG_TIME_TICK:
				c.mDateTime.setText(currentTimeString());
				c.mWifiRate.setText(getNetworkClass());
				sendEmptyMessageDelayed(MSG_TIME_TICK, TIME_TICK_INTERVAL);
				break;
			case MSG_HIDE_OPERATION_INFO:
				c.mOperationInfo.setVisibility(View.INVISIBLE);
				break;
			case MSG_HIDE_OPERATION_VOLLUM:
				c.relativeLayout_volume.setVisibility(View.INVISIBLE);
				break;
			}
		}
	};

	private static String currentTimeString() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(new java.util.Date());
	}

	private long setProgress() {
		if (mPlayer == null || mDragging)
			return 0;

		long position = mPlayer.getCurrentPosition();
		long duration = mPlayer.getDuration();
		if (duration > 0) {
			long pos = 1000L * position / duration;
			mProgress.setProgress((int) pos);
		}
		int percent = mPlayer.getBufferPercentage();
		mProgress.setSecondaryProgress(percent * 10);

		mDuration = duration;
		mTime.setText(length2time(position) + "/" + length2time(mDuration));

		return position;
	}

	public void initDuration() {
		if (mPlayer != null && mDuration <= 0l) {
			mDuration = mPlayer.getDuration();
		}
	}

	/**
	 * 将进度长度转变为进度时间
	 */
	private String length2time(long length) {
		int totalSeconds = (int) (length / 1000);
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;
		return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes,
				seconds) : String.format("%02d:%02d", minutes, seconds);
	}

	public static String generateTime(long time) {
		int totalSeconds = (int) (time / 1000);
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes,
				seconds) : String.format("%02d:%02d", minutes, seconds);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mHandler.removeMessages(MSG_HIDE_SYSTEM_UI);
		mHandler.sendEmptyMessageDelayed(MSG_HIDE_SYSTEM_UI, DEFAULT_TIME_OUT);
		return mGestures.onTouchEvent(event) || super.onTouchEvent(event);
	}

	private TouchListener mTouchListener = new TouchListener() {
		long mVideo_current_length;
		String total_length;

		@Override
		public void onGestureBegin() {
			mBrightness = mContext.getWindow().getAttributes().screenBrightness;
			mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (mBrightness <= 0.00f)
				mBrightness = 0.50f;
			if (mBrightness < 0.01f)
				mBrightness = 0.01f;
			if (mVolume < 0)
				mVolume = 0;
			initDuration();
			mSpeed = 0;
			toposition = -1l;
			istoanotherposition = false;
			total_length = length2time(mDuration);
			mVideo_current_length = mPlayer.getCurrentPosition();// 当前播放长度
		}

		@Override
		public void onLeftSlide(float percent) {
			setBrightness(mBrightness + percent);

		}

		@Override
		public void onGestureEnd() {
			relativeLayout_volume.setVisibility(View.INVISIBLE);
			relativeLayout_brightness.setVisibility(View.INVISIBLE);
			if (istoanotherposition) {
				istoanotherposition = false;
				mPlayer.seekTo(toposition);
			}
		}

		@Override
		public void onRightSlide(float percent) {
			int v = (int) (percent * mMaxVolume) + mVolume;
			setVolume(v);
		}

		@Override
		public void onVideoSpeed(float distanceX) {
			if (distanceX > 0) {// 往左滑动 --
				--mSpeed;
			} else if (distanceX < 0) {// 往右滑动 ++
				++mSpeed;
			}
			int i = mSpeed * 1000;// 快进长度
			long mVideo_start_length = mVideo_current_length + i;// 快进之后长度
			if (mVideo_start_length >= mDuration) {
				mVideo_start_length = (long) mDuration;
			} else if (mVideo_start_length <= 0) {
				mVideo_start_length = 0L;
			}
			istoanotherposition = true;
			toposition = (long) mVideo_start_length;
			String start_length = length2time(mVideo_start_length);
			int pasttime = (int) ((mVideo_start_length - mVideo_current_length) / 1000l);
			String pasttimestr;
			if (pasttime >= 0) {
				pasttimestr = "+" + String.valueOf(pasttime);
			} else {
				pasttimestr = String.valueOf(pasttime);
			}
			setOperationInfo(start_length + "/" + total_length + "\n"
					+ pasttimestr + "秒", 500);
		}

		@Override
		public void onSingleTap() {
			if (mShowing)
				hide();
			else
				show();
			if (mPlayer.getBufferPercentage() >= 100) {
				mPlayer.removeLoadingView();
			}
		}

		@Override
		public void onDoubleTap() {
			toggleVideoMode(true, true);
		}

		@Override
		public void onLongPress() {
			doPauseResume();
		}

		@Override
		public void onScale(float scaleFactor, int state) {
			switch (state) {
			case CommonGestures.SCALE_STATE_BEGIN:
				mVideoMode = VideoView.VIDEO_LAYOUT_SCALE_ZOOM;
				mScreenToggle
						.setImageResource(R.drawable.mediacontroller_sreen_size_100);
				mPlayer.toggleVideoMode(mVideoMode);
				break;
			case CommonGestures.SCALE_STATE_SCALEING:
				float currentRatio = mPlayer.scale(scaleFactor);
				setOperationInfo((int) (currentRatio * 100) + "%", 500);
				break;
			case CommonGestures.SCALE_STATE_END:
				break;
			}
		}
	};

	private void setVolume(int v) {
		relativeLayout_volume.setVisibility(View.VISIBLE);
		if (v > mMaxVolume)
			v = mMaxVolume;
		else if (v < 0)
			v = 0;
		mAM.setStreamVolume(AudioManager.STREAM_MUSIC, v, 0);
		int voltmp = (int) (100 * v / mMaxVolume);
		volumeProgressBar.setProgress(voltmp);
		if (voltmp <= 0) {
			setOperationInfo("静音", 1500);
		} else {
			setOperationInfo("音量：" + String.valueOf(voltmp) + "%", 1500);
		}
	}

	private void setBrightness(float f) {
		relativeLayout_brightness.setVisibility(View.VISIBLE);
		WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
		lp.screenBrightness = f;
		if (lp.screenBrightness > 1.0f)
			lp.screenBrightness = 1.0f;
		else if (lp.screenBrightness < 0.01f)
			lp.screenBrightness = 0.01f;
		mContext.getWindow().setAttributes(lp);
		int britmp = (int) ((lp.screenBrightness - 0.01f) / 0.99f * 100);
		brightnessProgressBar.setProgress(britmp);
		setOperationInfo("亮度：" + String.valueOf(britmp) + "%", 1500);
	}

	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		show(DEFAULT_TIME_OUT);
		return false;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		int keyMode = event.getAction();

		if (keyMode == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_MUTE:
				return super.dispatchKeyEvent(event);
			case KeyEvent.KEYCODE_VOLUME_UP:
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
				int step = keyCode == KeyEvent.KEYCODE_VOLUME_UP ? 1 : -1;
				setVolume(mVolume + step);
				mHandler.removeMessages(MSG_HIDE_OPERATION_VOLLUM);
				mHandler.sendEmptyMessageDelayed(MSG_HIDE_OPERATION_VOLLUM, 500);
				return true;
			}

			if (isLocked() && keyCode != KeyEvent.KEYCODE_BACK) {
				show();
				return true;
			}

			if (event.getRepeatCount() == 0
					&& (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
							|| keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)) {
				doPauseResume();
				show(DEFAULT_TIME_OUT);
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
				if (mPlayer.isPlaying()) {
					mPlayer.pause();
					updatePausePlay();
				}
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_BACK) {
				pressbacktime++;
				if (pressbacktime > 1) {
					release();
				}
				mPlayer.stop(pressbacktime);
				return true;
			} else {
				show(DEFAULT_TIME_OUT);
			}
			return super.dispatchKeyEvent(event);
		} else {
			return super.dispatchKeyEvent(event);
		}
	}

	@TargetApi(11)
	private void showSystemUi(boolean visible) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			int flag = visible ? 0 : View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LOW_PROFILE;
			mRoot.setSystemUiVisibility(flag);
		}
	}

	private void showButtons(boolean showButtons) {
		Window window = mContext.getWindow();
		WindowManager.LayoutParams layoutParams = window.getAttributes();
		float val = showButtons ? -1 : 0;
		try {
			Field buttonBrightness = layoutParams.getClass().getField(
					"buttonBrightness");
			buttonBrightness.set(layoutParams, val);
		} catch (Exception e) {
		}
		window.setAttributes(layoutParams);
	}

	private void updatePausePlay() {
		if (mPlayer.isPlaying())
			mPauseButton.setImageResource(R.drawable.mediacontroller_pause);
		else
			mPauseButton.setImageResource(R.drawable.mediacontroller_play);
	}

	private void doPauseResume() {
		if (mPlayer.isPlaying())
			mPlayer.pause();
		else
			mPlayer.start();
		updatePausePlay();
	}

	private View.OnClickListener mPauseListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mPlayer.isPlaying())
				show(DEFAULT_LONG_TIME_SHOW);
			else
				show();
			doPauseResume();
		}
	};

	private View.OnClickListener mMediacontroller_backListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mContext != null) {
				mContext.finish();
			}
		}
	};

	private View.OnClickListener mLockClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			hide();
			lock(!mScreenLocked);
			show();
		}
	};

	private View.OnClickListener mScreenToggleListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			show(DEFAULT_TIME_OUT);
			toggleVideoMode(true, true);
		}
	};

	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		private boolean wasStopped = false;
		long mVideo_current_length;

		@Override
		public void onStartTrackingTouch(SeekBar bar) {
			mDragging = true;
			show(3600000);
			mVideo_current_length = mPlayer.getCurrentPosition() / 1000l;// 当前播放长度
			mHandler.removeMessages(MSG_SHOW_PROGRESS);
			wasStopped = !mPlayer.isPlaying();
			if (mInstantSeeking) {
				mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
				if (wasStopped) {
					mPlayer.start();
				}
			}
		}

		@Override
		public void onProgressChanged(SeekBar bar, int progress,
				boolean fromuser) {
			if (!fromuser)
				return;

			long newposition = (mDuration * progress) / 1000;
			String time = length2time(newposition);
			if (mInstantSeeking)
				mPlayer.seekTo(newposition);
			int plustime = (int) (newposition / 1000 - mVideo_current_length);
			String plustimestr = "";
			if (plustime >= 0) {
				plustimestr = "\n+" + String.valueOf(plustime) + "秒";
			} else {
				plustimestr = "\n" + String.valueOf(plustime) + "秒";
			}

			setOperationInfo(time + "/" + length2time(mDuration) + plustimestr,
					1500);
		}

		@Override
		public void onStopTrackingTouch(SeekBar bar) {
			if (!mInstantSeeking) {
				mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
			} else if (wasStopped) {
				mPlayer.pause();
			}
			mOperationInfo.setVisibility(View.INVISIBLE);
			show(DEFAULT_TIME_OUT);
			mHandler.removeMessages(MSG_SHOW_PROGRESS);
			mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
			mDragging = false;
			mHandler.sendEmptyMessageDelayed(MSG_SHOW_PROGRESS, 1000);
		}
	};

	public interface MediaPlayerControl {
		void start();

		void pause();

		void stop(int pressbacktime);

		void seekTo(long pos);

		void setVideoQuality(int quality);

		boolean isPlaying();

		long getDuration();

		long getCurrentPosition();

		int getBufferPercentage();

		void previous();

		void next();

		long goForward();

		long goBack();

		void toggleVideoMode(int mode);

		void showMenu();

		void removeLoadingView();

		float scale(float scale);
	}

}
