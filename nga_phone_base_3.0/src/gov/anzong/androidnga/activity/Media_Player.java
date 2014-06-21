package gov.anzong.androidnga.activity;

import gov.anzong.androidnga.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sp.phone.utils.ActivityUtil;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;


public class Media_Player extends Activity {
	private static final String TAG= "MediaPlayerActivity";
    private String path = "";
    private VideoView mVideoView;
    private View mVolumeBrightnessLayout;
    private ImageView mOperationBg;
    private ImageView mOperationPercent;
    private AudioManager mAudioManager;
    private Toast toast = null;
    private long toposition=-1l;
    private long onpausevideopos=-1l;
    private boolean onpausemode=false;
    private int mSpeed =0;
    /** ������� */
    private int mMaxVolume;
    /** ��ǰ���� */
    private int mVolume = -1;
    /** ��ǰ���� */
    private float mBrightness = -1f;
    /** ��ǰ����ģʽ */
    private int mLayout = VideoView.VIDEO_LAYOUT_ZOOM;
    private GestureDetector mGestureDetector;
    private MediaController mMediaController;
    private View mLoadingView;
    private View mPositionView;
    private TextView mPositionTextView;
    private boolean istoanotherposition =false;
    private boolean openactivity = true;
    private boolean firstScroll=true;
    private int mode = 0;
    @Override
    public void onCreate(Bundle icicle) {
    	Bundle b = this.getIntent().getExtras();
    	path = b.getString("MEDIAPATH"); 
        super.onCreate(icicle);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (ActivityUtil.isNotLessThan_4_0()) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        setContentView(R.layout.videoview);
        mLoadingView = findViewById(R.id.video_loading);
        mPositionView = findViewById(R.id.video_position_second);
        mPositionTextView = (TextView) mPositionView.findViewById(R.id.video_loading_text);
        mVideoView = (VideoView) findViewById(R.id.surface_view);
        mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
        mOperationBg = (ImageView) findViewById(R.id.operation_bg);
        mOperationPercent = (ImageView) findViewById(R.id.operation_percent);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        if(!path.equals("")){
        	if (path.startsWith("http:"))
        		mVideoView.setVideoURI(Uri.parse(path));
    		else
    			mVideoView.setVideoPath(path);
            mMediaController = new MediaController(this);
            mVideoView.setMediaController(mMediaController);
            mVideoView.requestFocus();
            mVideoView.setBufferSize(512*1024);
            mVideoView.setOnInfoListener(new OnInfoListener() {
            	 private boolean needResume;
				@Override
				public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
					switch (arg1) {
					case MediaPlayer.MEDIA_INFO_BUFFERING_START:
						//��ʼ���棬��ͣ����
						if (isPlaying()) {
							stopPlayer();
							needResume = true;
						}
			            mPositionView.setVisibility(View.GONE);
						mLoadingView.setVisibility(View.VISIBLE);
						break;
					case MediaPlayer.MEDIA_INFO_BUFFERING_END:
						//������ɣ���������
						if(needResume || openactivity){
							openactivity=false;
							startPlayer();
						}
						mLoadingView.setVisibility(View.GONE);
						break;
			        case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
			            break;
			        }
			        return true;
				}
            	
            });
    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mGestureDetector = new GestureDetector(this, (OnGestureListener) new MyGestureListener());
            startPlayer();
        }
    }
    
    
    /*����������*/
    private void stopPlayer() {
		if (mVideoView != null){
			mVideoView.pause();
			onpausevideopos = mVideoView.getCurrentPosition();
		}
	}

	private void startPlayer() {
		if (mVideoView != null){
			mVideoView.start();
		if(onpausemode){
			onpausemode=false;
			if(onpausevideopos!=-1l){
				mVideoView.seekTo(onpausevideopos);
				onpausevideopos=-1l;
			}
		}
		}
	}

	private boolean isPlaying() {
		return mVideoView != null && mVideoView.isPlaying();
	}
	
	
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event))
            return true;

        if(mVideoView.isPlaying()){
        	mLoadingView.setVisibility(View.GONE);
        }
        // �������ƽ���
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_UP:
            endGesture();
            break;
        }

        return super.onTouchEvent(event);
    }

    /** ���ƽ��� */
    private void endGesture() {
        mVolume = -1;
        mBrightness = -1f;
        mSpeed = 0;
        // ����
        mDismissHandler.removeMessages(0);
        mDismissHandler.sendEmptyMessageDelayed(0, 500);
        if(istoanotherposition && mVideoView != null){
	    		mMediaController.show();
    			mVideoView.seekTo(toposition);
        }
        firstScroll=true;
        toposition = -1l;
    	istoanotherposition=false;
    	mode =0;
    	if (ActivityUtil.isNotLessThan_4_0()) {
    		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    	}
    }

    private class MyGestureListener extends SimpleOnGestureListener {
    	
    	@Override
        public boolean onDown(MotionEvent e) {
            // TODO Auto-generated method stub
            firstScroll = true;// �趨�Ǵ�����Ļ���һ��scroll�ı�־
            return false;
        }
        /** ˫�� */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mLayout == VideoView.VIDEO_LAYOUT_ZOOM)
                mLayout = VideoView.VIDEO_LAYOUT_ORIGIN;
            else
                mLayout++;
            String viewmode[]={"ԭʼ����","����ȫ��","��������","����ü�"};
            if (toast != null)
        	{
        		toast.setText(viewmode[mLayout]);
        		toast.setDuration(Toast.LENGTH_SHORT);
        		toast.show();
        	} else
        	{
        		toast = Toast.makeText(Media_Player.this, viewmode[mLayout], Toast.LENGTH_SHORT);
        		toast.show();
        	}
            if (mVideoView != null)
                mVideoView.setVideoLayout(mLayout, 0);
            return true;
        }          
        
        
        /** ���� */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            int y = (int) e2.getRawY();
            Display disp = getWindowManager().getDefaultDisplay();
            int windowWidth = disp.getWidth();
            int windowHeight = disp.getHeight();
            
        	if (firstScroll) {// �Դ�����Ļ���һ�λ���Ϊ��׼����������Ļ�ϲ����л�����
                // ����ľ���仯����������ȣ�����ı仯�����������
        		if(Math.abs(distanceY) > Math.abs(distanceX)){
                    if (mOldX > windowWidth * 3.0 / 5 && Math.abs(distanceY) > Math.abs(distanceX)) {
                    	mode=1;
                    } else if(mOldX < windowWidth * 2.0 / 5.0 && Math.abs(distanceY) > Math.abs(distanceX) ) {
                        mode =2;
                    }else{
                    	mode=3;
                    	}
        		}else{
                	mode=3;
        		}
        		firstScroll = false;// ��һ��scrollִ����ɣ��޸ı�־
            }
        	
            if (mode == 1){// ����Ļ���ұ߻���
            	 onVolumeSlide((mOldY - y) / windowHeight);
               } else if (mode==2){// ����Ļ����߻���
            	   onBrightnessSlide((mOldY - y) / windowHeight);
               } else if (mode==3){//��x���ϻ���
            	   if (mVideoView != null){
            		   istoanotherposition=true;
            		   onVideoSpeed(distanceX);//�������
            	   }
               }
            
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }
    
    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
    	 switch (keyCode) {
    	 case KeyEvent.KEYCODE_VOLUME_DOWN:
    		onVolumeSlideWithButton("down");
    		endGesture();
    	    return true;
    	 case KeyEvent.KEYCODE_VOLUME_UP:
     		onVolumeSlideWithButton("up");
     		endGesture();
     		return true;
    	 }
    	 return super.onKeyDown (keyCode, event);
    }
    
    /** ��ʱ���� */
    private Handler mDismissHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mVolumeBrightnessLayout.setVisibility(View.GONE);
            mPositionView.setVisibility(View.GONE);
        }
    };

    /**
     * ��������
     * 
     * @param percent
     */
    private void onVolumeSlideWithButton(String mode) {
    	mMediaController.hide();
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;

            // ��ʾ
            mOperationBg.setImageResource(R.drawable.video_volumn_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }
        int index = 0;
        if(mode.equals("up")){
        	index=mVolume+1;
        }else if(mode.equals("down")){
        	index=mVolume-1;
        }else{
        	index=mVolume;
        }
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // �������
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        // ���������
        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = findViewById(R.id.operation_full).getLayoutParams().width
                * index / mMaxVolume;
        mOperationPercent.setLayoutParams(lp);
    }
    
    
    
    /**
     * �������/��
     * 
     * @param percent
     */
    private void onVideoSpeed(float distanceX) {
    	mMediaController.hide();
    	mPositionView.setVisibility(View.VISIBLE);
    	long mVideo_total_length = (long) mVideoView.getDuration();//�ܳ���
    	String total_length = length2time(mVideo_total_length);
    	long mVideo_current_length = mVideoView.getCurrentPosition();//��ǰ���ų���
    	if(distanceX>0){//���󻬶� --
    	  --mSpeed;
    	} else if (distanceX < 0){//���һ��� ++
    	  ++mSpeed;
    	}
    	int i = mSpeed * 1000;//�������
    	long mVideo_start_length = mVideo_current_length + i;//���֮�󳤶�
    	if(mVideo_start_length >= mVideo_total_length){
    	  mVideo_start_length = (long) mVideo_total_length;
    	} else if(mVideo_start_length <= 0){
    	  mVideo_start_length = 0L;
    	}
    	toposition = (long) mVideo_start_length;
    	String start_length = length2time(mVideo_start_length);
  	    String text = start_length+"/"+total_length;
    	mPositionTextView.setText(text);
    }

    /**
     * �����ȳ���ת��Ϊ����ʱ��
     */
   private String length2time(long length){
     length /= 1000L;
     long minute = length / 60L;
     long hour = minute / 60L;
     long second = length % 60L;
     minute %= 60L;
     if(hour==0){
    	 return String.format("%02d:%02d", minute, second);
     }else{
    	 return String.format("%02d:%02d:%02d",hour, minute, second);
     }
   } 
    
    
    /**
     * �����ı�������С
     * 
     * @param percent
     */
    private void onVolumeSlide(float percent) {
    	mMediaController.hide();
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;

            // ��ʾ
            mOperationBg.setImageResource(R.drawable.video_volumn_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }

        int index = (int) (percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // �������
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        // ���������
        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = findViewById(R.id.operation_full).getLayoutParams().width
                * index / mMaxVolume;
        mOperationPercent.setLayoutParams(lp);
    }

    /**
     * �����ı�����
     * 
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
    	mMediaController.hide();
        if (mBrightness < 0) {
            mBrightness = getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.01f;

            // ��ʾ
            mOperationBg.setImageResource(R.drawable.video_brightness_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }
        WindowManager.LayoutParams lpa = getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        getWindow().setAttributes(lpa);

        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * lpa.screenBrightness);
        mOperationPercent.setLayoutParams(lp);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mVideoView != null)
            mVideoView.setVideoLayout(mLayout, 0);
        super.onConfigurationChanged(newConfig);
    }
    @Override
	protected void onPause() {
    	stopPlayer();
    	onpausemode=true;
		super.onPause();
	}

	@Override
	protected void onResume() {
    	startPlayer();
    	onpausemode=false;
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mVideoView != null)
			mVideoView.stopPlayback();
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	
}